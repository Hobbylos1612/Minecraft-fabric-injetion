package jooon.features.dojo

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues((a as ForceTargetPriority.Candidate).edgeDistance, (b as ForceTargetPriority.Candidate).edgeDistance)
   }