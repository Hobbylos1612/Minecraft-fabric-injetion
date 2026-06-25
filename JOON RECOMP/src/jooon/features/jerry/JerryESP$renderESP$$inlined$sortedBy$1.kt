package jooon.features.jerry

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      (a as JerryESP.JerryESPData).getEntity().method_73189().method_1022(this.$playerPos$inlined),
      (b as JerryESP.JerryESPData).getEntity().method_73189().method_1022(this.$playerPos$inlined)
   )
}