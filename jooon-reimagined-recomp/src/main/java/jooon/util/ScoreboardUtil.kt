package jooon.util

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.text.Text

object ScoreboardUtil {
   private val COLOR_RX: Regex = Regex("§.")

   private fun strip(s: String): String {
      return COLOR_RX.replace(s, "")
   }

   fun getSidebarTitle(): String? {
      val client = MinecraftClient.getInstance()
      val world = client.world ?: return null
      val objective = world.scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return null
      return strip(objective.displayName.string)
   }

   fun getSidebarLines(): List<String> {
      val client = MinecraftClient.getInstance()
      val world = client.world ?: return emptyList()
      val scoreboard = world.scoreboard
      val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return emptyList()

      return scoreboard.getScoreboardEntries(objective)
         .sortedByDescending { it.value() }
         .map { entry ->
            val owner = entry.owner()
            val team = scoreboard.getScoreHolderTeam(owner)
            val prefix = team?.getPrefix()?.string ?: ""
            val suffix = team?.getSuffix()?.string ?: ""
            strip("$prefix$owner$suffix")
         }
         .filter { it.isNotEmpty() }
   }
}
