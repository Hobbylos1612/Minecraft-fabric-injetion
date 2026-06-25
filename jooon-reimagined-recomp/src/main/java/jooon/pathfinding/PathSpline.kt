package jooon.pathfinding

import java.util.ArrayList
import jooon.pathfinding.WalkingPathfinder.PathStep
import net.minecraft.util.math.Vec3d
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView

object PathSpline {
   private const val INTERPOLATION_STEP: Double = 0.4
   private const val PLAYER_EYE_OFFSET: Double = 1.62
   private const val MIN_LOOK_POINT_SPACING: Double = 0.8
   private const val OUTWARD_OFFSET_STRENGTH: Double = 1.2

   fun generateSpline(steps: List<PathStep>, tolerance: Double = 1.0): List<Vec3d> {
      if (steps.size() < 2) {
         val var23: java.lang.Iterable = steps
         val var26: java.util.Collection = ArrayList(steps.count().coerceAtLeast(10))

         for (var35 in var23) {
            var26.add((var35 as WalkingPathfinder.PathStep).node.center())
         }

         return var26 as MutableList<Vec3d>
      } else {
         val i: java.lang.Iterable = steps
         val last: java.util.Collection = ArrayList(steps.count().coerceAtLeast(10))

         for (dist in i) {
            last.add((dist as WalkingPathfinder.PathStep).node.center())
         }

         val raw: java.util.List = last as java.util.List

         var19.add(first(last as java.util.List))
         var var21: Int = 1

         for (var24 in getLastIndex(raw)..var21) {


            if ((steps.get(var21) as WalkingPathfinder.PathStep).moveType != WalkingPathfinder.MoveType.WALK
               || ((steps.get(var21) as WalkingPathfinder.PathStep).flags and 380) != 0
               || this.distance(var27, var30) > tolerance) {
               var19.add(var30)
            }
         }

         var19.add(last(raw))
         if (var19.size() < 2) {
            return raw
         } else {

            var ix: Int = 0

            for (var28 in getLastIndex(var19)..ix) {
               var var10000: Any = var19.get(ix)

               var10000 = var19.get(ix + 1)



               repeat(var40) { j ->
                  if (ix <= 0 || j != 0) {
                     var22.add(
                        Vec3d(
                           var31.x + (var34.x - var31.x) * (j.toDouble() / var40.toDouble()),
                           var31.y + (var34.y - var31.y) * (j.toDouble() / var40.toDouble()),
                           var31.z + (var34.z - var31.z) * (j.toDouble() / var40.toDouble())
                        )
                     )
                  }
               }
            }

            var22.add(last(var19))
            return var22
         }
      }
   }

   fun createLookPoints(smoothSplineData: List<Vec3d>, minInterval: Double = 0.25, maxInterval: Double = 4.5): List<Vec3d> {
      if (smoothSplineData.size() < 2) {
         return smoothSplineData
      } else {



         var lastPlacedRaw: Vec3d = start
         var lastForwardDir: Pair = null
         lookPoints.add(Vec3d(start.x, start.y + 1.62, start.z))
         var i: Int = 1

         for (var12 in getLastIndex(smoothSplineData)..i) {










            var curvature: Double = 0.0
            var offsetX: Double = 0.0
            var offsetZ: Double = 0.0
            if (m1 > 0.05 && m2 > 0.05) {
               curvature = Math.min(Math.acos(((v1x * v2x + v1z * v2z) / (m1 * m2)).coerceIn(-1.0, 1.0)) / (Math.PI * 2.0 / 5.0), 1.0)




               if (forwardMagnitude > 0.01) {
                  offsetX = -(forwardZ / forwardMagnitude) * currentForwardMagnitude * curvature * 1.2
                  offsetZ = previousForward / forwardMagnitude * currentForwardMagnitude * curvature * 1.2
               }
            }

            if (!(dist < maxInterval - curvature * (maxInterval - minInterval))) {



               if (lastForwardDir == null
                  || !(var53 > 0.1)
                  || !(dist < 12.0)
                  || !(
                     (
                              var51 * (lastForwardDir.getFirst() as java.lang.Number).doubleValue()
                                 + var52 * (lastForwardDir.getSecond() as java.lang.Number).doubleValue()
                           )
                           / var53
                        < 0.4
                  )) {
                  this.appendLookPoint(
                     lookPoints, this.adjustLookPoint(Vec3d(curr.x + offsetX, curr.y + 1.62, curr.z + offsetZ), curr)
                  )
                  lastPlacedRaw = curr
                  if (var53 > 0.1) {
                     lastForwardDir = Pair(var51 / var53, var52 / var53)
                  }
               }
return continue
            }
         }

         this.appendLookPoint(lookPoints, Vec3d(endPoint.x, endPoint.y + 1.62, endPoint.z))
         return lookPoints
      }
   }

   fun distance(a: Vec3d, b: Vec3d): Double {
      Math.hypot(Math.hypot(a.x - b.x, a.z - b.z), a.y - b.y)
   }

   fun appendLookPoint(points: MutableList<Vec3d>, point: Vec3d) {
      if (points.isEmpty()) {
         points.add(point)
      } else {

         if ((point.x - last.x) * (point.x - last.x)
               + (point.z - last.z) * (point.z - last.z)
            < 0.6400000000000001) {
            points.set(getLastIndex(points), point)
         } else {
            points.add(point)
         }
      }
   }

   fun adjustLookPoint(point: Vec3d, rawNode: Vec3d): Vec3d {
      if (!this.isPointInsideBlock(point)) {
return point
      } else {

         if (!this.isPointInsideBlock(unoffset)) {
return unoffset
         } else {

            if (this.isPointInsideBlock(lowered)) unoffset else lowered
         }
      }
   }

   fun isPointInsideBlock(point: Vec3d): Boolean {

      if (var10000 == null) {
return false
      } else {


         !var5.getCollisionShape(var10000 as BlockView, pos).isEmpty()
      }
   }

   private fun Double.toIntFloor(): Int {
      return Math.floor(this).toInt()
   }
}
