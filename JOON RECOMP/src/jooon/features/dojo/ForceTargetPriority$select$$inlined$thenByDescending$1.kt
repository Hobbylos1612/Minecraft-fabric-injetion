package jooon.features.dojo

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenByDescending.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues((b as ForceTargetPriority.Candidate).pointValue, (a as ForceTargetPriority.Candidate).pointValue)
   }