package net.atired.creaturefeature.client;

import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.atired.creaturefeature.networking.payloads.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * PORT: client half of the clientbound payloads' handleData (the original ran it inline with context.player()).
 * Only reached through DistExecutor on the client, so a dedicated server never loads this class.
 * The accessors are implemented by LivingEntityGoopMixin/PlayerMixin (phase F6); until then those branches do nothing.
 */
public class CFClientPayloadHandler {
    private static Level level() {
        return Minecraft.getInstance().level;
    }

    public static void handleVelSync(VelSyncPayload payload) {
        Level test = level();
        if(test==null) return;
        Entity entity =test.getEntity(payload.playerID());
        // PORT: null check (the original would throw if the entity is not loaded on the client)
        if(entity!=null){
            entity.addDeltaMovement(new Vec3(payload.x(),payload.y(),payload.z()));
        }
    }

    public static void handleDeAmp(DeAmpPayload payload) {
        Level test = level();
        if(test==null) return;
        Entity entity =test.getEntity(payload.playerID());
        if(entity instanceof LivingEntityGoopAccessor accessor){
            accessor.setFallDamageAmped(payload.should());
        }
    }

    public static void handleRabies(RabiesPayload payload) {
        Level test = level();
        if(test==null) return;
        if(test.getEntity(payload.playerID()) instanceof PlayerBrainrotAccessor accessor){
            accessor.setRabies(1.0f);
        }
    }

    public static void handlePathogen(PathogenPayload payload) {
        Level test = level();
        if(test==null) return;
        if(test.getEntity(payload.playerID()) instanceof LivingEntityGoopAccessor accessor){
            accessor.setBacterial(payload.hpAgain());
        }
    }

    public static void handleSquashed(SquashedPayload payload) {
        Level test = level();
        if(test==null) return;
        if(test.getEntity(payload.playerID()) instanceof LivingEntityGoopAccessor accessor){
            accessor.setSquashed(0.8f);
        }
    }

    // As in the original, the damage goes to the local player (the payload's id is not used)
    public static void handleGasLeak(GasLeakPayload payload) {
        Player player = Minecraft.getInstance().player;
        if(player instanceof PlayerBrainrotAccessor accessor){
            accessor.setDelayedDamage(payload.damageAmp()+0.1f);
        }
    }
}
