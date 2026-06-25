package jooon.config

import org.jetbrains.annotations.NotNull

public class PersistentState {
   public companion object {
      @Entry
      @JvmField
      public final var mayorDisplayX: Int
         private set

      @Entry
      @JvmField
      public final var mayorDisplayY: Int
         private set

      @Entry
      @JvmField
      @NotNull
      public final var currentMayor: String
         private set

      @Entry
      @JvmField
      public final var nextMayorChange: Long
         private set

      @Entry
      @JvmField
      public final var mayorDisplayMovable: Boolean
         private set

      @Entry
      @JvmField
      public final var slayerHPDisplayX: Int
         private set

      @Entry
      @JvmField
      public final var slayerHPDisplayY: Int
         private set

      @Entry
      @JvmField
      public final var slayerHPDisplayMovable: Boolean
         private set

      @Entry
      @JvmField
      public final var slayerHPDisplayInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var dungeonMapX: Int
         private set

      @Entry
      @JvmField
      public final var dungeonMapY: Int
         private set

      @Entry
      @JvmField
      public final var dungeonMapMovable: Boolean
         private set

      @Entry
      @JvmField
      public final var dungeonMapInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var witherShieldX: Int
         private set

      @Entry
      @JvmField
      public final var witherShieldY: Int
         private set

      @Entry
      @JvmField
      public final var witherShieldMovable: Boolean
         private set

      @Entry
      @JvmField
      public final var witherShieldInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var dbDisplayX: Int
         private set

      @Entry
      @JvmField
      public final var dbDisplayY: Int
         private set

      @Entry
      @JvmField
      public final var dbDisplayMovable: Boolean
         private set

      @Entry
      @JvmField
      public final var dbDisplayInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorConfigured: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorPadX: Double
         private set

      @Entry
      @JvmField
      public final var autoVisitorPadY: Double
         private set

      @Entry
      @JvmField
      public final var autoVisitorPadZ: Double
         private set

      @Entry
      @JvmField
      public final var autoVisitorSetupCompleted: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorPadPlaced: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorAcceptAll: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorIgnoreSpaceman: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorMaxSpendCoins: Long
         private set

      @Entry
      @JvmField
      public final var autoVisitorTrySacksFirst: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorRareItemsOnly: Boolean
         private set

      @Entry
      @JvmField
      public final var autoVisitorMinFarmingXp: Long
         private set

      @Entry
      @JvmField
      public final var fishingHudX: Int
         private set

      @Entry
      @JvmField
      public final var fishingHudY: Int
         private set

      @Entry
      @JvmField
      public final var fishingHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var stridersurferFishingMacroHudX: Int
         private set

      @Entry
      @JvmField
      public final var stridersurferFishingMacroHudY: Int
         private set

      @Entry
      @JvmField
      public final var stridersurferFishingMacroHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var dojoHudX: Int
         private set

      @Entry
      @JvmField
      public final var dojoHudY: Int
         private set

      @Entry
      @JvmField
      public final var dojoHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var beachBallerHudX: Int
         private set

      @Entry
      @JvmField
      public final var beachBallerHudY: Int
         private set

      @Entry
      @JvmField
      public final var beachBallerHudInitDone: Boolean
         private set

      @Entry
      @JvmField
      public final var automaticIqPointsHudX: Int
         private set

      @Entry
      @JvmField
      public final var automaticIqPointsHudY: Int
         private set

      @Entry
      @JvmField
      public final var automaticIqPointsHudInitDone: Boolean
         private set
   }
}
