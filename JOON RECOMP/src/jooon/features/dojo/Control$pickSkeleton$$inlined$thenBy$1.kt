package jooon.features.dojo

import net.minecraft.entity.mob.WitherSkeletonEntity

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues((a as WitherSkeletonEntity).method_5628(), (b as WitherSkeletonEntity).method_5628())
   }