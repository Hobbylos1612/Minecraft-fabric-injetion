package jooon.features.slayers

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues(
         (a as SlayerESP.ESPData).getEntity().method_73189().method_1022(this.$playerPos$inlined),
         (b as SlayerESP.ESPData).getEntity().method_73189().method_1022(this.$playerPos$inlined)
      )
   }