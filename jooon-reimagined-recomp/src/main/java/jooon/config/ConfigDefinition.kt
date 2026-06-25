package jooon.config

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.Locale
import kotlin.jvm.internal.ArrayIteratorKt
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Window

internal data class ConfigDefinition(modId: String,
   configClass: Class<*>,
   title: String,
   translations: Map<String, String>,
   categories: List<ConfigCategory>,
   entries: List<ConfigEntryNode>,
   filePath: Path
) {
   val modId: String
   val configClass: Class<*>
   val title: String
   val translations: Map<String, String>
   val categories: List<ConfigCategory>
   val entries: List<ConfigEntryNode>
   val filePath: Path
   private val extras: LinkedHashMap<String, JsonElement>

   init {
      this.modId = modId
      this.configClass = configClass
      this.title = title
      this.translations = translations
      this.categories = categories
      this.entries = entries
      this.filePath = filePath
      this.extras = LinkedHashMap<>()
   }

   fun category(key: String): ConfigCategory? {
      val var4: java.util.Iterator = this.categories.iterator()

      var var10000: Any
      while (true) {
         if (var4.hasNext()) {
            val `element$iv`: Any = var4.next()
            if (!((`element$iv` as ConfigCategory).key == key)) {
return continue
            }

            var10000 = `element$iv`
break
         }

         var10000 = null
break
      }

      return var10000 as ConfigCategory
   }

   @Synchronized
   fun load(gson: Gson) {


      this.extras.clear()
      if (root != null) {
         val `this$iv`: java.lang.Iterable = this.entries
         val entry: java.util.Collection = HashSet()

         for (`item$iv` in `this$iv`) {
            entry.add((`item$iv` as ConfigEntryNode).fieldName)
         }


         for (var15 in root.entrySet()) {


            if (!known.contains(var17)) {
               this.extras.put(var17, var18.deepCopy())
            }
         }

         for (var16 in this.entries) {
            if (root.has(var16.fieldName)) {

               var16.applyJson(var10001)
            }
         }
      }

      if (loaded.migrated) {
         this.write(gson)
      }
   }

   @Synchronized
   fun write(gson: Gson) {


      for (entry in this.extras.entrySet()) {
         root.add(entry.getKey() as String, (entry.getValue() as JsonElement).deepCopy())
      }

      for (var8 in this.entries) {
         root.add(var8.fieldName, var8.toJson())
      }


      JooonConfigManagerKt.access$writeJsonAtomically(var10000, var10001)
   }

   fun enumLabel(value: Enum<*>): String {
      var var10000: String = this.translations.get("${this.modId}.midnightconfig.enum.${value.getClass().getSimpleName()}.${value.name()}")
      if (var10000 == null) {
         var10000 = JooonConfigManagerKt.access$prettifyName(value.name())
      }

      return var10000
   }

   private fun resolveLoadRoot(gson: Gson): LoadResult {
      if (Files.exists(this.filePath)) {
         return LoadResult(JooonConfigManagerKt.access$readJsonObject(this.filePath, gson), false)
      } else {
         if (this.modId == "jooonreimagined_state") {

            if (migrated != null) {
               return LoadResult(migrated, true)
            }
         }

         return LoadResult(null, false)
      }
   }

   private fun migrateLegacyState(gson: Gson): JsonObject? {

      if (!Files.exists(legacyPath)) {
         return null
      } else {
         var var10000: JsonObject = JooonConfigManagerKt.access$readJsonObject(legacyPath, gson)
         if (var10000 == null) {
            return null
         } else {
            var10000 = var10000.getAsJsonObject("overlays")
            if (var10000 == null) {
               return null
            } else {




               migrateLegacyState$migrateCentered(var10000, migrated, centerX, centerY, "witherShieldOverlay", "witherShield")
               migrateLegacyState$migrateCentered(var10000, migrated, centerX, centerY, "dbDisplay", "dbDisplay")
               migrateLegacyState$migrateCentered(var10000, migrated, centerX, centerY, "slayerHP", "slayerHPDisplay")
               return if (migrated.size() == 0) null else migrated
            }
         }
      }
   }

   public operator fun component1(): String {
      return this.modId
   }

   public operator fun component2(): Class<*> {
      return this.configClass
   }

   public operator fun component3(): String {
      return this.title
   }

   public operator fun component4(): Map<String, String> {
      return this.translations
   }

   public operator fun component5(): List<ConfigCategory> {
      return this.categories
   }

   public operator fun component6(): List<ConfigEntryNode> {
      return this.entries
   }

   public operator fun component7(): Path {
      return this.filePath
   }

   fun copy(
      modId: String = this.modId,
      configClass: Class<*> = this.configClass,
      title: String = this.title,
      translations: Map<String, String> = this.translations,
      categories: List<ConfigCategory> = this.categories,
      entries: List<ConfigEntryNode> = this.entries,
      filePath: Path = this.filePath
   ): ConfigDefinition {
      return ConfigDefinition(modId, configClass, title, translations, categories, entries, filePath)
   }

   override fun toString(): String {
      return "ConfigDefinition(modId=${this.modId}, configClass=${this.configClass}, title=${this.title}, translations=${this.translations}, categories=${this.categories}, entries=${this.entries}, filePath=${this.filePath})"
   }

   override fun hashCode(): Int {
      return (
               (
                        (((this.modId.hashCode() * 31 + this.configClass.hashCode()) * 31 + this.title.hashCode()) * 31 + this.translations.hashCode()) * 31
                           + this.categories.hashCode()
                     )
                     * 31
                  + this.entries.hashCode()
            )
            * 31
         + this.filePath.hashCode()
      }

   override operator fun equals(other: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is ConfigDefinition
            && this.modId == (other as ConfigDefinition).modId
            && this.configClass == (other as ConfigDefinition).configClass
            && this.title == (other as ConfigDefinition).title
            && this.translations == (other as ConfigDefinition).translations
            && this.categories == (other as ConfigDefinition).categories
            && this.entries == (other as ConfigDefinition).entries
            && this.filePath == (other as ConfigDefinition).filePath
         }
   }

   
   fun `migrateLegacyState$migrateCentered`(
      overlays: JsonObject, migrated: JsonObject, centerX: Int, centerY: Int, oldKey: String, newPrefix: String
   ) {

      if (var10000 != null) {

         if (var9 != null) {


            if (var10 != null) {

               migrated.addProperty("$newPrefixX", absoluteX - centerX)
               migrated.addProperty("$newPrefixY", absoluteY - centerY)
               var var10001: String = "$newPrefixMovable"
               var var10002: JsonElement = var10000.get("movable")
               migrated.addProperty(var10001, var10002 == null || var10002.getAsBoolean())
               var10001 = "$newPrefixInitDone"
               var10002 = var10000.get("init")
               migrated.addProperty(var10001, var10002 == null || var10002.getAsBoolean())
            }
         }
      }
   }

   @SourceDebugExtension(["SMAP\nJooonConfigManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonConfigManager.kt\njooon/config/ConfigDefinition$Companion\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,480:1\n372#2,7:481\n372#2,7:493\n1#3:488\n11065#4:489\n11400#4,3:490\n11065#4:504\n11400#4,3:505\n125#5:500\n152#5,3:501\n1179#6,2:508\n1253#6,4:510\n*S KotlinDebug\n*F\n+ 1 JooonConfigManager.kt\njooon/config/ConfigDefinition$Companion\n*L\n208#1:481,7\n226#1:493,7\n221#1:489\n221#1:490,3\n253#1:504\n253#1:505,3\n230#1:500\n230#1:501,3\n268#1:508,2\n268#1:510,4\n*E\n"])
   companion object {
      fun build(modId: String, configClass: Class<*>): ConfigDefinition {
         val translations: java.util.Map = this.loadTranslations(modId)
         val entries: java.util.List = ArrayList()

         val configCategories: java.util.Iterator = ArrayIteratorKt.iterator(configClass.getDeclaredFields())

         while (configCategories.hasNext()) {
            val `this$iv`: Field = configCategories.next() as Field
            if (Modifier.isStatic(`this$iv`.getModifiers()) && !`this$iv`.isSynthetic()) {
               `this$iv`.setAccessible(true)
               val ``: Comment = `this$iv`.getAnnotation(Comment::class.java)
               if (`` != null) {




                  var var10005: String = translations.get("$modId.midnightconfig.${`this$iv`.getName()}") as String
                  if (var10005 == null) {
                     var10005 = `this$iv`.getName()
                     var10005 = JooonConfigManagerKt.access$prettifyName(var10005)
                  }

                  var10000./* $VF: Unable to resugar constructor */<init>(var10002, var10003, var10004, var10005)
                  if (!isBlank(``.category)) {
                     val `destination$iv$iv`: java.util.Map = categories
                     val ``: Any = ``.category
                     val `item$iv$iv`: Any = `destination$iv$iv`.get(``)
                     val var69: Any
                     if (`item$iv$iv` == null) {

                        `destination$iv$iv`.put(``, var58)
                        var69 = var58
                     } else {
                        var69 = `item$iv$iv`
                     }

                     (var69 as java.util.Collection).add(var10000)
                  }
               }

               if (var70 != null) {
                  var var71: Field = `this$iv`
                  var var46: String = var70.category
                  var var10001: java.lang.CharSequence
                  if (isBlank(var46)) {
                     var10001 = null
                     var71 = `this$iv`
                  } else {
                     var10001 = var46
                  }

                  var10001 = var10001
                  var var76: String = translations.get("$modId.midnightconfig.${`this$iv`.getName()}") as String
                  if (var76 == null) {
                     var76 = `this$iv`.getName()
                     var76 = JooonConfigManagerKt.access$prettifyName(var76)
                  }




                  val var84: java.util.List
                  if (`this$iv`.getType().isEnum()) {
                     val var10007: Array<Any> = `this$iv`.getType().getEnumConstants()
                     val var59: java.util.Collection = ArrayList(var10007.length)

                     for (`item$iv$ivx` in var10007) {
                        var59.add(`item$iv$ivx` as java.lang.Enum)
                     }

                     var84 = var59 as java.util.List
                  } else {
                     var84 = emptyList()
                  }

                  entries.add(var44)
                  var46 = var44.category
                  if (var46 != null && !isBlank(var46)) {
                     val var53: java.util.Map = categories

                     val var72: Any
                     if (var60 == null) {

                        var53.put(var46, var63)
                        var72 = var63
                     } else {
                        var72 = var60
                     }

                     (var72 as java.util.Collection).add(var44)
                  }
               }
            }
         }

         val var43: java.util.Map = categories
         val var45: java.util.Collection = ArrayList(categories.size())

         for (var57 in var43.entrySet()) {

            val var66: java.util.List = var57.getValue() as java.util.List

            var var79: String = translations.get("$modId.midnightconfig.category.$var65") as String
            if (var79 == null) {
               var79 = JooonConfigManagerKt.access$prettifyName(var65)
            }

            var73./* $VF: Unable to resugar constructor */<init>(var65, var79, toList(var66))
            var45.add(var73)
         }

         val var38: java.util.List = var45 as java.util.List

         var var81: String = translations.get("$modId.midnightconfig.title") as String
         if (var81 == null) {
            var81 = JooonConfigManagerKt.access$prettifyName(modId)
         }

         var74./* $VF: Unable to resugar constructor */<init>(modId, configClass, var81, translations, var38, entries, var10008)
         return var74
      }

      private fun resolveControlKind(field: Field, entry: Entry): ConfigControlKind {
         val var10000: ConfigControlKind
         if (entry.isColor) {
            var10000 = ConfigControlKind.COLOR
         } else if (field.getType().isEnum()) {
            val var15: Array<Any> = field.getType().getEnumConstants()
            val `destination$iv$iv`: java.util.Collection = ArrayList(var15.length)

            for (`item$iv$iv` in var15) {

               `destination$iv$iv`.add(var16)
            }

            val names: java.util.Set = toSet(`destination$iv$iv` as java.util.List)
            var10000 = if (names.contains("IDLE") && names.contains("CLICK")) ConfigControlKind.ACTION else ConfigControlKind.ENUM
         } else {
            var10000 = if (!(field.getType() == Boolean::class.javaPrimitiveType) && !(field.getType() == Boolean::class.javaObjectType))
               (
                  if (entry.isSlider)
                     ConfigControlKind.SLIDER
return else
                     (if (field.getType() == String::class.java) ConfigControlKind.TEXT else ConfigControlKind.ENUM)
               )
return else
               ConfigControlKind.TOGGLE
            }

         return var10000
      }

      private fun loadTranslations(modId: String): Map<String, String> {

         if (var10000 != null) {

            var var4: java.lang.Throwable = null

            try {



               var var38: java.lang.Throwable = null

               try {
                  val var41: java.util.Set = JsonParser.parseReader(var37 as InputStreamReader).getAsJsonObject().entrySet()
                  val `this$iv`: java.lang.Iterable = var41
                  val `destination$iv$iv`: java.util.Map = LinkedHashMap(
                     (mapCapacity(var41.count().coerceAtLeast(10))).coerceAtLeast(16)
                  )

                  for (`element$iv$iv` in `this$iv`) {

                     `destination$iv$iv`.put(var39.getFirst(), var39.getSecond())
                  }

                  return `destination$iv$iv`
               } catch (var33: java.lang.Throwable) {
                  var38 = var33
                  throw var33
               } finally {
                  var37.close()
               }
            } catch (var35: java.lang.Throwable) {
               var4 = var35
               throw var35
            } finally {
               var3.close()
            }
         } else {
            return emptyMap()
         }
      }
   }
}
