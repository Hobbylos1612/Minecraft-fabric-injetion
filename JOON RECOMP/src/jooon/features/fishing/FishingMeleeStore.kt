package jooon.features.fishing

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.nio.file.Files
import java.nio.file.Path
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import jooon.config.Config
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nFishingMeleeStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FishingMeleeStore.kt\njooon/features/fishing/FishingMeleeStore\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,70:1\n1#2:71\n494#3,7:72\n*S KotlinDebug\n*F\n+ 1 FishingMeleeStore.kt\njooon/features/fishing/FishingMeleeStore\n*L\n60#1:72,7\n*E\n"])
public object FishingMeleeStore {
   private final val gson: Gson = Gson()
   private final val path: Path
   private final val enabledMap: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap()

   public fun load() {
      enabledMap.clear()
      if (Files.exists(path)) {
         val var3: FishingMeleeStore = this

         var `$this$load_u24lambda_u240`: FishingMeleeStore
         try {
            `$this$load_u24lambda_u240` = var3
            `$this$load_u24lambda_u240` = (FishingMeleeStore)Result.constructor_impl/* $VF was: constructor-impl */(Files.readString(path))
         } catch (var8: java.lang.Throwable) {
            `$this$load_u24lambda_u240` = (FishingMeleeStore)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8))
         }

         val var10000: java.lang.String = (
            if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$load_u24lambda_u240`)) null else `$this$load_u24lambda_u240`
         ) as java.lang.String
         if (var10000 != null) {
            val raw: java.lang.String = var10000
            `$this$load_u24lambda_u240` = this

            var var13: FishingMeleeStore
            try {
               var13 = `$this$load_u24lambda_u240`
               var13 = (FishingMeleeStore)Result.constructor_impl/* $VF was: constructor-impl */(
                  gson.fromJson(raw, FishingMeleeStore.Root.class) as FishingMeleeStore.Root
               )
            } catch (var7: java.lang.Throwable) {
               var13 = (FishingMeleeStore)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7))
            }

            val var15: FishingMeleeStore.Root = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var13)) null else var13) as FishingMeleeStore.Root
            if (var15 != null) {
               enabledMap.putAll(var15.enabled)
            }
         }
      }
   }

   public fun save() {
      val root: FishingMeleeStore.Root = FishingMeleeStore.Root(MapsKt.toMutableMap(enabledMap))
      val var2: FishingMeleeStore = this

      try {
         var var6: FishingMeleeStore = var2
         Files.createDirectories(path.getParent())
         var6 = (FishingMeleeStore)Result.constructor_impl/* $VF was: constructor-impl */(Files.writeString(path, gson.toJson(root)))
      } catch (var5: java.lang.Throwable) {
         val `$this$save_u24lambda_u242`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }
   }

   public fun setEnabled(mobName: String, on: Boolean) {
      enabledMap.put(this.normalize(mobName), on)
   }

   public fun toggle(mobName: String): Boolean {
      val k: java.lang.String = this.normalize(mobName)
      val var10000: java.lang.Boolean = enabledMap.get(k)
      val now: Boolean = var10000 == null || !var10000
      enabledMap.put(k, now)
      return now
   }

   public fun isEnabled(mobName: String): Boolean {
      return enabledMap.get(this.normalize(mobName)) == true
   }

   public fun shouldAttack(mobName: String): Boolean {
      return Config.fishingMeleeAllMobs || this.isEnabled(mobName)
   }

   public fun getAllEnabled(): Set<String> {
      val `$this$filterValues$iv`: java.util.Map = enabledMap
      val `result$iv`: LinkedHashMap = LinkedHashMap()

      for (`entry$iv` in `$this$filterValues$iv`.entrySet()) {
         if (`entry$iv`.getValue() as java.lang.Boolean) {
            `result$iv`.put(`entry$iv`.getKey(), `entry$iv`.getValue())
         }
      }

      return `result$iv`.keySet()
   }

   public fun normalize(name: String): String {
      val var10000: java.lang.String = name.toLowerCase(Locale.ROOT)
      return Regex("\\s+")
         .replace(
            StringsKt.trim(
                  Regex("[^a-z0-9 ]")
                     .replace(StringsKt.replace$default(StringsKt.replace$default(var10000, "’", "", false, 4, null), "'", "", false, 4, null), " ")
               )
               .toString(),
            "_"
         )
      }

   @JvmStatic
   fun {
      val var10000: Path = Path.of("config/jooonreimagined_fishing.json")
      path = var10000
   }

   private data class Root(enabled: MutableMap<String, Boolean> = LinkedHashMap() as java.util.Map) {
      @SerializedName("enabled")
      @NotNull
      public final val enabled: MutableMap<String, Boolean>

      init {
         this.enabled = enabled
      }

      public operator fun component1(): MutableMap<String, Boolean> {
         return this.enabled
      }

      public fun copy(enabled: MutableMap<String, Boolean> = this.enabled): jooon.features.fishing.FishingMeleeStore.Root {
         return FishingMeleeStore.Root(enabled)
      }

      public override fun toString(): String {
         return "Root(enabled=${this.enabled})"
      }

      public override fun hashCode(): Int {
         return this.enabled.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is FishingMeleeStore.Root && this.enabled == (other as FishingMeleeStore.Root).enabled
         }
      }

      fun Root() {
         this(null, 1, null)
      }
   }
}
