package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.client.CFClientPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT); client work in CFClientPayloadHandler.
public record RabiesPayload(int playerID) {
    public static void encode(RabiesPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
    }

    public static RabiesPayload decode(FriendlyByteBuf buf) {
        return new RabiesPayload(buf.readVarInt());
    }

    public static void handleData(RabiesPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientPayloadHandler.handleRabies(payload)));
        context.get().setPacketHandled(true);
    }
}
