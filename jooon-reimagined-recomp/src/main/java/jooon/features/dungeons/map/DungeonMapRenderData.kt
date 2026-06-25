package jooon.features.dungeons.map

import jooon.features.dungeons.map.api.DungeonDoor
import jooon.features.dungeons.map.api.DungeonRoom

data class DungeonMapRenderData(rooms: List<DungeonRoom?>, doors: List<DungeonDoor?>, options: DungeonMapRenderOptions) {
   val rooms: List<DungeonRoom?>
   val doors: List<DungeonDoor?>
   val options: DungeonMapRenderOptions

   init {
      this.rooms = rooms
      this.doors = doors
      this.options = options
   }

   public operator fun component1(): List<DungeonRoom?> {
      return this.rooms
   }

   public operator fun component2(): List<DungeonDoor?> {
      return this.doors
   }

   public operator fun component3(): DungeonMapRenderOptions {
      return this.options
   }

   fun copy(rooms: List<DungeonRoom?> = this.rooms, doors: List<DungeonDoor?> = this.doors, options: DungeonMapRenderOptions = this.options): DungeonMapRenderData {
      return DungeonMapRenderData(rooms, doors, options)
   }

   override fun toString(): String {
      return "DungeonMapRenderData(rooms=${this.rooms}, doors=${this.doors}, options=${this.options})"
   }

   override fun hashCode(): Int {
      return (this.rooms.hashCode() * 31 + this.doors.hashCode()) * 31 + this.options.hashCode()
   }

   override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is DungeonMapRenderData
            && this.rooms == (other as DungeonMapRenderData).rooms
            && this.doors == (other as DungeonMapRenderData).doors
            && this.options == (other as DungeonMapRenderData).options
         }
   }
}
