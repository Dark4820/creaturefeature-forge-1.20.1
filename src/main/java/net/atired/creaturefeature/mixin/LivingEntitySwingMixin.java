package net.atired.creaturefeature.mixin;

import net.atired.creaturefeature.entity.CanaryEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * PORT: port-only mixin. The original CanaryEntity overrides getCurrentSwingDuration (public in 1.21) to double it;
 * in 1.20.1 the method is private, so the canary's longer swing is applied here instead.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySwingMixin {
    @Inject(method = "getCurrentSwingDuration", at = @At("RETURN"), cancellable = true)
    private void creaturefeature$canarySwing(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof CanaryEntity) {
            cir.setReturnValue(cir.getReturnValue() * 2);
        }
    }
}
