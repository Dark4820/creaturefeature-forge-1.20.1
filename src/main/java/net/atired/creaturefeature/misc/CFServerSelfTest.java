package net.atired.creaturefeature.misc;

import com.mojang.logging.LogUtils;
import net.atired.creaturefeature.Config;
import net.atired.creaturefeature.CreatureFeature;
import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.init.CFBlockInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.atired.creaturefeature.init.CFMobEffectInit;
import net.atired.creaturefeature.networking.CFNetwork;
import net.atired.creaturefeature.networking.payloads.DeAmpPayload;
import net.atired.creaturefeature.networking.payloads.GasLeakPayload;
import net.atired.creaturefeature.networking.payloads.RabiesPayload;
import net.atired.creaturefeature.networking.payloads.VelSyncPayload;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PORT: port-only, opt-in check on a real dedicated server, for a throwaway world: start the server with
 * {@code -Dcreaturefeature.selftest=true} (registered only then, and only on a dedicated server) and connect a client
 * started with the same property (client/CFRemoteSelfTest). When the first player joins, it does on the server what
 * the game does there and sends the mod's packets the way the game sends them: blighted brain effects, Infested and
 * Oozing, a falling carapace that squashes a zombie (SquashedPayload), the rabies, gas leak, push and de-amp packets,
 * the fiendish / sleepy / bacterial states behind the screen effects, and one of each creature. It also checks the two
 * client-to-server packets the client test sends (the "man" one grants roots_man, the horn push moves the player).
 * Logs {@code [creaturefeature server selftest] PASS/FAIL} and DONE. It changes the player and spawns mobs.
 */
