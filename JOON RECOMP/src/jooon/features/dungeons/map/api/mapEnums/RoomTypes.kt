package jooon.features.dungeons.map.api.mapEnums

import java.util.Locale
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension

public enum class RoomTypes(prio: Int) {
   BLOOD(0),
   ENTRANCE(10),
   PUZZLE(20),
   RARE(30),
   YELLOW(40),
   TRAP(50),
   UNKNOWN(60),
   FAIRY(70),
   NORMAL(80);

   public final val prio: Int

   init {
      this.prio = prio
   }

   @JvmStatic
   fun getEntries(): EnumEntries<RoomTypes> {
      $ENTRIES
   }

   @SourceDebugExtension(["SMAP\nRoomTypes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RoomTypes.kt\njooon/features/dungeons/map/api/mapEnums/RoomTypes$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,19:1\n1#2:20\n*E\n"])
   public companion object {
      public fun byName(name: String): RoomTypes? {
         val var3: java.util.Iterator = (RoomTypes.getEntries() as java.lang.Iterable).iterator()

         var var10000: Any
         while (true) {
            if (var3.hasNext()) {
               val var4: Any = var3.next()
               val var7: java.lang.String = (var4 as RoomTypes).name()
               val var10001: java.lang.String = name.toUpperCase(Locale.ROOT)
               if (!(var7 == var10001)) {
                  continue
               }

               var10000 = var4
               break
            }

            var10000 = null
            break
         }

         return var10000 as RoomTypes
      }
   }
}
