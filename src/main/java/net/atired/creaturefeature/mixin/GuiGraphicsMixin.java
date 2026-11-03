package net.atired.creaturefeature.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.atired.creaturefeature.client.CreatureFeatureClient;
import net.atired.creaturefeature.init.CFDataComponentTypeInit;
import net.atired.creaturefeature.items.DreamCatcherContents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PORT: data components OPENED / DREAMCATCHER_CONTENTS -> the NBT helpers of CFDataComponentTypeInit. The original's
// unused shadows (blitSprite is 1.20.2+) are left out.
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow @Final private PoseStack pose;

    @Shadow protected abstract void renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed, int guiOffset);

    @Inject(method= "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",at=@At("HEAD"))
    private void renderSlotEvil(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci){
        if(stack!=null&& CreatureFeatureClient.PROXY.wobble>0.02f&&CreatureFeatureClient.PROXY.wobblyItem.getItem()==stack.getItem()&& Minecraft.getInstance().level!=null){
            if(CFDataComponentTypeInit.getOpened(stack)==0) return;
            float timed = Minecraft.getInstance().level.getGameTime();
            timed/=3.1f;
            pose.pushPose();
            pose.translate((float)(x + 8), (float)(y + 8),0);
            pose.scale(1.0f+Mth.sin(CreatureFeatureClient.PROXY.wobble*3.14f)/3.0f,1.0f-Mth.sin(CreatureFeatureClient.PROXY.wobble*3.14f)/4.0f,1);
            pose.translate(-(float)(x + 8), -(float)(y + 8),0);
            pose.rotateAround(new Quaternionf().rotationXYZ(0.0f,0.0f,Mth.sin(3.14f*CreatureFeatureClient.PROXY.wobble*6.0f)/12.0f),x+8,y+8,0);

        }
        // PORT: the NBT lookup only runs for stacks with a tag (most stacks have none)
        if(stack!=null&&stack.hasTag()&&CFDataComponentTypeInit.getDreamcatcherContents(stack).items().iterator().hasNext()){

            pose.pushPose();
            RenderSystem.enableBlend();
            pose.translate((float)(x + 6), (float)(y + 6),-10.8);
            pose.scale(0.75f,0.75f,1);
            pose.translate(-(float)(x + 6), -(float)(y + 6),0);
            pose.rotateAround(new Quaternionf().rotationXYZ(0.0f,0.0f,Mth.sin(Minecraft.getInstance().level.getGameTime()/10.0f)/3.0f),x+6,y+6,0);
            renderItem(entity,level,CFDataComponentTypeInit.getDreamcatcherContents(stack).items().iterator().next(),x-2,y-2,seed,guiOffset);
            pose.popPose();


        }
    }
    @Inject(method= "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",at=@At("RETURN"))
    private void renderSlotEviler(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci){
        if(stack!=null&&CreatureFeatureClient.PROXY.wobble>0.02f&&CreatureFeatureClient.PROXY.wobblyItem.getItem()==stack.getItem()&&Minecraft.getInstance().level!=null){
            if(CFDataComponentTypeInit.getOpened(stack)==0) return;
            pose.popPose();
        }
    }
}
