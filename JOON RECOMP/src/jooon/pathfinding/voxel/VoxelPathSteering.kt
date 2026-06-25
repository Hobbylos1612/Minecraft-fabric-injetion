package jooon.pathfinding.voxel

import jooon.pathfinding.voxel.world.VoxelBlockCache
import net.minecraft.class_243
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.BlockPos.Mutable
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.shape.VoxelShape

internal class VoxelPathSteering {
   private final var desiredYaw: Float = java.lang.Float.NaN
   private final var desiredPitch: Float = java.lang.Float.NaN

   public fun reset() {
      this.desiredYaw = java.lang.Float.NaN
      this.desiredPitch = java.lang.Float.NaN
   }

   fun steerSky(target: Vec3d, pos: Vec3d, player: ClientPlayerEntity) {
      val dx: Double = target.field_1352 - pos.field_1352
      val dz: Double = target.field_1350 - pos.field_1350
      val dy: Double = target.field_1351 - pos.field_1351
      val horizontalDist: Double = Math.sqrt(dx * dx + dz * dz)
      if (horizontalDist > 0.05) {
         val yawError: Pair = VoxelPathAngles.INSTANCE.calcAimAnglesFromDelta(dx, dy, dz)
         this.desiredYaw = (yawError.getFirst() as java.lang.Number).floatValue()
         this.desiredPitch = (yawError.getSecond() as java.lang.Number).floatValue()
      } else if (!player.method_24828() && dy < -1.0) {
         this.desiredPitch = 60.0F
      }

      VoxelPathInput.INSTANCE.releaseAll()
      if (this.isAheadHeadBlocked(pos, dx, dz, horizontalDist)) {
         VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.SNEAK)
         VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
      } else {
         if ((if (java.lang.Float.isNaN(this.desiredYaw)) 0.0F else Math.abs(VoxelPathAngles.INSTANCE.wrapDegrees(this.desiredYaw - player.method_36454())))
               < 25.0F
            || horizontalDist < 0.5) {
            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
         }

         if (dy > 0.35) {
            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.JUMP)
         } else if (dy < -0.35) {
            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.SNEAK)
         }
      }
   }

   fun steerGround(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d, player: ClientPlayerEntity, enableSpeedAdaptation: Boolean, enableSprint: Boolean) {
      val target: Vec3d = waypoints.get(idx) as Vec3d
      val predictedPos: Vec3d = VoxelPathFollowMath.predictedMovementPos$default(VoxelPathFollowMath.INSTANCE, pos, player, false, 4, null)
      val frame: VoxelPathFollowMath.GroundSegmentFrame = VoxelPathFollowMath.INSTANCE.groundSegmentFrame(waypoints, idx, predictedPos)
      val pathLookaheadTarget: Vec3d = VoxelPathFollowMath.INSTANCE.pathLookaheadTarget(waypoints, idx, predictedPos, player)
      val lookaheadDx: Double = pathLookaheadTarget.field_1352 - pos.field_1352
      val lookaheadDz: Double = pathLookaheadTarget.field_1350 - pos.field_1350
      val correctedDirection: Pair = VoxelPathFollowMath.INSTANCE.correctedDirection(frame, player)
      val dx: Double = if (lookaheadDx * lookaheadDx + lookaheadDz * lookaheadDz > 0.01)
         lookaheadDx
         else
         (correctedDirection.getFirst() as java.lang.Number).doubleValue()
         val dz: Double = if (lookaheadDx * lookaheadDx + lookaheadDz * lookaheadDz > 0.01)
         lookaheadDz
         else
         (correctedDirection.getSecond() as java.lang.Number).doubleValue()
         val targetDx: Double = target.field_1352 - pos.field_1352
      val targetDz: Double = target.field_1350 - pos.field_1350
      val targetHorizontalDist: Double = Math.sqrt(targetDx * targetDx + (target.field_1350 - pos.field_1350) * (target.field_1350 - pos.field_1350))
      VoxelPathInput.INSTANCE.releaseAll()
      if (target.field_1351 < pos.field_1351 - 1.5) {
         this.steerFall(target, pos, player, targetDx, targetDz)
      } else {
         val jumpProfile: VoxelJumpProfile = VoxelJumpProfile.Companion.current(player)
         val jumpRequiredHeight: Double = jumpProfile.stepHeight + 0.08
         val waypointDy: Double = if (idx > 0) target.field_1351 - (waypoints.get(idx - 1) as Vec3d).field_1351 else target.field_1351 - pos.field_1351
         val descendingSegment: Boolean = target.field_1351 < pos.field_1351 - 0.2 || waypointDy < -0.2
         val needsWaypointJump: Boolean = waypointDy > jumpRequiredHeight
            && waypointDy <= jumpProfile.maxClimb
            && targetHorizontalDist < RangesKt.coerceAtMost(1.5 + (double)(jumpProfile.maxSkipCells - 1) * 0.85, jumpProfile.maxHorizontalBlocks + 0.4)
            && target.field_1351 - pos.field_1351 > jumpRequiredHeight
            val upcomingJumpTarget: Vec3d = this.findUpcomingJumpTarget(waypoints, idx, pos, jumpProfile)
         val needsTerrainJump: Boolean = !descendingSegment
            && (needsWaypointJump || upcomingJumpTarget != null)
            && (
               this.terrainNeedsPrejump(player, frame.dirX, frame.dirZ, 1.0, jumpProfile)
                  || this.ledgeNeedsBridge(player, frame.dirX, frame.dirZ, 1.0, jumpProfile)
            )
            if ((needsWaypointJump || needsTerrainJump) && player.method_24828()) {
            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.JUMP)
         }

         if (targetHorizontalDist < 0.5) {
            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
         } else {
            var var10000: Vec3d = upcomingJumpTarget
            if (upcomingJumpTarget == null) {
               var10000 = target
            }

            val effectiveJumping: Boolean = needsWaypointJump || needsTerrainJump || upcomingJumpTarget != null
            val edx: Double = var10000.field_1352 - pos.field_1352
            val edz: Double = var10000.field_1350 - pos.field_1350
            val var44: Pair = if (effectiveJumping) TuplesKt.to(edx, edz) else TuplesKt.to(dx, dz)
            val lookDx: Double = (var44.component1() as java.lang.Number).doubleValue()
            val lookDz: Double = (var44.component2() as java.lang.Number).doubleValue()
            val lookAngles: Pair = VoxelPathAngles.INSTANCE
               .calcAimAnglesFromDelta(
                  lookDx,
                  (
                        if (effectiveJumping)
                           var10000.field_1351
                           else
                           player.method_23320() + (if (effectiveJumping) 0.0 else this.computePitchFromSlope(waypoints, idx, lookDx, lookDz))
                     )
                     - player.method_23320(),
                  lookDz
               )
               this.setGroundLookTarget((lookAngles.getFirst() as java.lang.Number).floatValue(), (lookAngles.getSecond() as java.lang.Number).floatValue())
            val absYawDiff: Float = Math.abs(
               VoxelPathAngles.INSTANCE
                  .wrapDegrees(
                     (VoxelPathAngles.INSTANCE.calcAimAnglesFromDelta(dx, 0.0, dz).getFirst() as java.lang.Number).floatValue() - player.method_36454()
                  )
            )
            if (enableSpeedAdaptation) {
               if (absYawDiff < 25.0F) {
                  VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
                  if (enableSprint) {
                     VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.SPRINT)
                  }
               } else if (absYawDiff < 55.0F) {
                  VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
               } else if (absYawDiff < 115.0F) {
                  VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
               }
            } else {
               if (absYawDiff < 125.0F) {
                  VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
               }

               if (enableSprint && absYawDiff < 60.0F) {
                  VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.SPRINT)
               }
            }
         }
      }
   }

   public fun applyRotation() {
      this.applyYawEasing()
   }

   public fun applyYawEasing() {
      if (!java.lang.Float.isNaN(this.desiredYaw) && !java.lang.Float.isNaN(this.desiredPitch)) {
         VoxelPathRotation.INSTANCE.easeTowards(this.desiredYaw, this.desiredPitch, 0.18F, 0.12F)
      }
   }

   fun isAheadHeadBlocked(pos: Vec3d, dx: Double, dz: Double, horizontalDist: Double): Boolean {
      label23@
      if (!VoxelBlockCache.INSTANCE
         .isPassable(BlockPos((int)Math.floor(pos.field_1352), (int)Math.floor(pos.field_1351 + 1.8), (int)Math.floor(pos.field_1350)))) {
         true
      } else {
         !(horizontalDist < 0.05)
            && !VoxelBlockCache.INSTANCE
               .isPassable(
                  BlockPos(
                     (int)Math.floor(pos.field_1352 + dx / horizontalDist * 1.2),
                     (int)Math.floor(pos.field_1351 + 1.8 - 0.3),
                     (int)Math.floor(pos.field_1350 + dz / horizontalDist * 1.2)
                  )
               )
            }
   }

   private fun setGroundLookTarget(yaw: Float, pitch: Float) {
      this.desiredYaw = yaw
      this.desiredPitch = pitch
   }

   fun findUpcomingJumpTarget(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d, jumpProfile: VoxelJumpProfile): Vec3d {
      val ceiling: Int = Math.min(idx + Math.max(2, jumpProfile.maxSkipCells), CollectionsKt.getLastIndex(waypoints))
      var i: Int = idx
      if (idx <= ceiling) {
         while (true) {
            val wp: Vec3d = waypoints.get(i) as Vec3d
            val dy: Double = wp.field_1351 - (if (i > 0) waypoints.get(i - 1) as Vec3d else pos).field_1351
            if (!(dy <= jumpProfile.stepHeight + 0.08)
               && !(dy > jumpProfile.maxClimb)
               && Math.sqrt(
                     (wp.field_1352 - pos.field_1352) * (wp.field_1352 - pos.field_1352) + (wp.field_1350 - pos.field_1350) * (wp.field_1350 - pos.field_1350)
                  )
                  <= Math.max(3.0, jumpProfile.maxHorizontalBlocks + 0.4)) {
               wp
            }

            if (i == ceiling) {
               break
            }

            i++
         }
      }

      null
   }

   fun steerFall(target: Vec3d, pos: Vec3d, player: ClientPlayerEntity, dx: Double, dz: Double) {
      val lookAngles: Pair = VoxelPathAngles.INSTANCE.calcAimAnglesFromDelta(dx, target.field_1351 - player.method_23320(), dz)
      this.desiredYaw = (lookAngles.getFirst() as java.lang.Number).floatValue()
      this.desiredPitch = (lookAngles.getSecond() as java.lang.Number).floatValue()
      if (Math.abs(pos.field_1352 + player.method_18798().field_1352 - target.field_1352)
               + Math.abs(pos.field_1350 + player.method_18798().field_1350 - target.field_1350)
            > 0.2
         && Math.abs(player.method_18798().field_1351) > 0.4) {
         VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.SNEAK)
      }

      VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
   }

   private fun computePitchFromSlope(waypoints: List<class_243>, idx: Int, lookDx: Double, lookDz: Double): Double {
      if (idx + 1 >= waypoints.size()) {
         return 0.0
      } else {
         run label37@{
            var totalSlope: Double = 0.0
            var totalWeight: Double = 0.0
            var accDist: Double = 0.0
            val maxSegs: Int = Math.min(12, waypoints.size() - idx - 1)

            repeat(maxSegs) { hLookDist ->
               val segStart: Vec3d = waypoints.get(idx + hLookDist) as Vec3d
               val segEnd: Vec3d = waypoints.get(idx + hLookDist + 1) as Vec3d
               val sHDist: Double = Math.sqrt(
                  (segEnd.field_1352 - segStart.field_1352) * (segEnd.field_1352 - segStart.field_1352)
                     + (segEnd.field_1350 - segStart.field_1350) * (segEnd.field_1350 - segStart.field_1350)
               )
               if (!(sHDist < 0.01)) {
                  accDist += sHDist
                  if (accDist > 12.0) {
                     break
                  }

                  totalSlope += (segEnd.field_1351 - segStart.field_1351) / sHDist * (sHDist * (1.0 - accDist / 12.0))
                  totalWeight += sHDist * (1.0 - accDist / 12.0)
               }
            }

            return if (totalWeight <= 0.01)
               0.0
               else
               RangesKt.coerceIn(totalSlope / totalWeight * Math.min(Math.sqrt(lookDx * lookDx + lookDz * lookDz), 8.0) * 0.4, -3.0, 3.0)
            }
      }
   }

   fun terrainNeedsPrejump(player: ClientPlayerEntity, dx: Double, dz: Double, horizontalDist: Double, jumpProfile: VoxelJumpProfile): Boolean {
      if (player.method_24828() && !(horizontalDist < 0.3)) {
         val nx: Double = dx / horizontalDist
         val nz: Double = dz / horizontalDist
         val feetY: Double = player.method_23318()
         val cursor: Mutable = Mutable()

         label59@ for (probeDist in PROBE_DISTANCES) {
            val px: Double = player.method_23317() + nx * probeDist
            val pz: Double = player.method_23321() + nz * probeDist
            val low: Int = (int)Math.floor(feetY + -0.5)
            val high: Int = (int)Math.floor(feetY + 1.8)
            var by: Int = low
            if (low <= high) {
               while (true) {
                  cursor.method_10103((int)Math.floor(px), by, (int)Math.floor(pz))
                  val shape: VoxelShape = VoxelBlockCache.INSTANCE.getCollisionShape(cursor as BlockPos)
                  if (!shape.method_1110()) {
                     val top: Double = by + shape.method_1105(Axis.field_11052)
                     val bottom: Double = by + shape.method_1091(Axis.field_11052)
                     if (top - feetY > jumpProfile.stepHeight + 0.08 && top - feetY <= jumpProfile.maxClimb && top > feetY + 0.01) {
                        true
                     }

                     if (bottom > feetY + 0.01 && bottom < feetY + 1.8 && top > feetY + jumpProfile.stepHeight + 0.08) {
                        true
                     }
                  }

                  if (by == high) {
                     continue@label59
                  }

                  by++
               }
            }
         }

         false
      } else {
         false
      }
   }

   fun ledgeNeedsBridge(player: ClientPlayerEntity, dx: Double, dz: Double, horizontalDist: Double, jumpProfile: VoxelJumpProfile): Boolean {
      if (player.method_24828() && !(horizontalDist < 0.8)) {
         val nx: Double = dx / horizontalDist
         val nz: Double = dz / horizontalDist
         val feetY: Double = player.method_23318()
         val var10000: java.lang.Double = this.surfaceTopNear(player.method_23317() + nx * 1.5, player.method_23321() + nz * 1.5, feetY, true, jumpProfile)
         if (var10000 == null) {
            false
         } else {
            val farTop: Double = var10000
            if (!(farTop <= feetY + jumpProfile.stepHeight + 0.08) && !(farTop > feetY + jumpProfile.maxClimb)) {
               val midTop: java.lang.Double = this.surfaceTopNear(player.method_23317() + nx * 0.7, player.method_23321() + nz * 0.7, feetY, false, jumpProfile)
               midTop == null || midTop < feetY - 0.01
            } else {
               false
            }
         }
      } else {
         false
      }
   }

   private fun surfaceTopNear(x: Double, z: Double, feetY: Double, allowJumpHigh: Boolean, jumpProfile: VoxelJumpProfile): Double? {
      val bx: Int = (int)Math.floor(x)
      val bz: Int = (int)Math.floor(z)
      val maxOffset: Int = if (allowJumpHigh) (int)Math.ceil(jumpProfile.maxClimb) else 0
      val cursor: Mutable = Mutable()

      for (yOff in maxOffset downTo -2) {
         val by: Int = (int)Math.floor(feetY) + yOff
         cursor.method_10103(bx, by, bz)
         val shape: VoxelShape = VoxelBlockCache.INSTANCE.getCollisionShape(cursor as BlockPos)
         if (!shape.method_1110()) {
            val top: Double = by + shape.method_1105(Axis.field_11052)
            if (top <= feetY + (if (allowJumpHigh) jumpProfile.maxClimb else 0.6) && feetY - 1.5 <= top) {
               return top
            }
         }
      }

      return null
   }
}
