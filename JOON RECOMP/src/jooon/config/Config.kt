package jooon.config

import kotlin.enums.EnumEntries
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

public class Config {
   public companion object {
      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var melodySplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      public final var enableAutoMelody: Boolean
         private set

      @Entry(category = "automation", min = 0.0, max = 500.0, isSlider = true)
      @JvmField
      public final var clickDelayMs: Int
         private set

      @Comment(category = "dojo", centered = true)
      @JvmField
      @Nullable
      public final var dojoSplitter: Splitter?
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var autoDojoEnabled: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var dojoClickerEnabled: Boolean
         private set

      @Entry(category = "dojo", min = 6.0, max = 16.0, isSlider = true)
      @JvmField
      public final var dojoClickerCps: Int
         private set

      @JvmField
      public final var controlPing: Int
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var forcePreventNegative: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var forceESP: Boolean
         private set

      @Comment(category = "dojo", centered = true)
      @JvmField
      @Nullable
      public final var fullyAutomaticDojoSplitter: Splitter?
         private set

      @JvmField
      public final var fullyAutomaticForce: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var fullyAutomaticControl: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var fullyAutomaticMastery: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var fullyAutomaticDiscipline: Boolean
         private set

      @Entry(category = "dojo")
      @JvmField
      public final var fullyAutomaticSwiftness: Boolean
         private set

      @JvmField
      public final var fullyAutomaticStamina: Boolean
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var experimentsSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      public final var autoExperiments: Boolean
         private set

      @Entry(category = "automation", min = 2.0, max = 20.0, isSlider = true)
      @JvmField
      public final var autoExperimentsTickDelay: Int
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      public final var wardrobeSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      public final var autoWardrobeEnabled: Boolean
         private set

      @Entry(category = "general", min = 50.0, max = 500.0, isSlider = true)
      @JvmField
      public final var autoWardrobeDelay: Int
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      public final var petSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      public final var enablePetKeybinds: Boolean
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      public final var qolSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      public final var fullbright: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      public final var witherShieldOverlay: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      public final var witherShieldCompact: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      @NotNull
      public final var witherShieldMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      public final var carnivalSplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      public final var autoCarnivalEnabled: Boolean
         private set

      @Entry(category = "general", min = 0.0, max = 500.0, isSlider = true)
      @JvmField
      public final var autoCarnivalPing: Int
         private set

      @JvmField
      public final var fishingEnabled: Boolean
         private set

      @Comment(category = "fishing", centered = true)
      @JvmField
      @Nullable
      public final var fishingKeybindComment: Splitter?
         private set

      @Entry(category = "fishing")
      @JvmField
      @NotNull
      public final var fishingHudMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "fishing", min = 250.0, max = 4000.0, isSlider = true)
      @JvmField
      public final var fishingRecastDelayMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 150.0, isSlider = true)
      @JvmField
      public final var fishingRecastJitterMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      public final var fishingWaterPreCatchMinMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      public final var fishingWaterPreCatchMaxMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      public final var fishingLavaPreCatchMinMs: Int
         private set

      @Entry(category = "fishing", min = 0.0, max = 1000.0, isSlider = true)
      @JvmField
      public final var fishingLavaPreCatchMaxMs: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingRotate: Boolean
         private set

      @Entry(category = "fishing", min = 2000.0, max = 10000.0, isSlider = true)
      @JvmField
      public final var fishingRotateIntervalMs: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingAutoSell: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingTotem: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingAutoPowerOrb: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingAutoUmbrella: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var enableGoldenFishCatch: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingIgnoreSquids: Boolean
         private set

      @Entry(category = "fishing")
      @JvmField
      @NotNull
      public final var fishingKillingMode: jooon.config.Config.Companion.FishingKillMode
         private set

      @Entry(category = "fishing", min = 0.0, max = 2000.0, isSlider = true)
      @JvmField
      public final var funnyFishingAutoKillingDelay: Int
         private set

      @Entry(category = "fishing", min = 1.0, max = 30.0, isSlider = true)
      @JvmField
      public final var fishingSessionMinutes: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var slugFishEnabled: Boolean
         private set

      @Entry(category = "fishing", min = 0.0, max = 25.0, isSlider = true)
      @JvmField
      public final var slugFishMinWaitSec: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingMeleeAllow: Boolean
         private set

      @Entry(category = "fishing", min = 1.0, max = 8.0, isSlider = true)
      @JvmField
      public final var fishingMeleeWeaponSlot: Int
         private set

      @Entry(category = "fishing", min = 5.0, max = 16.0, isSlider = true)
      @JvmField
      public final var fishingMeleeCps: Int
         private set

      @Entry(category = "fishing")
      @JvmField
      @NotNull
      public final var fishingMeleeOpenGui: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "fishing")
      @JvmField
      public final var fishingMeleeAllMobs: Boolean
         private set

      @Comment(category = "galatea", centered = true)
      @JvmField
      @Nullable
      public final var galateaSplitter: Splitter?
         private set

      @Comment(category = "galatea", centered = true)
      @JvmField
      @Nullable
      public final var galateaKeybindComment: Splitter?
         private set

      public final var stridersurferFishingMacroEnabled: Boolean

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      public final var stridersurferFishingMacroKeybindButton: jooon.config.Config.Companion.StridersurferKeybindAction
         private set

      @Entry(category = "galatea", min = 2.0, max = 30.0, isSlider = true)
      @JvmField
      public final var stridersurferFishingMacroThreshold: Int
         private set

      @Entry(category = "galatea")
      @JvmField
      public final var stridersurferFishingMacroUseWhipInstead: Boolean
         private set

      @Entry(category = "galatea")
      @JvmField
      public final var stridersurferFishingMacroAutoPetSwap: Boolean
         private set

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      public final var stridersurferFishingMacroKillPetName: String
         private set

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      public final var stridersurferFishingMacroRecastPetName: String
         private set

      @Entry(category = "galatea")
      @JvmField
      @NotNull
      public final var stridersurferFishingMacroMoveHudButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "dungeons", centered = true)
      @JvmField
      @Nullable
      public final var dungeonEspSplitter: Splitter?
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var dungeonESPEnabled: Boolean
         private set

      @JvmField
      public final var dungeonESPLineWidth: Int
         private set

      @Entry(category = "dungeons", isColor = true)
      @JvmField
      @NotNull
      public final var dungeonESPStarColor: String
         private set

      @Entry(category = "dungeons", isColor = true)
      @JvmField
      @NotNull
      public final var dungeonESPSAColor: String
         private set

      @Entry(category = "dungeons", isColor = true)
      @JvmField
      @NotNull
      public final var dungeonESPBatColor: String
         private set

      @Entry(category = "dungeons", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      public final var dungeonESPOpacityPct: Int
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var dungeonESPThroughWalls: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var dungeonESPPerfMode: Boolean
         private set

      @Comment(category = "dungeons", centered = true)
      @JvmField
      @Nullable
      public final var dungeonSplitter: Splitter?
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var terminatorClickerEnabled: Boolean
         private set

      @Entry(category = "dungeons", min = 5.0, max = 20.0, isSlider = true)
      @JvmField
      public final var terminatorClickerCps: Int
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var fastJoinDungeons: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var fastJoinMaster: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var fastJoinKuudra: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var autoGfsEnabled: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var autoGfsPearls: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var autoGfsSuperboom: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var autoGfsJerry: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var autoGfsDecoy: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var autoGfsInDungeonsOnly: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      public final var dbDisplayEnabled: Boolean
         private set

      @Entry(category = "dungeons")
      @JvmField
      @NotNull
      public final var dbDisplayMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "general", centered = true)
      @JvmField
      @Nullable
      public final var jerrySplitter: Splitter?
         private set

      @Entry(category = "general")
      @JvmField
      public final var jerryESPEnabled: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      public final var jerryESPIgnorePlayerNames: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      public final var jerryAlertEnabled: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      public final var jerryAlertSoundEnabled: Boolean
         private set

      @Entry(category = "general")
      @JvmField
      public final var jerryBoxOpenerEnabled: Boolean
         private set

      public final var jerryBoxOpenerActive: Boolean

      @Entry(category = "general")
      @JvmField
      public final var mayorDisplayEnabled: Boolean
         private set

      @Comment(category = "farming", centered = true)
      @JvmField
      @Nullable
      public final var farmingSplitter: Splitter?
         private set

      @Entry(category = "farming")
      @JvmField
      public final var autoVisitorEnabled: Boolean
         private set

      @Entry(category = "farming")
      @JvmField
      @NotNull
      public final var autoVisitorConfigButton: jooon.config.Config.Companion.AutoVisitorConfigAction
         private set

      @Entry(category = "farming")
      @JvmField
      @NotNull
      public final var autoVisitorResetTempButton: jooon.config.Config.Companion.AutoVisitorResetAction
         private set

      @Comment(category = "slayer", centered = true)
      @JvmField
      @Nullable
      public final var slayerEspSplitter: Splitter?
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var slayerESPEnabled: Boolean
         private set

      @Entry(category = "slayer", isColor = true)
      @JvmField
      @NotNull
      public final var slayerESPColor: String
         private set

      @Entry(category = "slayer", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      public final var slayerESPOpacityPct: Int
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var slayerESPThroughWalls: Boolean
         private set

      @Comment(category = "slayer", centered = true)
      @JvmField
      @Nullable
      public final var slayerSplitter: Splitter?
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var slayerClickerEnabled: Boolean
         private set

      @Entry(category = "slayer", min = 5.0, max = 20.0, isSlider = true)
      @JvmField
      public final var slayerClickerCps: Int
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var slayerHPDisplayEnabled: Boolean
         private set

      @Entry(category = "slayer")
      @JvmField
      @NotNull
      public final var slayerHPDisplayMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var autoSoulcryEnabled: Boolean
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var autoWandEnabled: Boolean
         private set

      @Entry(category = "slayer", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      public final var autoWandUsePct: Int
         private set

      @Entry(category = "slayer", min = 1.0, max = 20.0, isSlider = true)
      @JvmField
      public final var autoWandCooldownSec: Int
         private set

      @Entry(category = "slayer")
      @JvmField
      public final var autoZombieSwordEnabled: Boolean
         private set

      @Entry(category = "slayer", min = 1.0, max = 100.0, isSlider = true)
      @JvmField
      public final var autoZombieSwordUsePct: Int
         private set

      @Entry(category = "slayer", min = 1.0, max = 20.0, isSlider = true)
      @JvmField
      public final var autoZombieSwordCooldownSec: Int
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var minionsSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      public final var minionAutoClaim: Boolean
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var mirrorverseSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      public final var drSolverEnabled: Boolean
         private set

      @JvmField
      @Nullable
      public final var riftSplitter: Splitter?
         private set

      @JvmField
      public final var automaticIqPointsEnabled: Boolean
         private set

      @JvmField
      @NotNull
      public final var automaticIqPointsBoxToPick: jooon.config.Config.Companion.IqPointBox
         private set

      @JvmField
      public final var automaticIqPointsToGet: Int
         private set

      @JvmField
      @NotNull
      public final var automaticIqPointsInfusion: jooon.config.Config.Companion.RiftInfusion
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var beachBallerSplitter: Splitter?
         private set

      @JvmField
      public final var beachBallerEnabled: Boolean
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var beachBallerKeybindComment: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      public final var beachBallerHoldShift: Boolean
         private set

      @Entry(category = "automation")
      @JvmField
      @NotNull
      public final var beachBallerMoveHudButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Comment(category = "automation", centered = true)
      @JvmField
      @Nullable
      public final var extrasSplitter: Splitter?
         private set

      @Entry(category = "automation")
      @JvmField
      public final var factoryHelperEnabled: Boolean
         private set

      @Comment(category = "dungeons_map", centered = true)
      @JvmField
      @Nullable
      public final var dungeonMapSplitter: Splitter?
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapEnabled: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      @NotNull
      public final var dungeonMapMoveButton: jooon.config.Config.Companion.ButtonAction
         private set

      @Entry(category = "dungeons_map", min = 0.1, max = 5.0, isSlider = true)
      @JvmField
      public final var dungeonMapScale: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 2.0, isSlider = true)
      @JvmField
      public final var dungeonMapPadding: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 20.0, isSlider = true)
      @JvmField
      public final var dungeonMapBorder: Int
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 1.0, isSlider = true)
      @JvmField
      public final var dungeonMapRoomSize: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 1.0, isSlider = true)
      @JvmField
      public final var dungeonMapDoorSize: Double
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapRenderCheckmark: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapRenderRoomNames: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapRenderRoomNamesNotEFB: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapRenderSecretCount: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapRenderPuzzleName: Boolean
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapColorRoomName: Boolean
         private set

      @Entry(category = "dungeons_map", isColor = true)
      @JvmField
      @NotNull
      public final var dungeonMapBorderColor: String
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapRenderHiddenRooms: Boolean
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 1.0, isSlider = true)
      @JvmField
      public final var dungeonMapHiddenRoomDarken: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 2.0, isSlider = true)
      @JvmField
      public final var dungeonMapIconSize: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 2.0, isSlider = true)
      @JvmField
      public final var dungeonMapTextSize: Double
         private set

      @Entry(category = "dungeons_map", min = 0.0, max = 10.0, isSlider = true)
      @JvmField
      public final var dungeonMapMarkerScale: Double
         private set

      @Entry(category = "dungeons_map")
      @JvmField
      public final var dungeonMapPlayerHeads: Boolean
         private set

      @Comment(category = "puzzles", centered = true)
      @JvmField
      @Nullable
      public final var puzzlesSplitter: Splitter?
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var blazeSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var boulderSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var creeperBeamsSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var iceFillSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var icePathSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var teleportMazeSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var threeWeirdosSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var ticTacToeSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var triviaSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var waterBoardSolver: Boolean
         private set

      @Entry(category = "puzzles")
      @JvmField
      public final var waterBoardEfficient: Boolean
         private set

      @JvmField
      public final var testRandom: Boolean
         private set

      public fun fullyAutomaticDojoEnabled(): Boolean {
         return Config.fullyAutomaticControl || Config.fullyAutomaticMastery || Config.fullyAutomaticDiscipline || Config.fullyAutomaticSwiftness
      }

      public fun dojoChallengeTrackingEnabled(): Boolean {
         return Config.autoDojoEnabled || this.fullyAutomaticDojoEnabled()
      }

      public final val dungeonESPLineWidthF: Float
         public final get() {
            return Config.dungeonESPLineWidth / 10.0F
         }


      public enum class AutoVisitorConfigAction {
         IDLE,
         CLICK;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.AutoVisitorConfigAction> {
            $ENTRIES
         }
      }

      public enum class AutoVisitorResetAction {
         IDLE,
         CLICK;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.AutoVisitorResetAction> {
            $ENTRIES
         }
      }

      public enum class ButtonAction {
         IDLE,
         CLICK;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.ButtonAction> {
            $ENTRIES
         }
      }

      public enum class FishingKillMode {
         OFF,
         FIRE_VEIL,
         WITHER_BLADE,
         SPIRIT_SCEPTRE,
         MIDAS_STAFF;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.FishingKillMode> {
            $ENTRIES
         }
      }

      public enum class IqPointBox {
         LEFT,
         MIDDLE,
         RIGHT;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.IqPointBox> {
            $ENTRIES
         }
      }

      public enum class RiftInfusion {
         GRAND_EXP_BOTTLES,
         BITS;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.RiftInfusion> {
            $ENTRIES
         }
      }

      public enum class StridersurferKeybindAction {
         OPEN,
         OPENING;

         @JvmStatic
         fun getEntries(): EnumEntries<Config.Companion.StridersurferKeybindAction> {
            $ENTRIES
         }
      }
   }
}
