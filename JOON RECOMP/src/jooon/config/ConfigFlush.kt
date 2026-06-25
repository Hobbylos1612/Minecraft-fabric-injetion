package jooon.config

public object ConfigFlush {
   public const val MOD_ID: String = "jooonreimagined"

   public fun flush() {
      try {
         JooonConfigManager.INSTANCE.write("jooonreimagined")
      } catch (var2: java.lang.Throwable) {
      }
   }
}
