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

public object SwiftnessWalker {
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
   private final var enabled: Boolean
   private final var latestWoolSeq: Long
   private final var latestPlannedWoolSeq: Long = -1L
   @JvmStatic
   private BlockPos targetWool;
   private final var targetNode: jooon.features.mirrorverse.SwiftnessWalker.Node?
   private final var path: List<jooon.features.mirrorverse.SwiftnessWalker.Node> = CollectionsKt.emptyList()
   private final var pathCursor: Int
   private final var lastPlanMs: Long
   private final var lastProgressMs: Long
   private final var lastStepDist: Double = java.lang.Double.MAX_VALUE
   private final var noTargetWarned: Boolean
   private final var targetSeenMs: Long
   private final var jumpStateUntilMs: Long
   private final var jumpState: Int
   private final var jumpCooldownUntilMs: Long
   private final var jumpSegmentFrom: jooon.features.mirrorverse.SwiftnessWalker.Node?
   private final var jumpSegmentTo: jooon.features.mirrorverse.SwiftnessWalker.Node?
   private final var jumpPressUntilMs: Long

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
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
            val var10000: ClientPlayerEntity = INSTANCE.getMc().field_1724
            if (var10000 == null) {
               return@lambda_2_lambda_1 1
            } else {
               enabled = !enabled
               if (!enabled) {
                  INSTANCE.clearMovement()
                  path = CollectionsKt.emptyList()
                  pathCursor = 0
                  JooonReimagined.Companion.sendMessage("§cSwiftness walker disabled.")
                  return@lambda_2_lambda_1 1
               } else {
                  var10000.method_7353(Text.method_43470("Use §a/swiftness§7 again to stop.") as Text, true)
                  noTargetWarned = false
                  path = CollectionsKt.emptyList()
                  pathCursor = 0
                  latestPlannedWoolSeq = -1L
                  lastPlanMs = 0L
                  lastProgressMs = System.currentTimeMillis()
                  lastStepDist = java.lang.Double.MAX_VALUE
                  INSTANCE.resetJumpState()
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
      if (state.method_26204() === Blocks.field_10028) {
         val var3: Int = latestWoolSeq++
         targetWool = pos
         targetNode = SwiftnessWalker.Node(pos.method_10263(), pos.method_10264() + 1, pos.method_10260())
         targetSeenMs = System.currentTimeMillis()
         if (enabled) {
            path = CollectionsKt.emptyList()
            pathCursor = 0
         }
      } else {
         if (targetWool == pos && state.method_26204() != Blocks.field_10028) {
            targetWool = null
            targetNode = null
            path = CollectionsKt.emptyList()
            pathCursor = 0
            targetSeenMs = 0L
            latestPlannedWoolSeq = -1L
         }
      }
   }

   fun onSectionBlocksUpdate(packet: ChunkDeltaUpdateS2CPacket) {
      if (this.getMc().field_1687 != null) {
         packet.method_30621({ pos: BlockPos, state: BlockState ->
            INSTANCE.onBlockUpdate(pos, state)
         })
      }
   }

   fun onTick(client: MinecraftClient) {
      if (client.field_1724 != null) {
         val player: ClientPlayerEntity = client.field_1724
         if (targetWool == null) {
            val `$this$onTick_u24lambda_u244`: SwiftnessWalker = this
            if (!noTargetWarned) {
               noTargetWarned = true
               JooonReimagined.Companion.sendMessage("§eNo lime wool target yet. Spawn lime wool and it will lock automatically.")
            }

            `$this$onTick_u24lambda_u244`.clearMovement()
         } else {
            val targetPos: BlockPos = targetWool
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
                              "§aSwiftness reached lime wool at §f${targetPos.method_10263()}, ${targetPos.method_10264()}, ${targetPos.method_10260()}"
                           )
                           path = CollectionsKt.emptyList()
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
            val alignPoint: Pair = this.segmentPoint(from, to, RangesKt.coerceIn(metrics.projection, backProj, 0.15), 0.0)
            this.applyMovementToward(
               player, (alignPoint.getFirst() as java.lang.Number).doubleValue(), (alignPoint.getSecond() as java.lang.Number).doubleValue(), true, false
            )
            PlayerController.INSTANCE.pressJump(false)
            if (Math.abs(metrics.laneError) <= alignEps) {
               jumpState = 2
            }
         }
         2 -> {
            val var27: Pair = this.segmentPoint(from, to, backProj, 0.0)
            this.applyMovementToward(
               player, (var27.getFirst() as java.lang.Number).doubleValue(), (var27.getSecond() as java.lang.Number).doubleValue(), true, false
            )
            PlayerController.INSTANCE.pressJump(false)
            if (now >= jumpCooldownUntilMs && Math.abs(metrics.laneError) <= alignEps && Math.abs(metrics.projection - backProj) <= 0.06) {
               jumpState = 3
               jumpStateUntilMs = now + runupMs
            }
         }
         3 -> {
            val runPoint: Pair = this.segmentPoint(from, to, runTargetProj, 0.0)
            this.applyMovementToward(
               player, (runPoint.getFirst() as java.lang.Number).doubleValue(), (runPoint.getSecond() as java.lang.Number).doubleValue(), true, true
            )
            PlayerController.INSTANCE.pressJump(false)
            if (player.method_24828() && (metrics.projection >= jumpTriggerProj || now >= jumpStateUntilMs)) {
               jumpState = 4
               jumpPressUntilMs = now + holdMs
               jumpStateUntilMs = now + 1000L
               jumpCooldownUntilMs = now + 180L
            }
         }
         4 -> {
            PlayerController.INSTANCE.pressForward(true)
            PlayerController.INSTANCE.pressBack(false)
            PlayerController.INSTANCE.pressLeft(false)
            PlayerController.INSTANCE.pressRight(false)
            PlayerController.INSTANCE.pressSprint(true)
            PlayerController.INSTANCE.pressJump(now < jumpPressUntilMs)
            if (player.method_24828() && now > jumpPressUntilMs + 80L || now >= jumpStateUntilMs) {
               jumpState = 1
            }
         }
         else -> jumpState = 1
      }
   }

