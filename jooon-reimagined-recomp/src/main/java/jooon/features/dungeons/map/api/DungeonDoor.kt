package jooon.features.dungeons.map.api

import java.util.LinkedHashSet
import jooon.features.dungeons.map.api.mapEnums.DoorTypes
import jooon.features.dungeons.map.util.WorldUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks

class DungeonDoor(comp: WorldComponentPosition) {
   val comp: WorldComponentPosition
   var rotation: Int
   var opened: Boolean
   var type: DoorTypes
   val rooms: MutableSet<DungeonRoom>
   val roomComp1: ComponentPosition
   val roomComp2: ComponentPosition
   var holyShitFairyDoorPleaseStopFlashingSobs: Boolean

   init {
      this.comp = comp
      this.rotation = -1
      this.type = DoorTypes.NORMAL
      this.rooms = LinkedHashSet<>()


      if ((cx and 1) == 1) {
         this.roomComp1 = ComponentPosition(cx - 1 shr 1, cz shr 1)
         this.roomComp2 = ComponentPosition(cx + 1 shr 1, cz shr 1)
      } else {
         this.roomComp1 = ComponentPosition(cx shr 1, cz - 1 shr 1)
         this.roomComp2 = ComponentPosition(cx shr 1, cz + 1 shr 1)
      }
   }

   override fun toString(): String {
      return "DungeonDoor[type=\"${this.type}\", rotation=\"${this.rotation}\", opened=\"${this.opened}\"]"
   }

   fun check() {
      if (!this.opened) {


         if (WorldUtils.isChunkLoaded(x, z)) {

            if (var10000 != null) {
               this.opened = var10000.isAir() || var10000.getBlock() == Blocks.BARRIER
               val `this$iv`: Block = var10000.getBlock()
               this.type = if (!(`this$iv` == Blocks.INFESTED_COBBLESTONE)
                     && !(`this$iv` == Blocks.INFESTED_CHISELED_STONE_BRICKS)
                     && !(`this$iv` == Blocks.INFESTED_CRACKED_STONE_BRICKS)
                     && !(`this$iv` == Blocks.INFESTED_DEEPSLATE)
                     && !(`this$iv` == Blocks.INFESTED_MOSSY_STONE_BRICKS)
                     && !(`this$iv` == Blocks.INFESTED_STONE)
                     && !(`this$iv` == Blocks.INFESTED_STONE_BRICKS))
                  (
                     if (`this$iv` == Blocks.COAL_BLOCK)
                        DoorTypes.WITHER
return else
                        (if (`this$iv` == Blocks.RED_TERRACOTTA) DoorTypes.BLOOD else DoorTypes.NORMAL)
                  )
return else
                  DoorTypes.ENTRANCE
                  if (this.opened && this.holyShitFairyDoorPleaseStopFlashingSobs) {
                  val var10: java.lang.Iterable = this.rooms
                  var var11: Boolean
                  if (this.rooms is java.util.Collection && this.rooms.isEmpty()) {
                     var11 = true
                  } else {
                     val var6: java.util.Iterator = var10.iterator()

                     while (true) {
                        if (!var6.hasNext()) {
                           var11 = true
break
                        }

                        if (!(var6.next() as DungeonRoom).explored) {
                           var11 = false
break
                        }
                     }
                  }

                  if (!var11) {
                     this.type = DoorTypes.WITHER
                  }
               }
            }
         }
      }
   }
}
