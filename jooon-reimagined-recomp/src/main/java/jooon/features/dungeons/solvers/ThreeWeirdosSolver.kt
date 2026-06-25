package jooon.features.dungeons.solvers

import com.mojang.authlib.GameProfile
import java.awt.Color
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.network.message.SignedMessage
import net.minecraft.network.message.MessageType.Parameters
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

object ThreeWeirdosSolver {
   private val npcRegex: Regex = Regex("^\\[NPC\\] (\\w+): (.*)")
   private val solutions: List<Regex> =
      listOf(
         arrayOf(
            Regex("The reward is not in my chest!"),
            Regex("At least one of them is lying, and the reward is not in \\w+'s chest.?"),
            Regex("My chest doesn't have the reward\\. We are all telling the truth.?"),
            Regex("My chest has the reward and I'm telling the truth!"),
            Regex("The reward isn't in any of our chests.?"),
            Regex("Both of them are telling the truth\\. Also, \\w+ has the reward in their chest.?")
         )
      )
      private val wrongAnswers: List<Regex> =
      listOf(
         arrayOf(
            Regex("One of us is telling the truth!"),
            Regex("They are both telling the truth\\. The reward isn't in \\w+'s chest."),
            Regex("We are all telling the truth!"),
            Regex("\\w+ is telling the truth and the reward is in his chest."),
            Regex("My chest doesn't have the reward. At least one of the others is telling the truth!"),
            Regex("One of the others is lying."),
            Regex("They are both telling the truth, the reward is in \\w+'s chest."),
            Regex("They are both lying, the reward is in my chest!"),
            Regex("The reward is in my chest."),
            Regex("The reward is not in my chest\\. They are both lying."),
            Regex("\\w+ is telling the truth."),
            Regex("My chest has the reward.")
         )
      )
      private val answers: CopyOnWriteArrayList<jooon.features.dungeons.solvers.ThreeWeirdosSolver.AnswerData> = CopyOnWriteArrayList()
   private var inWeirdos: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.threeWeirdosSolver && Utils.inDungeon) {
            tick()
         } else {
            if (inWeirdos) {
               reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.threeWeirdosSolver && Utils.inDungeon && inWeirdos) {
            render(ctx)
         }
      })
      ClientReceiveMessageEvents.CHAT
         .register(
            { message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         ClientReceiveMessageEvents.GAME
         .register(
            { message: Text, var1: Boolean ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
      }

   private fun reset() {
      inWeirdos = false
      answers.clear()
   }

   private fun tick() {

      if ((if (currentRoom != null) currentRoom.type else null) != RoomTypes.PUZZLE || !(currentRoom.name == "Three Weirdos") && !(currentRoom.name == "Quiz")) {
         if (inWeirdos) {
            this.reset()
         }
      } else {
         inWeirdos = true
      }
   }

   private fun handleChatMessage(message: String) {

      if (var10000 != null) {


         val isWrong: java.lang.Iterable = solutions
         var var40: Boolean
         if (solutions is java.util.Collection && solutions.isEmpty()) {
            var40 = false
         } else {
            val entities: java.util.Iterator = isWrong.iterator()

            while (true) {
               if (!entities.hasNext()) {
                  var40 = false
break
               }

               if ((entities.next() as Regex).matches(npcMsg)) {
                  var40 = true
break
               }
            }
         }

         val `this$ivx`: java.lang.Iterable = wrongAnswers
         var var41: Boolean
         if (wrongAnswers is java.util.Collection && wrongAnswers.isEmpty()) {
            var41 = false
         } else {
            val var28: java.util.Iterator = `this$ivx`.iterator()

            while (true) {
               if (!var28.hasNext()) {
                  var41 = false
break
               }

               if ((var28.next() as Regex).matches(npcMsg)) {
                  var41 = true
break
               }
            }
         }

         if (var40 || var41) {

            if (var42 == null) {
return return
            }

            val var43: java.lang.Iterable = var42.getEntities()
            var var14: java.util.Iterator = toList(var43).iterator()

            while (true) {
               if (!var14.hasNext()) {
                  var48 = null
break
               }

               val `element$iv`: Any = var14.next()

               var var44: Boolean
               if (!((`element$iv` as Entity).getType() == EntityType.ARMOR_STAND)) {
                  var44 = false
               } else {
                  run label161@{

                     if (var45 != null) {

                        if (var46 != null) {
                           val var47: String
                           if (!startsWith$default(var46, "{\"text\":\"", false, 2, null)
                              && !contains$default(var46, "\"text\"", false, 2, null)) {
                              var47 = Utils.stripColor(var46)
                           } else {

                              if (start != -1) {


                                 if (e > s) {
                                    var47 = var46.substring(s, e)
                                 } else {
                                    var47 = Utils.stripColor(var46)
                                 }
                              } else {
                                 var47 = Utils.stripColor(var46)
                              }
                           }

                           var44 = var47.contains(npcName, true)
                           return@label161
                        }
                     }

                     var44 = false
                  }
               }

               if (var44) {
                  var48 = `element$iv`
break
               }
            }

            if (var48 as Entity == null) {
return return
            }


            if (var50 == null) {
return return
            }

            val var33: java.lang.Iterable = answers
            var var51: Boolean
            if (answers is java.util.Collection && answers.isEmpty()) {
               var51 = true
            } else {
               var14 = var33.iterator()

               while (true) {
                  if (!var14.hasNext()) {
                     var51 = true
break
                  }

                  if ((var14.next() as ThreeWeirdosSolver.AnswerData).npcName == npcName) {
                     var51 = false
break
                  }
               }
            }

            if (var51) {


               var52.add(
                  ThreeWeirdosSolver.AnswerData(
                     npcName, var10004, Vec3d(var50.getX().toDouble(), var50.getY().toDouble(), var50.getZ().toDouble()), var40
                  )
               )
            }
         }
      }
   }

   fun findNearbyChest(pos: BlockPos): BlockPos {

      if (var10000 == null) {
return null
      } else {


         for (dir in listOf(arrayOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)))) {

               pos.getX() + (dir.getFirst() as java.lang.Number).intValue(), 69, pos.getZ() + (dir.getSecond() as java.lang.Number).intValue()
            )
            if (world.getBlockState(p).getBlock() == Blocks.CHEST) {
return p
            }
         }
return null
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (answers.isEmpty()) {
return return
         }

         if (var10000 == null) {
return return
         }








         if (var25 == null) {
return return
         }


         for (`element$iv` in answers) {
            val it: ThreeWeirdosSolver.AnswerData = `element$iv` as ThreeWeirdosSolver.AnswerData
            if ((`element$iv` as ThreeWeirdosSolver.AnswerData).isCorrect) {


                  it.getChestPos().x, 69.0, it.getChestPos().z, it.getChestPos().x + 1.0, 70.0, it.getChestPos().z + 1.0
               )
               RenderUtils.INSTANCE
                  .renderBoxFill(
                     consumers,
                     posMat,
                     lastEntry,
                     cameraPos,
                     chestBox,
                     color.getRed().toFloat() / 255.0F,
                     color.getGreen().toFloat() / 255.0F,
                     color.getBlue().toFloat() / 255.0F,
                     0.4F
                  )
                  if (Config.dungeonESPThroughWalls) {




                  var28.renderBoxFillThroughWalls(var27, posMat, lastEntry, cameraPos, chestBox, var10006)
               }
            }
         }
      } catch (var20: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var20.printStackTrace()
         }
      }
   }

   data class AnswerData {
      val npcName: String
      private Vec3d entityPos;
      private Vec3d chestPos;
      val isCorrect: Boolean

      fun AnswerData(npcName: String, entityPos: Vec3d, chestPos: Vec3d, isCorrect: Boolean) {
         this.npcName = npcName
         this.entityPos = entityPos
         this.chestPos = chestPos
         this.isCorrect = isCorrect
      }

      fun getEntityPos(): Vec3d {
         this.entityPos
      }

      fun getChestPos(): Vec3d {
         this.chestPos
      }

      public operator fun component1(): String {
         return this.npcName
      }

      fun component2(): Vec3d {
         this.entityPos
      }

      fun component3(): Vec3d {
         this.chestPos
      }

      public operator fun component4(): Boolean {
         return this.isCorrect
      }

      fun copy(npcName: String, entityPos: Vec3d, chestPos: Vec3d, isCorrect: Boolean): ThreeWeirdosSolver.AnswerData {
         ThreeWeirdosSolver.AnswerData(npcName, entityPos, chestPos, isCorrect)
      }

      override fun toString(): String {
         return "AnswerData(npcName=${this.npcName}, entityPos=${this.entityPos}, chestPos=${this.chestPos}, isCorrect=${this.isCorrect})"
      }

      override fun hashCode(): Int {
         return ((this.npcName.hashCode() * 31 + this.entityPos.hashCode()) * 31 + this.chestPos.hashCode()) * 31 + java.lang.Boolean.hashCode(this.isCorrect)
      }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is ThreeWeirdosSolver.AnswerData
               && this.npcName == (other as ThreeWeirdosSolver.AnswerData).npcName
               && this.entityPos == (other as ThreeWeirdosSolver.AnswerData).entityPos
               && this.chestPos == (other as ThreeWeirdosSolver.AnswerData).chestPos
               && this.isCorrect == (other as ThreeWeirdosSolver.AnswerData).isCorrect
            }
      }
   }
}
