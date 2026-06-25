package jooon.gui

import jooon.config.JooonConfigManager
import jooon.features.jerry.MayorDisplay
import jooon.mixins.OptionsAccessor
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function4
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.input.KeyInput
import net.minecraft.client.option.AttackIndicator
import net.minecraft.client.option.GameOptions
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

public class MovableOverlayScreen : Screen {
   private final val overlayId: String
   private final val overlayName: String
   private final val initialX: Int
   private final val initialY: Int
   private final val onPositionChanged: (Int, Int) -> Unit
   private Screen returnScreen;
   private MinecraftClient mc;
   private final var overlayX: Int
   private final var overlayY: Int
   private final var isDragging: Boolean
   private final var dragOffsetX: Int
   private final var dragOffsetY: Int
   private final var movementStep: Int
   private final val overlayWidth: Int
   private final val overlayHeight: Int

   fun MovableOverlayScreen(
      overlayId: java.lang.String, overlayName: java.lang.String, initialX: Int, initialY: Int, onPositionChanged: (Int?, Int?) -> Unit, returnScreen: Screen?
   ) {
      super(Text.method_43470("Move $overlayName") as Text)
      this.overlayId = overlayId
      this.overlayName = overlayName
      this.initialX = initialX
      this.initialY = initialY
      this.onPositionChanged = onPositionChanged
      this.returnScreen = returnScreen
      val var10001: MinecraftClient = MinecraftClient.method_1551()
      this.mc = var10001
      this.overlayX = this.initialX
      this.overlayY = this.initialY
      this.movementStep = 5
      this.overlayWidth = 200
      this.overlayHeight = 20
   }

   fun method_25426() {
      super.method_25426()
      if (this.overlayId == "mayorDisplay") {
         MayorDisplay.INSTANCE.setPositioningMode(true)
      }

      val centerX: Int = this.field_22789 / 2
      val buttonY: Int = this.field_22790 - 30
      this.method_37063(ButtonWidget.method_46430(Text.method_43470("Reset Position") as Text, { it: ButtonWidget ->
         var var4: Pair
         run label18@{
            val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay(`this$0`.overlayId)
            if (overlay != null) {
               val var10000: Function0 = overlay.resetFunction
               if (var10000 != null) {
                  var4 = var10000() as Pair
                  if (var4 != null) {
                     return@label18
                  }
               }
            }

            var4 = `this$0`.defaultPosForOverlay()
         }

         `this$0`.overlayX = (var4.getFirst() as java.lang.Number).intValue()
         `this$0`.overlayY = (var4.getSecond() as java.lang.Number).intValue()
      }).method_46433(centerX - 100, buttonY).method_46437(100, 20).method_46431() as Element)
      this.method_37063(ButtonWidget.method_46430(Text.method_43470("Save & Close") as Text, { it: ButtonWidget ->
         `this$0`.onPositionChanged(`this$0`.overlayX, `this$0`.overlayY)
         `this$0`.openReturnScreen()
      }).method_46433(centerX + 10, buttonY).method_46437(100, 20).method_46431() as Element)
   }

   private fun defaultPosForOverlay(): Pair<Int, Int> {
      val var1: java.lang.String = this.overlayId
      when (this.overlayId.hashCode()) {
         -1660480652 -> {
            if (var1.equals("witherShieldOverlay")) {
               return TuplesKt.to(this.field_22789 / 2 - 60, this.field_22790 / 2 + 15)
            }
         }
         -1482230202 -> {
            if (var1.equals("slayerHP")) {
               return TuplesKt.to(this.field_22789 / 2 - 90, this.field_22790 / 2 - 72)
            }
         }
         1976318564 -> {
            if (var1.equals("dbDisplay")) {
               return TuplesKt.to(this.field_22789 / 2 - 50, this.field_22790 / 2 - 26)
            }
         }
         else -> {}
      }

      return TuplesKt.to(10, 10)
   }

