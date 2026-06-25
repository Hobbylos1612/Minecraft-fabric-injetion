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
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.class_2248
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

@SourceDebugExtension(["SMAP\nWaterBoardSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WaterBoardSolver.kt\njooon/features/dungeons/solvers/WaterBoardSolver\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,301:1\n1#2:302\n453#3:303\n403#3:304\n453#3:307\n403#3:308\n1238#4,2:305\n1238#4,2:309\n1179#4,2:311\n1253#4,2:313\n1549#4:315\n1620#4,3:316\n1256#4:319\n1241#4:320\n1241#4:321\n1549#4:325\n1620#4,3:326\n1045#4:332\n1549#4:333\n1620#4,3:334\n1864#4,3:337\n350#4,7:340\n76#5:322\n96#5,2:323\n98#5,3:329\n*S KotlinDebug\n*F\n+ 1 WaterBoardSolver.kt\njooon/features/dungeons/solvers/WaterBoardSolver\n*L\n52#1:303\n52#1:304\n53#1:307\n53#1:308\n52#1:305,2\n53#1:309,2\n54#1:311,2\n54#1:313,2\n55#1:315\n55#1:316,3\n54#1:319\n53#1:320\n52#1:321\n212#1:325\n212#1:326,3\n212#1:332\n213#1:333\n213#1:334,3\n243#1:337,3\n128#1:340,7\n212#1:322\n212#1:323,2\n212#1:329,3\n*E\n"])
public object WaterBoardSolver {
   private final val solutionsData: Map<String, Map<String, Map<jooon.features.dungeons.solvers.WaterBoardSolver.Lever, List<Int>>>> by LazyKt.lazy({ 
      INSTANCE.loadSolutions("/assets/jooonreimagined/dungeons/waterboard_solutions.json")
   })
      private final get() {
         return solutionsData$delegate.getValue() as MutableMap<java.lang.String, MutableMap<java.lang.String, MutableMap<WaterBoardSolver.Lever, MutableList<Int>>>>
      }


   private final val efficientSolutionsData: Map<String, Map<String, Map<jooon.features.dungeons.solvers.WaterBoardSolver.Lever, List<Int>>>> by LazyKt.lazy({ 
      INSTANCE.loadSolutions("/assets/jooonreimagined/dungeons/efficient_waterboard_solutions.json")
   })
      private final get() {
         return efficientSolutionsData$delegate.getValue() as MutableMap<java.lang.String, MutableMap<java.lang.String, MutableMap<WaterBoardSolver.Lever, MutableList<Int>>>>
      }


