package net.atired.creaturefeature.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    // PORT: 1.21 passes the model colour as one ARGB int (-1 = opaque white, replaced with @ModifyConstant); 1.20.1
    // passes r, g, b, a floats (1, 1, 1, translucent ? 0.15 : 1). Same tint, applied when the original's -1 would be
    // used (alpha 1); the original built it with java.awt.Color(added, 1, 0.4+0.6*added).
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void awesomeRender(EntityModel<T> model, PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Operation<Void> original, T entity, float entityYaw, float partialTicks, PoseStack poseStack2, MultiBufferSource buffer, int packedLight2){
        if(alpha==1.0f&&entity instanceof LivingEntityGoopAccessor accessor && accessor.getDelay()>0){
            float added = 0.4f+0.6f* Mth.clamp(1.0f-(accessor.getDelay()-1)/5.0f,0f,1f);
            original.call(model, poseStack, consumer, packedLight, packedOverlay, added, 1.0f, 0.4f+0.6f*added, alpha);
            return;
        }
        original.call(model, poseStack, consumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
