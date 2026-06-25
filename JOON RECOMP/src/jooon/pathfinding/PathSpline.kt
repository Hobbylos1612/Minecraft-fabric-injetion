package jooon.pathfinding

import java.util.ArrayList
import jooon.pathfinding.WalkingPathfinder.PathStep
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_243
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView

@SourceDebugExtension(["SMAP\nPathSpline.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathSpline.kt\njooon/pathfinding/PathSpline\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,169:1\n1549#2:170\n1620#2,3:171\n1549#2:174\n1620#2,3:175\n*S KotlinDebug\n*F\n+ 1 PathSpline.kt\njooon/pathfinding/PathSpline\n*L\n18#1:170\n18#1:171,3\n20#1:174\n20#1:175,3\n*E\n"])
public object PathSpline {
   private const val INTERPOLATION_STEP: Double = 0.4
   private const val PLAYER_EYE_OFFSET: Double = 1.62
   private const val MIN_LOOK_POINT_SPACING: Double = 0.8
   private const val OUTWARD_OFFSET_STRENGTH: Double = 1.2

   public fun generateSpline(steps: List<PathStep>, tolerance: Double = 1.0): List<class_243> {
      if (steps.size() < 2) {
         val var23: java.lang.Iterable = steps
         val var26: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(steps, 10))

         for (var35 in var23) {
            var26.add((var35 as WalkingPathfinder.PathStep).node.center())
         }

         return var26 as MutableList<Vec3d>
      } else {
         val i: java.lang.Iterable = steps
         val last: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(steps, 10))

         for (dist in i) {
            last.add((dist as WalkingPathfinder.PathStep).node.center())
         }

         val raw: java.util.List = last as java.util.List
         val var19: ArrayList = ArrayList((last as java.util.List).size())
         var19.add(CollectionsKt.first(last as java.util.List))
         var var21: Int = 1

         for (var24 in CollectionsKt.getLastIndex(raw)..var21) {
            val var27: Vec3d = CollectionsKt.last(var19) as Vec3d
            val var30: Vec3d = raw.get(var21) as Vec3d
            if ((steps.get(var21) as WalkingPathfinder.PathStep).moveType != WalkingPathfinder.MoveType.WALK
               || ((steps.get(var21) as WalkingPathfinder.PathStep).flags and 380) != 0
               || this.distance(var27, var30) > tolerance) {
               var19.add(var30)
            }
         }

