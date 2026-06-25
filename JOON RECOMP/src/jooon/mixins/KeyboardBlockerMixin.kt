package jooon.mixins

import jooon.util.PlayerController
import net.minecraft.client.Keyboard
import net.minecraft.client.input.KeyInput
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin([Keyboard::class])
public class KeyboardBlockerMixin {
   @Inject(method = ["method_1466"], at = [@At("HEAD")], cancellable = true)
   fun jooonBlockKeys(window: Long, action: Int, event: KeyInput, ci: CallbackInfo) {
      if (PlayerController.INSTANCE.shouldBlockKey(event.comp_4795(), action)) {
         ci.cancel()
      }
   }
}
