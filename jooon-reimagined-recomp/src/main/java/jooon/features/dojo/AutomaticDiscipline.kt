package jooon.features.dojo

import java.util.LinkedHashSet
import jooon.config.Config
import jooon.mixins.ServerboundInteractPacketAccessor
import jooon.pathfinding.PathSpline
import jooon.pathfinding.WalkingPathfinder
import jooon.pathfinding.WalkingPathfinder.Node
import jooon.pathfinding.WalkingPathfinder.Options
import jooon.util.PlayerController
import jooon.util.SyntheticMouseTurnBroker
import jooon.util.SyntheticMouseTurnBroker.TurnDelta
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.Ref.BooleanRef
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.util.math.Vec3d
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.PlayerInteractEntityC2SPacket.class_5908
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.item.Item
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView
import net.minecraft.world.World

object AutomaticDiscipline {
   private const val SYNTHETIC_TURN_OWNER: String = "dojo_automatic_discipline"
   private const val SEARCH_RADIUS: Double = 36.0
   private const val ATTACK_REACH_PAD: Double = 0.15
   private const val ATTACK_MOVE_DIST: Double = 3.15
   private const val ATTACK_COOLDOWN_TICKS: Int = 2
   private const val REPATH_TICKS: Int = 10
   private const val WAYPOINT_REACHED_DIST: Double = 0.55
   private const val MIN_REPLAN_TICKS: Int = 5
   private const val TARGET_SWITCH_RATIO: Double = 0.48
   private const val SPRINT_YAW_LIMIT: Float = 58.0F
   private const val WALK_YAW_LIMIT: Float = 76.0F
   private const val STUCK_CHECK_TICKS: Int = 6
   private const val STUCK_TRIGGER_TICKS: Int = 3
   private const val CORNER_BACK_TICKS: Int = 7
   private const val CORNER_JUMP_TICKS: Int = 11
   private const val CONNECTOR_MIN_CLEAR: Double = 2.65
   private const val CONNECTOR_MAX_SCAN: Double = 5.25
   private const val CONNECTOR_LATERAL_TOLERANCE: Double = 0.72
   private const val CONNECTOR_WALK_YAW_LIMIT: Float = 76.0F
   private const val CONNECTOR_SPRINT_YAW_LIMIT: Float = 30.0F
   private const val CONNECTOR_WIDE_SPRINT_YAW_LIMIT: Float = 46.0F
   private const val CONNECTOR_WIDE_SIDE_SAMPLE: Double = 0.86
   private const val CONNECTOR_EDGE_SIDE_SAMPLE: Double = 1.62
   private const val CONNECTOR_JUMP_MIN_CLEAR: Double = 3.35
   private const val CONNECTOR_JUMP_YAW_LIMIT: Float = 13.0F
   private const val CONNECTOR_TARGET_DOT_MIN: Double = 0.58
   private const val POLISHED_ANDESITE_PATH_PENALTY: Double = 6.0
   private const val TOP_STUCK_TRIGGER_MS: Long = 1500L
   private const val TOP_STUCK_SAMPLE_MS: Long = 250L
   private const val TOP_STUCK_MOVE_EPS: Double = 0.09
   private const val CENTER_RECOVERY_REACHED_DIST: Double = 0.95
   private const val CENTER_RECOVERY_MIN_EXIT_MOVE: Double = 1.15
   private const val CENTER_RECOVERY_MAX_SEARCH: Int = 8
   private val pathOptions: Options = WalkingPathfinder.Options(true, false, 6.0, true)
   private var active: Boolean
   private var isPaused: Boolean
   private var currentTargetId: Int?
   private var pathTargetId: Int?
   private var pathPoints: List<Vec3d> = emptyList()
   private var pathIndex: Int
   private var pathVariantSeed: Int
   private var lastPathTick: Int
   private var lastClickTick: Int
   private var swappedAtTick: Int = -100
   private var nextJumpAllowedTick: Int
   private var currentAimPlan: jooon.features.dojo.AutomaticDiscipline.AimPlan?
   private var lastObservedAttackEntityId: Int = -1
   private val presumedKilledIds: MutableSet<Int> = LinkedHashSet() as java.util.Set
   private var progressGoalDist: Double = java.lang.Double.MAX_VALUE
   private var progressCheckTick: Int
   private var noProgressTicks: Int
   private var cornerRecoveryPhase: jooon.features.dojo.AutomaticDiscipline.CornerRecoveryPhase = AutomaticDiscipline.CornerRecoveryPhase.NONE
   private var cornerRecoveryUntilTick: Int
   private var cornerJumpDirX: Double
   private var cornerJumpDirZ: Double
   private var cornerAimX: Double
   private var cornerAimZ: Double
   private var cornerNeedsGapJump: Boolean
   private var playerTargetYLevel: Int?
   private var topStillSampleAtMs: Long
   private var topStillSinceMs: Long
   
   private Vec3d topStillSamplePos;
   private var centerRecoveryPhase: jooon.features.dojo.AutomaticDiscipline.CenterRecoveryPhase = AutomaticDiscipline.CenterRecoveryPhase.NONE
   private var centerRecoveryGoal: Node?
   