   fun segmentMetrics(from: SwiftnessWalker.Node, to: SwiftnessWalker.Node, player: ClientPlayerEntity): SwiftnessWalker.SegmentMetrics {
      val centerX: Double = from.x + 0.5
      val centerZ: Double = from.z + 0.5
      val dirX: Int = RangesKt.coerceIn(to.x - from.x, -1, 1)
      if (dirX != 0)
         SwiftnessWalker.SegmentMetrics((player.method_23317() - centerX) * dirX, player.method_23321() - centerZ)
         else
         SwiftnessWalker.SegmentMetrics((player.method_23321() - centerZ) * RangesKt.coerceIn(to.z - from.z, -1, 1), player.method_23317() - centerX)
      }

   private fun segmentPoint(
      from: jooon.features.mirrorverse.SwiftnessWalker.Node,
      to: jooon.features.mirrorverse.SwiftnessWalker.Node,
      projection: Double,
      laneOffset: Double
   ): Pair<Double, Double> {
      val centerX: Double = from.x + 0.5
      val centerZ: Double = from.z + 0.5
      val dirX: Int = RangesKt.coerceIn(to.x - from.x, -1, 1)
      return if (dirX != 0)
         Pair(centerX + projection * (double)dirX, centerZ + laneOffset)
         else
         Pair(centerX + laneOffset, centerZ + projection * (double)RangesKt.coerceIn(to.z - from.z, -1, 1))
      }

   fun hasSteppedPastLongJump(from: SwiftnessWalker.Node, to: SwiftnessWalker.Node, player: ClientPlayerEntity): Boolean {
      if (!player.method_24828()) {
         false
      } else {
         val dirX: Int = to.x - from.x
         val dirZ: Int = to.z - from.z
         val landingX: Double = to.x + 0.5
         val landingZ: Double = to.z + 0.5
         if (dirX != 0) {
            if (Math.abs(player.method_23321() - landingZ) > 1.1) {
               false
            } else {
               if (dirX > 0) player.method_23317() >= landingX - 0.15 else player.method_23317() <= landingX + 0.15
            }
         } else if (dirZ != 0) {
            if (Math.abs(player.method_23317() - landingX) > 1.1) {
               false
            } else {
               if (dirZ > 0) player.method_23321() >= landingZ - 0.15 else player.method_23321() <= landingZ + 0.15
            }
         } else {
            false
         }
      }
   }

