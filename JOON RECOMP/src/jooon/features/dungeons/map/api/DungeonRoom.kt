package jooon.features.dungeons.map.api

import java.util.ArrayList
import java.util.LinkedHashSet
import jooon.features.dungeons.map.api.DungeonScanner.RoomData
import jooon.features.dungeons.map.api.mapEnums.CheckmarkTypes
import jooon.features.dungeons.map.api.mapEnums.ClearTypes
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.api.mapEnums.ShapeTypes
import jooon.features.dungeons.map.util.WorldUtils
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nDungeonRoom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonRoom.kt\njooon/features/dungeons/map/api/DungeonRoom\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,225:1\n1549#2:226\n1620#2,3:227\n1747#2,3:230\n1864#2,3:233\n1549#2:236\n1620#2,3:237\n1549#2:240\n1620#2,3:241\n*S KotlinDebug\n*F\n+ 1 DungeonRoom.kt\njooon/features/dungeons/map/api/DungeonRoom\n*L\n27#1:226\n27#1:227,3\n85#1:230,3\n89#1:233,3\n162#1:236\n162#1:237,3\n163#1:240\n163#1:241,3\n*E\n"])
public class DungeonRoom(comps: List<WorldComponentPosition>, height: Int) {
   public final var height: Int
   public final val comps: MutableList<WorldComponentPosition>
   private final val possibleCorners: MutableList<Triple<Int, WorldComponentPosition, WorldPosition>>
   public final var cores: List<Int>
   public final var explored: Boolean
   public final var name: String?
   public final var roomID: Int?
   public final var corner: WorldPosition
   public final var rotation: Int
   public final var type: RoomTypes
   public final var checkmark: CheckmarkTypes
   public final var shape: ShapeTypes
   public final var totalSecrets: Int
   public final var secretsCompleted: Int
   public final var clear: ClearTypes
   public final val doors: MutableSet<DungeonDoor>
   private final var shapeIn: String

   init {
      this.height = height
      this.comps = ArrayList<>()
      this.possibleCorners = ArrayList<>()
      this.cores = CollectionsKt.emptyList()
      this.corner = WorldPosition.Companion.EMPTY
      this.rotation = -1
      this.type = RoomTypes.UNKNOWN
      this.checkmark = CheckmarkTypes.UNEXPLORED
      this.shape = ShapeTypes.Shape1x1
      this.secretsCompleted = -1
      this.clear = ClearTypes.MOB
      this.doors = LinkedHashSet<>()
      this.shapeIn = ""
      val `$this$map$iv`: java.lang.Iterable = comps
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(comps, 10))

      for (`item$iv$iv` in `$this$map$iv`) {
         `destination$iv$iv`.add((`item$iv$iv` as WorldComponentPosition).toComponent())
      }

