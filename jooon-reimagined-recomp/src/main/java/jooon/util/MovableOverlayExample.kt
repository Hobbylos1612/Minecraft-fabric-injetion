package jooon.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object MovableOverlayExample {
   fun createSimpleTextOverlay(id: String, defaultX: Int, defaultY: Int, text: String) {

      overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var4: Float ->

         context.drawText(var10000, `$text`, x, y, -1, true)
return Unit
      }
      overlay.register()
   }

   fun createComplexOverlay(id: String, defaultX: Int, defaultY: Int) {

      overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->

         context.fill(x - 2, y - 2, x + 202, y + 62, Integer.MIN_VALUE)
         context.drawStrokedRectangle(x - 2, y - 2, 204, 64, -1)

         context.drawText(var10000, var9 as Text, x, y, -1, true)



         context.drawText(var10000, var10 as Text, x, y + 12, -1, true)
         context.drawText(var10000, var11 as Text, x, y + 24, -1, true)
         context.drawText(var10000, var12 as Text, x, y + 36, -1, true)
return Unit
      }
      overlay.onPositionChanged = { newX: Int, newY: Int ->
return Unit
      }
      overlay.register()
   }

   fun createDataOverlay(id: String, defaultX: Int, defaultY: Int, dataProvider: () -> String) {

      overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var4: Float ->


         context.drawText(var10000, var8 as Text, x, y, -1, true)
return Unit
      }
      overlay.register()
   }

   class ExampleFeature {
      private var overlay: MovableOverlay?

      fun initialize() {
         this.overlay = MovableOverlayManager.createOverlay("exampleFeature", "Example Feature", 10, 10, 100, 20)
         if (this.overlay != null) {
            this.overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var4: Float ->
               `this$0`.renderFeature(context, x, y)
return Unit
            }
         }

         if (this.overlay != null) {
            this.overlay.register()
         }
      }

      fun renderFeature(context: DrawContext, x: Int, y: Int) {


         context.drawText(var10000, var6 as Text, x, y, -1, true)
      }

      fun cleanup() {
         if (this.overlay != null) {
            this.overlay.unregister()
         }

         MovableOverlayManager.removeOverlay("exampleFeature")
      }
   }
}
