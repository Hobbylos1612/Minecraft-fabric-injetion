package jooon.pathfinding.voxel.solver

import java.util.ArrayList
import java.util.HashMap
import java.util.PriorityQueue
import jooon.pathfinding.voxel.VoxelJumpProfile
import jooon.pathfinding.voxel.world.VoxelBlockCache
import jooon.pathfinding.voxel.world.VoxelBlockCache.StandSurface
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_243
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

@SourceDebugExtension(["SMAP\nVoxelGroundSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VoxelGroundSolver.kt\njooon/pathfinding/voxel/solver/VoxelGroundSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,321:1\n2333#2,14:322\n2333#2,14:336\n2333#2,14:350\n*S KotlinDebug\n*F\n+ 1 VoxelGroundSolver.kt\njooon/pathfinding/voxel/solver/VoxelGroundSolver\n*L\n54#1:322,14\n219#1:336,14\n237#1:350,14\n*E\n"])
public object VoxelGroundSolver {
   private const val MAX_NODES: Int = 100000
   private const val MAX_PLAN_MS: Long = 1500L
   private const val HEURISTIC_WEIGHT: Double = 1.15
   private const val COST_CARDINAL: Double = 1.0
   private const val COST_DIAGONAL: Double = 1.414
   private const val COST_STEP_UP: Double = 1.3
   private const val COST_DESCEND: Double = 0.95
   private const val COST_FALL_PER_BLOCK: Double = 0.35
   private const val COST_JUMP_ACTION: Double = 6.0
   private const val COST_JUMP_SKIP_PER_BLOCK: Double = 1.8
   private const val FALL_LIMIT: Int = 64
   private const val GOAL_REACH_DIST_SQ: Double = 1.5
   private const val WALL_PENALTY_PER_NEIGHBOR: Double = 0.8
   private const val CLIFF_PENALTY_PER_NEIGHBOR: Double = 1.5
   private final val CARDINAL_DX: IntArray = intArrayOf(1, -1, 0, 0)
   private final val CARDINAL_DZ: IntArray = intArrayOf(0, 0, 1, -1)
   private final val MOVES: Array<jooon.pathfinding.voxel.solver.VoxelGroundSolver.Move>

   fun solve(start: Vec3d, goal: Vec3d): MutableList<Vec3d> {
      val var10000: VoxelGroundSolver.Node = this.snapToGround(start)
      label16@
      if (var10000 == null) {
         CollectionsKt.emptyList()
      } else {
         val var5: VoxelGroundSolver.Node = this.snapToGround(goal)
         if (var5 == null) CollectionsKt.emptyList() else this.aStar(var10000, var5)
      }
   }

   fun snapToGround(pos: Vec3d): VoxelGroundSolver.Node {
      run label39@{
         val bx: Int = (int)Math.floor(pos.field_1352)
         val bz: Int = (int)Math.floor(pos.field_1350)
         val `iterator$iv`: java.util.Iterator = VoxelBlockCache.INSTANCE.getStandableSurfaces(bx, bz, pos.field_1351 - 2.5, pos.field_1351 + 1.5).iterator()
         val var10000: Any
         if (!`iterator$iv`.hasNext()) {
            var10000 = null
         } else {
            var `minElem$iv`: Any = `iterator$iv`.next()
            if (!`iterator$iv`.hasNext()) {
               var10000 = `minElem$iv`
            } else {
               var var16: Double = Math.abs((`minElem$iv` as VoxelBlockCache.StandSurface).feetY - pos.field_1351)

               do {
                  val var17: Any = `iterator$iv`.next()
                  val var18: Double = Math.abs((var17 as VoxelBlockCache.StandSurface).feetY - pos.field_1351)
                  if (java.lang.Double.compare(var16, var18) > 0) {
                     `minElem$iv` = var17
                     var16 = var18
                  }
               } while (`iterator$iv`.hasNext())

               var10000 = `minElem$iv`
            }
         }

         if (var10000 as VoxelBlockCache.StandSurface == null)
            null
            else
            VoxelGroundSolver.Node(bx, (var10000 as VoxelBlockCache.StandSurface).getPos().method_10264(), bz, (var10000 as VoxelBlockCache.StandSurface).feetY)
         }
   }

