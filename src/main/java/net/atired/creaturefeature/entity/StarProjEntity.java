package net.atired.creaturefeature.entity;

import net.atired.creaturefeature.Config;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class StarProjEntity extends ThrowableItemProjectile {
    public Vec3[] positions = new Vec3[8];
    public float lastYaw;
    public float lastPitch;
    public int posTracker = 0;
    private static final EntityDataAccessor<Boolean> AGED= SynchedEntityData.defineId(StarProjEntity.class, EntityDataSerializers.BOOLEAN);

    public boolean hitStuff = false;
    public LivingEntity toHurt = null;
    public StarProjEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    // PORT: EntityType.Builder eyeHeight(0.2F)/passengerAttachments(0.3125F)/ridingOffset(-0.1F)
    // (non-living entities: Entity.getEyeHeight(Pose, EntityDimensions) in 1.20.1)
    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.2F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.3125;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.1;
    }
    public StarProjEntity(Level level, LivingEntity shooter) {
        super(CFEntityInit.STAR.get(), shooter, level);
    }

    public StarProjEntity(Level level, double x, double y, double z) {
        super(CFEntityInit.STAR.get(), x, y, z, level);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean save(CompoundTag compound) {
        return super.save(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
    }

    @Override
    public void tick() {
        super.tick();
        if(level() instanceof ServerLevel serverLevel && toHurt==null){
            LivingEntity otherLiving=null;
            for(LivingEntity living : serverLevel.getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(7))){
                if(living!=getOwner()){
                    if(otherLiving==null){
                        otherLiving=living;
                    }else if(distanceTo(otherLiving)>distanceTo(living)){
                        otherLiving=living;
                    }
                }
            }
            if(otherLiving!=null){
                toHurt=otherLiving;
            }
        }
        if(tickCount<30&&toHurt!=null){
            this.setDeltaMovement(getDeltaMovement().lerp(this.toHurt.getPosition(1).add(0,1.6,0).subtract(this.getPosition(1)).normalize().scale(1.9), Config.SAINT.get()?0.035f:0.065f /* PORT: isTrue() -> get() */));
        }
        if(this.isAged())this.tickCount=Math.max(47,this.tickCount);
        if(this.tickCount>53){
            if(level() instanceof ServerLevel serverLevel){
                this.setDeltaMovement(getDeltaMovement().scale(0.6));

            }
            if(this.tickCount>60){
                discard();
            }
        }
        if(this.posTracker<8){
            for(int i =0;i<8;i++){
                this.positions[i]=position();
                this.posTracker+=1;
            }
        }
        else{
            for (int i = 1; i < 8; i++) {
                this.positions[i-1]=this.positions[i];
            }
            this.positions[7]=position();
        }
    }

    // PORT: applyGravity() { return; } (1.20.5+) -> getGravity() = 0 (above)
    public void setHitStuff(){
        this.hitStuff=true;
        entityData.set(AGED,true);
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        if(this.hitStuff)return;
        if(result.getEntity() instanceof LivingEntity living&&(living!=getOwner()||this.tickCount>20)&&level()instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(CFParticleInit.STAR_PARTICLE.get(),getX(),getY(0.5),getZ(),1,0.02,0.02,0.02,0);

            living.setRemainingFireTicks(Math.max(0,living.getRemainingFireTicks())+15);
            living.hurt(damageSources().mobProjectile(this,(getOwner() instanceof LivingEntity ? (LivingEntity)getOwner() : null)),4);
        }

        if(level() instanceof ServerLevel serverLevel){
            setHitStuff();
        }
        super.onHitEntity(result);
    }
    public boolean isAged(){
        return this.entityData.get(AGED);
    }
    // PORT: defineSynchedData(SynchedEntityData.Builder) -> defineSynchedData() + entityData.define
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGED,false);
        
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if(this.hitStuff)return;
        if(result.getType()!= HitResult.Type.ENTITY||((EntityHitResult)result).getEntity()!=getOwner()){
            if(level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(CFParticleInit.STAR_PARTICLE.get(),getX(),getY(0.5),getZ(),1,0.02,0.02,0.02,0);
            }
            if(level() instanceof ServerLevel serverLevel){
                setHitStuff();
            }
        }else{
            if(result instanceof EntityHitResult hitResult && hitResult.getEntity()==getOwner()&&this.tickCount>20){
                if(level() instanceof ServerLevel serverLevel){
                    serverLevel.sendParticles(CFParticleInit.STAR_PARTICLE.get(),getX(),getY(0.5),getZ(),1,0.02,0.02,0.02,0);
                }
                if(level() instanceof ServerLevel serverLevel){
                    setHitStuff();
                }
            }
        }
    }

    // PORT: getDefaultGravity() -> ThrowableProjectile.getGravity() (1.20.1)
    @Override
    protected float getGravity() {
        return 0.0f;
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.MAGMA_CREAM.asItem();
    }
}
