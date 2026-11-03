package net.atired.creaturefeature.gametest;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * PORT: port-only, dev-only performance measurement (not a check: it always succeeds). Enabled only with
 * -Dcreaturefeature.perf=true (gradlew runGameTestServer -Dcreaturefeature.perf=true); otherwise it returns at once.
 * In its own batch, after every other test, it spawns 1, 10 and 30 zombies / pathogens / blossoms around a player
 * (a FakePlayer added to the level, so the mobs target it), lets them settle 40 ticks and then measures, over 100
 * ticks, the time of this level's tick (LevelTickEvent START -> END), next to an empty baseline measured in the same
 * pass. (Forge's per-entity TimeTracker recorded nothing on the GameTest server.) The whole set runs twice (JIT
 * warmup) and only the second pass counts. Results in the log, lines starting with CF_PERF.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class PerfGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean ENABLED = Boolean.getBoolean("creaturefeature.perf");
    private static final int WARMUP = 40;
    private static final int MEASURE = 100;
    private static final int SLOT = WARMUP + MEASURE + 20;

    private static final List<Long> LEVEL_NANOS = new CopyOnWriteArrayList<>();
    private static volatile ServerLevel measuring = null;
    private static long levelTickStart = 0;
    private static boolean listening = false;

    private record Scenario(String name, Supplier<? extends EntityType<? extends Mob>> type, int count) {}

    private static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level != measuring) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            levelTickStart = System.nanoTime();
        } else if (levelTickStart != 0) {
            LEVEL_NANOS.add(System.nanoTime() - levelTickStart);
        }
    }

    @GameTest(template = SkeletonGameTests.TEMPLATE, batch = "zz_perf", timeoutTicks = 12000, required = false)
    public static void perfPathogenBlossomVsZombie(GameTestHelper helper) {
        if (!ENABLED) {
            helper.succeed();
            return;
        }
        ServerLevel level = helper.getLevel();
        MinecraftServer server = level.getServer();
        if (!listening) {
            listening = true;
            MinecraftForge.EVENT_BUS.addListener(PerfGameTests::onLevelTick);
        }
        level.setDayTime(18000);   // night: zombies and blossoms do not burn
        level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        FakePlayer player = FakePlayerFactory.getMinecraft(level);
        Vec3 center = helper.absoluteVec(new Vec3(4.5, 2, 4.5));
        player.moveTo(center.x, center.y, center.z, 0, 0);
        level.addNewPlayer(player);

        List<Scenario> scenarios = new ArrayList<>();
        for (int pass = 0; pass < 2; pass++) {
            String prefix = pass == 0 ? "warmup-" : "";
            scenarios.add(new Scenario(prefix + "empty", () -> EntityType.ZOMBIE, 0));
            for (int n : new int[]{1, 10, 30}) {
                scenarios.add(new Scenario(prefix + "zombie", () -> EntityType.ZOMBIE, n));
                scenarios.add(new Scenario(prefix + "pathogen", CFEntityInit.PATHOGEN, n));
                scenarios.add(new Scenario(prefix + "blossom", CFEntityInit.BLOSSOM, n));
            }
            scenarios.add(new Scenario(prefix + "empty", () -> EntityType.ZOMBIE, 0));
        }
        List<String> results = new ArrayList<>();
        for (int s = 0; s < scenarios.size(); s++) {
            Scenario sc = scenarios.get(s);
            int start = 5 + s * SLOT;
            List<Entity> spawned = new ArrayList<>();
            helper.runAfterDelay(start, () -> {
                for (int i = 0; i < sc.count(); i++) {
                    double angle = i * 2.399963;   // golden angle: even spread, 2 to 4 blocks from the player
                    double r = 2.0 + 2.0 * i / Math.max(1, sc.count());
                    BlockPos pos = new BlockPos((int) Math.floor(4.5 + Math.cos(angle) * r), 2, (int) Math.floor(4.5 + Math.sin(angle) * r));
                    Mob mob = helper.spawn(sc.type().get(), pos);
                    mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    mob.setPersistenceRequired();
                    spawned.add(mob);
                }
            });
            helper.runAfterDelay(start + WARMUP, () -> {
                LEVEL_NANOS.clear();
                measuring = level;
            });
            helper.runAfterDelay(start + WARMUP + MEASURE, () -> {
                measuring = null;
                long sum = 0, max = 0;
                for (long n : LEVEL_NANOS) {
                    sum += n;
                    max = Math.max(max, n);
                }
                int ticks = Math.max(1, LEVEL_NANOS.size());
                int spat = level.getEntities(CFEntityInit.SPAT_BLOCK.get(), player.getBoundingBox().inflate(64), e -> true).size();
                String line = String.format(Locale.ROOT, "CF_PERF %-16s x%2d  level tick avg %.3f ms  max %.3f ms  over %d ticks  (spat_blocks flying at the end: %d)",
                        sc.name(), sc.count(), sum / 1e6 / ticks, max / 1e6, LEVEL_NANOS.size(), spat);
                results.add(line);
                LOGGER.info(line);
                spawned.forEach(Entity::discard);
                level.getEntities(CFEntityInit.SPAT_BLOCK.get(), player.getBoundingBox().inflate(64), e -> true).forEach(Entity::discard);
            });
        }
        helper.runAfterDelay(5 + scenarios.size() * SLOT, () -> {
            level.removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
            LOGGER.info("CF_PERF done ({} scenarios)", results.size());
            helper.succeed();
        });
    }
}
