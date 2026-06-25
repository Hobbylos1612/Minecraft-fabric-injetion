package jooon.pathfinding.voxel

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d

public object VoxelPathFollowMath {
   private const val PREDICT_BASE_TICKS: Double = 2.0
   private const val PREDICT_MAX_TICKS: Double = 6.0
   private const val PREDICT_SPEED_TICK_DIVISOR: Double = 100.0
   private const val PREDICT_MIN_DISTANCE: Double = 0.15
   private const val PREDICT_MAX_DISTANCE: Double = 2.6

   fun predictedGroundPos(pos: Vec3d, player: ClientPlayerEntity): Vec3d {
      this.predictedMovementPos(pos, player, false)
   }

   fun predictedMovementPos(pos: Vec3d, player: ClientPlayerEntity, includeVertical: Boolean): Vec3d {
      val var10000: Vec3d = player.method_18798()
      val horizontalSpeed: Double = Math.sqrt(var10000.field_1352 * var10000.field_1352 + var10000.field_1350 * var10000.field_1350)
      if (horizontalSpeed < 0.024999999999999998) {
         pos
      } else {
         val skyblockSpeed: Double = player.method_31549().method_7253() * 1000.0
         val ticksAhead: Double = RangesKt.coerceIn(2.0 + skyblockSpeed / 100.0, 2.0, 6.0)
         val projectedDistance: Double = horizontalSpeed * ticksAhead
         val maxDistance: Double = RangesKt.coerceIn(0.8 + skyblockSpeed / 250.0, 1.0, 2.6)
         val scale: Double = if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0
         Vec3d(
            pos.field_1352 + var10000.field_1352 * ticksAhead * (if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0),
            if (includeVertical)
               pos.field_1351 + var10000.field_1351 * ticksAhead * (if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0)
               else
               pos.field_1351,
            pos.field_1350 + var10000.field_1350 * ticksAhead * (if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0)
         )
      }
   }

   fun segmentDeviation(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d): VoxelPathFollowMath.SegmentDeviation? {
      if (idx > 0 && idx < waypoints.size()) this.segmentDeviationBetween(waypoints.get(idx - 1) as Vec3d, waypoints.get(idx) as Vec3d, pos) else null
   }

   fun routeDeviation(waypoints: MutableList<Vec3d>, cursor: Int, pos: Vec3d, behind: Int, ahead: Int): VoxelPathFollowMath.SegmentDeviation? {
      if (waypoints.size() < 2) {
         null
      } else {
         val first: Int = RangesKt.coerceAtLeast(cursor - behind, 1)
         val last: Int = RangesKt.coerceAtMost(cursor + ahead, CollectionsKt.getLastIndex(waypoints))
         var best: VoxelPathFollowMath.SegmentDeviation = null
         var bestScore: Double = java.lang.Double.MAX_VALUE
         var idx: Int = first
         if (first <= last) {
            while (true) {
               val var10000: VoxelPathFollowMath.SegmentDeviation = this.segmentDeviationBetween(
                  waypoints.get(idx - 1) as Vec3d, waypoints.get(idx) as Vec3d, pos
               )
               if (var10000 != null) {
                  val score: Double = var10000.lateral + Math.abs(var10000.verticalBelow) * 0.25
                  if (score < bestScore) {
                     bestScore = score
                     best = var10000
                  }
               }

               if (idx == last) {
                  break
               }

               idx++
            }
         }

         best
      }
   }

   fun segmentDeviationBetween(prev: Vec3d, target: Vec3d, pos: Vec3d): VoxelPathFollowMath.SegmentDeviation {
      val segDx: Double = target.field_1352 - prev.field_1352
      val segDz: Double = target.field_1350 - prev.field_1350
      val segLen: Double = Math.sqrt(segDx * segDx + (target.field_1350 - prev.field_1350) * (target.field_1350 - prev.field_1350))
      if (segLen < 0.01) {
         null
      } else {
         val dirX: Double = segDx / segLen
         val dirZ: Double = segDz / segLen
         val relX: Double = pos.field_1352 - prev.field_1352
         VoxelPathFollowMath.SegmentDeviation(
            Math.abs((pos.field_1352 - prev.field_1352) * -dirZ + (pos.field_1350 - prev.field_1350) * dirX),
            prev.field_1351
               + (target.field_1351 - prev.field_1351) * (RangesKt.coerceIn(relX * dirX + (pos.field_1350 - prev.field_1350) * dirZ, 0.0, segLen) / segLen)
               - pos.field_1351,
            target.field_1351 - prev.field_1351
         )
      }
   }

