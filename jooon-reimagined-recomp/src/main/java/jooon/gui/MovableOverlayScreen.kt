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

class MovableOverlayScreen : Screen {
   private val overlayId: String
   private val overlayName: String
   private val initialX: Int
   private val initialY: Int
   private val onPositionChanged: (Int, Int) -> Unit
   private Screen returnScreen;
   private MinecraftClient mc;
   private var overlayX: Int
   private var overlayY: Int
   private var isDragging: Boolean
   private var dragOffsetX: Int
   private var dragOffsetY: Int
   private var movementStep: Int
   private val overlayWidth: Int
   private val overlayHeight: Int

   fun MovableOverlayScreen(
      overlayId: String, overlayName: String, initialX: Int, initialY: Int, onPositionChanged: (Int?, Int?) -> Unit, returnScreen: Screen?
   ) {
      super(Text.literal("Move $overlayName") as Text)
      this.overlayId = overlayId
      this.overlayName = overlayName
      this.initialX = initialX
      this.initialY = initialY
      this.onPositionChanged = onPositionChanged
      this.returnScreen = returnScreen

      this.mc = var10001
      this.overlayX = this.initialX
      this.overlayY = this.initialY
      this.movementStep = 5
      this.overlayWidth = 200
      this.overlayHeight = 20
   }

