package net.atired.creaturefeature.statuseffects;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Consumer;

/**
 * PORT: port-only (user decision). 1.21's vanilla Oozing effect (MobEffects.OOZING, used by the blighted brain)
 * does not exist in 1.20.1. When Trials Chambers is installed the brain uses its trials:oozing instead
 * (misc/TrialsCompat); otherwise this copy of 1.21's behaviour: when the mob dies, two size-2 slimes appear where it
 * was, fewer if the maxEntityCramming rule would be exceeded by the slimes already around (TrialsCompat calls
 * {@link #onMobDeath}).
 */
public class OozingEffect extends MobEffect {
    private static final int SLIMES = 2;

    public OozingEffect() {
        super(MobEffectCategory.HARMFUL, 0x99FFA3);
    }

    public void onMobDeath(LivingEntity entity) {
        Level level = entity.level();
        int cramming = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
        int count = SLIMES;
        if (cramming >= 1) {
            int nearby = level.getEntitiesOfClass(Slime.class, entity.getBoundingBox().inflate(2.0)).size();
            count = Mth.clamp(cramming - nearby, 0, SLIMES);
        }
        for (int i = 0; i < count; i++) {
            Slime slime = EntityType.SLIME.create(level);
            if (slime != null) {
                slime.setSize(2, true);
                slime.moveTo(entity.getX(), entity.getY() + 0.5, entity.getZ(), level.getRandom().nextFloat() * 360.0f, 0.0f);
                level.addFreshEntity(slime);
            }
        }
    }

    // The icon draws a vanilla item (1.21's icon textures are not shipped); only runs on the client
    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        net.atired.creaturefeature.client.CFEffectIcons.item(consumer, net.minecraft.world.item.Items.SLIME_BALL);
    }
}