   fun groundSegmentFrame(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d): VoxelPathFollowMath.GroundSegmentFrame {
      val target: Vec3d = waypoints.get(idx) as Vec3d
      label16@
      if (idx <= 0) {
         this.directFrame(pos, target, target.field_1351 - pos.field_1351)
      } else {
         val prev: Vec3d = waypoints.get(idx - 1) as Vec3d
         val segDx: Double = target.field_1352 - prev.field_1352
         val segDz: Double = target.field_1350 - prev.field_1350
         val segLen: Double = Math.sqrt(segDx * segDx + (target.field_1350 - prev.field_1350) * (target.field_1350 - prev.field_1350))
         if (segLen < 0.01)
            this.directFrame(pos, target, target.field_1351 - prev.field_1351)
            else
            VoxelPathFollowMath.GroundSegmentFrame(
               segDx / segLen,
               segDz / segLen,
               -(segDz / segLen),
               segDx / segLen,
               (pos.field_1352 - prev.field_1352) * -(segDz / segLen) + (pos.field_1350 - prev.field_1350) * (segDx / segLen),
               segLen
                  - RangesKt.coerceIn(
                     (pos.field_1352 - prev.field_1352) * (segDx / segLen) + (pos.field_1350 - prev.field_1350) * (segDz / segLen), 0.0, segLen
                  ),
               target.field_1351 - prev.field_1351
            )
         }
   }

   fun correctedDirection(frame: VoxelPathFollowMath.GroundSegmentFrame, player: ClientPlayerEntity): Pair<java.lang.Double, java.lang.Double> {
      val lookAhead: Double = RangesKt.coerceIn(0.85 + (double)player.method_31549().method_7253() * 1000.0 / 210.0, 0.85, 2.2)
      val correction: Double = RangesKt.coerceIn(-frame.lateral * 1.35, -1.0, 1.0)
      val x: Double = frame.dirX * lookAhead + frame.perpX * correction
      val z: Double = frame.dirZ * lookAhead + frame.perpZ * correction
      val len: Double = Math.sqrt(x * x + z * z)
      if (len < 0.01) TuplesKt.to(frame.dirX, frame.dirZ) else TuplesKt.to(x / len, z / len)
   }

   fun centered(vec: Vec3d): Vec3d {
      Vec3d(Math.floor(vec.field_1352) + 0.5, vec.field_1351, Math.floor(vec.field_1350) + 0.5)
   }

   fun pathLookaheadTarget(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d, player: ClientPlayerEntity): Vec3d {
      if (waypoints.isEmpty()) {
         pos
      } else if (idx <= 0) {
         this.centered(waypoints.get(RangesKt.coerceIn(idx, 0, CollectionsKt.getLastIndex(waypoints))) as Vec3d)
      } else {
         var remainingLookahead: Double = RangesKt.coerceIn(1.15 + (double)player.method_31549().method_7253() * 1000.0 / 135.0, 1.15, 3.2)
         var segmentStart: Vec3d = this.centered(waypoints.get(idx - 1) as Vec3d)
         var segmentEnd: Vec3d = this.centered(waypoints.get(idx) as Vec3d)
         val currentProjection: Vec3d = this.projectOntoSegment(segmentStart, segmentEnd, pos)
         val distanceToSegmentEnd: Double = currentProjection.method_1022(segmentEnd)
         if (!(remainingLookahead <= distanceToSegmentEnd) && idx < CollectionsKt.getLastIndex(waypoints)) {
            remainingLookahead = remainingLookahead - distanceToSegmentEnd
            var nextIndex: Int = idx + 1
            segmentStart = segmentEnd

            while (nextIndex <= CollectionsKt.getLastIndex(waypoints)) {
               segmentEnd = this.centered(waypoints.get(nextIndex) as Vec3d)
               val segmentLength: Double = segmentStart.method_1022(segmentEnd)
               if (segmentLength >= remainingLookahead || nextIndex == CollectionsKt.getLastIndex(waypoints)) {
                  this.moveAlongSegment(segmentStart, segmentEnd, remainingLookahead)
               }

               remainingLookahead -= segmentLength
               segmentStart = segmentEnd
               nextIndex++
            }

            this.centered(CollectionsKt.last(waypoints) as Vec3d)
         } else {
            this.moveAlongSegment(currentProjection, segmentEnd, remainingLookahead)
         }
      }
   }

