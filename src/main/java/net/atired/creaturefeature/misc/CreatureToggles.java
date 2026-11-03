package net.atired.creaturefeature.misc;

import net.atired.creaturefeature.Config;
import net.atired.creaturefeature.CreatureFeature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * PORT: port-only (F10). Per-creature switches of the server config ([creatures] in creaturefeature-server.toml).
 * A disabled creature:
 * - does not spawn naturally (spawn placement check denied);
 * - is not added to the world from spawners, other creatures of the mod (detritus births, dreamweaver summons,
 *   infections...) or anything else: EntityJoinLevelEvent is cancelled, so nothing drops;
 * - if it already exists (saved in a chunk, or the switch is turned off while the world runs) it is removed without
 *   drops when it loads or on its next tick.
 * Admin spawns are kept: spawn eggs (also from dispensers) and /summon without NBT go through finalizeSpawn with
 * SPAWN_EGG / DISPENSER / COMMAND and get the tag {@link #ALLOWED_TAG}; /summon with NBT can add it by hand.
 * With every creature enabled the disabled set is empty and every handler returns at once (original behaviour).
 */
@Mod.EventBusSubscriber(modid = CreatureFeature.MODID)
public class CreatureToggles {
    public static final String ALLOWED_TAG = "creaturefeature_allowed";
    private static volatile Set<EntityType<?>> disabled = Set.of();

    public static boolean isDisabled(EntityType<?> type) {
        Set<EntityType<?>> set = disabled;
        return !set.isEmpty() && set.contains(type);
    }

    private static boolean blocked(Entity entity) {
        return isDisabled(entity.getType()) && !entity.getTags().contains(ALLOWED_TAG);
    }

    /** Rebuilds the disabled set from the config (on load and reload; GameTests call it after changing a value). */
    public static void refresh() {
        java.util.HashSet<EntityType<?>> set = new java.util.HashSet<>();
        for (var entry : Config.CREATURE_ENABLED.entrySet()) {
            if (!enabled(entry.getValue())) {
                EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(CreatureFeature.getId(entry.getKey()));
                if (type != null) {
                    set.add(type);
                    // the canary's segments go with the canary
                    if (entry.getKey().equals("canary")) {
                        set.add(ForgeRegistries.ENTITY_TYPES.getValue(CreatureFeature.getId("canary_part")));
                    }
                }
            }
        }
        disabled = Set.copyOf(set);
    }

    private static boolean enabled(ForgeConfigSpec.BooleanValue value) {
        try {
            return value.get();
        } catch (IllegalStateException notLoaded) {
            return true;    // server config not loaded (client side, or before a world starts): everything enabled
        }
    }

    @Mod.EventBusSubscriber(modid = CreatureFeature.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void configLoaded(ModConfigEvent.Loading event) {
            if (event.getConfig().getSpec() == Config.SPEC2) {
                refresh();
            }
        }

        @SubscribeEvent
        public static void configReloaded(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() == Config.SPEC2) {
                refresh();
            }
        }
    }

    @SubscribeEvent
    public static void spawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (isDisabled(event.getEntityType())) {
            event.setResult(Event.Result.DENY);
        }
    }

    // Runs before the entity is added: marks the admin spawns of a disabled creature
    @SubscribeEvent
    public static void finalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (isDisabled(event.getEntity().getType())) {
            MobSpawnType type = event.getSpawnType();
            if (type == MobSpawnType.SPAWN_EGG || type == MobSpawnType.DISPENSER || type == MobSpawnType.COMMAND) {
                event.getEntity().addTag(ALLOWED_TAG);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void joinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && blocked(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    // Turned off while the world runs: the ones already loaded go on their next tick (discard = no drops)
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide() && blocked(entity)) {
            entity.discard();
        }
    }
}
