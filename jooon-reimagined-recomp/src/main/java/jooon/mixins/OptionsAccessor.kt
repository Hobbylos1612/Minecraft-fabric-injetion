package jooon.mixins

import net.minecraft.client.option.AttackIndicator
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.SimpleOption
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(GameOptions::class)
interface OptionsAccessor {
   @Accessor("field_1895")
   fun getAttackIndicator(): SimpleOption<AttackIndicator>

   @Accessor("field_1837")
   abstract fun jooonGetPauseOnLostFocus(): Boolean {
   }

   @Accessor("field_1837")
   abstract fun jooonSetPauseOnLostFocus(value: Boolean) {
   }
}
