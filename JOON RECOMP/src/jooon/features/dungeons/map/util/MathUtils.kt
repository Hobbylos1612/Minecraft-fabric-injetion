package jooon.features.dungeons.map.util

public object MathUtils {
   public fun rescale(v: Double, oldMin: Double, oldMax: Double, newMin: Double, newMax: Double): Double {
      return (v - oldMin) / (oldMax - oldMin) * (newMax - newMin) + newMin
   }

   public fun lerp(delta: Double, start: Double, end: Double): Double {
      return (end - start) * (RangesKt.coerceIn(delta, RangesKt.rangeTo(0.0, 1.0)) as java.lang.Number).doubleValue() + start
   }

   public fun lerpAngle(pct: Double, start: Double, end: Double): Double {
      var diff: Double = (end - start) % 360.0
      if ((end - start) % 360.0 < -180.0) {
         diff += 360.0
      }

      if (diff > 180.0) {
         diff -= 360.0
      }

      return start + diff * (RangesKt.coerceIn(pct, RangesKt.rangeTo(0.0, 1.0)) as java.lang.Number).doubleValue()
   }
}
