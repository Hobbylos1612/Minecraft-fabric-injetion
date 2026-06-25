package jooon.features.dungeons.map.api

import java.awt.Color
import kotlin.enums.EnumEntries

enum class DungeonClass(shortName: String, singleLetter: Char, colorCode: String, color: Color) {
   Archer("Arch", 'a', "§c", Color(255, 85, 85, 255)),
   Berserk("Bers", 'b', "§6", Color(255, 170, 0, 255)),
   Mage("Mage", 'm', "§3", Color(0, 170, 170, 255)),
   Healer("Heal", 'h', "§5", Color(170, 0, 170, 255)),
   Tank("Tank", 't', "§a", Color(85, 255, 85, 255)),
   Unknown("Unknown", '\u0000', "", Color(0, 0, 0, 0));

   val shortName: String
   val singleLetter: Char
   val colorCode: String
   val color: Color
   val colorRgb: Int

   init {
      this.shortName = shortName
      this.singleLetter = singleLetter
      this.colorCode = colorCode
      this.color = color
      this.colorRgb = this.color.getRGB()
   }

   
   fun getEntries(): EnumEntries<DungeonClass> {
      $ENTRIES
   }

   companion object {
      fun from(fullName: String): DungeonClass {
         when (fullName.hashCode()) {
            -2137396043 -> {
               if (fullName.equals("Healer")) {
                  return DungeonClass.Healer
               }
            }
            2390418 -> {
               if (fullName.equals("Mage")) {
                  return DungeonClass.Mage
               }
            }
            2599178 -> {
               if (fullName.equals("Tank")) {
                  return DungeonClass.Tank
               }
            }
            1446053114 -> {
               if (fullName.equals("Berserk")) {
                  return DungeonClass.Berserk
               }
            }
            1969228707 -> {
               if (fullName.equals("Archer")) {
                  return DungeonClass.Archer
               }
            }
            else -> {}
         }

         return DungeonClass.Unknown
      }
   }
}
