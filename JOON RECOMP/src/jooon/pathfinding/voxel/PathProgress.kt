package jooon.pathfinding.voxel

import net.minecraft.util.math.Vec3d

internal object PathProgress {
   fun advanceCursor(cursor: Int, waypoints: MutableList<Vec3d>, pos: Vec3d, isSky: Boolean, onGround: Boolean): Int {
      var nextCursor: Int = cursor
      val yReach: Double = if (isSky) 0.6 else 2.0

      while (nextCursor < waypoints.size()) {
         val target: Vec3d = waypoints.get(nextCursor) as Vec3d
         if (!this.withinReach(target, pos, isSky, yReach) && !this.crossedSegmentPlane(waypoints, nextCursor, target, pos)) {
            break
         }

         nextCursor++
      }

      if (!isSky && !onGround) {
         nextCursor = this.skipMissedWaypointsWhileFalling(nextCursor, waypoints, pos)
      }

      nextCursor
   }

   fun skipMissedWaypointsWhileFalling(cursor: Int, waypoints: MutableList<Vec3d>, pos: Vec3d): Int {
            nextCursor
   }

   fun withinReach(target: Vec3d, pos: Vec3d, isSky: Boolean, yReach: Double): Boolean {
      val planarDist: Double = Math.sqrt(
         (pos.field_1352 - target.field_1352) * (pos.field_1352 - target.field_1352)
            + (pos.field_1350 - target.field_1350) * (pos.field_1350 - target.field_1350)
      )
      if (isSky)
         Math.sqrt(planarDist * planarDist + (pos.field_1351 - target.field_1351) * (pos.field_1351 - target.field_1351)) < 0.8
         else
         planarDist < 0.8 && Math.abs(pos.field_1351 - target.field_1351) < yReach
      }

   fun crossedSegmentPlane(waypoints: MutableList<Vec3d>, index: Int, target: Vec3d, pos: Vec3d): Boolean {
      val var10000: Vec3d = this.segmentDirection(waypoints, index, target)
      label34@
      if (var10000 == null) {
         false
      } else {
         val segmentLen: Double = Math.sqrt(var10000.field_1352 * var10000.field_1352 + var10000.field_1350 * var10000.field_1350)
         segmentLen != 0.0
            && (pos.field_1352 - target.field_1352) * (var10000.field_1352 / segmentLen)
                  + (pos.field_1350 - target.field_1350) * (var10000.field_1350 / segmentLen)
               > -0.1
            && Math.abs(
                  (pos.field_1352 - target.field_1352) * -(var10000.field_1350 / segmentLen)
                     + (pos.field_1350 - target.field_1350) * (var10000.field_1352 / segmentLen)
               )
               < RangesKt.coerceIn(segmentLen * 0.4, 1.5, 4.0)
            }
   }

   fun segmentDirection(waypoints: MutableList<Vec3d>, index: Int, target: Vec3d): Vec3d {
      if (index < CollectionsKt.getLastIndex(waypoints)) {
         val var5: Vec3d = waypoints.get(index + 1) as Vec3d
         Vec3d(var5.field_1352 - target.field_1352, 0.0, var5.field_1350 - target.field_1350)
      } else if (index > 0) {
         val prev: Vec3d = waypoints.get(index - 1) as Vec3d
         Vec3d(target.field_1352 - prev.field_1352, 0.0, target.field_1350 - prev.field_1350)
      } else {
         null
      }
   }
}
