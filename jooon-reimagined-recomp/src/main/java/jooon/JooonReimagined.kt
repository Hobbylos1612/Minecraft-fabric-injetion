package jooon

import jooon.commands.BZInstaSell
import jooon.commands.JerryOpenerCommand
import jooon.commands.JooonReimaginedGUI
import jooon.commands.PingCommand
import jooon.commands.RotateCommand
import jooon.config.Config
import jooon.config.ConfigButtonHandler
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.features.autoexperiments.SolverManager
import jooon.features.dojo.AutoDojo
import jooon.features.dojo.AutomaticDiscipline
import jooon.features.dojo.AutomaticSwiftness
import jooon.features.dojo.Clicker
import jooon.features.dojo.Control
import jooon.features.dojo.Discipline
import jooon.features.dojo.Force
import jooon.features.dojo.Mastery
import jooon.features.dungeons.AutoGFS
import jooon.features.dungeons.DBDisplay
import jooon.features.dungeons.DungeonESP
import jooon.features.dungeons.FastJoin
import jooon.features.dungeons.TerminatorClicker
import jooon.features.dungeons.map.DungeonMapFeature
import jooon.features.dungeons.solvers.BlazeSolver
import jooon.features.dungeons.solvers.BoulderSolver
import jooon.features.dungeons.solvers.CreeperBeamsSolver
import jooon.features.dungeons.solvers.IceFillSolver
import jooon.features.dungeons.solvers.IcePathSolver
import jooon.features.dungeons.solvers.TeleportMazeSolver
import jooon.features.dungeons.solvers.ThreeWeirdosSolver
import jooon.features.dungeons.solvers.TicTacToeSolver
import jooon.features.dungeons.solvers.TriviaSolver
import jooon.features.dungeons.solvers.WaterBoardSolver
import jooon.features.farming.AutoVisitor
import jooon.features.fishing.BazaarAutoSell
import jooon.features.fishing.FishingMeleeStore
import jooon.features.fishing.FunnyFishing
import jooon.features.galatea.StridersurferFishingMacro
import jooon.features.jerry.JerryAlert
import jooon.features.jerry.JerryBoxOpener
import jooon.features.jerry.JerryESP
import jooon.features.jerry.MayorDisplay
import jooon.features.minions.AutoMinion
import jooon.features.mirrorverse.DRSv2
import jooon.features.other.AutoCarnival
import jooon.features.other.AutoWardrobe
import jooon.features.other.BeachBaller
import jooon.features.other.FactoryHelper
import jooon.features.other.Fullbright
import jooon.features.other.Melody
import jooon.features.other.PetKeybinds
import jooon.features.other.WitherShieldOverlay
import jooon.features.slayers.AutoSoulcry
import jooon.features.slayers.AutoWand
import jooon.features.slayers.AutoZombieSword
import jooon.features.slayers.SlayerClicker
import jooon.features.slayers.SlayerESP
import jooon.features.slayers.SlayerHPDisplay
import jooon.util.OverlayScreen
import jooon.util.PingUtil
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text

class JooonReimagined : ClientModInitializer {
   override fun onInitializeClient() {
      JooonConfigManager.init("jooonreimagined", Config::class.java)
      JooonConfigManager.init("jooonreimagined_state", PersistentState::class.java)
      ConfigButtonHandler.initialize()
      JooonReimaginedGUI.onInitializeClient()
      Melody.onInitializeClient()
      AutoDojo.init()
      OverlayScreen.onInitializeClient()
      AutoMinion.init()
      FactoryHelper.init()
      Clicker.onInitializeClient()
      Control.onInitializeClient()
      Mastery.onInitializeClient()
      Force.onInitializeClient()
      Discipline.init()
      AutomaticDiscipline.init()
      AutomaticSwiftness.init()
      AutoWardrobe.onInitializeClient()
      PetKeybinds.onInitializeClient()
      Fullbright.onInitializeClient()
      WitherShieldOverlay.onInitializeClient()
      PingCommand.register()
      PingUtil.onInitializeClient()
      BZInstaSell.register()
      FunnyFishing.onInitializeClient()
      BazaarAutoSell.init()
      RotateCommand.register()
      AutoCarnival.register()
      StridersurferFishingMacro.init()
      DungeonESP.onInitializeClient()
      JerryESP.onInitializeClient()
      JerryAlert.onInitializeClient()
      JerryBoxOpener.onInitializeClient()
      MayorDisplay.onInitializeClient()
      DBDisplay.onInitializeClient()
      JerryOpenerCommand.register()
      FastJoin.register()
      AutoGFS.init()
      SlayerESP.onInitializeClient()
      SlayerClicker.onInitializeClient()
      TerminatorClicker.onInitializeClient()
      SlayerHPDisplay.onInitializeClient()
      AutoSoulcry.onInitializeClient()
      AutoWand.onInitializeClient()
      AutoZombieSword.onInitializeClient()
      FishingMeleeStore.load()
      SolverManager.init()
      DRSv2.init()
      BeachBaller.init()
      AutoVisitor.init()
      DungeonMapFeature.init()
      BlazeSolver.onInitializeClient()
      BoulderSolver.onInitializeClient()
      CreeperBeamsSolver.onInitializeClient()
      IceFillSolver.onInitializeClient()
      IcePathSolver.onInitializeClient()
      TeleportMazeSolver.onInitializeClient()
      ThreeWeirdosSolver.onInitializeClient()
      TicTacToeSolver.onInitializeClient()
      TriviaSolver.onInitializeClient()
      WaterBoardSolver.onInitializeClient()
   }

   companion object {
      const val PREFIX: String = "§a§lJooonReimagined §7» "
      const val MOD_NAME: String = "JooonReimagined"
      const val VERSION: String = "6.1"

      val PREFIX_CLEAN: String
         get() = PREFIX.replace("Â", "")

      var currentGui: Screen? = null

      fun getMc(): MinecraftClient = MinecraftClient.getInstance()

      fun getCurrentGui(): Screen? = currentGui

      fun setCurrentGui(gui: Screen?) {
         currentGui = gui
      }

      fun sendMessage(message: String) {
         getMc().player?.sendMessage(Text.literal("$PREFIX_CLEAN$message"), false)
      }
   }
}
