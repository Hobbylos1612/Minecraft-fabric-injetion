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
   private final val padX: Double
   private final val padY: Double
   private final val padZ: Double
   private Perspective previousCameraType;
   private final var previousYaw: Float
   private final var previousPitch: Float
   private final var previousHeadYaw: Float
   private final var previousBodyYaw: Float
   private final var previousYawOld: Float
   private final var previousPitchOld: Float
   private final var previousHeadYawOld: Float
   private final var previousBodyYawOld: Float
   private final var previewActive: Boolean

   fun AutoVisitorPlatePreviewScreen(parentScreen: Screen?, padX: Double, padY: Double, padZ: Double) {
      super(Text.method_43470("Auto Visitor Plate Confirmation") as Text)
      this.parentScreen = parentScreen
      this.padX = padX
      this.padY = padY
      this.padZ = padZ
   }

   fun method_25426() {
      super.method_25426()
      this.startLiveTopDownPreview()
      this.method_37063(ButtonWidget.method_46430(Text.method_43470("< No, go back") as Text, { it: ButtonWidget ->
         `this$0`.stopLiveTopDownPreview()
         AutoVisitor.INSTANCE.clearPadOnly()
         if (`this$0`.field_22787 != null) {
            `this$0`.field_22787.method_1507(AutoVisitorPadScreen(`this$0`.parentScreen, true))
         }
      }).method_46433(8, 8).method_46437(112, 20).method_46431() as Element)
      this.method_37063(ButtonWidget.method_46430(Text.method_43470("Yes, I'm happy >") as Text, { it: ButtonWidget ->
         `this$0`.stopLiveTopDownPreview()
         if (`this$0`.field_22787 != null) {
            `this$0`.field_22787.method_1507(AutoVisitorRulesScreen(`this$0`.parentScreen, { 
               AutoVisitorPadScreen(`this$0`.parentScreen, true) as Screen
            }, true))
         }
      }).method_46433(this.field_22789 - 122, 8).method_46437(114, 20).method_46431() as Element)
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.applyLiveTopDownPreview()
      val var10000: MutableText = Text.method_43470("You placed the Visitor Plate at ")
         .method_27692(Formatting.field_1060)
         .method_10852(Text.method_43470("${(int)this.padX}, ${(int)this.padY}, ${(int)this.padZ}").method_27692(Formatting.field_1068) as Text)
         val var9: MutableText = Text.method_43470("To trigger Auto Visitor, you can simply enter this pad.").method_27692(Formatting.field_1060)
      val var10: MutableText = Text.method_43470("You can continue now, or change it using the buttons above.").method_27692(Formatting.field_1060)
      val textYBase: Int = this.field_22790 / 2 - (this.field_22793.field_2000 + 7)
      context.method_51439(
         this.field_22793, var10000 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var10000 as StringVisitable) / 2, textYBase, -1, false
      )
      context.method_51439(
         this.field_22793, var9 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var9 as StringVisitable) / 2, textYBase + 14, -1, false
      )
      context.method_51439(
         this.field_22793, var10 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var10 as StringVisitable) / 2, textYBase + 28, -1, false
      )
      super.method_25394(context, mouseX, mouseY, delta)
   }

   fun method_25419() {
      this.stopLiveTopDownPreview()
      AutoVisitor.INSTANCE.cancelSetup(this.parentScreen)
   }

   fun method_25432() {
      this.stopLiveTopDownPreview()
      super.method_25432()
   }

   fun method_25421(): Boolean {
      false
   }

   private fun startLiveTopDownPreview() {
      if (this.field_22787 != null) {
         if (this.field_22787.field_1724 != null) {
            val player: ClientPlayerEntity = this.field_22787.field_1724
            if (!this.previewActive) {
               this.previousCameraType = this.field_22787.field_1690.method_31044()
               this.previousYaw = player.method_36454()
               this.previousPitch = player.method_36455()
               this.previousHeadYaw = player.field_6241
               this.previousBodyYaw = player.field_6283
               this.previousYawOld = player.field_5982
               this.previousPitchOld = player.field_6004
               this.previousHeadYawOld = player.field_6259
               this.previousBodyYawOld = player.field_6220
               AutoVisitor.INSTANCE.beginPreviewRenderSuppression()
               this.previewActive = true
            }
         }
      }
   }

   private fun applyLiveTopDownPreview() {
      if (this.field_22787 != null) {
         val mc: MinecraftClient = this.field_22787
         if (this.field_22787.field_1724 != null) {
            val player: ClientPlayerEntity = this.field_22787.field_1724
            if (!this.previewActive) {
               this.startLiveTopDownPreview()
            }

            this.field_22787.field_1690.method_31043(Perspective.field_26665)
            mc.method_1504(player as Entity)
            player.method_36456(90.0F)
            player.method_36457(89.0F)
            player.field_6241 = 90.0F
            player.field_6283 = 90.0F
            player.field_5982 = 90.0F
            player.field_6004 = 89.0F
            player.field_6259 = 90.0F
            player.field_6220 = 90.0F
         }
      }
   }

   private fun stopLiveTopDownPreview() {
      if (this.field_22787 != null) {
         val mc: MinecraftClient = this.field_22787
         if (this.field_22787.field_1724 != null) {
            val player: ClientPlayerEntity = this.field_22787.field_1724
            if (this.previewActive) {
               AutoVisitor.INSTANCE.endPreviewRenderSuppression()
               if (this.previousCameraType != null) {
                  mc.field_1690.method_31043(this.previousCameraType)
               }

               mc.method_1504(player as Entity)
               player.method_36456(this.previousYaw)
               player.method_36457(this.previousPitch)
               player.field_6241 = this.previousHeadYaw
               player.field_6283 = this.previousBodyYaw
               player.field_5982 = this.previousYawOld
               player.field_6004 = this.previousPitchOld
               player.field_6259 = this.previousHeadYawOld
               player.field_6220 = this.previousBodyYawOld
               this.previewActive = false
            }
         }
      }
   }
}
