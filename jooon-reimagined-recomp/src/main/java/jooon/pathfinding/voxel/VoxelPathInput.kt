package jooon.pathfinding.voxel

import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

object VoxelPathInput {
   fun press(action: jooon.pathfinding.voxel.VoxelPathInput.MoveAction) {
      when (VoxelPathInput.WhenMappings.$EnumSwitchMapping$0[action.ordinal()]) {
         1 -> PlayerController.pressForward(true)
         2 -> PlayerController.pressBack(true)
         3 -> PlayerController.pressLeft(true)
         4 -> PlayerController.pressRight(true)
         5 -> PlayerController.pressJump(true)
         6 -> PlayerController.pressSneak(true)
         7 -> {
            PlayerController.pressSprint(true)

            if (var10000 != null) {
               var10000.setSprinting(true)
            }
         }
         else -> throw NoWhenBranchMatchedException()
      }
   }

   fun release(action: jooon.pathfinding.voxel.VoxelPathInput.MoveAction) {
      when (VoxelPathInput.WhenMappings.$EnumSwitchMapping$0[action.ordinal()]) {
         1 -> PlayerController.pressForward(false)
         2 -> PlayerController.pressBack(false)
         3 -> PlayerController.pressLeft(false)
         4 -> PlayerController.pressRight(false)
         5 -> PlayerController.pressJump(false)
         6 -> PlayerController.pressSneak(false)
         7 -> {
            PlayerController.pressSprint(false)

            if (var10000 != null) {
               var10000.setSprinting(false)
            }
         }
         else -> throw NoWhenBranchMatchedException()
      }
   }

   fun releaseAll() {
      for (`element$iv` in VoxelPathInput.MoveAction.getEntries() as java.lang.Iterable) {
         release(`element$iv` as VoxelPathInput.MoveAction)
      }
   }

   enum class MoveAction {
      FORWARD,
      BACKWARD,
      LEFT,
      RIGHT,
      JUMP,
      SNEAK,
      SPRINT;

      
      fun getEntries(): EnumEntries<VoxelPathInput.MoveAction> {
         $ENTRIES
      }
   }
}
