package jooon.features.dungeons.map

import java.awt.Color
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.ArrayList
import java.util.HashSet
import java.util.NoSuchElementException
import javax.imageio.ImageIO
import jooon.features.dungeons.map.api.DungeonDoor
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.WorldComponentPosition
import jooon.features.dungeons.map.api.mapEnums.CheckmarkTypes
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.api.mapEnums.ShapeTypes
import jooon.features.dungeons.map.util.BoundingBox
import jooon.features.dungeons.map.util.bufimgrenderer.BufferedImageRenderer
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nDungeonMapBaseRenderer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonMapBaseRenderer.kt\njooon/features/dungeons/map/DungeonMapBaseRenderer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,402:1\n1855#2:403\n1726#2,3:404\n2333#2,14:407\n1856#2:421\n1655#2,8:422\n1855#2:430\n766#2:431\n857#2:432\n1747#2,2:433\n1747#2,3:435\n1749#2:438\n858#2:439\n2310#2,14:440\n2310#2,14:454\n1855#2,2:468\n1856#2:470\n2310#2,14:471\n2310#2,14:486\n2310#2,14:500\n2310#2,14:514\n1045#2:528\n1#3:485\n*S KotlinDebug\n*F\n+ 1 DungeonMapBaseRenderer.kt\njooon/features/dungeons/map/DungeonMapBaseRenderer\n*L\n142#1:403\n145#1:404,3\n152#1:407,14\n142#1:421\n245#1:422,8\n249#1:430\n260#1:431\n260#1:432\n263#1:433,2\n265#1:435,3\n263#1:438\n260#1:439\n301#1:440,14\n311#1:454,14\n337#1:468,2\n249#1:470\n214#1:471,14\n216#1:486,14\n218#1:500,14\n220#1:514,14\n223#1:528\n*E\n"])
public class DungeonMapBaseRenderer : BufferedImageRenderer("dungeonMapBaseRenderer") {
   private final val CHECKMARK: Map<CheckmarkTypes, BufferedImage?> =
      MapsKt.mapOf(
         arrayOf(
            TuplesKt.to(CheckmarkTypes.NONE, null),
            TuplesKt.to(CheckmarkTypes.WHITE, this.getImg("checks/white_check.png")),
            TuplesKt.to(CheckmarkTypes.GREEN, this.getImg("checks/green_check.png")),
            TuplesKt.to(CheckmarkTypes.FAILED, this.getImg("checks/failed_room.png")),
            TuplesKt.to(CheckmarkTypes.UNEXPLORED, this.getImg("checks/question_mark.png"))
         )
      )

   private fun getImg(path: String): BufferedImage? {
      val var10000: InputStream = this.getClass().getResourceAsStream("/assets/jooonreimagined/dungeons/map/$path")
      return if (var10000 == null) null else ImageIO.read(var10000)
   }

