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

public object WitherShieldOverlay {
   private final var witherImpactEndTime: Long
   private final var movableOverlay: MovableOverlay?
   private final var isPositioningMode: Boolean
   private final var appliedSavedPos: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun trigger() {
      if (Config.witherShieldOverlay) {
         witherImpactEndTime = System.currentTimeMillis() + 5000
      }
   }

   public fun onInitializeClient() {
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
         val var10000: TextRenderer = this.getMc().field_1772
         PersistentState.witherShieldX = -var10000.method_1727(if (Config.witherShieldCompact) "5" else "Wither Shield: 5") / 2
         PersistentState.witherShieldY = 15
         PersistentState.witherShieldInitDone = true
         JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      }

      if (movableOverlay == null) {
         movableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "witherShieldOverlay",
               "Wither Shield",
               this.getMc().method_22683().method_4486() / 2 + PersistentState.witherShieldX,
               this.getMc().method_22683().method_4502() / 2 + PersistentState.witherShieldY,
               160,
               this.getMc().field_1772.field_2000
            )
            if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_1@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->
               val remaining: Double = INSTANCE.remainingSec()
               if (remaining <= 0.0 && INSTANCE.getMc().field_1755 !is MovableOverlayScreen) {
                  return@lambda_1 Unit.INSTANCE
               } else {
                  ctx.method_51433(INSTANCE.getMc().field_1772, INSTANCE.displayText(if (remaining <= 0.0) 4.8 else remaining), x, y, -1, true)
                  return@lambda_1 Unit.INSTANCE
               }
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->
               val ox: Int = x - INSTANCE.getMc().method_22683().method_4486() / 2
               val oy: Int = y - INSTANCE.getMc().method_22683().method_4502() / 2
               PersistentState.witherShieldX = ox
               PersistentState.witherShieldY = oy
               PersistentState.witherShieldInitDone = true
               appliedSavedPos = true
               JooonConfigManager.INSTANCE.write("jooonreimagined_state")
               Unit.INSTANCE
            }
         }

         if (Config.witherShieldOverlay && PersistentState.witherShieldMovable) {
            if (movableOverlay != null) {
               movableOverlay.register()
            }
         }
      }

      if (!appliedSavedPos) {
         val var11: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.witherShieldX
         val var13: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.witherShieldY
         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(var11, var13)
         }

         appliedSavedPos = true
      }
   }

   fun renderStatic(context: DrawContext) {
      val remaining: Double = this.remainingSec()
      if (!(remaining <= 0.0)) {
         val x: Int = context.method_51421() / 2 + PersistentState.witherShieldX
         val y: Int = context.method_51443() / 2 + PersistentState.witherShieldY
         context.method_51433(this.getMc().field_1772, this.displayText(remaining), x, y, -1, true)
      }
   }

   private fun remainingSec(): Double {
      return (witherImpactEndTime - System.currentTimeMillis()) / 1000.0
   }

   private fun displayText(remaining: Double): String {
      return if (Config.witherShieldCompact)
         java.lang.String.valueOf(MathKt.roundToInt(Math.ceil(remaining)))
         else
         "§aWither Shield:§f ${MathKt.roundToInt(Math.ceil(remaining))}"
      }

   public fun toggleMovableOverlay() {
      PersistentState.witherShieldMovable = !PersistentState.witherShieldMovable
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      if (PersistentState.witherShieldMovable) {
         this.ensureOverlayReadyForToggle()
         val realX: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.witherShieldX
         val realY: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.witherShieldY
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
         val var5: MovableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "witherShieldOverlay",
               "Wither Shield",
               this.getMc().method_22683().method_4486() / 2 + PersistentState.witherShieldX,
               this.getMc().method_22683().method_4502() / 2 + PersistentState.witherShieldY,
               160,
               this.getMc().field_1772.field_2000
            )
            var5.resetFunction = { 
            val var10000: Window = INSTANCE.getMc().method_22683()
            val var5: TextRenderer = INSTANCE.getMc().field_1772
            TuplesKt.to(var10000.method_4486() / 2 + -var5.method_1727(INSTANCE.displayText(4.8)) / 2, var10000.method_4502() / 2 + 15)
         }
         var5.renderFunction = lambda_6_lambda_4@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->
            val remaining: Double = INSTANCE.remainingSec()
            if (remaining <= 0.0 && INSTANCE.getMc().field_1755 !is MovableOverlayScreen) {
               return@lambda_6_lambda_4 Unit.INSTANCE
            } else {
               ctx.method_51433(INSTANCE.getMc().field_1772, INSTANCE.displayText(if (remaining <= 0.0) 4.8 else remaining), x, y, -1, true)
               return@lambda_6_lambda_4 Unit.INSTANCE
            }
         }
         var5.onPositionChanged = { x: Int, y: Int ->
            val ox: Int = x - INSTANCE.getMc().method_22683().method_4486() / 2
            val oy: Int = y - INSTANCE.getMc().method_22683().method_4502() / 2
            PersistentState.witherShieldX = ox
            PersistentState.witherShieldY = oy
            PersistentState.witherShieldInitDone = true
            appliedSavedPos = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }
         movableOverlay = var5
      }
   }

   public fun onConfigChanged() {
      if (Config.witherShieldOverlay && PersistentState.witherShieldMovable) {
         this.ensureOverlayReadyForToggle()
         val realX: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.witherShieldX
         val realY: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.witherShieldY
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

   public fun setPositioningMode(enabled: Boolean) {
      isPositioningMode = enabled
   }

   public fun isInPositioningMode(): Boolean {
      return isPositioningMode
   }
}
