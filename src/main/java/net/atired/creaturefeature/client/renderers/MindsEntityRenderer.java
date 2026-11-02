package net.atired.creaturefeature.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.entity.MindsEntity;
import net.atired.creaturefeature.client.renderers.models.MindsEntityModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MindsEntityRenderer extends HumanoidMobRenderer<MindsEntity, MindsEntityModel<MindsEntity>> {
    private static final ResourceLocation MINDS_LOCATION = CreatureFeature.getId("textures/entity/minds.png");
    private static final ResourceLocation MINDS_POPPED_LOCATION = CreatureFeature.getId("textures/entity/minds_popped.png");
    private static final ResourceLocation MINDS_EYE_LOCATION = CreatureFeature.getId("textures/entity/minds_eye.png");

    public MindsEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new MindsEntityModel<>(context.bakeLayer(MindsEntityModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, new MindsEntityModel(context.bakeLayer(MindsEntityModel.LAYER_INNER_ARMOUR_LOCATION)), new MindsEntityModel(context.bakeLayer(MindsEntityModel.LAYER_ARMOUR_LOCATION)), context.getModelManager()));

    }

    @Override
    public void render(MindsEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        if(!entity.hasNoMind()||entity.getHealth()<=0.0){
            return;
        }
        poseStack.pushPose();
        // PORT: getPreciseBodyRotation(partialTicks) (1.21) = Mth.lerp(partialTicks, yBodyRotO, yBodyRot)
        Vec3 dir = new Vec3(0,0,1).yRot(-Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)/180f*3.14f).multiply(1,0,1).normalize().scale(0.5);
        poseStack.translate(dir.x,2.0,dir.z);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(MINDS_EYE_LOCATION));
        Vec3 vec3_4 = new Vec3(-1,1.5+Math.sin((entity.tickCount+partialTicks)/1.0f)/6.0f,0).multiply(0.45,.45,0.45);
        Vec3 vec3_1 = new Vec3(1,1.5+Math.cos((entity.tickCount+partialTicks)/1.0f)/6.0f,0).multiply(0.45,.45,0.45);
        Vec3 vec3_2 = new Vec3(1,-0.5,0).multiply(0.45,.45,0.45);
        Vec3 vec3_3 = new Vec3(-1,-0.5,0).multiply(0.45,.45,0.45);

        vertex(pose,consumer,vec3_4.x,vec3_4.y,vec3_4.z,0.0f,0.0f,0,-1,0,packedLight,0.0f);
        vertex(pose,consumer,vec3_1.x,vec3_1.y,vec3_1.z,1.0f,0.0f,0,-1,0,packedLight,0.0f);
        vertex(pose,consumer,vec3_2.x,vec3_2.y,vec3_2.z,1.0f,1.0f,0,-1,0,packedLight,1.0f);
        vertex(pose,consumer,vec3_3.x,vec3_3.y,vec3_3.z,0.0f,1.0f,0,-1,0,packedLight,1.0f);


        poseStack.popPose();
    }
    public void vertex(PoseStack.Pose pose, VertexConsumer consumer, double x, double y, double z, float u, float v, int normalX, int normalY, int normalZ, int packedLight,float alpha) {
        // PORT: 1.21 vertex API (addVertex/setColor/setUv/setOverlay/setLight/setNormal) -> 1.20.1 (vertex/color/uv/overlayCoords/uv2/normal/endVertex)
        consumer.vertex(pose.pose(), (float)x, (float)y, (float)z).color(1.0f,1.0f,1.0f,alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), (float)normalX, (float)normalZ, (float)normalY).endVertex();
    }
    @Override
    public ResourceLocation getTextureLocation(MindsEntity mindsEntity) {
        return mindsEntity.hasNoMind()?MINDS_POPPED_LOCATION:MINDS_LOCATION;
    }

}
