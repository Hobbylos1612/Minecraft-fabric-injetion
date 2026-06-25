package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.ArrayList
import java.util.Arrays
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.map.MapState
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nTicTacToeSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicTacToeSolver.kt\njooon/features/dungeons/solvers/TicTacToeSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,254:1\n2624#2,3:255\n1747#2,3:258\n1726#2,3:261\n1559#2:264\n1590#2,4:265\n1559#2:269\n1590#2,4:270\n1559#2:274\n1590#2,4:275\n*S KotlinDebug\n*F\n+ 1 TicTacToeSolver.kt\njooon/features/dungeons/solvers/TicTacToeSolver\n*L\n112#1:255,3\n195#1:258,3\n200#1:261,3\n209#1:264\n209#1:265,4\n220#1:269\n220#1:270,4\n242#1:274\n242#1:275,4\n*E\n"])
public object TicTacToeSolver {
   private final val boardPos: List<Triple<Int, Int, Int>> =
      CollectionsKt.listOf(
         arrayOf(
            Triple(8, 72, 17),
            Triple(8, 72, 16),
            Triple(8, 72, 15),
            Triple(8, 71, 17),
            Triple(8, 71, 16),
            Triple(8, 71, 15),
            Triple(8, 70, 17),
            Triple(8, 70, 16),
            Triple(8, 70, 15)
         )
      )
      private final var inTTT: Boolean
   private final var currentBestMove: Int = -1
   private final val entityPositions: MutableList<jooon.features.dungeons.solvers.TicTacToeSolver.TicTacToePlayer> = ArrayList() as java.util.List
   private final val currentBoard: Array<String?>
   private final val boardOrder: List<Int> = CollectionsKt.listOf(arrayOf(4, 0, 2, 6, 8, 1, 3, 5, 7))
   private final val winningSides: List<Triple<Int, Int, Int>> =
      CollectionsKt.listOf(
         arrayOf(Triple(0, 1, 2), Triple(3, 4, 5), Triple(6, 7, 8), Triple(0, 3, 6), Triple(1, 4, 7), Triple(2, 5, 8), Triple(0, 4, 8), Triple(2, 4, 6))
      )

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.ticTacToeSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inTTT) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.ticTacToeSolver && Utils.INSTANCE.inDungeon && inTTT) {
            INSTANCE.render(ctx)
         }
      })
   }

   private fun reset() {
      inTTT = false
      currentBestMove = -1
      entityPositions.clear()
      ArraysKt.fill$default(currentBoard, null, 0, 0, 6, null)
   }

   private fun tick() {
      try {
         if (!inTTT) {
            val e: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
            if ((if (e != null) e.type else null) != RoomTypes.PUZZLE || !(e.name == "Tic Tac Toe")) {
               return
            }

            inTTT = true
         } else {
            val var26: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
            if ((if (var26 != null) var26.type else null) != RoomTypes.PUZZLE || !(var26.name == "Tic Tac Toe")) {
               this.reset()
               return
            }
         }

         val var10000: ClientWorld = this.getMc().field_1687
         if (var10000 == null) {
            return
         }

         val var27: ClientWorld = var10000
         val var28: ClientPlayerEntity = this.getMc().field_1724
         if (var28 == null) {
            return
         }

         val player: ClientPlayerEntity = var28
         var boardChanged: Boolean = false
         val var29: java.lang.Iterable = var10000.method_18112()

         for (var30 in CollectionsKt.toList(var29)) {
            val ex: Entity = var30 as Entity
            if (var30 as Entity is ItemFrameEntity && (var30 as Entity).method_5858(player as Entity) < 256.0) {
               val var31: ItemStack = (ex as ItemFrameEntity).method_6940()
               val item: ItemStack = var31
               if (var31.method_7909() is FilledMapItem) {
                  try {
                     val var32: MapIdComponent = item.method_58694(DataComponentTypes.field_49646) as MapIdComponent
                     if (var32 != null) {
                        val var33: MapState = var27.method_17891(var32)
                        if (var33 != null && var33.field_122 != null) {
                           val idx: Int = ArraysKt.indexOf(var33.field_122, (byte)114)
                           if (idx != -1) {
                              val status: java.lang.String = if (idx == 2700) "X" else "O"
                              val exx: Int = (int)Math.floor(ex.method_23317())
                              val ey: Int = (int)ex.method_23318()
                              val ez: Int = (int)Math.floor(ex.method_23321())
                              val `$this$none$iv`: java.lang.Iterable = entityPositions
                              var var34: Boolean
                              if (entityPositions is java.util.Collection && entityPositions.isEmpty()) {
                                 var34 = true
                              } else {
                                 val var20: java.util.Iterator = `$this$none$iv`.iterator()

                                 while (true) {
                                    if (!var20.hasNext()) {
                                       var34 = true
                                       break
                                    }

                                    val it: TicTacToeSolver.TicTacToePlayer = var20.next() as TicTacToeSolver.TicTacToePlayer
                                    if (it.x == exx && it.y == ey && it.z == ez && it.status == status) {
                                       var34 = false
                                       break
                                    }
                                 }
                              }

                              if (var34) {
                                 entityPositions.removeIf({ p0: Any ->
                                    `$tmp0`(p0)
                                 })
                                 entityPositions.add(TicTacToeSolver.TicTacToePlayer(exx, ey, ez, status))
                                 boardChanged = true
                              }
                           }
                        }
                     }
                  } catch (ex: Exception) {
                     ex.printStackTrace()
                  }
               }
            }
         }

         if (boardChanged) {
            this.updateBoard()
         }
      } catch (var25: Exception) {
         var25.printStackTrace()
      }
   }

   private fun updateBoard() {
      val var10000: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      if (var10000 != null) {
         val currentRoom: DungeonRoom = var10000
         val newBoard: Array<java.lang.String> = arrayOfNulls(9)

         for (pos in entityPositions) {
            val var8: Pair = currentRoom.fromPos(pos.x, pos.z)
            if (var8 != null) {
               val idx: Int = boardPos.indexOf(Triple(var8.getFirst(), pos.y, var8.getSecond()))
               if (idx != -1) {
                  newBoard[idx] = pos.status
               }
            }
         }

         if (!Arrays.equals(newBoard, currentBoard)) {
            System.arraycopy(newBoard, 0, currentBoard, 0, 9)
            currentBestMove = this.bestMove(ArraysKt.toList(currentBoard), "O")
         }
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (currentBestMove == -1) {
            return
         }

         val var10000: MatrixStack = ctx.matrices()
         if (var10000 == null) {
            return
         }

         val var17: Camera = ctx.gameRenderer().method_19418()
         val var18: Vec3d = var17.method_71156()
         val var19: Entry = var10000.method_23760()
         val var20: Matrix4f = var19.method_23761()
         val var21: VertexConsumerProvider = ctx.consumers()
         if (var21 == null) {
            return
         }

         val var22: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
         if (var22 == null) {
            return
         }

         val var23: Triple = CollectionsKt.getOrNull(boardPos, currentBestMove) as Triple
         if (var23 == null) {
            return
         }

         val var24: Pair = var22.fromComp((var23.getFirst() as java.lang.Number).intValue() - 1, (var23.getThird() as java.lang.Number).intValue())
         if (var24 == null) {
            return
         }

         val box: Box = Box(
            (var24.getFirst() as java.lang.Number).intValue(),
            (var23.getSecond() as java.lang.Number).intValue(),
            (var24.getSecond() as java.lang.Number).intValue(),
            (var24.getFirst() as java.lang.Number).intValue() + 1.0,
            (var23.getSecond() as java.lang.Number).intValue() + 1.0,
            (var24.getSecond() as java.lang.Number).intValue() + 1.0
         )
         RenderUtils.INSTANCE.renderBoxOutlineRobust(var21, var20, var19, var18, box, 0.0F, 1.0F, 0.0F, 1.0F, 0.02F)
         if (Config.dungeonESPThroughWalls) {
            val var25: MinecraftClient = this.getMc()
            val var26: OutlineVertexConsumerProvider = (var25 as MinecraftAccessor).getRenderBuffers().method_23003()
            val var27: RenderUtils = RenderUtils.INSTANCE
            val var10006: Color = Color.GREEN
            var27.renderBoxOutlineThroughWalls(var26, var20, var19, var18, box, var10006, 0.02F)
         }
      } catch (var16: java.lang.Throwable) {
         if (System.currentTimeMillis() % 1000 < 50L) {
            var16.printStackTrace()
         }
      }
   }

   private fun isWinner(board: List<String?>, player: String): Boolean {
      val `$this$any$iv`: java.lang.Iterable = winningSides
      var var10000: Boolean
      if (winningSides is java.util.Collection && winningSides.isEmpty()) {
         var10000 = false
      } else {
         val var5: java.util.Iterator = `$this$any$iv`.iterator()

         while (true) {
            if (!var5.hasNext()) {
               var10000 = false
               break
            }

            val var7: Triple = var5.next() as Triple
            if (board.get((var7.component1() as java.lang.Number).intValue()) == player
               && board.get((var7.component2() as java.lang.Number).intValue()) == player
               && board.get((var7.component3() as java.lang.Number).intValue()) == player) {
               var10000 = true
               break
            }
         }
      }

      return var10000
   }

   private fun minMax(board: List<String?>, depth: Int, alpha: Int, beta: Int, isPlayer: Boolean): Int {
      if (this.isWinner(board, "X")) {
         return 10 - depth
      } else if (this.isWinner(board, "O")) {
         return depth - 10
      } else {
         val a: java.lang.Iterable = board
         var var10000: Boolean
         if (board is java.util.Collection && (board as java.util.Collection).isEmpty()) {
            var10000 = true
         } else {
            run label136@{
               for (`element$iv` in a) {
                  if (`element$iv` as java.lang.String == null) {
                     var10000 = false
                     return@label136
                  }
               }

               var10000 = true
            }
         }

         if (var10000) {
            return 0
         } else {
            var var25: Int = alpha
            var var26: Int = beta
            if (isPlayer) {
               var var28: Int = Integer.MIN_VALUE
               val var30: java.util.Iterator = boardOrder.iterator()

               while (var30.hasNext()) {
                  val var32: Int = (var30.next() as java.lang.Number).intValue()
                  if (board.get(var32) == null) {
                     val var39: java.lang.Iterable = board
                     val var40: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(board, 10))
                     var var42: Int = 0

                     for (var44 in var39) {
                        val var45: Int = var42++
                        if (var45 < 0) {
                           CollectionsKt.throwIndexOverflow()
                        }

                        var40.add(if (var32 == var45) "X" else var44 as java.lang.String)
                     }

                     val var37: Int = this.minMax(var40 as MutableList<java.lang.String>, depth + 1, var25, var26, false)
                     var28 = Math.max(var28, var37)
                     var25 = Math.max(var25, var37)
                     if (var26 <= var25) {
                        break
                     }
                  }
               }

               return var28
            } else {
               var var27: Int = Integer.MAX_VALUE
               val var29: java.util.Iterator = boardOrder.iterator()

               while (var29.hasNext()) {
                  val var31: Int = (var29.next() as java.lang.Number).intValue()
                  if (board.get(var31) == null) {
                     val `$this$mapIndexedTo$iv$iv`: java.lang.Iterable = board
                     val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(board, 10))
                     var `index$iv$iv`: Int = 0

                     for (`item$iv$iv` in `$this$mapIndexedTo$iv$iv`) {
                        val var20: Int = `index$iv$iv`++
                        if (var20 < 0) {
                           CollectionsKt.throwIndexOverflow()
                        }

                        `destination$iv$iv`.add(if (var31 == var20) "O" else `item$iv$iv` as java.lang.String)
                     }

                     val var35: Int = this.minMax(`destination$iv$iv` as MutableList<java.lang.String>, depth + 1, var25, var26, true)
                     var27 = Math.min(var27, var35)
                     var26 = Math.min(var26, var35)
                     if (var26 <= var25) {
                        break
                     }
                  }
               }

               return var27
            }
         }
      }
   }

   private fun bestMove(board: List<String?>, player: String): Int {
      if (CollectionsKt.filterNotNull(board).size() == 1) {
         return if (board.get(4) == null) 4 else 0
      } else {
         val maximizing: Boolean = player == "X"
         var bestScore: Int = if (maximizing) Integer.MIN_VALUE else Integer.MAX_VALUE
         var bestMove: Int = -1
         val var6: java.util.Iterator = boardOrder.iterator()

         while (var6.hasNext()) {
            val idx: Int = (var6.next() as java.lang.Number).intValue()
            if (board.get(idx) == null) {
               val `$this$mapIndexedTo$iv$iv`: java.lang.Iterable = board
               val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(board, 10))
               var `index$iv$iv`: Int = 0

               for (`item$iv$iv` in `$this$mapIndexedTo$iv$iv`) {
                  val var17: Int = `index$iv$iv`++
                  if (var17 < 0) {
                     CollectionsKt.throwIndexOverflow()
                  }

                  `destination$iv$iv`.add(if (idx == var17) player else `item$iv$iv` as java.lang.String)
               }

               val var22: Int = this.minMax(`destination$iv$iv` as MutableList<java.lang.String>, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, !(player == "X"))
               if (maximizing) {
                  if (var22 > bestScore) {
                     bestScore = var22
                     bestMove = idx
                  }
               } else if (var22 < bestScore) {
                  bestScore = var22
                  bestMove = idx
               }
            }
         }

         return bestMove
      }
   }

   public data class TicTacToePlayer(x: Int, y: Int, z: Int, status: String) {
      public final val x: Int
      public final val y: Int
      public final val z: Int
      public final val status: String

      init {
         this.x = x
         this.y = y
         this.z = z
         this.status = status
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

      public operator fun component4(): String {
         return this.status
      }

      public fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z, status: String = this.status): jooon.features.dungeons.solvers.TicTacToeSolver.TicTacToePlayer {
         return TicTacToeSolver.TicTacToePlayer(x, y, z, status)
      }

      public override fun toString(): String {
         return "TicTacToePlayer(x=${this.x}, y=${this.y}, z=${this.z}, status=${this.status})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)) * 31 + this.status.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is TicTacToeSolver.TicTacToePlayer
               && this.x == (other as TicTacToeSolver.TicTacToePlayer).x
               && this.y == (other as TicTacToeSolver.TicTacToePlayer).y
               && this.z == (other as TicTacToeSolver.TicTacToePlayer).z
               && this.status == (other as TicTacToeSolver.TicTacToePlayer).status
            }
      }
   }
}
