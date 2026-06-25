package jooon.features.dojo

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      ForceTargetPriority.INSTANCE.nextBestScore(a as ForceTargetPriority.Candidate, this.$nowMs$inlined),
      ForceTargetPriority.INSTANCE.nextBestScore(b as ForceTargetPriority.Candidate, this.$nowMs$inlined)
   )
}