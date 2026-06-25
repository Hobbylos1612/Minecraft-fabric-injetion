package jooon.pathfinding.voxel

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d

object VoxelPathFollowMath {
   private const val PREDICT_BASE_TICKS: Double = 2.0
   private const val PREDICT_MAX_TICKS: Double = 6.0
   private const val PREDICT_SPEED_TICK_DIVISOR: Double = 100.0
   private const val PREDICT_MIN_DISTANCE: Double = 0.15
   private const val PREDICT_MAX_DISTANCE: Double = 2.6

   fun predictedGroundPos(pos: Vec3d, player: ClientPlayerEntity): Vec3d {
      this.predictedMovementPos(pos, player, false)
   }

   fun predictedMovementPos(pos: Vec3d, player: ClientPlayerEntity, includeVertical: Boolean): Vec3d {


      if (horizontalSpeed < 0.024999999999999998) {
return pos
      } else {





         Vec3d(
            pos.x + var10000.x * ticksAhead * (if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0),
            if (includeVertical)
               pos.y + var10000.y * ticksAhead * (if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0)
return else
               pos.y,
            pos.z + var10000.z * ticksAhead * (if (projectedDistance > maxDistance) maxDistance / projectedDistance else 1.0)
         )
      }
   }

   fun segmentDeviation(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d): VoxelPathFollowMath.SegmentDeviation? {
      if (idx > 0 && idx < waypoints.size()) this.segmentDeviationBetween(waypoints.get(idx - 1) as Vec3d, waypoints.get(idx) as Vec3d, pos) else null
   }

