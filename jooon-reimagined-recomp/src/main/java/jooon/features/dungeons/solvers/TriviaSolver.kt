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

object TriviaSolver {
   private val solutions: Map<String, List<String>> =
      mapOf(
         arrayOf(
            Pair("What is the status of The Watcher?", listOf("Stalker")),
            Pair("What is the status of Bonzo?", listOf("New Necromancer")),
            Pair("What is the status of Scarf?", listOf("Apprentice Necromancer")),
            Pair("What is the status of The Professor?", listOf("Professor")),
            Pair("What is the status of Thorn?", listOf("Shaman Necromancer")),
            Pair("What is the status of Livid?", listOf("Master Necromancer")),
            Pair("What is the status of Sadan?", listOf("Necromancer Lord")),
            Pair("What is the status of Maxor, Storm),
            Pair("How many total Fairy Souls are there?", listOf("267 Fairy Souls")),
            Pair("How many Fairy Souls are there in Spider's Den?", listOf("19 Fairy Souls")),
            Pair("How many Fairy Souls are there in Spiders Den?", listOf("19 Fairy Souls")),
            Pair("How many Fairy Souls are there in The End?", listOf("12 Fairy Souls")),
            Pair("How many Fairy Souls are there in The Farming Islands?", listOf("20 Fairy Souls")),
            Pair("How many Fairy Souls are there in Crimson Isle?", listOf("29 Fairy Souls")),
            Pair("How many Fairy Souls are there in The Park?", listOf("12 Fairy Souls")),
            Pair("How many Fairy Souls are there in Jerry's Workshop?", listOf("5 Fairy Souls")),
            Pair("How many Fairy Souls are there in Hub?", listOf("80 Fairy Souls")),
            Pair("How many Fairy Souls are there in The Hub?", listOf("80 Fairy Souls")),
            Pair("How many Fairy Souls are there in Deep Caverns?", listOf("21 Fairy Souls")),
            Pair("How many Fairy Souls are there in Gold Mine?", listOf("12 Fairy Souls")),
            Pair("How many Fairy Souls are there in Dungeon Hub?", listOf("7 Fairy Souls")),
            Pair("Which brother is on the Spider's Den?", listOf("Rick")),
            Pair("Which brother is on the Spiders Den?", listOf("Rick")),
            Pair("What is the name of Rick's brother?", listOf("Pat")),
            Pair("What is the name of the vendor in the Hub who sells stained glass?", listOf("Wool Weaver")),
            Pair("What is the name of the person that upgrades pets?", listOf("Kat")),
            Pair("What is the name of the lady of the Nether?", listOf("Elle")),
            Pair("Which villager in the Village gives you a Rogue Sword?", listOf("Jamie")),
            Pair("How many unique minions are there?", listOf("60 Minions")),
            Pair("Which of these enemies does not spawn in the Spider's Den?", listOf(arrayOf("Zombie Spider", "Cave Spider", "Wither Skeleton", "Dashing Spooder", "Broodfather", "Night Spider"))),
            Pair("Which of these enemies does not spawn in the Spiders Den?", listOf(arrayOf("Zombie Spider", "Cave Spider", "Wither Skeleton", "Dashing Spooder", "Broodfather", "Night Spider"))),
            Pair("Which of these monsters only spawns at night?", listOf(arrayOf("Zombie Villager", "Ghast"))),
            Pair("Which of these is not a dragon in The End?", listOf(
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
               ))
         )
      )
      private val typeBlocks: Map<String, Pair<Int, Int>> =
      mapOf(
         arrayOf(
            Pair("Ⓐ", Pair(20, 6)),
            Pair("Ⓑ", Pair(15, 9)),
            Pair("Ⓒ", Pair(10, 6)),
            Pair("ⓐ", Pair(20, 6)),
            Pair("ⓑ", Pair(15, 9)),
            Pair("ⓒ", Pair(10, 6))
         )
      )
      private var inTrivia: Boolean
   private var solution: List<String>?
   private var currentType: String?
   private var pendingAnswer: String?

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.triviaSolver && Utils.inDungeon) {
            tick()
         } else {
            if (inTrivia) {
               reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.triviaSolver && Utils.inDungeon) {
            render(ctx)
         }
      })
      ClientReceiveMessageEvents.CHAT.register({ message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->


         var10000.handleChatMessage(var10001)
      })
      ClientReceiveMessageEvents.GAME.register({ message: Text, var1: Boolean ->


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

      inTrivia = (if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE
         && (currentRoom.name == "Quiz" || currentRoom.name == "Three Weirdos")
         if (!Utils.inDungeon) {
         this.reset()
      }
   }

   private fun handleChatMessage(message: String) {

      if (endsWith$default(clean, "?", false, 2, null)) {

         if (optionMatch != null) {
            var var14: String = optionMatch.getGroupValues().get(1) as String
            if (var14 == "What SkyBlock year is it?") {
               solution = listOf(this.currentYear())
            } else {
               if (trim(var14).toString() == "glass?") {
                  var14 = "What is the name of the vendor in the Hub who sells stained glass?"
               }

               solution = solutions.get(var14)
            }

            if (solution != null && solution.size() == 1) {
               pendingAnswer = if (solution != null) firstOrNull(solution) as String else null
            }
return return
         }
      }

      if (var13 != null) {
         if (solution == null) {
return return
         }

         val sol: java.util.List = solution


         val `this$iv`: java.lang.Iterable = sol
         var var10000: Boolean
         if (sol is java.util.Collection && (sol as java.util.Collection).isEmpty()) {
            var10000 = false
         } else {
            val ans: java.util.Iterator = `this$iv`.iterator()

            while (true) {
               if (!ans.hasNext()) {
                  var10000 = false
break
               }

               if (equals(ans.next() as String, msg, true)) {
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
                  Utils.addMessage("§a§lJooonReimagined §7» §6QUIZ: §aThe answer is §l$`$ans`§f!")
                  pendingAnswer = null
return Unit
               }, 23, null)
            }
         }
      }

      if (contains$default(clean, "answered Question", false, 2, null)
         || contains$default(clean, "answered the final question", false, 2, null)
         || contains$default(clean, "Yikes", false, 2, null)) {
         solution = null
         currentType = null
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (!inTrivia || currentType == null) {
return return
         }

         var var10000: Pair = typeBlocks.get(currentType)
         if (var10000 == null) {
return return
         }

         if (var17 == null) {
return return
         }

         var10000 = var17.fromComp((var10000.getFirst() as java.lang.Number).intValue(), (var10000.getSecond() as java.lang.Number).intValue())
         if (var10000 == null) {
return return
         }

         if (var19 == null) {
return return
         }





         if (var24 == null) {
return return
         }

            (var10000.getFirst() as java.lang.Number).intValue(),
            70.0,
            (var10000.getSecond() as java.lang.Number).intValue(),
            (var10000.getFirst() as java.lang.Number).intValue() + 1.0,
            71.0,
            (var10000.getSecond() as java.lang.Number).intValue() + 1.0
         )
         RenderUtils.renderBoxOutlineRobust(var24, var23, var22, var21, box, 0.0F, 1.0F, 0.0F, 1.0F, 0.02F)
         if (Config.dungeonESPThroughWalls) {




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
