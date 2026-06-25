package jooon.features.dungeons.map.api

public data class WorldComponentPosition(wx: Int, wz: Int, cx: Int, cz: Int) {
   public final val wx: Int
   public final val wz: Int
   public final val cx: Int
   public final val cz: Int

   init {
      this.wx = wx
      this.wz = wz
      this.cx = cx
      this.cz = cz
   }

   public override fun toString(): String {
      return if (this == EMPTY) "WorldComp()" else "WorldComp(${this.wx}, ${this.wz}, ${this.cx}, ${this.cz})"
   }

   public fun toWorld(): WorldPosition {
      return WorldPosition(this.wx, this.wz)
   }

   public fun toComponent(): ComponentPosition {
      return ComponentPosition(this.cx, this.cz)
   }

   public operator fun component1(): Int {
      return this.wx
   }

   public operator fun component2(): Int {
      return this.wz
   }

   public operator fun component3(): Int {
      return this.cx
   }

   public operator fun component4(): Int {
      return this.cz
   }

   public fun copy(wx: Int = this.wx, wz: Int = this.wz, cx: Int = this.cx, cz: Int = this.cz): WorldComponentPosition {
      return WorldComponentPosition(wx, wz, cx, cz)
   }

   public override fun hashCode(): Int {
      return ((Integer.hashCode(this.wx) * 31 + Integer.hashCode(this.wz)) * 31 + Integer.hashCode(this.cx)) * 31 + Integer.hashCode(this.cz)
   }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is WorldComponentPosition
            && this.wx == (other as WorldComponentPosition).wx
            && this.wz == (other as WorldComponentPosition).wz
            && this.cx == (other as WorldComponentPosition).cx
            && this.cz == (other as WorldComponentPosition).cz
         }
   }

   public companion object {
      public final val EMPTY: WorldComponentPosition
   }
}
