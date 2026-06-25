package jooon.config

import kotlin.enums.EnumEntries

internal enum class ConfigControlKind {
   TOGGLE,
   SLIDER,
   TEXT,
   COLOR,
   ACTION,
   ENUM;

   
   fun getEntries(): EnumEntries<ConfigControlKind> {
      $ENTRIES
   }
}
