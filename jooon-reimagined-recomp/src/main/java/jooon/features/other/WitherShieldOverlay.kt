package jooon.features.other

import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.gui.MovableOverlayScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import kotlin.math.MathKt
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.Window

object WitherShieldOverlay {
   private var witherImpactEndTime: Long
   private var movableOverlay: MovableOverlay?
   private var isPositioningMode: Boolean
   private var appliedSavedPos: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun trigger() {
      if (Config.witherShieldOverlay) {
         witherImpactEndTime = System.currentTimeMillis() + 5000
      }
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
      }

   fun ensureOverlayReady(context: DrawContext) {
      if (!PersistentState.witherShieldInitDone) {

         PersistentState.witherShieldX = -var10000.getWidth(if (Config.witherShieldCompact) "5" else "Wither Shield: 5") / 2
         PersistentState.witherShieldY = 15
         PersistentState.witherShieldInitDone = true
         JooonConfigManager.write("jooonreimagined_state")
      }

      if (movableOverlay == null) {
         movableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "witherShieldOverlay",
               "Wither Shield",
               this.getMc().getWindow().getScaledWidth() / 2 + PersistentState.witherShieldX,
               this.getMc().getWindow().getScaledHeight() / 2 + PersistentState.witherShieldY,
               160,
               this.getMc().textRenderer.fontHeight
            )
            if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_1@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->

               if (remaining <= 0.0 && getMc().currentScreen !is MovableOverlayScreen) {
                  return@lambda_1 Unit
               } else {
                  ctx.drawText(getMc().textRenderer, displayText(if (remaining <= 0.0) 4.8 else remaining), x, y, -1, true)
                  return@lambda_1 Unit
               }
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->


               PersistentState.witherShieldX = ox
               PersistentState.witherShieldY = oy
               PersistentState.witherShieldInitDone = true
               appliedSavedPos = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
         }

         if (Config.witherShieldOverlay && PersistentState.witherShieldMovable) {
            if (movableOverlay != null) {
               movableOverlay.register()
            }
         }
      }

      if (!appliedSavedPos) {


         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(var11, var13)
         }

         appliedSavedPos = true
      }
   }

   fun renderStatic(context: DrawContext) {

      if (!(remaining <= 0.0)) {


         context.drawText(this.getMc().textRenderer, this.displayText(remaining), x, y, -1, true)
      }
   }

   private fun remainingSec(): Double {
      return (witherImpactEndTime - System.currentTimeMillis()) / 1000.0
   }

   private fun displayText(remaining: Double): String {
      return if (Config.witherShieldCompact)
         java.lang.String.valueOf((Math.ceil(remaining)).roundToInt())
return else
         "§aWither Shield:§f ${(Math.ceil(remaining)).roundToInt()}"
      }

   fun toggleMovableOverlay() {
      PersistentState.witherShieldMovable = !PersistentState.witherShieldMovable
      JooonConfigManager.write("jooonreimagined_state")
      if (PersistentState.witherShieldMovable) {
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
               "witherShieldOverlay",
               "Wither Shield",
               this.getMc().getWindow().getScaledWidth() / 2 + PersistentState.witherShieldX,
               this.getMc().getWindow().getScaledHeight() / 2 + PersistentState.witherShieldY,
               160,
               this.getMc().textRenderer.fontHeight
            )
            var5.resetFunction = { 


            Pair(var10000.getScaledWidth() / 2 + -var5.getWidth(displayText(4.8)) / 2, var10000.getScaledHeight() / 2 + 15)
         }
         var5.renderFunction = lambda_6_lambda_4@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->

            if (remaining <= 0.0 && getMc().currentScreen !is MovableOverlayScreen) {
               return@lambda_6_lambda_4 Unit
            } else {
               ctx.drawText(getMc().textRenderer, displayText(if (remaining <= 0.0) 4.8 else remaining), x, y, -1, true)
               return@lambda_6_lambda_4 Unit
            }
         }
         var5.onPositionChanged = { x: Int, y: Int ->


            PersistentState.witherShieldX = ox
            PersistentState.witherShieldY = oy
            PersistentState.witherShieldInitDone = true
            appliedSavedPos = true
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }
         movableOverlay = var5
      }
   }

   fun onConfigChanged() {
      if (Config.witherShieldOverlay && PersistentState.witherShieldMovable) {
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

   fun setPositioningMode(enabled: Boolean) {
      isPositioningMode = enabled
   }

   fun isInPositioningMode(): Boolean {
      return isPositioningMode
   }
}
