package jooon.features.dungeons.map

import com.mojang.authlib.GameProfile
import java.awt.Color
import java.time.Instant
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.features.dungeons.map.api.DungeonDoor
import jooon.features.dungeons.map.api.DungeonPlayer
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.Dungeons
import jooon.features.dungeons.map.api.FloorType
import jooon.features.dungeons.map.api.PlayerComponentPosition
import jooon.features.dungeons.map.util.BoundingBox
import jooon.features.dungeons.map.util.MapRenderUtils
import jooon.features.dungeons.map.util.MathUtils
import jooon.features.dungeons.map.util.bufimgrenderer.BufferedImageUploader
import jooon.gui.MovableOverlayScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import jooon.util.ScoreboardUtil
import jooon.util.Utils
import kotlin.math.MathKt
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.PlayerSkinDrawer
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.client.util.Window
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.FilledMapItem
import net.minecraft.item.map.MapState
import net.minecraft.network.message.SignedMessage
import net.minecraft.network.message.MessageType.Parameters
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World

object DungeonMapFeature {
   private val mapRenderer: DungeonMapBaseRenderer by lazy({ 
      DungeonMapBaseRenderer()
   })
      private get() {
         return mapRenderer$delegate.getValue() as DungeonMapBaseRenderer
      }


   private var movableOverlay: MovableOverlay?
   private var appliedSavedPos: Boolean
   private var lastScale: Double = -1.0
   
   private Identifier mcidMarkerAtlas;

   private val markerAtlasUploader: BufferedImageUploader by lazy({ 

      var10000.register(mcidMarkerAtlas)
   })
      private get() {
         return markerAtlasUploader$delegate.getValue() as BufferedImageUploader
      }

















