package net.atired.creaturefeature.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.client.renderers.FriendEntityRenderer;
import net.atired.creaturefeature.client.renderers.SaintSolisEntityRenderer;
import net.atired.creaturefeature.client.renderers.StarProjEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

// PORT: client-only state, as in the original. The cached BufferSources arrive with their creatures:
// getDetritusSource in F3, getFendSource and getFriendSource in F4, getSunSource in F7 (with the sun post chain).
public class CFClientProxy {
    public float eebyDeebyNess = 0.0f;
    public float flayed2 = 0.0f;
    public boolean searchingForHim=false;
    public float rabies2 = 0.0f;
    public float nan_title = 0.0f;
    public float gasLeak = 0.0f;
    public float wobble = 0.0f;
    public float manShader = 0.0f;
    public float bacterial = 0.0f;
    public float fiendish2 = 0.0f;
    public float fiendish = 0.0f;
    public ItemStack wobblyItem=null;
    public int attachment = 0;
    public float[] colours = {0.0f,0.0f,0.0f,0.0f};
    private static final ResourceLocation DETRITUS_LOCATION = CreatureFeature.getId("textures/entity/detritus.png");
    public static MultiBufferSource.BufferSource DETRITUS_SOURCE = null;

    // PORT: SequencedMap<RenderType, ByteBufferBuilder> + immediateWithBuffers(map, ByteBufferBuilder) ->
    // Map<RenderType, BufferBuilder> + immediateWithBuffers(map, BufferBuilder) (1.20.1 types, same behaviour)
    public static MultiBufferSource.BufferSource getDetritusSource(){
        if(DETRITUS_SOURCE!=null){
            return DETRITUS_SOURCE;
        }else{
            Map<RenderType,BufferBuilder> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(CFRenderTypes.entityMonochromeCull(DETRITUS_LOCATION),
                    new BufferBuilder(CFRenderTypes.entityMonochromeCull(DETRITUS_LOCATION).bufferSize()));
            DETRITUS_SOURCE=MultiBufferSource.immediateWithBuffers(buffers,new BufferBuilder(256));
            return DETRITUS_SOURCE;
        }
    }

    private static final ResourceLocation FEND_LOCATION = CreatureFeature.getId("textures/entity/fend.png");
    public static MultiBufferSource.BufferSource FEND_SOURCE = null;

    // PORT: same buffers as the original minus entityWraithCutout (the wraith shader is ★ content, F9; only the
    // wraith renderer draws with it). ByteBufferBuilder -> BufferBuilder as in getDetritusSource.
    public static MultiBufferSource.BufferSource getFendSource(){
        if(FEND_SOURCE!=null){
            return FEND_SOURCE;
        }else{
            Map<RenderType,BufferBuilder> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(CFRenderTypes.entityFendCutout(FEND_LOCATION),
                    new BufferBuilder(CFRenderTypes.entityFendCutout(FEND_LOCATION).bufferSize()));
            FEND_SOURCE=MultiBufferSource.immediateWithBuffers(buffers,new BufferBuilder(256));
            return FEND_SOURCE;
        }
    }

    public static MultiBufferSource.BufferSource SUN_SOURCE = null;

    // PORT: ByteBufferBuilder -> BufferBuilder (same buffers as the original)
    public static MultiBufferSource.BufferSource getSunSource(){
        if(SUN_SOURCE!=null){
            return SUN_SOURCE;
        }else{
            Map<RenderType,BufferBuilder> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(RenderType.entityTranslucent((SaintSolisEntityRenderer.SUN_LOCATION)),
                    new BufferBuilder(RenderType.entityTranslucent(SaintSolisEntityRenderer.SUN_LOCATION).bufferSize()));
            buffers.put(RenderType.entityTranslucent(StarProjEntityRenderer.STARPROJ_LOCATION),
                    new BufferBuilder(RenderType.entityTranslucent(StarProjEntityRenderer.STARPROJ_LOCATION).bufferSize()));

            SUN_SOURCE=MultiBufferSource.immediateWithBuffers(buffers,new BufferBuilder(256));
            return SUN_SOURCE;
        }
    }
    public static MultiBufferSource.BufferSource FRIEND_SOURCE = null;

    // PORT: ByteBufferBuilder -> BufferBuilder (same buffers as the original)
    public static MultiBufferSource.BufferSource getFriendSource(){
        if(FRIEND_SOURCE!=null){
            return FRIEND_SOURCE;
        }else{
            Map<RenderType,BufferBuilder> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(CFRenderTypes.entityFriendCutout(FriendEntityRenderer.CUBE_LOCATION),
                    new BufferBuilder(CFRenderTypes.entityFriendCutout(FriendEntityRenderer.CUBE_LOCATION).bufferSize()));
            buffers.put(CFRenderTypes.entitySilkCull(FriendEntityRenderer.CUBE_LOCATION),
                    new BufferBuilder(CFRenderTypes.entitySilkCull(FriendEntityRenderer.CUBE_LOCATION).bufferSize()));
            buffers.put(RenderType.entityTranslucent(FriendEntityRenderer.SMILE_FRIENDLESS_LOCATION),
                    new BufferBuilder(RenderType.entityTranslucent(FriendEntityRenderer.SMILE_FRIENDLESS_LOCATION).bufferSize()));

            buffers.put(RenderType.entityTranslucent(FriendEntityRenderer.SMILE_LOCATION),
                    new BufferBuilder(RenderType.entityTranslucent(FriendEntityRenderer.SMILE_LOCATION).bufferSize()));
            FRIEND_SOURCE=MultiBufferSource.immediateWithBuffers(buffers,new BufferBuilder(256));
            return FRIEND_SOURCE;
        }
    }
}
