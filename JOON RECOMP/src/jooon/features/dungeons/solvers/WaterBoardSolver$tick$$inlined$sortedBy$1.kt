package jooon.features.dungeons.solvers

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as Pair).getSecond() as Int, (b as Pair).getSecond() as Int)
}