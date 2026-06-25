package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.ArrayList
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
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nCreeperBeamsSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreeperBeamsSolver.kt\njooon/features/dungeons/solvers/CreeperBeamsSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,159:1\n1747#2,3:160\n*S KotlinDebug\n*F\n+ 1 CreeperBeamsSolver.kt\njooon/features/dungeons/solvers/CreeperBeamsSolver\n*L\n98#1:160,3\n*E\n"])
public object CreeperBeamsSolver {
   private final val solutions: List<jooon.features.dungeons.solvers.CreeperBeamsSolver.BeamsSolutionData> =
      CollectionsKt.listOf(
         arrayOf(
            CreeperBeamsSolver.BeamsSolutionData(15, 74, 15, 15, 84, 13, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(15, 78, 3, 15, 76, 27, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(5, 76, 24, 24, 77, 7, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(2, 75, 16, 27, 78, 14, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(4, 72, 8, 25, 79, 21, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(4, 75, 9, 25, 76, 23, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(22, 80, 22, 4, 72, 8, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(3, 76, 18, 26, 78, 12, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(9, 81, 20, 26, 70, 7, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(18, 81, 21, 9, 69, 3, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(18, 82, 8, 10, 69, 27, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(25, 76, 23, 6, 74, 5, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(6, 74, 5, 25, 76, 23, false, 64, null),
            CreeperBeamsSolver.BeamsSolutionData(26, 70, 7, 9, 81, 20, false, 64, null)
         )
      )
      private final val colorChoicesOutline: List<Color> = CollectionsKt.listOf(arrayOf(Color.CYAN, Color.GREEN, Color.RED, Color.ORANGE))
   private final val solutionList: MutableList<jooon.features.dungeons.solvers.CreeperBeamsSolver.BeamsSolutionData> = ArrayList() as java.util.List
   private final var inRoom: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.creeperBeamsSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inRoom) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.creeperBeamsSolver && Utils.INSTANCE.inDungeon && inRoom) {
            INSTANCE.render(ctx)
         }
      })
   }

   private fun reset() {
      inRoom = false
      solutionList.clear()
   }

   private fun tick() {
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Creeper Beams") {
         if (!inRoom) {
            inRoom = true
            var var10000: Pair = currentRoom.fromComp(15, 15)
            if (var10000 == null) {
               return
            }

            val blockState: BlockState = WorldUtils.INSTANCE
               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), 74, (var10000.getSecond() as java.lang.Number).intValue())
               if (!((if (blockState != null) blockState.method_26204() else null) == Blocks.field_10174)) {
               return
            }

            for (solution in solutions) {
               var10000 = currentRoom.fromComp(solution.x1, solution.z1)
               if (var10000 != null) {
                  val comp1: Pair = var10000
                  var10000 = currentRoom.fromComp(solution.x2, solution.z2)
                  if (var10000 != null) {
                     val comp2: Pair = var10000
                     val var21: BlockState = WorldUtils.INSTANCE
                        .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), solution.y1, (var10000.getSecond() as java.lang.Number).intValue())
                        if (var21 != null) {
                        val var22: Block = var21.method_26204()
                        if (var22 != null) {
                           val var23: BlockState = WorldUtils.INSTANCE
                              .getBlockState(
                                 (var10000.getFirst() as java.lang.Number).intValue(), solution.y2, (var10000.getSecond() as java.lang.Number).intValue()
                              )
                              if (var23 != null) {
                              val var24: Block = var23.method_26204()
                              if (var24 != null && var22 == Blocks.field_10174 && var24 == Blocks.field_10174) {
                                 val `$this$any$iv`: java.lang.Iterable = solutionList
                                 var var25: Boolean
                                 if (solutionList is java.util.Collection && solutionList.isEmpty()) {
                                    var25 = false
                                 } else {
                                    val var12: java.util.Iterator = `$this$any$iv`.iterator()

                                    while (true) {
                                       if (!var12.hasNext()) {
                                          var25 = false
                                          break
                                       }

                                       val it: CreeperBeamsSolver.BeamsSolutionData = var12.next() as CreeperBeamsSolver.BeamsSolutionData
                                       if (it.containsOneOf(
                                             (comp1.getFirst() as java.lang.Number).intValue(), solution.y1, (comp1.getSecond() as java.lang.Number).intValue()
                                          )
                                          || it.containsOneOf(
                                             (comp2.getFirst() as java.lang.Number).intValue(), solution.y2, (comp2.getSecond() as java.lang.Number).intValue()
                                          )) {
                                          var25 = true
                                          break
                                       }
                                    }
                                 }

                                 if (!var25) {
                                    solutionList.add(
                                       CreeperBeamsSolver.BeamsSolutionData(
                                          (comp1.getFirst() as java.lang.Number).intValue(),
                                          solution.y1,
                                          (comp1.getSecond() as java.lang.Number).intValue(),
                                          (comp2.getFirst() as java.lang.Number).intValue(),
                                          solution.y2,
                                          (comp2.getSecond() as java.lang.Number).intValue(),
                                          false,
                                          64,
                                          null
                                       )
                                    )
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         val var26: ClientWorld = this.getMc().field_1687
         if (var26 != null) {
            val var16: ClientWorld = var26

            for (var18 in solutionList) {
               if (!var18.blacklisted
                  && (
                     var16.method_8320(BlockPos(var18.x1, var18.y1, var18.z1)).method_26204() == Blocks.field_10135
                        || var16.method_8320(BlockPos(var18.x2, var18.y2, var18.z2)).method_26204() == Blocks.field_10135
                  )) {
                  var18.blacklisted = true
               }
            }
         }
      } else {
         if (inRoom) {
            this.reset()
         }
      }
   }

   private fun render(ctx: WorldRenderContext) {
      if (!solutionList.isEmpty()) {
         val var10000: MatrixStack = ctx.matrices()
         if (var10000 != null) {
            val var14: Camera = ctx.gameRenderer().method_19418()
            val var15: Vec3d = var14.method_71156()
            val cameraPos: Vec3d = var15
            val var16: Entry = var10000.method_23760()
            val lastEntry: Entry = var16
            val var17: Matrix4f = var16.method_23761()
            val posMat: Matrix4f = var17
            val var18: VertexConsumerProvider = ctx.consumers()
            if (var18 != null) {
               val consumers: VertexConsumerProvider = var18
               var idx: Int = 0

               // $VF: Unable to resugar Kotlin loop from Java for loop
               val var9: Int = solutionList.size()
               while (true) {
                  if (idx < var9 && idx < 4) break
                  val solution: CreeperBeamsSolver.BeamsSolutionData = solutionList.get(idx)
                  if (!solution.blacklisted) {
                     val color: Color = colorChoicesOutline.get(idx)
                     val box: Box = Box(solution.x1, solution.y1, solution.z1, solution.x1 + 1.0, solution.y1 + 1.0, solution.z1 + 1.0)
                     val box2: Box = Box(solution.x2, solution.y2, solution.z2, solution.x2 + 1.0, solution.y2 + 1.0, solution.z2 + 1.0)
                     this.renderBox(consumers, posMat, lastEntry, cameraPos, box, color)
                     this.renderBox(consumers, posMat, lastEntry, cameraPos, box2, color)
                     RenderUtils.INSTANCE
                        .renderLineRobust(
                           consumers,
                           posMat,
                           lastEntry,
                           cameraPos,
                           Vec3d((double)solution.x1 + 0.5, (double)solution.y1 + 0.5, (double)solution.z1 + 0.5),
                           Vec3d((double)solution.x2 + 0.5, (double)solution.y2 + 0.5, (double)solution.z2 + 0.5),
                           (float)color.getRed() / 255.0F,
                           (float)color.getGreen() / 255.0F,
                           (float)color.getBlue() / 255.0F,
                           1.0F,
                           0.08F
                        )
                     }

                  idx++
               }
            }
         }
      }
   }

   fun renderBox(consumers: VertexConsumerProvider, posMat: Matrix4f, entry: Entry, cameraPos: Vec3d, box: Box, color: Color) {
      RenderUtils.INSTANCE
         .renderBoxFill(
            consumers, posMat, entry, cameraPos, box, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.4F
         )
         if (Config.dungeonESPThroughWalls) {
         val var10000: MinecraftClient = this.getMc()
         val var11: OutlineVertexConsumerProvider = (var10000 as MinecraftAccessor).getRenderBuffers().method_23003()
         RenderUtils.INSTANCE.renderBoxFillThroughWalls(var11, posMat, entry, cameraPos, box, color)
      }
   }

   public data class BeamsSolutionData(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int, blacklisted: Boolean = false) {
      public final val x1: Int
      public final val y1: Int
      public final val z1: Int
      public final val x2: Int
      public final val y2: Int
      public final val z2: Int
      public final var blacklisted: Boolean

      init {
         this.x1 = x1
         this.y1 = y1
         this.z1 = z1
         this.x2 = x2
         this.y2 = y2
         this.z2 = z2
         this.blacklisted = blacklisted
      }

      public fun containsOneOf(x: Int, y: Int, z: Int): Boolean {
         return this.x1 == x && this.y1 == y && this.z1 == z || this.x2 == x && this.y2 == y && this.z2 == z
      }

      public operator fun component1(): Int {
         return this.x1
      }

      public operator fun component2(): Int {
         return this.y1
      }

      public operator fun component3(): Int {
         return this.z1
      }

      public operator fun component4(): Int {
         return this.x2
      }

      public operator fun component5(): Int {
         return this.y2
      }

      public operator fun component6(): Int {
         return this.z2
      }

      public operator fun component7(): Boolean {
         return this.blacklisted
      }

      public fun copy(
         x1: Int = this.x1,
         y1: Int = this.y1,
         z1: Int = this.z1,
         x2: Int = this.x2,
         y2: Int = this.y2,
         z2: Int = this.z2,
         blacklisted: Boolean = this.blacklisted
      ): jooon.features.dungeons.solvers.CreeperBeamsSolver.BeamsSolutionData {
         return CreeperBeamsSolver.BeamsSolutionData(x1, y1, z1, x2, y2, z2, blacklisted)
      }

      public override fun toString(): String {
         return "BeamsSolutionData(x1=${this.x1}, y1=${this.y1}, z1=${this.z1}, x2=${this.x2}, y2=${this.y2}, z2=${this.z2}, blacklisted=${this.blacklisted})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (((Integer.hashCode(this.x1) * 31 + Integer.hashCode(this.y1)) * 31 + Integer.hashCode(this.z1)) * 31 + Integer.hashCode(this.x2))
                                 * 31
                              + Integer.hashCode(this.y2)
                        )
                        * 31
                     + Integer.hashCode(this.z2)
               )
               * 31
            + java.lang.Boolean.hashCode(this.blacklisted)
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is CreeperBeamsSolver.BeamsSolutionData
               && this.x1 == (other as CreeperBeamsSolver.BeamsSolutionData).x1
               && this.y1 == (other as CreeperBeamsSolver.BeamsSolutionData).y1
               && this.z1 == (other as CreeperBeamsSolver.BeamsSolutionData).z1
               && this.x2 == (other as CreeperBeamsSolver.BeamsSolutionData).x2
               && this.y2 == (other as CreeperBeamsSolver.BeamsSolutionData).y2
               && this.z2 == (other as CreeperBeamsSolver.BeamsSolutionData).z2
               && this.blacklisted == (other as CreeperBeamsSolver.BeamsSolutionData).blacklisted
            }
      }
   }
}
