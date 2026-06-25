package jooon.features.farming

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private class AutoVisitorPlatePreviewScreen : Screen {
   private Screen parentScreen;
   private val padX: Double
   private val padY: Double
   private val padZ: Double
   private Perspective previousCameraType;
   private var previousYaw: Float
   private var previousPitch: Float
   private var previousHeadYaw: Float
   private var previousBodyYaw: Float
   private var previousYawOld: Float
   private var previousPitchOld: Float
   private var previousHeadYawOld: Float
   private var previousBodyYawOld: Float
   private var previewActive: Boolean

   fun AutoVisitorPlatePreviewScreen(parentScreen: Screen?, padX: Double, padY: Double, padZ: Double) {
      super(Text.literal("Auto Visitor Plate Confirmation") as Text)
      this.parentScreen = parentScreen
      this.padX = padX
      this.padY = padY
      this.padZ = padZ
   }

   fun method_25426() {
      super.init()
      this.startLiveTopDownPreview()
      this.addDrawableChild(ButtonWidget.builder(Text.literal("< No, go back") as Text, { it: ButtonWidget ->
         `this$0`.stopLiveTopDownPreview()
         AutoVisitor.clearPadOnly()
         if (`this$0`.client != null) {
            `this$0`.client.setScreen(AutoVisitorPadScreen(`this$0`.parentScreen, true))
         }
      }).position(8, 8).size(112, 20).build() as Element)
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Yes, I'm happy >") as Text, { it: ButtonWidget ->
         `this$0`.stopLiveTopDownPreview()
         if (`this$0`.client != null) {
            `this$0`.client.setScreen(AutoVisitorRulesScreen(`this$0`.parentScreen, { 
               AutoVisitorPadScreen(`this$0`.parentScreen, true) as Screen
            }, true))
         }
      }).position(this.width - 122, 8).size(114, 20).build() as Element)
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.applyLiveTopDownPreview()

         .formatted(Formatting.GREEN)
         .append(Text.literal("${this.padX.toInt()}, ${this.padY.toInt()}, ${this.padZ.toInt()}").formatted(Formatting.WHITE) as Text)



      context.drawText(
         this.textRenderer, var10000 as Text, this.width / 2 - this.textRenderer.getWidth(var10000 as StringVisitable) / 2, textYBase, -1, false
      )
      context.drawText(
         this.textRenderer, var9 as Text, this.width / 2 - this.textRenderer.getWidth(var9 as StringVisitable) / 2, textYBase + 14, -1, false
      )
      context.drawText(
         this.textRenderer, var10 as Text, this.width / 2 - this.textRenderer.getWidth(var10 as StringVisitable) / 2, textYBase + 28, -1, false
      )
      super.render(context, mouseX, mouseY, delta)
   }

   fun method_25419() {
      this.stopLiveTopDownPreview()
      AutoVisitor.cancelSetup(this.parentScreen)
   }

   fun method_25432() {
      this.stopLiveTopDownPreview()
      super.removed()
   }

   fun method_25421(): Boolean {
return false
   }

   private fun startLiveTopDownPreview() {
      if (this.client != null) {
         if (this.client.player != null) {

            if (!this.previewActive) {
               this.previousCameraType = this.client.options.getPerspective()
               this.previousYaw = player.getYaw()
               this.previousPitch = player.getPitch()
               this.previousHeadYaw = player.headYaw
               this.previousBodyYaw = player.bodyYaw
               this.previousYawOld = player.lastYaw
               this.previousPitchOld = player.lastPitch
               this.previousHeadYawOld = player.lastHeadYaw
               this.previousBodyYawOld = player.lastBodyYaw
               AutoVisitor.beginPreviewRenderSuppression()
               this.previewActive = true
            }
         }
      }
   }

   private fun applyLiveTopDownPreview() {
      if (this.client != null) {

         if (this.client.player != null) {

            if (!this.previewActive) {
               this.startLiveTopDownPreview()
            }

            this.client.options.setPerspective(Perspective.THIRD_PERSON_BACK)
            mc.setCameraEntity(player as Entity)
            player.setYaw(90.0F)
            player.setPitch(89.0F)
            player.headYaw = 90.0F
            player.bodyYaw = 90.0F
            player.lastYaw = 90.0F
            player.lastPitch = 89.0F
            player.lastHeadYaw = 90.0F
            player.lastBodyYaw = 90.0F
         }
      }
   }

   private fun stopLiveTopDownPreview() {
      if (this.client != null) {

         if (this.client.player != null) {

            if (this.previewActive) {
               AutoVisitor.endPreviewRenderSuppression()
               if (this.previousCameraType != null) {
                  mc.options.setPerspective(this.previousCameraType)
               }

               mc.setCameraEntity(player as Entity)
               player.setYaw(this.previousYaw)
               player.setPitch(this.previousPitch)
               player.headYaw = this.previousHeadYaw
               player.bodyYaw = this.previousBodyYaw
               player.lastYaw = this.previousYawOld
               player.lastPitch = this.previousPitchOld
               player.lastHeadYaw = this.previousHeadYawOld
               player.lastBodyYaw = this.previousBodyYawOld
               this.previewActive = false
            }
         }
      }
   }
}
