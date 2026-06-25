package jooon.pathfinding.voxel

public object VoxelPathAngles {
   public fun calcAimAnglesFromDelta(dx: Double, dy: Double, dz: Double): Pair<Float, Float> {
      return TuplesKt.to((float)Math.toDegrees(Math.atan2(-dx, dz)), (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))))
   }

   public fun wrapDegrees(delta: Float): Float {
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
