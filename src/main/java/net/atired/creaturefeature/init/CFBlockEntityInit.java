package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.blocks.blockentities.DoohickeyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: NeoForge DeferredRegister/Supplier -> Forge DeferredRegister/RegistryObject (same id)
public class CFBlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreatureFeature.MODID);

    public static final RegistryObject<BlockEntityType<DoohickeyBlockEntity>> DOOHICKEY = BLOCK_ENTITY_TYPES.register(
            "doohickey",
            () -> BlockEntityType.Builder.of(DoohickeyBlockEntity::new, CFBlockInit.DOOHICKEY.get()).build(null)
    );
}
