package jooon.features.slayers

import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object AutoWand {
   private val wandNames: List<String> = listOf(arrayOf("Wand of Healing", "Wand of Mending", "Wand of Restoration", "Wand of Atonement"))
   private var state: jooon.features.slayers.AutoWand.State = AutoWand.State.IDLE
   private var prevSlot: Int = -1
   private var wandSlot: Int = -1
   private var gateNextMs: Long
   private var stepAtMs: Long
   private var worldReadyAfterMs: Long
   private val nameColorRx: Regex = Regex("(?i)§[0-9A-FK-OR]")

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         worldReadyAfterMs = System.currentTimeMillis() + 5000L
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         worldReadyAfterMs = 0L
      })
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { it: MinecraftClient ->
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

   private fun resetAll() {
      state = AutoWand.State.IDLE
      prevSlot = -1
      wandSlot = -1
      stepAtMs = 0L
      AbilityManager.endWand()
      if (PlayerController.isSuppressMost()) {
         PlayerController.setSuppressMost(false)
      }
   }

   private fun findWandSlot(): Int? {

      if (var10000 == null) {
         return null
      } else {


         repeat(8) { i ->

            if (!var11.isEmpty() && var11.getItem() == Items.STICK) {



               val `this$iv`: java.lang.Iterable = wandNames
               var var13: Boolean
               if (wandNames is java.util.Collection && wandNames.isEmpty()) {
                  var13 = false
               } else {
                  val var7: java.util.Iterator = `this$iv`.iterator()

                  while (true) {
                     if (!var7.hasNext()) {
                        var13 = false
break
                     }

                     if (contains(name, var7.next() as String, true)) {
                        var13 = true
break
                     }
                  }
               }

               if (var13) {
                  return i
               }
            }
         }

         return null
      }
   }

   private enum class State {
      IDLE,
      SWAPPING,
      USING,
      RETURNING;

      
      fun getEntries(): EnumEntries<AutoWand.State> {
         $ENTRIES
      }
   }
}