   private final val TOP_LEFT_BLOCK: Pair<Int, Int> = TuplesKt.to(16, 26)
   private final val TOP_RIGHT_BLOCK: Pair<Int, Int> = TuplesKt.to(14, 26)
   private final val SEA_LANTERN_MIDDLE: Pair<Int, Int> = TuplesKt.to(15, 27)
   private final val PURPLE_WOOL: Pair<Int, Int> = TuplesKt.to(15, 19)
   private final val woolOrder: List<class_2248> =
      CollectionsKt.listOf(arrayOf(Blocks.field_10259, Blocks.field_10095, Blocks.field_10514, Blocks.field_10028, Blocks.field_10314))
      private final val FIRST_COLOR: Color = Color(0, 255, 0, 255)
   private final val SECOND_COLOR: Color = Color(255, 165, 0, 255)
   private final var inWaterBoard: Boolean
   private final var variant: Int?
   private final var subvariant: String?
   private final var solution: MutableList<jooon.features.dungeons.solvers.WaterBoardSolver.SolutionEntry>?
   private final var openedWaterAt: Long = -1L

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   private fun loadSolutions(path: String): Map<String, Map<String, Map<jooon.features.dungeons.solvers.WaterBoardSolver.Lever, List<Int>>>> {
      var stream: java.util.Map
      try {
         val var72: InputStream = WaterBoardSolver.class.getResourceAsStream(path)
         if (var72 == null) {
            return MapsKt.emptyMap()
         }

         val `$i$f$mapValues`: Reader = InputStreamReader(var72, Charsets.UTF_8)
         val var73: Closeable = if (`$i$f$mapValues` is BufferedReader) `$i$f$mapValues` as BufferedReader else BufferedReader(`$i$f$mapValues`, 8192)
         var var75: java.lang.Throwable = null

         try {
            var78 = TextStreamsKt.readText(var73 as BufferedReader)
         } catch (var69: java.lang.Throwable) {
            var75 = var69
            throw var69
         } finally {
            CloseableKt.closeFinally(var73, var75)
         }

         val var10000: Any = Gson().fromJson(var78, java.util.Map.class)
         val var81: java.util.Map = var10000 as java.util.Map
         val `destination$iv$iv`: java.util.Map = LinkedHashMap(MapsKt.mapCapacity((var10000 as java.util.Map).size()))

         for (`element$iv$iv$iv` in var81.entrySet()) {
            var var10001: Any = (`element$iv$iv$iv` as Entry).getKey()
            val `$this$mapValues$iv`: java.util.Map = (`element$iv$iv$iv` as Entry).getValue() as java.util.Map
            val `destination$iv$ivx`: java.util.Map = LinkedHashMap(MapsKt.mapCapacity(`$this$mapValues$iv`.size()))

            for (`element$iv$iv$ivx` in `$this$mapValues$iv`.entrySet()) {
               var10001 = (`element$iv$iv$ivx` as Entry).getKey()
               val `$this$associate$iv`: java.lang.Iterable = ((`element$iv$iv$ivx` as Entry).getValue() as java.util.Map).entrySet()
               val `destination$iv$ivxx`: java.util.Map = LinkedHashMap(
                  RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associate$iv`, 10)), 16)
               )

               for (`element$iv$iv` in `$this$associate$iv`) {
                  val lever: java.lang.String = (`element$iv$iv` as Entry).getKey() as java.lang.String
                  val times: java.util.List = (`element$iv$iv` as Entry).getValue() as java.util.List
                  val var83: WaterBoardSolver.Lever = WaterBoardSolver.Lever.Companion.from(lever)
                  val `$this$map$iv`: java.lang.Iterable = times
                  val `destination$iv$ivxxx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(times, 10))

                  for (`item$iv$iv` in `$this$map$iv`) {
                     `destination$iv$ivxxx`.add(MathKt.roundToInt((`item$iv$iv` as java.lang.Number).doubleValue() * (double)20))
                  }

                  val var82: Pair = TuplesKt.to(var83, `destination$iv$ivxxx` as java.util.List)
                  `destination$iv$ivxx`.put(var82.getFirst(), var82.getSecond())
               }

               `destination$iv$ivx`.put(var10001, `destination$iv$ivxx`)
            }

            `destination$iv$iv`.put(var10001, `destination$iv$ivx`)
         }

         stream = `destination$iv$iv`
      } catch (var71: Exception) {
         stream = MapsKt.emptyMap()
      }

