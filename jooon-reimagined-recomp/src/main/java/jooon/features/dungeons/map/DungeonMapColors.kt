package jooon.features.dungeons.map

import kotlin.enums.EnumEntries

enum class DungeonMapColors {
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

   
   fun getEntries(): EnumEntries<DungeonMapColors> {
      $ENTRIES
   }
}
