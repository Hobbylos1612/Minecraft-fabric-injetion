package jooon.config

import org.jetbrains.annotations.NotNull

class PersistentState {
   companion object {
      @Entry
      @JvmField
      var mayorDisplayX: Int
         private set

      @Entry
      @JvmField
      var mayorDisplayY: Int
         private set

      @Entry
      @JvmField
      @NotNull
      var currentMayor: String
         private set

      @Entry
      @JvmField
      var nextMayorChange: Long
         private set

      @Entry
      @JvmField
      var mayorDisplayMovable: Boolean
         private set

      @Entry
      @JvmField
      var slayerHPDisplayX: Int
         private set

      @Entry
      @JvmField
      var slayerHPDisplayY: Int
         private set

      @Entry
      @JvmField
      var slayerHPDisplayMovable: Boolean
         private set

      @Entry
      @JvmField
      var slayerHPDisplayInitDone: Boolean
         private set

      @Entry
      @JvmField
      var dungeonMapX: Int
         private set

      @Entry
      @JvmField
      var dungeonMapY: Int
         private set

      @Entry
      @JvmField
      var dungeonMapMovable: Boolean
         private set

      @Entry
      @JvmField
      var dungeonMapInitDone: Boolean
         private set

      @Entry
      @JvmField
      var witherShieldX: Int
         private set

      @Entry
      @JvmField
      var witherShieldY: Int
         private set

      @Entry
      @JvmField
      var witherShieldMovable: Boolean
         private set

      @Entry
      @JvmField
      var witherShieldInitDone: Boolean
         private set

      @Entry
      @JvmField
      var dbDisplayX: Int
         private set

      @Entry
      @JvmField
      var dbDisplayY: Int
         private set

      @Entry
      @JvmField
      var dbDisplayMovable: Boolean
         private set

      @Entry
      @JvmField
      var dbDisplayInitDone: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorConfigured: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorPadX: Double
         private set

      @Entry
      @JvmField
      var autoVisitorPadY: Double
         private set

      @Entry
      @JvmField
      var autoVisitorPadZ: Double
         private set

      @Entry
      @JvmField
      var autoVisitorSetupCompleted: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorPadPlaced: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorAcceptAll: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorIgnoreSpaceman: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorMaxSpendCoins: Long
         private set

      @Entry
      @JvmField
      var autoVisitorTrySacksFirst: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorRareItemsOnly: Boolean
         private set

      @Entry
      @JvmField
      var autoVisitorMinFarmingXp: Long
         private set

      @Entry
      @JvmField
      var fishingHudX: Int
         private set

      @Entry
      @JvmField
      var fishingHudY: Int
         private set

      @Entry
      @JvmField
      var fishingHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      var stridersurferFishingMacroHudX: Int
         private set

      @Entry
      @JvmField
      var stridersurferFishingMacroHudY: Int
         private set

      @Entry
      @JvmField
      var stridersurferFishingMacroHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      var dojoHudX: Int
         private set

      @Entry
      @JvmField
      var dojoHudY: Int
         private set

      @Entry
      @JvmField
      var dojoHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      var beachBallerHudX: Int
         private set

      @Entry
      @JvmField
      var beachBallerHudY: Int
         private set

      @Entry
      @JvmField
      var beachBallerHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      var automaticIqPointsHudX: Int
         private set

      @Entry
      @JvmField
      var automaticIqPointsHudY: Int
         private set

      @Entry
      @JvmField
      var automaticIqPointsHudInitDone: Boolean
         private set
   }
}