   private fun aStar(start: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node): List<
         class_243
      > {
      val jumpProfile: VoxelJumpProfile = VoxelJumpProfile.Companion.current$default(VoxelJumpProfile.Companion, null, 1, null)
      val open: PriorityQueue = PriorityQueue(VoxelGroundSolver$aStar$$inlined$compareBy$1())
      val closed: HashMap = HashMap()
      val startEntry: VoxelGroundSolver.Entry = VoxelGroundSolver.Entry(start, 0.0, this.heuristic(start, goal), null)
      open.add(startEntry)
      closed.put(start.packKey(), 0.0)
      var expanded: Int = 0
      var bestNear: VoxelGroundSolver.Entry = startEntry
      var bestNearH: Double = this.heuristic(start, goal)
      val deadlineMs: Long = System.currentTimeMillis() + 1500L

      while (!open.isEmpty() && expanded < 100000 && ((expanded and 63) != 0 || System.currentTimeMillis() < deadlineMs)) {
         val cur: VoxelGroundSolver.Entry = open.poll() as VoxelGroundSolver.Entry
         val var10000: java.lang.Double = closed.get(cur.node.packKey()) as java.lang.Double
         if (!(cur.g > (var10000 ?: java.lang.Double.MAX_VALUE) + 1.0E-6)) {
            expanded++
            if (this.isAtGoal(cur.node, goal)) {
               return this.reconstruct(cur)
            }

            val h: Double = this.heuristic(cur.node, goal)
            if (h < bestNearH) {
               bestNearH = h
               bestNear = cur
            }

            this.expandNeighbors(cur, goal, jumpProfile, open, closed)
         }
      }

      return this.reconstruct(bestNear)
   }

   private fun isAtGoal(n: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node): Boolean {
      val dx: Int = n.x - goal.x
      val dz: Int = n.z - goal.z
      return dx * dx + dz * dz <= 1.5 && Math.abs(n.y - goal.y) <= 1
   }

   private fun heuristic(a: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, b: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node): Double {
      val dx: Double = Math.abs(a.x - b.x)
      val dz: Double = Math.abs(a.z - b.z)
      return Math.max(dx, dz) * 1.0 + (Math.max(dx, dz) - Math.min(dx, dz)) * 0.0 + Math.min(dx, dz) * 0.4139999999999999 + Math.abs(a.feetY - b.feetY) * 0.5
   }

   private fun expandNeighbors(
      cur: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry,
      goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      jumpProfile: VoxelJumpProfile,
      open: PriorityQueue<jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry>,
      closed: HashMap<Long, Double>
   ) {
      for (dir in MOVES) {
         this.tryMove(cur, dir.dx, dir.dz, dir.diag, jumpProfile, goal, open, closed)
      }

      if (jumpProfile.maxSkipCells >= 2) {
         this.expandJumpSkips(cur, goal, jumpProfile, open, closed)
      }
   }

   private fun tryMove(
      cur: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry,
      dx: Int,
      dz: Int,
      diag: Boolean,
      jumpProfile: VoxelJumpProfile,
      goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      open: PriorityQueue<jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry>,
      closed: HashMap<Long, Double>
   ) {
      val baseCost: Double = if (diag) 1.414 else 1.0
      val nx: Int = cur.node.x + dx
      val nz: Int = cur.node.z + dz
      if (!diag || this.diagonalAllowed(cur.node, dx, dz)) {
         val maxDy: Int = RangesKt.coerceAtLeast((int)Math.ceil(jumpProfile.maxClimb), 1)
         var landing: Int = -1
         if (-1 <= maxDy) {
            while (true) {
               val var10000: VoxelBlockCache.StandSurface = this.findStandableAtVoxel(nx, cur.node.y + landing, nz, cur.node.feetY)
               if (var10000 != null && !(var10000.feetY - cur.node.feetY > jumpProfile.maxClimb) && this.headroomClear(cur.node, nx, var10000.feetY, nz)) {
                  this.pushNeighbor(
                     cur,
                     VoxelGroundSolver.Node(nx, var10000.getPos().method_10264(), nz, var10000.feetY),
                     cur.g
                        + (baseCost + (if (landing > 0) 0.30000000000000004 else (if (landing < 0) -0.050000000000000044 else 0.0)))
                        + this.terrainPenalty(nx, nz, var10000.feetY),
                     goal,
                     open,
                     closed
                  )
               }

               if (landing == maxDy) {
                  break
               }

               landing++
            }
         }

         if (!diag) {
            val var23: VoxelBlockCache.StandSurface = this.findFallLandingAtVoxel(nx, nz, cur.node.feetY)
            if (var23 != null && this.fallClear(cur.node, nx, var23.feetY, nz)) {
               this.pushNeighbor(
                  cur,
                  VoxelGroundSolver.Node(nx, var23.getPos().method_10264(), nz, var23.feetY),
                  cur.g + (baseCost + (cur.node.feetY - var23.feetY) * 0.35) + this.terrainPenalty(nx, nz, var23.feetY),
                  goal,
                  open,
                  closed
               )
            }
         }
      }
   }

