package net.atired.creaturefeature.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * PORT: port-only. Client-side bits that the original ran from common item code (CreatureFeatureClient.PROXY
 * from items and mixins). Common code reaches it only through DistExecutor, so a dedicated server never loads it.
 */
public class CFClientItemHooks {
    // From JukeBoxPlayableMixin: start the "New Age Nevermore" title and clear the vanilla "Now playing" text
    public static void startNanTitle(Player player) {
        CreatureFeatureClient.PROXY.nan_title=1.0f;
        player.displayClientMessage(Component.empty(),true);
    }

    // From VertigoHornItem.use: the local player's dash along its movement input, mirrored to the server
    public static void vertigoHornDash(Player player) {
        if(player instanceof net.minecraft.client.player.LocalPlayer abstractClientPlayer){
            net.minecraft.world.phys.Vec3 dir = new net.minecraft.world.phys.Vec3(abstractClientPlayer.input.leftImpulse,0,abstractClientPlayer.input.forwardImpulse).normalize().scale(1.66).yRot(-abstractClientPlayer.getYHeadRot()/180.0f*3.14f);
            abstractClientPlayer.addDeltaMovement(dir);
            net.atired.creaturefeature.networking.payloads.C2SVelSyncPayload payload = new net.atired.creaturefeature.networking.payloads.C2SVelSyncPayload(abstractClientPlayer.getId(),dir.x,dir.y,dir.z);
            net.atired.creaturefeature.networking.CFNetwork.sendToServer(payload);
        }
    }

    // From OpenMindItem.use: client sounds and the item wobble (drawn by the F7 hotbar mixin)
    public static void openMindUsed(Player player, net.minecraft.world.item.ItemStack itemstack) {
        player.playSound(net.minecraft.sounds.SoundEvents.SLIME_SQUISH,1.0f,0.9f);
        player.playSound(net.minecraft.sounds.SoundEvents.SLIME_SQUISH,1.5f,0.7f);
        player.playSound(net.minecraft.sounds.SoundEvents.PLAYER_BURP,0.6f,0.9f);
        CreatureFeatureClient.PROXY.wobble=1.0f;
        CreatureFeatureClient.PROXY.wobblyItem=itemstack;
    }
}
