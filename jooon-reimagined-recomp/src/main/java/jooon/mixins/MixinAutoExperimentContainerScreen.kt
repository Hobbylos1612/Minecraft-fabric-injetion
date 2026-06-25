package jooon.mixins

import jooon.config.Config
import jooon.config.ConfigFlush
import jooon.features.autoexperiments.SolverManager
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(HandledScreen::class)
abstract class MixinAutoExperimentContainerScreen : Screen {
   constructor(title: Text) {
      super(title)
   }

   @Inject(method = ["method_25426"], at = [@At("TAIL")])
   private fun onInit(ci: CallbackInfo) {
      if (this.title != null) {

         if (var10000 != null) {
            if (SolverManager.isExperimentScreen(var10000)) {




                  Config.autoExperiments = !Config.autoExperiments
                  button.setMessage(Text.literal(if (Config.autoExperiments) "Auto Experiments: §aEnabled" else "Auto Experiments: §cDisabled") as Text)
                  ConfigFlush.flush()
               }).dimensions(buttonX, buttonY, 150, 20).build()
               val tickSlider: <unrepresentable> = object : SliderWidget {
                  fun method_25346() {
                     this.message = Text.literal("Tick Delay: ${(50.toDouble() * this.value).toInt()}") as Text
                  }

                  fun method_25344() {
                     Config.autoExperimentsTickDelay = (50 * this.value).toInt()
                     if (Config.autoExperimentsTickDelay < 1) {
                        Config.autoExperimentsTickDelay = 1
                     }

                     ConfigFlush.flush()
                  }
               }
               tickSlider.setMessage(Text.literal("Tick Delay: ${Config.autoExperimentsTickDelay}") as Text)
               this.addDrawableChild(var13 as Element)
               this.addDrawableChild(tickSlider as Element)
            }
return return
         }
      }
   }
}
