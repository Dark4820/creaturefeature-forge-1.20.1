package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.entity.*;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * PORT: port-only tests for phase F3 (minds, machination, sinister, beauty, detritus).
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F3GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    private static final List<Supplier<? extends EntityType<?>>> TYPES = List.of(
            CFEntityInit.MINDS, CFEntityInit.MACHINATION, CFEntityInit.SINISTER, CFEntityInit.BEAUTY, CFEntityInit.DETRITUS);

    @GameTest(template = TEMPLATE)
    public static void spawnEggsSpawnTheirCreature(GameTestHelper helper) {
        List<Supplier<? extends Item>> eggs = List.of(CFItemInit.MINDS_EGG, CFItemInit.MACHINATION_EGG, CFItemInit.SINISTER_EGG,
                CFItemInit.BEAUTY_EGG, CFItemInit.DETRITUS_EGG);
        Player player = helper.makeMockPlayer();
        for (int i = 0; i < eggs.size(); i++) {
            BlockPos floor = new BlockPos(1 + i * 2 % 8, 1, 1 + i * 2 / 8 * 3);
            helper.setBlock(floor, Blocks.STONE);
            BlockPos abs = helper.absolutePos(floor);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(eggs.get(i).get()));
            eggs.get(i).get().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
            helper.assertEntityPresent(TYPES.get(i).get(), floor.above());
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void lootTablesDropTheOriginalItems(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player killer = helper.makeMockPlayer();
        assertDrops(helper, CFEntityInit.MINDS.get(), killer, Set.of(Items.ROTTEN_FLESH, CFItemInit.BLIGHTED_BRAIN.get()));
        assertDrops(helper, CFEntityInit.MACHINATION.get(), killer, Set.of(Items.GOLD_NUGGET, CFItemInit.THINGAMABOB.get()));
        assertDrops(helper, CFEntityInit.SINISTER.get(), killer, Set.of(Items.MUD));
        assertDrops(helper, CFEntityInit.BEAUTY.get(), killer, Set.of(Items.MOSS_BLOCK));
        assertDrops(helper, CFEntityInit.DETRITUS.get(), killer, Set.of(Items.ROTTEN_FLESH, Items.BONE, Items.OAK_PLANKS));
        helper.succeed();
    }

    private static void assertDrops(GameTestHelper helper, EntityType<? extends Mob> type, Player killer, Set<Item> expected) {
        ServerLevel level = helper.getLevel();
        LootTable table = level.getServer().getLootData().getLootTable(type.getDefaultLootTable());
        helper.assertTrue(table != LootTable.EMPTY, "no loot table for " + type);
        Mob mob = helper.spawnWithNoFreeWill(type, new BlockPos(4, 2, 4));
        DamageSource source = level.damageSources().playerAttack(killer);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.ORIGIN, mob.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, killer)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, killer)
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, killer)
                .create(LootContextParamSets.ENTITY);
        Set<Item> dropped = new HashSet<>();
        for (int i = 0; i < 300; i++) {
            table.getRandomItems(params).forEach(stack -> dropped.add(stack.getItem()));
        }
        mob.discard();
        helper.assertTrue(dropped.containsAll(expected), type + " dropped " + dropped + ", expected " + expected);
    }

    @GameTest(template = TEMPLATE)
    public static void mindsLosesItsBrainWhenThePartIsHit(GameTestHelper helper) {
        MindsEntity minds = helper.spawnWithNoFreeWill(CFEntityInit.MINDS.get(), new BlockPos(4, 2, 4));
        helper.assertTrue(minds.isMultipartEntity() && minds.getParts() != null, "minds has no brain part");
        minds.mind.hurt(minds.damageSources().playerAttack(helper.makeMockPlayer()), 1.0f);
        helper.assertTrue(minds.hasNoMind(), "the brain was not popped");
        helper.assertTrue(minds.getParts() == null && !minds.isMultipartEntity(), "parts still active");
        helper.assertTrue(minds.getHealth() < minds.getMaxHealth(), "popping the brain did no damage");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void sinisterMirrorBlocksFrontHits(GameTestHelper helper) {
        SinisterEntity sinister = helper.spawnWithNoFreeWill(CFEntityInit.SINISTER.get(), new BlockPos(4, 2, 4));
        Player player = helper.makeMockPlayer();
        float max = sinister.getHealth();
        // The sinister looks to +Z (yRot 0): the mirror is in front of it
        player.setPos(sinister.position().add(0, 0, 2));
        helper.assertTrue(!sinister.hurt(sinister.damageSources().playerAttack(player), 6.0f), "front hit was not blocked");
        helper.assertTrue(sinister.getHealth() == max, "front hit did damage");
        player.setPos(sinister.position().add(0, 0, -2));
        helper.assertTrue(sinister.hurt(sinister.damageSources().playerAttack(player), 6.0f), "back hit was blocked");
        helper.assertTrue(sinister.getHealth() < max, "back hit did no damage");
        // The melee goal never reaches (original: isWithinMeleeAttackRange is always false)
        // (spawnWithNoFreeWill removes the goals: check them on a normally spawned sinister)
        SinisterEntity walker = helper.spawn(CFEntityInit.SINISTER.get(), new BlockPos(1, 2, 1));
        helper.assertTrue(walker.goalSelector.getAvailableGoals().stream().anyMatch(g -> g.getGoal() instanceof SinisterEntity.SinisterMeleeGoal),
                "no sinister melee goal");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void beautyDodgesTheFirstHitAndFloats(GameTestHelper helper) {
        BeautyEntity beauty = helper.spawn(CFEntityInit.BEAUTY.get(), new BlockPos(4, 2, 4));
        DamageSource hit = beauty.damageSources().playerAttack(helper.makeMockPlayer());
        helper.assertTrue(!beauty.hurt(hit, 3.0f), "first hit was not dodged");
        helper.assertTrue(beauty.getSpin() == 1.5f && beauty.getHealth() == beauty.getMaxHealth(), "dodge: spin " + beauty.getSpin());
        helper.runAfterDelay(2, () -> {
            // gravity = 0.08 * 0.7 * (1 - spin): negative while spin > 1
            double gravity = beauty.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
            helper.assertTrue(gravity < 0, "gravity while spinning: " + gravity);
            helper.succeedWhen(() -> {
                helper.assertTrue(beauty.getSpin() == 0.0f, "still spinning");
                double rest = beauty.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
                helper.assertTrue(Math.abs(rest - 0.08 * 0.7) < 1e-6, "gravity at rest: " + rest);
            });
        });
    }

    @GameTest(template = TEMPLATE)
    public static void detritusIsUndeadAndSplits(GameTestHelper helper) {
        DetritusEntity detritus = helper.spawnWithNoFreeWill(CFEntityInit.DETRITUS.get(), new BlockPos(4, 2, 4));
        helper.assertTrue(detritus.getMobType() == MobType.UNDEAD, "detritus is not undead");
        AABB around = detritus.getBoundingBox().inflate(16);
        int before = helper.getLevel().getEntities(CFEntityInit.DETRITUS.get(), around, e -> true).size();
        // 12 -> 8 health: births at 12, 10.2 and 8.4 health (original loop)
        detritus.hurt(detritus.damageSources().playerAttack(helper.makeMockPlayer()), 4.0f);
        int after = helper.getLevel().getEntities(CFEntityInit.DETRITUS.get(), around, e -> true).size();
        helper.assertTrue(after - before == 3, "births: " + (after - before));
        helper.getLevel().getEntities(CFEntityInit.DETRITUS.get(), around, e -> e != detritus).forEach(child -> {
            helper.assertTrue(child.getCopy() > 0.3f && child.getMaxHealth() < 12.0f, "child copy " + child.getCopy() + " max " + child.getMaxHealth());
            child.discard();
        });
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void attributesMatchTheOriginal(GameTestHelper helper) {
        MachinationEntity machination = helper.spawnWithNoFreeWill(CFEntityInit.MACHINATION.get(), new BlockPos(2, 2, 2));
        helper.assertTrue(machination.getArmorValue() == 10 && machination.getMaxHealth() == 20.0f, "machination attributes");
        helper.assertTrue(Math.abs(machination.getEyeHeight() - 1.58f) < 1e-4, "machination eye height " + machination.getEyeHeight());
        SinisterEntity sinister = helper.spawnWithNoFreeWill(CFEntityInit.SINISTER.get(), new BlockPos(6, 2, 6));
        helper.assertTrue(sinister.getMaxHealth() == 35.0f, "sinister health");
        BeautyEntity beauty = helper.spawnWithNoFreeWill(CFEntityInit.BEAUTY.get(), new BlockPos(2, 2, 6));
        helper.assertTrue(beauty.getMaxHealth() == 10.0f, "beauty health");
        helper.succeed();
    }
}
