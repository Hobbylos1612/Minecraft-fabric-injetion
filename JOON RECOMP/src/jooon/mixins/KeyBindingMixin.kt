package jooon.mixins

import net.minecraft.client.option.KeyBinding
import org.spongepowered.asm.mixin.Mixin

@Mixin([KeyBinding::class])
public interface KeyBindingMixin {
   public var isDown: Boolean
      public abstract get() {
      }

      public abstract set(value) {
      }


   public var clickCount: Int
      public abstract get() {
      }

      public abstract set(value) {
      }

}
