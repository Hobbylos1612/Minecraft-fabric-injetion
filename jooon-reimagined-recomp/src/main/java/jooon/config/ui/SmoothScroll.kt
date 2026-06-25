package jooon.config.ui

import kotlin.math.MathKt

internal class SmoothScroll {
   var target: Int
      private set

   private var current: Float
   private var lastTime: Long = System.currentTimeMillis()

   val value: Int
      public get() {
         return (this.current).roundToInt()
      }


   fun set(newTarget: Int) {
      this.target = newTarget
   }

   fun jump(position: Int) {
      this.target = position
      this.current = position
   }

   fun addDelta(delta: Int, maxScroll: Int) {
      this.target = (this.target + delta).coerceIn(0, maxScroll)
   }

   fun clamp(maxScroll: Int) {
      this.target = (this.target).coerceIn(0, maxScroll)
      this.current = (this.current).coerceIn(0.0F, maxScroll.toFloat())
   }

   fun tick(maxScroll: Int): Int {
      this.target = (this.target).coerceIn(0, maxScroll)


      this.lastTime = now

      if (Math.abs(this.target.toFloat() - this.current) < 0.5F) {
         this.current = this.target
      } else {
         this.current = this.current + diff * (1.0F - Math.exp((-12.0F * dt).toDouble()).toFloat())
      }

      return (this.current).roundToInt()
   }
}
