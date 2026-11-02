package net.atired.creaturefeature;

import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// PORT: NeoForge's @EventBusSubscriber picks the bus per event; Forge 1.20.1 needs one subscriber per bus.
// Game-bus handlers stay in CFEvents (they arrive with their content), mod-bus handlers go in CFEvents.ModBus.
@Mod.EventBusSubscriber(modid = CreatureFeature.MODID)
public class CFEvents {

    @Mod.EventBusSubscriber(modid = CreatureFeature.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        // PORT: insertAfter/insertBefore -> getEntries().putAfter/putBefore (see insertAfter/insertBefore below).
        // Only the entries of the content already ported are here; the rest come back phase by phase.
        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            if(event.getTabKey()==CreativeModeTabs.INGREDIENTS){
                insertBefore(event,Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,CFItemInit.BI_SCROLL.get());
                insertBefore(event,Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,CFItemInit.PRIDE_SCROLL.get());
                insertBefore(event,Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,CFItemInit.TRANS_SCROLL.get());
                insertBefore(event,Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,CFItemInit.PAN_SCROLL.get());


                insertBefore(event,Items.SLIME_BALL,CFItemInit.MINEDFLAYER_GOOP.get());
                // PORT: BREEZE_ROD does not exist in 1.20.1 -> after BLAZE_ROD
                insertAfter(event,Items.BLAZE_ROD,CFItemInit.BLITZ_ROD.get());
                insertBefore(event,Items.NAUTILUS_SHELL,CFItemInit.CARAPACE.get());
                insertAfter(event,Items.POPPED_CHORUS_FRUIT,CFItemInit.DREAM_SILK.get());
                insertAfter(event,Items.HONEYCOMB,CFItemInit.ECTOPLASM.get());
                insertBefore(event,Items.GHAST_TEAR,CFItemInit.THINGAMABOB.get());
                insertBefore(event,Items.GHAST_TEAR,CFItemInit.FIENDISH_ESSENCE.get());
                insertBefore(event,Items.LEATHER,CFItemInit.COAT_SCRAPS.get());
                insertBefore(event,Items.GUNPOWDER,CFItemInit.SLEEPING_POWDER.get());
            }
            if(event.getTabKey()==CreativeModeTabs.BUILDING_BLOCKS) {
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.MURKY_PEARL_BLOCK.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.MURKY_PEARL_TILES.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.MURKY_PEARL_TILE_STAIRS.get());

                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.DOWN_FEATHERS.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.DOWN_FEATHERS_CARPET.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.MOSAIC_DOWN_FEATHERS.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.MOSAIC_DOWN_FEATHERS_CARPET.get());

                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.FIENDISH_TILES.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.BLIND_FIENDISH_TILES.get());

                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.CARAPACE_BLOCK.get());
                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.CARAPACE_BRICKS.get());

                insertBefore(event,Items.GOLD_BLOCK, CFBlockInit.CARAPACE_BRICK_STAIRS.get());
                insertBefore(event,Items.PURPUR_BLOCK, CFBlockInit.DREAM_SILK_SPOOL.get());
                insertBefore(event,Items.PURPUR_BLOCK, CFBlockInit.SOLAR_BLOCK.get());
                insertBefore(event,Items.PURPUR_BLOCK, CFBlockInit.SOLAR_BRICKS.get());
                insertBefore(event,Items.PACKED_MUD, CFBlockInit.WALLPAPER.get());

            }
            if(event.getTabKey()==CreativeModeTabs.FUNCTIONAL_BLOCKS){
                insertAfter(event,Items.SOUL_CAMPFIRE, CFBlockInit.CARAPACE_BLOCK.get());
                insertBefore(event,Items.LODESTONE, CFBlockInit.MINEDFLAYER_JELLY.get());
            }
            if(event.getTabKey()==CreativeModeTabs.FOOD_AND_DRINKS){
                insertBefore(event,Items.SPIDER_EYE, CFItemInit.VERTIGO_CHUNK.get());
                insertBefore(event,Items.ROTTEN_FLESH, CFItemInit.BLIGHTED_BRAIN.get());
            }
        }

        // PORT: Forge's putBefore/putAfter need the anchor to be in the tab; if it is missing (another mod
        // removed it), the item is added at the end instead of failing.
        private static void insertBefore(BuildCreativeModeTabContentsEvent event, ItemLike anchor, ItemLike item) {
            ItemStack anchorStack = new ItemStack(anchor);
            if(event.getEntries().contains(anchorStack)){
                event.getEntries().putBefore(anchorStack, new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }else{
                event.accept(item);
            }
        }
        private static void insertAfter(BuildCreativeModeTabContentsEvent event, ItemLike anchor, ItemLike item) {
            ItemStack anchorStack = new ItemStack(anchor);
            if(event.getEntries().contains(anchorStack)){
                event.getEntries().putAfter(anchorStack, new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }else{
                event.accept(item);
            }
        }
    }
}
