package jooon.features.slayers

import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.gui.MovableOverlayScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.Window

@SourceDebugExtension(["SMAP\nSlayerHPDisplay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SlayerHPDisplay.kt\njooon/features/slayers/SlayerHPDisplay\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,291:1\n766#2:292\n857#2,2:293\n1603#2,9:295\n1855#2:304\n1856#2:306\n1612#2:307\n288#2,2:308\n288#2,2:310\n288#2,2:312\n1#3:305\n*S KotlinDebug\n*F\n+ 1 SlayerHPDisplay.kt\njooon/features/slayers/SlayerHPDisplay\n*L\n94#1:292\n94#1:293,2\n97#1:295,9\n97#1:304\n97#1:306\n97#1:307\n103#1:308,2\n105#1:310,2\n107#1:312,2\n97#1:305\n*E\n"])
public object SlayerHPDisplay {
   private const val SCAN_INTERVAL: Int = 10
   private final var tickCounter: Int
   private final var current: jooon.features.slayers.SlayerHPDisplay.BossLines?
   private final var overlay: MovableOverlay?
   private final var appliedSavedPos: Boolean
   private final val COLOR_RX: Regex = Regex("§.")
   private final val HEART_RX: Regex = Regex("[❤♥]")
   private final val TIME_LINE_RX: Regex = Regex("^\\s*§?[0-9a-fk-or]?(\\d{1,2}:\\d{2})\\s*$", RegexOption.IGNORE_CASE)

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   private fun strip(s: String): String {
      return StringsKt.trim(COLOR_RX.replace(s, "")).toString()
   }

   private fun fromJsonOrRaw(s: String): String {
      if (StringsKt.startsWith$default(s, "{\"text\":\"", false, 2, null)) {
         val i: Int = StringsKt.indexOf$default(s, "\"text\":\"", 0, false, 6, null) + 8
         val j: Int = StringsKt.indexOf$default(s, "\"", i, false, 4, null)
         val var10000: java.lang.String
         if (j > i) {
            var10000 = s.substring(i, j)
         } else {
            var10000 = s
         }

         return var10000
      } else {
         return s
      }
   }

