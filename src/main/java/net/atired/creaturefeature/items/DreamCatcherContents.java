package net.atired.creaturefeature.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

// PORT: the Codec/StreamCodec of the 1.21 data component -> NBT (save/load), stored in the item's tag by
// CFDataComponentTypeInit. ItemStack.listMatches/hashStackList (1.20.5+) -> the same comparison done by hand.
public class DreamCatcherContents  implements TooltipComponent  {
    public static final DreamCatcherContents EMPTY = new DreamCatcherContents(List.of());
    final List<ItemStack> items;

    public DreamCatcherContents(List<ItemStack> items) {
        this.items = items;
    }
    public Iterable<ItemStack> items() {
        return this.items;
    }

    public ListTag save() {
        ListTag list = new ListTag();
        for (ItemStack stack : this.items) {
            list.add(stack.save(new CompoundTag()));
        }
        return list;
    }

    public static DreamCatcherContents load(ListTag list) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.of(list.getCompound(i));
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return items.isEmpty() ? EMPTY : new DreamCatcherContents(List.copyOf(items));
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){return true;}
        else{
            if(obj instanceof DreamCatcherContents contents && listMatches(this.items, contents.items)){
                return true;
            }else{return false;}
        }
    }
    public int hashCode() {
        int hash = 0;
        for (ItemStack stack : this.items) {
            hash = hash * 31 + (stack.isEmpty() ? 0 : 31 * stack.getItem().hashCode() + stack.getCount());
        }
        return hash;
    }

    private static boolean listMatches(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!ItemStack.matches(a.get(i), b.get(i))) return false;
        }
        return true;
    }
}
