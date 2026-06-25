package jooon.features.dojo

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity

{ a: Any, b: Any ->
   val previousCompare: Int = this.$this_thenBy.compare(a, b)
   return if (previousCompare != 0)
      previousCompare
      else
      ComparisonsKt.compareValues(
         this.$player$inlined.method_5858((a as ZombieEntity) as Entity), this.$player$inlined.method_5858((b as ZombieEntity) as Entity)
      )
   }