package jooon.pathfinding.voxel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

public object VoxelPathRotation {
   public fun easeTowards(yaw: Float, pitch: Float, yawFactor: Float = 0.18F, pitchFactor: Float = 0.12F) {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         var10000.method_36456(
            var10000.method_36454() + VoxelPathAngles.INSTANCE.wrapDegrees(yaw - var10000.method_36454()) * RangesKt.coerceIn(yawFactor, 0.01F, 1.0F)
         )
         var10000.method_36457(
            var10000.method_36455() + (RangesKt.coerceIn(pitch, -90.0F, 90.0F) - var10000.method_36455()) * RangesKt.coerceIn(pitchFactor, 0.01F, 1.0F)
         )
      }
   }
}
