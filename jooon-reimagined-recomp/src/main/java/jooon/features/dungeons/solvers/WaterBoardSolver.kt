package jooon.features.dungeons.solvers

import com.google.gson.Gson
import java.awt.Color
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Locale
import java.util.Map.Entry
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.util.WorldUtils
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import kotlin.enums.EnumEntries
import kotlin.math.MathKt
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.block.Block
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

object WaterBoardSolver {
   private val solutionsData: Map<String, Map<String, Map<jooon.features.dungeons.solvers.WaterBoardSolver.Lever, List<Int>>>> by lazy({ 
      loadSolutions("/assets/jooonreimagined/dungeons/waterboard_solutions.json")
   })
      private get() {
         return solutionsData$delegate.getValue() as MutableMap<String, MutableMap<String, MutableMap<WaterBoardSolver.Lever, MutableList<Int>>>>
      }


   private val efficientSolutionsData: Map<String, Map<String, Map<jooon.features.dungeons.solvers.WaterBoardSolver.Lever, List<Int>>>> by lazy({ 
      loadSolutions("/assets/jooonreimagined/dungeons/efficient_waterboard_solutions.json")
   })
      private get() {
         return efficientSolutionsData$delegate.getValue() as MutableMap<String, MutableMap<String, MutableMap<WaterBoardSolver.Lever, MutableList<Int>>>>
      }


   private val TOP_LEFT_BLOCK: Pair<Int, Int> = Pair(16, 26)
   private val TOP_RIGHT_BLOCK: Pair<Int, Int> = Pair(14, 26)
   private val SEA_LANTERN_MIDDLE: Pair<Int, Int> = Pair(15, 27)
   private val PURPLE_WOOL: Pair<Int, Int> = Pair(15, 19)
   private val woolOrder: List<Block> =
      listOf(arrayOf(Blocks.PURPLE_WOOL, Blocks.ORANGE_WOOL, Blocks.BLUE_WOOL, Blocks.LIME_WOOL, Blocks.RED_WOOL))
      private val FIRST_COLOR: Color = Color(0, 255, 0, 255)
   private val SECOND_COLOR: Color = Color(255, 165, 0, 255)
   private var inWaterBoard: Boolean
   private var variant: Int?
   private var subvariant: String?
   private var solution: MutableList<jooon.features.dungeons.solvers.WaterBoardSolver.SolutionEntry>?
   private var openedWaterAt: Long = -1L

   fun getMc(): MinecraftClient {
return var10000
   }

   private fun loadSolutions(path: String): Map<String, Map<String, Map<jooon.features.dungeons.solvers.WaterBoardSolver.Lever, List<Int>>>> {
      var stream: java.util.Map
      try {

         if (var72 == null) {
            return emptyMap()
         }

         val ``: Reader = InputStreamReader(var72, Charsets.UTF_8)

         var var75: java.lang.Throwable = null

         try {
            var78 = TextStreamsKt.readText(var73 as BufferedReader)
         } catch (var69: java.lang.Throwable) {
            var75 = var69
            throw var69
         } finally {
            var73.close()
         }

         val var81: java.util.Map = var10000 as java.util.Map
         val `destination$iv$iv`: java.util.Map = LinkedHashMap(mapCapacity((var10000 as java.util.Map).size()))

         for (`element$iv$iv$iv` in var81.entrySet()) {
            var var10001: Any = (`element$iv$iv$iv` as Entry).getKey()
            val `this$iv`: java.util.Map = (`element$iv$iv$iv` as Entry).getValue() as java.util.Map
            val `destination$iv$ivx`: java.util.Map = LinkedHashMap(mapCapacity(`this$iv`.size()))

            for (`element$iv$iv$ivx` in `this$iv`.entrySet()) {
               var10001 = (`element$iv$iv$ivx` as Entry).getKey()
               val `this$iv`: java.lang.Iterable = ((`element$iv$iv$ivx` as Entry).getValue() as java.util.Map).entrySet()
               val `destination$iv$ivxx`: java.util.Map = LinkedHashMap(
                  (mapCapacity(`this$iv`.count().coerceAtLeast(10))).coerceAtLeast(16)
               )

               for (`element$iv$iv` in `this$iv`) {

                  val times: java.util.List = (`element$iv$iv` as Entry).getValue() as java.util.List
                  val var83: WaterBoardSolver.Lever = WaterBoardSolver.Lever.Companion.from(lever)
                  val `this$iv`: java.lang.Iterable = times
                  val `destination$iv$ivxxx`: java.util.Collection = ArrayList(times.count().coerceAtLeast(10))

                  for (`item$iv$iv` in `this$iv`) {
                     `destination$iv$ivxxx`.add(((`item$iv$iv` as java.lang.Number).doubleValue() * 20.toDouble()).roundToInt())
                  }

                  `destination$iv$ivxx`.put(var82.getFirst(), var82.getSecond())
               }

               `destination$iv$ivx`.put(var10001, `destination$iv$ivxx`)
            }

            `destination$iv$iv`.put(var10001, `destination$iv$ivx`)
         }

         stream = `destination$iv$iv`
      } catch (var71: Exception) {
         stream = emptyMap()
      }

      return stream
   }

