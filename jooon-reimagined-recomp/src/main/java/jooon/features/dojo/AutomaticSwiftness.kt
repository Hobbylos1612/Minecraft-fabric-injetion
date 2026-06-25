package jooon.features.dojo

import com.mojang.logging.LogUtils
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.Map.Entry
import jooon.config.Config
import jooon.features.dojo.SwiftnessMovementLogic.AirborneControl
import jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics
import jooon.features.dojo.SwiftnessMovementLogic.JumpPhase
import jooon.features.dojo.SwiftnessMovementLogic.JumpTuning
import jooon.features.dojo.SwiftnessMovementLogic.Node
import jooon.features.dojo.SwiftnessMovementLogic.Route
import jooon.features.dojo.SwiftnessMovementLogic.Step
import jooon.util.PlayerController
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import org.slf4j.Logger

object AutomaticSwiftness {
   private val logger: Logger = LogUtils.getLogger()
   private const val LOG_PREFIX: String = "JR-DOJO-SWIFTNESS"
   private const val WALK_REACHED_DISTANCE: Double = 0.28
   private const val WALK_YAW_LIMIT: Double = 18.0
   private const val WALK_ADJACENT_YAW_LIMIT: Double = 80.0
   private const val WALK_ADJACENT_DISTANCE_LIMIT: Double = 1.18
   private const val WALK_SPRINT_YAW_LIMIT: Double = 38.0
   private const val LOOK_STEP_DEGREES: Float = 50.0F
   private const val JUMP_LOOK_STEP_DEGREES: Float = 120.0F
   private const val WALK_FLOOR_PROBE_DISTANCE: Double = 0.92
   private const val WALK_FLOOR_SIDE_OFFSET: Double = 0.2
   private const val WALK_ADJACENT_LATERAL_READY_LIMIT: Double = 1.0
   private const val TARGET_CENTERED_DISTANCE: Double = 0.26
   private const val TARGET_SETTLED_SPEED: Double = 0.16
   private const val JUMP_CAPTURE_REACHED_DISTANCE: Double = 0.28
   private const val JUMP_FAST_HANDOFF_DISTANCE: Double = 0.76
   private const val JUMP_FAST_HANDOFF_AFTER_LONG_GAP_DISTANCE: Double = 0.48
   private const val JUMP_MISSED_DISTANCE: Double = 1.55
   private const val JUMP_TIMEOUT_TICKS: Int = 34
   private const val RECOVERY_SNEAK_TICKS: Int = 5
   private const val POST_JUMP_BRAKE_TICKS: Int = 0
   private const val MOVEMENT_LATERAL_READY_LIMIT: Double = 0.3
   private const val RECENTER_LATERAL_READY_LIMIT: Double = 0.62
   private const val MAX_ACTIVE_TARGET_DISTANCE: Double = 8.0
   private const val MAX_ACTIVE_TARGET_Y_DELTA: Double = 4.0
   private const val STARTUP_UNDERFOOT_IGNORE_TICKS: Int = 100
   private const val STARTUP_LIME_SCAN_RADIUS: Int = 8
   private const val STARTUP_LIME_SCAN_Y_RADIUS: Int = 4
   private const val BACKSTEP_STALL_TICKS: Int = 3
   private var active: Boolean
   
   private BlockPos latestTargetBlock;
   private var latestTargetSeq: Long
   
