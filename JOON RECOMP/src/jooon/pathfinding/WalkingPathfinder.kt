package jooon.pathfinding

import java.util.ArrayList
import java.util.HashMap
import java.util.Locale
import java.util.PriorityQueue
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FenceBlock
import net.minecraft.block.FenceGateBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.WallBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView
import net.minecraft.world.World

public object WalkingPathfinder {
   public const val FLAG_FLUID_FEET: Int = 1
   public const val FLAG_FLUID_HEAD: Int = 2
   public const val FLAG_LOW_HEADROOM: Int = 4
   public const val FLAG_NEAR_EDGE: Int = 8
   public const val FLAG_NEAR_WALL: Int = 16
   public const val FLAG_STEP_UP_NEXT: Int = 32
   public const val FLAG_DROP_NEXT: Int = 64
   public const val FLAG_TIGHT_CORRIDOR: Int = 128
   public const val FLAG_GAP_JUMP_NEXT: Int = 256
   private const val HEURISTIC_WEIGHT: Double = 1.05
   private const val MAX_NODES: Int = 160000
   private const val MAX_AXIS_DISTANCE: Int = 192
   private const val MAX_DROP: Int = 20
   private const val SEARCH_MARGIN: Int = 18
   private const val SPRINT_ONE_BLOCK_TIME: Double = 3.563791874554526
   private const val SPRINT_DIAGONAL_TIME: Double = 5.039962802470047
   private const val MOMENTUM_LOSS_PENALTY: Double = 6.0
   private const val SLAB_ASCENT_TIME: Double = 3.920171062009979
   private const val WALK_OFF_EDGE_TIME: Double = 1.781895937277263
   private const val LAND_RECOVERY_TIME: Double = 2.0
   private const val JUMP_UP_ONE_BLOCK_TIME: Double = 37.563791874554525

   fun findPath(level: World, start: Node, goal: Node, moveOrderOffset: Int, options: Options): Result {
      val startedAt = System.nanoTime()
      if (Math.abs(start.x - goal.x) > MAX_AXIS_DISTANCE || Math.abs(start.z - goal.z) > MAX_AXIS_DISTANCE) {
         return Result(emptyList(), 0, elapsedMs(startedAt), "Target is too far for walk pathfinding.")
      }

      val bounds = Bounds(
         Math.min(start.x, goal.x) - SEARCH_MARGIN,
         Math.max(start.x, goal.x) + SEARCH_MARGIN,
         Math.min(start.y, goal.y) - MAX_DROP - 3,
         Math.max(start.y, goal.y) + 5,
         Math.min(start.z, goal.z) - SEARCH_MARGIN,
         Math.max(start.z, goal.z) + SEARCH_MARGIN
      )
      val cache = CollisionCache(level)

      val resolvedStart = resolveStandableNear(cache, start, bounds, options)
         ?: return Result(emptyList(), 0, elapsedMs(startedAt), "No valid standing position near start.")
      val resolvedGoal = resolveStandableNear(cache, goal, bounds, options)
         ?: return Result(emptyList(), 0, elapsedMs(startedAt), "No valid standing position near target.")

      val open = PriorityQueue<OpenNode>(compareBy<OpenNode> { it.f }.thenBy { it.hashCode() })
      val gScore = HashMap<Node, Double>(80000)
      val parent = HashMap<Node, Node>(80000)
      val moveType = HashMap<Node, MoveType>(80000)
      var nodesExplored = 0

      gScore[resolvedStart] = 0.0
      moveType[resolvedStart] = MoveType.WALK
      open.add(OpenNode(resolvedStart, 0.0, heuristic(resolvedStart, resolvedGoal)))

      while (open.isNotEmpty() && nodesExplored < MAX_NODES) {
         val current = open.poll()
         val currentG = current.g
         val bestG = gScore[current.node]

         if (currentG > (bestG ?: Double.MAX_VALUE) + 1.0E-4) continue

         nodesExplored++
         if (isGoal(current.node, resolvedGoal)) {
            return buildResult(current.node, parent, moveType, cache, nodesExplored, startedAt)
         }

         for (edge in neighbors(cache, current.node, bounds, moveOrderOffset, options)) {
            val nextCost = currentG + edge.cost
            val bestNext = gScore[edge.to]
            if (nextCost >= (bestNext ?: Double.MAX_VALUE)) continue

            gScore[edge.to] = nextCost
            parent[edge.to] = current.node
            moveType[edge.to] = edge.moveType
            open.add(OpenNode(edge.to, nextCost, nextCost + heuristic(edge.to, resolvedGoal) * HEURISTIC_WEIGHT))
         }
      }

      return if (nodesExplored >= MAX_NODES) {
         Result(emptyList(), nodesExplored, elapsedMs(startedAt), "Search limit reached.")
      } else {
         Result(emptyList(), nodesExplored, elapsedMs(startedAt), "No walkable path found.")
      }
   }