public final class CFServerSelfTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG = "[creaturefeature server selftest] ";
    private ServerPlayer player;
    private int tick = -1;
    private int pass;
    private int fail;
    private final List<Entity> spawned = new ArrayList<>();
    private double maxPush;
    private BlockPos base;
    private Entity squashedZombie;
    private float serverSquash;

    private void check(String name, boolean ok, String detail) {
        if (ok) {
            this.pass++;
            LOGGER.info(TAG + "PASS {} ({})", name, detail);
        } else {
            this.fail++;
            LOGGER.error(TAG + "FAIL {} ({})", name, detail);
        }
    }

    private static ResourceLocation id(MobEffect effect) {
        return ForgeRegistries.MOB_EFFECTS.getKey(effect);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (this.player == null && event.getEntity() instanceof ServerPlayer sp) {
            this.player = sp;
            this.tick = 0;
            LOGGER.info(TAG + "player {} joined, Trials Chambers installed: {}", sp.getGameProfile().getName(), ModList.get().isLoaded(TrialsCompat.TRIALS));
        }
    }

    // positions on the flat test area built at tick 200 (the floor is at base.y - 1)
    private BlockPos ground(ServerLevel level, int dx, int dz) {
        return this.base.offset(dx, 0, dz);
    }

    private void flatten(ServerLevel level) {
        BlockState floor = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                level.setBlock(this.base.offset(dx, -1, dz), floor, 2);
                for (int dy = 0; dy <= 10; dy++) {
                    level.setBlock(this.base.offset(dx, dy, dz), air, 2);
                }
            }
        }
    }

    private Zombie zombie(ServerLevel level, BlockPos pos) {
        Zombie zombie = EntityType.ZOMBIE.create(level);
        zombie.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        zombie.setNoAi(true);
        zombie.setPersistenceRequired();
        level.addFreshEntity(zombie);
        this.spawned.add(zombie);
        return zombie;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || this.tick < 0 || this.player == null) {
            return;
        }
        if (this.player.isRemoved()) {
            this.check("player stayed connected", false, "disconnected at tick " + this.tick);
            this.finish();
            return;
        }
        this.tick++;
        if (this.tick > 250) {
            this.maxPush = Math.max(this.maxPush, this.player.getDeltaMovement().x);
        }
        try {
            this.step(this.player.serverLevel());
        } catch (RuntimeException e) {
            this.fail++;
            LOGGER.error(TAG + "FAIL threw at tick {}", this.tick, e);
            this.finish();
        }
    }

    private void step(ServerLevel level) {
        ServerPlayer sp = this.player;
        boolean trials = ModList.get().isLoaded(TrialsCompat.TRIALS);
        switch (this.tick) {
            case 200 -> {
                // the blighted brain (each effect is an 80 % roll: eat until all three were seen)
                sp.setGameMode(GameType.SURVIVAL);
                sp.removeAllEffects();
                this.base = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sp.blockPosition()).above(2);
                this.flatten(level);
                sp.teleportTo(this.base.getX() + 0.5, this.base.getY(), this.base.getZ() + 0.5);
                Set<ResourceLocation> seen = new HashSet<>();
                int eaten = 0;
                while (eaten < 40 && seen.size() < 3) {
                    sp.removeAllEffects();
                    new ItemStack(CFItemInit.BLIGHTED_BRAIN.get()).finishUsingItem(level, sp);
                    eaten++;
                    sp.getActiveEffects().forEach(e -> seen.add(id(e.getEffect())));
                }
                ResourceLocation infested = id(TrialsCompat.infested());
                ResourceLocation oozing = id(TrialsCompat.oozing());
                this.check("Infested comes from " + (trials ? "Trials Chambers" : "the port"),
                        infested.getNamespace().equals(trials ? "trials" : CreatureFeature.MODID), "effect=" + infested);
                this.check("Oozing comes from " + (trials ? "Trials Chambers" : "the port"),
                        oozing.getNamespace().equals(trials ? "trials" : CreatureFeature.MODID), "effect=" + oozing);
                this.check("eating the blighted brain gives Nausea, Infested and Oozing",
                        seen.contains(new ResourceLocation("minecraft", "nausea")) && seen.contains(infested) && seen.contains(oozing),
                        "brains eaten=" + eaten + " effects seen=" + seen);
                // effects the client must receive: both brain effects, and the fiendish / sleepy screen effects
                sp.removeAllEffects();
                sp.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 255, false, false));
                sp.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 6000, 255, false, false));
                sp.addEffect(new MobEffectInstance(MobEffects.SATURATION, 6000, 255, false, false));
                sp.addEffect(new MobEffectInstance(TrialsCompat.infested(), 1200, 0));
                sp.addEffect(new MobEffectInstance(TrialsCompat.oozing(), 1200, 0));
                sp.addEffect(new MobEffectInstance(CFMobEffectInit.FIENDISH.get(), 200, 0));
                sp.addEffect(new MobEffectInstance(CFMobEffectInit.SLEEPY.get(), 200, 0));
                // bacterial infection (synced entity data) -> the client's bacteria screen effect
                if (sp instanceof LivingEntityGoopAccessor goop) {
                    goop.setBacterial(1.0f);
                }
                // the mod's server-to-client packets, sent as the game sends them
                CFNetwork.sendToPlayer(sp, new RabiesPayload(sp.getId()));
                CFNetwork.sendToPlayer(sp, new GasLeakPayload(sp.getId(), 1.0f));
                CFNetwork.sendToPlayer(sp, new VelSyncPayload(sp.getId(), 0.0, 1.2, 0.0));
                LOGGER.info(TAG + "sent rabies, gas leak and push packets; fiendish, sleepy and bacterial set");
            }
            case 220 -> {
                // Infested: hurt a zombie that has it (the player attacks)
                Zombie infestedZombie = this.zombie(level, this.ground(level, -6, 0));
                infestedZombie.addEffect(new MobEffectInstance(TrialsCompat.infested(), 400, 0));
                for (int i = 0; i < 100; i++) {
                    infestedZombie.invulnerableTime = 0;
                    infestedZombie.setHealth(infestedZombie.getMaxHealth());
                    infestedZombie.hurt(sp.damageSources().playerAttack(sp), 0.5f);
                }
                AABB box = infestedZombie.getBoundingBox().inflate(6);
                int silverfish = level.getEntitiesOfClass(Silverfish.class, box).size();
                this.check("Infested: silverfish come out when the mob is hurt", silverfish > 0, "silverfish=" + silverfish + " after 100 hits");
                level.getEntitiesOfClass(Silverfish.class, box).forEach(Entity::discard);
                // Oozing: kill a zombie that has it
                Zombie oozingZombie = this.zombie(level, this.ground(level, -6, 4));
                oozingZombie.addEffect(new MobEffectInstance(TrialsCompat.oozing(), 200, 0));
                AABB box2 = oozingZombie.getBoundingBox().inflate(6);
                oozingZombie.hurt(sp.damageSources().playerAttack(sp), 1000.0f);
                List<Slime> slimes = level.getEntitiesOfClass(Slime.class, box2);
                this.check("Oozing: slimes appear when the mob dies", oozingZombie.isDeadOrDying() && !slimes.isEmpty(),
                        "slimes=" + slimes.size() + " sizes=" + slimes.stream().map(Slime::getSize).toList());
                this.spawned.addAll(slimes);
                // squash: a carapace falls next to a zombie (CarapaceBlock sends SquashedPayload to the chunk's players)
                BlockPos zpos = this.ground(level, 4, 0);
                Zombie squashed = this.zombie(level, zpos);
                FallingBlockEntity.fall(level, zpos.above(5), CFBlockInit.CARAPACE_BLOCK.get().defaultBlockState());
                this.squashedZombie = squashed;
                // de-amp packet (heavy axe) about this zombie, to the players tracking it
                CFNetwork.sendToPlayersTrackingEntity(squashed, new DeAmpPayload(squashed.getId(), true));
                LOGGER.info(TAG + "carapace dropped on zombie {}, de-amp sent", squashed.getId());
            }
            case 232, 236, 240, 244, 248, 252 -> {
                // the carapace lands after a few ticks: the server side of the squash (the client checks the packet)
                if (this.squashedZombie instanceof LivingEntityGoopAccessor goop) {
                    this.serverSquash = Math.max(this.serverSquash, goop.getSquashed());
                }
                if (this.tick == 252) {
                    this.check("falling carapace squashes the zombie on the server", this.serverSquash > 0.3f, "max=" + this.serverSquash);
                }
            }
            case 260 -> {
                // one of each creature around the player (their spawn packets reach the client)
                int i = 0;
                int creatures = 0;
                for (String name : Config.CREATURES) {
                    EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(CreatureFeature.getId(name));
                    double angle = Math.PI * 2 * i / Config.CREATURES.length;
                    int dx = (int) Math.round(Math.cos(angle) * 9);
                    int dz = (int) Math.round(Math.sin(angle) * 9);
                    // the minedflayer goes next to the player: its brainrot is computed on both sides
                    BlockPos pos = name.equals("minedflayer") ? this.ground(level, 2, 2) : this.ground(level, dx, dz);
                    Entity e = type.spawn(level, pos.above(), MobSpawnType.COMMAND);
                    if (e != null) {
                        // still, so distances stay as placed (the minedflayer's brainrot runs in its tick, not its AI)
                        if (e instanceof Mob mob) {
                            mob.setNoAi(true);
                        }
                        this.spawned.add(e);
                        creatures++;
                    }
                    i++;
                }
                this.check("all creatures spawned on the server", creatures == Config.CREATURES.length,
                        "spawned=" + creatures + "/" + Config.CREATURES.length);
            }
            case 520 -> {
                // client-to-server packets sent by the client test around its tick 300
                Advancement man = sp.server.getAdvancements().getAdvancement(CreatureFeature.getId("roots_man"));
                boolean done = man != null && sp.getAdvancements().getOrStartProgress(man).isDone();
                this.check("C2S man packet: roots_man granted", done, "done=" + done);
                // the client pushes +0.9 along x; one tick of ground friction (x0.546) leaves ~0.49 when the server samples it
                this.check("C2S horn push packet: the player was pushed (+x)", this.maxPush >= 0.4, "max server-side x speed=" + String.format("%.2f", this.maxPush));
                this.spawned.forEach(Entity::discard);
                this.finish();
            }
            default -> { }
        }
    }

    private void finish() {
        LOGGER.info(TAG + "DONE pass={} fail={}", this.pass, this.fail);
        this.tick = -1;
    }
}
