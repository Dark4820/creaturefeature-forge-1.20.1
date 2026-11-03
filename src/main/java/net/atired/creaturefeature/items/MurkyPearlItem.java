package net.atired.creaturefeature.items;

import net.atired.creaturefeature.entity.MurkyPearlEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class MurkyPearlItem  extends Item {
    public MurkyPearlItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            MurkyPearlEntity snowball = new MurkyPearlEntity(level, player);
            snowball.setItem(itemstack);
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(snowball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        // PORT: ItemStack.consume(1, player) (1.20.5+) -> its body: shrink unless the player is in creative
        if (!player.getAbilities().instabuild) itemstack.shrink(1);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    @Override
    // PORT: appendHoverText(stack, TooltipContext, ...) -> appendHoverText(stack, Level, ...)
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        MutableComponent mutablecomponent = Component.translatable("desc.creaturefeature.murky");
        tooltipComponents.add(mutablecomponent.withStyle(ChatFormatting.GRAY));
    }
    // PORT: ProjectileItem (1.21 dispenser interface) does not exist: this is now a plain helper, used by the
    // dispenser behaviour registered in CreatureFeature.commonSetup (same default power/uncertainty)
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        MurkyPearlEntity snowball = new MurkyPearlEntity(level, pos.x(), pos.y(), pos.z());
        snowball.setItem(stack);
        return snowball;
    }
}
