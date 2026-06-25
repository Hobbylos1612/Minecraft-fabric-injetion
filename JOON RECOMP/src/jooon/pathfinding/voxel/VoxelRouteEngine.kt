package jooon.pathfinding.voxel

import java.util.ArrayList
import jooon.pathfinding.voxel.solver.VoxelGroundSolver
import jooon.pathfinding.voxel.world.VoxelBlockCache
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.util.math.Vec3d

@SourceDebugExtension(["SMAP\nVoxelRouteEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VoxelRouteEngine.kt\njooon/pathfinding/voxel/VoxelRouteEngine\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n2333#2,14:62\n*S KotlinDebug\n*F\n+ 1 VoxelRouteEngine.kt\njooon/pathfinding/voxel/VoxelRouteEngine\n*L\n41#1:62,14\n*E\n"])
public object VoxelRouteEngine {
   fun planWalk(start: Vec3d, goal: Vec3d): VoxelRoutePlan {
      var var10000: Vec3d = this.snapToWalkableSurface(goal)
      if (var10000 == null) {
         var10000 = goal
      }

      val waypoints: java.util.List = VoxelGroundSolver.INSTANCE.solve(start, var10000)
      if (waypoints.size() < 2) VoxelRoutePlan.Failed.INSTANCE as VoxelRoutePlan else VoxelRoutePlan.Ground(waypoints) as VoxelRoutePlan
   }

   fun snapToWalkableSurface(target: Vec3d): Vec3d {
      val maxSearchY: Double = 8.0
      val maxLateralSpiral: Int = 4
      val centerX: Int = (int)Math.floor(target.field_1352)
      val centerZ: Int = (int)Math.floor(target.field_1350)
      var radius: Int = 0

      while (true) {
         for (var10 in if (radius == 0) CollectionsKt.listOf(TuplesKt.to(centerX, centerZ)) else this.ringCells(centerX, centerZ, radius)) {
            val cx: Int = (var10.component1() as java.lang.Number).intValue()
            val cz: Int = (var10.component2() as java.lang.Number).intValue()
            if (VoxelBlockCache.INSTANCE.isChunkLoaded(cx, cz)) {
               val `iterator$iv`: java.util.Iterator = VoxelBlockCache.INSTANCE
                  .getStandableSurfaces(cx, cz, target.field_1351 - maxSearchY, target.field_1351 + maxSearchY)
                  .iterator()
                  var var10000: VoxelBlockCache.StandSurface
               if (!`iterator$iv`.hasNext()) {
                  var10000 = null
               } else {
                  var `minElem$iv`: Any = `iterator$iv`.next()
                  if (!`iterator$iv`.hasNext()) {
                     var10000 = (VoxelBlockCache.StandSurface)`minElem$iv`
                  } else {
                     var var25: Double = Math.abs((`minElem$iv` as VoxelBlockCache.StandSurface).feetY - target.field_1351)

                     do {
                        val var26: Any = `iterator$iv`.next()
                        val var27: Double = Math.abs((var26 as VoxelBlockCache.StandSurface).feetY - target.field_1351)
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
            null
         }

         radius++
      }
   }

   private fun ringCells(cx: Int, cz: Int, r: Int): List<Pair<Int, Int>> {
      val out: ArrayList = ArrayList(8 * r)
      var dz: Int = -r
      if (-r <= r) {
         while (true) {
            out.add(TuplesKt.to(cx + dz, cz - r))
            out.add(TuplesKt.to(cx + dz, cz + r))
            if (dz == r) {
               break
            }

            dz++
         }
      }

      dz = -r + 1
      val var6: Int = r - 1
      if (dz <= r - 1) {
         while (true) {
            out.add(TuplesKt.to(cx - r, cz + dz))
            out.add(TuplesKt.to(cx + r, cz + dz))
            if (dz == var6) {
               break
            }

            dz++
         }
      }

      return out
   }
}
