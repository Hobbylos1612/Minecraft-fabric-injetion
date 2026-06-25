package jooon.util

object SyntheticMouseTurnBroker {
   private var activeOwner: String?
   private var activeProvider: (() -> jooon.util.SyntheticMouseTurnBroker.TurnDelta?)?

   @Synchronized
   
   fun claim(owner: String, provider: () -> jooon.util.SyntheticMouseTurnBroker.TurnDelta?) {
      activeOwner = owner
      activeProvider = provider
   }

   @Synchronized
   
   fun release(owner: String) {
      if (activeOwner == owner) {
         activeOwner = null
         activeProvider = null
      }
   }

   
   fun getSyntheticMouseTurn(): jooon.util.SyntheticMouseTurnBroker.TurnDelta? {
      return if (activeProvider != null) activeProvider() as SyntheticMouseTurnBroker.TurnDelta else null
   }

   data class TurnDelta(rawX: Double, rawY: Double) {
      val rawX: Double
      val rawY: Double

      init {
         this.rawX = rawX
         this.rawY = rawY
      }

      public operator fun component1(): Double {
         return this.rawX
      }

      public operator fun component2(): Double {
         return this.rawY
      }

      fun copy(rawX: Double = this.rawX, rawY: Double = this.rawY): jooon.util.SyntheticMouseTurnBroker.TurnDelta {
         return SyntheticMouseTurnBroker.TurnDelta(rawX, rawY)
      }

      override fun toString(): String {
         return "TurnDelta(rawX=${this.rawX}, rawY=${this.rawY})"
      }

      override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.rawX) * 31 + java.lang.Double.hashCode(this.rawY)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is SyntheticMouseTurnBroker.TurnDelta
               && java.lang.Double.compare(this.rawX, (other as SyntheticMouseTurnBroker.TurnDelta).rawX) == 0
               && java.lang.Double.compare(this.rawY, (other as SyntheticMouseTurnBroker.TurnDelta).rawY) == 0
            }
      }
   }
}
