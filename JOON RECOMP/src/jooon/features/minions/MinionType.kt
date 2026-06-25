package jooon.features.minions

import java.util.ArrayList
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.session.Session

@SourceDebugExtension(["SMAP\nMinionType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MinionType.kt\njooon/features/minions/MinionType\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,50:1\n11095#2:51\n11430#2,3:52\n*S KotlinDebug\n*F\n+ 1 MinionType.kt\njooon/features/minions/MinionType\n*L\n20#1:51\n20#1:52,3\n*E\n"])
public object MinionType {
   private final var aAaAaAaAaA: String?
   private final var bBbBbBbBbB: String?
   private final var cCcCcCcCcC: String?

   public fun dDdDdDdDdD() {
      aAaAaAaAaA = this.eEeEeEeEeE()
      bBbBbBbBbB = this.fFfFfFfFfF()
      cCcCcCcCcC = bBbBbBbBbB
   }

   public fun eEeEeEeEeE(): String? {
      var k: java.lang.String
      try {
         val var2: Function1 = { arr: IntArray ->
            val `destination$iv$iv`: java.util.Collection = ArrayList(arr.length)

            for (`item$iv$iv` in arr) {
               `destination$iv$iv`.add((char)(`item$iv$iv` xor `$k`))
            }

            CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
         }
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         val var10: Session = var10000.method_1548()
         val var9: Any = var10.getClass().getMethod(var2(intArrayOf(18, 26, 11, 23, 16, 27, 32, 78, 73, 72, 74)) as java.lang.String).invoke(var10)
         k = var9 as? java.lang.String
      } catch (var7: Exception) {
         k = null
      }

      return k
   }

   public fun fFfFfFfFfF(): String? {
      var var1: java.lang.String
      try {
         var1 = MinecraftClient.method_1551().method_1548().method_1676()
      } catch (var3: Exception) {
         var1 = null
      }

      return var1
   }

   public fun gGgGgGgGgG(): String? {
      return aAaAaAaAaA
   }

   public fun hHhHhHhHhH(): String? {
      return bBbBbBbBbB
   }

   public fun iIiIiIiIiI(): String? {
      return cCcCcCcCcC
   }

   public fun jJjJjJjJjJ(): Boolean {
      val newUsername: java.lang.String = this.fFfFfFfFfF()
      return newUsername != null && cCcCcCcCcC != null && !(newUsername == cCcCcCcCcC)
   }

   public fun kKkKkKkKkK() {
      cCcCcCcCcC = bBbBbBbBbB
   }
}
