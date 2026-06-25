package jooon.pathfinding

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as WalkingPathfinder.OpenNode).f, (b as WalkingPathfinder.OpenNode).f)
}