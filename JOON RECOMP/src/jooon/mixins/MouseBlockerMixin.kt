package jooon.mixins

import jooon.util.PlayerController
import net.minecraft.client.Mouse
import net.minecraft.client.input.MouseInput
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin([Mouse::class])
public class MouseBlockerMixin {
   @Inject(method = ["method_1601"], at = [@At("HEAD")], cancellable = true)
   fun jooonBlockMouseBtn(window: Long, info: MouseInput, action: Int, ci: CallbackInfo) {
      if (PlayerController.INSTANCE.shouldBlockMouseButton(info.comp_4801(), action)) {
         ci.cancel()
      }
   }
}
