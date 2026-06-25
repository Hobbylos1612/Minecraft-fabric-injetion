package jooon.features.dojo

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues(
      this.$player$inlined.method_5858((a as ZombieEntity) as Entity), this.$player$inlined.method_5858((b as ZombieEntity) as Entity)
   )
}