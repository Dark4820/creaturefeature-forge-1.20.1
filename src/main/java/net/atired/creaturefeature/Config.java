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

        SPEC2 = builder.build();
    }
}
