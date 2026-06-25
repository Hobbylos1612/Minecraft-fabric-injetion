package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

enum class DoorTypes {
   NORMAL,
   WITHER,
   BLOOD,
   ENTRANCE;

   
   fun getEntries(): EnumEntries<DoorTypes> {
      $ENTRIES
   }
}
