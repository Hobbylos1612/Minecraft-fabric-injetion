package jooon.features.dojo

import com.mojang.logging.LogUtils
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Locale
import jooon.config.Config
import jooon.features.dojo.StaminaMovementLogic.ApproachPhase
import jooon.features.dojo.StaminaMovementLogic.Axis
import jooon.features.dojo.StaminaMovementLogic.HoleDirection
import jooon.features.dojo.StaminaMovementLogic.Pass
import jooon.features.dojo.StaminaMovementLogic.Point
import jooon.pathfinding.voxel.VoxelJumpProfile
import jooon.util.PlayerController
import jooon.util.SyntheticMouseTurnBroker
import jooon.util.SyntheticMouseTurnBroker.TurnDelta
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView
import net.minecraft.world.World
import org.slf4j.Logger

object AutomaticStamina {
   private val logger: Logger = LogUtils.getLogger()
   private const val SYNTHETIC_TURN_OWNER: String = "dojo_automatic_stamina"
   private const val DEBUG_LOGS: Boolean = true
   private const val DEBUG_LOG_INTERVAL_TICKS: Int = 5
   private const val WALL_THRESHOLD_VALUE: Int = 13
   private const val WALL_HEIGHT: Int = 5
   private const val WALL_SCAN_RADIUS: Int = 15
   private const val CENTER_SCAN_RADIUS: Int = 10
   private const val CENTER_SCAN_DOWN: Int = 5
   private const val WALK_YAW_LIMIT: Float = 86.0F
   private const val SPRINT_YAW_LIMIT: Float = 48.0F
   private const val JUMP_YAW_LIMIT: Float = 38.0F
   private const val JUMP_COOLDOWN_TICKS: Int = 8
   private const val PASS_APPROACH_DISTANCE: Double = 2.75
   private const val PASS_THROUGH_DISTANCE: Double = 2.1
   private const val ENTRY_APPROACH_DISTANCE: Double = 1.35
   private const val STAGED_JUMP_DISTANCE: Double = 2.6
   private const val HIGH_WALL_PASS_JUMP_WINDOW: Double = 3.65
   private const val HIGH_WALL_PASS_JUMP_BOOST_WINDOW: Double = 4.2
   private const val LATERAL_ALIGN_TOLERANCE: Double = 0.34
   private const val PASS_COMPLETE_EXIT_DISTANCE: Double = 1.9
   private const val JUMP_PASS_AIR_BRAKE_DISTANCE: Double = 1.55
   private const val PASSED_WALL_MEMORY_TICKS: Int = 80
   private const val PASSED_WALL_CENTER_DISTANCE: Double = 5.25
   private const val ACTIVE_PASS_REFRESH_DISTANCE: Double = 5.25
   private const val ACTIVE_PASS_REFRESH_EXIT_LIMIT: Double = -0.05
   private const val APPROACH_LOCK_REFRESH_DISTANCE: Double = 5.25
   private const val GAP_JUMP_MAX_WIDTH: Double = 3.2
   private const val FORWARD_FLOOR_PROBE_DISTANCE: Double = 0.95
   private var active: Boolean
   
