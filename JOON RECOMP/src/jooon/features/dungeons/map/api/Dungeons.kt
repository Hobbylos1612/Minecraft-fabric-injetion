package jooon.features.dungeons.map.api

import java.util.concurrent.ConcurrentHashMap

public object Dungeons {
   public final val players: ConcurrentHashMap<String, DungeonPlayer> = ConcurrentHashMap()
   public final val playerClasses: ConcurrentHashMap<String, DungeonClass> = ConcurrentHashMap()
   public final var floor: FloorType = FloorType.None
   public final var inBoss: Boolean
   public final var started: Boolean

   public fun reset() {
      players.clear()
      playerClasses.clear()
      floor = FloorType.None
      inBoss = false
      DungeonScanner.INSTANCE.reset()
   }
}
