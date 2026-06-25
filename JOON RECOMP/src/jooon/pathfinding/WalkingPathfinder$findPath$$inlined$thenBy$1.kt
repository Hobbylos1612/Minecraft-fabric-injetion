package jooon.pathfinding

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0) previousCompare else ComparisonsKt.compareValues((a as WalkingPathfinder.OpenNode).g, (b as WalkingPathfinder.OpenNode).g)
}