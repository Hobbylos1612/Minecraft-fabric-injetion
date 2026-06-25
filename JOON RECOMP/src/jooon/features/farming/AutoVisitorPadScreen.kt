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
   private final val firstTimeMode: Boolean
   private ButtonWidget plateButton;
   private ButtonWidget nextButton;
   private final val placeTooltip: String

   fun AutoVisitorPadScreen(parentScreen: Screen?, firstTimeMode: Boolean) {
      super(Text.method_43470("Auto Visitor Setup") as Text)
      this.parentScreen = parentScreen
      this.firstTimeMode = firstTimeMode
      this.placeTooltip = "Click this button to place a plate at your feet. When you enter this pad, Jooon will automatically look for visitors and accept them. Don't worry - this pad can be configured again at any time."
   }

   fun method_25426() {
      super.method_25426()
      val centerX: Int = this.field_22789 / 2
      val centerY: Int = this.field_22790 / 2
      this.plateButton = this.method_37063(ButtonWidget.method_46430(Text.method_43470(this.mainButtonLabel()) as Text, { it: ButtonWidget ->
         `this$0`.handlePrimary()
      }).method_46433(centerX - 140, centerY + 28).method_46437(280, 24).method_46431() as Element) as ButtonWidget
      if (!this.firstTimeMode) {
         this.nextButton = this.method_37063(ButtonWidget.method_46430(Text.method_43470("Next >") as Text, { it: ButtonWidget ->
            if (`this$0`.field_22787 != null) {
               `this$0`.field_22787.method_1507(AutoVisitorRulesScreen(`this$0`.parentScreen, { 
                  AutoVisitorPadScreen(`this$0`.parentScreen, false) as Screen
               }, false))
            }
         }).method_46433(this.field_22789 - 92, 8).method_46437(84, 20).method_46431() as Element) as ButtonWidget
      }
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      context.method_25294(0, 0, this.field_22789, this.field_22790, -804253160)
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.method_25420(context, mouseX, mouseY, delta)
      val padText: MutableText = if (this.firstTimeMode)
         Text.method_43470("Jooon Auto Visitor First-time setup").method_27692(Formatting.field_1060)
         else
         Text.method_43470("Jooon Auto Visitor Config").method_27692(Formatting.field_1060)
         context.method_51439(
         this.field_22793, padText as Text, this.field_22789 / 2 - this.field_22793.method_27525(padText as StringVisitable) / 2, 34, -1, false
      )
      if (this.firstTimeMode) {
         val var18: MutableText = Text.method_43470("You're seeing this menu as you").method_27695(arrayOf(Formatting.field_1080, Formatting.field_1056))
         val var20: MutableText = Text.method_43470("have not configured Auto Visitor yet!")
            .method_27695(arrayOf(Formatting.field_1080, Formatting.field_1056))
            context.method_51439(
            this.field_22793, var18 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var18 as StringVisitable) / 2, 54, -1, false
         )
         context.method_51439(
            this.field_22793, var20 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var20 as StringVisitable) / 2, 68, -1, false
         )
      } else {
         val var22: MutableText = Text.method_43470(
               "If you'd like to move your placed pad, remove the current one, then place a new one. This won't reset your configuration."
            )
            .method_27695(arrayOf(Formatting.field_1080, Formatting.field_1056))
            val var23: java.util.List = this.field_22793.method_1728(var22 as StringVisitable, MathHelper.method_15340(this.field_22789 - 96, 220, 620))
         var var16: Int = 54

         for (var24 in var23) {
            context.method_51430(
               this.field_22793, var24 as OrderedText, this.field_22789 / 2 - this.field_22793.method_30880(var24 as OrderedText) / 2, var16, -1, false
            )
            var16 += this.field_22793.field_2000 + 1
         }
      }

      if (PersistentState.autoVisitorPadPlaced) {
         val var25: MutableText = Text.method_43470(
               "Current Pad: ${(int)PersistentState.autoVisitorPadX}, ${(int)PersistentState.autoVisitorPadY}, ${(int)PersistentState.autoVisitorPadZ}"
            )
            .method_27692(Formatting.field_1060)
            context.method_51439(
            this.field_22793,
            var25 as Text,
            this.field_22789 / 2 - this.field_22793.method_27525(var25 as StringVisitable) / 2,
            this.field_22790 / 2 - 28,
            -1,
            false
         )
         AutoVisitorKt.access$drawTopDownPadScene(context, this.field_22789 / 2, this.field_22790 / 2 - 86, 180, 128, false)
      }

      this.drawPlacementWarnings(context)
      super.method_25394(context, mouseX, mouseY, delta)
      var var26: ButtonWidget = this.plateButton
      if (this.plateButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("plateButton")
         var26 = null
      }

      if (var26.method_25367()) {
         var26 = this.plateButton
         if (this.plateButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("plateButton")
            var26 = null
         }

         val var28: java.lang.String = var26.method_25369().getString()
         if (StringsKt.contains(var28, "Place", true)) {
            AutoVisitorKt.access$renderTooltipBox(context, CollectionsKt.listOf(Text.method_43470(this.placeTooltip)), mouseX, mouseY)
         }
      }
   }

   private fun handlePrimary() {
      if (this.firstTimeMode) {
         if (AutoVisitor.INSTANCE.placePlateAtPlayerFeet()) {
            if (this.field_22787 != null) {
               this.field_22787
                  .method_1507(
                     AutoVisitorPlatePreviewScreen(
                        this.parentScreen, PersistentState.autoVisitorPadX, PersistentState.autoVisitorPadY, PersistentState.autoVisitorPadZ
                     )
                  )
               }
         }
      } else {
         if (PersistentState.autoVisitorPadPlaced) {
            AutoVisitor.INSTANCE.clearPadOnly()
            JooonReimagined.Companion.sendMessage("§ePlate removed.")
         } else {
            AutoVisitor.INSTANCE.placePlateAtPlayerFeet()
         }

         var var10000: ButtonWidget = this.plateButton
         if (this.plateButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("plateButton")
            var10000 = null
         }

         var10000.method_25355(Text.method_43470(this.mainButtonLabel()) as Text)
      }
   }

   private fun mainButtonLabel(): String {
      return if (this.firstTimeMode)
         "Place down Auto Visitor Plate"
         else
         (if (PersistentState.autoVisitorPadPlaced) "Remove current Auto Visitor Plate" else "Place down Auto Visitor Plate")
      }

   fun drawPlacementWarnings(context: DrawContext) {
      if (this.field_22787 != null && this.field_22787.field_1724 != null) {
         val player: ClientPlayerEntity = this.field_22787.field_1724
         val warnings: java.util.List = ArrayList()
         if (!AutoVisitor.INSTANCE.isInGarden()) {
            warnings.add(Text.method_43470("Warning: You must be in The Garden to place the plate.").method_27692(Formatting.field_1061))
         }

         if (player.method_31549().field_7479 || !player.method_24828()) {
            warnings.add(Text.method_43470("Warning: You must be on the ground to place the plate.").method_27692(Formatting.field_1061))
         }

         if (!warnings.isEmpty()) {
            var y: Int = this.field_22790 / 2 + 58

            for (var10000 in warnings) {
               context.method_51439(
                  this.field_22793,
                  var10000 as Text,
                  this.field_22789 / 2 - this.field_22793.method_27525((var10000 as Text) as StringVisitable) / 2,
                  y,
                  -1,
                  false
               )
               y += this.field_22793.field_2000 + 2
            }
         }
      }
   }

   fun method_25419() {
      if (this.firstTimeMode) {
         AutoVisitor.INSTANCE.cancelSetup(this.parentScreen)
      } else if (this.field_22787 != null) {
         this.field_22787.method_1507(this.parentScreen)
      }
   }

   fun method_25421(): Boolean {
      false
   }
}
