package jooon.features.slayers

object AbilityManager {
   private var lockUntilMs: Long
   private var wandBusy: Boolean
   private var swordBusy: Boolean

   fun canUseWand(): Boolean {
      return System.currentTimeMillis() >= lockUntilMs && !swordBusy
   }

   fun canUseSword(): Boolean {
      return System.currentTimeMillis() >= lockUntilMs && !wandBusy
   }

   fun startWand() {
      wandBusy = true
      this.extendLock(100L)
   }

   fun endWand() {
      wandBusy = false
      this.extendLock(100L)
   }

   fun startSword() {
      swordBusy = true
      this.extendLock(100L)
   }

   fun endSword() {
      swordBusy = false
      this.extendLock(100L)
   }

   private fun extendLock(extraMs: Long) {
      lockUntilMs = Math.max(lockUntilMs, System.currentTimeMillis() + extraMs)
   }
}
