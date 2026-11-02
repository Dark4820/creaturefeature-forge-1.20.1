package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: Supplier -> RegistryObject. Phase F1 registers the types only (common code); the client providers,
// particles/*.json and textures arrive in F2. RED_POWDER and FISHEYE (★, source only) wait for F9.
public class CFParticleInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CreatureFeature.MODID);

    public static final RegistryObject<SimpleParticleType> SLEEPY_PARTICLE = PARTICLE_TYPES.register(
            "sleepy",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> CRIT_TEXT_PARTICLE = PARTICLE_TYPES.register(
            "crit_text",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> LEAF_PARTICLE = PARTICLE_TYPES.register(
            "leaf",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> CASING_PARTICLE = PARTICLE_TYPES.register(
            "casing",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> GLASS_SHARD_PARTICLE = PARTICLE_TYPES.register(
            "glass_shard",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> GLASS_SHARD_SINISTER_PARTICLE = PARTICLE_TYPES.register(
            "glass_shard_sinister",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> FLOWER_PARTICLE = PARTICLE_TYPES.register(
            "flower",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> CRIT_PARTICLE = PARTICLE_TYPES.register(
            "crit",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> CRIT_VER_PARTICLE = PARTICLE_TYPES.register(
            "crit_ver",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> SPARKLE_PARTICLE = PARTICLE_TYPES.register(
            "sparkle",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> SLEEPY_EXPLODE_PARTICLE = PARTICLE_TYPES.register(
            "sleepy_explode",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> RABIES_PARTICLE = PARTICLE_TYPES.register(
            "rabies",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> TOOT_PARTICLE = PARTICLE_TYPES.register(
            "toot",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> CLEAVE_PARTICLE = PARTICLE_TYPES.register(
            "cleave",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> SPORE_PARTICLE = PARTICLE_TYPES.register(
            "spore",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> STAR_PARTICLE = PARTICLE_TYPES.register(
            "star",
            () -> new SimpleParticleType(false)
    );
    public static final RegistryObject<SimpleParticleType> SPIT_PARTICLE = PARTICLE_TYPES.register(
            "spores_spit",
            () -> new SimpleParticleType(false)
    );
}
