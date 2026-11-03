package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.entity.*;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
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
 * PORT: port-only tests for phase F4 (fiend, fend, friend, blitz, eeper, nothing, canary, vertigo, coat_of_arms,
 * bullet, kicked_block, flintlock).
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F4GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    @GameTest(template = TEMPLATE)
    public static void f4SpawnEggsSpawnTheirCreature(GameTestHelper helper) {
        List<Supplier<? extends Item>> eggs = List.of(CFItemInit.FIEND_EGG, CFItemInit.FEND_EGG, CFItemInit.FRIEND_EGG, CFItemInit.BLITZ_EGG,
                CFItemInit.EEPER_EGG, CFItemInit.NOTHING_EGG, CFItemInit.CANARY_EGG, CFItemInit.VERTIGO_EGG, CFItemInit.COAT_OF_ARMS_EGG);
        List<Supplier<? extends EntityType<?>>> types = List.of(CFEntityInit.FIEND, CFEntityInit.FEND, CFEntityInit.FRIEND, CFEntityInit.BLITZ,
                CFEntityInit.EEPER, CFEntityInit.NOTHING, CFEntityInit.CANARY, CFEntityInit.VERTIGO, CFEntityInit.COATOFARMS);
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
    public static void f4LootTablesDropTheOriginalItems(GameTestHelper helper) {
        Player killer = helper.makeMockPlayer();
        assertDrops(helper, CFEntityInit.BLITZ.get(), killer, Set.of(CFItemInit.BLITZ_ROD.get()));
        assertDrops(helper, CFEntityInit.CANARY.get(), killer, Set.of(Items.IRON_NUGGET, Items.IRON_INGOT));
        assertDrops(helper, CFEntityInit.COATOFARMS.get(), killer, Set.of(CFItemInit.FLINTLOCK.get(), CFItemInit.COAT_SCRAPS.get()));
        assertDrops(helper, CFEntityInit.EEPER.get(), killer, Set.of(CFItemInit.SLEEPING_POWDER.get()));
        assertDrops(helper, CFEntityInit.FEND.get(), killer, Set.of(CFItemInit.ECTOPLASM.get(), Items.GLOWSTONE_DUST));
        assertDrops(helper, CFEntityInit.FIEND.get(), killer, Set.of(Items.QUARTZ, CFItemInit.FIENDISH_ESSENCE.get(), CFItemInit.FLINTLOCK.get()));
        assertDrops(helper, CFEntityInit.VERTIGO.get(), killer, Set.of(CFItemInit.VERTIGO_CHUNK.get(), Items.ROTTEN_FLESH));
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
    public static void attributeReplacementsMatchTheOriginal(GameTestHelper helper) {
        // getDefaultGravity overrides -> ENTITY_GRAVITY base values; STEP_HEIGHT -> setMaxUpStep
        CanaryPart part = helper.spawnWithNoFreeWill(CFEntityInit.CANARY_PART.get(), new BlockPos(1, 2, 1));
        CoatOfArmsEntity coat = helper.spawnWithNoFreeWill(CFEntityInit.COATOFARMS.get(), new BlockPos(4, 2, 1));
        FriendEntity friend = helper.spawnWithNoFreeWill(CFEntityInit.FRIEND.get(), new BlockPos(4, 2, 5));
        BlitzEntity blitz = helper.spawnWithNoFreeWill(CFEntityInit.BLITZ.get(), new BlockPos(1, 2, 7));
        double canaryGravity = CanaryEntity.createCanaryAttributes().build().getBaseValue(ForgeMod.ENTITY_GRAVITY.get());
        helper.assertTrue(Math.abs(canaryGravity - 0.01) < 1e-6, "canary gravity " + canaryGravity);
        helper.assertTrue(part.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) == 0.0, "canary part gravity");
        helper.assertTrue(Math.abs(coat.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) - 0.048) < 1e-6, "coat of arms gravity");
        helper.assertTrue(friend.getStepHeight() >= 10.0f, "friend step height " + friend.getStepHeight());
        helper.assertTrue(blitz.getStepHeight() >= 1.5f, "blitz step height " + blitz.getStepHeight());
        helper.assertTrue(Math.abs(blitz.getEyeHeight() - 0.78f) < 1e-4, "blitz eye height " + blitz.getEyeHeight());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void canarySwingsTwiceAsLong(GameTestHelper helper) {
        // LivingEntitySwingMixin: the original doubles getCurrentSwingDuration (6 ticks) for the canary
        CanaryEntity canary = helper.spawnWithNoFreeWill(CFEntityInit.CANARY.get(), new BlockPos(2, 2, 2));
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(6, 2, 6));
        canary.swing(InteractionHand.MAIN_HAND);
        zombie.swing(InteractionHand.MAIN_HAND);
        helper.runAfterDelay(9, () -> {
            helper.assertTrue(!zombie.swinging, "the zombie (normal swing) is still swinging");
            helper.assertTrue(canary.swinging, "the canary stopped swinging like a normal mob");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 80)
    public static void eeperExplodesIntoSleep(GameTestHelper helper) {
        EeperEntity eeper = helper.spawnWithNoFreeWill(CFEntityInit.EEPER.get(), new BlockPos(4, 2, 4));
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(5, 2, 4));
        eeper.setTarget(zombie);
        helper.succeedWhen(() -> {
            helper.assertTrue(eeper.isRemoved(), "the eeper did not explode (fuse " + eeper.getFuse() + ")");
            helper.assertTrue(zombie.hasEffect(CFMobEffectInit.SLEEPY.get()), "the target is not sleepy");
            helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "the explosion did no damage");
        });
    }

    @GameTest(template = TEMPLATE)
    public static void bulletHitsWhatItFaces(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(4, 2, 7));
        BulletEntity bullet = new BulletEntity(CFEntityInit.BULLET.get(), level);
        Vec3 from = helper.absoluteVec(new Vec3(4.5, 2.6, 1.5));
        bullet.setPos(from);
        bullet.setYRot(0.0f);   // facing +Z, towards the zombie
        bullet.setXRot(0.0f);
        level.addFreshEntity(bullet);
        helper.succeedWhen(() -> {
            helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "the bullet did not hit");
            helper.assertTrue(bullet.isRemoved(), "the bullet is still alive");
        });
    }

    @GameTest(template = TEMPLATE)
    public static void kickedBlockHitsAndBreaks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(4, 2, 6));
        KickedBlockEntity block = new KickedBlockEntity(CFEntityInit.KICKED_BLOCK.get(), level);
        block.state = Blocks.DIRT.defaultBlockState();
        block.setPos(helper.absoluteVec(new Vec3(4.5, 3.0, 2.5)));
        block.setDeltaMovement(0, 0.1, 0.8);
        level.addFreshEntity(block);
        helper.succeedWhen(() -> {
            // 5 damage minus the zombie's 2 armour points = 4.6
            helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth() - 4.0f, "the kicked block did not hit for 5 (health " + zombie.getHealth() + ")");
            helper.assertTrue(block.isRemoved(), "the kicked block is still alive");
        });
    }

    @GameTest(template = TEMPLATE)
    public static void friendFlattensAndFendEquips(GameTestHelper helper) {
        FriendEntity friend = helper.spawnWithNoFreeWill(CFEntityInit.FRIEND.get(), new BlockPos(4, 2, 4));
        DamageSource hit = friend.damageSources().playerAttack(helper.makeMockPlayer());
        helper.assertTrue(friend.hurt(hit, 2.0f), "first hit missed");
        helper.assertTrue(friend.getFlattened() == 2.0f, "not flattened: " + friend.getFlattened());
        friend.invulnerableTime = 0;
        helper.assertTrue(!friend.hurt(hit, 2.0f), "hit while flattened");

        FendEntity fend = helper.spawnWithNoFreeWill(CFEntityInit.FEND.get(), new BlockPos(1, 2, 1));
        fend.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(fend.blockPosition()), MobSpawnType.SPAWN_EGG, null, null);
        Item weapon = fend.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        helper.assertTrue(weapon == Items.BOW || weapon == Items.STONE_SWORD || weapon == Items.STONE_PICKAXE, "fend weapon " + weapon);
        helper.assertTrue(fend.getItemBySlot(EquipmentSlot.HEAD).is(Items.CHAINMAIL_HELMET), "fend helmet");
        helper.succeed();
    }
}
