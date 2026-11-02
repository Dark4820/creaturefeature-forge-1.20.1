package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.client.CFClientPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT, FLOAT); client work in CFClientPayloadHandler.
public record PathogenPayload(int playerID,float hpAgain) {
    public static void encode(PathogenPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
        buf.writeFloat(payload.hpAgain);
    }

    public static PathogenPayload decode(FriendlyByteBuf buf) {
        return new PathogenPayload(buf.readVarInt(), buf.readFloat());
    }

    public static void handleData(PathogenPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientPayloadHandler.handlePathogen(payload)));
        context.get().setPacketHandled(true);
    }
}
