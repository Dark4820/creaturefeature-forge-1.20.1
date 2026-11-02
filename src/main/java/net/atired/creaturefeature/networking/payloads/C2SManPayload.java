package net.atired.creaturefeature.networking.payloads;

import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.atired.creaturefeature.init.CFAchievements;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// PORT: CustomPacketPayload + StreamCodec -> SimpleChannel message (VAR_INT). Server-side handler.
public record C2SManPayload(int playerID) {
    public static void encode(C2SManPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.playerID);
    }

    public static C2SManPayload decode(FriendlyByteBuf buf) {
        return new C2SManPayload(buf.readVarInt());
    }

    public static void handleData(C2SManPayload payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer sender = context.get().getSender();
            if(sender==null) return;
            Level test = sender.level();
            // PORT: the original accepted any player id; only the sender itself is affected now
            if(test!=null&&test.getEntity(payload.playerID)==sender){
                if(sender instanceof PlayerBrainrotAccessor accessor){
                    accessor.setManCan(true);
                }
                CFAchievements.NOBODY.trigger(sender);
            }
        });
        context.get().setPacketHandled(true);
    }
}
