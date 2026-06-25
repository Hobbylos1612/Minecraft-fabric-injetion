package jooon.mixins

import jooon.util.PlayerController
import net.minecraft.client.Mouse
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Mouse::class)
class MouseScrollBlockerMixin {
   @Inject(method = ["method_1598"], at = [@At("HEAD")], cancellable = true)
   private fun jooon_blockScroll(window: Long, horizontal: Double, vertical: Double, ci: CallbackInfo) {
      if (PlayerController.shouldBlockScroll(vertical, horizontal)) {
         ci.cancel()
      }
   }
}
