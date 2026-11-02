package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.client.CFClientPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT, FLOAT); client work in CFClientPayloadHandler.
public record GasLeakPayload(int playerID,float damageAmp) {
    public static void encode(GasLeakPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
        buf.writeFloat(payload.damageAmp);
    }

    public static GasLeakPayload decode(FriendlyByteBuf buf) {
        return new GasLeakPayload(buf.readVarInt(), buf.readFloat());
    }

    public static void handleData(GasLeakPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientPayloadHandler.handleGasLeak(payload)));
        context.get().setPacketHandled(true);
    }
}