   private fun isSameLongJumpSegment(from: jooon.features.mirrorverse.SwiftnessWalker.Node, to: jooon.features.mirrorverse.SwiftnessWalker.Node): Boolean {
      return jumpSegmentFrom == from && jumpSegmentTo == to
   }

   fun applyMovementToward(player: ClientPlayerEntity, targetX: Double, targetZ: Double, allowBackAndStrafe: Boolean, sprint: Boolean) {
      val dx: Double = targetX - player.method_23317()
      val dz: Double = targetZ - player.method_23321()
      val yawRad: Double = Math.toRadians((double)player.method_36454())
      val forwardInput: Double = RangesKt.coerceIn(dx * -Math.sin(yawRad) + dz * Math.cos(yawRad), -1.0, 1.0)
      val rightInput: Double = RangesKt.coerceIn(dx * Math.cos(yawRad) + dz * Math.sin(yawRad), -1.0, 1.0)
      val wantForward: Boolean = forwardInput > 0.03
      val wantBack: Boolean = allowBackAndStrafe && forwardInput < -0.03
      val wantLeft: Boolean = allowBackAndStrafe && rightInput < -0.03
      val wantRight: Boolean = allowBackAndStrafe && rightInput > 0.03
      PlayerController.INSTANCE.pressForward(wantForward)
      PlayerController.INSTANCE.pressBack(wantBack)
      PlayerController.INSTANCE.pressLeft(wantLeft)
      PlayerController.INSTANCE.pressRight(wantRight)
      PlayerController.INSTANCE.pressSprint(sprint)
   }

   private fun cardinalYawForStep(from: jooon.features.mirrorverse.SwiftnessWalker.Node, to: jooon.features.mirrorverse.SwiftnessWalker.Node): Float {
      val dx: Int = to.x - from.x
      return if (dx > 0) -90.0F else (if (dx < 0) 90.0F else (if (to.z - from.z > 0) 0.0F else 180.0F))
   }

   fun applySmoothLook(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, maxStep: Float) {
      player.method_36456(player.method_36454() + RangesKt.coerceIn(MathHelper.method_15393(targetYaw - player.method_36454()), -maxStep, maxStep))
      player.method_36457(player.method_36455() + RangesKt.coerceIn(MathHelper.method_15393(targetPitch - player.method_36455()), -maxStep, maxStep))
      player.field_6241 = player.method_36454()
      player.field_6283 = player.method_36454()
      player.field_6259 = player.method_36454()
      player.field_6220 = player.method_36454()
   }

   private fun getNeighbors(node: jooon.features.mirrorverse.SwiftnessWalker.Node, minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int): List<
         jooon.features.mirrorverse.SwiftnessWalker.Node
      > {
      val out: ArrayList = ArrayList(24)

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
      val distance: Int = this.manhattanStep(a, b)
      return if (distance == 1) 1 else (if (2 <= distance && distance < 4) distance + 1 else Integer.MAX_VALUE)
   }

   private fun manhattanStep(a: jooon.features.mirrorverse.SwiftnessWalker.Node, b: jooon.features.mirrorverse.SwiftnessWalker.Node): Int {
      return Math.abs(a.x - b.x) + Math.abs(a.z - b.z)
   }

