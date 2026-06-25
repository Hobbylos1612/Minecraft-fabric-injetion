package jooon.features.minions

import java.util.ArrayList
import kotlin.jvm.functions.Function1
import net.minecraft.client.MinecraftClient
import net.minecraft.client.session.Session

object MinionType {
   private var aAaAaAaAaA: String?
   private var bBbBbBbBbB: String?
   private var cCcCcCcCcC: String?

   fun dDdDdDdDdD() {
      aAaAaAaAaA = this.eEeEeEeEeE()
      bBbBbBbBbB = this.fFfFfFfFfF()
      cCcCcCcCcC = bBbBbBbBbB
   }

   fun eEeEeEeEeE(): String? {
      var k: String
      try {

            val `destination$iv$iv`: java.util.Collection = ArrayList(arr.length)

            for (`item$iv$iv` in arr) {
               `destination$iv$iv`.add((`item$iv$iv` xor `$k`).toChar())
            }

            `destination$iv$iv` as java.util.List.joinToString("")
         }



         k = var9 as? String
      } catch (var7: Exception) {
         k = null
      }

      return k
   }

   fun fFfFfFfFfF(): String? {
      var var1: String
      try {
         var1 = MinecraftClient.getInstance().getSession().getUsername()
      } catch (var3: Exception) {
         var1 = null
      }

      return var1
   }

   fun gGgGgGgGgG(): String? {
      return aAaAaAaAaA
   }

   fun hHhHhHhHhH(): String? {
      return bBbBbBbBbB
   }

   fun iIiIiIiIiI(): String? {
      return cCcCcCcCcC
   }

   fun jJjJjJjJjJ(): Boolean {

      return newUsername != null && cCcCcCcCcC != null && !(newUsername == cCcCcCcCcC)
   }

   fun kKkKkKkKkK() {
      cCcCcCcCcC = bBbBbBbBbB
   }
}
