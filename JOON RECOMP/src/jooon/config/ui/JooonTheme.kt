package jooon.config.ui

import kotlin.enums.EnumEntries

internal enum class JooonTheme(displayName: String) {
   DARK("Dark"),
   LIGHT("Light"),
   JOONS_LIME("Jooon's Lime");

   public final val displayName: String

   init {
      this.displayName = displayName
   }

   @JvmStatic
   fun getEntries(): EnumEntries<JooonTheme> {
      $ENTRIES
   }
}
