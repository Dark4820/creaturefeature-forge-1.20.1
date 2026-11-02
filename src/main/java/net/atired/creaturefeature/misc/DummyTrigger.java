package net.atired.creaturefeature.misc;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

// PORT: 1.20.1 triggers carry their own id and read JSON (no Codec, no TRIGGER_TYPE registry);
// the instance keeps the original "player" field.
public class DummyTrigger extends SimpleCriterionTrigger<DummyTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public DummyTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new TriggerInstance(this.id, player);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (a)->{return true;});
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ResourceLocation id, ContextAwarePredicate player) {
            super(id, player);
        }

        public boolean matches(Player item) {
            return this.getPlayerPredicate()!=null&&item.level()!=null;
        }
    }
}
