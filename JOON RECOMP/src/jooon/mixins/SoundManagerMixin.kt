package jooon.mixins

import jooon.features.other.WitherShieldOverlay
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.sound.SoundEvents
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin([SoundManager::class])
public abstract class SoundManagerMixin {
   @Inject(method = ["method_4873"], at = [@At("HEAD")])
   fun onPlaySound(soundInstance: SoundInstance, ci: CallbackInfoReturnable<*>) {
      if (soundInstance.method_4776() == SoundEvents.field_15152.comp_349() && soundInstance.method_4781() == 1.0F && soundInstance.method_4782() == 1.0F) {
         WitherShieldOverlay.INSTANCE.trigger()
      }
   }
}
