package net.atired.creaturefeature.misc;

import net.atired.creaturefeature.init.CFMobEffectInit;
import net.atired.creaturefeature.statuseffects.InfestedEffect;
import net.atired.creaturefeature.statuseffects.OozingEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * PORT: port-only (user decision). The blighted brain gives 1.21's Infested and Oozing, absent in 1.20.1.
 * Optional Trials Chambers compat (mod id "trials", not required): when it is installed its trials:infested and
 * trials:oozing are used (looked up by registry name, so nothing breaks without it); otherwise the port's own
 * creaturefeature:infested / creaturefeature:oozing, which copy 1.21's behaviour. The hooks below run only the
 * port's own effects (Trials' effects do their own work).
 */
@Mod.EventBusSubscriber(modid = net.atired.creaturefeature.CreatureFeature.MODID)
public class TrialsCompat {
    public static final String TRIALS = "trials";

    public static MobEffect infested() {
        return pick(new ResourceLocation(TRIALS, "infested"), CFMobEffectInit.INFESTED.get());
    }

    public static MobEffect oozing() {
        return pick(new ResourceLocation(TRIALS, "oozing"), CFMobEffectInit.OOZING.get());
    }

    private static MobEffect pick(ResourceLocation trialsId, MobEffect own) {
        if (ModList.get().isLoaded(TRIALS)) {
            MobEffect trials = ForgeRegistries.MOB_EFFECTS.getValue(trialsId);
            if (trials != null) {
                return trials;
            }
        }
        return own;
    }

    // 1.21 calls MobEffect.onMobHurt when the mob takes damage: LivingDamageEvent is the 1.20.1 point where the
    // damage is actually applied
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getAmount() > 0
                && event.getEntity().hasEffect(CFMobEffectInit.INFESTED.get())) {
            ((InfestedEffect) CFMobEffectInit.INFESTED.get()).onMobHurt(event.getEntity());
        }
    }

    // 1.21 calls MobEffect.onMobRemoved with reason KILLED
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity().hasEffect(CFMobEffectInit.OOZING.get())) {
            ((OozingEffect) CFMobEffectInit.OOZING.get()).onMobDeath(event.getEntity());
        }
    }
}
