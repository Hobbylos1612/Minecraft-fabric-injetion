package jooon.features.slayers

import java.util.Locale
import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

public object AutoZombieSword {
   private final var state: jooon.features.slayers.AutoZombieSword.State = AutoZombieSword.State.IDLE
   private final var prevSlot: Int = -1
   private final var zsSlot: Int = -1
   private final var gateNextMs: Long
   private final var stepAtMs: Long
   private final var lastUseAttemptMs: Long
   private final var worldReadyAfterMs: Long
   private final val COLOR_RX: Regex = Regex("(?i)§[0-9A-FK-OR]")
   private final val CHARGES_RX: Regex = Regex("No more charges, next one in ([0-9]+(?:\\.[0-9])?)s", RegexOption.IGNORE_CASE)

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
            { client: MinecraftClient ->
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

   private fun resetAll() {
      state = AutoZombieSword.State.IDLE
      prevSlot = -1
      zsSlot = -1
      stepAtMs = 0L
      AbilityManager.INSTANCE.endSword()
      if (PlayerController.INSTANCE.isSuppressMost()) {
         PlayerController.INSTANCE.setSuppressMost(false)
      }
   }

   private fun findZombieSwordSlot(): Int? {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 == null) {
         return null
      } else {
         val p: ClientPlayerEntity = var10000

         repeat(8) { i ->
            val var5: ItemStack = p.method_31548().method_5438(i)
            if (!var5.method_7960()) {
               val var6: Regex = COLOR_RX
               val var10001: java.lang.String = var5.method_7964().getString()
               val var7: java.lang.String = var6.replace(var10001, "").toLowerCase(Locale.ROOT)
               if (StringsKt.contains$default(var7, "zombie sword", false, 2, null)
                  && !StringsKt.contains$default(var7, "ornate", false, 2, null)
                  && !StringsKt.contains$default(var7, "florid", false, 2, null)
                  && var5.method_7909() == Items.field_8371) {
                  return i
               }

               if (StringsKt.contains$default(var7, "ornate zombie sword", false, 2, null) && var5.method_7909() == Items.field_8845) {
                  return i
               }

               if (StringsKt.contains$default(var7, "florid zombie sword", false, 2, null) && var5.method_7909() == Items.field_8845) {
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
      fun getEntries(): EnumEntries<AutoZombieSword.State> {
         $ENTRIES
      }
   }
}