   fun onInitializeClient() {
   }

   private fun reset() {
      inWaterBoard = false
      variant = null
      subvariant = null
      solution = null
      openedWaterAt = -1L
   }

   private fun tick() {

      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Water Board") {
         inWaterBoard = true
         if (variant == null) {
            var sb: Int = 77
            var var10000: Pair = currentRoom.fromComp(
               (SEA_LANTERN_MIDDLE.getFirst() as java.lang.Number).intValue(), (SEA_LANTERN_MIDDLE.getSecond() as java.lang.Number).intValue()
            )
            if (var10000 == null) {
return return
            }

               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), 77, (var10000.getSecond() as java.lang.Number).intValue())
               if (!((if (var60 != null) var60.getBlock() else null) == Blocks.SEA_LANTERN)) {
               sb = 78
            }

            var10000 = currentRoom.fromComp(
               (TOP_LEFT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_LEFT_BLOCK.getSecond() as java.lang.Number).intValue()
            )
            if (var10000 == null) {
return return
            }

            var10000 = currentRoom.fromComp(
               (TOP_RIGHT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_RIGHT_BLOCK.getSecond() as java.lang.Number).intValue()
            )
            if (var10000 == null) {
return return
            }

               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
               if (var63 == null) {
return return
            }

            var `this$iv`: BlockState = var63

               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
               if (var64 == null) {
return return
            }

            var ``: BlockState = var64
            if (var63.isAir() || var63.getBlock() == Blocks.STONE) {
               var10000 = currentRoom.fromComp(
                  (TOP_LEFT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_LEFT_BLOCK.getSecond() as java.lang.Number).intValue() + 1
               )
               if (var10000 == null) {
return return
               }

                  .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
                  if (var66 == null) {
return return
               }

               `this$iv` = var66
            }

            if (var64.isAir() || var64.getBlock() == Blocks.STONE) {
               var10000 = currentRoom.fromComp(
                  (TOP_RIGHT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_RIGHT_BLOCK.getSecond() as java.lang.Number).intValue() + 1
               )
               if (var10000 == null) {
return return
               }

                  .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
                  if (var68 == null) {
return return
               }

               `` = var68
            }


