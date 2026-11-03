package net.atired.creaturefeature;

import net.minecraftforge.common.ForgeConfigSpec;

// PORT: ModConfigSpec (NeoForge) -> ForgeConfigSpec. Keys, comments and defaults are unchanged
// (including SAINT reusing the "pathogen" key, as in the original).
public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec SPEC2;
    public static final ForgeConfigSpec.BooleanValue REMODEL_FRIEND;
    public static final ForgeConfigSpec.BooleanValue REMODEL_MB;
    public static final ForgeConfigSpec.BooleanValue FIX_SAS;
    public static final ForgeConfigSpec.BooleanValue END_DREAMWEAVER;
    public static final ForgeConfigSpec.BooleanValue PATHOGEN;
    public static final ForgeConfigSpec.BooleanValue SAINT;
    public static final ForgeConfigSpec.BooleanValue FIEND_FOLIO_RELOADED;
    // PORT: port-only (F10). One switch per creature in the server config; all true = the original's behaviour.
    // See misc/CreatureToggles for what "disabled" means.
    public static final String[] CREATURES = {"minds", "machination", "sinister", "detritus", "beauty", "fiend", "fend",
            "friend", "blitz", "eeper", "nothing", "canary", "vertigo", "coat_of_arms", "minedflayer", "mockingbird",
            "pathogen", "blossom", "saint_solis", "stained_glass", "dreamweaver", "cannonball_crab"};
    public static final java.util.Map<String, ForgeConfigSpec.BooleanValue> CREATURE_ENABLED = new java.util.LinkedHashMap<>();
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        REMODEL_FRIEND = builder
                .comment("Shove FRIEND into a woodchipper.")
                .define("remodelfriend", false);

        REMODEL_MB = builder
                .comment("Pressure wash the Mockingbird.")
                .define("remodelmb", false);
        FIX_SAS = builder
                .comment("Remove postprocessing from the bear (in case of broken depth).")
                .define("fixsas", false);

        SPEC = builder.build();
        builder = new ForgeConfigSpec.Builder();
        END_DREAMWEAVER = builder
                .comment("Dreamweavers only spawn Nothings, Endermen and themselves.")
                .define("endreamweaver", false);
        PATHOGEN = builder
                .comment("Pathogen only spawns in temperate overworld biomes.")
                .define("pathogen", false);
        SAINT = builder
                .comment("Weakens the rotation of Saint Solis' Stars")
                .define("pathogen", false);
        FIEND_FOLIO_RELOADED = builder
                .comment("Gives Fiend a gun.")
                .define("ff_reloaded", true);
        // PORT: port-only (F10)
        builder.comment("Turn single creatures off. A disabled creature never spawns (naturally, from spawners or",
                        "from other creatures of the mod) and, if it already exists, vanishes without drops.",
                        "Spawn eggs and /summon still work for admins (with NBT, add Tags:[\"creaturefeature_allowed\"]).")
                .push("creatures");
        for (String creature : CREATURES) {
            CREATURE_ENABLED.put(creature, builder.define(creature, true));
        }
        builder.pop();

        SPEC2 = builder.build();
    }
}
