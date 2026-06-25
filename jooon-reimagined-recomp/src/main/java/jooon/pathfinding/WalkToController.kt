package jooon.pathfinding

import jooon.JooonReimagined
import jooon.pathfinding.WalkingPathfinder.Node
import jooon.pathfinding.WalkingPathfinder.Options
import jooon.pathfinding.WalkingPathfinder.PathStep
import jooon.util.PlayerController
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.util.math.Vec3d
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

object WalkToController {
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
   private var active: Boolean
   private var target: Node?
   private var pathSteps: List<PathStep> = emptyList()
   private var splinePath: List<Vec3d> = emptyList()
   private var lookPath: List<Vec3d> = emptyList()
   private var currentPathPosition: Double
   private var smoothedLookahead: Double = 3.5
   private var currentPathCurvature: Double
   private var closeLookTicks: Int
   private var currentYaw: Double
   private var currentPitch: Double
   private var rawTargetYaw: Double
   private var rawTargetPitch: Double
   private var yawVelocity: Double
   private var pitchVelocity: Double
   private var initialTurnBoostTicks: Int
   private var stuckTicks: Int
   private var stuckLevel: Int
   
   private Vec3d lastPos;
   
   private Vec3d stuckPos;
   private var bestPathPosition: Double?
   private var nonChangeTicks: Int
   private var jumpTicks: Int
   private var backupTicks: Int
   private var lastReplanMs: Long
   private var fallingOffPathTicks: Int
   private var lastSearchStats: String = ""
   private var pathVariantSeed: Int
   private var announceProgress: Boolean = true
   private var pathOptions: Options = WalkingPathfinder.Options(false, false, 0.0, false, 15, null)

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
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

   fun startWalkTo(x: Double, y: Double, z: Double) {
      startWalkTo$default(this, x, y, z, true, null, 16, null)
   }

