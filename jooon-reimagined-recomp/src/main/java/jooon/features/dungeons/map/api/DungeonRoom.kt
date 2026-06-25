package jooon.features.dungeons.map.api

import java.util.ArrayList
import java.util.LinkedHashSet
import jooon.features.dungeons.map.api.DungeonScanner.RoomData
import jooon.features.dungeons.map.api.mapEnums.CheckmarkTypes
import jooon.features.dungeons.map.api.mapEnums.ClearTypes
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.api.mapEnums.ShapeTypes
import jooon.features.dungeons.map.util.WorldUtils
class DungeonRoom(comps: List<WorldComponentPosition>, height: Int) {
   var height: Int
   val comps: MutableList<WorldComponentPosition>
   private val possibleCorners: MutableList<Triple<Int, WorldComponentPosition, WorldPosition>>
   var cores: List<Int>
   var explored: Boolean
   var name: String?
   var roomID: Int?
   var corner: WorldPosition
   var rotation: Int
   var type: RoomTypes
   var checkmark: CheckmarkTypes
   var shape: ShapeTypes
   var totalSecrets: Int
   var secretsCompleted: Int
   var clear: ClearTypes
   val doors: MutableSet<DungeonDoor>
   private var shapeIn: String

   init {
      this.height = height
      this.comps = ArrayList<>()
      this.possibleCorners = ArrayList<>()
      this.cores = emptyList()
      this.corner = WorldPosition.Companion.EMPTY
      this.rotation = -1
      this.type = RoomTypes.UNKNOWN
      this.checkmark = CheckmarkTypes.UNEXPLORED
      this.shape = ShapeTypes.Shape1x1
      this.secretsCompleted = -1
      this.clear = ClearTypes.MOB
      this.doors = LinkedHashSet<>()
      this.shapeIn = ""
      val `this$iv`: java.lang.Iterable = comps
      val `destination$iv$iv`: java.util.Collection = ArrayList(comps.count().coerceAtLeast(10))

      for (`item$iv$iv` in `this$iv`) {
         `destination$iv$iv`.add((`item$iv$iv` as WorldComponentPosition).toComponent())
      }

      this.addComponents(`destination$iv$iv` as MutableList<ComponentPosition>)
   }

   override fun toString(): String {
      return "DungeonRoom[name=\"${this.name}\", type=\"${this.type}\", rotation=\"${this.rotation}\", shape=\"${this.shape}\", checkmark=\"${this.checkmark}\", corner=\"${this.corner}\", cores=\"${this.cores}\"]"
   }

   private fun loadFromData(data: RoomData) {
      this.cores = data.cores
      this.name = data.name
      this.roomID = data.roomID
      var var10001: RoomTypes = RoomTypes.Companion.byName(data.type)
      if (var10001 == null) {
         var10001 = RoomTypes.NORMAL
      }

      this.type = var10001

      this.clear = if (var2 == "mob") ClearTypes.MOB else (if (var2 == "miniboss") ClearTypes.MINIBOSS else ClearTypes.OTHER)
      this.totalSecrets = data.secrets
      this.shapeIn = data.shape
   }

   private fun loadFromCore(core: Int): Boolean {
      for (room in DungeonScanner.roomsData) {
         if (room.cores.contains(core)) {
            this.loadFromData(room)
            return true
         }
      }

      return false
   }

   fun update() {
      sortWith(this.comps, DungeonRoom$update$$inlined$thenBy$1(DungeonRoom$update$$inlined$compareBy$1()))
      this.scan()
      this.shape()
   }

   fun scan(): DungeonRoom {
      val `this24lambda_u243`: DungeonRoom = this

      for (comp in this.comps) {


         if (WorldUtils.isChunkLoaded(x, z)) {
            if (`this24lambda_u243`.height == 0) {
               `this24lambda_u243`.height = DungeonScanner.getHighestY(x, z)
            }

            `this24lambda_u243`.loadFromCore(DungeonScanner.hashCeil$default(DungeonScanner.INSTANCE, x, z, false, 4, null))
         }
      }

      return this
   }

