package jooon.pathfinding.voxel

import net.minecraft.class_243

public sealed class VoxelRoutePlan protected constructor() {
   public abstract val waypoints: List<class_243>

   public abstract val isEmpty: Boolean

   public object Failed : VoxelRoutePlan() {
      public open val waypoints: List<class_243> = CollectionsKt.emptyList()
      public open val isEmpty: Boolean = true
   }

   public data class Ground(waypoints: List<class_243>) : VoxelRoutePlan() {
      public open val waypoints: List<class_243>

      init {
         this.waypoints = waypoints
      }

      public open val isEmpty: Boolean
         public open get() {
            return this.waypoints.size() < 2
         }


      public operator fun component1(): List<class_243> {
         return this.waypoints
      }

      public fun copy(waypoints: List<class_243> = this.waypoints): jooon.pathfinding.voxel.VoxelRoutePlan.Ground {
         return VoxelRoutePlan.Ground(waypoints)
      }

      public override fun toString(): String {
         return "Ground(waypoints=${this.waypoints})"
      }

      public override fun hashCode(): Int {
         return this.waypoints.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is VoxelRoutePlan.Ground && this.waypoints == (other as VoxelRoutePlan.Ground).waypoints
         }
      }
   }
}