      this.addComponents(`destination$iv$iv` as MutableList<ComponentPosition>)
   }

   public override fun toString(): String {
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
      val var2: java.lang.String = data.clear
      this.clear = if (var2 == "mob") ClearTypes.MOB else (if (var2 == "miniboss") ClearTypes.MINIBOSS else ClearTypes.OTHER)
      this.totalSecrets = data.secrets
      this.shapeIn = data.shape
   }

   private fun loadFromCore(core: Int): Boolean {
      for (room in DungeonScanner.INSTANCE.roomsData) {
         if (room.cores.contains(core)) {
            this.loadFromData(room)
            return true
         }
      }

      return false
   }

   public fun update() {
      CollectionsKt.sortWith(this.comps, DungeonRoom$update$$inlined$thenBy$1(DungeonRoom$update$$inlined$compareBy$1()))
      this.scan()
      this.shape()
   }

   public fun scan(): DungeonRoom {
      val `$this$scan_u24lambda_u243`: DungeonRoom = this

      for (comp in this.comps) {
         val x: Int = comp.wx
         val z: Int = comp.wz
         if (WorldUtils.INSTANCE.isChunkLoaded(x, z)) {
            if (`$this$scan_u24lambda_u243`.height == 0) {
               `$this$scan_u24lambda_u243`.height = DungeonScanner.INSTANCE.getHighestY(x, z)
            }

            `$this$scan_u24lambda_u243`.loadFromCore(DungeonScanner.hashCeil$default(DungeonScanner.INSTANCE, x, z, false, 4, null))
         }
      }

      return this
   }

   public fun addComponent(comp: ComponentPosition, update: Boolean = true): DungeonRoom {
      val `$this$addComponent_u24lambda_u246`: DungeonRoom = this
      val w: java.lang.Iterable = this.comps
      var var10000: Boolean
      if (this.comps is java.util.Collection && this.comps.isEmpty()) {
         var10000 = false
      } else {
         val `$i$f$forEachIndexed`: java.util.Iterator = w.iterator()

         while (true) {
            if (!`$i$f$forEachIndexed`.hasNext()) {
               var10000 = false
               break
            }

            if ((`$i$f$forEachIndexed`.next() as WorldComponentPosition).toComponent() == comp) {
               var10000 = true
               break
            }
         }
      }

      if (!var10000) {
         val var16: WorldComponentPosition = comp.withWorld()
         `$this$addComponent_u24lambda_u246`.comps.add(var16)
         val var17: java.lang.Iterable = roomOffset
         var var19: Int = 0

         for (var21 in var17) {
            val var12: Int = var19++
            if (var12 < 0) {
               CollectionsKt.throwIndexOverflow()
            }

            `$this$addComponent_u24lambda_u246`.possibleCorners
               .add(Triple(var12, var16, WorldPosition(var16.wx + (var21 as WorldPosition).x, var16.wz + (var21 as WorldPosition).z)))
            }

         if (update) {
            `$this$addComponent_u24lambda_u246`.update()
         }
      }

      return this
   }

   public fun addComponents(comps: List<ComponentPosition>): DungeonRoom {
      val `$this$addComponents_u24lambda_u247`: DungeonRoom = this

      for (comp in comps) {
         `$this$addComponents_u24lambda_u247`.addComponent(comp, false)
      }

      `$this$addComponents_u24lambda_u247`.update()
      return this
   }

   public fun findRotation() {
      if (this.height != 0) {
         if (!(this.shapeIn == "1x4") || this.comps.size() >= 4) {
            if (this.type === RoomTypes.FAIRY) {
               val x: Int = (this.comps.get(0) as WorldComponentPosition).wx
               val z: Int = (this.comps.get(0) as WorldComponentPosition).wz
               this.rotation = 0
               this.corner = WorldPosition(x - 15, z - 15)
            } else {
               this.possibleCorners.removeIf({ p0: Any ->
                  `$tmp0`(p0)
               })
               if (this.rotation == -1 && !this.possibleCorners.isEmpty()) {
               }
            }
         }
      }
   }

   private fun shape() {
      val `$i$f$map`: java.lang.Iterable = this.comps
      val `$this$mapTo$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(this.comps, 10))

      for (`item$iv$iv` in `$i$f$map`) {
         `$this$mapTo$iv$iv`.add((`item$iv$iv` as WorldComponentPosition).cx)
      }

      val distCompA: Int = CollectionsKt.distinct(`$this$mapTo$iv$iv` as java.util.List).size()
      val var16: java.lang.Iterable = this.comps
      val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(this.comps, 10))

      for (var20 in var16) {
         `destination$iv$ivx`.add((var20 as WorldComponentPosition).cz)
      }

      val var13: Int = CollectionsKt.distinct(`destination$iv$ivx` as java.util.List).size()
      this.shape = if (!this.comps.isEmpty() && this.comps.size() <= 4)
         (
            if (this.comps.size() == 1)
               ShapeTypes.Shape1x1
               else
               (
                  if (this.comps.size() == 2)
                     ShapeTypes.Shape1x2
                     else
                     (
                        if (this.comps.size() == 4)
                           (if (distCompA != 1 && var13 != 1) ShapeTypes.Shape2x2 else ShapeTypes.Shape1x4)
                           else
                           (if (distCompA != this.comps.size() && var13 != this.comps.size()) ShapeTypes.ShapeL else ShapeTypes.Shape1x3)
                     )
               )
         )
         else
         ShapeTypes.Unknown
      }

   private fun rotatePos(x: Int, z: Int, degree: Int): Pair<Int, Int> {
      var var10000: Pair
      when (degree) {
         0 -> var10000 = TuplesKt.to(x, z)
         90 -> var10000 = TuplesKt.to(z, -x)
         180 -> var10000 = TuplesKt.to(-x, -z)
         270 -> var10000 = TuplesKt.to(-z, x)
         else -> var10000 = TuplesKt.to(x, z)
      }

      return var10000
   }

   public fun fromPos(x: Int, z: Int): Pair<Int, Int>? {
      return if (!this.hasRotation())
         null
         else
         this.rotatePos(x - (int)Math.floor((double)this.corner.x + 0.5), z - (int)Math.floor((double)this.corner.z + 0.5), this.rotation)
      }

   public fun fromComp(x: Int, z: Int): Pair<Int, Int>? {
      if (!this.hasRotation()) {
         return null
      } else {
         val var3: Pair = this.rotatePos(x, z, 360 - this.rotation)
         return TuplesKt.to(
            (var3.component1() as java.lang.Number).intValue() + this.corner.x, (var3.component2() as java.lang.Number).intValue() + this.corner.z
         )
      }
   }

   public fun hasRotation(): Boolean {
      return this.rotation != -1 && !(this.corner == WorldPosition.Companion.EMPTY)
   }

   public companion object {
      public final val roomOffset: List<WorldPosition>
   }
}
