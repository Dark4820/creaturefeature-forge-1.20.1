package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: DeferredRegister/Supplier -> DeferredRegister/RegistryObject. EntityType.Builder in 1.20.1 has no
// eyeHeight/passengerAttachments/ridingOffset: each entity overrides getStandingEyeHeight,
// getPassengersRidingOffset and getMyRidingOffset with the original values (see the entity classes).
// Filled phase by phase: F3 = minds, machination, sinister, beauty, detritus (see docs/PORT_MAP.md).
public class CFEntityInit {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CreatureFeature.MODID);

    public static final RegistryObject<EntityType<MindsEntity>> MINDS =
            ENTITIES.register("minds", () -> EntityType.Builder.of(MindsEntity::new, MobCategory.MONSTER)
                    .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8)
                    .build("minds"));
    public static final RegistryObject<EntityType<MachinationEntity>> MACHINATION =
            ENTITIES.register("machination", () -> EntityType.Builder.of(MachinationEntity::new, MobCategory.MONSTER)
                    .sized(EntityType.ZOMBIE.getWidth()*1.1f, 1.7f).clientTrackingRange(12)
                    .build("machination"));
    public static final RegistryObject<EntityType<SinisterEntity>> SINISTER =
            ENTITIES.register("sinister", () -> EntityType.Builder.of(SinisterEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 1.9f).clientTrackingRange(12)
                    .build("sinister"));
    public static final RegistryObject<EntityType<DetritusEntity>> DETRITUS =
            ENTITIES.register("detritus", () -> EntityType.Builder.of(DetritusEntity::new, MobCategory.MONSTER)
                    .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(12)
                    .build("detritus"));
    public static final RegistryObject<EntityType<BeautyEntity>> BEAUTY =
            ENTITIES.register("beauty", () -> EntityType.Builder.of(BeautyEntity::new, MobCategory.MONSTER)
                    .sized(0.8f,1.9f).clientTrackingRange(12)
                    .build("beauty"));
}
