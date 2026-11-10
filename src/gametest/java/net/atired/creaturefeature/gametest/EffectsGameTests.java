package net.atired.creaturefeature.gametest;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.init.CFItemInit;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.atired.creaturefeature.misc.TrialsCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.List;

/**
 * Port-only: the blighted brain's Infested / Oozing (1.21 effects). Run once as usual (no Trials Chambers: the port's
 * own effects) and once with -PwithTrials (Trials Chambers in the dev run: its trials:infested / trials:oozing).
 */
@GameTestHolder(CreatureFeature.MODID)
@PrefixGameTestTemplate(false)
public class EffectsGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEMPLATE = SkeletonGameTests.TEMPLATE;

    private static ResourceLocation id(MobEffect effect) {
        return ForgeRegistries.MOB_EFFECTS.getKey(effect);
    }

    // With Trials Chambers: its effects; without: the port's own. The brain keeps the original's three effects.
    @GameTest(template = TEMPLATE)
    public void effBlightedBrainUsesTrialsOrOwnEffects(GameTestHelper helper) {
        boolean trials = ModList.get().isLoaded(TrialsCompat.TRIALS);
        ResourceLocation infested = trials ? new ResourceLocation("trials", "infested") : CreatureFeature.getId("infested");
        ResourceLocation oozing = trials ? new ResourceLocation("trials", "oozing") : CreatureFeature.getId("oozing");
        List<ResourceLocation> effects = CFItemInit.BLIGHTED_BRAIN_FOOD.getEffects().stream().map(p -> id(p.getFirst().getEffect())).toList();
        LOGGER.info("CF_EFFECTS trials loaded: {}, blighted brain effects: {}", trials, effects);
        helper.assertTrue(effects.size() == 3 && effects.get(0).equals(new ResourceLocation("minecraft", "nausea")),
                "blighted brain: nausea + two effects, got " + effects);
        helper.assertTrue(effects.get(1).equals(infested), "infested should be " + infested + ", got " + effects.get(1));
        helper.assertTrue(effects.get(2).equals(oozing), "oozing should be " + oozing + ", got " + effects.get(2));
        helper.assertTrue(CFItemInit.BLIGHTED_BRAIN_FOOD.getEffects().stream().allMatch(p -> p.getSecond() == 0.8f), "80 % each, as the original");
        helper.succeed();
    }

    // The port's own Infested: about one hit in ten releases 1-2 silverfish (100 hits: practically certain)
    @GameTest(template = TEMPLATE)
    public void effOwnInfestedReleasesSilverfish(GameTestHelper helper) {
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(1, 2, 1));
        zombie.setInvulnerable(false);
        zombie.addEffect(new MobEffectInstance(CFMobEffectInit.INFESTED.get(), 400, 0));
        for (int i = 0; i < 100; i++) {
            zombie.invulnerableTime = 0;
            zombie.setHealth(zombie.getMaxHealth());
            zombie.hurt(helper.getLevel().damageSources().generic(), 0.5f);
        }
        AABB box = zombie.getBoundingBox().inflate(4);
        int silverfish = helper.getLevel().getEntitiesOfClass(Silverfish.class, box).size();
        helper.assertTrue(silverfish > 0 && silverfish <= 200, "expected silverfish, got " + silverfish);
        helper.succeed();
    }

    // The port's own Oozing: two size-2 slimes when the mob dies
    @GameTest(template = TEMPLATE)
    public void effOwnOozingSpawnsSlimesOnDeath(GameTestHelper helper) {
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(1, 2, 1));
        zombie.addEffect(new MobEffectInstance(CFMobEffectInit.OOZING.get(), 200, 0));
        AABB box = zombie.getBoundingBox().inflate(4);
        zombie.kill();
        List<Slime> slimes = helper.getLevel().getEntitiesOfClass(Slime.class, box);
        helper.assertTrue(slimes.size() == 2 && slimes.stream().allMatch(s -> s.getSize() == 2),
                "expected two size-2 slimes, got " + slimes.size());
        helper.succeed();
    }
}
