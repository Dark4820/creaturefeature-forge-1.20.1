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
}
