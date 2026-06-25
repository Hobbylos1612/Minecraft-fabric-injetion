package jooon.features.dungeons.map.api

import java.util.LinkedHashSet
import jooon.features.dungeons.map.api.mapEnums.DoorTypes
import jooon.features.dungeons.map.util.WorldUtils
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks

@SourceDebugExtension(["SMAP\nDungeonDoor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonDoor.kt\njooon/features/dungeons/map/api/DungeonDoor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,60:1\n1726#2,3:61\n*S KotlinDebug\n*F\n+ 1 DungeonDoor.kt\njooon/features/dungeons/map/api/DungeonDoor\n*L\n57#1:61,3\n*E\n"])
public class DungeonDoor(comp: WorldComponentPosition) {
   public final val comp: WorldComponentPosition
   public final var rotation: Int
   public final var opened: Boolean
   public final var type: DoorTypes
   public final val rooms: MutableSet<DungeonRoom>
   public final val roomComp1: ComponentPosition
   public final val roomComp2: ComponentPosition
   public final var holyShitFairyDoorPleaseStopFlashingSobs: Boolean

   init {
      this.comp = comp
      this.rotation = -1
      this.type = DoorTypes.NORMAL
      this.rooms = LinkedHashSet<>()
      val cx: Int = this.comp.cx
      val cz: Int = this.comp.cz
      if ((cx and 1) == 1) {
         this.roomComp1 = ComponentPosition(cx - 1 shr 1, cz shr 1)
         this.roomComp2 = ComponentPosition(cx + 1 shr 1, cz shr 1)
      } else {
         this.roomComp1 = ComponentPosition(cx shr 1, cz - 1 shr 1)
         this.roomComp2 = ComponentPosition(cx shr 1, cz + 1 shr 1)
      }
   }

   public override fun toString(): String {
      return "DungeonDoor[type=\"${this.type}\", rotation=\"${this.rotation}\", opened=\"${this.opened}\"]"
   }

   public fun check() {
      if (!this.opened) {
         val x: Int = this.comp.wx
         val z: Int = this.comp.wz
         if (WorldUtils.INSTANCE.isChunkLoaded(x, z)) {
            val var10000: BlockState = WorldUtils.INSTANCE.getBlockState(x, 69, z)
            if (var10000 != null) {
               this.opened = var10000.method_26215() || var10000.method_26204() == Blocks.field_10499
               val `$this$all$iv`: Block = var10000.method_26204()
               this.type = if (!(`$this$all$iv` == Blocks.field_10492)
                     && !(`$this$all$iv` == Blocks.field_10176)
                     && !(`$this$all$iv` == Blocks.field_10100)
                     && !(`$this$all$iv` == Blocks.field_29224)
                     && !(`$this$all$iv` == Blocks.field_10480)
                     && !(`$this$all$iv` == Blocks.field_10277)
                     && !(`$this$all$iv` == Blocks.field_10387))
                  (
                     if (`$this$all$iv` == Blocks.field_10381)
                        DoorTypes.WITHER
                        else
                        (if (`$this$all$iv` == Blocks.field_10328) DoorTypes.BLOOD else DoorTypes.NORMAL)
                  )
                  else
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
