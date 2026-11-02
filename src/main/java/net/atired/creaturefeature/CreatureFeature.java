package net.atired.creaturefeature;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// PORT: Forge 1.20.1 entry point (the original constructor receives IEventBus/ModContainer from NeoForge).
// Phase 0 skeleton: registries, config and events are added phase by phase (see docs/PORT_MAP.md).
@Mod(CreatureFeature.MODID)
public class CreatureFeature {
    public static final String MODID = "creaturefeature";

    public CreatureFeature() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    // PORT: ResourceLocation.fromNamespaceAndPath does not exist in 1.20.1
    public static ResourceLocation getId(String string) {
        return new ResourceLocation(MODID, string);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }
}
