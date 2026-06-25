package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

public enum class ClearTypes {
   MOB,
   MINIBOSS,
   OTHER;

   @JvmStatic
   fun getEntries(): EnumEntries<ClearTypes> {
      $ENTRIES
   }
}
