package jooon.features.dojo

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      Math.abs((a as SwiftnessMovementLogic.Node).x - this.$ignoredBlock$inlined.x)
         + Math.abs((a as SwiftnessMovementLogic.Node).z - this.$ignoredBlock$inlined.z),
      Math.abs((b as SwiftnessMovementLogic.Node).x - this.$ignoredBlock$inlined.x)
         + Math.abs((b as SwiftnessMovementLogic.Node).z - this.$ignoredBlock$inlined.z)
   )
}