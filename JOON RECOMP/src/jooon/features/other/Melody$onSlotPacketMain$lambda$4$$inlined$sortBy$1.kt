package jooon.features.other

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as Melody.PrimaryClick).dueMs, (b as Melody.PrimaryClick).dueMs)
}