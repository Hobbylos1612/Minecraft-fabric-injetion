package jooon.mixins

import net.minecraft.entity.player.PlayerInventory
import org.spongepowered.asm.mixin.Mixin

@Mixin(PlayerInventory::class)
interface InventoryAccessor {
   var selected: Int
      abstract get() {
      }

      abstract set(value) {
      }

}
