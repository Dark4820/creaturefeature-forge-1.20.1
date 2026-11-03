package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.recipe.FlowerCloneRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: NeoForge DeferredRegister/Supplier -> Forge DeferredRegister/RegistryObject (same id). The original's unused
// register()/init() helpers are dropped.
public class CFRecipeSerialisers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreatureFeature.MODID);
    public static final RegistryObject<RecipeSerializer<FlowerCloneRecipe>> FLOWER_CLONE = RECIPES.register("crafting_special_flowerclone",()-> new SimpleCraftingRecipeSerializer<>(FlowerCloneRecipe::new));
}
