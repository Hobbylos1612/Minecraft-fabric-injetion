package jooon.pathfinding.voxel.world

import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentHashMap.KeySetView
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.minecraft.class_2680
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

@SourceDebugExtension(["SMAP\nVoxelBlockCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VoxelBlockCache.kt\njooon/pathfinding/voxel/world/VoxelBlockCache\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,269:1\n1#2:270\n1549#3:271\n1620#3,3:272\n1549#3:275\n1620#3,3:276\n1855#3,2:279\n1855#3,2:281\n1011#3,2:283\n*S KotlinDebug\n*F\n+ 1 VoxelBlockCache.kt\njooon/pathfinding/voxel/world/VoxelBlockCache\n*L\n78#1:271\n78#1:272,3\n95#1:275\n95#1:276,3\n154#1:279,2\n159#1:281,2\n211#1:283,2\n*E\n"])
public object VoxelBlockCache {
   private final val cache: ConcurrentHashMap<Long, class_2680> = ConcurrentHashMap()
   private final val supportTopCache: ConcurrentHashMap<Long, List<Double>> = ConcurrentHashMap()
   private final val scannedChunks: KeySetView<Long, Boolean> = ConcurrentHashMap.newKeySet()
   private final val columnSurfacesCache: ConcurrentHashMap<
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
   private final val BODY_OFFSETS: List<Pair<Double, Double>> =
      CollectionsKt.listOf(arrayOf(TuplesKt.to(0.5, 0.5), TuplesKt.to(0.3, 0.3), TuplesKt.to(0.7, 0.3), TuplesKt.to(0.3, 0.7), TuplesKt.to(0.7, 0.7)))

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   fun getBlockState(pos: BlockPos): BlockState {
      val key: Long = pos.method_10063()
      val world: BlockState = cache.get(key)
      if (world != null) {
         world
      } else {
         val var10000: ClientWorld = this.getMc().field_1687
         if (var10000 == null) {
            val var12: BlockState = Blocks.field_10124.method_9564()
            var12
         } else if (!var10000.method_8393(pos.method_10263() shr 4, pos.method_10260() shr 4)) {
            val var11: BlockState = Blocks.field_10124.method_9564()
            var11
         } else {
            val var10: BlockState = var10000.method_8320(pos)
            cache.put(key, var10)
            scannedChunks.add(this.chunkKey(pos.method_10263() shr 4, pos.method_10260() shr 4))
            var10
         }
      }
   }

   private fun chunkKey(cx: Int, cz: Int): Long {
      return (long)cx shl 32 or cz and 4294967295L
   }

   private fun columnKey(x: Int, z: Int): Long {
      return (long)x shl 32 or z and 4294967295L
   }

   public fun isChunkAvailable(x: Int, z: Int): Boolean {
      return this.isChunkLoaded(x, z) || scannedChunks.contains(this.chunkKey(x shr 4, z shr 4))
   }

