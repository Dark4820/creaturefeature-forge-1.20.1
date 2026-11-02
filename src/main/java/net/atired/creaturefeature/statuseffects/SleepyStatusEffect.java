package net.atired.creaturefeature.statuseffects;

import net.atired.creaturefeature.init.CFAchievements;
import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SleepyStatusEffect extends MobEffect {
    public SleepyStatusEffect(MobEffectCategory category) {
        super(category, 0x93c077);
    }

    // PORT: applyEffectTick returns void in 1.20.1
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.setXRot(Math.min(90.0f,livingEntity.getViewXRot(1)+1));
        if(livingEntity instanceof ServerPlayer serverPlayer){
            CFAchievements.SLEEPY.trigger(serverPlayer);

        }
        if(Math.random()>0.9)
        livingEntity.level().addParticle(CFParticleInit.SLEEPY_PARTICLE.get(),livingEntity.getX(-0.5f+Math.random()),livingEntity.getEyeY(),livingEntity.getZ(-0.5f+Math.random()),0,-0.1,0);
        super.applyEffectTick(livingEntity, amplifier);
    }

    // PORT: shouldApplyEffectTickThisTick -> isDurationEffectTick
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