            variant = if (var69 == Blocks.GOLD_BLOCK && var70 == Blocks.TERRACOTTA)
return 0
return else
               (
                  if (var69 == Blocks.EMERALD_BLOCK && var70 == Blocks.QUARTZ_BLOCK)
return 1
return else
                     (
                        if (var69 == Blocks.QUARTZ_BLOCK && var70 == Blocks.DIAMOND_BLOCK)
return 2
return else
                           (if (var69 == Blocks.GOLD_BLOCK && var70 == Blocks.QUARTZ_BLOCK) 3 else null)
                     )
               )
            }

         try {
            if (subvariant == null && variant != null) {

               var var31: Int = 0

               for (var33 in woolOrder.size()..var31) {

                     (PURPLE_WOOL.getFirst() as java.lang.Number).intValue(), (PURPLE_WOOL.getSecond() as java.lang.Number).intValue() - var31
                  )
                  if (var71 != null) {

                        .getBlockState((var71.getFirst() as java.lang.Number).intValue(), 57, (var71.getSecond() as java.lang.Number).intValue())


                        .getBlockState((var71.getFirst() as java.lang.Number).intValue(), 56, (var71.getSecond() as java.lang.Number).intValue())
                        if (var37 == woolOrder.get(var31) || (if (var73 != null) var73.getBlock() else null) == woolOrder.get(var31)) {
                        var30.append(var31)
                     }
                  }
               }

               if (var30.length() == 3) {
                  subvariant = var30.toString()
                  val var74: java.util.Map = (if (Config.waterBoardEfficient) this.efficientSolutionsData else this.solutionsData)
                     .get(java.lang.String.valueOf(variant))
                     val var34: java.util.Map = if (var74 != null) var74.get(subvariant) as java.util.Map else null
                  if (var34 != null) {
                     var var49: java.util.Collection = ArrayList()

                     for (`item$iv$iv` in var34.entrySet()) {
                        val lever: WaterBoardSolver.Lever = `item$iv$iv`.getKey() as WaterBoardSolver.Lever
                        val pos: java.lang.Iterable = `item$iv$iv`.getValue() as java.util.List
                        val `destination$iv$ivx`: java.util.Collection = ArrayList(pos.count().coerceAtLeast(10))

                        for (`item$iv$ivx` in pos) {
                           `destination$iv$ivx`.add(Pair(lever, (`item$iv$ivx` as java.lang.Number).intValue()))
                        }

                        addAll(var49, `destination$iv$ivx` as java.util.List)
                     }

                     val var40: java.lang.Iterable = sortedWith(var49 as java.util.List, WaterBoardSolver$tick$$inlined$sortedBy$1())
                     var49 = ArrayList(var40.count().coerceAtLeast(10))

                     for (var53 in var40) {
                        val var57: WaterBoardSolver.Lever = (var53 as Pair).component1() as WaterBoardSolver.Lever


                        var49.add(
                           WaterBoardSolver.SolutionEntry(
                              (var75.getFirst() as java.lang.Number).intValue(), var57.y, (var75.getSecond() as java.lang.Number).intValue(), var58, var57
                           )
                        )
                     }

                     solution = toMutableList(var49 as java.util.List)
                  }
               }
            }
         } catch (var28: Exception) {
         } catch (var29: Exception) {
         }
      } else {
         if (inWaterBoard) {
            this.reset()
         }
      }
   }

   private fun render(ctx: WorldRenderContext) {
      val sol: java.util.List = solution
      if (solution != null) {

         if (var10000 != null) {





            val var48: net.minecraft.client.util.math.MatrixStack.Entry = var10000.peek()
            val lastEntry: net.minecraft.client.util.math.MatrixStack.Entry = var48



            if (var50 != null) {

               val leverUsage: java.util.Map = LinkedHashMap()
               var lastX: Double = 0.0
               var lastY: Double = 0.0
               var lastZ: Double = 0.0
               val `this$iv`: java.lang.Iterable = sol
               var `index$iv`: Int = 0

               for (`item$iv` in `this$iv`) {

                  if (var17 < 0) {
                     throwIndexOverflow()
                  }

                  val entry: WaterBoardSolver.SolutionEntry = `item$iv` as WaterBoardSolver.SolutionEntry

                  leverUsage.put((`item$iv` as WaterBoardSolver.SolutionEntry).lever, offset + 1)





                  RenderUtils.renderBoxOutlineRobust(consumers, posMat, lastEntry, cameraPos, box, r, g, b, 1.0F, 0.02F)
                  if (Config.dungeonESPThroughWalls) {


                     RenderUtils.renderBoxOutlineThroughWalls(var53, posMat, lastEntry, cameraPos, box, color, 0.02F)
                  }



                  if (var17 > 0 && var17 <= 2) {
                     RenderUtils.INSTANCE
                        .renderLineRobust(consumers, posMat, lastEntry, cameraPos, Vec3d(lastX, lastY, lastZ), Vec3d(curX, curY, curZ), r, g, b, 1.0F, 0.08F)
                     }

                  lastX = curX
                  lastY = curY
                  lastZ = curZ


                  val var54: String
                  if (remaining <= 0) {
                     var54 = "§aClick Now!"
                  } else {
                     val var45: Array<Any> = arrayOf(remaining.toDouble() * 0.05)

                     var54 = "§e$var55"
                  }

                  RenderUtils.INSTANCE
                     .renderText(
                        consumers, matrices, var54, entry.x.toDouble() + 0.5, entry.y.toDouble() + 1.2 + offset.toDouble(), entry.z.toDouble() + 0.5, -1, camera, true
                     )
                  }
            }
         }
      }
   }

   fun onHudRender(context: DrawContext) {
      if (inWaterBoard && Config.waterBoardSolver) {
         if (solution != null) {
            val sol: java.util.List = solution
            if (!solution.isEmpty()) {

               val entry: WaterBoardSolver.SolutionEntry = first(sol) as WaterBoardSolver.SolutionEntry

               var var10000: String = entry.lever.type.replace("_", " ").toUpperCase(Locale.ROOT)
               if (remaining <= 0) {
                  var10000 = "§a§lCLICK $var10000 NOW!"
               } else {
                  val var11: Array<Any> = arrayOf(remaining.toDouble() * 0.05)

                  var10000 = "§bNext Action: §e$var14 §7($var10000)"
               }

               context.drawCenteredTextWithShadow(this.getMc().textRenderer, var10000, context.getScaledWindowWidth() / 2, context.getScaledWindowHeight() / 2 + 40, 16777215)
            }
         }
      }
   }

   enum class Lever(type: String, x: Int, y: Int, z: Int) {
      Quartz("quartz_block", 20, 61, 20),
      Gold("gold_block", 20, 61, 15),
      Coal("coal_block", 20, 61, 10),
      Diamond("diamond_block", 10, 61, 20),
      Emerald("emerald_block", 10, 61, 15),
      Terracotta("hardened_clay", 10, 61, 10),
      Water("water", 15, 60, 5);

      val type: String
      val x: Int
      val y: Int
      val z: Int

      init {
         this.type = type
         this.x = x
         this.y = y
         this.z = z
      }

      
      fun getEntries(): EnumEntries<WaterBoardSolver.Lever> {
         $ENTRIES
      }

      @SourceDebugExtension(["SMAP\nWaterBoardSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WaterBoardSolver.kt\njooon/features/dungeons/solvers/WaterBoardSolver$Lever$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,301:1\n1#2:302\n*E\n"])
      companion object {
         fun from(type: String): jooon.features.dungeons.solvers.WaterBoardSolver.Lever {
            val var3: java.util.Iterator = (WaterBoardSolver.Lever.getEntries() as java.lang.Iterable).iterator()

            var var10000: Any
            while (true) {
               if (var3.hasNext()) {

                  if (!((var4 as WaterBoardSolver.Lever).type == type)) {
return continue
                  }

                  var10000 = var4
break
               }

               var10000 = null
break
            }

            return var10000 as WaterBoardSolver.Lever
         }
      }
   }

   data class SolutionEntry(x: Int, y: Int, z: Int, time: Int, lever: jooon.features.dungeons.solvers.WaterBoardSolver.Lever) {
      val x: Int
      val y: Int
      val z: Int
      val time: Int
      val lever: jooon.features.dungeons.solvers.WaterBoardSolver.Lever

      init {
         this.x = x
         this.y = y
         this.z = z
         this.time = time
         this.lever = lever
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.y
      }

      public operator fun component3(): Int {
         return this.z
      }

      public operator fun component4(): Int {
         return this.time
      }

      public operator fun component5(): jooon.features.dungeons.solvers.WaterBoardSolver.Lever {
         return this.lever
      }

      fun copy(
         x: Int = this.x,
         y: Int = this.y,
         z: Int = this.z,
         time: Int = this.time,
         lever: jooon.features.dungeons.solvers.WaterBoardSolver.Lever = this.lever
      ): jooon.features.dungeons.solvers.WaterBoardSolver.SolutionEntry {
         return WaterBoardSolver.SolutionEntry(x, y, z, time, lever)
      }

      override fun toString(): String {
         return "SolutionEntry(x=${this.x}, y=${this.y}, z=${this.z}, time=${this.time}, lever=${this.lever})"
      }

      override fun hashCode(): Int {
         return (((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)) * 31 + Integer.hashCode(this.time)) * 31
            + this.lever.hashCode()
         }

      override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is WaterBoardSolver.SolutionEntry
               && this.x == (other as WaterBoardSolver.SolutionEntry).x
               && this.y == (other as WaterBoardSolver.SolutionEntry).y
               && this.z == (other as WaterBoardSolver.SolutionEntry).z
               && this.time == (other as WaterBoardSolver.SolutionEntry).time
               && this.lever === (other as WaterBoardSolver.SolutionEntry).lever
            }
      }
   }
}
