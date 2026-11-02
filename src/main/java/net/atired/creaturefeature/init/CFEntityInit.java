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
// Filled phase by phase: F3 = minds, machination, sinister, beauty, detritus; F4 = fiend, fend, friend, blitz, eeper,
// nothing, canary (+ canary_part), vertigo, coat_of_arms + bullet and kicked_block (see docs/PORT_MAP.md).
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
    public static final RegistryObject<EntityType<FiendEntity>> FIEND =
            ENTITIES.register("fiend", () -> EntityType.Builder.of(FiendEntity::new, MobCategory.MONSTER)
                    .sized(0.8f,1.85f).clientTrackingRange(12)
                    .build("fiend"));
    public static final RegistryObject<EntityType<FendEntity>> FEND =
            ENTITIES.register("fend", () -> EntityType.Builder.of(FendEntity::new, MobCategory.MONSTER)
                    .sized(0.68f,1.9f).clientTrackingRange(12)
                    .build("fend"));
    public static final RegistryObject<EntityType<FriendEntity>> FRIEND =
            ENTITIES.register("friend", () -> EntityType.Builder.of(FriendEntity::new, MobCategory.MONSTER)
                    .sized(1.9f,1.95f).clientTrackingRange(12)
                    .build("friend"));
    public static final RegistryObject<EntityType<BlitzEntity>> BLITZ =
            ENTITIES.register("blitz", () -> EntityType.Builder.of(BlitzEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 1.9f).clientTrackingRange(12)
                    .build("blitz"));
    public static final RegistryObject<EntityType<EeperEntity>> EEPER =
            ENTITIES.register("eeper", () -> EntityType.Builder.of(EeperEntity::new, MobCategory.MONSTER)
                    .sized(0.65f, 2.1f).clientTrackingRange(12)
                    .build("eeper"));
    public static final RegistryObject<EntityType<NoThingEntity>> NOTHING =
            ENTITIES.register("nothing", () -> EntityType.Builder.of(NoThingEntity::new, MobCategory.MONSTER)
                    .sized(0.65f, 1.8f).clientTrackingRange(12)
                    .build("nothing"));
    public static final RegistryObject<EntityType<CanaryEntity>> CANARY =
            ENTITIES.register("canary", () -> EntityType.Builder.of(CanaryEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 0.9f).clientTrackingRange(12)
                    .build("canary"));
    public static final RegistryObject<EntityType<CanaryPart>> CANARY_PART =
            ENTITIES.register("canary_part", () -> EntityType.Builder.of(CanaryPart::new, MobCategory.MONSTER)
                    .sized(0.6f, 0.6f).clientTrackingRange(12)
                    .build("canary_part"));
    public static final RegistryObject<EntityType<VertigoEntity>> VERTIGO =
            ENTITIES.register("vertigo", () -> EntityType.Builder.of(VertigoEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 0.95f).clientTrackingRange(12)
                    .build("vertigo"));
    public static final RegistryObject<EntityType<CoatOfArmsEntity>> COATOFARMS =
            ENTITIES.register("coat_of_arms", () -> EntityType.Builder.of(CoatOfArmsEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 1.9f).clientTrackingRange(12)
                    .build("coat_of_arms"));
    public static final RegistryObject<EntityType<BulletEntity>> BULLET =
            ENTITIES.register("bullet", () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.2f).clientTrackingRange(12)
                    .build("bullet"));
    public static final RegistryObject<EntityType<KickedBlockEntity>> KICKED_BLOCK =
            ENTITIES.register("kicked_block", () -> EntityType.Builder.<KickedBlockEntity>of(KickedBlockEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.6f).clientTrackingRange(12)
                    .build("kicked_block"));
}