      return stream
   }

   public fun onInitializeClient() {
   }

   private fun reset() {
      inWaterBoard = false
      variant = null
      subvariant = null
      solution = null
      openedWaterAt = -1L
   }

   private fun tick() {
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Water Board") {
         inWaterBoard = true
         if (variant == null) {
            var sb: Int = 77
            var var10000: Pair = currentRoom.fromComp(
               (SEA_LANTERN_MIDDLE.getFirst() as java.lang.Number).intValue(), (SEA_LANTERN_MIDDLE.getSecond() as java.lang.Number).intValue()
            )
            if (var10000 == null) {
               return
            }

            val var60: BlockState = WorldUtils.INSTANCE
               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), 77, (var10000.getSecond() as java.lang.Number).intValue())
               if (!((if (var60 != null) var60.method_26204() else null) == Blocks.field_10174)) {
               sb = 78
            }

            var10000 = currentRoom.fromComp(
               (TOP_LEFT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_LEFT_BLOCK.getSecond() as java.lang.Number).intValue()
            )
            if (var10000 == null) {
               return
            }

            var10000 = currentRoom.fromComp(
               (TOP_RIGHT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_RIGHT_BLOCK.getSecond() as java.lang.Number).intValue()
            )
            if (var10000 == null) {
               return
            }

            val var63: BlockState = WorldUtils.INSTANCE
               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
               if (var63 == null) {
               return
            }

            var `$this$map$iv`: BlockState = var63
            val var64: BlockState = WorldUtils.INSTANCE
               .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
               if (var64 == null) {
               return
            }

            var `$i$f$map`: BlockState = var64
            if (var63.method_26215() || var63.method_26204() == Blocks.field_10340) {
               var10000 = currentRoom.fromComp(
                  (TOP_LEFT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_LEFT_BLOCK.getSecond() as java.lang.Number).intValue() + 1
               )
               if (var10000 == null) {
                  return
               }

               val var66: BlockState = WorldUtils.INSTANCE
                  .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
                  if (var66 == null) {
                  return
               }

               `$this$map$iv` = var66
            }

            if (var64.method_26215() || var64.method_26204() == Blocks.field_10340) {
               var10000 = currentRoom.fromComp(
                  (TOP_RIGHT_BLOCK.getFirst() as java.lang.Number).intValue(), (TOP_RIGHT_BLOCK.getSecond() as java.lang.Number).intValue() + 1
               )
               if (var10000 == null) {
                  return
               }

               val var68: BlockState = WorldUtils.INSTANCE
                  .getBlockState((var10000.getFirst() as java.lang.Number).intValue(), sb, (var10000.getSecond() as java.lang.Number).intValue())
                  if (var68 == null) {
                  return
               }

               `$i$f$map` = var68
            }

            val var69: Block = `$this$map$iv`.method_26204()
            val var70: Block = `$i$f$map`.method_26204()
            variant = if (var69 == Blocks.field_10205 && var70 == Blocks.field_10415)
               0
               else
               (
                  if (var69 == Blocks.field_10234 && var70 == Blocks.field_10153)
                     1
                     else
                     (
                        if (var69 == Blocks.field_10153 && var70 == Blocks.field_10201)
                           2
                           else
                           (if (var69 == Blocks.field_10205 && var70 == Blocks.field_10153) 3 else null)
                     )
               )
            }

         try {
            if (subvariant == null && variant != null) {
               val var30: StringBuilder = StringBuilder()
               var var31: Int = 0

               for (var33 in woolOrder.size()..var31) {
                  val var71: Pair = currentRoom.fromComp(
                     (PURPLE_WOOL.getFirst() as java.lang.Number).intValue(), (PURPLE_WOOL.getSecond() as java.lang.Number).intValue() - var31
                  )
                  if (var71 != null) {
                     val var72: BlockState = WorldUtils.INSTANCE
                        .getBlockState((var71.getFirst() as java.lang.Number).intValue(), 57, (var71.getSecond() as java.lang.Number).intValue())
                        val var37: Block = if (var72 != null) var72.method_26204() else null
                     val var73: BlockState = WorldUtils.INSTANCE
                        .getBlockState((var71.getFirst() as java.lang.Number).intValue(), 56, (var71.getSecond() as java.lang.Number).intValue())
                        if (var37 == woolOrder.get(var31) || (if (var73 != null) var73.method_26204() else null) == woolOrder.get(var31)) {
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
                        val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(pos, 10))

                        for (`item$iv$ivx` in pos) {
                           `destination$iv$ivx`.add(TuplesKt.to(lever, (`item$iv$ivx` as java.lang.Number).intValue()))
                        }

                        CollectionsKt.addAll(var49, `destination$iv$ivx` as java.util.List)
                     }

                     val var40: java.lang.Iterable = CollectionsKt.sortedWith(var49 as java.util.List, WaterBoardSolver$tick$$inlined$sortedBy$1())
                     var49 = ArrayList(CollectionsKt.collectionSizeOrDefault(var40, 10))

                     for (var53 in var40) {
                        val var57: WaterBoardSolver.Lever = (var53 as Pair).component1() as WaterBoardSolver.Lever
                        val var58: Int = ((var53 as Pair).component2() as java.lang.Number).intValue()
                        val var75: Pair = currentRoom.fromComp(var57.x, var57.z)
                        var49.add(
                           WaterBoardSolver.SolutionEntry(
                              (var75.getFirst() as java.lang.Number).intValue(), var57.y, (var75.getSecond() as java.lang.Number).intValue(), var58, var57
                           )
                        )
                     }

                     solution = CollectionsKt.toMutableList(var49 as java.util.List)
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
         val var10000: MatrixStack = ctx.matrices()
         if (var10000 != null) {
            val matrices: MatrixStack = var10000
            val var46: Camera = ctx.gameRenderer().method_19418()
            val camera: Camera = var46
            val var47: Vec3d = var46.method_71156()
            val cameraPos: Vec3d = var47
            val var48: net.minecraft.client.util.math.MatrixStack.Entry = var10000.method_23760()
            val lastEntry: net.minecraft.client.util.math.MatrixStack.Entry = var48
            val var49: Matrix4f = var48.method_23761()
            val posMat: Matrix4f = var49
            val var50: VertexConsumerProvider = ctx.consumers()
            if (var50 != null) {
               val consumers: VertexConsumerProvider = var50
               val leverUsage: java.util.Map = LinkedHashMap()
               var lastX: Double = 0.0
               var lastY: Double = 0.0
               var lastZ: Double = 0.0
               val `$this$forEachIndexed$iv`: java.lang.Iterable = sol
               var `index$iv`: Int = 0

               for (`item$iv` in `$this$forEachIndexed$iv`) {
                  val var17: Int = `index$iv`++
                  if (var17 < 0) {
                     CollectionsKt.throwIndexOverflow()
                  }

                  val entry: WaterBoardSolver.SolutionEntry = `item$iv` as WaterBoardSolver.SolutionEntry
                  val offset: Int = leverUsage.getOrDefault((`item$iv` as WaterBoardSolver.SolutionEntry).lever, 0).intValue()
                  leverUsage.put((`item$iv` as WaterBoardSolver.SolutionEntry).lever, offset + 1)
                  val box: Box = Box(entry.x, (double)entry.y + offset, entry.z, entry.x + 1.0, (double)entry.y + offset + 1.0, entry.z + 1.0)
                  val color: Color = if (var17 == 0) FIRST_COLOR else SECOND_COLOR
                  val r: Float = (if (var17 == 0) FIRST_COLOR else SECOND_COLOR).getRed() / 255.0F
                  val g: Float = color.getGreen() / 255.0F
                  val b: Float = color.getBlue() / 255.0F
                  RenderUtils.INSTANCE.renderBoxOutlineRobust(consumers, posMat, lastEntry, cameraPos, box, r, g, b, 1.0F, 0.02F)
                  if (Config.dungeonESPThroughWalls) {
                     val var52: MinecraftClient = INSTANCE.getMc()
                     val var53: OutlineVertexConsumerProvider = (var52 as MinecraftAccessor).getRenderBuffers().method_23003()
                     RenderUtils.INSTANCE.renderBoxOutlineThroughWalls(var53, posMat, lastEntry, cameraPos, box, color, 0.02F)
                  }

                  val curX: Double = entry.x + 0.5
                  val curY: Double = entry.y + 0.5 + offset
                  val curZ: Double = entry.z + 0.5
                  if (var17 > 0 && var17 <= 2) {
                     RenderUtils.INSTANCE
                        .renderLineRobust(consumers, posMat, lastEntry, cameraPos, Vec3d(lastX, lastY, lastZ), Vec3d(curX, curY, curZ), r, g, b, 1.0F, 0.08F)
                     }

                  lastX = curX
                  lastY = curY
                  lastZ = curZ
                  val elapsedTicks: Int = if (openedWaterAt == -1L) 0 else (int)((System.currentTimeMillis() - openedWaterAt) / 50)
                  val remaining: Int = entry.time - elapsedTicks
                  val var54: java.lang.String
                  if (remaining <= 0) {
                     var54 = "§aClick Now!"
                  } else {
                     val var45: Array<Any> = arrayOf((double)remaining * 0.05)
                     val var55: java.lang.String = java.lang.String.format("%.2fs", Arrays.copyOf(var45, var45.length))
                     var54 = "§e$var55"
                  }

                  RenderUtils.INSTANCE
                     .renderText(
                        consumers, matrices, var54, (double)entry.x + 0.5, (double)entry.y + 1.2 + (double)offset, (double)entry.z + 0.5, -1, camera, true
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
               val elapsedTicks: Int = if (openedWaterAt == -1L) 0 else (int)((System.currentTimeMillis() - openedWaterAt) / 50)
               val entry: WaterBoardSolver.SolutionEntry = CollectionsKt.first(sol) as WaterBoardSolver.SolutionEntry
               val remaining: Int = entry.time - elapsedTicks
               var var10000: java.lang.String = StringsKt.replace$default(entry.lever.type, "_", " ", false, 4, null).toUpperCase(Locale.ROOT)
               if (remaining <= 0) {
                  var10000 = "§a§lCLICK $var10000 NOW!"
               } else {
                  val var11: Array<Any> = arrayOf((double)remaining * 0.05)
                  val var14: java.lang.String = java.lang.String.format("%.2fs", Arrays.copyOf(var11, var11.length))
                  var10000 = "§bNext Action: §e$var14 §7($var10000)"
               }

               context.method_25300(this.getMc().field_1772, var10000, context.method_51421() / 2, context.method_51443() / 2 + 40, 16777215)
            }
         }
      }
   }

   public enum class Lever(type: String, x: Int, y: Int, z: Int) {
      Quartz("quartz_block", 20, 61, 20),
      Gold("gold_block", 20, 61, 15),
      Coal("coal_block", 20, 61, 10),
      Diamond("diamond_block", 10, 61, 20),
      Emerald("emerald_block", 10, 61, 15),
      Terracotta("hardened_clay", 10, 61, 10),
      Water("water", 15, 60, 5);

      public final val type: String
      public final val x: Int
      public final val y: Int
      public final val z: Int

      init {
         this.type = type
         this.x = x
         this.y = y
         this.z = z
      }

      @JvmStatic
      fun getEntries(): EnumEntries<WaterBoardSolver.Lever> {
         $ENTRIES
      }

      @SourceDebugExtension(["SMAP\nWaterBoardSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WaterBoardSolver.kt\njooon/features/dungeons/solvers/WaterBoardSolver$Lever$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,301:1\n1#2:302\n*E\n"])
      public companion object {
         public fun from(type: String): jooon.features.dungeons.solvers.WaterBoardSolver.Lever {
            val var3: java.util.Iterator = (WaterBoardSolver.Lever.getEntries() as java.lang.Iterable).iterator()

            var var10000: Any
            while (true) {
               if (var3.hasNext()) {
                  val var4: Any = var3.next()
                  if (!((var4 as WaterBoardSolver.Lever).type == type)) {
                     continue
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

   public data class SolutionEntry(x: Int, y: Int, z: Int, time: Int, lever: jooon.features.dungeons.solvers.WaterBoardSolver.Lever) {
      public final val x: Int
      public final val y: Int
      public final val z: Int
      public final val time: Int
      public final val lever: jooon.features.dungeons.solvers.WaterBoardSolver.Lever

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

      public fun copy(
         x: Int = this.x,
         y: Int = this.y,
         z: Int = this.z,
         time: Int = this.time,
         lever: jooon.features.dungeons.solvers.WaterBoardSolver.Lever = this.lever
      ): jooon.features.dungeons.solvers.WaterBoardSolver.SolutionEntry {
         return WaterBoardSolver.SolutionEntry(x, y, z, time, lever)
      }

      public override fun toString(): String {
         return "SolutionEntry(x=${this.x}, y=${this.y}, z=${this.z}, time=${this.time}, lever=${this.lever})"
      }

      public override fun hashCode(): Int {
         return (((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)) * 31 + Integer.hashCode(this.time)) * 31
            + this.lever.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
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
