package jooon.features.dungeons.map.api

import java.util.ArrayList
data class ComponentPosition(x: Int, z: Int) {
   val x: Int
   val z: Int

   init {
      this.x = x
      this.z = z
   }

   override fun toString(): String {
      return if (this == EMPTY) "Component()" else "Component(${this.x}, ${this.z})"
   }

   fun toWorld(): WorldPosition {
      return WorldPosition(cornerStart.x + 15 + 16 * this.x, cornerStart.z + 15 + 16 * this.z)
   }

   fun withWorld(): WorldComponentPosition {

      return WorldComponentPosition(it.x, it.z, this.x, this.z)
   }

   fun toRoom(): ComponentPosition {
      return ComponentPosition(this.x and -2, this.z and -2)
   }

   fun isValid(): Boolean {
      return 0 <= this.x && this.x < 11 && 0 <= this.z && this.z < 11
   }

   fun isValidRoom(): Boolean {
      return (this.x and 1) == 0 && (this.z and 1) == 0
   }

   fun isValidDoor(): Boolean {
      return (this.x and 1 xor this.z and 1) == 1
   }

   fun getNeighboringRooms(): List<ComponentPosition> {
      if (this.isValidDoor()) {
         val `this$iv$iv`: java.lang.Iterable = if ((this.x and 1) == 1)
            listOf(arrayOf(ComponentPosition(this.x - 1, this.z), ComponentPosition(this.x + 1, this.z)))
return else
            listOf(arrayOf(ComponentPosition(this.x, this.z - 1), ComponentPosition(this.x, this.z + 1)))
            val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if ((`element$iv$iv` as ComponentPosition).isValid()) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ComponentPosition>
      } else {
         return emptyList()
      }
   }

   fun getNeighboringDoors(): List<ComponentPosition> {
      if (this.isValidRoom()) {
         val `this$iv$iv`: java.lang.Iterable = listOf(
            arrayOf(
               ComponentPosition(this.x, this.z - 1),
               ComponentPosition(this.x, this.z + 1),
               ComponentPosition(this.x - 1, this.z),
               ComponentPosition(this.x + 1, this.z)
            )
         )
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if ((`element$iv$iv` as ComponentPosition).isValid()) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ComponentPosition>
      } else {
         return emptyList()
      }
   }

   fun getNeighbors(): List<jooon.features.dungeons.map.api.ComponentPosition.Neighbor> {
      if (!this.isValidRoom()) {
         return emptyList()
      } else {
         val `this$iv$iv`: java.lang.Iterable = listOf(
            arrayOf(
               ComponentPosition.Neighbor(ComponentPosition(this.x, this.z - 2), ComponentPosition(this.x, this.z - 1)),
               ComponentPosition.Neighbor(ComponentPosition(this.x, this.z + 2), ComponentPosition(this.x, this.z + 1)),
               ComponentPosition.Neighbor(ComponentPosition(this.x - 2, this.z), ComponentPosition(this.x - 1, this.z)),
               ComponentPosition.Neighbor(ComponentPosition(this.x + 2, this.z), ComponentPosition(this.x + 1, this.z))
            )
         )
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if ((`element$iv$iv` as ComponentPosition.Neighbor).room.isValid()) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ComponentPosition.Neighbor>
      }
   }

   fun getRoomIdx(): Int {
      return this.z / 2 * 6 + this.x / 2
   }

   fun getDoorIdx(): Int {
      return (this.x - 1 shr 1) + 6 * this.z - ((this.x - 1 shr 1) + 6 * this.z) / 12
   }

   public operator fun component1(): Int {
      return this.x
   }

   public operator fun component2(): Int {
      return this.z
   }

   fun copy(x: Int = this.x, z: Int = this.z): ComponentPosition {
      return ComponentPosition(x, z)
   }

   override fun hashCode(): Int {
      return Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)
   }

   override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ComponentPosition && this.x == (other as ComponentPosition).x && this.z == (other as ComponentPosition).z
      }
   }

   companion object {
      val EMPTY: ComponentPosition
   }

   data class Neighbor(room: ComponentPosition, door: ComponentPosition) {
      val room: ComponentPosition
      val door: ComponentPosition

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

      fun copy(room: ComponentPosition = this.room, door: ComponentPosition = this.door): jooon.features.dungeons.map.api.ComponentPosition.Neighbor {
         return ComponentPosition.Neighbor(room, door)
      }

      override fun toString(): String {
         return "Neighbor(room=${this.room}, door=${this.door})"
      }

      override fun hashCode(): Int {
         return this.room.hashCode() * 31 + this.door.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
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
