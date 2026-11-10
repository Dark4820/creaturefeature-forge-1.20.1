package net.atired.creaturefeature.client;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.Config;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.atired.creaturefeature.misc.TrialsCompat;
import net.atired.creaturefeature.networking.CFNetwork;
import net.atired.creaturefeature.networking.payloads.C2SManPayload;
import net.atired.creaturefeature.networking.payloads.C2SVelSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * PORT: port-only, client half of the dedicated-server check (the server half is misc/CFServerSelfTest). Start the
 * client with {@code -Dcreaturefeature.selftest=true} and connect it to a dedicated server started with the same
 * property. It does nothing in singleplayer (that is client/CFSelfTest). It records what arrives from the server
 * (effects, the screen-effect states, rabies, gas leak, push, squash and de-amp packets, every creature), sends the two
 * client-to-server packets (the "man" one and the horn push) and takes screenshots
 * ({@code screenshots/creaturefeature-server-*.png}). Logs {@code [creaturefeature client selftest] PASS/FAIL} and
 * DONE.
 */
public final class CFRemoteSelfTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG = "[creaturefeature client selftest] ";
    private int tick = -1;
    private int pass;
    private int fail;
    private int oldGuiScale = -1;
    private final Set<ResourceLocation> effects = new HashSet<>();
    private final Set<String> creatures = new TreeSet<>();
    private float fiendish, sleepy, bacterial, gas, rabies, brainrot, squashed;
    private double pushY;
    private boolean deAmp;

    private void check(String name, boolean ok, String detail) {
        if (ok) {
            this.pass++;
            LOGGER.info(TAG + "PASS {} ({})", name, detail);
        } else {
            this.fail++;
            LOGGER.error(TAG + "FAIL {} ({})", name, detail);
        }
    }

    private void shot(Minecraft mc, String name) {
        Screenshot.grab(mc.gameDirectory, "creaturefeature-server-" + name + ".png", mc.getMainRenderTarget(),
                message -> LOGGER.info(TAG + "{}", message.getString()));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || mc.player == null || mc.level == null || mc.getSingleplayerServer() != null) {
            return;
        }
        if (this.tick == -2) {
            return;
        }
        if (this.tick == -1) {
            this.tick = 0;
            LOGGER.info(TAG + "connected to a dedicated server, Trials Chambers installed: {}", ModList.get().isLoaded(TrialsCompat.TRIALS));
        }
        this.tick++;
        try {
            this.sample(mc);
            this.step(mc);
        } catch (RuntimeException e) {
            this.fail++;
            LOGGER.error(TAG + "FAIL threw at tick {}", this.tick, e);
            this.finish(mc);
        }
    }

    private void sample(Minecraft mc) {
        CFClientProxy proxy = CreatureFeatureClient.PROXY;
        mc.player.getActiveEffects().forEach(e -> this.effects.add(ForgeRegistries.MOB_EFFECTS.getKey(e.getEffect())));
        this.fiendish = Math.max(this.fiendish, proxy.fiendish);
        this.sleepy = Math.max(this.sleepy, proxy.eebyDeebyNess);
        this.bacterial = Math.max(this.bacterial, proxy.bacterial);
        this.gas = Math.max(this.gas, proxy.gasLeak);
        if (mc.player instanceof PlayerBrainrotAccessor a) {
            this.rabies = Math.max(this.rabies, a.getRabies());
            this.brainrot = Math.max(this.brainrot, a.getBrainrot());
        }
        this.pushY = Math.max(this.pushY, mc.player.getDeltaMovement().y);
        if (this.tick % 5 == 0) {
            for (Entity e : mc.level.entitiesForRendering()) {
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
                if (key != null && key.getNamespace().equals(CreatureFeature.MODID)) {
                    this.creatures.add(key.getPath());
                }
                if (e instanceof Zombie && e instanceof LivingEntityGoopAccessor goop) {
                    this.squashed = Math.max(this.squashed, goop.getSquashed());
                    this.deAmp |= goop.isFallDamageAmped();
                }
            }
        }
    }

    private void step(Minecraft mc) {
        switch (this.tick) {
            case 215 -> this.shot(mc, "screen-effects");
            case 240 -> {
                this.oldGuiScale = mc.options.guiScale().get();
                mc.options.guiScale().set(2);
                mc.resizeDisplay();
                mc.setScreen(new InventoryScreen(mc.player));
            }
            case 270 -> {
                this.shot(mc, "effects");
                mc.setScreen(null);
                mc.options.guiScale().set(this.oldGuiScale);
                mc.resizeDisplay();
            }
            case 300 -> {
                // client-to-server packets, as the game sends them (the "man" search and the vertigo horn dash)
                CFNetwork.sendToServer(new C2SManPayload(mc.player.getId()));
                CFNetwork.sendToServer(new C2SVelSyncPayload(mc.player.getId(), 0.9, 0.0, 0.0));
                LOGGER.info(TAG + "sent the man and horn push packets");
            }
            case 330 -> this.shot(mc, "creatures");
            case 560 -> {
                Set<String> missing = new TreeSet<>(Set.of(Config.CREATURES));
                missing.removeAll(this.creatures);
                ResourceLocation infested = ForgeRegistries.MOB_EFFECTS.getKey(TrialsCompat.infested());
                ResourceLocation oozing = ForgeRegistries.MOB_EFFECTS.getKey(TrialsCompat.oozing());
                this.check("the client receives the blighted brain effects", this.effects.contains(infested) && this.effects.contains(oozing),
                        "effects seen=" + this.effects);
                this.check("fiendish screen effect", this.fiendish > 0.3f, "max=" + this.fiendish);
                this.check("sleep screen effect", this.sleepy > 0.3f, "max=" + this.sleepy);
                this.check("bacteria screen effect (synced infection)", this.bacterial > 0.3f, "max=" + this.bacterial);
                this.check("gas leak packet -> haze screen effect", this.gas > 0.3f, "max=" + this.gas);
                this.check("rabies packet", this.rabies > 0.5f, "max=" + this.rabies);
                this.check("push packet (VelSync)", this.pushY > 0.8, "max y speed=" + String.format("%.2f", this.pushY));
                this.check("squash packet from the falling carapace", this.squashed > 0.3f, "max=" + this.squashed);
                this.check("de-amp packet", this.deAmp, "seen=" + this.deAmp);
                this.check("minedflayer brainrot next to the player", this.brainrot > 0.05f, "max=" + this.brainrot);
                this.check("every creature reaches the client", missing.isEmpty(), "seen=" + this.creatures.size() + " missing=" + missing);
                // the Tools tab, built on a client joined to a dedicated server (the goat horn anchor bug)
                net.minecraft.world.item.CreativeModeTabs.tryRebuildTabContents(mc.player.connection.enabledFeatures(),
                        mc.player.canUseGameMasterBlocks(), mc.level.registryAccess());
                var tools = net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES);
                Set<String> inTab = new TreeSet<>();
                for (var item : java.util.List.of(net.atired.creaturefeature.init.CFItemInit.VERTIGO_HORN.get(), net.atired.creaturefeature.init.CFItemInit.OPEN_MIND.get(),
                        net.atired.creaturefeature.init.CFItemInit.DREAM_CATCHER.get(), net.atired.creaturefeature.init.CFItemInit.BOUQUET.get())) {
                    if (tools.getDisplayItems().stream().anyMatch(st -> st.is(item))) {
                        inTab.add(ForgeRegistries.ITEMS.getKey(item).getPath());
                    }
                }
                this.check("the mod's items are in the Tools tab", inTab.size() == 4, "found=" + inTab);
                this.finish(mc);
            }
            default -> { }
        }
    }

    private void finish(Minecraft mc) {
        if (this.oldGuiScale >= 0 && mc.options.guiScale().get() != this.oldGuiScale) {
            mc.options.guiScale().set(this.oldGuiScale);
            mc.resizeDisplay();
        }
        LOGGER.info(TAG + "DONE pass={} fail={}", this.pass, this.fail);
        this.tick = -2;
    }
}
