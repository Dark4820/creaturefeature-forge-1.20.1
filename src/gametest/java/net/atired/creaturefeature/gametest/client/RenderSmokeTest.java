package net.atired.creaturefeature.gametest.client;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.client.CFClientProxy;
import net.atired.creaturefeature.client.CreatureFeatureClient;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;

/**
 * PORT: port-only, dev-only automatic render test (lives in src/gametest: never in the jar). GameTests run on a
 * server and cannot draw anything, so this one drives the real client: it creates a flat world, puts a survival
 * player that cannot be hurt in front of every creature of the mod, lets them attack for a while (opening, shooting,
 * exploding...), takes screenshots (run-smoke/screenshots) and closes the game. A renderer that crashes leaves a crash report; when everything rendered, the log
 * says "CF_RENDER_SMOKE_OK". Enabled only with -Dcreaturefeature.renderSmoke=true (gradlew runClientRenderSmoke).
 */
@Mod.EventBusSubscriber(modid = CreatureFeature.MODID, value = Dist.CLIENT)
public class RenderSmokeTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean ENABLED = Boolean.getBoolean("creaturefeature.renderSmoke");
    private static final String WORLD = "cf_render_smoke";
    private static final int SUMMON_AT = 60;
    // F7: after the creatures, every screen effect is forced in turn under each graphics mode (one screenshot each)
    private static final int EFFECTS_AT = SUMMON_AT + 300;
    private static final int SLOT = 50;
    private static final String[] EFFECTS = {"none", "sleepy", "fiendish", "rabies", "flayed", "gas", "bacte", "man", "nan_title", "entity_fx"};
    private static final GraphicsStatus[] MODES = {GraphicsStatus.FAST, GraphicsStatus.FANCY, GraphicsStatus.FABULOUS};
    private static final int STOP_AT = EFFECTS_AT + MODES.length * EFFECTS.length * SLOT + 5;
    private static boolean worldRequested = false;
    private static int ticksInWorld = 0;
    private static int summoned = 0;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (!ENABLED || event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!worldRequested && mc.screen instanceof TitleScreen) {
            worldRequested = true;
            LOGGER.info("CF_RENDER_SMOKE creating world {}", WORLD);
            mc.createWorldOpenFlows().createFreshLevel(WORLD,
                    new LevelSettings(WORLD, GameType.SURVIVAL, false, Difficulty.NORMAL, true, new GameRules(), WorldDataConfiguration.DEFAULT),
                    new WorldOptions(1234L, false, false),
                    registries -> registries.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions());
            return;
        }
        if (mc.level == null || mc.player == null || mc.getSingleplayerServer() == null) {
            return;
        }
        ticksInWorld++;
        MinecraftServer server = mc.getSingleplayerServer();
        if (ticksInWorld == SUMMON_AT) {
            server.execute(() -> summonEverything(server));
        }
        // Screenshots in run-smoke/screenshots, to look at what was drawn (an invisible creature does not crash)
        if (ticksInWorld >= EFFECTS_AT && ticksInWorld < STOP_AT - 5) {
            effectTick(mc, server, ticksInWorld - EFFECTS_AT);
        }
        if (ticksInWorld == SUMMON_AT + 40 || ticksInWorld == SUMMON_AT + 200 || ticksInWorld == STOP_AT - 5) {
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "cf_smoke_" + ticksInWorld + ".png", mc.getMainRenderTarget(),
                    message -> LOGGER.info("CF_RENDER_SMOKE screenshot {}", message.getString()));
        }
        if (ticksInWorld == STOP_AT) {
            LOGGER.info("CF_RENDER_SMOKE_OK summoned={} rendered for {} ticks, effects={} modes={}", summoned, STOP_AT - SUMMON_AT, EFFECTS.length, MODES.length);
            mc.stop();
        }
    }

    private static void summonEverything(MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayers().get(0);
        run(server, player, "time set midnight");
        run(server, player, "gamerule doDaylightCycle false");
        run(server, player, "effect give @s minecraft:resistance infinite 255 true");
        run(server, player, "effect give @s minecraft:saturation infinite 255 true");
        run(server, player, "effect give @s minecraft:regeneration infinite 255 true");
        // Look along +Z: the creatures stand in a column ahead (same X, so the coat of arms is aligned and shoots)
        run(server, player, "tp @s 0 -60 0 0 20");
        List<? extends EntityType<?>> types = CFEntityInit.ENTITIES.getEntries().stream()
                .map(r -> r.get()).filter(t -> t.getCategory() == MobCategory.MONSTER)
                // the coat of arms only opens (and draws its flintlocks) within 17 blocks: put it closest
                .sorted(java.util.Comparator.comparing((EntityType<?> t) -> t != CFEntityInit.COATOFARMS.get())).toList();
        int i = 0;
        for (EntityType<?> type : types) {
            String id = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(type).toString();
            if (id.endsWith("canary_part")) {
                continue;   // spawned by the canary itself
            }
            run(server, player, "summon " + id + " 0 -60 " + (4 + i * 2));
            i++;
        }
        summoned = i;
        // The projectiles and auxiliary entities are drawn by their own renderers too
        run(server, player, "give @s creaturefeature:flintlock");
        // F6: a doohickey holding an item draws its sphere and the floating item
        run(server, player, "setblock 2 -60 3 creaturefeature:doohickey{doohickey_item:{id:\"creaturefeature:sleeping_powder\",Count:1b}}");
        // F7: a still zombie close to the camera for the entity effects (pathogen rings, goop puddle, squash, trail),
        // an armour stand with the mod's trims (HumanoidArmorLayerMixin)
        run(server, player, "summon minecraft:zombie 20.5 -60 4.5 {NoAI:1b,Invulnerable:1b,Silent:1b,PersistenceRequired:1b,Rotation:[180f,0f]}");
        run(server, player, "summon minecraft:armor_stand 22.5 -60 5.5 {Rotation:[180f,0f],ArmorItems:["
                + "{id:\"minecraft:iron_boots\",Count:1b,tag:{Trim:{material:\"creaturefeature:scroll_trans\",pattern:\"minecraft:coast\"}}},"
                + "{id:\"minecraft:iron_leggings\",Count:1b,tag:{Trim:{material:\"creaturefeature:scroll_pride\",pattern:\"minecraft:coast\"}}},"
                + "{id:\"minecraft:iron_chestplate\",Count:1b,tag:{Trim:{material:\"creaturefeature:ectoplasm\",pattern:\"minecraft:coast\"}}},"
                + "{id:\"minecraft:iron_helmet\",Count:1b,tag:{Trim:{material:\"creaturefeature:scroll_bi\",pattern:\"minecraft:coast\"}}}]}");
        LOGGER.info("CF_RENDER_SMOKE summoned {} creatures", i);
    }

    private static void effectTick(Minecraft mc, MinecraftServer server, int t) {
        int slot = t / SLOT;
        int inSlot = t % SLOT;
        GraphicsStatus mode = MODES[slot / EFFECTS.length];
        String effect = EFFECTS[slot % EFFECTS.length];
        CFClientProxy proxy = CreatureFeatureClient.PROXY;
        if (inSlot == 0) {
            if (mc.options.graphicsMode().get() != mode) {
                // the option's callback rebuilds the level renderer (and Fabulous' transparency chain), as the menu does
                // as the video settings button does: in 1.20.1 the option's own callback is empty, the button sets it
                // and rebuilds the level renderer (without that, Fabulous has no transparency targets)
                mc.options.graphicsMode().set(mode);
                mc.levelRenderer.allChanged();
                LOGGER.info("CF_RENDER_SMOKE graphics {}", mode);
            }
            // reset what the previous effect left
            proxy.rabies2 = proxy.flayed2 = proxy.gasLeak = proxy.manShader = proxy.nan_title = 0.0f;
            proxy.eebyDeebyNess = proxy.fiendish = proxy.bacterial = 0.0f;
            server.execute(() -> {
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                run(server, player, "effect clear @s creaturefeature:sleepy");
                run(server, player, "effect clear @s creaturefeature:fiendish");
                if (effect.equals("sleepy")) run(server, player, "effect give @s creaturefeature:sleepy 10 0 true");
                if (effect.equals("fiendish")) run(server, player, "effect give @s creaturefeature:fiendish 10 0 true");
                if (effect.equals("entity_fx")) {
                    // away from the creatures, looking at the victim, the trimmed armour stand and a falling carapace
                    run(server, player, "tp @s 20 -60 0 facing 20.5 -59 5");
                    run(server, player, "kill @e[type=minecraft:falling_block]");
                    run(server, player, "summon minecraft:falling_block 20.5 -57 9.5 {BlockState:{Name:\"creaturefeature:carapace_block\"},NoGravity:1b,Time:-100000}");
                } else {
                    run(server, player, "tp @s 0 -60 0 0 20");
                }
            });
            LOGGER.info("CF_RENDER_SMOKE effect {} ({})", effect, mode);
        }
        switch (effect) {
            case "rabies" -> proxy.rabies2 = 1.0f;
            case "flayed" -> proxy.flayed2 = 1.0f;
            case "gas" -> proxy.gasLeak = 1.0f;
            case "man" -> proxy.manShader = 1.0f;
            case "nan_title" -> proxy.nan_title = 0.5f;
            case "bacte" -> server.execute(() -> {
                // the player's infection drives the bacte screen (the victim's draws the rings, in entity_fx)
                if (server.getPlayerList().getPlayers().get(0) instanceof LivingEntityGoopAccessor goop && goop.getDelay() < 20) {
                    goop.setBacterial(1.0f);
                }
            });
            case "entity_fx" -> {
                server.execute(() -> {
                    for (Zombie zombie : server.overworld().getEntitiesOfClass(Zombie.class, VICTIM_BOX)) {
                        if (zombie instanceof LivingEntityGoopAccessor goop && goop.getDelay() < 20) {
                            goop.setBacterial(1.0f);
                        }
                    }
                });
                // client-side state read by the render events: goop puddle, squash and the fall-amped trail
                for (Zombie zombie : mc.level.getEntitiesOfClass(Zombie.class, VICTIM_BOX)) {
                    if (zombie instanceof LivingEntityGoopAccessor goop) {
                        goop.setGoop(1.0f);
                        goop.setSquashed(0.3f);
                        goop.setFallDamageAmped(true);
                    }
                }
            }
            default -> { }
        }
        if (inSlot == 45) {
            if (effect.equals("entity_fx")) {
                LOGGER.info("CF_RENDER_SMOKE entity_fx: player at {}, falling blocks {}", mc.player.position(),
                        mc.level.getEntitiesOfClass(net.minecraft.world.entity.item.FallingBlockEntity.class, mc.player.getBoundingBox().inflate(30))
                                .stream().map(f -> f.getBlockState() + "@" + f.position()).toList());
            }
            String name = "cf_fx_" + mode.name().toLowerCase(java.util.Locale.ROOT) + "_" + effect + ".png";
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(),
                    message -> LOGGER.info("CF_RENDER_SMOKE screenshot {}", message.getString()));
        }
    }

    private static final net.minecraft.world.phys.AABB VICTIM_BOX = new net.minecraft.world.phys.AABB(19, -61, 3, 22, -57, 6);

    private static void run(MinecraftServer server, ServerPlayer player, String command) {
        server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withPermission(4).withSuppressedOutput(), command);
    }
}
