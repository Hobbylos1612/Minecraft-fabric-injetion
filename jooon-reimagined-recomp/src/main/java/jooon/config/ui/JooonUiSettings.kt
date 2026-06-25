package jooon.config.ui

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
internal object JooonUiSettings {
   private val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
   private val filePath: Path
   private var loaded: Boolean

   var theme: JooonTheme = JooonTheme.DARK
      private set

   @Synchronized
   fun ensureLoaded() {
      if (!loaded) {
         loaded = true
         if (!Files.exists(filePath)) {
            this.save()
         } else {


            var `this24lambda_u242`: JooonUiSettings
            try {
               `this24lambda_u242` = var1

               var var5: java.lang.Throwable = null

               try {


                  val var12: java.util.Iterator = (JooonTheme.getEntries() as java.lang.Iterable).iterator()

                  while (true) {
                     if (!var12.hasNext()) {
                        var26 = null
break
                     }

                     val `element$iv`: Any = var12.next()
                     if (equals((`element$iv` as JooonTheme).name(), storedTheme, true)) {
                        var26 = `element$iv`
break
                     }
                  }

                  var var27: JooonTheme = var26 as JooonTheme
                  if (var26 as JooonTheme == null) {
                     var27 = JooonTheme.DARK
                  }

                  theme = var27
               } catch (var19: java.lang.Throwable) {
                  var5 = var19
                  throw var19
               } finally {
                  var4.close()
               }

               `this24lambda_u242` = Result(Unit)
            } catch (var21: java.lang.Throwable) {
               `this24lambda_u242` = Result(ResultKt.createFailure(var21))
            }

            if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`this24lambda_u242`) != null) {
               theme = JooonTheme.DARK
            }
         }
      }
   }

   @Synchronized
   fun setTheme(newTheme: JooonTheme) {
      this.ensureLoaded()
      if (theme != newTheme) {
         theme = newTheme
         this.save()
      }
   }

   @Synchronized
   fun save() {
      Files.createDirectories(filePath.getParent())

      root.addProperty("theme", theme.name())


      var var4: java.lang.Throwable = null

      try {
         (var3 as BufferedWriter).write(gson.toJson(root as JsonElement))
      } catch (var11: java.lang.Throwable) {
         var4 = var11
         throw var11
      } finally {
         var3.close()
      }

      try {
         Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
      } catch (var10: Exception) {
         Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING)
      }
   }

   
   fun {

      filePath = var10000
   }
}
