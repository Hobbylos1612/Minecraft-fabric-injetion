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
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nDungeonMapFeature.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonMapFeature.kt\njooon/features/dungeons/map/DungeonMapFeature\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,415:1\n1045#2:416\n1747#2,3:417\n*S KotlinDebug\n*F\n+ 1 DungeonMapFeature.kt\njooon/features/dungeons/map/DungeonMapFeature\n*L\n357#1:416\n294#1:417,3\n*E\n"])
public object DungeonMapFeature {
   private final val mapRenderer: DungeonMapBaseRenderer by LazyKt.lazy({ 
      DungeonMapBaseRenderer()
   })
      private final get() {
         return mapRenderer$delegate.getValue() as DungeonMapBaseRenderer
      }


   private final var movableOverlay: MovableOverlay?
   private final var appliedSavedPos: Boolean
   private final var lastScale: Double = -1.0
   @JvmStatic
   private Identifier mcidMarkerAtlas;

   private final val markerAtlasUploader: BufferedImageUploader by LazyKt.lazy({ 
      val var10000: BufferedImageUploader = BufferedImageUploader.Companion.fromResource("/assets/jooonreimagined/dungeons/map/markeratlas.png")
      var10000.register(mcidMarkerAtlas)
   })
      private final get() {
         return markerAtlasUploader$delegate.getValue() as BufferedImageUploader
      }


   public final val MARKER_SELF_U0: Float = 0 / 200.0F
   public final val MARKER_SELF_V0: Float = 0 / 280.0F
   public final val MARKER_SELF_U1: Float = (0 + 100) / 200.0F
   public final val MARKER_SELF_V1: Float = (0 + 140) / 280.0F
   public final val MARKER_OTHER_U0: Float = 100 / 200.0F
   public final val MARKER_OTHER_V0: Float = 0 / 280.0F
   public final val MARKER_OTHER_U1: Float = (100 + 100) / 200.0F
   public final val MARKER_OTHER_V1: Float = (0 + 140) / 280.0F
   public final val MARKER_POINTER_OUTLINE_U0: Float = 0 / 200.0F
   public final val MARKER_POINTER_OUTLINE_V0: Float = 140 / 280.0F
   public final val MARKER_POINTER_OUTLINE_U1: Float = (0 + 100) / 200.0F
   public final val MARKER_POINTER_OUTLINE_V1: Float = (140 + 140) / 280.0F
   public final val MARKER_HEAD_OUTLINE_U0: Float = 100 / 200.0F
   public final val MARKER_HEAD_OUTLINE_V0: Float = 140 / 280.0F
   public final val MARKER_HEAD_OUTLINE_U1: Float = (100 + 80) / 200.0F
   public final val MARKER_HEAD_OUTLINE_V1: Float = (140 + 80) / 280.0F

