package jooon.features.dungeons.map.api

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as WorldComponentPosition).cx, (b as WorldComponentPosition).cx)
}