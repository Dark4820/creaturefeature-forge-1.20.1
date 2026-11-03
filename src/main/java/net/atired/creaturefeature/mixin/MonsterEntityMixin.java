package net.atired.creaturefeature.mixin;

import net.atired.creaturefeature.Config;
import net.atired.creaturefeature.accessors.LivingEntityGoopAccessor;
import net.atired.creaturefeature.networking.payloads.PathogenPayload;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.Tags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BiomeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MonsterEntityMixin extends LivingEntity {

    protected MonsterEntityMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
    @Inject(method = "finalizeSpawn",at=@At("HEAD"))
    // PORT: Mob.finalizeSpawn takes an extra CompoundTag in 1.20.1
    private void finaliseCFspawnUndead(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir){
        // PORT: #minecraft:undead (1.20.5+) -> MobType.UNDEAD; Config isTrue() -> get(); Forge 1.20.1 has no
        // c:is_temperate/overworld: temperate = overworld and neither hot nor cold (Forge's IS_HOT/IS_COLD_OVERWORLD)
        if(((Mob)(Object)this).getMobType()==net.minecraft.world.entity.MobType.UNDEAD&&(!Config.PATHOGEN.get()||isTemperateOverworld(level.getBiome(getOnPos())))){
            if(this instanceof LivingEntityGoopAccessor accessor && Math.random()>0.92){
                accessor.setBacterial(9);
            }
        }
    }

    private static boolean isTemperateOverworld(net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome) {
        return biome.is(BiomeTags.IS_OVERWORLD) && !biome.is(Tags.Biomes.IS_HOT_OVERWORLD) && !biome.is(Tags.Biomes.IS_COLD_OVERWORLD);
    }
}
