package net.atired.creaturefeature.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.client.renderers.models.CanaryEntityModel;
import net.atired.creaturefeature.client.renderers.models.CanaryEntityPartModel;
import net.atired.creaturefeature.entity.CanaryEntity;
import net.atired.creaturefeature.entity.CanaryPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class CanaryPartRenderer extends MobRenderer<CanaryPart, CanaryEntityPartModel<CanaryPart>> {
    private static final ResourceLocation CANARY_LOCATION = CreatureFeature.getId("textures/entity/canary.png");

    public CanaryPartRenderer(EntityRendererProvider.Context context) {
        super(context, new CanaryEntityPartModel<>(context.bakeLayer(CanaryEntityPartModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CanaryPart canaryEntity) {
        return CANARY_LOCATION;
    }


    public void vertex(PoseStack.Pose pose, VertexConsumer consumer, double x, double y, double z, float u, float v, int normalX, int normalY, int normalZ, int packedLight,float alpha) {
        // PORT: 1.21 vertex API (addVertex/setColor/setUv/setOverlay/setLight/setNormal) -> 1.20.1 (vertex/color/uv/overlayCoords/uv2/normal/endVertex)
        consumer.vertex(pose.pose(), (float)x, (float)y, (float)z).color(1.0f,1.0f,1.0f,alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), (float)normalX, (float)normalZ, (float)normalY).endVertex();
    }

}
