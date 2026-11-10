package net.atired.creaturefeature;

import net.atired.creaturefeature.init.*;
import net.atired.creaturefeature.networking.CFNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// PORT: Forge 1.20.1 entry point (the original constructor receives IEventBus/ModContainer from NeoForge).
// Registries are added phase by phase (see docs/PORT_MAP.md). Entity attributes and spawn placements: CFEvents.ModBus.
@Mod(CreatureFeature.MODID)
public class CreatureFeature {
    public static final String MODID = "creaturefeature";
    // sphere mesh shared by the saint solis renderer (F5) and the doohickey renderer (F6); plain geometry, no client classes
    public static net.atired.creaturefeature.misc.IcoSphere ICO = net.atired.creaturefeature.misc.IcoSphere.MakeIcosphere(2);

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
        CFEntityInit.ENTITIES.register(modEventBus);
        CFBlockEntityInit.BLOCK_ENTITY_TYPES.register(modEventBus);
        CFRecipeSerialisers.RECIPES.register(modEventBus);
        // PORT: port-only add_table loot modifier (Forge 1.20.1 has none) for the Oreganized / C&C compat data
        net.atired.creaturefeature.init.CFLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    // PORT: ResourceLocation.fromNamespaceAndPath does not exist in 1.20.1
    public static ResourceLocation getId(String string) {
        return new ResourceLocation(MODID, string);
    }

    // PORT: 1.21 items implementing ProjectileItem are dispensed automatically; in 1.20.1 each one registers an
    // AbstractProjectileDispenseBehavior (same default power 1.1 and uncertainty 6 as ProjectileItem's)
    private static void registerDispenserBehaviours() {
        net.minecraft.world.level.block.DispenserBlock.registerBehavior(CFItemInit.MURKY_PEARL.get(), projectile(
                (level, pos, stack) -> ((net.atired.creaturefeature.items.MurkyPearlItem) CFItemInit.MURKY_PEARL.get()).asProjectile(level, pos, stack, null)));
        net.minecraft.world.level.block.DispenserBlock.registerBehavior(CFItemInit.DOWN_FEATHER.get(), projectile(
                (level, pos, stack) -> ((net.atired.creaturefeature.items.DownFeatherItem) CFItemInit.DOWN_FEATHER.get()).asProjectile(level, pos, stack, null)));
        net.minecraft.world.level.block.DispenserBlock.registerBehavior(CFItemInit.SOLAR_SHARD.get(), projectile(
                (level, pos, stack) -> ((net.atired.creaturefeature.items.SolarShardItem) CFItemInit.SOLAR_SHARD.get()).asProjectile(level, pos, stack, null)));
        net.minecraft.world.level.block.DispenserBlock.registerBehavior(CFItemInit.VITRIC_ARROW.get(), projectile(
                (level, pos, stack) -> ((net.atired.creaturefeature.items.VitricArrowItem) CFItemInit.VITRIC_ARROW.get()).asProjectile(level, pos, stack, null)));
    }

    private interface ProjectileFactory {
        net.minecraft.world.entity.projectile.Projectile create(net.minecraft.world.level.Level level, net.minecraft.core.Position pos, net.minecraft.world.item.ItemStack stack);
    }

    private static net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior projectile(ProjectileFactory factory) {
        return new net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior() {
            @Override
            protected net.minecraft.world.entity.projectile.Projectile getProjectile(net.minecraft.world.level.Level level, net.minecraft.core.Position pos, net.minecraft.world.item.ItemStack stack) {
                return factory.create(level, pos, stack);
            }
        };
    }

    // PORT: the payloads were registered in CFEvents.register (RegisterPayloadHandlersEvent); see CFNetwork
    private void commonSetup(FMLCommonSetupEvent event) {
        // PORT: port-only opt-in check on a real dedicated server (misc/CFServerSelfTest), only with -Dcreaturefeature.selftest=true
        if (Boolean.getBoolean("creaturefeature.selftest") && net.minecraftforge.fml.loading.FMLEnvironment.dist.isDedicatedServer()) {
            org.slf4j.LoggerFactory.getLogger(MODID).warn("creaturefeature: server self-test enabled (-Dcreaturefeature.selftest); it changes the first player and spawns mobs, use a throwaway world");
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new net.atired.creaturefeature.misc.CFServerSelfTest());
        }
        event.enqueueWork(CFNetwork::register);
        event.enqueueWork(CreatureFeature::registerDispenserBehaviours);
    }
}
