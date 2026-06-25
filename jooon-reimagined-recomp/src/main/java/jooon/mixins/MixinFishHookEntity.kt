package jooon.mixins

import jooon.features.fishing.FunnyFishing
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.util.math.BlockPos
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(FishingBobberEntity::class)
abstract class MixinFishHookEntity {
   @Shadow
   private int field_7173;

   @Inject(method = ["method_6949"], at = [@At("TAIL")])
   fun jooonTickFishingLogic(pos: BlockPos, ci: CallbackInfo) {

      FunnyFishing.onHookTick(if (owner != null) owner.getUuid() else null, this.hookCountdown)
   }
}