   private Vec3d centerRecoveryStartedAt;

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         tick(client)
      })
   }

   fun onOutgoingPacket(packet: Packet) {
      if (packet is PlayerInteractEntityC2SPacket) {
         if (active && Config.fullyAutomaticDiscipline) {

            (packet as PlayerInteractEntityC2SPacket).handle(object : class_5908 {
               fun method_34219(hand: Hand) {
               }

               fun method_34220(hand: Hand, location: Vec3d) {
               }

               fun method_34218() {
                  isAttack.element = true
               }
            })
            if (isAttack.element) {


               var `this24lambda_u241`: AutomaticDiscipline
               try {
                  `this24lambda_u241` = var4
                  `this24lambda_u241` = Result(
                     (packet as ServerboundInteractPacketAccessor).jooonEntityId()
                  )
               } catch (var7: java.lang.Throwable) {
                  `this24lambda_u241` = Result(
                     ResultKt.createFailure(var7)
                  )
               }

                     if (Result.isFailure/* $VF was: isFailure-impl */(`this24lambda_u241`))
                        -1
return else
                        `this24lambda_u241`
                  ) as java.lang.Number)
                  .intValue()
                  if (entityId >= 0) {
                  lastObservedAttackEntityId = entityId
               }
            }
         }
      }
   }

   fun tick(client: MinecraftClient) {
      if (!Config.fullyAutomaticDiscipline || !AutoDojo.isChallengeActive(AutoDojo.Challenge.DISCIPLINE)) {
         if (active) {
            this.reset()
         }
      } else {


         if (client.player != null && client.world != null && client.currentScreen == null) {
            if (!active) {
               active = true
               presumedKilledIds.clear()
               lastObservedAttackEntityId = -1
               currentTargetId = null
               isPaused = false
               DojoPauseInput.reset()
               playerTargetYLevel = null
               this.resetTopStuckTracking()
               this.resetCenterRecovery()
               AutoDojo.subtitle = "§7[§cSneak to pause§7]"
            }

            this.updatePlayerTargetYLevel(client.world as World, client.player)
            if (DojoPauseInput.consumeSneakTap()) {
               isPaused = !isPaused
               if (!isPaused) {
                  AutoDojo.subtitle = "§7[§cSneak to pause§7]"
               }
            }

            if (isPaused) {
               this.pauseFully()
               AutoDojo.subtitle = "§7[§cPAUSED§7]"
            } else {
               if (AutoDojo.subtitle.length() == 0 || contains$default(AutoDojo.subtitle, "PAUSED", false, 2, null)) {
                  AutoDojo.subtitle = "§7[§cSneak to pause§7]"
               }

               this.cleanupPresumedKilled()
               this.consumeObservedAttack()

               if (target == null) {
                  currentTargetId = null
                  this.clearPath()
                  this.resetTopStuckTracking()
                  this.resetCenterRecovery()
                  this.stopMotion()
                  this.requestIdleAimRelease()
               } else if (!this.handleTopLevelCenterRecovery(player, level as World, target)) {
                  if (currentTargetId == null || currentTargetId != target.getId()) {
                     currentTargetId = target.getId()
                     this.clearPath()
                     this.resetCornerRecovery()
                  }

                  this.ensureSword(player, target)
                  if (this.canAttemptAttack(player, target)) {
                     this.clearPath()
                     this.stopMotion()
                     this.aimAtEntity(player, target, 55)
                     this.tryAttack(player, target)
                     this.consumeObservedAttack()
                     this.handOffToNextTarget(player, level as World, target)
                  } else {
                     if (this.isNearAttackRange(player, target)) {
                        this.aimAtEntity(player, target, 50)
                        if (!this.nudgeTowardTarget(player, level as World, target)) {
                           this.followPathToTarget(player, level as World, target)
                        }
                     } else {
                        this.followPathToTarget(player, level as World, target)
                     }
                  }
               }
            }
         } else {
            this.stopMotion()
         }
      }
   }

   private fun consumeObservedAttack() {

      if (lastObservedAttackEntityId >= 0) {
         lastObservedAttackEntityId = -1
         presumedKilledIds.add(attackedId)
         if (currentTargetId != null && currentTargetId == attackedId) {
            currentTargetId = null
            this.clearPath()
         }
      }
   }

   fun handOffToNextTarget(player: ClientPlayerEntity, level: World, previous: ZombieEntity) {
      if (currentTargetId == null) {

         if (var10000 != null) {
            if (var10000.getId() != previous.getId()) {
               currentTargetId = var10000.getId()
               this.clearPath()
               this.ensureSword(player, var10000)
               if (this.canAttemptAttack(player, var10000)) {
                  this.stopMotion()
                  this.aimAtEntity(player, var10000, 55)
                  this.tryAttack(player, var10000)
                  this.consumeObservedAttack()
               } else {
                  if (this.isNearAttackRange(player, var10000)) {
                     this.aimAtEntity(player, var10000, 50)
                     if (!this.nudgeTowardTarget(player, level, var10000)) {
                        this.followPathToTarget(player, level, var10000)
                     }
                  } else {
                     this.followPathToTarget(player, level, var10000)
                  }
               }
            }
         }
      }
   }

   private fun cleanupPresumedKilled() {
      if (!presumedKilledIds.isEmpty()) {

         if (var10000 != null) {
            val var2: java.lang.Iterable = var10000.getEntities()
            if (var2 != null) {

               if (var3 != null) {

                     it.getId()
                  })
                  if (var4 != null) {
                     val var5: java.util.Set = toSet(var4)
                     if (var5 != null) {
                        presumedKilledIds.removeIf({ p0: Any ->
                           ``(p0)
                        })
return return
                     }
                  }
               }
            }
         }
      }
   }

   fun updateMovementProgress(player: ClientPlayerEntity, goalDist: Double, forwardBlocked: Boolean) {
      if (cornerRecoveryPhase === AutomaticDiscipline.CornerRecoveryPhase.NONE) {

         if (player.age - progressCheckTick >= 6) {

            if (progressGoalDist - goalDist < 0.2) {

            } else {
               noProgressTicks = 0
            }

            if (forwardBlocked && gained < 0.45) {

            }

            progressGoalDist = goalDist
            progressCheckTick = tick
         }
      }
   }

   fun tryCornerRecovery(
      player: ClientPlayerEntity, level: World, target: ZombieEntity, travelDirX: Double, travelDirZ: Double, travelAimX: Double, travelAimZ: Double
   ): Boolean {
      if (cornerRecoveryPhase === AutomaticDiscipline.CornerRecoveryPhase.NONE) {
         if (noProgressTicks < 3) {
return false
         }

         val plan: AutomaticDiscipline.RecoveryJumpPlan = this.planRecoveryJump(level, player, travelDirX, travelDirZ, travelAimX, travelAimZ)
         if (plan == null) {
            noProgressTicks = 0
return false
         }

         cornerJumpDirX = plan.dirX
         cornerJumpDirZ = plan.dirZ
         cornerAimX = plan.aimX
         cornerAimZ = plan.aimZ
         cornerNeedsGapJump = plan.needsGapJump
         cornerRecoveryPhase = AutomaticDiscipline.CornerRecoveryPhase.BACKING
         cornerRecoveryUntilTick = player.age + 7
         noProgressTicks = 0
         this.clearPath()

      }

      when (AutomaticDiscipline.WhenMappings.$EnumSwitchMapping$0[cornerRecoveryPhase.ordinal()]) {
         1 -> {
            this.aimAtPoint(player, cornerAimX, player.getEyeY(), cornerAimZ, 40)
            PlayerController.pressForward(false)
            PlayerController.pressBack(true)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressSprint(false)
            PlayerController.pressJump(false)
            player.setSprinting(false)
            if (player.age >= cornerRecoveryUntilTick) {
               if (cornerNeedsGapJump && !this.isGapJumpSafe(level, player, cornerJumpDirX, cornerJumpDirZ)) {
                  this.resetCornerRecovery()
return false
               }

               cornerRecoveryPhase = AutomaticDiscipline.CornerRecoveryPhase.COMMIT_JUMP
               cornerRecoveryUntilTick = player.age + 11
               nextJumpAllowedTick = player.age
            }
         }
         2 -> {
            this.aimAtPoint(player, cornerAimX, player.getEyeY(), cornerAimZ, 38)
            PlayerController.pressBack(false)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressForward(true)
            PlayerController.pressSprint(true)
            player.setSprinting(true)


               && player.isOnGround()
               && ticksLeft >= 9
               && this.isGapJumpSafe(level, player, cornerJumpDirX, cornerJumpDirZ)
               PlayerController.pressJump(jump)
            if (jump) {
               nextJumpAllowedTick = player.age + 8
            }

            if (player.age >= cornerRecoveryUntilTick || !player.isOnGround() && !cornerNeedsGapJump) {
               this.resetCornerRecovery()
            }
         }
         3 -> {}
         else -> throw NoWhenBranchMatchedException()
      }

      cornerRecoveryPhase != AutomaticDiscipline.CornerRecoveryPhase.NONE
   }

   fun planRecoveryJump(level: World, player: ClientPlayerEntity, prefDirX: Double, prefDirZ: Double, fallbackAimX: Double, fallbackAimZ: Double): AutomaticDiscipline.RecoveryJumpPlan {

      val var13: java.util.Iterator = listOf(arrayOf(0.0, -14.0, 14.0, -28.0, 28.0)).iterator()

      while (var13.hasNext()) {



         if (this.isGapJumpSafe(level, player, dirX, dirZ)) {

            if (var10000 != null) {
               AutomaticDiscipline.RecoveryJumpPlan(dirX, dirZ, var10000.x, var10000.z, true)
            }
         } else if (isMoveDirectionSafe$default(this, level, player, dirX, dirZ, false, null, null, false, 0.0, 480, null)
            && !this.isGapAhead(level, player, dirX, dirZ)) {
            AutomaticDiscipline.RecoveryJumpPlan(dirX, dirZ, player.getX() + dirX * 2.5, player.getZ() + dirZ * 2.5, false)
         }
      }

      if (isMoveDirectionSafe$default(this, level, player, prefDirX, prefDirZ, false, null, null, false, 0.0, 480, null))
         AutomaticDiscipline.RecoveryJumpPlan(prefDirX, prefDirZ, fallbackAimX, fallbackAimZ, false)
return else
return null
      }

   fun isGapAhead(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double): Boolean {



      var floorSeen: Boolean = this.hasWalkableFloor(level, px, py, pz)
      val var14: java.util.Iterator = listOf(arrayOf(0.6, 1.0, 1.4, 1.8, 2.2)).iterator()

      while (var14.hasNext()) {


         if (floorSeen && !hasFloor) {
return true
         }

         floorSeen = hasFloor
      }
return false
   }

   fun findLandingPoint(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double): Vec3d {



      val var13: java.util.Iterator = listOf(arrayOf(2.6, 3.0, 3.4, 3.8, 4.2)).iterator()

      while (var13.hasNext()) {



         if (this.hasWalkableFloor(level, x, py, pz + dirZ * var20)) {
            Vec3d(x, py, z)
         }

         if (this.hasWalkableFloor(level, x, py - 1.0, z)) {
            Vec3d(x, py - 1.0, z)
         }
      }
return null
   }

   fun isGapJumpSafe(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double): Boolean {
      if (!this.hasWalkableFloor(level, player.getX(), player.getY(), player.getZ())) {
return false
      } else if (!this.hasWalkableFloor(level, player.getX() + dirX * 0.65, player.getY(), player.getZ() + dirZ * 0.65)) {
return false
      } else {

         if (var10000 == null) {
return false
         } else {

            if (!(landDist < 2.0) && !(landDist > 4.8)) {
               run label48@{
                  var crossedOpen: Boolean = false

                  // $VF: Unable to resugar Kotlin loop from Java for loop
                  var probe: Double = 0.9
                  while (true) {
                     if (probe < landDist - 0.55) break
                     if (!this.hasWalkableFloor(level, player.getX() + dirX * probe, player.getY(), player.getZ() + dirZ * probe)) {
                        crossedOpen = true
                     }

                     probe += 0.45
                  }
return crossedOpen
                     && this.isSafeStandable(
                        level,
                        WalkingPathfinder.Node(
                           MathHelper.floor(var10000.x),
                           MathHelper.floor(var10000.y),
                           MathHelper.floor(var10000.z)
                        )
                     )
                  }
            } else {
return false
            }
         }
      }
   }

   private fun resetCornerRecovery() {
      cornerRecoveryPhase = AutomaticDiscipline.CornerRecoveryPhase.NONE
      cornerRecoveryUntilTick = 0
      cornerJumpDirX = 0.0
      cornerJumpDirZ = 0.0
      cornerAimX = 0.0
      cornerAimZ = 0.0
      cornerNeedsGapJump = false
      noProgressTicks = 0
      progressGoalDist = java.lang.Double.MAX_VALUE
      progressCheckTick = 0
   }

   fun handleTopLevelCenterRecovery(player: ClientPlayerEntity, level: World, target: ZombieEntity): Boolean {
      if (this.isNearAttackRange(player, target) && this.isCrosshairOnTarget(target)) {
         this.resetTopStuckTracking()
return false
      } else if (centerRecoveryPhase != AutomaticDiscipline.CenterRecoveryPhase.RETREATING) {
         this.updateTopStuckTracking(player)
         if (topStillSinceMs != 0L && System.currentTimeMillis() - topStillSinceMs >= 1500L) {
            val var22: WalkingPathfinder.Node = this.chooseCenterRecoveryGoal(level, player, target)
            if (var22 == null) {
return false
            } else {
               centerRecoveryPhase = AutomaticDiscipline.CenterRecoveryPhase.RETREATING
               centerRecoveryGoal = var22
               centerRecoveryStartedAt = player.getEntityPos()
               currentTargetId = null
               this.clearPath()
               this.resetCornerRecovery()
               this.moveTowardCenterRecoveryGoal(player, level, var22.center())
return true
            }
         } else {
return false
         }
      } else {
         var var10000: WalkingPathfinder.Node = centerRecoveryGoal
         if (centerRecoveryGoal == null) {
            var10000 = this.chooseCenterRecoveryGoal(level, player, target)
            if (var10000 != null) {
               centerRecoveryGoal = var10000
               var10000 = var10000
            } else {
               var10000 = null
            }

            if (var10000 == null) {

               this.resetCenterRecovery()
               var16.resetTopStuckTracking()
return false
            }
         }

         var var10001: Vec3d = player.getEntityPos()

         val var21: Double
         if (centerRecoveryStartedAt != null) {


            var10001 = player.getEntityPos()
            var21 = var20.horizontalDistance(var10001, it)
         } else {
            var21 = 0.0
         }

         if (!player.isOnGround()
            || !this.isStableFooting(level, player.getX(), player.getY(), player.getZ())
            || !(goal <= 0.95) && !(var21 >= 1.15)) {
            this.moveTowardCenterRecoveryGoal(player, level, goalCenter)
return true
         } else {
            this.resetCenterRecovery()
            this.resetTopStuckTracking()
            this.clearPath()
            currentTargetId = null
return false
         }
      }
   }

   fun updateTopStuckTracking(player: ClientPlayerEntity) {


      if (topStillSamplePos != null && topStillSampleAtMs != 0L) {
         if (now - topStillSampleAtMs >= 250L) {
            if (!player.isOnGround() || this.horizontalDistance(var10000, topStillSamplePos) > 0.09) {
               topStillSinceMs = 0L
            } else if (topStillSinceMs == 0L) {
               topStillSinceMs = topStillSampleAtMs
            }

            topStillSampleAtMs = now
            topStillSamplePos = var10000
         }
      } else {
         topStillSampleAtMs = now
         topStillSamplePos = var10000
         topStillSinceMs = 0L
      }
   }

   fun moveTowardCenterRecoveryGoal(player: ClientPlayerEntity, level: World, goalCenter: Vec3d) {



      if (dist <= 0.05) {
         this.stopMotion()
      } else {

         if (var10000 == null) {

            this.stopMotion()
            var24.aimAtPoint(player, goalCenter.x, player.getEyeY(), goalCenter.z, 50)
         } else {





               && dist > 2.4
               && yawError <= 34.0F
               && isMoveDirectionSafe$default(this, level, player, dirX, dirZ, true, 1.85, 0.14, false, 0.0, 256, null)
               this.aimAtPoint(player, player.getX() + dirX * 2.0, player.getEyeY(), player.getZ() + dirZ * 2.0, 48)
            PlayerController.pressSprint(sprint)
            player.setSprinting(sprint)
            PlayerController.pressJump(false)
            PlayerController.pressBack(false)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressForward(move)
         }
      }
   }

   fun chooseSafeRecoveryDirection(level: World, player: ClientPlayerEntity, primaryX: Double, primaryZ: Double): Pair<Double, Double> {

      var best: Pair = null
      var bestScore: Double = -java.lang.Double.MAX_VALUE
      val var12: java.util.Iterator = listOf(arrayOf(0.0, -12.0, 12.0, -24.0, 24.0, -38.0, 38.0)).iterator()

      while (var12.hasNext()) {



         if (isMoveDirectionSafe$default(this, level, player, dirX, dirZ, false, 1.25, 0.18, false, 0.0, 256, null)) {

               + this.footingScore(level, player.getX() + dirX * 0.9, player.getY(), player.getZ() + dirZ * 0.9)
               if (score > bestScore) {
               bestScore = score
               best = Pair(dirX, dirZ)
            }
         }
      }
return best
   }

   fun chooseCenterRecoveryGoal(level: World, player: ClientPlayerEntity, target: ZombieEntity): WalkingPathfinder.Node {
      var var10000: Vec3d = this.targetCentroid(player)
      if (var10000 == null) {
         var10000 = target.getEntityPos()
      }




      var best: WalkingPathfinder.Node = null
      var bestScore: Double = java.lang.Double.MAX_VALUE

      repeat(8) { radius ->
         var dx: Int = -radius
         if (-radius <= radius) {
            while (true) {
               var dz: Int = -radius
               if (-radius <= radius) {
                  while (true) {
                     if (radius <= 0 || Math.max(Math.abs(dx), Math.abs(dz)) == radius) {
                        for (dy in -1..1) {
                           val node: WalkingPathfinder.Node = WalkingPathfinder.Node(baseX + dx, feetY + dy, baseZ + dz)
                           if (this.isSafeStandable(level, node)) {


                              if (stability >= 5) {



                                    + this.horizontalDistance(nodeCenter, var10003) * 0.12
                                    - stability * 0.65
                                    + (
                                       if (this.isAvoidedPolishedAndesiteFloor(level, nodeCenter.x, nodeCenter.y, nodeCenter.z))
                                          5.0
return else
                                          0.0
                                    )
                                    if (score < bestScore) {
                                    bestScore = score
                                    best = node
                                 }
                              }
                           }
                        }
                     }

                     if (dz == radius) {
break
                     }

                     dz++
                  }
               }

               if (dx == radius) {
break
               }

               dx++
            }
         }

         if (best != null && radius >= 2) {
break
         }
      }
return best
   }

   fun isStableFooting(level: World, x: Double, y: Double, z: Double): Boolean {
      this.hasWalkableFloor(level, x, y, z) && this.footingScore(level, x, y, z) >= 6
   }

   fun footingScore(level: World, x: Double, y: Double, z: Double): Int {
      var score: Int = 0

      for (var15 in listOf(
         arrayOf(
            Pair(0.0, 0.0),
            Pair(0.95, 0.0),
            Pair(-0.95, 0.0),
            Pair(0.0, 0.95),
            Pair(0.0, -0.95),
            Pair(0.75, 0.75),
            Pair(-0.75, 0.75),
            Pair(0.75, -0.75),
            Pair(-0.75, -0.75)
         )
      )) {
         if (this.hasWalkableFloor(
            level, x + (var15.component1() as java.lang.Number).doubleValue(), y, z + (var15.component2() as java.lang.Number).doubleValue()
         )) {
            score++
         }
      }
return score
   }

   private fun resetTopStuckTracking() {
      topStillSampleAtMs = 0L
      topStillSinceMs = 0L
      topStillSamplePos = null
   }

   private fun resetCenterRecovery() {
      centerRecoveryPhase = AutomaticDiscipline.CenterRecoveryPhase.NONE
      centerRecoveryGoal = null
      centerRecoveryStartedAt = null
   }

   fun followPathToTarget(player: ClientPlayerEntity, level: World, target: ZombieEntity) {
      if (!this.tryDirectChase(player, level, target, false)) {
         if (!this.tryConnectorLaneTraverse(player, level, target)) {

            val goal: WalkingPathfinder.Node = this.chooseSafeGoalNearTarget(level, player, target)
            if (goal == null) {
               this.stopMotion()
               this.clearPath()
               aimAtEntity$default(this, player, target, 0, 4, null)
            } else {
               if ((pathPoints.isEmpty() || pathTargetId == null || pathTargetId != target.getId() || nowTick - lastPathTick >= 10)
                  && nowTick - lastPathTick >= 5) {
                  this.computePath(level, player, target.getId(), goal)
               }

               if (pathPoints.isEmpty()) {
                  this.stopMotion()
                  aimAtEntity$default(this, player, target, 0, 4, null)
               } else {
                  while (pathIndex < getLastIndex(pathPoints)) {

                     if (!(this.horizontalDistance(var10001, pathPoints.get(pathIndex)) < 0.55)) {
break
                     }

                  }




                  if (dist < 0.25) {
                     this.clearPath()

                     if (!this.nudgeOutOfCompletedPathDeadzone(player, level, target)) {
                        this.stopMotion()
                        aimAtEntity$default(this, player, target, 0, 4, null)
                     }
                  } else {





                        && isMoveDirectionSafe$default(this, level, player, dirX, dirZ, false, null, null, false, 0.0, 352, null)


                     if (gapAhead && landing != null && this.isGapJumpSafe(level, player, dirX, dirZ)) {
                        this.aimAtPoint(player, landing.x, player.getEyeY(), landing.z, 48)

                                 MathHelper.wrapDegrees(
                                    this.yawTo(landing.x - player.getX(), landing.z - player.getZ()) - player.getYaw()
                                 )
                              )
                              <= 22.0F
                           && player.isOnGround()
                           && player.age >= nextJumpAllowedTick
                           PlayerController.pressSprint(ready)
                        player.setSprinting(ready)
                        PlayerController.pressForward(ready)
                        PlayerController.pressJump(ready)
                        PlayerController.pressBack(false)
                        PlayerController.pressLeft(false)
                        PlayerController.pressRight(false)
                        if (ready) {
                           nextJumpAllowedTick = player.age + 13
                        }
                     } else {


                        this.updateMovementProgress(player, this.horizontalDistance(var10003, var10004), !safeForward)
                        if (!this.tryCornerRecovery(player, level, target, dirX, dirZ, var29.x, var29.z)) {
                           if (landing != null) {
                              this.aimAtPoint(player, landing.x, player.getEyeY(), landing.z, 50)
                           } else {
                              this.aimAtPoint(player, var29.x, player.getEyeY(), var29.z, 55)
                           }

                              && dist > 0.95
                              && yawError <= 58.0F
                              && this.isSprintRunwaySafe(level, player, dirX, dirZ, false)

                           PlayerController.pressSprint(safeSprint)
                           player.setSprinting(safeSprint)
                           PlayerController.pressJump(jump)
                           PlayerController.pressBack(false)
                           PlayerController.pressLeft(false)
                           PlayerController.pressRight(false)
                           PlayerController.pressForward(safeForward)
                        }
                     }
                  }
               }
            }
         }
      }
   }

   fun nudgeOutOfCompletedPathDeadzone(player: ClientPlayerEntity, level: World, target: ZombieEntity): Boolean {



      if (dist <= 0.001) {
return false
      } else if (!(Math.abs(MathHelper.wrapDegrees(this.yawTo(dx * (1.0 / dist), dz * (1.0 / dist)) - player.getYaw())) <= 76.0F)
         || !isMoveDirectionSafe$default(this, level, player, dx * (1.0 / dist), dz * (1.0 / dist), false, 0.95, 0.12, false, 0.0, 256, null)) {
return false
      } else {
         this.aimAtEntity(player, target, 50)
         PlayerController.pressSprint(false)
         player.setSprinting(false)
         PlayerController.pressJump(false)
         PlayerController.pressBack(false)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressForward(true)
return true
      }
   }

   fun tryConnectorLaneTraverse(player: ClientPlayerEntity, level: World, target: ZombieEntity): Boolean {

      val var10000: AutomaticDiscipline.ConnectorLane = this.findConnectorLane(level, player, var10003)
      if (var10000 == null) {
return false
      } else {







            && var10000.wide
            && var10000.lateralError <= 0.38
            && var10000.clearDistance >= 3.35
            && yawError <= 13.0F
            && player.isOnGround()
            && player.age >= nextJumpAllowedTick
            this.resetCornerRecovery()
         this.aimAtPoint(player, aimX, player.getEyeY(), aimZ, if (jump) 38 else 58)
         PlayerController.pressSprint(sprint)
         player.setSprinting(sprint)
         PlayerController.pressJump(jump)
         PlayerController.pressBack(false)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressForward(move)
         if (jump) {
            nextJumpAllowedTick = player.age + 10
         }
return true
      }
   }

   fun findConnectorLane(level: World, player: ClientPlayerEntity, targetPos: Vec3d): AutomaticDiscipline.ConnectorLane {



      if (toTargetDist < 3.0) {
return null
      } else {
         var best: AutomaticDiscipline.ConnectorLane = null

         for (var14 in arrayOf(Pair(1.0, 0.0), Pair(-1.0, 0.0), Pair(0.0, 1.0), Pair(0.0, -1.0))) {



            if (!((dirX * toTargetX + dirZ * toTargetZ) / toTargetDist < 0.58)) {
               for (offset in -1..1) {



                  if (!(lateralError > 0.72)) {

                     if (!(clear < 2.65)) {
                        val profile: AutomaticDiscipline.ConnectorProfile = this.connectorProfile(level, player, dirX, dirZ, centerX, centerZ, clear)
                        if (profile.edgeSamples >= 2) {

                           val candidate: AutomaticDiscipline.ConnectorLane = AutomaticDiscipline.ConnectorLane(
                              dirX,
                              dirZ,
                              centerX,
                              centerZ,
                              clear,
                              lateralError,
                              wide,
                              lateralError * 3.0 - targetDot * 8.0 - clear * 0.15 - (if (wide) 2.5 else 0.0)
                           )
                           if (best == null || candidate.score < best.score) {
                              best = candidate
                           }
                        }
                     }
                  }
               }
            }
         }
return best
      }
   }

   fun connectorClearDistance(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double, centerX: Double, centerZ: Double): Double {
      var clear: Double = 0.0

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var forward: Double = 0.35
      while (true) {
         if (forward <= 5.25) break
         if (!this.hasWalkableFloor(
            level,
            if (Math.abs(dirX) > 0.5) player.getX() + dirX * forward else centerX,
            player.getY(),
            if (Math.abs(dirZ) > 0.5) player.getZ() + dirZ * forward else centerZ
         )) {
break
         }

         clear = forward

         forward += 0.35
      }
return clear
   }

   fun connectorProfile(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double, centerX: Double, centerZ: Double, clearDistance: Double): AutomaticDiscipline.ConnectorProfile {


      var edgeSamples: Int = 0
      var wideSamples: Int = 0
      var checkedSamples: Int = 0
      var forward: Double = 0.75

      // $VF: Unable to resugar Kotlin loop from Java for loop

      while (true) {
         if (forward <= maxForward) break






         checkedSamples++
         if (leftWide && rightWide) {
            wideSamples++
         }

         if (!leftEdge || !rightEdge) {
            edgeSamples++
         }

         forward += 0.55
      }

      AutomaticDiscipline.ConnectorProfile(edgeSamples, wideSamples, checkedSamples)
   }

   fun isConnectorCenterlineSafe(level: World, player: ClientPlayerEntity, lane: AutomaticDiscipline.ConnectorLane): Boolean {
      var forward: Double = 0.35

      // $VF: Unable to resugar Kotlin loop from Java for loop

      while (true) {
         if (forward <= maxForward) break
         if (!this.hasWalkableFloor(
            level,
            if (Math.abs(lane.dirX) > 0.5) player.getX() + lane.dirX * forward else lane.centerX,
            player.getY(),
            if (Math.abs(lane.dirZ) > 0.5) player.getZ() + lane.dirZ * forward else lane.centerZ
         )) {
return false
         }

         forward += 0.35
      }
return true
   }

   fun tryDirectChase(player: ClientPlayerEntity, level: World, target: ZombieEntity, allowRecovery: Boolean): Boolean {



      if (dist <= 0.001) {
return false
      } else {





         this.updateMovementProgress(player, dist, !safeForward)
         if (!safeForward) {
            allowRecovery && this.tryCornerRecovery(player, level, target, dirX, dirZ, target.getX(), target.getZ())
         } else {


            this.clearPath()
            this.aimAtEntity(player, target, 50)
            PlayerController.pressSprint(safeSprint)
            player.setSprinting(safeSprint)
            PlayerController.pressJump(jump)
            PlayerController.pressBack(false)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressForward(true)
return true
         }
      }
   }

   fun nudgeTowardTarget(player: ClientPlayerEntity, level: World, target: ZombieEntity): Boolean {



      if (dist <= 0.001) {
         this.stopMotion()
return true
      } else {





         if (!directWalkSafe && dist > 1.05) {
return false
         } else {


               && yawError <= 76.0F
               && directWalkSafe
               && dist > 1.85
               && yawError <= 48.0F
               && this.isSprintRunwaySafe(level, player, dirX, dirZ, false)

            PlayerController.pressSprint(safeSprint)
            player.setSprinting(safeSprint)
            PlayerController.pressJump(jump)
            PlayerController.pressBack(false)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressForward(shouldNudge)
return true
         }
      }
   }

   fun computePath(level: World, player: ClientPlayerEntity, targetId: Int, goal: WalkingPathfinder.Node) {


      val result: WalkingPathfinder.Result = WalkingPathfinder.INSTANCE
         .findPath(level, var10000.fromBlockPosFeet(var10001), goal, pathVariantSeed++, pathOptions)
         lastPathTick = player.age
      if (!result.success) {
         this.clearPath()
      } else {
         pathTargetId = targetId
         pathPoints = PathSpline.generateSpline(result.steps, 0.75)
         var var9: Int = 0
         val var10: java.util.Iterator = pathPoints.iterator()

         while (true) {
            if (!var10.hasNext()) {
               var19 = -1
break
            }



            if (var18.horizontalDistance(var20, it) > 0.55) {
               var19 = var9
break
            }

            var9++
         }

         pathIndex = if (var19 < 0) getLastIndex(pathPoints) else (var19).coerceIn(0, getLastIndex(pathPoints))
      }
   }

   fun chooseSafeGoalNearTarget(level: World, player: ClientPlayerEntity, target: ZombieEntity): WalkingPathfinder.Node {



      var best: WalkingPathfinder.Node = null
      var bestScore: Double = java.lang.Double.MAX_VALUE

      repeat(4) label73@{ radius ->
         var dx: Int = -radius
         if (-radius <= radius) {
            while (true) {
               var dz: Int = -radius
               if (-radius <= radius) {
                  while (true) {
                     if (Math.max(Math.abs(dx), Math.abs(dz)) == radius) {
                        for (dy in -1..1) {
                           val node: WalkingPathfinder.Node = WalkingPathfinder.Node(baseX + dx, baseY + dy, baseZ + dz)
                           if (this.isSafeStandable(level, node)) {





                              if (score < bestScore) {
                                 bestScore = score
                                 best = node
                              }
                           }
                        }
                     }

                     if (dz == radius) {
break
                     }

                     dz++
                  }
               }

               if (dx == radius) {
                  continue@label73
               }

               dx++
            }
         }
      }
return best
   }

   fun isSafeStandable(level: World, node: WalkingPathfinder.Node): Boolean {
      this.isPassable(level, node.x, node.y, node.z)
         && this.isPassable(level, node.x, node.y + 1, node.z)
         && this.hasSolidFloor(level, node.x, node.y - 1, node.z)
      }

   fun shouldSprintJump(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double, dist: Double, yawError: Float, safeSprint: Boolean): Boolean {

      if (!safeSprint || dist < 5.25 || yawError > 16.0F) {
return false
      } else if (!player.isOnGround() || tick < nextJumpAllowedTick) {
return false
      } else if (!this.isInteriorRunwaySafe(level, player, dirX, dirZ)) {
return false
      } else {
         nextJumpAllowedTick = tick + 13
return true
      }
   }

   fun isSprintRunwaySafe(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double, avoidAvoidedPolishedAndesite: Boolean): Boolean {
      isMoveDirectionSafe$default(this, level, player, dirX, dirZ, true, 2.75, 0.08, avoidAvoidedPolishedAndesite, 0.0, 256, null)
   }

   fun isInteriorRunwaySafe(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double): Boolean {
      isMoveDirectionSafe$default(this, level, player, dirX, dirZ, true, 4.1, 0.52, false, 0.0, 256, null)
   }

   fun isMoveDirectionSafe(
      level: World,
      player: ClientPlayerEntity,
      dirX: Double,
      dirZ: Double,
      sprinting: Boolean,
      maxForward: Double,
      sideClearance: Double,
      avoidAvoidedPolishedAndesite: Boolean,
      ignoreAvoidedFloorUntil: Double
   ): Boolean {





      val var10000: java.util.List = if (sprinting)
         listOf(arrayOf(0.45, 0.95, 1.45, 1.95, maxForward ?: 2.35))
return else
         listOf(arrayOf(0.35, 0.7, 1.1, 1.45))

      val var42: java.util.Iterator = var10000.iterator()

      while (var42.hasNext()) {



         if (!this.hasWalkableFloor(level, centerX, py, pz + dirZ * forward)) {
return false
         }

         if (avoidAvoidedPolishedAndesite && forward > ignoreAvoidedFloorUntil && this.isAvoidedPolishedAndesiteFloor(level, centerX, py, centerZ)) {
return false
         }

         if (sprinting) {
            val var33: java.util.Iterator = listOf(arrayOf(-var41, var41)).iterator()

            while (var33.hasNext()) {



               if (!this.hasWalkableFloor(level, x, py, centerZ + sideZ * var43)) {
return false
               }

               if (avoidAvoidedPolishedAndesite && forward > ignoreAvoidedFloorUntil && this.isAvoidedPolishedAndesiteFloor(level, x, py, z)) {
return false
               }
            }
         }
      }
return true
   }

   fun isAvoidedPolishedAndesiteFloor(level: World, x: Double, y: Double, z: Double): Boolean {




      for (dy in 0 downTo -2) {



         if (!var10000.getCollisionShape(level as BlockView, pos, ShapeContext.absent()).isEmpty()) {
            if (!(var10000.getBlock() == Blocks.POLISHED_ANDESITE)) {
return false
            }

            !this.isNearChiseledStoneBricks(level, bx, floorY, bz)
         }
      }
return false
   }

   fun isNearChiseledStoneBricks(level: World, x: Int, y: Int, z: Int): Boolean {
      for (dx in -1..1) {
         for (dz in -1..1) {
            if ((dx != 0 || dz != 0) && level.getBlockState(BlockPos(x + dx, y, z + dz)).getBlock() == Blocks.CHISELED_STONE_BRICKS) {
return true
            }
         }
      }
return false
   }

   fun hasWalkableFloor(level: World, x: Double, y: Double, z: Double): Boolean {




      for (dy in 0 downTo -2) {
         if (this.hasSolidFloor(level, bx, topFloor + dy, bz)) {
return true
         }
      }
return false
   }

   fun hasFloorBelow(level: World, x: Double, y: Double, z: Double): Boolean {
      this.hasWalkableFloor(level, x, y, z)
   }

   fun priorityTarget(player: ClientPlayerEntity): ZombieEntity {

      var var10000: ZombieEntity = this.nearestTarget(player)
      if (var10000 == null) {
return null
      } else {
         run label106@{
            if (currentTargetId != null) {
               run label102@{


                  if (var21 != null) {
                     val var22: java.lang.Iterable = var21.getEntities()
                     if (var22 != null) {
                        val var12: java.util.Iterator = var22.iterator()

                        while (true) {
                           if (!var12.hasNext()) {
                              var24 = null
break
                           }

                           val `element$iv`: Any = var12.next()
                           if ((`element$iv` as Entity).getId() == it && `element$iv` as Entity is ZombieEntity) {
                              var24 = `element$iv`
break
                           }
                        }

                        var23 = var24 as Entity
                        return@label102
                     }
                  }

                  var23 = null
               }

               if ((var23 as? ZombieEntity) != null) {
                  var10000 = if (isValidTarget(var6) && isSameYLevel(targetY, var6)) var6 else null
                  return@label106
               }
            }

            var10000 = null
         }

         if (var10000 != null && var10000.getId() != var10000.getId()) {
            if (player.squaredDistanceTo(var10000 as Entity) <= player.squaredDistanceTo(var10000 as Entity) * 0.48) var10000 else var10000
         } else {
return var10000
         }
      }
   }

   fun ensureSword(player: ClientPlayerEntity, target: ZombieEntity) {


      if (var5 != null) {

         if (player.getInventory().getSelectedSlot() != targetSlot) {
            player.getInventory().setSelectedSlot(targetSlot)
            PlayerController.noteHotbarSwapThisTick()
            swappedAtTick = player.age
         }
      }
   }

   fun tryAttack(player: ClientPlayerEntity, target: ZombieEntity) {
      if (player.age - swappedAtTick > 1) {
         if (player.age - lastClickTick >= 2) {
            if (this.isCrosshairOnTarget(target)) {
               if (PlayerController.leftClick()) {
                  lastClickTick = player.age
               }
            }
         }
      }
   }

   fun canAttemptAttack(player: ClientPlayerEntity, target: ZombieEntity): Boolean {
      this.isNearAttackRange(player, target) && this.isCrosshairOnTarget(target)
   }

   fun isNearAttackRange(player: ClientPlayerEntity, target: ZombieEntity): Boolean {
      player.canAttackEntityIn(target.getBoundingBox(), 0.15) && player.distanceTo(target as Entity) <= 3.15
   }

   fun isCrosshairOnTarget(target: ZombieEntity): Boolean {

      (var3 as? EntityHitResult) != null && (var3 as? EntityHitResult).getEntity().getId() == target.getId()
   }

   fun aimAtEntity(player: ClientPlayerEntity, target: ZombieEntity, durationBaseMs: Int) {

      this.requestAim(player, (var4.component1() as java.lang.Number).floatValue(), (var4.component2() as java.lang.Number).floatValue(), durationBaseMs)
   }

   fun aimAtPoint(player: ClientPlayerEntity, x: Double, y: Double, z: Double, durationBaseMs: Int) {



      this.requestAim(
         player,
         Math.toDegrees(Math.atan2(-dx, dz)).toFloat(),
         Math.toDegrees(-Math.atan2(dy, (Math.sqrt(dx * dx + dz * dz)).coerceAtLeast(0.001))).toFloat(),
return durationBaseMs
      )
   }

   fun requestAim(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, durationBaseMs: Int) {

      val plan: AutomaticDiscipline.AimPlan = currentAimPlan

         java.lang.Float.MAX_VALUE
return else
         Math.hypot(MathHelper.wrapDegrees(targetYaw - currentAimPlan.targetYaw).toDouble(), (targetPitch - plan.targetPitch).toDouble()).toFloat()
         if (currentAimPlan == null
         || currentAimPlan == null
         || now - currentAimPlan.startAtMs >= plan.durationMs
         || targetShift > 9.0F && now - currentAimPlan.startAtMs >= 55L) {
         currentAimPlan = this.createAimPlan(
            player.getYaw(), player.getPitch(), targetYaw, (targetPitch).coerceIn(-89.9F, 89.9F), durationBaseMs, now
         )
      }

      SyntheticMouseTurnBroker.claim("dojo_automatic_discipline", { 
         getSyntheticMouseTurn()
      })
   }

   private fun getSyntheticMouseTurn(): TurnDelta? {
      if (active && Config.fullyAutomaticDiscipline) {

         if (var10000 == null) {
            return null
         } else if (currentAimPlan == null) {
            return null
         } else {
            val plan: AutomaticDiscipline.AimPlan = currentAimPlan
            val sample: AutomaticDiscipline.AimSample = this.sampleAim(currentAimPlan, System.currentTimeMillis())



            return if (Math.abs(yawDelta) < 0.01 && Math.abs(pitchDelta) < 0.01)
return null
return else
               SyntheticMouseTurnBroker.TurnDelta(yawDelta / stepDeg, pitchDelta / stepDeg)
            }
      } else {
         return null
      }
   }

   private fun createAimPlan(startYaw: Float, startPitch: Float, targetYaw: Float, targetPitch: Float, durationBaseMs: Int, nowMs: Long): jooon.features.dojo.AutomaticDiscipline.AimPlan {





      var `this24lambda_u2424`: Any
      try {
         `this24lambda_u2424` = Result.constructor_impl/* $VF was: constructor-impl */(
            var14.getMc().options.getMouseSensitivity().getValue() as Double
         )
      } catch (var17: java.lang.Throwable) {
         `this24lambda_u2424` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var17))
      }

         0.5
