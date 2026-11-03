package net.atired.creaturefeature.items;

import net.minecraft.world.level.Level;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DoohickeyBlockItem extends BlockItem {
    public DoohickeyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // PORT: appendHoverText(stack, TooltipContext, ...) -> appendHoverText(stack, Level, ...)
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        MutableComponent mutablecomponent = Component.translatable("desc.creaturefeature.doohickey");
        tooltipComponents.add(mutablecomponent.withStyle(ChatFormatting.GRAY));
    }
}
