package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.accessors.NoBlockContainerLevelAccess;
import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.atired.creaturefeature.blocks.blockentities.DoohickeyBlockEntity;
import net.atired.creaturefeature.entity.VertigoEntity;
import net.atired.creaturefeature.init.*;
import net.atired.creaturefeature.items.DreamCatcherContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * PORT: port-only tests for phase F6 (items with logic and the common mixins).
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F6GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    @GameTest(template = TEMPLATE)
    public static void f6EveryRecipeFileLoads(GameTestHelper helper) {
        // a recipe that fails to parse (e.g. a 1.21-only vanilla item) is only an error in the log: catch it here.
        // compat/ recipes are conditional (forge:mod_loaded) and may be absent on purpose.
        var resources = helper.getLevel().getServer().getResourceManager()
                .listResources("recipes", rl -> rl.getNamespace().equals(CreatureFeature.MODID) && rl.getPath().endsWith(".json"));
        java.util.List<String> broken = new java.util.ArrayList<>();
        for (var rl : resources.keySet()) {
            String path = rl.getPath().substring("recipes/".length(), rl.getPath().length() - ".json".length());
            if (path.startsWith("compat/")) continue;
            if (helper.getLevel().getRecipeManager().byKey(CreatureFeature.getId(path)).isEmpty()) broken.add(path);
        }
        helper.assertTrue(broken.isEmpty(), "recipes that did not load: " + broken);
        helper.assertTrue(resources.size() > 40, "only " + resources.size() + " recipe files found");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6ShearsAndBedrockMixins(GameTestHelper helper) {
        ItemStack shears = new ItemStack(Items.SHEARS);
        // ShearsItemMixin: speed 8 on #creaturefeature:shearable, vanilla rules untouched
        helper.assertTrue(shears.getDestroySpeed(CFBlockInit.DREAM_SILK_SPOOL.get().defaultBlockState()) == 8.0f, "silk spool speed");
        helper.assertTrue(shears.getDestroySpeed(CFBlockInit.DOWN_FEATHERS.get().defaultBlockState()) == 8.0f, "feathers speed");
        helper.assertTrue(shears.getDestroySpeed(Blocks.WHITE_WOOL.defaultBlockState()) == 5.0f, "wool speed changed");
        helper.assertTrue(shears.getDestroySpeed(Blocks.STONE.defaultBlockState()) == 1.0f, "stone speed changed");
        // AbstractBlockMixin: the friend may spawn on bedrock, a zombie may not
        helper.setBlock(new BlockPos(4, 1, 4), Blocks.BEDROCK);
        BlockPos abs = helper.absolutePos(new BlockPos(4, 1, 4));
        helper.assertTrue(Blocks.BEDROCK.defaultBlockState().isValidSpawn(helper.getLevel(), abs, CFEntityInit.FRIEND.get()), "friend on bedrock");
        helper.assertTrue(!Blocks.BEDROCK.defaultBlockState().isValidSpawn(helper.getLevel(), abs, EntityType.ZOMBIE), "zombie on bedrock");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6BouquetClonesFlowers(GameTestHelper helper) {
        AbstractContainerMenu dummy = new CraftingMenu(0, helper.makeMockPlayer().getInventory());
        TransientCraftingContainer grid = new TransientCraftingContainer(dummy, 3, 3);
        grid.setItem(0, new ItemStack(Items.POPPY));
        grid.setItem(1, new ItemStack(CFItemInit.BOUQUET.get()));
        var match = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
        helper.assertTrue(match.isPresent(), "flower + bouquet side by side does not match");
        ItemStack result = match.get().assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.POPPY) && result.getCount() == 2, "result " + result);
        ItemStack remaining = match.get().getRemainingItems(grid).get(1);
        helper.assertTrue(remaining.is(CFItemInit.BOUQUET.get()) && remaining.getDamageValue() == 1, "bouquet remainder " + remaining);
        // diagonal: the occupied area is 2x2, not 2 (the original's trimmed CraftingInput)
        grid.setItem(1, ItemStack.EMPTY);
        grid.setItem(4, new ItemStack(CFItemInit.BOUQUET.get()));
        helper.assertTrue(helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel()).isEmpty(), "diagonal matched");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6DreamCatcherMovesASpawner(GameTestHelper helper) {
        BlockPos from = new BlockPos(2, 2, 2);
        helper.setBlock(from.below(), Blocks.STONE);
        helper.setBlock(from, Blocks.SPAWNER);
        ((SpawnerBlockEntity) helper.getBlockEntity(from)).setEntityId(EntityType.BLAZE, helper.getLevel().getRandom());
        Player player = helper.makeMockPlayer();
        ItemStack catcher = new ItemStack(CFItemInit.DREAM_CATCHER.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, catcher);
        BlockPos absFrom = helper.absolutePos(from);
        catcher.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absFrom), Direction.UP, absFrom, false)));
        helper.assertBlockNotPresent(Blocks.SPAWNER, from);
        DreamCatcherContents contents = CFDataComponentTypeInit.getDreamcatcherContents(catcher);
        helper.assertTrue(contents.items().iterator().hasNext(), "nothing caught");
        BlockPos floor = new BlockPos(6, 1, 6);
        helper.setBlock(floor, Blocks.STONE);
        BlockPos absFloor = helper.absolutePos(floor);
        catcher.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absFloor), Direction.UP, absFloor, false)));
        helper.assertBlockPresent(Blocks.SPAWNER, floor.above());
        CompoundTag data = helper.getBlockEntity(floor.above()).saveWithoutMetadata();
        helper.assertTrue(data.toString().contains("minecraft:blaze"), "the spawner lost its mob: " + data);
        helper.assertTrue(!CFDataComponentTypeInit.getDreamcatcherContents(catcher).items().iterator().hasNext(), "catcher not emptied");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6OpenMindCraftingStaysValid(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        // AbstractContainerMenuMixin: a crafting menu with NoBlockContainerLevelAccess (no crafting table) stays valid
        CraftingMenu menu = new CraftingMenu(1, player.getInventory(), new NoBlockContainerLevelAccess(player));
        helper.assertTrue(menu.stillValid(player), "open mind crafting menu closed");
        CraftingMenu normal = new CraftingMenu(2, player.getInventory(), net.minecraft.world.inventory.ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(new BlockPos(1, 1, 1))));
        helper.assertTrue(!normal.stillValid(player), "a crafting menu without a crafting table stayed valid");
        ItemStack mind = new ItemStack(CFItemInit.OPEN_MIND.get());
        CFDataComponentTypeInit.setOpened(mind, 1);
        helper.assertTrue(CFDataComponentTypeInit.getOpened(mind) == 1, "opened flag");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6FiendishHitsHarder(GameTestHelper helper) {
        // LivingEntityGoopMixin: an attacker with fiendish deals x1.8 and loses the effect
        Zombie attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(2, 2, 2));
        Zombie target = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(6, 2, 6));
        Zombie control = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(6, 2, 2));
        attacker.addEffect(new MobEffectInstance(CFMobEffectInit.FIENDISH.get(), 200, 0));
        float before = target.getHealth();
        target.hurt(target.damageSources().mobAttack(attacker), 5.0f);
        float fiendish = before - target.getHealth();
        Zombie plain = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(2, 2, 6));
        control.hurt(control.damageSources().mobAttack(plain), 5.0f);
        float normal = before - control.getHealth();
        helper.assertTrue(Math.abs(fiendish / normal - 1.8f) < 0.05f, "fiendish damage " + fiendish + " vs " + normal);
        helper.assertTrue(!attacker.hasEffect(CFMobEffectInit.FIENDISH.get()), "fiendish not consumed");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void f6BacterialZombieReleasesAPathogen(GameTestHelper helper) {
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(4, 2, 4));
        zombie.setHealth(10.0f);   // hurt: the countdown runs (original: a monster with a target or hurt)
        ((LivingEntityGoopAccessor) zombie).setBacterial(9.0f);
        helper.assertTrue(((LivingEntityGoopAccessor) zombie).getDelay() == 30, "delay not set");
        helper.succeedWhen(() -> helper.assertEntityPresent(CFEntityInit.PATHOGEN.get()));
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void f6UndeadSpawnInfectedSometimes(GameTestHelper helper) {
        // MonsterEntityMixin: 8 % of the undead get "bacterial" at finalizeSpawn (config pathogen = false)
        int infected = 0;
        for (int i = 0; i < 400; i++) {
            Zombie zombie = EntityType.ZOMBIE.create(helper.getLevel());
            zombie.moveTo(helper.absoluteVec(new Vec3(4.5, 2, 4.5)));
            zombie.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.NATURAL, null, null);
            if (((LivingEntityGoopAccessor) zombie).getDelay() > 0) infected++;
        }
        helper.assertTrue(infected >= 10 && infected <= 60, "infected " + infected + "/400 (expected about 32)");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6PlayerMixinTicks(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        helper.assertTrue(player instanceof PlayerBrainrotAccessor, "PlayerMixin not applied");
        PlayerBrainrotAccessor accessor = (PlayerBrainrotAccessor) player;
        accessor.setRabies(0.5f);
        accessor.setBrainrot(0.5f);
        for (int i = 0; i < 10; i++) player.tick();
        helper.assertTrue(Math.abs(accessor.getRabies() - 0.4f) < 0.011f, "rabies " + accessor.getRabies());
        helper.assertTrue(Math.abs(accessor.getBrainrot() - 0.3f) < 0.021f, "brainrot " + accessor.getBrainrot());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6ConsumablesAndGlass(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        new ItemStack(CFItemInit.THE_PILL.get()).finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(CFMobEffectInit.SLEEPY.get()), "the pill gives no sleepy");
        new ItemStack(CFItemInit.FIENDISH_SODA.get()).finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(CFMobEffectInit.FIENDISH.get()), "the soda gives no fiendish");
        // living glass: a hit removes one effect of the target and uses one shard
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(4, 2, 4));
        zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200));
        ItemStack glass = new ItemStack(CFItemInit.LIVING_GLASS_SHARDS.get(), 3);
        glass.hurtEnemy(zombie, player);
        helper.assertTrue(zombie.getActiveEffects().isEmpty() && glass.getCount() == 2, "living glass");
        // bacterium ball: infects the player who uses it
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(CFItemInit.BACTERIUM_BALL.get(), 2));
        CFItemInit.BACTERIUM_BALL.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(((LivingEntityGoopAccessor) player).getDelay() == 30, "bacterium ball did not infect");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6VertigoHornHasAnInstrument(GameTestHelper helper) {
        VertigoEntity vertigo = helper.spawnWithNoFreeWill(CFEntityInit.VERTIGO.get(), new BlockPos(4, 2, 4));
        ItemStack horn = vertigo.createHorn();
        helper.assertTrue(horn.is(CFItemInit.VERTIGO_HORN.get()) && horn.getTag() != null && horn.getTag().contains("instrument"), "horn " + horn.getTag());
        helper.assertTrue(horn.getUseDuration() > 0, "horn without use duration");
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, horn);
        horn.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.getCooldowns().isOnCooldown(CFItemInit.VERTIGO_HORN.get()), "the horn has no cooldown");
        helper.assertTrue(((PlayerBrainrotAccessor) player).getRabies() == 0.5f, "the horn did not set rabies");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void f6DoohickeyHoldsAnItem(GameTestHelper helper) {
        BlockPos pos = new BlockPos(4, 2, 4);
        helper.setBlock(pos, CFBlockInit.DOOHICKEY.get());
        DoohickeyBlockEntity be = (DoohickeyBlockEntity) helper.getBlockEntity(pos);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(CFItemInit.SLEEPING_POWDER.get()));
        BlockPos abs = helper.absolutePos(pos);
        helper.getLevel().getBlockState(abs).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
        helper.assertTrue(be.placedItem.is(CFItemInit.SLEEPING_POWDER.get()) && player.getMainHandItem().isEmpty(), "the doohickey did not take the powder");
        CompoundTag saved = be.saveWithoutMetadata();
        DoohickeyBlockEntity copy = new DoohickeyBlockEntity(abs, helper.getLevel().getBlockState(abs));
        copy.load(saved);
        helper.assertTrue(copy.placedItem.is(CFItemInit.SLEEPING_POWDER.get()), "save/load lost the item");
        helper.succeed();
    }
}
