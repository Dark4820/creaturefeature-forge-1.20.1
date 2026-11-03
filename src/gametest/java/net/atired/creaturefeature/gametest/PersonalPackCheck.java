package net.atired.creaturefeature.gametest;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.misc.CreatureToggles;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * PORT: dev-only (src/gametest, never in the jar). Checks the user's personal datapack and config (C:\cf\personal)
 * once the server has started: run with -Dcreaturefeature.personalCheck=true after copying cf_personal into
 * run/world/datapacks and creaturefeature-server.toml into run/world/serverconfig. Logs CF_PERSONAL_OK or the
 * failures (CF_PERSONAL_FAIL).
 */
@Mod.EventBusSubscriber(modid = CreatureFeature.MODID)
public class PersonalPackCheck {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean("creaturefeature.personalCheck")) {
            return;
        }
        MinecraftServer server = event.getServer();
        List<String> fails = new ArrayList<>();
        expect(fails, server, Biomes.SNOWY_TAIGA, CFEntityInit.VERTIGO.get(), true);
        expect(fails, server, Biomes.ICE_SPIKES, CFEntityInit.VERTIGO.get(), true);
        expect(fails, server, Biomes.DARK_FOREST, CFEntityInit.VERTIGO.get(), false);
        expect(fails, server, Biomes.PLAINS, CFEntityInit.VERTIGO.get(), false);
        expect(fails, server, Biomes.END_HIGHLANDS, CFEntityInit.VERTIGO.get(), false);
        expect(fails, server, Biomes.END_HIGHLANDS, CFEntityInit.STAINED_GLASS.get(), true);
        expect(fails, server, Biomes.SMALL_END_ISLANDS, CFEntityInit.STAINED_GLASS.get(), true);
        expect(fails, server, Biomes.DESERT, CFEntityInit.STAINED_GLASS.get(), false);
        expect(fails, server, Biomes.LUSH_CAVES, CFEntityInit.BLOSSOM.get(), true);
        expect(fails, server, Biomes.JUNGLE, CFEntityInit.BLOSSOM.get(), false);
        expect(fails, server, Biomes.LUSH_CAVES, CFEntityInit.BEAUTY.get(), false);
        expect(fails, server, Biomes.JAGGED_PEAKS, CFEntityInit.MOCKINGBIRD.get(), false);
        expect(fails, server, Biomes.END_HIGHLANDS, CFEntityInit.NOTHING.get(), false);
        // untouched ones keep the original's spawns
        expect(fails, server, Biomes.SAVANNA, CFEntityInit.MINDS.get(), true);
        expect(fails, server, Biomes.END_HIGHLANDS, CFEntityInit.DREAMWEAVER.get(), true);
        for (EntityType<?> type : List.of(CFEntityInit.BEAUTY.get(), CFEntityInit.NOTHING.get(), CFEntityInit.MOCKINGBIRD.get())) {
            if (!CreatureToggles.isDisabled(type)) fails.add(type.getDescriptionId() + " should be disabled by the config");
        }
        if (CreatureToggles.isDisabled(CFEntityInit.VERTIGO.get())) fails.add("vertigo should stay enabled");
        boolean zombiflore = server.registryAccess().registryOrThrow(ForgeRegistries.Keys.BIOME_MODIFIERS)
                .containsKey(new ResourceLocation("skarrier_mobs", "zombiflore_biome_modifier"));
        LOGGER.info("CF_PERSONAL zombiflore modifier loaded: {} (Skarrier Mobs installed: {})", zombiflore,
                net.minecraftforge.fml.ModList.get().isLoaded("skarrier_mobs"));
        if (fails.isEmpty()) {
            LOGGER.info("CF_PERSONAL_OK");
        } else {
            fails.forEach(f -> LOGGER.error("CF_PERSONAL_FAIL {}", f));
        }
    }

    private static void expect(List<String> fails, MinecraftServer server, ResourceKey<Biome> biome, EntityType<?> type, boolean present) {
        Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
        boolean has = biomes.getOrThrow(biome).getMobSettings().getMobs(MobCategory.MONSTER).unwrap().stream().anyMatch(d -> d.type == type);
        if (has != present) {
            fails.add(type.getDescriptionId() + (present ? " missing in " : " should not be in ") + biome.location());
        }
    }
}