   private fun crosshairAbove(): Pair<Int, Int> {
      val var10000: TextRenderer = this.field_22793
      val var9: MutableText = Text.method_43470("⛏ 20")
      val w: Int = var10000.method_27525(var9 as StringVisitable)
      val h: Int = var10000.field_2000
      val var10: GameOptions = this.mc.field_1690
      val attackGap: Int = if ((var10 as OptionsAccessor).getAttackIndicator().method_41753() === AttackIndicator.field_18152) 9 else 0
      val x0: Int = this.field_22789 / 2 - w / 2
      val y0: Int = this.field_22790 / 2 - attackGap - 4 - h
      return TuplesKt.to(x0, y0)
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.drawTransparentBackground(context)
      var var10000: MutableText = Text.method_43470("Move ${this.overlayName} Overlay").method_27692(Formatting.field_1065)
      context.method_51439(
         this.field_22793, var10000 as Text, (this.field_22789 - this.field_22793.method_27525(var10000 as StringVisitable)) / 2, 20, -1, false
      )
      var10000 = Text.method_43470("Click and drag to move • Arrow keys for precise movement • ESC to cancel").method_27692(Formatting.field_1080)
      context.method_51439(
         this.field_22793, var10000 as Text, (this.field_22789 - this.field_22793.method_27525(var10000 as StringVisitable)) / 2, 40, -1, false
      )
      this.renderOverlayPreview(context, this.overlayX, this.overlayY, delta)
      var10000 = Text.method_43470("Position: (${this.overlayX}, ${this.overlayY})").method_27692(Formatting.field_1054)
      context.method_51439(this.field_22793, var10000 as Text, 10, this.field_22790 - 50, -1, false)
      super.method_25394(context, mouseX, mouseY, delta)
   }

