package jooon.pathfinding

import jooon.JooonReimagined
import jooon.pathfinding.WalkingPathfinder.Node
import jooon.pathfinding.WalkingPathfinder.Options
import jooon.pathfinding.WalkingPathfinder.PathStep
import jooon.util.PlayerController
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.class_243
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

@SourceDebugExtension(["SMAP\nWalkToController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WalkToController.kt\njooon/pathfinding/WalkToController\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,684:1\n1#2:685\n*E\n"])
public object WalkToController {
   private const val COMPLETION_RADIUS: Double = 2.5
   private const val MIN_LOOKAHEAD: Double = 1.1
   private const val MAX_LOOKAHEAD: Double = 3.5
   private const val RECOVERY_LOOKAHEAD: Double = 0.35
   private const val PROXIMITY_THRESHOLD: Double = 4.0
   private const val PREDICTION_TICKS: Int = 10
   private const val PREDICTION_MIN_SPEED_XZ: Double = 0.05
   private const val PREDICTION_MAX_ADVANCE_GROUND: Double = 0.9
   private const val PREDICTION_MAX_ADVANCE_AIR: Double = 2.4
   private const val MAX_LOOK_DISTANCE: Double = 0.8
   private const val BASE_KP: Double = 0.05
   private const val KD: Double = 0.55
   private const val MAX_VELOCITY: Double = 8.0
   private const val ACCEL_LIMIT: Double = 1.2
   private const val SETTLE_THRESHOLD: Double = 0.15
   private const val YAW_DEADZONE: Double = 1.2
   private const val PITCH_DEADZONE: Double = 1.8
   private const val SMOOTH_FACTOR: Double = 0.1
   private const val STUCK_TICKS_JUMP: Int = 10
   private const val STUCK_TICKS_CLOSE_LOOK: Int = 22
   private const val STUCK_TICKS_BACKUP_RECALC: Int = 44
   private const val NON_CHANGE_TICKS_RECALC: Int = 35
   private const val REPLAN_COOLDOWN_MS: Long = 650L
   private const val FALL_REPLAN_COOLDOWN_MS: Long = 260L
   private const val BACKUP_TICKS: Int = 5
   private const val JUMP_TICKS: Int = 4
   private final var active: Boolean
   private final var target: Node?
   private final var pathSteps: List<PathStep> = CollectionsKt.emptyList()
   private final var splinePath: List<class_243> = CollectionsKt.emptyList()
   private final var lookPath: List<class_243> = CollectionsKt.emptyList()
   private final var currentPathPosition: Double
   private final var smoothedLookahead: Double = 3.5
   private final var currentPathCurvature: Double
   private final var closeLookTicks: Int
   private final var currentYaw: Double
   private final var currentPitch: Double
   private final var rawTargetYaw: Double
   private final var rawTargetPitch: Double
   private final var yawVelocity: Double
   private final var pitchVelocity: Double
   private final var initialTurnBoostTicks: Int
   private final var stuckTicks: Int
   private final var stuckLevel: Int
   @JvmStatic
   private Vec3d lastPos;
   @JvmStatic
   private Vec3d stuckPos;
   private final var bestPathPosition: Double?
   private final var nonChangeTicks: Int
   private final var jumpTicks: Int
   private final var backupTicks: Int
   private final var lastReplanMs: Long
   private final var fallingOffPathTicks: Int
   private final var lastSearchStats: String = ""
   private final var pathVariantSeed: Int
   private final var announceProgress: Boolean = true
   private final var pathOptions: Options = WalkingPathfinder.Options(false, false, 0.0, false, 15, null)

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { client: MinecraftClient ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         HudRenderCallback.EVENT
         .register(
            { var0: DrawContext, var1: RenderTickCounter ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
      }

   public fun startWalkTo(x: Double, y: Double, z: Double) {
      startWalkTo$default(this, x, y, z, true, null, 16, null)
   }

   public fun startWalkTo(
      x: Double,
      y: Double,
      z: Double,
      announce: Boolean = true,
      options: Options = WalkingPathfinder.Options(false, false, 0.0, false, 15, null)
   ) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val goal: WalkingPathfinder.Node = WalkingPathfinder.Node(MathHelper.method_15357(x), MathHelper.method_15357(y) + 1, MathHelper.method_15357(z))
         target = goal
         pathVariantSeed = 0
         announceProgress = announce
         pathOptions = options
         val result: WalkingPathfinder.Result = this.computePath(var10000, goal)
         if (!result.success) {
            active = false
            this.stopMovement()
            if (announceProgress) {
               val var12: JooonReimagined.Companion = JooonReimagined.Companion
               var var10001: java.lang.String = result.error
               if (var10001 == null) {
                  var10001 = "unknown error"
               }

               var12.sendMessage("§cPathfinder failed: §7$var10001")
            }
         } else {
            this.installPath(result, var10000)
            active = true
            if (announceProgress) {
               JooonReimagined.Companion.sendMessage("§aWalking to §f${goal.x}, ${goal.y - 1}, ${goal.z} §7(${lastSearchStats})")
            }
         }
      }
   }

