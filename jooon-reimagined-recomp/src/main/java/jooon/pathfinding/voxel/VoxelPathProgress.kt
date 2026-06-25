package jooon.pathfinding.voxel

import net.minecraft.util.math.Vec3d

private object VoxelPathProgress {
   fun advanceCursor(cursor: Int, waypoints: MutableList<Vec3d>, pos: Vec3d, isSky: Boolean, onGround: Boolean): Int {
      var nextCursor: Int = cursor


      while (nextCursor < waypoints.size()) {

         if (!this.withinReach(target, pos, isSky, yReach) && !this.crossedSegmentPlane(waypoints, nextCursor, target, pos)) {
break
         }

         nextCursor++
      }
return nextCursor
   }

   fun withinReach(target: Vec3d, pos: Vec3d, isSky: Boolean, yReach: Double): Boolean {

      if (isSky)
         Math.hypot(planarDist, pos.y - target.y) < 0.8
return else
         planarDist < 0.8 && Math.abs(pos.y - target.y) < yReach
      }

   fun crossedSegmentPlane(waypoints: MutableList<Vec3d>, index: Int, target: Vec3d, pos: Vec3d): Boolean {

      label34@
      if (var10000 == null) {
return false
      } else {

         segmentLen != 0.0
            && (pos.x - target.x) * (var10000.x / segmentLen)
                  + (pos.z - target.z) * (var10000.z / segmentLen)
               > -0.1
            && Math.abs(
                  (pos.x - target.x) * -(var10000.z / segmentLen)
                     + (pos.z - target.z) * (var10000.x / segmentLen)
               )
               < (segmentLen * 0.4).coerceIn(1.5, 4.0)
            }
   }

   fun segmentDirection(waypoints: MutableList<Vec3d>, index: Int, target: Vec3d): Vec3d {
      if (index < getLastIndex(waypoints)) {

         Vec3d(var5.x - target.x, 0.0, var5.z - target.z)
      } else if (index > 0) {

         Vec3d(target.x - prev.x, 0.0, target.z - prev.z)
      } else {
return null
      }
   }
}
