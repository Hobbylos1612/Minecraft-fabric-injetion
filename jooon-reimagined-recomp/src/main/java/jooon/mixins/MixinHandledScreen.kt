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

@Mixin(HandledScreen::class)
abstract class MixinHandledScreen : Screen {
   constructor(title: Text) {
      super(title)
   }

   @Inject(method = ["method_25426"], at = [@At("TAIL")])
   private fun jooonInit(ci: CallbackInfo) {
      if (Config.factoryHelperEnabled && FactoryHelper.inFactory) {



            FactoryHelper.autoUpgradeEnabled = !FactoryHelper.autoUpgradeEnabled
            button.setMessage(`this$0`.getAutoUpgradeText())
         }).dimensions(buttonX, buttonY, 150, 20).build()
         this.addDrawableChild(var10000 as Element)
      }
   }

   fun getAutoUpgradeText(): Text {

         "jooonreimagined.factoryhelper.button", arrayOf(if (FactoryHelper.autoUpgradeEnabled) "§aEnabled" else "§cDisabled")
      )
      var10000 as Text
   }

   @Inject(method = ["method_2385"], at = [@At("TAIL")])
   fun jooonDrawSlot(context: DrawContext, slot: Slot, i: Int, j: Int, ci: CallbackInfo) {
      if (FactoryHelper.shouldRenderSlot(slot)) {
         FactoryHelper.renderSlotOverlay(context, slot)
      }
   }

   @Inject(method = ["method_25394"], at = [@At("TAIL")])
   fun jooonRenderHarpTooltip(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float, ci: CallbackInfo) {

      var10000.renderHarpTooltip(context, this as HandledScreen)
   }
}
