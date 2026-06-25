package jooon.features.fishing

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.nio.file.Files
import java.nio.file.Path
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import jooon.config.Config
import org.jetbrains.annotations.NotNull

object FishingMeleeStore {
   private val gson: Gson = Gson()
   private val path: Path
   private val enabledMap: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap()

   fun load() {
      enabledMap.clear()
      if (Files.exists(path)) {


         var `this24lambda_u240`: FishingMeleeStore
         try {
            `this24lambda_u240` = var3
            `this24lambda_u240` = Result(Files.readString(path))
         } catch (var8: java.lang.Throwable) {
            `this24lambda_u240` = Result(ResultKt.createFailure(var8))
         }

            if (Result.isFailure/* $VF was: isFailure-impl */(`this24lambda_u240`)) null else `this24lambda_u240`
         ) as String
         if (var10000 != null) {

            `this24lambda_u240` = this

            var var13: FishingMeleeStore
            try {
               var13 = `this24lambda_u240`
               var13 = Result(
                  gson.fromJson(raw, FishingMeleeStore.Root::class.java) as FishingMeleeStore.Root
               )
            } catch (var7: java.lang.Throwable) {
               var13 = Result(ResultKt.createFailure(var7))
            }

            val var15: FishingMeleeStore.Root = (if (Result.isFailure/* $VF was: isFailure-impl */(var13)) null else var13) as FishingMeleeStore.Root
            if (var15 != null) {
               enabledMap.putAll(var15.enabled)
            }
         }
      }
   }

   fun save() {
      val root: FishingMeleeStore.Root = FishingMeleeStore.Root(toMutableMap(enabledMap))


      try {
         var var6: FishingMeleeStore = var2
         Files.createDirectories(path.getParent())
         var6 = Result(Files.writeString(path, gson.toJson(root)))
      } catch (var5: java.lang.Throwable) {
         val `this24lambda_u242`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }
   }

   fun setEnabled(mobName: String, on: Boolean) {
      enabledMap.put(this.normalize(mobName), on)
   }

   fun toggle(mobName: String): Boolean {



      enabledMap.put(k, now)
      return now
   }

   fun isEnabled(mobName: String): Boolean {
      return enabledMap.get(this.normalize(mobName)) == true
   }

   fun shouldAttack(mobName: String): Boolean {
      return Config.fishingMeleeAllMobs || this.isEnabled(mobName)
   }

   fun getAllEnabled(): Set<String> {
      val `this$iv`: java.util.Map = enabledMap
      val `result$iv`: LinkedHashMap = LinkedHashMap()

      for (`entry$iv` in `this$iv`.entrySet()) {
         if (`entry$iv`.getValue() as Boolean) {
            `result$iv`.put(`entry$iv`.getKey(), `entry$iv`.getValue())
         }
      }

      return `result$iv`.keySet()
   }

   fun normalize(name: String): String {

      return Regex("\\s+")
         .replace(
            trim(
                  Regex("[^a-z0-9 ]")
                     .replace(replace$default(var10000.replace("’", ""), "'", "", false, 4, null), " ")
               )
               .toString(),
            "_"
         )
      }

   
   fun {

      path = var10000
   }

   private data class Root(enabled: MutableMap<String, Boolean> = LinkedHashMap() as java.util.Map) {
      @SerializedName("enabled")
      @NotNull
      val enabled: MutableMap<String, Boolean>

      init {
         this.enabled = enabled
      }

      public operator fun component1(): MutableMap<String, Boolean> {
         return this.enabled
      }

      fun copy(enabled: MutableMap<String, Boolean> = this.enabled): jooon.features.fishing.FishingMeleeStore.Root {
         return FishingMeleeStore.Root(enabled)
      }

      override fun toString(): String {
         return "Root(enabled=${this.enabled})"
      }

      override fun hashCode(): Int {
         return this.enabled.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
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
