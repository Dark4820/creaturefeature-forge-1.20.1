package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFEntityInit;
import net.atired.creaturefeature.init.CFLootModifiers;
import net.atired.creaturefeature.misc.AddTableLootModifier;
import net.atired.creaturefeature.misc.DummyTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * F8: natural spawns (biome modifiers), the compat loot modifiers and the advancements.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F8GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    private static List<MobSpawnSettings.SpawnerData> monsters(MinecraftServer server, ResourceKey<Biome> biome) {
        Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
        // Forge patches getMobSettings to return the settings after the biome modifiers
        return biomes.getOrThrow(biome).getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
    }

    private static MobSpawnSettings.SpawnerData find(List<MobSpawnSettings.SpawnerData> list, EntityType<?> type, int weight) {
        return list.stream().filter(d -> d.type == type && d.getWeight().asInt() == weight).findFirst().orElse(null);
    }

    // Each kind of biome mapping (direct biome, vanilla tag, forge tag, list for a 1.21 "c:" tag, remove_spawns)
    // with the original's weight and group size
    @GameTest(template = TEMPLATE)
    public void f8BiomeModifiersAddTheOriginalSpawns(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        record Expect(ResourceKey<Biome> biome, EntityType<?> type, int weight, int min, int max) {}
        List<Expect> expected = List.of(
                new Expect(Biomes.LUSH_CAVES, CFEntityInit.BEAUTY.get(), 140, 1, 2),                 // biome
                new Expect(Biomes.SWAMP, CFEntityInit.COATOFARMS.get(), 15, 1, 2),                  // biome list
                new Expect(Biomes.SAVANNA, CFEntityInit.MINDS.get(), 40, 2, 4),                      // #minecraft:is_savanna
                new Expect(Biomes.PLAINS, CFEntityInit.SINISTER.get(), 20, 1, 1),                    // #minecraft:is_overworld
                new Expect(Biomes.CRIMSON_FOREST, CFEntityInit.FRIEND.get(), 3, 1, 1),               // #minecraft:is_nether
                new Expect(Biomes.JAGGED_PEAKS, CFEntityInit.MOCKINGBIRD.get(), 22, 1, 2),           // #forge:is_mountain
                new Expect(Biomes.DESERT, CFEntityInit.STAINED_GLASS.get(), 14, 1, 1),               // #forge:is_desert
                new Expect(Biomes.END_HIGHLANDS, CFEntityInit.DREAMWEAVER.get(), 4, 2, 3),           // c:is_outer_end_island
                new Expect(Biomes.SMALL_END_ISLANDS, CFEntityInit.MINEDFLAYER.get(), 1, 4, 5),
                new Expect(Biomes.CHERRY_GROVE, CFEntityInit.FEND.get(), 6, 1, 2),                   // c:is_floral
                new Expect(Biomes.SUNFLOWER_PLAINS, CFEntityInit.FEND.get(), 16, 3, 3),
                new Expect(Biomes.BEACH, CFEntityInit.CANNONBALL_CRAB.get(), 25, 1, 1),
                new Expect(Biomes.DARK_FOREST, CFEntityInit.VERTIGO.get(), 18, 1, 2));
        for (Expect e : expected) {
            MobSpawnSettings.SpawnerData data = find(monsters(server, e.biome()), e.type(), e.weight());
            helper.assertTrue(data != null, e.type().getDescriptionId() + " (weight " + e.weight() + ") should spawn in " + e.biome().location());
            helper.assertTrue(data.minCount == e.min() && data.maxCount == e.max(),
                    e.type().getDescriptionId() + " group in " + e.biome().location() + " should be " + e.min() + "-" + e.max());
        }
        // the main end island (not an outer island) gets none of the end creatures
        helper.assertTrue(find(monsters(server, Biomes.THE_END), CFEntityInit.DREAMWEAVER.get(), 4) == null, "no dreamweaver in the_end");
        // remove_spawns: mushroom fields and the deep dark keep no canary, detritus or sinister (c:no_default_monsters)
        for (ResourceKey<Biome> biome : List.of(Biomes.MUSHROOM_FIELDS, Biomes.DEEP_DARK)) {
            List<MobSpawnSettings.SpawnerData> mobs = monsters(server, biome);
            for (EntityType<?> type : List.of(CFEntityInit.CANARY.get(), CFEntityInit.DETRITUS.get(), CFEntityInit.SINISTER.get())) {
                helper.assertTrue(mobs.stream().noneMatch(d -> d.type == type), type.getDescriptionId() + " should not spawn in " + biome.location());
            }
        }
        helper.succeed();
    }

    // All 26 biome modifiers of the original (toadstool excluded) are in the registry
    @GameTest(template = TEMPLATE)
    public void f8AllBiomeModifiersLoad(GameTestHelper helper) {
        var registry = helper.getLevel().getServer().registryAccess().registryOrThrow(ForgeRegistries.Keys.BIOME_MODIFIERS);
        long ours = registry.keySet().stream().filter(k -> k.getNamespace().equals(CreatureFeature.MODID)).count();
        helper.assertTrue(ours == 26, "expected 26 creaturefeature biome modifiers, found " + ours);
        helper.succeed();
    }

    // The 9 advancements load, keep their tree and use the mod's triggers (they parse through DummyTrigger)
    @GameTest(template = TEMPLATE)
    public void f8AdvancementsLoadWithTheirTriggers(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        String[][] tree = {{"root", null}, {"sleepy", "root"}, {"pill", "sleepy"}, {"horse", "sleepy"}, {"friendless", "root"},
                {"sinister", "root"}, {"gas", "sinister"}, {"renovation", "root"}, {"roots_man", "root"}};
        for (String[] node : tree) {
            Advancement advancement = server.getAdvancements().getAdvancement(CreatureFeature.getId(node[0]));
            helper.assertTrue(advancement != null, "advancement " + node[0] + " missing");
            ResourceLocation parent = advancement.getParent() == null ? null : advancement.getParent().getId();
            helper.assertTrue(node[1] == null ? parent == null : CreatureFeature.getId(node[1]).equals(parent),
                    node[0] + " should hang from " + node[1] + " (is " + parent + ")");
            if (!node[0].equals("root")) {
                for (Criterion criterion : advancement.getCriteria().values()) {
                    helper.assertTrue(criterion.getTrigger() instanceof DummyTrigger.TriggerInstance,
                            node[0] + " should use a creaturefeature trigger");
                }
            }
        }
        helper.assertTrue(server.getAdvancements().getAdvancement(CreatureFeature.getId("roots_man")).getRequirements()[0][0]
                .equals("man_test_criteria_name"), "roots_man keeps the criterion name the advancement tab looks for");
        helper.succeed();
    }

    // creaturefeature:add_table rolls the extra table and adds its items (one_egg: the roots_man reward)
    @GameTest(template = TEMPLATE)
    public void f8AddTableLootModifierAddsTheTable(GameTestHelper helper) {
        helper.assertTrue(CFLootModifiers.ADD_TABLE.isPresent(), "add_table serializer registered");
        AddTableLootModifier modifier = new AddTableLootModifier(new LootItemCondition[0], CreatureFeature.getId("entities/one_egg"));
        LootParams params = new LootParams.Builder(helper.getLevel()).create(LootContextParamSets.EMPTY);
        LootContext context = new LootContext.Builder(params).create(null);
        ObjectArrayList<ItemStack> loot = new ObjectArrayList<>();
        loot.add(new ItemStack(Items.BONE));
        loot = modifier.apply(loot, context);
        helper.assertTrue(loot.size() == 2 && loot.get(1).is(Items.EGG), "bone + one egg expected, got " + loot);
        helper.succeed();
    }

    // Without Oreganized / Caverns and Chasms the compat tags are empty (optional entries): nothing extra drops and
    // nothing fails to load
    @GameTest(template = TEMPLATE)
    public void f8CompatLootIsEmptyWithoutTheMods(GameTestHelper helper) {
        for (String tag : List.of("compat/oreganized/lead_nugget", "compat/oreganized/electrum_nugget",
                "compat/oreganized/silver_ingot", "compat/caverns_and_chasms/silver_ingot")) {
            TagKey<Item> key = TagKey.create(Registries.ITEM, CreatureFeature.getId(tag));
            boolean empty = ForgeRegistries.ITEMS.tags().getTag(key).isEmpty();
            helper.assertTrue(empty, tag + " should be empty without the mod");
        }
        for (String table : List.of("oreganized:entities/extra_minds", "oreganized:entities/extra_machination",
                "oreganized:entities/extra_sinister", "caverns_and_chasms:entities/extra_sinister")) {
            var lootTable = helper.getLevel().getServer().getLootData().getLootTable(new ResourceLocation(table));
            helper.assertTrue(lootTable != net.minecraft.world.level.storage.loot.LootTable.EMPTY, table + " should load");
        }
        helper.succeed();
    }

    // The 1.21 entity tags aquatic / can_breathe_under_water (not in 1.20.1) become overrides on the crab
    @GameTest(template = TEMPLATE)
    public void f8CrabIsAquaticAndBreathesUnderwater(GameTestHelper helper) {
        Monster crab = CFEntityInit.CANNONBALL_CRAB.get().create(helper.getLevel());
        helper.assertTrue(crab != null && crab.canBreatheUnderwater() && crab.getMobType() == MobType.WATER, "crab: water mob that breathes underwater");
        helper.succeed();
    }
}
