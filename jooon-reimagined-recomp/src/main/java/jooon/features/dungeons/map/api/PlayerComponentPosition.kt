package jooon.features.dungeons.map.api

import java.util.Arrays

data class PlayerComponentPosition(x: Double, z: Double, r: Double) {
   val x: Double
   val z: Double
   val r: Double

   init {
      this.x = x
      this.z = z
      this.r = r
   }

   override fun toString(): String {
      val var3: Array<Any> = arrayOf(this.x, this.z, this.r)

      return var10000
   }

   fun toComponent(): ComponentPosition {
      return ComponentPosition(this.x.toInt(), this.z.toInt())
   }

   public operator fun component1(): Double {
      return this.x
   }

   public operator fun component2(): Double {
      return this.z
   }

   public operator fun component3(): Double {
      return this.r
   }

   fun copy(x: Double = this.x, z: Double = this.z, r: Double = this.r): PlayerComponentPosition {
      return PlayerComponentPosition(x, z, r)
   }

   override fun hashCode(): Int {
      return (java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.z)) * 31 + java.lang.Double.hashCode(this.r)
   }

   override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is PlayerComponentPosition
            && java.lang.Double.compare(this.x, (other as PlayerComponentPosition).x) == 0
            && java.lang.Double.compare(this.z, (other as PlayerComponentPosition).z) == 0
            && java.lang.Double.compare(this.r, (other as PlayerComponentPosition).r) == 0
         }
   }

   companion object {
      fun fromWorld(wx: Double, wz: Double, r: Double): PlayerComponentPosition {
         return PlayerComponentPosition((wx - cornerStart.x) / 16, (wz - cornerStart.z) / 16, r)
      }
   }
}
