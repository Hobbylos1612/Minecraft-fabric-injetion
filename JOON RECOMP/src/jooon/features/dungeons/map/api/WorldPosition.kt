package jooon.features.dungeons.map.api

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCoordinates.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coordinates.kt\njooon/features/dungeons/map/api/WorldPosition\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,126:1\n1#2:127\n*E\n"])
public data class WorldPosition(x: Int, z: Int) {
   public final val x: Int
   public final val z: Int

   init {
      this.x = x
      this.z = z
   }

   public override fun toString(): String {
      return if (this == EMPTY) "World()" else "World(${this.x}, ${this.z})"
   }

   public fun toComponent(): ComponentPosition {
      return ComponentPosition((this.x - cornerStart.x) / 16, (this.z - cornerStart.z) / 16)
   }

   public fun withComponent(): WorldComponentPosition {
      val it: ComponentPosition = this.toComponent()
      return WorldComponentPosition(this.x, this.z, it.x, it.z)
   }

   public operator fun component1(): Int {
      return this.x
   }

   public operator fun component2(): Int {
      return this.z
   }

   public fun copy(x: Int = this.x, z: Int = this.z): WorldPosition {
      return WorldPosition(x, z)
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is WorldPosition && this.x == (other as WorldPosition).x && this.z == (other as WorldPosition).z
      }
   }

   public companion object {
      public final val EMPTY: WorldPosition
   }
}
