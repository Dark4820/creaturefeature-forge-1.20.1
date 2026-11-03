package net.atired.creaturefeature.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.client.renderers.models.PathogenEntityModel;
import net.atired.creaturefeature.entity.PathogenesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PathogenEntityRenderer extends MobRenderer<PathogenesisEntity, PathogenEntityModel<PathogenesisEntity>> {
    private static final ResourceLocation PATHOGEN_LOCATION = CreatureFeature.getId("textures/entity/pathogen.png");

    public PathogenEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PathogenEntityModel<>(context.bakeLayer(PathogenEntityModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PathogenesisEntity machinationEntity) {
        return PATHOGEN_LOCATION;
    }

    // PORT: getShadowRadius(entity) (1.20.5+) does not exist: in 1.20.1 the dispatcher reads the shadowRadius field
    // after render(), so the original's value (0.5 * (1 - fading)) is set here
    @Override
    public void render(PathogenesisEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = 0.5f*(1.0f-entity.getFading());
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public void vertex(PoseStack.Pose pose, VertexConsumer consumer, double x, double y, double z, float u, float v, int normalX, int normalY, int normalZ, int packedLight, float alpha) {
        // PORT: 1.21 vertex API (addVertex/setColor/setUv/setOverlay/setLight/setNormal) -> 1.20.1 (vertex/color/uv/overlayCoords/uv2/normal/endVertex)
        consumer.vertex(pose.pose(), (float)x, (float)y, (float)z).color(1.0f,1.0f,1.0f,alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), (float)normalX, (float)normalZ, (float)normalY).endVertex();
    }

}
