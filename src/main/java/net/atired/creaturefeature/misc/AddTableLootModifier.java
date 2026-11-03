package net.atired.creaturefeature.misc;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

/**
 * PORT: port-only. NeoForge's built-in "neoforge:add_table" loot modifier does not exist in Forge 1.20.1; this is the
 * same thing ("creaturefeature:add_table": rolls the given table with the same context and adds its items). Used by
 * the Oreganized / Caverns and Chasms compat modifiers, which keep the original's files and conditions.
 */
public class AddTableLootModifier extends LootModifier {
    public static final Supplier<Codec<AddTableLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst)
                    .and(ResourceLocation.CODEC.fieldOf("table").forGetter(m -> m.table))
                    .apply(inst, AddTableLootModifier::new)));

    private final ResourceLocation table;

    public AddTableLootModifier(LootItemCondition[] conditions, ResourceLocation table) {
        super(conditions);
        this.table = table;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootTable extra = context.getResolver().getLootTable(this.table);
        // getRandomItemsRaw (not getRandomItems) so the global modifiers are not applied a second time
        extra.getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
