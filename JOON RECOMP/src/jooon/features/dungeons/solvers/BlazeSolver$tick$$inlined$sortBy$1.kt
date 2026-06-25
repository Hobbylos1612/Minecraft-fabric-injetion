package jooon.features.dungeons.solvers

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as BlazeSolver.BlazeEntity).maxHP, (b as BlazeSolver.BlazeEntity).maxHP)
}