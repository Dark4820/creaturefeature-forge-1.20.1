package net.atired.creaturefeature.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * PORT: port-only mixin. In 1.21 the sleepy effect lowers the generic JUMP_STRENGTH attribute by 100%
 * (multiplied base), so jump power drops to 0. 1.20.1 has no jump attribute for players and mobs, so the jump
 * power (0.42 * block jump factor) is zeroed here while the entity has the effect; jump boost still adds on top,
 * as in 1.21.
 */
@Mixin(LivingEntity.class)
public abstract class SleepyJumpMixin {
    @ModifyReturnValue(method = "getJumpPower", at = @At("RETURN"))
    private float creaturefeature$sleepyJump(float original) {
        if (((LivingEntity) (Object) this).hasEffect(CFMobEffectInit.SLEEPY.get())) {
            return 0.0f;
        }
        return original;
    }
}
