package jooon.features.slayers

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(-(a as SlayerESP.ESPData).priority, -(b as SlayerESP.ESPData).priority)
}