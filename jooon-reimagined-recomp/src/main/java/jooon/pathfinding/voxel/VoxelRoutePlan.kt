package jooon.pathfinding.voxel

import net.minecraft.util.math.Vec3d

public sealed class VoxelRoutePlan protected constructor() {
   abstract val waypoints: List<Vec3d>

   abstract val isEmpty: Boolean

   object Failed : VoxelRoutePlan() {
      open val waypoints: List<Vec3d> = emptyList()
      open val isEmpty: Boolean = true
   }

   data class Ground(waypoints: List<Vec3d>) : VoxelRoutePlan() {
      open val waypoints: List<Vec3d>

      init {
         this.waypoints = waypoints
      }

      open val isEmpty: Boolean
         open get() {
            return this.waypoints.size() < 2
         }


      public operator fun component1(): List<Vec3d> {
         return this.waypoints
      }

      fun copy(waypoints: List<Vec3d> = this.waypoints): jooon.pathfinding.voxel.VoxelRoutePlan.Ground {
         return VoxelRoutePlan.Ground(waypoints)
      }

      override fun toString(): String {
         return "Ground(waypoints=${this.waypoints})"
      }

      override fun hashCode(): Int {
         return this.waypoints.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VoxelRoutePlan.Ground && this.waypoints == (other as VoxelRoutePlan.Ground).waypoints
         }
      }
   }
}
