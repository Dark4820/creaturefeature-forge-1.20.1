// PORT (mechanical): DUST_PILLAR does not exist in 1.20.1 -> the generic BLOCK particle (decision D3)
package net.atired.creaturefeature.entity;

import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.atired.creaturefeature.networking.payloads.VelSyncPayload;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
// PORT: the original's unused NeoForge PacketDistributor import is dropped

public class SpatBlockEntity extends Projectile {
    public BlockState state = Blocks.STONE.defaultBlockState();
    private static final EntityDataAccessor<Boolean> CRITICAL= SynchedEntityData.defineId(SpatBlockEntity.class, EntityDataSerializers.BOOLEAN);

    public SpatBlockEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    // PORT: EntityType.Builder eyeHeight(0.3F)/passengerAttachments(0.3125F)/ridingOffset(-0.1F)
    // (non-living entities: Entity.getEyeHeight(Pose, EntityDimensions) in 1.20.1)
    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.3F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.3125;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.1;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

    }
    // PORT: getAddEntityPacket(ServerEntity) -> getAddEntityPacket(); the block state still travels as the packet data
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.state));
    }


    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.state = Block.stateById(packet.getData());
        this.blocksBuilding = true;
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();
        this.setPos(d0, d1, d2);
    }
    public void setCritical(boolean critical) {
        entityData.set(CRITICAL,critical);
    }
    public boolean getCritical() {
        return entityData.get(CRITICAL);
    }
    // PORT: defineSynchedData(SynchedEntityData.Builder) -> defineSynchedData() + entityData.define
    @Override
    protected void defineSynchedData() {
        this.entityData.define(CRITICAL,false);
    }
    @Override
    public void tick() {
        if(level()!=null){
            for (int i = 0; i < 3; i++) {
                level().addParticle(CFParticleInit.SPORE_PARTICLE.get(),getX((Math.random()-0.5)*1.5),getY()+0.3,getZ((Math.random()-0.5)*1.5),0,0,0);
            }
        }
        this.move(MoverType.SELF,getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.96));
        // PORT: getGravity() = getDefaultGravity()*0.5 = 0 for a plain Projectile (1.21) -> 0.0
        addDeltaMovement(new Vec3(0,0.0-0.08,0));
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        // PORT: EventHooks.onProjectileImpact/hitTargetOrDeflectSelf -> ForgeEventFactory.onProjectileImpact/onHit
        if (hitresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if(level() instanceof ServerLevel serverLevel){
            playSound(SoundEvents.SLIME_SQUISH,1.5f,2.0f);
            playSound(SoundEvents.HONEY_DRINK,1.5f,2.0f);
            for(LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(4))){
                if(entity.distanceTo(this)<3){
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40,2,true,false));
                }
            }
            Vec3 pos = result.getBlockPos().getCenter().add(0,0.5,0);
            serverLevel.sendParticles(CFParticleInit.SPIT_PARTICLE.get(),pos.x,pos.y+0.01,pos.z,1,0.1,0,0.1,0);
            serverLevel.sendParticles(CFParticleInit.SPORE_PARTICLE.get(),pos.x,pos.y+0.21,pos.z,16,0.3,0.2,0.3,0);
            for (int i = 0; i < 3; i++) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,this.state),getX(),getY()+0.3,getZ(),1,0.2,0,0.2,1);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,this.state),getX(),getY()+0.3,getZ(),1,0.2,0,0.2,1);
            }

        }
        discard();
    }

    
}
