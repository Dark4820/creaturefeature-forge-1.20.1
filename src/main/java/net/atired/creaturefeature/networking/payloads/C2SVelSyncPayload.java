package net.atired.creaturefeature.networking.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT, DOUBLE x3). Server-side handler.
public record C2SVelSyncPayload(int playerID, double x, double y, double z) {
    // PORT: the only sender (vertigo horn dash) sends a horizontal push of length 1.66. The server now ignores
    // pushes that could not come from it (longer than this, or not finite), so a modified client cannot fly.
    public static final double MAX_PUSH = 1.7;

    public static void encode(C2SVelSyncPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
        buf.writeDouble(payload.x);
        buf.writeDouble(payload.y);
        buf.writeDouble(payload.z);
    }

    public static C2SVelSyncPayload decode(FriendlyByteBuf buf) {
        return new C2SVelSyncPayload(buf.readVarInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static boolean isValidPush(double x, double y, double z) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)
                && new Vec3(x, y, z).length() <= MAX_PUSH;
    }

    public static void handleData(C2SVelSyncPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if(player==null) return;
            Level test = player.level();
            if(test.getEntity(payload.playerID)==player&&isValidPush(payload.x,payload.y,payload.z)){
                player.addDeltaMovement(new Vec3(payload.x,payload.y,payload.z));
            }
        });
        context.get().setPacketHandled(true);
    }
}
