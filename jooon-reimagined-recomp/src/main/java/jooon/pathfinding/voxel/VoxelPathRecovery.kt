package jooon.pathfinding.voxel

import kotlin.random.Random
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d

internal class VoxelPathRecovery {
   private var lastCursor: Int
   private var noProgressTicks: Int
   private var positionStuckTicks: Int
   private Vec3d lastPosForStuck;
   private var inRecovery: Boolean
   private var recoveryTicks: Int
   private var recoveryStrafeRight: Boolean = true
   private var offPathTicks: Int

   fun getMc(): MinecraftClient {
return var10000
   }

   fun reset() {
      this.lastCursor = 0
      this.noProgressTicks = 0
      this.positionStuckTicks = 0
      this.lastPosForStuck = null
      this.inRecovery = false
      this.recoveryTicks = 0
      this.offPathTicks = 0
   }

   fun clearForFall() {
      this.inRecovery = false
      this.noProgressTicks = 0
      this.positionStuckTicks = 0
      this.offPathTicks = 0
   }

   fun tick(pos: Vec3d, isSky: Boolean, cursor: Int, waypoints: MutableList<Vec3d>, repathInFlight: Boolean): VoxelPathRecovery.Decision {
      if (!isSky) {

         if (var10000 != null && !var10000.isOnGround()) {
            this.noProgressTicks = 0
            this.positionStuckTicks = 0
            this.lastPosForStuck = pos
            this.clearRecoveryInputs()
            VoxelPathRecovery.Decision.None.INSTANCE as VoxelPathRecovery.Decision
         }
      }

      if (!repathInFlight) {
         if (this.isOffPath(pos, cursor, waypoints)) {

            if (stuck != null) {
               this.offPathTicks = 0
               VoxelPathRecovery.Decision.SkipTo(stuck) as VoxelPathRecovery.Decision
            }

            if (this.offPathTicks >= 30) {
               this.offPathTicks = 0
               VoxelPathRecovery.Decision.OffPath.INSTANCE as VoxelPathRecovery.Decision
            }
         } else {
            this.offPathTicks = 0
         }
      }

      if (cursor != this.lastCursor) {
         this.lastCursor = cursor
         this.noProgressTicks = 0
         this.positionStuckTicks = 0
         this.lastPosForStuck = null
         this.clearRecoveryInputs()
         VoxelPathRecovery.Decision.None.INSTANCE as VoxelPathRecovery.Decision
      } else {

         if (this.lastPosForStuck != null) {
            if ((pos.x - this.lastPosForStuck.x) * (pos.x - this.lastPosForStuck.x)
                  + (pos.z - this.lastPosForStuck.z) * (pos.z - this.lastPosForStuck.z)
               < 0.04) {

            } else {
               this.positionStuckTicks = Math.max(0, this.positionStuckTicks - 2)
            }
         }

         this.lastPosForStuck = pos

         if (this.noProgressTicks >= 100 && this.positionStuckTicks >= 50 && !repathInFlight) {
            VoxelPathRecovery.Decision.Stuck.INSTANCE as VoxelPathRecovery.Decision
         } else {
            if (var17 && !this.inRecovery) {
               this.inRecovery = true
               this.recoveryTicks = 0
               this.recoveryStrafeRight = Random.Default.nextBoolean()
            }

            VoxelPathRecovery.Decision.None.INSTANCE as VoxelPathRecovery.Decision
         }
      }
   }

   fun applyRecoveryInputsIfNeeded(): Boolean {
      if (!this.inRecovery) {
         return false
      } else {
         VoxelPathInput.releaseAll()

         if (this.recoveryTicks <= 15) {
            VoxelPathInput.press(VoxelPathInput.MoveAction.BACKWARD)
         } else {
            if ((this.recoveryTicks - 15) % 10 == 0) {
               this.recoveryStrafeRight = !this.recoveryStrafeRight
            }

            if (this.recoveryStrafeRight) {
               VoxelPathInput.press(VoxelPathInput.MoveAction.RIGHT)
            } else {
               VoxelPathInput.press(VoxelPathInput.MoveAction.LEFT)
            }

            VoxelPathInput.press(VoxelPathInput.MoveAction.FORWARD)
         }

         return true
      }
   }

