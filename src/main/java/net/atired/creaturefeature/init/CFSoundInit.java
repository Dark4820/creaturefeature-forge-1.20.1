package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: Holder<SoundEvent> -> RegistryObject<SoundEvent>. FISHEYE_SCREECH (★, source only) waits for phase F9.
public class CFSoundInit {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CreatureFeature.MODID);
    public static final RegistryObject<SoundEvent> NOTHING = register("music_disc.nothing");
    public static final RegistryObject<SoundEvent> NEW_AGE_NEVERMORE = register("music_disc.newagenevermore");
    public static final RegistryObject<SoundEvent> SINISTER_HURT = register("entity.sinister_hurt");
    public static final RegistryObject<SoundEvent> SINISTER_DIE = register("entity.sinister_die");
    //SOURCED FROM FIEND FOLIO REHEATED!!! PLAY IT IT'S FUCKING AWESOME
    public static final RegistryObject<SoundEvent> FIEND_HURT = register("entity.fiend_hurt");
    public static final RegistryObject<SoundEvent> FIEND_DIE = register("entity.fiend_die");
    public static final RegistryObject<SoundEvent> SHOT = register("entity.shot");
    public static final RegistryObject<SoundEvent> SG_RETREAT = register("entity.stained_glass_retreat");
    public static final RegistryObject<SoundEvent> SG_DIE = register("entity.stained_glass_die");
    public static final RegistryObject<SoundEvent> COA_HURT = register("entity.coa_hurt");
    public static final RegistryObject<SoundEvent> FRIEND = register("entity.friend");
    public static final RegistryObject<SoundEvent> FRIEND_HURT = register("entity.friend_hurt");

    // PORT: helper instead of repeating SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent)
    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(CreatureFeature.getId(name)));
    }
}
