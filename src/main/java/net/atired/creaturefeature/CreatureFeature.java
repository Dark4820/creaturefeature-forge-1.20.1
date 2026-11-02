package net.atired.creaturefeature;

import net.atired.creaturefeature.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// PORT: Forge 1.20.1 entry point (the original constructor receives IEventBus/ModContainer from NeoForge).
// Registries are added phase by phase (see docs/PORT_MAP.md); entity attributes arrive with the creatures (F3-F5).
@Mod(CreatureFeature.MODID)
public class CreatureFeature {
    public static final String MODID = "creaturefeature";

    public CreatureFeature() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        // PORT: modContainer.registerConfig -> ModLoadingContext.registerConfig
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC2);
        // PORT: no TRIGGER_TYPE registry in 1.20.1 (see CFAchievements)
        CFAchievements.init();
        CFSoundInit.SOUND_EVENTS.register(modEventBus);
        CFParticleInit.PARTICLE_TYPES.register(modEventBus);
        CFMobEffectInit.MOB_EFFECTS.register(modEventBus);
        CFItemInit.ITEMS.register(modEventBus);
        CFBlockInit.BLOCKS.register(modEventBus);
    }

    // PORT: ResourceLocation.fromNamespaceAndPath does not exist in 1.20.1
    public static ResourceLocation getId(String string) {
        return new ResourceLocation(MODID, string);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }
}
