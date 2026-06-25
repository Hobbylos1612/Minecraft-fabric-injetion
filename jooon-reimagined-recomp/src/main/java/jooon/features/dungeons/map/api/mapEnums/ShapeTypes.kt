package jooon.features.dungeons.map.api.mapEnums

import kotlin.enums.EnumEntries

enum class ShapeTypes {
   Unknown,
   Shape1x1,
   Shape1x2,
   Shape1x3,
   Shape1x4,
   Shape2x2,
   ShapeL;

   
   fun getEntries(): EnumEntries<ShapeTypes> {
      $ENTRIES
   }
}
