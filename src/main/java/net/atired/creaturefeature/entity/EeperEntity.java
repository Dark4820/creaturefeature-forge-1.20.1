// PORT (mechanical): Math.clamp (Java 21) -> Mth.clamp; PacketDistributor -> CFNetwork (SimpleChannel); Holder<MobEffect> -> RegistryObject.get()
package net.atired.creaturefeature.entity;

import com.ibm.icu.impl.Pair;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.util.Mth;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.atired.creaturefeature.init.CFParticleInit;
import net.atired.creaturefeature.networking.payloads.VelSyncPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.atired.creaturefeature.networking.CFNetwork;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class EeperEntity extends Monster {
    private static final EntityDataAccessor<Integer> FUSILAGE= SynchedEntityData.defineId(EeperEntity.class, EntityDataSerializers.INT);
    public int leapDelay = 12;
    public EeperEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    // PORT: EntityType.Builder eyeHeight(1.78F)/passengerAttachments(2.0125F)/ridingOffset(-0.7F)
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.78F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 2.0125;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.7;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        super.registerGoals();
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }
    @Override
    public boolean doHurtTarget(Entity entity) {
        return false;
    }

    @Override
    public void tick() {
        if(this.random.nextFloat()>0.8&&getDeltaMovement().length()<0.4&&!walkAnimation.isMoving()){
            level().addParticle(CFParticleInit.SLEEPY_PARTICLE.get(),getX(Math.random()-0.5),getEyeY(),getZ(Math.random()-0.5),0,-0.1,0);
        }
        if(getTarget()!=null&&getFuse()<4&&distanceTo(getTarget())>3.5){
            if(this.leapDelay<15&&this.leapDelay>0&&!onGround()&&this.leapDelay%3==0){
                this.getNavigation().stop();
            }
            this.leapDelay-=1;
            if(onGround()){
                if(this.leapDelay<=0){
                    Vec3 dir = getTarget().getPosition(1).subtract(getPosition(1)).multiply(1,0,1).normalize();
                    float angle = (float)Math.atan2(dir.x,dir.z);
                    addDeltaMovement(new Vec3(0,0.55,0.9).yRot(angle+(float)(Math.random()-0.5f)/2.0f));
                    this.setYRot(-angle*180.0f/3.14f);
                    this.yBodyRot=this.getYRot();
                    this.getNavigation().stop();
                    this.leapDelay=5;

                }

            }
        }
        if(level()instanceof ServerLevel serverLevel){
            if(getTarget()!=null&&distanceTo(getTarget())<2){
                setFuse(Mth.clamp(getFuse()+1,0,21));
                if(getFuse()==21){
                    float f = 1.7f;
                    this.dead = true;
                    serverLevel.sendParticles(CFParticleInit.SLEEPY_PARTICLE.get(), this.getX(), this.getY(), this.getZ(),40,2,1.8,2,0.1);
                    serverLevel.sendParticles(CFParticleInit.SLEEPY_PARTICLE.get(), this.getX(), this.getY(), this.getZ(),5,0.4,0.4,0.4,0.1);
                    serverLevel.sendParticles(CFParticleInit.SLEEPY_EXPLODE_PARTICLE.get(), this.getX(), this.getY(), this.getZ(),70,2.3,1.8,2.3,0.1);
                    for(LivingEntity i : serverLevel.getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(5))){
                        if(i!=this&&this.distanceTo(i)<5){
                            Vec3 dir = i.getDeltaMovement().add(i.getPosition(1).subtract(getPosition(1)).multiply(1,0.2,1).normalize().multiply(2,2,2).add(0,0.6,0));
                            i.setDeltaMovement(dir);
                            i.hurt(damageSources().explosion(this,this),6.0f);
                            i.addEffect(new MobEffectInstance(CFMobEffectInit.SLEEPY.get(),120,0));
                            if(i instanceof ServerPlayer player){
                                VelSyncPayload payload = new VelSyncPayload(player.getId(),dir.x,dir.y,dir.z);
                                CFNetwork.sendToPlayer(player,payload);
                            }
                        }
                    }
                    // PORT: triggerOnDeathMobEffects (1.20.5+, for weaving/oozing/infested) does not exist in 1.20.1, where no
                    // effect reacts to the death: nothing to call (vanilla's 1.20.1 creeper does not either)
                    this.discard();
                }
            }else{
                setFuse(Math.max(0,getFuse()-2));
            }
        }

        super.tick();
    }

    public void setFuse(int fuse) {
        entityData.set(FUSILAGE,fuse);
    }
    public int getFuse() {
        return entityData.get(FUSILAGE);
    }
    // PORT: defineSynchedData(SynchedEntityData.Builder) -> defineSynchedData() + entityData.define
    @Override
    protected void defineSynchedData() {
        this.entityData.define(FUSILAGE,-1);
        super.defineSynchedData();
    }

    public static AttributeSupplier.Builder createEeperAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 20.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.MAX_HEALTH,30.0);
    }

}
