package jooon.config.ui

internal data class HsvColor(hue: Double, saturation: Double, value: Double) {
   val hue: Double
   val saturation: Double
   val value: Double

   init {
      this.hue = hue
      this.saturation = saturation
      this.value = value
   }

   public operator fun component1(): Double {
      return this.hue
   }

   public operator fun component2(): Double {
      return this.saturation
   }

   public operator fun component3(): Double {
      return this.value
   }

   fun copy(hue: Double = this.hue, saturation: Double = this.saturation, value: Double = this.value): HsvColor {
      return HsvColor(hue, saturation, value)
   }

   override fun toString(): String {
      return "HsvColor(hue=${this.hue}, saturation=${this.saturation}, value=${this.value})"
   }

   override fun hashCode(): Int {
      return (java.lang.Double.hashCode(this.hue) * 31 + java.lang.Double.hashCode(this.saturation)) * 31 + java.lang.Double.hashCode(this.value)
   }

   override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is HsvColor
            && java.lang.Double.compare(this.hue, (other as HsvColor).hue) == 0
            && java.lang.Double.compare(this.saturation, (other as HsvColor).saturation) == 0
            && java.lang.Double.compare(this.value, (other as HsvColor).value) == 0
         }
   }
}
