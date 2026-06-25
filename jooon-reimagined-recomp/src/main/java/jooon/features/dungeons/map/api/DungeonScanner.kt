package jooon.features.dungeons.map.api

import com.google.gson.Gson
import com.mojang.authlib.GameProfile
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashSet
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import jooon.features.dungeons.map.DungeonMapFeature
import jooon.features.dungeons.map.api.mapEnums.CheckmarkTypes
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.util.MathUtils
import jooon.features.dungeons.map.util.WorldUtils
import jooon.util.Utils
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.text.MatchResult.Destructured
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.SlabBlock
import net.minecraft.block.enums.SlabType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.fluid.FluidState
import net.minecraft.item.map.MapDecoration
import net.minecraft.item.map.MapDecorationTypes
import net.minecraft.item.map.MapState
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.FluidTags
import net.minecraft.state.property.Property
import net.minecraft.text.Text

object DungeonScanner {
   val roomsData: List<jooon.features.dungeons.map.api.DungeonScanner.RoomData>
   var lastIdx: Int?
   var currentRoom: DungeonRoom?
   var rooms: MutableList<DungeonRoom?>
   var doors: MutableList<DungeonDoor?>
   var availablePos: MutableList<WorldComponentPosition>
   private var worldChangeCooldown: Int
   private var foundEntrance: Int
   private var wasInEntrance: Boolean
   private const val COLOR_SIZE: Int = 16384
   private const val SCAN: Int = 128
   private const val ROOM_SPACING: Int = 4
   var roomSize: Int
   var roomGap: Int
   var mapOffsetX: Int
   var mapOffsetZ: Int
   var mapWidth: Int
   var mapHeight: Int
   private val playerInfoRegex: Regex

   private fun findAvailablePos(): MutableList<WorldComponentPosition> {
      val pos: java.util.List = ArrayList()

      repeat(10) { z ->
         repeat(10) { x ->
            if (x % 2 == 0 || z % 2 == 0) {
               pos.add(ComponentPosition(x, z).withWorld())
            }
         }
      }

      return pos
   }

   fun getHighestY(x: Int, z: Int): Int {
      if (WorldUtils.getWorld() == null) {
         return -1
      } else {
         var height: Int = 0

         for (idx in 256 downTo 0) {

            if (blockState != null) {

               if (var10000 != null && !blockState.isAir() && !(var10000 == Blocks.GOLD_BLOCK)) {
                  height = idx
break
               }
            }
         }

         return height
      }
   }

   fun getLegacyId(blockState: BlockState, debug: Boolean): Int {

      var registryName: String = WorldUtils.registryName(var10000)

      if (!var7.isEmpty()) {
         if (var7.isIn(FluidTags.WATER)) {
            if (var7.isStill()) 9 else 8
         }

         if (var7.isIn(FluidTags.LAVA)) {
            if (var7.isStill()) 11 else 10
         }
      }

      if (var10000 is SlabBlock) {

         registryName = "$registryName[type=$var10001]"
      }

      LegacyRegistry.BLOCKS.get(registryName) as Int
   }

   @JvmOverloads
   fun hashCeil(x: Int, z: Int, debug: Boolean = false): Int {
      var str: String = ""

      for (idx in 140 downTo 12) {

         if (var10000 != null) {

            if (var9 != null) {

               if (var10 != null) {

                  if (!(var9 == Blocks.IRON_BARS) && !(var9 == Blocks.CHEST)) {
                     str = "$str$blockId"
                  } else {
                     str = "$str0"
                  }
               }
            }
         }
      }

      return str.hashCode()
   }

   private fun checkDoorState() {
      for (door in doors) {
         if (door != null && !door.opened) {
            door.check()
         }
      }
   }

   private fun checkRoomState() {
      for (room in rooms) {
         if (room != null && room.rotation == -1) {
            room.findRotation()
         }
      }
   }

