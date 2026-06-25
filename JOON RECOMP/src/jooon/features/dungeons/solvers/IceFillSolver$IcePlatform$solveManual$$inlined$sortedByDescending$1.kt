package jooon.features.dungeons.solvers

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      if ((b as IceFillSolver.IcePlatform.CompWorld).comp.x
               - this.$d$inlined.data.comp.x
               + ((b as IceFillSolver.IcePlatform.CompWorld).comp.z - this.$d$inlined.data.comp.z) * 100
            == this.$d$inlined.dir)
         1
         else
         0,
      if ((a as IceFillSolver.IcePlatform.CompWorld).comp.x
               - this.$d$inlined.data.comp.x
               + ((a as IceFillSolver.IcePlatform.CompWorld).comp.z - this.$d$inlined.data.comp.z) * 100
            == this.$d$inlined.dir)
         1
         else
         0
   )
}