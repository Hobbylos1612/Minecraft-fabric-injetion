package jooon.pathfinding.voxel.world

import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentHashMap.KeySetView
import kotlin.math.MathKt
import net.minecraft.block.BlockState
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.Heightmap.Type
import net.minecraft.world.chunk.ChunkSection
import net.minecraft.world.chunk.WorldChunk

object VoxelBlockCache {
   private val cache: ConcurrentHashMap<Long, BlockState> = ConcurrentHashMap()
   private val supportTopCache: ConcurrentHashMap<Long, List<Double>> = ConcurrentHashMap()
   private val scannedChunks: KeySetView<Long, Boolean> = ConcurrentHashMap.newKeySet()
   private val columnSurfacesCache: ConcurrentHashMap<
      jooon.pathfinding.voxel.world.VoxelBlockCache.ColumnRangeKey,
      List<jooon.pathfinding.voxel.world.VoxelBlockCache.StandSurface>
   > = ConcurrentHashMap()
   public const val STEP_HEIGHT: Double = 0.6
   public const val MAX_JUMP_RISE: Double = 1.25
   public const val PLAYER_WIDTH: Double = 0.6
   public const val PLAYER_HEIGHT: Double = 1.8
   public const val PLAYER_HALF_WIDTH: Double = 0.3
   private const val CENTER_X: Double = 0.5
   private const val CENTER_Z: Double = 0.5
   private const val BODY_EPSILON: Double = 0.001
   private const val SUPPORT_EPSILON: Double = 0.05
   private const val HORIZONTAL_MARGIN: Double = 0.001
   private val BODY_OFFSETS: List<Pair<Double, Double>> =
      listOf(arrayOf(Pair(0.5, 0.5), Pair(0.3, 0.3), Pair(0.7, 0.3), Pair(0.3, 0.7), Pair(0.7, 0.7)))

   fun getMc(): MinecraftClient {
return var10000
   }

   fun getBlockState(pos: BlockPos): BlockState {


      if (world != null) {
return world
      } else {

         if (var10000 == null) {
return var12
         } else if (!var10000.isChunkLoaded(pos.getX() shr 4, pos.getZ() shr 4)) {
return var11
         } else {

            cache.put(key, var10)
            scannedChunks.add(this.chunkKey(pos.getX() shr 4, pos.getZ() shr 4))
return var10
         }
      }
   }

   private fun chunkKey(cx: Int, cz: Int): Long {
      return cx.toLong() shl 32 or cz and 4294967295L
   }

   private fun columnKey(x: Int, z: Int): Long {
      return x.toLong() shl 32 or z and 4294967295L
   }

   fun isChunkAvailable(x: Int, z: Int): Boolean {
      return this.isChunkLoaded(x, z) || scannedChunks.contains(this.chunkKey(x shr 4, z shr 4))
   }

   fun getCollisionShape(pos: BlockPos): VoxelShape {

      if (var10000 == null) {
return var4
      } else {
return var3
      }
   }

   fun getCollisionHeight(pos: BlockPos): Double {

      if (shape.isEmpty()) 0.0 else shape.getBoundingBox().maxY
   }

   fun getSupportTopYs(pos: BlockPos): MutableList<Double> {

      val shape: java.util.List = supportTopCache.get(key)
      if (shape != null) {
         val var27: java.lang.Iterable = shape
         val var28: java.util.Collection = ArrayList(shape.count().coerceAtLeast(10))

         for (`item$iv$iv` in var27) {
            var28.add(pos.getY().toDouble() + (`item$iv$iv` as java.lang.Number).doubleValue())
         }

         var28 as java.util.List
      } else {

         if (var20.isEmpty()) {
            supportTopCache.put(key, emptyList())
            emptyList()
         } else {
            val var10000: java.util.List = var20.getBoundingBoxes()
            val localTopsx: java.util.List = toList(
               sortedDescending(distinct(map(asSequence(var10000), { it: Box ->
                  (it.maxY * 16.0).roundToInt() / 16.0
               })))
            )
            supportTopCache.put(key, localTopsx)
            val `this$iv$iv`: java.lang.Iterable = localTopsx
            val `destination$iv$iv`: java.util.Collection = ArrayList(localTopsx.count().coerceAtLeast(10))

            for (`item$iv$iv` in `this$iv$iv`) {
               `destination$iv$iv`.add(pos.getY().toDouble() + (`item$iv$iv` as java.lang.Number).doubleValue())
            }

            `destination$iv$iv` as java.util.List
         }
      }
   }

