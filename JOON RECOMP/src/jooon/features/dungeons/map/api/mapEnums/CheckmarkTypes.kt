package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

public enum class CheckmarkTypes(prio: Int) {
   UNEXPLORED(0),
   NONE(1),
   FAILED(2),
   WHITE(3),
   GREEN(4);

   public final val prio: Int

   init {
      this.prio = prio
   }

   @JvmStatic
   fun getEntries(): EnumEntries<CheckmarkTypes> {
      $ENTRIES
   }
}
