package jooon.pathfinding.voxel.solver

import java.util.ArrayList
import java.util.HashMap
import java.util.PriorityQueue
import jooon.pathfinding.voxel.VoxelJumpProfile
import jooon.pathfinding.voxel.world.VoxelBlockCache
import jooon.pathfinding.voxel.world.VoxelBlockCache.StandSurface
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object VoxelGroundSolver {
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
   private val CARDINAL_DX: IntArray = intArrayOf(1, -1, 0, 0)
   private val CARDINAL_DZ: IntArray = intArrayOf(0, 0, 1, -1)
   private val MOVES: Array<jooon.pathfinding.voxel.solver.VoxelGroundSolver.Move>

   fun solve(start: Vec3d, goal: Vec3d): MutableList<Vec3d> {
      val var10000: VoxelGroundSolver.Node = this.snapToGround(start)
      label16@
      if (var10000 == null) {
         emptyList()
      } else {
         val var5: VoxelGroundSolver.Node = this.snapToGround(goal)
         if (var5 == null) emptyList() else this.aStar(var10000, var5)
      }
   }

   fun snapToGround(pos: Vec3d): VoxelGroundSolver.Node {
      run label39@{


         val `iterator$iv`: java.util.Iterator = VoxelBlockCache.getStandableSurfaces(bx, bz, pos.y - 2.5, pos.y + 1.5).iterator()
         val var10000: Any
         if (!`iterator$iv`.hasNext()) {
            var10000 = null
         } else {
            var `minElem$iv`: Any = `iterator$iv`.next()
            if (!`iterator$iv`.hasNext()) {
               var10000 = `minElem$iv`
            } else {
               var var16: Double = Math.abs((`minElem$iv` as VoxelBlockCache.StandSurface).feetY - pos.y)

               do {


                  if (java.lang.Double.compare(var16, var18) > 0) {
                     `minElem$iv` = var17
                     var16 = var18
                  }
               } while (`iterator$iv`.hasNext())

               var10000 = `minElem$iv`
            }
         }

         if (var10000 as VoxelBlockCache.StandSurface == null)
return null
return else
            VoxelGroundSolver.Node(bx, (var10000 as VoxelBlockCache.StandSurface).getPos().getY(), bz, (var10000 as VoxelBlockCache.StandSurface).feetY)
         }
   }

   private fun aStar(start: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, goal: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node): List<
return Vec3d
      > {



      val startEntry: VoxelGroundSolver.Entry = VoxelGroundSolver.Entry(start, 0.0, this.heuristic(start, goal), null)
      open.add(startEntry)
      closed.put(start.packKey(), 0.0)
      var expanded: Int = 0
      var bestNear: VoxelGroundSolver.Entry = startEntry
      var bestNearH: Double = this.heuristic(start, goal)


      while (!open.isEmpty() && expanded < 100000 && ((expanded and 63) != 0 || System.currentTimeMillis() < deadlineMs)) {
         val cur: VoxelGroundSolver.Entry = open.poll() as VoxelGroundSolver.Entry

         if (!(cur.g > (var10000 ?: java.lang.Double.MAX_VALUE) + 1.0E-6)) {
            expanded++
            if (this.isAtGoal(cur.node, goal)) {
               return this.reconstruct(cur)
            }

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


      return dx * dx + dz * dz <= 1.5 && Math.abs(n.y - goal.y) <= 1
   }

   private fun heuristic(a: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, b: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node): Double {


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



      if (!diag || this.diagonalAllowed(cur.node, dx, dz)) {

         var landing: Int = -1
         if (-1 <= maxDy) {
            while (true) {
               val var10000: VoxelBlockCache.StandSurface = this.findStandableAtVoxel(nx, cur.node.y + landing, nz, cur.node.feetY)
               if (var10000 != null && !(var10000.feetY - cur.node.feetY > jumpProfile.maxClimb) && this.headroomClear(cur.node, nx, var10000.feetY, nz)) {
                  this.pushNeighbor(
                     cur,
                     VoxelGroundSolver.Node(nx, var10000.getPos().getY(), nz, var10000.feetY),
                     cur.g
                        + (baseCost + (if (landing > 0) 0.30000000000000004 else (if (landing < 0) -0.050000000000000044 else 0.0)))
                        + this.terrainPenalty(nx, nz, var10000.feetY),
                     goal,
                     open,
return closed
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
                  VoxelGroundSolver.Node(nx, var23.getPos().getY(), nz, var23.feetY),
                  cur.g + (baseCost + (cur.node.feetY - var23.feetY) * 0.35) + this.terrainPenalty(nx, nz, var23.feetY),
                  goal,
                  open,
return closed
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

      var dx: Int = -maxSkip
      if (-maxSkip <= maxSkip) {
         while (true) {
            var dz: Int = -maxSkip
            if (-maxSkip <= maxSkip) {
               while (true) {
                  if ((dx != 0 || dz != 0) && Math.max(Math.abs(dx), Math.abs(dz)) > 1) {

                     if (!(horizontal > jumpProfile.maxHorizontalBlocks)) {


                        val var10000: VoxelBlockCache.StandSurface = this.findJumpLandingAtVoxel(nx, nz, cur.node.feetY, jumpProfile)
                        if (var10000 != null) {

                           if (!(deltaY <= jumpProfile.stepHeight + 0.08) && this.jumpArcClear(cur.node, nx, var10000.feetY, nz, jumpProfile)) {
                              this.pushNeighbor(
                                 cur,
                                 VoxelGroundSolver.Node(nx, var10000.getPos().getY(), nz, var10000.feetY),
                                 cur.g + (6.0 + horizontal * 1.8 + deltaY * 1.3 * 2.0) + this.terrainPenalty(nx, nz, var10000.feetY),
                                 goal,
                                 open,
return closed
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


      var i: Int = 0

      for (var11 in CARDINAL_DX.length..i) {


         if (!VoxelBlockCache.isBodyClearAt(ax.toDouble() + 0.5, midY, (z + CARDINAL_DZ[i]).toDouble() + 0.5)) {
            solidNeighbors++
         }

         if (VoxelBlockCache.isPassable(BlockPos(ax, belowFeet, az))) {
            cliffNeighbors++
         }
      }

      return solidNeighbors * 0.8 + cliffNeighbors * 1.5
   }

   private fun diagonalAllowed(cur: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, dx: Int, dz: Int): Boolean {



         && VoxelBlockCache.isBodyClearAt((cur.x + dx).toDouble() + 0.5, midY, cur.z.toDouble() + 0.5)

         && VoxelBlockCache.isBodyClearAt(cur.x.toDouble() + 0.5, midY, (cur.z + dz).toDouble() + 0.5)
         return (c1Clear || c2Clear)
         && (
            c1Clear && VoxelBlockCache.isSolid(BlockPos(cur.x + dx, belowFeet, cur.z))
               || c2Clear && VoxelBlockCache.isSolid(BlockPos(cur.x, belowFeet, cur.z + dz))
         )
      }

   private fun findStandableAtVoxel(x: Int, y: Int, z: Int, anchorFeetY: Double): StandSurface? {
      val `iterator$iv`: java.util.Iterator = VoxelBlockCache.getStandableSurfaces(x, z, y.toDouble() - 0.1, y.toDouble() + 1.1).iterator()
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
      return firstOrNull(VoxelBlockCache.getStandableSurfaces(x, z, fromFeetY - 64.toDouble(), fromFeetY - 1.1)) as VoxelBlockCache.StandSurface
   }

   private fun findJumpLandingAtVoxel(x: Int, z: Int, fromFeetY: Double, jumpProfile: VoxelJumpProfile): StandSurface? {


      val `iterator$iv`: java.util.Iterator = VoxelBlockCache.getStandableSurfaces(x, z, minFeet, maxFeet).iterator()
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
      return VoxelBlockCache.isBodyClearAt(tx.toDouble() + 0.5, Math.max(from.feetY, tFeetY), tz.toDouble() + 0.5)
   }

   private fun fallClear(from: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, tx: Int, tFeetY: Double, tz: Int): Boolean {
      if (!VoxelBlockCache.INSTANCE
         .isSweepClear(from.x.toDouble() + 0.5, from.feetY, from.z.toDouble() + 0.5, tx.toDouble() + 0.5, from.feetY, tz.toDouble() + 0.5, 2, { it: Double ->
            `$from`.feetY
         })) {
         return false
      } else {



         // $VF: Unable to resugar Kotlin loop from Java for loop
         var y: Double = from.feetY
         while (true) {
            if (y > tFeetY) break
            if (!VoxelBlockCache.isBodyClearAt(cx, y, cz)) {
               return false
            }

            y -= 0.5
         }

         return true
      }
   }

   private fun jumpArcClear(from: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node, tx: Int, tFeetY: Double, tz: Int, jumpProfile: VoxelJumpProfile): Boolean {




      return VoxelBlockCache.INSTANCE
         .isSweepClear(from.x.toDouble() + 0.5, from.feetY, from.z.toDouble() + 0.5, tx.toDouble() + 0.5, tFeetY, tz.toDouble() + 0.5, steps, { t: Double ->
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


      if (prev == null || !(prev <= g)) {
         closed.put(key, g)
         open.add(VoxelGroundSolver.Entry(nb, g, g + this.heuristic(nb, goal) * 1.15, cur))
      }
   }

   private fun reconstruct(end: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry): List<Vec3d> {


      // $VF: Unable to resugar Kotlin loop from Java for loop
      var cur: VoxelGroundSolver.Entry = end
      while (true) {
         if (cur != null) break
         val n: VoxelGroundSolver.Node = cur.node
         out.add(Vec3d(n.x.toDouble() + 0.5, n.feetY, n.z.toDouble() + 0.5))

         cur = cur.parent
      }

      return asReversedMutable(out)
   }

   private class Entry(node: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node,
      g: Double,
      f: Double,
      parent: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry?
   ) {
      val node: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node
      val g: Double
      val f: Double
      val parent: jooon.pathfinding.voxel.solver.VoxelGroundSolver.Entry?

      init {
         this.node = node
         this.g = g
         this.f = f
         this.parent = parent
      }
   }

   private data class Move(dx: Int, dz: Int, diag: Boolean) {
      val dx: Int
      val dz: Int
      val diag: Boolean

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

      fun copy(dx: Int = this.dx, dz: Int = this.dz, diag: Boolean = this.diag): jooon.pathfinding.voxel.solver.VoxelGroundSolver.Move {
         return VoxelGroundSolver.Move(dx, dz, diag)
      }

      override fun toString(): String {
         return "Move(dx=${this.dx}, dz=${this.dz}, diag=${this.diag})"
      }

      override fun hashCode(): Int {
         return (Integer.hashCode(this.dx) * 31 + Integer.hashCode(this.dz)) * 31 + java.lang.Boolean.hashCode(this.diag)
      }

      override operator fun equals(other: Any?): Boolean {
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
      val x: Int
      val y: Int
      val z: Int
      val feetY: Double

      init {
         this.x = x
         this.y = y
         this.z = z
         this.feetY = feetY
      }

      fun packKey(): Long {
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

      fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z, feetY: Double = this.feetY): jooon.pathfinding.voxel.solver.VoxelGroundSolver.Node {
         return VoxelGroundSolver.Node(x, y, z, feetY)
      }

      override fun toString(): String {
         return "Node(x=${this.x}, y=${this.y}, z=${this.z}, feetY=${this.feetY})"
      }

      override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)) * 31 + java.lang.Double.hashCode(this.feetY)
      }

      override operator fun equals(other: Any?): Boolean {
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
