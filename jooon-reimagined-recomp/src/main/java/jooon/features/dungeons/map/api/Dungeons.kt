package jooon.features.dungeons.map.api

import java.util.concurrent.ConcurrentHashMap

object Dungeons {
   val players: ConcurrentHashMap<String, DungeonPlayer> = ConcurrentHashMap()
   val playerClasses: ConcurrentHashMap<String, DungeonClass> = ConcurrentHashMap()
   var floor: FloorType = FloorType.None
   var inBoss: Boolean
   var started: Boolean

   fun reset() {
      players.clear()
      playerClasses.clear()
      floor = FloorType.None
      inBoss = false
      DungeonScanner.reset()
   }
}
