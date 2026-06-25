package jooon.features.dungeons.map.util

data class BoundingBox(x: Double, y: Double, w: Double, h: Double) {
   val x: Double
   val y: Double
   val w: Double
   val h: Double

   init {
      this.x = x
      this.y = y
      this.w = w
      this.h = h
   }

   fun inBounds(px: Double, py: Double): Boolean {
      return this.x <= px && px <= this.x + this.w && this.y <= py && py <= this.y + this.h
   }

   fun fitInside(box: BoundingBox): Pair<Double, BoundingBox> {



      return Pair(f, BoundingBox(box.x + (box.w - w1) * 0.5, box.y + (box.h - h1) * 0.5, w1, h1))
   }

   fun centerInside(box: BoundingBox): BoundingBox {
      return BoundingBox(box.x + 0.5 * (box.w - this.w), box.y + 0.5 * (box.h - this.h), this.w, this.h)
   }

   public operator fun component1(): Double {
      return this.x
   }

   public operator fun component2(): Double {
      return this.y
   }

   public operator fun component3(): Double {
      return this.w
   }

   public operator fun component4(): Double {
      return this.h
   }

   fun copy(x: Double = this.x, y: Double = this.y, w: Double = this.w, h: Double = this.h): BoundingBox {
      return BoundingBox(x, y, w, h)
   }

   override fun toString(): String {
      return "BoundingBox(x=${this.x}, y=${this.y}, w=${this.w}, h=${this.h})"
   }

   override fun hashCode(): Int {
      return ((java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.y)) * 31 + java.lang.Double.hashCode(this.w)) * 31
         + java.lang.Double.hashCode(this.h)
      }

   override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is BoundingBox
            && java.lang.Double.compare(this.x, (other as BoundingBox).x) == 0
            && java.lang.Double.compare(this.y, (other as BoundingBox).y) == 0
            && java.lang.Double.compare(this.w, (other as BoundingBox).w) == 0
            && java.lang.Double.compare(this.h, (other as BoundingBox).h) == 0
         }
   }
}
