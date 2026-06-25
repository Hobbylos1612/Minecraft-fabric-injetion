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
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nBoulderSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BoulderSolver.kt\njooon/features/dungeons/solvers/BoulderSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,151:1\n1549#2:152\n1620#2,3:153\n*S KotlinDebug\n*F\n+ 1 BoulderSolver.kt\njooon/features/dungeons/solvers/BoulderSolver\n*L\n97#1:152\n97#1:153,3\n*E\n"])
public object BoulderSolver {
   private final val solutions: Map<String, List<Pair<Int, Int>>> =
      MapsKt.mapOf(
         arrayOf(
            TuplesKt.to("100101001000000010101001111101010101010101", CollectionsKt.listOf(arrayOf(TuplesKt.to(21, 11), TuplesKt.to(22, 21)))),
            TuplesKt.to("010000010111101001010011100000101110000111", CollectionsKt.listOf(TuplesKt.to(13, 12))),
            TuplesKt.to("000000011111101001010011100000101110000110", CollectionsKt.listOf(TuplesKt.to(13, 12))),
            TuplesKt.to(
               "100000111101111011101110001110111010000000",
               CollectionsKt.listOf(arrayOf(TuplesKt.to(21, 14), TuplesKt.to(15, 17), TuplesKt.to(15, 20), TuplesKt.to(13, 21)))
            ),
            TuplesKt.to("110001110111011010001100111111100011000001", CollectionsKt.listOf(arrayOf(TuplesKt.to(15, 14), TuplesKt.to(19, 21)))),
            TuplesKt.to("100100101000100010100010101000101000100010", CollectionsKt.listOf(TuplesKt.to(22, 21))),
            TuplesKt.to("000000010101110101010011010000010100000000", CollectionsKt.listOf(TuplesKt.to(22, 18))),
            TuplesKt.to(
               "000000001111100100010010001011111110000000",
               CollectionsKt.listOf(arrayOf(TuplesKt.to(24, 11), TuplesKt.to(24, 14), TuplesKt.to(24, 17), TuplesKt.to(24, 20), TuplesKt.to(22, 21)))
            )
         )
      )
      private final var inBoulder: Boolean
   private final var currentSolution: CopyOnWriteArrayList<Pair<Int, Int>> = CopyOnWriteArrayList()

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.boulderSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inBoulder) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.boulderSolver && Utils.INSTANCE.inDungeon && inBoulder) {
            INSTANCE.render(ctx)
         }
      })
      UseBlockCallback.EVENT
         .register(
            lambda_2@{ player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult ->
               if (Config.boulderSolver && inBoulder && !currentSolution.isEmpty() && world.method_8608()) {
                  val var10000: BlockPos = hitResult.method_17777()
                  val x: Int = var10000.method_10263()
                  val y: Int = var10000.method_10264()
                  val z: Int = var10000.method_10260()
                  val var12: Block = world.method_8320(var10000).method_26204()
                  if (!(var12 == Blocks.field_10494) && !(var12 == Blocks.field_10187)) {
                     return@lambda_2 ActionResult.field_5811 as ActionResult
                  } else {
                     val var13: java.util.Iterator = currentSolution.iterator()
                     val var9: java.util.Iterator = var13

                     while (var9.hasNext()) {
                        val data: Pair = var9.next() as Pair
                        if (Math.abs(x - (data.getFirst() as java.lang.Number).intValue()) + Math.abs(z - (data.getSecond() as java.lang.Number).intValue())
                              <= 1
                           && y >= 64
                           && y <= 66) {
                           currentSolution.remove(data)
                        }
                     }

                     return@lambda_2 ActionResult.field_5811 as ActionResult
                  }
               } else {
                  return@lambda_2 ActionResult.field_5811 as ActionResult
               }
            }
         )
      }

   private fun reset() {
      inBoulder = false
      currentSolution.clear()
   }

   private fun tick() {
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Boulder") {
         if (!inBoulder) {
            inBoulder = true
            val var3: java.util.List = solutions.get(this.getGridLayout(currentRoom))
            if (var3 != null) {
               val it: java.lang.Iterable = var3
               val var7: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10))

               for (`item$iv$iv` in it) {
                  val worldPos: Pair = currentRoom.fromComp(
                     ((`item$iv$iv` as Pair).getFirst() as java.lang.Number).intValue(), ((`item$iv$iv` as Pair).getSecond() as java.lang.Number).intValue()
                  )
                  var7.add(if (worldPos != null) TuplesKt.to(worldPos.getFirst(), worldPos.getSecond()) else null)
               }

               val var15: java.util.List = CollectionsKt.filterNotNull(var7 as java.util.List)
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
      val var2: Triple = Triple(24, 65, 24)
      val x0: Int = (var2.component1() as java.lang.Number).intValue()
      val y0: Int = (var2.component2() as java.lang.Number).intValue()
      val z0: Int = (var2.component3() as java.lang.Number).intValue()
      val var6: StringBuilder = StringBuilder()
      val `$this$getGridLayout_u24lambda_u245`: StringBuilder = var6
      val var9: IntProgression = RangesKt.step(RangesKt.until(0, 16) as IntProgression, 3)
      var z: Int = var9.getFirst()
      val var11: Int = var9.getLast()
      val var12: Int = var9.getStep()
      if (var12 > 0 && z <= var11 || var12 < 0 && var11 <= z) {
         while (true) {
            val var13: IntProgression = RangesKt.step(RangesKt.until(0, 19) as IntProgression, 3)
            var x: Int = var13.getFirst()
            val var15: Int = var13.getLast()
            val var16: Int = var13.getStep()
            if (var16 > 0 && x <= var15 || var16 < 0 && var15 <= x) {
               while (true) {
                  val var10000: Pair = room.fromComp(x0 - x, z0 - z)
                  if (var10000 != null) {
                     val block: BlockState = WorldUtils.INSTANCE
                        .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), y0, (var10000.getSecond() as java.lang.Number).intValue())
                        if (block != null && !block.method_26215()) {
                        `$this$getGridLayout_u24lambda_u245`.append("1")
                     } else {
                        `$this$getGridLayout_u24lambda_u245`.append("0")
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

      val var19: java.lang.String = var6.toString()
      return var19
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (currentSolution.isEmpty()) {
            return
         }

         val var10000: MatrixStack = ctx.matrices()
         if (var10000 == null) {
            return
         }

         val var16: Camera = ctx.gameRenderer().method_19418()
         val var17: Vec3d = var16.method_71156()
         val cameraPos: Vec3d = var17
         val var18: Entry = var10000.method_23760()
         val lastEntry: Entry = var18
         val var19: Matrix4f = var18.method_23761()
         val posMat: Matrix4f = var19
         val var20: VertexConsumerProvider = ctx.consumers()
         if (var20 == null) {
            return
         }

         val consumers: VertexConsumerProvider = var20
         val var21: java.util.Iterator = currentSolution.iterator()
         val var8: java.util.Iterator = var21

         while (var8.hasNext()) {
            val it: Pair = var8.next() as Pair
            val box: Box = Box(
               (it.getFirst() as java.lang.Number).intValue(),
               65.0,
               (it.getSecond() as java.lang.Number).intValue(),
               (it.getFirst() as java.lang.Number).intValue() + 1.0,
               66.0,
               (it.getSecond() as java.lang.Number).intValue() + 1.0
            )
            RenderUtils.INSTANCE.renderBoxFill(consumers, posMat, lastEntry, cameraPos, box, 0.0F, 1.0F, 1.0F, 0.4F)
            if (Config.dungeonESPThroughWalls) {
               val var22: MinecraftClient = this.getMc()
               val var23: OutlineVertexConsumerProvider = (var22 as MinecraftAccessor).getRenderBuffers().method_23003()
               val var24: RenderUtils = RenderUtils.INSTANCE
               val var10006: Color = Color.CYAN
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