   fun fromBlockPosFeet(pos: BlockPos): Node = Node(pos.x, pos.y, pos.z)

   private fun resolveStandableNear(
      cache: CollisionCache,
      node: Node,
      bounds: Bounds,
      options: Options
   ): Node? {
      if (bounds.contains(node) && cache.isStandable(node) && isAllowed(node, cache, options)) {
         return node
      }

      for (radius in 0 downTo -2) {
         val dx = Node(node.x, node.y + radius, node.z)
         if (bounds.contains(dx) && cache.isStandable(dx) && isAllowed(dx, cache, options)) {
            return dx
         }
      }

      label106@ for (var10 in 1..3) {
         var var11 = -var10
         if (-var10 <= var10) {
            while (true) {
               var dz = -var10
               if (-var10 <= var10) {
                  while (true) {
                     for (dy in -2..2) {
                        if (Math.abs(var11) == var10 || Math.abs(dz) == var10) {
                           val candidate = Node(node.x + var11, node.y + dy, node.z + dz)
                           if (bounds.contains(candidate) && cache.isStandable(candidate) && isAllowed(candidate, cache, options)) {
                              return candidate
                           }
                        }
                     }
                     if (dz == var10) break
                     dz++
                  }
               }
               if (var11 == var10) continue@label106
               var11++
            }
         }
      }
      return null
   }

   private fun neighbors(
      cache: CollisionCache,
      node: Node,
      bounds: Bounds,
      moveOrderOffset: Int,
      options: Options
   ): List<Edge> {
      val out = ArrayList<Edge>(36)

      for (move in orderedWalkMoves(moveOrderOffset)) {
         val dx = move.dx
         val dz = move.dz
         val dy = move.dy
         if (dy == 0) {
            addFlat(cache, node, dx, dz, bounds, out, options)
         } else if (dy > 0) {
            addStep(cache, node, dx, dz, bounds, out, options)
         } else if (options.allowDrops) {
            addDrop(cache, node, dx, dz, bounds, out, options)
         }
      }
      return out
   }

   private fun orderedWalkMoves(offset: Int): List<Move> {
      val moves = listOf(
         Move(0, -1, 0),
         Move(0, 1, 0),
         Move(1, 0, 0),
         Move(-1, 0, 0),
         Move(1, -1, 0),
         Move(-1, -1, 0),
         Move(1, 1, 0),
         Move(-1, 1, 0),
         Move(0, -1, 1),
         Move(0, 1, 1),
         Move(1, 0, 1),
         Move(-1, 0, 1),
         Move(0, -1, -1),
         Move(0, 1, -1),
         Move(1, 0, -1),
         Move(-1, 0, -1)
      )
      if (offset <= 0) return moves
      val split = offset % moves.size
      return moves.drop(split) + moves.take(split)
   }

   private fun addFlat(
      cache: CollisionCache,
      node: Node,
      dx: Int,
      dz: Int,
      bounds: Bounds,
      out: MutableList<Edge>,
      options: Options
   ) {
      if (dx == 0 || dz == 0 || cache.canCutCorner(node, dx, dz)) {
         val flat = Node(node.x + dx, node.y, node.z + dz)
         if (bounds.contains(flat) && cache.isStandable(flat) && isAllowed(flat, cache, options)) {
            out.add(Edge(flat, moveCost(dx, dz) + cache.penalty(flat, options), MoveType.WALK))
         }
      }
   }

