package jooon.config.ui

import kotlin.math.MathKt

internal class SmoothScroll {
   public final var target: Int
      private set

   private final var current: Float
   private final var lastTime: Long = System.currentTimeMillis()

   public final val value: Int
      public final get() {
         return MathKt.roundToInt(this.current)
      }


   public fun set(newTarget: Int) {
      this.target = newTarget
   }

   public fun jump(position: Int) {
      this.target = position
      this.current = position
   }

   public fun addDelta(delta: Int, maxScroll: Int) {
      this.target = RangesKt.coerceIn(this.target + delta, 0, maxScroll)
   }

   public fun clamp(maxScroll: Int) {
      this.target = RangesKt.coerceIn(this.target, 0, maxScroll)
      this.current = RangesKt.coerceIn(this.current, 0.0F, (float)maxScroll)
   }

   public fun tick(maxScroll: Int): Int {
      this.target = RangesKt.coerceIn(this.target, 0, maxScroll)
      val now: Long = System.currentTimeMillis()
      val dt: Float = (float)RangesKt.coerceIn(now - this.lastTime, 1L, 50L) / 1000.0F
      this.lastTime = now
      val diff: Float = this.target - this.current
      if (Math.abs((float)this.target - this.current) < 0.5F) {
         this.current = this.target
      } else {
         this.current = this.current + diff * (1.0F - (float)Math.exp((double)(-12.0F * dt)))
      }

      return MathKt.roundToInt(this.current)
   }
}
