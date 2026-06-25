package jooon.pathfinding.voxel

import java.util.ArrayList
import jooon.pathfinding.voxel.solver.VoxelGroundSolver
import jooon.pathfinding.voxel.world.VoxelBlockCache
import net.minecraft.util.math.Vec3d

object VoxelRouteEngine {
   fun planWalk(start: Vec3d, goal: Vec3d): VoxelRoutePlan {
      var var10000: Vec3d = this.snapToWalkableSurface(goal)
      if (var10000 == null) {
         var10000 = goal
      }

      val waypoints: java.util.List = VoxelGroundSolver.solve(start, var10000)
      if (waypoints.size() < 2) VoxelRoutePlan.Failed.INSTANCE as VoxelRoutePlan else VoxelRoutePlan.Ground(waypoints) as VoxelRoutePlan
   }

   fun snapToWalkableSurface(target: Vec3d): Vec3d {




      var radius: Int = 0

      while (true) {
         for (var10 in if (radius == 0) listOf(Pair(centerX, centerZ)) else this.ringCells(centerX, centerZ, radius)) {


            if (VoxelBlockCache.isChunkLoaded(cx, cz)) {
               val `iterator$iv`: java.util.Iterator = VoxelBlockCache.INSTANCE
                  .getStandableSurfaces(cx, cz, target.y - maxSearchY, target.y + maxSearchY)
                  .iterator()
                  var var10000: VoxelBlockCache.StandSurface
               if (!`iterator$iv`.hasNext()) {
                  var10000 = null
               } else {
                  var `minElem$iv`: Any = `iterator$iv`.next()
                  if (!`iterator$iv`.hasNext()) {
                     var10000 = (VoxelBlockCache.StandSurface)`minElem$iv`
                  } else {
                     var var25: Double = Math.abs((`minElem$iv` as VoxelBlockCache.StandSurface).feetY - target.y)

                     do {


                        if (java.lang.Double.compare(var25, var27) > 0) {
                           `minElem$iv` = var26
                           var25 = var27
                        }
                     } while (`iterator$iv`.hasNext())

                     var10000 = (VoxelBlockCache.StandSurface)`minElem$iv`
                  }
               }

               var10000 = var10000
               if (var10000 != null) {
                  Vec3d(cx + 0.5, var10000.feetY, cz + 0.5)
               }
            }
         }

         if (radius == maxLateralSpiral) {
return null
         }

         radius++
      }
   }

   private fun ringCells(cx: Int, cz: Int, r: Int): List<Pair<Int, Int>> {

      var dz: Int = -r
      if (-r <= r) {
         while (true) {
            out.add(Pair(cx + dz, cz - r))
            out.add(Pair(cx + dz, cz + r))
            if (dz == r) {
break
            }

            dz++
         }
      }

      dz = -r + 1

      if (dz <= r - 1) {
         while (true) {
            out.add(Pair(cx - r, cz + dz))
            out.add(Pair(cx + r, cz + dz))
            if (dz == var6) {
break
            }

            dz++
         }
      }

      return out
   }
}
