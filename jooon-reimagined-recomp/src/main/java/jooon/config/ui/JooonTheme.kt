package jooon.config.ui

import kotlin.enums.EnumEntries

internal enum class JooonTheme(displayName: String) {
   DARK("Dark"),
   LIGHT("Light"),
   JOONS_LIME("Jooon's Lime");

   val displayName: String

   init {
      this.displayName = displayName
   }

   
   fun getEntries(): EnumEntries<JooonTheme> {
      $ENTRIES
   }
}
