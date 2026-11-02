package net.atired.creaturefeature.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.atired.creaturefeature.CreatureFeature;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

// PORT: client-only state, as in the original. The cached BufferSources arrive with their creatures:
// getDetritusSource in F3; getFendSource, getSunSource and getFriendSource in F4-F5.
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
}
