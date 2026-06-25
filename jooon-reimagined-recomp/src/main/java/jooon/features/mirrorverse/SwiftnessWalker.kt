package jooon.features.mirrorverse

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.HashMap
import java.util.PriorityQueue
import jooon.JooonReimagined
import jooon.util.PlayerController
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

object SwiftnessWalker {
   private const val SEARCH_EXPANSION: Int = 24
   private const val MAX_GAP: Int = 3
   private const val LOOK_SPEED: Float = 45.0F
   private const val REPLAN_INTERVAL_MS: Long = 320L
   private const val STUCK_MS: Long = 900L
   private const val ARRIVE_EPS: Double = 0.24
   private const val LONG_ARRIVE_EPS: Double = 0.62
   private const val JUMP_STATE_IDLE: Int = 0
   private const val JUMP_WINDUP_MS: Long = 250L
   private const val JUMP_HOLD_MS: Long = 150L
   private const val JUMP_COOLDOWN_MS: Long = 180L
   private const val JUMP_MAX_AIR_MS: Long = 1000L
   private const val LONG_PHASE_ALIGN: Int = 1
   private const val LONG_PHASE_BACKSTEP: Int = 2
   private const val LONG_PHASE_RUNUP: Int = 3
   private const val LONG_PHASE_AIRBORNE: Int = 4
   private var enabled: Boolean
   private var latestWoolSeq: Long
   private var latestPlannedWoolSeq: Long = -1L
   
