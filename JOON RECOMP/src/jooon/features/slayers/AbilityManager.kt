package jooon.features.slayers

public object AbilityManager {
   private final var lockUntilMs: Long
   private final var wandBusy: Boolean
   private final var swordBusy: Boolean

   public fun canUseWand(): Boolean {
      return System.currentTimeMillis() >= lockUntilMs && !swordBusy
   }

   public fun canUseSword(): Boolean {
      return System.currentTimeMillis() >= lockUntilMs && !wandBusy
   }

   public fun startWand() {
      wandBusy = true
      this.extendLock(100L)
   }

   public fun endWand() {
      wandBusy = false
      this.extendLock(100L)
   }

   public fun startSword() {
      swordBusy = true
      this.extendLock(100L)
   }

   public fun endSword() {
      swordBusy = false
      this.extendLock(100L)
   }

   private fun extendLock(extraMs: Long) {
      lockUntilMs = Math.max(lockUntilMs, System.currentTimeMillis() + extraMs)
   }
}
