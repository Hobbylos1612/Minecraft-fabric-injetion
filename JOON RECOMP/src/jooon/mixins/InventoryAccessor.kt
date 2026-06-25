package jooon.mixins

import net.minecraft.entity.player.PlayerInventory
import org.spongepowered.asm.mixin.Mixin

@Mixin([PlayerInventory::class])
public interface InventoryAccessor {
   public var selected: Int
      public abstract get() {
      }

      public abstract set(value) {
      }

}
