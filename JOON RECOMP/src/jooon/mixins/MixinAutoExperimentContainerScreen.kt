package jooon.mixins

import jooon.config.Config
import jooon.config.ConfigFlush
import jooon.features.autoexperiments.SolverManager
import net.minecraft.class_357
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

@Mixin([HandledScreen::class])
public abstract class MixinAutoExperimentContainerScreen : Screen {
   open fun MixinAutoExperimentContainerScreen(title: Text) {
      super(title)
   }

   @Inject(method = ["method_25426"], at = [@At("TAIL")])
   private fun onInit(ci: CallbackInfo) {
      if (this.field_22785 != null) {
         val var10000: java.lang.String = this.field_22785.getString()
         if (var10000 != null) {
            if (SolverManager.INSTANCE.isExperimentScreen(var10000)) {
               val buttonX: Int = this.field_22789 / 2 + 95
               val buttonY: Int = this.field_22790 / 2 - 80
               val var12: MutableText = Text.method_43470(if (Config.autoExperiments) "Auto Experiments: §aEnabled" else "Auto Experiments: §cDisabled")
               val var13: ButtonWidget = ButtonWidget.method_46430(var12 as Text, { button: ButtonWidget ->
                  Config.autoExperiments = !Config.autoExperiments
                  button.method_25355(Text.method_43470(if (Config.autoExperiments) "Auto Experiments: §aEnabled" else "Auto Experiments: §cDisabled") as Text)
                  ConfigFlush.INSTANCE.flush()
               }).method_46434(buttonX, buttonY, 150, 20).method_46431()
               val tickSlider: <unrepresentable> = object : class_357 {
                  fun method_25346() {
                     this.field_22754 = Text.method_43470("Tick Delay: ${(int)((double)50 * this.field_22753)}") as Text
                  }

                  fun method_25344() {
                     Config.autoExperimentsTickDelay = (int)(50 * this.field_22753)
                     if (Config.autoExperimentsTickDelay < 1) {
                        Config.autoExperimentsTickDelay = 1
                     }

                     ConfigFlush.INSTANCE.flush()
                  }
               }
               tickSlider.method_25355(Text.method_43470("Tick Delay: ${Config.autoExperimentsTickDelay}") as Text)
               this.method_37063(var13 as Element)
               this.method_37063(tickSlider as Element)
            }

            return
         }
      }
   }
}
