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
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Window

@SourceDebugExtension(["SMAP\nJooonConfigManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonConfigManager.kt\njooon/config/ConfigDefinition\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,480:1\n288#2,2:481\n1620#2,3:483\n*S KotlinDebug\n*F\n+ 1 JooonConfigManager.kt\njooon/config/ConfigDefinition\n*L\n101#1:481,2\n110#1:483,3\n*E\n"])
internal data class ConfigDefinition(modId: String,
   configClass: Class<*>,
   title: String,
   translations: Map<String, String>,
   categories: List<ConfigCategory>,
   entries: List<ConfigEntryNode>,
   filePath: Path
) {
   public final val modId: String
   public final val configClass: Class<*>
   public final val title: String
   public final val translations: Map<String, String>
   public final val categories: List<ConfigCategory>
   public final val entries: List<ConfigEntryNode>
   public final val filePath: Path
   private final val extras: LinkedHashMap<String, JsonElement>

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

   public fun category(key: String): ConfigCategory? {
      val var4: java.util.Iterator = this.categories.iterator()

      var var10000: Any
      while (true) {
         if (var4.hasNext()) {
            val `element$iv`: Any = var4.next()
            if (!((`element$iv` as ConfigCategory).key == key)) {
               continue
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
   public fun load(gson: Gson) {
      val loaded: LoadResult = this.resolveLoadRoot(gson)
      val root: JsonObject = loaded.root
      this.extras.clear()
      if (root != null) {
         val `$this$mapTo$iv`: java.lang.Iterable = this.entries
         val entry: java.util.Collection = HashSet()

         for (`item$iv` in `$this$mapTo$iv`) {
            entry.add((`item$iv` as ConfigEntryNode).fieldName)
         }

         val known: HashSet = entry as HashSet

         for (var15 in root.entrySet()) {
            val var17: java.lang.String = var15.getKey() as java.lang.String
            val var18: JsonElement = var15.getValue() as JsonElement
            if (!known.contains(var17)) {
               this.extras.put(var17, var18.deepCopy())
            }
         }

         for (var16 in this.entries) {
            if (root.has(var16.fieldName)) {
               val var10001: JsonElement = root.get(var16.fieldName)
               var16.applyJson(var10001)
            }
         }
      }

      if (loaded.migrated) {
         this.write(gson)
      }
   }

   @Synchronized
   public fun write(gson: Gson) {
      val root: JsonObject = JsonObject()

      for (entry in this.extras.entrySet()) {
         root.add(entry.getKey() as java.lang.String, (entry.getValue() as JsonElement).deepCopy())
      }

      for (var8 in this.entries) {
         root.add(var8.fieldName, var8.toJson())
      }

      val var10000: Path = this.filePath
      val var10001: java.lang.String = gson.toJson(root as JsonElement)
      JooonConfigManagerKt.access$writeJsonAtomically(var10000, var10001)
   }

   public fun enumLabel(value: Enum<*>): String {
      var var10000: java.lang.String = this.translations.get("${this.modId}.midnightconfig.enum.${value.getClass().getSimpleName()}.${value.name()}")
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
            val migrated: JsonObject = this.migrateLegacyState(gson)
            if (migrated != null) {
               return LoadResult(migrated, true)
            }
         }

         return LoadResult(null, false)
      }
   }

   private fun migrateLegacyState(gson: Gson): JsonObject? {
      val legacyPath: Path = Path.of("config", "jooonreimagined_overlays.json")
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
               val migrated: JsonObject = JsonObject()
               val var11: Window = MinecraftClient.method_1551().method_22683()
               val centerX: Int = (if (var11.method_4486() > 0) var11.method_4486() else 960) / 2
               val centerY: Int = (if (var11.method_4502() > 0) var11.method_4502() else 540) / 2
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

   public fun copy(
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

   public override fun toString(): String {
      return "ConfigDefinition(modId=${this.modId}, configClass=${this.configClass}, title=${this.title}, translations=${this.translations}, categories=${this.categories}, entries=${this.entries}, filePath=${this.filePath})"
   }

   public override fun hashCode(): Int {
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

   public override operator fun equals(other: Any?): Boolean {
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

   @JvmStatic
   fun `migrateLegacyState$migrateCentered`(
      overlays: JsonObject, migrated: JsonObject, centerX: Int, centerY: Int, oldKey: java.lang.String, newPrefix: java.lang.String
   ) {
      val var10000: JsonObject = overlays.getAsJsonObject(oldKey)
      if (var10000 != null) {
         val var9: JsonElement = var10000.get("x")
         if (var9 != null) {
            val absoluteX: Int = var9.getAsInt()
            val var10: JsonElement = var10000.get("y")
            if (var10 != null) {
               val absoluteY: Int = var10.getAsInt()
               migrated.addProperty("$newPrefixX", absoluteX - centerX)
               migrated.addProperty("$newPrefixY", absoluteY - centerY)
               var var10001: java.lang.String = "$newPrefixMovable"
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
   public companion object {
      public fun build(modId: String, configClass: Class<*>): ConfigDefinition {
         val translations: java.util.Map = this.loadTranslations(modId)
         val entries: java.util.List = ArrayList()
         val categories: LinkedHashMap = LinkedHashMap()
         val configCategories: java.util.Iterator = ArrayIteratorKt.iterator(configClass.getDeclaredFields())

         while (configCategories.hasNext()) {
            val `$this$map$iv`: Field = configCategories.next() as Field
            if (Modifier.isStatic(`$this$map$iv`.getModifiers()) && !`$this$map$iv`.isSynthetic()) {
               `$this$map$iv`.setAccessible(true)
               val `$i$f$map`: Comment = `$this$map$iv`.getAnnotation(Comment.class)
               if (`$i$f$map` != null) {
                  val var10000: ConfigCommentNode = ConfigCommentNode
                  val var10002: java.lang.String = `$this$map$iv`.getName()
                  val var10003: java.lang.String = `$i$f$map`.category
                  val var10004: Boolean = `$i$f$map`.centered
                  var var10005: java.lang.String = translations.get("$modId.midnightconfig.${`$this$map$iv`.getName()}") as java.lang.String
                  if (var10005 == null) {
                     var10005 = `$this$map$iv`.getName()
                     var10005 = JooonConfigManagerKt.access$prettifyName(var10005)
                  }

                  var10000./* $VF: Unable to resugar constructor */<init>(var10002, var10003, var10004, var10005)
                  if (!StringsKt.isBlank(`$i$f$map`.category)) {
                     val `destination$iv$iv`: java.util.Map = categories
                     val `$i$f$mapTo`: Any = `$i$f$map`.category
                     val `item$iv$iv`: Any = `destination$iv$iv`.get(`$i$f$mapTo`)
                     val var69: Any
                     if (`item$iv$iv` == null) {
                        val var58: Any = ArrayList()
                        `destination$iv$iv`.put(`$i$f$mapTo`, var58)
                        var69 = var58
                     } else {
                        var69 = `item$iv$iv`
                     }

                     (var69 as java.util.Collection).add(var10000)
                  }
               }

               val var70: Entry = `$this$map$iv`.getAnnotation(Entry.class)
               if (var70 != null) {
                  var var71: Field = `$this$map$iv`
                  var var46: java.lang.String = var70.category
                  var var10001: java.lang.CharSequence
                  if (StringsKt.isBlank(var46)) {
                     var10001 = null
                     var71 = `$this$map$iv`
                  } else {
                     var10001 = var46
                  }

                  var10001 = var10001
                  var var76: java.lang.String = translations.get("$modId.midnightconfig.${`$this$map$iv`.getName()}") as java.lang.String
                  if (var76 == null) {
                     var76 = `$this$map$iv`.getName()
                     var76 = JooonConfigManagerKt.access$prettifyName(var76)
                  }

                  val var78: java.lang.String = translations.get("$modId.midnightconfig.${`$this$map$iv`.getName()}.tooltip") as java.lang.String
                  val var80: ConfigControlKind = this.resolveControlKind(`$this$map$iv`, var70)
                  val var83: java.lang.Double = if (var70.isSlider) var70.min else null
                  val var10006: java.lang.Double = if (var70.isSlider) var70.max else null
                  val var84: java.util.List
                  if (`$this$map$iv`.getType().isEnum()) {
                     val var10007: Array<Any> = `$this$map$iv`.getType().getEnumConstants()
                     val var59: java.util.Collection = ArrayList(var10007.length)

                     for (`item$iv$ivx` in var10007) {
                        var59.add(`item$iv$ivx` as java.lang.Enum)
                     }

                     var84 = var59 as java.util.List
                  } else {
                     var84 = CollectionsKt.emptyList()
                  }

                  val var44: ConfigEntryNode = ConfigEntryNode(var71, var10001, var76, var78, var80, var83, var10006, var84)
                  entries.add(var44)
                  var46 = var44.category
                  if (var46 != null && !StringsKt.isBlank(var46)) {
                     val var53: java.util.Map = categories
                     val var60: Any = categories.get(var46)
                     val var72: Any
                     if (var60 == null) {
                        val var63: Any = ArrayList()
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
            val var65: java.lang.String = var57.getKey() as java.lang.String
            val var66: java.util.List = var57.getValue() as java.util.List
            val var73: ConfigCategory = ConfigCategory
            var var79: java.lang.String = translations.get("$modId.midnightconfig.category.$var65") as java.lang.String
            if (var79 == null) {
               var79 = JooonConfigManagerKt.access$prettifyName(var65)
            }

            var73./* $VF: Unable to resugar constructor */<init>(var65, var79, CollectionsKt.toList(var66))
            var45.add(var73)
         }

         val var38: java.util.List = var45 as java.util.List
         val var74: ConfigDefinition = ConfigDefinition
         var var81: java.lang.String = translations.get("$modId.midnightconfig.title") as java.lang.String
         if (var81 == null) {
            var81 = JooonConfigManagerKt.access$prettifyName(modId)
         }

         val var10008: Path = Path.of("config", "$modId.json")
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
               val var16: java.lang.String = (`item$iv$iv` as java.lang.Enum).name().toUpperCase(Locale.ROOT)
               `destination$iv$iv`.add(var16)
            }

            val names: java.util.Set = CollectionsKt.toSet(`destination$iv$iv` as java.util.List)
            var10000 = if (names.contains("IDLE") && names.contains("CLICK")) ConfigControlKind.ACTION else ConfigControlKind.ENUM
         } else {
            var10000 = if (!(field.getType() == java.lang.Boolean::class.javaPrimitiveType) && !(field.getType() == java.lang.Boolean::class.javaObjectType))
               (
                  if (entry.isSlider)
                     ConfigControlKind.SLIDER
                     else
                     (if (field.getType() == java.lang.String::class.java) ConfigControlKind.TEXT else ConfigControlKind.ENUM)
               )
               else
               ConfigControlKind.TOGGLE
            }

         return var10000
      }

      private fun loadTranslations(modId: String): Map<String, String> {
         val var10000: InputStream = JooonConfigManager.class.getResourceAsStream("/assets/$modId/lang/en_us.json")
         if (var10000 != null) {
            val var3: Closeable = var10000
            var var4: java.lang.Throwable = null

            try {
               val var7: InputStream = var3 as InputStream
               val var40: Charset = StandardCharsets.UTF_8
               val var37: Closeable = InputStreamReader(var7, var40)
               var var38: java.lang.Throwable = null

               try {
                  val var41: java.util.Set = JsonParser.parseReader(var37 as InputStreamReader).getAsJsonObject().entrySet()
                  val `$this$associate$iv`: java.lang.Iterable = var41
                  val `destination$iv$iv`: java.util.Map = LinkedHashMap(
                     RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(var41, 10)), 16)
                  )

                  for (`element$iv$iv` in `$this$associate$iv`) {
                     val var39: Pair = TuplesKt.to(
                        (`element$iv$iv` as java.util.Map.Entry).getKey(), ((`element$iv$iv` as java.util.Map.Entry).getValue() as JsonElement).getAsString()
                     )
                     `destination$iv$iv`.put(var39.getFirst(), var39.getSecond())
                  }

                  return `destination$iv$iv`
               } catch (var33: java.lang.Throwable) {
                  var38 = var33
                  throw var33
               } finally {
                  CloseableKt.closeFinally(var37, var38)
               }
            } catch (var35: java.lang.Throwable) {
               var4 = var35
               throw var35
            } finally {
               CloseableKt.closeFinally(var3, var4)
            }
         } else {
            return MapsKt.emptyMap()
         }
      }
   }
}
