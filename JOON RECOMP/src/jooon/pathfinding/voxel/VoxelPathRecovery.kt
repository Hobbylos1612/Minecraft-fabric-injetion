package jooon.pathfinding.voxel

import kotlin.random.Random
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d

internal class VoxelPathRecovery {
   private final var lastCursor: Int
   private final var noProgressTicks: Int
   private final var positionStuckTicks: Int
   private Vec3d lastPosForStuck;
   private final var inRecovery: Boolean
   private final var recoveryTicks: Int
   private final var recoveryStrafeRight: Boolean = true
   private final var offPathTicks: Int

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun reset() {
      this.lastCursor = 0
      this.noProgressTicks = 0
      this.positionStuckTicks = 0
      this.lastPosForStuck = null
      this.inRecovery = false
      this.recoveryTicks = 0
      this.offPathTicks = 0
   }

   public fun clearForFall() {
      this.inRecovery = false
      this.noProgressTicks = 0
      this.positionStuckTicks = 0
      this.offPathTicks = 0
   }

   fun tick(pos: Vec3d, isSky: Boolean, cursor: Int, waypoints: MutableList<Vec3d>, repathInFlight: Boolean): VoxelPathRecovery.Decision {
      if (!isSky) {
         val var10000: ClientPlayerEntity = this.getMc().field_1724
         if (var10000 != null && !var10000.method_24828()) {
            this.noProgressTicks = 0
            this.positionStuckTicks = 0
            this.lastPosForStuck = pos
            this.clearRecoveryInputs()
            VoxelPathRecovery.Decision.None.INSTANCE as VoxelPathRecovery.Decision
         }
      }

      if (!repathInFlight) {
         if (this.isOffPath(pos, cursor, waypoints)) {
            val stuck: Int = this.nearestReachableWaypoint(pos, cursor, waypoints)
            if (stuck != null) {
               this.offPathTicks = 0
               VoxelPathRecovery.Decision.SkipTo(stuck) as VoxelPathRecovery.Decision
            }

            val veryStuck: Int = this.offPathTicks++
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
         val var14: Int = this.noProgressTicks++
         if (this.lastPosForStuck != null) {
            if ((pos.field_1352 - this.lastPosForStuck.field_1352) * (pos.field_1352 - this.lastPosForStuck.field_1352)
                  + (pos.field_1350 - this.lastPosForStuck.field_1350) * (pos.field_1350 - this.lastPosForStuck.field_1350)
               < 0.04) {
               val var13: Int = this.positionStuckTicks++
            } else {
               this.positionStuckTicks = Math.max(0, this.positionStuckTicks - 2)
            }
         }

         this.lastPosForStuck = pos
         val var17: Boolean = this.noProgressTicks >= 40 && this.positionStuckTicks >= 20
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

   public fun applyRecoveryInputsIfNeeded(): Boolean {
      if (!this.inRecovery) {
         return false
      } else {
         VoxelPathInput.INSTANCE.releaseAll()
         val phase: Int = this.recoveryTicks++
         if (this.recoveryTicks <= 15) {
            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.BACKWARD)
         } else {
            if ((this.recoveryTicks - 15) % 10 == 0) {
               this.recoveryStrafeRight = !this.recoveryStrafeRight
            }

            if (this.recoveryStrafeRight) {
               VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.RIGHT)
            } else {
               VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.LEFT)
            }

            VoxelPathInput.INSTANCE.press(VoxelPathInput.MoveAction.FORWARD)
         }

         return true
      }
   }

   private fun clearRecoveryInputs() {
      if (this.inRecovery) {
         this.inRecovery = false
         this.recoveryTicks = 0
         VoxelPathInput.INSTANCE.release(VoxelPathInput.MoveAction.LEFT)
         VoxelPathInput.INSTANCE.release(VoxelPathInput.MoveAction.RIGHT)
         VoxelPathInput.INSTANCE.release(VoxelPathInput.MoveAction.BACKWARD)
      }
   }

   fun nearestReachableWaypoint(pos: Vec3d, cursor: Int, waypoints: MutableList<Vec3d>): Int {
      if (cursor >= waypoints.size()) {
         null
      } else {
         val maxLook: Int = Math.min(cursor + 6, CollectionsKt.getLastIndex(waypoints))
         var bestIdx: Int = -1
         var bestDistSq: Double = 25.0
         var i: Int = cursor + 1
         if (cursor + 1 <= maxLook) {
            while (true) {
               val wp: Vec3d = waypoints.get(i) as Vec3d
               val dx: Double = pos.field_1352 - wp.field_1352
               val dz: Double = pos.field_1350 - wp.field_1350
               if (!(Math.abs(pos.field_1351 - wp.field_1351) > 4.0)) {
                  val d: Double = dx * dx + dz * dz
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
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if ((var10000 == null || var10000.method_24828()) && cursor < waypoints.size()) {
         val deviation: VoxelPathFollowMath.SegmentDeviation = VoxelPathFollowMath.INSTANCE.routeDeviation(waypoints, cursor, pos, 3, 8)
         if (deviation != null) {
            deviation.lateral > 3.5 || Math.abs(deviation.verticalBelow) > 4.0
         } else {
            val tgt: Vec3d = waypoints.get(cursor) as Vec3d
            (pos.field_1352 - tgt.field_1352) * (pos.field_1352 - tgt.field_1352) + (pos.field_1350 - tgt.field_1350) * (pos.field_1350 - tgt.field_1350)
                  > 25.0
               || Math.abs(pos.field_1351 - tgt.field_1351) > 4.0
            }
      } else {
         false
      }
   }

   public sealed class Decision protected constructor() {
      public data object None : VoxelPathRecovery.Decision() {
         public override fun toString(): String {
            return "None"
         }

         public override fun hashCode(): Int {
            return -1210389019
         }

         public override operator fun equals(other: Any?): Boolean {
            return this === other || other is VoxelPathRecovery.Decision.None
         }
      }

      public data object OffPath : VoxelPathRecovery.Decision() {
         public override fun toString(): String {
            return "OffPath"
         }

         public override fun hashCode(): Int {
            return -1826890649
         }

         public override operator fun equals(other: Any?): Boolean {
            return this === other || other is VoxelPathRecovery.Decision.OffPath
         }
      }

      public data class SkipTo(cursor: Int) : VoxelPathRecovery.Decision() {
         public final val cursor: Int

         init {
            this.cursor = cursor
         }

         public operator fun component1(): Int {
            return this.cursor
         }

         public fun copy(cursor: Int = this.cursor): jooon.pathfinding.voxel.VoxelPathRecovery.Decision.SkipTo {
            return VoxelPathRecovery.Decision.SkipTo(cursor)
         }

         public override fun toString(): String {
            return "SkipTo(cursor=${this.cursor})"
         }

         public override fun hashCode(): Int {
            return Integer.hashCode(this.cursor)
         }

         public override operator fun equals(other: Any?): Boolean {
            label22@
            if (this === other) {
               return true
            } else {
               return other is VoxelPathRecovery.Decision.SkipTo && this.cursor == (other as VoxelPathRecovery.Decision.SkipTo).cursor
            }
         }
      }

      public data object Stuck : VoxelPathRecovery.Decision() {
         public override fun toString(): String {
            return "Stuck"
         }

         public override fun hashCode(): Int {
            return 1137419407
         }

         public override operator fun equals(other: Any?): Boolean {
            return this === other || other is VoxelPathRecovery.Decision.Stuck
         }
      }
   }
}