         var19.add(CollectionsKt.last(raw))
         if (var19.size() < 2) {
            return raw
         } else {
            val var22: ArrayList = ArrayList(var19.size() * 3)
            var ix: Int = 0

            for (var28 in CollectionsKt.getLastIndex(var19)..ix) {
               var var10000: Any = var19.get(ix)
               val var31: Vec3d = var10000 as Vec3d
               var10000 = var19.get(ix + 1)
               val var34: Vec3d = var10000 as Vec3d
               val var40: Int = RangesKt.coerceAtLeast((int)Math.ceil(this.distance(var31, var10000 as Vec3d) / 0.4), 1)

               repeat(var40) { j ->
                  if (ix <= 0 || j != 0) {
                     var22.add(
                        Vec3d(
                           var31.field_1352 + (var34.field_1352 - var31.field_1352) * ((double)j / (double)var40),
                           var31.field_1351 + (var34.field_1351 - var31.field_1351) * ((double)j / (double)var40),
                           var31.field_1350 + (var34.field_1350 - var31.field_1350) * ((double)j / (double)var40)
                        )
                     )
                  }
               }
            }

            var22.add(CollectionsKt.last(var19))
            return var22
         }
      }
   }

   public fun createLookPoints(smoothSplineData: List<class_243>, minInterval: Double = 0.25, maxInterval: Double = 4.5): List<class_243> {
      if (smoothSplineData.size() < 2) {
         return smoothSplineData
      } else {
         val start: Vec3d = CollectionsKt.first(smoothSplineData) as Vec3d
         val endPoint: Vec3d = CollectionsKt.last(smoothSplineData) as Vec3d
         val lookPoints: ArrayList = ArrayList()
         var lastPlacedRaw: Vec3d = start
         var lastForwardDir: Pair = null
         lookPoints.add(Vec3d(start.field_1352, start.field_1351 + 1.62, start.field_1350))
         var i: Int = 1

         for (var12 in CollectionsKt.getLastIndex(smoothSplineData)..i) {
            val curr: Vec3d = smoothSplineData.get(i) as Vec3d
            val dist: Double = this.distance(curr, lastPlacedRaw)
            val prev: Vec3d = smoothSplineData.get(Math.max(0, i - 4)) as Vec3d
            val next: Vec3d = smoothSplineData.get(Math.min(CollectionsKt.getLastIndex(smoothSplineData), i + 4)) as Vec3d
            val v1x: Double = curr.field_1352 - prev.field_1352
            val v1z: Double = curr.field_1350 - prev.field_1350
            val v2x: Double = next.field_1352 - curr.field_1352
            val v2z: Double = next.field_1350 - curr.field_1350
            val m1: Double = Math.hypot(v1x, v1z)
            val m2: Double = Math.hypot(v2x, v2z)
            var curvature: Double = 0.0
            var offsetX: Double = 0.0
            var offsetZ: Double = 0.0
            if (m1 > 0.05 && m2 > 0.05) {
               curvature = Math.min(Math.acos(RangesKt.coerceIn((v1x * v2x + v1z * v2z) / (m1 * m2), -1.0, 1.0)) / (Math.PI * 2.0 / 5.0), 1.0)
               val currentForwardMagnitude: Double = if (v1x * v2z - v1z * v2x > 0.0) 1.0 else -1.0
               val previousForward: Double = v1x / m1 + v2x / m2
               val forwardZ: Double = v1z / m1 + v2z / m2
               val forwardMagnitude: Double = Math.hypot(previousForward, v1z / m1 + v2z / m2)
               if (forwardMagnitude > 0.01) {
                  offsetX = -(forwardZ / forwardMagnitude) * currentForwardMagnitude * curvature * 1.2
                  offsetZ = previousForward / forwardMagnitude * currentForwardMagnitude * curvature * 1.2
               }
            }

            if (!(dist < maxInterval - curvature * (maxInterval - minInterval))) {
               val var51: Double = curr.field_1352 - lastPlacedRaw.field_1352
               val var52: Double = curr.field_1350 - lastPlacedRaw.field_1350
               val var53: Double = Math.hypot(var51, curr.field_1350 - lastPlacedRaw.field_1350)
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
                     lookPoints, this.adjustLookPoint(Vec3d(curr.field_1352 + offsetX, curr.field_1351 + 1.62, curr.field_1350 + offsetZ), curr)
                  )
                  lastPlacedRaw = curr
                  if (var53 > 0.1) {
                     lastForwardDir = TuplesKt.to(var51 / var53, var52 / var53)
                  }
               }
               continue
            }
         }

         this.appendLookPoint(lookPoints, Vec3d(endPoint.field_1352, endPoint.field_1351 + 1.62, endPoint.field_1350))
         return lookPoints
      }
   }

   fun distance(a: Vec3d, b: Vec3d): Double {
      Math.hypot(Math.hypot(a.field_1352 - b.field_1352, a.field_1350 - b.field_1350), a.field_1351 - b.field_1351)
   }

   fun appendLookPoint(points: MutableList<Vec3d>, point: Vec3d) {
      if (points.isEmpty()) {
         points.add(point)
      } else {
         val last: Vec3d = CollectionsKt.last(points) as Vec3d
         if ((point.field_1352 - last.field_1352) * (point.field_1352 - last.field_1352)
               + (point.field_1350 - last.field_1350) * (point.field_1350 - last.field_1350)
            < 0.6400000000000001) {
            points.set(CollectionsKt.getLastIndex(points), point)
         } else {
            points.add(point)
         }
      }
   }

   fun adjustLookPoint(point: Vec3d, rawNode: Vec3d): Vec3d {
      if (!this.isPointInsideBlock(point)) {
         point
      } else {
         val unoffset: Vec3d = Vec3d(rawNode.field_1352, point.field_1351, rawNode.field_1350)
         if (!this.isPointInsideBlock(unoffset)) {
            unoffset
         } else {
            val lowered: Vec3d = Vec3d(rawNode.field_1352, point.field_1351 - 0.5, rawNode.field_1350)
            if (this.isPointInsideBlock(lowered)) unoffset else lowered
         }
      }
   }

   fun isPointInsideBlock(point: Vec3d): Boolean {
      val var10000: ClientWorld = MinecraftClient.method_1551().field_1687
      if (var10000 == null) {
         false
      } else {
         val pos: BlockPos = BlockPos(this.toIntFloor(point.field_1352), this.toIntFloor(point.field_1351), this.toIntFloor(point.field_1350))
         val var5: BlockState = var10000.method_8320(pos)
         !var5.method_26220(var10000 as BlockView, pos).method_1110()
      }
   }

   private fun Double.toIntFloor(): Int {
      return (int)Math.floor(`$this$toIntFloor`)
   }
}
