package net.atired.creaturefeature.entity;

import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class BeautyEntity extends Monster {
    private static final EntityDataAccessor<Float> SPIN= SynchedEntityData.defineId(BeautyEntity.class, EntityDataSerializers.FLOAT);
    // PORT: LivingEntity's default gravity (0.08), the base value of Forge's ENTITY_GRAVITY attribute
    private static final double DEFAULT_GRAVITY = 0.08;

    public BeautyEntity(EntityType<? extends Monster> entityType, Level level) {
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
    public void tick() {
        if(getSpin()>0.0f&&!level().isClientSide()){
            setSpin(Math.max(getSpin()-0.05f,0.0f));
        }
        if(getSpin()>0.2f&&level().isClientSide()){
            level().addParticle(CFParticleInit.LEAF_PARTICLE.get(),getX(Math.random()-0.5),getY(Math.random()/1.5f+0.4),getZ(Math.random()-0.5),0,0.1,0);
        }
        updateGravity();
        super.tick();
    }

    // PORT: getDefaultGravity() (1.20.5+) does not exist; LivingEntity.travel in Forge 1.20.1 reads the
    // ENTITY_GRAVITY attribute, so its base value gets the original formula (default * 0.7 * (1 - spin)).
    // Above spin 1 the value is negative and the beauty floats up while it spins, as in the original.
    private void updateGravity() {
        AttributeInstance gravity = getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        double value = DEFAULT_GRAVITY*0.7f*(1.0f-getSpin());
        if(gravity!=null&&gravity.getBaseValue()!=value){
            gravity.setBaseValue(value);
        }
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
    public boolean hurt(DamageSource source, float amount) {
        if(!level().isClientSide()){
            if(source.is(DamageTypeTags.IS_PROJECTILE)){
                setSpin(1.5f);
                // PORT: BREEZE_WHIRL does not exist in 1.20.1 -> PLAYER_ATTACK_SWEEP, a similar whoosh (decision D3)
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.1F);
                this.playSound(SoundEvents.GRASS_BREAK);
                addDeltaMovement(getLookAngle().multiply(-0.5,0.0,-0.5).add(0,0.1,0));
                if(source.getDirectEntity()!=null){
                    source.getDirectEntity().setDeltaMovement(source.getDirectEntity().getDeltaMovement().multiply(3,1,3).add(0,0.3,0));
                }
                return false;
            }else{
                if(getSpin()>0.1f){
                    return super.hurt(source,amount);
                }
                setSpin(1.5f);
                // PORT: BREEZE_WHIRL -> PLAYER_ATTACK_SWEEP (decision D3)
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.1F);
                this.playSound(SoundEvents.GRASS_BREAK);
                addDeltaMovement(getLookAngle().multiply(-0.5,0.0,-0.5).add(0,0.1,0));
                return false;
            }
        }
        if(getSpin()<=0.1f){
            return false;
        }
        return super.hurt(source,amount);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean shallHurt = super.doHurtTarget(entity);
        if(getSpin()<1.0&&shallHurt){
            entity.addDeltaMovement(new Vec3(0,0.1,0));
            setSpin(1.0f);
        }
        return shallHurt;
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    public static AttributeSupplier.Builder createBeautyAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 30.0).add(Attributes.ATTACK_DAMAGE,4.0).add(Attributes.MOVEMENT_SPEED, 0.31).add(Attributes.MAX_HEALTH,10.0).add(Attributes.ATTACK_KNOCKBACK,-1.0);
    }
    public void setSpin(float spin) {
        entityData.set(SPIN,spin);
    }
    public float getSpin() {
        return entityData.get(SPIN);
    }

    @Override
    public float getSpeed() {
        return super.getSpeed()*(1.0f-getSpin());
    }

    // PORT: defineSynchedData(SynchedEntityData.Builder) -> defineSynchedData() + entityData.define
    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPIN,0.0f);
        super.defineSynchedData();
    }



}
