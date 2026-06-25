package jooon.features.dojo

import net.minecraft.entity.mob.ZombieEntity

{ a: Any, b: Any ->
   var var10000: Int = (a as ZombieEntity).method_5628()
   var var10001: Int = Control.access$getMeleeTargetId$p()
   val var8: java.lang.Comparable = if (var10001 != null && var10000 == var10001) 0 else 1
   var10000 = (b as ZombieEntity).method_5628()
   var10001 = Control.access$getMeleeTargetId$p()
   return ComparisonsKt.compareValues(var8, if (var10001 != null && var10000 == var10001) 0 else 1)
}