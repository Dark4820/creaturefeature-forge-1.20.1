package net.atired.creaturefeature.init;

import com.mojang.serialization.Codec;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.misc.AddTableLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: port-only (see AddTableLootModifier): Forge 1.20.1 has no "add_table" loot modifier serializer
public class CFLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CreatureFeature.MODID);

    public static final RegistryObject<Codec<AddTableLootModifier>> ADD_TABLE =
            LOOT_MODIFIER_SERIALIZERS.register("add_table", AddTableLootModifier.CODEC);
}
