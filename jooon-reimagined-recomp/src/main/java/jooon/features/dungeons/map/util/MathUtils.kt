package jooon.features.dungeons.map.util

object MathUtils {
   fun rescale(v: Double, oldMin: Double, oldMax: Double, newMin: Double, newMax: Double): Double {
      return (v - oldMin) / (oldMax - oldMin) * (newMax - newMin) + newMin
   }

   fun lerp(delta: Double, start: Double, end: Double): Double {
      return (end - start) * (coerceIn(delta, rangeTo(0.0, 1.0)) as java.lang.Number).doubleValue() + start
   }

   fun lerpAngle(pct: Double, start: Double, end: Double): Double {
      var diff: Double = (end - start) % 360.0
      if ((end - start) % 360.0 < -180.0) {
         diff += 360.0
      }

      if (diff > 180.0) {
         diff -= 360.0
      }

      return start + diff * (coerceIn(pct, rangeTo(0.0, 1.0)) as java.lang.Number).doubleValue()
   }
}
