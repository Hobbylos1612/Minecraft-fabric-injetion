package jooon.mixins

import jooon.features.farming.AutoVisitor
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.entity.Entity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(EntityRenderer::class)
class EntityRendererMixin {
   @Inject(method = ["method_3933"], at = [@At("HEAD")], cancellable = true)
   fun jooonHidePreviewPlayer(entity: Entity, frustum: Frustum, x: Double, y: Double, z: Double, cir: CallbackInfoReturnable<Boolean>) {
      if (AutoVisitor.shouldSuppressPreviewPlayerRender(entity)) {
         cir.setReturnValue(false)
      }
   }
}
