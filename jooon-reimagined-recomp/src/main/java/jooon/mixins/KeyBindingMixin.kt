package jooon.mixins

import net.minecraft.client.option.KeyBinding
import org.spongepowered.asm.mixin.Mixin

@Mixin(KeyBinding::class)
interface KeyBindingMixin {
   var isDown: Boolean
      abstract get() {
      }

      abstract set(value) {
      }


   var clickCount: Int
      abstract get() {
      }

      abstract set(value) {
      }

}