   public fun stop(reason: String? = null) {
      if (active || reason != null) {
         active = false
         target = null
         pathSteps = CollectionsKt.emptyList()
         splinePath = CollectionsKt.emptyList()
         lookPath = CollectionsKt.emptyList()
         this.resetRuntime()
         this.stopMovement()
         if (announceProgress) {
            if (reason != null) {
               JooonReimagined.Companion.sendMessage(reason)
            }
         }

         announceProgress = true
         pathOptions = WalkingPathfinder.Options(false, false, 0.0, false, 15, null)
      }
   }

   public fun isActive(): Boolean {
      return active
   }

   fun tick(client: MinecraftClient) {
      if (client.field_1724 == null) {
         stop$default(this, null, 1, null)
      } else {
         val player: ClientPlayerEntity = client.field_1724
         if (client.field_1687 == null) {
            stop$default(this, null, 1, null)
         } else if (client.field_1755 != null && client.field_1755 !is ChatScreen) {
            this.stopMovement()
         } else if (target == null) {
            stop$default(this, null, 1, null)
         } else {
            val goal: WalkingPathfinder.Node = target
            if (this.reached(client.field_1724, target)) {
               this.stop("§aReached §f${goal.x}, ${goal.y - 1}, ${goal.z}§a.")
            } else if (lookPath.size() < 2) {
               this.replan(player, goal, "empty_path")
            } else {
               this.updatePathPosition(player)
               if (this.reached(player, goal)) {
                  this.stop("§aReached §f${goal.x}, ${goal.y - 1}, ${goal.z}§a.")
               } else if (!this.handleFallDivergence(player, goal)) {
                  this.updateTargetRotation(player)
                  this.updateJump(player)
                  this.beginMovement()
                  this.trackRecovery(player, goal)
               }
            }
         }
      }
   }

