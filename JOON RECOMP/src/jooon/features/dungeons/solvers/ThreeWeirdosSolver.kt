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
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nThreeWeirdosSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThreeWeirdosSolver.kt\njooon/features/dungeons/solvers/ThreeWeirdosSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,178:1\n1747#2,3:179\n1747#2,3:182\n2624#2,3:185\n1855#2,2:188\n*S KotlinDebug\n*F\n+ 1 ThreeWeirdosSolver.kt\njooon/features/dungeons/solvers/ThreeWeirdosSolver\n*L\n103#1:179,3\n104#1:182,3\n126#1:185,3\n152#1:188,2\n*E\n"])
public object ThreeWeirdosSolver {
   private final val npcRegex: Regex = Regex("^\\[NPC\\] (\\w+): (.*)")
   private final val solutions: List<Regex> =
      CollectionsKt.listOf(
         arrayOf(
            Regex("The reward is not in my chest!"),
            Regex("At least one of them is lying, and the reward is not in \\w+'s chest.?"),
            Regex("My chest doesn't have the reward\\. We are all telling the truth.?"),
            Regex("My chest has the reward and I'm telling the truth!"),
            Regex("The reward isn't in any of our chests.?"),
            Regex("Both of them are telling the truth\\. Also, \\w+ has the reward in their chest.?")
         )
      )
      private final val wrongAnswers: List<Regex> =
      CollectionsKt.listOf(
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
      private final val answers: CopyOnWriteArrayList<jooon.features.dungeons.solvers.ThreeWeirdosSolver.AnswerData> = CopyOnWriteArrayList()
   private final var inWeirdos: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.threeWeirdosSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inWeirdos) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.threeWeirdosSolver && Utils.INSTANCE.inDungeon && inWeirdos) {
            INSTANCE.render(ctx)
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
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      if ((if (currentRoom != null) currentRoom.type else null) != RoomTypes.PUZZLE || !(currentRoom.name == "Three Weirdos") && !(currentRoom.name == "Quiz")) {
         if (inWeirdos) {
            this.reset()
         }
      } else {
         inWeirdos = true
      }
   }

   private fun handleChatMessage(message: String) {
      val var10000: MatchResult = Regex.find$default(npcRegex, Utils.stripColor(message), 0, 2, null)
      if (var10000 != null) {
         val npcName: java.lang.String = var10000.getGroupValues().get(1) as java.lang.String
         val npcMsg: java.lang.String = var10000.getGroupValues().get(2) as java.lang.String
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

         val `$this$any$ivx`: java.lang.Iterable = wrongAnswers
         var var41: Boolean
         if (wrongAnswers is java.util.Collection && wrongAnswers.isEmpty()) {
            var41 = false
         } else {
            val var28: java.util.Iterator = `$this$any$ivx`.iterator()

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
            val var42: ClientWorld = this.getMc().field_1687
            if (var42 == null) {
               return
            }

            val var43: java.lang.Iterable = var42.method_18112()
            var var14: java.util.Iterator = CollectionsKt.toList(var43).iterator()

            while (true) {
               if (!var14.hasNext()) {
                  var48 = null
                  break
               }

               val `element$iv`: Any = var14.next()
               val it: Entity = `element$iv` as Entity
               var var44: Boolean
               if (!((`element$iv` as Entity).method_5864() == EntityType.field_6131)) {
                  var44 = false
               } else {
                  run label161@{
                     val var45: Text = it.method_5797()
                     if (var45 != null) {
                        val var46: java.lang.String = var45.getString()
                        if (var46 != null) {
                           val var47: java.lang.String
                           if (!StringsKt.startsWith$default(var46, "{\"text\":\"", false, 2, null)
                              && !StringsKt.contains$default(var46, "\"text\"", false, 2, null)) {
                              var47 = Utils.stripColor(var46)
                           } else {
                              val start: Int = StringsKt.indexOf$default(var46, "\"text\":\"", 0, false, 6, null)
                              if (start != -1) {
                                 val s: Int = start + 8
                                 val e: Int = StringsKt.indexOf$default(var46, "\"", start + 8, false, 4, null)
                                 if (e > s) {
                                    var47 = var46.substring(s, e)
                                 } else {
                                    var47 = Utils.stripColor(var46)
                                 }
                              } else {
                                 var47 = Utils.stripColor(var46)
                              }
                           }

                           var44 = StringsKt.contains(var47, npcName, true)
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

            val var49: Entity = var48 as Entity
            if (var48 as Entity == null) {
               return
            }

            val var10001: BlockPos = var49.method_24515()
            val var50: BlockPos = this.findNearbyChest(var10001)
            if (var50 == null) {
               return
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
               val var52: CopyOnWriteArrayList = answers
               val var10004: Vec3d = var49.method_73189()
               var52.add(
                  ThreeWeirdosSolver.AnswerData(
                     npcName, var10004, Vec3d((double)var50.method_10263(), (double)var50.method_10264(), (double)var50.method_10260()), var40
                  )
               )
            }
         }
      }
   }

   fun findNearbyChest(pos: BlockPos): BlockPos {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val world: ClientWorld = var10000

         for (dir in CollectionsKt.listOf(arrayOf(TuplesKt.to(1, 0), TuplesKt.to(-1, 0), TuplesKt.to(0, 1), TuplesKt.to(0, -1)))) {
            val p: BlockPos = BlockPos(
               pos.method_10263() + (dir.getFirst() as java.lang.Number).intValue(), 69, pos.method_10260() + (dir.getSecond() as java.lang.Number).intValue()
            )
            if (world.method_8320(p).method_26204() == Blocks.field_10034) {
               p
            }
         }

         null
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (answers.isEmpty()) {
            return
         }

         val var10000: MatrixStack = ctx.matrices()
         if (var10000 == null) {
            return
         }

         val var21: Camera = ctx.gameRenderer().method_19418()
         val var22: Vec3d = var21.method_71156()
         val cameraPos: Vec3d = var22
         val var23: Entry = var10000.method_23760()
         val lastEntry: Entry = var23
         val var24: Matrix4f = var23.method_23761()
         val posMat: Matrix4f = var24
         val var25: VertexConsumerProvider = ctx.consumers()
         if (var25 == null) {
            return
         }

         val consumers: VertexConsumerProvider = var25

         for (`element$iv` in answers) {
            val it: ThreeWeirdosSolver.AnswerData = `element$iv` as ThreeWeirdosSolver.AnswerData
            if ((`element$iv` as ThreeWeirdosSolver.AnswerData).isCorrect) {
               val color: Color = Color.GREEN
               val chestBox: Box = Box(
                  it.getChestPos().field_1352, 69.0, it.getChestPos().field_1350, it.getChestPos().field_1352 + 1.0, 70.0, it.getChestPos().field_1350 + 1.0
               )
               RenderUtils.INSTANCE
                  .renderBoxFill(
                     consumers,
                     posMat,
                     lastEntry,
                     cameraPos,
                     chestBox,
                     (float)color.getRed() / 255.0F,
                     (float)color.getGreen() / 255.0F,
                     (float)color.getBlue() / 255.0F,
                     0.4F
                  )
                  if (Config.dungeonESPThroughWalls) {
                  val var26: MinecraftClient = INSTANCE.getMc()
                  val var27: OutlineVertexConsumerProvider = (var26 as MinecraftAccessor).getRenderBuffers().method_23003()
                  val var28: RenderUtils = RenderUtils.INSTANCE
                  val var10006: Color = Color.GREEN
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

   public data class AnswerData {
      public final val npcName: String
      private Vec3d entityPos;
      private Vec3d chestPos;
      public final val isCorrect: Boolean

      fun AnswerData(npcName: java.lang.String, entityPos: Vec3d, chestPos: Vec3d, isCorrect: Boolean) {
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

      fun copy(npcName: java.lang.String, entityPos: Vec3d, chestPos: Vec3d, isCorrect: Boolean): ThreeWeirdosSolver.AnswerData {
         ThreeWeirdosSolver.AnswerData(npcName, entityPos, chestPos, isCorrect)
      }

      public override fun toString(): String {
         return "AnswerData(npcName=${this.npcName}, entityPos=${this.entityPos}, chestPos=${this.chestPos}, isCorrect=${this.isCorrect})"
      }

      public override fun hashCode(): Int {
         return ((this.npcName.hashCode() * 31 + this.entityPos.hashCode()) * 31 + this.chestPos.hashCode()) * 31 + java.lang.Boolean.hashCode(this.isCorrect)
      }

      public override operator fun equals(other: Any?): Boolean {
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
