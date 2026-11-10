package net.atired.creaturefeature.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Consumer;

/**
 * PORT: port-only, client side. Icons of the port's own Infested / Oozing effects: a vanilla item drawn in place of
 * the effect sprite (in the inventory and on the HUD), so no texture of 1.21 has to be copied.
 */
public class CFEffectIcons {
    public static void item(Consumer<IClientMobEffectExtensions> consumer, Item item) {
        ItemStack stack = new ItemStack(item);
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                guiGraphics.renderItem(stack, x + 7, y + 8);
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
                guiGraphics.renderItem(stack, x + 4, y + 4);
                return true;
            }
        });
    }
}
