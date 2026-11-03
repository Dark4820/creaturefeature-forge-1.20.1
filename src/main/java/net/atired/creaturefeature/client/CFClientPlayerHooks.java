package net.atired.creaturefeature.client;

import net.atired.creaturefeature.networking.CFNetwork;
import net.atired.creaturefeature.networking.payloads.C2SManPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * PORT: client half of the original PlayerMixin.tick (the "man" easter egg), which reads and writes
 * CreatureFeatureClient.PROXY. PlayerMixin is common code, so it calls this through DistExecutor on the client.
 * The counters were fields of the mixin (one set per player); here they are kept per player in a weak map.
 * Same conditions, sounds and numbers as the original.
 */
public final class CFClientPlayerHooks {
    private static final class ManState {
        int counter = 0;
        int aged = 0;
        double oldX = 0;
        double oldZ = 0;
    }

    private static final Map<Player, ManState> STATES = new WeakHashMap<>();

    private CFClientPlayerHooks() {
    }

    public static void tickManSearch(Player player) {
        ManState st = STATES.computeIfAbsent(player, p -> new ManState());
        st.aged +=1;
        if(player.getY()>127.8
                &&player.level()!=null
                &&(player.getYRot()<-89&&player.getYRot()>-91)
                &&player.level().isClientSide()
                &&CreatureFeatureClient.PROXY.searchingForHim
                &&player.level().dimensionType().respawnAnchorWorks()
                &&st.aged%50==(48-st.counter *2)){
            if(Math.abs(st.oldX -player.getX())>0.02&&Math.abs(st.oldZ -player.getZ())<0.02){
                st.counter +=1;
                st.aged =0;
                player.playSound(SoundEvents.BELL_RESONATE, st.counter /5f, st.counter /10f);
                player.playSound(SoundEvents.ARROW_HIT_PLAYER, st.counter /5f, st.counter /10f);
                CreatureFeatureClient.PROXY.manShader= st.counter /24f;
                if(st.counter >23){
                    player.playSound(SoundEvents.WARDEN_SONIC_BOOM, st.counter /5f, st.counter /10f);
                    st.counter =0;
                    CreatureFeatureClient.PROXY.searchingForHim=false;
                    CreatureFeatureClient.PROXY.manShader=0;
                    C2SManPayload payload = new C2SManPayload(player.getId());
                    // PORT: PacketDistributor.sendToServer -> CFNetwork (SimpleChannel)
                    CFNetwork.sendToServer(payload);
                    //CFAchievements.NOBODY.get().trigger();
                }
            }
        }
        else if(player.getY()<127.8||!(player.getYRot()<-89&&player.getYRot()>-91)||player.level()==null||!player.level().dimensionType().respawnAnchorWorks()){
            st.counter =0;
        }
        st.oldX =player.getX();
        st.oldZ =player.getZ();
    }
}