   private BlockPos middleBase;
   private val wallHoles: MutableList<Box> = ArrayList() as java.util.List
   private val lastHoles: MutableList<Box> = ArrayList() as java.util.List
   private val holeDirections: LinkedHashMap<Box, HoleDirection> = LinkedHashMap()
   private var currentAimPlan: jooon.features.dojo.AutomaticStamina.AimPlan?
   private var nextJumpAllowedTick: Int
   private var activePass: jooon.features.dojo.AutomaticStamina.ActivePass?
   private var lockedApproach: Pass?
   private var recentlyPassedWall: jooon.features.dojo.AutomaticStamina.ActivePass?
   private var recentlyPassedWallTicks: Int
   private var lastHoleSummary: String = ""
   private var lastTargetSummary: String = ""
   private var lastNoTargetLogTick: Int = -1000

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         tick(client)
      })
   }

   fun tick(client: MinecraftClient) {
      if (!Config.autoDojoEnabled || !Config.fullyAutomaticStamina || !AutoDojo.isChallengeActive(AutoDojo.Challenge.STAMINA)) {
         if (active) {
            this.reset()
         }
      } else {


         if (client.player != null && client.world != null && client.currentScreen == null) {
            if (!active) {
               active = true
               middleBase = null
               wallHoles.clear()
               lastHoles.clear()
               holeDirections.clear()
               activePass = null
               lockedApproach = null
               recentlyPassedWall = null
               recentlyPassedWallTicks = 0
               AutoDojo.subtitle = "§7[§cSneak to pause§7]"

               debug$default(this, player, "S001_START", "pos=${this.fmt(var10004)}", false, 8, null)
            }

            if (client.player.isSneaking()) {

               debug$default(
                  this,
                  player,
                  "S002_PAUSE_USER_SNEAK",
                  "pos=${this.fmt(var10)} activePass=${this.passSummary(activePass)} locked=${this.logicPassSummary(lockedApproach)} recent=${this.passSummary(
return recentlyPassedWall
                  )}",
                  false,
                  8,
return null
               )
               this.pauseFully()
               AutoDojo.subtitle = "§7[§cPAUSED§7]"
            } else {
               if (AutoDojo.subtitle.length() == 0 || contains$default(AutoDojo.subtitle, "PAUSED", false, 2, null)) {
                  AutoDojo.subtitle = "§7[§cSneak to pause§7]"
               }

               this.updateHoles(level as World, player)
               if (recentlyPassedWallTicks > 0) {
                  recentlyPassedWallTicks += -1
               } else {
                  recentlyPassedWall = null
               }

               if (this.hasCompletedActivePass(player)) {


                  this.debug(player, "S050_PASS_COMPLETE", "pass=$var10003 pos=${this.fmt(var10005)}", true)
                  this.markActivePassComplete()
                  activePass = null
               } else {
                  this.refreshActivePass(level as World, player)
               }

               if (activePass != null) {
                  this.moveThroughActivePass(level as World, player, activePass)
               } else {
                  val target: AutomaticStamina.HoleTarget = this.chooseTarget(level as World, player)
                  if (target == null) {
                     if (player.age - lastNoTargetLogTick >= 5) {
                        lastNoTargetLogTick = player.age






                        debug$default(
                           this,
                           player,
                           "S041_NO_TARGET",
                           "holes=$var8 activePass=$var9 locked=$var11 recent=$var10006 recentTicks=$var10007 pos=${this.fmt(var10009)}",
                           false,
                           8,
return null
                        )
                     }

                     this.stopMotion()
                     this.clearAim()
                  } else {
                     this.moveToHole(level as World, player, target)
                  }
               }
            }
         } else {
            this.stopMotion()
         }
      }
   }

   fun updateHoles(level: World, player: ClientPlayerEntity) {
      val var10000: java.util.List = this.findWallBlocks(level, player)
      if (var10000 != null) {
         val walls: java.util.List = this.findWalls(var10000)
         lastHoles.clear()
         lastHoles.addAll(wallHoles)
         wallHoles.clear()

         for (summary in walls) {
            wallHoles.addAll(this.findHolesInBox(level, summary))
         }

         holeDirections.clear()

         for (hole in wallHoles) {
            val direction: StaminaMovementLogic.HoleDirection = this.getHoleDirection(hole)
            if (direction === StaminaMovementLogic.HoleDirection.UNCHANGED) {
               val var9: java.util.Map = holeDirections
               var var14: StaminaMovementLogic.HoleDirection = var11.get(hole) as StaminaMovementLogic.HoleDirection
               if (var14 == null) {
                  var14 = StaminaMovementLogic.HoleDirection.UNCHANGED
               }

               var9.put(hole, var14)
            } else {
               holeDirections.put(hole, direction)
            }
         }

            var var10000: StaminaMovementLogic.HoleDirection = holeDirections.get(hole)
            if (var10000 == null) {
               var10000 = StaminaMovementLogic.HoleDirection.NEW
            }

            ("${var10000.name()}@${fmtCenter(hole)}") as java.lang.CharSequence
         }, 23, null)
         if (!(var13 == lastHoleSummary) || player.age % 20 == 0) {
            lastHoleSummary = var13
            debug$default(
               this,
               player,
               "S020_HOLES",
               "walls=${walls.size()} holes=${wallHoles.size()} summary=[$var13] recent=${this.passSummary(recentlyPassedWall)} recentTicks=${recentlyPassedWallTicks}",
               false,
               8,
return null
            )
         }
      }
   }

   fun findWallBlocks(level: World, player: ClientPlayerEntity): MutableList<BlockPos> {


      if (middleBase == null) {
         var var11: Int = var10000.getX() - 10

         for (var12 in var10000.getX() + 10..var11) {
            var var13: Int = playerPos.getY() - 5

            for (var14 in playerPos.getY()..var13) {
               var var15: Int = playerPos.getZ() - 10

               for (var16 in playerPos.getZ() + 10..var15) {

                  if (level.getBlockState(var17).getBlock() == Blocks.CHISELED_STONE_BRICKS) {
                     middleBase = var17
return null
                  }
               }
            }
         }
return null
      } else if (middleBase == null) {
return null
      } else {


         var x: Int = center.getX() - 15

         for (var7 in center.getX() + 15..x) {
            var z: Int = center.getZ() - 15

            for (var9 in center.getZ() + 15..z) {

               if (!level.getBlockState(pos).isAir()) {
                  blocks.add(pos)
               }
            }
         }

         blocks as java.util.List
      }
   }

   private fun findWalls(bottomWallBlocks: List<BlockPos>): List<Box> {



      for (walls in bottomWallBlocks) {
         var line: java.util.Map = possibleX
         var linex: Any = walls.getX()
         var minZ: Any = line.get(linex)
         var var10000: Any
         if (minZ == null) {

            line.put(linex, var29)
            var10000 = var29
         } else {
            var10000 = minZ
         }

         (var10000 as java.util.List).add(walls)
         line = possibleZ
         linex = walls.getZ()
         minZ = line.get(linex)
         if (minZ == null) {

            line.put(linex, var31)
            var10000 = var31
         } else {
            var10000 = minZ
         }

         (var10000 as java.util.List).add(walls)
      }


      for (var36 in possibleX.values()) {
         val var20: java.util.List = var36 as java.util.List
         if ((var36 as java.util.List).size() >= 13) {
            var15.add(var20)
         }
      }

      for (var37 in possibleZ.values()) {
         val var21: java.util.List = var37 as java.util.List
         if ((var37 as java.util.List).size() >= 13) {
            var15.add(var21)
         }
      }

      var var38: java.util.List = var15.iterator()
      val var22: java.util.Iterator = var38

      while (var22.hasNext()) {
         var38 = (java.util.List)var22.next()
         val var24: java.util.List = var38
         var var26: Int = (first(var38) as BlockPos).getX()
         var var28: Int = (first(var38) as BlockPos).getZ()
         var var32: Int = (first(var24) as BlockPos).getX()
         var maxZ: Int = (first(var24) as BlockPos).getZ()


         for (maxPos in var24) {
            var26 = Math.min(var26, maxPos.getX())
            var28 = Math.min(var28, maxPos.getZ())
            var32 = Math.max(var32, maxPos.getX())
            maxZ = Math.max(maxZ, maxPos.getZ())
         }

         var18.add(Box.enclosing(BlockPos(var26, y, var28), BlockPos(var32, y + 5, maxZ)))
      }

      return var18
   }

   fun findHolesInBox(level: World, box: Box): MutableList<Box> {






      var index: Int = MathHelper.floor(box.minZ)

      for (var10 in MathHelper.floor(box.maxZ)..index) {
         var hole: Int = MathHelper.floor(box.minX)

         for (var12 in MathHelper.floor(box.maxX)..hole) {
            var y: Int = MathHelper.floor(box.minY)

            for (var14 in MathHelper.floor(box.maxY)..y) {

               if (level.getBlockState(pos).isAir()) {




                  if (top) {
                     if (left) {
                        topLeft.add(pos)
                     }

                     if (right) {
                        topRight.add(pos)
                     }
                  }

                  if (bottom && left) {
                     bottomLeft.add(pos)
                  }
               }
            }
         }
      }

      index = 0

      // $VF: Unable to resugar Kotlin loop from Java for loop

      while (true) {
         if (index < var21 && topRight.size() > index && bottomLeft.size() > index) break

         holes.add(
            var10000.stretch(0.0, ((bottomLeft.get(index) as BlockPos).getY() - (topLeft.get(index) as BlockPos).getY()).toDouble(), 0.0)
         )

         index++
      }

      holes as java.util.List
   }

   fun getHoleDirection(hole: Box): StaminaMovementLogic.HoleDirection {

      val var10001: StaminaMovementLogic.HoleBox = this.logicHole(hole)
      val `this$iv`: java.lang.Iterable = lastHoles
      val `destination$iv$iv`: java.util.Collection = ArrayList(lastHoles.count().coerceAtLeast(10))

      for (`item$iv$iv` in `this$iv`) {
         `destination$iv$iv`.add(this.logicHole(`item$iv$iv` as Box))
      }

      var10000.directionFromPrevious(var10001, `destination$iv$iv`)
   }

   fun chooseTarget(level: World, player: ClientPlayerEntity): AutomaticStamina.HoleTarget {
      if (wallHoles.isEmpty()) {
return null
      } else {
         val locked: AutomaticStamina.HoleTarget = this.chooseLockedTarget(level, player)
         if (locked != null) {
return locked
         } else {
            var best: AutomaticStamina.HoleTarget = null

            for (hole in wallHoles) {
               var var10004: StaminaMovementLogic.HoleDirection = holeDirections.get(hole)
               if (var10004 == null) {
                  var10004 = StaminaMovementLogic.HoleDirection.NEW
               }

               val var10000: AutomaticStamina.HoleTarget = this.buildHoleTarget(level, player, hole, var10004)
               if (var10000 != null && (best == null || var10000.score < best.score)) {
                  best = var10000
               }
            }

            if (best != null) {
               lockedApproach = StaminaMovementLogic.Pass(best.axis, this.logicPoint(best.getCenter().x, best.getCenter().z), best.playerSide)
            }

            var var11: String
            run label68@{
               if (best != null) {
                  var11 = "dir=${best.direction} phase=${best.phase} center=${fmt(best.getCenter())} target=${fmt(best.getTarget())} score=${fmt(
                     best.score
                  )} wallDist=${fmt(best.wallDistance)} lateral=${fmt(best.lateralError)} side=${fmt(best.playerSide)} jump=${best.requiresJump} lock=${logicPassSummary(
return lockedApproach
                  )}"
                  if (var11 != null) {
                     return@label68
                  }
               }

               var11 = "none holes=${wallHoles.size()}"
            }

            if (!(var11 == lastTargetSummary) || player.age % 20 == 0) {
               lastTargetSummary = var11
               debug$default(this, player, "S040_TARGET", var11, false, 8, null)
            }
return best
         }
      }
   }

   fun chooseLockedTarget(level: World, player: ClientPlayerEntity): AutomaticStamina.HoleTarget {
      if (lockedApproach == null) {
return null
      } else {
         val lock: StaminaMovementLogic.Pass = lockedApproach
         var bestHole: Box = null
         var bestDistance: Double = java.lang.Double.MAX_VALUE

         for (summary in wallHoles) {
            val logicHole: StaminaMovementLogic.HoleBox = this.logicHole(summary)

            var var10001: StaminaMovementLogic.HoleDirection = holeDirections.get(summary)
            if (var10001 == null) {
               var10001 = StaminaMovementLogic.HoleDirection.NEW
            }

            if (var10000.axisFor(var10001, logicHole) === lock.axis) {

               if (!(distance > 5.25) && distance < bestDistance) {
                  bestHole = summary
                  bestDistance = distance
               }
            }
         }

         if (bestHole == null) {
            debug$default(this, player, "S042_LOCK_LOST_NO_MATCH", "lock=${this.logicPassSummary(lock)} holes=${wallHoles.size()}", false, 8, null)
            lockedApproach = null
return null
         } else {
            var var10004: StaminaMovementLogic.HoleDirection = holeDirections.get(bestHole)
            if (var10004 == null) {
               var10004 = StaminaMovementLogic.HoleDirection.NEW
            }

            val var13: AutomaticStamina.HoleTarget = this.buildHoleTarget(level, player, bestHole, var10004)
            if (var13 == null) {
               debug$default(this, player, "S043_LOCK_LOST_INVALID", "lock=${this.logicPassSummary(lock)} matched=${this.boxSummary(bestHole)}", false, 8, null)
               lockedApproach = null
return null
            } else {
               lockedApproach = StaminaMovementLogic.Pass(
                  var13.axis, this.logicPoint(var13.getCenter().x, var13.getCenter().z), var13.playerSide
               )

                  var13.getTarget()
               )} score=${this.fmt(var13.score)} wallDist=${this.fmt(var13.wallDistance)} lateral=${this.fmt(var13.lateralError)} side=${this.fmt(
                  var13.playerSide
               )} jump=${var13.requiresJump} lock=${this.logicPassSummary(lockedApproach)}"
               if (!(var14 == lastTargetSummary) || player.age % 20 == 0) {
                  lastTargetSummary = var14
                  debug$default(this, player, "S044_LOCKED_TARGET", var14, false, 8, null)
               }
return var13
            }
         }
      }
   }

   fun buildHoleTarget(level: World, player: ClientPlayerEntity, hole: Box, direction: StaminaMovementLogic.HoleDirection): AutomaticStamina.HoleTarget {

      if (rejectBeforePlan != null) {
         this.debugCandidateReject(player, "S030_REJECT_PREPLAN", hole, direction, rejectBeforePlan)
return null
      } else {

         val var10001: StaminaMovementLogic.HoleBox = this.logicHole(hole)
         var var10002: StaminaMovementLogic.HoleDirection = direction
         val var10003: StaminaMovementLogic.Point = this.logicPoint(player.getX(), player.getZ())
         val var10004: StaminaMovementLogic.Pass
         if (recentlyPassedWall != null) {
            val centerY: AutomaticStamina.ActivePass = recentlyPassedWall
            val var39: StaminaMovementLogic.Pass = StaminaMovementLogic.Pass(
               recentlyPassedWall.axis, logicPoint(centerY.getCenter().x, centerY.getCenter().z), centerY.entrySide
            )
            var10002 = direction
            var10004 = var39
         } else {
            var10004 = null
         }

         val var44: StaminaMovementLogic.PlannedHole = var10000.planHole(var10001, var10002, var10003, var10004, 0.34, 2.75, 2.1, 5.25, 1.35)
         if (var44 == null) {
            this.debugCandidateReject(player, "S030_REJECT_PLAN_NULL", hole, direction, "planHole returned null after precheck passed")
return null
         } else {
            val axis: StaminaMovementLogic.Axis = var44.axis






            var phase: StaminaMovementLogic.ApproachPhase = var44.phase
            var target: Vec3d = Vec3d(var44.target.x, var40, var44.target.z)
            if (!this.routeHasFloorOrShortGap(level, player, target)) {
               if (phase != StaminaMovementLogic.ApproachPhase.PASS_THROUGH) {
                  this.debugCandidateReject(
                     player, "S031_REJECT_ROUTE", hole, direction, "route has no floor or short jumpable gap target=${this.fmt(target)} phase=$phase"
                  )
return null
               }

               val jumpProfile: StaminaMovementLogic.Point = StaminaMovementLogic.stagedJumpTarget(axis, var44.center, playerSide, 2.6)

               if (!this.routeHasFloorOrShortGap(level, player, requiredRise)) {
                  this.debugCandidateReject(
                     player,
                     "S031_REJECT_ROUTE",
                     hole,
                     direction,
                     "route and stage have no floor or short jumpable gap target=${this.fmt(target)} stage=${this.fmt(requiredRise)} phase=$phase"
                  )
return null
               }

               this.debugCandidateReject(
                  player,
                  "S033_STAGE_BEFORE_JUMP",
                  hole,
                  direction,
                  "pass route unsafe; staging target=${this.fmt(requiredRise)} passTarget=${this.fmt(target)}"
               )
               phase = StaminaMovementLogic.ApproachPhase.STAGE_FOR_JUMP
               target = requiredRise
            }




            if (phase === StaminaMovementLogic.ApproachPhase.PASS_THROUGH && var43 > 0.38 && !jumpPossible) {
               this.debugCandidateReject(
                  player,
                  "S032_REJECT_JUMP_IMPOSSIBLE",
                  hole,
                  direction,
                  "requiredRise=${this.fmt(var43)} maxClimb=${this.fmt(var42.maxClimb)} target=${this.fmt(target)}"
               )
return null
            } else {


               AutomaticStamina.HoleTarget(
                  hole,
                  direction,
                  axis,
                  phase,
                  center,
                  target,
                  desiredYaw,
                  wallDistance * 0.68
                     + lateralError * 2.25
                     + this.horizontalDistance(var45, target) * 0.3
                     + (if (phase === StaminaMovementLogic.ApproachPhase.ALIGN_LATERAL) 0.35 else 0.0)
                     + -7.0
                     + (
                        if (direction != StaminaMovementLogic.HoleDirection.NEW && direction != StaminaMovementLogic.HoleDirection.UNCHANGED)
                           0.0 + (if (requiresJump) 0.55 else 0.0)
return else
                           1.2
                     ),
                  wallDistance,
                  lateralError,
                  playerSide,
                  true,
return requiresJump
               )
            }
         }
      }
   }

   fun moveToHole(level: World, player: ClientPlayerEntity, target: AutomaticStamina.HoleTarget) {

      if (dist < 0.2) {
         this.stopMotion()
         this.clearAim()
      } else {
         if (target.phase === StaminaMovementLogic.ApproachPhase.PASS_THROUGH) {
            activePass = AutomaticStamina.ActivePass(
               target.axis, target.getCenter(), target.playerSide, target.getTarget(), target.desiredYaw, target.requiresJump
            )
            lockedApproach = null
         }







         this.debugEvery(
            player,
            "S070_MOVE_TO_HOLE",
            "phase=${target.phase} dir=${target.direction} target=${this.fmt(target.getTarget())} dist=${this.fmt(dist)} yawErr=${this.fmt(yawError)} routeGap=$routeHasGap jump=$jump forwardFloorSafe=$forwardFloorSafe move=$move sprint=${move
               && yawError <= 48.0F} activePass=${this.passSummary(activePass)}"
         )
         if (routeHasGap && !jump) {
            this.debugEvery(
               player,
               "S080_GAP_NO_JUMP_MOVE_TO_HOLE",
               "target=${this.fmt(target.getTarget())} yawErr=${this.fmt(yawError)} onGround=${player.isOnGround()} cooldownLeft=${(nextJumpAllowedTick - player.age).coerceAtLeast(0)} forwardFloorSafe=$forwardFloorSafe move=$move"
            )
         }

         this.aimAtYaw(player, yaw, if (target.requiresJump) 55 else 65)
         PlayerController.pressSprint(sprint)
         PlayerController.pressJump(jump)
         PlayerController.pressSneak(false)
         PlayerController.pressBack(false)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressForward(move)
      }
   }

   fun moveThroughActivePass(level: World, player: ClientPlayerEntity, pass: AutomaticStamina.ActivePass) {


      if (dist < 0.2 && !airborneJumpPass) {
         this.markActivePassComplete()
         activePass = null
         this.stopMotion()
         this.clearAim()
      } else if (!airborneJumpPass && !this.routeHasFloorOrShortGap(level, player, pass.getTarget())) {
         activePass = null
         this.stopMotion()
         this.clearAim()
      } else {





            && StaminaMovementLogic.INSTANCE
               .shouldBrakeAfterJumpPass(
                  StaminaMovementLogic.Pass(pass.axis, this.logicPoint(pass.getCenter().x, pass.getCenter().z), pass.entrySide),
                  this.logicPoint(player.getX(), player.getZ()),
                  1.55
               )


         this.debugEvery(
            player,
            "S060_PASS_MOVE",
            "pass=${this.passSummary(pass)} dist=${this.fmt(dist)} yawErr=${this.fmt(yawError)} routeGap=$routeHasGap jump=$jump forwardFloorSafe=$forwardFloorSafe airBrake=$airBrake airborneJump=$airborneJumpPass move=$move sprint=${move
               && yawError <= 48.0F}"
         )
         if (routeHasGap && !jump) {
            this.debugEvery(
               player,
               "S081_GAP_NO_JUMP_PASS",
               "pass=${this.passSummary(pass)} yawErr=${this.fmt(yawError)} onGround=${player.isOnGround()} cooldownLeft=${(nextJumpAllowedTick - player.age).coerceAtLeast(0)} forwardFloorSafe=$forwardFloorSafe move=$move"
            )
         }

         this.aimAtYaw(player, pass.desiredYaw, if (pass.requiresJump) 55 else 65)
         PlayerController.pressSprint(sprint)
         PlayerController.pressJump(jump)
         PlayerController.pressSneak(false)
         PlayerController.pressBack(airBrake)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressForward(move)
      }
   }

   fun shouldJump(level: World, player: ClientPlayerEntity, target: AutomaticStamina.HoleTarget, yawError: Float): Boolean {
      if (target.phase != StaminaMovementLogic.ApproachPhase.PASS_THROUGH) {
return false
      } else if (!player.isOnGround()) {
return false
      } else if (player.age < nextJumpAllowedTick) {
return false
      } else if (yawError > 38.0F) {
return false
      } else {


         val gapScan: StaminaMovementLogic.GapScan = this.routeGapScan(level, player, target.getTarget(), 0.45, 0.25, 4.2)



         if (StaminaMovementLogic.INSTANCE
            .shouldStartJumpForRoute(
               highHole, gapScan.hasShortJumpableGap, gapScan.firstGapStartDistance, target.wallDistance, distanceToTarget, jumpWindow, jumpWindow
            )) {
            debug$default(
               this,
               player,
               "S082_JUMP_START_TARGET",
               "highHole=$highHole gap=${gapScan.hasShortJumpableGap} gapStart=${this.fmtNullable(gapScan.firstGapStartDistance)} wallDist=${this.fmt(
                  target.wallDistance
               )} targetDist=${this.fmt(distanceToTarget)} window=${this.fmt(jumpWindow)} highWindow=${this.fmt(jumpWindow)}",
               false,
               8,
return null
            )
            nextJumpAllowedTick = player.age + 8
return true
         } else {
            if (highHole || gapScan.hasShortJumpableGap) {
               this.debugEvery(
                  player,
                  "S083_JUMP_BLOCKED_TARGET",
                  "highHole=$highHole gap=${gapScan.hasShortJumpableGap} gapStart=${this.fmtNullable(gapScan.firstGapStartDistance)} wallDist=${this.fmt(
                     target.wallDistance
                  )} targetDist=${this.fmt(distanceToTarget)} window=${this.fmt(jumpWindow)} highWindow=${this.fmt(jumpWindow)} yawErr=${this.fmt(yawError)} onGround=${player.isOnGround()} cooldownLeft=${(nextJumpAllowedTick - player.age).coerceAtLeast(0)}"
               )
            }
