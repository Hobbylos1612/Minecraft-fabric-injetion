package jooon.mixins

import jooon.util.BowDrawResetFixes
import net.minecraft.client.render.item.HeldItemRenderer
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin([HeldItemRenderer::class])
public class ItemInHandRendererMixin {
   @Inject(method = ["method_65910"], at = [@At("RETURN")], cancellable = true)
   fun jooonPreserveEquivalentBowDraw(from: ItemStack, to: ItemStack, cir: CallbackInfoReturnable<java.lang.Boolean>) {
      if (!cir.getReturnValueZ() && BowDrawResetFixes.shouldPreserveBowDraw(from, to)) {
         cir.setReturnValue(true)
      }
   }
}
