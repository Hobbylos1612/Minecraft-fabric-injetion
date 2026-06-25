package jooon.config.ui

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJooonColorSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonColorSupport.kt\njooon/config/ui/RgbColor\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,85:1\n1#2:86\n*E\n"])
internal data class RgbColor(red: Int, green: Int, blue: Int) {
   public final val red: Int
   public final val green: Int
   public final val blue: Int

   init {
      this.red = red
      this.green = green
      this.blue = blue
   }

   public fun packed(): Int {
      return JooonColorSupportKt.rgbInt(this.red, this.green, this.blue)
   }

   public fun toHsv(): HsvColor {
      val r: Double = this.red / 255.0
      val g: Double = this.green / 255.0
      val b: Double = this.blue / 255.0
      val max: Double = Math.max(r, Math.max(g, (double)this.blue / 255.0))
      val delta: Double = max - Math.min(r, Math.min(g, b))
      val var10000: Double
      if (delta == 0.0) {
         var10000 = 0.0
      } else if (max == r) {
         val saturation: Double = (g - b) / delta
         val var20: Double = (g - b) / delta % 6.0
         var10000 = 60.0
            * (
               if ((g - b) / delta % 6.0 != 0.0 && Math.signum((g - b) / delta % 6.0) != Math.signum(6.0))
                  (g - b) / delta % 6.0 + 6.0
                  else
                  (g - b) / delta % 6.0
            )
         } else {
         var10000 = if (max == g) 60.0 * ((b - r) / delta + 2.0) else 60.0 * ((r - g) / delta + 4.0)
      }

      return HsvColor(if (var10000 < 0.0) var10000 + 360.0 else var10000, if (max == 0.0) 0.0 else delta / max, max)
   }

   public operator fun component1(): Int {
      return this.red
   }

   public operator fun component2(): Int {
      return this.green
   }

   public operator fun component3(): Int {
      return this.blue
   }

   public fun copy(red: Int = this.red, green: Int = this.green, blue: Int = this.blue): RgbColor {
      return RgbColor(red, green, blue)
   }

   public override fun toString(): String {
      return "RgbColor(red=${this.red}, green=${this.green}, blue=${this.blue})"
   }

   public override fun hashCode(): Int {
      return (Integer.hashCode(this.red) * 31 + Integer.hashCode(this.green)) * 31 + Integer.hashCode(this.blue)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is RgbColor && this.red == (other as RgbColor).red && this.green == (other as RgbColor).green && this.blue == (other as RgbColor).blue
      }
   }
}