   fun addComponent(comp: ComponentPosition, update: Boolean = true): DungeonRoom {
      val `this24lambda_u246`: DungeonRoom = this
      val w: java.lang.Iterable = this.comps
      var var10000: Boolean
      if (this.comps is java.util.Collection && this.comps.isEmpty()) {
         var10000 = false
      } else {
         val ``: java.util.Iterator = w.iterator()

         while (true) {
            if (!``.hasNext()) {
               var10000 = false
break
            }

            if ((``.next() as WorldComponentPosition).toComponent() == comp) {
               var10000 = true
break
            }
         }
      }

      if (!var10000) {

         `this24lambda_u246`.comps.add(var16)
         val var17: java.lang.Iterable = roomOffset
         var var19: Int = 0

         for (var21 in var17) {

            if (var12 < 0) {
               throwIndexOverflow()
            }

            `this24lambda_u246`.possibleCorners
               .add(Triple(var12, var16, WorldPosition(var16.wx + (var21 as WorldPosition).x, var16.wz + (var21 as WorldPosition).z)))
            }

         if (update) {
            `this24lambda_u246`.update()
         }
      }

      return this
   }

   fun addComponents(comps: List<ComponentPosition>): DungeonRoom {
      val `this24lambda_u247`: DungeonRoom = this

      for (comp in comps) {
         `this24lambda_u247`.addComponent(comp, false)
      }

      `this24lambda_u247`.update()
      return this
   }

   fun findRotation() {
      if (this.height != 0) {
         if (!(this.shapeIn == "1x4") || this.comps.size() >= 4) {
            if (this.type === RoomTypes.FAIRY) {


               this.rotation = 0
               this.corner = WorldPosition(x - 15, z - 15)
            } else {
               this.possibleCorners.removeIf({ p0: Any ->
                  ``(p0)
               })
               if (this.rotation == -1 && !this.possibleCorners.isEmpty()) {
               }
            }
         }
      }
   }

   private fun shape() {
      val ``: java.lang.Iterable = this.comps
      val `this$iv$iv`: java.util.Collection = ArrayList(this.comps.count().coerceAtLeast(10))

      for (`item$iv$iv` in ``) {
         `this$iv$iv`.add((`item$iv$iv` as WorldComponentPosition).cx)
      }

      val var16: java.lang.Iterable = this.comps
      val `destination$iv$ivx`: java.util.Collection = ArrayList(this.comps.count().coerceAtLeast(10))

      for (var20 in var16) {
         `destination$iv$ivx`.add((var20 as WorldComponentPosition).cz)
      }

      this.shape = if (!this.comps.isEmpty() && this.comps.size() <= 4)
         (
            if (this.comps.size() == 1)
               ShapeTypes.Shape1x1
return else
               (
                  if (this.comps.size() == 2)
                     ShapeTypes.Shape1x2
return else
                     (
                        if (this.comps.size() == 4)
                           (if (distCompA != 1 && var13 != 1) ShapeTypes.Shape2x2 else ShapeTypes.Shape1x4)
return else
                           (if (distCompA != this.comps.size() && var13 != this.comps.size()) ShapeTypes.ShapeL else ShapeTypes.Shape1x3)
                     )
               )
         )
return else
         ShapeTypes.Unknown
      }

   private fun rotatePos(x: Int, z: Int, degree: Int): Pair<Int, Int> {
      var var10000: Pair
      when (degree) {
         0 -> var10000 = Pair(x, z)
         90 -> var10000 = Pair(z, -x)
         180 -> var10000 = Pair(-x, -z)
         270 -> var10000 = Pair(-z, x)
         else -> var10000 = Pair(x, z)
      }

      return var10000
   }

   fun fromPos(x: Int, z: Int): Pair<Int, Int>? {
      return if (!this.hasRotation())
return null
return else
         this.rotatePos(x - Math.floor(this.corner.x.toDouble() + 0.5).toInt(), z - Math.floor(this.corner.z.toDouble() + 0.5).toInt(), this.rotation)
      }

   fun fromComp(x: Int, z: Int): Pair<Int, Int>? {
      if (!this.hasRotation()) {
         return null
      } else {

         return Pair((var3.component1() as java.lang.Number).intValue() + this.corner.x, (var3.component2() as java.lang.Number).intValue() + this.corner.z)
      }
   }

   fun hasRotation(): Boolean {
      return this.rotation != -1 && !(this.corner == WorldPosition.Companion.EMPTY)
   }

   companion object {
      val roomOffset: List<WorldPosition>
   }
}