   fun tick() {
      if (Utils.inDungeon) {

         if (var10000 != null) {
            if (var10000.age >= 60) {
               if (var10000.age % 20 == 0) {
                  this.scanTabList()
               }

               val var14: java.util.Collection = Dungeons.players.values()

               for (`element$iv` in var14) {
                  (`element$iv` as DungeonPlayer).tick()
               }

               worldChangeCooldown += -1
               if (worldChangeCooldown < 0) {
                  if (WorldUtils.isChunkLoaded(var10000.getX(), var10000.getZ())) {

                     if (0 <= var10 && var10 < 36) {
                        this.scan()

                        if (!wasInEntrance) {
                           if ((if (var11 != null) var11.type else null) === RoomTypes.ENTRANCE) {
                              wasInEntrance = true
                           } else if (foundEntrance > 0) {
return return
                           }
                        }

                        this.checkRoomState()
                        this.checkDoorState()
                        currentRoom = rooms.get(var10)
                        if (currentRoom != null) {
                           currentRoom.explored = true
                        }

                        if (lastIdx == null || lastIdx != var10) {
                           lastIdx = var10
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private fun scanTabList() {


      if (var12 != null) {
         run label69@{
            var13 = var12.getPlayerList()
            if (var10000.player != null) {

               if (var14 != null) {
                  var15 = var14.name()
                  if (var15 != null) {
                     return@label69
                  }
               }
            }

            var15 = ""
         }


         for (var16 in var13) {
            var info: PlayerListEntry
            run label72@{
               info = var16 as PlayerListEntry

               if (var17 != null) {
                  var18 = var17.getString()
                  if (var18 != null) {
                     return@label72
                  }
               }

               var18 = info.getProfile().name()
            }

            var var19: String = var18
            if (var18 == null) {
               var19 = ""
            }


            if (match != null) {

               if (var20 != null) {

                  if (var21 != null) {

                     if (var22 != null) {

                        if (var23 != null) {

                           this.addDungeonPlayer(var24, var21, var23)
                        }
                     }
                  }
               }
            } else if (cleanName == localName || info.getProfile().name() == localName) {

               this.addDungeonPlayer(var10001, localName, "Unknown")
            }
         }
      }
   }

   fun onPlayerInfoPacket(packet: PlayerListS2CPacket) {
      if (packet.getActions().contains(Action.ADD_PLAYER)) {
         var var19: String
         run label57@{

            if (var10000 != null) {

               if (var18 != null) {
                  var19 = var18.name()
                  if (var19 != null) {
                     return@label57
                  }
               }
            }

            var19 = ""
         }

         val var20: java.util.List = packet.getEntries()

         for (`element$iv` in var20) {


            if (var21 != null) {
               var name: String
               run label60@{
                  name = var21.name()

                  if (var22 != null) {
                     var19 = var22.getString()
                     if (var19 != null) {
                        return@label60
                     }
                  }

                  var19 = name
               }


               if (match != null) {






                  var24.addDungeonPlayer(var10001, pName, role)
               } else if (name == localName || cleanName == localName) {


                  var25.addDungeonPlayer(var26, localName, "Unknown")
               }
            }
         }
      }
   }

   private fun addDungeonPlayer(uuid: UUID, name: String, roleStr: String) {
      val `this$iv`: ConcurrentMap = Dungeons.players
      var var10000: Any = `this$iv`.get(name)
      if (var10000 == null) {

         newRole.role = DungeonClass.Companion.from(roleStr)
         var10000 = `this$iv`.putIfAbsent(name, newRole)
         if (var10000 == null) {
            var10000 = newRole
         }
      }


      if (var13 != DungeonClass.Unknown) {
         var11.role = var13
      }
   }

   private fun scanMapDimensions(colors: ByteArray): Boolean {
      var floor: FloorType = Dungeons.floor
      if (floor === FloorType.None) {
         floor = FloorType.F7
      }

      var bestSize: Int = 0
      val bestPixelL: java.util.Iterator = rooms.iterator()

      var var10000: Int
      while (true) {
         if (!bestPixelL.hasNext()) {
            var10000 = -1
break
         }

         if ((if (idx != null) idx.type else null) === RoomTypes.ENTRANCE) {
            var10000 = bestSize
break
         }

         bestSize++
      }

      if (var10000 == -1) {
         return false
      } else {


         bestSize = 0
         var var22: Int = -1
         var var23: Int = -1
         var var24: Int = 0

         for (var25 in colors.length..var24) {

            if (29 <= colors[var24] && colors[var24] < 32) {

               var l: Int = var24
               var r: Int = var24

               while (l % 128 != 0) {

                  if (var26 == null || var26 != colByte) {
break
                  }

                  l--
               }

               while ((r + 1) % 128 != 0) {

                  if (var27 == null || var27 != colByte) {
break
                  }

                  r++
               }

               var t: Int = var24
               var b: Int = var24

               while (t >= 128) {

                  if (var28 == null || var28 != colByte) {
break
                  }

                  t -= 128
               }

               while (b + 128 < colors.length) {

                  if (var29 == null || var29 != colByte) {
break
                  }

                  b += 128
               }

               if (r % 128 - l % 128 + 1 >= 6 && r % 128 - l % 128 + 1 == b / 128 - t / 128 + 1 && r % 128 - l % 128 + 1 > bestSize) {
                  bestSize = currentW
                  var22 = l % 128
                  var23 = t / 128
               }
            }
         }

         if (bestSize < 6) {
            return false
         } else {
            roomSize = bestSize
            roomGap = roomSize + 4
            mapOffsetX = var22 - var19 * roomGap
            mapOffsetZ = var23 - var20 * roomGap
            mapWidth = roomGap * (floor.roomsW - 1) + roomSize
            mapHeight = roomGap * (floor.roomsH - 1) + roomSize
            return true
         }
      }
   }

   fun updateRooms(colors: ByteArray, mapState: MapState) {
      if (colors.length >= 16384) {
         if (colors[0] == DungeonScanner.MapColors.EMPTY.color || colors[0] == 0) {
            val `this$iv`: java.lang.Iterable = rooms
            var `index$iv`: Int = 0

            for (`item$iv` in `this$iv`) {

               if (var8 < 0) {
                  throwIndexOverflow()
               }

               if (`item$iv` as DungeonRoom != null) {



                  var var43: Byte = getOrNull(colors, mrx + (mapOffsetZ + z * roomGap) * 128)
                  if (var43 != null) {

                     if (roomCol != DungeonScanner.MapColors.EMPTY.color) {
                        if (room_.type === RoomTypes.UNKNOWN) {
                           room_.type = if (roomCol == DungeonScanner.MapColors.ROOM_ENTRANCE.color)
                              RoomTypes.ENTRANCE
return else
                              (
                                 if (roomCol == DungeonScanner.MapColors.ROOM_BLOOD.color)
                                    RoomTypes.BLOOD
return else
                                    (
                                       if (roomCol == DungeonScanner.MapColors.ROOM_BOSS.color)
                                          RoomTypes.YELLOW
return else
                                          (
                                             if (roomCol == DungeonScanner.MapColors.ROOM_FAIRY.color)
                                                RoomTypes.FAIRY
return else
                                                (
                                                   if (roomCol == DungeonScanner.MapColors.ROOM_NORMAL.color)
                                                      RoomTypes.NORMAL
return else
                                                      (
                                                         if (roomCol == DungeonScanner.MapColors.ROOM_PUZZLE.color)
                                                            RoomTypes.PUZZLE
return else
                                                            (if (roomCol == DungeonScanner.MapColors.ROOM_TRAP.color) RoomTypes.TRAP else RoomTypes.UNKNOWN)
                                                      )
                                                )
                                          )
                                    )
                              )
                           }

                        if (!room_.explored) {
                           room_.explored = roomCol != DungeonScanner.MapColors.ROOM_UNOPENED.color
                        }

                        var var36: Any = room_.checkmark
                        var `this$iv`: Int = 0

                        for (`` in roomSize..`this$iv`) {
                           var dz: Int = 0

                           for (`element$iv` in roomSize..dz) {


                              if (dec >= 0 && dec < 128 && mrz + dz >= 0 && mrz + dz < 128) {
                                 var43 = getOrNull(colors, dec + var25 * 128)
                                 if (var43 != null) {

                                    if (pixelCol != DungeonScanner.MapColors.EMPTY.color) {
                                       var var45: CheckmarkTypes
                                       when (pixelCol) {
                                          17, 18, 19 -> var45 = CheckmarkTypes.FAILED
                                          29, 30, 31 -> var45 = if (29 <= roomCol && roomCol < 32) null else CheckmarkTypes.GREEN
                                          33, 34, 35 -> var45 = CheckmarkTypes.WHITE
                                          118, 119, 120 -> var45 = CheckmarkTypes.UNEXPLORED
                                          else -> var45 = null
                                       }

                                       if (var45 != null && var45.prio > var36.prio) {
                                          var36 = var45
                                       }
                                    }
                                 }
                              }
                           }
                        }

                        val var46: java.lang.Iterable = mapState.getDecorations()

                        for (var40 in var46) {



                           if (dxx >= mrx && dxx < mrx + roomSize && dzx >= mrz && dzx < mrz + roomSize) {


                                 CheckmarkTypes.WHITE
return else
                                 (
                                    if (var33 == MapDecorationTypes.BANNER_GREEN)
                                       CheckmarkTypes.GREEN
return else
                                       (if (var33 == MapDecorationTypes.RED_X) CheckmarkTypes.FAILED else null)
                                 )
                                 if (detected != null && detected.prio > var36.prio) {
                                 var36 = detected
                              }
                           }
                        }

                        room_.checkmark = var36
                     }
                  }
               }
            }
         }
      }
   }

   fun updatePlayerIcons(mapState: MapState) {

      if (!players.isEmpty()) {
         if (Dungeons.floor != FloorType.None) {
            var var57: String
            run label147@{

               if (var10000.player != null) {

                  if (var56 != null) {
                     var57 = var56.name()
                     if (var57 != null) {
                        return@label147
                     }
                  }
               }

               var57 = ""
            }

            val var58: java.lang.Iterable = mapState.getDecorations()
            var unassignedTeammates: java.util.List = toList(var58)
            val unassignedDecs: java.util.Collection = ArrayList()

            for (`this$iv` in unassignedTeammates) {
               if (!((`this$iv` as MapDecoration).type() == MapDecorationTypes.FRAME)) {
                  unassignedDecs.add(`this$iv`)
               }
            }

            val potentialTeammateDecs: java.util.List = unassignedDecs as java.util.List
            val var21: java.util.Set = LinkedHashSet()
            val var22: java.util.Set = LinkedHashSet()

            for (var32 in potentialTeammateDecs) {


               if (var59 != null) {
                  var57 = var59.getString()
                  if (var57 != null) {
                     var57 = Utils.stripColor(var57)
                     if (var57 != null) {
                        var57 = trim(var57).toString()
                        if (var57 != null) {
                           val `element$iv`: DungeonPlayer = players.get(var57) as DungeonPlayer
                           if (`element$iv` != null) {
                              if (!(var57 == localName)) {

                                 var63.processDecForPlayer(`element$iv`, var36)
                              }

                              var21.add(var57)
                              var22.add(var36)
                           }
                        }
                     }
                  }
               }
            }

            val var64: java.util.Collection = players.values()
            val var33: java.lang.Iterable = var64
            val `destination$iv$ivx`: java.util.Collection = ArrayList()

            for (var48 in var33) {
               if (!var21.contains((var48 as DungeonPlayer).name) && !((var48 as DungeonPlayer).name == localName)) {
                  `destination$iv$ivx`.add(var48)
               }
            }

            unassignedTeammates = `destination$iv$ivx` as java.util.List
            val var38: java.lang.Iterable = potentialTeammateDecs
            val `destination$iv$ivxx`: java.util.Collection = ArrayList()

            for (var51 in var38) {
               if (!var22.contains(var51 as MapDecoration)) {
                  `destination$iv$ivxx`.add(var51)
               }
            }

            val var27: java.util.List = `destination$iv$ivxx` as java.util.List
            var var31: Boolean = false
            var var35: Int = 0

            for (var50 in var27) {

               if (!var31 && (var50 as MapDecoration).type() == MapDecorationTypes.PLAYER) {
                  var31 = true
               } else if (var35 < unassignedTeammates.size()) {


                  var35 += 1
                  var var10001: DungeonPlayer = (DungeonPlayer)unassignedTeammates.get(var55)
                  var10001 = var10001
                  var65.processDecForPlayer(var10001, var52)
               }
            }
         }
      }
   }

   fun processDecForPlayer(player: DungeonPlayer, dec: MapDecoration) {

      player.updatePosition(
         PlayerComponentPosition(
            MathUtils.INSTANCE
               .rescale(
                  (dec.x().toDouble() + 128.0) * 0.5, mapOffsetX.toDouble(), (mapOffsetX + roomGap * floor.roomsW).toDouble(), 0.0, floor.roomsW.toDouble() * 2.0
               ),
            MathUtils.INSTANCE
               .rescale(
                  (dec.z().toDouble() + 128.0) * 0.5, mapOffsetZ.toDouble(), (mapOffsetZ + roomGap * floor.roomsH).toDouble(), 0.0, floor.roomsH.toDouble() * 2.0
               ),
            dec.rotation().toDouble() * 360.0 / 16.0 + 180.0
         )
      )
   }

   fun onMapPacket(packet: MapUpdateS2CPacket, mapState: MapState) {
      var var10000: ByteArray = mapState.colors
      if (var10000.length != 0 && roomSize == -1 && this.scanMapDimensions(var10000)) {
      }

      if (roomSize != -1) {
         this.updatePlayerIcons(mapState)
         var10000 = mapState.colors
         this.updateRooms(var10000, mapState)
         DungeonMapFeature.redrawMap(toList(rooms), toList(doors))
      } else {

         if (var6 != null && var6.age % 100 == 0) {
         }
      }
   }

   fun mergeRooms(comp1: ComponentPosition, comp2: ComponentPosition): Boolean {




      if (r1 != null && r2 != null) {
         if (i1 < i2) {
            this.mergeRooms(r1, r2)
         } else {
            this.mergeRooms(r2, r1)
         }

         return true
      } else if (r1 == null && r2 == null) {
         return false
      } else {
         val var9: DungeonRoom
         val var10: ComponentPosition
         if (r1 == null) {
            var9 = r2
            var10 = comp1
         } else {
            var9 = r1
            var10 = comp2
         }

         DungeonRoom.addComponent$default(var9, var10, false, 2, null)
         addRoom$default(this, var10, var9, false, 4, null)
         return true
      }
   }

   fun mergeRooms(room1: DungeonRoom, room2: DungeonRoom) {
      if (room1 != room2) {
         if (room1.type != RoomTypes.ENTRANCE && room2.type != RoomTypes.ENTRANCE) {
            for (`` in room2.comps) {

               room1.addComponent(c, false)
               this.addRoom(c, room1, true)
            }

            room1.update()
            if (room2.explored) {
               room1.explored = true
            }

            for (`element$iv` in room2.doors) {
               (`element$iv` as DungeonDoor).rooms.remove(room2)
            }
         }
      }
   }

   fun addDoor(door: DungeonDoor) {


      if (0 <= idx && idx < 60) {
         doors.set(idx, door)

         for (`element$iv` in comp.getNeighboringRooms()) {

            if (var10000 != null) {
               var10000.doors.add(door)
               door.rooms.add(var10000)
            }
         }
      }
   }

   fun addRoom(comp: ComponentPosition, room: DungeonRoom, force: Boolean = false) {

      if (0 <= idx && idx < 36) {
         if (!force) {
            val `this$iv`: DungeonRoom = rooms.get(idx)
            if (`this$iv` != null) {
               if (room.name == null) {
                  mergeRooms(`this$iv`, room)
               } else {
                  mergeRooms(room, `this$iv`)
               }
return return
            }
         }

         rooms.set(idx, room)

         for (`element$iv` in comp.getNeighboringDoors()) {

            if (var10000 != null) {
               var10000.rooms.add(room)
               room.doors.add(var10000)
            }
         }
      }
   }

   fun reset() {
      worldChangeCooldown = 5
      foundEntrance = 5
      wasInEntrance = false
      Collections.fill(rooms, null)
      Collections.fill(doors, null)
      lastIdx = null
      currentRoom = null
      availablePos = asReversedMutable(this.findAvailablePos())
      roomSize = -1
      roomGap = -1
      mapOffsetX = -1
      mapOffsetZ = -1
      mapWidth = -1
      mapHeight = -1
   }

   fun scan() {
      foundEntrance += -1
      if (!availablePos.isEmpty()) {


         availablePos.removeIf({ p0: Any ->
            ``(p0)
         })
         if (var3.element) {
            DungeonMapFeature.redrawMap(toList(rooms), toList(doors))
         }
      }
   }

   @JvmOverloads
   fun hashCeil(x: Int, z: Int): Int {
      hashCeil$default(this, x, z, false, 4, null)
   }

   
   fun {


      val var10001: BufferedReader
      if (var1 != null) {

         var10001 = if (it is BufferedReader) it as BufferedReader else BufferedReader(it, 8192)
      } else {
         var10001 = null
      }

      var var15: java.lang.Throwable = null

      var var19: String
      try {
         var19 = if (var12 as BufferedReader != null) TextStreamsKt.readText(var12 as BufferedReader) else null
      } catch (var9: java.lang.Throwable) {
         var15 = var9
         throw var9
      } finally {
         var12.close()
      }

      roomsData = toList(var25 as Array<Any>)
      var var0: Byte = 36


      repeat(var0) { var16 ->
         var13.add(null)
      }

      rooms = var13
      var0 = 60


      repeat(var0) { var17 ->
         var14.add(null)
      }

      doors = var14
      availablePos = findAvailablePos()
      worldChangeCooldown = 5
      foundEntrance = 5
      roomSize = -1
      roomGap = -1
      mapOffsetX = -1
      mapOffsetZ = -1
      mapWidth = -1
      mapHeight = -1
      playerInfoRegex = Regex("^\\[\\d+\\] (?:.* )?(\\w+).*?\\((\\w+)(?: (\\w+))?\\)$")
   }

   private enum class MapColors(color: Byte) {
      EMPTY(0.toByte()),
      CHECK_WHITE(34.toByte()),
      CHECK_GREEN(30.toByte()),
      CHECK_FAIL(18.toByte()),
      CHECK_UNKNOWN(119.toByte()),
      ROOM_ENTRANCE(30.toByte()),
      ROOM_NORMAL(63.toByte()),
      ROOM_UNOPENED(85.toByte()),
      ROOM_TRAP(62.toByte()),
      ROOM_BOSS(74.toByte()),
      ROOM_PUZZLE(66.toByte()),
      ROOM_FAIRY(82.toByte()),
      ROOM_BLOOD(18.toByte()),
      DOOR_WITHER(119.toByte()),
      DOOR_BLOOD(18.toByte());

      val color: Byte

      init {
         this.color = color
      }

      
      fun getEntries(): EnumEntries<DungeonScanner.MapColors> {
         $ENTRIES
      }
   }

   data class RoomData(name: String,
      type: String,
      secrets: Int,
      cores: List<Int>,
      trappedChests: Int,
      roomID: Int,
      clear: String?,
      crypts: Int,
      clearScore: Int?,
      secretScore: Int?,
      shape: String
   ) {
      val name: String
      val type: String
      val secrets: Int
      val cores: List<Int>
      val trappedChests: Int
      val roomID: Int
      val clear: String?
      val crypts: Int
      val clearScore: Int?
      val secretScore: Int?
      val shape: String

      init {
         this.name = name
         this.type = type
         this.secrets = secrets
         this.cores = cores
         this.trappedChests = trappedChests
         this.roomID = roomID
         this.clear = clear
         this.crypts = crypts
         this.clearScore = clearScore
         this.secretScore = secretScore
         this.shape = shape
      }

      public operator fun component1(): String {
         return this.name
      }

      public operator fun component2(): String {
         return this.type
      }

      public operator fun component3(): Int {
         return this.secrets
      }

      public operator fun component4(): List<Int> {
         return this.cores
      }

      public operator fun component5(): Int {
         return this.trappedChests
      }

      public operator fun component6(): Int {
         return this.roomID
      }

      public operator fun component7(): String? {
         return this.clear
      }

      public operator fun component8(): Int {
         return this.crypts
      }

      public operator fun component9(): Int? {
         return this.clearScore
      }

      public operator fun component10(): Int? {
         return this.secretScore
      }

      public operator fun component11(): String {
         return this.shape
      }

      fun copy(
         name: String = this.name,
         type: String = this.type,
         secrets: Int = this.secrets,
         cores: List<Int> = this.cores,
         trappedChests: Int = this.trappedChests,
         roomID: Int = this.roomID,
         clear: String? = this.clear,
         crypts: Int = this.crypts,
         clearScore: Int? = this.clearScore,
         secretScore: Int? = this.secretScore,
         shape: String = this.shape
      ): jooon.features.dungeons.map.api.DungeonScanner.RoomData {
         return DungeonScanner.RoomData(name, type, secrets, cores, trappedChests, roomID, clear, crypts, clearScore, secretScore, shape)
      }

      override fun toString(): String {
         return "RoomData(name=${this.name}, type=${this.type}, secrets=${this.secrets}, cores=${this.cores}, trappedChests=${this.trappedChests}, roomID=${this.roomID}, clear=${this.clear}, crypts=${this.crypts}, clearScore=${this.clearScore}, secretScore=${this.secretScore}, shape=${this.shape})"
      }

      override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (
                                                               (
                                                                        (
                                                                                 (this.name.hashCode() * 31 + this.type.hashCode()) * 31
                                                                                    + Integer.hashCode(this.secrets)
                                                                              )
                                                                              * 31
                                                                           + this.cores.hashCode()
                                                                     )
                                                                     * 31
                                                                  + Integer.hashCode(this.trappedChests)
                                                            )
                                                            * 31
                                                         + Integer.hashCode(this.roomID)
                                                   )
                                                   * 31
                                                + (if (this.clear == null) 0 else this.clear.hashCode())
                                          )
                                          * 31
                                       + Integer.hashCode(this.crypts)
                                 )
                                 * 31
                              + (if (this.clearScore == null) 0 else this.clearScore.hashCode())
                        )
                        * 31
                     + (if (this.secretScore == null) 0 else this.secretScore.hashCode())
               )
               * 31
            + this.shape.hashCode()
         }

      override operator fun equals(other: Any?): Boolean {
         label82@
         if (this === other) {
            return true
         } else {
            return other is DungeonScanner.RoomData
               && this.name == (other as DungeonScanner.RoomData).name
               && this.type == (other as DungeonScanner.RoomData).type
               && this.secrets == (other as DungeonScanner.RoomData).secrets
               && this.cores == (other as DungeonScanner.RoomData).cores
               && this.trappedChests == (other as DungeonScanner.RoomData).trappedChests
               && this.roomID == (other as DungeonScanner.RoomData).roomID
               && this.clear == (other as DungeonScanner.RoomData).clear
               && this.crypts == (other as DungeonScanner.RoomData).crypts
               && this.clearScore == (other as DungeonScanner.RoomData).clearScore
               && this.secretScore == (other as DungeonScanner.RoomData).secretScore
               && this.shape == (other as DungeonScanner.RoomData).shape
            }
      }
   }
}
