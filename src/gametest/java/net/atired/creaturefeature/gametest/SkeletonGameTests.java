package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * PORT: port-only tests. Phase 0: the mod loads on a dedicated (GameTest) server.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class SkeletonGameTests {
    public static final String TEMPLATE = "empty9x5x9";

    @GameTest(template = TEMPLATE)
    public static void modIsLoaded(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded(CreatureFeature.MODID), "mod not loaded");
        helper.succeed();
    }
}
