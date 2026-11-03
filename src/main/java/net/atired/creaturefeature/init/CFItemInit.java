package net.atired.creaturefeature.init;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.items.*;
import net.atired.creaturefeature.items.NanDiscItem;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// PORT: DeferredRegister.Items/DeferredItem -> DeferredRegister/RegistryObject.
// Phase F1 registers the items without logic of their own. Still to come (see docs/PORT_MAP.md):
// eggs F3-F5, items with their own class F4-F6 (see docs/PORT_MAP.md). Music discs: F2.
public class CFItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreatureFeature.MODID);

    // PORT: SpawnEggItem(EntityType) -> ForgeSpawnEggItem(Supplier<EntityType>): in 1.20.1 items are registered
    // before entity types, so the egg can only take the type lazily. Forge also registers the egg colours.
    public static final RegistryObject<Item> MINDS_EGG = ITEMS.register("minds_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.MINDS,0xd17989,0x923a70,new  Item.Properties()));
    public static final RegistryObject<Item> SINISTER_EGG = ITEMS.register("sinister_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.SINISTER,0xffffff,0xf1e49f,new  Item.Properties()));
    public static final RegistryObject<Item> MACHINATION_EGG = ITEMS.register("machination_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.MACHINATION,0xf1e49f,0x6f5adb,new  Item.Properties()));
    public static final RegistryObject<Item> BEAUTY_EGG = ITEMS.register("beauty_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.BEAUTY,0x629062,0x91344d,new  Item.Properties()));
    public static final RegistryObject<Item> DETRITUS_EGG = ITEMS.register("detritus_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.DETRITUS,0xcb67ad,0x7d6a42,new  Item.Properties()));
    public static final RegistryObject<Item> FIEND_EGG = ITEMS.register("fiend_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.FIEND,0xf58dfc,0xdf52f2,new  Item.Properties()));
    public static final RegistryObject<Item> FRIEND_EGG = ITEMS.register("friend_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.FRIEND,0xffffff,0x000000,new  Item.Properties()));
    public static final RegistryObject<Item> FEND_EGG = ITEMS.register("fend_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.FEND,0xffe873,0xf2a574,new  Item.Properties()));
    public static final RegistryObject<Item> BLITZ_EGG = ITEMS.register("blitz_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.BLITZ,0xeec1db,0xd442dd,new  Item.Properties()));
    public static final RegistryObject<Item> EEPER_EGG = ITEMS.register("eeper_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.EEPER,0xd1e198,0x5d9b98,new  Item.Properties()));
    public static final RegistryObject<Item> NOTHING_EGG = ITEMS.register("nothing_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.NOTHING,0xdfce9b,0xdfce9b,new  Item.Properties()));
    public static final RegistryObject<Item> CANARY_EGG = ITEMS.register("canary_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.CANARY,0xaab5b3,0x99e550,new  Item.Properties()));
    public static final RegistryObject<Item> VERTIGO_EGG = ITEMS.register("vertigo_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.VERTIGO,0xa97eb6,0x736967,new  Item.Properties()));
    public static final RegistryObject<Item> COAT_OF_ARMS_EGG = ITEMS.register("coat_of_arms_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.COATOFARMS,0xdcafa7,0x975249,new  Item.Properties()));
    public static final RegistryObject<Item> MINEDFLAYER_EGG = ITEMS.register("minedflayer_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.MINEDFLAYER,0xe7b9d1,0x75a6cd,new  Item.Properties()));
    public static final RegistryObject<Item> DREAMWEAVER_EGG = ITEMS.register("dreamweaver_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.DREAMWEAVER,0x64775d,0x515657,new  Item.Properties()));
    public static final RegistryObject<Item> RUNAWAY_EGG = ITEMS.register("runaway_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.CANNONBALL_CRAB,0x8a9a71,0x507590,new  Item.Properties()));
    public static final RegistryObject<Item> MOCKINGBIRD_EGG = ITEMS.register("mockingbird_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.MOCKINGBIRD,0xefefc3,0x9ae1d7,new  Item.Properties()));
    public static final RegistryObject<Item> PATHOGEN_EGG = ITEMS.register("pathogen_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.PATHOGEN,0xd8e7c3,0x4ca150,new  Item.Properties()));
    public static final RegistryObject<Item> BLOSSOM_EGG = ITEMS.register("blossom_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.BLOSSOM,0x67e081,0x729e65,new  Item.Properties()));
    public static final RegistryObject<Item> SAINT_SOLIS_EGG = ITEMS.register("saint_solis_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.SAINT_SOLIS,0xeb9e5a,0xdb4b4b,new  Item.Properties()));
    public static final RegistryObject<Item> STAINED_GLASS_EGG = ITEMS.register("stained_glass_spawn_egg", ()->new
            ForgeSpawnEggItem(CFEntityInit.STAINED_GLASS,0x9f96b6,0x45467f,new  Item.Properties()));
    // PORT: the throwables of the F5 entities come with them (solar_shard from F6: it only throws stars)
    public static final RegistryObject<Item> VITRIC_ARROW = ITEMS.register(
            "vitric_arrow",
            ()->new VitricArrowItem(new Item.Properties())
    );
    public static final RegistryObject<Item> DOWN_FEATHER = ITEMS.register(
            "down_feather",
            ()->new DownFeatherItem(new Item.Properties())
    );
    public static final RegistryObject<Item> SOLAR_SHARD = ITEMS.register(
            "solar_shard",
            ()->new SolarShardItem(new Item.Properties())
    );
    public static final RegistryObject<Item> MURKY_PEARL = ITEMS.register(
            "murky_pearl",
            ()->new MurkyPearlItem(new Item.Properties())
    );
    // PORT: flintlock moved from F6 to F4: the fiend and the coat of arms hold it (renderers) and the fiend's shots
    // throw its item particles.
    public static final RegistryObject<Item> FLINTLOCK = ITEMS.register(
            "flintlock",
            // PORT: durability(4).stacksTo(1) -> durability(4): in 1.20.1 stacksTo after durability throws
            // "Unable to have damage AND stack", and durability already makes the stack size 1
            ()->new FlintlockItem(new Item.Properties().durability(4))
    );

    // PORT: jukebox_song data (1.21) -> RecordItem(comparator output, sound, properties, length in ticks), with the
    // values of data/creaturefeature/jukebox_song: nothing = 15 / 16.3 s, new_age_nevermore = 3 / 71 s.
    // The disc tooltip reads "<item id>.desc" in 1.20.1 (added to the lang files by tools/convert_resources.py).
    public static final RegistryObject<Item> NOTHING_DISC = ITEMS.register(
            "music_disc_nothing",
            ()->new RecordItem(15, CFSoundInit.NOTHING, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE), 326)
    );
    public static final RegistryObject<Item> NEW_AGE_NEVERMORE_DISC = ITEMS.register(
            "music_disc_nan",
            ()->new NanDiscItem(3, CFSoundInit.NEW_AGE_NEVERMORE, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE), 71*20)
    );
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


    // ---- F6: items with their own logic
    // PORT: durability(8).stacksTo(1) -> durability(8): stacksTo after durability throws in 1.20.1 (durability already makes it 1)
    public static final RegistryObject<Item> BOUQUET = ITEMS.register(
            "bouquet",
            ()->new BouquetItem(new Item.Properties().durability(8))
    );
    // PORT: saturationModifier/alwaysEdible -> saturationMod/alwaysEat; effect(Supplier, chance) is the same in Forge
    public static final FoodProperties FIEND_FOOD = (new FoodProperties.Builder()).nutrition(-1).saturationMod(0.0F).alwaysEat().fast()
            .effect(()->{return new MobEffectInstance(CFMobEffectInit.FIENDISH.get(),300,0);},1f).build();
    public static final RegistryObject<Item> FIENDISH_SODA = ITEMS.register(
            "fiendish_soda",
            ()->new CriticalSodaItem(new Item.Properties().food(FIEND_FOOD))
    );
    public static final RegistryObject<Item> LIVING_GLASS_SHARDS = ITEMS.register(
            "living_glass_shards",
            ()->new LivingGlassItem(new Item.Properties())
    );
    public static final RegistryObject<Item> BACTERIUM_BALL = ITEMS.register(
            "bacterium_ball",
            ()->new BacteriumBallItem(new Item.Properties())
    );
    // PORT: .component(DREAMCATCHER_CONTENTS, EMPTY) -> nothing: the NBT helper returns EMPTY when the tag is missing
    public static final RegistryObject<Item> DREAM_CATCHER = ITEMS.register(
            "dream_catcher",
            ()->new SpawnerCaptureItem(new Item.Properties().stacksTo(1))
    );
    public static final FoodProperties PILL_FOOD = (new FoodProperties.Builder()).nutrition(0).alwaysEat().saturationMod(0.01F)
            .effect(()->{return new MobEffectInstance(CFMobEffectInit.SLEEPY.get(),20,0);}, 1.0f).fast().build();
    public static final RegistryObject<Item> THE_PILL = ITEMS.register(
            "the_pill",
            ()->new ThePillItem(new Item.Properties().food(PILL_FOOD))
    );
    public static final RegistryObject<Item> OPEN_MIND = ITEMS.register(
            "open_mind",
            ()->new OpenMindItem(new Item.Properties().stacksTo(1))
    );
    public static final RegistryObject<Item> VERTIGO_HORN = ITEMS.register(
            "vertigo_horn",
            ()->new VertigoHornItem((new Item.Properties()).stacksTo(1), InstrumentTags.GOAT_HORNS)
    );
    // PORT: AxeItem.createAttributes(DIAMOND, 5, -3.6) in the properties -> the 1.20.1 constructor (DIAMOND, 5, -3.6)
    public static final RegistryObject<Item> HEAVY_AXE = ITEMS.register(
            "heavy_axe",
            ()->new HeavyAxeItem(Tiers.DIAMOND, 5.0F, -3.6F, (new Item.Properties()).rarity(Rarity.EPIC))
    );
}
