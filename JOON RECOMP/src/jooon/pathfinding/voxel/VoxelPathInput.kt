package jooon.pathfinding.voxel

import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

@SourceDebugExtension(["SMAP\nVoxelPathInput.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VoxelPathInput.kt\njooon/pathfinding/voxel/VoxelPathInput\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,44:1\n1855#2,2:45\n*S KotlinDebug\n*F\n+ 1 VoxelPathInput.kt\njooon/pathfinding/voxel/VoxelPathInput\n*L\n41#1:45,2\n*E\n"])
public object VoxelPathInput {
   public fun press(action: jooon.pathfinding.voxel.VoxelPathInput.MoveAction) {
      when (VoxelPathInput.WhenMappings.$EnumSwitchMapping$0[action.ordinal()]) {
         1 -> PlayerController.INSTANCE.pressForward(true)
         2 -> PlayerController.INSTANCE.pressBack(true)
         3 -> PlayerController.INSTANCE.pressLeft(true)
         4 -> PlayerController.INSTANCE.pressRight(true)
         5 -> PlayerController.INSTANCE.pressJump(true)
         6 -> PlayerController.INSTANCE.pressSneak(true)
         7 -> {
            PlayerController.INSTANCE.pressSprint(true)
            val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
            if (var10000 != null) {
               var10000.method_5728(true)
            }
         }
         else -> throw NoWhenBranchMatchedException()
      }
   }

   public fun release(action: jooon.pathfinding.voxel.VoxelPathInput.MoveAction) {
      when (VoxelPathInput.WhenMappings.$EnumSwitchMapping$0[action.ordinal()]) {
         1 -> PlayerController.INSTANCE.pressForward(false)
         2 -> PlayerController.INSTANCE.pressBack(false)
         3 -> PlayerController.INSTANCE.pressLeft(false)
         4 -> PlayerController.INSTANCE.pressRight(false)
         5 -> PlayerController.INSTANCE.pressJump(false)
         6 -> PlayerController.INSTANCE.pressSneak(false)
         7 -> {
            PlayerController.INSTANCE.pressSprint(false)
            val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
            if (var10000 != null) {
               var10000.method_5728(false)
            }
         }
         else -> throw NoWhenBranchMatchedException()
      }
   }

   public fun releaseAll() {
      for (`element$iv` in VoxelPathInput.MoveAction.getEntries() as java.lang.Iterable) {
         INSTANCE.release(`element$iv` as VoxelPathInput.MoveAction)
      }
   }

   public enum class MoveAction {
      FORWARD,
      BACKWARD,
      LEFT,
      RIGHT,
      JUMP,
      SNEAK,
      SPRINT;

      @JvmStatic
      fun getEntries(): EnumEntries<VoxelPathInput.MoveAction> {
         $ENTRIES
      }
   }
}
