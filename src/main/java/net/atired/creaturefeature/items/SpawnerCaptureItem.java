package net.atired.creaturefeature.items;

import net.minecraft.world.level.Level;

import net.atired.creaturefeature.entity.BlitzEntity;
import net.atired.creaturefeature.init.CFDataComponentTypeInit;
import net.minecraft.ChatFormatting;
// PORT: the original's unused client import (AbstractClientPlayer) is dropped: this item loads on the server
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpawnerCaptureItem extends Item {

    public SpawnerCaptureItem( Properties properties) {
        super( properties);
    }



    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.creaturefeature.dreamcatcher");
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // PORT: appendHoverText(stack, TooltipContext, ...) -> appendHoverText(stack, Level, ...)
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        MutableComponent mutablecomponent = Component.translatable("desc.creaturefeature.dreamcatcher");
        tooltipComponents.add(mutablecomponent.withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // PORT: data components (1.20.5+) -> NBT: DREAMCATCHER_CONTENTS through CFDataComponentTypeInit, BLOCK_ENTITY_DATA as
        // the item's BlockEntityTag; BlockEntity.loadCustomOnly/saveToItem(…, registries) -> load/saveToItem
        DreamCatcherContents contents = CFDataComponentTypeInit.getDreamcatcherContents(context.getItemInHand());
        BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());
        if(contents.items().iterator().hasNext()&&(context.getLevel().isEmptyBlock(pos)||context.getLevel().getBlockState(pos).canBeReplaced())){
            ItemStack itemStack = contents.items().iterator().next();
            BlockItem item = (BlockItem)itemStack.getItem();
            BlockState state = item.getBlock().defaultBlockState();
            context.getLevel().setBlock(pos,state,2);
            // PORT: "x instanceof BlockEntity b" with x already a BlockEntity is a Java 21 pattern (a null check); Java 17 needs it written out
            BlockEntity spawnerBlockEntity = context.getLevel().getBlockEntity(pos);
            if(spawnerBlockEntity!=null&&BlockItem.getBlockEntityData(itemStack)!=null){
                spawnerBlockEntity.load(BlockItem.getBlockEntityData(itemStack).copy());

            }
            context.getPlayer().swing(context.getHand());

            CFDataComponentTypeInit.setDreamcatcherContents(context.getItemInHand(), DreamCatcherContents.EMPTY);
            return InteractionResult.SUCCESS;
        }
        if(!contents.items().iterator().hasNext()&&!context.getLevel().getBlockState(context.getClickedPos()).isAir()/* PORT: isEmpty() (1.20.5+) -> isAir() */&&context.getLevel() instanceof ServerLevel serverLevel){
            BlockState stated  =context.getLevel().getBlockState(context.getClickedPos());
            if(stated.getBlock().defaultDestroyTime()<0.0f){
                return InteractionResult.FAIL;
            }
            ItemStack stacked = stated.getBlock().asItem().getDefaultInstance();
            if(stacked.isEmpty()||stacked.getItem()==Items.AIR)return InteractionResult.FAIL;
            context.getLevel().playSound(null,context.getClickedPos(), stated.getBlock().getSoundType(stated,context.getLevel(),context.getClickedPos(),null).getBreakSound(), SoundSource.PLAYERS,1.2f,0.8f);
            context.getLevel().playSound(null,context.getClickedPos(), SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS,1.1f,0.6f);
            BlockEntity spawnerBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos()); // PORT: Java 17, see above
            if(spawnerBlockEntity!=null){
                spawnerBlockEntity.saveToItem(stacked);
                if(spawnerBlockEntity instanceof Container){
                    if(context.getPlayer() instanceof ServerPlayer serverPlayer){
                        serverPlayer.displayClientMessage(Component.translatable("info.creaturefeature.dreamcatcher").withStyle(style -> style.withColor(0xAAAAAA)) /* PORT: withColor(int) -> withStyle */,true);
                    }
                    return InteractionResult.FAIL;
                }
            }
            CFDataComponentTypeInit.setDreamcatcherContents(context.getItemInHand(), new DreamCatcherContents(List.of(stacked)));
            Vec3 center = context.getClickedPos().getCenter();
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,stated),center.x,center.y,center.z,20,0.3,0.3,0.3,0.3);
            context.getLevel().destroyBlock(context.getClickedPos(),false);

            context.getPlayer().swing(context.getHand());
            return InteractionResult.SUCCESS;

        }
        return super.useOn(context);
    }
}
