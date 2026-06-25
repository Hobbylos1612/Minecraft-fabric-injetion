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

public object WorldUtils {
   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   fun getWorld(): ClientWorld? {
      this.getMc().field_1687
   }

   fun getChunkManager(): ClientChunkManager? {
      val var10000: ClientWorld = this.getWorld()
      if (var10000 != null) var10000.method_2935() else null
   }

   public fun isChunkLoaded(x: Double, z: Double): Boolean {
      return this.isChunkLoaded((int)x, (int)z)
   }

   public fun isChunkLoaded(x: Int, z: Int): Boolean {
      val var10000: ClientChunkManager = this.getChunkManager()
      return var10000 != null && var10000.method_12123(x shr 4, z shr 4)
   }

   fun fromBlockTypeOrNull(x: Double, y: Double, z: Double, blockType: Block): BlockState? {
      this.fromBlockTypeOrNull((int)x, (int)y, (int)z, blockType)
   }

   fun fromBlockTypeOrNull(x: Int, y: Int, z: Int, blockType: Block): BlockState? {
      val var10000: BlockState = this.getBlockState(x, y, z)
      if (var10000 == null) {
         null
      } else {
         if (!(var10000.method_26204() == blockType)) null else var10000
      }
   }

   fun getBlockState(x: Double, y: Double, z: Double): BlockState? {
      this.getBlockState((int)x, (int)y, (int)z)
   }

   fun getBlockState(x: Int, y: Int, z: Int): BlockState? {
      val var10000: ClientWorld = this.getWorld()
      if (var10000 != null) var10000.method_8320(BlockPos(x, y, z)) else null
   }

   fun getBlockId(block: Block): Int {
      val var10000: DefaultedRegistry = Registries.field_41175
      CollectionsKt.indexOf(var10000 as java.lang.Iterable, block)
   }

   public fun getBlockIdAt(x: Double, y: Double, z: Double): Int? {
      val var10000: BlockState = this.getBlockState(x, y, z)
      label16@
      if (var10000 == null) {
         return null
      } else {
         val var9: Block = var10000.method_26204()
         return if (var9 == null) null else this.getBlockId(var9)
      }
   }

   fun registryName(block: Block): java.lang.String {
      val var10000: Identifier = Registries.field_41175.method_10221(block)
      "${var10000.method_12836()}:${var10000.method_12832()}"
   }
}