   private fun expandJumpSkips(
      cur: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry,
      goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      jumpProfile: VoxelJumpProfile,
      open: PriorityQueue<jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry>,
      closed: HashMap<Long, Double>
   ) {
      val maxSkip: Int = jumpProfile.maxSkipCells
      var dx: Int = -maxSkip
      if (-maxSkip <= maxSkip) {
         while (true) {
            var dz: Int = -maxSkip
            if (-maxSkip <= maxSkip) {
               while (true) {
                  if ((dx != 0 || dz != 0) && Math.max(Math.abs(dx), Math.abs(dz)) > 1) {
                     val horizontal: Double = Math.sqrt((double)(dx * dx + dz * dz))
                     if (!(horizontal > jumpProfile.maxHorizontalBlocks)) {
                        val nx: Int = cur.node.x + dx
                        val nz: Int = cur.node.z + dz
                        val var10000: VoxelBlockCache.StandSurface = this.findJumpLandingAtVoxel(nx, nz, cur.node.feetY, jumpProfile)
                        if (var10000 != null) {
                           val deltaY: Double = var10000.feetY - cur.node.feetY
                           if (!(deltaY <= jumpProfile.stepHeight + 0.08) && this.jumpArcClear(cur.node, nx, var10000.feetY, nz, jumpProfile)) {
                              this.pushNeighbor(
                                 cur,
                                 VoxelGroundSolver.Node(nx, var10000.getPos().method_10264(), nz, var10000.feetY),
                                 cur.g + (6.0 + horizontal * 1.8 + deltaY * 1.3 * 2.0) + this.terrainPenalty(nx, nz, var10000.feetY),
                                 goal,
                                 open,
                                 closed
                              )
                           }
                        }
                     }
                  }

                  if (dz == maxSkip) {
                     break
                  }

                  dz++
               }
            }

            if (dx == maxSkip) {
               break
            }

            dx++
         }
      }
   }

   private fun terrainPenalty(x: Int, z: Int, feetY: Double): Double {
      var solidNeighbors: Int = 0
      var cliffNeighbors: Int = 0
      val midY: Double = feetY + 0.9
      val belowFeet: Int = (int)Math.floor(feetY - 0.05)
      var i: Int = 0

      for (var11 in CARDINAL_DX.length..i) {
         val ax: Int = x + CARDINAL_DX[i]
         val az: Int = z + CARDINAL_DZ[i]
         if (!VoxelBlockCache.INSTANCE.isBodyClearAt((double)ax + 0.5, midY, (double)(z + CARDINAL_DZ[i]) + 0.5)) {
            solidNeighbors++
         }

         if (VoxelBlockCache.INSTANCE.isPassable(BlockPos(ax, belowFeet, az))) {
            cliffNeighbors++
         }
      }

      return solidNeighbors * 0.8 + cliffNeighbors * 1.5
   }

   private fun diagonalAllowed(cur: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, dx: Int, dz: Int): Boolean {
      val midY: Double = cur.feetY + 0.9
      val belowFeet: Int = (int)Math.floor(cur.feetY - 0.05)
      val c1Clear: Boolean = VoxelBlockCache.INSTANCE.isBodyClearAt((double)(cur.x + dx) + 0.5, cur.feetY, (double)cur.z + 0.5)
         && VoxelBlockCache.INSTANCE.isBodyClearAt((double)(cur.x + dx) + 0.5, midY, (double)cur.z + 0.5)
         val c2Clear: Boolean = VoxelBlockCache.INSTANCE.isBodyClearAt((double)cur.x + 0.5, cur.feetY, (double)(cur.z + dz) + 0.5)
         && VoxelBlockCache.INSTANCE.isBodyClearAt((double)cur.x + 0.5, midY, (double)(cur.z + dz) + 0.5)
         return (c1Clear || c2Clear)
         && (
            c1Clear && VoxelBlockCache.INSTANCE.isSolid(BlockPos(cur.x + dx, belowFeet, cur.z))
               || c2Clear && VoxelBlockCache.INSTANCE.isSolid(BlockPos(cur.x, belowFeet, cur.z + dz))
         )
      }