return else
         `this24lambda_u2424`
         return AutomaticDiscipline.AimPlan(
         startYaw,
         startPitch,
         targetYaw,
         targetPitch,
         deltaYaw,
         deltaPitch,
         deltaYaw * 0.34F,
         deltaPitch * 0.34F,
         deltaYaw * 0.72F,
         deltaPitch * 0.72F,
         nowMs,
         duration,
         this.sensitivityStepDeg((var10000 as java.lang.Number).doubleValue())
      )
   }

   private fun sampleAim(plan: jooon.features.dojo.AutomaticDiscipline.AimPlan, nowMs: Long): jooon.features.dojo.AutomaticDiscipline.AimSample {

      if (elapsed >= plan.durationMs) {
         return AutomaticDiscipline.AimSample(MathHelper.wrapDegrees(plan.targetYaw), (plan.targetPitch).coerceIn(-89.9F, 89.9F))
      } else {

         return AutomaticDiscipline.AimSample(
            MathHelper.wrapDegrees(
               plan.startYaw + this.cubicBezier(0.0, plan.controlYawOne.toDouble(), plan.controlYawTwo.toDouble(), plan.deltaYaw.toDouble(), eased).toFloat()
            ),
            (plan.startPitch + this.cubicBezier(0.0, plan.controlPitchOne.toDouble(), plan.controlPitchTwo.toDouble(), plan.deltaPitch.toDouble(), eased).toFloat()).coerceIn(-89.9F, 89.9F)
         )
      }
   }

   private fun cubicBezier(p0: Double, p1: Double, p2: Double, p3: Double, t: Double): Double {
      return (1.0 - t) * (1.0 - t) * (1.0 - t) * p0 + 3.0 * (1.0 - t) * (1.0 - t) * t * p1 + 3.0 * (1.0 - t) * t * t * p2 + t * t * t * p3
   }

   private fun sensitivityStepDeg(sensitivity: Double): Float {

      return ((scaled * scaled * scaled * 1.2).toFloat()).coerceAtLeast(0.01F)
   }

   fun isValidTarget(zombie: ZombieEntity): Boolean {
      if (presumedKilledIds.contains(zombie.getId())) {
return false
      } else if (zombie.isAlive() && !zombie.isRemoved()) {

         Discipline.requiredSwordForHelmet$JooonReimagined_noMidnightLib(var10000) != null
      } else {
return false
      }
   }

   fun updatePlayerTargetYLevel(level: World, player: ClientPlayerEntity) {

      if (var10000 != null) {
         playerTargetYLevel = var10000.intValue()
      }

      if (playerTargetYLevel == null) {
         playerTargetYLevel = MathHelper.floor(player.getY())
      }
   }

   fun detectPlatformY(level: World, player: ClientPlayerEntity): Int {



      var feetY: Int = feet

      if (feet - 4 <= feet) {
         while (true) {
            if (this.hasSolidFloor(level, x, feetY - 1, z)) {
return feetY
            }

            if (feetY == var7) {
break
            }

            feetY--
         }
      }
return null
   }

   fun isSameYLevel(targetY: Int, zombie: ZombieEntity): Boolean {
      MathHelper.floor(zombie.getY()) == targetY
   }

   fun isPassable(level: World, x: Int, y: Int, z: Int): Boolean {


      var10000.getCollisionShape(level as BlockView, pos, ShapeContext.absent()).isEmpty()
   }

   fun hasSolidFloor(level: World, x: Int, y: Int, z: Int): Boolean {


      !var10000.getCollisionShape(level as BlockView, pos, ShapeContext.absent()).isEmpty()
   }

   fun aimAngles(player: ClientPlayerEntity, target: ZombieEntity): Pair<Float, Float> {



      Pair(Math.toDegrees(Math.atan2(-dx, dz)).toFloat(), Math.toDegrees(-Math.atan2(dy, (Math.sqrt(dx * dx + dz * dz)).coerceAtLeast(0.001))).toFloat())
   }

   private fun yawTo(dx: Double, dz: Double): Float {
      return Math.toDegrees(Math.atan2(-dx, dz)).toFloat()
   }

   fun horizontalDistance(a: Vec3d, b: Vec3d): Double {
      Math.hypot(a.x - b.x, a.z - b.z)
   }

   private fun clearPath() {
      pathTargetId = null
      pathPoints = emptyList()
      pathIndex = 0
   }

   private fun requestIdleAimRelease() {
      currentAimPlan = null
      SyntheticMouseTurnBroker.release("dojo_automatic_discipline")
   }

   private fun clearAim() {
      currentAimPlan = null
      SyntheticMouseTurnBroker.release("dojo_automatic_discipline")
   }

   private fun reset() {
      active = false
      isPaused = false
      DojoPauseInput.reset()
      currentTargetId = null
      lastObservedAttackEntityId = -1
      presumedKilledIds.clear()
      playerTargetYLevel = null
      this.resetCornerRecovery()
      this.resetCenterRecovery()
      this.resetTopStuckTracking()
      this.clearPath()
      this.clearAim()
      this.stopMotion()
   }

   private fun pauseFully() {
      currentTargetId = null
      lastObservedAttackEntityId = -1
      this.clearPath()
      this.resetCornerRecovery()
      this.resetCenterRecovery()
      this.resetTopStuckTracking()
      this.clearAim()
      this.stopMotion()
   }

   private fun stopMotion() {
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressJump(false)
      PlayerController.pressSneak(false)
      PlayerController.pressSprint(false)

      if (var10000 != null) {
         var10000.setSprinting(false)
      }
   }

   private data class AimPlan(startYaw: Float,
      startPitch: Float,
      targetYaw: Float,
      targetPitch: Float,
      deltaYaw: Float,
      deltaPitch: Float,
      controlYawOne: Float,
      controlPitchOne: Float,
      controlYawTwo: Float,
      controlPitchTwo: Float,
      startAtMs: Long,
      durationMs: Int,
      sensitivityStepDeg: Float
   ) {
      val startYaw: Float
      val startPitch: Float
      val targetYaw: Float
      val targetPitch: Float
      val deltaYaw: Float
      val deltaPitch: Float
      val controlYawOne: Float
      val controlPitchOne: Float
      val controlYawTwo: Float
      val controlPitchTwo: Float
      val startAtMs: Long
      val durationMs: Int
      val sensitivityStepDeg: Float

      init {
         this.startYaw = startYaw
         this.startPitch = startPitch
         this.targetYaw = targetYaw
         this.targetPitch = targetPitch
         this.deltaYaw = deltaYaw
         this.deltaPitch = deltaPitch
         this.controlYawOne = controlYawOne
         this.controlPitchOne = controlPitchOne
         this.controlYawTwo = controlYawTwo
         this.controlPitchTwo = controlPitchTwo
         this.startAtMs = startAtMs
         this.durationMs = durationMs
         this.sensitivityStepDeg = sensitivityStepDeg
      }

      public operator fun component1(): Float {
         return this.startYaw
      }

      public operator fun component2(): Float {
         return this.startPitch
      }

      public operator fun component3(): Float {
         return this.targetYaw
      }

      public operator fun component4(): Float {
         return this.targetPitch
      }

      public operator fun component5(): Float {
         return this.deltaYaw
      }

      public operator fun component6(): Float {
         return this.deltaPitch
      }

      public operator fun component7(): Float {
         return this.controlYawOne
      }

      public operator fun component8(): Float {
         return this.controlPitchOne
      }

      public operator fun component9(): Float {
         return this.controlYawTwo
      }

      public operator fun component10(): Float {
         return this.controlPitchTwo
      }

      public operator fun component11(): Long {
         return this.startAtMs
      }

      public operator fun component12(): Int {
         return this.durationMs
      }

      public operator fun component13(): Float {
         return this.sensitivityStepDeg
      }

      fun copy(
         startYaw: Float = this.startYaw,
         startPitch: Float = this.startPitch,
         targetYaw: Float = this.targetYaw,
         targetPitch: Float = this.targetPitch,
         deltaYaw: Float = this.deltaYaw,
         deltaPitch: Float = this.deltaPitch,
         controlYawOne: Float = this.controlYawOne,
         controlPitchOne: Float = this.controlPitchOne,
         controlYawTwo: Float = this.controlYawTwo,
         controlPitchTwo: Float = this.controlPitchTwo,
         startAtMs: Long = this.startAtMs,
         durationMs: Int = this.durationMs,
         sensitivityStepDeg: Float = this.sensitivityStepDeg
      ): jooon.features.dojo.AutomaticDiscipline.AimPlan {
         return AutomaticDiscipline.AimPlan(
            startYaw,
            startPitch,
            targetYaw,
            targetPitch,
            deltaYaw,
            deltaPitch,
            controlYawOne,
            controlPitchOne,
            controlYawTwo,
            controlPitchTwo,
            startAtMs,
            durationMs,
return sensitivityStepDeg
         )
      }

      override fun toString(): String {
         return "AimPlan(startYaw=${this.startYaw}, startPitch=${this.startPitch}, targetYaw=${this.targetYaw}, targetPitch=${this.targetPitch}, deltaYaw=${this.deltaYaw}, deltaPitch=${this.deltaPitch}, controlYawOne=${this.controlYawOne}, controlPitchOne=${this.controlPitchOne}, controlYawTwo=${this.controlYawTwo}, controlPitchTwo=${this.controlPitchTwo}, startAtMs=${this.startAtMs}, durationMs=${this.durationMs}, sensitivityStepDeg=${this.sensitivityStepDeg})"
      }

      override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (
                                                               (
                                                                        (
                                                                                 (
                                                                                          (
                                                                                                   (
                                                                                                            java.lang.Float.hashCode(this.startYaw) * 31
                                                                                                               + java.lang.Float.hashCode(this.startPitch)
                                                                                                         )
                                                                                                         * 31
                                                                                                      + java.lang.Float.hashCode(this.targetYaw)
                                                                                                )
                                                                                                * 31
                                                                                             + java.lang.Float.hashCode(this.targetPitch)
                                                                                       )
                                                                                       * 31
                                                                                    + java.lang.Float.hashCode(this.deltaYaw)
                                                                              )
                                                                              * 31
                                                                           + java.lang.Float.hashCode(this.deltaPitch)
                                                                     )
                                                                     * 31
                                                                  + java.lang.Float.hashCode(this.controlYawOne)
                                                            )
                                                            * 31
                                                         + java.lang.Float.hashCode(this.controlPitchOne)
                                                   )
                                                   * 31
                                                + java.lang.Float.hashCode(this.controlYawTwo)
                                          )
                                          * 31
                                       + java.lang.Float.hashCode(this.controlPitchTwo)
                                 )
                                 * 31
                              + java.lang.Long.hashCode(this.startAtMs)
                        )
                        * 31
                     + Integer.hashCode(this.durationMs)
               )
               * 31
            + java.lang.Float.hashCode(this.sensitivityStepDeg)
         }

      override operator fun equals(other: Any?): Boolean {
         label94@
         if (this === other) {
            return true
         } else {
            return other is AutomaticDiscipline.AimPlan
               && java.lang.Float.compare(this.startYaw, (other as AutomaticDiscipline.AimPlan).startYaw) == 0
               && java.lang.Float.compare(this.startPitch, (other as AutomaticDiscipline.AimPlan).startPitch) == 0
               && java.lang.Float.compare(this.targetYaw, (other as AutomaticDiscipline.AimPlan).targetYaw) == 0
               && java.lang.Float.compare(this.targetPitch, (other as AutomaticDiscipline.AimPlan).targetPitch) == 0
               && java.lang.Float.compare(this.deltaYaw, (other as AutomaticDiscipline.AimPlan).deltaYaw) == 0
               && java.lang.Float.compare(this.deltaPitch, (other as AutomaticDiscipline.AimPlan).deltaPitch) == 0
               && java.lang.Float.compare(this.controlYawOne, (other as AutomaticDiscipline.AimPlan).controlYawOne) == 0
               && java.lang.Float.compare(this.controlPitchOne, (other as AutomaticDiscipline.AimPlan).controlPitchOne) == 0
               && java.lang.Float.compare(this.controlYawTwo, (other as AutomaticDiscipline.AimPlan).controlYawTwo) == 0
               && java.lang.Float.compare(this.controlPitchTwo, (other as AutomaticDiscipline.AimPlan).controlPitchTwo) == 0
               && this.startAtMs == (other as AutomaticDiscipline.AimPlan).startAtMs
               && this.durationMs == (other as AutomaticDiscipline.AimPlan).durationMs
               && java.lang.Float.compare(this.sensitivityStepDeg, (other as AutomaticDiscipline.AimPlan).sensitivityStepDeg) == 0
            }
      }
   }

   private data class AimSample(yaw: Float, pitch: Float) {
      val yaw: Float
      val pitch: Float

      init {
         this.yaw = yaw
         this.pitch = pitch
      }

      public operator fun component1(): Float {
         return this.yaw
      }

      public operator fun component2(): Float {
         return this.pitch
      }

      fun copy(yaw: Float = this.yaw, pitch: Float = this.pitch): jooon.features.dojo.AutomaticDiscipline.AimSample {
         return AutomaticDiscipline.AimSample(yaw, pitch)
      }

      override fun toString(): String {
         return "AimSample(yaw=${this.yaw}, pitch=${this.pitch})"
      }

      override fun hashCode(): Int {
         return java.lang.Float.hashCode(this.yaw) * 31 + java.lang.Float.hashCode(this.pitch)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutomaticDiscipline.AimSample
               && java.lang.Float.compare(this.yaw, (other as AutomaticDiscipline.AimSample).yaw) == 0
               && java.lang.Float.compare(this.pitch, (other as AutomaticDiscipline.AimSample).pitch) == 0
            }
      }
   }

   private enum class CenterRecoveryPhase {
      NONE,
      RETREATING;

      
      fun getEntries(): EnumEntries<AutomaticDiscipline.CenterRecoveryPhase> {
         $ENTRIES
      }
   }

   private data class ConnectorLane(dirX: Double,
      dirZ: Double,
      centerX: Double,
      centerZ: Double,
      clearDistance: Double,
      lateralError: Double,
      wide: Boolean,
      score: Double
   ) {
      val dirX: Double
      val dirZ: Double
      val centerX: Double
      val centerZ: Double
      val clearDistance: Double
      val lateralError: Double
      val wide: Boolean
      val score: Double

      init {
         this.dirX = dirX
         this.dirZ = dirZ
         this.centerX = centerX
         this.centerZ = centerZ
         this.clearDistance = clearDistance
         this.lateralError = lateralError
         this.wide = wide
         this.score = score
      }

      public operator fun component1(): Double {
         return this.dirX
      }

      public operator fun component2(): Double {
         return this.dirZ
      }

      public operator fun component3(): Double {
         return this.centerX
      }

      public operator fun component4(): Double {
         return this.centerZ
      }

      public operator fun component5(): Double {
         return this.clearDistance
      }

      public operator fun component6(): Double {
         return this.lateralError
      }

      public operator fun component7(): Boolean {
         return this.wide
      }

      public operator fun component8(): Double {
         return this.score
      }

      fun copy(
         dirX: Double = this.dirX,
         dirZ: Double = this.dirZ,
         centerX: Double = this.centerX,
         centerZ: Double = this.centerZ,
         clearDistance: Double = this.clearDistance,
         lateralError: Double = this.lateralError,
         wide: Boolean = this.wide,
         score: Double = this.score
      ): jooon.features.dojo.AutomaticDiscipline.ConnectorLane {
         return AutomaticDiscipline.ConnectorLane(dirX, dirZ, centerX, centerZ, clearDistance, lateralError, wide, score)
      }

      override fun toString(): String {
         return "ConnectorLane(dirX=${this.dirX}, dirZ=${this.dirZ}, centerX=${this.centerX}, centerZ=${this.centerZ}, clearDistance=${this.clearDistance}, lateralError=${this.lateralError}, wide=${this.wide}, score=${this.score})"
      }

      override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (java.lang.Double.hashCode(this.dirX) * 31 + java.lang.Double.hashCode(this.dirZ)) * 31
                                                         + java.lang.Double.hashCode(this.centerX)
                                                   )
                                                   * 31
                                                + java.lang.Double.hashCode(this.centerZ)
                                          )
                                          * 31
                                       + java.lang.Double.hashCode(this.clearDistance)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.lateralError)
                        )
                        * 31
                     + java.lang.Boolean.hashCode(this.wide)
               )
               * 31
            + java.lang.Double.hashCode(this.score)
         }

      override operator fun equals(other: Any?): Boolean {
         label64@
         if (this === other) {
            return true
         } else {
            return other is AutomaticDiscipline.ConnectorLane
               && java.lang.Double.compare(this.dirX, (other as AutomaticDiscipline.ConnectorLane).dirX) == 0
               && java.lang.Double.compare(this.dirZ, (other as AutomaticDiscipline.ConnectorLane).dirZ) == 0
               && java.lang.Double.compare(this.centerX, (other as AutomaticDiscipline.ConnectorLane).centerX) == 0
               && java.lang.Double.compare(this.centerZ, (other as AutomaticDiscipline.ConnectorLane).centerZ) == 0
               && java.lang.Double.compare(this.clearDistance, (other as AutomaticDiscipline.ConnectorLane).clearDistance) == 0
               && java.lang.Double.compare(this.lateralError, (other as AutomaticDiscipline.ConnectorLane).lateralError) == 0
               && this.wide == (other as AutomaticDiscipline.ConnectorLane).wide
               && java.lang.Double.compare(this.score, (other as AutomaticDiscipline.ConnectorLane).score) == 0
            }
      }
   }

   private data class ConnectorProfile(edgeSamples: Int, wideSamples: Int, checkedSamples: Int) {
      val edgeSamples: Int
      val wideSamples: Int
      val checkedSamples: Int

      init {
         this.edgeSamples = edgeSamples
         this.wideSamples = wideSamples
         this.checkedSamples = checkedSamples
      }

      public operator fun component1(): Int {
         return this.edgeSamples
      }

      public operator fun component2(): Int {
         return this.wideSamples
      }

      public operator fun component3(): Int {
         return this.checkedSamples
      }

      fun copy(edgeSamples: Int = this.edgeSamples, wideSamples: Int = this.wideSamples, checkedSamples: Int = this.checkedSamples): jooon.features.dojo.AutomaticDiscipline.ConnectorProfile {
         return AutomaticDiscipline.ConnectorProfile(edgeSamples, wideSamples, checkedSamples)
      }

      override fun toString(): String {
         return "ConnectorProfile(edgeSamples=${this.edgeSamples}, wideSamples=${this.wideSamples}, checkedSamples=${this.checkedSamples})"
      }

      override fun hashCode(): Int {
         return (Integer.hashCode(this.edgeSamples) * 31 + Integer.hashCode(this.wideSamples)) * 31 + Integer.hashCode(this.checkedSamples)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is AutomaticDiscipline.ConnectorProfile
               && this.edgeSamples == (other as AutomaticDiscipline.ConnectorProfile).edgeSamples
               && this.wideSamples == (other as AutomaticDiscipline.ConnectorProfile).wideSamples
               && this.checkedSamples == (other as AutomaticDiscipline.ConnectorProfile).checkedSamples
            }
      }
   }

   private enum class CornerRecoveryPhase {
      NONE,
      BACKING,
      COMMIT_JUMP;

      
      fun getEntries(): EnumEntries<AutomaticDiscipline.CornerRecoveryPhase> {
         $ENTRIES
      }
   }

   private data class RecoveryJumpPlan(dirX: Double, dirZ: Double, aimX: Double, aimZ: Double, needsGapJump: Boolean) {
      val dirX: Double
      val dirZ: Double
      val aimX: Double
      val aimZ: Double
      val needsGapJump: Boolean

      init {
         this.dirX = dirX
         this.dirZ = dirZ
         this.aimX = aimX
         this.aimZ = aimZ
         this.needsGapJump = needsGapJump
      }

      public operator fun component1(): Double {
         return this.dirX
      }

      public operator fun component2(): Double {
         return this.dirZ
      }

      public operator fun component3(): Double {
         return this.aimX
      }

      public operator fun component4(): Double {
         return this.aimZ
      }

      public operator fun component5(): Boolean {
         return this.needsGapJump
      }

      fun copy(
         dirX: Double = this.dirX,
         dirZ: Double = this.dirZ,
         aimX: Double = this.aimX,
         aimZ: Double = this.aimZ,
         needsGapJump: Boolean = this.needsGapJump
      ): jooon.features.dojo.AutomaticDiscipline.RecoveryJumpPlan {
         return AutomaticDiscipline.RecoveryJumpPlan(dirX, dirZ, aimX, aimZ, needsGapJump)
      }

      override fun toString(): String {
         return "RecoveryJumpPlan(dirX=${this.dirX}, dirZ=${this.dirZ}, aimX=${this.aimX}, aimZ=${this.aimZ}, needsGapJump=${this.needsGapJump})"
      }

      override fun hashCode(): Int {
         return (
                  ((java.lang.Double.hashCode(this.dirX) * 31 + java.lang.Double.hashCode(this.dirZ)) * 31 + java.lang.Double.hashCode(this.aimX)) * 31
                     + java.lang.Double.hashCode(this.aimZ)
               )
               * 31
            + java.lang.Boolean.hashCode(this.needsGapJump)
         }

      override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is AutomaticDiscipline.RecoveryJumpPlan
               && java.lang.Double.compare(this.dirX, (other as AutomaticDiscipline.RecoveryJumpPlan).dirX) == 0
               && java.lang.Double.compare(this.dirZ, (other as AutomaticDiscipline.RecoveryJumpPlan).dirZ) == 0
               && java.lang.Double.compare(this.aimX, (other as AutomaticDiscipline.RecoveryJumpPlan).aimX) == 0
               && java.lang.Double.compare(this.aimZ, (other as AutomaticDiscipline.RecoveryJumpPlan).aimZ) == 0
               && this.needsGapJump == (other as AutomaticDiscipline.RecoveryJumpPlan).needsGapJump
            }
      }
   }
}