return false
         }
      }
   }

   fun shouldJumpForPoint(level: World, player: ClientPlayerEntity, target: Vec3d, forceJump: Boolean, yawError: Float): Boolean {
      if (!player.isOnGround()) {
return false
      } else if (player.age < nextJumpAllowedTick) {
return false
      } else if (yawError > 38.0F) {
return false
      } else {





         val gapScan: StaminaMovementLogic.GapScan = this.routeGapScan(level, player, target, 0.45, 0.25, 4.2)
         if (StaminaMovementLogic.INSTANCE
            .shouldStartJumpForRoute(forceJump, false, gapScan.firstGapStartDistance, distanceToTarget, distanceToTarget, jumpWindow, highHoleJumpWindow)) {
            debug$default(
               this,
               player,
               "S084_JUMP_START_PASS",
               "forceJump=$forceJump gap=${gapScan.hasShortJumpableGap} gapStart=${this.fmtNullable(gapScan.firstGapStartDistance)} targetDist=${this.fmt(
return distanceToTarget
               )} window=${this.fmt(jumpWindow)} highWindow=${this.fmt(highHoleJumpWindow)}",
               false,
               8,
return null
            )
            nextJumpAllowedTick = player.age + 8
return true
         } else {
            if (forceJump || gapScan.hasShortJumpableGap) {
               this.debugEvery(
                  player,
                  "S085_JUMP_BLOCKED_PASS",
                  "forceJump=$forceJump gap=${gapScan.hasShortJumpableGap} gapStart=${this.fmtNullable(gapScan.firstGapStartDistance)} targetDist=${this.fmt(
return distanceToTarget
                  )} window=${this.fmt(jumpWindow)} highWindow=${this.fmt(highHoleJumpWindow)} yawErr=${this.fmt(yawError)} onGround=${player.isOnGround()} cooldownLeft=${(nextJumpAllowedTick - player.age).coerceAtLeast(0)}"
               )
            }
