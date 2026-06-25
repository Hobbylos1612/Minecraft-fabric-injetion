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

object ConfigButtonHandler {
   fun initialize() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.dbDisplayMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.dbDisplayMoveButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            handleDBDisplayMoveButton()
         }

         if (Config.witherShieldMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.witherShieldMoveButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            handleWitherShieldMoveButton()
         }

         if (Config.fishingMeleeOpenGui === Config.Companion.ButtonAction.CLICK) {
            FishingMeleeScreen.open()
            Config.fishingMeleeOpenGui = Config.Companion.ButtonAction.IDLE
            ConfigFlush.flush()
         }

         if (Config.fishingHudMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.fishingHudMoveButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            handleFishingHudMoveButton()
         }

         if (Config.slayerHPDisplayMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.slayerHPDisplayMoveButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            handleSlayerHPDisplayMoveButton()
         }

         if (Config.dungeonMapMoveButton === Config.Companion.ButtonAction.CLICK) {
            Config.dungeonMapMoveButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            handleDungeonMapMoveButton()
         }

         if (Config.autoVisitorConfigButton === Config.Companion.AutoVisitorConfigAction.CLICK) {
            Config.autoVisitorConfigButton = Config.Companion.AutoVisitorConfigAction.IDLE
            flushConfig()
            AutoVisitor.openSetupFromConfig(MinecraftClient.getInstance().currentScreen)
         }

         if (Config.autoVisitorResetTempButton === Config.Companion.AutoVisitorResetAction.CLICK) {
            Config.autoVisitorResetTempButton = Config.Companion.AutoVisitorResetAction.IDLE
            flushConfig()
            AutoVisitor.resetForTestingFromConfig()
         }

         if (Config.stridersurferFishingMacroMoveHudButton === Config.Companion.ButtonAction.CLICK) {
            Config.stridersurferFishingMacroMoveHudButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            StridersurferFishingMacro.openHudEditor()
         }

         if (Config.stridersurferFishingMacroKeybindButton === Config.Companion.StridersurferKeybindAction.OPENING) {
            Config.stridersurferFishingMacroKeybindButton = Config.Companion.StridersurferKeybindAction.OPEN
            flushConfig()
            StridersurferFishingMacro.openKeybindMenu()
         }

         if (Config.beachBallerMoveHudButton === Config.Companion.ButtonAction.CLICK) {
            Config.beachBallerMoveHudButton = Config.Companion.ButtonAction.IDLE
            flushConfig()
            BeachBaller.openHudEditor()
         }
      })
   }

   private fun flushConfig() {
      try {
         JooonConfigManager.write("jooonreimagined")
      } catch (var2: Exception) {
      }
   }

   private fun flushState() {
      try {
         JooonConfigManager.write("jooonreimagined_state")
      } catch (var2: Exception) {
      }
   }

   private fun handleDBDisplayMoveButton() {
      if (!PersistentState.dbDisplayMovable) {

         if (var3 != null) {
            var3.sendMessage(
               Text.literal("DBDisplay movable overlay is disabled! Enable it in config first.").formatted(Formatting.RED) as Text, false
            )
         }
      } else {


         if (overlay != null) {
            var10000.execute({ 
               `$overlay`.openPositioningGUI()
            })
         } else if (var10000.player != null) {
            var10000.player
               .sendMessage(Text.literal("DBDisplay overlay not found! Try enabling it first.").formatted(Formatting.RED) as Text, false)
            }
      }
   }

   private fun handleWitherShieldMoveButton() {
      if (!PersistentState.witherShieldMovable) {

         if (var7 != null) {
            var7.sendMessage(
               Text.literal("Wither Shield movable overlay is disabled! Enable it in config first.").formatted(Formatting.RED) as Text, false
            )
         }
      } else {


         overlay.element = MovableOverlayManager.getOverlay("witherShieldOverlay")
         if (overlay.element == null) {

               .createOverlay(
                  "witherShieldOverlay", "Wither Shield", PersistentState.witherShieldX, PersistentState.witherShieldY, 160, var10000.textRenderer.fontHeight
               )
               var3.renderFunction = { ctx: DrawContext, x: Int, y: Int, var4: Float ->
               ctx.drawText(`$mc`.textRenderer, "Wither Shield", x, y, -1, false)
return Unit
            }
            var3.onPositionChanged = { x: Int, y: Int ->
               PersistentState.witherShieldX = x
               PersistentState.witherShieldY = y
               PersistentState.witherShieldInitDone = true
               flushState()
return Unit
            }
            overlay.element = var3
         }

         var10000.execute({ 
            (`$overlay`.element as MovableOverlay).openPositioningGUI()
         })
      }
   }

   private fun handleFishingHudMoveButton() {


      if (overlay != null) {
         overlay.setPositionSilently(PersistentState.fishingHudX, PersistentState.fishingHudY)
         var10000.execute({ 
            `$overlay`.openPositioningGUI()
         })
      } else if (var10000.player != null) {
         var10000.player
            .sendMessage(
               Text.literal("Fishing HUD overlay not ready. Please enter a world or enable Auto Fishing.").formatted(Formatting.RED) as Text,
return false
            )
         }
   }

   private fun handleSlayerHPDisplayMoveButton() {
      if (!PersistentState.slayerHPDisplayMovable) {

         if (var7 != null) {
            var7.sendMessage(
               Text.literal("Slayer HP overlay is disabled! Enable movable overlay first.").formatted(Formatting.RED) as Text, false
            )
         }
      } else {


         overlay.element = MovableOverlayManager.getOverlay("slayerHP")
         if (overlay.element == null) {

               .createOverlay("slayerHP", "(02:59) Atoned Horror 9.8M❤", PersistentState.slayerHPDisplayX, PersistentState.slayerHPDisplayY, 180, 12)
               var3.renderFunction = { ctx: DrawContext, x: Int, y: Int, var4: Float ->
               ctx.drawTextWithShadow(`$mc`.textRenderer, "(02:59) Atoned Horror 9.8M❤", x, y, -1)
return Unit
            }
            var3.onPositionChanged = { x: Int, y: Int ->
               PersistentState.slayerHPDisplayX = x
               PersistentState.slayerHPDisplayY = y
               PersistentState.slayerHPDisplayInitDone = true
               flushState()
return Unit
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

         if (var4 != null) {
            var4.sendMessage(
               Text.literal("Dungeon Map movable overlay is disabled! Enable it in config first.").formatted(Formatting.RED) as Text, false
            )
         }
      } else {


         if (overlay != null) {
            var10000.execute({ 
               `$overlay`.openPositioningGUI()
            })
         } else {

            if (var3 != null) {
               var3.sendMessage(
                  Text.literal("Dungeon Map overlay not ready. Please enter a world or toggle the feature.").formatted(Formatting.RED) as Text,
return false
               )
            }
         }
      }
   }
}
