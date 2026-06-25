package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

enum class CheckmarkTypes(prio: Int) {
   UNEXPLORED(0),
   NONE(1),
   FAILED(2),
   WHITE(3),
   GREEN(4);

   val prio: Int

   init {
      this.prio = prio
   }

   
   fun getEntries(): EnumEntries<CheckmarkTypes> {
      $ENTRIES
   }
}