   fun startWalkTo(
      x: Double,
      y: Double,
      z: Double,
      announce: Boolean = true,
      options: Options = WalkingPathfinder.Options(false, false, 0.0, false, 15, null)
   ) {

      if (var10000 != null) {
         val goal: WalkingPathfinder.Node = WalkingPathfinder.Node(MathHelper.floor(x), MathHelper.floor(y) + 1, MathHelper.floor(z))
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
               var var10001: String = result.error
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

   fun stop(reason: String? = null) {
      if (active || reason != null) {
         active = false
         target = null
         pathSteps = emptyList()
         splinePath = emptyList()
         lookPath = emptyList()
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

   fun isActive(): Boolean {
      return active
   }

   fun tick(client: MinecraftClient) {
      if (client.player == null) {
         stop$default(this, null, 1, null)
      } else {

         if (client.world == null) {
            stop$default(this, null, 1, null)
         } else if (client.currentScreen != null && client.currentScreen !is ChatScreen) {
            this.stopMovement()
         } else if (target == null) {
            stop$default(this, null, 1, null)
         } else {
            val goal: WalkingPathfinder.Node = target
            if (this.reached(client.player, target)) {
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

      if (var10000 == null) {
         WalkingPathfinder.Result(emptyList(), 0, 0L, "World is not loaded.")
      } else {


         WalkingPathfinder.findPath(var10000 as World, var5.fromBlockPosFeet(var10001), goal, pathVariantSeed, pathOptions)
      }
   }

   fun installPath(result: WalkingPathfinder.Result, player: ClientPlayerEntity) {
      pathSteps = result.steps
      splinePath = PathSpline.generateSpline(result.steps, 1.0)
      lookPath = PathSpline.createLookPoints$default(PathSpline.INSTANCE, splinePath, 0.0, 0.0, 6, null)
      currentPathPosition = 0.0
      smoothedLookahead = 3.5
      closeLookTicks = 0
      currentPathCurvature = 0.0
      this.resetRecovery()
      currentYaw = this.wrap(player.getYaw().toDouble())
      currentPitch = player.getPitch()
      rawTargetYaw = currentYaw
      rawTargetPitch = currentPitch
      yawVelocity = 0.0
      pitchVelocity = 0.0
      initialTurnBoostTicks = 10
      lastSearchStats = "${result.steps.size()} nodes, ${result.nodesExplored} explored, ${result.timeMs}ms"
      fallingOffPathTicks = 0
   }

   fun replan(player: ClientPlayerEntity, goal: WalkingPathfinder.Node, reason: String) {

      if (now - lastReplanMs >= (if (!(reason == "fall_divergence") && !(reason == "off_edge")) 650L else 260L)) {
         lastReplanMs = now

         val var10: WalkingPathfinder.Result = this.computePath(player, goal)
         if (!var10.success) {
            var var10001: String = var10.error
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
      if (player.isOnGround() || !(player.getVelocity().y < -0.18)) {
         fallingOffPathTicks = 0
return false
      } else {

         if (this.isPathDropping()
            || !(anchor.y - player.getY() > 1.15)
               && !(Math.hypot(player.getX() - anchor.x, player.getZ() - anchor.z) > 2.35)) {
            fallingOffPathTicks = 0
         } else {

         }

         if (fallingOffPathTicks >= 3) {
            this.replan(player, goal, "fall_divergence")
return true
         } else {
return false
         }
      }
   }

   fun updatePathPosition(player: ClientPlayerEntity) {





      var bestT: Double = currentPathPosition
      var bestDistSq: Double = java.lang.Double.MAX_VALUE
      var threshold: Int = startIdx
      if (startIdx <= endIdx) {
         while (true) {



               + (if (!falling && !jumpingHigh) this.closest3d(eyes, a, maxAdvance) else this.closestHorizontal(eyes, a, maxAdvance))
               if (!falling || !(candidateT < currentPathPosition)) {


                  this.sq(eyes.x - projected.x) + this.sq(eyes.z - projected.z)
return else
                  eyes.squaredDistanceTo(projected)
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

         val var10000: Double
         if (closeLookTicks > 0) {
            closeLookTicks += -1
            var10000 = 0.35
         } else {
            var10000 = this.adaptiveLookahead(eyes)
         }

         var var36: Vec3d = this.interpolated(Math.min(getLastIndex(lookPath).toDouble(), currentPathPosition + var10000))




         if (dist > 0.8) {
            var36 = Vec3d(eyes.x + dx * (0.8 / dist), eyes.y + dy * (0.8 / dist), eyes.z + dz * (0.8 / dist))
         }


               Math.atan2(
                  var36.y - player.getEyeY(),
                  (Math.hypot(var36.x - player.getX(), var36.z - player.getZ())).coerceAtLeast(0.001)
               )
            )).coerceIn(-50.0, 50.0)





         if (Math.abs(yawDelta) > (if (straight) 1.7999999999999998 else 1.2) * finishFactor) {
            rawTargetYaw = this.wrap(rawTargetYaw + yawDelta * dynamicSmooth)
         }

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

      if (var10000 != null) {
         currentYaw = this.wrap(currentYaw)









         if (absYawError < 0.15 && Math.abs(yawVelocity) < 0.02) {
            currentYaw = rawTargetYaw
            yawVelocity = 0.0
         } else {
            yawVelocity += (yawError * dynamicKp - yawVelocity * dynamicKd).coerceIn(-accelLimit, accelLimit)
            yawVelocity *= 0.92
            yawVelocity = (yawVelocity).coerceIn(-maxVelocity, maxVelocity)
            currentYaw = currentYaw + yawVelocity
         }

         if (Math.abs(pitchError) < 0.15 && Math.abs(pitchVelocity) < 0.02) {
            currentPitch = rawTargetPitch
            pitchVelocity = 0.0
         } else {
            pitchVelocity += (pitchError * dynamicKp - pitchVelocity * dynamicKd).coerceIn(-accelLimit, accelLimit)
            pitchVelocity *= 0.92
            pitchVelocity = (pitchVelocity).coerceIn(-maxVelocity, maxVelocity)
            currentPitch = currentPitch + pitchVelocity
         }

         var10000.setYaw(currentYaw.toFloat())
         var10000.setPitch((currentPitch).coerceIn(-90.0, 90.0).toFloat())
      }
   }

   fun adaptiveLookahead(eyes: Vec3d): Double {
      if (lookPath.size() < 4) {
return smoothedLookahead
      } else {



         var maxAngle: Double = 0.0





         val curveFactor: java.util.Iterator = listOf(arrayOf(4, 6, 8)).iterator()

         while (curveFactor.hasNext()) {

            if (adjust > targetIndex + 2) {





               if (cmag > 0.8 && fmag > 0.8) {
                  maxAngle = Math.max(maxAngle, Math.acos(((cdx * fdx + cdz * fdz) / (cmag * fmag)).coerceIn(-1.0, 1.0)))
               }
            }
         }

         currentPathCurvature = maxAngle

         smoothedLookahead = smoothedLookahead + (var34 - smoothedLookahead) * (if (var34 > smoothedLookahead) 0.1 else 0.05)
return smoothedLookahead
      }
   }

   private fun beginMovement() {
      if (backupTicks > 0) {
         PlayerController.pressForward(false)
         PlayerController.pressBack(true)
         PlayerController.pressSprint(false)
         backupTicks += -1
      } else {
         PlayerController.pressSprint(true)
         PlayerController.pressForward(true)
         PlayerController.pressBack(false)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressJump(jumpTicks > 0)
         PlayerController.pressSneak(false)
      }
   }

   fun trackRecovery(player: ClientPlayerEntity, goal: WalkingPathfinder.Node) {


      if ((if (lastPos == null) 1.0 else this.sq(var10000.x - lastPos.x) + this.sq(var10000.z - last.z)) > 0.0144) {
         stuckTicks = 0
         stuckLevel = 0
         stuckPos = null
         lastPos = var10000
      } else if (player.isOnGround()) {
         if (stuckTicks == 0) {
            stuckPos = var10000
         }

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
      if (!player.isOnGround() || !(Math.hypot(player.getVelocity().x, player.getVelocity().z) < 0.05)) {


         if (!(var9 <= currentPathPosition)) {
            currentPathPosition = Math.min(currentPathPosition + (if (player.isOnGround()) 0.9 else 2.4), var9)
         }
      }
   }

   fun predictXZ(player: ClientPlayerEntity, ticks: Int): Vec3d {
      var px: Double = 0.0
      px = player.getX()
      var py: Double = 0.0
      py = player.getY()
      var pz: Double = 0.0
      pz = player.getZ()
      var vx: Double = 0.0
      vx = player.getVelocity().x
      var vy: Double = 0.0
      vy = player.getVelocity().y
      var vz: Double = 0.0
      vz = player.getVelocity().z


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




         var bestT: Double = (hint).coerceIn(0.0, getLastIndex(lookPath).toDouble())
         var bestDistSq: Double = java.lang.Double.MAX_VALUE
         var i: Int = start
         if (start <= end) {
            while (true) {






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


            Vec3d(player.getX(), player.getY(), player.getZ())
return else
            this.interpolated(currentPathPosition)
            var best: Int = 0
         var bestDist: Double = java.lang.Double.MAX_VALUE
         var i: Int = 0

         for (var7 in pathSteps.size()..i) {

            if (dist < bestDist) {
               bestDist = dist
               best = i
            }
         }

         return best
      }
   }

   fun reached(player: ClientPlayerEntity, goal: WalkingPathfinder.Node): Boolean {



      dx * dx + dz * dz <= 6.25 && dy >= -1.35 && dy <= 5.5 && player.isOnGround()
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
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressJump(false)
      PlayerController.pressSneak(false)
      PlayerController.pressSprint(false)
   }

   private fun isPathDropping(): Boolean {
      return lookPath.size() >= 3
         && !(currentPathPosition >= getLastIndex(lookPath) - 1)
         && this.interpolated(currentPathPosition).y
               - this.interpolated(Math.min(getLastIndex(lookPath).toDouble(), currentPathPosition + 2.0)).y
            > 0.8
         }

   fun interpolated(indexFloat: Double): Vec3d {
      if (lookPath.isEmpty()) {
return var10000
      } else {





         if (frac <= 0.0)
return p1
return else
            Vec3d(
               p1.x + (p2.x - p1.x) * frac,
               p1.y + (p2.y - p1.y) * frac,
               p1.z + (p2.z - p1.z) * frac
            )
         }
   }

   fun closest3d(p: Vec3d, a: Vec3d, b: Vec3d): Double {
      if ((b.x - a.x) * (b.x - a.x)
               + (b.y - a.y) * (b.y - a.y)
               + (b.z - a.z) * (b.z - a.z)
            <= 1.0E-8)
         0.0
return else
         ((
                  (p.x - a.x) * (b.x - a.x)
                     + (p.y - a.y) * (b.y - a.y)
                     + (p.z - a.z) * (b.z - a.z)
               )
               / (
                  (b.x - a.x) * (b.x - a.x)
                     + (b.y - a.y) * (b.y - a.y)
                     + (b.z - a.z) * (b.z - a.z)
               )).coerceIn(0.0, 1.0)
      }

   fun closestHorizontal(p: Vec3d, a: Vec3d, b: Vec3d): Double {
      if ((b.x - a.x) * (b.x - a.x) + (b.z - a.z) * (b.z - a.z) <= 1.0E-8)
         0.0
return else
         (((p.x - a.x) * (b.x - a.x) + (p.z - a.z) * (b.z - a.z))
               / ((b.x - a.x) * (b.x - a.x) + (b.z - a.z) * (b.z - a.z))).coerceIn(0.0, 1.0)
      }

   private fun initialTurnBoostFactor(yawError: Double): Double {
      return if (initialTurnBoostTicks > 0 && Math.abs(yawError) >= Math.max(35.0, 4.8)) 2.0 else 1.0
   }

   private fun angleDiff(current: Double, target: Double): Double {
      return MathHelper.wrapDegrees(target - current)
   }

   private fun wrap(value: Double): Double {
      return MathHelper.wrapDegrees(value)
   }

   private fun sq(value: Double): Double {
      return value * value
   }
}
