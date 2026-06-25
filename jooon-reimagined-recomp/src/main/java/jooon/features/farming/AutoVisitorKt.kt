@file:SourceDebugExtension(["SMAP\nAutoVisitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoVisitor.kt\njooon/features/farming/AutoVisitorKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1010:1\n1#2:1011\n*E\n"])

package jooon.features.farming

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

fun drawTopDownPadScene(context: DrawContext, centerX: Int, centerY: Int, sceneWidth: Int, sceneHeight: Int, drawCrosshair: Boolean) {






   context.fill(x0, y0, x1, centerY + halfH, -13820654)
   context.drawStrokedRectangle(x0, y0, sceneWidth, sceneHeight, -8760010)




   context.fill(ix0, iy0, ix1, y1 - 20, -9737102)

   var ty: Int = iy0
   var odd: Boolean = false

   while (ty < iy1) {
      var padOuter: Int = ix0
      odd = !odd

      while (padOuter < ix1) {
         context.fill(
            padOuter, ty, (padOuter + tile).coerceAtMost(ix1), (ty + tile).coerceAtMost(iy1), if (odd) -8486776 else -10526360
         )
         odd = !odd
         padOuter += tile
      }

      ty += tile
   }

   context.fill(ix0, centerY - 8, ix1, centerY + 8, -868603363)
   context.fill(centerX - 8, iy0, centerX + 8, iy1, -868603363)
   context.fill(centerX - 22, centerY - 22, centerX + 22, centerY + 22, -1439771014)
   context.drawStrokedRectangle(centerX - 22, centerY - 22, 22 * 2, 22 * 2, -7733312)
   context.fill(centerX - 12, centerY - 12, centerX + 12, centerY + 12, -1437077087)
   context.drawStrokedRectangle(centerX - 12, centerY - 12, 12 * 2, 12 * 2, -1900562)
   if (drawCrosshair) {
      context.fill(centerX - 1, centerY - 10, centerX + 1, centerY + 10, -1712980496)
      context.fill(centerX - 10, centerY - 1, centerX + 10, centerY + 1, -1712980496)
   }
}

fun renderTooltipBox(context: DrawContext, linesIn: MutableList<Text>, mouseX: Int, mouseY: Int) {



   val boxWidth: java.util.List = createListBuilder()
   val boxHeight: java.util.List = boxWidth

   for (drawY in linesIn) {
      val var10001: java.util.List = font.wrapLines(drawY as StringVisitable, 320)
      boxHeight.addAll(var10001)
   }

   val expanded: java.util.List = build(boxWidth)
   val var17: java.util.Iterator = expanded.iterator()
   val var27: java.lang.Comparable
   if (!var17.hasNext()) {
      var27 = null
   } else {
      var var20: java.lang.Comparable = font.getWidth(var17.next() as OrderedText)

      while (var17.hasNext()) {
         val var23: java.lang.Comparable = font.getWidth(var17.next() as OrderedText)
         if (var20.compareTo(var23) < 0) {
            var20 = var23
         }
      }

      var27 = var20
   }




   context.fill(var16, var18, var16 + var14, var18 + var15, -300870113)
   context.drawStrokedRectangle(var16, var18, var14, var15, -7362624)
   var var21: Int = var18 + 5

   for (var25 in expanded) {
      context.drawText(font, var25, var16 + 6, var21, -1642759, false)
      var21 += font.fontHeight + 2
   }
}
