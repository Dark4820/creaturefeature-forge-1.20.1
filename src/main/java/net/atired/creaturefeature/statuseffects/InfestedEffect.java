package net.atired.creaturefeature.statuseffects;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * PORT: port-only (user decision). 1.21's vanilla Infested effect (MobEffects.INFESTED, used by the blighted brain)
 * does not exist in 1.20.1. When Trials Chambers is installed the brain uses its trials:infested instead
 * (misc/TrialsCompat); otherwise this copy of 1.21's behaviour: each time the mob takes damage, a 10 % chance to
 * release 1-2 silverfish (TrialsCompat calls {@link #onMobHurt}).
 */
public class InfestedEffect extends MobEffect {
    private static final float CHANCE_TO_SPAWN = 0.1f;

    public InfestedEffect() {
        super(MobEffectCategory.HARMFUL, 0x8C9B8C);
    }

    public void onMobHurt(LivingEntity entity) {
        RandomSource random = entity.getRandom();
        if (random.nextFloat() <= CHANCE_TO_SPAWN) {
            int count = Mth.randomBetweenInclusive(random, 1, 2);
            for (int i = 0; i < count; i++) {
                spawnSilverfish(entity.level(), entity, entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ());
            }
        }
    }

    private static void spawnSilverfish(Level level, LivingEntity entity, double x, double y, double z) {
        Silverfish silverfish = EntityType.SILVERFISH.create(level);
        if (silverfish != null) {
            RandomSource random = entity.getRandom();
            float quarter = (float) Math.PI / 2.0f;
            float angle = Mth.randomBetween(random, -quarter, quarter);
            Vector3f dir = entity.getLookAngle().toVector3f().mul(0.3f).mul(1.0f, 1.5f, 1.0f).rotateY(angle);
            silverfish.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0f, 0.0f);
            silverfish.setDeltaMovement(new Vec3(dir));
            level.addFreshEntity(silverfish);
            silverfish.playSound(SoundEvents.SILVERFISH_HURT);
        }
    }

    // The icon draws a vanilla item (1.21's icon textures are not shipped); only runs on the client
    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        net.atired.creaturefeature.client.CFEffectIcons.item(consumer, net.minecraft.world.item.Items.SILVERFISH_SPAWN_EGG);
    }
}
