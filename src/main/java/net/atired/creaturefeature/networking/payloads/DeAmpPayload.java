package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.client.CFClientPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT, BOOL); client work in CFClientPayloadHandler.
public record DeAmpPayload(int playerID,boolean should) {
    public static void encode(DeAmpPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
        buf.writeBoolean(payload.should);
    }

    public static DeAmpPayload decode(FriendlyByteBuf buf) {
        return new DeAmpPayload(buf.readVarInt(), buf.readBoolean());
    }

    public static void handleData(DeAmpPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientPayloadHandler.handleDeAmp(payload)));
        context.get().setPacketHandled(true);
    }
}
