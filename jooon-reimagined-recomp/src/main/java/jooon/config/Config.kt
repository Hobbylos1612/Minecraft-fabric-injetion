package jooon.config

import kotlin.enums.EnumEntries
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class Config {
   companion object {
      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var melodySplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      var enableAutoMelody: Boolean
         private set

      @Entry(category = "automation", min = 0.0, max = 500.0, isSlider = true)
      @JvmField
      var clickDelayMs: Int
         private set

      @Comment(category = "dojo", centered = true)
      @JvmField
      @Nullable
      var dojoSplitter: Splitter?
         private set

      @Entry(category = "dojo")
      @JvmField
      var autoDojoEnabled: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      var dojoClickerEnabled: Boolean
         private set

      @Entry(category = "dojo", min = 6.0, max = 16.0, isSlider = true)
      @JvmField
      var dojoClickerCps: Int
         private set

      @JvmField
      var controlPing: Int
         private set

      @Entry(category = "dojo")
      @JvmField
      var forcePreventNegative: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      var forceESP: Boolean
         private set

      @Comment(category = "dojo", centered = true)
      @JvmField
      @Nullable
      var fullyAutomaticDojoSplitter: Splitter?
         private set

      @JvmField
      var fullyAutomaticForce: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      var fullyAutomaticControl: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      var fullyAutomaticMastery: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      var fullyAutomaticDiscipline: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      var fullyAutomaticSwiftness: Boolean
         private set

      @JvmField
      var fullyAutomaticStamina: Boolean
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var experimentsSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      var autoExperiments: Boolean
         private set

      @Entry(category = "automation", min = 2.0, max = 20.0, isSlider = true)
      @JvmField
      var autoExperimentsTickDelay: Int
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      var wardrobeSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      var autoWardrobeEnabled: Boolean
         private set

      @Entry(category = "general", min = 50.0, max = 500.0, isSlider = true)
      @JvmField
      var autoWardrobeDelay: Int
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      var petSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      var enablePetKeybinds: Boolean
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      var qolSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      var fullbright: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      var witherShieldOverlay: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      var witherShieldCompact: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      @NotNull
      var witherShieldMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      var carnivalSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      var autoCarnivalEnabled: Boolean
         private set

      @Entry(category = "general", min = 0.0, max = 500.0, isSlider = true)
      @JvmField
      var autoCarnivalPing: Int
         private set

      @JvmField
      var fishingEnabled: Boolean
         private set

      @Comment(category = "fishing", centered = true)
      @JvmField
      @Nullable
      var fishingKeybindComment: Splitter?
         private set

      @Entry(category = "fishing")
      @JvmField
      @NotNull
      var fishingHudMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "fishing", min = 250.0, max = 4000.0, isSlider = true)
      @JvmField
      var fishingRecastDelayMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 150.0, isSlider = true)
      @JvmField
      var fishingRecastJitterMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      var fishingWaterPreCatchMinMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      var fishingWaterPreCatchMaxMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      var fishingLavaPreCatchMinMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      var fishingLavaPreCatchMaxMs: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingRotate: Boolean
         private set

      @Entry(category = "fishing", min = 2000.0, max = 10000.0, isSlider = true)
      @JvmField
      var fishingRotateIntervalMs: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingAutoSell: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingTotem: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingAutoPowerOrb: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingAutoUmbrella: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      var enableGoldenFishCatch: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingIgnoreSquids: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      @NotNull
      var fishingKillingMode: jooon.config.Config.Companion.FishingKillMode
         private set

      @Entry(category = "fishing", min = 0.0, max = 2000.0, isSlider = true)
      @JvmField
      var funnyFishingAutoKillingDelay: Int
         private set

      @Entry(category = "fishing", min = 1.0, max = 30.0, isSlider = true)
      @JvmField
      var fishingSessionMinutes: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      var slugFishEnabled: Boolean
         private set

      @Entry(category = "fishing", min = 0.0, max = 25.0, isSlider = true)
      @JvmField
      var slugFishMinWaitSec: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingMeleeAllow: Boolean
         private set

      @Entry(category = "fishing", min = 1.0, max = 8.0, isSlider = true)
      @JvmField
      var fishingMeleeWeaponSlot: Int
         private set

      @Entry(category = "fishing", min = 5.0, max = 16.0, isSlider = true)
      @JvmField
      var fishingMeleeCps: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      @NotNull
      var fishingMeleeOpenGui: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "fishing")
      @JvmField
      var fishingMeleeAllMobs: Boolean
         private set

      @Comment(category = "galatea", centered = true)
      @JvmField
      @Nullable
      var galateaSplitter: Splitter?
         private set

      @Comment(category = "galatea", centered = true)
      @JvmField
      @Nullable
      var galateaKeybindComment: Splitter?
         private set

      var stridersurferFishingMacroEnabled: Boolean

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      var stridersurferFishingMacroKeybindButton: jooon.config.Config.Companion.StridersurferKeybindAction
         private set

      @Entry(category = "galatea", min = 2.0, max = 30.0, isSlider = true)
      @JvmField
      var stridersurferFishingMacroThreshold: Int
         private set

      @Entry(category = "galatea")
      @JvmField
      var stridersurferFishingMacroUseWhipInstead: Boolean
         private set

      @Entry(category = "galatea")
      @JvmField
      var stridersurferFishingMacroAutoPetSwap: Boolean
         private set

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      var stridersurferFishingMacroKillPetName: String
         private set

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      var stridersurferFishingMacroRecastPetName: String
         private set

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      var stridersurferFishingMacroMoveHudButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "dungeons", centered = true)
      @JvmField
      @Nullable
      var dungeonEspSplitter: Splitter?
         private set

      @Entry(category = "dungeons")
      @JvmField
      var dungeonESPEnabled: Boolean
         private set

      @JvmField
      var dungeonESPLineWidth: Int
         private set

      @Entry(category = "dungeons", isColor = true)
      @JvmField
      @NotNull
      var dungeonESPStarColor: String
         private set

      @Entry(category = "dungeons", isColor = true)
      @JvmField
      @NotNull
      var dungeonESPSAColor: String
         private set

      @Entry(category = "dungeons", isColor = true)
      @JvmField
      @NotNull
      var dungeonESPBatColor: String
         private set

      @Entry(category = "dungeons", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      var dungeonESPOpacityPct: Int
         private set

      @Entry(category = "dungeons")
      @JvmField
      var dungeonESPThroughWalls: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var dungeonESPPerfMode: Boolean
         private set

      @Comment(category = "dungeons", centered = true)
      @JvmField
      @Nullable
      var dungeonSplitter: Splitter?
         private set

      @Entry(category = "dungeons")
      @JvmField
      var terminatorClickerEnabled: Boolean
         private set

      @Entry(category = "dungeons", min = 5.0, max = 20.0, isSlider = true)
      @JvmField
      var terminatorClickerCps: Int
         private set

      @Entry(category = "dungeons")
      @JvmField
      var fastJoinDungeons: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var fastJoinMaster: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var fastJoinKuudra: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var autoGfsEnabled: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var autoGfsPearls: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var autoGfsSuperboom: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var autoGfsJerry: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var autoGfsDecoy: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var autoGfsInDungeonsOnly: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      var dbDisplayEnabled: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      @NotNull
      var dbDisplayMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      var jerrySplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      var jerryESPEnabled: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      var jerryESPIgnorePlayerNames: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      var jerryAlertEnabled: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      var jerryAlertSoundEnabled: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      var jerryBoxOpenerEnabled: Boolean
         private set

      var jerryBoxOpenerActive: Boolean

      @Entry(category = "general")
      @JvmField
      var mayorDisplayEnabled: Boolean
         private set

      @Comment(category = "farming", centered = true)
      @JvmField
      @Nullable
      var farmingSplitter: Splitter?
         private set

      @Entry(category = "farming")
      @JvmField
      var autoVisitorEnabled: Boolean
         private set

      @Entry(category = "farming")
      @JvmField
      @NotNull
      var autoVisitorConfigButton: jooon.config.Config.Companion.AutoVisitorConfigAction
         private set

      @Entry(category = "farming")
      @JvmField
      @NotNull
      var autoVisitorResetTempButton: jooon.config.Config.Companion.AutoVisitorResetAction
         private set

      @Comment(category = "slayer", centered = true)
      @JvmField
      @Nullable
      var slayerEspSplitter: Splitter?
         private set

      @Entry(category = "slayer")
      @JvmField
      var slayerESPEnabled: Boolean
         private set

      @Entry(category = "slayer", isColor = true)
      @JvmField
      @NotNull
      var slayerESPColor: String
         private set

      @Entry(category = "slayer", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      var slayerESPOpacityPct: Int
         private set

      @Entry(category = "slayer")
      @JvmField
      var slayerESPThroughWalls: Boolean
         private set

      @Comment(category = "slayer", centered = true)
      @JvmField
      @Nullable
      var slayerSplitter: Splitter?
         private set

      @Entry(category = "slayer")
      @JvmField
      var slayerClickerEnabled: Boolean
         private set

      @Entry(category = "slayer", min = 5.0, max = 20.0, isSlider = true)
      @JvmField
      var slayerClickerCps: Int
         private set

      @Entry(category = "slayer")
      @JvmField
      var slayerHPDisplayEnabled: Boolean
         private set

      @Entry(category = "slayer")
      @JvmField
      @NotNull
      var slayerHPDisplayMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "slayer")
      @JvmField
      var autoSoulcryEnabled: Boolean
         private set

      @Entry(category = "slayer")
      @JvmField
      var autoWandEnabled: Boolean
         private set

      @Entry(category = "slayer", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      var autoWandUsePct: Int
         private set

      @Entry(category = "slayer", min = 1.0, max = 20.0, isSlider = true)
      @JvmField
      var autoWandCooldownSec: Int
         private set

      @Entry(category = "slayer")
      @JvmField
      var autoZombieSwordEnabled: Boolean
         private set

      @Entry(category = "slayer", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      var autoZombieSwordUsePct: Int
         private set

      @Entry(category = "slayer", min = 1.0, max = 20.0, isSlider = true)
      @JvmField
      var autoZombieSwordCooldownSec: Int
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var minionsSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      var minionAutoClaim: Boolean
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var mirrorverseSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      var drSolverEnabled: Boolean
         private set

      @JvmField
      @Nullable
      var riftSplitter: Splitter?
         private set

      @JvmField
      var automaticIqPointsEnabled: Boolean
         private set

      @JvmField
      @NotNull
      var automaticIqPointsBoxToPick: jooon.config.Config.Companion.IqPointBox
         private set

      @JvmField
      var automaticIqPointsToGet: Int
         private set

      @JvmField
      @NotNull
      var automaticIqPointsInfusion: jooon.config.Config.Companion.RiftInfusion
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var beachBallerSplitter: Splitter?
         private set

      @JvmField
      var beachBallerEnabled: Boolean
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var beachBallerKeybindComment: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      var beachBallerHoldShift: Boolean
         private set

      @Entry(category = "automation")
      @JvmField
      @NotNull
      var beachBallerMoveHudButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      var extrasSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      var factoryHelperEnabled: Boolean
         private set

      @Comment(category = "dungeons_map", centered = true)
      @JvmField
      @Nullable
      var dungeonMapSplitter: Splitter?
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapEnabled: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      @NotNull
      var dungeonMapMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "dungeons_map", min = 0.1, max = 5.0, isSlider = true)
      @JvmField
      var dungeonMapScale: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 2.0, isSlider = true)
      @JvmField
      var dungeonMapPadding: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 20.0, isSlider = true)
      @JvmField
      var dungeonMapBorder: Int
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 1.0, isSlider = true)
      @JvmField
      var dungeonMapRoomSize: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 1.0, isSlider = true)
      @JvmField
      var dungeonMapDoorSize: Double
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapRenderCheckmark: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapRenderRoomNames: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapRenderRoomNamesNotEFB: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapRenderSecretCount: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapRenderPuzzleName: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapColorRoomName: Boolean
         private set

      @Entry(category = "dungeons_map", isColor = true)
      @JvmField
      @NotNull
      var dungeonMapBorderColor: String
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapRenderHiddenRooms: Boolean
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 1.0, isSlider = true)
      @JvmField
      var dungeonMapHiddenRoomDarken: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 2.0, isSlider = true)
      @JvmField
      var dungeonMapIconSize: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 2.0, isSlider = true)
      @JvmField
      var dungeonMapTextSize: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 10.0, isSlider = true)
      @JvmField
      var dungeonMapMarkerScale: Double
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      var dungeonMapPlayerHeads: Boolean
         private set

      @Comment(category = "puzzles", centered = true)
      @JvmField
      @Nullable
      var puzzlesSplitter: Splitter?
         private set

      @Entry(category = "puzzles")
      @JvmField
      var blazeSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var boulderSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var creeperBeamsSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var iceFillSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var icePathSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var teleportMazeSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var threeWeirdosSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var ticTacToeSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var triviaSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var waterBoardSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      var waterBoardEfficient: Boolean
         private set

      @JvmField
      var testRandom: Boolean
         private set

      fun fullyAutomaticDojoEnabled(): Boolean {
         return Config.fullyAutomaticControl || Config.fullyAutomaticMastery || Config.fullyAutomaticDiscipline || Config.fullyAutomaticSwiftness
      }

      fun dojoChallengeTrackingEnabled(): Boolean {
         return Config.autoDojoEnabled || this.fullyAutomaticDojoEnabled()
      }

      val dungeonESPLineWidthF: Float
         public get() {
            return Config.dungeonESPLineWidth / 10.0F
         }


      enum class AutoVisitorConfigAction {
         IDLE,
         CLICK;

         
         fun getEntries(): EnumEntries<Config.Companion.AutoVisitorConfigAction> {
            $ENTRIES
         }
      }

      enum class AutoVisitorResetAction {
         IDLE,
         CLICK;

         
         fun getEntries(): EnumEntries<Config.Companion.AutoVisitorResetAction> {
            $ENTRIES
         }
      }

      enum class ButtonAction {
         IDLE,
         CLICK;

         
         fun getEntries(): EnumEntries<Config.Companion.ButtonAction> {
            $ENTRIES
         }
      }

      enum class FishingKillMode {
         OFF,
         FIRE_VEIL,
         WITHER_BLADE,
         SPIRIT_SCEPTRE,
         MIDAS_STAFF;

         
         fun getEntries(): EnumEntries<Config.Companion.FishingKillMode> {
            $ENTRIES
         }
      }

      enum class IqPointBox {
         LEFT,
         MIDDLE,
         RIGHT;

         
         fun getEntries(): EnumEntries<Config.Companion.IqPointBox> {
            $ENTRIES
         }
      }

      enum class RiftInfusion {
         GRAND_EXP_BOTTLES,
         BITS;

         
         fun getEntries(): EnumEntries<Config.Companion.RiftInfusion> {
            $ENTRIES
         }
      }

      enum class StridersurferKeybindAction {
         OPEN,
         OPENING;

         
         fun getEntries(): EnumEntries<Config.Companion.StridersurferKeybindAction> {
            $ENTRIES
         }
      }
   }
}