   public fun init() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { it: MinecraftClient ->
               if (Config.dungeonMapScale != lastScale) {
                  lastScale = Config.dungeonMapScale
                  INSTANCE.onConfigChanged()
               }

               DungeonScanner.INSTANCE.tick()
               val isDungeon: Boolean = Utils.INSTANCE.inDungeon
               if (!isDungeon && Dungeons.INSTANCE.started) {
                  Dungeons.INSTANCE.reset()
               }

               if (it.field_1724 != null && it.field_1724.field_6012 % 20 == 0 && isDungeon) {
                  for (line in ScoreboardUtil.INSTANCE.getSidebarLines()) {
                     if (StringsKt.contains$default(line, "The Catacombs", false, 2, null) && StringsKt.contains$default(line, "(", false, 2, null)) {
                        var var10000: FloorType
                        run label139@{
                           val var7: java.lang.String = StringsKt.trim(
            StringsKt.substringBefore$default(StringsKt.substringAfter$default(line, "(", null, 2, null), ")", null, 2, null)
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

                        if (var10000 != FloorType.None && Dungeons.INSTANCE.floor != var10000) {
                           Dungeons.INSTANCE.floor = var10000
                           Dungeons.INSTANCE.started = true
                        }
                     }
                  }
               }
            }
         )
         ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         Dungeons.INSTANCE.reset()
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         Dungeons.INSTANCE.reset()
      })
      HudRenderCallback.EVENT
         .register(
            { ctx: DrawContext, deltaTracker: RenderTickCounter ->
               if (Config.dungeonMapEnabled) {
                  INSTANCE.ensureOverlayReady(ctx.method_51421(), ctx.method_51443())
                  if (Utils.INSTANCE.inDungeon && !PersistentState.dungeonMapMovable) {
                     val tickDelta: Float = deltaTracker.method_60637(false)
                     val var10000: Window = MinecraftClient.method_1551().method_22683()
                     INSTANCE.draw(
                        ctx,
                        (float)((double)PersistentState.dungeonMapX / 10000.0 * (double)var10000.method_4486()),
                        (float)((double)PersistentState.dungeonMapY / 10000.0 * (double)var10000.method_4502()),
                        tickDelta
                     )
                  }
               }
            }
         )
         ClientReceiveMessageEvents.CHAT.register({ message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->
         var var10000: java.lang.String = message.getString()
         if (StringsKt.contains$default(var10000, "entered The Catacombs, Floor", false, 2, null)) {
            run label59@{
               val var8: java.lang.String = StringsKt.trim(StringsKt.substringAfter$default(var10000, "Floor ", null, 2, null), charArrayOf('!', '.'))
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

            if (Dungeons.INSTANCE.floor != var10000) {
               Dungeons.INSTANCE.floor = var10000
               Dungeons.INSTANCE.started = true
            }
         }
      })
   }

   fun onMapPacket(packet: MapUpdateS2CPacket) {
      if (Config.dungeonMapEnabled) {
         val var10000: MapIdComponent = packet.comp_2270()
         if ((var10000.comp_2315() and 1000) == 0) {
            val var6: MinecraftClient = MinecraftClient.method_1551()
            if (var6.field_1687 != null) {
               val var7: MapState = FilledMapItem.method_7997(var10000, var6.field_1687 as World)
               if (var7 != null) {
                  DungeonScanner.INSTANCE.onMapPacket(packet, var7)
               }
            }
         }
      }
   }

   public fun redrawMap(rooms: List<DungeonRoom?>, doors: List<DungeonDoor?>) {
      val var10000: Window = MinecraftClient.method_1551().method_22683()
      val options: DungeonMapRenderOptions = DungeonMapRenderOptions(
         MapsKt.mapOf(
            arrayOf(
               TuplesKt.to(DungeonMapColors.Background, Color(0, 0, 0, 0)),
               TuplesKt.to(DungeonMapColors.Border, Color(0, 255, 0, 255)),
               TuplesKt.to(DungeonMapColors.RoomEntrance, Color(0, 123, 0)),
               TuplesKt.to(DungeonMapColors.RoomNormal, Color(114, 67, 27)),
               TuplesKt.to(DungeonMapColors.RoomMiniboss, Color(114, 67, 27)),
               TuplesKt.to(DungeonMapColors.RoomFairy, Color(239, 126, 163)),
               TuplesKt.to(DungeonMapColors.RoomBlood, Color(255, 0, 0)),
               TuplesKt.to(DungeonMapColors.RoomPuzzle, Color(176, 75, 213)),
               TuplesKt.to(DungeonMapColors.RoomTrap, Color(213, 126, 50)),
               TuplesKt.to(DungeonMapColors.RoomYellow, Color(226, 226, 50)),
               TuplesKt.to(DungeonMapColors.RoomRare, Color(0, 67, 27)),
               TuplesKt.to(DungeonMapColors.RoomUnknown, Color(64, 64, 64)),
               TuplesKt.to(DungeonMapColors.DoorWither, Color(0, 0, 0)),
               TuplesKt.to(DungeonMapColors.DoorBlood, Color(255, 0, 0)),
               TuplesKt.to(DungeonMapColors.DoorEntrance, Color(0, 123, 0))
            )
         ),
         Config.dungeonMapRoomSize,
         Config.dungeonMapDoorSize,
         if (Dungeons.INSTANCE.floor.roomsW > 0) Dungeons.INSTANCE.floor.roomsW else 6,
         if (Dungeons.INSTANCE.floor.roomsH > 0) Dungeons.INSTANCE.floor.roomsH else 6,
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
         Dungeons.INSTANCE.started,
         Config.dungeonMapHiddenRoomDarken
      )
      val boundsW: Double = 100.0 * Config.dungeonMapScale
      val boundsH: Double = 100.0 * Config.dungeonMapScale
      this.mapRenderer
         .update(
            (int)(boundsW * (double)var10000.method_4495() + 0.5),
            (int)(boundsH * (double)var10000.method_4495() + 0.5),
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
         val var10000: Window = MinecraftClient.method_1551().method_22683()
         movableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "dungeonMap",
               "Dungeon Map",
               MathKt.roundToInt((double)PersistentState.dungeonMapX / 10000.0 * (double)var10000.method_4486()),
               MathKt.roundToInt((double)PersistentState.dungeonMapY / 10000.0 * (double)var10000.method_4502()),
               (int)((double)100 * Config.dungeonMapScale),
               (int)((double)100 * Config.dungeonMapScale)
            )
            if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_8@{ ctx: DrawContext, x: Int, y: Int, tickDelta: Float ->
               val isDungeon: Boolean = Utils.INSTANCE.inDungeon
               val isPreview: Boolean = MinecraftClient.method_1551().field_1755 is MovableOverlayScreen
               if (isDungeon) {
                  val scale: java.lang.Iterable = DungeonScanner.INSTANCE.rooms
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
                     INSTANCE.draw(ctx, (float)x, (float)y, tickDelta)
                     return@lambda_8 Unit.INSTANCE
                  }
               }

               if (isPreview) {
                  ctx.method_25294(
                     x,
                     y,
                     x + (int)((float)100 * (float)Config.dungeonMapScale),
                     y + (int)((float)100 * (float)Config.dungeonMapScale),
                     Color(0, 0, 0, 100).getRGB()
                  )
                  ctx.method_25303(MinecraftClient.method_1551().field_1772, "Dungeon Map", x + 5, y + 5, -1)
               }

               return@lambda_8 Unit.INSTANCE
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->
               val var10000: Window = MinecraftClient.method_1551().method_22683()
               PersistentState.dungeonMapX = MathKt.roundToInt((double)x / (double)var10000.method_4486() * (double)10000)
               PersistentState.dungeonMapY = MathKt.roundToInt((double)y / (double)var10000.method_4502() * (double)10000)
               PersistentState.dungeonMapInitDone = true
               JooonConfigManager.INSTANCE.write("jooonreimagined_state")
               Unit.INSTANCE
            }
         }
      }

      if (Config.dungeonMapEnabled && PersistentState.dungeonMapMovable) {
         if (movableOverlay != null) {
            movableOverlay.register()
         }
      }

      if (movableOverlay != null) {
         movableOverlay.width = (int)((double)100 * Config.dungeonMapScale)
      }

      if (movableOverlay != null) {
         movableOverlay.height = (int)((double)100 * Config.dungeonMapScale)
      }

      if (!appliedSavedPos) {
         val var9: Window = MinecraftClient.method_1551().method_22683()
         val var7: Int = MathKt.roundToInt((double)PersistentState.dungeonMapX / 10000.0 * (double)var9.method_4486())
         val var8: Int = MathKt.roundToInt((double)PersistentState.dungeonMapY / 10000.0 * (double)var9.method_4502())
         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(var7, var8)
         }

         appliedSavedPos = true
      }
   }

   fun draw(ctx: DrawContext, x: Float, y: Float, tickDelta: Float) {
      var var10000: Window = MinecraftClient.method_1551()
      val mc: MinecraftClient = var10000
      val scale: Float = (float)Config.dungeonMapScale
      this.markerAtlasUploader
      var floor: FloorType = Dungeons.INSTANCE.floor
      if (floor === FloorType.None) {
         floor = FloorType.M7
      }

      var compBounds: BoundingBox
      run label85@{
         var10000 = MinecraftClient.method_1551().method_22683()
         this.mapRenderer.draw(ctx, x, y, (float)(1.0 / (double)var10000.method_4495()))
         val bounds: BoundingBox = BoundingBox(x, y, 100.0 * scale, 100.0 * scale)
         val totalMaxDim: Double = floor.maxDim + Config.dungeonMapPadding * 2
         val boundsOX: Double = (floor.maxDim - floor.roomsW) / 2.0 + Config.dungeonMapPadding
         val boundsOY: Double = (floor.maxDim - floor.roomsH) / 2.0 + Config.dungeonMapPadding
         compBounds = BoundingBox(
            bounds.x + boundsOX / totalMaxDim * bounds.w,
            bounds.y + boundsOY / totalMaxDim * bounds.h,
            floor.maxDim / totalMaxDim * bounds.w,
            floor.maxDim / totalMaxDim * bounds.h
         )
         if (var10000.field_1724 != null) {
            var10000 = var10000.field_1724.method_7334()
            if (var10000 != null) {
               var10000 = var10000.name()
               if (var10000 != null) {
                  return@label85
               }
            }
         }

         var10000 = ""
      }

      val localName: java.lang.String = var10000
      val var37: java.util.Collection = Dungeons.INSTANCE.players.values()

      for (var10000 in CollectionsKt.sortedWith(var37, DungeonMapFeature$draw$$inlined$sortedBy$1(var10000))) {
         val var31: DungeonPlayer = var10000 as DungeonPlayer
         val var39: PlayerComponentPosition = (var10000 as DungeonPlayer).getLerpedPosition(tickDelta)
         if (var39 != null) {
            val px: Float = (float)MathUtils.INSTANCE.rescale(var39.x, 0.0, (double)floor.maxDim * 2.0, compBounds.x, compBounds.x + compBounds.w)
            val py: Float = (float)MathUtils.INSTANCE.rescale(var39.z, 0.0, (double)floor.maxDim * 2.0, compBounds.y, compBounds.y + compBounds.h)
            val isSelf: Boolean = var31.name == localName
            val markerScale: Float = RangesKt.coerceIn((float)Config.dungeonMapMarkerScale, 2.0F, 8.0F)
            if (Config.dungeonMapPlayerHeads && !isSelf) {
               run label88@{
                  val var40: ClientPlayNetworkHandler = mc.method_1562()
                  val var32: PlayerListEntry = if (var40 != null) var40.method_2871(var31.uuid) else null
                  if (var32 != null) {
                     var10000 = var32.method_52810()
                     if (var10000 != null) {
                        return@label88
                     }
                  }

                  var10000 = DefaultSkinHelper.method_4648(var31.uuid)
               }

               PlayerSkinDrawer.method_52722(
                  ctx, var10000, (int)(px - markerScale * scale / (float)2), (int)(py - markerScale * scale / (float)2), (int)(markerScale * scale)
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
                     (float)(var39.r * Math.PI / 180.0)
                  )
               }
         }
      }
   }

   public fun onConfigChanged() {
      appliedSavedPos = false
      if (Config.dungeonMapEnabled) {
         if (PersistentState.dungeonMapMovable) {
            this.ensureOverlayReady(MinecraftClient.method_1551().method_22683().method_4486(), MinecraftClient.method_1551().method_22683().method_4502())
            if (movableOverlay != null) {
               movableOverlay.register()
            }
         }

         this.redrawMap(CollectionsKt.toList(DungeonScanner.INSTANCE.rooms), CollectionsKt.toList(DungeonScanner.INSTANCE.doors))
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   @JvmStatic
   fun {
      val var10000: Identifier = Identifier.method_60654("jooonreimagined:dungeons/map/markeratlas.png")
      mcidMarkerAtlas = var10000
   }
}
