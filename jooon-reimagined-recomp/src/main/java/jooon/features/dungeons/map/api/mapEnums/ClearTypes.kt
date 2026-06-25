package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

enum class ClearTypes {
   MOB,
   MINIBOSS,
   OTHER;

   
   fun getEntries(): EnumEntries<ClearTypes> {
      $ENTRIES
   }
}
