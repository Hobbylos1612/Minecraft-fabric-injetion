package jooon.features.dungeons.solvers

import com.mojang.authlib.GameProfile
import java.awt.Color
import java.time.Instant
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.concurrent.ThreadsKt
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.network.message.SignedMessage
import net.minecraft.network.message.MessageType.Parameters
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nTriviaSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TriviaSolver.kt\njooon/features/dungeons/solvers/TriviaSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,204:1\n1747#2,3:205\n*S KotlinDebug\n*F\n+ 1 TriviaSolver.kt\njooon/features/dungeons/solvers/TriviaSolver\n*L\n140#1:205,3\n*E\n"])
public object TriviaSolver {
   private final val solutions: Map<String, List<String>> =
      MapsKt.mapOf(
         arrayOf(
            TuplesKt.to("What is the status of The Watcher?", CollectionsKt.listOf("Stalker")),
            TuplesKt.to("What is the status of Bonzo?", CollectionsKt.listOf("New Necromancer")),
            TuplesKt.to("What is the status of Scarf?", CollectionsKt.listOf("Apprentice Necromancer")),
            TuplesKt.to("What is the status of The Professor?", CollectionsKt.listOf("Professor")),
            TuplesKt.to("What is the status of Thorn?", CollectionsKt.listOf("Shaman Necromancer")),
            TuplesKt.to("What is the status of Livid?", CollectionsKt.listOf("Master Necromancer")),
            TuplesKt.to("What is the status of Sadan?", CollectionsKt.listOf("Necromancer Lord")),
            TuplesKt.to("What is the status of Maxor, Storm, Goldor, and Necron?", CollectionsKt.listOf("The Wither Lords")),
            TuplesKt.to("How many total Fairy Souls are there?", CollectionsKt.listOf("267 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Spider's Den?", CollectionsKt.listOf("19 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Spiders Den?", CollectionsKt.listOf("19 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in The End?", CollectionsKt.listOf("12 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in The Farming Islands?", CollectionsKt.listOf("20 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Crimson Isle?", CollectionsKt.listOf("29 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in The Park?", CollectionsKt.listOf("12 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Jerry's Workshop?", CollectionsKt.listOf("5 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Hub?", CollectionsKt.listOf("80 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in The Hub?", CollectionsKt.listOf("80 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Deep Caverns?", CollectionsKt.listOf("21 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Gold Mine?", CollectionsKt.listOf("12 Fairy Souls")),
            TuplesKt.to("How many Fairy Souls are there in Dungeon Hub?", CollectionsKt.listOf("7 Fairy Souls")),
            TuplesKt.to("Which brother is on the Spider's Den?", CollectionsKt.listOf("Rick")),
            TuplesKt.to("Which brother is on the Spiders Den?", CollectionsKt.listOf("Rick")),
            TuplesKt.to("What is the name of Rick's brother?", CollectionsKt.listOf("Pat")),
            TuplesKt.to("What is the name of the vendor in the Hub who sells stained glass?", CollectionsKt.listOf("Wool Weaver")),
            TuplesKt.to("What is the name of the person that upgrades pets?", CollectionsKt.listOf("Kat")),
            TuplesKt.to("What is the name of the lady of the Nether?", CollectionsKt.listOf("Elle")),
            TuplesKt.to("Which villager in the Village gives you a Rogue Sword?", CollectionsKt.listOf("Jamie")),
            TuplesKt.to("How many unique minions are there?", CollectionsKt.listOf("60 Minions")),
            TuplesKt.to(
               "Which of these enemies does not spawn in the Spider's Den?",
               CollectionsKt.listOf(arrayOf("Zombie Spider", "Cave Spider", "Wither Skeleton", "Dashing Spooder", "Broodfather", "Night Spider"))
            ),
            TuplesKt.to(
               "Which of these enemies does not spawn in the Spiders Den?",
               CollectionsKt.listOf(arrayOf("Zombie Spider", "Cave Spider", "Wither Skeleton", "Dashing Spooder", "Broodfather", "Night Spider"))
            ),
            TuplesKt.to("Which of these monsters only spawns at night?", CollectionsKt.listOf(arrayOf("Zombie Villager", "Ghast"))),
            TuplesKt.to(
               "Which of these is not a dragon in The End?",
               CollectionsKt.listOf(
                  arrayOf(
                     "Zoomer Dragon",
                     "Weak Dragon",
                     "Stonk Dragon",
                     "Holy Dragon",
                     "Boomer Dragon",
                     "Booger Dragon",
                     "Older Dragon",
                     "Elder Dragon",
                     "Stable Dragon",
                     "Professor Dragon"
                  )
               )
            )
         )
      )
      private final val typeBlocks: Map<String, Pair<Int, Int>> =
      MapsKt.mapOf(
         arrayOf(
            TuplesKt.to("Ⓐ", TuplesKt.to(20, 6)),
            TuplesKt.to("Ⓑ", TuplesKt.to(15, 9)),
            TuplesKt.to("Ⓒ", TuplesKt.to(10, 6)),
            TuplesKt.to("ⓐ", TuplesKt.to(20, 6)),
            TuplesKt.to("ⓑ", TuplesKt.to(15, 9)),
            TuplesKt.to("ⓒ", TuplesKt.to(10, 6))
         )
      )
      private final var inTrivia: Boolean
   private final var solution: List<String>?
   private final var currentType: String?
   private final var pendingAnswer: String?

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.triviaSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inTrivia) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.triviaSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.render(ctx)
         }
      })
      ClientReceiveMessageEvents.CHAT.register({ message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->
         val var10000: TriviaSolver = INSTANCE
         val var10001: java.lang.String = message.getString()
         var10000.handleChatMessage(var10001)
      })
      ClientReceiveMessageEvents.GAME.register({ message: Text, var1: Boolean ->
         val var10000: TriviaSolver = INSTANCE
         val var10001: java.lang.String = message.getString()
         var10000.handleChatMessage(var10001)
      })
   }

   private fun reset() {
      inTrivia = false
      solution = null
      currentType = null
      pendingAnswer = null
   }

   private fun tick() {
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      inTrivia = (if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE
         && (currentRoom.name == "Quiz" || currentRoom.name == "Three Weirdos")
         if (!Utils.INSTANCE.inDungeon) {
         this.reset()
      }
   }

   private fun handleChatMessage(message: String) {
      val clean: java.lang.String = Utils.stripColor(message)
      if (StringsKt.endsWith$default(clean, "?", false, 2, null)) {
         val optionMatch: MatchResult = Regex.find$default(Regex("^ *(.*\\?)$"), clean, 0, 2, null)
         if (optionMatch != null) {
            var var14: java.lang.String = optionMatch.getGroupValues().get(1) as java.lang.String
            if (var14 == "What SkyBlock year is it?") {
               solution = CollectionsKt.listOf(this.currentYear())
            } else {
               if (StringsKt.trim(var14).toString() == "glass?") {
                  var14 = "What is the name of the vendor in the Hub who sells stained glass?"
               }

               solution = solutions.get(var14)
            }

            if (solution != null && solution.size() == 1) {
               pendingAnswer = if (solution != null) CollectionsKt.firstOrNull(solution) as java.lang.String else null
            }

            return
         }
      }

      val var13: MatchResult = Regex.find$default(Regex("^ *([ⒶⒷⒸⓐⓑⓒ]) (.*)$"), clean, 0, 2, null)
      if (var13 != null) {
         if (solution == null) {
            return
         }

         val sol: java.util.List = solution
         val type: java.lang.String = var13.getGroupValues().get(1) as java.lang.String
         val msg: java.lang.String = StringsKt.trim(var13.getGroupValues().get(2) as java.lang.String).toString()
         val `$this$any$iv`: java.lang.Iterable = sol
         var var10000: Boolean
         if (sol is java.util.Collection && (sol as java.util.Collection).isEmpty()) {
            var10000 = false
         } else {
            val ans: java.util.Iterator = `$this$any$iv`.iterator()

            while (true) {
               if (!ans.hasNext()) {
                  var10000 = false
                  break
               }

               if (StringsKt.equals(ans.next() as java.lang.String, msg, true)) {
                  var10000 = true
                  break
               }
            }
         }

         if (var10000) {
            currentType = type
            pendingAnswer = msg
         }

         if (type == "Ⓒ" || type == "ⓒ") {
            if (pendingAnswer != null) {
               ThreadsKt.thread$default(false, false, null, "Trivia-Announce", 0, { 
                  Thread.sleep(100L)
                  Utils.INSTANCE.addMessage("§a§lJooonReimagined §7» §6QUIZ: §aThe answer is §l$`$ans`§f!")
                  pendingAnswer = null
                  Unit.INSTANCE
               }, 23, null)
            }
         }
      }

      if (StringsKt.contains$default(clean, "answered Question", false, 2, null)
         || StringsKt.contains$default(clean, "answered the final question", false, 2, null)
         || StringsKt.contains$default(clean, "Yikes", false, 2, null)) {
         solution = null
         currentType = null
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (!inTrivia || currentType == null) {
            return
         }

         var var10000: Pair = typeBlocks.get(currentType)
         if (var10000 == null) {
            return
         }

         val var17: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
         if (var17 == null) {
            return
         }

         var10000 = var17.fromComp((var10000.getFirst() as java.lang.Number).intValue(), (var10000.getSecond() as java.lang.Number).intValue())
         if (var10000 == null) {
            return
         }

         val var19: MatrixStack = ctx.matrices()
         if (var19 == null) {
            return
         }

         val var20: Camera = ctx.gameRenderer().method_19418()
         val var21: Vec3d = var20.method_71156()
         val var22: Entry = var19.method_23760()
         val var23: Matrix4f = var22.method_23761()
         val var24: VertexConsumerProvider = ctx.consumers()
         if (var24 == null) {
            return
         }

         val box: Box = Box(
            (var10000.getFirst() as java.lang.Number).intValue(),
            70.0,
            (var10000.getSecond() as java.lang.Number).intValue(),
            (var10000.getFirst() as java.lang.Number).intValue() + 1.0,
            71.0,
            (var10000.getSecond() as java.lang.Number).intValue() + 1.0
         )
         RenderUtils.INSTANCE.renderBoxOutlineRobust(var24, var23, var22, var21, box, 0.0F, 1.0F, 0.0F, 1.0F, 0.02F)
         if (Config.dungeonESPThroughWalls) {
            val var25: MinecraftClient = this.getMc()
            val var26: OutlineVertexConsumerProvider = (var25 as MinecraftAccessor).getRenderBuffers().method_23003()
            val var27: RenderUtils = RenderUtils.INSTANCE
            val var10006: Color = Color.GREEN
            var27.renderBoxOutlineThroughWalls(var26, var23, var22, var21, box, var10006, 0.02F)
         }
      } catch (var16: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var16.printStackTrace()
         }
      }
   }

   private fun currentYear(): String {
      return "Year ${(System.currentTimeMillis() / 1000 - 1560276000) / 446400 + 1L}"
   }
}
