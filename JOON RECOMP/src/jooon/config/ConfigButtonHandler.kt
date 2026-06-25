package jooon.config

import jooon.features.farming.AutoVisitor
import jooon.features.galatea.StridersurferFishingMacro
import jooon.features.other.BeachBaller
import jooon.gui.FishingMeleeScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import kotlin.jvm.internal.Ref.ObjectRef
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

public object ConfigButtonHandler {
   public fun initialize() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.dbDisplayMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.dbDisplayMoveButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            INSTANCE.handleDBDisplayMoveButton()
         }

         if (Config.witherShieldMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.witherShieldMoveButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            INSTANCE.handleWitherShieldMoveButton()
         }

         if (Config.fishingMeleeOpenGui === Config.Companion.ButtonAction.CLICK) {
            FishingMeleeScreen.INSTANCE.open()
            Config.fishingMeleeOpenGui = Config.Companion.ButtonAction.IDLE
            ConfigFlush.INSTANCE.flush()
         }

         if (Config.fishingHudMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.fishingHudMoveButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            INSTANCE.handleFishingHudMoveButton()
         }

         if (Config.slayerHPDisplayMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.slayerHPDisplayMoveButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            INSTANCE.handleSlayerHPDisplayMoveButton()
         }

         if (Config.dungeonMapMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.dungeonMapMoveButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            INSTANCE.handleDungeonMapMoveButton()
         }

         if (Config.autoVisitorConfigButton === Config.Companion.AutoVisitorConfigAction.CLICK) {
            Config.autoVisitorConfigButton = Config.Companion.AutoVisitorConfigAction.IDLE
            INSTANCE.flushConfig()
            AutoVisitor.INSTANCE.openSetupFromConfig(MinecraftClient.method_1551().field_1755)
         }

         if (Config.autoVisitorResetTempButton === Config.Companion.AutoVisitorResetAction.CLICK) {
            Config.autoVisitorResetTempButton = Config.Companion.AutoVisitorResetAction.IDLE
            INSTANCE.flushConfig()
            AutoVisitor.INSTANCE.resetForTestingFromConfig()
         }

         if (Config.stridersurferFishingMacroMoveHudButton === Config.Companion.ButtonAction.CLICK) {
            Config.stridersurferFishingMacroMoveHudButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            StridersurferFishingMacro.INSTANCE.openHudEditor()
         }

         if (Config.stridersurferFishingMacroKeybindButton === Config.Companion.StridersurferKeybindAction.OPENING) {
            Config.stridersurferFishingMacroKeybindButton = Config.Companion.StridersurferKeybindAction.OPEN
            INSTANCE.flushConfig()
            StridersurferFishingMacro.INSTANCE.openKeybindMenu()
         }

         if (Config.beachBallerMoveHudButton === Config.Companion.ButtonAction.CLICK) {
            Config.beachBallerMoveHudButton = Config.Companion.ButtonAction.IDLE
            INSTANCE.flushConfig()
            BeachBaller.INSTANCE.openHudEditor()
         }
      })
   }

   private fun flushConfig() {
      try {
         JooonConfigManager.INSTANCE.write("jooonreimagined")
      } catch (var2: Exception) {
      }
   }

   private fun flushState() {
      try {
         JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      } catch (var2: Exception) {
      }
   }

   private fun handleDBDisplayMoveButton() {
      if (!PersistentState.dbDisplayMovable) {
         val var3: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
         if (var3 != null) {
            var3.method_7353(
               Text.method_43470("DBDisplay movable overlay is disabled! Enable it in config first.").method_27692(Formatting.field_1061) as Text, false
            )
         }
      } else {
         val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("dbDisplay")
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         if (overlay != null) {
            var10000.execute({ 
               `$overlay`.openPositioningGUI()
            })
         } else if (var10000.field_1724 != null) {
            var10000.field_1724
               .method_7353(Text.method_43470("DBDisplay overlay not found! Try enabling it first.").method_27692(Formatting.field_1061) as Text, false)
            }
      }
   }

   private fun handleWitherShieldMoveButton() {
      if (!PersistentState.witherShieldMovable) {
         val var7: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
         if (var7 != null) {
            var7.method_7353(
               Text.method_43470("Wither Shield movable overlay is disabled! Enable it in config first.").method_27692(Formatting.field_1061) as Text, false
            )
         }
      } else {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         val overlay: ObjectRef = ObjectRef()
         overlay.element = MovableOverlayManager.INSTANCE.getOverlay("witherShieldOverlay")
         if (overlay.element == null) {
            val var3: MovableOverlay = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "witherShieldOverlay", "Wither Shield", PersistentState.witherShieldX, PersistentState.witherShieldY, 160, var10000.field_1772.field_2000
               )
               var3.renderFunction = { ctx: DrawContext, x: Int, y: Int, var4: Float ->
               ctx.method_51433(`$mc`.field_1772, "Wither Shield", x, y, -1, false)
               Unit.INSTANCE
            }
            var3.onPositionChanged = { x: Int, y: Int ->
               PersistentState.witherShieldX = x
               PersistentState.witherShieldY = y
               PersistentState.witherShieldInitDone = true
               INSTANCE.flushState()
               Unit.INSTANCE
            }
            overlay.element = var3
         }

         var10000.execute({ 
            (`$overlay`.element as MovableOverlay).openPositioningGUI()
         })
      }
   }

   private fun handleFishingHudMoveButton() {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("fishingHud")
      if (overlay != null) {
         overlay.setPositionSilently(PersistentState.fishingHudX, PersistentState.fishingHudY)
         var10000.execute({ 
            `$overlay`.openPositioningGUI()
         })
      } else if (var10000.field_1724 != null) {
         var10000.field_1724
            .method_7353(
               Text.method_43470("Fishing HUD overlay not ready. Please enter a world or enable Auto Fishing.").method_27692(Formatting.field_1061) as Text,
               false
            )
         }
   }

   private fun handleSlayerHPDisplayMoveButton() {
      if (!PersistentState.slayerHPDisplayMovable) {
         val var7: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
         if (var7 != null) {
            var7.method_7353(
               Text.method_43470("Slayer HP overlay is disabled! Enable movable overlay first.").method_27692(Formatting.field_1061) as Text, false
            )
         }
      } else {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         val overlay: ObjectRef = ObjectRef()
         overlay.element = MovableOverlayManager.INSTANCE.getOverlay("slayerHP")
         if (overlay.element == null) {
            val var3: MovableOverlay = MovableOverlayManager.INSTANCE
               .createOverlay("slayerHP", "(02:59) Atoned Horror 9.8M❤", PersistentState.slayerHPDisplayX, PersistentState.slayerHPDisplayY, 180, 12)
               var3.renderFunction = { ctx: DrawContext, x: Int, y: Int, var4: Float ->
               ctx.method_25303(`$mc`.field_1772, "(02:59) Atoned Horror 9.8M❤", x, y, -1)
               Unit.INSTANCE
            }
            var3.onPositionChanged = { x: Int, y: Int ->
               PersistentState.slayerHPDisplayX = x
               PersistentState.slayerHPDisplayY = y
               PersistentState.slayerHPDisplayInitDone = true
               INSTANCE.flushState()
               Unit.INSTANCE
            }
            overlay.element = var3
         }

         var10000.execute({ 
            (`$overlay`.element as MovableOverlay).openPositioningGUI()
         })
      }
   }

   private fun handleDungeonMapMoveButton() {
      if (!PersistentState.dungeonMapMovable) {
         val var4: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
         if (var4 != null) {
            var4.method_7353(
               Text.method_43470("Dungeon Map movable overlay is disabled! Enable it in config first.").method_27692(Formatting.field_1061) as Text, false
            )
         }
      } else {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         val overlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("dungeonMap")
         if (overlay != null) {
            var10000.execute({ 
               `$overlay`.openPositioningGUI()
            })
         } else {
            val var3: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
            if (var3 != null) {
               var3.method_7353(
                  Text.method_43470("Dungeon Map overlay not ready. Please enter a world or toggle the feature.").method_27692(Formatting.field_1061) as Text,
                  false
               )
            }
         }
      }
   }
}
