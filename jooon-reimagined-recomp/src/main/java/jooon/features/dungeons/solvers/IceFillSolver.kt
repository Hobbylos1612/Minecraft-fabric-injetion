package jooon.features.dungeons.solvers

import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Arrays
import java.util.Deque
import java.util.LinkedHashMap
import java.util.LinkedList
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.util.WorldUtils
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.concurrent.ThreadsKt
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

object IceFillSolver {
   private var inIce: Boolean
   private val platforms: List<jooon.features.dungeons.solvers.IceFillSolver.IcePlatform> =
      listOf(
         arrayOf(
            IceFillSolver.IcePlatform(
               IceFillSolver.Coord(15, 69, 10), IceFillSolver.Coord(15, 69, 7), IceFillSolver.Coord(14, 69, 7), IceFillSolver.Coord(16, 69, 9)
            ),
            IceFillSolver.IcePlatform(
               IceFillSolver.Coord(15, 70, 17), IceFillSolver.Coord(15, 70, 12), IceFillSolver.Coord(13, 70, 12), IceFillSolver.Coord(17, 70, 16)
            ),
            IceFillSolver.IcePlatform(
               IceFillSolver.Coord(15, 71, 26), IceFillSolver.Coord(15, 71, 19), IceFillSolver.Coord(12, 71, 19), IceFillSolver.Coord(18, 71, 25)
            )
         )
      )

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.iceFillSolver && Utils.inDungeon) {

            if ((if (room != null) room.type else null) === RoomTypes.PUZZLE && room.name == "Ice Fill") {
               if (!inIce) {
                  inIce = true

                  for (`element$iv` in platforms) {
                     (`element$iv` as IceFillSolver.IcePlatform).rescan(room)
                  }
               }
            } else if (inIce) {
               inIce = false
            }
         } else {
            if (inIce) {
               inIce = false
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.iceFillSolver && Utils.inDungeon && inIce) {
            render(ctx)
         }
      })
   }

   fun onBlockUpdate(pos: BlockPos, state: BlockState) {
      if (inIce) {

         if (var10000 != null) {


            for (`element$iv` in platforms) {
               val platform: IceFillSolver.IcePlatform = `element$iv` as IceFillSolver.IcePlatform
               if ((`element$iv` as IceFillSolver.IcePlatform).contains(room, pos.getX(), pos.getY(), pos.getZ())) {
                  var sol: Boolean = false
                  if (state.getBlock() == Blocks.PACKED_ICE) {
                     sol = platform.removeBlock(room, pos.getX(), pos.getZ())
                  } else if (state.isAir()) {
                     platform.reset(room)
                     sol = true
                  }

                  if (sol) {
                     platform.asyncSolve(room)
                  }
               }
            }
         }
      }
   }

   fun onSectionBlocksUpdate(packet: ChunkDeltaUpdateS2CPacket) {
      packet.visitUpdates({ p: BlockPos, s: BlockState ->
         onBlockUpdate(p, s)
      })
   }

   private fun render(ctx: WorldRenderContext) {

      if (var10000 != null) {








         if (var29 != null) {


            for (`element$iv` in platforms) {

               if (var30 != null) {
                  val var31: java.util.List = toList(var30)
                  if (var31 != null && !var31.isEmpty()) {
                     var prev: Any = null

                     for (`element$ivx` in var31) {
                        val curr: IceFillSolver.Coord = `element$ivx` as IceFillSolver.Coord
                        val p: IceFillSolver.Coord = (IceFillSolver.Coord)prev
                        prev = curr
                        if (p != null) {
                           RenderUtils.INSTANCE
                              .renderLineRobust(
                                 consumers,
                                 posMat,
                                 entry,
                                 camPos,
                                 Vec3d(p.x.toDouble() + 0.5, p.y.toDouble() + 1.1, p.z.toDouble() + 0.5),
                                 Vec3d(curr.x.toDouble() + 0.5, curr.y.toDouble() + 1.1, curr.z.toDouble() + 0.5),
                                 0.0F,
                                 1.0F,
                                 0.0F,
                                 1.0F,
                                 0.08F
                              )
                           }
                     }

                     val var23: IceFillSolver.Coord = first(var31) as IceFillSolver.Coord
                     val var24: IceFillSolver.Coord = last(var31) as IceFillSolver.Coord
                     RenderUtils.INSTANCE
                        .renderBoxFill(
                           consumers,
                           posMat,
                           entry,
                           camPos,
                           Box(var23.x.toDouble(), var23.y.toDouble(), var23.z.toDouble(), var23.x.toDouble() + 1.0, var23.y.toDouble() + 1.1, var23.z.toDouble() + 1.0),
                           0.0F,
                           0.0F,
                           1.0F,
                           0.4F
                        )
                        RenderUtils.INSTANCE
                        .renderBoxOutlineRobust(
                           consumers,
                           posMat,
                           entry,
                           camPos,
                           Box(var24.x.toDouble(), var24.y.toDouble(), var24.z.toDouble(), var24.x.toDouble() + 1.0, var24.y.toDouble() + 1.1, var24.z.toDouble() + 1.0),
                           1.0F,
                           0.0F,
                           0.0F,
                           1.0F,
                           0.02F
                        )
                     }
               }
            }
         }
      }
   }

   private data class Coord(x: Int, y: Int, z: Int) {
      val x: Int
      val y: Int
      val z: Int

      init {
         this.x = x
         this.y = y
         this.z = z
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

      fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z): jooon.features.dungeons.solvers.IceFillSolver.Coord {
         return IceFillSolver.Coord(x, y, z)
      }

      override fun toString(): String {
         return "Coord(x=${this.x}, y=${this.y}, z=${this.z})"
      }

      override fun hashCode(): Int {
         return (Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is IceFillSolver.Coord
               && this.x == (other as IceFillSolver.Coord).x
               && this.y == (other as IceFillSolver.Coord).y
               && this.z == (other as IceFillSolver.Coord).z
            }
      }
   }

   @SourceDebugExtension(["SMAP\nIceFillSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IceFillSolver.kt\njooon/features/dungeons/solvers/IceFillSolver$IcePlatform\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,179:1\n215#2:180\n216#2:185\n1855#3,2:181\n1603#3,9:192\n1855#3:201\n1856#3:203\n1612#3:204\n1054#3:205\n1855#3,2:206\n37#4,2:183\n1#5:186\n1#5:202\n13309#6,2:187\n3792#6:189\n4307#6,2:190\n*S KotlinDebug\n*F\n+ 1 IceFillSolver.kt\njooon/features/dungeons/solvers/IceFillSolver$IcePlatform\n*L\n106#1:180\n106#1:185\n108#1:181,2\n163#1:192,9\n163#1:201\n163#1:203\n163#1:204\n164#1:205\n165#1:206,2\n111#1:183,2\n163#1:202\n146#1:187,2\n163#1:189\n163#1:190,2\n*E\n"])
   private class IcePlatform(end: jooon.features.dungeons.solvers.IceFillSolver.Coord,
      start: jooon.features.dungeons.solvers.IceFillSolver.Coord,
      corner1: jooon.features.dungeons.solvers.IceFillSolver.Coord,
      corner2: jooon.features.dungeons.solvers.IceFillSolver.Coord
   ) {
      val end: jooon.features.dungeons.solvers.IceFillSolver.Coord
      val start: jooon.features.dungeons.solvers.IceFillSolver.Coord
      val corner1: jooon.features.dungeons.solvers.IceFillSolver.Coord
      val corner2: jooon.features.dungeons.solvers.IceFillSolver.Coord
      private val maxId: Int
      private val nodes: Array<jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.CompWorld?>
      private var mutableNodes: Array<jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.CompWorld?>
      var solution: Deque<jooon.features.dungeons.solvers.IceFillSolver.Coord>?
      private var solveTask: Thread?

      init {
         this.end = end
         this.start = start
         this.corner1 = corner1
         this.corner2 = corner2
         this.maxId = (this.corner2.x - this.corner1.x + 1) * (this.corner2.z - this.corner1.z + 1)
         this.nodes = arrayOfNulls(this.maxId + 1)
         val var10001: Array<Any> = Arrays.copyOf(this.nodes, this.nodes.length)
         this.mutableNodes = var10001 as Array<IceFillSolver.IcePlatform.CompWorld>
      }

      private fun idAt(x: Int, z: Int): Int {
         return if (z == this.end.z) this.maxId else x - this.corner1.x + (this.corner2.x - this.corner1.x + 1) * (z - this.corner1.z)
      }

      private fun parityOf(x: Int, z: Int): Int {
         return x + z and 1
      }

      fun rescan(room: DungeonRoom) {
         fill$default(this.nodes, null, 0, 0, 6, null)
         val temp: java.util.Map = LinkedHashMap()
         temp.put(this.maxId, IceFillSolver.Coord(this.end.x, this.end.y, this.end.z))
         var `this$iv`: Int = this.corner1.x
         val ``: Int = this.corner2.x
         if (`this$iv` <= ``) {
            while (true) {
               var cz: Int = this.corner1.z
               val `element$iv`: Int = this.corner2.z
               if (cz <= `element$iv`) {
                  while (true) {

                     if (var10000 != null) {

                           .getBlockState(
                              (var10000.getFirst() as java.lang.Number).intValue(), this.end.y + 1, (var10000.getSecond() as java.lang.Number).intValue()
                           )
                           if (var31 != null && var31.isAir()) {
                           temp.put(this.idAt(`this$iv`, cz), IceFillSolver.Coord(`this$iv`, this.end.y, cz))
                        }
                     }

                     if (cz == `element$iv`) {
break
                     }

                     cz++
                  }
               }

               if (`this$iv` == ``) {
break
               }

               `this$iv`++
            }
         }

         for (var25 in temp.entrySet()) {

            val comp: IceFillSolver.Coord = var25.getValue() as IceFillSolver.Coord

            val n: java.util.List = ArrayList()

            for (`element$ivx` in listOf(arrayOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)))) {



               if (this.contains(comp.x + dx, comp.z + dz) && temp.containsKey(nid)) {
                  n.add(nid)
               }
            }

            this.nodes[id] = IceFillSolver.IcePlatform.CompWorld(comp, var32, this.parityOf(comp.x, comp.z), n.toArray(arrayOfNulls(0)))
         }

         this.reset(room)
         this.asyncSolve(room)
      }

      fun reset(room: DungeonRoom) {
         val var10001: Array<Any> = Arrays.copyOf(this.nodes, this.nodes.length)
         this.mutableNodes = var10001 as Array<IceFillSolver.IcePlatform.CompWorld>
         var cx: Int = this.corner1.x

         if (cx <= var3) {
            while (true) {
               var cz: Int = this.corner1.z

               if (cz <= var5) {
                  while (true) {

                     if (var10000 != null) {

                           .getBlockState(
                              (var10000.getFirst() as java.lang.Number).intValue(), this.end.y, (var10000.getSecond() as java.lang.Number).intValue()
                           )
                           if ((if (var7 != null) var7.getBlock() else null) == Blocks.PACKED_ICE) {
                           this.mutableNodes[this.idAt(cx, cz)] = null
                        }
                     }

                     if (cz == var5) {
break
                     }

                     cz++
                  }
               }

               if (cx == var3) {
break
               }

               cx++
            }
         }

         this.solution = null
      }

      private fun contains(x: Int, z: Int): Boolean {
         label50@
         if (x != this.end.x || z != this.end.z) {
            return x <= this.corner2.x && this.corner1.x <= x && z <= this.corner2.z && this.corner1.z <= z
         } else {
            return true
         }
      }

      fun contains(room: DungeonRoom, x: Int, y: Int, z: Int): Boolean {

         return var10000 != null
            && this.end.y == y
            && this.contains((var10000.getFirst() as java.lang.Number).intValue(), (var10000.getSecond() as java.lang.Number).intValue())
         }

      fun removeBlock(room: DungeonRoom, wx: Int, wz: Int): Boolean {

         if (var10000 == null) {
            return false
         } else {
            this.mutableNodes[this.idAt((var10000.getFirst() as java.lang.Number).intValue(), (var10000.getSecond() as java.lang.Number).intValue())] = null
            if (this.solution == null) {
               return true
            } else {

               if (!this.solution.isEmpty()) {
                  val var10: IceFillSolver.Coord = s.peekFirst() as IceFillSolver.Coord
                  if (var10 != null && var10.x == wx && var10.z == wz) {
                     s.removeFirst()
                     return false
                  }

                  val itx: IceFillSolver.Coord = s.peekLast() as IceFillSolver.Coord
                  if (itx != null && itx.x == wx && itx.z == wz) {
                     s.removeLast()
                     return false
                  }
               }

               this.solution = null
               return true
            }
         }
      }

      fun asyncSolve(room: DungeonRoom) {
         if (this.solveTask != null) {
            this.solveTask.interrupt()
         }

         this.solveTask = ThreadsKt.thread$default(false, false, null, "IceFill-Solver", 0, { 
            try {

               if (s != null) {
                  `this$0`.solution = s
               }
            } catch (var3: InterruptedException) {
            }
return Unit
         }, 23, null)
      }

      private fun solveManual(room: DungeonRoom): Deque<jooon.features.dungeons.solvers.IceFillSolver.Coord>? {
         var total: Int = 0
         var odd: Int = 0

         for (queue in this.mutableNodes) {
            if (queue != null) {
               total++
               odd += queue.parity
            }
         }

         if (total == 0) {
            return LinkedList<>()
         } else if (Math.abs(odd - (total - odd)) >= 2) {
            return null
         } else {

            var var10000: IceFillSolver.IcePlatform.CompWorld
            if (var30 != null && Math.abs(var30.getY() - (this.end.y + 1).toDouble()) < 0.8) {


               var10000 = if (var34 != null
                     && this.contains((var34.getFirst() as java.lang.Number).intValue(), (var34.getSecond() as java.lang.Number).intValue()))
                  this.mutableNodes[this.idAt((var34.getFirst() as java.lang.Number).intValue(), (var34.getSecond() as java.lang.Number).intValue())]
return else
return null
               } else {
               var10000 = null
            }

            var10000 = var10000
            if (var10000 == null) {
               var10000 = this.mutableNodes[this.idAt(this.start.x, this.start.z)]
               if (var10000 == null) {
                  return null
               }
            }

            var35.add(
               IceFillSolver.IcePlatform.Destination(
                  var10000,
                  -1,
                  1,
                  0,
                  1L shl this.idAt(var10000.comp.x, var10000.comp.z),
                  IceFillSolver.IcePlatform.Step(
                     IceFillSolver.Coord(
                        (var10000.world.getFirst() as java.lang.Number).intValue(),
                        var10000.comp.y,
                        (var10000.world.getSecond() as java.lang.Number).intValue()
                     ),
return null
                  )
               )
            )
            var var36: IceFillSolver.IcePlatform.Step = null
            var var37: Int = Integer.MAX_VALUE


            while (!var35.isEmpty() && !Thread.interrupted() && System.currentTimeMillis() - var38 <= 1000L) {
               val d: IceFillSolver.IcePlatform.Destination = var35.removeLast() as IceFillSolver.IcePlatform.Destination
               if (d.size == total) {
                  if (d.cost < var37) {
                     var37 = d.cost
                     var36 = d.step
                  }
               } else if (d.cost < var37) {
                  val `this$iv$iv`: Array<Any> = d.data.neighbors
                  var `element$iv`: java.util.Collection = ArrayList()

                  for (`element$iv$iv` in `this$iv$iv`) {
                     if ((d.mask and 1L shl (`element$iv$iv` as java.lang.Number).intValue()) == 0L) {
                        `element$iv`.add(`element$iv$iv`)
                     }
                  }

                  val var45: java.lang.Iterable = `element$iv` as java.util.List
                  `element$iv` = ArrayList()

                  for (var56 in var45) {

                     var10000 = this.mutableNodes[it]
                     if (this.mutableNodes[it] != null) {
                        `element$iv`.add(var10000)
                     }
                  }

                  for (var48 in sortedWith(`element$iv` as java.util.List, IceFillSolver$IcePlatform$solveManual$$inlined$sortedByDescending$1(d))) {

                        - d.data.comp.x
                        + ((var48 as IceFillSolver.IcePlatform.CompWorld).comp.z - d.data.comp.z) * 100
                        var35.add(
                        IceFillSolver.IcePlatform.Destination(
                           var48 as IceFillSolver.IcePlatform.CompWorld,
                           var54,
                           d.size + 1,
                           d.cost + (if (var54 == d.dir) 0 else 1),
                           d.mask or 1L shl this.idAt(
                              (var48 as IceFillSolver.IcePlatform.CompWorld).comp.x, (var48 as IceFillSolver.IcePlatform.CompWorld).comp.z
                           ),
                           IceFillSolver.IcePlatform.Step(
                              IceFillSolver.Coord(
                                 ((var48 as IceFillSolver.IcePlatform.CompWorld).world.getFirst() as java.lang.Number).intValue(),
                                 (var48 as IceFillSolver.IcePlatform.CompWorld).comp.y,
                                 ((var48 as IceFillSolver.IcePlatform.CompWorld).world.getSecond() as java.lang.Number).intValue()
                              ),
                              d.step
                           )
                        )
                     )
                  }
               }
            }

            return if (var36 != null) var36.toDeque() else null
         }
      }

      class CompWorld(comp: jooon.features.dungeons.solvers.IceFillSolver.Coord, world: Pair<Int, Int>, parity: Int, vararg neighbors: Any) {
         val comp: jooon.features.dungeons.solvers.IceFillSolver.Coord
         val world: Pair<Int, Int>
         val parity: Int
         val neighbors: Array<Int>

         init {
            this.comp = comp
            this.world = world
            this.parity = parity
            this.neighbors = neighbors
         }
      }

      data class Destination(data: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.CompWorld,
         dir: Int,
         size: Int,
         cost: Int,
         mask: Long,
         step: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step
      ) {
         val data: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.CompWorld
         val dir: Int
         val size: Int
         val cost: Int
         val mask: Long
         val step: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step

         init {
            this.data = data
            this.dir = dir
            this.size = size
            this.cost = cost
            this.mask = mask
            this.step = step
         }

         public operator fun component1(): jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.CompWorld {
            return this.data
         }

         public operator fun component2(): Int {
            return this.dir
         }

         public operator fun component3(): Int {
            return this.size
         }

         public operator fun component4(): Int {
            return this.cost
         }

         public operator fun component5(): Long {
            return this.mask
         }

         public operator fun component6(): jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step {
            return this.step
         }

         fun copy(
            data: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.CompWorld = this.data,
            dir: Int = this.dir,
            size: Int = this.size,
            cost: Int = this.cost,
            mask: Long = this.mask,
            step: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step = this.step
         ): jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Destination {
            return IceFillSolver.IcePlatform.Destination(data, dir, size, cost, mask, step)
         }

         override fun toString(): String {
            return "Destination(data=${this.data}, dir=${this.dir}, size=${this.size}, cost=${this.cost}, mask=${this.mask}, step=${this.step})"
         }

         override fun hashCode(): Int {
            return (
                     (((this.data.hashCode() * 31 + Integer.hashCode(this.dir)) * 31 + Integer.hashCode(this.size)) * 31 + Integer.hashCode(this.cost)) * 31
                        + java.lang.Long.hashCode(this.mask)
                  )
                  * 31
               + this.step.hashCode()
            }

         override operator fun equals(other: Any?): Boolean {
            label52@
            if (this === other) {
               return true
            } else {
               return other is IceFillSolver.IcePlatform.Destination
                  && this.data == (other as IceFillSolver.IcePlatform.Destination).data
                  && this.dir == (other as IceFillSolver.IcePlatform.Destination).dir
                  && this.size == (other as IceFillSolver.IcePlatform.Destination).size
                  && this.cost == (other as IceFillSolver.IcePlatform.Destination).cost
                  && this.mask == (other as IceFillSolver.IcePlatform.Destination).mask
                  && this.step == (other as IceFillSolver.IcePlatform.Destination).step
               }
         }
      }

      data class Step(coord: jooon.features.dungeons.solvers.IceFillSolver.Coord,
         parent: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step?
      ) {
         val coord: jooon.features.dungeons.solvers.IceFillSolver.Coord
         val parent: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step?

         init {
            this.coord = coord
            this.parent = parent
         }

         fun toDeque(): Deque<jooon.features.dungeons.solvers.IceFillSolver.Coord> {


            // $VF: Unable to resugar Kotlin loop from Java for loop
            var c: IceFillSolver.IcePlatform.Step = this
            while (true) {
               if (c != null) break
               l.addFirst(c.coord)

               c = c.parent
            }

            return l
         }

         public operator fun component1(): jooon.features.dungeons.solvers.IceFillSolver.Coord {
            return this.coord
         }

         public operator fun component2(): jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step? {
            return this.parent
         }

         fun copy(
            coord: jooon.features.dungeons.solvers.IceFillSolver.Coord = this.coord,
            parent: jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step? = this.parent
         ): jooon.features.dungeons.solvers.IceFillSolver.IcePlatform.Step {
            return IceFillSolver.IcePlatform.Step(coord, parent)
         }

         override fun toString(): String {
            return "Step(coord=${this.coord}, parent=${this.parent})"
         }

         override fun hashCode(): Int {
            return this.coord.hashCode() * 31 + (if (this.parent == null) 0 else this.parent.hashCode())
         }

         override operator fun equals(other: Any?): Boolean {
            label28@
            if (this === other) {
               return true
            } else {
               return other is IceFillSolver.IcePlatform.Step
                  && this.coord == (other as IceFillSolver.IcePlatform.Step).coord
                  && this.parent == (other as IceFillSolver.IcePlatform.Step).parent
               }
         }
      }
   }
}
