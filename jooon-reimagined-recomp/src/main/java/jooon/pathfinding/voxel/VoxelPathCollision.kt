package jooon.pathfinding.voxel

import jooon.pathfinding.voxel.world.VoxelBlockCache
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.BlockPos.Mutable
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.shape.VoxelShape

internal object VoxelPathCollision {
   fun isSegmentStillClear(from: Vec3d, to: Vec3d): Boolean {




      if (dist < 0.5) {
return true
      } else {


         for (i in 1..steps) {

            if (!this.isBodyClearAt(sample.x, sample.y, sample.z)) {
return false
            }
         }
return true
      }
   }

   fun quickLineOfSight(from: Vec3d, to: Vec3d): Boolean {



      if (dist < 0.5) {
return true
      } else {






         for (var27 in 1..steps) {


            if (var10000 == null) {
return false
            }


            for (offset in offsets) {
               if (!this.isBodyClearAt(sample.x + perpX * offset, groundY, sample.z + perpZ * offset)) {
return false
               }
            }
         }
return true
      }
   }

   fun samplePoint(from: Vec3d, dx: Double, dy: Double, dz: Double, t: Double): Vec3d {
      Vec3d(from.x + dx * t, from.y + dy * t, from.z + dz * t)
   }

   private fun findGroundY(x: Double, approxY: Double, z: Double): Double? {




      for (yOff in 2 downTo -2) {


         if (!shape.isEmpty()) {

            if (top <= approxY + 1.5 && approxY - 2.0 <= top) {
               return top
            }
         }
      }

      return null
   }

   private fun isBodyClearAt(x: Double, feetY: Double, z: Double): Boolean {





      var by: Int = minBlockY
      if (minBlockY <= maxBlockY) {
         while (true) {

            if (!shape.isEmpty() && by + shape.getMax(Axis.Y) > feetY + 0.01 && by + shape.getMin(Axis.Y) < feetY + 1.8) {
               return false
            }

            if (by == maxBlockY) {
break
            }

            by++
         }
      }

      return true
   }

   fun collisionShapeAt(cursor: Mutable, x: Int, y: Int, z: Int): VoxelShape {


      var10000.getCollisionShape(var10001 as BlockPos)
   }
}