   private fun clearRecoveryInputs() {
      if (this.inRecovery) {
         this.inRecovery = false
         this.recoveryTicks = 0
         VoxelPathInput.release(VoxelPathInput.MoveAction.LEFT)
         VoxelPathInput.release(VoxelPathInput.MoveAction.RIGHT)
         VoxelPathInput.release(VoxelPathInput.MoveAction.BACKWARD)
      }
   }

   fun nearestReachableWaypoint(pos: Vec3d, cursor: Int, waypoints: MutableList<Vec3d>): Int {
      if (cursor >= waypoints.size()) {
return null
      } else {

         var bestIdx: Int = -1
         var bestDistSq: Double = 25.0
         var i: Int = cursor + 1
         if (cursor + 1 <= maxLook) {
            while (true) {



               if (!(Math.abs(pos.y - wp.y) > 4.0)) {

                  if (dx * dx + dz * dz < bestDistSq) {
                     bestDistSq = d
                     bestIdx = i
                  }
               }

               if (i == maxLook) {
break
               }

               i++
            }
         }

         if (bestIdx > cursor) bestIdx else null
      }
   }

   fun isOffPath(pos: Vec3d, cursor: Int, waypoints: MutableList<Vec3d>): Boolean {

      if ((var10000 == null || var10000.isOnGround()) && cursor < waypoints.size()) {
         val deviation: VoxelPathFollowMath.SegmentDeviation = VoxelPathFollowMath.routeDeviation(waypoints, cursor, pos, 3, 8)
         if (deviation != null) {
            deviation.lateral > 3.5 || Math.abs(deviation.verticalBelow) > 4.0
         } else {

            (pos.x - tgt.x) * (pos.x - tgt.x) + (pos.z - tgt.z) * (pos.z - tgt.z)
                  > 25.0
               || Math.abs(pos.y - tgt.y) > 4.0
            }
      } else {
return false
      }
   }

   public sealed class Decision protected constructor() {
      data object None : VoxelPathRecovery.Decision() {
         override fun toString(): String {
            return "None"
         }

         override fun hashCode(): Int {
            return -1210389019
         }

         override operator fun equals(other: Any?): Boolean {
            return this === other || other is VoxelPathRecovery.Decision.None
         }
      }

      data object OffPath : VoxelPathRecovery.Decision() {
         override fun toString(): String {
            return "OffPath"
         }

         override fun hashCode(): Int {
            return -1826890649
         }

         override operator fun equals(other: Any?): Boolean {
            return this === other || other is VoxelPathRecovery.Decision.OffPath
         }
      }

      data class SkipTo(cursor: Int) : VoxelPathRecovery.Decision() {
         val cursor: Int

         init {
            this.cursor = cursor
         }

         public operator fun component1(): Int {
            return this.cursor
         }

         fun copy(cursor: Int = this.cursor): jooon.pathfinding.voxel.VoxelPathRecovery.Decision.SkipTo {
            return VoxelPathRecovery.Decision.SkipTo(cursor)
         }

         override fun toString(): String {
            return "SkipTo(cursor=${this.cursor})"
         }

         override fun hashCode(): Int {
            return Integer.hashCode(this.cursor)
         }

         override operator fun equals(other: Any?): Boolean {
            label22@
            if (this === other) {
               return true
            } else {
               return other is VoxelPathRecovery.Decision.SkipTo && this.cursor == (other as VoxelPathRecovery.Decision.SkipTo).cursor
            }
         }
      }

      data object Stuck : VoxelPathRecovery.Decision() {
         override fun toString(): String {
            return "Stuck"
         }

         override fun hashCode(): Int {
            return 1137419407
         }

         override operator fun equals(other: Any?): Boolean {
            return this === other || other is VoxelPathRecovery.Decision.Stuck
         }
      }
   }
}
