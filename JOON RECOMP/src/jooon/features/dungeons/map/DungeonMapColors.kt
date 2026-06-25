package jooon.features.dungeons.map

import kotlin.enums.EnumEntries

public enum class DungeonMapColors {
   Background,
   Border,
   RoomEntrance,
   RoomNormal,
   RoomMiniboss,
   RoomFairy,
   RoomBlood,
   RoomPuzzle,
   RoomTrap,
   RoomYellow,
   RoomRare,
   RoomUnknown,
   DoorEntrance,
   DoorWither,
   DoorBlood;

   @JvmStatic
   fun getEntries(): EnumEntries<DungeonMapColors> {
      $ENTRIES
   }
}
