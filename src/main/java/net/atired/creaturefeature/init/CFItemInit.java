package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: DeferredRegister.Items/DeferredItem -> DeferredRegister/RegistryObject.
// Phase F1 registers the items without logic of their own. Still to come (see docs/PORT_MAP.md):
// spawn eggs (F3-F5), music discs (F2), the items with their own class (F6).
public class CFItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreatureFeature.MODID);

    // PORT: saturationModifier -> saturationMod. INFESTED and OOZING do not exist in 1.20.1: removed (decision D3).
    public static final FoodProperties BLIGHTED_BRAIN_FOOD = (new FoodProperties.Builder()).nutrition(11).saturationMod(0.3F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0), 0.8F).build();

    public static final RegistryObject<Item> BLIGHTED_BRAIN = ITEMS.register(
            "blighted_brain",
            ()->new Item(new Item.Properties().food(BLIGHTED_BRAIN_FOOD))
    );
    public static final FoodProperties VERTIGO_FOOD = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.3F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 100, 0), 0.4F).build();

    public static final RegistryObject<Item> VERTIGO_CHUNK = ITEMS.register(
            "vertigo_chunk",
            ()->new Item(new Item.Properties().food(VERTIGO_FOOD))
    );
    public static final RegistryObject<Item> SLEEPING_POWDER = ITEMS.register(
            "sleeping_powder",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> COAT_SCRAPS = ITEMS.register(
            "coat_scraps",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> FIENDISH_ESSENCE = ITEMS.register(
            "fiendish_essence",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> CARAPACE = ITEMS.register(
            "carapace",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> THINGAMABOB = ITEMS.register(
            "thingamabob",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> BLITZ_ROD = ITEMS.register(
            "blitz_rod",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> DREAM_SILK = ITEMS.register(
            "dream_silk",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> ECTOPLASM = ITEMS.register(
            "ectoplasm",
            ()->new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> PAN_SCROLL = ITEMS.register(
            "scroll_pan",
            ()->new Item(new Item.Properties().stacksTo(8))
    );
    public static final RegistryObject<Item> TRANS_SCROLL = ITEMS.register(
            "scroll_trans",
            ()->new Item(new Item.Properties().stacksTo(8))
    );
    public static final RegistryObject<Item> PRIDE_SCROLL = ITEMS.register(
            "scroll_pride",
            ()->new Item(new Item.Properties().stacksTo(8))
    );
    public static final RegistryObject<Item> BI_SCROLL = ITEMS.register(
            "scroll_bi",
            ()->new Item(new Item.Properties().stacksTo(8))
    );
    public static final RegistryObject<Item> MINEDFLAYER_GOOP = ITEMS.register(
            "minedflayer_goop",
            ()->new Item(new Item.Properties())
    );

}
