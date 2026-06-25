package jooon.features.dungeons

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues(
         -(a as DungeonESP.ESPData).getEntity().method_73189().method_1022(this.$playerPos$inlined),
         -(b as DungeonESP.ESPData).getEntity().method_73189().method_1022(this.$playerPos$inlined)
      )
   }