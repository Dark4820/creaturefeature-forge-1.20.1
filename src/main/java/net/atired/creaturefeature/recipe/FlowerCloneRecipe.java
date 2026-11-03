package net.atired.creaturefeature.recipe;

import net.atired.creaturefeature.init.CFRecipeSerialisers;
import net.atired.creaturefeature.items.BouquetItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

// PORT: 1.21's CraftingInput is the grid trimmed to the occupied area, so the original's "input.size() != 2" means
// "the occupied area is exactly 2 slots" (the flower and the bouquet next to each other, in a row or a column).
// 1.20.1 gives the whole CraftingContainer: occupiedArea() computes that same trimmed size. CustomRecipe takes an id.
public class FlowerCloneRecipe  extends CustomRecipe {
    public FlowerCloneRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private static int occupiedArea(CraftingContainer input) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -1, maxY = -1;
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                if (!input.getItem(x + y * input.getWidth()).isEmpty()) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        return maxX < 0 ? 0 : (maxX - minX + 1) * (maxY - minY + 1);
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack2 = ItemStack.EMPTY;
        if(occupiedArea(input)!=2){
            return false;
        }
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack itemstack1 = input.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ItemTags.FLOWERS)) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }
                    itemstack = itemstack1;
                } else {
                    if (!(itemstack1.getItem() instanceof BouquetItem)) {
                        return false;
                    }
                    itemstack2=itemstack1;
                }
            }
        }
        return !itemstack.isEmpty() && !itemstack2.isEmpty();
    }

    // PORT: assemble(CraftingInput, HolderLookup.Provider) -> assemble(CraftingContainer, RegistryAccess)
    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        ItemStack itemstack2 = ItemStack.EMPTY;
        ItemStack itemstack = ItemStack.EMPTY;
        if(occupiedArea(input)!=2){
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack itemstack1 = input.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ItemTags.FLOWERS)) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack1.copy();
                } else {
                    if (!(itemstack1.getItem() instanceof BouquetItem)) {
                        return ItemStack.EMPTY;
                    }
                    itemstack2=itemstack1;
                }
            }
        }
        ItemStack clone = itemstack.copy();
        clone.setCount(2);
        return !itemstack.isEmpty() && !itemstack2.isEmpty() ? clone : ItemStack.EMPTY;
    }
    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return CFRecipeSerialisers.FLOWER_CLONE.get();
    }
}