   fun init() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { it: MinecraftClient ->
               if (Config.dungeonMapScale != lastScale) {
                  lastScale = Config.dungeonMapScale
                  onConfigChanged()
               }

               DungeonScanner.tick()

               if (!isDungeon && Dungeons.started) {
                  Dungeons.reset()
               }

               if (it.player != null && it.player.age % 20 == 0 && isDungeon) {
                  for (line in ScoreboardUtil.getSidebarLines()) {
                     if (contains$default(line, "The Catacombs", false, 2, null) && contains$default(line, "(", false, 2, null)) {
                        var var10000: FloorType
                        run label139@{

            substringBefore$default(substringAfter$default(line, "(", null, 2, null), ")", null, 2, null)
         )
         .toString()
                              when (var7.hashCode()) {
                              69 -> {
                                 if (var7.equals("E")) {
                                    var10000 = FloorType.Entrance
                                    return@label139
                                 }
                              }
                              2219 -> {
                                 if (var7.equals("F1")) {
                                    var10000 = FloorType.F1
                                    return@label139
                                 }
                              }
                              2220 -> {
                                 if (var7.equals("F2")) {
                                    var10000 = FloorType.F2
                                    return@label139
                                 }
                              }
                              2221 -> {
                                 if (var7.equals("F3")) {
                                    var10000 = FloorType.F3
                                    return@label139
                                 }
                              }
                              2222 -> {
                                 if (var7.equals("F4")) {
                                    var10000 = FloorType.F4
                                    return@label139
                                 }
                              }
                              2223 -> {
                                 if (var7.equals("F5")) {
                                    var10000 = FloorType.F5
                                    return@label139
                                 }
                              }
                              2224 -> {
                                 if (var7.equals("F6")) {
                                    var10000 = FloorType.F6
                                    return@label139
                                 }
                              }
                              2225 -> {
                                 if (var7.equals("F7")) {
                                    var10000 = FloorType.F7
                                    return@label139
                                 }
                              }
                              2436 -> {
                                 if (var7.equals("M1")) {
                                    var10000 = FloorType.M1
                                    return@label139
                                 }
                              }
                              2437 -> {
                                 if (var7.equals("M2")) {
                                    var10000 = FloorType.M2
                                    return@label139
                                 }
                              }
                              2438 -> {
                                 if (var7.equals("M3")) {
                                    var10000 = FloorType.M3
                                    return@label139
                                 }
                              }
                              2439 -> {
                                 if (var7.equals("M4")) {
                                    var10000 = FloorType.M4
                                    return@label139
                                 }
                              }
                              2440 -> {
                                 if (var7.equals("M5")) {
                                    var10000 = FloorType.M5
                                    return@label139
                                 }
                              }
                              2441 -> {
                                 if (var7.equals("M6")) {
                                    var10000 = FloorType.M6
                                    return@label139
                                 }
                              }
                              2442 -> {
                                 if (var7.equals("M7")) {
                                    var10000 = FloorType.M7
                                    return@label139
                                 }
                              }
                              else -> {}
                           }

                           var10000 = FloorType.None
                        }

                        if (var10000 != FloorType.None && Dungeons.floor != var10000) {
                           Dungeons.floor = var10000
                           Dungeons.started = true
                        }
                     }
                  }
               }
            }
         )
         ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         Dungeons.reset()
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         Dungeons.reset()
      })
      HudRenderCallback.EVENT
         .register(
            { ctx: DrawContext, deltaTracker: RenderTickCounter ->
               if (Config.dungeonMapEnabled) {
                  ensureOverlayReady(ctx.getScaledWindowWidth(), ctx.getScaledWindowHeight())
                  if (Utils.inDungeon && !PersistentState.dungeonMapMovable) {


                     draw(
                        ctx,
                        (PersistentState.dungeonMapX.toDouble() / 10000.0 * var10000.getScaledWidth().toDouble()).toFloat(),
                        (PersistentState.dungeonMapY.toDouble() / 10000.0 * var10000.getScaledHeight().toDouble()).toFloat(),
return tickDelta
                     )
                  }
               }
            }
         )
         ClientReceiveMessageEvents.CHAT.register({ message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->
         var var10000: String = message.getString()
         if (contains$default(var10000, "entered The Catacombs, Floor", false, 2, null)) {
            run label59@{

               when (var8.hashCode()) {
                  -2029719850 -> {
                     if (var8.equals("Entrance")) {
                        var10000 = FloorType.Entrance
                        return@label59
                     }
                  }
                  73 -> {
                     if (var8.equals("I")) {
                        var10000 = FloorType.F1
                        return@label59
                     }
                  }
                  86 -> {
                     if (var8.equals("V")) {
                        var10000 = FloorType.F5
                        return@label59
                     }
                  }
                  2336 -> {
                     if (var8.equals("II")) {
                        var10000 = FloorType.F2
                        return@label59
                     }
                  }
                  2349 -> {
                     if (var8.equals("IV")) {
                        var10000 = FloorType.F4
                        return@label59
                     }
                  }
                  2739 -> {
                     if (var8.equals("VI")) {
                        var10000 = FloorType.F6
                        return@label59
                     }
                  }
                  72489 -> {
                     if (var8.equals("III")) {
                        var10000 = FloorType.F3
                        return@label59
                     }
                  }
                  84982 -> {
                     if (var8.equals("VII")) {
                        var10000 = FloorType.F7
                        return@label59
                     }
                  }
                  else -> {}
               }

               var10000 = FloorType.F1
            }

            if (Dungeons.floor != var10000) {
               Dungeons.floor = var10000
               Dungeons.started = true
            }
         }
      })
   }

   fun onMapPacket(packet: MapUpdateS2CPacket) {
      if (Config.dungeonMapEnabled) {

         if ((var10000.id() and 1000) == 0) {

            if (var6.world != null) {

               if (var7 != null) {
                  DungeonScanner.onMapPacket(packet, var7)
               }
            }
         }
      }
   }

   fun redrawMap(rooms: List<DungeonRoom?>, doors: List<DungeonDoor?>) {


         mapOf(
            arrayOf(
               Pair(DungeonMapColors.Background, Color(0, 0, 0, 0)),
               Pair(DungeonMapColors.Border, Color(0, 255, 0, 255)),
               Pair(DungeonMapColors.RoomEntrance, Color(0, 123, 0)),
               Pair(DungeonMapColors.RoomNormal, Color(114, 67, 27)),
               Pair(DungeonMapColors.RoomMiniboss, Color(114, 67, 27)),
               Pair(DungeonMapColors.RoomFairy, Color(239, 126, 163)),
               Pair(DungeonMapColors.RoomBlood, Color(255, 0, 0)),
               Pair(DungeonMapColors.RoomPuzzle, Color(176, 75, 213)),
               Pair(DungeonMapColors.RoomTrap, Color(213, 126, 50)),
               Pair(DungeonMapColors.RoomYellow, Color(226, 226, 50)),
               Pair(DungeonMapColors.RoomRare, Color(0, 67, 27)),
               Pair(DungeonMapColors.RoomUnknown, Color(64, 64, 64)),
               Pair(DungeonMapColors.DoorWither, Color(0, 0, 0)),
               Pair(DungeonMapColors.DoorBlood, Color(255, 0, 0)),
               Pair(DungeonMapColors.DoorEntrance, Color(0, 123, 0))
            )
         ),
         Config.dungeonMapRoomSize,
         Config.dungeonMapDoorSize,
         if (Dungeons.floor.roomsW > 0) Dungeons.floor.roomsW else 6,
         if (Dungeons.floor.roomsH > 0) Dungeons.floor.roomsH else 6,
         Config.dungeonMapPadding,
         Config.dungeonMapBorder,
         Config.dungeonMapRenderCheckmark,
         Config.dungeonMapRenderRoomNames,
         Config.dungeonMapRenderRoomNamesNotEFB,
         Config.dungeonMapRenderSecretCount,
         Config.dungeonMapRenderPuzzleName,
         Config.dungeonMapIconSize,
         DungeonMapRoomInfoAlignment.Center,
         Config.dungeonMapTextSize,
         DungeonMapRoomInfoAlignment.TopLeft,
         Config.dungeonMapColorRoomName,
         Config.dungeonMapRenderHiddenRooms,
         Dungeons.started,
         Config.dungeonMapHiddenRoomDarken
      )


      this.mapRenderer
         .update(
            (boundsW * var10000.getScaleFactor().toDouble() + 0.5).toInt(),
            (boundsH * var10000.getScaleFactor().toDouble() + 0.5).toInt(),
            DungeonMapRenderData(rooms, doors, options)
         )
      }

   private fun ensureOverlayReady(sw: Int, sh: Int) {
      if (!PersistentState.dungeonMapInitDone) {
         PersistentState.dungeonMapX = 100
         PersistentState.dungeonMapY = 100
         PersistentState.dungeonMapInitDone = true
      }

      if (movableOverlay == null) {

         movableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "dungeonMap",
               "Dungeon Map",
               (PersistentState.dungeonMapX.toDouble() / 10000.0 * var10000.getScaledWidth().toDouble()).roundToInt(),
               (PersistentState.dungeonMapY.toDouble() / 10000.0 * var10000.getScaledHeight().toDouble()).roundToInt(),
               (100.toDouble() * Config.dungeonMapScale).toInt(),
               (100.toDouble() * Config.dungeonMapScale).toInt()
            )
            if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_8@{ ctx: DrawContext, x: Int, y: Int, tickDelta: Float ->


               if (isDungeon) {
                  val scale: java.lang.Iterable = DungeonScanner.rooms
                  var var10000: Boolean
                  if (scale is java.util.Collection && (scale as java.util.Collection).isEmpty()) {
                     var10000 = false
                  } else {
                     run label51@{
                        for (`element$iv` in scale) {
                           if (`element$iv` as DungeonRoom != null) {
                              var10000 = true
                              return@label51
                           }
                        }

                        var10000 = false
                     }
                  }

                  if (var10000) {
                     draw(ctx, x.toFloat(), y.toFloat(), tickDelta)
                     return@lambda_8 Unit
                  }
               }

               if (isPreview) {
                  ctx.fill(
                     x,
                     y,
                     x + (100.toFloat() * Config.dungeonMapScale.toFloat()).toInt(),
                     y + (100.toFloat() * Config.dungeonMapScale.toFloat()).toInt(),
                     Color(0, 0, 0, 100).getRGB()
                  )
                  ctx.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, "Dungeon Map", x + 5, y + 5, -1)
               }

               return@lambda_8 Unit
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->

               PersistentState.dungeonMapX = (x.toDouble() / var10000.getScaledWidth().toDouble() * 10000.toDouble()).roundToInt()
               PersistentState.dungeonMapY = (y.toDouble() / var10000.getScaledHeight().toDouble() * 10000.toDouble()).roundToInt()
               PersistentState.dungeonMapInitDone = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
         }
      }

      if (Config.dungeonMapEnabled && PersistentState.dungeonMapMovable) {
         if (movableOverlay != null) {
            movableOverlay.register()
         }
      }

      if (movableOverlay != null) {
         movableOverlay.width = (100.toDouble() * Config.dungeonMapScale).toInt()
      }

      if (movableOverlay != null) {
         movableOverlay.height = (100.toDouble() * Config.dungeonMapScale).toInt()
      }

      if (!appliedSavedPos) {



         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(var7, var8)
         }

         appliedSavedPos = true
      }
   }

   fun draw(ctx: DrawContext, x: Float, y: Float, tickDelta: Float) {
      var var10000: Window = MinecraftClient.getInstance()


      this.markerAtlasUploader
      var floor: FloorType = Dungeons.floor
      if (floor === FloorType.None) {
         floor = FloorType.M7
      }

      var compBounds: BoundingBox
      run label85@{
         var10000 = MinecraftClient.getInstance().getWindow()
         this.mapRenderer.draw(ctx, x, y, (1.0 / var10000.getScaleFactor().toDouble()).toFloat())




         compBounds = BoundingBox(
            bounds.x + boundsOX / totalMaxDim * bounds.w,
            bounds.y + boundsOY / totalMaxDim * bounds.h,
            floor.maxDim / totalMaxDim * bounds.w,
            floor.maxDim / totalMaxDim * bounds.h
         )
         if (var10000.player != null) {
            var10000 = var10000.player.getGameProfile()
            if (var10000 != null) {
               var10000 = var10000.name()
               if (var10000 != null) {
                  return@label85
               }
            }
         }

         var10000 = ""
      }

      val var37: java.util.Collection = Dungeons.players.values()

      for (var10000 in sortedWith(var37, DungeonMapFeature$draw$$inlined$sortedBy$1(var10000))) {


         if (var39 != null) {




            if (Config.dungeonMapPlayerHeads && !isSelf) {
               run label88@{


                  if (var32 != null) {
                     var10000 = var32.getSkinTextures()
                     if (var10000 != null) {
                        return@label88
                     }
                  }

                  var10000 = DefaultSkinHelper.getSkinTextures(var31.uuid)
               }

               PlayerSkinDrawer.draw(
                  ctx, var10000, (px - markerScale * scale / 2.toFloat()).toInt(), (py - markerScale * scale / 2.toFloat()).toInt(), (markerScale * scale).toInt()
               )
            } else {
               MapRenderUtils.INSTANCE
                  .drawRotatedQuad(
                     ctx,
                     mcidMarkerAtlas,
                     px,
                     py,
                     markerScale * scale,
                     markerScale * 1.4F * scale,
                     if (isSelf) MARKER_SELF_U0 else MARKER_OTHER_U0,
                     if (isSelf) MARKER_SELF_V0 else MARKER_OTHER_V0,
                     if (isSelf) MARKER_SELF_U1 else MARKER_OTHER_U1,
                     if (isSelf) MARKER_SELF_V1 else MARKER_OTHER_V1,
                     200,
                     280,
                     (var39.r * Math.PI / 180.0).toFloat()
                  )
               }
         }
      }
   }

   fun onConfigChanged() {
      appliedSavedPos = false
      if (Config.dungeonMapEnabled) {
         if (PersistentState.dungeonMapMovable) {
            this.ensureOverlayReady(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight())
            if (movableOverlay != null) {
               movableOverlay.register()
            }
         }

         this.redrawMap(toList(DungeonScanner.rooms), toList(DungeonScanner.doors))
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   
   fun {

      mcidMarkerAtlas = var10000
   }
}