   private fun addStep(
      cache: CollisionCache,
      node: Node,
      dx: Int,
      dz: Int,
      bounds: Bounds,
      out: MutableList<Edge>,
      options: Options
   ) {
      if (cache.isPassable(node.x, node.y + 2, node.z)) {
         if (cache.isSolidFloor(node.x + dx, node.y, node.z + dz)) {
            if (cache.isPassable(node.x + dx, node.y + 1, node.z + dz)) {
               if (cache.isPassable(node.x + dx, node.y + 2, node.z + dz)) {
                  val up = Node(node.x + dx, node.y + 1, node.z + dz)
                  if (bounds.contains(up) && isAllowed(up, cache, options)) {
                     val src = cache.blockState(node.x, node.y - 1, node.z)
                     val dst = cache.blockState(up.x, up.y - 1, up.z)
                     if (cache.isFenceLike(dst)) return

                     out.add(
                        Edge(
                           up,
                           (if (!isSoftStep(src) && !isSoftStep(dst)) JUMP_UP_ONE_BLOCK_TIME else SLAB_ASCENT_TIME) + cache.penalty(up, options),
                           MoveType.STEP_UP
                        )
                     )
                  }
               }
            }
         }
      }
   }

   private fun addDrop(
      cache: CollisionCache,
      node: Node,
      dx: Int,
      dz: Int,
      bounds: Bounds,
      out: MutableList<Edge>,
      options: Options
   ) {
      val destX = node.x + dx
      val destZ = node.z + dz
      if (cache.isPassable(destX, node.y + 1, destZ)) {
         if (cache.isPassable(destX, node.y, destZ)) {
            if (cache.isPassable(destX, node.y - 1, destZ)) {
               for (drop in 1..MAX_DROP) {
                  val floorY = node.y - drop - 1
                  if (!cache.isPassable(destX, floorY, destZ)) {
                     if (!cache.isSolidFloor(destX, floorY, destZ)) return

                     val down = Node(destX, floorY + 1, destZ)
                     if (!bounds.contains(down)) return
                     if (!cache.isPassable(down.x, down.y, down.z)) return
                     if (!cache.isPassable(down.x, down.y + 1, down.z)) return
                     if (!isAllowed(down, cache, options)) return

                     var totalCost = WALK_OFF_EDGE_TIME + fallTime(drop)
                     if (drop > 3) {
                        totalCost += (drop - 3) * (drop - 3) * LAND_RECOVERY_TIME
                     }
                     out.add(Edge(down, totalCost + cache.penalty(down, options), MoveType.DROP))
                     return
                  }
               }
            }
         }
      }
   }

   private fun isAllowed(
      node: Node,
      cache: CollisionCache,
      options: Options
   ): Boolean {
      return !options.avoidEdges || !cache.nearEdge(node)
   }

   private fun buildResult(
      end: Node,
      parent: Map<Node, Node>,
      moveType: Map<Node, MoveType>,
      cache: CollisionCache,
      nodesExplored: Int,
      startedAt: Long
   ): Result {
      val reversed = ArrayList<Node>()
      var current: Node? = end
      while (current != null) {
         reversed.add(current)
         current = parent[current]
      }
      reversed.reverse()
      val steps = reversed.mapIndexed { index, node ->
         val next = reversed.getOrNull(index + 1)
         val mt = moveType[node] ?: MoveType.WALK
         val nextMv = moveType[next]
         PathStep(node, mt, flagsFor(cache, node, next, nextMv))
      }
      return Result(steps, nodesExplored, elapsedMs(startedAt))
   }

