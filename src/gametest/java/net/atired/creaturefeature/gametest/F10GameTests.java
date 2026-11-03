package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.Config;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.misc.CreatureToggles;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * F10 (port-only): per-creature switches of the server config. Own batch, so no other test spawns while a creature
 * is turned off; every test turns it back on.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F10GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;
    private static final String BATCH = "f10_toggles";

    private static void set(String creature, boolean enabled) {
        Config.CREATURE_ENABLED.get(creature).set(enabled);
        CreatureToggles.refresh();
    }

    private static long count(GameTestHelper helper, EntityType<?> type) {
        AABB box = new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16);
        return helper.getLevel().getEntities(type, box, e -> true).size();
    }

    private static long drops(GameTestHelper helper) {
        AABB box = new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16);
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, box).size();
    }

    // Disabled: not added from code (spawners, other creatures), an existing one vanishes without drops, the spawn
    // egg / command path still works (tagged)
    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 60)
    public void f10DisabledCreatureDoesNotSpawn(GameTestHelper helper) {
        EntityType<?> type = CFEntityInit.BEAUTY.get();
        // one that exists before the switch
        Entity before = helper.spawn(type, new BlockPos(1, 2, 1));
        set("beauty", false);
        try {
            helper.assertTrue(CreatureToggles.isDisabled(type), "beauty should be disabled");
            Entity added = helper.spawn(type, new BlockPos(2, 2, 2));
            helper.assertFalse(added.isAddedToWorld(), "a disabled creature must not be added to the world");
            Entity egg = type.spawn(helper.getLevel(), helper.absolutePos(new BlockPos(3, 2, 3)), MobSpawnType.SPAWN_EGG);
            helper.assertTrue(egg != null && egg.isAlive() && egg.getTags().contains(CreatureToggles.ALLOWED_TAG),
                    "the spawn egg still works for admins (tagged)");
            helper.runAfterDelay(5, () -> {
                helper.assertTrue(before.isRemoved(), "the existing beauty should vanish");
                helper.assertTrue(egg.isAlive(), "the egg-spawned beauty stays");
                helper.assertTrue(drops(helper) == 0, "no drops when it vanishes");
                set("beauty", true);
                helper.succeed();
            });
        } catch (RuntimeException e) {
            set("beauty", true);
            throw e;
        }
    }

    // Enabled (default): spawns as always, untagged, nothing removed
    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 60)
    public void f10EnabledCreatureSpawnsAsBefore(GameTestHelper helper) {
        EntityType<?> type = CFEntityInit.MINDS.get();
        helper.assertTrue(Config.CREATURE_ENABLED.get("minds").get(), "minds enabled by default");
        helper.assertFalse(CreatureToggles.isDisabled(type), "minds not in the disabled set");
        Entity minds = helper.spawn(type, new BlockPos(1, 2, 1));
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(minds.isAlive() && minds.isAddedToWorld(), "minds should stay");
            helper.assertFalse(minds.getTags().contains(CreatureToggles.ALLOWED_TAG), "no tag when enabled");
            helper.assertTrue(count(helper, type) >= 1, "minds in the world");
            helper.succeed();
        });
    }

    // Every creature switch exists and defaults to true (with all on, the mod behaves as before)
    @GameTest(template = TEMPLATE, batch = BATCH)
    public void f10AllCreaturesEnabledByDefault(GameTestHelper helper) {
        helper.assertTrue(Config.CREATURE_ENABLED.size() == 22, "22 creature switches, found " + Config.CREATURE_ENABLED.size());
        for (var entry : Config.CREATURE_ENABLED.entrySet()) {
            helper.assertTrue(entry.getValue().getDefault(), entry.getKey() + " should default to true");
            helper.assertTrue(net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.containsKey(CreatureFeature.getId(entry.getKey())),
                    entry.getKey() + " is a registered entity");
        }
        helper.succeed();
    }
}
