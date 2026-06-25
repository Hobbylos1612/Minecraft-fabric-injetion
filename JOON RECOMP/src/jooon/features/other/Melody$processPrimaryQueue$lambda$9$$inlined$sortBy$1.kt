package jooon.features.other

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as Melody.SecondaryClick).dueMs, (b as Melody.SecondaryClick).dueMs)
}