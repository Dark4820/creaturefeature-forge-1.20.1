package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.misc.DummyTrigger;
import net.minecraft.advancements.CriteriaTriggers;

// PORT: there is no TRIGGER_TYPE registry in 1.20.1: triggers go to CriteriaTriggers.register
// (public static in 1.20.1), called once from the mod constructor through init(). Ids are unchanged.
public class CFAchievements {
    public static final DummyTrigger SLEEPY = register("sleepy");
    public static final DummyTrigger FRIENDLESS = register("friendless");
    public static final DummyTrigger PUNCH_EVERYONE = register("punch_everyone");
    public static final DummyTrigger GYAS = register("gyas");
    public static final DummyTrigger SINISTER = register("sinister");
    public static final DummyTrigger PILL = register("pill");
    public static final DummyTrigger RENOVATION = register("renovation");
    public static final DummyTrigger NOBODY = register("nobody");

    private static DummyTrigger register(String name) {
        return CriteriaTriggers.register(new DummyTrigger(CreatureFeature.getId(name)));
    }

    public static void init() {
    }
}
