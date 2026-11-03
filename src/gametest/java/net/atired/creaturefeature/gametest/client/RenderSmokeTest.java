package net.atired.creaturefeature.gametest.client;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFEntityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
    private static final int STOP_AT = SUMMON_AT + 600;
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
        if (ticksInWorld == SUMMON_AT + 40 || ticksInWorld == SUMMON_AT + 200 || ticksInWorld == STOP_AT - 5) {
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "cf_smoke_" + ticksInWorld + ".png", mc.getMainRenderTarget(),
                    message -> LOGGER.info("CF_RENDER_SMOKE screenshot {}", message.getString()));
        }
        if (ticksInWorld == STOP_AT) {
            LOGGER.info("CF_RENDER_SMOKE_OK summoned={} rendered for {} ticks", summoned, STOP_AT - SUMMON_AT);
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
        LOGGER.info("CF_RENDER_SMOKE summoned {} creatures", i);
    }

    private static void run(MinecraftServer server, ServerPlayer player, String command) {
        server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withPermission(4).withSuppressedOutput(), command);
    }
}
