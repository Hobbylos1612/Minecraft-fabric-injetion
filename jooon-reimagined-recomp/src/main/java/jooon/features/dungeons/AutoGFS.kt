package jooon.features.dungeons

import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Locale
import jooon.config.Config
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text

object AutoGFS {
   private var globalTicks: Int
   private var nextScanTick: Int = 30
   private var sequencing: Boolean
   private var nextSendTick: Int
   private val sendQueue: ArrayDeque<jooon.features.dungeons.AutoGFS.GfsRequest> = ArrayDeque()

   private val targets: List<jooon.features.dungeons.AutoGFS.GfsItem> = listOf(arrayOf(AutoGFS.GfsItem("Ender Pearl", 16, { 
      Config.autoGfsPearls
   }), AutoGFS.GfsItem("Superboom TNT", 64, { 
      Config.autoGfsSuperboom
   }), AutoGFS.GfsItem("Inflatable Jerry", 64, { 
      Config.autoGfsJerry
   }), AutoGFS.GfsItem("Decoy", 64, { 
      Config.autoGfsDecoy
   })))

   private val pendingGfs: MutableList<jooon.features.dungeons.AutoGFS.PendingGfs> = ArrayList() as java.util.List
   private val STRIP: Regex = Regex("(?i)§[0-9A-FK-OR]")

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
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
         ClientReceiveMessageEvents.ALLOW_GAME
         .register(
            lambda_7@{ message: Text, overlay: Boolean ->
               if (!Config.autoGfsEnabled) {
                  return@lambda_7 true
               } else {
                  removeAll(pendingGfs, { it: AutoGFS.PendingGfs ->
                     it.expiresAt < `$now`
                  })
                  var var10000: AutoGFS.PendingGfs = message.getString()

                  if (startsWith$default(var10000, "Command Failed: This command is on cooldown!", false, 2, null)) {
                     return@lambda_7 false
                  } else {
                     val var8: java.util.Iterator = pendingGfs.iterator()

                     while (true) {
                        if (var8.hasNext()) {
                           val `element$iv`: Any = var8.next()
                           if (!(
return raw
                                 == "Moved ${(`element$iv` as AutoGFS.PendingGfs).amount} ${(`element$iv` as AutoGFS.PendingGfs).item} from your Sacks to your inventory."
                           )) {
return continue
                           }

                           var10000 = (AutoGFS.PendingGfs)`element$iv`
break
                        }

                        var10000 = null
break
                     }

                     val match: AutoGFS.PendingGfs = var10000
                     if (var10000 != null) {

                        if (var13 != null) {
                           var13.sendMessage(Text.literal("§7[§aJR§7] §aGrabbed §e${match.amount} ${match.item} §afrom your sacks!") as Text, false)
                        }

                        pendingGfs.remove(match)
                        return@lambda_7 false
                     } else {
                        return@lambda_7 true
                     }
                  }
               }
            }
         )
      }

   private fun sendGfs(itemName: String, amount: Int) {
      if (amount > 0) {




         if (var16 != null && var16.networkHandler != null) {




            var it: AutoGFS
            try {
               it = var7
               var11.sendChatCommand(command)
               it = Result(true)
            } catch (var10: java.lang.Throwable) {
               it = Result(ResultKt.createFailure(var10))
            }

            val var18: Any
            if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(it) == null) {
               var18 = it
            } else {
               var11.sendChatMessage("/$command")
               var18 = true
            }

            if (var18 as Boolean) {
               pendingGfs.add(AutoGFS.PendingGfs(itemName, amount, System.currentTimeMillis() + 6000.toLong()))
            }
         }
      }
   }

   private data class GfsItem(name: String, stackSize: Int, enabled: () -> Boolean) {
      val name: String
      val stackSize: Int
      val enabled: () -> Boolean

      init {
         this.name = name
         this.stackSize = stackSize
         this.enabled = enabled
      }

      public operator fun component1(): String {
         return this.name
      }

      public operator fun component2(): Int {
         return this.stackSize
      }

      public operator fun component3(): () -> Boolean {
         return this.enabled
      }

      fun copy(name: String = this.name, stackSize: Int = this.stackSize, enabled: () -> Boolean = this.enabled): jooon.features.dungeons.AutoGFS.GfsItem {
         return AutoGFS.GfsItem(name, stackSize, enabled)
      }

      override fun toString(): String {
         return "GfsItem(name=${this.name}, stackSize=${this.stackSize}, enabled=${this.enabled})"
      }

      override fun hashCode(): Int {
         return (this.name.hashCode() * 31 + Integer.hashCode(this.stackSize)) * 31 + this.enabled.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is AutoGFS.GfsItem
               && this.name == (other as AutoGFS.GfsItem).name
               && this.stackSize == (other as AutoGFS.GfsItem).stackSize
               && this.enabled == (other as AutoGFS.GfsItem).enabled
            }
      }
   }

   private data class GfsRequest(item: String, amount: Int) {
      val item: String
      val amount: Int

      init {
         this.item = item
         this.amount = amount
      }

      public operator fun component1(): String {
         return this.item
      }

      public operator fun component2(): Int {
         return this.amount
      }

      fun copy(item: String = this.item, amount: Int = this.amount): jooon.features.dungeons.AutoGFS.GfsRequest {
         return AutoGFS.GfsRequest(item, amount)
      }

      override fun toString(): String {
         return "GfsRequest(item=${this.item}, amount=${this.amount})"
      }

      override fun hashCode(): Int {
         return this.item.hashCode() * 31 + Integer.hashCode(this.amount)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutoGFS.GfsRequest && this.item == (other as AutoGFS.GfsRequest).item && this.amount == (other as AutoGFS.GfsRequest).amount
         }
      }
   }

   private data class PendingGfs(item: String, amount: Int, expiresAt: Long) {
      val item: String
      val amount: Int
      val expiresAt: Long

      init {
         this.item = item
         this.amount = amount
         this.expiresAt = expiresAt
      }

      public operator fun component1(): String {
         return this.item
      }

      public operator fun component2(): Int {
         return this.amount
      }

      public operator fun component3(): Long {
         return this.expiresAt
      }

      fun copy(item: String = this.item, amount: Int = this.amount, expiresAt: Long = this.expiresAt): jooon.features.dungeons.AutoGFS.PendingGfs {
         return AutoGFS.PendingGfs(item, amount, expiresAt)
      }

      override fun toString(): String {
         return "PendingGfs(item=${this.item}, amount=${this.amount}, expiresAt=${this.expiresAt})"
      }

      override fun hashCode(): Int {
         return (this.item.hashCode() * 31 + Integer.hashCode(this.amount)) * 31 + java.lang.Long.hashCode(this.expiresAt)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is AutoGFS.PendingGfs
               && this.item == (other as AutoGFS.PendingGfs).item
               && this.amount == (other as AutoGFS.PendingGfs).amount
               && this.expiresAt == (other as AutoGFS.PendingGfs).expiresAt
            }
      }
   }
}
