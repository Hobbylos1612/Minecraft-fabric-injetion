package jooon.features.mirrorverse

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as SwiftnessWalker.SearchState).f, (b as SwiftnessWalker.SearchState).f)
}