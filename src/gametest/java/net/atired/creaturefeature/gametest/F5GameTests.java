package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.entity.*;
import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * PORT: port-only tests for phase F5 (minedflayer, dreamweaver, cannonball_crab, mockingbird, pathogen, blossom,
 * saint_solis, stained_glass + murky_pearl, feather, vitric_arrow, star, spat_block, eep and their items).
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F5GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    @GameTest(template = TEMPLATE)
    public static void f5SpawnEggsSpawnTheirCreature(GameTestHelper helper) {
        List<Supplier<? extends Item>> eggs = List.of(CFItemInit.MINEDFLAYER_EGG, CFItemInit.DREAMWEAVER_EGG, CFItemInit.RUNAWAY_EGG,
                CFItemInit.MOCKINGBIRD_EGG, CFItemInit.PATHOGEN_EGG, CFItemInit.BLOSSOM_EGG, CFItemInit.SAINT_SOLIS_EGG, CFItemInit.STAINED_GLASS_EGG);
        List<Supplier<? extends EntityType<?>>> types = List.of(CFEntityInit.MINEDFLAYER, CFEntityInit.DREAMWEAVER, CFEntityInit.CANNONBALL_CRAB,
                CFEntityInit.MOCKINGBIRD, CFEntityInit.PATHOGEN, CFEntityInit.BLOSSOM, CFEntityInit.SAINT_SOLIS, CFEntityInit.STAINED_GLASS);
        Player player = helper.makeMockPlayer();
        for (int i = 0; i < eggs.size(); i++) {
            BlockPos floor = new BlockPos(i % 3 * 3 + 1, 1, i / 3 * 3 + 1);
            helper.setBlock(floor, Blocks.STONE);
            BlockPos abs = helper.absolutePos(floor);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(eggs.get(i).get()));
            eggs.get(i).get().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
            helper.assertEntityPresent(types.get(i).get());
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f5LootTablesDropTheOriginalItems(GameTestHelper helper) {
        Player killer = helper.makeMockPlayer();
        assertDrops(helper, CFEntityInit.BLOSSOM.get(), killer, Set.of(Items.ROTTEN_FLESH, Items.IRON_NUGGET, Items.HONEYCOMB, Items.MELON_SEEDS));
        assertDrops(helper, CFEntityInit.CANNONBALL_CRAB.get(), killer, Set.of(Items.COPPER_INGOT, CFItemInit.CARAPACE.get()));
        assertDrops(helper, CFEntityInit.DREAMWEAVER.get(), killer, Set.of(CFItemInit.DREAM_SILK.get(), Items.STRING, Items.SPIDER_EYE));
        assertDrops(helper, CFEntityInit.MINEDFLAYER.get(), killer, Set.of(CFItemInit.MINEDFLAYER_GOOP.get()));
        // the 1.21 "smelt when burning or killed with fire aspect" condition, converted to 1.20.1: the table loads
        assertDrops(helper, CFEntityInit.MOCKINGBIRD.get(), killer, Set.of(Items.FEATHER, Items.ROTTEN_FLESH, Items.CHICKEN));
        assertDrops(helper, CFEntityInit.SAINT_SOLIS.get(), killer, Set.of(Items.BLAZE_POWDER, Items.LEATHER));
        assertDrops(helper, CFEntityInit.NOTHING.get(), killer, Set.of(CFItemInit.MURKY_PEARL.get()));
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
    public static void gravityReplacementsMatchTheOriginal(GameTestHelper helper) {
        MinedFlayerEntity flayer = helper.spawnWithNoFreeWill(CFEntityInit.MINEDFLAYER.get(), new BlockPos(1, 2, 1));
        PathogenesisEntity pathogen = helper.spawnWithNoFreeWill(CFEntityInit.PATHOGEN.get(), new BlockPos(4, 2, 1));
        StainedGlassEntity glass = helper.spawnWithNoFreeWill(CFEntityInit.STAINED_GLASS.get(), new BlockPos(7, 2, 1));
        helper.assertTrue(flayer.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) == 0.0, "minedflayer gravity");
        helper.assertTrue(Math.abs(pathogen.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) - 0.01) < 1e-6, "pathogen gravity");
        helper.assertTrue(Math.abs(glass.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) - 0.01) < 1e-6, "stained glass gravity");
        // vitric arrow: vanilla 0.05 gravity is off, the original's variable gravity (0.1 - horizontal speed) is applied
        VitricArrowEntity arrow = new VitricArrowEntity(helper.getLevel(), helper.absoluteVec(new Vec3(4.5, 3, 4.5)).x, helper.absoluteVec(new Vec3(4.5, 3, 4.5)).y,
                helper.absoluteVec(new Vec3(4.5, 3, 4.5)).z, new ItemStack(CFItemInit.VITRIC_ARROW.get()), null);
        helper.assertTrue(arrow.isNoGravity(), "vitric arrow keeps vanilla gravity");
        arrow.setDeltaMovement(0, 0, 0);
        helper.getLevel().addFreshEntity(arrow);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(arrow.getDeltaMovement().y < -0.05, "a still vitric arrow should fall faster than vanilla: " + arrow.getDeltaMovement());
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void throwablesThrowTheirEntities(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        player.setPos(helper.absoluteVec(new Vec3(4.5, 2, 4.5)));
        AABB area = new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(30);
        throwOnce(helper, player, CFItemInit.MURKY_PEARL.get());
        throwOnce(helper, player, CFItemInit.DOWN_FEATHER.get());
        throwOnce(helper, player, CFItemInit.SOLAR_SHARD.get());
        helper.assertTrue(helper.getLevel().getEntities(CFEntityInit.MURKY_PEARL.get(), area, e -> true).size() == 1, "murky pearl");
        helper.assertTrue(helper.getLevel().getEntities(CFEntityInit.FEATHER.get(), area, e -> true).size() == 3, "down feather throws 3");
        helper.assertTrue(helper.getLevel().getEntities(CFEntityInit.STAR.get(), area, e -> true).size() == 8, "solar shard throws 8");
        AbstractArrow arrow = ((ArrowItem) CFItemInit.VITRIC_ARROW.get()).createArrow(helper.getLevel(), new ItemStack(CFItemInit.VITRIC_ARROW.get()), player);
        helper.assertTrue(arrow instanceof VitricArrowEntity, "vitric arrow item");
        helper.assertTrue(ForgeHooks.getBurnTime(new ItemStack(CFItemInit.SOLAR_SHARD.get()), null) == 1600 * 8, "solar shard fuel");
        helper.getLevel().getEntities((net.minecraft.world.entity.Entity) null, area, e -> e instanceof net.minecraft.world.entity.projectile.Projectile).forEach(e -> e.discard());
        helper.succeed();
    }

    private static void throwOnce(GameTestHelper helper, Player player, Item item) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, 2));
        item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.getMainHandItem().getCount() == 1, item + " was not consumed");
    }

    @GameTest(template = TEMPLATE)
    public static void dispenserShootsMurkyPearls(GameTestHelper helper) {
        BlockPos pos = new BlockPos(4, 2, 4);
        helper.setBlock(pos, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.UP));
        DispenserBlockEntity dispenser = (DispenserBlockEntity) helper.getBlockEntity(pos);
        dispenser.setItem(0, new ItemStack(CFItemInit.MURKY_PEARL.get()));
        helper.pulseRedstone(pos.east(), 2);
        // the pearl flies up out of the test area: look for it around; a dropped item would mean no dispenser behaviour
        AABB around = new AABB(helper.absolutePos(pos)).inflate(40);
        helper.succeedWhen(() -> {
            helper.assertTrue(dispenser.isEmpty(), "the dispenser did not fire");
            // the original's dispenser constructor (x, y, z) gives the pearl the SNOWBALL entity type: look for the class
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(MurkyPearlEntity.class, around).size() == 1, "no murky pearl entity");
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, around).isEmpty(), "the pearl was dropped as an item");
        });
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void eepBlockExplodesIntoSleep(GameTestHelper helper) {
        BlockPos eep = new BlockPos(4, 2, 4);
        helper.setBlock(eep.below(), Blocks.STONE);
        helper.setBlock(eep, CFBlockInit.EEP.get());
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(6, 2, 4));
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL));
        BlockPos abs = helper.absolutePos(eep);
        helper.getLevel().getBlockState(abs).use(helper.getLevel(), player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
        helper.assertBlockNotPresent(CFBlockInit.EEP.get(), eep);
        helper.assertEntityPresent(CFEntityInit.EEP.get());
        helper.succeedWhen(() -> {
            helper.assertEntityNotPresent(CFEntityInit.EEP.get());
            helper.assertTrue(zombie.hasEffect(CFMobEffectInit.SLEEPY.get()), "the zombie is not sleepy");
        });
    }
}