   fun getCollisionShape(pos: BlockPos): VoxelShape {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         val var4: VoxelShape = VoxelShapes.method_1073()
         var4
      } else {
         val var3: VoxelShape = this.getBlockState(pos).method_26194(var10000 as BlockView, pos, ShapeContext.method_16194())
         var3
      }
   }

   fun getCollisionHeight(pos: BlockPos): Double {
      val shape: VoxelShape = this.getCollisionShape(pos)
      if (shape.method_1110()) 0.0 else shape.method_1107().field_1325
   }

   fun getSupportTopYs(pos: BlockPos): MutableList<java.lang.Double> {
      val key: Long = pos.method_10063()
      val shape: java.util.List = supportTopCache.get(key)
      if (shape != null) {
         val var27: java.lang.Iterable = shape
         val var28: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(shape, 10))

         for (`item$iv$iv` in var27) {
            var28.add((double)pos.method_10264() + (`item$iv$iv` as java.lang.Number).doubleValue())
         }

         var28 as java.util.List
      } else {
         val var20: VoxelShape = this.getCollisionShape(pos)
         if (var20.method_1110()) {
            supportTopCache.put(key, CollectionsKt.emptyList())
            CollectionsKt.emptyList()
         } else {
            val var10000: java.util.List = var20.method_1090()
            val localTopsx: java.util.List = SequencesKt.toList(
               SequencesKt.sortedDescending(SequencesKt.distinct(SequencesKt.map(CollectionsKt.asSequence(var10000), { it: Box ->
                  MathKt.roundToInt(it.field_1325 * 16.0) / 16.0
               })))
            )
            supportTopCache.put(key, localTopsx)
            val `$this$mapTo$iv$iv`: java.lang.Iterable = localTopsx
            val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(localTopsx, 10))

            for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
               `destination$iv$iv`.add((double)pos.method_10264() + (`item$iv$iv` as java.lang.Number).doubleValue())
            }

            `destination$iv$iv` as java.util.List
         }
      }
   }

   fun getSupportTopY(pos: BlockPos): java.lang.Double? {
      CollectionsKt.firstOrNull(this.getSupportTopYs(pos)) as java.lang.Double
   }

   fun quantizeFeetOffset(pos: BlockPos, feetY: Double): Int {
      MathKt.roundToInt((feetY - (double)pos.method_10264()) * 16.0)
   }

   fun buildPlayerBox(centerX: Double, feetY: Double, centerZ: Double): Box {
      Box(centerX - 0.3 + 0.001, feetY + 0.001, centerZ - 0.3 + 0.001, centerX + 0.3 - 0.001, feetY + 1.8 - 0.001, centerZ + 0.3 - 0.001)
   }

   fun hasBlockCollision(box: Box): Boolean {
      val var10000: ClientWorld = this.getMc().field_1687
      var10000 != null && var10000.method_20812(null, box).iterator().hasNext()
   }

   public fun isBodyClearAt(centerX: Double, feetY: Double, centerZ: Double): Boolean {
      return !this.hasBlockCollision(this.buildPlayerBox(centerX, feetY, centerZ))
   }

   fun isStandable(pos: BlockPos, feetY: Double): Boolean {
      if ((int)Math.floor(feetY + 0.001) != pos.method_10264()) {
         false
      } else {
         for (var5 in BODY_OFFSETS) {
            val bodyBox: Box = this.buildPlayerBox(
               (double)pos.method_10263() + (var5.component1() as java.lang.Number).doubleValue(),
               feetY,
               (double)pos.method_10260() + (var5.component2() as java.lang.Number).doubleValue()
            )
            if (!this.hasBlockCollision(bodyBox)
               && this.hasBlockCollision(Box(bodyBox.field_1323, feetY - 0.05, bodyBox.field_1321, bodyBox.field_1320, feetY + 0.001, bodyBox.field_1324))) {
               true
            }
         }

         false
      }
   }

   fun resolveStandingSurface(pos: BlockPos): VoxelBlockCache.StandSurface? {
      val candidates: LinkedHashSet = LinkedHashSet()

      for (`element$iv` in this.getSupportTopYs(pos)) {
         val p0: Double = (`element$iv` as java.lang.Number).doubleValue()
         if (p0 - pos.method_10264() <= 0.601) {
            candidates.add(p0)
         }
      }

      val var10001: BlockPos = pos.method_10074()

      for (var15 in this.getSupportTopYs(var10001)) {
         candidates.add(var15 as java.lang.Double)
      }

      val var11: java.util.Iterator = CollectionsKt.sortedDescending(candidates).iterator()

      while (var11.hasNext()) {
         val var13: Double = (var11.next() as java.lang.Number).doubleValue()
         if (this.isStandable(pos, var13)) {
            VoxelBlockCache.StandSurface(pos, var13)
         }
      }

      null
   }

   public fun isChunkLoaded(x: Int, z: Int): Boolean {
      val var10000: ClientWorld = this.getMc().field_1687
      return var10000 != null && var10000.method_8393(x shr 4, z shr 4)
   }

   public fun getStandableSurfaces(x: Int, z: Int, minFeetY: Double, maxFeetY: Double): List<jooon.pathfinding.voxel.world.VoxelBlockCache.StandSurface> {
      if (maxFeetY + 0.001 < minFeetY) {
         return CollectionsKt.emptyList()
      } else if (!this.isChunkAvailable(x, z)) {
         return CollectionsKt.emptyList()
      } else {
         val minSupportY: Int = (int)Math.floor(minFeetY) - 1
         var maxSupportY: Int = (int)Math.floor(maxFeetY) + 1
         val world: ClientWorld = this.getMc().field_1687
         val chunk: WorldChunk = if (world != null) world.method_8497(x shr 4, z shr 4) else null
         if (chunk != null) {
            val cacheKey: Int = chunk.method_12005(Type.field_13202, x and 15, z and 15)
            if (cacheKey < maxSupportY) {
               maxSupportY = cacheKey
            }
         }

         if (maxSupportY < minSupportY) {
            return CollectionsKt.emptyList()
         } else {
            val var24: VoxelBlockCache.ColumnRangeKey = VoxelBlockCache.ColumnRangeKey(x, z, minSupportY, maxSupportY)
            var surfaces: java.util.List = columnSurfacesCache.get(var24)
            if (surfaces != null) {
               return surfaces
            } else {
               surfaces = ArrayList()
               val seen: HashSet = HashSet()
               val sections: Array<ChunkSection> = if (chunk != null) chunk.method_12006() else null
               val chunkMinY: Int = if (world != null) world.method_31607() else 0
               var y: Int = maxSupportY

               while (y >= minSupportY) {
                  val `$this$sortByDescending$iv`: Int = y - chunkMinY shr 4
                  if (sections != null && 0 <= y - chunkMinY shr 4 && y - chunkMinY shr 4 < sections.length && sections[y - chunkMinY shr 4].method_38292()) {
                     y = chunkMinY + (`$this$sortByDescending$iv` shl 4) - 1
                  } else {
                     val var19: java.util.Iterator = this.getSupportTopYs(BlockPos(x, y, z)).iterator()

                     while (var19.hasNext()) {
                        val topY: Double = (var19.next() as java.lang.Number).doubleValue()
                        if (!(topY + 0.001 < minFeetY) && !(topY - 0.001 > maxFeetY)) {
                           val feetPos: BlockPos = BlockPos(x, (int)Math.floor(topY + 0.001), z)
                           if (this.isStandable(feetPos, topY) && seen.add(TuplesKt.to(feetPos.method_10063(), this.quantizeFeetOffset(feetPos, topY)))) {
                              surfaces.add(VoxelBlockCache.StandSurface(feetPos, topY))
                           }
                        }
                     }

                     y--
                  }
               }

               if (surfaces.size() > 1) {
                  CollectionsKt.sortWith(surfaces, VoxelBlockCache$getStandableSurfaces$$inlined$sortByDescending$1())
               }

               columnSurfacesCache.put(var24, surfaces)
               return surfaces
            }
         }
      }
   }

   public fun isSweepClear(fromX: Double, fromFeetY: Double, fromZ: Double, toX: Double, toFeetY: Double, toZ: Double, steps: Int, yAt: (Double) -> Double): Boolean {
      if (steps <= 0) {
         return true
      } else {
         var i: Int = 0
         if (0 <= steps) {
            while (true) {
               if (!this.isBodyClearAt(
                  fromX + (toX - fromX) * ((double)i / (double)steps),
                  (yAt((double)i / (double)steps) as java.lang.Number).doubleValue(),
                  fromZ + (toZ - fromZ) * ((double)i / (double)steps)
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
      val feetClear: Boolean = this.isSteppable(pos)
      var var10001: BlockPos = pos.method_10084()
      val headClear: Boolean = this.isPassable(var10001)
      var10001 = pos.method_10074()
      feetClear && headClear && this.isSolid(var10001)
   }

   public fun clear() {
      cache.clear()
      supportTopCache.clear()
      scannedChunks.clear()
      columnSurfacesCache.clear()
   }

   fun invalidate(pos: BlockPos) {
      cache.remove(pos.method_10063())
      supportTopCache.remove(pos.method_10063())
      (columnSurfacesCache.keySet() as KeySetView).removeIf({ p0: Any ->
         `$tmp0`(p0)
      })
   }

   public fun onWorldClear() {
      this.clear()
   }

   private data class ColumnRangeKey(x: Int, z: Int, minY: Int, maxY: Int) {
      public final val x: Int
      public final val z: Int
      public final val minY: Int
      public final val maxY: Int

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

      public fun copy(x: Int = this.x, z: Int = this.z, minY: Int = this.minY, maxY: Int = this.maxY): jooon.pathfinding.voxel.world.VoxelBlockCache.ColumnRangeKey {
         return VoxelBlockCache.ColumnRangeKey(x, z, minY, maxY)
      }

      public override fun toString(): String {
         return "ColumnRangeKey(x=${this.x}, z=${this.z}, minY=${this.minY}, maxY=${this.maxY})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)) * 31 + Integer.hashCode(this.minY)) * 31 + Integer.hashCode(this.maxY)
      }

      public override operator fun equals(other: Any?): Boolean {
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

   public data class StandSurface {
      private BlockPos pos;
      public final val feetY: Double

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

      public override fun toString(): String {
         return "StandSurface(pos=${this.pos}, feetY=${this.feetY})"
      }

      public override fun hashCode(): Int {
         return this.pos.hashCode() * 31 + java.lang.Double.hashCode(this.feetY)
      }

      public override operator fun equals(other: Any?): Boolean {
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
