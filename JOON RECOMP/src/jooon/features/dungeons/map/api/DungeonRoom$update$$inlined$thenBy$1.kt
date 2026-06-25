package jooon.features.dungeons.map.api

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0) previousCompare else ComparisonsKt.compareValues((a as WorldComponentPosition).cz, (b as WorldComponentPosition).cz)
}