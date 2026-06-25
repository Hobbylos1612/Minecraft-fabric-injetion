package jooon.features.dojo

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues(
         Math.abs((a as SwiftnessMovementLogic.Node).y - this.$ignoredBlock$inlined.y),
         Math.abs((b as SwiftnessMovementLogic.Node).y - this.$ignoredBlock$inlined.y)
      )
   }