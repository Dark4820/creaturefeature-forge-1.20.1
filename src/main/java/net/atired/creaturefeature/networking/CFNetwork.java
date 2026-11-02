package net.atired.creaturefeature.networking;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.networking.payloads.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * PORT: NeoForge's RegisterPayloadHandlersEvent/PayloadRegistrar (CFEvents.register in the original) ->
 * a Forge SimpleChannel. Same protocol version ("1") and the same payloads; ToadstoolRenderPayload is removed
 * (Sable, decision D2). The send helpers mirror NeoForge's PacketDistributor methods used by the original.
 */
public class CFNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            CreatureFeature.getId("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, VelSyncPayload.class, VelSyncPayload::encode, VelSyncPayload::decode, VelSyncPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, DeAmpPayload.class, DeAmpPayload::encode, DeAmpPayload::decode, DeAmpPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, C2SVelSyncPayload.class, C2SVelSyncPayload::encode, C2SVelSyncPayload::decode, C2SVelSyncPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, C2SManPayload.class, C2SManPayload::encode, C2SManPayload::decode, C2SManPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, RabiesPayload.class, RabiesPayload::encode, RabiesPayload::decode, RabiesPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, PathogenPayload.class, PathogenPayload::encode, PathogenPayload::decode, PathogenPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, SquashedPayload.class, SquashedPayload::encode, SquashedPayload::decode, SquashedPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, GasLeakPayload.class, GasLeakPayload::encode, GasLeakPayload::decode, GasLeakPayload::handleData, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    // PORT: PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload)
    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, Object... payloads) {
        for (Object payload : payloads) {
            CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(chunkPos.x, chunkPos.z)), payload);
        }
    }

    // PORT: PacketDistributor.sendToPlayersTrackingEntity(entity, payloads...) (the entity itself is not included)
    public static void sendToPlayersTrackingEntity(Entity entity, Object... payloads) {
        for (Object payload : payloads) {
            CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), payload);
        }
    }

    // PORT: PacketDistributor.sendToPlayer(player, payloads...)
    public static void sendToPlayer(net.minecraft.server.level.ServerPlayer player, Object... payloads) {
        for (Object payload : payloads) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }
    }

    // PORT: PacketDistributor.sendToServer(payload)
    public static void sendToServer(Object payload) {
        CHANNEL.sendToServer(payload);
    }
}
