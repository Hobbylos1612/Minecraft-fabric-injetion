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
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJooonUiTheme.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonUiTheme.kt\njooon/config/ui/JooonUiSettings\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,194:1\n288#2,2:195\n*S KotlinDebug\n*F\n+ 1 JooonUiTheme.kt\njooon/config/ui/JooonUiSettings\n*L\n44#1:195,2\n*E\n"])
internal object JooonUiSettings {
   private final val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
   private final val filePath: Path
   private final var loaded: Boolean

   public final var theme: JooonTheme = JooonTheme.DARK
      private set

   @Synchronized
   public fun ensureLoaded() {
      if (!loaded) {
         loaded = true
         if (!Files.exists(filePath)) {
            this.save()
         } else {
            val var1: JooonUiSettings = this

            var `$this$ensureLoaded_u24lambda_u242`: JooonUiSettings
            try {
               `$this$ensureLoaded_u24lambda_u242` = var1
               val var4: Closeable = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)
               var var5: java.lang.Throwable = null

               try {
                  val var10000: JsonElement = JsonParser.parseReader(var4 as BufferedReader).getAsJsonObject().get("theme")
                  val storedTheme: java.lang.String = if (var10000 != null) var10000.getAsString() else null
                  val var12: java.util.Iterator = (JooonTheme.getEntries() as java.lang.Iterable).iterator()

                  while (true) {
                     if (!var12.hasNext()) {
                        var26 = null
                        break
                     }

                     val `element$iv`: Any = var12.next()
                     if (StringsKt.equals((`element$iv` as JooonTheme).name(), storedTheme, true)) {
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
                  CloseableKt.closeFinally(var4, var5)
               }

               `$this$ensureLoaded_u24lambda_u242` = (JooonUiSettings)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
            } catch (var21: java.lang.Throwable) {
               `$this$ensureLoaded_u24lambda_u242` = (JooonUiSettings)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var21))
            }

            if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$ensureLoaded_u24lambda_u242`) != null) {
               theme = JooonTheme.DARK
            }
         }
      }
   }

   @Synchronized
   public fun setTheme(newTheme: JooonTheme) {
      this.ensureLoaded()
      if (theme != newTheme) {
         theme = newTheme
         this.save()
      }
   }

   @Synchronized
   public fun save() {
      Files.createDirectories(filePath.getParent())
      val root: JsonObject = JsonObject()
      root.addProperty("theme", theme.name())
      val tempPath: Path = filePath.resolveSibling("${filePath.getFileName()}.tmp")
      val var3: Closeable = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)
      var var4: java.lang.Throwable = null

      try {
         (var3 as BufferedWriter).write(gson.toJson(root as JsonElement))
      } catch (var11: java.lang.Throwable) {
         var4 = var11
         throw var11
      } finally {
         CloseableKt.closeFinally(var3, var4)
      }

      try {
         Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
      } catch (var10: Exception) {
         Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING)
      }
   }

   @JvmStatic
   fun {
      val var10000: Path = Path.of("config", "jooonreimagined_ui.json")
      filePath = var10000
   }
}