   fun computePath(player: ClientPlayerEntity, goal: WalkingPathfinder.Node): WalkingPathfinder.Result {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         WalkingPathfinder.Result(CollectionsKt.emptyList(), 0, 0L, "World is not loaded.")
      } else {
         val var5: WalkingPathfinder = WalkingPathfinder.INSTANCE
         val var10001: BlockPos = player.method_24515()
         WalkingPathfinder.INSTANCE.findPath(var10000 as World, var5.fromBlockPosFeet(var10001), goal, pathVariantSeed, pathOptions)
      }
   }

   fun installPath(result: WalkingPathfinder.Result, player: ClientPlayerEntity) {
      pathSteps = result.steps
      splinePath = PathSpline.INSTANCE.generateSpline(result.steps, 1.0)
      lookPath = PathSpline.createLookPoints$default(PathSpline.INSTANCE, splinePath, 0.0, 0.0, 6, null)
      currentPathPosition = 0.0
      smoothedLookahead = 3.5
      closeLookTicks = 0
      currentPathCurvature = 0.0
      this.resetRecovery()
      currentYaw = this.wrap((double)player.method_36454())
      currentPitch = player.method_36455()
      rawTargetYaw = currentYaw
      rawTargetPitch = currentPitch
      yawVelocity = 0.0
      pitchVelocity = 0.0
      initialTurnBoostTicks = 10
      lastSearchStats = "${result.steps.size()} nodes, ${result.nodesExplored} explored, ${result.timeMs}ms"
      fallingOffPathTicks = 0
   }

   fun replan(player: ClientPlayerEntity, goal: WalkingPathfinder.Node, reason: java.lang.String) {
      val now: Long = System.currentTimeMillis()
      if (now - lastReplanMs >= (if (!(reason == "fall_divergence") && !(reason == "off_edge")) 650L else 260L)) {
         lastReplanMs = now
         val var9: Int = pathVariantSeed++
         val var10: WalkingPathfinder.Result = this.computePath(player, goal)
         if (!var10.success) {
            var var10001: java.lang.String = var10.error
            if (var10001 == null) {
               var10001 = reason
            }

            this.stop("§cPathfinder stopped: §7$var10001")
         } else {
            this.installPath(var10, player)
         }
      }
   }

   fun handleFallDivergence(player: ClientPlayerEntity, goal: WalkingPathfinder.Node): Boolean {
      if (player.method_24828() || !(player.method_18798().field_1351 < -0.18)) {
         fallingOffPathTicks = 0
         false
      } else {
         val anchor: Vec3d = this.interpolated(currentPathPosition)
         if (this.isPathDropping()
            || !(anchor.field_1351 - player.method_23318() > 1.15)
               && !(Math.hypot(player.method_23317() - anchor.field_1352, player.method_23321() - anchor.field_1350) > 2.35)) {
            fallingOffPathTicks = 0
         } else {
            val var10: Int = fallingOffPathTicks++
         }

         if (fallingOffPathTicks >= 3) {
            this.replan(player, goal, "fall_divergence")
            true
         } else {
            false
         }
      }
   }

   fun updatePathPosition(player: ClientPlayerEntity) {
      val eyes: Vec3d = Vec3d(player.method_23317(), player.method_23320(), player.method_23321())
      val falling: Boolean = player.method_18798().field_1351 < -0.4 || this.isPathDropping()
      val jumpingHigh: Boolean = player.method_18798().field_1351 > 0.1 || player.method_23318() - this.interpolated(currentPathPosition).field_1351 > 2.0
      val startIdx: Int = if (falling) (int)currentPathPosition else Math.max(0, (int)currentPathPosition - 2)
      val endIdx: Int = Math.min(CollectionsKt.getLastIndex(lookPath) - 1, startIdx + (if (falling) 4 else (if (jumpingHigh) 12 else 8)))
      var bestT: Double = currentPathPosition
      var bestDistSq: Double = java.lang.Double.MAX_VALUE
      var threshold: Int = startIdx
      if (startIdx <= endIdx) {
         while (true) {
            val a: Vec3d = lookPath.get(threshold)
            val maxAdvance: Vec3d = lookPath.get(threshold + 1)
            val candidateT: Double = threshold
               + (if (!falling && !jumpingHigh) this.closest3d(eyes, a, maxAdvance) else this.closestHorizontal(eyes, a, maxAdvance))
               if (!falling || !(candidateT < currentPathPosition)) {
               val projected: Vec3d = this.interpolated(candidateT)
               val distSq: Double = if (falling)
                  this.sq(eyes.field_1352 - projected.field_1352) + this.sq(eyes.field_1350 - projected.field_1350)
                  else
                  eyes.method_1025(projected)
                  if (distSq < bestDistSq) {
                  bestDistSq = distSq
                  bestT = candidateT
               }
            }

            if (threshold == endIdx) {
               break
            }

            threshold++
         }
      }

      if (bestDistSq < (if (falling) 5.0 else (if (jumpingHigh) 8.0 else 4.0)) * (if (falling) 5.0 else (if (jumpingHigh) 8.0 else 4.0))) {
         currentPathPosition = Math.min(currentPathPosition + (if (falling) 0.5 else 2.0), bestT)
      }

      this.applyPredictedPathProgress(player)
   }

   fun updateTargetRotation(player: ClientPlayerEntity) {
      if (lookPath.size() >= 2) {
         val eyes: Vec3d = Vec3d(player.method_23317(), player.method_23320(), player.method_23321())
         val var10000: Double
         if (closeLookTicks > 0) {
            closeLookTicks += -1
            var10000 = 0.35
         } else {
            var10000 = this.adaptiveLookahead(eyes)
         }

         var var36: Vec3d = this.interpolated(Math.min((double)CollectionsKt.getLastIndex(lookPath), currentPathPosition + var10000))
         val dx: Double = var36.field_1352 - eyes.field_1352
         val dy: Double = var36.field_1351 - eyes.field_1351
         val dz: Double = var36.field_1350 - eyes.field_1350
         val dist: Double = Math.hypot(Math.hypot(dx, var36.field_1350 - eyes.field_1350), dy)
         if (dist > 0.8) {
            var36 = Vec3d(eyes.field_1352 + dx * (0.8 / dist), eyes.field_1351 + dy * (0.8 / dist), eyes.field_1350 + dz * (0.8 / dist))
         }

         val var37: Double = this.wrap(Math.toDegrees(Math.atan2(var36.field_1350 - player.method_23321(), var36.field_1352 - player.method_23317())) - 90.0)
         val pitch: Double = RangesKt.coerceIn(
            -Math.toDegrees(
               Math.atan2(
                  var36.field_1351 - player.method_23320(),
                  RangesKt.coerceAtLeast(Math.hypot(var36.field_1352 - player.method_23317(), var36.field_1350 - player.method_23321()), 0.001)
               )
            ),
            -50.0,
            50.0
         )
         val yawDelta: Double = this.angleDiff(rawTargetYaw, var37)
         val remainingPath: Double = CollectionsKt.getLastIndex(lookPath) - currentPathPosition
         val finishFactor: Double = if (remainingPath < 3.0) Math.max(0.1, remainingPath / 3.0) else 1.0
         val straight: Boolean = currentPathCurvature < 0.15
         val dynamicSmooth: Double = Math.min(1.0, (if (currentPathCurvature < 0.15) 0.05 else 0.1) / finishFactor * this.initialTurnBoostFactor(yawDelta))
         if (Math.abs(yawDelta) > (if (straight) 1.7999999999999998 else 1.2) * finishFactor) {
            rawTargetYaw = this.wrap(rawTargetYaw + yawDelta * dynamicSmooth)
         }

         val pitchDelta: Double = pitch - rawTargetPitch
         if (Math.abs(pitch - rawTargetPitch) > 1.8 * finishFactor) {
            rawTargetPitch += pitchDelta * dynamicSmooth
         }

         if (initialTurnBoostTicks > 0) {
            if (Math.abs(this.angleDiff(currentYaw, rawTargetYaw)) <= Math.max(10.0, 2.4)) {
               initialTurnBoostTicks = 0
            } else {
               initialTurnBoostTicks += -1
            }
         }
      }
   }

   private fun applyRotationFrame() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         currentYaw = this.wrap(currentYaw)
         val yawError: Double = this.angleDiff(currentYaw, rawTargetYaw)
         val pitchError: Double = rawTargetPitch - currentPitch
         val absYawError: Double = Math.abs(yawError)
         val straight: Boolean = currentPathCurvature < 0.2
         val boost: Double = this.initialTurnBoostFactor(yawError)
         val dynamicKp: Double = 0.05 * Math.min(1.5, Math.max(0.6, absYawError / 10.0)) * boost
         val dynamicKd: Double = if (straight) 0.7150000000000001 else 0.55
         val accelLimit: Double = 1.2 * boost
         val maxVelocity: Double = 8.0 * boost
         if (absYawError < 0.15 && Math.abs(yawVelocity) < 0.02) {
            currentYaw = rawTargetYaw
            yawVelocity = 0.0
         } else {
            yawVelocity += RangesKt.coerceIn(yawError * dynamicKp - yawVelocity * dynamicKd, -accelLimit, accelLimit)
            yawVelocity *= 0.92
            yawVelocity = RangesKt.coerceIn(yawVelocity, -maxVelocity, maxVelocity)
            currentYaw = currentYaw + yawVelocity
         }

         if (Math.abs(pitchError) < 0.15 && Math.abs(pitchVelocity) < 0.02) {
            currentPitch = rawTargetPitch
            pitchVelocity = 0.0
         } else {
            pitchVelocity += RangesKt.coerceIn(pitchError * dynamicKp - pitchVelocity * dynamicKd, -accelLimit, accelLimit)
            pitchVelocity *= 0.92
            pitchVelocity = RangesKt.coerceIn(pitchVelocity, -maxVelocity, maxVelocity)
            currentPitch = currentPitch + pitchVelocity
         }

         var10000.method_36456((float)currentYaw)
         var10000.method_36457((float)RangesKt.coerceIn(currentPitch, -90.0, 90.0))
      }
   }

   fun adaptiveLookahead(eyes: Vec3d): Double {
      if (lookPath.size() < 4) {
         smoothedLookahead
      } else {
         val targetIndex: Int = RangesKt.coerceIn((int)currentPathPosition, 0, CollectionsKt.getLastIndex(lookPath) - 1)
         val anchor: Vec3d = this.interpolated(currentPathPosition)
         val deviationFactor: Double = RangesKt.coerceIn(
            (Math.hypot(eyes.field_1352 - anchor.field_1352, eyes.field_1350 - anchor.field_1350) - 1.6) / 2.0, 0.0, 1.0
         )
         var maxAngle: Double = 0.0
         val currentA: Vec3d = lookPath.get(targetIndex)
         val currentB: Vec3d = lookPath.get(Math.min(targetIndex + 2, CollectionsKt.getLastIndex(lookPath)))
         val cdx: Double = currentB.field_1352 - currentA.field_1352
         val cdz: Double = currentB.field_1350 - currentA.field_1350
         val cmag: Double = Math.hypot(cdx, currentB.field_1350 - currentA.field_1350)
         val curveFactor: java.util.Iterator = CollectionsKt.listOf(arrayOf(4, 6, 8)).iterator()

         while (curveFactor.hasNext()) {
            val adjust: Int = Math.min(targetIndex + (curveFactor.next() as java.lang.Number).intValue(), CollectionsKt.getLastIndex(lookPath) - 2)
            if (adjust > targetIndex + 2) {
               val fa: Vec3d = lookPath.get(adjust)
               val targetLookahead: Vec3d = lookPath.get(Math.min(adjust + 2, CollectionsKt.getLastIndex(lookPath)))
               val fdx: Double = targetLookahead.field_1352 - fa.field_1352
               val fdz: Double = targetLookahead.field_1350 - fa.field_1350
               val fmag: Double = Math.hypot(fdx, targetLookahead.field_1350 - fa.field_1350)
               if (cmag > 0.8 && fmag > 0.8) {
                  maxAngle = Math.max(maxAngle, Math.acos(RangesKt.coerceIn((cdx * fdx + cdz * fdz) / (cmag * fmag), -1.0, 1.0)))
               }
            }
         }

         currentPathCurvature = maxAngle
         val var34: Double = 3.5 - 2.4 * Math.max(deviationFactor, RangesKt.coerceIn((maxAngle - 0.61) / 0.7, 0.0, 1.0))
         smoothedLookahead = smoothedLookahead + (var34 - smoothedLookahead) * (if (var34 > smoothedLookahead) 0.1 else 0.05)
         smoothedLookahead
      }
   }

   private fun beginMovement() {
      if (backupTicks > 0) {
         PlayerController.INSTANCE.pressForward(false)
         PlayerController.INSTANCE.pressBack(true)
         PlayerController.INSTANCE.pressSprint(false)
         backupTicks += -1
      } else {
         PlayerController.INSTANCE.pressSprint(true)
         PlayerController.INSTANCE.pressForward(true)
         PlayerController.INSTANCE.pressBack(false)
         PlayerController.INSTANCE.pressLeft(false)
         PlayerController.INSTANCE.pressRight(false)
         PlayerController.INSTANCE.pressJump(jumpTicks > 0)
         PlayerController.INSTANCE.pressSneak(false)
      }
   }

   fun trackRecovery(player: ClientPlayerEntity, goal: WalkingPathfinder.Node) {
      val var10000: Vec3d = player.method_73189()
      val last: Vec3d = lastPos
      if ((if (lastPos == null) 1.0 else this.sq(var10000.field_1352 - lastPos.field_1352) + this.sq(var10000.field_1350 - last.field_1350)) > 0.0144) {
         stuckTicks = 0
         stuckLevel = 0
         stuckPos = null
         lastPos = var10000
      } else if (player.method_24828()) {
         if (stuckTicks == 0) {
            stuckPos = var10000
         }

         val best: Int = stuckTicks++
         lastPos = var10000
      }

      if (stuckTicks >= 44 && stuckLevel < 3) {
         stuckLevel = 3
         backupTicks = 5
         this.replan(player, goal, "backup_recalc")
      } else {
         if (stuckTicks >= 22 && stuckLevel < 2) {
            stuckLevel = 2
            closeLookTicks = 20
         }

         if (stuckTicks >= 10 && stuckLevel < 1) {
            stuckLevel = 1
            jumpTicks = 4
         }

         if (bestPathPosition != null && !(currentPathPosition > bestPathPosition + 0.45)) {
            val var8: Int = nonChangeTicks++
            if (nonChangeTicks >= 35) {
               nonChangeTicks = 0
               this.replan(player, goal, "path_progress_stalled")
            }
         } else {
            bestPathPosition = currentPathPosition
            nonChangeTicks = 0
         }
      }
   }

   fun applyPredictedPathProgress(player: ClientPlayerEntity) {
      if (!player.method_24828() || !(Math.hypot(player.method_18798().field_1352, player.method_18798().field_1350) < 0.05)) {
         val projected: Vec3d = if (player.method_24828()) player.method_73189() else this.predictXZ(player, 10)
         val var9: Double = this.projectPathPositionHorizontal(projected.field_1352, projected.field_1350, currentPathPosition)
         if (!(var9 <= currentPathPosition)) {
            currentPathPosition = Math.min(currentPathPosition + (if (player.method_24828()) 0.9 else 2.4), var9)
         }
      }
   }

   fun predictXZ(player: ClientPlayerEntity, ticks: Int): Vec3d {
      var px: Double = 0.0
      px = player.method_23317()
      var py: Double = 0.0
      py = player.method_23318()
      var pz: Double = 0.0
      pz = player.method_23321()
      var vx: Double = 0.0
      vx = player.method_18798().field_1352
      var vy: Double = 0.0
      vy = player.method_18798().field_1351
      var vz: Double = 0.0
      vz = player.method_18798().field_1350
      val var3: Int = RangesKt.coerceAtLeast(ticks, 1)

      repeat(var3) { var4 ->
         px += vx
         py += vy
         pz += vz
         vy = vy + -0.08
         vx *= 0.91
         vz *= 0.91
         vy = vy * 0.98
      }

      Vec3d(px, py, pz)
   }

   private fun projectPathPositionHorizontal(x: Double, z: Double, hint: Double): Double {
      if (lookPath.size() < 2) {
         return 0.0
      } else {
         val lastSegment: Int = lookPath.size() - 2
         val base: Int = RangesKt.coerceIn((int)hint, 0, lastSegment)
         val start: Int = Math.max(0, base - 8)
         val end: Int = Math.min(lastSegment, base + 28)
         var bestT: Double = RangesKt.coerceIn(hint, 0.0, (double)CollectionsKt.getLastIndex(lookPath))
         var bestDistSq: Double = java.lang.Double.MAX_VALUE
         var i: Int = start
         if (start <= end) {
            while (true) {
               val a: Vec3d = lookPath.get(i)
               val b: Vec3d = lookPath.get(i + 1)
               val t: Double = this.closestHorizontal(Vec3d(x, a.field_1351, z), a, b)
               val px: Double = a.field_1352 + (b.field_1352 - a.field_1352) * t
               val pz: Double = a.field_1350 + (b.field_1350 - a.field_1350) * t
               val distSq: Double = this.sq(x - px) + this.sq(z - pz)
               if (distSq < bestDistSq) {
                  bestDistSq = distSq
                  bestT = i + t
               }

               if (i == end) {
                  break
               }

               i++
            }
         }

         return bestT
      }
   }

   private fun nearestStepIndex(): Int {
      if (pathSteps.isEmpty()) {
         return 0
      } else {
         val player: ClientPlayerEntity = this.getMc().field_1724
         val current: Vec3d = if (player != null)
            Vec3d(player.method_23317(), player.method_23318(), player.method_23321())
            else
            this.interpolated(currentPathPosition)
            var best: Int = 0
         var bestDist: Double = java.lang.Double.MAX_VALUE
         var i: Int = 0

         for (var7 in pathSteps.size()..i) {
            val dist: Double = (pathSteps.get(i) as WalkingPathfinder.PathStep).node.center().method_1025(current)
            if (dist < bestDist) {
               bestDist = dist
               best = i
            }
         }

         return best
      }
   }

   fun reached(player: ClientPlayerEntity, goal: WalkingPathfinder.Node): Boolean {
      val dx: Double = player.method_23317() - (goal.x + 0.5)
      val dy: Double = player.method_23318() - goal.y
      val dz: Double = player.method_23321() - (goal.z + 0.5)
      dx * dx + dz * dz <= 6.25 && dy >= -1.35 && dy <= 5.5 && player.method_24828()
   }

   private fun resetRuntime() {
      currentPathPosition = 0.0
      smoothedLookahead = 3.5
      closeLookTicks = 0
      yawVelocity = 0.0
      pitchVelocity = 0.0
      this.resetRecovery()
   }

   private fun resetRecovery() {
      stuckTicks = 0
      stuckLevel = 0
      lastPos = null
      stuckPos = null
      bestPathPosition = null
      nonChangeTicks = 0
      jumpTicks = 0
      backupTicks = 0
      fallingOffPathTicks = 0
   }

   private fun stopMovement() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
      PlayerController.INSTANCE.pressSprint(false)
   }

   private fun isPathDropping(): Boolean {
      return lookPath.size() >= 3
         && !(currentPathPosition >= CollectionsKt.getLastIndex(lookPath) - 1)
         && this.interpolated(currentPathPosition).field_1351
               - this.interpolated(Math.min((double)CollectionsKt.getLastIndex(lookPath), currentPathPosition + 2.0)).field_1351
            > 0.8
         }

   fun interpolated(indexFloat: Double): Vec3d {
      if (lookPath.isEmpty()) {
         val var10000: Vec3d = Vec3d.field_1353
         var10000
      } else {
         val safe: Double = RangesKt.coerceIn(indexFloat, 0.0, (double)CollectionsKt.getLastIndex(lookPath))
         val idx: Int = (int)safe
         val frac: Double = safe - (int)safe
         val p1: Vec3d = lookPath.get((int)safe)
         val p2: Vec3d = lookPath.get(Math.min(idx + 1, CollectionsKt.getLastIndex(lookPath)))
         if (frac <= 0.0)
            p1
            else
            Vec3d(
               p1.field_1352 + (p2.field_1352 - p1.field_1352) * frac,
               p1.field_1351 + (p2.field_1351 - p1.field_1351) * frac,
               p1.field_1350 + (p2.field_1350 - p1.field_1350) * frac
            )
         }
   }

   fun closest3d(p: Vec3d, a: Vec3d, b: Vec3d): Double {
      if ((b.field_1352 - a.field_1352) * (b.field_1352 - a.field_1352)
               + (b.field_1351 - a.field_1351) * (b.field_1351 - a.field_1351)
               + (b.field_1350 - a.field_1350) * (b.field_1350 - a.field_1350)
            <= 1.0E-8)
         0.0
         else
         RangesKt.coerceIn(
            (
                  (p.field_1352 - a.field_1352) * (b.field_1352 - a.field_1352)
                     + (p.field_1351 - a.field_1351) * (b.field_1351 - a.field_1351)
                     + (p.field_1350 - a.field_1350) * (b.field_1350 - a.field_1350)
               )
               / (
                  (b.field_1352 - a.field_1352) * (b.field_1352 - a.field_1352)
                     + (b.field_1351 - a.field_1351) * (b.field_1351 - a.field_1351)
                     + (b.field_1350 - a.field_1350) * (b.field_1350 - a.field_1350)
               ),
            0.0,
            1.0
         )
      }

   fun closestHorizontal(p: Vec3d, a: Vec3d, b: Vec3d): Double {
      if ((b.field_1352 - a.field_1352) * (b.field_1352 - a.field_1352) + (b.field_1350 - a.field_1350) * (b.field_1350 - a.field_1350) <= 1.0E-8)
         0.0
         else
         RangesKt.coerceIn(
            ((p.field_1352 - a.field_1352) * (b.field_1352 - a.field_1352) + (p.field_1350 - a.field_1350) * (b.field_1350 - a.field_1350))
               / ((b.field_1352 - a.field_1352) * (b.field_1352 - a.field_1352) + (b.field_1350 - a.field_1350) * (b.field_1350 - a.field_1350)),
            0.0,
            1.0
         )
      }

   private fun initialTurnBoostFactor(yawError: Double): Double {
      return if (initialTurnBoostTicks > 0 && Math.abs(yawError) >= Math.max(35.0, 4.8)) 2.0 else 1.0
   }

   private fun angleDiff(current: Double, target: Double): Double {
      return MathHelper.method_15338(target - current)
   }

   private fun wrap(value: Double): Double {
      return MathHelper.method_15338(value)
   }

   private fun sq(value: Double): Double {
      return value * value
   }
}