return false
         }
      }
   }

   fun hasCompletedActivePass(player: ClientPlayerEntity): Boolean {
      if (activePass == null) {
return false
      } else {
         val pass: AutomaticStamina.ActivePass = activePass
         (!activePass.requiresJump || player.isOnGround())
            && StaminaMovementLogic.INSTANCE
               .hasCompletedPass(
                  StaminaMovementLogic.Pass(pass.axis, this.logicPoint(pass.getCenter().x, pass.getCenter().z), pass.entrySide),
                  this.logicPoint(player.getX(), player.getZ()),
                  1.9
               )
            }
   }

   private fun markActivePassComplete() {
      if (activePass != null) {
         recentlyPassedWall = activePass
         recentlyPassedWallTicks = 80
      }
   }

   fun refreshActivePass(level: World, player: ClientPlayerEntity) {
      if (activePass != null) {
         val pass: AutomaticStamina.ActivePass = activePass
         val logicPass: StaminaMovementLogic.Pass = StaminaMovementLogic.Pass(
            activePass.axis, this.logicPoint(pass.getCenter().x, pass.getCenter().z), pass.entrySide
         )
         if (!StaminaMovementLogic.shouldRefreshActivePass(logicPass, this.logicPoint(player.getX(), player.getZ()), -0.05)) {


            this.debugEvery(player, "S051_REFRESH_SKIP_EXIT_SIDE", "pass=$var10003 pos=${this.fmt(var10005)} exitLimit=${this.fmt(-0.05)}")
         } else {
            val center: java.lang.Iterable = wallHoles

            val `destination$iv$iv`: java.util.Collection = ArrayList(center.count().coerceAtLeast(10))

            for (`item$iv$iv` in center) {
               `destination$iv$iv`.add(this.logicHole(`item$iv$iv` as Box))
            }

            val matchingHole: StaminaMovementLogic.HoleBox = var15.nearestMatchingPassHole(logicPass, `destination$iv$iv`, 5.25)
            if (matchingHole == null) {
               debug$default(this, player, "S052_REFRESH_LOST_NO_MATCH", "pass=${this.passSummary(pass)} holes=${wallHoles.size()}", false, 8, null)
               activePass = null
            } else {
               val var18: StaminaMovementLogic.Point = matchingHole.center
               val var19: StaminaMovementLogic.Point = StaminaMovementLogic.passTarget(pass.axis, var18, pass.entrySide, 2.1)

               if (!this.routeHasFloorOrShortGap(level, player, var20)) {
                  debug$default(
                     this,
                     player,
                     "S053_REFRESH_LOST_ROUTE",
                     "pass=${this.passSummary(pass)} matchCenter=${this.fmtPoint(var18)} target=${this.fmt(var20)}",
                     false,
                     8,
return null
                  )
                  activePass = null
               } else {
                  this.debugEvery(
                     player, "S054_REFRESH_ACTIVE_PASS", "old=${this.passSummary(pass)} matchCenter=${this.fmtPoint(var18)} target=${this.fmt(var20)}"
                  )
                  activePass = AutomaticStamina.ActivePass.copy$default(
                     pass,
                     null,
                     Vec3d(var18.x, pass.getCenter().y, var18.z),
                     0.0,
                     var20,
                     this.desiredCardinalYaw(player, var20),
                     pass.requiresJump,
                     5,
return null
                  )
               }
            }
         }
      }
   }

   fun desiredCardinalYaw(player: ClientPlayerEntity, target: Vec3d): Float {


      if (Math.abs(dx) >= Math.abs(dz)) (if (dx >= 0.0) -90.0F else 90.0F) else (if (dz >= 0.0) 0.0F else 180.0F)
   }

   fun horizontalDistance(a: Vec3d, b: Vec3d): Double {
      Math.hypot(a.x - b.x, a.z - b.z)
   }

   private fun horizontalDistance(a: Point, b: Point): Double {
      return Math.hypot(a.x - b.x, a.z - b.z)
   }

   fun routeNeedsGapJump(level: World, player: ClientPlayerEntity, target: Vec3d): Boolean {
      this.routeGapScan(level, player, target, 0.45, 0.25, 4.2).hasShortJumpableGap
   }

   fun routeHasFloorOrShortGap(level: World, player: ClientPlayerEntity, target: Vec3d): Boolean {
      this.routeGapScan(level, player, target, 0.45, 0.25, 4.4).routeAcceptable
   }

   fun routeGapScan(level: World, player: ClientPlayerEntity, target: Vec3d, sampleStep: Double, firstSampleDistance: Double, maxForward: Double): StaminaMovementLogic.GapScan {



      if (dist <= 0.25) {
         StaminaMovementLogic.GapScan(false, true, null, 4, null)
      } else {



         var forward: Double = firstSampleDistance


         while (forward <= cappedForward) {
            samples.add(this.hasWalkableFloor(level, player.getX() + dirX * forward, player.getY(), player.getZ() + dirZ * forward))
            forward += sampleStep
         }

         StaminaMovementLogic.scanGap(samples, sampleStep, 3.2, firstSampleDistance)
      }
   }

   fun prePlanRejectReason(player: ClientPlayerEntity, hole: Box, direction: StaminaMovementLogic.HoleDirection): String {
      val logicHole: StaminaMovementLogic.HoleBox = this.logicHole(hole)
      val axis: StaminaMovementLogic.Axis = StaminaMovementLogic.axisFor(direction, logicHole)
      val center: StaminaMovementLogic.Point = logicHole.center
      if (!StaminaMovementLogic.isIncoming(logicHole, direction, this.logicPoint(player.getX(), player.getZ()))) {

         "outgoing direction=$direction player=${this.fmt(var10002)} hole=${this.boxSummary(hole)}"
      } else {
         val var10000: StaminaMovementLogic.Pass
         if (recentlyPassedWall != null) {
            val it: AutomaticStamina.ActivePass = recentlyPassedWall
            var10000 = StaminaMovementLogic.Pass(
               recentlyPassedWall.axis, logicPoint(it.getCenter().x, it.getCenter().z), it.entrySide
            )
         } else {
            var10000 = null
         }

         if (StaminaMovementLogic.isRecentlyPassedWall(axis, center, this.logicPoint(player.getX(), player.getZ()), var10000, 5.25))
            "recently-passed same moving wall axis=$axis center=${this.fmtPoint(center)} passed=${this.passSummary(recentlyPassedWall)} recentTicks=${recentlyPassedWallTicks}"
return else
return null
         }
   }

   fun debugCandidateReject(
      player: ClientPlayerEntity, code: String, hole: Box, direction: StaminaMovementLogic.HoleDirection, reason: String
   ) {
      if (player.age % 5 == 0) {
         debug$default(this, player, code, "dir=$direction hole=${this.boxSummary(hole)} reason=$reason", false, 8, null)
      }
   }

   fun debugEvery(player: ClientPlayerEntity, code: String, message: String) {
      if (player.age % 5 == 0) {
         debug$default(this, player, code, message, false, 8, null)
      }
   }

   fun debug(player: ClientPlayerEntity, code: String, message: String, force: Boolean) {
   }

   private fun fmt(value: Double): String {

      val var6: Array<Any> = arrayOf(value)

      return var10000
   }

   private fun fmt(value: Float): String {

      val var5: Array<Any> = arrayOf(value)

      return var10000
   }

   private fun fmtNullable(value: Double?): String {
      if (value != null) {

         if (var10000 != null) {
            return var10000
         }
      }

      return "null"
   }

   fun fmt(vec: Vec3d): String {
      "(${this.fmt(vec.x)},${this.fmt(vec.y)},${this.fmt(vec.z)})"
   }

   private fun fmtPoint(point: Point): String {
      return "(${this.fmt(point.x)},${this.fmt(point.z)})"
   }

   fun fmtCenter(box: Box): String {
      "(${this.fmt((box.minX + box.maxX) * 0.5)},${this.fmt((box.minZ + box.maxZ) * 0.5)})"
   }

   fun boxSummary(box: Box): String {
      "center=${this.fmtCenter(box)} min=(${this.fmt(box.minX)},${this.fmt(box.minZ)}) max=(${this.fmt(box.maxX)},${this.fmt(box.maxZ)})"
   }

   private fun passSummary(pass: jooon.features.dojo.AutomaticStamina.ActivePass?): String {
      if (pass != null) {

            pass.getTarget()
         )} jump=${pass.requiresJump}"
         if (var10000 != null) {
            return var10000
         }
      }

      return "none"
   }

   private fun logicPassSummary(pass: Pass?): String {
      if (pass != null) {

         if (var10000 != null) {
            return var10000
         }
      }

      return "none"
   }

   fun canMoveTowardPointNow(level: World, player: ClientPlayerEntity, target: Vec3d, allowJumpGap: Boolean): Boolean {



      if (dist <= 0.25) {
return true
      } else {



         var forward: Double = 0.18

         // $VF: Unable to resugar Kotlin loop from Java for loop

         while (true) {
            if (forward <= maxForward) break
            samples.add(this.hasWalkableFloor(level, player.getX() + dirX * forward, player.getY(), player.getZ() + dirZ * forward))

            forward += 0.24
         }

         val gapScan: StaminaMovementLogic.GapScan = StaminaMovementLogic.scanGap(samples, 0.24, 3.2, 0.18)
         var var10000: Boolean
         if (allowJumpGap) {
            var10000 = gapScan.routeAcceptable
         } else {
            val `this$iv`: java.lang.Iterable = samples
            if (samples is java.util.Collection && (samples as java.util.Collection).isEmpty()) {
               var10000 = true
            } else {
               val var25: java.util.Iterator = `this$iv`.iterator()

               while (true) {
                  if (!var25.hasNext()) {
                     var10000 = true
break
                  }

                  if (!var25.next() as Boolean) {
                     var10000 = false
break
                  }
               }
            }
         }
return var10000
      }
   }

   fun logicHole(hole: Box): StaminaMovementLogic.HoleBox {
      StaminaMovementLogic.HoleBox(hole.minX, hole.maxX, hole.minZ, hole.maxZ)
   }

   private fun logicPoint(x: Double, z: Double): Point {
      return StaminaMovementLogic.Point(x, z)
   }

   fun hasWalkableFloor(level: World, x: Double, y: Double, z: Double): Boolean {
      StaminaMovementLogic.INSTANCE
         .exactFloorOrFallback(
            MathHelper.floor(x),
            MathHelper.floor(z),
            if (middleBase != null) middleBase.getY() else null,
            MathHelper.floor(y) - 1,
            { sx: Int, sy: Int, sz: Int ->
               hasSolidFloor(`$level`, sx, sy, sz)
            }
         )
      }

   fun hasSolidFloor(level: World, x: Int, y: Int, z: Int): Boolean {


      !var10000.getCollisionShape(level as BlockView, pos, ShapeContext.absent()).isEmpty()
   }

   fun aimAtPoint(player: ClientPlayerEntity, x: Double, y: Double, z: Double, durationBaseMs: Int) {



      this.requestAim(
         player,
         this.yawTo(dx, dz),
         (Math.toDegrees(-Math.atan2(dy, (Math.sqrt(dx * dx + dz * dz)).coerceAtLeast(0.001))).toFloat()).coerceIn(-32.0F, 32.0F),
return durationBaseMs
      )
   }

   fun aimAtYaw(player: ClientPlayerEntity, yaw: Float, durationBaseMs: Int) {
      this.requestAim(player, yaw, 0.0F, durationBaseMs)
   }

   fun requestAim(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, durationBaseMs: Int) {

      val plan: AutomaticStamina.AimPlan = currentAimPlan

         java.lang.Float.MAX_VALUE
return else
         Math.hypot(MathHelper.wrapDegrees(targetYaw - currentAimPlan.targetYaw).toDouble(), (targetPitch - plan.targetPitch).toDouble()).toFloat()
         if (currentAimPlan == null
         || currentAimPlan == null
         || now - currentAimPlan.startAtMs >= plan.durationMs
         || targetShift > 12.0F && now - currentAimPlan.startAtMs >= 70L) {


         currentAimPlan = AutomaticStamina.AimPlan(
            player.getYaw(),
            player.getPitch(),
            targetYaw,
            targetPitch,
            deltaYaw,
            deltaPitch,
            now,
            ((durationBaseMs.toFloat() + Math.hypot(deltaYaw.toDouble(), deltaPitch.toDouble()).toFloat() * 0.72F).toInt()).coerceIn(46, 135),
            this.sensitivityStepDeg()
         )
      }

      SyntheticMouseTurnBroker.claim("dojo_automatic_stamina", { 
         getSyntheticMouseTurn()
      })
   }

   private fun getSyntheticMouseTurn(): TurnDelta? {
      if (active && Config.fullyAutomaticStamina) {

         if (var10000 == null) {
            return null
         } else if (currentAimPlan == null) {
            return null
         } else {
            val plan: AutomaticStamina.AimPlan = currentAimPlan
            val sample: AutomaticStamina.AimSample = this.sampleAim(currentAimPlan, System.currentTimeMillis())



            return if (Math.abs(yawDelta) < 0.01 && Math.abs(pitchDelta) < 0.01)
return null
return else
               SyntheticMouseTurnBroker.TurnDelta(yawDelta / step, pitchDelta / step)
            }
      } else {
         return null
      }
   }

   private fun sampleAim(plan: jooon.features.dojo.AutomaticStamina.AimPlan, nowMs: Long): jooon.features.dojo.AutomaticStamina.AimSample {

      if (elapsed >= plan.durationMs) {
         return AutomaticStamina.AimSample(MathHelper.wrapDegrees(plan.targetYaw), plan.targetPitch)
      } else {

         return AutomaticStamina.AimSample(
            MathHelper.wrapDegrees(plan.startYaw + plan.deltaYaw * eased.toFloat()),
            (plan.startPitch + plan.deltaPitch * eased.toFloat()).coerceIn(-89.9F, 89.9F)
         )
      }
   }

   private fun sensitivityStepDeg(): Float {


      var `this24lambda_u2415`: Any
      try {
         `this24lambda_u2415` = Result.constructor_impl/* $VF was: constructor-impl */(
            var4.getMc().options.getMouseSensitivity().getValue() as Double
         )
      } catch (var7: java.lang.Throwable) {
         `this24lambda_u2415` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7))
      }

         0.5
