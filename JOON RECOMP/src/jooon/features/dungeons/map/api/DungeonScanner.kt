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
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nDungeonScanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonScanner.kt\njooon/features/dungeons/map/api/DungeonScanner\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,699:1\n1855#2,2:700\n1855#2,2:702\n350#2,7:707\n1864#2,2:714\n1855#2,2:716\n1866#2:718\n766#2:719\n857#2,2:720\n1855#2,2:722\n766#2:724\n857#2,2:725\n766#2:727\n857#2,2:728\n1855#2,2:730\n1855#2,2:732\n1855#2,2:734\n1855#2,2:736\n1855#2,2:738\n72#3,2:704\n1#4:706\n1#4:740\n*S KotlinDebug\n*F\n+ 1 DungeonScanner.kt\njooon/features/dungeons/map/api/DungeonScanner\n*L\n172#1:700,2\n237#1:702,2\n283#1:707,7\n339#1:714,2\n401#1:716,2\n339#1:718\n434#1:719\n434#1:720,2\n442#1:722,2\n455#1:724\n455#1:725,2\n458#1:727\n458#1:728,2\n464#1:730,2\n556#1:732,2\n565#1:734,2\n585#1:736,2\n647#1:738,2\n266#1:704,2\n266#1:706\n*E\n"])
public object DungeonScanner {
   public final val roomsData: List<jooon.features.dungeons.map.api.DungeonScanner.RoomData>
   public final var lastIdx: Int?
   public final var currentRoom: DungeonRoom?
   public final var rooms: MutableList<DungeonRoom?>
   public final var doors: MutableList<DungeonDoor?>
   public final var availablePos: MutableList<WorldComponentPosition>
   private final var worldChangeCooldown: Int
   private final var foundEntrance: Int
   private final var wasInEntrance: Boolean
   private const val COLOR_SIZE: Int = 16384
   private const val SCAN: Int = 128
   private const val ROOM_SPACING: Int = 4
   public final var roomSize: Int
   public final var roomGap: Int
   public final var mapOffsetX: Int
   public final var mapOffsetZ: Int
   public final var mapWidth: Int
   public final var mapHeight: Int
   private final val playerInfoRegex: Regex

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

   public fun getHighestY(x: Int, z: Int): Int {
      if (WorldUtils.INSTANCE.getWorld() == null) {
         return -1
      } else {
         var height: Int = 0

         for (idx in 256 downTo 0) {
            val blockState: BlockState = WorldUtils.INSTANCE.getBlockState(x, idx, z)
            if (blockState != null) {
               val var10000: Block = blockState.method_26204()
               if (var10000 != null && !blockState.method_26215() && !(var10000 == Blocks.field_10205)) {
                  height = idx
                  break
               }
            }
         }

         return height
      }
   }

   fun getLegacyId(blockState: BlockState, debug: Boolean): Int {
      val var10000: Block = blockState.method_26204()
      var registryName: java.lang.String = WorldUtils.INSTANCE.registryName(var10000)
      val var7: FluidState = blockState.method_26227()
      if (!var7.method_15769()) {
         if (var7.method_15767(FluidTags.field_15517)) {
            if (var7.method_15771()) 9 else 8
         }

         if (var7.method_15767(FluidTags.field_15518)) {
            if (var7.method_15771()) 11 else 10
         }
      }

      if (var10000 is SlabBlock) {
         val var10001: java.lang.String = (blockState.method_11654(SlabBlock.field_11501 as Property) as SlabType).name().toLowerCase(Locale.ROOT)
         registryName = "$registryName[type=$var10001]"
      }

      LegacyRegistry.INSTANCE.BLOCKS.get(registryName) as Int
   }

