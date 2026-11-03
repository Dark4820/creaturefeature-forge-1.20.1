package net.atired.creaturefeature.mixin;

import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.accessors.PostChainDepthPassAccessor;
import net.atired.creaturefeature.client.CreatureFeatureClient;
import net.atired.creaturefeature.accessors.GameRendererResourceManagerAccessor;
import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PORT: render(DeltaTracker, boolean) -> render(float partialTicks, long nanoTime, boolean renderLevel);
// deltaTracker.getRealtimeDeltaTicks() -> Minecraft.getDeltaFrameTime() (the same real-time frame delta);
// chain.setUniform(...) (1.20.5+) -> CreatureFeatureClient.setUniform(chain, ...); Math.clamp -> Mth.clamp;
// getSectionRenderDispatcher -> getChunkRenderDispatcher. The original's empty injection into renderLevel is left
// out (it did nothing).
@Mixin(GameRenderer.class)
public class CFGameRendererMixin implements GameRendererResourceManagerAccessor {
    @Shadow @Final private ResourceManager resourceManager;

    @Inject(method = "render(FJZ)V",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V",ordinal = 0,shift= At.Shift.BEFORE))
    private void renderCFDepth(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci){
        CreatureFeatureClient.SUN_TARGET2.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
        if(CreatureFeatureClient.SUN instanceof PostChainDepthPassAccessor accessor2){
            for(PostPass pass : accessor2.getDemPostPasses()){
                pass.getEffect().setSampler("DiffuseDepthSampler",CreatureFeatureClient.SUN_TARGET2::getDepthTextureId);

            }
        }
    }

    @Inject(method = "render(FJZ)V",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V",ordinal = 0,shift= At.Shift.BEFORE))
    private void renderCF(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci){
        if(Minecraft.getInstance().levelRenderer!=null&&CreatureFeatureClient.RABIES_TARGET!=null&&Minecraft.getInstance().levelRenderer.getChunkRenderDispatcher()!=null&&
                CreatureFeatureClient.MINEDFLAYER !=null&&Minecraft.getInstance().player!=null&&
                Minecraft.getInstance().gameRenderer instanceof GameRendererResourceManagerAccessor accessor){
            float delta = Minecraft.getInstance().getDeltaFrameTime();

            PostChain chain2 = CreatureFeatureClient.FRIEND;
            chain2.process(delta);
            chain2 = CreatureFeatureClient.SUN;
            CreatureFeatureClient.setUniform(chain2,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
            chain2.process(delta);
            if(Minecraft.getInstance().player instanceof PlayerBrainrotAccessor accessor1&&
                    Minecraft.getInstance().player instanceof LivingEntityGoopAccessor accessor2&&(accessor1.getBrainrot()>0.01f||accessor2.getGoop()>0.01f||CreatureFeatureClient.PROXY.flayed2>0)){
                PostChain chain = CreatureFeatureClient.MINEDFLAYER;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",Math.max(Math.max(accessor1.getBrainrot(),Mth.clamp(accessor2.getGoop(),0.0f,0.33f)),Math.min(1.0f,CreatureFeatureClient.PROXY.flayed2*1.1f)));
                chain.process(delta);
            }
            if(Minecraft.getInstance().player instanceof PlayerBrainrotAccessor accessor1&&(accessor1.getRabies()>0.01f||CreatureFeatureClient.PROXY.rabies2>0)){

                PostChain chain = CreatureFeatureClient.RABIES;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",Math.max(accessor1.getRabies(),Math.min(1.0f,CreatureFeatureClient.PROXY.rabies2*1.1f)));

                chain.process(delta);
            }
            if(CreatureFeatureClient.PROXY!=null&&CreatureFeatureClient.PROXY.gasLeak>0.0f){

                PostChain chain = CreatureFeatureClient.HAZE;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",CreatureFeatureClient.PROXY.gasLeak);
                chain.process(delta);
            }
            if(CreatureFeatureClient.PROXY!=null&&CreatureFeatureClient.PROXY.bacterial>0.0f){

                PostChain chain = CreatureFeatureClient.BACTE;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",CreatureFeatureClient.PROXY.bacterial);
                chain.process(delta);
            }
            if(CreatureFeatureClient.PROXY!=null&&CreatureFeatureClient.PROXY.manShader>0.0f){

                PostChain chain = CreatureFeatureClient.MAN;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",CreatureFeatureClient.PROXY.manShader);
                chain.process(delta);
            }
            if(CreatureFeatureClient.PROXY!=null&&(CreatureFeatureClient.PROXY.fiendish>0.0f||CreatureFeatureClient.PROXY.fiendish2>0)){

                PostChain chain = CreatureFeatureClient.FIEND;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",Math.max(CreatureFeatureClient.PROXY.fiendish,Math.min(1.0f,CreatureFeatureClient.PROXY.fiendish2*1.1f)));
                chain.process(delta);
            }
            if(CreatureFeatureClient.PROXY!=null&&CreatureFeatureClient.PROXY.eebyDeebyNess>0.0f){

                PostChain chain = CreatureFeatureClient.SLEEP;
                CreatureFeatureClient.setUniform(chain,"GameTime",(Minecraft.getInstance().level.getGameTime()%24000));
                CreatureFeatureClient.setUniform(chain,"FadeInTest",CreatureFeatureClient.PROXY.eebyDeebyNess);
                chain.process(delta);
            }
            CreatureFeatureClient.FRIEND_TARGET.clear(Minecraft.ON_OSX);
            CreatureFeatureClient.SUN_TARGET.clear(Minecraft.ON_OSX);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    }
    @Override
    public ResourceManager creaturefeature$myPrecious() {
        return this.resourceManager;
    }
}