   fun method_25426() {
      super.init()
      if (this.overlayId == "mayorDisplay") {
         MayorDisplay.setPositioningMode(true)
      }


      this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset Position") as Text, { it: ButtonWidget ->
         var var4: Pair
         run label18@{

            if (overlay != null) {

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
      }).position(centerX - 100, buttonY).size(100, 20).build() as Element)
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close") as Text, { it: ButtonWidget ->
         `this$0`.onPositionChanged(`this$0`.overlayX, `this$0`.overlayY)
         `this$0`.openReturnScreen()
      }).position(centerX + 10, buttonY).size(100, 20).build() as Element)
   }

   private fun defaultPosForOverlay(): Pair<Int, Int> {

      when (this.overlayId.hashCode()) {
         -1660480652 -> {
            if (var1.equals("witherShieldOverlay")) {
               return Pair(this.width / 2 - 60, this.height / 2 + 15)
            }
         }
         -1482230202 -> {
            if (var1.equals("slayerHP")) {
               return Pair(this.width / 2 - 90, this.height / 2 - 72)
            }
         }
         1976318564 -> {
            if (var1.equals("dbDisplay")) {
               return Pair(this.width / 2 - 50, this.height / 2 - 26)
            }
         }
         else -> {}
      }

      return Pair(10, 10)
   }

   private fun crosshairAbove(): Pair<Int, Int> {








      return Pair(x0, y0)
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.drawTransparentBackground(context)
      var var10000: MutableText = Text.literal("Move ${this.overlayName} Overlay").formatted(Formatting.GOLD)
      context.drawText(
         this.textRenderer, var10000 as Text, (this.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2, 20, -1, false
      )
      var10000 = Text.literal("Click and drag to move • Arrow keys for precise movement • ESC to cancel").formatted(Formatting.GRAY)
      context.drawText(
         this.textRenderer, var10000 as Text, (this.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2, 40, -1, false
      )
      this.renderOverlayPreview(context, this.overlayX, this.overlayY, delta)
      var10000 = Text.literal("Position: (${this.overlayX}, ${this.overlayY})").formatted(Formatting.YELLOW)
      context.drawText(this.textRenderer, var10000 as Text, 10, this.height - 50, -1, false)
      super.render(context, mouseX, mouseY, delta)
   }

   fun drawTransparentBackground(context: DrawContext) {
      context.fill(0, 0, this.width, this.height, Integer.MIN_VALUE)
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun renderOverlayPreview(context: DrawContext, x: Int, y: Int, delta: Float) {



      context.fill(x - 2, y - 2, x + textWidth + 2, y + textHeight + 2, Integer.MIN_VALUE)
      context.drawStrokedRectangle(x - 2, y - 2, textWidth + 4, textHeight + 4, -1)
      if (this.overlayId == "mayorDisplay") {


         val overlayText: MayorDisplay.Mayor = MayorDisplay.getCurrentMayor()
         val var22: MutableText
         if (overlayText != null) {

            var22 = var20.copy().append(var21 as Text)
         } else {
            var22 = var20.copy()
               .append(Text.literal("Unknown! Please open your Skyblock Calendar.").formatted(Formatting.GRAY) as Text)
            }

         context.drawText(var10000, var22 as Text, x, y, -1, false)
      } else {


         if (var16 != null && var14 != null) {
            var16(context, x, y, delta)
            context.drawStrokedRectangle(x - 1, y - 1, var14.width + 2, var14.height + 2, -1)
         } else {

            context.drawText(this.textRenderer, var23 as Text, x, y, -1, false)
         }
      }

      context.drawText(this.textRenderer, var24 as Text, x + textWidth - 20, y, -1, false)
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (event.button() == 0 && this.isPointInOverlay(event.x().toInt(), event.y().toInt())) {
         this.isDragging = true
         this.dragOffsetX = event.x().toInt() - this.overlayX
         this.dragOffsetY = event.y().toInt() - this.overlayY
return true
      } else {
         super.mouseClicked(event, handled)
      }
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      if (this.isDragging && event.button() == 0) {

         this.overlayX = (event.x().toInt() - this.dragOffsetX).coerceIn(0, this.width - (var6.component1() as java.lang.Number).intValue())
         this.overlayY = (event.y().toInt() - this.dragOffsetY).coerceIn(0, this.height - (var6.component2() as java.lang.Number).intValue())
return true
      } else {
         super.mouseDragged(event, dx, dy)
      }
   }

   fun method_25406(event: Click): Boolean {
      if (event.button() == 0) {
         this.isDragging = false
return true
      } else {
         super.mouseReleased(event)
      }
   }

   fun method_25404(event: KeyInput): Boolean {
      when (event.key()) {
         256 -> {
            this.onPositionChanged(this.overlayX, this.overlayY)
            this.openReturnScreen()
return true
         }
         257, 258, 259, 260, 261 -> super.keyPressed(event)
         262 -> {
            this.overlayX = (this.overlayX + this.movementStep).coerceAtMost(this.width - (this.currentOverlayBounds().getFirst() as java.lang.Number).intValue())
return true
         }
         263 -> {
            this.overlayX = (this.overlayX - this.movementStep).coerceAtLeast(0)
return true
         }
         264 -> {
            this.overlayY = (this.overlayY + this.movementStep).coerceAtMost(this.height - (this.currentOverlayBounds().getSecond() as java.lang.Number).intValue())
return true
         }
         265 -> {
            this.overlayY = (this.overlayY - this.movementStep).coerceAtLeast(0)
return true
         }
         else -> super.keyPressed(event)
      }
   }

   fun method_25419() {
      if (this.overlayId == "mayorDisplay") {
         MayorDisplay.setPositioningMode(false)
      }

      if (this.returnScreen != null) {
         this.mc.setScreen(this.returnScreen)
      } else {
         super.close()
      }
   }

   private fun openReturnScreen() {
      if (this.overlayId == "mayorDisplay") {
         MayorDisplay.setPositioningMode(false)
      }

      if (this.returnScreen != null) {
         this.mc.setScreen(this.returnScreen)
      } else {
         try {
            this.mc.setScreen(JooonConfigManager.getScreen(null, "jooonreimagined"))
         } catch (var2: Exception) {
            var2.printStackTrace()
            super.close()
         }
      }
   }

   private fun isPointInOverlay(px: Int, py: Int): Boolean {

      return px >= this.overlayX
         && px <= this.overlayX + (var3.component1() as java.lang.Number).intValue()
         && py >= this.overlayY
         && py <= this.overlayY + (var3.component2() as java.lang.Number).intValue()
      }

   private fun currentOverlayBounds(): Pair<Int, Int> {
      if (this.overlayId == "mayorDisplay") {
         return Pair(this.textRenderer.getWidth(MayorDisplay.getCurrentMayorText() as StringVisitable), this.textRenderer.fontHeight)
      } else {

         return if (overlay != null) Pair(overlay.width, overlay.height) else Pair(this.overlayWidth, this.overlayHeight)
      }
   }
}