   @JvmOverloads
   public fun hashCeil(x: Int, z: Int, debug: Boolean = false): Int {
      var str: java.lang.String = ""

      for (idx in 140 downTo 12) {
         val var10000: BlockState = WorldUtils.INSTANCE.getBlockState(x, idx, z)
         if (var10000 != null) {
            val var9: Block = var10000.method_26204()
            if (var9 != null) {
               val var10: Int = this.getLegacyId(var10000, debug)
               if (var10 != null) {
                  val blockId: Int = var10
                  if (!(var9 == Blocks.field_10576) && !(var9 == Blocks.field_10034)) {
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

   public fun tick() {
      if (Utils.INSTANCE.inDungeon) {
         val var10000: ClientPlayerEntity = WorldUtils.INSTANCE.getMc().field_1724
         if (var10000 != null) {
            if (var10000.field_6012 >= 60) {
               if (var10000.field_6012 % 20 == 0) {
                  this.scanTabList()
               }

               val var14: java.util.Collection = Dungeons.INSTANCE.players.values()

               for (`element$iv` in var14) {
                  (`element$iv` as DungeonPlayer).tick()
               }

               worldChangeCooldown += -1
               if (worldChangeCooldown < 0) {
                  if (WorldUtils.INSTANCE.isChunkLoaded(var10000.method_23317(), var10000.method_23321())) {
                     val var10: Int = WorldPosition((int)var10000.method_23317(), (int)var10000.method_23321()).toComponent().getRoomIdx()
                     if (0 <= var10 && var10 < 36) {
                        this.scan()
                        val var11: DungeonRoom = rooms.get(var10)
                        if (!wasInEntrance) {
                           if ((if (var11 != null) var11.type else null) === RoomTypes.ENTRANCE) {
                              wasInEntrance = true
                           } else if (foundEntrance > 0) {
                              return
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
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      val var12: ClientPlayNetworkHandler = var10000.method_1562()
      if (var12 != null) {
         run label69@{
            var13 = var12.method_2880()
            if (var10000.field_1724 != null) {
               val var14: GameProfile = var10000.field_1724.method_7334()
               if (var14 != null) {
                  var15 = var14.name()
                  if (var15 != null) {
                     return@label69
                  }
               }
            }

            var15 = ""
         }

         val localName: java.lang.String = var15

         for (var16 in var13) {
            var info: PlayerListEntry
            run label72@{
               info = var16 as PlayerListEntry
               val var17: Text = (var16 as PlayerListEntry).method_2971()
               if (var17 != null) {
                  var18 = var17.getString()
                  if (var18 != null) {
                     return@label72
                  }
               }

               var18 = info.method_2966().name()
            }

            var var19: java.lang.String = var18
            if (var18 == null) {
               var19 = ""
            }

            val cleanName: java.lang.String = StringsKt.trim(Utils.stripColor(var19)).toString()
            val match: MatchResult = Regex.find$default(playerInfoRegex, cleanName, 0, 2, null)
            if (match != null) {
               val var20: MatchGroup = match.getGroups().get(1)
               if (var20 != null) {
                  val var21: java.lang.String = var20.getValue()
                  if (var21 != null) {
                     val var22: MatchGroup = match.getGroups().get(2)
                     if (var22 != null) {
                        val var23: java.lang.String = var22.getValue()
                        if (var23 != null) {
                           val var24: UUID = info.method_2966().id()
                           this.addDungeonPlayer(var24, var21, var23)
                        }
                     }
                  }
               }
            } else if (cleanName == localName || info.method_2966().name() == localName) {
               val var10001: UUID = info.method_2966().id()
               this.addDungeonPlayer(var10001, localName, "Unknown")
            }
         }
      }
   }

   fun onPlayerInfoPacket(packet: PlayerListS2CPacket) {
      if (packet.method_46327().contains(Action.field_29136)) {
         var var19: java.lang.String
         run label57@{
            val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
            if (var10000 != null) {
               val var18: GameProfile = var10000.method_7334()
               if (var18 != null) {
                  var19 = var18.name()
                  if (var19 != null) {
                     return@label57
                  }
               }
            }

            var19 = ""
         }

         val localName: java.lang.String = var19
         val var20: java.util.List = packet.method_46329()

         for (`element$iv` in var20) {
            val entry: Entry = `element$iv` as Entry
            val var21: GameProfile = (`element$iv` as Entry).comp_1107()
            if (var21 != null) {
               var name: java.lang.String
               run label60@{
                  name = var21.name()
                  val var22: Text = entry.comp_1111()
                  if (var22 != null) {
                     var19 = var22.getString()
                     if (var19 != null) {
                        return@label60
                     }
                  }

                  var19 = name
               }

               val cleanName: java.lang.String = StringsKt.trim(Utils.stripColor(var19)).toString()
               val match: MatchResult = Regex.find$default(playerInfoRegex, cleanName, 0, 2, null)
               if (match != null) {
                  val var14: Destructured = match.getDestructured()
                  val pName: java.lang.String = var14.getMatch().getGroupValues().get(1) as java.lang.String
                  val role: java.lang.String = var14.getMatch().getGroupValues().get(2) as java.lang.String
                  val level: java.lang.String = var14.getMatch().getGroupValues().get(3) as java.lang.String
                  val var24: DungeonScanner = INSTANCE
                  val var10001: UUID = var21.id()
                  var24.addDungeonPlayer(var10001, pName, role)
               } else if (name == localName || cleanName == localName) {
                  val var25: DungeonScanner = INSTANCE
                  val var26: UUID = var21.id()
                  var25.addDungeonPlayer(var26, localName, "Unknown")
               }
            }
         }
      }
   }

   private fun addDungeonPlayer(uuid: UUID, name: String, roleStr: String) {
      val `$this$getOrPut$iv`: ConcurrentMap = Dungeons.INSTANCE.players
      var var10000: Any = `$this$getOrPut$iv`.get(name)
      if (var10000 == null) {
         val newRole: DungeonPlayer = DungeonPlayer(uuid, name)
         newRole.role = DungeonClass.Companion.from(roleStr)
         var10000 = `$this$getOrPut$iv`.putIfAbsent(name, newRole)
         if (var10000 == null) {
            var10000 = newRole
         }
      }

      val var11: DungeonPlayer = var10000 as DungeonPlayer
      val var13: DungeonClass = DungeonClass.Companion.from(roleStr)
      if (var13 != DungeonClass.Unknown) {
         var11.role = var13
      }
   }

   private fun scanMapDimensions(colors: ByteArray): Boolean {
      var floor: FloorType = Dungeons.INSTANCE.floor
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

         val idx: DungeonRoom = bestPixelL.next() as DungeonRoom
         if ((if (idx != null) idx.type else null) === RoomTypes.ENTRANCE) {
            var10000 = bestSize
            break
         }

         bestSize++
      }

      if (var10000 == -1) {
         return false
      } else {
         val var19: Int = var10000 % 6
         val var20: Int = var10000 / 6
         bestSize = 0
         var var22: Int = -1
         var var23: Int = -1
         var var24: Int = 0

         for (var25 in colors.length..var24) {
            val col: Int = colors[var24]
            if (29 <= colors[var24] && colors[var24] < 32) {
               val colByte: Byte = (byte)col
               var l: Int = var24
               var r: Int = var24

               while (l % 128 != 0) {
                  val var26: java.lang.Byte = ArraysKt.getOrNull(colors, l - 1)
                  if (var26 == null || var26 != colByte) {
                     break
                  }

                  l--
               }

               while ((r + 1) % 128 != 0) {
                  val var27: java.lang.Byte = ArraysKt.getOrNull(colors, r + 1)
                  if (var27 == null || var27 != colByte) {
                     break
                  }

                  r++
               }

               var t: Int = var24
               var b: Int = var24

               while (t >= 128) {
                  val var28: java.lang.Byte = ArraysKt.getOrNull(colors, t - 128)
                  if (var28 == null || var28 != colByte) {
                     break
                  }

                  t -= 128
               }

               while (b + 128 < colors.length) {
                  val var29: java.lang.Byte = ArraysKt.getOrNull(colors, b + 128)
                  if (var29 == null || var29 != colByte) {
                     break
                  }

                  b += 128
               }

               val currentW: Int = r % 128 - l % 128 + 1
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
            val `$this$forEachIndexed$iv`: java.lang.Iterable = rooms
            var `index$iv`: Int = 0

            for (`item$iv` in `$this$forEachIndexed$iv`) {
               val var8: Int = `index$iv`++
               if (var8 < 0) {
                  CollectionsKt.throwIndexOverflow()
               }

               val room_: DungeonRoom = `item$iv` as DungeonRoom
               if (`item$iv` as DungeonRoom != null) {
                  val z: Int = var8 / 6
                  val mrx: Int = mapOffsetX + var8 % 6 * roomGap
                  val mrz: Int = mapOffsetZ + var8 / 6 * roomGap
                  var var43: java.lang.Byte = ArraysKt.getOrNull(colors, mrx + (mapOffsetZ + z * roomGap) * 128)
                  if (var43 != null) {
                     val roomCol: Byte = var43
                     if (roomCol != DungeonScanner.MapColors.EMPTY.color) {
                        if (room_.type === RoomTypes.UNKNOWN) {
                           room_.type = if (roomCol == DungeonScanner.MapColors.ROOM_ENTRANCE.color)
                              RoomTypes.ENTRANCE
                              else
                              (
                                 if (roomCol == DungeonScanner.MapColors.ROOM_BLOOD.color)
                                    RoomTypes.BLOOD
                                    else
                                    (
                                       if (roomCol == DungeonScanner.MapColors.ROOM_BOSS.color)
                                          RoomTypes.YELLOW
                                          else
                                          (
                                             if (roomCol == DungeonScanner.MapColors.ROOM_FAIRY.color)
                                                RoomTypes.FAIRY
                                                else
                                                (
                                                   if (roomCol == DungeonScanner.MapColors.ROOM_NORMAL.color)
                                                      RoomTypes.NORMAL
                                                      else
                                                      (
                                                         if (roomCol == DungeonScanner.MapColors.ROOM_PUZZLE.color)
                                                            RoomTypes.PUZZLE
                                                            else
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
                        var `$this$forEach$iv`: Int = 0

                        for (`$i$f$forEach` in roomSize..`$this$forEach$iv`) {
                           var dz: Int = 0

                           for (`element$iv` in roomSize..dz) {
                              val dec: Int = mrx + `$this$forEach$iv`
                              val var25: Int = mrz + dz
                              if (dec >= 0 && dec < 128 && mrz + dz >= 0 && mrz + dz < 128) {
                                 var43 = ArraysKt.getOrNull(colors, dec + var25 * 128)
                                 if (var43 != null) {
                                    val pixelCol: Byte = var43
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

                        val var46: java.lang.Iterable = mapState.method_32373()

                        for (var40 in var46) {
                           val var41: MapDecoration = var40 as MapDecoration
                           val dxx: Double = ((var40 as MapDecoration).comp_1843() + 128.0) * 0.5
                           val dzx: Double = ((var40 as MapDecoration).comp_1844() + 128.0) * 0.5
                           if (dxx >= mrx && dxx < mrx + roomSize && dzx >= mrz && dzx < mrz + roomSize) {
                              val var33: RegistryEntry = var41.comp_1842()
                              val detected: CheckmarkTypes = if (var33 == MapDecorationTypes.field_96)
                                 CheckmarkTypes.WHITE
                                 else
                                 (
                                    if (var33 == MapDecorationTypes.field_102)
                                       CheckmarkTypes.GREEN
                                       else
                                       (if (var33 == MapDecorationTypes.field_110) CheckmarkTypes.FAILED else null)
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
      val players: ConcurrentHashMap = Dungeons.INSTANCE.players
      if (!players.isEmpty()) {
         if (Dungeons.INSTANCE.floor != FloorType.None) {
            var var57: java.lang.String
            run label147@{
               val var10000: MinecraftClient = MinecraftClient.method_1551()
               if (var10000.field_1724 != null) {
                  val var56: GameProfile = var10000.field_1724.method_7334()
                  if (var56 != null) {
                     var57 = var56.name()
                     if (var57 != null) {
                        return@label147
                     }
                  }
               }

               var57 = ""
            }

            val localName: java.lang.String = var57
            val var58: java.lang.Iterable = mapState.method_32373()
            var unassignedTeammates: java.util.List = CollectionsKt.toList(var58)
            val unassignedDecs: java.util.Collection = ArrayList()

            for (`$this$forEach$iv` in unassignedTeammates) {
               if (!((`$this$forEach$iv` as MapDecoration).comp_1842() == MapDecorationTypes.field_95)) {
                  unassignedDecs.add(`$this$forEach$iv`)
               }
            }

            val potentialTeammateDecs: java.util.List = unassignedDecs as java.util.List
            val var21: java.util.Set = LinkedHashSet()
            val var22: java.util.Set = LinkedHashSet()

            for (var32 in potentialTeammateDecs) {
               val var36: MapDecoration = var32 as MapDecoration
               val var59: Text = (var32 as MapDecoration).comp_1846().orElse(null) as Text
               if (var59 != null) {
                  var57 = var59.getString()
                  if (var57 != null) {
                     var57 = Utils.stripColor(var57)
                     if (var57 != null) {
                        var57 = StringsKt.trim(var57).toString()
                        if (var57 != null) {
                           val `element$iv`: DungeonPlayer = players.get(var57) as DungeonPlayer
                           if (`element$iv` != null) {
                              if (!(var57 == localName)) {
                                 val var63: DungeonScanner = INSTANCE
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
               val var52: MapDecoration = var50 as MapDecoration
               if (!var31 && (var50 as MapDecoration).comp_1842() == MapDecorationTypes.field_91) {
                  var31 = true
               } else if (var35 < unassignedTeammates.size()) {
                  val var65: DungeonScanner = INSTANCE
                  val var55: Int = var35
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
      val floor: FloorType = Dungeons.INSTANCE.floor
      player.updatePosition(
         PlayerComponentPosition(
            MathUtils.INSTANCE
               .rescale(
                  ((double)dec.comp_1843() + 128.0) * 0.5, (double)mapOffsetX, (double)(mapOffsetX + roomGap * floor.roomsW), 0.0, (double)floor.roomsW * 2.0
               ),
            MathUtils.INSTANCE
               .rescale(
                  ((double)dec.comp_1844() + 128.0) * 0.5, (double)mapOffsetZ, (double)(mapOffsetZ + roomGap * floor.roomsH), 0.0, (double)floor.roomsH * 2.0
               ),
            (double)dec.comp_1845() * 360.0 / 16.0 + 180.0
         )
      )
   }

   fun onMapPacket(packet: MapUpdateS2CPacket, mapState: MapState) {
      var var10000: ByteArray = mapState.field_122
      if (var10000.length != 0 && roomSize == -1 && this.scanMapDimensions(var10000)) {
      }

      if (roomSize != -1) {
         this.updatePlayerIcons(mapState)
         var10000 = mapState.field_122
         this.updateRooms(var10000, mapState)
         DungeonMapFeature.INSTANCE.redrawMap(CollectionsKt.toList(rooms), CollectionsKt.toList(doors))
      } else {
         val var6: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
         if (var6 != null && var6.field_6012 % 100 == 0) {
         }
      }
   }

   public fun mergeRooms(comp1: ComponentPosition, comp2: ComponentPosition): Boolean {
      val i1: Int = comp1.getRoomIdx()
      val i2: Int = comp2.getRoomIdx()
      val r1: DungeonRoom = rooms.get(comp1.getRoomIdx())
      val r2: DungeonRoom = rooms.get(comp2.getRoomIdx())
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

   public fun mergeRooms(room1: DungeonRoom, room2: DungeonRoom) {
      if (room1 != room2) {
         if (room1.type != RoomTypes.ENTRANCE && room2.type != RoomTypes.ENTRANCE) {
            for (`$i$f$forEach` in room2.comps) {
               val c: ComponentPosition = `$i$f$forEach`.toComponent()
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

   public fun addDoor(door: DungeonDoor) {
      val comp: ComponentPosition = door.comp.toComponent()
      val idx: Int = comp.getDoorIdx()
      if (0 <= idx && idx < 60) {
         doors.set(idx, door)

         for (`element$iv` in comp.getNeighboringRooms()) {
            val var10000: DungeonRoom = CollectionsKt.getOrNull(rooms, (`element$iv` as ComponentPosition).getRoomIdx()) as DungeonRoom
            if (var10000 != null) {
               var10000.doors.add(door)
               door.rooms.add(var10000)
            }
         }
      }
   }

   public fun addRoom(comp: ComponentPosition, room: DungeonRoom, force: Boolean = false) {
      val idx: Int = comp.getRoomIdx()
      if (0 <= idx && idx < 36) {
         if (!force) {
            val `$this$forEach$iv`: DungeonRoom = rooms.get(idx)
            if (`$this$forEach$iv` != null) {
               if (room.name == null) {
                  INSTANCE.mergeRooms(`$this$forEach$iv`, room)
               } else {
                  INSTANCE.mergeRooms(room, `$this$forEach$iv`)
               }

               return
            }
         }

         rooms.set(idx, room)

         for (`element$iv` in comp.getNeighboringDoors()) {
            val var10000: DungeonDoor = CollectionsKt.getOrNull(doors, (`element$iv` as ComponentPosition).getDoorIdx()) as DungeonDoor
            if (var10000 != null) {
               var10000.rooms.add(room)
               room.doors.add(var10000)
            }
         }
      }
   }

   public fun reset() {
      worldChangeCooldown = 5
      foundEntrance = 5
      wasInEntrance = false
      Collections.fill(rooms, null)
      Collections.fill(doors, null)
      lastIdx = null
      currentRoom = null
      availablePos = CollectionsKt.asReversedMutable(this.findAvailablePos())
      roomSize = -1
      roomGap = -1
      mapOffsetX = -1
      mapOffsetZ = -1
      mapWidth = -1
      mapHeight = -1
   }

   public fun scan() {
      foundEntrance += -1
      if (!availablePos.isEmpty()) {
         val var3: BooleanRef = BooleanRef()
         val startLen: Int = availablePos.size()
         availablePos.removeIf({ p0: Any ->
            `$tmp0`(p0)
         })
         if (var3.element) {
            DungeonMapFeature.INSTANCE.redrawMap(CollectionsKt.toList(rooms), CollectionsKt.toList(doors))
         }
      }
   }

   @JvmOverloads
   fun hashCeil(x: Int, z: Int): Int {
      hashCeil$default(this, x, z, false, 4, null)
   }

   @JvmStatic
   fun {
      val var10000: Gson = Gson()
      val var1: InputStream = INSTANCE.getClass().getResourceAsStream("/assets/jooonreimagined/dungeons/rooms.json")
      val var10001: BufferedReader
      if (var1 != null) {
         val it: Reader = InputStreamReader(var1, Charsets.UTF_8)
         var10001 = if (it is BufferedReader) it as BufferedReader else BufferedReader(it, 8192)
      } else {
         var10001 = null
      }

      val var12: Closeable = var10001
      var var15: java.lang.Throwable = null

      var var19: java.lang.String
      try {
         var19 = if (var12 as BufferedReader != null) TextStreamsKt.readText(var12 as BufferedReader) else null
      } catch (var9: java.lang.Throwable) {
         var15 = var9
         throw var9
      } finally {
         CloseableKt.closeFinally(var12, var15)
      }

      val var25: Any = var10000.fromJson(var19, DungeonScanner.RoomData[].class)
      roomsData = ArraysKt.toList(var25 as Array<Any>)
      var var0: Byte = 36
      val var13: ArrayList = ArrayList(36)

      repeat(var0) { var16 ->
         var13.add(null)
      }

      rooms = var13
      var0 = 60
      val var14: ArrayList = ArrayList(60)

      repeat(var0) { var17 ->
         var14.add(null)
      }

      doors = var14
      availablePos = INSTANCE.findAvailablePos()
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
      EMPTY((byte)0),
      CHECK_WHITE((byte)34),
      CHECK_GREEN((byte)30),
      CHECK_FAIL((byte)18),
      CHECK_UNKNOWN((byte)119),
      ROOM_ENTRANCE((byte)30),
      ROOM_NORMAL((byte)63),
      ROOM_UNOPENED((byte)85),
      ROOM_TRAP((byte)62),
      ROOM_BOSS((byte)74),
      ROOM_PUZZLE((byte)66),
      ROOM_FAIRY((byte)82),
      ROOM_BLOOD((byte)18),
      DOOR_WITHER((byte)119),
      DOOR_BLOOD((byte)18);

      public final val color: Byte

      init {
         this.color = color
      }

      @JvmStatic
      fun getEntries(): EnumEntries<DungeonScanner.MapColors> {
         $ENTRIES
      }
   }

   public data class RoomData(name: String,
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
      public final val name: String
      public final val type: String
      public final val secrets: Int
      public final val cores: List<Int>
      public final val trappedChests: Int
      public final val roomID: Int
      public final val clear: String?
      public final val crypts: Int
      public final val clearScore: Int?
      public final val secretScore: Int?
      public final val shape: String

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

      public fun copy(
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

      public override fun toString(): String {
         return "RoomData(name=${this.name}, type=${this.type}, secrets=${this.secrets}, cores=${this.cores}, trappedChests=${this.trappedChests}, roomID=${this.roomID}, clear=${this.clear}, crypts=${this.crypts}, clearScore=${this.clearScore}, secretScore=${this.secretScore}, shape=${this.shape})"
      }

      public override fun hashCode(): Int {
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

      public override operator fun equals(other: Any?): Boolean {
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
