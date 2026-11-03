package net.atired.creaturefeature.items;

import net.atired.creaturefeature.entity.VitricArrowEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class VitricArrowItem extends ArrowItem {
    public VitricArrowItem(Item.Properties properties) {
        super(properties);
    }

    // PORT: createArrow(level, ammo, shooter, weapon) -> createArrow(level, ammo, shooter) (no weapon in 1.20.1)
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter) {
        return new VitricArrowEntity(level, shooter, ammo.copyWithCount(1), null);
    }

    // PORT: ProjectileItem (1.21 dispenser interface) does not exist: this is now a plain helper, used by the
    // dispenser behaviour registered in CreatureFeature.commonSetup (same default power/uncertainty)
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        VitricArrowEntity arrow = new VitricArrowEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), (ItemStack)null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }

    // PORT: isInfinite(ammo, bow, LivingEntity) -> isInfinite(ammo, bow, Player) (Forge 1.20.1)
    public boolean isInfinite(ItemStack ammo, ItemStack bow, net.minecraft.world.entity.player.Player livingEntity) {
        return false;
    }
}