   fun drawTransparentBackground(context: DrawContext) {
      context.method_25294(0, 0, this.field_22789, this.field_22790, Integer.MIN_VALUE)
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun renderOverlayPreview(context: DrawContext, x: Int, y: Int, delta: Float) {
      val var5: Pair = this.currentOverlayBounds()
      val textWidth: Int = (var5.component1() as java.lang.Number).intValue()
      val textHeight: Int = (var5.component2() as java.lang.Number).intValue()
      context.method_25294(x - 2, y - 2, x + textWidth + 2, y + textHeight + 2, Integer.MIN_VALUE)
      context.method_73198(x - 2, y - 2, textWidth + 4, textHeight + 4, -1)
      if (this.overlayId == "mayorDisplay") {
         val var10000: TextRenderer = this.mc.field_1772
         val var20: MutableText = Text.method_43470("Current Perkpocalypse Mayor: ").method_27692(Formatting.field_1054)
         val overlayText: MayorDisplay.Mayor = MayorDisplay.INSTANCE.getCurrentMayor()
         val var22: MutableText
         if (overlayText != null) {
            val var21: MutableText = Text.method_43470(overlayText.displayName).method_27692(overlayText.getColor())
            var22 = var20.method_27661().method_10852(var21 as Text)
         } else {
            var22 = var20.method_27661()
               .method_10852(Text.method_43470("Unknown! Please open your Skyblock Calendar.").method_27692(Formatting.field_1080) as Text)
            }

         context.method_51439(var10000, var22 as Text, x, y, -1, false)
      } else {
         val var14: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay(this.overlayId)
         val var16: Function4 = if (var14 != null) var14.renderFunction else null
         if (var16 != null && var14 != null) {
            var16(context, x, y, delta)
            context.method_73198(x - 1, y - 1, var14.width + 2, var14.height + 2, -1)
         } else {
            val var23: MutableText = Text.method_43470("${this.overlayName} Preview").method_27692(Formatting.field_1068)
            context.method_51439(this.field_22793, var23 as Text, x, y, -1, false)
         }
      }

      val var24: MutableText = Text.method_43470("⋮⋮").method_27692(Formatting.field_1080)
      context.method_51439(this.field_22793, var24 as Text, x + textWidth - 20, y, -1, false)
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (event.method_74245() == 0 && this.isPointInOverlay((int)event.comp_4798(), (int)event.comp_4799())) {
         this.isDragging = true
         this.dragOffsetX = (int)event.comp_4798() - this.overlayX
         this.dragOffsetY = (int)event.comp_4799() - this.overlayY
         true
      } else {
         super.method_25402(event, handled)
      }
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      if (this.isDragging && event.method_74245() == 0) {
         val var6: Pair = this.currentOverlayBounds()
         this.overlayX = RangesKt.coerceIn((int)event.comp_4798() - this.dragOffsetX, 0, this.field_22789 - (var6.component1() as java.lang.Number).intValue())
         this.overlayY = RangesKt.coerceIn((int)event.comp_4799() - this.dragOffsetY, 0, this.field_22790 - (var6.component2() as java.lang.Number).intValue())
         true
      } else {
         super.method_25403(event, dx, dy)
      }
   }

   fun method_25406(event: Click): Boolean {
      if (event.method_74245() == 0) {
         this.isDragging = false
         true
      } else {
         super.method_25406(event)
      }
   }

   fun method_25404(event: KeyInput): Boolean {
      when (event.comp_4795()) {
         256 -> {
            this.onPositionChanged(this.overlayX, this.overlayY)
            this.openReturnScreen()
            true
         }
         257, 258, 259, 260, 261 -> super.method_25404(event)
         262 -> {
            this.overlayX = RangesKt.coerceAtMost(
               this.overlayX + this.movementStep, this.field_22789 - (this.currentOverlayBounds().getFirst() as java.lang.Number).intValue()
            )
            true
         }
         263 -> {
            this.overlayX = RangesKt.coerceAtLeast(this.overlayX - this.movementStep, 0)
            true
         }
         264 -> {
            this.overlayY = RangesKt.coerceAtMost(
               this.overlayY + this.movementStep, this.field_22790 - (this.currentOverlayBounds().getSecond() as java.lang.Number).intValue()
            )
            true
         }
         265 -> {
            this.overlayY = RangesKt.coerceAtLeast(this.overlayY - this.movementStep, 0)
            true
         }
         else -> super.method_25404(event)
      }
   }

   fun method_25419() {
      if (this.overlayId == "mayorDisplay") {
         MayorDisplay.INSTANCE.setPositioningMode(false)
      }

      if (this.returnScreen != null) {
         this.mc.method_1507(this.returnScreen)
      } else {
         super.method_25419()
      }
   }

   private fun openReturnScreen() {
      if (this.overlayId == "mayorDisplay") {
         MayorDisplay.INSTANCE.setPositioningMode(false)
      }

      if (this.returnScreen != null) {
         this.mc.method_1507(this.returnScreen)
      } else {
         try {
            this.mc.method_1507(JooonConfigManager.INSTANCE.getScreen(null, "jooonreimagined"))
         } catch (var2: Exception) {
            var2.printStackTrace()
            super.method_25419()
         }
      }
   }

   private fun isPointInOverlay(px: Int, py: Int): Boolean {
      val var3: Pair = this.currentOverlayBounds()
      return px >= this.overlayX
         && px <= this.overlayX + (var3.component1() as java.lang.Number).intValue()
         && py >= this.overlayY
         && py <= this.overlayY + (var3.component2() as java.lang.Number).intValue()
      }

   private fun currentOverlayBounds(): Pair<Int, Int> {
      if (this.overlayId == "mayorDisplay") {
         return TuplesKt.to(this.field_22793.method_27525(MayorDisplay.INSTANCE.getCurrentMayorText() as StringVisitable), this.field_22793.field_2000)
      } else {
         val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay(this.overlayId)
         return if (overlay != null) TuplesKt.to(overlay.width, overlay.height) else TuplesKt.to(this.overlayWidth, this.overlayHeight)
      }
   }
}
