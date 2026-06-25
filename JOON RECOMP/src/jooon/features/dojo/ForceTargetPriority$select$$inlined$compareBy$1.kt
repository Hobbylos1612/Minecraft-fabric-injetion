package jooon.features.dojo

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      ForceTargetPriority.access$normalizedEdgeDistance(ForceTargetPriority.INSTANCE, (a as ForceTargetPriority.Candidate).edgeDistance),
      ForceTargetPriority.access$normalizedEdgeDistance(ForceTargetPriority.INSTANCE, (b as ForceTargetPriority.Candidate).edgeDistance)
   )
}