package net.atired.creaturefeature.statuseffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FiendishEffect extends MobEffect {
    public FiendishEffect(MobEffectCategory category) {
        super(category, 0xff57f9);
    }

    // PORT: applyEffectTick returns void in 1.20.1
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
//        if(Math.random()>0.9)
//        livingEntity.level().addParticle(CFParticleInit.CRIT_VER_PARTICLE.get(),livingEntity.getX(-0.5f+Math.random()),livingEntity.getY(Math.random()),livingEntity.getZ(-0.5f+Math.random()),0,-0.1,0);
        super.applyEffectTick(livingEntity, amplifier);
    }

    // PORT: onMobHurt (1.20.5+) removed: the original override only called super.

    // PORT: shouldApplyEffectTickThisTick -> isDurationEffectTick
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
