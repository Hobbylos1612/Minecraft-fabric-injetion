package jooon.util

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.ScoreboardEntry
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.scoreboard.Team
import net.minecraft.text.MutableText
import net.minecraft.text.Text

@SourceDebugExtension(["SMAP\nScoreboardUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ScoreboardUtil.kt\njooon/util/ScoreboardUtil\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,65:1\n1#2:66\n1#2:78\n1054#3:67\n1603#3,9:68\n1855#3:77\n1856#3:79\n1612#3:80\n*S KotlinDebug\n*F\n+ 1 ScoreboardUtil.kt\njooon/util/ScoreboardUtil\n*L\n36#1:78\n34#1:67\n36#1:68,9\n36#1:77\n36#1:79\n36#1:80\n*E\n"])
public object ScoreboardUtil {
   private final val COLOR_RX: Regex = Regex("§.")

   private fun strip(s: String): String {
      return StringsKt.trim(COLOR_RX.replace(s, "")).toString()
   }

   public fun getSidebarTitle(): String? {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1687 != null) {
         val var5: Scoreboard = var10000.field_1687.method_8428()
         if (var5 != null) {
            val var6: ScoreboardObjective = var5.method_1189(ScoreboardDisplaySlot.field_45157)
            if (var6 != null) {
               val var7: java.lang.String = var6.method_1114().getString()
               val var3: java.lang.CharSequence = StringsKt.trim(var7).toString()
               return (if (var3.length() == 0) null else var3) as java.lang.String
            }
         }
      }

      return null
   }

   public fun getSidebarLines(): List<String> {
      var var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1687 == null) {
         return CollectionsKt.emptyList()
      } else {
         var10000 = var10000.field_1687.method_8428()
         val board: Scoreboard = var10000
         val var34: ScoreboardObjective = var10000.method_1189(ScoreboardDisplaySlot.field_45157)
         if (var34 == null) {
            return CollectionsKt.emptyList()
         } else {
            val var35: java.util.Collection = var10000.method_1184(var34)
            val `$this$mapNotNullTo$iv$iv`: java.lang.Iterable = CollectionsKt.sortedWith(var35, ScoreboardUtil$getSidebarLines$$inlined$sortedByDescending$1())
            val `destination$iv$iv`: java.util.Collection = ArrayList()

            for (`element$iv$iv$iv` in `$this$mapNotNullTo$iv$iv`) {
               var team: Team
               run label62@{
                  var10000 = (`element$iv$iv$iv` as ScoreboardEntry).comp_2127()
                  team = board.method_1164(var10000)
                  if (team != null) {
                     var10000 = team.method_1144()
                     if (var10000 != null) {
                        return@label62
                     }
                  }

                  val var38: MutableText = Text.method_43473()
                  var10000 = var38 as Text
               }

               run label65@{
                  if (team != null) {
                     var10000 = team.method_1136()
                     if (var10000 != null) {
                        return@label65
                     }
                  }

                  val var40: MutableText = Text.method_43473()
                  var10000 = var40 as Text
               }

               val var41: MutableText = Text.method_43473().method_10852(var10000).method_10852(Text.method_43470(var10000) as Text).method_10852(var10000)
               val var42: ScoreboardUtil = INSTANCE
               val var10001: java.lang.String = var41.getString()
               val var27: java.lang.CharSequence = var42.strip(var10001)
               val var43: java.lang.CharSequence = if (var27.length() == 0) null else var27
               val var44: Any = var43 as java.lang.String
               if (var43 as java.lang.String != null) {
                  `destination$iv$iv`.add(var44)
               }
            }

            return `destination$iv$iv` as MutableList<java.lang.String>
         }
      }
   }
}
