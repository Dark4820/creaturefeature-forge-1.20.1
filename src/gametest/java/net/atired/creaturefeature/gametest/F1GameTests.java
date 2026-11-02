package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.atired.creaturefeature.init.CFTrimMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

/**
 * PORT: port-only tests for phase F1 (blocks, simple items, effects, recipes, trims).
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F1GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    @GameTest(template = TEMPLATE)
    public static void blocksHaveItemsAndLootTables(GameTestHelper helper) {
        for (RegistryObject<Block> block : CFBlockInit.BLOCKS.getEntries()) {
            Item item = block.get().asItem();
            helper.assertTrue(item instanceof BlockItem, "no block item for " + block.getId());
            helper.assertTrue(helper.getLevel().getServer().getLootData()
                    .getElement(LootDataType.TABLE, block.get().getLootTable()) != null, "no loot table for " + block.getId());
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void recipesLoaded(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        String[] ids = {"bi_scroll", "carapace_bricks", "carapace_brick_stairs_stonecutting", "minedflayer_jelly", "wallpaper"};
        for (String id : ids) {
            Optional<? extends Recipe<?>> recipe = recipes.byKey(CreatureFeature.getId(id));
            helper.assertTrue(recipe.isPresent(), "missing recipe " + id);
        }
        ItemStack scroll = recipes.byKey(CreatureFeature.getId("bi_scroll")).get()
                .getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(scroll.is(CFItemInit.BI_SCROLL.get()), "bi_scroll recipe gives " + scroll);
        ItemStack stairs = recipes.byKey(CreatureFeature.getId("carapace_brick_stairs_stonecutting")).get()
                .getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(stairs.is(CFBlockInit.CARAPACE_BRICK_STAIRS.get().asItem()), "stonecutting gives " + stairs);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void trimMaterialsLoaded(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);
        for (ResourceKey<TrimMaterial> key : CFTrimMaterials.MATERIALS) {
            TrimMaterial material = registry.get(key);
            helper.assertTrue(material != null, "missing trim material " + key.location());
            helper.assertTrue(new ItemStack(material.ingredient().value()).is(ItemTags.TRIM_MATERIALS),
                    key.location() + " ingredient is not in #minecraft:trim_materials");
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void sleepySlowsDown(GameTestHelper helper) {
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(4, 2, 4));
        double before = zombie.getAttributeValue(Attributes.MOVEMENT_SPEED);
        zombie.addEffect(new MobEffectInstance(CFMobEffectInit.SLEEPY.get(), 200));
        double after = zombie.getAttributeValue(Attributes.MOVEMENT_SPEED);
        helper.assertTrue(Math.abs(after - before * 0.5) < 1.0E-6, "speed " + before + " -> " + after);
        helper.succeed();
    }

    // The sleepy zombie keeps trying to jump but stays on the floor; the control zombie jumps.
    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void sleepyCannotJump(GameTestHelper helper) {
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
            }
        }
        // Mobs with AI: the jump goes through JumpControl (a mob without AI never jumps)
        Zombie sleepy = helper.spawn(EntityType.ZOMBIE, new BlockPos(2, 2, 2));
        Zombie control = helper.spawn(EntityType.ZOMBIE, new BlockPos(6, 2, 6));
        sleepy.addEffect(new MobEffectInstance(CFMobEffectInit.SLEEPY.get(), 1200));
        double floor = helper.absolutePos(new BlockPos(0, 2, 0)).getY();
        double[] maxY = {floor, floor};
        helper.onEachTick(() -> {
            sleepy.getJumpControl().jump();
            control.getJumpControl().jump();
            maxY[0] = Math.max(maxY[0], sleepy.getY());
            maxY[1] = Math.max(maxY[1], control.getY());
        });
        helper.runAfterDelay(40, () -> {
            helper.assertTrue(maxY[1] > floor + 0.5, "control zombie did not jump (max " + (maxY[1] - floor) + ")");
            helper.assertTrue(maxY[0] < floor + 0.1, "sleepy zombie jumped (max " + (maxY[0] - floor) + ")");
            helper.succeed();
        });
    }
}
