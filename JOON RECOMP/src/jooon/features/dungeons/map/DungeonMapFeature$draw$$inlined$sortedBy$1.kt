package jooon.features.dungeons.map

import jooon.features.dungeons.map.api.DungeonPlayer

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as DungeonPlayer).name == this.$localName$inlined, (b as DungeonPlayer).name == this.$localName$inlined)
}