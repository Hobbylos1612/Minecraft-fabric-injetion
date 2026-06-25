package jooon.features.farming

import java.util.ArrayList
import jooon.JooonReimagined
import jooon.config.PersistentState
import kotlin.jvm.internal.Intrinsics
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper

private class AutoVisitorPadScreen : Screen {
   private Screen parentScreen;
   private val firstTimeMode: Boolean
   private ButtonWidget plateButton;
   private ButtonWidget nextButton;
   private val placeTooltip: String

   fun AutoVisitorPadScreen(parentScreen: Screen?, firstTimeMode: Boolean) {
      super(Text.literal("Auto Visitor Setup") as Text)
      this.parentScreen = parentScreen
      this.firstTimeMode = firstTimeMode
      this.placeTooltip = "Click this button to place a plate at your feet. When you enter this pad, Jooon will automatically look for visitors and accept them. Don't worry - this pad can be configured again at any time."
   }

   fun method_25426() {
      super.init()


      this.plateButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(this.mainButtonLabel()) as Text, { it: ButtonWidget ->
         `this$0`.handlePrimary()
      }).position(centerX - 140, centerY + 28).size(280, 24).build() as Element) as ButtonWidget
      if (!this.firstTimeMode) {
         this.nextButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Next >") as Text, { it: ButtonWidget ->
            if (`this$0`.client != null) {
               `this$0`.client.setScreen(AutoVisitorRulesScreen(`this$0`.parentScreen, { 
                  AutoVisitorPadScreen(`this$0`.parentScreen, false) as Screen
               }, false))
            }
         }).position(this.width - 92, 8).size(84, 20).build() as Element) as ButtonWidget
      }
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      context.fill(0, 0, this.width, this.height, -804253160)
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.renderBackground(context, mouseX, mouseY, delta)

         Text.literal("Jooon Auto Visitor First-time setup").formatted(Formatting.GREEN)
return else
         Text.literal("Jooon Auto Visitor Config").formatted(Formatting.GREEN)
         context.drawText(
         this.textRenderer, padText as Text, this.width / 2 - this.textRenderer.getWidth(padText as StringVisitable) / 2, 34, -1, false
      )
      if (this.firstTimeMode) {


            .formatted(arrayOf(Formatting.GRAY, Formatting.ITALIC))
            context.drawText(
            this.textRenderer, var18 as Text, this.width / 2 - this.textRenderer.getWidth(var18 as StringVisitable) / 2, 54, -1, false
         )
         context.drawText(
            this.textRenderer, var20 as Text, this.width / 2 - this.textRenderer.getWidth(var20 as StringVisitable) / 2, 68, -1, false
         )
      } else {

               "If you'd like to move your placed pad, remove the current one, then place a new one. This won't reset your configuration."
            )
            .formatted(arrayOf(Formatting.GRAY, Formatting.ITALIC))
            val var23: java.util.List = this.textRenderer.wrapLines(var22 as StringVisitable, MathHelper.clamp(this.width - 96, 220, 620))
         var var16: Int = 54

         for (var24 in var23) {
            context.drawText(
               this.textRenderer, var24 as OrderedText, this.width / 2 - this.textRenderer.getWidth(var24 as OrderedText) / 2, var16, -1, false
            )
            var16 += this.textRenderer.fontHeight + 1
         }
      }

      if (PersistentState.autoVisitorPadPlaced) {

               "Current Pad: ${PersistentState.autoVisitorPadX.toInt()}, ${PersistentState.autoVisitorPadY.toInt()}, ${PersistentState.autoVisitorPadZ.toInt()}"
            )
            .formatted(Formatting.GREEN)
            context.drawText(
            this.textRenderer,
            var25 as Text,
            this.width / 2 - this.textRenderer.getWidth(var25 as StringVisitable) / 2,
            this.height / 2 - 28,
            -1,
return false
         )
         AutoVisitorKt.access$drawTopDownPadScene(context, this.width / 2, this.height / 2 - 86, 180, 128, false)
      }

      this.drawPlacementWarnings(context)
      super.render(context, mouseX, mouseY, delta)
      var var26: ButtonWidget = this.plateButton
      if (this.plateButton == null) {
         throwUninitializedPropertyAccessException("plateButton")
         var26 = null
      }

      if (var26.isSelected()) {
         var26 = this.plateButton
         if (this.plateButton == null) {
            throwUninitializedPropertyAccessException("plateButton")
            var26 = null
         }

         if (var28.contains("Place", true)) {
            AutoVisitorKt.access$renderTooltipBox(context, listOf(Text.literal(this.placeTooltip)), mouseX, mouseY)
         }
      }
   }

   private fun handlePrimary() {
      if (this.firstTimeMode) {
         if (AutoVisitor.placePlateAtPlayerFeet()) {
            if (this.client != null) {
               this.client
                  .setScreen(
                     AutoVisitorPlatePreviewScreen(
                        this.parentScreen, PersistentState.autoVisitorPadX, PersistentState.autoVisitorPadY, PersistentState.autoVisitorPadZ
                     )
                  )
               }
         }
      } else {
         if (PersistentState.autoVisitorPadPlaced) {
            AutoVisitor.clearPadOnly()
            JooonReimagined.Companion.sendMessage("§ePlate removed.")
         } else {
            AutoVisitor.placePlateAtPlayerFeet()
         }

         var var10000: ButtonWidget = this.plateButton
         if (this.plateButton == null) {
            throwUninitializedPropertyAccessException("plateButton")
            var10000 = null
         }

         var10000.setMessage(Text.literal(this.mainButtonLabel()) as Text)
      }
   }

   private fun mainButtonLabel(): String {
      return if (this.firstTimeMode)
         "Place down Auto Visitor Plate"
return else
         (if (PersistentState.autoVisitorPadPlaced) "Remove current Auto Visitor Plate" else "Place down Auto Visitor Plate")
      }

   fun drawPlacementWarnings(context: DrawContext) {
      if (this.client != null && this.client.player != null) {

         val warnings: java.util.List = ArrayList()
         if (!AutoVisitor.isInGarden()) {
            warnings.add(Text.literal("Warning: You must be in The Garden to place the plate.").formatted(Formatting.RED))
         }

         if (player.getAbilities().flying || !player.isOnGround()) {
            warnings.add(Text.literal("Warning: You must be on the ground to place the plate.").formatted(Formatting.RED))
         }

         if (!warnings.isEmpty()) {
            var y: Int = this.height / 2 + 58

            for (var10000 in warnings) {
               context.drawText(
                  this.textRenderer,
                  var10000 as Text,
                  this.width / 2 - this.textRenderer.getWidth((var10000 as Text) as StringVisitable) / 2,
                  y,
                  -1,
return false
               )
               y += this.textRenderer.fontHeight + 2
            }
         }
      }
   }

   fun method_25419() {
      if (this.firstTimeMode) {
         AutoVisitor.cancelSetup(this.parentScreen)
      } else if (this.client != null) {
         this.client.setScreen(this.parentScreen)
      }
   }

   fun method_25421(): Boolean {
return false
   }
}
