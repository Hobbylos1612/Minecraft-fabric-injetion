package jooon.features.dungeons.map.api

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCoordinates.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coordinates.kt\njooon/features/dungeons/map/api/ComponentPosition\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,126:1\n1#2:127\n766#3:128\n857#3,2:129\n766#3:131\n857#3,2:132\n766#3:134\n857#3,2:135\n*S KotlinDebug\n*F\n+ 1 Coordinates.kt\njooon/features/dungeons/map/api/ComponentPosition\n*L\n58#1:128\n58#1:129,2\n69#1:131\n69#1:132,2\n81#1:134\n81#1:135,2\n*E\n"])
public data class ComponentPosition(x: Int, z: Int) {
   public final val x: Int
   public final val z: Int

   init {
      this.x = x
      this.z = z
   }

   public override fun toString(): String {
      return if (this == EMPTY) "Component()" else "Component(${this.x}, ${this.z})"
   }

   public fun toWorld(): WorldPosition {
      return WorldPosition(cornerStart.x + 15 + 16 * this.x, cornerStart.z + 15 + 16 * this.z)
   }

   public fun withWorld(): WorldComponentPosition {
      val it: WorldPosition = this.toWorld()
      return WorldComponentPosition(it.x, it.z, this.x, this.z)
   }

   public fun toRoom(): ComponentPosition {
      return ComponentPosition(this.x and -2, this.z and -2)
   }

   public fun isValid(): Boolean {
      return 0 <= this.x && this.x < 11 && 0 <= this.z && this.z < 11
   }

   public fun isValidRoom(): Boolean {
      return (this.x and 1) == 0 && (this.z and 1) == 0
   }

   public fun isValidDoor(): Boolean {
      return (this.x and 1 xor this.z and 1) == 1
   }

   public fun getNeighboringRooms(): List<ComponentPosition> {
      if (this.isValidDoor()) {
         val `$this$filterTo$iv$iv`: java.lang.Iterable = if ((this.x and 1) == 1)
            CollectionsKt.listOf(arrayOf(ComponentPosition(this.x - 1, this.z), ComponentPosition(this.x + 1, this.z)))
            else
            CollectionsKt.listOf(arrayOf(ComponentPosition(this.x, this.z - 1), ComponentPosition(this.x, this.z + 1)))
            val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterTo$iv$iv`) {
            if ((`element$iv$iv` as ComponentPosition).isValid()) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ComponentPosition>
      } else {
         return CollectionsKt.emptyList()
      }
   }

   public fun getNeighboringDoors(): List<ComponentPosition> {
      if (this.isValidRoom()) {
         val `$this$filterTo$iv$iv`: java.lang.Iterable = CollectionsKt.listOf(
            arrayOf(
               ComponentPosition(this.x, this.z - 1),
               ComponentPosition(this.x, this.z + 1),
               ComponentPosition(this.x - 1, this.z),
               ComponentPosition(this.x + 1, this.z)
            )
         )
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterTo$iv$iv`) {
            if ((`element$iv$iv` as ComponentPosition).isValid()) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ComponentPosition>
      } else {
         return CollectionsKt.emptyList()
      }
   }

   public fun getNeighbors(): List<jooon.features.dungeons.map.api.ComponentPosition.Neighbor> {
      if (!this.isValidRoom()) {
         return CollectionsKt.emptyList()
      } else {
         val `$this$filterTo$iv$iv`: java.lang.Iterable = CollectionsKt.listOf(
            arrayOf(
               ComponentPosition.Neighbor(ComponentPosition(this.x, this.z - 2), ComponentPosition(this.x, this.z - 1)),
               ComponentPosition.Neighbor(ComponentPosition(this.x, this.z + 2), ComponentPosition(this.x, this.z + 1)),
               ComponentPosition.Neighbor(ComponentPosition(this.x - 2, this.z), ComponentPosition(this.x - 1, this.z)),
               ComponentPosition.Neighbor(ComponentPosition(this.x + 2, this.z), ComponentPosition(this.x + 1, this.z))
            )
         )
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterTo$iv$iv`) {
            if ((`element$iv$iv` as ComponentPosition.Neighbor).room.isValid()) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ComponentPosition.Neighbor>
      }
   }

   public fun getRoomIdx(): Int {
      return this.z / 2 * 6 + this.x / 2
   }

   public fun getDoorIdx(): Int {
      return (this.x - 1 shr 1) + 6 * this.z - ((this.x - 1 shr 1) + 6 * this.z) / 12
   }

   public operator fun component1(): Int {
      return this.x
   }

   public operator fun component2(): Int {
      return this.z
   }

   public fun copy(x: Int = this.x, z: Int = this.z): ComponentPosition {
      return ComponentPosition(x, z)
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ComponentPosition && this.x == (other as ComponentPosition).x && this.z == (other as ComponentPosition).z
      }
   }

   public companion object {
      public final val EMPTY: ComponentPosition
   }

   public data class Neighbor(room: ComponentPosition, door: ComponentPosition) {
      public final val room: ComponentPosition
      public final val door: ComponentPosition

      init {
         this.room = room
         this.door = door
      }

      public operator fun component1(): ComponentPosition {
         return this.room
      }

      public operator fun component2(): ComponentPosition {
         return this.door
      }

      public fun copy(room: ComponentPosition = this.room, door: ComponentPosition = this.door): jooon.features.dungeons.map.api.ComponentPosition.Neighbor {
         return ComponentPosition.Neighbor(room, door)
      }

      public override fun toString(): String {
         return "Neighbor(room=${this.room}, door=${this.door})"
      }

      public override fun hashCode(): Int {
         return this.room.hashCode() * 31 + this.door.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is ComponentPosition.Neighbor
               && this.room == (other as ComponentPosition.Neighbor).room
               && this.door == (other as ComponentPosition.Neighbor).door
            }
      }
   }
}
