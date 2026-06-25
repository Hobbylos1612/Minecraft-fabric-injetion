@file:SourceDebugExtension(["SMAP\nJooonColorSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonColorSupport.kt\njooon/config/ui/JooonColorSupportKt\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,85:1\n1083#2,2:86\n*S KotlinDebug\n*F\n+ 1 JooonColorSupport.kt\njooon/config/ui/JooonColorSupportKt\n*L\n34#1:86,2\n*E\n"])

package jooon.config.ui

import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension

internal fun parseHexColor(hex: String): RgbColor {
   val clean: java.lang.String = StringsKt.trim(StringsKt.removePrefix(hex, "#")).toString()
   if (clean.length() == 6) {
      val var2: java.lang.CharSequence = clean
      var var4: Int = 0

      var var14: Boolean
      while (true) {
         if (var4 >= var2.length()) {
            var14 = false
            break
         }

         run label64@{
            val it: Char = var2.charAt(var4)
            if (!Character.isDigit(it)) {
               val var8: Char = Character.toLowerCase(it)
               if ('a' > var8 || var8 >= 'g') {
                  var14 = true
                  return@label64
               }
            }

            var14 = false
         }

         if (var14) {
            var14 = true
            break
         }

         var4++
      }

      if (!var14) {
         try {
            val var10002: java.lang.String = clean.substring(0, 2)
            val var15: Int = Integer.parseInt(var10002, CharsKt.checkRadix(16))
            val var10003: java.lang.String = clean.substring(2, 4)
            val var16: Int = Integer.parseInt(var10003, CharsKt.checkRadix(16))
            val var10004: java.lang.String = clean.substring(4, 6)
            var10 = Result.constructor_impl/* $VF was: constructor-impl */(RgbColor(var15, var16, Integer.parseInt(var10004, CharsKt.checkRadix(16))))
         } catch (var9: java.lang.Throwable) {
            var10 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var9))
         }

         return (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var10)) RgbColor(255, 255, 255) else var10) as RgbColor
      }
   }

   return RgbColor(255, 255, 255)
}

internal fun formatHexColor(red: Int, green: Int, blue: Int): String {
   val var5: Array<Any> = arrayOf(RangesKt.coerceIn(red, 0, 255), RangesKt.coerceIn(green, 0, 255), RangesKt.coerceIn(blue, 0, 255))
   val var10000: java.lang.String = java.lang.String.format("#%02X%02X%02X", Arrays.copyOf(var5, var5.length))
   return var10000
}

internal fun rgbInt(red: Int, green: Int, blue: Int): Int {
   return -16777216 or RangesKt.coerceIn(red, 0, 255) shl 16 or RangesKt.coerceIn(green, 0, 255) shl 8 or RangesKt.coerceIn(blue, 0, 255)
}

internal fun hsvToRgb(hue: Double, saturation: Double, value: Double): RgbColor {
   val normalizedHue: Double = (hue % 360.0 + 360.0) % 360.0
   val clampedSaturation: Double = RangesKt.coerceIn(saturation, 0.0, 1.0)
   val clampedValue: Double = RangesKt.coerceIn(value, 0.0, 1.0)
   val chroma: Double = clampedValue * clampedSaturation
   val segment: Double = normalizedHue / 60.0
   val x: Double = chroma * (1.0 - Math.abs(normalizedHue / 60.0 % 2.0 - 1.0))
   val match: Double = clampedValue - chroma
   val var20: Triple = if (segment < 1.0)
      Triple(chroma, x, 0.0)
      else
      (
         if (segment < 2.0)
            Triple(x, chroma, 0.0)
            else
            (
               if (segment < 3.0)
                  Triple(0.0, chroma, x)
                  else
                  (if (segment < 4.0) Triple(0.0, x, chroma) else (if (segment < 5.0) Triple(x, 0.0, chroma) else Triple(chroma, 0.0, x)))
            )
      )
      return RgbColor(
      RangesKt.coerceIn((int)(((var20.component1() as java.lang.Number).doubleValue() + match) * 255.0), 0, 255),
      RangesKt.coerceIn((int)(((var20.component2() as java.lang.Number).doubleValue() + match) * 255.0), 0, 255),
      RangesKt.coerceIn((int)(((var20.component3() as java.lang.Number).doubleValue() + match) * 255.0), 0, 255)
   )
}
