package jooon.pathfinding.voxel

object VoxelPathAngles {
   fun calcAimAnglesFromDelta(dx: Double, dy: Double, dz: Double): Pair<Float, Float> {
      return Pair(Math.toDegrees(Math.atan2(-dx, dz)).toFloat(), (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))).toFloat())
   }

   fun wrapDegrees(delta: Float): Float {
      var d: Float = delta % 360.0F
      if (delta % 360.0F > 180.0F) {
         d -= 360.0F
      }

      if (d < -180.0F) {
         d += 360.0F
      }

      return d
   }
}