   protected open fun drawImage(img: BufferedImage, param: DungeonMapRenderData): BufferedImage {
      val g: Graphics2D = img.createGraphics()
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
      val w: Int = img.getWidth()
      val h: Int = img.getHeight()
      val rooms: java.util.List = param.rooms
      val doors: java.util.List = param.doors
      val options: DungeonMapRenderOptions = param.options
      val colors: java.util.Map = options.colors
      var var10000: Color = colors.get(DungeonMapColors.Background) as Color
      if ((if (var10000 != null) var10000.getAlpha() else 0) > 0) {
         g.setPaint(colors.get(DungeonMapColors.Background) as Paint)
         g.fillRect(0, 0, w, h)
      }

      if (options.border > 0) {
         var10000 = colors.get(DungeonMapColors.Border) as Color
         if ((if (var10000 != null) var10000.getAlpha() else 0) > 0) {
            g.setPaint(colors.get(DungeonMapColors.Border) as Paint)
            val roomRectOffset: Int = options.border
            g.fillRect(0, 0, w, roomRectOffset)
            g.fillRect(0, roomRectOffset, roomRectOffset, h - roomRectOffset)
            g.fillRect(w - roomRectOffset, roomRectOffset, roomRectOffset, h - roomRectOffset)
            g.fillRect(roomRectOffset, h - roomRectOffset, w - roomRectOffset - roomRectOffset, roomRectOffset)
         }
      }

      val var65: Double = (1.0 - options.roomWidth) * 0.5
      val maxDim: Int = Math.max(options.dungeonWidth, options.dungeonHeight)
      val totalMaxDim: Double = maxDim + options.padding * 2
      val compToBImgFW: Double = w / totalMaxDim
      val compToBImgFH: Double = h / totalMaxDim
      val bImgOX: Double = ((maxDim - options.dungeonWidth) / 2.0 + options.padding) * compToBImgFW
      val bImgOY: Double = ((maxDim - options.dungeonHeight) / 2.0 + options.padding) * compToBImgFH
      val doorOffset: Double = (1.0 - options.doorWidth) * 0.5

      for (distinctRooms in doors) {
         val finalRooms: DungeonDoor = distinctRooms as DungeonDoor
         if (distinctRooms as DungeonDoor != null) {
            if (!options.renderUnknownRooms) {
               val `$i$f$forEach`: java.lang.Iterable = finalRooms.rooms
               var var170: Boolean
               if (`$i$f$forEach` is java.util.Collection && (`$i$f$forEach` as java.util.Collection).isEmpty()) {
                  var170 = true
               } else {
                  run label534@{
                     for (`element$ivx` in `$i$f$forEach`) {
                        if ((`element$ivx` as DungeonRoom).explored) {
                           var170 = false
                           return@label534
                        }
                     }

                     var170 = true
                  }
               }

               if (var170) {
                  continue
               }
            }

            when (DungeonMapBaseRenderer.WhenMappings.$EnumSwitchMapping$2[finalRooms.type.ordinal()]) {
               1 -> var10000 = colors.get(DungeonMapColors.DoorEntrance) as Color
               2 -> var10000 = colors.get(DungeonMapColors.DoorWither) as Color
               3 -> var10000 = colors.get(DungeonMapColors.DoorBlood) as Color
               4 -> {
                  if (!finalRooms.opened) {
                     continue
                  }

                  val renderRoomInfo: java.util.Iterator = finalRooms.rooms.iterator()
                  var var171: DungeonRoom
                  if (!renderRoomInfo.hasNext()) {
                     var171 = null
                  } else {
                     var decoration: Any = renderRoomInfo.next()
                     if (!renderRoomInfo.hasNext()) {
                        var171 = (DungeonRoom)decoration
                     } else {
                        var var94: Int = (decoration as DungeonRoom).type.prio
                           - (if (!(decoration as DungeonRoom).explored && !options.renderUnknownRooms) 100 else 0)

                        do {
                           val var97: Any = renderRoomInfo.next()
                           val var103: Int = (var97 as DungeonRoom).type.prio
                              - (if (!(var97 as DungeonRoom).explored && !options.renderUnknownRooms) 100 else 0)
                              if (var94 > var103) {
                              decoration = var97
                              var94 = var103
                           }
                        } while (renderRoomInfo.hasNext())

                        var171 = (DungeonRoom)decoration
                     }
                  }

                  var171 = var171
                  if (var171 == null) {
                     continue
                  }

                  var10000 = if (!var171.explored && finalRooms.holyShitFairyDoorPleaseStopFlashingSobs)
                     colors.get(DungeonMapColors.DoorWither) as Color
                     else
                     drawImage$colorForRoom(options, colors, var171)
                     break
               }
               else -> throw NoWhenBranchMatchedException()
            }

            if (var10000 == null) {
               var10000 = colors.get(DungeonMapColors.RoomNormal) as Color
               if (var10000 == null) {
                  continue
               }
            }

            g.setPaint(var10000)
            drawImage$drawDoor(
               doorOffset,
               var65,
               options,
               compToBImgFW,
               bImgOX,
               compToBImgFH,
               bImgOY,
               g,
               finalRooms.roomComp1.x,
               finalRooms.roomComp1.z,
               finalRooms.roomComp2.x,
               finalRooms.roomComp2.z
            )
         }
      }

      g.setFont(g.getFont().deriveFont(1, (float)((int)Math.ceil(options.roomWidth * compToBImgFH * 0.25 * options.textSize))))
      val var68: FontMetrics = g.getFontMetrics()
      val var71: java.lang.Iterable = CollectionsKt.distinct(CollectionsKt.filterNotNull(rooms))
      val var76: HashSet = HashSet()
      val var78: ArrayList = ArrayList()

      for (var83 in var71) {
         val var85: DungeonRoom = var83 as DungeonRoom
         if (var76.add(
            if ((var83 as DungeonRoom).name != null && (var83 as DungeonRoom).type != RoomTypes.ENTRANCE) (var83 as DungeonRoom).name else var83 as DungeonRoom
         )) {
            var78.add(var83)
         }
      }

      for (var79 in var78) {
         val var81: DungeonRoom = var79 as DungeonRoom
         if (!(var79 as DungeonRoom).doors.isEmpty() || (var79 as DungeonRoom).name != null) {
            var10000 = drawImage$colorForRoom(options, colors, var81)
            if (var10000 == null) {
               var10000 = colors.get(DungeonMapColors.RoomNormal) as Color
               if (var10000 == null) {
                  var10000 = Color(0, true)
               }
            }

            var var89: ShapeTypes = var81.shape
            if (var89 != ShapeTypes.Unknown) {
               var var90: java.util.List = CollectionsKt.toList(var81.comps)
               var var91: Boolean = true
               if (!var81.explored && !options.renderUnknownRooms) {
                  var89 = ShapeTypes.Shape1x1
                  val var98: java.lang.Iterable = var81.comps
                  val var104: java.util.Collection = ArrayList()

                  for (cy in var98) {
                     val currentY: Int = (cy as WorldComponentPosition).cx
                     val `element$iv`: Int = (cy as WorldComponentPosition).cz
                     val line: java.lang.Iterable = var81.doors
                     var var177: Boolean
                     if (line is java.util.Collection && (line as java.util.Collection).isEmpty()) {
                        var177 = false
                     } else {
                        val var53: java.util.Iterator = line.iterator()

                        while (true) {
                           if (!var53.hasNext()) {
                              var177 = false
                              break
                           }

                           run label567@{
                              val it: DungeonDoor = var53.next() as DungeonDoor
                              if (Math.abs(currentY - it.comp.cx) + Math.abs(`element$iv` - it.comp.cz) == 1) {
                                 val `$this$any$ivx`: java.lang.Iterable = it.rooms
                                 var var175: Boolean
                                 if (`$this$any$ivx` is java.util.Collection && (`$this$any$ivx` as java.util.Collection).isEmpty()) {
                                    var175 = false
                                 } else {
                                    val var59: java.util.Iterator = `$this$any$ivx`.iterator()

                                    while (true) {
                                       if (!var59.hasNext()) {
                                          var175 = false
                                          break
                                       }

                                       if ((var59.next() as DungeonRoom).explored) {
                                          var175 = true
                                          break
                                       }
                                    }
                                 }

                                 if (var175) {
                                    var176 = true
                                    return@label567
                                 }
                              }

                              var176 = false
                           }

                           if (var176) {
                              var177 = true
                              break
                           }
                        }
                     }

                     if (var177) {
                        var104.add(cy)
                     }
                  }

                  var90 = var104 as java.util.List
                  var91 = false
               }

               if (!var90.isEmpty()) {
                  if (var89 === ShapeTypes.Shape2x2 && var90.size() != 4) {
                     var89 = ShapeTypes.ShapeL
                  }

                  if (var89 === ShapeTypes.ShapeL && var90.size() != 3) {
                     var var178: ShapeTypes
                     when (var90.size()) {
                        1 -> var178 = ShapeTypes.Shape1x1
                        2 -> var178 = ShapeTypes.Shape1x2
                        else -> var178 = ShapeTypes.ShapeL
                     }

                     var89 = var178
                  }

                  g.setPaint(var10000)
                  when (DungeonMapBaseRenderer.WhenMappings.$EnumSwitchMapping$4[var89.ordinal()]) {
                     1 -> continue
                     2 -> {
                        var var101: Int = 0

                        for (var108 in var90.size()..var101) {
                           val var114: Int = (var90.get(var101) as WorldComponentPosition).cx / 2
                           val var120: Int = (var90.get(var101) as WorldComponentPosition).cz / 2
                           drawImage$drawRoom(var65, options, compToBImgFW, bImgOX, compToBImgFH, bImgOY, g, var114, var120, 1, 1)
                           var var126: Int = var101

                           for (var137 in var90.size()..var126) {
                              val var147: Int = (var90.get(var126) as WorldComponentPosition).cx / 2
                              val var157: Int = (var90.get(var126) as WorldComponentPosition).cz / 2
                              if (Math.abs(var114 - var147) + Math.abs(var120 - var157) == 1) {
                                 drawImage$drawRoomJoined(var65, options, compToBImgFW, bImgOX, compToBImgFH, bImgOY, g, var114, var120, var147, var157)
                              }
                           }
                        }
                        break
                     }
                     3 -> drawImage$drawRoom(
                           var65,
                           options,
                           compToBImgFW,
                           bImgOX,
                           compToBImgFH,
                           bImgOY,
                           g,
                           (var90.get(0) as WorldComponentPosition).cx / 2,
                           (var90.get(0) as WorldComponentPosition).cz / 2,
                           1,
                           1
                        )
                     4, 5, 6 -> {
                        val var119: java.util.Iterator = var90.iterator()
                        if (!var119.hasNext()) {
                           throw NoSuchElementException()
                        }

                        var var125: Any = var119.next()
                        val var180: Any
                        if (!var119.hasNext()) {
                           var180 = var125
                        } else {
                           var var136: Int = (var125 as WorldComponentPosition).cx + (var125 as WorldComponentPosition).cz

                           do {
                              val var146: Any = var119.next()
                              val var156: Int = (var146 as WorldComponentPosition).cx + (var146 as WorldComponentPosition).cz
                              if (var136 > var156) {
                                 var125 = var146
                                 var136 = var156
                              }
                           } while (var119.hasNext())

                           var180 = var125
                        }

                        val var100: WorldComponentPosition = var180 as WorldComponentPosition
                        val var107: Int = (var90.get(0) as WorldComponentPosition).cx
                        val var113: Int = (var90.get(1) as WorldComponentPosition).cx
                        drawImage$drawRoom(
                           var65,
                           options,
                           compToBImgFW,
                           bImgOX,
                           compToBImgFH,
                           bImgOY,
                           g,
                           var100.cx / 2,
                           var100.cz / 2,
                           if (var107 == var113) 1 else var90.size(),
                           if (var107 == var113) var90.size() else 1
                        )
                        break
                     }
                     7 -> {
                        val var118: java.util.Iterator = var90.iterator()
                        if (!var118.hasNext()) {
                           throw NoSuchElementException()
                        }

                        var var124: Any = var118.next()
                        val var179: Any
                        if (!var118.hasNext()) {
                           var179 = var124
                        } else {
                           var var134: Int = (var124 as WorldComponentPosition).cx + (var124 as WorldComponentPosition).cz

                           do {
                              val var144: Any = var118.next()
                              val var154: Int = (var144 as WorldComponentPosition).cx + (var144 as WorldComponentPosition).cz
                              if (var134 > var154) {
                                 var124 = var144
                                 var134 = var154
                              }
                           } while (var118.hasNext())

                           var179 = var124
                        }

                        drawImage$drawRoom(
                           var65,
                           options,
                           compToBImgFW,
                           bImgOX,
                           compToBImgFH,
                           bImgOY,
                           g,
                           (var179 as WorldComponentPosition).cx / 2,
                           (var179 as WorldComponentPosition).cz / 2,
                           2,
                           2
                        )
                        break
                     }
                     else -> throw NoWhenBranchMatchedException()
                  }

                  val var93: BufferedImage = if (options.checkMark)
                     (
                        if (options.renderUnknownRooms && var81.checkmark === CheckmarkTypes.UNEXPLORED && var81.name != null)
                           null
                           else
                           this.CHECKMARK.get(var81.checkmark)
                     )
                     else
                     null
                     val var96: java.util.List = ArrayList()
                  if (var91
                     && (!options.roomNameNotEFB || var81.type != RoomTypes.ENTRANCE && var81.type != RoomTypes.FAIRY && var81.type != RoomTypes.BLOOD)
                     && (if (var81.type === RoomTypes.PUZZLE) options.puzzleName else options.roomName)) {
                     val var181: java.lang.String = var81.name
                     if (var181 != null) {
                        for (`element$ivx` in StringsKt.split$default(
                           Regex("&[0-9a-fk-or]")
                              .replace(Regex("§[0-9a-fk-or]").replace(StringsKt.replace$default(var181, "\u200b", "", false, 4, null), ""), ""),
                           arrayOf(" "),
                           false,
                           0,
                           6,
                           null
                        )) {
                           var96.add(`element$ivx` as java.lang.String)
                        }
                     }
                  }

                  if (var91 && options.secretCount && var81.totalSecrets > 0) {
                     var96.add(
                        "${if (var81.checkmark === CheckmarkTypes.GREEN)
                           Math.max(var81.totalSecrets, var81.secretsCompleted)
                           else
                           (if (var81.secretsCompleted < 0) "?" else var81.secretsCompleted)}/${var81.totalSecrets}"
                     )
                  }

                  if (var93 != null || !var96.isEmpty()) {
                     val decW: Double = options.iconSize * options.roomWidth
                     if (var93 != null) {
                        val var116: Pair = drawImage$getCenterOf(var90, var89, options.iconAlignment)
                        val var122: BoundingBox = BoundingBox(
                           (var116.getFirst() as java.lang.Number).doubleValue() - decW * 0.5,
                           (var116.getSecond() as java.lang.Number).doubleValue() - decW * 0.5,
                           decW,
                           decW
                        )
                        g.drawImage(
                           var93,
                           (int)(var122.x * compToBImgFW + bImgOX),
                           (int)(var122.y * compToBImgFH + bImgOY),
                           (int)Math.ceil(var122.w * compToBImgFW),
                           (int)Math.ceil(var122.h * compToBImgFH),
                           null
                        )
                     }

                     if (!var96.isEmpty()) {
                        val var117: Pair = drawImage$getCenterOf(var90, var89, options.textAlignment)
                        g.setColor(Color.WHITE)
                        val var123: Int = (int)((var117.getFirst() as java.lang.Number).doubleValue() * compToBImgFW + bImgOX)
                        val var132: Int = (int)((var117.getSecond() as java.lang.Number).doubleValue() * compToBImgFH + bImgOY)
                        val var142: Int = var68.getHeight()
                        var var160: Int = var132 - var142 * var96.size() / 2 + var68.getAscent()

                        for (var166 in var96) {
                           val var168: Int = var68.stringWidth(var166)
                           g.setColor(Color(0, 0, 0, 255))
                           g.drawString(var166, var123 - var168 / 2 - 1, var160)
                           g.drawString(var166, var123 - var168 / 2 + 1, var160)
                           g.drawString(var166, var123 - var168 / 2, var160 - 1)
                           g.drawString(var166, var123 - var168 / 2, var160 + 1)
                           g.setColor(Color.WHITE)
                           g.drawString(var166, var123 - var168 / 2, var160)
                           var160 += var142
                        }
                     }
                  }
               }
            }
         }
      }

