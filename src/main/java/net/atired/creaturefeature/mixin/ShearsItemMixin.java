package net.atired.creaturefeature.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    // PORT: 1.21 builds the shears' Tool component in createToolProperties and the original appends a rule
    // (speed 8 on #creaturefeature:shearable) after the vanilla ones, so it only applies when no vanilla rule matched.
    // 1.20.1 has no Tool component: ShearsItem.getDestroySpeed returns 1.0 when none of its own blocks matched,
    // which is where the same rule goes.
    @ModifyReturnValue(method = "getDestroySpeed(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)F", at = @At("RETURN"))
    private float shearableSpeed(float original, ItemStack stack, BlockState state) {
        if (original == 1.0F && state.is(CFBlockTags.SHEARABLE)) {
            return 8.0F;
        }
        return original;
    }

    @ModifyReturnValue(method = "mineBlock(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z",at=@At("RETURN"))
    private boolean mineMyBlock(boolean original, ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving){
        // PORT: state.is(DeferredBlock) -> state.is(block)
        if(state.is(CFBlockInit.DREAM_SILK_SPOOL.get())||
                state.is(CFBlockInit.DOWN_FEATHERS.get())||state.is(CFBlockInit.DOWN_FEATHERS_CARPET.get())||
                state.is(CFBlockInit.MOSAIC_DOWN_FEATHERS.get())||state.is(CFBlockInit.MOSAIC_DOWN_FEATHERS_CARPET.get())){
            return true;
        }
        return original;
    }
}