   fun directFrame(pos: Vec3d, target: Vec3d, dy: Double): VoxelPathFollowMath.GroundSegmentFrame {
      val dx: Double = target.field_1352 - pos.field_1352
      val dz: Double = target.field_1350 - pos.field_1350
      val dist: Double = Math.sqrt(dx * dx + (target.field_1350 - pos.field_1350) * (target.field_1350 - pos.field_1350))
      if (dist < 0.01)
         VoxelPathFollowMath.GroundSegmentFrame(0.0, 1.0, -1.0, 0.0, 0.0, dist, dy)
         else
         VoxelPathFollowMath.GroundSegmentFrame(dx / dist, dz / dist, -(dz / dist), dx / dist, 0.0, dist, dy)
      }

   fun projectOntoSegment(start: Vec3d, end: Vec3d, pos: Vec3d): Vec3d {
      val dx: Double = end.field_1352 - start.field_1352
      val dz: Double = end.field_1350 - start.field_1350
      val lenSq: Double = dx * dx + (end.field_1350 - start.field_1350) * (end.field_1350 - start.field_1350)
      if (dx * dx + (end.field_1350 - start.field_1350) * (end.field_1350 - start.field_1350) < 1.0E-4) {
         end
      } else {
         val t: Double = RangesKt.coerceIn(((pos.field_1352 - start.field_1352) * dx + (pos.field_1350 - start.field_1350) * dz) / lenSq, 0.0, 1.0)
         Vec3d(start.field_1352 + dx * t, start.field_1351 + (end.field_1351 - start.field_1351) * t, start.field_1350 + dz * t)
      }
   }

   fun moveAlongSegment(start: Vec3d, end: Vec3d, distance: Double): Vec3d {
      val dx: Double = end.field_1352 - start.field_1352
      val dy: Double = end.field_1351 - start.field_1351
      val dz: Double = end.field_1350 - start.field_1350
      val len: Double = Math.sqrt(dx * dx + (end.field_1350 - start.field_1350) * (end.field_1350 - start.field_1350))
      if (len < 0.01) {
         end
      } else {
         val t: Double = RangesKt.coerceIn(distance / len, 0.0, 1.0)
         Vec3d(start.field_1352 + dx * t, start.field_1351 + dy * t, start.field_1350 + dz * t)
      }
   }

   public data class GroundSegmentFrame(dirX: Double, dirZ: Double, perpX: Double, perpZ: Double, lateral: Double, distanceToTarget: Double, segmentDy: Double) {
      public final val dirX: Double
      public final val dirZ: Double
      public final val perpX: Double
      public final val perpZ: Double
      public final val lateral: Double
      public final val distanceToTarget: Double
      public final val segmentDy: Double

      init {
         this.dirX = dirX
         this.dirZ = dirZ
         this.perpX = perpX
         this.perpZ = perpZ
         this.lateral = lateral
         this.distanceToTarget = distanceToTarget
         this.segmentDy = segmentDy
      }

      public operator fun component1(): Double {
         return this.dirX
      }

      public operator fun component2(): Double {
         return this.dirZ
      }

      public operator fun component3(): Double {
         return this.perpX
      }

      public operator fun component4(): Double {
         return this.perpZ
      }

      public operator fun component5(): Double {
         return this.lateral
      }

      public operator fun component6(): Double {
         return this.distanceToTarget
      }

      public operator fun component7(): Double {
         return this.segmentDy
      }

