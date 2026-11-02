package net.atired.creaturefeature.gametest;

import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.blocks.FeatherTints;
import net.atired.creaturefeature.init.CFBlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * PORT: port-only test. Brushing down feathers (block and carpet) turns them into the mosaic version and back,
 * through the same server path as a real right click (ServerPlayerGameMode.useItemOn with a real brush).
 * Normal and mosaic feathers share one model, so in the game only their tints tell them apart: the tint test checks
 * the colours that the client colour handlers (CreatureFeatureClient) use. The handlers themselves are client-only
 * and cannot run on the GameTest server.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class FeathersBrushGameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    @GameTest(template = TEMPLATE)
    public static void brushTogglesMosaicFeathers(GameTestHelper helper) {
        FakePlayer player = FakePlayerFactory.getMinecraft((ServerLevel) helper.getLevel());
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));

        BlockPos block = new BlockPos(2, 2, 2);
        helper.setBlock(block, CFBlockInit.DOWN_FEATHERS.get());
        brush(helper, player, block, CFBlockInit.MOSAIC_DOWN_FEATHERS.get());
        brush(helper, player, block, CFBlockInit.DOWN_FEATHERS.get());

        BlockPos carpet = new BlockPos(6, 2, 6);
        helper.setBlock(carpet.below(), Blocks.STONE);
        helper.setBlock(carpet, CFBlockInit.DOWN_FEATHERS_CARPET.get());
        brush(helper, player, carpet, CFBlockInit.MOSAIC_DOWN_FEATHERS_CARPET.get());
        brush(helper, player, carpet, CFBlockInit.DOWN_FEATHERS_CARPET.get());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void mosaicAndNormalFeathersHaveDifferentTints(GameTestHelper helper) {
        for (int tint = 0; tint <= 1; tint++) {
            helper.assertTrue(FeatherTints.item(true, tint) != FeatherTints.item(false, tint), "same item tint " + tint);
            int differing = 0;
            for (BlockPos pos : BlockPos.betweenClosed(-3, -3, -3, 3, 3, 3)) {
                int mosaic = FeatherTints.mosaic(pos, tint);
                int normal = FeatherTints.normal(pos, tint);
                helper.assertTrue((mosaic & 0xFFFFFF) != 0xFFFFFF && (normal & 0xFFFFFF) != 0xFFFFFF, "untinted at " + pos);
                if ((mosaic & 0xFFFFFF) != (normal & 0xFFFFFF)) differing++;
            }
            helper.assertTrue(differing > 300, "mosaic and normal tints differ at only " + differing + "/343 positions");
        }
        // Same values as the original: item colours and one mosaic stripe
        helper.assertTrue(FeatherTints.item(true, 0) == 0xc3efc7 && FeatherTints.item(false, 1) == 0x81cdd8, "item colours");
        helper.assertTrue(FeatherTints.mosaic(new BlockPos(1, 2, 0), 1) == 0xaae19a, "mosaic stripe");
        helper.succeed();
    }

    private static void brush(GameTestHelper helper, FakePlayer player, BlockPos pos, Block expected) {
        BlockPos abs = helper.absolutePos(pos);
        InteractionResult result = player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(),
                InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
        helper.assertTrue(result.consumesAction(), "brush on " + pos + " did not act: " + result);
        helper.assertBlockPresent(expected, pos);
    }
}