   fun getSupportTopY(pos: BlockPos): Double? {
      firstOrNull(this.getSupportTopYs(pos)) as Double
   }

   fun quantizeFeetOffset(pos: BlockPos, feetY: Double): Int {
      ((feetY - pos.getY().toDouble()) * 16.0).roundToInt()
   }

   fun buildPlayerBox(centerX: Double, feetY: Double, centerZ: Double): Box {
      Box(centerX - 0.3 + 0.001, feetY + 0.001, centerZ - 0.3 + 0.001, centerX + 0.3 - 0.001, feetY + 1.8 - 0.001, centerZ + 0.3 - 0.001)
   }

   fun hasBlockCollision(box: Box): Boolean {

      var10000 != null && var10000.getBlockCollisions(null, box).iterator().hasNext()
   }

   fun isBodyClearAt(centerX: Double, feetY: Double, centerZ: Double): Boolean {
      return !this.hasBlockCollision(this.buildPlayerBox(centerX, feetY, centerZ))
   }

   fun isStandable(pos: BlockPos, feetY: Double): Boolean {
      if (Math.floor(feetY + 0.001).toInt() != pos.getY()) {
return false
      } else {
         for (var5 in BODY_OFFSETS) {

               pos.getX().toDouble() + (var5.component1() as java.lang.Number).doubleValue(),
               feetY,
               pos.getZ().toDouble() + (var5.component2() as java.lang.Number).doubleValue()
            )
            if (!this.hasBlockCollision(bodyBox)
               && this.hasBlockCollision(Box(bodyBox.minX, feetY - 0.05, bodyBox.minZ, bodyBox.maxX, feetY + 0.001, bodyBox.maxZ))) {
return true
            }
         }
return false
      }
   }

   fun resolveStandingSurface(pos: BlockPos): VoxelBlockCache.StandSurface? {


      for (`element$iv` in this.getSupportTopYs(pos)) {

         if (p0 - pos.getY() <= 0.601) {
            candidates.add(p0)
         }
      }


      for (var15 in this.getSupportTopYs(var10001)) {
         candidates.add(var15 as Double)
      }

      val var11: java.util.Iterator = sortedDescending(candidates).iterator()

      while (var11.hasNext()) {

         if (this.isStandable(pos, var13)) {
            VoxelBlockCache.StandSurface(pos, var13)
         }
      }
return null
   }

   fun isChunkLoaded(x: Int, z: Int): Boolean {

      return var10000 != null && var10000.isChunkLoaded(x shr 4, z shr 4)
   }

   fun getStandableSurfaces(x: Int, z: Int, minFeetY: Double, maxFeetY: Double): List<jooon.pathfinding.voxel.world.VoxelBlockCache.StandSurface> {
      if (maxFeetY + 0.001 < minFeetY) {
         return emptyList()
      } else if (!this.isChunkAvailable(x, z)) {
         return emptyList()
      } else {

         var maxSupportY: Int = Math.floor(maxFeetY).toInt() + 1


         if (chunk != null) {

            if (cacheKey < maxSupportY) {
               maxSupportY = cacheKey
            }
         }

         if (maxSupportY < minSupportY) {
            return emptyList()
         } else {
            val var24: VoxelBlockCache.ColumnRangeKey = VoxelBlockCache.ColumnRangeKey(x, z, minSupportY, maxSupportY)
            var surfaces: java.util.List = columnSurfacesCache.get(var24)
            if (surfaces != null) {
               return surfaces
            } else {
               surfaces = ArrayList()

               val sections: Array<ChunkSection> = if (chunk != null) chunk.getSectionArray() else null

               var y: Int = maxSupportY

               while (y >= minSupportY) {
                  val `this$iv`: Int = y - chunkMinY shr 4
                  if (sections != null && 0 <= y - chunkMinY shr 4 && y - chunkMinY shr 4 < sections.length && sections[y - chunkMinY shr 4].isEmpty()) {
                     y = chunkMinY + (`this$iv` shl 4) - 1
                  } else {
                     val var19: java.util.Iterator = this.getSupportTopYs(BlockPos(x, y, z)).iterator()

                     while (var19.hasNext()) {

                        if (!(topY + 0.001 < minFeetY) && !(topY - 0.001 > maxFeetY)) {

                           if (this.isStandable(feetPos, topY) && seen.add(Pair(feetPos.asLong(), this.quantizeFeetOffset(feetPos, topY)))) {
                              surfaces.add(VoxelBlockCache.StandSurface(feetPos, topY))
                           }
                        }
                     }

                     y--
                  }
               }

               if (surfaces.size() > 1) {
                  sortWith(surfaces, VoxelBlockCache$getStandableSurfaces$$inlined$sortByDescending$1())
               }

               columnSurfacesCache.put(var24, surfaces)
               return surfaces
            }
         }
      }
   }

   fun isSweepClear(fromX: Double, fromFeetY: Double, fromZ: Double, toX: Double, toFeetY: Double, toZ: Double, steps: Int, yAt: (Double) -> Double): Boolean {
      if (steps <= 0) {
         return true
      } else {
         var i: Int = 0
         if (0 <= steps) {
            while (true) {
               if (!this.isBodyClearAt(
                  fromX + (toX - fromX) * (i.toDouble() / steps.toDouble()),
                  (yAt(i.toDouble() / steps.toDouble()) as java.lang.Number).doubleValue(),
                  fromZ + (toZ - fromZ) * (i.toDouble() / steps.toDouble())
               )) {
                  return false
               }

               if (i == steps) {
break
               }

               i++
            }
         }

         return true
      }
   }

   fun isPassable(pos: BlockPos): Boolean {
      this.getCollisionHeight(pos) == 0.0
   }

   fun isSteppable(pos: BlockPos): Boolean {
      this.getCollisionHeight(pos) <= 0.6
   }

   fun isSolid(pos: BlockPos): Boolean {
      this.getCollisionHeight(pos) > 0.0
   }

   fun isWalkable(pos: BlockPos): Boolean {

      var var10001: BlockPos = pos.up()

      var10001 = pos.down()
      feetClear && headClear && this.isSolid(var10001)
   }

   fun clear() {
      cache.clear()
      supportTopCache.clear()
      scannedChunks.clear()
      columnSurfacesCache.clear()
   }

   fun invalidate(pos: BlockPos) {
      cache.remove(pos.asLong())
      supportTopCache.remove(pos.asLong())
      (columnSurfacesCache.keySet() as KeySetView).removeIf({ p0: Any ->
         ``(p0)
      })
   }

   fun onWorldClear() {
      this.clear()
   }

   private data class ColumnRangeKey(x: Int, z: Int, minY: Int, maxY: Int) {
      val x: Int
      val z: Int
      val minY: Int
      val maxY: Int

      init {
         this.x = x
         this.z = z
         this.minY = minY
         this.maxY = maxY
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.z
      }

      public operator fun component3(): Int {
         return this.minY
      }

      public operator fun component4(): Int {
         return this.maxY
      }

      fun copy(x: Int = this.x, z: Int = this.z, minY: Int = this.minY, maxY: Int = this.maxY): jooon.pathfinding.voxel.world.VoxelBlockCache.ColumnRangeKey {
         return VoxelBlockCache.ColumnRangeKey(x, z, minY, maxY)
      }

      override fun toString(): String {
         return "ColumnRangeKey(x=${this.x}, z=${this.z}, minY=${this.minY}, maxY=${this.maxY})"
      }

      override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)) * 31 + Integer.hashCode(this.minY)) * 31 + Integer.hashCode(this.maxY)
      }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is VoxelBlockCache.ColumnRangeKey
               && this.x == (other as VoxelBlockCache.ColumnRangeKey).x
               && this.z == (other as VoxelBlockCache.ColumnRangeKey).z
               && this.minY == (other as VoxelBlockCache.ColumnRangeKey).minY
               && this.maxY == (other as VoxelBlockCache.ColumnRangeKey).maxY
            }
      }
   }

   data class StandSurface {
      private BlockPos pos;
      val feetY: Double

      fun StandSurface(pos: BlockPos, feetY: Double) {
         this.pos = pos
         this.feetY = feetY
      }

      fun getPos(): BlockPos {
         this.pos
      }

      fun component1(): BlockPos {
         this.pos
      }

      public operator fun component2(): Double {
         return this.feetY
      }

      fun copy(pos: BlockPos, feetY: Double): VoxelBlockCache.StandSurface {
         VoxelBlockCache.StandSurface(pos, feetY)
      }

      override fun toString(): String {
         return "StandSurface(pos=${this.pos}, feetY=${this.feetY})"
      }

      override fun hashCode(): Int {
         return this.pos.hashCode() * 31 + java.lang.Double.hashCode(this.feetY)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is VoxelBlockCache.StandSurface
               && this.pos == (other as VoxelBlockCache.StandSurface).pos
               && java.lang.Double.compare(this.feetY, (other as VoxelBlockCache.StandSurface).feetY) == 0
            }
      }
   }
}
