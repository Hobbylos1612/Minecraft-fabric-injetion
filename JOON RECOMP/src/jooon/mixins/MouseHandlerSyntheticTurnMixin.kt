package jooon.mixins

import jooon.util.SyntheticMouseTurnBroker
import kotlin.jvm.internal.Intrinsics
import net.minecraft.client.MinecraftClient
import net.minecraft.client.Mouse
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin([Mouse::class])
public abstract class MouseHandlerSyntheticTurnMixin {
   @Shadow
   @Final
   private MinecraftClient field_1779;
   @Shadow
   private double field_1789;
   @Shadow
   private double field_1787;

   @Inject(method = ["method_1606"], at = [@At("HEAD")])
   private fun jooonInjectSyntheticMouseTurn(deltaTime: Double, ci: CallbackInfo) {
      var var10000: MinecraftClient = this.field_1779
      if (this.field_1779 == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minecraft")
         var10000 = null
      }

      if (var10000.field_1724 != null) {
         val var5: SyntheticMouseTurnBroker.TurnDelta = SyntheticMouseTurnBroker.getSyntheticMouseTurn()
         if (var5 != null) {
            this.field_1789 = this.field_1789 + var5.rawX
            this.field_1787 = this.field_1787 + var5.rawY
         }
      }
   }
}
