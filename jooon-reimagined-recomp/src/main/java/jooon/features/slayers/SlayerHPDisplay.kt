package jooon.features.slayers

import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.gui.MovableOverlayScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.Window

object SlayerHPDisplay {
   private const val SCAN_INTERVAL: Int = 10
   private var tickCounter: Int
   private var current: jooon.features.slayers.SlayerHPDisplay.BossLines?
   private var overlay: MovableOverlay?
   private var appliedSavedPos: Boolean
   private val COLOR_RX: Regex = Regex("§.")
   private val HEART_RX: Regex = Regex("[❤♥]")
   private val TIME_LINE_RX: Regex = Regex("^\\s*§?[0-9a-fk-or]?(\\d{1,2}:\\d{2})\\s*$", RegexOption.IGNORE_CASE)

   fun getMc(): MinecraftClient {
return var10000
   }

   private fun strip(s: String): String {
      return trim(COLOR_RX.replace(s, "")).toString()
   }

   private fun fromJsonOrRaw(s: String): String {
      if (startsWith$default(s, "{\"text\":\"", false, 2, null)) {


         val var10000: String
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

   fun onInitializeClient() {
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

      var var10000: MatchResult = Regex.find$default(HEART_RX, plain, 0, 2, null)
      if (var10000 != null) {

         if (var9 != null) {

            var10000 = Regex.find$default(Regex("([0-9][0-9.,]*)(?:\\s*[kKmMbB])?\\s*$"), trimEnd(var10).toString(), 0, 2, null)
            if (var10000 == null) {
               return false
            }

               replace$default(var10000.getGroupValues().get(1) as String, ",", "", false, 4, null)
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



         PersistentState.slayerHPDisplayX = realX
         PersistentState.slayerHPDisplayY = realY
         PersistentState.slayerHPDisplayInitDone = true
         JooonConfigManager.write("jooonreimagined_state")
      }

      if (overlay == null) {

            .createOverlay(
               "slayerHP",
               "(02:59) Atoned Horror 9.8M❤",
               this.getMc().getWindow().getScaledWidth() / 2 + PersistentState.slayerHPDisplayX,
               this.getMc().getWindow().getScaledHeight() / 2 + PersistentState.slayerHPDisplayY,
               180,
return 12
            )
            var6.resetFunction = { 




            Pair(var10000.getScaledWidth() / 2 + ox, var10000.getScaledHeight() / 2 + oy)
         }
         overlay = var6
         if (overlay != null) {
            overlay.renderFunction = { c: DrawContext, x: Int, y: Int, var3: Float ->
               drawLine(c, x, y)
return Unit
            }
         }

         if (overlay != null) {
            overlay.onPositionChanged = { x: Int, y: Int ->


               PersistentState.slayerHPDisplayX = ox
               PersistentState.slayerHPDisplayY = oy
               PersistentState.slayerHPDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
         }

         if (Config.slayerHPDisplayEnabled && PersistentState.slayerHPDisplayMovable) {
            if (overlay != null) {
               overlay.register()
            }
         }
      }

      if (!appliedSavedPos) {


         if (overlay != null) {
            overlay.setPositionSilently(var14, var16)
         }

         appliedSavedPos = true
      }
   }

   fun drawLine(ctx: DrawContext, x: Int, y: Int) {

      if (this.getMc().currentScreen is MovableOverlayScreen && current == null) {
         ctx.drawText(var10000, "(02:59) Atoned Horror 9.8M§c❤", x, y, -1, true)
      } else if (current != null) {
         if (this.isZeroHp(current)) {
            current = null
         } else {

            if (var8 != null) {
               ctx.drawText(var10000, var8, x, y, -1, true)
            }
         }
      }
   }

   fun renderAt(ctx: DrawContext, ox: Int, oy: Int) {
      if (!PersistentState.slayerHPDisplayMovable || overlay == null) {
         this.drawLine(ctx, ctx.getScaledWindowWidth() / 2 + ox, ctx.getScaledWindowHeight() / 2 + oy)
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

               if (var13 != null) {


                  var10000 = Regex.find$default(HEART_RX, bottomPlain, 0, 2, null)
                  if (var10000 != null) {

                     if (var15 != null) {



                        if (splitAt <= 0) {
                           return null
                        }



                        return "$timePart §a$namePlain§r §c${trim(var18).toString()} ❤§r"
                     }
                  }

                  return null
               }
            }
         }

         return null
      }
   }

   fun onConfigChanged() {
      if (Config.slayerHPDisplayEnabled && PersistentState.slayerHPDisplayMovable) {
         if (overlay == null) {

               .createOverlay(
                  "slayerHP",
                  "(02:59) Atoned Horror 9.8M❤",
                  this.getMc().getWindow().getScaledWidth() / 2 + PersistentState.slayerHPDisplayX,
                  this.getMc().getWindow().getScaledHeight() / 2 + PersistentState.slayerHPDisplayY,
                  180,
return 12
               )
               var5.renderFunction = { c: DrawContext, x: Int, y: Int, var3: Float ->
               drawLine(c, x, y)
return Unit
            }
            var5.onPositionChanged = { x: Int, y: Int ->


               PersistentState.slayerHPDisplayX = ox
               PersistentState.slayerHPDisplayY = oy
               PersistentState.slayerHPDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
            overlay = var5
         }


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
      val timeColored: String
      val bottomColored: String
      val lastSeenMs: Long

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

      fun copy(timeColored: String = this.timeColored, bottomColored: String = this.bottomColored, lastSeenMs: Long = this.lastSeenMs): jooon.features.slayers.SlayerHPDisplay.BossLines {
         return SlayerHPDisplay.BossLines(timeColored, bottomColored, lastSeenMs)
      }

      override fun toString(): String {
         return "BossLines(timeColored=${this.timeColored}, bottomColored=${this.bottomColored}, lastSeenMs=${this.lastSeenMs})"
      }

      override fun hashCode(): Int {
         return (this.timeColored.hashCode() * 31 + this.bottomColored.hashCode()) * 31 + java.lang.Long.hashCode(this.lastSeenMs)
      }

      override operator fun equals(other: Any?): Boolean {
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
