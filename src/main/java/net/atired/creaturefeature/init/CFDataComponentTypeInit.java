package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.items.DreamCatcherContents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

/**
 * PORT: data components (1.20.5+) do not exist in 1.20.1. The two components of the original are stored in the
 * item's NBT under the same ids ("creaturefeature:opened_mind", "creaturefeature:dreamcatcher_contents").
 * NBT is synced to clients with the stack, like the original's networkSynchronized components.
 * Usage: stack.getOrDefault(OPENED, 0) -> getOpened(stack); stack.set(OPENED, v) -> setOpened(stack, v);
 * same for DREAMCATCHER_CONTENTS. A missing value reads as the original's default (0 / EMPTY).
 */
public class CFDataComponentTypeInit {
    public static final String OPENED = CreatureFeature.getId("opened_mind").toString();
    public static final String DREAMCATCHER_CONTENTS = CreatureFeature.getId("dreamcatcher_contents").toString();

    public static int getOpened(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(OPENED, Tag.TAG_INT) ? tag.getInt(OPENED) : 0;
    }

    public static void setOpened(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(OPENED, value);
    }

    public static DreamCatcherContents getDreamcatcherContents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(DREAMCATCHER_CONTENTS, Tag.TAG_LIST)) {
            return DreamCatcherContents.EMPTY;
        }
        return DreamCatcherContents.load(tag.getList(DREAMCATCHER_CONTENTS, Tag.TAG_COMPOUND));
    }

    public static void setDreamcatcherContents(ItemStack stack, DreamCatcherContents contents) {
        stack.getOrCreateTag().put(DREAMCATCHER_CONTENTS, contents.save());
    }
}