   private BlockPos startupIgnoredFloorBlock;
   private var startupIgnoreUntilTick: Int
   private var isPaused: Boolean
   private var route: Route?
   private var routeTargetSeq: Long = -1L
   private var routeTargetNode: Node?
   private var stepIndex: Int
   private var currentJumpStep: Step?
   private var jumpPhase: JumpPhase = SwiftnessMovementLogic.JumpPhase.IDLE
   private var jumpPhaseStartedTick: Int
   private var jumpPressUntilTick: Int
   private var backstepLastProjection: Double = java.lang.Double.NaN
   private var backstepStallTicks: Int
   private var recoverySneakTicks: Int
   private var waitForGroundRecovery: Boolean
   private var replanCooldownUntilTick: Int
   private var postJumpBrakeTicks: Int
   private var automatedSneakTicks: Int
   private val woolTrace: HashMap<BlockPos, jooon.features.dojo.AutomaticSwiftness.WoolTrace> = HashMap()

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         tick(client)
      })
   }

   fun onBlockUpdate(pos: BlockPos, state: BlockState) {
      this.traceWoolUpdate(pos, state)
      if (state.getBlock() == Blocks.LIME_WOOL) {



         if (active && player != null && this.shouldIgnoreStartupUnderfootTarget(var10000, player)) {
            if (latestTargetBlock == var10000) {
               latestTargetBlock = null

               this.clearRoute()
               this.debug("cleared startup underfoot target block=${pos.getX()},${pos.getY()},${pos.getZ()}")
            }

            this.debug("ignored startup underfoot lime block=${pos.getX()},${pos.getY()},${pos.getZ()}")
            if (level != null) {

               if (latestTargetBlock == null || latestTargetBlock == var10000 || !this.isPlausibleActiveTarget(var9, player)) {
                  this.promoteStartupReplacementTarget(level as World, player, "startup underfoot")
               }
            }
         } else if (!(latestTargetBlock == var10000)) {
            if (active && player != null && !this.isPlausibleActiveTarget(var10000, player)) {
               this.debug(this.describeIgnoredTarget("ignored remote target", var10000, player))
            } else {
               latestTargetBlock = var10000

               if (active) {
                  this.debug("target seq=${latestTargetSeq} block=${pos.getX()},${pos.getY()},${pos.getZ()}")
               }
            }
         }
      }
   }

   fun onSectionBlocksUpdate(packet: ChunkDeltaUpdateS2CPacket) {
      packet.visitUpdates({ pos: BlockPos, state: BlockState ->
         onBlockUpdate(pos, state)
      })
   }

   fun planRoute(level: World, player: ClientPlayerEntity, targetNode: SwiftnessMovementLogic.Node) {
      val start: SwiftnessMovementLogic.Node = this.playerStartNode(level, player)
      val directPlan: SwiftnessMovementLogic.Route = SwiftnessMovementLogic.INSTANCE
         .planTemporaryPlatformRoute(start, targetNode, { it: SwiftnessMovementLogic.Node ->
            isStandable(`$level`, it)
         }, { it: SwiftnessMovementLogic.Node ->
            isPassable(`$level`, it)
         })
         var var10000: SwiftnessMovementLogic.Route = directPlan
      if (directPlan == null) {
         var10000 = SwiftnessMovementLogic.INSTANCE
            .planTemporaryPlatformRouteWithIntermediates(start, targetNode, this.liveLimeFeetNodes(level), { it: SwiftnessMovementLogic.Node ->
               isStandable(`$level`, it)
            }, { it: SwiftnessMovementLogic.Node ->
               isPassable(`$level`, it)
            })
         }

      if (var10000 == null) {
         this.debug("route rejected start=$start target=$targetNode")
         this.stopAndCooldown(player.age, 4)
      } else {
         route = var10000
         routeTargetSeq = latestTargetSeq
         routeTargetNode = targetNode
         stepIndex = 0
         this.resetJumpState()
         this.debug(
            "route seq=${routeTargetSeq} kind=${if (directPlan == null) "via-lime" else "direct"} steps=${var10000.steps.size()} target=$targetNode path=${this.describeRoute(
return var10000
            )}"
         )
      }
   }

   fun executeRoute(level: World, player: ClientPlayerEntity) {
      if (route != null) {
         val currentRoute: SwiftnessMovementLogic.Route = route
         if (route.steps.isEmpty()) {
            this.stopMotion()
         } else if (0 > stepIndex || stepIndex >= currentRoute.steps.size()) {
            this.stopMotion()
            this.clearRoute()
         } else {
            val var6: SwiftnessMovementLogic.Step = currentRoute.steps.get(stepIndex)
            if (!this.isStandable(level, var6.to)) {
               this.debug("route invalid landing=${var6.to}")
               this.stopAndCooldown(player.age, 3)
            } else {
               when (AutomaticSwiftness.WhenMappings.$EnumSwitchMapping$0[var6.moveType.ordinal()]) {
                  1 -> this.executeWalkStep(level, player, var6)
                  2 -> this.executeJumpStep(level, player, var6)
                  else -> throw NoWhenBranchMatchedException()
               }
            }
         }
      }
   }

   private fun advanceStep() {


      this.resetJumpState()
      val var3: SwiftnessMovementLogic.Route = route
      if (route == null || stepIndex >= route.steps.size()) {
         this.stopMotion()
         if (var3 != null) {
            route = SwiftnessMovementLogic.Route(emptyList())
            stepIndex = 0
         }

         if (finishedJump) {
         }
      }
   }

   private fun recoverWithSneak(waitForGround: Boolean) {
      this.clearRoute()
      this.stopMotion()
      this.pressSneak(true)
      recoverySneakTicks = 5
      waitForGroundRecovery = waitForGround
   }

   fun recenterOnSourceTile(player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step): Boolean {
      if (this.horizontalDistanceTo(player, step.from) <= 0.24) {
return false
      } else {
         this.recenterOnNode(player, step.from, false)
return true
      }
   }

   fun recenterOnNode(player: ClientPlayerEntity, node: SwiftnessMovementLogic.Node, sneak: Boolean) {


      this.aimAtYaw(player, this.yawTo(player, centerX, centerZ), 50.0F)
      this.applyMovementToward(player, centerX, centerZ, true, false, false, sneak, 0.62)
   }

   private fun stopAndCooldown(currentTick: Int, ticks: Int) {
      this.clearRoute()
      this.stopMotion()
      recoverySneakTicks = 0
      waitForGroundRecovery = false
      replanCooldownUntilTick = currentTick + ticks
   }

   private fun pauseFully() {
      this.stopMotion()
      this.clearRoute()
   }

   private fun reset() {
      active = false
      latestTargetBlock = null

      startupIgnoredFloorBlock = null
      startupIgnoreUntilTick = 0
      isPaused = false
      DojoPauseInput.reset()
      woolTrace.clear()
      this.clearRoute()
      recoverySneakTicks = 0
      waitForGroundRecovery = false
      replanCooldownUntilTick = 0
      postJumpBrakeTicks = 0
      automatedSneakTicks = 0
      this.stopMotion()
      this.debug("deactivated")
   }

   private fun clearRoute() {
      route = null
      routeTargetSeq = -1L
      routeTargetNode = null
      stepIndex = 0
      this.resetJumpState()
   }

   private fun resetJumpState() {
      currentJumpStep = null
      jumpPhase = SwiftnessMovementLogic.JumpPhase.IDLE
      jumpPhaseStartedTick = 0
      jumpPressUntilTick = 0
      backstepLastProjection = java.lang.Double.NaN
      backstepStallTicks = 0
   }

   private fun isCommittedJump(): Boolean {
      return jumpPhase === SwiftnessMovementLogic.JumpPhase.AIRBORNE || jumpPhase === SwiftnessMovementLogic.JumpPhase.CAPTURE
   }

   private fun stopMotion() {
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressJump(false)
      PlayerController.pressSprint(false)
      this.pressSneak(false)
   }

   private fun holdAirborneJumpControl(step: Step, projection: Double, jump: Boolean) {
      val control: SwiftnessMovementLogic.AirborneControl = this.airborneControlForStep(step, projection)
      PlayerController.pressForward(control.forward)
      PlayerController.pressBack(control.back)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressSprint(control.sprint)
      PlayerController.pressJump(jump)
      this.pressSneak(this.shouldSneakDuringLanding(step, projection))
   }

   private fun updateBackstepStall(metrics: JumpMetrics) {
      if (!java.lang.Double.isNaN(backstepLastProjection) && metrics.projection >= backstepLastProjection - 0.015) {

      } else {
         backstepStallTicks = 0
      }

      backstepLastProjection = metrics.projection
   }

   fun isBackstepBlocked(level: World, player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step): Boolean {






      val sourceBehind: java.util.Iterator = listOf(arrayOf(0.36, 0.62)).iterator()

      while (sourceBehind.hasNext()) {

         val sample: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
            MathHelper.floor(player.getX() - unitX * var20), feetY, MathHelper.floor(player.getZ() - unitZ * var20)
         )
         if (!(sample == this.toNode(player)) && (!this.isPassable(level, sample) || !this.isPassable(level, sample.above()))) {
return true
         }
      }

      val var19: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
         MathHelper.floor(step.from.x.toDouble() + 0.5 - unitX * 0.62), step.from.y, MathHelper.floor(step.from.z.toDouble() + 0.5 - unitZ * 0.62)
      )
      !(var19 == step.from) && (!this.isPassable(level, var19) || !this.isPassable(level, var19.above()))
   }

   private fun airborneControlForStep(step: Step, projection: Double): AirborneControl {
      if (!this.isAngledStep(step)) {
         return SwiftnessMovementLogic.airborneControl(step.gapBlocks, projection)
      } else {
         val tuning: SwiftnessMovementLogic.JumpTuning = this.jumpTuningForStep(step)


         return if (projection >= (Math.min(tuning.brakeProjection, distance + 0.16)).coerceAtLeast(release + 0.14))
            SwiftnessMovementLogic.AirborneControl(false, true, false)
return else
            (
               if (projection >= release)
                  SwiftnessMovementLogic.AirborneControl(false, false, false)
return else
                  SwiftnessMovementLogic.AirborneControl(true, false, tuning.airborneSprint)
            )
         }
   }

   fun applyMovementToward(
      player: ClientPlayerEntity,
      targetX: Double,
      targetZ: Double,
      allowBack: Boolean,
      sprint: Boolean,
      jump: Boolean,
      sneak: Boolean,
      lateralReadyLimit: Double
   ) {







      if (Math.abs(rightInput) > lateralReadyLimit) {
         PlayerController.pressForward(false)
         PlayerController.pressBack(false)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressSprint(false)
         PlayerController.pressJump(false)
         this.pressSneak(false)
         if (player.age % 5 == 0) {
            var var33: Array<Any> = arrayOf(targetX)

            var33 = arrayOf(targetZ)

            var33 = arrayOf(forwardInput)

            var33 = arrayOf(rightInput)

            var33 = arrayOf(lateralReadyLimit)

            var33 = arrayOf(player.getYaw())

            this.debug("movement yaw hold target=$var10001,$var10002 forward=$var10003 right=$var10004 limit=$var10005 yaw=$var10006")
         }
      } else {
         PlayerController.pressForward(wantForward)
         PlayerController.pressBack(wantBack)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressSprint(sprint && wantForward)
         PlayerController.pressJump(jump)
         this.pressSneak(sneak)
      }
   }

   private fun pressSneak(down: Boolean) {
      PlayerController.pressSneak(down)
      automatedSneakTicks = if (down) 2 else 0
   }

   fun safeForwardFloor(level: World, player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step, targetX: Double, targetZ: Double): Boolean {
      if (!this.isStandable(level, step.to)) {
return false
      } else {



         if (length < 0.001) {
return true
         } else {







            // $VF: Unable to resugar Kotlin loop from Java for loop
            var distance: Double = 0.3
            while (true) {
               if (distance <= maxDistance + 0.001) break
               val var27: java.util.Iterator = listOf(arrayOf(-0.2, 0.0, 0.2)).iterator()

               while (var27.hasNext()) {

                  val sampleNode: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
                     MathHelper.floor(player.getX() + dirX * distance + sideX * var35),
                     feetY,
                     MathHelper.floor(player.getZ() + dirZ * distance + sideZ * var35)
                  )
                  if (!(sampleNode == step.from) && !(sampleNode == step.to) && !this.isStandable(level, sampleNode)) {
return false
                  }
               }

               distance += 0.31
            }
return true
         }
      }
   }

   fun jumpMetrics(player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step, targetYaw: Float): SwiftnessMovementLogic.JumpMetrics {







      SwiftnessMovementLogic.JumpMetrics(
         relX * unitX + relZ * unitZ,
         Math.abs(relX * unitZ - relZ * unitX),
         this.yawError(player, targetYaw),
         player.isOnGround(),
         player.age - jumpPhaseStartedTick
      )
   }

   private fun segmentPoint(step: Step, projection: Double): Pair<Double, Double> {



      return Pair(step.from.x.toDouble() + 0.5 + projection * (dx / length), step.from.z.toDouble() + 0.5 + projection * (dz / length))
   }

   fun yawForStep(player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step): Float {
      this.yawTo(player, step.to.x.toDouble() + 0.5, step.to.z.toDouble() + 0.5)
   }

   fun yawTo(player: ClientPlayerEntity, targetX: Double, targetZ: Double): Float {
      (Math.atan2(-(targetX - player.getX()), targetZ - player.getZ()) * 180.0 / Math.PI).toFloat()
   }

   fun aimAtYaw(player: ClientPlayerEntity, targetYaw: Float, maxStep: Float) {

      player.setYaw(nextYaw)
      player.headYaw = nextYaw
      player.bodyYaw = nextYaw
   }

   fun yawError(player: ClientPlayerEntity, targetYaw: Float): Double {
      Math.abs(MathHelper.wrapDegrees(targetYaw - player.getYaw()).toDouble())
   }

   fun horizontalDistanceTo(player: ClientPlayerEntity, node: SwiftnessMovementLogic.Node): Double {
      Math.hypot(node.x.toDouble() + 0.5 - player.getX(), node.z.toDouble() + 0.5 - player.getZ())
   }

   fun isSafelyStandingOnTarget(player: ClientPlayerEntity, target: SwiftnessMovementLogic.Node): Boolean {
      player.isOnGround()
         && !(Math.abs(player.getY() - target.y.toDouble()) > 0.45)
         && player.getBlockPos().getX() == target.x
         && player.getBlockPos().getZ() == target.z
         && this.horizontalDistanceTo(player, target) <= 0.26
         && this.horizontalSpeed(player) <= 0.16
      }

   fun horizontalSpeed(player: ClientPlayerEntity): Double {

      Math.hypot(var10000.x, var10000.z)
   }

   fun ClientPlayerEntity.toNode(): SwiftnessMovementLogic.Node {
      SwiftnessMovementLogic.Node(
         MathHelper.floor(this.getX()),
         MathHelper.floor(this.getY()),
         MathHelper.floor(this.getZ())
      )
   }

   fun playerStartNode(level: World, player: ClientPlayerEntity): SwiftnessMovementLogic.Node {
      val base: SwiftnessMovementLogic.Node = this.toNode(player)
      var best: SwiftnessMovementLogic.Node = if (this.isStandable(level, base)) base else null
      var bestScore: Double = if (best != null) nodeDistanceScore(player, best) else java.lang.Double.POSITIVE_INFINITY

      for (dy in -1..1) {
         for (dx in -1..1) {
            for (dz in -1..1) {
               val candidate: SwiftnessMovementLogic.Node = base.offset(dx, dy, dz)
               if (this.isStandable(level, candidate)) {

                  if (var13 < bestScore) {
                     best = candidate
                     bestScore = var13
                  }
               }
            }
         }
      }

      var var14: SwiftnessMovementLogic.Node = best
      if (best == null) {
         var14 = base
      }
return var14
   }

   fun nodeDistanceScore(player: ClientPlayerEntity, node: SwiftnessMovementLogic.Node): Double {
      Math.hypot(node.x.toDouble() + 0.5 - player.getX(), node.z.toDouble() + 0.5 - player.getZ())
         + Math.abs(node.y.toDouble() - player.getY()) * 0.35
      }

   fun BlockPos.toNode(): SwiftnessMovementLogic.Node {
      SwiftnessMovementLogic.Node(this.getX(), this.getY(), this.getZ())
   }

   fun isStandable(level: World, node: SwiftnessMovementLogic.Node): Boolean {
      this.isPassable(level, node) && this.isPassable(level, node.above()) && this.hasSolidFloor(level, node.x, node.y - 1, node.z)
   }

   fun isPassable(level: World, node: SwiftnessMovementLogic.Node): Boolean {



      var6.isEmpty()
   }

   fun hasSolidFloor(level: World, x: Int, y: Int, z: Int): Boolean {



      !var8.isEmpty()
   }

   fun isFloorBlock(level: World, node: SwiftnessMovementLogic.Node, block: Block): Boolean {
      level.getBlockState(BlockPos(node.x, node.y - 1, node.z)).getBlock() == block
   }

   fun isExpiringFloor(level: World, node: SwiftnessMovementLogic.Node): Boolean {

      var10000 == Blocks.ORANGE_WOOL || var10000 == Blocks.RED_WOOL
   }

   private fun isAngledStep(step: Step): Boolean {
      return step.to.x != step.from.x && step.to.z != step.from.z
   }

   private fun stepHorizontalDistance(step: Step): Double {
      return Math.hypot((step.to.x - step.from.x).toDouble(), (step.to.z - step.from.z).toDouble())
   }

   private fun jumpTuningForStep(step: Step): JumpTuning {
      return SwiftnessMovementLogic.jumpTuningForDistance(step.gapBlocks, this.stepHorizontalDistance(step), this.isAngledStep(step))
   }

   fun canFastHandoffAfterJump(level: World, step: SwiftnessMovementLogic.Step, player: ClientPlayerEntity, landingDistance: Double, speed: Double): Boolean {
      val nextStep: SwiftnessMovementLogic.Step = this.plannedFirstStepFromLanding(level, step.to)
      val var10000: SwiftnessMovementLogic.JumpMetrics = if (nextStep != null)
         jumpMetrics(player, nextStep, yawForStep(player, nextStep))
return else
return null
         .shouldFastHandoffAfterJump(
            latestTargetSeq, routeTargetSeq, player.isOnGround(), step.gapBlocks, landingDistance, speed, 0.76, 0.48, 0.16, 0.28, nextStep, var10000
         )
         if (!allowed
         && (if (nextStep != null) nextStep.moveType else null) === SwiftnessMovementLogic.MoveType.JUMP
         && nextStep.gapBlocks >= 3
         && player.age % 3 == 0) {

         var var19: Array<Any> = arrayOf(landingDistance)

         var19 = arrayOf(speed)

         var19 = arrayOf(if (var10000 != null) var10000.projection else 0.0)

         var19 = arrayOf(if (var10000 != null) var10000.laneError else 0.0)

         var19 = arrayOf(if (var10000 != null) var10000.yawError else 0.0)

         this.debug("jump capture gated handoff nextGap=$var10001 distance=$var10002 speed=$var10003 nextProj=$var10004 nextLane=$var10005 nextYaw=$var10006")
      }
return allowed
   }

   fun plannedFirstStepFromLanding(level: World, landing: SwiftnessMovementLogic.Node): SwiftnessMovementLogic.Step {
      if (latestTargetBlock == null) {
return null
      } else {
         val planned: SwiftnessMovementLogic.Route = SwiftnessMovementLogic.INSTANCE
            .planTemporaryPlatformRoute(
               landing, SwiftnessMovementLogic.targetFeetNode(this.toNode(latestTargetBlock)), { it: SwiftnessMovementLogic.Node ->
                  isStandable(`$level`, it)
               }, { it: SwiftnessMovementLogic.Node ->
                  isPassable(`$level`, it)
               }
            )
            if (planned != null) {
            val var10000: java.util.List = planned.steps
            if (var10000 != null) {
               firstOrNull(var10000) as SwiftnessMovementLogic.Step
            }
         }
return null
      }
   }

   fun repairStartupUnderfootTarget(level: World, player: ClientPlayerEntity): Boolean {
      if (startupIgnoredFloorBlock == null) {
return false
      } else {

         if (player.age > startupIgnoreUntilTick) {
            startupIgnoredFloorBlock = null
return false
         } else if (latestTargetBlock == startupIgnoredFloorBlock) {
            latestTargetBlock = null

            this.clearRoute()
            this.debug("cleared startup underfoot target block=${ignored.getX()},${ignored.getY()},${ignored.getZ()}")
            this.promoteStartupReplacementTarget(level, player, "startup underfoot")
            latestTargetBlock != null
         } else {
            latestTargetBlock == null && this.promoteStartupReplacementTarget(level, player, "startup underfoot")
         }
      }
   }

   fun promoteStartupReplacementTarget(level: World, player: ClientPlayerEntity, reason: String): Boolean {

      if (var10000 == null) {
return false
      } else {
         latestTargetBlock = var10000

         this.clearRoute()
         this.debug("$reason replacement target seq=${latestTargetSeq} block=${var10000.getX()},${var10000.getY()},${var10000.getZ()}")
return true
      }
   }

   fun liveLimeFeetNodes(level: World): MutableSet<SwiftnessMovementLogic.Node> {
      toSet(
         map(
            filter(
               asSequence(woolTrace),
               { var1: Entry ->
                  (var1.getValue() as AutomaticSwiftness.WoolTrace).color == "lime"
                     && `$level`.getBlockState(var1.getKey() as BlockPos).getBlock() == Blocks.LIME_WOOL
                  }
            ),
            { var0: Entry ->
               SwiftnessMovementLogic.targetFeetNode(toNode(var0.getKey() as BlockPos))
            }
         )
      )
   }

   fun selectStartupReplacementTarget(level: World, player: ClientPlayerEntity): BlockPos {
      if (startupIgnoredFloorBlock == null) {
return null
      } else {
         val var10000: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.INSTANCE
            .startupReplacementTarget(this.toNode(startupIgnoredFloorBlock), this.liveStartupLimeTargetBlocks(level, player, startupIgnoredFloorBlock))
            if (var10000 == null) {
return null
         } else {

            if (this.isPlausibleActiveTarget(var7, player)) var7 else null
         }
      }
   }

   fun liveStartupLimeTargetBlocks(level: World, player: ClientPlayerEntity, ignored: BlockPos): MutableSet<SwiftnessMovementLogic.Node> {
      val traced: java.util.Set = this.liveLimeTargetBlocks(level)





      var x: Int = var10000.getX() - 8

      if (x <= var10) {
         while (true) {
            var z: Int = center.getZ() - 8

            if (z <= var12) {
               while (true) {
                  var y: Int = minY
                  if (minY <= maxY) {
                     while (true) {

                        if (level.getBlockState(pos).getBlock() == Blocks.LIME_WOOL) {
                           scanned.add(this.toNode(pos))
                        }

                        if (y == maxY) {
break
                        }

                        y++
                     }
                  }

                  if (z == var12) {
break
                  }

                  z++
               }
            }

            if (x == var10) {
break
            }

            x++
         }
      }

      SetsKt.plus(traced, scanned)
   }

   fun liveLimeTargetBlocks(level: World): MutableSet<SwiftnessMovementLogic.Node> {
      toSet(
         map(
            filter(
               asSequence(woolTrace),
               { var1: Entry ->
                  (var1.getValue() as AutomaticSwiftness.WoolTrace).color == "lime"
                     && `$level`.getBlockState(var1.getKey() as BlockPos).getBlock() == Blocks.LIME_WOOL
                  }
            ),
            { var0: Entry ->
               toNode(var0.getKey() as BlockPos)
            }
         )
      )
   }

   fun startupIgnoredFloorBlock(level: World, player: ClientPlayerEntity): BlockPos {
      val startNode: SwiftnessMovementLogic.Node = this.playerStartNode(level, player)


      if (!(var10000 == Blocks.CHISELED_STONE_BRICKS) && !(var10000 == Blocks.LIME_WOOL)) null else floor.toImmutable()
   }

   fun shouldIgnoreStartupUnderfootTarget(target: BlockPos, player: ClientPlayerEntity): Boolean {
      if (startupIgnoredFloorBlock == null) {
return false
      } else {
         label28@
         if (player.age > startupIgnoreUntilTick) {
            startupIgnoredFloorBlock = null
return false
         } else {
            target == startupIgnoredFloorBlock
               && this.horizontalDistanceTo(player, SwiftnessMovementLogic.targetFeetNode(this.toNode(target))) <= 0.85
            }
      }
   }

   fun isPlausibleActiveTarget(pos: BlockPos, player: ClientPlayerEntity): Boolean {
      !(Math.abs(player.getY() - (pos.getY().toDouble() + 1.0)) > 4.0) && this.targetDistanceFromPlayer(pos, player) <= 8.0
   }

   fun targetDistanceFromPlayer(pos: BlockPos, player: ClientPlayerEntity): Double {
      Math.hypot(pos.getX().toDouble() + 0.5 - player.getX(), pos.getZ().toDouble() + 0.5 - player.getZ())
   }

   fun describeIgnoredTarget(prefix: String, pos: BlockPos, player: ClientPlayerEntity): String {





      var var14: Array<Any> = arrayOf(player.getX())

      var14 = arrayOf(player.getY())

      var14 = arrayOf(player.getZ())

      var14 = arrayOf(distance)

      var14 = arrayOf(dy)

      "$prefix block=$var10001,$var10002,$var10003 player=$var10004,$var10005,$var10006 distance=$var10007 dy=$var10008"
   }

   private fun shouldSneakDuringLanding(step: Step, projection: Double): Boolean {
      return this.isAngledStep(step) && projection >= this.stepHorizontalDistance(step) - 0.75
   }

   private fun overrunProjection(step: Step, tuning: JumpTuning): Double {
      return SwiftnessMovementLogic.airborneOverrunProjection(tuning, this.stepHorizontalDistance(step), this.isAngledStep(step))
   }

   private fun landingProjectionLimit(step: Step, tuning: JumpTuning): Double {
      return SwiftnessMovementLogic.landingProjectionLimit(tuning, this.stepHorizontalDistance(step), step.gapBlocks, this.isAngledStep(step))
   }

   private fun landingProjectionFloor(step: Step): Double {
      return if (step.gapBlocks < 2) java.lang.Double.NEGATIVE_INFINITY else this.stepHorizontalDistance(step) - 0.55
   }

   private fun Int.signInt(): Int {
      return Math.signum(this.toDouble()).toInt()
   }

   private fun debug(message: String) {
      logger.info("[JR-DOJO-SWIFTNESS] $message")
   }

   fun traceWoolUpdate(pos: BlockPos, state: BlockState) {





      if (color == null) {
         val var10: AutomaticSwiftness.WoolTrace = woolTrace.remove(var11)
         if (active && var10 != null) {
            this.debug("wool ${pos.getX()},${pos.getY()},${pos.getZ()} ${var10.color}->clear age=${tick - var10.tick}")
         }
      } else {
         val previous: AutomaticSwiftness.WoolTrace = woolTrace.put(var11, AutomaticSwiftness.WoolTrace(color, tick))
         if (active && !((if (previous != null) previous.color else null) == color)) {
            var var10002: Int
            var var10003: Int
            var var10004: String
            run label54@{
               var12 = if (previous != null) tick - previous.tick else 0
               var13 = pos.getX()
               var10002 = pos.getY()
               var10003 = pos.getZ()
               if (previous != null) {
                  var10004 = previous.color
                  if (var10004 != null) {
                     return@label54
                  }
               }

               var10004 = "new"
            }

            this.debug("wool $var13,$var10002,$var10003 $var10004->$color age=$var12")
         }
      }
   }

   fun swiftnessWoolColor(block: Block): String {
      if (block == Blocks.LIME_WOOL) "lime" else (if (block == Blocks.ORANGE_WOOL) "orange" else (if (block == Blocks.RED_WOOL) "red" else null))
   }

   private fun describeRoute(route: Route): String {
      return joinToString$default(route.steps, "|", null, null, 0, null, { step: SwiftnessMovementLogic.Step ->
         ("${step.moveType}:${step.from.x},${step.from.y},${step.from.z}->${step.to.x},${step.to.y},${step.to.z}:g${step.gapBlocks}") as java.lang.CharSequence
      }, 30, null)
   }

   private data class WoolTrace(color: String, tick: Int) {
      val color: String
      val tick: Int

      init {
         this.color = color
         this.tick = tick
      }

      public operator fun component1(): String {
         return this.color
      }

      public operator fun component2(): Int {
         return this.tick
      }

      fun copy(color: String = this.color, tick: Int = this.tick): jooon.features.dojo.AutomaticSwiftness.WoolTrace {
         return AutomaticSwiftness.WoolTrace(color, tick)
      }

      override fun toString(): String {
         return "WoolTrace(color=${this.color}, tick=${this.tick})"
      }

      override fun hashCode(): Int {
         return this.color.hashCode() * 31 + Integer.hashCode(this.tick)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutomaticSwiftness.WoolTrace
               && this.color == (other as AutomaticSwiftness.WoolTrace).color
               && this.tick == (other as AutomaticSwiftness.WoolTrace).tick
            }
      }
   }
}