   private fun flagsFor(
      cache: CollisionCache,
      node: Node,
      next: Node?,
      nextMove: MoveType?
   ): Int {
      var flags = 0
      if (cache.isFluid(node.x, node.y, node.z)) flags = flags or FLAG_FLUID_FEET
      if (cache.isFluid(node.x, node.y + 1, node.z)) flags = flags or FLAG_FLUID_HEAD
      if (!cache.hasHighHeadroom(node)) flags = flags or FLAG_LOW_HEADROOM
      if (cache.nearEdge(node)) flags = flags or FLAG_NEAR_EDGE
      if (cache.nearWall(node)) flags = flags or FLAG_NEAR_WALL
      if (cache.tightCorridor(node)) flags = flags or FLAG_TIGHT_CORRIDOR
      when (nextMove) {
         MoveType.STEP_UP -> flags = flags or FLAG_STEP_UP_NEXT
         MoveType.DROP -> flags = flags or FLAG_DROP_NEXT
         MoveType.GAP_JUMP -> flags = flags or FLAG_GAP_JUMP_NEXT
         else -> {}
      }
      if (next != null && next.y > node.y) flags = flags or FLAG_STEP_UP_NEXT
      if (next != null && next.y < node.y) flags = flags or FLAG_DROP_NEXT
      return flags
   }

   private fun isGoal(node: Node, goal: Node): Boolean {
      return Math.abs(node.x - goal.x) <= 0 && Math.abs(node.z - goal.z) <= 0 && Math.abs(node.y - goal.y) <= 1
   }

   private fun heuristic(a: Node, b: Node): Double {
      val dx = Math.abs(a.x - b.x).toDouble()
      val dy = Math.abs(a.y - b.y).toDouble()
      val dz = Math.abs(a.z - b.z).toDouble()
      val diagonal = Math.min(dx, dz)
      return diagonal * SPRINT_DIAGONAL_TIME + (Math.max(dx, dz) - diagonal) * SPRINT_ONE_BLOCK_TIME + dy * 8.0
   }

   private fun moveCost(dx: Int, dz: Int): Double {
      val ax = Math.abs(dx)
      val az = Math.abs(dz)
      val diagonal = Math.min(ax, az)
      return diagonal * SPRINT_DIAGONAL_TIME + (Math.max(ax, az) - diagonal) * SPRINT_ONE_BLOCK_TIME
   }

   private fun fallTime(drop: Int): Double {
      return Math.sqrt(drop.toDouble()) * 5.0 + 2.0
   }

   fun isSoftStep(state: BlockState): Boolean {
      val block = state.block
      val key = block.translationKey.lowercase(Locale.ROOT)
      return key.contains("slab") || key.contains("stair") || block == Blocks.SNOW_BLOCK
   }

   private fun elapsedMs(startedAt: Long): Long {
      return (System.nanoTime() - startedAt) / 1000000L
   }

   private data class Bounds(
      val minX: Int,
      val maxX: Int,
      val minY: Int,
      val maxY: Int,
      val minZ: Int,
      val maxZ: Int
   ) {
      fun contains(node: Node): Boolean {
         return node.x in minX..maxX && node.y in minY..maxY && node.z in minZ..maxZ
      }
   }

   private class CollisionCache(private val level: World) {
      private val passable = HashMap<BlockPos, Boolean>(8192)
      private val solid = HashMap<BlockPos, Boolean>(8192)
      private val fluid = HashMap<BlockPos, Boolean>(512)
      private val states = HashMap<BlockPos, BlockState>(8192)
      private val context = ShapeContext.absent()

      fun isStandable(node: Node): Boolean {
         return isPassable(node.x, node.y, node.z) && isPassable(node.x, node.y + 1, node.z) && isSolidFloor(node.x, node.y - 1, node.z)
      }

      fun hasHeadroom(node: Node): Boolean {
         return isPassable(node.x, node.y + 1, node.z) && isPassable(node.x, node.y + 2, node.z)
      }

      fun hasHighHeadroom(node: Node): Boolean {
         return isPassable(node.x, node.y + 2, node.z) && isPassable(node.x, node.y + 3, node.z)
      }

      fun canCutCorner(node: Node, dx: Int, dz: Int): Boolean {
         return isStandable(Node(node.x + dx, node.y, node.z)) && isStandable(Node(node.x, node.y, node.z + dz))
      }

      fun nearEdge(node: Node): Boolean {
         for (dx in -1..1) {
            for (dz in -1..1) {
               if ((dx != 0 || dz != 0) && !isSolidFloor(node.x + dx, node.y - 1, node.z + dz)) {
                  return true
               }
            }
         }
         return false
      }

