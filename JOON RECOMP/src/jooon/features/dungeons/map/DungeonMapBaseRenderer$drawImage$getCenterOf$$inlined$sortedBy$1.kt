package jooon.features.dungeons.map

import jooon.features.dungeons.map.api.WorldComponentPosition

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      (a as WorldComponentPosition).cx + (a as WorldComponentPosition).cz * 11, (b as WorldComponentPosition).cx + (b as WorldComponentPosition).cz * 11
   )
}