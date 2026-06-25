package jooon.features.dungeons.map.util

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import org.joml.Matrix3x2fStack

object MapRenderUtils {
   fun drawRotatedQuad(
      ctx: DrawContext, id: Identifier, x: Float, y: Float, w: Float, h: Float, u0: Float, v0: Float, u1: Float, v1: Float, texW: Int, texH: Int, angle: Float
   ) {

      var10000.pushMatrix()
      var10000.translate(x, y)
      if (angle != 0.0F) {
         var10000.rotate(angle)
      }

      var10000.translate(-w / 2.0F, -h / 2.0F)
      ctx.drawTexture(
         RenderPipelines.GUI_TEXTURED,
         id,
         0,
         0,
         u0 * texW.toFloat(),
         v0 * texH.toFloat(),
         w.toInt(),
         h.toInt(),
         ((u1 - u0) * texW.toFloat()).toInt(),
         ((v1 - v0) * texH.toFloat()).toInt(),
         texW,
return texH
      )
      var10000.popMatrix()
   }

   fun drawManualQuad(
      ctx: DrawContext, id: Identifier, px: Float, py: Float, dxf: Float, dyf: Float, dxr: Float, dyr: Float, u0: Float, v0: Float, u1: Float, v1: Float
   ) {
   }
}