      fun nearWall(node: Node): Boolean {
         for ((dx, dz) in arrayOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)) {
            if (!isPassable(node.x + dx, node.y, node.z + dz) || !isPassable(node.x + dx, node.y + 1, node.z + dz)) {
               return true
            }
         }
         return false
      }

      fun tightCorridor(node: Node): Boolean {
         return (!isPassable(node.x + 1, node.y, node.z) || !isPassable(node.x - 1, node.y, node.z))
            && (!isPassable(node.x, node.y, node.z + 1) || !isPassable(node.x, node.y, node.z - 1))
      }

      fun penalty(node: Node, options: Options): Double {
         var value = 0.0
         if (nearWall(node)) value += 1.8
         if (tightCorridor(node)) value += 3.0
         if (isFluid(node.x, node.y, node.z)) value += 20.0
         if (options.polishedAndesitePenalty > 0.0 && isAvoidedPolishedAndesiteFloor(node, options)) {
            value += options.polishedAndesitePenalty
         }
         return value
      }

      private fun isAvoidedPolishedAndesiteFloor(node: Node, options: Options): Boolean {
         val block = blockState(node.x, node.y - 1, node.z).block
         return block == Blocks.POLISHED_ANDESITE
            && (!options.allowPolishedAndesiteNearChiseledStoneBricks || !isNearChiseledStoneBricks(node.x, node.y - 1, node.z))
      }

      private fun isNearChiseledStoneBricks(x: Int, y: Int, z: Int): Boolean {
         for (dx in -1..1) {
            for (dz in -1..1) {
               if ((dx != 0 || dz != 0) && blockState(x + dx, y, z + dz).block == Blocks.CHISELED_STONE_BRICKS) {
                  return true
               }
            }
         }
         return false
      }

      fun isFluid(x: Int, y: Int, z: Int): Boolean {
         val pos = BlockPos(x, y, z)
         return fluid.getOrPut(pos) { !blockState(x, y, z).fluidState.isEmpty() }
      }

      fun blockState(x: Int, y: Int, z: Int): BlockState {
         val pos = BlockPos(x, y, z)
         return states.getOrPut(pos) { level.getBlockState(pos) }
      }

      fun isPassable(x: Int, y: Int, z: Int): Boolean {
         val pos = BlockPos(x, y, z)
         return passable.getOrPut(pos) { blockState(x, y, z).getCollisionShape(level as BlockView, pos, context).isEmpty() }
      }

      fun isSolidFloor(x: Int, y: Int, z: Int): Boolean {
         val pos = BlockPos(x, y, z)
         return solid.getOrPut(pos) {
            val state = blockState(x, y, z)
            !isFenceLike(state) && !state.getCollisionShape(level as BlockView, pos, context).isEmpty()
         }
      }

      fun isFenceLike(state: BlockState): Boolean {
         val block = state.block
         return block is FenceBlock || block is WallBlock || block is FenceGateBlock
      }
   }

   private data class Edge(val to: Node, val cost: Double, val moveType: MoveType)

   private data class Move(val dx: Int, val dz: Int, val dy: Int)

   public enum class MoveType {
      WALK,
      STEP_UP,
      DROP,
      GAP_JUMP;

      companion object {
         @JvmStatic
         fun getEntries(): kotlin.enums.EnumEntries<MoveType> = entries
      }
   }

   public data class Node(val x: Int, val y: Int, val z: Int) {
      fun center(): Vec3d = Vec3d(x + 0.5, y.toDouble(), z + 0.5)
   }

   private data class OpenNode(val node: Node, val g: Double, val f: Double)

   public data class Options(
      val allowDrops: Boolean = true,
      val avoidEdges: Boolean = false,
      val polishedAndesitePenalty: Double = 0.0,
      val allowPolishedAndesiteNearChiseledStoneBricks: Boolean = false
   )

   public data class PathStep(val node: Node, val moveType: MoveType, val flags: Int)

   public data class Result(
      val steps: List<PathStep>,
      val nodesExplored: Int,
      val timeMs: Long,
      val error: String? = null
   ) {
      val success: Boolean get() = steps.isNotEmpty() && error == null
   }
}
