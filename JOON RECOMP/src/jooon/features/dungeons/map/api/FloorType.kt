package jooon.features.dungeons.map.api

import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension

public enum class FloorType(floorNum: Int,
   masterMode: Boolean,
   shortName: String,
   roomsW: Int,
   roomsH: Int,
   longName: String = shortName,
   requiredPercent: Double = 1.0,
   requiredSpeed: Int = 600,
   bloodMobs: Int = floorNum + 12
) {
   None(0, false, "", 0, 0, "", 0.0, 0, 0, 448, null),
   Entrance(0, false, "E", 4, 4, "Entrance", 0.3, 1200, 9),
   F1(1, false, "F1", 4, 5, null, 0.3, 0, 0, 416, null),
   F2(2, false, "F2", 5, 5, null, 0.4, 0, 0, 416, null),
   F3(3, false, "F3", 5, 5, null, 0.5, 0, 0, 416, null),
   F4(4, false, "F4", 6, 5, null, 0.6, 720, 0, 288, null),
   F5(5, false, "F5", 6, 6, null, 0.7, 0, 0, 416, null),
   F6(6, false, "F6", 6, 6, null, 0.85, 720, 0, 288, null),
   F7(7, false, "F7", 6, 6, null, 0.0, 840, 0, 352, null),
   M1(1, true, "M1", 4, 5, null, 0.0, 480, 0, 352, null),
   M2(2, true, "M2", 5, 5, null, 0.0, 480, 0, 352, null),
   M3(3, true, "M3", 5, 5, null, 0.0, 480, 0, 352, null),
   M4(4, true, "M4", 6, 5, null, 0.0, 480, 0, 352, null),
   M5(5, true, "M5", 6, 6, null, 0.0, 480, 0, 352, null),
   M6(6, true, "M6", 6, 6, null, 0.0, 480, 0, 352, null),
   M7(7, true, "M7", 6, 6, null, 0.0, 900, 0, 352, null);

   public final val floorNum: Int
   public final val masterMode: Boolean
   public final val shortName: String
   public final val roomsW: Int
   public final val roomsH: Int
   public final val longName: String
   public final val requiredPercent: Double
   public final val requiredSpeed: Int
   public final val bloodMobs: Int
   public final val maxDim: Int

   init {
      this.floorNum = floorNum
      this.masterMode = masterMode
      this.shortName = shortName
      this.roomsW = roomsW
      this.roomsH = roomsH
      this.longName = longName
      this.requiredPercent = requiredPercent
      this.requiredSpeed = requiredSpeed
      this.bloodMobs = bloodMobs
      this.maxDim = Math.max(this.roomsW, this.roomsH)
   }

   @JvmStatic
   fun getEntries(): EnumEntries<FloorType> {
      $ENTRIES
   }

   @SourceDebugExtension(["SMAP\nFloorType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FloorType.kt\njooon/features/dungeons/map/api/FloorType$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,42:1\n1#2:43\n*E\n"])
   public companion object {
      public fun from(name: String): FloorType {
         val var3: java.util.Iterator = (FloorType.getEntries() as java.lang.Iterable).iterator()

         var var10000: Any
         while (true) {
            if (var3.hasNext()) {
               val var4: Any = var3.next()
               if (!((var4 as FloorType).shortName == name)) {
                  continue
               }

               var10000 = (FloorType)var4
               break
            }

            var10000 = null
            break
         }

         var10000 = var10000
         if (var10000 == null) {
            var10000 = FloorType.None
         }

         return var10000
      }
   }
}
