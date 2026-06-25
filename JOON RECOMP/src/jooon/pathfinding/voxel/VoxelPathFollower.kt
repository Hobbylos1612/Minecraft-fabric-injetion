package jooon.pathfinding.voxel

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_243
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d

@SourceDebugExtension(["SMAP\nVoxelPathFollower.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VoxelPathFollower.kt\njooon/pathfinding/voxel/VoxelPathFollower\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,220:1\n1#2:221\n*E\n"])
public object VoxelPathFollower {
   private final var plan: VoxelRoutePlan = VoxelRoutePlan.Failed.INSTANCE as VoxelRoutePlan
   private final var waypoints: MutableList<class_243> = ArrayList() as java.util.List
   private final var cursor: Int = 1
   @JvmStatic
   private Vec3d finalGoal;
   private final var lastPlanTick: Int
   private final var repathCooldown: Int
   private final var lookAheadCooldown: Int = 10
   private final var obstructionHits: Int
   private final var graceTicks: Int = 40
   private final val steering: VoxelPathSteering = VoxelPathSteering()
   private final val recovery: VoxelPathRecovery = VoxelPathRecovery()

   public fun isActive(): Boolean {
      return plan !is VoxelRoutePlan.Failed
   }

   public fun stop() {
      if (plan !is VoxelRoutePlan.Failed) {
         plan = VoxelRoutePlan.Failed.INSTANCE
         waypoints.clear()
         cursor = 0
         finalGoal = null
         steering.reset()
         recovery.reset()
         VoxelPathInput.INSTANCE.releaseAll()
      }
   }

   fun followTo(player: ClientPlayerEntity, goal: Vec3d, forceReplan: Boolean) {
      val tick: Int = player.field_6012
      val currentGoal: Vec3d = finalGoal
      if (forceReplan
         || plan is VoxelRoutePlan.Failed
         || finalGoal == null
         || Math.hypot(finalGoal.field_1352 - goal.field_1352, finalGoal.field_1350 - goal.field_1350) > 1.25
         || Math.abs(currentGoal.field_1351 - goal.field_1351) > 1.5
         || player.field_6012 - lastPlanTick >= 80) {
         if (repathCooldown > 0 && !forceReplan && plan !is VoxelRoutePlan.Failed) {
            repathCooldown += -1
         } else {
            val newPlan: VoxelRoutePlan = VoxelRouteEngine.INSTANCE.planWalk(Vec3d(player.method_23317(), player.method_23318(), player.method_23321()), goal)
            lastPlanTick = tick
            repathCooldown = 8
            if (newPlan is VoxelRoutePlan.Failed) {
               this.stop()
            } else {
               plan = newPlan
               waypoints = CollectionsKt.toMutableList(newPlan.waypoints)
               cursor = RangesKt.coerceAtMost(1, CollectionsKt.getLastIndex(waypoints))
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
         false
      } else if (waypoints.size() < 2) {
         this.stop()
         false
      } else {
         val var10000: Vec3d = player.method_73189()
         cursor = VoxelPathProgress.INSTANCE.advanceCursor(cursor, waypoints, var10000, false, player.method_24828())
         if (cursor >= waypoints.size()) {
            this.stop()
            false
         } else {
            val decision: VoxelPathRecovery.Decision = recovery.tick(var10000, false, cursor, waypoints, false)
            if (!(decision == VoxelPathRecovery.Decision.None.INSTANCE)) {
               if (decision !is VoxelPathRecovery.Decision.SkipTo) {
                  if (!(decision == VoxelPathRecovery.Decision.OffPath.INSTANCE) && !(decision == VoxelPathRecovery.Decision.Stuck.INSTANCE)) {
                     throw NoWhenBranchMatchedException()
                  }

                  if (finalGoal != null) {
                     INSTANCE.followTo(player, finalGoal, true)
                  }

                  this.isActive()
               }

               cursor = (decision as VoxelPathRecovery.Decision.SkipTo).cursor
            }

            if (recovery.applyRecoveryInputsIfNeeded()) {
               steering.applyRotation()
               true
            } else if (player.method_24828() && this.dynamicRepathBlocked()) {
               if (finalGoal != null) {
                  INSTANCE.followTo(player, finalGoal, true)
               }

               this.isActive()
            } else {
               this.tryLookAheadShortcut(var10000, player)
               if (cursor >= waypoints.size()) {
                  this.stop()
                  false
               } else {
                  steering.steerGround(waypoints, cursor, var10000, player, true, true)
                  steering.applyRotation()
                  true
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
            val var4: Int = Math.min(cursor + 4, CollectionsKt.getLastIndex(waypoints))
            var blocked: Boolean = false

            for (i in cursor..var4) {
               if (!VoxelPathCollision.INSTANCE.isSegmentStillClear(waypoints.get(i), waypoints.get(i + 1))) {
                  blocked = true
                  break
               }
            }

            if (!blocked) {
               obstructionHits = 0
               return false
            } else {
               val var6: Int = obstructionHits++
               return obstructionHits >= 5
            }
         }
      }
   }

   fun tryLookAheadShortcut(pos: Vec3d, player: ClientPlayerEntity) {
      if (player.method_24828()) {
         lookAheadCooldown += -1
         if (lookAheadCooldown <= 0) {
            lookAheadCooldown = 10
            var i: Int = Math.min(cursor + 15, CollectionsKt.getLastIndex(waypoints))
            val var5: Int = cursor + 1
            if (cursor + 1 <= i) {
               while (true) {
                  val wp: Vec3d = waypoints.get(i)
                  if (!(
                        (pos.field_1352 - wp.field_1352) * (pos.field_1352 - wp.field_1352)
                              + (pos.field_1351 - wp.field_1351) * (pos.field_1351 - wp.field_1351)
                              + (pos.field_1350 - wp.field_1350) * (pos.field_1350 - wp.field_1350)
                           > 900.0
                     )
                     && !(wp.field_1351 - pos.field_1351 > 1.0)
                     && VoxelPathCollision.INSTANCE.quickLineOfSight(pos, wp)) {
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
