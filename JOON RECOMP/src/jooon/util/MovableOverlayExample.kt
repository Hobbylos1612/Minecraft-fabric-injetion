package jooon.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

public object MovableOverlayExample {
   public fun createSimpleTextOverlay(id: String, defaultX: Int, defaultY: Int, text: String) {
      val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.createOverlay(id, "Simple Text Overlay", defaultX, defaultY, 100, 20)
      overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var4: Float ->
         val var10000: TextRenderer = MinecraftClient.method_1551().field_1772
         context.method_51433(var10000, `$text`, x, y, -1, true)
         Unit.INSTANCE
      }
      overlay.register()
   }

   public fun createComplexOverlay(id: String, defaultX: Int, defaultY: Int) {
      val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.createOverlay(id, "Complex Overlay", defaultX, defaultY, 200, 60)
      overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
         val var10000: TextRenderer = MinecraftClient.method_1551().field_1772
         context.method_25294(x - 2, y - 2, x + 202, y + 62, Integer.MIN_VALUE)
         context.method_73198(x - 2, y - 2, 204, 64, -1)
         val var9: MutableText = Text.method_43470("Example Overlay").method_27692(Formatting.field_1065)
         context.method_51439(var10000, var9 as Text, x, y, -1, true)
         val var10: MutableText = Text.method_43470("Line 1: Some information").method_27692(Formatting.field_1068)
         val var11: MutableText = Text.method_43470("Line 2: More information").method_27692(Formatting.field_1080)
         val var12: MutableText = Text.method_43470("Line 3: Even more info").method_27692(Formatting.field_1054)
         context.method_51439(var10000, var10 as Text, x, y + 12, -1, true)
         context.method_51439(var10000, var11 as Text, x, y + 24, -1, true)
         context.method_51439(var10000, var12 as Text, x, y + 36, -1, true)
         Unit.INSTANCE
      }
      overlay.onPositionChanged = { newX: Int, newY: Int ->
         Unit.INSTANCE
      }
      overlay.register()
   }

   public fun createDataOverlay(id: String, defaultX: Int, defaultY: Int, dataProvider: () -> String) {
      val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.createOverlay(id, "Data Overlay", defaultX, defaultY, 150, 20)
      overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var4: Float ->
         val var10000: TextRenderer = MinecraftClient.method_1551().field_1772
         val var8: MutableText = Text.method_43470("Data: ${`$dataProvider`() as java.lang.String}").method_27692(Formatting.field_1060)
         context.method_51439(var10000, var8 as Text, x, y, -1, true)
         Unit.INSTANCE
      }
      overlay.register()
   }

   public class ExampleFeature {
      private final var overlay: MovableOverlay?

      public fun initialize() {
         this.overlay = MovableOverlayManager.INSTANCE.createOverlay("exampleFeature", "Example Feature", 10, 10, 100, 20)
         if (this.overlay != null) {
            this.overlay.renderFunction = { context: DrawContext, x: Int, y: Int, var4: Float ->
               `this$0`.renderFeature(context, x, y)
               Unit.INSTANCE
            }
         }

         if (this.overlay != null) {
            this.overlay.register()
         }
      }

      fun renderFeature(context: DrawContext, x: Int, y: Int) {
         val var10000: TextRenderer = MinecraftClient.method_1551().field_1772
         val var6: MutableText = Text.method_43470("Example Feature Active").method_27692(Formatting.field_1075)
         context.method_51439(var10000, var6 as Text, x, y, -1, true)
      }

      public fun cleanup() {
         if (this.overlay != null) {
            this.overlay.unregister()
         }

         MovableOverlayManager.INSTANCE.removeOverlay("exampleFeature")
      }
   }
}
