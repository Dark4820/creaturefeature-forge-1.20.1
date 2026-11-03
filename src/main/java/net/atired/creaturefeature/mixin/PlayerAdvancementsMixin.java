package net.atired.creaturefeature.mixin;

import net.atired.creaturefeature.accessors.PlayerBrainrotAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {
    @Shadow private ServerPlayer player;

    @Inject(method = "award",at= @At(value = "HEAD"))
    // PORT: award(AdvancementHolder, ...) -> award(Advancement, ...); requirements are a String[][] in 1.20.1
    private void  unperform(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir){
        if(advancement.getRequirements()[0][0].equals("man_test_criteria_name")&&player instanceof PlayerBrainrotAccessor accessor && !accessor.getManCan()){
            player.sendSystemMessage(Component.literal("Well, there was not a            here."));

            //No.
            System.out.println(criterionKey);
            cir.cancel();
        }
    }
}
