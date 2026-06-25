package jooon.util

import net.minecraft.scoreboard.ScoreboardEntry

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((b as ScoreboardEntry).comp_2128(), (a as ScoreboardEntry).comp_2128())
}