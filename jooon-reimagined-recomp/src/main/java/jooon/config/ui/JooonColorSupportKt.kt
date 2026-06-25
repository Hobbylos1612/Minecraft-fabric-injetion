@file:SourceDebugExtension(["SMAP\nJooonColorSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonColorSupport.kt\njooon/config/ui/JooonColorSupportKt\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,85:1\n1083#2,2:86\n*S KotlinDebug\n*F\n+ 1 JooonColorSupport.kt\njooon/config/ui/JooonColorSupportKt\n*L\n34#1:86,2\n*E\n"])

package jooon.config.ui

import java.util.Arrays
internal fun parseHexColor(hex: String): RgbColor {

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

            if (!Character.isDigit(it)) {

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





            var10 = Result.constructor_impl/* $VF was: constructor-impl */(RgbColor(var15, var16, Integer.parseInt(var10004, CharsKt.checkRadix(16))))
         } catch (var9: java.lang.Throwable) {
            var10 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var9))
         }

         return (if (Result.isFailure/* $VF was: isFailure-impl */(var10)) RgbColor(255, 255, 255) else var10) as RgbColor
      }
   }

   return RgbColor(255, 255, 255)
}

internal fun formatHexColor(red: Int, green: Int, blue: Int): String {
   val var5: Array<Any> = arrayOf((red).coerceIn(0, 255), (green).coerceIn(0, 255), (blue).coerceIn(0, 255))

   return var10000
}

internal fun rgbInt(red: Int, green: Int, blue: Int): Int {
   return -16777216 or (red).coerceIn(0, 255) shl 16 or (green).coerceIn(0, 255) shl 8 or (blue).coerceIn(0, 255)
}

internal fun hsvToRgb(hue: Double, saturation: Double, value: Double): RgbColor {








      Triple(chroma, x, 0.0)
return else
      (
         if (segment < 2.0)
            Triple(x, chroma, 0.0)
return else
            (
               if (segment < 3.0)
                  Triple(0.0, chroma, x)
return else
                  (if (segment < 4.0) Triple(0.0, x, chroma) else (if (segment < 5.0) Triple(x, 0.0, chroma) else Triple(chroma, 0.0, x)))
            )
      )
      return RgbColor(
      ((((var20.component1() as java.lang.Number).doubleValue() + match) * 255.0).toInt()).coerceIn(0, 255),
      ((((var20.component2() as java.lang.Number).doubleValue() + match) * 255.0).toInt()).coerceIn(0, 255),
      ((((var20.component3() as java.lang.Number).doubleValue() + match) * 255.0).toInt()).coerceIn(0, 255)
   )
}
