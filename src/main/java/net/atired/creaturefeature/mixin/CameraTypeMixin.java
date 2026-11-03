package net.atired.creaturefeature.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CameraType.class)
public class CameraTypeMixin {
    // PORT: Holder<MobEffect> -> CFMobEffectInit.SLEEPY.get()
    @ModifyReturnValue(method = "isFirstPerson",at=@At("RETURN"))
    private boolean firstPerson(boolean original){
        if(Minecraft.getInstance().player!=null&&Minecraft.getInstance().player.hasEffect(CFMobEffectInit.SLEEPY.get())){
            return false;
        }
        return original;
    }
}
