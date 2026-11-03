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
// nothing, canary (+ canary_part), vertigo, coat_of_arms + bullet and kicked_block; F5 = minedflayer, mockingbird,
// pathogen, blossom, saint_solis, stained_glass, dreamweaver, cannonball_crab + murky_pearl, feather, vitric_arrow,
// star, spat_block, eep (see docs/PORT_MAP.md).
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
    public static final RegistryObject<EntityType<MinedFlayerEntity>> MINEDFLAYER =
            ENTITIES.register("minedflayer", () -> EntityType.Builder.of(MinedFlayerEntity::new, MobCategory.MONSTER)
                    .sized(1.1f, 1.4f).clientTrackingRange(12)
                    .build("minedflayer"));
    public static final RegistryObject<EntityType<MockingBirdEntity>> MOCKINGBIRD =
            ENTITIES.register("mockingbird", () -> EntityType.Builder.of(MockingBirdEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 2.8f).clientTrackingRange(12)
                    .build("mockingbird"));
    public static final RegistryObject<EntityType<PathogenesisEntity>> PATHOGEN =
            ENTITIES.register("pathogen", () -> EntityType.Builder.of(PathogenesisEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 1.37f).clientTrackingRange(12)
                    .build("pathogen"));
    public static final RegistryObject<EntityType<BlossomEntity>> BLOSSOM =
            ENTITIES.register("blossom", () -> EntityType.Builder.of(BlossomEntity::new, MobCategory.MONSTER)
                    .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(12)
                    .build("blossom"));
    public static final RegistryObject<EntityType<SaintSolisEntity>> SAINT_SOLIS =
            ENTITIES.register("saint_solis", () -> EntityType.Builder.of(SaintSolisEntity::new, MobCategory.MONSTER)
                    .sized(1.1f, 1.5f).clientTrackingRange(12)
                    .build("saint_solis"));
    public static final RegistryObject<EntityType<StainedGlassEntity>> STAINED_GLASS =
            ENTITIES.register("stained_glass", () -> EntityType.Builder.of(StainedGlassEntity::new, MobCategory.MONSTER)
                    .sized(1.1f, 1.1f).clientTrackingRange(12)
                    .build("stained_glass"));
    public static final RegistryObject<EntityType<DreamWeaverEntity>> DREAMWEAVER =
            ENTITIES.register("dreamweaver", () -> EntityType.Builder.of(DreamWeaverEntity::new, MobCategory.MONSTER)
                    .sized(1.4f, 0.9f).clientTrackingRange(12)
                    .build("dreamweaver"));
    public static final RegistryObject<EntityType<CannonballCrabEntity>> CANNONBALL_CRAB =
            ENTITIES.register("cannonball_crab", () -> EntityType.Builder.of(CannonballCrabEntity::new, MobCategory.MONSTER)
                    .sized(1.13f, 1.95f).clientTrackingRange(12)
                    .build("cannonball_crab"));
    public static final RegistryObject<EntityType<MurkyPearlEntity>> MURKY_PEARL =
            ENTITIES.register("murky_pearl", () -> EntityType.Builder.<MurkyPearlEntity>of(MurkyPearlEntity::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f).clientTrackingRange(12)
                    .build("murky_pearl"));
    public static final RegistryObject<EntityType<FeatherEntity>> FEATHER =
            ENTITIES.register("feather", () -> EntityType.Builder.<FeatherEntity>of(FeatherEntity::new, MobCategory.MISC)
                    .sized(0.3f, 0.3f).clientTrackingRange(12)
                    .build("feather"));
    public static final RegistryObject<EntityType<VitricArrowEntity>> VITRIC_ARROW =
            ENTITIES.register("vitric_arrow", () -> EntityType.Builder.<VitricArrowEntity>of(VitricArrowEntity::new, MobCategory.MISC)
                    .sized(0.3f, 0.3f).clientTrackingRange(12)
                    .build("vitric_arrow"));
    public static final RegistryObject<EntityType<StarProjEntity>> STAR =
            ENTITIES.register("star", () -> EntityType.Builder.<StarProjEntity>of(StarProjEntity::new, MobCategory.MISC)
                    .sized(0.3f, 0.3f).clientTrackingRange(12)
                    .build("star"));
    public static final RegistryObject<EntityType<SpatBlockEntity>> SPAT_BLOCK =
            ENTITIES.register("spat_block", () -> EntityType.Builder.<SpatBlockEntity>of(SpatBlockEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.6f).clientTrackingRange(12)
                    .build("spat_block"));
    public static final RegistryObject<EntityType<PrimedEepEntity>> EEP =
            ENTITIES.register("eep", () -> EntityType.Builder.<PrimedEepEntity>of(PrimedEepEntity::new, MobCategory.MISC)
                    .sized(0.98f, 0.98f).clientTrackingRange(12)
                    .build("eep"));
}
