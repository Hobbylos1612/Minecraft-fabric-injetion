package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

public enum class DoorTypes {
   NORMAL,
   WITHER,
   BLOOD,
   ENTRANCE;

   @JvmStatic
   fun getEntries(): EnumEntries<DoorTypes> {
      $ENTRIES
   }
}
