package jooon.api.dungeon

import java.util.Locale
import kotlin.enums.EnumEntries
enum class RoomTypes(prio: Int) {
   BLOOD(0),
   ENTRANCE(10),
   PUZZLE(20),
   RARE(30),
   YELLOW(40),
   TRAP(50),
   UNKNOWN(60),
   FAIRY(70),
   NORMAL(80);

   val prio: Int

   init {
      this.prio = prio
   }

   
   fun getEntries(): EnumEntries<RoomTypes> {
      $ENTRIES
   }

   @SourceDebugExtension(["SMAP\nRoomTypes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RoomTypes.kt\njooon/api/dungeon/RoomTypes$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,19:1\n1#2:20\n*E\n"])
   companion object {
      fun byName(name: String): RoomTypes? {
         val var3: java.util.Iterator = (RoomTypes.getEntries() as java.lang.Iterable).iterator()

         var var10000: Any
         while (true) {
            if (var3.hasNext()) {



               if (!(var7 == var10001)) {
return continue
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
