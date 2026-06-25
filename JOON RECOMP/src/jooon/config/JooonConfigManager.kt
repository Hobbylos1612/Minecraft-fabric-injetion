package jooon.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.LinkedHashMap
import jooon.config.ui.JooonConfigScreen
import jooon.features.dungeons.DBDisplay
import jooon.features.dungeons.map.DungeonMapFeature
import jooon.features.jerry.MayorDisplay
import jooon.features.other.WitherShieldOverlay
import jooon.features.slayers.SlayerHPDisplay
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.gui.screen.Screen

@SourceDebugExtension(["SMAP\nJooonConfigManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonConfigManager.kt\njooon/config/JooonConfigManager\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,480:1\n1#2:481\n*E\n"])
public object JooonConfigManager {
   private final val gson: Gson
   private final val definitions: LinkedHashMap<String, ConfigDefinition> = LinkedHashMap()

   public fun init(modId: String, configClass: Class<*>) {
      val definition: ConfigDefinition = ConfigDefinition.Companion.build(modId, configClass)
      definitions.put(modId, definition)
      definition.load(gson)
   }

   public fun write(modId: String) {
      val var10000: ConfigDefinition = definitions.get(modId)
      if (var10000 != null) {
         var10000.write(gson)
         this.notifyConfigWritten(modId)
      }
   }

   fun getScreen(parent: Screen?, modId: java.lang.String): Screen {
      val var10000: ConfigDefinition = definitions.get(modId)
      if (var10000 == null) {
         throw IllegalStateException(("Config '$modId' has not been initialized.").toString())
      } else {
         JooonConfigScreen(parent, var10000) as Screen
      }
   }

   private fun notifyConfigWritten(modId: String) {
      if (modId == "jooonreimagined") {
         var var2: JooonConfigManager = this

         try {
            var var14: JooonConfigManager = var2
            WitherShieldOverlay.INSTANCE.onConfigChanged()
            var14 = (JooonConfigManager)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var9: java.lang.Throwable) {
            val `$this$notifyConfigWritten_u24lambda_u244`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var9))
         }

         var2 = this

         try {
            var var17: JooonConfigManager = var2
            DBDisplay.INSTANCE.onConfigChanged()
            var17 = (JooonConfigManager)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var8: java.lang.Throwable) {
            val var16: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8))
         }

         var2 = this

         try {
            var var20: JooonConfigManager = var2
            SlayerHPDisplay.INSTANCE.onConfigChanged()
            var20 = (JooonConfigManager)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var7: java.lang.Throwable) {
            val var19: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7))
         }

         var2 = this

         try {
            var var23: JooonConfigManager = var2
            MayorDisplay.INSTANCE.onConfigChanged()
            var23 = (JooonConfigManager)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var6: java.lang.Throwable) {
            val var22: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var6))
         }

         var2 = this

         try {
            var var26: JooonConfigManager = var2
            DungeonMapFeature.INSTANCE.onConfigChanged()
            var26 = (JooonConfigManager)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var5: java.lang.Throwable) {
            val var25: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
         }
      }
   }

   @JvmStatic
   fun {
      val var10000: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
      gson = var10000
   }
}
