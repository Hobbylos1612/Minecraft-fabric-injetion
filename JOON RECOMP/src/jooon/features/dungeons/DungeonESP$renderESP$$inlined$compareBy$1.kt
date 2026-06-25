package jooon.features.dungeons

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(-(a as DungeonESP.ESPData).priority, -(b as DungeonESP.ESPData).priority)
}