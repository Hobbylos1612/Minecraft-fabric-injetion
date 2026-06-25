package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.util.WorldUtils
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Matrix4f

object BoulderSolver {
   private val solutions: Map<String, List<Pair<Int, Int>>> =
      mapOf(
         arrayOf(
            Pair("100101001000000010101001111101010101010101", listOf(arrayOf(Pair(21, 11), Pair(22, 21)))),
            Pair("010000010111101001010011100000101110000111", listOf(Pair(13, 12))),
            Pair("000000011111101001010011100000101110000110", listOf(Pair(13, 12))),
            Pair("100000111101111011101110001110111010000000", listOf(arrayOf(Pair(21, 14), Pair(15, 17), Pair(15, 20), Pair(13, 21)))),
            Pair("110001110111011010001100111111100011000001", listOf(arrayOf(Pair(15, 14), Pair(19, 21)))),
            Pair("100100101000100010100010101000101000100010", listOf(Pair(22, 21))),
            Pair("000000010101110101010011010000010100000000", listOf(Pair(22, 18))),
            Pair("000000001111100100010010001011111110000000", listOf(arrayOf(Pair(24, 11), Pair(24, 14), Pair(24, 17), Pair(24, 20), Pair(22, 21))))
         )
      )
      private var inBoulder: Boolean
   private var currentSolution: CopyOnWriteArrayList<Pair<Int, Int>> = CopyOnWriteArrayList()

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.boulderSolver && Utils.inDungeon) {
            tick()
         } else {
            if (inBoulder) {
               reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.boulderSolver && Utils.inDungeon && inBoulder) {
            render(ctx)
         }
      })
      UseBlockCallback.EVENT
         .register(
            lambda_2@{ player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult ->
               if (Config.boulderSolver && inBoulder && !currentSolution.isEmpty() && world.isClient()) {





                  if (!(var12 == Blocks.STONE_BUTTON) && !(var12 == Blocks.OAK_WALL_SIGN)) {
                     return@lambda_2 ActionResult.PASS as ActionResult
                  } else {
                     val var13: java.util.Iterator = currentSolution.iterator()
                     val var9: java.util.Iterator = var13

                     while (var9.hasNext()) {

                        if (Math.abs(x - (data.getFirst() as java.lang.Number).intValue()) + Math.abs(z - (data.getSecond() as java.lang.Number).intValue())
                              <= 1
                           && y >= 64
                           && y <= 66) {
                           currentSolution.remove(data)
                        }
                     }

                     return@lambda_2 ActionResult.PASS as ActionResult
                  }
               } else {
                  return@lambda_2 ActionResult.PASS as ActionResult
               }
            }
         )
      }

   private fun reset() {
      inBoulder = false
      currentSolution.clear()
   }

   private fun tick() {

      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Boulder") {
         if (!inBoulder) {
            inBoulder = true
            val var3: java.util.List = solutions.get(this.getGridLayout(currentRoom))
            if (var3 != null) {
               val it: java.lang.Iterable = var3
               val var7: java.util.Collection = ArrayList(var3.count().coerceAtLeast(10))

               for (`item$iv$iv` in it) {

                     ((`item$iv$iv` as Pair).getFirst() as java.lang.Number).intValue(), ((`item$iv$iv` as Pair).getSecond() as java.lang.Number).intValue()
                  )
                  var7.add(if (worldPos != null) Pair(worldPos.getFirst(), worldPos.getSecond()) else null)
               }

               val var15: java.util.List = filterNotNull(var7 as java.util.List)
               if (var15 != null) {
                  currentSolution.clear()
                  currentSolution.addAll(var15)
               }
            }
         }
      } else {
         if (inBoulder) {
            this.reset()
         }
      }
   }

   private fun getGridLayout(room: DungeonRoom): String {





      val `this24lambda_u245`: StringBuilder = var6

      var z: Int = var9.getFirst()


      if (var12 > 0 && z <= var11 || var12 < 0 && var11 <= z) {
         while (true) {

            var x: Int = var13.getFirst()


            if (var16 > 0 && x <= var15 || var16 < 0 && var15 <= x) {
               while (true) {

                  if (var10000 != null) {

                        .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), y0, (var10000.getSecond() as java.lang.Number).intValue())
                        if (block != null && !block.isAir()) {
                        `this24lambda_u245`.append("1")
                     } else {
                        `this24lambda_u245`.append("0")
                     }
                  }

                  if (x == var15) {
break
                  }

                  x += var16
               }
            }

            if (z == var11) {
break
            }

            z += var12
         }
      }

      return var19
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (currentSolution.isEmpty()) {
return return
         }

         if (var10000 == null) {
return return
         }








         if (var20 == null) {
return return
         }

         val var21: java.util.Iterator = currentSolution.iterator()
         val var8: java.util.Iterator = var21

         while (var8.hasNext()) {


               (it.getFirst() as java.lang.Number).intValue(),
               65.0,
               (it.getSecond() as java.lang.Number).intValue(),
               (it.getFirst() as java.lang.Number).intValue() + 1.0,
               66.0,
               (it.getSecond() as java.lang.Number).intValue() + 1.0
            )
            RenderUtils.renderBoxFill(consumers, posMat, lastEntry, cameraPos, box, 0.0F, 1.0F, 1.0F, 0.4F)
            if (Config.dungeonESPThroughWalls) {




               var24.renderBoxFillThroughWalls(var23, posMat, lastEntry, cameraPos, box, var10006)
            }
         }
      } catch (var15: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var15.printStackTrace()
         }
      }
   }
}