      public fun copy(
         dirX: Double = this.dirX,
         dirZ: Double = this.dirZ,
         perpX: Double = this.perpX,
         perpZ: Double = this.perpZ,
         lateral: Double = this.lateral,
         distanceToTarget: Double = this.distanceToTarget,
         segmentDy: Double = this.segmentDy
      ): jooon.pathfinding.voxel.VoxelPathFollowMath.GroundSegmentFrame {
         return VoxelPathFollowMath.GroundSegmentFrame(dirX, dirZ, perpX, perpZ, lateral, distanceToTarget, segmentDy)
      }

      public override fun toString(): String {
         return "GroundSegmentFrame(dirX=${this.dirX}, dirZ=${this.dirZ}, perpX=${this.perpX}, perpZ=${this.perpZ}, lateral=${this.lateral}, distanceToTarget=${this.distanceToTarget}, segmentDy=${this.segmentDy})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (java.lang.Double.hashCode(this.dirX) * 31 + java.lang.Double.hashCode(this.dirZ)) * 31
                                                + java.lang.Double.hashCode(this.perpX)
                                          )
                                          * 31
                                       + java.lang.Double.hashCode(this.perpZ)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.lateral)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.distanceToTarget)
               )
               * 31
            + java.lang.Double.hashCode(this.segmentDy)
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is VoxelPathFollowMath.GroundSegmentFrame
               && java.lang.Double.compare(this.dirX, (other as VoxelPathFollowMath.GroundSegmentFrame).dirX) == 0
               && java.lang.Double.compare(this.dirZ, (other as VoxelPathFollowMath.GroundSegmentFrame).dirZ) == 0
               && java.lang.Double.compare(this.perpX, (other as VoxelPathFollowMath.GroundSegmentFrame).perpX) == 0
               && java.lang.Double.compare(this.perpZ, (other as VoxelPathFollowMath.GroundSegmentFrame).perpZ) == 0
               && java.lang.Double.compare(this.lateral, (other as VoxelPathFollowMath.GroundSegmentFrame).lateral) == 0
               && java.lang.Double.compare(this.distanceToTarget, (other as VoxelPathFollowMath.GroundSegmentFrame).distanceToTarget) == 0
               && java.lang.Double.compare(this.segmentDy, (other as VoxelPathFollowMath.GroundSegmentFrame).segmentDy) == 0
            }
      }
   }

   public data class SegmentDeviation(lateral: Double, verticalBelow: Double, segmentDy: Double) {
      public final val lateral: Double
      public final val verticalBelow: Double
      public final val segmentDy: Double

      init {
         this.lateral = lateral
         this.verticalBelow = verticalBelow
         this.segmentDy = segmentDy
      }

      public operator fun component1(): Double {
         return this.lateral
      }

      public operator fun component2(): Double {
         return this.verticalBelow
      }

      public operator fun component3(): Double {
         return this.segmentDy
      }

      public fun copy(lateral: Double = this.lateral, verticalBelow: Double = this.verticalBelow, segmentDy: Double = this.segmentDy): jooon.pathfinding.voxel.VoxelPathFollowMath.SegmentDeviation {
         return VoxelPathFollowMath.SegmentDeviation(lateral, verticalBelow, segmentDy)
      }

      public override fun toString(): String {
         return "SegmentDeviation(lateral=${this.lateral}, verticalBelow=${this.verticalBelow}, segmentDy=${this.segmentDy})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Double.hashCode(this.lateral) * 31 + java.lang.Double.hashCode(this.verticalBelow)) * 31 + java.lang.Double.hashCode(this.segmentDy)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VoxelPathFollowMath.SegmentDeviation
               && java.lang.Double.compare(this.lateral, (other as VoxelPathFollowMath.SegmentDeviation).lateral) == 0
               && java.lang.Double.compare(this.verticalBelow, (other as VoxelPathFollowMath.SegmentDeviation).verticalBelow) == 0
               && java.lang.Double.compare(this.segmentDy, (other as VoxelPathFollowMath.SegmentDeviation).segmentDy) == 0
            }
      }
   }
}
