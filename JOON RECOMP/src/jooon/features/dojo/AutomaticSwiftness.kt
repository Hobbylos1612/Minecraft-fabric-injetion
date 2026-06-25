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
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.class_2338
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

@SourceDebugExtension(["SMAP\nAutomaticSwiftness.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutomaticSwiftness.kt\njooon/features/dojo/AutomaticSwiftness\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1407:1\n1#2:1408\n*E\n"])
public object AutomaticSwiftness {
   private final val logger: Logger = LogUtils.getLogger()
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
   private final var active: Boolean
   @JvmStatic
   private BlockPos latestTargetBlock;
   private final var latestTargetSeq: Long
   @JvmStatic
   private BlockPos startupIgnoredFloorBlock;
   private final var startupIgnoreUntilTick: Int
   private final var isPaused: Boolean
   private final var route: Route?
   private final var routeTargetSeq: Long = -1L
   private final var routeTargetNode: Node?
   private final var stepIndex: Int
   private final var currentJumpStep: Step?
   private final var jumpPhase: JumpPhase = SwiftnessMovementLogic.JumpPhase.IDLE
   private final var jumpPhaseStartedTick: Int
   private final var jumpPressUntilTick: Int
   private final var backstepLastProjection: Double = java.lang.Double.NaN
   private final var backstepStallTicks: Int
   private final var recoverySneakTicks: Int
   private final var waitForGroundRecovery: Boolean
   private final var replanCooldownUntilTick: Int
   private final var postJumpBrakeTicks: Int
   private final var automatedSneakTicks: Int
   private final val woolTrace: HashMap<class_2338, jooon.features.dojo.AutomaticSwiftness.WoolTrace> = HashMap()

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         INSTANCE.tick(client)
      })
   }

   fun onBlockUpdate(pos: BlockPos, state: BlockState) {
      this.traceWoolUpdate(pos, state)
      if (state.method_26204() == Blocks.field_10028) {
         val var10000: BlockPos = pos.method_10062()
         val player: ClientPlayerEntity = this.getMc().field_1724
         val level: ClientWorld = this.getMc().field_1687
         if (active && player != null && this.shouldIgnoreStartupUnderfootTarget(var10000, player)) {
            if (latestTargetBlock == var10000) {
               latestTargetBlock = null
               val var8: Int = latestTargetSeq++
               this.clearRoute()
               this.debug("cleared startup underfoot target block=${pos.method_10263()},${pos.method_10264()},${pos.method_10260()}")
            }

            this.debug("ignored startup underfoot lime block=${pos.method_10263()},${pos.method_10264()},${pos.method_10260()}")
            if (level != null) {
               val var9: BlockPos = latestTargetBlock
               if (latestTargetBlock == null || latestTargetBlock == var10000 || !this.isPlausibleActiveTarget(var9, player)) {
                  this.promoteStartupReplacementTarget(level as World, player, "startup underfoot")
               }
            }
         } else if (!(latestTargetBlock == var10000)) {
            if (active && player != null && !this.isPlausibleActiveTarget(var10000, player)) {
               this.debug(this.describeIgnoredTarget("ignored remote target", var10000, player))
            } else {
               latestTargetBlock = var10000
               val currentTarget: Int = latestTargetSeq++
               if (active) {
                  this.debug("target seq=${latestTargetSeq} block=${pos.method_10263()},${pos.method_10264()},${pos.method_10260()}")
               }
            }
         }
      }
   }

   fun onSectionBlocksUpdate(packet: ChunkDeltaUpdateS2CPacket) {
      packet.method_30621({ pos: BlockPos, state: BlockState ->
         INSTANCE.onBlockUpdate(pos, state)
      })
   }

   fun planRoute(level: World, player: ClientPlayerEntity, targetNode: SwiftnessMovementLogic.Node) {
      val start: SwiftnessMovementLogic.Node = this.playerStartNode(level, player)
      val directPlan: SwiftnessMovementLogic.Route = SwiftnessMovementLogic.INSTANCE
         .planTemporaryPlatformRoute(start, targetNode, { it: SwiftnessMovementLogic.Node ->
            INSTANCE.isStandable(`$level`, it)
         }, { it: SwiftnessMovementLogic.Node ->
            INSTANCE.isPassable(`$level`, it)
         })
         var var10000: SwiftnessMovementLogic.Route = directPlan
      if (directPlan == null) {
         var10000 = SwiftnessMovementLogic.INSTANCE
            .planTemporaryPlatformRouteWithIntermediates(start, targetNode, this.liveLimeFeetNodes(level), { it: SwiftnessMovementLogic.Node ->
               INSTANCE.isStandable(`$level`, it)
            }, { it: SwiftnessMovementLogic.Node ->
               INSTANCE.isPassable(`$level`, it)
            })
         }

      if (var10000 == null) {
         this.debug("route rejected start=$start target=$targetNode")
         this.stopAndCooldown(player.field_6012, 4)
      } else {
         route = var10000
         routeTargetSeq = latestTargetSeq
         routeTargetNode = targetNode
         stepIndex = 0
         this.resetJumpState()
         this.debug(
            "route seq=${routeTargetSeq} kind=${if (directPlan == null) "via-lime" else "direct"} steps=${var10000.steps.size()} target=$targetNode path=${this.describeRoute(
               var10000
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
               this.stopAndCooldown(player.field_6012, 3)
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
      val finishedJump: Boolean = currentJumpStep != null
      val currentRoute: Int = stepIndex++
      this.resetJumpState()
      val var3: SwiftnessMovementLogic.Route = route
      if (route == null || stepIndex >= route.steps.size()) {
         this.stopMotion()
         if (var3 != null) {
            route = SwiftnessMovementLogic.Route(CollectionsKt.emptyList())
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
         false
      } else {
         this.recenterOnNode(player, step.from, false)
         true
      }
   }

   fun recenterOnNode(player: ClientPlayerEntity, node: SwiftnessMovementLogic.Node, sneak: Boolean) {
      val centerX: Double = node.x + 0.5
      val centerZ: Double = node.z + 0.5
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
      val var1: Int = latestTargetSeq++
      startupIgnoredFloorBlock = null
      startupIgnoreUntilTick = 0
      isPaused = false
      DojoPauseInput.INSTANCE.reset()
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
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSprint(false)
      this.pressSneak(false)
   }

   private fun holdAirborneJumpControl(step: Step, projection: Double, jump: Boolean) {
      val control: SwiftnessMovementLogic.AirborneControl = this.airborneControlForStep(step, projection)
      PlayerController.INSTANCE.pressForward(control.forward)
      PlayerController.INSTANCE.pressBack(control.back)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressSprint(control.sprint)
      PlayerController.INSTANCE.pressJump(jump)
      this.pressSneak(this.shouldSneakDuringLanding(step, projection))
   }

   private fun updateBackstepStall(metrics: JumpMetrics) {
      if (!java.lang.Double.isNaN(backstepLastProjection) && metrics.projection >= backstepLastProjection - 0.015) {
         val var2: Int = backstepStallTicks++
      } else {
         backstepStallTicks = 0
      }

      backstepLastProjection = metrics.projection
   }

   fun isBackstepBlocked(level: World, player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step): Boolean {
      val dx: Double = step.to.x - step.from.x
      val dz: Double = step.to.z - step.from.z
      val length: Double = RangesKt.coerceAtLeast(Math.hypot(dx, dz), 1.0E-4)
      val unitX: Double = dx / length
      val unitZ: Double = dz / length
      val feetY: Int = MathHelper.method_15357(player.method_23318())
      val sourceBehind: java.util.Iterator = CollectionsKt.listOf(arrayOf(0.36, 0.62)).iterator()

      while (sourceBehind.hasNext()) {
         val var20: Double = (sourceBehind.next() as java.lang.Number).doubleValue()
         val sample: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
            MathHelper.method_15357(player.method_23317() - unitX * var20), feetY, MathHelper.method_15357(player.method_23321() - unitZ * var20)
         )
         if (!(sample == this.toNode(player)) && (!this.isPassable(level, sample) || !this.isPassable(level, sample.above()))) {
            true
         }
      }

      val var19: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
         MathHelper.method_15357((double)step.from.x + 0.5 - unitX * 0.62), step.from.y, MathHelper.method_15357((double)step.from.z + 0.5 - unitZ * 0.62)
      )
      !(var19 == step.from) && (!this.isPassable(level, var19) || !this.isPassable(level, var19.above()))
   }

   private fun airborneControlForStep(step: Step, projection: Double): AirborneControl {
      if (!this.isAngledStep(step)) {
         return SwiftnessMovementLogic.INSTANCE.airborneControl(step.gapBlocks, projection)
      } else {
         val tuning: SwiftnessMovementLogic.JumpTuning = this.jumpTuningForStep(step)
         val distance: Double = this.stepHorizontalDistance(step)
         val release: Double = RangesKt.coerceAtLeast(Math.min(tuning.forwardReleaseProjection, distance - 0.2), tuning.jumpTriggerProjection + 0.35)
         return if (projection >= RangesKt.coerceAtLeast(Math.min(tuning.brakeProjection, distance + 0.16), release + 0.14))
            SwiftnessMovementLogic.AirborneControl(false, true, false)
            else
            (
               if (projection >= release)
                  SwiftnessMovementLogic.AirborneControl(false, false, false)
                  else
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
      val dx: Double = targetX - player.method_23317()
      val dz: Double = targetZ - player.method_23321()
      val yawRad: Double = Math.toRadians((double)player.method_36454())
      val forwardInput: Double = RangesKt.coerceIn(dx * -Math.sin(yawRad) + dz * Math.cos(yawRad), -1.0, 1.0)
      val rightInput: Double = RangesKt.coerceIn(dx * Math.cos(yawRad) + dz * Math.sin(yawRad), -1.0, 1.0)
      val wantForward: Boolean = forwardInput > 0.035
      val wantBack: Boolean = allowBack && forwardInput < -0.035
      if (Math.abs(rightInput) > lateralReadyLimit) {
         PlayerController.INSTANCE.pressForward(false)
         PlayerController.INSTANCE.pressBack(false)
         PlayerController.INSTANCE.pressLeft(false)
         PlayerController.INSTANCE.pressRight(false)
         PlayerController.INSTANCE.pressSprint(false)
         PlayerController.INSTANCE.pressJump(false)
         this.pressSneak(false)
         if (player.field_6012 % 5 == 0) {
            var var33: Array<Any> = arrayOf(targetX)
            val var10001: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var33, var33.length))
            var33 = arrayOf(targetZ)
            val var10002: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var33, var33.length))
            var33 = arrayOf(forwardInput)
            val var10003: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var33, var33.length))
            var33 = arrayOf(rightInput)
            val var10004: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var33, var33.length))
            var33 = arrayOf(lateralReadyLimit)
            val var10005: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var33, var33.length))
            var33 = arrayOf(player.method_36454())
            val var10006: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var33, var33.length))
            this.debug("movement yaw hold target=$var10001,$var10002 forward=$var10003 right=$var10004 limit=$var10005 yaw=$var10006")
         }
      } else {
         PlayerController.INSTANCE.pressForward(wantForward)
         PlayerController.INSTANCE.pressBack(wantBack)
         PlayerController.INSTANCE.pressLeft(false)
         PlayerController.INSTANCE.pressRight(false)
         PlayerController.INSTANCE.pressSprint(sprint && wantForward)
         PlayerController.INSTANCE.pressJump(jump)
         this.pressSneak(sneak)
      }
   }

   private fun pressSneak(down: Boolean) {
      PlayerController.INSTANCE.pressSneak(down)
      automatedSneakTicks = if (down) 2 else 0
   }

   fun safeForwardFloor(level: World, player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step, targetX: Double, targetZ: Double): Boolean {
      if (!this.isStandable(level, step.to)) {
         false
      } else {
         val dx: Double = targetX - player.method_23317()
         val dz: Double = targetZ - player.method_23321()
         val length: Double = Math.hypot(dx, dz)
         if (length < 0.001) {
            true
         } else {
            val dirX: Double = dx / length
            val dirZ: Double = dz / length
            val sideX: Double = -(dz / length)
            val sideZ: Double = dirX
            val feetY: Int = step.from.y
            val maxDistance: Double = Math.min(0.92, Math.max(0.35, length + 0.15))

            // $VF: Unable to resugar Kotlin loop from Java for loop
            var distance: Double = 0.3
            while (true) {
               if (distance <= maxDistance + 0.001) break
               val var27: java.util.Iterator = CollectionsKt.listOf(arrayOf(-0.2, 0.0, 0.2)).iterator()

               while (var27.hasNext()) {
                  val var35: Double = (var27.next() as java.lang.Number).doubleValue()
                  val sampleNode: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
                     MathHelper.method_15357(player.method_23317() + dirX * distance + sideX * var35),
                     feetY,
                     MathHelper.method_15357(player.method_23321() + dirZ * distance + sideZ * var35)
                  )
                  if (!(sampleNode == step.from) && !(sampleNode == step.to) && !this.isStandable(level, sampleNode)) {
                     false
                  }
               }

               distance += 0.31
            }

            true
         }
      }
   }

   fun jumpMetrics(player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step, targetYaw: Float): SwiftnessMovementLogic.JumpMetrics {
      val dx: Double = step.to.x - step.from.x
      val dz: Double = step.to.z - step.from.z
      val length: Double = RangesKt.coerceAtLeast(Math.hypot(dx, dz), 1.0E-4)
      val unitX: Double = dx / length
      val unitZ: Double = dz / length
      val relX: Double = player.method_23317() - (step.from.x + 0.5)
      val relZ: Double = player.method_23321() - (step.from.z + 0.5)
      SwiftnessMovementLogic.JumpMetrics(
         relX * unitX + relZ * unitZ,
         Math.abs(relX * unitZ - relZ * unitX),
         this.yawError(player, targetYaw),
         player.method_24828(),
         player.field_6012 - jumpPhaseStartedTick
      )
   }

   private fun segmentPoint(step: Step, projection: Double): Pair<Double, Double> {
      val dx: Double = step.to.x - step.from.x
      val dz: Double = step.to.z - step.from.z
      val length: Double = RangesKt.coerceAtLeast(Math.hypot(dx, dz), 1.0E-4)
      return Pair((double)step.from.x + 0.5 + projection * (dx / length), (double)step.from.z + 0.5 + projection * (dz / length))
   }

   fun yawForStep(player: ClientPlayerEntity, step: SwiftnessMovementLogic.Step): Float {
      this.yawTo(player, (double)step.to.x + 0.5, (double)step.to.z + 0.5)
   }

   fun yawTo(player: ClientPlayerEntity, targetX: Double, targetZ: Double): Float {
      (float)(Math.atan2(-(targetX - player.method_23317()), targetZ - player.method_23321()) * 180.0 / Math.PI)
   }

   fun aimAtYaw(player: ClientPlayerEntity, targetYaw: Float, maxStep: Float) {
      val nextYaw: Float = player.method_36454() + RangesKt.coerceIn(MathHelper.method_15393(targetYaw - player.method_36454()), -maxStep, maxStep)
      player.method_36456(nextYaw)
      player.field_6241 = nextYaw
      player.field_6283 = nextYaw
   }

   fun yawError(player: ClientPlayerEntity, targetYaw: Float): Double {
      Math.abs((double)MathHelper.method_15393(targetYaw - player.method_36454()))
   }

   fun horizontalDistanceTo(player: ClientPlayerEntity, node: SwiftnessMovementLogic.Node): Double {
      Math.hypot((double)node.x + 0.5 - player.method_23317(), (double)node.z + 0.5 - player.method_23321())
   }

   fun isSafelyStandingOnTarget(player: ClientPlayerEntity, target: SwiftnessMovementLogic.Node): Boolean {
      player.method_24828()
         && !(Math.abs(player.method_23318() - (double)target.y) > 0.45)
         && player.method_24515().method_10263() == target.x
         && player.method_24515().method_10260() == target.z
         && this.horizontalDistanceTo(player, target) <= 0.26
         && this.horizontalSpeed(player) <= 0.16
      }

   fun horizontalSpeed(player: ClientPlayerEntity): Double {
      val var10000: Vec3d = player.method_18798()
      Math.hypot(var10000.field_1352, var10000.field_1350)
   }

   fun ClientPlayerEntity.toNode(): SwiftnessMovementLogic.Node {
      SwiftnessMovementLogic.Node(
         MathHelper.method_15357(`$this$toNode`.method_23317()),
         MathHelper.method_15357(`$this$toNode`.method_23318()),
         MathHelper.method_15357(`$this$toNode`.method_23321())
      )
   }

   fun playerStartNode(level: World, player: ClientPlayerEntity): SwiftnessMovementLogic.Node {
      val base: SwiftnessMovementLogic.Node = this.toNode(player)
      var best: SwiftnessMovementLogic.Node = if (this.isStandable(level, base)) base else null
      var bestScore: Double = if (best != null) INSTANCE.nodeDistanceScore(player, best) else java.lang.Double.POSITIVE_INFINITY

      for (dy in -1..1) {
         for (dx in -1..1) {
            for (dz in -1..1) {
               val candidate: SwiftnessMovementLogic.Node = base.offset(dx, dy, dz)
               if (this.isStandable(level, candidate)) {
                  val var13: Double = this.nodeDistanceScore(player, candidate)
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

      var14
   }

   fun nodeDistanceScore(player: ClientPlayerEntity, node: SwiftnessMovementLogic.Node): Double {
      Math.hypot((double)node.x + 0.5 - player.method_23317(), (double)node.z + 0.5 - player.method_23321())
         + Math.abs((double)node.y - player.method_23318()) * 0.35
      }

   fun BlockPos.toNode(): SwiftnessMovementLogic.Node {
      SwiftnessMovementLogic.Node(`$this$toNode`.method_10263(), `$this$toNode`.method_10264(), `$this$toNode`.method_10260())
   }

   fun isStandable(level: World, node: SwiftnessMovementLogic.Node): Boolean {
      this.isPassable(level, node) && this.isPassable(level, node.above()) && this.hasSolidFloor(level, node.x, node.y - 1, node.z)
   }

   fun isPassable(level: World, node: SwiftnessMovementLogic.Node): Boolean {
      val pos: BlockPos = BlockPos(node.x, node.y, node.z)
      val var10000: BlockState = level.method_8320(pos)
      val var6: VoxelShape = var10000.method_26194(level as BlockView, pos, ShapeContext.method_16194())
      var6.method_1110()
   }

   fun hasSolidFloor(level: World, x: Int, y: Int, z: Int): Boolean {
      val pos: BlockPos = BlockPos(x, y, z)
      val var10000: BlockState = level.method_8320(pos)
      val var8: VoxelShape = var10000.method_26194(level as BlockView, pos, ShapeContext.method_16194())
      !var8.method_1110()
   }

   fun isFloorBlock(level: World, node: SwiftnessMovementLogic.Node, block: Block): Boolean {
      level.method_8320(BlockPos(node.x, node.y - 1, node.z)).method_26204() == block
   }

   fun isExpiringFloor(level: World, node: SwiftnessMovementLogic.Node): Boolean {
      val var10000: Block = level.method_8320(BlockPos(node.x, node.y - 1, node.z)).method_26204()
      var10000 == Blocks.field_10095 || var10000 == Blocks.field_10314
   }

   private fun isAngledStep(step: Step): Boolean {
      return step.to.x != step.from.x && step.to.z != step.from.z
   }

   private fun stepHorizontalDistance(step: Step): Double {
      return Math.hypot((double)(step.to.x - step.from.x), (double)(step.to.z - step.from.z))
   }

   private fun jumpTuningForStep(step: Step): JumpTuning {
      return SwiftnessMovementLogic.INSTANCE.jumpTuningForDistance(step.gapBlocks, this.stepHorizontalDistance(step), this.isAngledStep(step))
   }

   fun canFastHandoffAfterJump(level: World, step: SwiftnessMovementLogic.Step, player: ClientPlayerEntity, landingDistance: Double, speed: Double): Boolean {
      val nextStep: SwiftnessMovementLogic.Step = this.plannedFirstStepFromLanding(level, step.to)
      val var10000: SwiftnessMovementLogic.JumpMetrics = if (nextStep != null)
         INSTANCE.jumpMetrics(player, nextStep, INSTANCE.yawForStep(player, nextStep))
         else
         null
         val allowed: Boolean = SwiftnessMovementLogic.INSTANCE
         .shouldFastHandoffAfterJump(
            latestTargetSeq, routeTargetSeq, player.method_24828(), step.gapBlocks, landingDistance, speed, 0.76, 0.48, 0.16, 0.28, nextStep, var10000
         )
         if (!allowed
         && (if (nextStep != null) nextStep.moveType else null) === SwiftnessMovementLogic.MoveType.JUMP
         && nextStep.gapBlocks >= 3
         && player.field_6012 % 3 == 0) {
         val var10001: Int = nextStep.gapBlocks
         var var19: Array<Any> = arrayOf(landingDistance)
         val var10002: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var19, var19.length))
         var19 = arrayOf(speed)
         val var10003: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var19, var19.length))
         var19 = arrayOf(if (var10000 != null) var10000.projection else 0.0)
         val var10004: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var19, var19.length))
         var19 = arrayOf(if (var10000 != null) var10000.laneError else 0.0)
         val var10005: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var19, var19.length))
         var19 = arrayOf(if (var10000 != null) var10000.yawError else 0.0)
         val var10006: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var19, var19.length))
         this.debug("jump capture gated handoff nextGap=$var10001 distance=$var10002 speed=$var10003 nextProj=$var10004 nextLane=$var10005 nextYaw=$var10006")
      }

      allowed
   }

   fun plannedFirstStepFromLanding(level: World, landing: SwiftnessMovementLogic.Node): SwiftnessMovementLogic.Step {
      if (latestTargetBlock == null) {
         null
      } else {
         val planned: SwiftnessMovementLogic.Route = SwiftnessMovementLogic.INSTANCE
            .planTemporaryPlatformRoute(
               landing, SwiftnessMovementLogic.INSTANCE.targetFeetNode(this.toNode(latestTargetBlock)), { it: SwiftnessMovementLogic.Node ->
                  INSTANCE.isStandable(`$level`, it)
               }, { it: SwiftnessMovementLogic.Node ->
                  INSTANCE.isPassable(`$level`, it)
               }
            )
            if (planned != null) {
            val var10000: java.util.List = planned.steps
            if (var10000 != null) {
               CollectionsKt.firstOrNull(var10000) as SwiftnessMovementLogic.Step
            }
         }

         null
      }
   }

   fun repairStartupUnderfootTarget(level: World, player: ClientPlayerEntity): Boolean {
      if (startupIgnoredFloorBlock == null) {
         false
      } else {
         val ignored: BlockPos = startupIgnoredFloorBlock
         if (player.field_6012 > startupIgnoreUntilTick) {
            startupIgnoredFloorBlock = null
            false
         } else if (latestTargetBlock == startupIgnoredFloorBlock) {
            latestTargetBlock = null
            val var4: Int = latestTargetSeq++
            this.clearRoute()
            this.debug("cleared startup underfoot target block=${ignored.method_10263()},${ignored.method_10264()},${ignored.method_10260()}")
            this.promoteStartupReplacementTarget(level, player, "startup underfoot")
            latestTargetBlock != null
         } else {
            latestTargetBlock == null && this.promoteStartupReplacementTarget(level, player, "startup underfoot")
         }
      }
   }

   fun promoteStartupReplacementTarget(level: World, player: ClientPlayerEntity, reason: java.lang.String): Boolean {
      val var10000: BlockPos = this.selectStartupReplacementTarget(level, player)
      if (var10000 == null) {
         false
      } else {
         latestTargetBlock = var10000
         val var5: Int = latestTargetSeq++
         this.clearRoute()
         this.debug("$reason replacement target seq=${latestTargetSeq} block=${var10000.method_10263()},${var10000.method_10264()},${var10000.method_10260()}")
         true
      }
   }

   fun liveLimeFeetNodes(level: World): MutableSet<SwiftnessMovementLogic.Node> {
      SequencesKt.toSet(
         SequencesKt.map(
            SequencesKt.filter(
               MapsKt.asSequence(woolTrace),
               { var1: Entry ->
                  (var1.getValue() as AutomaticSwiftness.WoolTrace).color == "lime"
                     && `$level`.method_8320(var1.getKey() as BlockPos).method_26204() == Blocks.field_10028
                  }
            ),
            { var0: Entry ->
               SwiftnessMovementLogic.INSTANCE.targetFeetNode(INSTANCE.toNode(var0.getKey() as BlockPos))
            }
         )
      )
   }

   fun selectStartupReplacementTarget(level: World, player: ClientPlayerEntity): BlockPos {
      if (startupIgnoredFloorBlock == null) {
         null
      } else {
         val var10000: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.INSTANCE
            .startupReplacementTarget(this.toNode(startupIgnoredFloorBlock), this.liveStartupLimeTargetBlocks(level, player, startupIgnoredFloorBlock))
            if (var10000 == null) {
            null
         } else {
            val var7: BlockPos = BlockPos(var10000.x, var10000.y, var10000.z).method_10062()
            if (this.isPlausibleActiveTarget(var7, player)) var7 else null
         }
      }
   }

   fun liveStartupLimeTargetBlocks(level: World, player: ClientPlayerEntity, ignored: BlockPos): MutableSet<SwiftnessMovementLogic.Node> {
      val traced: java.util.Set = this.liveLimeTargetBlocks(level)
      val scanned: HashSet = HashSet()
      val var10000: BlockPos = player.method_24515()
      val center: BlockPos = var10000
      val minY: Int = ignored.method_10264() - 4
      val maxY: Int = ignored.method_10264() + 4
      var x: Int = var10000.method_10263() - 8
      val var10: Int = var10000.method_10263() + 8
      if (x <= var10) {
         while (true) {
            var z: Int = center.method_10260() - 8
            val var12: Int = center.method_10260() + 8
            if (z <= var12) {
               while (true) {
                  var y: Int = minY
                  if (minY <= maxY) {
                     while (true) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        if (level.method_8320(pos).method_26204() == Blocks.field_10028) {
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
      SequencesKt.toSet(
         SequencesKt.map(
            SequencesKt.filter(
               MapsKt.asSequence(woolTrace),
               { var1: Entry ->
                  (var1.getValue() as AutomaticSwiftness.WoolTrace).color == "lime"
                     && `$level`.method_8320(var1.getKey() as BlockPos).method_26204() == Blocks.field_10028
                  }
            ),
            { var0: Entry ->
               INSTANCE.toNode(var0.getKey() as BlockPos)
            }
         )
      )
   }

   fun startupIgnoredFloorBlock(level: World, player: ClientPlayerEntity): BlockPos {
      val startNode: SwiftnessMovementLogic.Node = this.playerStartNode(level, player)
      val floor: BlockPos = BlockPos(startNode.x, startNode.y - 1, startNode.z)
      val var10000: Block = level.method_8320(floor).method_26204()
      if (!(var10000 == Blocks.field_10552) && !(var10000 == Blocks.field_10028)) null else floor.method_10062()
   }

   fun shouldIgnoreStartupUnderfootTarget(target: BlockPos, player: ClientPlayerEntity): Boolean {
      if (startupIgnoredFloorBlock == null) {
         false
      } else {
         label28@
         if (player.field_6012 > startupIgnoreUntilTick) {
            startupIgnoredFloorBlock = null
            false
         } else {
            target == startupIgnoredFloorBlock
               && this.horizontalDistanceTo(player, SwiftnessMovementLogic.INSTANCE.targetFeetNode(this.toNode(target))) <= 0.85
            }
      }
   }

   fun isPlausibleActiveTarget(pos: BlockPos, player: ClientPlayerEntity): Boolean {
      !(Math.abs(player.method_23318() - ((double)pos.method_10264() + 1.0)) > 4.0) && this.targetDistanceFromPlayer(pos, player) <= 8.0
   }

   fun targetDistanceFromPlayer(pos: BlockPos, player: ClientPlayerEntity): Double {
      Math.hypot((double)pos.method_10263() + 0.5 - player.method_23317(), (double)pos.method_10260() + 0.5 - player.method_23321())
   }

   fun describeIgnoredTarget(prefix: java.lang.String, pos: BlockPos, player: ClientPlayerEntity): java.lang.String {
      val distance: Double = this.targetDistanceFromPlayer(pos, player)
      val dy: Double = Math.abs(player.method_23318() - ((double)pos.method_10264() + 1.0))
      val var10001: Int = pos.method_10263()
      val var10002: Int = pos.method_10264()
      val var10003: Int = pos.method_10260()
      var var14: Array<Any> = arrayOf(player.method_23317())
      val var10004: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var14, var14.length))
      var14 = arrayOf(player.method_23318())
      val var10005: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var14, var14.length))
      var14 = arrayOf(player.method_23321())
      val var10006: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var14, var14.length))
      var14 = arrayOf(distance)
      val var10007: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var14, var14.length))
      var14 = arrayOf(dy)
      val var10008: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var14, var14.length))
      "$prefix block=$var10001,$var10002,$var10003 player=$var10004,$var10005,$var10006 distance=$var10007 dy=$var10008"
   }

   private fun shouldSneakDuringLanding(step: Step, projection: Double): Boolean {
      return this.isAngledStep(step) && projection >= this.stepHorizontalDistance(step) - 0.75
   }

   private fun overrunProjection(step: Step, tuning: JumpTuning): Double {
      return SwiftnessMovementLogic.INSTANCE.airborneOverrunProjection(tuning, this.stepHorizontalDistance(step), this.isAngledStep(step))
   }

   private fun landingProjectionLimit(step: Step, tuning: JumpTuning): Double {
      return SwiftnessMovementLogic.INSTANCE.landingProjectionLimit(tuning, this.stepHorizontalDistance(step), step.gapBlocks, this.isAngledStep(step))
   }

   private fun landingProjectionFloor(step: Step): Double {
      return if (step.gapBlocks < 2) java.lang.Double.NEGATIVE_INFINITY else this.stepHorizontalDistance(step) - 0.55
   }

   private fun Int.signInt(): Int {
      return (int)Math.signum((double)`$this$signInt`)
   }

   private fun debug(message: String) {
      logger.info("[JR-DOJO-SWIFTNESS] $message")
   }

   fun traceWoolUpdate(pos: BlockPos, state: BlockState) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      val tick: Int = if (var10000 != null) var10000.field_6012 else 0
      val var10001: Block = state.method_26204()
      val color: java.lang.String = this.swiftnessWoolColor(var10001)
      val var11: BlockPos = pos.method_10062()
      if (color == null) {
         val var10: AutomaticSwiftness.WoolTrace = woolTrace.remove(var11)
         if (active && var10 != null) {
            this.debug("wool ${pos.method_10263()},${pos.method_10264()},${pos.method_10260()} ${var10.color}->clear age=${tick - var10.tick}")
         }
      } else {
         val previous: AutomaticSwiftness.WoolTrace = woolTrace.put(var11, AutomaticSwiftness.WoolTrace(color, tick))
         if (active && !((if (previous != null) previous.color else null) == color)) {
            var var10002: Int
            var var10003: Int
            var var10004: java.lang.String
            run label54@{
               var12 = if (previous != null) tick - previous.tick else 0
               var13 = pos.method_10263()
               var10002 = pos.method_10264()
               var10003 = pos.method_10260()
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

   fun swiftnessWoolColor(block: Block): java.lang.String {
      if (block == Blocks.field_10028) "lime" else (if (block == Blocks.field_10095) "orange" else (if (block == Blocks.field_10314) "red" else null))
   }

   private fun describeRoute(route: Route): String {
      return CollectionsKt.joinToString$default(route.steps, "|", null, null, 0, null, { step: SwiftnessMovementLogic.Step ->
         ("${step.moveType}:${step.from.x},${step.from.y},${step.from.z}->${step.to.x},${step.to.y},${step.to.z}:g${step.gapBlocks}") as java.lang.CharSequence
      }, 30, null)
   }

   private data class WoolTrace(color: String, tick: Int) {
      public final val color: String
      public final val tick: Int

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

      public fun copy(color: String = this.color, tick: Int = this.tick): jooon.features.dojo.AutomaticSwiftness.WoolTrace {
         return AutomaticSwiftness.WoolTrace(color, tick)
      }

      public override fun toString(): String {
         return "WoolTrace(color=${this.color}, tick=${this.tick})"
      }

      public override fun hashCode(): Int {
         return this.color.hashCode() * 31 + Integer.hashCode(this.tick)
      }

      public override operator fun equals(other: Any?): Boolean {
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
