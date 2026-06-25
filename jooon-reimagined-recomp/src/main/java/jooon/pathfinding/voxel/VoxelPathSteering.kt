package jooon.pathfinding.voxel

import jooon.pathfinding.voxel.world.VoxelBlockCache
import net.minecraft.util.math.Vec3d
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.BlockPos.Mutable
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.shape.VoxelShape

internal class VoxelPathSteering {
   private var desiredYaw: Float = java.lang.Float.NaN
   private var desiredPitch: Float = java.lang.Float.NaN

   fun reset() {
      this.desiredYaw = java.lang.Float.NaN
      this.desiredPitch = java.lang.Float.NaN
   }

   fun steerSky(target: Vec3d, pos: Vec3d, player: ClientPlayerEntity) {




      if (horizontalDist > 0.05) {

         this.desiredYaw = (yawError.getFirst() as java.lang.Number).floatValue()
         this.desiredPitch = (yawError.getSecond() as java.lang.Number).floatValue()
      } else if (!player.isOnGround() && dy < -1.0) {
         this.desiredPitch = 60.0F
      }

      VoxelPathInput.releaseAll()
      if (this.isAheadHeadBlocked(pos, dx, dz, horizontalDist)) {
         VoxelPathInput.press(VoxelPathInput.MoveAction.SNEAK)
         VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
      } else {
         if ((if (java.lang.Float.isNaN(this.desiredYaw)) 0.0F else Math.abs(VoxelPathAngles.wrapDegrees(this.desiredYaw - player.getYaw())))
               < 25.0F
            || horizontalDist < 0.5) {
            VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
         }

         if (dy > 0.35) {
            VoxelPathInput.press(VoxelPathInput.MoveAction.JUMP)
         } else if (dy < -0.35) {
            VoxelPathInput.press(VoxelPathInput.MoveAction.SNEAK)
         }
      }
   }

