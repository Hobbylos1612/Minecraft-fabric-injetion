package jooon.features.dungeons

import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.mixins.OptionsAccessor
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.AttackIndicator
import net.minecraft.client.option.GameOptions
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.Window
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object DBDisplay {
   private var currentCharges: Int
   private var isHoldingDungeonBreaker: Boolean
   private var movableOverlay: MovableOverlay?
   private var isPositioningMode: Boolean
   private var appliedSavedPos: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      HudRenderCallback.EVENT
         .register(
            { context: DrawContext, var1: RenderTickCounter ->
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

   fun ensureOverlayReady(context: DrawContext) {
      if (!PersistentState.dbDisplayInitDone) {







         PersistentState.dbDisplayX = ox
         PersistentState.dbDisplayY = oy
         PersistentState.dbDisplayInitDone = true
         JooonConfigManager.write("jooonreimagined_state")
      }

      if (movableOverlay == null) {
         movableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "dbDisplay",
               "⛏ 20",
               this.getMc().getWindow().getScaledWidth() / 2 + PersistentState.dbDisplayX,
               this.getMc().getWindow().getScaledHeight() / 2 + PersistentState.dbDisplayY,
               50,
return 20
            )
            if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_2@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->
               if (!isHoldingDungeonBreaker) {
                  return@lambda_2 Unit
               } else {

                  ctx.drawText(var10000, getDisplayTextWithColor(), x, y, -1, true)
                  return@lambda_2 Unit
               }
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->


               PersistentState.dbDisplayX = ox
               PersistentState.dbDisplayY = oy
               PersistentState.dbDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
         }

         if (Config.dbDisplayEnabled && PersistentState.dbDisplayMovable) {
            if (movableOverlay != null) {
               movableOverlay.register()
            }
         }
      }

      if (!appliedSavedPos) {


         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(var14, var16)
         }

         appliedSavedPos = true
      }
   }

   private fun updateChargesCount() {

      if (var10000 != null) {

         if (!this.isDungeonBreaker(var3)) {
            currentCharges = 0
            isHoldingDungeonBreaker = false
         } else {
            isHoldingDungeonBreaker = true
            currentCharges = this.extractChargesFromLore(var3)
         }
      }
   }

   fun isDungeonBreaker(itemStack: ItemStack): Boolean {
      if (itemStack.isEmpty()) {
return false
      } else {
         var var10000: String = itemStack.getItem().toString()
         if (!contains$default(var10000, "diamond_pickaxe", false, 2, null)) {
return false
         } else {

            if (var3 != null) {
               var10000 = var3.getString()
               if (var10000 != null) {
                  var10000.contains("DungeonBreaker", true)
               }
            }
return false
         }
      }
   }

   fun extractChargesFromLore(itemStack: ItemStack): Int {

      if (var10000 == null) {
return 0
      } else {
         val var5: java.util.List = var10000.lines()

            Regex("Charges:\\s*(\\d+)/20", RegexOption.IGNORE_CASE), joinToString$default(var5, " ", null, null, 0, null, { it: Text ->

               var10000 as java.lang.CharSequence
            }, 30, null), 0, 2, null
         )
         if (var6 == null) {
return 0
         } else {

            var7 ?: 0
         }
      }
   }

   fun renderDBDisplay(context: DrawContext) {
      if (!isPositioningMode) {

         if (scr == null || !(scr.getClass().getSimpleName() == "MovableOverlayScreen")) {
            if (!PersistentState.dbDisplayMovable || movableOverlay == null) {
               if (isHoldingDungeonBreaker) {
                  this.renderDBDisplayAt(
                     context, context.getScaledWindowWidth() / 2 + PersistentState.dbDisplayX, context.getScaledWindowHeight() / 2 + PersistentState.dbDisplayY
                  )
               }
            }
         }
      }
   }

   fun renderDBDisplayAt(context: DrawContext, x: Int, y: Int) {
      if (isHoldingDungeonBreaker) {

         context.drawText(var10000, this.getDisplayTextWithColor(), x, y, -1, true)
      }
   }

   fun setPositioningMode(enabled: Boolean) {
      isPositioningMode = enabled
   }

   fun isInPositioningMode(): Boolean {
      return isPositioningMode
   }

   fun toggleMovableOverlay() {
      PersistentState.dbDisplayMovable = !PersistentState.dbDisplayMovable
      JooonConfigManager.write("jooonreimagined_state")
      if (PersistentState.dbDisplayMovable) {
         this.ensureOverlayReadyForToggle()


         if (movableOverlay != null) {
            movableOverlay.register()
         }

         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(realX, realY)
         }
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   private fun ensureOverlayReadyForToggle() {
      if (movableOverlay == null) {

            .createOverlay(
               "dbDisplay",
               "⛏ 20",
               this.getMc().getWindow().getScaledWidth() / 2 + PersistentState.dbDisplayX,
               this.getMc().getWindow().getScaledHeight() / 2 + PersistentState.dbDisplayY,
               50,
return 20
            )
            var5.resetFunction = { 




            Pair(var10000.getScaledWidth() / 2 + ox, var10000.getScaledHeight() / 2 + oy)
         }
         movableOverlay = var5
         if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_7@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->
               if (!isHoldingDungeonBreaker) {
                  return@lambda_7 Unit
               } else {

                  ctx.drawText(var10000, getDisplayTextWithColor(), x, y, -1, true)
                  return@lambda_7 Unit
               }
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->


               PersistentState.dbDisplayX = ox
               PersistentState.dbDisplayY = oy
               PersistentState.dbDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
         }
      }
   }

   fun getOverlayPosition(): Pair<Int, Int> {
      if (movableOverlay != null) {

         if (var10000 != null) {
            return var10000
         }
      }

      return Pair(PersistentState.dbDisplayX, PersistentState.dbDisplayY)
   }

   fun onConfigChanged() {
      if (Config.dbDisplayEnabled && PersistentState.dbDisplayMovable) {
         this.ensureOverlayReadyForToggle()


         if (movableOverlay != null) {
            movableOverlay.register()
         }

         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(realX, realY)
         }
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   fun getCurrentCharges(): Int {
      return currentCharges
   }

   fun getDisplayTextWithColor(): Text {


      var10000 as Text
   }

   fun getDisplayText(): Text {
      this.getDisplayTextWithColor()
   }
}
