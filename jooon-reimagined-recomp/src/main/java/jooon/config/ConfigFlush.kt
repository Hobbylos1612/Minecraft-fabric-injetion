package jooon.config

object ConfigFlush {
   public const val MOD_ID: String = "jooonreimagined"

   fun flush() {
      try {
         JooonConfigManager.write("jooonreimagined")
      } catch (var2: java.lang.Throwable) {
      }
   }
}