   public fun onInitializeClient() {
      HudRenderCallback.EVENT
         .register(
            { ctx: DrawContext, var1: RenderTickCounter ->
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

   private fun isZeroHp(cur: jooon.features.slayers.SlayerHPDisplay.BossLines): Boolean {
      val plain: java.lang.String = this.strip(cur.bottomColored)
      var var10000: MatchResult = Regex.find$default(HEART_RX, plain, 0, 2, null)
      if (var10000 != null) {
         val var9: IntRange = var10000.getRange()
         if (var9 != null) {
            val var10: java.lang.String = plain.substring(0, var9.getFirst())
            var10000 = Regex.find$default(Regex("([0-9][0-9.,]*)(?:\\s*[kKmMbB])?\\s*$"), StringsKt.trimEnd(var10).toString(), 0, 2, null)
            if (var10000 == null) {
               return false
            }

            val var12: java.lang.Double = StringsKt.toDoubleOrNull(
               StringsKt.replace$default(var10000.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null)
            )
            if (var12 != null) {
               return var12 == 0.0
            }

            return false
         }
      }

      return false
   }

   fun ensureOverlayReady(ctx: DrawContext) {
      if (!PersistentState.slayerHPDisplayInitDone) {
         val var10000: TextRenderer = this.getMc().field_1772
         val realX: Int = -var10000.method_1727("(02:59) Atoned Horror 9.8M§c❤") / 2
         val realY: Int = -60 - var10000.field_2000
         PersistentState.slayerHPDisplayX = realX
         PersistentState.slayerHPDisplayY = realY
         PersistentState.slayerHPDisplayInitDone = true
         JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      }

      if (overlay == null) {
         val var6: MovableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "slayerHP",
               "(02:59) Atoned Horror 9.8M❤",
               this.getMc().method_22683().method_4486() / 2 + PersistentState.slayerHPDisplayX,
               this.getMc().method_22683().method_4502() / 2 + PersistentState.slayerHPDisplayY,
               180,
               12
            )
            var6.resetFunction = { 
            val var10000: Window = INSTANCE.getMc().method_22683()
            val var5: TextRenderer = INSTANCE.getMc().field_1772
            val ox: Int = -var5.method_1727("(02:59) Atoned Horror 9.8M§c❤") / 2
            val oy: Int = -60 - var5.field_2000
            TuplesKt.to(var10000.method_4486() / 2 + ox, var10000.method_4502() / 2 + oy)
         }
         overlay = var6
         if (overlay != null) {
            overlay.renderFunction = { c: DrawContext, x: Int, y: Int, var3: Float ->
               INSTANCE.drawLine(c, x, y)
               Unit.INSTANCE
            }
         }

         if (overlay != null) {
            overlay.onPositionChanged = { x: Int, y: Int ->
               val ox: Int = x - INSTANCE.getMc().method_22683().method_4486() / 2
               val oy: Int = y - INSTANCE.getMc().method_22683().method_4502() / 2
               PersistentState.slayerHPDisplayX = ox
               PersistentState.slayerHPDisplayY = oy
               PersistentState.slayerHPDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.INSTANCE.write("jooonreimagined_state")
               Unit.INSTANCE
            }
         }

         if (Config.slayerHPDisplayEnabled && PersistentState.slayerHPDisplayMovable) {
            if (overlay != null) {
               overlay.register()
            }
         }
      }

      if (!appliedSavedPos) {
         val var14: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.slayerHPDisplayX
         val var16: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.slayerHPDisplayY
         if (overlay != null) {
            overlay.setPositionSilently(var14, var16)
         }

         appliedSavedPos = true
      }
   }

   fun drawLine(ctx: DrawContext, x: Int, y: Int) {
      val var10000: TextRenderer = this.getMc().field_1772
      if (this.getMc().field_1755 is MovableOverlayScreen && current == null) {
         ctx.method_51433(var10000, "(02:59) Atoned Horror 9.8M§c❤", x, y, -1, true)
      } else if (current != null) {
         if (this.isZeroHp(current)) {
            current = null
         } else {
            val var8: java.lang.String = this.buildDisplayString()
            if (var8 != null) {
               ctx.method_51433(var10000, var8, x, y, -1, true)
            }
         }
      }
   }

   fun renderAt(ctx: DrawContext, ox: Int, oy: Int) {
      if (!PersistentState.slayerHPDisplayMovable || overlay == null) {
         this.drawLine(ctx, ctx.method_51421() / 2 + ox, ctx.method_51443() / 2 + oy)
      }
   }

   private fun buildDisplayString(): String? {
      if (current == null) {
         return null
      } else {
         val cur: SlayerHPDisplay.BossLines = current
         var var10000: MatchResult = Regex.find$default(TIME_LINE_RX, this.strip(current.timeColored), 0, 2, null)
         if (var10000 != null) {
            val var12: java.util.List = var10000.getGroupValues()
            if (var12 != null) {
               val var13: java.lang.String = CollectionsKt.getOrNull(var12, 1) as java.lang.String
               if (var13 != null) {
                  val timePart: java.lang.String = "§c($var13)§r"
                  val bottomPlain: java.lang.String = this.strip(cur.bottomColored)
                  var10000 = Regex.find$default(HEART_RX, bottomPlain, 0, 2, null)
                  if (var10000 != null) {
                     val var15: IntRange = var10000.getRange()
                     if (var15 != null) {
                        val var16: java.lang.String = bottomPlain.substring(0, var15.getFirst())
                        val beforeHeart: java.lang.String = StringsKt.trimEnd(var16).toString()
                        val splitAt: Int = StringsKt.lastIndexOf$default(beforeHeart, ' ', 0, false, 6, null)
                        if (splitAt <= 0) {
                           return null
                        }

                        val var17: java.lang.String = beforeHeart.substring(0, splitAt)
                        val namePlain: java.lang.String = StringsKt.trim(var17).toString()
                        val var18: java.lang.String = beforeHeart.substring(splitAt + 1)
                        return "$timePart §a$namePlain§r §c${StringsKt.trim(var18).toString()} ❤§r"
                     }
                  }

                  return null
               }
            }
         }

         return null
      }
   }

   public fun onConfigChanged() {
      if (Config.slayerHPDisplayEnabled && PersistentState.slayerHPDisplayMovable) {
         if (overlay == null) {
            val var5: MovableOverlay = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "slayerHP",
                  "(02:59) Atoned Horror 9.8M❤",
                  this.getMc().method_22683().method_4486() / 2 + PersistentState.slayerHPDisplayX,
                  this.getMc().method_22683().method_4502() / 2 + PersistentState.slayerHPDisplayY,
                  180,
                  12
               )
               var5.renderFunction = { c: DrawContext, x: Int, y: Int, var3: Float ->
               INSTANCE.drawLine(c, x, y)
               Unit.INSTANCE
            }
            var5.onPositionChanged = { x: Int, y: Int ->
               val ox: Int = x - INSTANCE.getMc().method_22683().method_4486() / 2
               val oy: Int = y - INSTANCE.getMc().method_22683().method_4502() / 2
               PersistentState.slayerHPDisplayX = ox
               PersistentState.slayerHPDisplayY = oy
               PersistentState.slayerHPDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.INSTANCE.write("jooonreimagined_state")
               Unit.INSTANCE
            }
            overlay = var5
         }

         val var10: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.slayerHPDisplayX
         val var11: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.slayerHPDisplayY
         if (overlay != null) {
            overlay.register()
         }

         if (overlay != null) {
            overlay.setPositionSilently(var10, var11)
         }
      } else if (overlay != null) {
         overlay.unregister()
      }
   }

   private data class BossLines(timeColored: String, bottomColored: String, lastSeenMs: Long) {
      public final val timeColored: String
      public final val bottomColored: String
      public final val lastSeenMs: Long

      init {
         this.timeColored = timeColored
         this.bottomColored = bottomColored
         this.lastSeenMs = lastSeenMs
      }

      public operator fun component1(): String {
         return this.timeColored
      }

      public operator fun component2(): String {
         return this.bottomColored
      }

      public operator fun component3(): Long {
         return this.lastSeenMs
      }

      public fun copy(timeColored: String = this.timeColored, bottomColored: String = this.bottomColored, lastSeenMs: Long = this.lastSeenMs): jooon.features.slayers.SlayerHPDisplay.BossLines {
         return SlayerHPDisplay.BossLines(timeColored, bottomColored, lastSeenMs)
      }

      public override fun toString(): String {
         return "BossLines(timeColored=${this.timeColored}, bottomColored=${this.bottomColored}, lastSeenMs=${this.lastSeenMs})"
      }

      public override fun hashCode(): Int {
         return (this.timeColored.hashCode() * 31 + this.bottomColored.hashCode()) * 31 + java.lang.Long.hashCode(this.lastSeenMs)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is SlayerHPDisplay.BossLines
               && this.timeColored == (other as SlayerHPDisplay.BossLines).timeColored
               && this.bottomColored == (other as SlayerHPDisplay.BossLines).bottomColored
               && this.lastSeenMs == (other as SlayerHPDisplay.BossLines).lastSeenMs
            }
      }
   }
}
