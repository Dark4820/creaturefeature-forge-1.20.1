package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.client.CFClientPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (encode/decode/handleData). Same fields and
// wire types (VAR_INT, DOUBLE x3). The client-side work lives in client.CFClientPayloadHandler.
public record VelSyncPayload( int playerID,double x,double y,double z) {
    public static void encode(VelSyncPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
        buf.writeDouble(payload.x);
        buf.writeDouble(payload.y);
        buf.writeDouble(payload.z);
    }

    public static VelSyncPayload decode(FriendlyByteBuf buf) {
        return new VelSyncPayload(buf.readVarInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handleData(VelSyncPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientPayloadHandler.handleVelSync(payload)));
        context.get().setPacketHandled(true);
    }
}
