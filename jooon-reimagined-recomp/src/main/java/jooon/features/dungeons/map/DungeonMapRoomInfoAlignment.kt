package jooon.features.dungeons.map

import kotlin.enums.EnumEntries
enum class DungeonMapRoomInfoAlignment(str: String) {
   TopLeft("Top Left"),
   TopRight("Top Right"),
   BottomLeft("Bottom Left"),
   BottomRight("Bottom Right"),
   Center("Center");

   val str: String

   init {
      this.str = str
   }

   
   fun getEntries(): EnumEntries<DungeonMapRoomInfoAlignment> {
      $ENTRIES
   }

   @SourceDebugExtension(["SMAP\nDungeonMapRenderOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonMapRenderOptions.kt\njooon/features/dungeons/map/DungeonMapRoomInfoAlignment$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,57:1\n1#2:58\n*E\n"])
   companion object {
      fun from(name: String): DungeonMapRoomInfoAlignment {
         val var3: java.util.Iterator = (DungeonMapRoomInfoAlignment.getEntries() as java.lang.Iterable).iterator()

         var var10000: Any
         while (true) {
            if (var3.hasNext()) {

               if (!((var4 as DungeonMapRoomInfoAlignment).str == name)) {
return continue
               }

               var10000 = (DungeonMapRoomInfoAlignment)var4
break
            }

            var10000 = null
break
         }

         var10000 = var10000
         if (var10000 == null) {
            var10000 = DungeonMapRoomInfoAlignment.Center
         }

         return var10000
      }
   }
}
