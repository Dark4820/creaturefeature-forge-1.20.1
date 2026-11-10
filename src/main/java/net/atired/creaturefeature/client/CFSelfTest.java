package net.atired.creaturefeature.client;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.init.CFItemInit;
import net.atired.creaturefeature.misc.TrialsCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * PORT: port-only, opt-in in-game check of the blighted brain effects for a throwaway world, in the real game (not
 * the dev environment): start the client with {@code -Dcreaturefeature.selftest=true} and quick-play a copy of a
 * world. It eats blighted brains until Nausea, Infested and Oozing have all been applied (each is an 80 % roll),
 * checks they are Trials Chambers' when it is installed and the port's own otherwise, hurts a zombie with Infested
 * (silverfish must come out), kills a zombie with Oozing (slimes must appear) and takes a screenshot of the inventory
 * with both effects ({@code screenshots/creaturefeature-selftest-effects.png}). Logs
 * {@code [creaturefeature selftest] PASS/FAIL ...} and a final DONE line. It changes the player (survival, effects)
 * and spawns mobs, so never run it in a real world. Registered only when the property is set.
 */
public final class CFSelfTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG = "[creaturefeature selftest] ";
    private int step;
    private int wait = 100;
    private int pass;
    private int fail;
    private BlockPos spot;
    private int oldGuiScale = -1;

    private void check(String name, boolean ok, String detail) {
        if (ok) {
            this.pass++;
            LOGGER.info(TAG + "PASS {} ({})", name, detail);
        } else {
            this.fail++;
            LOGGER.error(TAG + "FAIL {} ({})", name, detail);
        }
    }

    private static void onServer(Minecraft mc, Consumer<ServerPlayer> action) {
        var server = mc.getSingleplayerServer();
        if (server != null && mc.player != null) {
            server.execute(() -> {
                ServerPlayer sp = server.getPlayerList().getPlayer(mc.player.getUUID());
                if (sp != null) {
                    action.accept(sp);
                }
            });
        }
    }

    private static ResourceLocation id(MobEffect effect) {
        return ForgeRegistries.MOB_EFFECTS.getKey(effect);
    }

    private void go(int ticks) {
        this.step++;
        this.wait = ticks;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || this.step < 0 || mc.player == null || mc.level == null || mc.getSingleplayerServer() == null) {
            return;
        }
        if (this.wait-- > 0) {
            return;
        }
        try {
            this.advance(mc);
        } catch (RuntimeException e) {
            this.fail++;
            LOGGER.error(TAG + "FAIL threw at step {}", this.step, e);
            this.finish(mc);
        }
    }

    private void advance(Minecraft mc) {
        switch (this.step) {
            case 0 -> {
                boolean trials = ModList.get().isLoaded(TrialsCompat.TRIALS);
                LOGGER.info(TAG + "Trials Chambers installed: {}", trials);
                onServer(mc, sp -> {
                    sp.setGameMode(GameType.SURVIVAL); // creative turns the inventory into the creative menu
                    sp.removeAllEffects();
                    sp.getFoodData().setFoodLevel(20);
                    this.spot = sp.blockPosition().offset(6, 0, 0);
                    // Eat blighted brains until all three effects have been applied (80 % each; 40 tries)
                    Set<ResourceLocation> seen = new HashSet<>();
                    int eaten = 0;
                    while (eaten < 40 && seen.size() < 3) {
                        sp.removeAllEffects();
                        new ItemStack(CFItemInit.BLIGHTED_BRAIN.get()).finishUsingItem(sp.level(), sp);
                        eaten++;
                        sp.getActiveEffects().forEach(e -> seen.add(id(e.getEffect())));
                    }
                    ResourceLocation infested = id(TrialsCompat.infested());
                    ResourceLocation oozing = id(TrialsCompat.oozing());
                    ResourceLocation expectInfested = trials ? new ResourceLocation("trials", "infested") : new ResourceLocation("creaturefeature", "infested");
                    ResourceLocation expectOozing = trials ? new ResourceLocation("trials", "oozing") : new ResourceLocation("creaturefeature", "oozing");
                    this.check("Infested comes from " + (trials ? "Trials Chambers" : "the port"), infested.equals(expectInfested), "effect=" + infested);
                    this.check("Oozing comes from " + (trials ? "Trials Chambers" : "the port"), oozing.equals(expectOozing), "effect=" + oozing);
                    this.check("eating the blighted brain gives Nausea, Infested and Oozing",
                            seen.contains(new ResourceLocation("minecraft", "nausea")) && seen.contains(infested) && seen.contains(oozing),
                            "brains eaten=" + eaten + " effects seen=" + seen);
                });
                this.go(20);
            }
            case 1 -> {
                // Infested: hurt a zombie with it (player attacks, so mods that react to "being attacked" fire too)
                onServer(mc, sp -> {
                    ServerLevel level = sp.serverLevel();
                    Zombie zombie = EntityType.ZOMBIE.create(level);
                    zombie.moveTo(this.spot.getX() + 0.5, this.spot.getY(), this.spot.getZ() + 0.5, 0, 0);
                    zombie.setNoAi(true);
                    level.addFreshEntity(zombie);
                    zombie.addEffect(new MobEffectInstance(TrialsCompat.infested(), 400, 0));
                    for (int i = 0; i < 100; i++) {
                        zombie.invulnerableTime = 0;
                        zombie.setHealth(zombie.getMaxHealth());
                        zombie.hurt(sp.damageSources().playerAttack(sp), 0.5f);
                    }
                    AABB box = zombie.getBoundingBox().inflate(6);
                    int silverfish = level.getEntitiesOfClass(Silverfish.class, box).size();
                    this.check("Infested: silverfish come out when the mob is hurt", silverfish > 0, "silverfish=" + silverfish + " after 100 hits");
                    level.getEntitiesOfClass(Silverfish.class, box).forEach(e -> e.discard());
                    zombie.discard();
                });
                this.go(20);
            }
            case 2 -> {
                // Oozing: kill a zombie that has it
                onServer(mc, sp -> {
                    ServerLevel level = sp.serverLevel();
                    Zombie zombie = EntityType.ZOMBIE.create(level);
                    zombie.moveTo(this.spot.getX() + 0.5, this.spot.getY(), this.spot.getZ() + 0.5, 0, 0);
                    zombie.setNoAi(true);
                    level.addFreshEntity(zombie);
                    zombie.addEffect(new MobEffectInstance(TrialsCompat.oozing(), 200, 0));
                    AABB box = zombie.getBoundingBox().inflate(6);
                    zombie.hurt(sp.damageSources().playerAttack(sp), 1000.0f);
                    List<Slime> slimes = level.getEntitiesOfClass(Slime.class, box);
                    this.check("Oozing: slimes appear when the mob dies", zombie.isDeadOrDying() && !slimes.isEmpty(),
                            "dead=" + zombie.isDeadOrDying() + " slimes=" + slimes.size() + " sizes=" + slimes.stream().map(Slime::getSize).toList());
                    slimes.forEach(e -> e.discard());
                });
                this.go(20);
            }
            case 3 -> {
                // Both effects on the player for the inventory screenshot (icons and names)
                onServer(mc, sp -> {
                    sp.removeAllEffects();
                    sp.addEffect(new MobEffectInstance(TrialsCompat.infested(), 1200, 0));
                    sp.addEffect(new MobEffectInstance(TrialsCompat.oozing(), 1200, 0));
                });
                this.go(20);
            }
            case 4 -> {
                // GUI scale 2 for the screenshot, so the effect list has room for the names (restored right after,
                // the options file is not written)
                this.oldGuiScale = mc.options.guiScale().get();
                mc.options.guiScale().set(2);
                mc.resizeDisplay();
                mc.setScreen(new InventoryScreen(mc.player));
                this.go(40);
            }
            case 5 -> {
                Set<ResourceLocation> client = new HashSet<>();
                mc.player.getActiveEffects().forEach(e -> client.add(id(e.getEffect())));
                this.check("the client shows both effects", client.contains(id(TrialsCompat.infested())) && client.contains(id(TrialsCompat.oozing())),
                        "client effects=" + client);
                Screenshot.grab(mc.gameDirectory, "creaturefeature-selftest-effects.png", mc.getMainRenderTarget(),
                        message -> LOGGER.info(TAG + "{}", message.getString()));
                this.go(20);
            }
            default -> this.finish(mc);
        }
    }

    private void finish(Minecraft mc) {
        if (this.oldGuiScale >= 0) {
            mc.options.guiScale().set(this.oldGuiScale);
            mc.resizeDisplay();
        }
        LOGGER.info(TAG + "DONE pass={} fail={}", this.pass, this.fail);
        this.step = -1;
        mc.setScreen(null);
    }
}
