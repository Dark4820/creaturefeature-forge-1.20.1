package net.atired.creaturefeature.gametest;

import io.netty.buffer.Unpooled;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFDataComponentTypeInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.atired.creaturefeature.init.CFParticleInit;
import net.atired.creaturefeature.init.CFSoundInit;
import net.atired.creaturefeature.items.DreamCatcherContents;
import net.atired.creaturefeature.networking.payloads.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * PORT: port-only tests for phase F2 (network, NBT instead of data components, particles, music discs).
 * Running on the GameTest server (dedicated dist) also proves that the payload classes load without client classes.
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class F2GameTests {
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    private static <T> T roundTrip(T payload, BiConsumer<T, FriendlyByteBuf> encode, Function<FriendlyByteBuf, T> decode, GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encode.accept(payload, buf);
        T read = decode.apply(buf);
        helper.assertTrue(buf.readableBytes() == 0, "bytes left over for " + payload);
        helper.assertTrue(read.equals(payload), "round trip " + payload + " -> " + read);
        return read;
    }

    @GameTest(template = TEMPLATE)
    public static void payloadsRoundTrip(GameTestHelper helper) {
        roundTrip(new VelSyncPayload(123456, 0.5, -1.25, 3.0), VelSyncPayload::encode, VelSyncPayload::decode, helper);
        roundTrip(new DeAmpPayload(7, true), DeAmpPayload::encode, DeAmpPayload::decode, helper);
        roundTrip(new C2SVelSyncPayload(42, 1.1, 0.0, -1.2), C2SVelSyncPayload::encode, C2SVelSyncPayload::decode, helper);
        roundTrip(new C2SManPayload(99), C2SManPayload::encode, C2SManPayload::decode, helper);
        roundTrip(new RabiesPayload(5), RabiesPayload::encode, RabiesPayload::decode, helper);
        roundTrip(new PathogenPayload(8, 0.75f), PathogenPayload::encode, PathogenPayload::decode, helper);
        roundTrip(new SquashedPayload(3), SquashedPayload::encode, SquashedPayload::decode, helper);
        roundTrip(new GasLeakPayload(11, 2.5f), GasLeakPayload::encode, GasLeakPayload::decode, helper);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void hornPushValidation(GameTestHelper helper) {
        // The vertigo horn dash: horizontal, length 1.66
        Vec3 dash = new Vec3(0, 0, 1).normalize().scale(1.66).yRot(1.2f);
        helper.assertTrue(C2SVelSyncPayload.isValidPush(dash.x, dash.y, dash.z), "real horn dash rejected");
        helper.assertTrue(!C2SVelSyncPayload.isValidPush(0, 10, 0), "huge push accepted");
        helper.assertTrue(!C2SVelSyncPayload.isValidPush(Double.NaN, 0, 0), "NaN push accepted");
        helper.assertTrue(!C2SVelSyncPayload.isValidPush(Double.POSITIVE_INFINITY, 0, 0), "infinite push accepted");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void componentsAsNbt(GameTestHelper helper) {
        ItemStack catcher = new ItemStack(Items.PAPER);
        helper.assertTrue(CFDataComponentTypeInit.getDreamcatcherContents(catcher) == DreamCatcherContents.EMPTY, "default is not EMPTY");
        helper.assertTrue(CFDataComponentTypeInit.getOpened(catcher) == 0, "default opened is not 0");

        ItemStack egg = new ItemStack(Items.ZOMBIE_SPAWN_EGG, 2);
        egg.setHoverName(net.minecraft.network.chat.Component.literal("Bob"));
        CFDataComponentTypeInit.setDreamcatcherContents(catcher, new DreamCatcherContents(List.of(egg)));
        CFDataComponentTypeInit.setOpened(catcher, 1);

        // Save and load the whole stack, as a world save or the network would
        ItemStack reloaded = ItemStack.of(catcher.save(new net.minecraft.nbt.CompoundTag()));
        DreamCatcherContents contents = CFDataComponentTypeInit.getDreamcatcherContents(reloaded);
        helper.assertTrue(contents.equals(new DreamCatcherContents(List.of(egg))), "contents lost: " + reloaded.getTag());
        helper.assertTrue(CFDataComponentTypeInit.getOpened(reloaded) == 1, "opened lost");

        CFDataComponentTypeInit.setDreamcatcherContents(reloaded, DreamCatcherContents.EMPTY);
        helper.assertTrue(!CFDataComponentTypeInit.getDreamcatcherContents(reloaded).items().iterator().hasNext(), "not emptied");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void particleTypesRegistered(GameTestHelper helper) {
        helper.assertTrue(CFParticleInit.PARTICLE_TYPES.getEntries().size() == 17, "particle types: " + CFParticleInit.PARTICLE_TYPES.getEntries().size());
        CFParticleInit.PARTICLE_TYPES.getEntries().forEach(p ->
                helper.assertTrue(BuiltInRegistries.PARTICLE_TYPE.containsKey(p.getId()), "missing particle " + p.getId()));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void discsHaveSongData(GameTestHelper helper) {
        RecordItem nothing = (RecordItem) CFItemInit.NOTHING_DISC.get();
        RecordItem nan = (RecordItem) CFItemInit.NEW_AGE_NEVERMORE_DISC.get();
        helper.assertTrue(nothing.getAnalogOutput() == 15 && nothing.getLengthInTicks() == 326, "nothing disc data");
        helper.assertTrue(nan.getAnalogOutput() == 3 && nan.getLengthInTicks() == 1420, "nan disc data");
        helper.assertTrue(nothing.getSound() == CFSoundInit.NOTHING.get(), "nothing disc sound");
        helper.assertTrue(nan.getSound() == CFSoundInit.NEW_AGE_NEVERMORE.get(), "nan disc sound");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void discsGoIntoJukebox(GameTestHelper helper) {
        insertDisc(helper, new BlockPos(2, 2, 2), new ItemStack(CFItemInit.NOTHING_DISC.get()));
        insertDisc(helper, new BlockPos(6, 2, 6), new ItemStack(CFItemInit.NEW_AGE_NEVERMORE_DISC.get()));
        helper.succeed();
    }

    private static void insertDisc(GameTestHelper helper, BlockPos pos, ItemStack disc) {
        helper.setBlock(pos, Blocks.JUKEBOX);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, disc);
        BlockPos abs = helper.absolutePos(pos);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
        ItemStack expected = disc.copy();
        InteractionResult result = disc.getItem().useOn(context);
        helper.assertTrue(result.consumesAction(), expected + " not inserted: " + result);
        helper.assertTrue(helper.getBlockState(pos).getValue(JukeboxBlock.HAS_RECORD), "jukebox has no record");
        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) helper.getBlockEntity(pos);
        helper.assertTrue(ItemStack.isSameItem(jukebox.getFirstItem(), expected), "jukebox holds " + jukebox.getFirstItem());
    }
}
