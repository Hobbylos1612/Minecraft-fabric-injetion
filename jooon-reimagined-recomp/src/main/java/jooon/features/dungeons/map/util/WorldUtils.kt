package jooon.features.dungeons.map.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientChunkManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.registry.DefaultedRegistry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object WorldUtils {
   fun getMc(): MinecraftClient {
return var10000
   }

   fun getWorld(): ClientWorld? {
      this.getMc().world
   }

   fun getChunkManager(): ClientChunkManager? {

      if (var10000 != null) var10000.getChunkManager() else null
   }

   fun isChunkLoaded(x: Double, z: Double): Boolean {
      return this.isChunkLoaded(x.toInt(), z.toInt())
   }

   fun isChunkLoaded(x: Int, z: Int): Boolean {

      return var10000 != null && var10000.isChunkLoaded(x shr 4, z shr 4)
   }

   fun fromBlockTypeOrNull(x: Double, y: Double, z: Double, blockType: Block): BlockState? {
      this.fromBlockTypeOrNull(x.toInt(), y.toInt(), z.toInt(), blockType)
   }

   fun fromBlockTypeOrNull(x: Int, y: Int, z: Int, blockType: Block): BlockState? {

      if (var10000 == null) {
return null
      } else {
         if (!(var10000.getBlock() == blockType)) null else var10000
      }
   }

   fun getBlockState(x: Double, y: Double, z: Double): BlockState? {
      this.getBlockState(x.toInt(), y.toInt(), z.toInt())
   }

   fun getBlockState(x: Int, y: Int, z: Int): BlockState? {

      if (var10000 != null) var10000.getBlockState(BlockPos(x, y, z)) else null
   }

   fun getBlockId(block: Block): Int {

      indexOf(var10000 as java.lang.Iterable, block)
   }

   fun getBlockIdAt(x: Double, y: Double, z: Double): Int? {

      label16@
      if (var10000 == null) {
         return null
      } else {

         return if (var9 == null) null else this.getBlockId(var9)
      }
   }

   fun registryName(block: Block): String {

      "${var10000.getNamespace()}:${var10000.getPath()}"
   }
}
