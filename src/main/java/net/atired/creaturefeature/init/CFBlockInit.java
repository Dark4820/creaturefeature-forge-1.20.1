package net.atired.creaturefeature.init;


import java.util.function.Supplier;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.blocks.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: DeferredRegister.Blocks/DeferredBlock -> DeferredRegister/RegistryObject; Properties.ofFullCopy -> copy.
// Phase F1 registers the plain blocks. Still to come: EEP (F5, needs PrimedEepEntity) and DOOHICKEY (F6, block entity).
// OBELISK (★) waits for F9. RUNIC_STONE_BRICKS is removed (Sable, decision D2).
public class CFBlockInit {
    public static final DeferredRegister<Block>
            BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreatureFeature.MODID);
    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return CFItemInit.ITEMS.register(name, () -> new BlockItem(block.get(),new Item.Properties()));
    }
   public static final RegistryObject<Block> MINEDFLAYER_JELLY = registerBlock("minedflayer_jelly",
            () -> new MinedFlayerJellyBlock(BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).friction(1.04F).sound(SoundType.FUNGUS)));

    // PORT: ColorRGBA does not exist in 1.20.1: the dust color is a plain ARGB int
    public static final RegistryObject<Block> CARAPACE_BLOCK = registerBlock("carapace_block",
            () -> new CarapaceBlock(0xFF7b775f,BlockBehaviour.Properties.copy(Blocks.PACKED_MUD)));
    public static final RegistryObject<Block> EEP = registerBlock("eep",
            () -> new EepBlock(BlockBehaviour.Properties.copy(Blocks.TNT)));
    public static final RegistryObject<Block> CARAPACE_BRICKS = registerBlock("carapace_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    // PORT: Forge's StairBlock(Supplier<BlockState>, Properties) because CARAPACE_BRICKS is not built yet here
    public static final RegistryObject<Block> CARAPACE_BRICK_STAIRS = registerBlock("carapace_brick_stairs",
            () -> new StairBlock(() -> CARAPACE_BRICKS.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> DOWN_FEATHERS = registerBlock("down_feathers",
            () -> new FeathersBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> DOWN_FEATHERS_CARPET = registerBlock("down_feathers_carpet",
            () -> new FeathersCarpetBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> MOSAIC_DOWN_FEATHERS = registerBlock("mosaic_down_feathers",
            () -> new FeathersBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> MOSAIC_DOWN_FEATHERS_CARPET = registerBlock("mosaic_down_feathers_carpet",
            () -> new FeathersCarpetBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> FIENDISH_TILES = registerBlock("fiendish_tiles",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> BLIND_FIENDISH_TILES = registerBlock("blind_fiendish_tiles",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> MURKY_PEARL_TILES = registerBlock("murky_pearl_tiles",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE)));
    public static final RegistryObject<Block> MURKY_PEARL_TILE_STAIRS = registerBlock("murky_pearl_tile_stairs",
            () -> new StairBlock(() -> MURKY_PEARL_TILES.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE)));
    public static final RegistryObject<Block> MURKY_PEARL_BLOCK = registerBlock("murky_pearl_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE)));
    public static final RegistryObject<Block> DREAM_SILK_SPOOL = registerBlock("dream_silk_spool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> WALLPAPER = registerBlock("wallpaper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(1.0F, 3.0F)));
    public static final RegistryObject<Block> SOLAR_BLOCK = registerBlock("solar_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT)));
    public static final RegistryObject<Block> SOLAR_BRICKS = registerBlock("solar_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT)));

}
