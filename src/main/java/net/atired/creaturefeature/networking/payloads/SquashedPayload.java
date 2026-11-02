package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.client.CFClientPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT); client work in CFClientPayloadHandler.
public record SquashedPayload(int playerID) {
    public static void encode(SquashedPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
    }

    public static SquashedPayload decode(FriendlyByteBuf buf) {
        return new SquashedPayload(buf.readVarInt());
    }

    public static void handleData(SquashedPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientPayloadHandler.handleSquashed(payload)));
        context.get().setPacketHandled(true);
    }
}
