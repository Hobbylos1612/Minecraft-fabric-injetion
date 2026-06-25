package jooon.features.dungeons

import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Locale
import jooon.config.Config
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text

@SourceDebugExtension(["SMAP\nAutoGFS.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoGFS.kt\njooon/features/dungeons/AutoGFS\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,178:1\n288#2,2:179\n*S KotlinDebug\n*F\n+ 1 AutoGFS.kt\njooon/features/dungeons/AutoGFS\n*L\n128#1:179,2\n*E\n"])
public object AutoGFS {
   private final var globalTicks: Int
   private final var nextScanTick: Int = 30
   private final var sequencing: Boolean
   private final var nextSendTick: Int
   private final val sendQueue: ArrayDeque<jooon.features.dungeons.AutoGFS.GfsRequest> = ArrayDeque()

   private final val targets: List<jooon.features.dungeons.AutoGFS.GfsItem> = CollectionsKt.listOf(arrayOf(AutoGFS.GfsItem("Ender Pearl", 16, { 
      Config.autoGfsPearls
   }), AutoGFS.GfsItem("Superboom TNT", 64, { 
      Config.autoGfsSuperboom
   }), AutoGFS.GfsItem("Inflatable Jerry", 64, { 
      Config.autoGfsJerry
   }), AutoGFS.GfsItem("Decoy", 64, { 
      Config.autoGfsDecoy
   })))

   private final val pendingGfs: MutableList<jooon.features.dungeons.AutoGFS.PendingGfs> = ArrayList() as java.util.List
   private final val STRIP: Regex = Regex("(?i)§[0-9A-FK-OR]")

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
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
                  CollectionsKt.removeAll(pendingGfs, { it: AutoGFS.PendingGfs ->
                     it.expiresAt < `$now`
                  })
                  var var10000: AutoGFS.PendingGfs = message.getString()
                  val raw: java.lang.String = var10000
                  if (StringsKt.startsWith$default(var10000, "Command Failed: This command is on cooldown!", false, 2, null)) {
                     return@lambda_7 false
                  } else {
                     val var8: java.util.Iterator = pendingGfs.iterator()

                     while (true) {
                        if (var8.hasNext()) {
                           val `element$iv`: Any = var8.next()
                           if (!(
                              raw
                                 == "Moved ${(`element$iv` as AutoGFS.PendingGfs).amount} ${(`element$iv` as AutoGFS.PendingGfs).item} from your Sacks to your inventory."
                           )) {
                              continue
                           }

                           var10000 = (AutoGFS.PendingGfs)`element$iv`
                           break
                        }

                        var10000 = null
                        break
                     }

                     val match: AutoGFS.PendingGfs = var10000
                     if (var10000 != null) {
                        val var13: ClientPlayerEntity = INSTANCE.getMc().field_1724
                        if (var13 != null) {
                           var13.method_7353(Text.method_43470("§7[§aJR§7] §aGrabbed §e${match.amount} ${match.item} §afrom your sacks!") as Text, false)
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
         val var10000: Locale = Locale.ROOT
         val var15: java.lang.String = itemName.toLowerCase(var10000)
         val arg: java.lang.String = StringsKt.replace$default(var15, "’", "'", false, 4, null)
         val var16: ClientPlayerEntity = this.getMc().field_1724
         if (var16 != null && var16.field_3944 != null) {
            val var11: ClientPlayNetworkHandler = var16.field_3944
            val command: java.lang.String = "gfs $arg $amount"
            val var7: AutoGFS = this

            var it: AutoGFS
            try {
               it = var7
               var11.method_45730(command)
               it = (AutoGFS)Result.constructor_impl/* $VF was: constructor-impl */(true)
            } catch (var10: java.lang.Throwable) {
               it = (AutoGFS)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var10))
            }

            val var18: Any
            if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(it) == null) {
               var18 = it
            } else {
               var11.method_45729("/$command")
               var18 = true
            }

            if (var18 as java.lang.Boolean) {
               pendingGfs.add(AutoGFS.PendingGfs(itemName, amount, System.currentTimeMillis() + (long)6000))
            }
         }
      }
   }

   private data class GfsItem(name: String, stackSize: Int, enabled: () -> Boolean) {
      public final val name: String
      public final val stackSize: Int
      public final val enabled: () -> Boolean

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

      public fun copy(name: String = this.name, stackSize: Int = this.stackSize, enabled: () -> Boolean = this.enabled): jooon.features.dungeons.AutoGFS.GfsItem {
         return AutoGFS.GfsItem(name, stackSize, enabled)
      }

      public override fun toString(): String {
         return "GfsItem(name=${this.name}, stackSize=${this.stackSize}, enabled=${this.enabled})"
      }

      public override fun hashCode(): Int {
         return (this.name.hashCode() * 31 + Integer.hashCode(this.stackSize)) * 31 + this.enabled.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val item: String
      public final val amount: Int

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

      public fun copy(item: String = this.item, amount: Int = this.amount): jooon.features.dungeons.AutoGFS.GfsRequest {
         return AutoGFS.GfsRequest(item, amount)
      }

      public override fun toString(): String {
         return "GfsRequest(item=${this.item}, amount=${this.amount})"
      }

      public override fun hashCode(): Int {
         return this.item.hashCode() * 31 + Integer.hashCode(this.amount)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutoGFS.GfsRequest && this.item == (other as AutoGFS.GfsRequest).item && this.amount == (other as AutoGFS.GfsRequest).amount
         }
      }
   }

   private data class PendingGfs(item: String, amount: Int, expiresAt: Long) {
      public final val item: String
      public final val amount: Int
      public final val expiresAt: Long

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

      public fun copy(item: String = this.item, amount: Int = this.amount, expiresAt: Long = this.expiresAt): jooon.features.dungeons.AutoGFS.PendingGfs {
         return AutoGFS.PendingGfs(item, amount, expiresAt)
      }

      public override fun toString(): String {
         return "PendingGfs(item=${this.item}, amount=${this.amount}, expiresAt=${this.expiresAt})"
      }

      public override fun hashCode(): Int {
         return (this.item.hashCode() * 31 + Integer.hashCode(this.amount)) * 31 + java.lang.Long.hashCode(this.expiresAt)
      }

      public override operator fun equals(other: Any?): Boolean {
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