   fun steerGround(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d, player: ClientPlayerEntity, enableSpeedAdaptation: Boolean, enableSprint: Boolean) {


      val frame: VoxelPathFollowMath.GroundSegmentFrame = VoxelPathFollowMath.groundSegmentFrame(waypoints, idx, predictedPos)
return lookaheadDx
return else
         (correctedDirection.getFirst() as java.lang.Number).doubleValue()
return lookaheadDz
return else
         (correctedDirection.getSecond() as java.lang.Number).doubleValue()



      VoxelPathInput.releaseAll()
      if (target.y < pos.y - 1.5) {
         this.steerFall(target, pos, player, targetDx, targetDz)
      } else {





            && waypointDy <= jumpProfile.maxClimb
            && targetHorizontalDist < (1.5 + (jumpProfile.maxSkipCells - 1).toDouble() * 0.85).coerceAtMost(jumpProfile.maxHorizontalBlocks + 0.4)
            && target.y - pos.y > jumpRequiredHeight


            && (needsWaypointJump || upcomingJumpTarget != null)
            && (
               this.terrainNeedsPrejump(player, frame.dirX, frame.dirZ, 1.0, jumpProfile)
                  || this.ledgeNeedsBridge(player, frame.dirX, frame.dirZ, 1.0, jumpProfile)
            )
            if ((needsWaypointJump || needsTerrainJump) && player.isOnGround()) {
            VoxelPathInput.press(VoxelPathInput.MoveAction.JUMP)
         }

         if (targetHorizontalDist < 0.5) {
            VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
         } else {
            var var10000: Vec3d = upcomingJumpTarget
            if (upcomingJumpTarget == null) {
               var10000 = target
            }







               .calcAimAnglesFromDelta(
                  lookDx,
                  (
                        if (effectiveJumping)
                           var10000.y
return else
                           player.getEyeY() + (if (effectiveJumping) 0.0 else this.computePitchFromSlope(waypoints, idx, lookDx, lookDz))
                     )
                     - player.getEyeY(),
return lookDz
               )
               this.setGroundLookTarget((lookAngles.getFirst() as java.lang.Number).floatValue(), (lookAngles.getSecond() as java.lang.Number).floatValue())

               VoxelPathAngles.INSTANCE
                  .wrapDegrees(
                     (VoxelPathAngles.calcAimAnglesFromDelta(dx, 0.0, dz).getFirst() as java.lang.Number).floatValue() - player.getYaw()
                  )
            )
            if (enableSpeedAdaptation) {
               if (absYawDiff < 25.0F) {
                  VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
                  if (enableSprint) {
                     VoxelPathInput.press(VoxelPathInput.MoveAction.SPRINT)
                  }
               } else if (absYawDiff < 55.0F) {
                  VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
               } else if (absYawDiff < 115.0F) {
                  VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
               }
            } else {
               if (absYawDiff < 125.0F) {
                  VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
               }

               if (enableSprint && absYawDiff < 60.0F) {
                  VoxelPathInput.press(VoxelPathInput.MoveAction.SPRINT)
               }
            }
         }
      }
   }

   fun applyRotation() {
      this.applyYawEasing()
   }

   fun applyYawEasing() {
      if (!java.lang.Float.isNaN(this.desiredYaw) && !java.lang.Float.isNaN(this.desiredPitch)) {
         VoxelPathRotation.easeTowards(this.desiredYaw, this.desiredPitch, 0.18F, 0.12F)
      }
   }

   fun isAheadHeadBlocked(pos: Vec3d, dx: Double, dz: Double, horizontalDist: Double): Boolean {
      label23@
      if (!VoxelBlockCache.INSTANCE
         .isPassable(BlockPos(Math.floor(pos.x).toInt(), Math.floor(pos.y + 1.8).toInt(), Math.floor(pos.z).toInt()))) {
return true
      } else {
         !(horizontalDist < 0.05)
            && !VoxelBlockCache.INSTANCE
               .isPassable(
                  BlockPos(
                     Math.floor(pos.x + dx / horizontalDist * 1.2).toInt(),
                     Math.floor(pos.y + 1.8 - 0.3).toInt(),
                     Math.floor(pos.z + dz / horizontalDist * 1.2).toInt()
                  )
               )
            }
   }

   private fun setGroundLookTarget(yaw: Float, pitch: Float) {
      this.desiredYaw = yaw
      this.desiredPitch = pitch
   }

   fun findUpcomingJumpTarget(waypoints: MutableList<Vec3d>, idx: Int, pos: Vec3d, jumpProfile: VoxelJumpProfile): Vec3d {

      var i: Int = idx
      if (idx <= ceiling) {
         while (true) {


            if (!(dy <= jumpProfile.stepHeight + 0.08)
               && !(dy > jumpProfile.maxClimb)
               && Math.sqrt(
                     (wp.x - pos.x) * (wp.x - pos.x) + (wp.z - pos.z) * (wp.z - pos.z)
                  )
                  <= Math.max(3.0, jumpProfile.maxHorizontalBlocks + 0.4)) {
return wp
            }

            if (i == ceiling) {
break
            }

            i++
         }
      }
return null
   }

   fun steerFall(target: Vec3d, pos: Vec3d, player: ClientPlayerEntity, dx: Double, dz: Double) {

      this.desiredYaw = (lookAngles.getFirst() as java.lang.Number).floatValue()
      this.desiredPitch = (lookAngles.getSecond() as java.lang.Number).floatValue()
      if (Math.abs(pos.x + player.getVelocity().x - target.x)
               + Math.abs(pos.z + player.getVelocity().z - target.z)
            > 0.2
         && Math.abs(player.getVelocity().y) > 0.4) {
         VoxelPathInput.press(VoxelPathInput.MoveAction.SNEAK)
      }

      VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
   }

   private fun computePitchFromSlope(waypoints: List<Vec3d>, idx: Int, lookDx: Double, lookDz: Double): Double {
      if (idx + 1 >= waypoints.size()) {
         return 0.0
      } else {
         run label37@{
            var totalSlope: Double = 0.0
            var totalWeight: Double = 0.0
            var accDist: Double = 0.0


            repeat(maxSegs) { hLookDist ->



                  (segEnd.x - segStart.x) * (segEnd.x - segStart.x)
                     + (segEnd.z - segStart.z) * (segEnd.z - segStart.z)
               )
               if (!(sHDist < 0.01)) {
                  accDist += sHDist
                  if (accDist > 12.0) {
break
                  }

                  totalSlope += (segEnd.y - segStart.y) / sHDist * (sHDist * (1.0 - accDist / 12.0))
                  totalWeight += sHDist * (1.0 - accDist / 12.0)
               }
            }

            return if (totalWeight <= 0.01)
               0.0
return else
               (totalSlope / totalWeight * Math.min(Math.sqrt(lookDx * lookDx + lookDz * lookDz), 8.0) * 0.4).coerceIn(-3.0, 3.0)
            }
      }
   }

   fun terrainNeedsPrejump(player: ClientPlayerEntity, dx: Double, dz: Double, horizontalDist: Double, jumpProfile: VoxelJumpProfile): Boolean {
      if (player.isOnGround() && !(horizontalDist < 0.3)) {





         label59@ for (probeDist in PROBE_DISTANCES) {




            var by: Int = low
            if (low <= high) {
               while (true) {
                  cursor.set(Math.floor(px).toInt(), by, Math.floor(pz).toInt())

                  if (!shape.isEmpty()) {


                     if (top - feetY > jumpProfile.stepHeight + 0.08 && top - feetY <= jumpProfile.maxClimb && top > feetY + 0.01) {
return true
                     }

                     if (bottom > feetY + 0.01 && bottom < feetY + 1.8 && top > feetY + jumpProfile.stepHeight + 0.08) {
return true
                     }
                  }

                  if (by == high) {
                     continue@label59
                  }

                  by++
               }
            }
         }
return false
      } else {
return false
      }
   }

   fun ledgeNeedsBridge(player: ClientPlayerEntity, dx: Double, dz: Double, horizontalDist: Double, jumpProfile: VoxelJumpProfile): Boolean {
      if (player.isOnGround() && !(horizontalDist < 0.8)) {




         if (var10000 == null) {
return false
         } else {

            if (!(farTop <= feetY + jumpProfile.stepHeight + 0.08) && !(farTop > feetY + jumpProfile.maxClimb)) {

               midTop == null || midTop < feetY - 0.01
            } else {
return false
            }
         }
      } else {
return false
      }
   }

   private fun surfaceTopNear(x: Double, z: Double, feetY: Double, allowJumpHigh: Boolean, jumpProfile: VoxelJumpProfile): Double? {





      for (yOff in maxOffset downTo -2) {

         cursor.set(bx, by, bz)

         if (!shape.isEmpty()) {

            if (top <= feetY + (if (allowJumpHigh) jumpProfile.maxClimb else 0.6) && feetY - 1.5 <= top) {
               return top
            }
         }
      }

      return null
   }
}