   private fun findStandableAtVoxel(x: Int, y: Int, z: Int, anchorFeetY: Double): StandSurface? {
      val `iterator$iv`: java.util.Iterator = VoxelBlockCache.INSTANCE.getStandableSurfaces(x, z, (double)y - 0.1, (double)y + 1.1).iterator()
      val var10000: Any
      if (!`iterator$iv`.hasNext()) {
         var10000 = null
      } else {
         var `minElem$iv`: Any = `iterator$iv`.next()
         if (!`iterator$iv`.hasNext()) {
            var10000 = `minElem$iv`
         } else {
            var var21: Double = Math.abs((`minElem$iv` as VoxelBlockCache.StandSurface).feetY - anchorFeetY)

            do {
               val var22: Any = `iterator$iv`.next()
               val var23: Double = Math.abs((var22 as VoxelBlockCache.StandSurface).feetY - anchorFeetY)
               if (java.lang.Double.compare(var21, var23) > 0) {
                  `minElem$iv` = var22
                  var21 = var23
               }
            } while (`iterator$iv`.hasNext())

            var10000 = `minElem$iv`
         }
      }

      return var10000 as VoxelBlockCache.StandSurface
   }

   private fun findFallLandingAtVoxel(x: Int, z: Int, fromFeetY: Double): StandSurface? {
      return CollectionsKt.firstOrNull(VoxelBlockCache.INSTANCE.getStandableSurfaces(x, z, fromFeetY - (double)64, fromFeetY - 1.1)) as VoxelBlockCache.StandSurface
   }

   private fun findJumpLandingAtVoxel(x: Int, z: Int, fromFeetY: Double, jumpProfile: VoxelJumpProfile): StandSurface? {
      val minFeet: Double = fromFeetY + jumpProfile.stepHeight + 0.08
      val maxFeet: Double = fromFeetY + jumpProfile.maxClimb
      val `iterator$iv`: java.util.Iterator = VoxelBlockCache.INSTANCE.getStandableSurfaces(x, z, minFeet, maxFeet).iterator()
      val var10000: Any
      if (!`iterator$iv`.hasNext()) {
         var10000 = null
      } else {
         var `minElem$iv`: Any = `iterator$iv`.next()
         if (!`iterator$iv`.hasNext()) {
            var10000 = `minElem$iv`
         } else {
            var var20: Double = Math.abs((`minElem$iv` as VoxelBlockCache.StandSurface).feetY - maxFeet)

            do {
               val var21: Any = `iterator$iv`.next()
               val var22: Double = Math.abs((var21 as VoxelBlockCache.StandSurface).feetY - maxFeet)
               if (java.lang.Double.compare(var20, var22) > 0) {
                  `minElem$iv` = var21
                  var20 = var22
               }
            } while (`iterator$iv`.hasNext())

            var10000 = `minElem$iv`
         }
      }

      return var10000 as VoxelBlockCache.StandSurface
   }

   private fun headroomClear(from: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, tx: Int, tFeetY: Double, tz: Int): Boolean {
      return VoxelBlockCache.INSTANCE.isBodyClearAt((double)tx + 0.5, Math.max(from.feetY, tFeetY), (double)tz + 0.5)
   }

   private fun fallClear(from: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, tx: Int, tFeetY: Double, tz: Int): Boolean {
      if (!VoxelBlockCache.INSTANCE
         .isSweepClear((double)from.x + 0.5, from.feetY, (double)from.z + 0.5, (double)tx + 0.5, from.feetY, (double)tz + 0.5, 2, { it: Double ->
            `$from`.feetY
         })) {
         return false
      } else {
         val cx: Double = tx + 0.5
         val cz: Double = tz + 0.5

         // $VF: Unable to resugar Kotlin loop from Java for loop
         var y: Double = from.feetY
         while (true) {
            if (y > tFeetY) break
            if (!VoxelBlockCache.INSTANCE.isBodyClearAt(cx, y, cz)) {
               return false
            }

            y -= 0.5
         }

         return true
      }
   }