return else
         `this24lambda_u2415`

      return ((scaled * scaled * scaled * 1.2).toFloat()).coerceAtLeast(0.01F)
   }

   private fun yawTo(dx: Double, dz: Double): Float {
      return Math.toDegrees(Math.atan2(-dx, dz)).toFloat()
   }

   private fun clearAim() {
      currentAimPlan = null
      SyntheticMouseTurnBroker.release("dojo_automatic_stamina")
   }

   private fun reset() {
      active = false
      middleBase = null
      wallHoles.clear()
      lastHoles.clear()
      holeDirections.clear()
      activePass = null
      lockedApproach = null
      recentlyPassedWall = null
      recentlyPassedWallTicks = 0
      this.clearAim()
      this.stopMotion()
   }

   private fun pauseFully() {
      currentAimPlan = null
      activePass = null
      lockedApproach = null
      recentlyPassedWall = null
      recentlyPassedWallTicks = 0
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
   }

   private data class ActivePass {
      val axis: Axis
      private Vec3d center;
      val entrySide: Double
      private Vec3d target;
      val desiredYaw: Float
      val requiresJump: Boolean

      fun ActivePass(axis: StaminaMovementLogic.Axis, center: Vec3d, entrySide: Double, target: Vec3d, desiredYaw: Float, requiresJump: Boolean) {
         this.axis = axis
         this.center = center
         this.entrySide = entrySide
         this.target = target
         this.desiredYaw = desiredYaw
         this.requiresJump = requiresJump
      }

      fun getCenter(): Vec3d {
         this.center
      }

      fun getTarget(): Vec3d {
         this.target
      }

      public operator fun component1(): Axis {
         return this.axis
      }

      fun component2(): Vec3d {
         this.center
      }

      public operator fun component3(): Double {
         return this.entrySide
      }

      fun component4(): Vec3d {
         this.target
      }

      public operator fun component5(): Float {
         return this.desiredYaw
      }

      public operator fun component6(): Boolean {
         return this.requiresJump
      }

      fun copy(axis: StaminaMovementLogic.Axis, center: Vec3d, entrySide: Double, target: Vec3d, desiredYaw: Float, requiresJump: Boolean): AutomaticStamina.ActivePass {
         AutomaticStamina.ActivePass(axis, center, entrySide, target, desiredYaw, requiresJump)
      }

      override fun toString(): String {
         return "ActivePass(axis=${this.axis}, center=${this.center}, entrySide=${this.entrySide}, target=${this.target}, desiredYaw=${this.desiredYaw}, requiresJump=${this.requiresJump})"
      }

      override fun hashCode(): Int {
         return (
                  (((this.axis.hashCode() * 31 + this.center.hashCode()) * 31 + java.lang.Double.hashCode(this.entrySide)) * 31 + this.target.hashCode()) * 31
                     + java.lang.Float.hashCode(this.desiredYaw)
               )
               * 31
            + java.lang.Boolean.hashCode(this.requiresJump)
         }

      override operator fun equals(other: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is AutomaticStamina.ActivePass
               && this.axis === (other as AutomaticStamina.ActivePass).axis
               && this.center == (other as AutomaticStamina.ActivePass).center
               && java.lang.Double.compare(this.entrySide, (other as AutomaticStamina.ActivePass).entrySide) == 0
               && this.target == (other as AutomaticStamina.ActivePass).target
               && java.lang.Float.compare(this.desiredYaw, (other as AutomaticStamina.ActivePass).desiredYaw) == 0
               && this.requiresJump == (other as AutomaticStamina.ActivePass).requiresJump
            }
      }
   }

   private data class AimPlan(startYaw: Float,
      startPitch: Float,
      targetYaw: Float,
      targetPitch: Float,
      deltaYaw: Float,
      deltaPitch: Float,
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

      public operator fun component7(): Long {
         return this.startAtMs
      }

      public operator fun component8(): Int {
         return this.durationMs
      }

      public operator fun component9(): Float {
         return this.sensitivityStepDeg
      }

      fun copy(
         startYaw: Float = this.startYaw,
         startPitch: Float = this.startPitch,
         targetYaw: Float = this.targetYaw,
         targetPitch: Float = this.targetPitch,
         deltaYaw: Float = this.deltaYaw,
         deltaPitch: Float = this.deltaPitch,
         startAtMs: Long = this.startAtMs,
         durationMs: Int = this.durationMs,
         sensitivityStepDeg: Float = this.sensitivityStepDeg
      ): jooon.features.dojo.AutomaticStamina.AimPlan {
         return AutomaticStamina.AimPlan(startYaw, startPitch, targetYaw, targetPitch, deltaYaw, deltaPitch, startAtMs, durationMs, sensitivityStepDeg)
      }

      override fun toString(): String {
         return "AimPlan(startYaw=${this.startYaw}, startPitch=${this.startPitch}, targetYaw=${this.targetYaw}, targetPitch=${this.targetPitch}, deltaYaw=${this.deltaYaw}, deltaPitch=${this.deltaPitch}, startAtMs=${this.startAtMs}, durationMs=${this.durationMs}, sensitivityStepDeg=${this.sensitivityStepDeg})"
      }

      override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (
                                                               (java.lang.Float.hashCode(this.startYaw) * 31 + java.lang.Float.hashCode(this.startPitch)) * 31
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
                              + java.lang.Long.hashCode(this.startAtMs)
                        )
                        * 31
                     + Integer.hashCode(this.durationMs)
               )
               * 31
            + java.lang.Float.hashCode(this.sensitivityStepDeg)
         }

      override operator fun equals(other: Any?): Boolean {
         label70@
         if (this === other) {
            return true
         } else {
            return other is AutomaticStamina.AimPlan
               && java.lang.Float.compare(this.startYaw, (other as AutomaticStamina.AimPlan).startYaw) == 0
               && java.lang.Float.compare(this.startPitch, (other as AutomaticStamina.AimPlan).startPitch) == 0
               && java.lang.Float.compare(this.targetYaw, (other as AutomaticStamina.AimPlan).targetYaw) == 0
               && java.lang.Float.compare(this.targetPitch, (other as AutomaticStamina.AimPlan).targetPitch) == 0
               && java.lang.Float.compare(this.deltaYaw, (other as AutomaticStamina.AimPlan).deltaYaw) == 0
               && java.lang.Float.compare(this.deltaPitch, (other as AutomaticStamina.AimPlan).deltaPitch) == 0
               && this.startAtMs == (other as AutomaticStamina.AimPlan).startAtMs
               && this.durationMs == (other as AutomaticStamina.AimPlan).durationMs
               && java.lang.Float.compare(this.sensitivityStepDeg, (other as AutomaticStamina.AimPlan).sensitivityStepDeg) == 0
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

      fun copy(yaw: Float = this.yaw, pitch: Float = this.pitch): jooon.features.dojo.AutomaticStamina.AimSample {
         return AutomaticStamina.AimSample(yaw, pitch)
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
            return other is AutomaticStamina.AimSample
               && java.lang.Float.compare(this.yaw, (other as AutomaticStamina.AimSample).yaw) == 0
               && java.lang.Float.compare(this.pitch, (other as AutomaticStamina.AimSample).pitch) == 0
            }
      }
   }

   private data class HoleTarget {
      private Box hole;
      val direction: HoleDirection
      val axis: Axis
      val phase: ApproachPhase
      private Vec3d center;
      private Vec3d target;
      val desiredYaw: Float
      val score: Double
      val wallDistance: Double
      val lateralError: Double
      val playerSide: Double
      val incoming: Boolean
      val requiresJump: Boolean

      fun HoleTarget(
         hole: Box,
         direction: StaminaMovementLogic.HoleDirection,
         axis: StaminaMovementLogic.Axis,
         phase: StaminaMovementLogic.ApproachPhase,
         center: Vec3d,
         target: Vec3d,
         desiredYaw: Float,
         score: Double,
         wallDistance: Double,
         lateralError: Double,
         playerSide: Double,
         incoming: Boolean,
         requiresJump: Boolean
      ) {
         this.hole = hole
         this.direction = direction
         this.axis = axis
         this.phase = phase
         this.center = center
         this.target = target
         this.desiredYaw = desiredYaw
         this.score = score
         this.wallDistance = wallDistance
         this.lateralError = lateralError
         this.playerSide = playerSide
         this.incoming = incoming
         this.requiresJump = requiresJump
      }

      fun getHole(): Box {
         this.hole
      }

      fun getCenter(): Vec3d {
         this.center
      }

      fun getTarget(): Vec3d {
         this.target
      }

      fun component1(): Box {
         this.hole
      }

      public operator fun component2(): HoleDirection {
         return this.direction
      }

      public operator fun component3(): Axis {
         return this.axis
      }

      public operator fun component4(): ApproachPhase {
         return this.phase
      }

      fun component5(): Vec3d {
         this.center
      }

      fun component6(): Vec3d {
         this.target
      }

      public operator fun component7(): Float {
         return this.desiredYaw
      }

      public operator fun component8(): Double {
         return this.score
      }

      public operator fun component9(): Double {
         return this.wallDistance
      }

      public operator fun component10(): Double {
         return this.lateralError
      }

      public operator fun component11(): Double {
         return this.playerSide
      }

      public operator fun component12(): Boolean {
         return this.incoming
      }

      public operator fun component13(): Boolean {
         return this.requiresJump
      }

      fun copy(
         hole: Box,
         direction: StaminaMovementLogic.HoleDirection,
         axis: StaminaMovementLogic.Axis,
         phase: StaminaMovementLogic.ApproachPhase,
         center: Vec3d,
         target: Vec3d,
         desiredYaw: Float,
         score: Double,
         wallDistance: Double,
         lateralError: Double,
         playerSide: Double,
         incoming: Boolean,
         requiresJump: Boolean
      ): AutomaticStamina.HoleTarget {
         AutomaticStamina.HoleTarget(
            hole, direction, axis, phase, center, target, desiredYaw, score, wallDistance, lateralError, playerSide, incoming, requiresJump
         )
      }

      override fun toString(): String {
         return "HoleTarget(hole=${this.hole}, direction=${this.direction}, axis=${this.axis}, phase=${this.phase}, center=${this.center}, target=${this.target}, desiredYaw=${this.desiredYaw}, score=${this.score}, wallDistance=${this.wallDistance}, lateralError=${this.lateralError}, playerSide=${this.playerSide}, incoming=${this.incoming}, requiresJump=${this.requiresJump})"
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
                                                                                                   (this.hole.hashCode() * 31 + this.direction.hashCode()) * 31
                                                                                                      + this.axis.hashCode()
                                                                                                )
                                                                                                * 31
                                                                                             + this.phase.hashCode()
                                                                                       )
                                                                                       * 31
                                                                                    + this.center.hashCode()
                                                                              )
                                                                              * 31
                                                                           + this.target.hashCode()
                                                                     )
                                                                     * 31
                                                                  + java.lang.Float.hashCode(this.desiredYaw)
                                                            )
                                                            * 31
                                                         + java.lang.Double.hashCode(this.score)
                                                   )
                                                   * 31
                                                + java.lang.Double.hashCode(this.wallDistance)
                                          )
                                          * 31
                                       + java.lang.Double.hashCode(this.lateralError)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.playerSide)
                        )
                        * 31
                     + java.lang.Boolean.hashCode(this.incoming)
               )
               * 31
            + java.lang.Boolean.hashCode(this.requiresJump)
         }

      override operator fun equals(other: Any?): Boolean {
         label94@
         if (this === other) {
            return true
         } else {
            return other is AutomaticStamina.HoleTarget
               && this.hole == (other as AutomaticStamina.HoleTarget).hole
               && this.direction === (other as AutomaticStamina.HoleTarget).direction
               && this.axis === (other as AutomaticStamina.HoleTarget).axis
               && this.phase === (other as AutomaticStamina.HoleTarget).phase
               && this.center == (other as AutomaticStamina.HoleTarget).center
               && this.target == (other as AutomaticStamina.HoleTarget).target
               && java.lang.Float.compare(this.desiredYaw, (other as AutomaticStamina.HoleTarget).desiredYaw) == 0
               && java.lang.Double.compare(this.score, (other as AutomaticStamina.HoleTarget).score) == 0
               && java.lang.Double.compare(this.wallDistance, (other as AutomaticStamina.HoleTarget).wallDistance) == 0
               && java.lang.Double.compare(this.lateralError, (other as AutomaticStamina.HoleTarget).lateralError) == 0
               && java.lang.Double.compare(this.playerSide, (other as AutomaticStamina.HoleTarget).playerSide) == 0
               && this.incoming == (other as AutomaticStamina.HoleTarget).incoming
               && this.requiresJump == (other as AutomaticStamina.HoleTarget).requiresJump
            }
      }
   }
}
