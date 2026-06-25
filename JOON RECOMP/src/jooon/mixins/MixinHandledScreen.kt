package jooon.mixins

import jooon.config.Config
import jooon.features.other.FactoryHelper
import jooon.features.other.Melody
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.screen.slot.Slot
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin([HandledScreen::class])
public abstract class MixinHandledScreen : Screen {
   open fun MixinHandledScreen(title: Text) {
      super(title)
   }

   @Inject(method = ["method_25426"], at = [@At("TAIL")])
   private fun jooonInit(ci: CallbackInfo) {
      if (Config.factoryHelperEnabled && FactoryHelper.INSTANCE.inFactory) {
         val buttonX: Int = this.field_22789 / 2 + 95
         val buttonY: Int = this.field_22790 / 2 - 80
         val var10000: ButtonWidget = ButtonWidget.method_46430(this.getAutoUpgradeText(), { button: ButtonWidget ->
            FactoryHelper.INSTANCE.autoUpgradeEnabled = !FactoryHelper.INSTANCE.autoUpgradeEnabled
            button.method_25355(`this$0`.getAutoUpgradeText())
         }).method_46434(buttonX, buttonY, 150, 20).method_46431()
         this.method_37063(var10000 as Element)
      }
   }

   fun getAutoUpgradeText(): Text {
      val var10000: MutableText = Text.method_43469(
         "jooonreimagined.factoryhelper.button", arrayOf(if (FactoryHelper.INSTANCE.autoUpgradeEnabled) "§aEnabled" else "§cDisabled")
      )
      var10000 as Text
   }

   @Inject(method = ["method_2385"], at = [@At("TAIL")])
   fun jooonDrawSlot(context: DrawContext, slot: Slot, i: Int, j: Int, ci: CallbackInfo) {
      if (FactoryHelper.INSTANCE.shouldRenderSlot(slot)) {
         FactoryHelper.INSTANCE.renderSlotOverlay(context, slot)
      }
   }

   @Inject(method = ["method_25394"], at = [@At("TAIL")])
   fun jooonRenderHarpTooltip(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float, ci: CallbackInfo) {
      val var10000: Melody = Melody.INSTANCE
      var10000.renderHarpTooltip(context, this as HandledScreen<*>)
   }
}