   private fun jumpArcClear(from: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, tx: Int, tFeetY: Double, tz: Int, jumpProfile: VoxelJumpProfile): Boolean {
      val dx: Double = tx + 0.5 - (from.x + 0.5)
      val dz: Double = tz + 0.5 - (from.z + 0.5)
      val steps: Int = Math.max(2, (int)Math.ceil(Math.sqrt(dx * dx + dz * dz) / 0.35))
      val baseDelta: Double = tFeetY - from.feetY
      return VoxelBlockCache.INSTANCE
         .isSweepClear((double)from.x + 0.5, from.feetY, (double)from.z + 0.5, (double)tx + 0.5, tFeetY, (double)tz + 0.5, steps, { t: Double ->
            `$from`.feetY + `$baseDelta` * t + 4.0 * `$arcLift` * t * (1.0 - t)
         })
      }

   private fun pushNeighbor(
      cur: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry,
      nb: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      g: Double,
      goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      open: PriorityQueue<jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry>,
      closed: HashMap<Long, Double>
   ) {
      val key: Long = nb.packKey()
      val prev: java.lang.Double = closed.get(key) as java.lang.Double
      if (prev == null || !(prev <= g)) {
         closed.put(key, g)
         open.add(VoxelGroundSolver.Entry(nb, g, g + this.heuristic(nb, goal) * 1.15, cur))
      }
   }

   private fun reconstruct(end: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry): List<class_243> {
      val out: ArrayList = ArrayList()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var cur: VoxelGroundSolver.Entry = end
      while (true) {
         if (cur != null) break
         val n: VoxelGroundSolver.Node = cur.node
         out.add(Vec3d((double)n.x + 0.5, n.feetY, (double)n.z + 0.5))

         cur = cur.parent
      }

      return CollectionsKt.asReversedMutable(out)
   }

   private class Entry(node: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      g: Double,
      f: Double,
      parent: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry?
   ) {
      public final val node: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node
      public final val g: Double
      public final val f: Double
      public final val parent: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry?

      init {
         this.node = node
         this.g = g
         this.f = f
         this.parent = parent
      }
   }

   private data class Move(dx: Int, dz: Int, diag: Boolean) {
      public final val dx: Int
      public final val dz: Int
      public final val diag: Boolean

      init {
         this.dx = dx
         this.dz = dz
         this.diag = diag
      }

      public operator fun component1(): Int {
         return this.dx
      }

      public operator fun component2(): Int {
         return this.dz
      }

      public operator fun component3(): Boolean {
         return this.diag
      }

      public fun copy(dx: Int = this.dx, dz: Int = this.dz, diag: Boolean = this.diag): jooon.pathfinding.voxel.solver.VoxelGroundSolver.Move {
         return VoxelGroundSolver.Move(dx, dz, diag)
      }

      public override fun toString(): String {
         return "Move(dx=${this.dx}, dz=${this.dz}, diag=${this.diag})"
      }

      public override fun hashCode(): Int {
         return (Integer.hashCode(this.dx) * 31 + Integer.hashCode(this.dz)) * 31 + java.lang.Boolean.hashCode(this.diag)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VoxelGroundSolver.Move
               && this.dx == (other as VoxelGroundSolver.Move).dx
               && this.dz == (other as VoxelGroundSolver.Move).dz
               && this.diag == (other as VoxelGroundSolver.Move).diag
            }
      }
   }

   private data class Node(x: Int, y: Int, z: Int, feetY: Double) {
      public final val x: Int
      public final val y: Int
      public final val z: Int
      public final val feetY: Double

      init {
         this.x = x
         this.y = y
         this.z = z
         this.feetY = feetY
      }

      public fun packKey(): Long {
         return (this.x and 67108863L) shl 38 or (this.z and 67108863L) shl 12 or this.y and 4095L
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

      public operator fun component4(): Double {
         return this.feetY
      }

      public fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z, feetY: Double = this.feetY): jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node {
         return VoxelGroundSolver.Node(x, y, z, feetY)
      }

      public override fun toString(): String {
         return "Node(x=${this.x}, y=${this.y}, z=${this.z}, feetY=${this.feetY})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)) * 31 + java.lang.Double.hashCode(this.feetY)
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is VoxelGroundSolver.Node
               && this.x == (other as VoxelGroundSolver.Node).x
               && this.y == (other as VoxelGroundSolver.Node).y
               && this.z == (other as VoxelGroundSolver.Node).z
               && java.lang.Double.compare(this.feetY, (other as VoxelGroundSolver.Node).feetY) == 0
            }
      }
   }
}
