package jooon.features.dungeons.map.api

import java.util.Arrays

public data class PlayerComponentPosition(x: Double, z: Double, r: Double) {
   public final val x: Double
   public final val z: Double
   public final val r: Double

   init {
      this.x = x
      this.z = z
      this.r = r
   }

   public override fun toString(): String {
      val var3: Array<Any> = arrayOf(this.x, this.z, this.r)
      val var10000: java.lang.String = java.lang.String.format("PlayerPosition(%.3f, %.3f, %.3f)", Arrays.copyOf(var3, var3.length))
      return var10000
   }

   public fun toComponent(): ComponentPosition {
      return ComponentPosition((int)this.x, (int)this.z)
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

   public fun copy(x: Double = this.x, z: Double = this.z, r: Double = this.r): PlayerComponentPosition {
      return PlayerComponentPosition(x, z, r)
   }

   public override fun hashCode(): Int {
      return (java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.z)) * 31 + java.lang.Double.hashCode(this.r)
   }

   public override operator fun equals(other: Any?): Boolean {
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

   public companion object {
      public fun fromWorld(wx: Double, wz: Double, r: Double): PlayerComponentPosition {
         return PlayerComponentPosition((wx - cornerStart.x) / 16, (wz - cornerStart.z) / 16, r)
      }
   }
}
