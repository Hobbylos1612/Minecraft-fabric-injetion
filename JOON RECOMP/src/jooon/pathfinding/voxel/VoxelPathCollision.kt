package jooon.pathfinding.voxel

import jooon.pathfinding.voxel.world.VoxelBlockCache
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.BlockPos.Mutable
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.shape.VoxelShape

internal object VoxelPathCollision {
   fun isSegmentStillClear(from: Vec3d, to: Vec3d): Boolean {
      val dx: Double = to.field_1352 - from.field_1352
      val dy: Double = to.field_1351 - from.field_1351
      val dz: Double = to.field_1350 - from.field_1350
      val dist: Double = Math.sqrt(dx * dx + (to.field_1350 - from.field_1350) * (to.field_1350 - from.field_1350))
      if (dist < 0.5) {
         true
      } else {
         val steps: Int = Math.max(2, (int)Math.ceil(dist / 0.8))

         for (i in 1..steps) {
            val sample: Vec3d = this.samplePoint(from, dx, dy, dz, (double)i / (double)steps)
            if (!this.isBodyClearAt(sample.field_1352, sample.field_1351, sample.field_1350)) {
               false
            }
         }

         true
      }
   }

   fun quickLineOfSight(from: Vec3d, to: Vec3d): Boolean {
      val dx: Double = to.field_1352 - from.field_1352
      val dz: Double = to.field_1350 - from.field_1350
      val dist: Double = Math.sqrt(dx * dx + (to.field_1350 - from.field_1350) * (to.field_1350 - from.field_1350))
      if (dist < 0.5) {
         true
      } else {
         val perpX: Double = -dz / dist
         val perpZ: Double = dx / dist
         val steps: Int = (int)Math.ceil(dist / 0.3)
         val dy: Double = to.field_1351 - from.field_1351
         val offsets: DoubleArray = doubleArrayOf(0.0, -0.3, 0.3)

         for (var27 in 1..steps) {
            val sample: Vec3d = this.samplePoint(from, dx, dy, dz, (double)var27 / (double)steps)
            val var10000: java.lang.Double = this.findGroundY(sample.field_1352, sample.field_1351, sample.field_1350)
            if (var10000 == null) {
               false
            }

            val groundY: Double = var10000

            for (offset in offsets) {
               if (!this.isBodyClearAt(sample.field_1352 + perpX * offset, groundY, sample.field_1350 + perpZ * offset)) {
                  false
               }
            }
         }

         true
      }
   }

   fun samplePoint(from: Vec3d, dx: Double, dy: Double, dz: Double, t: Double): Vec3d {
      Vec3d(from.field_1352 + dx * t, from.field_1351 + dy * t, from.field_1350 + dz * t)
   }

   private fun findGroundY(x: Double, approxY: Double, z: Double): Double? {
      val bx: Int = (int)Math.floor(x)
      val bz: Int = (int)Math.floor(z)
      val cursor: Mutable = Mutable()

      for (yOff in 2 downTo -2) {
         val by: Int = (int)Math.floor(approxY - 0.05 + (double)yOff)
         val shape: VoxelShape = this.collisionShapeAt(cursor, bx, by, bz)
         if (!shape.method_1110()) {
            val top: Double = by + shape.method_1105(Axis.field_11052)
            if (top <= approxY + 1.5 && approxY - 2.0 <= top) {
               return top
            }
         }
      }

      return null
   }

   private fun isBodyClearAt(x: Double, feetY: Double, z: Double): Boolean {
      val bx: Int = (int)Math.floor(x)
      val bz: Int = (int)Math.floor(z)
      val minBlockY: Int = (int)Math.floor(feetY)
      val maxBlockY: Int = (int)Math.floor(feetY + 1.8)
      val cursor: Mutable = Mutable()
      var by: Int = minBlockY
      if (minBlockY <= maxBlockY) {
         while (true) {
            val shape: VoxelShape = this.collisionShapeAt(cursor, bx, by, bz)
            if (!shape.method_1110() && by + shape.method_1105(Axis.field_11052) > feetY + 0.01 && by + shape.method_1091(Axis.field_11052) < feetY + 1.8) {
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
      val var10000: VoxelBlockCache = VoxelBlockCache.INSTANCE
      val var10001: Mutable = cursor.method_10103(x, y, z)
      var10000.getCollisionShape(var10001 as BlockPos)
   }
}
