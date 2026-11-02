package net.atired.creaturefeature.client;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.client.particles.*;
import net.atired.creaturefeature.blocks.FeatherTints;
import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * PORT: client entry point. NeoForge's @Mod(dist = CLIENT) class + @EventBusSubscriber -> a Forge mod-bus
 * subscriber limited to Dist.CLIENT (a dedicated server never loads it). The original also registered NeoForge's
 * ConfigurationScreen; Forge 1.20.1 has no built-in config screen, so the config is edited in its file.
 * Filled phase by phase: F1 = feather colour handlers (added after the in-game test), F2 = PROXY and particle
 * providers (shaders are registered in CFRenderTypes). Still to come: item properties (F6), post chains and render
 * targets (F7), layers and renderers (F3-F5) and the client tick (with their content).
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
