package jooon.pathfinding.voxel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

object VoxelPathRotation {
   fun easeTowards(yaw: Float, pitch: Float, yawFactor: Float = 0.18F, pitchFactor: Float = 0.12F) {

      if (var10000 != null) {
         var10000.setYaw(
            var10000.getYaw() + VoxelPathAngles.wrapDegrees(yaw - var10000.getYaw()) * (yawFactor).coerceIn(0.01F, 1.0F)
         )
         var10000.setPitch(
            var10000.getPitch() + ((pitch).coerceIn(-90.0F, 90.0F) - var10000.getPitch()) * (pitchFactor).coerceIn(0.01F, 1.0F)
         )
      }
   }
}