   private BlockPos targetWool;
   private var targetNode: jooon.features.mirrorverse.SwiftnessWalker.Node?
   private var path: List<jooon.features.mirrorverse.SwiftnessWalker.Node> = emptyList()
   private var pathCursor: Int
   private var lastPlanMs: Long
   private var lastProgressMs: Long
   private var lastStepDist: Double = java.lang.Double.MAX_VALUE
   private var noTargetWarned: Boolean
   private var targetSeenMs: Long
   private var jumpStateUntilMs: Long
   private var jumpState: Int
   private var jumpCooldownUntilMs: Long
   private var jumpSegmentFrom: jooon.features.mirrorverse.SwiftnessWalker.Node?
   private var jumpSegmentTo: jooon.features.mirrorverse.SwiftnessWalker.Node?
   private var jumpPressUntilMs: Long

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      this.registerCommand()
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
      }

   private fun registerCommand() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(ClientCommandManager.literal("swiftness").executes(lambda_2_lambda_1@{ it: CommandContext ->

            if (var10000 == null) {
               return@lambda_2_lambda_1 1
            } else {
               enabled = !enabled
               if (!enabled) {
                  clearMovement()
                  path = emptyList()
                  pathCursor = 0
                  JooonReimagined.Companion.sendMessage("§cSwiftness walker disabled.")
                  return@lambda_2_lambda_1 1
               } else {
                  var10000.sendMessage(Text.literal("Use §a/swiftness§7 again to stop.") as Text, true)
                  noTargetWarned = false
                  path = emptyList()
                  pathCursor = 0
                  latestPlannedWoolSeq = -1L
                  lastPlanMs = 0L
                  lastProgressMs = System.currentTimeMillis()
                  lastStepDist = java.lang.Double.MAX_VALUE
                  resetJumpState()
                  targetSeenMs = if (targetWool != null) System.currentTimeMillis() else 0L
                  if (targetWool != null) {
                     JooonReimagined.Companion.sendMessage("§aSwiftness walker enabled. Heading to latest lime wool.")
                  } else {
                     JooonReimagined.Companion.sendMessage("§aSwiftness walker enabled. Spawn lime wool to assign target.")
                  }

                  return@lambda_2_lambda_1 1
               }
            }
         }) as LiteralArgumentBuilder)
      })
   }

   fun onBlockUpdate(pos: BlockPos, state: BlockState) {
      if (state.getBlock() === Blocks.LIME_WOOL) {

         targetWool = pos
         targetNode = SwiftnessWalker.Node(pos.getX(), pos.getY() + 1, pos.getZ())
         targetSeenMs = System.currentTimeMillis()
         if (enabled) {
            path = emptyList()
            pathCursor = 0
         }
      } else {
         if (targetWool == pos && state.getBlock() != Blocks.LIME_WOOL) {
            targetWool = null
            targetNode = null
            path = emptyList()
            pathCursor = 0
            targetSeenMs = 0L
            latestPlannedWoolSeq = -1L
         }
      }
   }

   fun onSectionBlocksUpdate(packet: ChunkDeltaUpdateS2CPacket) {
      if (this.getMc().world != null) {
         packet.visitUpdates({ pos: BlockPos, state: BlockState ->
            onBlockUpdate(pos, state)
         })
      }
   }

   fun onTick(client: MinecraftClient) {
      if (client.player != null) {

         if (targetWool == null) {
            val `this24lambda_u244`: SwiftnessWalker = this
            if (!noTargetWarned) {
               noTargetWarned = true
               JooonReimagined.Companion.sendMessage("§eNo lime wool target yet. Spawn lime wool and it will lock automatically.")
            }

            `this24lambda_u244`.clearMovement()
         } else {

            if (targetNode == null) {
               this.clearMovement()
            } else {
               val navTarget: SwiftnessWalker.Node = targetNode
               if (!this.isStandable(targetNode)) {
                  this.clearMovement()
                  if (System.currentTimeMillis() - targetSeenMs >= 500L) {
                     JooonReimagined.Companion.sendMessage("§eLatest lime wool target not walkable yet. Waiting for valid landing block.")
                     targetSeenMs = System.currentTimeMillis()
                  }
               } else {
                  if (path.isEmpty() || latestPlannedWoolSeq != latestWoolSeq || System.currentTimeMillis() - lastPlanMs >= 320L) {
                     this.planPath(player, navTarget)
                     lastPlanMs = System.currentTimeMillis()
                  }

                  if (!path.isEmpty()) {
                     if (path.size() > 1 && pathCursor < path.size() - 1) {
                        this.moveAlongPath(player)
                     } else {
                        this.clearMovement()
                        JooonReimagined.Companion
                           .sendMessage(
                              "§aSwiftness reached lime wool at §f${targetPos.getX()}, ${targetPos.getY()}, ${targetPos.getZ()}"
                           )
                           path = emptyList()
                        pathCursor = 0
                     }
                  }
               }
            }
         }
      }
   }

   fun runLongJumpSegment(player: ClientPlayerEntity, from: SwiftnessWalker.Node, to: SwiftnessWalker.Node, stepDistance: Int, now: Long) {
      if (!this.isSameLongJumpSegment(from, to)) {
         jumpSegmentFrom = from
         jumpSegmentTo = to
         jumpState = 1
         jumpStateUntilMs = 0L
         jumpPressUntilMs = 0L
      }

      this.applySmoothLook(player, this.cardinalYawForStep(from, to), 0.0F, 120.0F)
val metrics: SwiftnessWalker.SegmentMetrics = this.segmentMetrics(from, to, player)
val isThree: Boolean = stepDistance >= 3
val alignEps: Double = if (stepDistance >= 3) 0.12 else 0.1
val backProj: Double = if (stepDistance >= 3) -0.34 else -0.28
val runTargetProj: Double = if (isThree) 0.5 else 0.48
val jumpTriggerProj: Double = if (isThree) 0.43 else 0.39
val runupMs: Long = if (isThree) 370L else 250L
val holdMs: Long = if (isThree) 170L else 150L
      when (jumpState) {
         1 -> {

            this.applyMovementToward(
               player, (alignPoint.getFirst() as java.lang.Number).doubleValue(), (alignPoint.getSecond() as java.lang.Number).doubleValue(), true, false
            )
            PlayerController.pressJump(false)
            if (Math.abs(metrics.laneError) <= alignEps) {
               jumpState = 2
            }
         }
         2 -> {

            this.applyMovementToward(
               player, (var27.getFirst() as java.lang.Number).doubleValue(), (var27.getSecond() as java.lang.Number).doubleValue(), true, false
            )
            PlayerController.pressJump(false)
            if (now >= jumpCooldownUntilMs && Math.abs(metrics.laneError) <= alignEps && Math.abs(metrics.projection - backProj) <= 0.06) {
               jumpState = 3
               jumpStateUntilMs = now + runupMs
            }
         }
         3 -> {

            this.applyMovementToward(
               player, (runPoint.getFirst() as java.lang.Number).doubleValue(), (runPoint.getSecond() as java.lang.Number).doubleValue(), true, true
            )
            PlayerController.pressJump(false)
            if (player.isOnGround() && (metrics.projection >= jumpTriggerProj || now >= jumpStateUntilMs)) {
               jumpState = 4
               jumpPressUntilMs = now + holdMs
               jumpStateUntilMs = now + 1000L
               jumpCooldownUntilMs = now + 180L
            }
         }
         4 -> {
            PlayerController.pressForward(true)
            PlayerController.pressBack(false)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressSprint(true)
            PlayerController.pressJump(now < jumpPressUntilMs)
            if (player.isOnGround() && now > jumpPressUntilMs + 80L || now >= jumpStateUntilMs) {
               jumpState = 1
            }
         }
         else -> jumpState = 1
      }
   }

   fun segmentMetrics(from: SwiftnessWalker.Node, to: SwiftnessWalker.Node, player: ClientPlayerEntity): SwiftnessWalker.SegmentMetrics {



      if (dirX != 0)
         SwiftnessWalker.SegmentMetrics((player.getX() - centerX) * dirX, player.getZ() - centerZ)
return else
         SwiftnessWalker.SegmentMetrics((player.getZ() - centerZ) * (to.z - from.z).coerceIn(-1, 1), player.getX() - centerX)
      }

   private fun segmentPoint(
      from: jooon.features.mirrorverse.SwiftnessWalker.Node,
      to: jooon.features.mirrorverse.SwiftnessWalker.Node,
      projection: Double,
      laneOffset: Double
   ): Pair<Double, Double> {



      return if (dirX != 0)
         Pair(centerX + projection * dirX.toDouble(), centerZ + laneOffset)
return else
         Pair(centerX + laneOffset, centerZ + projection * (to.z - from.z).coerceIn(-1, 1).toDouble())
      }

   fun hasSteppedPastLongJump(from: SwiftnessWalker.Node, to: SwiftnessWalker.Node, player: ClientPlayerEntity): Boolean {
      if (!player.isOnGround()) {
return false
      } else {




         if (dirX != 0) {
            if (Math.abs(player.getZ() - landingZ) > 1.1) {
return false
            } else {
               if (dirX > 0) player.getX() >= landingX - 0.15 else player.getX() <= landingX + 0.15
            }
         } else if (dirZ != 0) {
            if (Math.abs(player.getX() - landingX) > 1.1) {
return false
            } else {
               if (dirZ > 0) player.getZ() >= landingZ - 0.15 else player.getZ() <= landingZ + 0.15
            }
         } else {
return false
         }
      }
   }

   private fun isSameLongJumpSegment(from: jooon.features.mirrorverse.SwiftnessWalker.Node, to: jooon.features.mirrorverse.SwiftnessWalker.Node): Boolean {
      return jumpSegmentFrom == from && jumpSegmentTo == to
   }

   fun applyMovementToward(player: ClientPlayerEntity, targetX: Double, targetZ: Double, allowBackAndStrafe: Boolean, sprint: Boolean) {









      PlayerController.pressForward(wantForward)
      PlayerController.pressBack(wantBack)
      PlayerController.pressLeft(wantLeft)
      PlayerController.pressRight(wantRight)
      PlayerController.pressSprint(sprint)
   }

   private fun cardinalYawForStep(from: jooon.features.mirrorverse.SwiftnessWalker.Node, to: jooon.features.mirrorverse.SwiftnessWalker.Node): Float {

      return if (dx > 0) -90.0F else (if (dx < 0) 90.0F else (if (to.z - from.z > 0) 0.0F else 180.0F))
   }

   fun applySmoothLook(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, maxStep: Float) {
      player.setYaw(player.getYaw() + (MathHelper.wrapDegrees(targetYaw - player.getYaw())).coerceIn(-maxStep, maxStep))
      player.setPitch(player.getPitch() + (MathHelper.wrapDegrees(targetPitch - player.getPitch())).coerceIn(-maxStep, maxStep))
      player.headYaw = player.getYaw()
      player.bodyYaw = player.getYaw()
      player.lastHeadYaw = player.getYaw()
      player.lastBodyYaw = player.getYaw()
   }

   private fun getNeighbors(node: jooon.features.mirrorverse.SwiftnessWalker.Node, minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int): List<
         jooon.features.mirrorverse.SwiftnessWalker.Node
      > {


      for (axis in intArrayOf(-1, 1)) {
         val sx: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x + axis, node.y, node.z)
         val sz: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x, node.y, node.z + axis)
         if (this.isStandable(sx)) {
            getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, sx.x, sx.y, sx.z)
         }

         if (this.isStandable(sz)) {
            getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, sz.x, sz.y, sz.z)
         }

         val upX: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x + axis, node.y + 1, node.z)
         val downX: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x + axis, node.y - 1, node.z)
         val upZ: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x, node.y + 1, node.z + axis)
         val downZ: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x, node.y - 1, node.z + axis)
         if (this.isStandable(upX)) {
            getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, upX.x, upX.y, upX.z)
         }

         if (this.isStandable(downX)) {
            getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, downX.x, downX.y, downX.z)
         }

         if (this.isStandable(upZ)) {
            getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, upZ.x, upZ.y, upZ.z)
         }

         if (this.isStandable(downZ)) {
            getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, downZ.x, downZ.y, downZ.z)
         }

         for (gap in 2..3) {
            val jumpX: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x + axis * gap, node.y, node.z)
            if (this.canJumpNode(node, axis, 0, gap) && this.isStandable(jumpX)) {
               getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, jumpX.x, jumpX.y, jumpX.z)
            }

            val jumpZ: SwiftnessWalker.Node = SwiftnessWalker.Node(node.x, node.y, node.z + axis * gap)
            if (this.canJumpNode(node, 0, axis, gap) && this.isStandable(jumpZ)) {
               getNeighbors$addIfInside(minX, maxX, minZ, maxZ, minY, maxY, out, jumpZ.x, jumpZ.y, jumpZ.z)
            }
         }
      }

      return out
   }

   private fun canJumpNode(from: jooon.features.mirrorverse.SwiftnessWalker.Node, stepX: Int, stepZ: Int, gapLen: Int): Boolean {
      if (gapLen >= 2 && gapLen <= 3) {
         if (!this.isStandable(SwiftnessWalker.Node(from.x + stepX * gapLen, from.y, from.z + stepZ * gapLen))) {
            return false
         } else {
            for (offset in 1..gapLen) {
               val mid: SwiftnessWalker.Node = SwiftnessWalker.Node(from.x + stepX * offset, from.y, from.z + stepZ * offset)
               if (!this.isPassable(mid)) {
                  return false
               }

               if (!this.isPassable(SwiftnessWalker.Node(mid.x, mid.y + 1, mid.z))) {
                  return false
               }
            }

            return true
         }
      } else {
         return false
      }
   }

   private fun movementCost(a: jooon.features.mirrorverse.SwiftnessWalker.Node, b: jooon.features.mirrorverse.SwiftnessWalker.Node): Int {

      return if (distance == 1) 1 else (if (2 <= distance && distance < 4) distance + 1 else Integer.MAX_VALUE)
   }

   private fun manhattanStep(a: jooon.features.mirrorverse.SwiftnessWalker.Node, b: jooon.features.mirrorverse.SwiftnessWalker.Node): Int {
      return Math.abs(a.x - b.x) + Math.abs(a.z - b.z)
   }

   private fun isStandable(node: jooon.features.mirrorverse.SwiftnessWalker.Node): Boolean {

      if (var10000 == null) {
         return false
      } else {

         var var10001: BlockPos = feet.down()
         if (this.isSolid(var10001) && this.isPassable(feet)) {
            var10001 = feet.up()
            if (this.isPassable(var10001) && var10000.getBlockState(feet).getFluidState().isEmpty()) {
               return true
            }
         }

         return false
      }
   }

   private fun isPassable(node: jooon.features.mirrorverse.SwiftnessWalker.Node): Boolean {
      return this.isPassable(this.toBlockPos(node))
   }

   fun isPassable(pos: BlockPos): Boolean {
      this.isPassableBlock(pos)
   }

   fun isPassableBlock(pos: BlockPos): Boolean {

      if (var10000 == null) {
return false
      } else {


         var5.isAir() || var6.isEmpty()
      }
   }

   fun isSolid(pos: BlockPos): Boolean {

      if (var10000 == null) {
return false
      } else {

         if (!var5.getFluidState().isEmpty()) {
return false
         } else {

            !var5.isAir() && !var6.isEmpty()
         }
      }
   }

   private fun heuristic(a: jooon.features.mirrorverse.SwiftnessWalker.Node, b: jooon.features.mirrorverse.SwiftnessWalker.Node): Int {
      return Math.abs(a.x - b.x) + Math.abs(a.z - b.z) + Math.abs(a.y - b.y) * 2
   }

   fun getLevelOrNull(): World {
      this.getMc().world as World
   }

   private fun clearMovement() {
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressSprint(false)
      PlayerController.pressJump(false)
      this.resetJumpState()
   }

   private fun resetJumpState() {
      jumpState = 0
      jumpStateUntilMs = 0L
      jumpCooldownUntilMs = 0L
      jumpPressUntilMs = 0L
      jumpSegmentFrom = null
      jumpSegmentTo = null
   }

   fun BlockPos.toNode(): SwiftnessWalker.Node {
      SwiftnessWalker.Node(this.getX(), this.getY(), this.getZ())
   }

   fun SwiftnessWalker.Node.toBlockPos(): BlockPos {
      BlockPos(this.x, this.y, this.z)
   }

   
   fun `getNeighbors$addIfInside`(
      `$minX`: Int, `$maxX`: Int, `$minZ`: Int, `$maxZ`: Int, `$minY`: Int, `$maxY`: Int, out: ArrayList<SwiftnessWalker.Node>, nx: Int, ny: Int, nz: Int
   ) {
      if (nx >= `$minX` && nx <= `$maxX`) {
         if (nz >= `$minZ` && nz <= `$maxZ`) {
            if (ny >= `$minY` && ny <= `$maxY`) {
               val candidate: SwiftnessWalker.Node = SwiftnessWalker.Node(nx, ny, nz)
               if (isStandable(candidate)) {
                  out.add(candidate)
               }
            }
         }
      }
   }

   private data class Node(x: Int, y: Int, z: Int) {
      val x: Int
      val y: Int
      val z: Int

      init {
         this.x = x
         this.y = y
         this.z = z
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.y
      }

      public operator fun component3(): Int {
         return this.z
      }

      fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z): jooon.features.mirrorverse.SwiftnessWalker.Node {
         return SwiftnessWalker.Node(x, y, z)
      }

      override fun toString(): String {
         return "Node(x=${this.x}, y=${this.y}, z=${this.z})"
      }

      override fun hashCode(): Int {
         return (Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessWalker.Node
               && this.x == (other as SwiftnessWalker.Node).x
               && this.y == (other as SwiftnessWalker.Node).y
               && this.z == (other as SwiftnessWalker.Node).z
            }
      }
   }

   private data class SearchState(node: jooon.features.mirrorverse.SwiftnessWalker.Node, g: Int, f: Int) {
      val node: jooon.features.mirrorverse.SwiftnessWalker.Node
      val g: Int
      val f: Int

      init {
         this.node = node
         this.g = g
         this.f = f
      }

      public operator fun component1(): jooon.features.mirrorverse.SwiftnessWalker.Node {
         return this.node
      }

      public operator fun component2(): Int {
         return this.g
      }

      public operator fun component3(): Int {
         return this.f
      }

      fun copy(node: jooon.features.mirrorverse.SwiftnessWalker.Node = this.node, g: Int = this.g, f: Int = this.f): jooon.features.mirrorverse.SwiftnessWalker.SearchState {
         return SwiftnessWalker.SearchState(node, g, f)
      }

      override fun toString(): String {
         return "SearchState(node=${this.node}, g=${this.g}, f=${this.f})"
      }

      override fun hashCode(): Int {
         return (this.node.hashCode() * 31 + Integer.hashCode(this.g)) * 31 + Integer.hashCode(this.f)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessWalker.SearchState
               && this.node == (other as SwiftnessWalker.SearchState).node
               && this.g == (other as SwiftnessWalker.SearchState).g
               && this.f == (other as SwiftnessWalker.SearchState).f
            }
      }
   }

   private data class SegmentMetrics(projection: Double, laneError: Double) {
      val projection: Double
      val laneError: Double

      init {
         this.projection = projection
         this.laneError = laneError
      }

      public operator fun component1(): Double {
         return this.projection
      }

      public operator fun component2(): Double {
         return this.laneError
      }

      fun copy(projection: Double = this.projection, laneError: Double = this.laneError): jooon.features.mirrorverse.SwiftnessWalker.SegmentMetrics {
         return SwiftnessWalker.SegmentMetrics(projection, laneError)
      }

      override fun toString(): String {
         return "SegmentMetrics(projection=${this.projection}, laneError=${this.laneError})"
      }

      override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.projection) * 31 + java.lang.Double.hashCode(this.laneError)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessWalker.SegmentMetrics
               && java.lang.Double.compare(this.projection, (other as SwiftnessWalker.SegmentMetrics).projection) == 0
               && java.lang.Double.compare(this.laneError, (other as SwiftnessWalker.SegmentMetrics).laneError) == 0
            }
      }
   }
}
