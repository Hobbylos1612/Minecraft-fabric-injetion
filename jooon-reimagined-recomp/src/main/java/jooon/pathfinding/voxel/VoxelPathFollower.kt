package jooon.pathfinding.voxel

import java.util.ArrayList
import net.minecraft.util.math.Vec3d
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d

object VoxelPathFollower {
   private var plan: VoxelRoutePlan = VoxelRoutePlan.Failed.INSTANCE as VoxelRoutePlan
   private var waypoints: MutableList<Vec3d> = ArrayList() as java.util.List
   private var cursor: Int = 1
   
   private Vec3d finalGoal;
   private var lastPlanTick: Int
   private var repathCooldown: Int
   private var lookAheadCooldown: Int = 10
   private var obstructionHits: Int
   private var graceTicks: Int = 40
   private val steering: VoxelPathSteering = VoxelPathSteering()
   private val recovery: VoxelPathRecovery = VoxelPathRecovery()

   fun isActive(): Boolean {
      return plan !is VoxelRoutePlan.Failed
   }

   fun stop() {
      if (plan !is VoxelRoutePlan.Failed) {
         plan = VoxelRoutePlan.Failed.INSTANCE
         waypoints.clear()
         cursor = 0
         finalGoal = null
         steering.reset()
         recovery.reset()
         VoxelPathInput.releaseAll()
      }
   }

   fun followTo(player: ClientPlayerEntity, goal: Vec3d, forceReplan: Boolean) {


      if (forceReplan
         || plan is VoxelRoutePlan.Failed
         || finalGoal == null
         || Math.hypot(finalGoal.x - goal.x, finalGoal.z - goal.z) > 1.25
         || Math.abs(currentGoal.y - goal.y) > 1.5
         || player.age - lastPlanTick >= 80) {
         if (repathCooldown > 0 && !forceReplan && plan !is VoxelRoutePlan.Failed) {
            repathCooldown += -1
         } else {

            lastPlanTick = tick
            repathCooldown = 8
            if (newPlan is VoxelRoutePlan.Failed) {
               this.stop()
            } else {
               plan = newPlan
               waypoints = toMutableList(newPlan.waypoints)
               cursor = (1).coerceAtMost(getLastIndex(waypoints))
               finalGoal = goal
               steering.reset()
               recovery.reset()
               lookAheadCooldown = 10
               obstructionHits = 0
               graceTicks = 40
            }
         }
      }
   }

   fun tick(player: ClientPlayerEntity): Boolean {
      if (plan is VoxelRoutePlan.Failed) {
return false
      } else if (waypoints.size() < 2) {
         this.stop()
return false
      } else {

         cursor = VoxelPathProgress.advanceCursor(cursor, waypoints, var10000, false, player.isOnGround())
         if (cursor >= waypoints.size()) {
            this.stop()
return false
         } else {
            val decision: VoxelPathRecovery.Decision = recovery.tick(var10000, false, cursor, waypoints, false)
            if (!(decision == VoxelPathRecovery.Decision.None.INSTANCE)) {
               if (decision !is VoxelPathRecovery.Decision.SkipTo) {
                  if (!(decision == VoxelPathRecovery.Decision.OffPath.INSTANCE) && !(decision == VoxelPathRecovery.Decision.Stuck.INSTANCE)) {
                     throw NoWhenBranchMatchedException()
                  }

                  if (finalGoal != null) {
                     followTo(player, finalGoal, true)
                  }

                  this.isActive()
               }

               cursor = (decision as VoxelPathRecovery.Decision.SkipTo).cursor
            }

            if (recovery.applyRecoveryInputsIfNeeded()) {
               steering.applyRotation()
return true
            } else if (player.isOnGround() && this.dynamicRepathBlocked()) {
               if (finalGoal != null) {
                  followTo(player, finalGoal, true)
               }

               this.isActive()
            } else {
               this.tryLookAheadShortcut(var10000, player)
               if (cursor >= waypoints.size()) {
                  this.stop()
return false
               } else {
                  steering.steerGround(waypoints, cursor, var10000, player, true, true)
                  steering.applyRotation()
return true
               }
            }
         }
      }
   }

   private fun dynamicRepathBlocked(): Boolean {
      if (graceTicks > 0) {
         graceTicks += -1
         return false
      } else {
         repathCooldown += -1
         if (repathCooldown > 0) {
            return false
         } else {

            var blocked: Boolean = false

            for (i in cursor..var4) {
               if (!VoxelPathCollision.isSegmentStillClear(waypoints.get(i), waypoints.get(i + 1))) {
                  blocked = true
break
               }
            }

            if (!blocked) {
               obstructionHits = 0
               return false
            } else {

               return obstructionHits >= 5
            }
         }
      }
   }

   fun tryLookAheadShortcut(pos: Vec3d, player: ClientPlayerEntity) {
      if (player.isOnGround()) {
         lookAheadCooldown += -1
         if (lookAheadCooldown <= 0) {
            lookAheadCooldown = 10
            var i: Int = Math.min(cursor + 15, getLastIndex(waypoints))

            if (cursor + 1 <= i) {
               while (true) {

                  if (!(
                        (pos.x - wp.x) * (pos.x - wp.x)
                              + (pos.y - wp.y) * (pos.y - wp.y)
                              + (pos.z - wp.z) * (pos.z - wp.z)
                           > 900.0
                     )
                     && !(wp.y - pos.y > 1.0)
                     && VoxelPathCollision.quickLineOfSight(pos, wp)) {
                     cursor = i
break
                  }

                  if (i == var5) {
break
                  }

                  i--
               }
            }
         }
      }
   }
}