      g.dispose()
      return img
   }

   @JvmStatic
   fun `drawImage$colorForRoom`(options: DungeonMapRenderOptions, colors: MutableMap<DungeonMapColors, Color>, room: DungeonRoom): Color {
      var var10000: Color
      if (room.explored || options.renderUnknownRooms && room.name != null) {
         run label46@{
            when (DungeonMapBaseRenderer.WhenMappings.$EnumSwitchMapping$1[room.type.ordinal()]) {
               1 -> var10000 = colors.get(DungeonMapColors.RoomEntrance) as Color
               2 -> {
                  when (DungeonMapBaseRenderer.WhenMappings.$EnumSwitchMapping$0[room.clear.ordinal()]) {
                     1, 2 -> {
                        var10000 = colors.get(DungeonMapColors.RoomNormal) as Color
                        break@label46
                     }
                     3 -> {
                        var10000 = colors.get(DungeonMapColors.RoomMiniboss) as Color
                        break@label46
                     }
                     else -> throw NoWhenBranchMatchedException()
                  }
               }
               3 -> var10000 = colors.get(DungeonMapColors.RoomFairy) as Color
               4 -> var10000 = colors.get(DungeonMapColors.RoomBlood) as Color
               5 -> var10000 = colors.get(DungeonMapColors.RoomPuzzle) as Color
               6 -> var10000 = colors.get(DungeonMapColors.RoomTrap) as Color
               7 -> var10000 = colors.get(DungeonMapColors.RoomYellow) as Color
               8 -> var10000 = colors.get(DungeonMapColors.RoomRare) as Color
               9 -> var10000 = colors.get(DungeonMapColors.RoomUnknown) as Color
               else -> throw NoWhenBranchMatchedException()
            }
         }
      } else {
         var10000 = colors.get(DungeonMapColors.RoomUnknown) as Color
      }

      var col: Color = var10000
      if (var10000 != null && options.dungeonStarted && !room.explored) {
         col = Color(
            (int)(var10000.getRed() * options.unknownRoomsDarkenFactor + 0.5),
            (int)(var10000.getGreen() * options.unknownRoomsDarkenFactor + 0.5),
            (int)(var10000.getBlue() * options.unknownRoomsDarkenFactor + 0.5),
            var10000.getAlpha()
         )
      }

      col
   }

   @JvmStatic
   fun `drawImage$drawRect`(g: Graphics2D, bx: Int, bz: Int, bw: Int, bh: Int) {
      g.fillRect(bx, bz, bw + 1, bh + 1)
   }

   @JvmStatic
   fun `drawImage$drawDoor`(
      doorOffset: Double,
      roomRectOffset: Double,
      options: DungeonMapRenderOptions,
      compToBImgFW: Double,
      bImgOX: Double,
      compToBImgFH: Double,
      bImgOY: Double,
      g: Graphics2D,
      cx1: Int,
      cz1: Int,
      cx2: Int,
      cz2: Int
   ) {
      if (cx1 <= cx2 && cz1 <= cz2) {
         var cx: Double = 0.0
         var cz: Double = 0.0
         var cw: Double = 0.0
         var ch: Double = 0.0
         if (cx1 == cx2) {
            cx = cx1 + doorOffset
            cz = cz1 + roomRectOffset + options.roomWidth
            cw = options.doorWidth
            ch = roomRectOffset * 2.0
         } else {
            cx = cx1 + roomRectOffset + options.roomWidth
            cz = cz1 + doorOffset
            cw = roomRectOffset * 2.0
            ch = options.doorWidth
         }

         drawImage$drawRect(
            g, (int)(compToBImgFW * cx + bImgOX), (int)(compToBImgFH * cz + bImgOY), (int)Math.ceil(compToBImgFW * cw), (int)Math.ceil(compToBImgFH * ch)
         )
      } else {
         drawImage$drawDoor(doorOffset, roomRectOffset, options, compToBImgFW, bImgOX, compToBImgFH, bImgOY, g, cx2, cz2, cx1, cz1)
      }
   }

   @JvmStatic
   fun `drawImage$drawRoom`(
      roomRectOffset: Double,
      options: DungeonMapRenderOptions,
      compToBImgFW: Double,
      bImgOX: Double,
      compToBImgFH: Double,
      bImgOY: Double,
      g: Graphics2D,
      x: Int,
      z: Int,
      w: Int,
      h: Int
   ) {
      drawImage$drawRect(
         g,
         (int)(compToBImgFW * ((double)x + roomRectOffset) + bImgOX),
         (int)(compToBImgFH * ((double)z + roomRectOffset) + bImgOY),
         (int)Math.ceil(compToBImgFW * (options.roomWidth + (double)w - (double)1)),
         (int)Math.ceil(compToBImgFH * (options.roomWidth + (double)h - (double)1))
      )
   }

   @JvmStatic
   fun `drawImage$drawRoomJoined`(
      roomRectOffset: Double,
      options: DungeonMapRenderOptions,
      compToBImgFW: Double,
      bImgOX: Double,
      compToBImgFH: Double,
      bImgOY: Double,
      g: Graphics2D,
      cx1: Int,
      cz1: Int,
      cx2: Int,
      cz2: Int
   ) {
      if (cx1 <= cx2 && cz1 <= cz2) {
         var cx: Double = 0.0
         var cz: Double = 0.0
         var cw: Double = 0.0
         var ch: Double = 0.0
         if (cx1 == cx2) {
            cx = cx1 + roomRectOffset
            cz = cz1 + roomRectOffset + options.roomWidth
            cw = options.roomWidth
            ch = roomRectOffset * 2.0
         } else {
            cx = cx1 + roomRectOffset + options.roomWidth
            cz = cz1 + roomRectOffset
            cw = roomRectOffset * 2.0
            ch = options.roomWidth
         }

         drawImage$drawRect(
            g, (int)(compToBImgFW * cx + bImgOX), (int)(compToBImgFH * cz + bImgOY), (int)Math.ceil(compToBImgFW * cw), (int)Math.ceil(compToBImgFH * ch)
         )
      } else {
         drawImage$drawRoomJoined(roomRectOffset, options, compToBImgFW, bImgOX, compToBImgFH, bImgOY, g, cx2, cz2, cx1, cz1)
      }
   }

   @JvmStatic
   fun `drawImage$getCenterOf`(cells: MutableList<WorldComponentPosition>, shape: ShapeTypes, alignment: DungeonMapRoomInfoAlignment): Pair<java.lang.Double, java.lang.Double> {
      var var10000: Pair
      when (DungeonMapBaseRenderer.WhenMappings.$EnumSwitchMapping$3[alignment.ordinal()]) {
         1 -> {
            val var42: java.util.Iterator = cells.iterator()
            if (!var42.hasNext()) {
               throw NoSuchElementException()
            }

            var var48: Any = var42.next()
            val var85: Any
            if (!var42.hasNext()) {
               var85 = var48
            } else {
               var var57: Int = (var48 as WorldComponentPosition).cx + (var48 as WorldComponentPosition).cz

               do {
                  val var66: Any = var42.next()
                  val var75: Int = (var66 as WorldComponentPosition).cx + (var66 as WorldComponentPosition).cz
                  if (var57 > var75) {
                     var48 = var66
                     var57 = var75
                  }
               } while (var42.hasNext())

               var85 = var48
            }

            var10000 = Pair((double)((var85 as WorldComponentPosition).cx / 2) + 0.5, (double)((var85 as WorldComponentPosition).cz / 2) + 0.5)
            break
         }
         2 -> {
            val var40: java.util.Iterator = cells.iterator()
            if (!var40.hasNext()) {
               throw NoSuchElementException()
            }

            var var47: Any = var40.next()
            val var84: Any
            if (!var40.hasNext()) {
               var84 = var47
            } else {
               var var55: Int = -(var47 as WorldComponentPosition).cx + (var47 as WorldComponentPosition).cz

               do {
                  val var64: Any = var40.next()
                  val var73: Int = -(var64 as WorldComponentPosition).cx + (var64 as WorldComponentPosition).cz
                  if (var55 > var73) {
                     var47 = var64
                     var55 = var73
                  }
               } while (var40.hasNext())

               var84 = var47
            }

            var10000 = Pair((double)((var84 as WorldComponentPosition).cx / 2) + 0.5, (double)((var84 as WorldComponentPosition).cz / 2) + 0.5)
            break
         }
         3 -> {
            val var38: java.util.Iterator = cells.iterator()
            if (!var38.hasNext()) {
               throw NoSuchElementException()
            }

            var var46: Any = var38.next()
            val var83: Any
            if (!var38.hasNext()) {
               var83 = var46
            } else {
               var var53: Int = (var46 as WorldComponentPosition).cx - (var46 as WorldComponentPosition).cz

               do {
                  val var62: Any = var38.next()
                  val var71: Int = (var62 as WorldComponentPosition).cx - (var62 as WorldComponentPosition).cz
                  if (var53 > var71) {
                     var46 = var62
                     var53 = var71
                  }
               } while (var38.hasNext())

               var83 = var46
            }

            var10000 = Pair((double)((var83 as WorldComponentPosition).cx / 2) + 0.5, (double)((var83 as WorldComponentPosition).cz / 2) + 0.5)
            break
         }
         4 -> {
            val var36: java.util.Iterator = cells.iterator()
            if (!var36.hasNext()) {
               throw NoSuchElementException()
            }

            var var45: Any = var36.next()
            val var82: Any
            if (!var36.hasNext()) {
               var82 = var45
            } else {
               var var51: Int = -(var45 as WorldComponentPosition).cx - (var45 as WorldComponentPosition).cz

               do {
                  val var60: Any = var36.next()
                  val var69: Int = -(var60 as WorldComponentPosition).cx - (var60 as WorldComponentPosition).cz
                  if (var51 > var69) {
                     var45 = var60
                     var51 = var69
                  }
               } while (var36.hasNext())

               var82 = var45
            }

            var10000 = Pair((double)((var82 as WorldComponentPosition).cx / 2) + 0.5, (double)((var82 as WorldComponentPosition).cz / 2) + 0.5)
            break
         }
         5 -> {
            if (shape === ShapeTypes.ShapeL) {
               val sorted: java.util.List = CollectionsKt.sortedWith(cells, DungeonMapBaseRenderer$drawImage$getCenterOf$$inlined$sortedBy$1())
               val var25: Int = if ((sorted.get(0) as WorldComponentPosition).cx > (sorted.get(1) as WorldComponentPosition).cx)
                  2
                  else
                  (if ((sorted.get(0) as WorldComponentPosition).cx == (sorted.get(2) as WorldComponentPosition).cx) 0 else 1)
                  var10000 = Pair(
                  (double)((sorted.get(var25) as WorldComponentPosition).cx / 2) + 0.5, (double)((sorted.get(var25) as WorldComponentPosition).cz / 2) + 0.5
               )
            } else {
               var var19: java.lang.Iterable = cells
               var var26: Double = 0.0

               for (`minValue$iv` in var19) {
                  var26 += (`minValue$iv` as WorldComponentPosition).cx / 2.0
               }

               val var81: java.lang.Double = var26 / (double)cells.size() + 0.5
               var19 = cells
               var26 = 0.0

               for (var49 in var19) {
                  var26 += (var49 as WorldComponentPosition).cz / 2.0
               }

               var10000 = Pair(var81, var26 / (double)cells.size() + 0.5)
            }
         }
         else -> throw NoWhenBranchMatchedException()
      }

      var10000
   }
}
