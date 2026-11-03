package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFItemInit;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/**
 * PORT: port-only tests for the compatibility added by the port (PORT_MAP, "Agregados del port").
 * The dev environment has no Goety: these tests also prove that everything works without it.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class CompatGameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;
    private static final TagKey<Item> ECTOPLASM = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ectoplasm"));

    @GameTest(template = TEMPLATE)
    public static void goetyConversionRecipesNeedGoety(GameTestHelper helper) {
        // forge:mod_loaded goety: without Goety (dev environment) the 1:1 conversion recipes are not loaded
        for (String id : List.of("compat/goety/ectoplasm_from_goety", "compat/goety/ectoplasm_to_goety")) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(CreatureFeature.getId(id)).isEmpty(), id + " loaded without Goety");
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void ectoplasmRecipesUseTheSharedTag(GameTestHelper helper) {
        ItemStack ectoplasm = new ItemStack(CFItemInit.ECTOPLASM.get());
        helper.assertTrue(ectoplasm.is(ECTOPLASM), "creaturefeature:ectoplasm is not in #forge:ectoplasm");
        // goety:ectoplasm is an optional entry: without Goety the tag still loads, with only this mod's ectoplasm
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.ITEM).getTag(ECTOPLASM)
                .map(set -> set.size() == 1).orElse(false), "the tag does not load as expected without Goety");
        for (String id : List.of("bi_scroll", "pan_scroll", "pride_scroll", "trans_scroll", "torch")) {
            Recipe<?> recipe = helper.getLevel().getRecipeManager().byKey(CreatureFeature.getId(id)).orElse(null);
            helper.assertTrue(recipe != null, "missing recipe " + id);
            boolean usesTag = recipe.getIngredients().stream().anyMatch(ing -> ing.test(ectoplasm) && !ing.test(new ItemStack(Items.PAPER))
                    && List.of(ing.getItems()).stream().allMatch(s -> s.is(ECTOPLASM)));
            helper.assertTrue(usesTag, id + " does not take the ectoplasm through the tag");
        }
        helper.succeed();
    }
}
