package jooon.features.slayers

import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

@SourceDebugExtension(["SMAP\nAutoWand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoWand.kt\njooon/features/slayers/AutoWand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,166:1\n1747#2,3:167\n1747#2,3:171\n1#3:170\n*S KotlinDebug\n*F\n+ 1 AutoWand.kt\njooon/features/slayers/AutoWand\n*L\n161#1:167,3\n95#1:171,3\n*E\n"])
public object AutoWand {
   private final val wandNames: List<String> = CollectionsKt.listOf(arrayOf("Wand of Healing", "Wand of Mending", "Wand of Restoration", "Wand of Atonement"))
   private final var state: jooon.features.slayers.AutoWand.State = AutoWand.State.IDLE
   private final var prevSlot: Int = -1
   private final var wandSlot: Int = -1
   private final var gateNextMs: Long
   private final var stepAtMs: Long
   private final var worldReadyAfterMs: Long
   private final val nameColorRx: Regex = Regex("(?i)§[0-9A-FK-OR]")

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
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
      AbilityManager.INSTANCE.endWand()
      if (PlayerController.INSTANCE.isSuppressMost()) {
         PlayerController.INSTANCE.setSuppressMost(false)
      }
   }

   private fun findWandSlot(): Int? {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 == null) {
         return null
      } else {
         val player: ClientPlayerEntity = var10000

         repeat(8) { i ->
            val var11: ItemStack = player.method_31548().method_5438(i)
            if (!var11.method_7960() && var11.method_7909() == Items.field_8600) {
               val var12: Regex = nameColorRx
               val var10001: java.lang.String = var11.method_7964().getString()
               val name: java.lang.String = var12.replace(var10001, "")
               val `$this$any$iv`: java.lang.Iterable = wandNames
               var var13: Boolean
               if (wandNames is java.util.Collection && wandNames.isEmpty()) {
                  var13 = false
               } else {
                  val var7: java.util.Iterator = `$this$any$iv`.iterator()

                  while (true) {
                     if (!var7.hasNext()) {
                        var13 = false
                        break
                     }

                     if (StringsKt.contains(name, var7.next() as java.lang.String, true)) {
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

      @JvmStatic
      fun getEntries(): EnumEntries<AutoWand.State> {
         $ENTRIES
      }
   }
}
