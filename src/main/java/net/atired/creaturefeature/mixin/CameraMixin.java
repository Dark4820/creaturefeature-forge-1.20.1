package net.atired.creaturefeature.mixin;

import net.atired.creaturefeature.init.CFMobEffectInit;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow private boolean detached;

    // PORT: Holder<MobEffect> -> CFMobEffectInit.SLEEPY.get()
    @ModifyVariable(method = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at= @At(value = "HEAD"),ordinal = 0)
    private boolean detatchAmirite(boolean value,BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick){
        if(entity instanceof Player player && player.hasEffect(CFMobEffectInit.SLEEPY.get())){
            return true;
        }
        return value;
    }
    // PORT: setRotation(FFF)V (1.21, with roll) -> setRotation(FF)V; xRot is still the float of ordinal 1
    @ModifyVariable(method = "Lnet/minecraft/client/Camera;setRotation(FF)V",at=@At("HEAD"),ordinal = 1)
    private float setEvilFuckingRotation(float xRot){
        if(Minecraft.getInstance().player!=null&&Minecraft.getInstance().player.hasEffect(CFMobEffectInit.SLEEPY.get())){
            return Mth.lerp(0.66f,xRot,90.0f);
        }
        return xRot;
    }
}
