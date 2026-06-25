package jooon.features.dungeons.map.api

data class WorldPosition(x: Int, z: Int) {
   val x: Int
   val z: Int

   init {
      this.x = x
      this.z = z
   }

   override fun toString(): String {
      return if (this == EMPTY) "World()" else "World(${this.x}, ${this.z})"
   }

   fun toComponent(): ComponentPosition {
      return ComponentPosition((this.x - cornerStart.x) / 16, (this.z - cornerStart.z) / 16)
   }

   fun withComponent(): WorldComponentPosition {

      return WorldComponentPosition(this.x, this.z, it.x, it.z)
   }

   public operator fun component1(): Int {
      return this.x
   }

   public operator fun component2(): Int {
      return this.z
   }

   fun copy(x: Int = this.x, z: Int = this.z): WorldPosition {
      return WorldPosition(x, z)
   }

   override fun hashCode(): Int {
      return Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)
   }

   override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is WorldPosition && this.x == (other as WorldPosition).x && this.z == (other as WorldPosition).z
      }
   }

   companion object {
      val EMPTY: WorldPosition
   }
}
