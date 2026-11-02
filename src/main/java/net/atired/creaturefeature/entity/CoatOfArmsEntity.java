// PORT (mechanical): Holder.value() -> RegistryObject.get(); Math.clamp (Java 21) -> Mth.clamp
package net.atired.creaturefeature.entity;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.init.CFParticleInit;
import net.atired.creaturefeature.init.CFSoundInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
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

public class CoatOfArmsEntity extends Monster {
    public int delayOfShooting=-1;
    public int delayOfOpening=100;
    private static final EntityDataAccessor<Float> OPENING= SynchedEntityData.defineId(CoatOfArmsEntity.class, EntityDataSerializers.FLOAT);
    
    public CoatOfArmsEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    // PORT: EntityType.Builder eyeHeight(1.48F)/passengerAttachments(1.0125F)/ridingOffset(-0.7F)
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.48F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.0125;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.7;
    }

    @Override
    public void setYBodyRot(float offset) {
        if(getOpening()>0.6)return;
        super.setYBodyRot(offset);
    }

    @Override
    public void setYHeadRot(float rotation) {

        if(getOpening()>0.6)return;
        super.setYHeadRot(rotation);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CFSoundInit.COA_HURT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CFSoundInit.COA_HURT.get();
    }

    @Override
    public void setYRot(float yRot) {
        if(getOpening()>0.6)return;
        super.setYRot(yRot);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new CoatRotationControl(this);
    }
    public Vec3 lerpedDelta=new Vec3(0,0,0);
    @Override
    public void tick() {
        super.tick();
        this.lerpedDelta=this.lerpedDelta.lerp(getDeltaMovement(),0.3f);
        if(level() instanceof ServerLevel serverLevel&&!isDeadOrDying()){
            if(this.delayOfShooting>0){
                float angleTo=0f;
                if(getTarget()!=null){
                    Vec3 dirTo=getTarget().getPosition(1).subtract(getPosition(1)).multiply(1,0,1).normalize();
                    double sourceAngle=-getYRot()/180f*3.14f;
                    double targetAngle=Mth.atan2(dirTo.x,dirTo.z);
                    double sin = Math.sin(targetAngle - sourceAngle);
                    double cos = Math.cos(targetAngle - sourceAngle);
                    angleTo=(float)Math.atan2(sin,cos);
                    angleTo*=-1f;
                    angleTo=Mth.clamp(angleTo,-0.15f,0.15f);
                }
                else{
                    if(this.delayOfShooting>0){
                        this.delayOfShooting-=1;
                        if(this.delayOfShooting<=0){
                            this.delayOfShooting=-1;
                            this.delayOfOpening=100;
                        }
                    }
                }
                if(this.delayOfShooting<18&&this.delayOfShooting>1){
                    BulletEntity bulletEntity=new BulletEntity(CFEntityInit.BULLET.get(),serverLevel);
                    bulletEntity.ownerUsual=this;
                    serverLevel.addFreshEntity(bulletEntity);
                    bulletEntity.setPos(getPosition(1).add((Math.random()-0.5)/2f,1.1+(Math.random()-0.5)*1.2f,(Math.random()-0.5)/2f));
                    bulletEntity.setYRot(getYRot()+angleTo/3.14f*180f);
                    serverLevel.sendParticles(CFParticleInit.CASING_PARTICLE.get(),bulletEntity.getX(),bulletEntity.getY(),bulletEntity.getZ(),1,0,0,0,0.2);

                }
            }
            if(getTarget()!=null){

                this.delayOfOpening--;
                float dist = distanceTo(getTarget());
                boolean xOrZ=(Math.abs(getX()-getTarget().getX())<0.8||Math.abs(getZ()-getTarget().getZ())<0.8)&&Math.abs(getY()-getTarget().getY())<1.5;
                if(((dist<17&&xOrZ&dist>2)||this.delayOfShooting>0)&&this.delayOfOpening<0){
                    if(getOpening()<0.2f){
                        this.navigation.stop();
                        // PORT: Mob.stopInPlace() (1.20.5+) -> its body: stop the navigation and the movement inputs
                        this.getNavigation().stop(); this.setXxa(0.0F); this.setYya(0.0F); this.setSpeed(0.0F);
                        this.navigation.moveTo(getTarget(),1f);
                        // PORT: BREEZE_WHIRL does not exist in 1.20.1 -> PLAYER_ATTACK_SWEEP, as for the beauty (decision D3)
                        playSound(SoundEvents.PLAYER_ATTACK_SWEEP,1f,0.2f);
                        this.delayOfShooting=30;
                    }
                    lookAt(getTarget(),450,450);
                    this.setYRot(Math.round((this.getYRot()/90))*90f);
                    setOpening(Mth.lerp(0.3f,getOpening(),1f));
                    if(this.delayOfShooting>0){
                        this.delayOfShooting-=1;
                        if(this.delayOfShooting<=0){
                            this.delayOfShooting=-1;
                            this.delayOfOpening=100;
                        }
                    }
                }
                else {
                    if(dist<7){
                        Vec3 dirTo=getTarget().getPosition(1).subtract(getPosition(1)).multiply(1,0,1).normalize();
                        Vec3 dirTwo=dirTo.yRot(1.57f).add(getPosition(1));
                        Vec3 dirThree=dirTo.yRot(-1.57f).add(getPosition(1));
                        this.navigation.stop();
                        if(Mth.sin(tickCount/20.0f)>0){
                            this.moveControl.strafe(-0.1f,-1);
                        }
                        else{
                            this.moveControl.strafe(-0.1f,1);
                        }
                        this.lookAt(getTarget(),360,360);
                        this.setSpeed(9f);
                    }
                    setOpening(getOpening()*0.9f);
                }
            }
            else setOpening(getOpening()*0.9f);
        }
        if(level()!=null&&level().isClientSide()){
            if(getOpening()>0.1&&getOpening()<0.6){
                this.setYRot(Math.round((this.getYRot()/90))*90f);
            }
            level().addParticle(ParticleTypes.LARGE_SMOKE,getX((Math.random()-0.5f)*0.2),getY(0.4+Math.random()*0.6f),getZ((Math.random()-0.5f)*0.2),0,-0.1,0);
        }
    }


    // PORT: defineSynchedData(SynchedEntityData.Builder) -> defineSynchedData() + entityData.define

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OPENING,0f);
        super.defineSynchedData();
    }
    public void setOpening(float opening) {
        entityData.set(OPENING,opening);
    }
    public float getOpening() {
        return entityData.get(OPENING);
    }
    public static AttributeSupplier.Builder createCoatOfArmsAttributes() {
        // PORT: getDefaultGravity() = default * 0.6 -> base value of Forge's ENTITY_GRAVITY attribute (0.08 * 0.6)
        return Monster.createMonsterAttributes().add(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get(), 0.08*0.6f).add(Attributes.FOLLOW_RANGE, 30.0).add(Attributes.ATTACK_DAMAGE,0.0).add(Attributes.MOVEMENT_SPEED, 0.31).add(Attributes.MAX_HEALTH,20.0);
    }

    @Override
    public float getSpeed() {
        float speeded = super.getSpeed()*(1.0f-getOpening()*0.8f);
        if(getTarget()!=null){
            float dist = distanceTo(getTarget());
            dist=Mth.clamp(dist/2.0f-1.5f,0f,1f);
            return dist*speeded;
        }
        return speeded;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0])));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));

        super.registerGoals();
    }
    public static class CoatRotationControl extends BodyRotationControl{
        private CoatOfArmsEntity coat = null;
        public CoatRotationControl(CoatOfArmsEntity mob) {
            super(mob);
            this.coat=mob;
        }

        @Override
        public void clientTick() {

            this.coat.yBodyRot = this.coat.getYRot();
            if(this.coat.getOpening()>0.6)return;
            super.clientTick();
        }
    }
}
