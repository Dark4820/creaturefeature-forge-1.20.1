// PORT (mechanical): PacketDistributor -> CFNetwork (SimpleChannel); DUST_PILLAR does not exist in 1.20.1 -> the generic BLOCK particle (decision D3)
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.atired.creaturefeature.networking.CFNetwork;

public class KickedBlockEntity extends Projectile {
    public BlockState state = Blocks.STONE.defaultBlockState();
    private static final EntityDataAccessor<Boolean> CRITICAL= SynchedEntityData.defineId(KickedBlockEntity.class, EntityDataSerializers.BOOLEAN);
    
    public KickedBlockEntity(EntityType<? extends Projectile> entityType, Level level) {
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
        if(level() instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(CFParticleInit.CRIT_PARTICLE.get(),getX(),getY()+0.3,getZ(),1,0.1,0,0.1,0);
            serverLevel.sendParticles(CFParticleInit.CRIT_VER_PARTICLE.get(),getX(),getY(0.5),getZ(),10,0.2,0.2,0.2,0.1);

            Vec3 dir = getDeltaMovement().multiply(1,0,1).normalize().scale(0.6).add(0,0.5,0);
            result.getEntity().addDeltaMovement(dir);

            result.getEntity().hurt(damageSources().mobProjectile(this,(getOwner() instanceof LivingEntity living)?living:null),5.0f);
            if(result.getEntity() instanceof ServerPlayer player){
                VelSyncPayload payload = new VelSyncPayload(player.getId(),dir.x,dir.y,dir.z);
                CFNetwork.sendToPlayer(player,payload);
            }
            discard();
        }
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
        this.move(MoverType.SELF,getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.96));
        // PORT: getGravity() (1.20.5+) is 0 for a plain Projectile -> 0.0
        addDeltaMovement(new Vec3(0,0.0-0.08,0));
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        // PORT: EventHooks.onProjectileImpact/hitTargetOrDeflectSelf -> ForgeEventFactory.onProjectileImpact/onHit (1.20.1 has no deflection)
        if (hitresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if(level() instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(CFParticleInit.CRIT_PARTICLE.get(),getX(),getY()+0.3,getZ(),1,0.1,0,0.1,0);
            for (int i = 0; i < 32; i++) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,this.state),getX(),getY()+0.3,getZ(),1,0.2,0,0.2,1);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,this.state),getX(),getY()+0.3,getZ(),1,0.2,0,0.2,1);
            }

        }
        discard();
    }

    
}
