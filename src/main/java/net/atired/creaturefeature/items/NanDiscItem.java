package net.atired.creaturefeature.items;

import net.atired.creaturefeature.client.CFClientItemHooks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

/**
 * PORT: 1.21 discs are plain items with a jukebox_song component, and the "New Age Nevermore" title was started
 * by JukeBoxPlayableMixin (JukeboxPlayable.tryInsertIntoJukebox) on the client. In 1.20.1 the disc is a RecordItem
 * whose useOn inserts it, so the mixin is not needed: when the insertion succeeds on the client, the title starts
 * here. The client part goes through DistExecutor (the original touched CreatureFeatureClient.PROXY directly).
 * The title itself is drawn by CFGuiMixin (phase F7).
 */
public class NanDiscItem extends RecordItem {
    public NanDiscItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Properties properties, int lengthInTicks) {
        super(comparatorValue, soundSupplier, properties, lengthInTicks);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = super.useOn(context);
        if(context.getLevel().isClientSide()&&result.consumesAction()&&context.getPlayer()!=null){
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CFClientItemHooks.startNanTitle(context.getPlayer()));
        }
        return result;
    }
}
