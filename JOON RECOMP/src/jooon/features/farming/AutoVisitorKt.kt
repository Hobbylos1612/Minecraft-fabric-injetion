@file:SourceDebugExtension(["SMAP\nAutoVisitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoVisitor.kt\njooon/features/farming/AutoVisitorKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1010:1\n1#2:1011\n*E\n"])

package jooon.features.farming

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

fun drawTopDownPadScene(context: DrawContext, centerX: Int, centerY: Int, sceneWidth: Int, sceneHeight: Int, drawCrosshair: Boolean) {
   val halfW: Int = sceneWidth / 2
   val halfH: Int = sceneHeight / 2
   val x0: Int = centerX - halfW
   val y0: Int = centerY - halfH
   val x1: Int = centerX + halfW
   val y1: Int = centerY + halfH
   context.method_25294(x0, y0, x1, centerY + halfH, -13820654)
   context.method_73198(x0, y0, sceneWidth, sceneHeight, -8760010)
   val ix0: Int = x0 + 20
   val iy0: Int = y0 + 20
   val ix1: Int = x1 - 20
   val iy1: Int = y1 - 20
   context.method_25294(ix0, iy0, ix1, y1 - 20, -9737102)
   val tile: Int = 16
   var ty: Int = iy0
   var odd: Boolean = false

   while (ty < iy1) {
      var padOuter: Int = ix0
      odd = !odd

      while (padOuter < ix1) {
         context.method_25294(
            padOuter, ty, RangesKt.coerceAtMost(padOuter + tile, ix1), RangesKt.coerceAtMost(ty + tile, iy1), if (odd) -8486776 else -10526360
         )
         odd = !odd
         padOuter += tile
      }

      ty += tile
   }

   context.method_25294(ix0, centerY - 8, ix1, centerY + 8, -868603363)
   context.method_25294(centerX - 8, iy0, centerX + 8, iy1, -868603363)
   context.method_25294(centerX - 22, centerY - 22, centerX + 22, centerY + 22, -1439771014)
   context.method_73198(centerX - 22, centerY - 22, 22 * 2, 22 * 2, -7733312)
   context.method_25294(centerX - 12, centerY - 12, centerX + 12, centerY + 12, -1437077087)
   context.method_73198(centerX - 12, centerY - 12, 12 * 2, 12 * 2, -1900562)
   if (drawCrosshair) {
      context.method_25294(centerX - 1, centerY - 10, centerX + 1, centerY + 10, -1712980496)
      context.method_25294(centerX - 10, centerY - 1, centerX + 10, centerY + 1, -1712980496)
   }
}

fun renderTooltipBox(context: DrawContext, linesIn: MutableList<Text>, mouseX: Int, mouseY: Int) {
   val var10000: MinecraftClient = MinecraftClient.method_1551()
   val var26: TextRenderer = var10000.field_1772
   val font: TextRenderer = var26
   val boxWidth: java.util.List = CollectionsKt.createListBuilder()
   val boxHeight: java.util.List = boxWidth

   for (drawY in linesIn) {
      val var10001: java.util.List = font.method_1728(drawY as StringVisitable, 320)
      boxHeight.addAll(var10001)
   }

   val expanded: java.util.List = CollectionsKt.build(boxWidth)
   val var17: java.util.Iterator = expanded.iterator()
   val var27: java.lang.Comparable
   if (!var17.hasNext()) {
      var27 = null
   } else {
      var var20: java.lang.Comparable = font.method_30880(var17.next() as OrderedText)

      while (var17.hasNext()) {
         val var23: java.lang.Comparable = font.method_30880(var17.next() as OrderedText)
         if (var20.compareTo(var23) < 0) {
            var20 = var23
         }
      }

      var27 = var20
   }

   val var14: Int = (if (var27 as Int != null) var27 as Int else 0) + 12
   val var15: Int = expanded.size() * (font.field_2000 + 2) + 10
   val var16: Int = RangesKt.coerceAtLeast(RangesKt.coerceAtMost(mouseX + 12, var10000.method_22683().method_4486() - var14 - 6), 6)
   val var18: Int = RangesKt.coerceAtLeast(RangesKt.coerceAtMost(mouseY + 12, var10000.method_22683().method_4502() - var15 - 6), 6)
   context.method_25294(var16, var18, var16 + var14, var18 + var15, -300870113)
   context.method_73198(var16, var18, var14, var15, -7362624)
   var var21: Int = var18 + 5

   for (var25 in expanded) {
      context.method_51430(font, var25, var16 + 6, var21, -1642759, false)
      var21 += font.field_2000 + 2
   }
}
