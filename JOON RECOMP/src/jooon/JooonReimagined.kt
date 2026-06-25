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

public class JooonReimagined : ClientModInitializer {
   @JvmStatic
   private Screen currentGui;

   public open fun onInitializeClient() {
      JooonConfigManager.INSTANCE.init("jooonreimagined", Config::class.java)
      JooonConfigManager.INSTANCE.init("jooonreimagined_state", PersistentState::class.java)
      ConfigButtonHandler.INSTANCE.initialize()
      JooonReimaginedGUI.INSTANCE.onInitializeClient()
      Melody.INSTANCE.onInitializeClient()
      AutoDojo.INSTANCE.init()
      OverlayScreen.INSTANCE.onInitializeClient()
      AutoMinion.INSTANCE.init()
      FactoryHelper.INSTANCE.init()
      Clicker.INSTANCE.onInitializeClient()
      Control.INSTANCE.onInitializeClient()
      Mastery.INSTANCE.onInitializeClient()
      Force.INSTANCE.onInitializeClient()
      Discipline.INSTANCE.init()
      AutomaticDiscipline.INSTANCE.init()
      AutomaticSwiftness.INSTANCE.init()
      AutoWardrobe.INSTANCE.onInitializeClient()
      PetKeybinds.INSTANCE.onInitializeClient()
      Fullbright.INSTANCE.onInitializeClient()
      WitherShieldOverlay.INSTANCE.onInitializeClient()
      PingCommand.INSTANCE.register()
      PingUtil.INSTANCE.onInitializeClient()
      BZInstaSell.INSTANCE.register()
      FunnyFishing.INSTANCE.onInitializeClient()
      BazaarAutoSell.INSTANCE.init()
      RotateCommand.INSTANCE.register()
      AutoCarnival.INSTANCE.register()
      StridersurferFishingMacro.INSTANCE.init()
      DungeonESP.INSTANCE.onInitializeClient()
      JerryESP.INSTANCE.onInitializeClient()
      JerryAlert.INSTANCE.onInitializeClient()
      JerryBoxOpener.INSTANCE.onInitializeClient()
      MayorDisplay.INSTANCE.onInitializeClient()
      DBDisplay.INSTANCE.onInitializeClient()
      JerryOpenerCommand.INSTANCE.register()
      FastJoin.INSTANCE.register()
      AutoGFS.INSTANCE.init()
      SlayerESP.INSTANCE.onInitializeClient()
      SlayerClicker.INSTANCE.onInitializeClient()
      TerminatorClicker.INSTANCE.onInitializeClient()
      SlayerHPDisplay.INSTANCE.onInitializeClient()
      AutoSoulcry.INSTANCE.onInitializeClient()
      AutoWand.INSTANCE.onInitializeClient()
      AutoZombieSword.INSTANCE.onInitializeClient()
      FishingMeleeStore.INSTANCE.load()
      SolverManager.INSTANCE.init()
      DRSv2.INSTANCE.init()
      BeachBaller.INSTANCE.init()
      AutoVisitor.INSTANCE.init()
      DungeonMapFeature.INSTANCE.init()
      BlazeSolver.INSTANCE.onInitializeClient()
      BoulderSolver.INSTANCE.onInitializeClient()
      CreeperBeamsSolver.INSTANCE.onInitializeClient()
      IceFillSolver.INSTANCE.onInitializeClient()
      IcePathSolver.INSTANCE.onInitializeClient()
      TeleportMazeSolver.INSTANCE.onInitializeClient()
      ThreeWeirdosSolver.INSTANCE.onInitializeClient()
      TicTacToeSolver.INSTANCE.onInitializeClient()
      TriviaSolver.INSTANCE.onInitializeClient()
      WaterBoardSolver.INSTANCE.onInitializeClient()
   }

   public companion object {
      public const val PREFIX: String = "§a§lJooonReimagined §7» "
      public const val MOD_NAME: String = "JooonReimagined"
      public const val VERSION: String = "6.1"

      public final val PREFIX_CLEAN: String
         public final get() {
            return StringsKt.replace$default("§a§lJooonReimagined §7» ", "Â", "", false, 4, null)
         }


      fun getMc(): MinecraftClient {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         var10000
      }

      fun getCurrentGui(): Screen? {
         JooonReimagined.currentGui
      }

      fun setCurrentGui(`<set-?>`: Screen?) {
         JooonReimagined.currentGui = `<set-?>`
      }

      public fun sendMessage(message: String) {
         val var10000: ClientPlayerEntity = this.getMc().field_1724
         if (var10000 != null) {
            var10000.method_7353(Text.method_43470("${this.PREFIX_CLEAN}$message") as Text, false)
         }
      }
   }
}
