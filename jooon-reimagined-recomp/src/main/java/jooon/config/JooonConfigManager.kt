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
import net.minecraft.client.gui.screen.Screen

object JooonConfigManager {
   private val gson: Gson
   private val definitions: LinkedHashMap<String, ConfigDefinition> = LinkedHashMap()

   fun init(modId: String, configClass: Class<*>) {

      definitions.put(modId, definition)
      definition.load(gson)
   }

   fun write(modId: String) {

      if (var10000 != null) {
         var10000.write(gson)
         this.notifyConfigWritten(modId)
      }
   }

   fun getScreen(parent: Screen?, modId: String): Screen {

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
            WitherShieldOverlay.onConfigChanged()
            var14 = Result(Unit)
         } catch (var9: java.lang.Throwable) {
            val `this24lambda_u244`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var9))
         }

         var2 = this

         try {
            var var17: JooonConfigManager = var2
            DBDisplay.onConfigChanged()
            var17 = Result(Unit)
         } catch (var8: java.lang.Throwable) {

         }

         var2 = this

         try {
            var var20: JooonConfigManager = var2
            SlayerHPDisplay.onConfigChanged()
            var20 = Result(Unit)
         } catch (var7: java.lang.Throwable) {

         }

         var2 = this

         try {
            var var23: JooonConfigManager = var2
            MayorDisplay.onConfigChanged()
            var23 = Result(Unit)
         } catch (var6: java.lang.Throwable) {

         }

         var2 = this

         try {
            var var26: JooonConfigManager = var2
            DungeonMapFeature.onConfigChanged()
            var26 = Result(Unit)
         } catch (var5: java.lang.Throwable) {

         }
      }
   }

   
   fun {

      gson = var10000
   }
}
