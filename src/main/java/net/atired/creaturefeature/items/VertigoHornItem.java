package net.atired.creaturefeature.items;

import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.atired.creaturefeature.init.CFParticleInit;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

// PORT: the instrument is stored as in 1.20.1's InstrumentItem (NBT string "instrument", the registry id) instead of
// the 1.20.5+ INSTRUMENT data component. The client-only dash (LocalPlayer input) moved to CFClientItemHooks, called
// through DistExecutor: this item class is loaded on the dedicated server.
public class VertigoHornItem extends Item {
    private static final String TAG_INSTRUMENT = "instrument";
    private final TagKey<Instrument> instruments;

    public VertigoHornItem(Item.Properties properties, TagKey<Instrument> instruments) {
        super(properties);
        this.instruments = instruments;
    }

    // PORT: appendHoverText(stack, TooltipContext, ...) -> appendHoverText(stack, Level, ...)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        Optional<ResourceKey<Instrument>> optional = this.getInstrument(stack).flatMap(Holder::unwrapKey);
        if (optional.isPresent()) {
            MutableComponent mutablecomponent = Component.translatable(Util.makeDescriptionId("instrument", ((ResourceKey)optional.get()).location())).append("?");
            tooltipComponents.add(mutablecomponent.withStyle(ChatFormatting.GRAY));
        }

    }

    // PORT: stack.set(DataComponents.INSTRUMENT, ...) -> 1.20.1's InstrumentItem.create (writes the same NBT)
    public static ItemStack create(Item item, Holder<Instrument> instrument) {
        return InstrumentItem.create(item, instrument);
    }

    public static void setRandom(ItemStack stack, TagKey<Instrument> instrumentTag, RandomSource random) {
        InstrumentItem.setRandom(stack, instrumentTag, random);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if(level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(CFParticleInit.RABIES_PARTICLE.get(),livingEntity.getX(),livingEntity.getY(0.8),livingEntity.getZ(),4,0.2,0.2,0.2,0.2);
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        Optional<? extends Holder<Instrument>> optional = this.getInstrument(itemstack);
        if (optional.isPresent()) {
            Instrument instrument = (Instrument)((Holder)optional.get()).value();
            player.startUsingItem(usedHand);
            play(level, player, instrument);
            player.getCooldowns().addCooldown(this, instrument.useDuration());
            player.addDeltaMovement(new Vec3(0,0.4,0));
            // PORT: the original checks "player instanceof LocalPlayer" here (client class in a common item)
            if(level.isClientSide()){
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> net.atired.creaturefeature.client.CFClientItemHooks.vertigoHornDash(player));
            }
            if(level instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(CFParticleInit.TOOT_PARTICLE.get(),player.getX(),player.getY(0.5),player.getZ(),1,0,0,0,0);
                serverLevel.sendParticles(CFParticleInit.RABIES_PARTICLE.get(),player.getX(),player.getY(0.5),player.getZ(),32,0.6,0.2,0.6,0.2);
            }
            if(player instanceof PlayerBrainrotAccessor accessor){
                accessor.setRabies(0.5f);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    // PORT: getUseDuration(stack, entity) -> getUseDuration(stack)
    @Override
    public int getUseDuration(ItemStack stack) {
        Optional<Holder<Instrument>> optional = this.getInstrument(stack);
        return (Integer)optional.map((p_248418_) -> {
            return ((Instrument)p_248418_.value()).useDuration();
        }).orElse(0);
    }

    // PORT: 1.20.1's InstrumentItem.getInstrument (private there): the NBT id, or the first instrument of the tag
    private Optional<Holder<Instrument>> getInstrument(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_INSTRUMENT, 8)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(TAG_INSTRUMENT));
            if (id != null) {
                return BuiltInRegistries.INSTRUMENT.getHolder(ResourceKey.create(Registries.INSTRUMENT, id)).map(h -> (Holder<Instrument>) h);
            }
        }
        Iterator<Holder<Instrument>> iterator = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    private static void play(Level level, Player player, Instrument instrument) {
        SoundEvent soundevent = (SoundEvent)instrument.soundEvent().value();
        float f = instrument.range() / 16.0F;
        level.playSound(player, player, soundevent, SoundSource.RECORDS, f, 0.66F);
        level.playSound(player, player, soundevent, SoundSource.RECORDS, f*0.5f, 1.66F);
        level.playSound(player, player, soundevent, SoundSource.RECORDS, f*0.5f, 0.33F);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }
}
