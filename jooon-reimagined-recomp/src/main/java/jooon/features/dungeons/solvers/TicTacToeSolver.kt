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

object TicTacToeSolver {
   private val boardPos: List<Triple<Int, Int, Int>> =
      listOf(
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
      private var inTTT: Boolean
   private var currentBestMove: Int = -1
   private val entityPositions: MutableList<jooon.features.dungeons.solvers.TicTacToeSolver.TicTacToePlayer> = ArrayList() as java.util.List
   private val currentBoard: Array<String?>
   private val boardOrder: List<Int> = listOf(arrayOf(4, 0, 2, 6, 8, 1, 3, 5, 7))
   private val winningSides: List<Triple<Int, Int, Int>> =
      listOf(
         arrayOf(Triple(0, 1, 2), Triple(3, 4, 5), Triple(6, 7, 8), Triple(0, 3, 6), Triple(1, 4, 7), Triple(2, 5, 8), Triple(0, 4, 8), Triple(2, 4, 6))
      )

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.ticTacToeSolver && Utils.inDungeon) {
            tick()
         } else {
            if (inTTT) {
               reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.ticTacToeSolver && Utils.inDungeon && inTTT) {
            render(ctx)
         }
      })
   }

   private fun reset() {
      inTTT = false
      currentBestMove = -1
      entityPositions.clear()
      fill$default(currentBoard, null, 0, 0, 6, null)
   }

   private fun tick() {
      try {
         if (!inTTT) {

            if ((if (e != null) e.type else null) != RoomTypes.PUZZLE || !(e.name == "Tic Tac Toe")) {
return return
            }

            inTTT = true
         } else {

            if ((if (var26 != null) var26.type else null) != RoomTypes.PUZZLE || !(var26.name == "Tic Tac Toe")) {
               this.reset()
return return
            }
         }

         if (var10000 == null) {
return return
         }


         if (var28 == null) {
return return
         }

         var boardChanged: Boolean = false
         val var29: java.lang.Iterable = var10000.getEntities()

         for (var30 in toList(var29)) {

            if (var30 as Entity is ItemFrameEntity && (var30 as Entity).squaredDistanceTo(player as Entity) < 256.0) {


               if (var31.getItem() is FilledMapItem) {
                  try {

                     if (var32 != null) {

                        if (var33 != null && var33.colors != null) {

                           if (idx != -1) {




                              val `this$iv`: java.lang.Iterable = entityPositions
                              var var34: Boolean
                              if (entityPositions is java.util.Collection && entityPositions.isEmpty()) {
                                 var34 = true
                              } else {
                                 val var20: java.util.Iterator = `this$iv`.iterator()

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
                                    ``(p0)
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

      if (var10000 != null) {

         val newBoard: Array<String> = arrayOfNulls(9)

         for (pos in entityPositions) {

            if (var8 != null) {

               if (idx != -1) {
                  newBoard[idx] = pos.status
               }
            }
         }

         if (!Arrays.equals(newBoard, currentBoard)) {
            System.arraycopy(newBoard, 0, currentBoard, 0, 9)
            currentBestMove = this.bestMove(toList(currentBoard), "O")
         }
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (currentBestMove == -1) {
return return
         }

         if (var10000 == null) {
return return
         }





         if (var21 == null) {
return return
         }

         if (var22 == null) {
return return
         }

         if (var23 == null) {
return return
         }

         if (var24 == null) {
return return
         }

            (var24.getFirst() as java.lang.Number).intValue(),
            (var23.getSecond() as java.lang.Number).intValue(),
            (var24.getSecond() as java.lang.Number).intValue(),
            (var24.getFirst() as java.lang.Number).intValue() + 1.0,
            (var23.getSecond() as java.lang.Number).intValue() + 1.0,
            (var24.getSecond() as java.lang.Number).intValue() + 1.0
         )
         RenderUtils.renderBoxOutlineRobust(var21, var20, var19, var18, box, 0.0F, 1.0F, 0.0F, 1.0F, 0.02F)
         if (Config.dungeonESPThroughWalls) {




            var27.renderBoxOutlineThroughWalls(var26, var20, var19, var18, box, var10006, 0.02F)
         }
      } catch (var16: java.lang.Throwable) {
         if (System.currentTimeMillis() % 1000 < 50L) {
            var16.printStackTrace()
         }
      }
   }

   private fun isWinner(board: List<String?>, player: String): Boolean {
      val `this$iv`: java.lang.Iterable = winningSides
      var var10000: Boolean
      if (winningSides is java.util.Collection && winningSides.isEmpty()) {
         var10000 = false
      } else {
         val var5: java.util.Iterator = `this$iv`.iterator()

         while (true) {
            if (!var5.hasNext()) {
               var10000 = false
break
            }

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
                  if (`element$iv` as String == null) {
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

                  if (board.get(var32) == null) {
                     val var39: java.lang.Iterable = board
                     val var40: java.util.Collection = ArrayList(board.count().coerceAtLeast(10))
                     var var42: Int = 0

                     for (var44 in var39) {

                        if (var45 < 0) {
                           throwIndexOverflow()
                        }

                        var40.add(if (var32 == var45) "X" else var44 as String)
                     }

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

                  if (board.get(var31) == null) {
                     val `this$iv$iv`: java.lang.Iterable = board
                     val `destination$iv$iv`: java.util.Collection = ArrayList(board.count().coerceAtLeast(10))
                     var `index$iv$iv`: Int = 0

                     for (`item$iv$iv` in `this$iv$iv`) {

                        if (var20 < 0) {
                           throwIndexOverflow()
                        }

                        `destination$iv$iv`.add(if (var31 == var20) "O" else `item$iv$iv` as String)
                     }

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
      if (filterNotNull(board).size() == 1) {
         return if (board.get(4) == null) 4 else 0
      } else {

         var bestScore: Int = if (maximizing) Integer.MIN_VALUE else Integer.MAX_VALUE
         var bestMove: Int = -1
         val var6: java.util.Iterator = boardOrder.iterator()

         while (var6.hasNext()) {

            if (board.get(idx) == null) {
               val `this$iv$iv`: java.lang.Iterable = board
               val `destination$iv$iv`: java.util.Collection = ArrayList(board.count().coerceAtLeast(10))
               var `index$iv$iv`: Int = 0

               for (`item$iv$iv` in `this$iv$iv`) {

                  if (var17 < 0) {
                     throwIndexOverflow()
                  }

                  `destination$iv$iv`.add(if (idx == var17) player else `item$iv$iv` as String)
               }

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

   data class TicTacToePlayer(x: Int, y: Int, z: Int, status: String) {
      val x: Int
      val y: Int
      val z: Int
      val status: String

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

      fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z, status: String = this.status): jooon.features.dungeons.solvers.TicTacToeSolver.TicTacToePlayer {
         return TicTacToeSolver.TicTacToePlayer(x, y, z, status)
      }

      override fun toString(): String {
         return "TicTacToePlayer(x=${this.x}, y=${this.y}, z=${this.z}, status=${this.status})"
      }

      override fun hashCode(): Int {
         return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)) * 31 + this.status.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
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
