package net.atired.creaturefeature.client;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.client.particles.*;
import net.atired.creaturefeature.blocks.FeatherTints;
import net.atired.creaturefeature.client.renderers.*;
import net.atired.creaturefeature.client.renderers.models.*;
import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * PORT: client entry point. NeoForge's @Mod(dist = CLIENT) class + @EventBusSubscriber -> a Forge mod-bus
 * subscriber limited to Dist.CLIENT (a dedicated server never loads it). The original also registered NeoForge's
 * ConfigurationScreen; Forge 1.20.1 has no built-in config screen, so the config is edited in its file.
 * Filled phase by phase: F1 = feather colour handlers (added after the in-game test), F2 = PROXY and particle
 * providers (shaders are registered in CFRenderTypes), F3-F5 = layers and renderers of the creatures.
 * Still to come: item properties (F6), post chains and render targets (F7), the other layers and renderers (F4-F5)
 * and the client tick (with their content).
 */
@Mod.EventBusSubscriber(modid = CreatureFeature.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreatureFeatureClient {
    public static CFClientProxy PROXY = new CFClientProxy();

    // PORT: the colour math moved to blocks/FeatherTints (testable on the server); registration as in the original.
    // These handlers belonged to F1 and were missed: without them normal and mosaic feathers looked the same.
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> FeatherTints.item(true, tintIndex),
                CFBlockInit.MOSAIC_DOWN_FEATHERS.get(), CFBlockInit.MOSAIC_DOWN_FEATHERS_CARPET.get());
        event.register((stack, tintIndex) -> FeatherTints.item(false, tintIndex),
                CFBlockInit.DOWN_FEATHERS_CARPET.get(), CFBlockInit.DOWN_FEATHERS.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((blockState, blockAndTintGetter, blockPos, tintIndex) -> FeatherTints.mosaic(blockPos, tintIndex),
                CFBlockInit.MOSAIC_DOWN_FEATHERS.get(), CFBlockInit.MOSAIC_DOWN_FEATHERS_CARPET.get());
        event.register((blockState, blockAndTintGetter, blockPos, tintIndex) -> FeatherTints.normal(blockPos, tintIndex),
                CFBlockInit.DOWN_FEATHERS.get(), CFBlockInit.DOWN_FEATHERS_CARPET.get());
    }

    // PORT: ItemProperties as in the original's onClientSetup; ResourceLocation.withDefaultNamespace -> new ResourceLocation
    // (minecraft namespace), data components -> the NBT helpers of CFDataComponentTypeInit
    @SubscribeEvent
    public static void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(()->{
            net.minecraft.client.renderer.item.ItemProperties.register(net.atired.creaturefeature.init.CFItemInit.VERTIGO_HORN.get(), new net.minecraft.resources.ResourceLocation("tooting"), (stack, level, entity, seed) -> {
                if(entity!=null&&entity.isUsingItem()){
                    return 1;
                }
                return 0;
            });
            net.minecraft.client.renderer.item.ItemProperties.register(net.atired.creaturefeature.init.CFItemInit.DREAM_CATCHER.get(), new net.minecraft.resources.ResourceLocation("full"), (stack, level, entity, seed) -> {
                if(net.atired.creaturefeature.init.CFDataComponentTypeInit.getDreamcatcherContents(stack).items().iterator().hasNext()){
                    return 1;
                }
                return 0;
            });
            net.minecraft.client.renderer.item.ItemProperties.register(net.atired.creaturefeature.init.CFItemInit.OPEN_MIND.get(), new net.minecraft.resources.ResourceLocation("open"), (stack, level, entity, seed) -> {
                if(net.atired.creaturefeature.init.CFDataComponentTypeInit.getOpened(stack)!=0&&entity!=null){
                    return 1;
                }
                return 0;
            });
        });
    }

    // PORT: only the creatures already ported (F3: minds, machination, sinister, beauty, detritus;
    // F4: fiend, fend, friend, blitz, eeper, nothing, canary, vertigo, coat_of_arms + bullet, kicked_block;
    // F5: minedflayer, cannonball_crab, dreamweaver, mockingbird, pathogen, blossom, saint_solis, stained_glass +
    // eep, vitric_arrow, feather, star, spat_block, murky_pearl)
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MindsEntityModel.LAYER_LOCATION, MindsEntityModel::createBodyLayer);
        event.registerLayerDefinition(MindsEntityModel.LAYER_INNER_ARMOUR_LOCATION,()->{return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION), 64, 32);});
        event.registerLayerDefinition(MindsEntityModel.LAYER_ARMOUR_LOCATION, ()->{return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION), 64, 32);});
        event.registerLayerDefinition(MachinationEntityModel.LAYER_LOCATION, MachinationEntityModel::createBodyLayer);
        event.registerLayerDefinition(SinisterEntityModel.LAYER_LOCATION, SinisterEntityModel::createBodyLayer);
        event.registerLayerDefinition(BeautyEntityModel.LAYER_LOCATION, BeautyEntityModel::createBodyLayer);
        event.registerLayerDefinition(DetritusEntityModel.LAYER_LOCATION, DetritusEntityModel::createBodyLayer);
        event.registerLayerDefinition(BlitzEntityModel.LAYER_LOCATION, BlitzEntityModel::createBodyLayer);
        event.registerLayerDefinition(EeperEntityModel.LAYER_LOCATION, EeperEntityModel::createBodyLayer);
        event.registerLayerDefinition(NoThingEntityModel.LAYER_LOCATION, NoThingEntityModel::createBodyLayer);
        event.registerLayerDefinition(CanaryEntityModel.LAYER_LOCATION, CanaryEntityModel::createBodyLayer);
        event.registerLayerDefinition(CanaryEntityPartModel.LAYER_LOCATION, CanaryEntityPartModel::createBodyLayer);
        event.registerLayerDefinition(FiendEntityModel.LAYER_LOCATION, ()->{return FiendEntityModel.createBodyLayer(0f);});
        event.registerLayerDefinition(FiendEntityModel.INNER_LAYER_LOCATION, ()->{return FiendEntityModel.createBodyLayer(-1.05f);});
        event.registerLayerDefinition(VertigoEntityModel.LAYER_LOCATION, VertigoEntityModel::createBodyLayer);
        event.registerLayerDefinition(CoatOfArmsEntityModel.LAYER_LOCATION, CoatOfArmsEntityModel::createBodyLayer);
        event.registerLayerDefinition(FendEntityModel.LAYER_LOCATION, FendEntityModel::createBodyLayer);
        event.registerLayerDefinition(FendEntityModel.LAYER_INNER_ARMOUR_LOCATION,()->{return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION), 64, 32);});
        event.registerLayerDefinition(FendEntityModel.LAYER_ARMOUR_LOCATION, ()->{return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION), 64, 32);});
        event.registerLayerDefinition(MinedFlayerEntityModel.LAYER_LOCATION, MinedFlayerEntityModel::createBodyLayer);
        event.registerLayerDefinition(DreamWeaverEntityModel.LAYER_LOCATION, DreamWeaverEntityModel::createBodyLayer);
        event.registerLayerDefinition(DreamWeaverEntityModel.INNER_LAYER_LOCATION, ()->{return DreamWeaverEntityModel.createBodyLayer(-0.1f);});
        event.registerLayerDefinition(DreamWeaverEntityModel.INNERER_LAYER_LOCATION, ()->{return DreamWeaverEntityModel.createBodyLayer(-0.05f);});
        event.registerLayerDefinition(CannonballCrabEntityModel.LAYER_LOCATION, CannonballCrabEntityModel::createBodyLayer);
        event.registerLayerDefinition(MockingBirdEntityModel.LAYER_LOCATION, MockingBirdEntityModel::createBodyLayer);
        event.registerLayerDefinition(PathogenEntityModel.LAYER_LOCATION, PathogenEntityModel::createBodyLayer);
        event.registerLayerDefinition(BlossomEntityModel.LAYER_LOCATION, BlossomEntityModel::createBodyLayer);
        event.registerLayerDefinition(SaintSolisEntityModel.LAYER_LOCATION, SaintSolisEntityModel::createBodyLayer);
        event.registerLayerDefinition(StainedGlassEntityModel.LAYER_LOCATION, StainedGlassEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CFEntityInit.MINDS.get(), MindsEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.MACHINATION.get(), MachinationEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.SINISTER.get(), SinisterEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.BEAUTY.get(), BeautyEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.DETRITUS.get(), DetritusEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.BLITZ.get(), BlitzEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.EEPER.get(), EeperEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.NOTHING.get(), NoThingEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.CANARY.get(), CanaryEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.CANARY_PART.get(), CanaryPartRenderer::new);
        event.registerEntityRenderer(CFEntityInit.VERTIGO.get(), VertigoEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.FIEND.get(), FiendEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.FRIEND.get(), FriendEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.COATOFARMS.get(), CoatOfArmsEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.FEND.get(), FendEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.BULLET.get(), BulletEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.KICKED_BLOCK.get(), KickedBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(net.atired.creaturefeature.init.CFBlockEntityInit.DOOHICKEY.get(), net.atired.creaturefeature.client.renderers.blockentity.DoohickeyBlockEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.MINEDFLAYER.get(), MinedFlayerEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.CANNONBALL_CRAB.get(), CannonballCrabEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.DREAMWEAVER.get(), DreamWeaverEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.MOCKINGBIRD.get(), MockingBirdEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.PATHOGEN.get(), PathogenEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.BLOSSOM.get(), BlossomEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.SAINT_SOLIS.get(), SaintSolisEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.STAINED_GLASS.get(), StainedGlassEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.EEP.get(), EepEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.VITRIC_ARROW.get(),  (c)->{return new VitricArrowEntityRenderer(c);});
        event.registerEntityRenderer(CFEntityInit.FEATHER.get(),  (c)->{return new FeatherEntityRenderer(c,1.0f,false);});
        event.registerEntityRenderer(CFEntityInit.STAR.get(),  (c)->{return new StarProjEntityRenderer(c,1.0f,false);});
        event.registerEntityRenderer(CFEntityInit.SPAT_BLOCK.get(), SpatBlockEntityRenderer::new);
        event.registerEntityRenderer(CFEntityInit.MURKY_PEARL.get(), (c)->{return new MurkyPearlEntityRenderer(c,1.0f,false);});
    }

    // PORT: FISHEYE and RED_POWDER (★) are left out until F9
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CFParticleInit.SLEEPY_PARTICLE.get(), SleepyParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.SLEEPY_EXPLODE_PARTICLE.get(), SleepyExplodeParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.LEAF_PARTICLE.get(), LeafParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.RABIES_PARTICLE.get(), RabiesParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.TOOT_PARTICLE.get(), VertigoHornParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.CLEAVE_PARTICLE.get(), RotatedVertigoHornParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.FLOWER_PARTICLE.get(), FlowerParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.CRIT_PARTICLE.get(), CritParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.CRIT_TEXT_PARTICLE.get(), CritTextParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.CRIT_VER_PARTICLE.get(), CritVerParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.SPARKLE_PARTICLE.get(), SparkleParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.SPORE_PARTICLE.get(), SporeParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.SPIT_PARTICLE.get(), SpitParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.STAR_PARTICLE.get(), StarParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.CASING_PARTICLE.get(), CasingParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.GLASS_SHARD_PARTICLE.get(), GlassParticle.Provider::new);
        event.registerSpriteSet(CFParticleInit.GLASS_SHARD_SINISTER_PARTICLE.get(), GlassSinisterParticle.Provider::new);
    }
}