   private fun isStandable(node: jooon.features.mirrorverse.SwiftnessWalker.Node): Boolean {
      val var10000: World = this.getLevelOrNull()
      if (var10000 == null) {
         return false
      } else {
         val feet: BlockPos = this.toBlockPos(node)
         var var10001: BlockPos = feet.method_10074()
         if (this.isSolid(var10001) && this.isPassable(feet)) {
            var10001 = feet.method_10084()
            if (this.isPassable(var10001) && var10000.method_8320(feet).method_26227().method_15769()) {
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
      val var10000: World = this.getLevelOrNull()
      if (var10000 == null) {
         false
      } else {
         val var5: BlockState = var10000.method_8320(pos)
         val var6: VoxelShape = var5.method_26194(var10000 as BlockView, pos, ShapeContext.method_16194())
         var5.method_26215() || var6.method_1110()
      }
   }

   fun isSolid(pos: BlockPos): Boolean {
      val var10000: World = this.getLevelOrNull()
      if (var10000 == null) {
         false
      } else {
         val var5: BlockState = var10000.method_8320(pos)
         if (!var5.method_26227().method_15769()) {
            false
         } else {
            val var6: VoxelShape = var5.method_26194(var10000 as BlockView, pos, ShapeContext.method_16194())
            !var5.method_26215() && !var6.method_1110()
         }
      }
   }

   private fun heuristic(a: jooon.features.mirrorverse.SwiftnessWalker.Node, b: jooon.features.mirrorverse.SwiftnessWalker.Node): Int {
      return Math.abs(a.x - b.x) + Math.abs(a.z - b.z) + Math.abs(a.y - b.y) * 2
   }

   fun getLevelOrNull(): World {
      this.getMc().field_1687 as World
   }

   private fun clearMovement() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressSprint(false)
      PlayerController.INSTANCE.pressJump(false)
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
      SwiftnessWalker.Node(`$this$toNode`.method_10263(), `$this$toNode`.method_10264(), `$this$toNode`.method_10260())
   }

   fun SwiftnessWalker.Node.toBlockPos(): BlockPos {
      BlockPos(`$this$toBlockPos`.x, `$this$toBlockPos`.y, `$this$toBlockPos`.z)
   }

   @JvmStatic
   fun `getNeighbors$addIfInside`(
      `$minX`: Int, `$maxX`: Int, `$minZ`: Int, `$maxZ`: Int, `$minY`: Int, `$maxY`: Int, out: ArrayList<SwiftnessWalker.Node>, nx: Int, ny: Int, nz: Int
   ) {
      if (nx >= `$minX` && nx <= `$maxX`) {
         if (nz >= `$minZ` && nz <= `$maxZ`) {
            if (ny >= `$minY` && ny <= `$maxY`) {
               val candidate: SwiftnessWalker.Node = SwiftnessWalker.Node(nx, ny, nz)
               if (INSTANCE.isStandable(candidate)) {
                  out.add(candidate)
               }
            }
         }
      }
   }

   private data class Node(x: Int, y: Int, z: Int) {
      public final val x: Int
      public final val y: Int
      public final val z: Int

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

      public fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z): jooon.features.mirrorverse.SwiftnessWalker.Node {
         return SwiftnessWalker.Node(x, y, z)
      }

      public override fun toString(): String {
         return "Node(x=${this.x}, y=${this.y}, z=${this.z})"
      }

      public override fun hashCode(): Int {
         return (Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val node: jooon.features.mirrorverse.SwiftnessWalker.Node
      public final val g: Int
      public final val f: Int

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

      public fun copy(node: jooon.features.mirrorverse.SwiftnessWalker.Node = this.node, g: Int = this.g, f: Int = this.f): jooon.features.mirrorverse.SwiftnessWalker.SearchState {
         return SwiftnessWalker.SearchState(node, g, f)
      }

      public override fun toString(): String {
         return "SearchState(node=${this.node}, g=${this.g}, f=${this.f})"
      }

      public override fun hashCode(): Int {
         return (this.node.hashCode() * 31 + Integer.hashCode(this.g)) * 31 + Integer.hashCode(this.f)
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val projection: Double
      public final val laneError: Double

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

      public fun copy(projection: Double = this.projection, laneError: Double = this.laneError): jooon.features.mirrorverse.SwiftnessWalker.SegmentMetrics {
         return SwiftnessWalker.SegmentMetrics(projection, laneError)
      }

      public override fun toString(): String {
         return "SegmentMetrics(projection=${this.projection}, laneError=${this.laneError})"
      }

      public override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.projection) * 31 + java.lang.Double.hashCode(this.laneError)
      }

      public override operator fun equals(other: Any?): Boolean {
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