   fun routeDeviation(waypoints: MutableList<Vec3d>, cursor: Int, pos: Vec3d, behind: Int, ahead: Int): VoxelPathFollowMath.SegmentDeviation? {
      if (waypoints.size() < 2) {
return null
      } else {


         var best: VoxelPathFollowMath.SegmentDeviation = null
         var bestScore: Double = java.lang.Double.MAX_VALUE
         var idx: Int = first
         if (first <= last) {
            while (true) {
               val var10000: VoxelPathFollowMath.SegmentDeviation = this.segmentDeviationBetween(
                  waypoints.get(idx - 1) as Vec3d, waypoints.get(idx) as Vec3d, pos
               )
               if (var10000 != null) {

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
return best
      }
   }

   fun segmentDeviationBetween(prev: Vec3d, target: Vec3d, pos: Vec3d): VoxelPathFollowMath.SegmentDeviation {



      if (segLen < 0.01) {
return null
      } else {



         VoxelPathFollowMath.SegmentDeviation(
            Math.abs((pos.x - prev.x) * -dirZ + (pos.z - prev.z) * dirX),
            prev.y
               + (target.y - prev.y) * ((relX * dirX + (pos.z - prev.z) * dirZ).coerceIn(0.0, segLen) / segLen)
               - pos.y,
            target.y - prev.y
         )
      }
   }

   fun groundSegmentFrame(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d): VoxelPathFollowMath.GroundSegmentFrame {

      label16@
      if (idx <= 0) {
         this.directFrame(pos, target, target.y - pos.y)
      } else {




         if (segLen < 0.01)
            this.directFrame(pos, target, target.y - prev.y)
return else
            VoxelPathFollowMath.GroundSegmentFrame(
               segDx / segLen,
               segDz / segLen,
               -(segDz / segLen),
               segDx / segLen,
               (pos.x - prev.x) * -(segDz / segLen) + (pos.z - prev.z) * (segDx / segLen),
return segLen
                  - ((pos.x - prev.x) * (segDx / segLen) + (pos.z - prev.z) * (segDz / segLen)).coerceIn(0.0, segLen),
               target.y - prev.y
            )
         }
   }

   fun correctedDirection(frame: VoxelPathFollowMath.GroundSegmentFrame, player: ClientPlayerEntity): Pair<Double, Double> {





      if (len < 0.01) Pair(frame.dirX, frame.dirZ) else Pair(x / len, z / len)
   }

   fun centered(vec: Vec3d): Vec3d {
      Vec3d(Math.floor(vec.x) + 0.5, vec.y, Math.floor(vec.z) + 0.5)
   }

   fun pathLookaheadTarget(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d, player: ClientPlayerEntity): Vec3d {
      if (waypoints.isEmpty()) {
return pos
      } else if (idx <= 0) {
         this.centered(waypoints.get((idx).coerceIn(0, getLastIndex(waypoints))) as Vec3d)
      } else {
         var remainingLookahead: Double = (1.15 + player.getAbilities().getWalkSpeed().toDouble() * 1000.0 / 135.0).coerceIn(1.15, 3.2)
         var segmentStart: Vec3d = this.centered(waypoints.get(idx - 1) as Vec3d)
         var segmentEnd: Vec3d = this.centered(waypoints.get(idx) as Vec3d)


         if (!(remainingLookahead <= distanceToSegmentEnd) && idx < getLastIndex(waypoints)) {
            remainingLookahead = remainingLookahead - distanceToSegmentEnd
            var nextIndex: Int = idx + 1
            segmentStart = segmentEnd

            while (nextIndex <= getLastIndex(waypoints)) {
               segmentEnd = this.centered(waypoints.get(nextIndex) as Vec3d)

               if (segmentLength >= remainingLookahead || nextIndex == getLastIndex(waypoints)) {
                  this.moveAlongSegment(segmentStart, segmentEnd, remainingLookahead)
               }

               remainingLookahead -= segmentLength
               segmentStart = segmentEnd
               nextIndex++
            }

            this.centered(last(waypoints) as Vec3d)
         } else {
            this.moveAlongSegment(currentProjection, segmentEnd, remainingLookahead)
         }
      }
   }

   fun directFrame(pos: Vec3d, target: Vec3d, dy: Double): VoxelPathFollowMath.GroundSegmentFrame {



      if (dist < 0.01)
         VoxelPathFollowMath.GroundSegmentFrame(0.0, 1.0, -1.0, 0.0, 0.0, dist, dy)
return else
         VoxelPathFollowMath.GroundSegmentFrame(dx / dist, dz / dist, -(dz / dist), dx / dist, 0.0, dist, dy)
      }

   fun projectOntoSegment(start: Vec3d, end: Vec3d, pos: Vec3d): Vec3d {



      if (dx * dx + (end.z - start.z) * (end.z - start.z) < 1.0E-4) {
return end
      } else {

         Vec3d(start.x + dx * t, start.y + (end.y - start.y) * t, start.z + dz * t)
      }
   }

   fun moveAlongSegment(start: Vec3d, end: Vec3d, distance: Double): Vec3d {




      if (len < 0.01) {
return end
      } else {

         Vec3d(start.x + dx * t, start.y + dy * t, start.z + dz * t)
      }
   }

   data class GroundSegmentFrame(dirX: Double, dirZ: Double, perpX: Double, perpZ: Double, lateral: Double, distanceToTarget: Double, segmentDy: Double) {
      val dirX: Double
      val dirZ: Double
      val perpX: Double
      val perpZ: Double
      val lateral: Double
      val distanceToTarget: Double
      val segmentDy: Double

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

      fun copy(
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

      override fun toString(): String {
         return "GroundSegmentFrame(dirX=${this.dirX}, dirZ=${this.dirZ}, perpX=${this.perpX}, perpZ=${this.perpZ}, lateral=${this.lateral}, distanceToTarget=${this.distanceToTarget}, segmentDy=${this.segmentDy})"
      }

      override fun hashCode(): Int {
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

      override operator fun equals(other: Any?): Boolean {
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

   data class SegmentDeviation(lateral: Double, verticalBelow: Double, segmentDy: Double) {
      val lateral: Double
      val verticalBelow: Double
      val segmentDy: Double

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

      fun copy(lateral: Double = this.lateral, verticalBelow: Double = this.verticalBelow, segmentDy: Double = this.segmentDy): jooon.pathfinding.voxel.VoxelPathFollowMath.SegmentDeviation {
         return VoxelPathFollowMath.SegmentDeviation(lateral, verticalBelow, segmentDy)
      }

      override fun toString(): String {
         return "SegmentDeviation(lateral=${this.lateral}, verticalBelow=${this.verticalBelow}, segmentDy=${this.segmentDy})"
      }

      override fun hashCode(): Int {
         return (java.lang.Double.hashCode(this.lateral) * 31 + java.lang.Double.hashCode(this.verticalBelow)) * 31 + java.lang.Double.hashCode(this.segmentDy)
      }

      override operator fun equals(other: Any?): Boolean {
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
