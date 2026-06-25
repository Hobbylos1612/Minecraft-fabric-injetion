package jooon.gui

import java.io.Closeable
import java.io.InputStream
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.Optional
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.ConfigFlush
import jooon.config.JooonConfigManager
import jooon.features.fishing.FishingMeleeMobs
import jooon.features.fishing.FishingMeleeStore
import jooon.mixins.MinecraftAccessor
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.minecraft.resource.Resource
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

@SourceDebugExtension(["SMAP\nFishingMeleeScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FishingMeleeScreen.kt\njooon/gui/FishingMeleeScreen\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,491:1\n1#2:492\n1855#3,2:493\n766#3:495\n857#3,2:496\n*S KotlinDebug\n*F\n+ 1 FishingMeleeScreen.kt\njooon/gui/FishingMeleeScreen\n*L\n480#1:493,2\n209#1:495\n209#1:496,2\n*E\n"])
public object FishingMeleeScreen : Screen(Text.method_43470("Auto Fishing Melee Mobs") as Text) {
   private final val CARD_W: Int = 300
   private final val CARD_H: Int = 360
   private final val CARD_PAD: Int = 10
   private final val CARD_GAP: Int = 16
   private final val MARGIN_L: Int = 40
   private final val MARGIN_T: Int = 70
   private final val VIEW_H: Int = CARD_H + 2
   private final var barGrabOffset: Float
   private final val allMobs: List<String> =
      CollectionsKt.listOf(
         arrayOf(
            "Squid",
            "Sea Walker",
            "Sea Guardian",
            "Sea Witch",
            "Sea Archer",
            "Rider Of The Deep",
            "Catfish",
            "Carrot King",
            "Sea Leech",
            "Guardian Defender",
            "Deep Sea Protector",
            "Water Hydra",
            "Sea Emperor",
            "Scarecrow",
            "Nightmare",
            "Werewolf",
            "Phantom Fisher",
            "Grim Reaper",
            "Frozen Steve",
            "Frosty The Snowman",
            "Grinch",
            "Yeti",
            "Nutcracker",
            "Reindrake",
            "Nurse Shark",
            "Blue Shark",
            "Tiger Shark",
            "Great White Shark",
            "Magma Slug",
            "Moogma",
            "Lava Leech",
            "Pyroclastic Worm",
            "Lava Flame",
            "Fire Eel",
            "Taurus",
            "Thunder",
            "Lord Jawbus",
            "Water Worm",
            "Poisoned Water Worm",
            "Zombie Miner",
            "Flaming Worm",
            "Lava Blaze",
            "Lava Pigman",
            "Agarimoo",
            "Oasis Rabbit",
            "Oasis Sheep",
            "Abyssal Miner",
            "Banshee",
            "Bayou Sludge",
            "Blue Ringed Octopus",
            "Dumpster Diver",
            "Frog Man",
            "Snapping Turtle",
            "Titanoboa",
            "Trash Gobbler",
            "Wiki Tiki",
            "Alligator",
            "Bloated Mithril Grubber",
            "Large Mithril Grubber",
            "Medium Mithril Grubber",
            "Mithril Grubber",
            "Fried Chicken",
            "Fireproof Witch",
            "Fiery Scuttler",
            "Ragnarok",
            "Loch Emperor",
            "Ent",
            "Tadgang",
            "Wetwing",
            "Bogged"
         )
      )
      private final var query: String = ""
   private final var filtered: List<String> = allMobs
   private final var scrollX: Float
   private final var dragging: Boolean
   private final var dragStartX: Double
   private final var scrollStartX: Float
   private final var barDragging: Boolean
   private final var barKnobX0: Int
   private final var barKnobW: Int
   @JvmStatic
   private TextFieldWidget search;
   private final val cache: MutableMap<String, jooon.gui.FishingMeleeScreen.TexInfo> = LinkedHashMap() as java.util.Map
   private final val gifOnly: MutableSet<String> = LinkedHashSet() as java.util.Set
   private final var blitSignatureLogged: Boolean
   private final var lastLoggedError: String = ""
   private final val hits: ArrayList<jooon.gui.FishingMeleeScreen.Hit> = ArrayList(64)
   private final val enabledMobsNorm: HashSet<String> = HashSet()

   public fun open() {
      MinecraftClient.method_1551().method_1507(this)
   }

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   fun getBufferSource(): Immediate {
      val var10000: MinecraftClient = this.getMc()
      val var1: Immediate = (var10000 as MinecraftAccessor).getRenderBuffers().method_23000()
      var1
   }

   private fun debug(msg: String) {
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      val mx: Double = event.comp_4798()
      val my: Double = event.comp_4799()
      val viewX: Int = MARGIN_L
      val viewY: Int = MARGIN_T
      val viewW: Int = this.field_22789 - MARGIN_L - 40
      val maxScroll: Float = RangesKt.coerceAtLeast((float)filtered.size() * (float)(CARD_W + CARD_GAP) - (float)CARD_GAP - (float)viewW, 0.0F)
      if (barDragging) {
         val var19: Int = RangesKt.coerceAtLeast(viewW - barKnobW, 1)
         scrollX = (float)(RangesKt.coerceIn((int)(mx - (double)barGrabOffset), viewX, viewX + var19) - viewX) / var19 * maxScroll
         true
      } else if (dragging) {
         scrollX = RangesKt.coerceIn(scrollStartX - (float)(mx - dragStartX), 0.0F, maxScroll)
         true
      } else {
         super.method_25403(event, dx, dy)
      }
   }

   fun method_25406(event: Click): Boolean {
      dragging = false
      barDragging = false
      super.method_25406(event)
   }

   fun method_25401(mx: Double, my: Double, horizontal: Double, vertical: Double): Boolean {
      val viewX: Int = MARGIN_L
      val viewY: Int = MARGIN_T
      val viewW: Int = this.field_22789 - MARGIN_L - 40
      val viewH: Int = VIEW_H
      val maxScroll: Float = RangesKt.coerceAtLeast((float)(filtered.size() * (CARD_W + CARD_GAP) - CARD_GAP - viewW), 0.0F)
      if (mx >= viewX && mx <= viewX + viewW && my >= viewY && my <= viewY + viewH) {
         val var10000: Window = MinecraftClient.method_1551().method_22683()
         val step: Float = if (InputUtil.method_15987(var10000, 340) || InputUtil.method_15987(var10000, 344)) 120.0F else 60.0F
         scrollX = RangesKt.coerceIn(scrollX + (float)(-vertical * (double)step + horizontal * (double)step), 0.0F, maxScroll)
         true
      } else {
         super.method_25401(mx, my, horizontal, vertical)
      }
   }

   private fun normName(s: String): String {
      val var10000: java.lang.String = s.toLowerCase(Locale.ROOT)
      return StringsKt.trim(var10000).toString()
   }

   private fun installOrUpdateMeleePredicateFromStore() {
      enabledMobsNorm.clear()

      for (n in allMobs) {
         if (FishingMeleeStore.INSTANCE.isEnabled(n)) {
            enabledMobsNorm.add(this.normName(n))
         }
      }

      FishingMeleeMobs.INSTANCE.installGuiEnableCheck({ nameNorm: java.lang.String ->
         Config.fishingMeleeAllow && (Config.fishingMeleeAllMobs || enabledMobsNorm.contains(nameNorm))
      })
   }

   fun method_25426() {
      FishingMeleeStore.INSTANCE.load()
      this.installOrUpdateMeleePredicateFromStore()
      search = TextFieldWidget(this.field_22793, MARGIN_L, 36, 220, 16, Text.method_43470("Search") as Text)
      val var1: FishingMeleeScreen = this

      try {
         var var5: FishingMeleeScreen = var1
         var var10000: TextFieldWidget = search
         if (search == null) {
            Intrinsics.throwUninitializedPropertyAccessException("search")
            var10000 = null
         }

         var10000.method_47404(Text.method_43470("Search...") as Text)
         var5 = (FishingMeleeScreen)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var4: java.lang.Throwable) {
         val `$this$init_u24lambda_u243`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var4))
      }

      var var7: TextFieldWidget = search
      if (search == null) {
         Intrinsics.throwUninitializedPropertyAccessException("search")
         var7 = null
      }

      var7.method_1852(query)
      var7 = search
      if (search == null) {
         Intrinsics.throwUninitializedPropertyAccessException("search")
         var7 = null
      }

      var7.method_1863({ it: java.lang.String ->
         query = StringsKt.trim(it).toString()
         val var10000: java.util.List
         if (query.length() == 0) {
            var10000 = allMobs
         } else {
            val `$this$filterTo$iv$iv`: java.lang.Iterable = allMobs
            val `destination$iv$iv`: java.util.Collection = ArrayList()

            for (`element$iv$iv` in `$this$filterTo$iv$iv`) {
               if (StringsKt.contains(`element$iv$iv` as java.lang.String, query, true)) {
                  `destination$iv$iv`.add(`element$iv$iv`)
               }
            }

            var10000 = `destination$iv$iv` as java.util.List
         }

         filtered = var10000
         INSTANCE.clampScroll()
      })
      var var10001: TextFieldWidget = search
      if (search == null) {
         Intrinsics.throwUninitializedPropertyAccessException("search")
         var10001 = null
      }

      this.method_37063(var10001 as Element)
      var10001 = search
      if (search == null) {
         Intrinsics.throwUninitializedPropertyAccessException("search")
         var10001 = null
      }

      this.method_25395(var10001 as Element)
      var7 = search
      if (search == null) {
         Intrinsics.throwUninitializedPropertyAccessException("search")
         var7 = null
      }

      var7.method_25365(true)
      this.method_37063(ButtonWidget.method_46430(Text.method_43470("Save & Close") as Text, { it: ButtonWidget ->
         FishingMeleeStore.INSTANCE.save()
         ConfigFlush.INSTANCE.flush()
         INSTANCE.installOrUpdateMeleePredicateFromStore()

         try {
            val configScreen: Screen = JooonConfigManager.INSTANCE.getScreen(null, "jooonreimagined")
            if (INSTANCE.field_22787 != null) {
               INSTANCE.field_22787.method_1507(configScreen)
            }
         } catch (var3: Exception) {
            var3.printStackTrace()
            INSTANCE.method_25419()
         }
      }).method_46433(this.field_22789 / 2 - 40, this.field_22790 - 28).method_46437(80, 20).method_46431() as Element)
      super.method_25426()
   }

   private fun normalizedBase(name: String): String {
      val var10000: java.lang.String = name.toLowerCase(Locale.ROOT)
      return Regex("\\s+")
         .replace(
            StringsKt.trim(
                  Regex("[^a-z0-9 ]")
                     .replace(StringsKt.replace$default(StringsKt.replace$default(var10000, "â€™", "", false, 4, null), "'", "", false, 4, null), " ")
               )
               .toString(),
            "_"
         )
      }

   private fun nameToCandidates(name: String): List<String> {
      val base: java.lang.String = this.normalizedBase(name)
      val noUnderscore: java.lang.String = StringsKt.replace$default(base, "_", "", false, 4, null)
      return CollectionsKt.listOf(
         arrayOf(
            "textures/gui/skyblock_entities_$base.png",
            "textures.gui/skyblock_entities_$base.png",
            "textures/gui/$base.png",
            "textures.gui/$base.png",
            "textures/gui/$noUnderscore.png",
            "textures.gui/$noUnderscore.png",
            "textures/gui/skyblock_entities_$base.gif",
            "textures/gui/$base.gif",
            "textures/gui/$noUnderscore.gif"
         )
      )
   }

   private fun loadTextureFor(name: String): jooon.gui.FishingMeleeScreen.TexInfo? {
      val candidates: FishingMeleeScreen.TexInfo = cache.get(name)
      if (candidates != null) {
         return candidates
      } else {
         val var22: java.util.List = this.nameToCandidates(name)
         this.debug("Checking candidates for: $name")

         for (c in var22) {
            val var10000: java.lang.String = c.toLowerCase(Locale.ROOT)
            val var25: Identifier = Identifier.method_60654("jooonreimagined:$var10000")
            val id: Identifier = var25
            val var26: Optional = this.getMc().method_1478().method_14486(var25)
            val res: Optional = var26
            if (var26.isPresent()) {
               try {
                  val e: Closeable = (res.get() as Resource).method_14482()
                  var var8: java.lang.Throwable = null

                  try {
                     val var27: NativeImage = NativeImage.method_4309(e as InputStream)
                     val w: Int = var27.method_4307()
                     val h: Int = var27.method_4323()
                     var27.close()
                     INSTANCE.debug("Image read: $wx$h")
                     val info: FishingMeleeScreen.TexInfo = FishingMeleeScreen.TexInfo(id, w, h, 0, 0, w, h)
                     cache.put(name, info)
                     INSTANCE.debug("Using resource texture: $id")
                     return info
                  } catch (var19: java.lang.Throwable) {
                     var8 = var19
                     throw var19
                  } finally {
                     CloseableKt.closeFinally(e, var8)
                  }
               } catch (var21: Exception) {
                  this.debug("Error processing $c: $var21")
               }
            }
         }

         return null
      }
   }

   fun method_25420(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      ctx.method_25294(0, 0, this.field_22789, this.field_22790, Integer.MIN_VALUE)
      hits.clear()
      var var10000: MutableText = Text.method_43470("Auto Fishing Melee Mobs").method_27692(Formatting.field_1078)
      val var21: MutableText = Text.method_43470("Enable mobs here to be auto-killed by Melee Mode when caught.").method_27692(Formatting.field_1080)
      val cx: Int = this.field_22789 / 2
      ctx.method_51439(this.field_22793, var10000 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var10000 as StringVisitable) / 2, 12, -1, false)
      ctx.method_51439(this.field_22793, var21 as Text, cx - this.field_22793.method_27525(var21 as StringVisitable) / 2, 24, -1, false)
      if (Config.fishingMeleeAllMobs) {
         var10000 = Text.method_43470("Attack All Mobs is ON - per-mob toggles ignored.").method_27692(Formatting.field_1065)
         ctx.method_51439(this.field_22793, var10000 as Text, cx - this.field_22793.method_27525(var10000 as StringVisitable) / 2, 38, -1, false)
      }

      val var18: Int = MARGIN_L
      val viewY: Int = MARGIN_T
      val viewW: Int = this.field_22789 - MARGIN_L - 40
      ctx.method_44379(MARGIN_L, MARGIN_T, MARGIN_L + (this.field_22789 - MARGIN_L - 40), MARGIN_T + VIEW_H)
      var x: Int = (int)(var18 - scrollX)
      val y: Int = viewY

      for (barY in filtered) {
         this.drawCard(ctx, x, y, barY)
         x += CARD_W + CARD_GAP
      }

      ctx.method_44380()
      val var19: Int = filtered.size() * (CARD_W + CARD_GAP) - CARD_GAP
      if (var19 > viewW) {
         val var20: Int = viewY + VIEW_H + 6
         ctx.method_25294(var18, viewY + VIEW_H + 6, var18 + viewW, viewY + VIEW_H + 6 + 2, 1090519039)
         barKnobW = Math.max((int)((float)viewW * ((float)viewW / (float)var19)), 24)
         val maxScroll: Float = RangesKt.coerceAtLeast((float)(var19 - viewW), 1.0F)
         val knobTravel: Int = RangesKt.coerceAtLeast(viewW - barKnobW, 1)
         barKnobX0 = var18 + RangesKt.coerceIn((int)((float)knobTravel * (scrollX / maxScroll)), 0, knobTravel)
         ctx.method_25294(barKnobX0, var20 - 1, barKnobX0 + barKnobW, var20 + 3, -1593835521)
      }

      super.method_25394(ctx, mouseX, mouseY, delta)
   }

   fun drawCard(ctx: DrawContext, x: Int, y: Int, name: java.lang.String) {
      val bw: Int = CARD_W
      val bh: Int = CARD_H
      if (x + CARD_W >= MARGIN_L - 8 && x <= MARGIN_L + (this.field_22789 - MARGIN_L - 40) + 8) {
         ctx.method_25294(x, y, x + CARD_W, y + CARD_H, 1275068416)
         ctx.method_73198(x, y, bw, bh, -2130706433)
         val var10000: MutableText = Text.method_43470(name).method_27692(Formatting.field_1075)
         ctx.method_51439(
            this.field_22793, var10000 as Text, x + bw / 2 - this.field_22793.method_27525(var10000 as StringVisitable) / 2, y + CARD_PAD, -1, false
         )
         val top: Int = y + CARD_PAD + 16 + 6
         val bottom: Int = y + bh - CARD_PAD - 24
         val left: Int = x + CARD_PAD
         val right: Int = x + bw - CARD_PAD
         val boxW: Int = RangesKt.coerceAtLeast(x + bw - CARD_PAD - left, 1)
         val boxH: Int = RangesKt.coerceAtLeast(bottom - top, 1)
         val info: FishingMeleeScreen.TexInfo = this.loadTextureFor(name)
         if (info != null) {
            val scale: Float = Math.min((float)boxW / (float)info.cropW, (float)boxH / (float)info.cropH)
            val dw: Int = RangesKt.coerceAtLeast((int)((float)info.cropW * scale), 1)
            val dh: Int = RangesKt.coerceAtLeast((int)((float)info.cropH * scale), 1)
            this.robustBlit(
               ctx, info.getId(), left + (boxW - dw) / 2, top + (boxH - dh) / 2, info.cropX, info.cropY, info.cropW, info.cropH, dw, dh, info.texW, info.texH
            )
         } else {
            ctx.method_25294(left, top, right, bottom, -12303292)
            val var28: MutableText = if (gifOnly.contains(name))
               Text.method_43470("gif not supported").method_27692(Formatting.field_1061)
               else
               Text.method_43470("image missing").method_27692(Formatting.field_1079)
               ctx.method_51439(
               this.field_22793, var28 as Text, x + bw / 2 - this.field_22793.method_27525(var28 as StringVisitable) / 2, y + bh / 2 - 4, -1, false
            )
         }

         this.drawToggle(ctx, x + bw / 2 - 50, y + bh - CARD_PAD - 20, 100, 20, name)
      }
   }

   fun drawToggle(ctx: DrawContext, x: Int, y: Int, w: Int, h: Int, name: java.lang.String) {
      val global: Boolean = Config.fishingMeleeAllMobs
      val on: Boolean = Config.fishingMeleeAllMobs || FishingMeleeStore.INSTANCE.isEnabled(name)
      val bg: Int = if (on) -2143224000 else -2130755520
      ctx.method_25294(x - 1, y - 1, x + w + 1, y + h + 1, if (Config.fishingMeleeAllMobs) 1627389951 else -16777216)
      ctx.method_25294(x, y, x + w, y + h, bg)
      val color: MutableText = if (global)
         Text.method_43470("All Mobs").method_27692(Formatting.field_1065)
         else
         (if (on) Text.method_43470("Enabled").method_27692(Formatting.field_1060) else Text.method_43470("Disabled").method_27692(Formatting.field_1061))
         ctx.method_51439(
         this.field_22793,
         color as Text,
         x + w / 2 - this.field_22793.method_27525(color as StringVisitable) / 2,
         y + (h - 8) / 2,
         if (global) -2639542 else -1,
         false
      )
      if (!global) {
         hits.add(FishingMeleeScreen.Hit(name, x, y, w, h))
      }
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (event.method_74245() == 0) {
         val ix: Int = (int)event.comp_4798()
         val iy: Int = (int)event.comp_4799()
         val mx: Double = event.comp_4798()
         val viewW: Int = this.field_22789 - MARGIN_L - 40
         if (iy <= MARGIN_T + VIEW_H + 6 + 3 && MARGIN_T + VIEW_H + 6 - 3 <= iy && ix >= barKnobX0 && ix <= barKnobX0 + barKnobW) {
            barDragging = true
            barGrabOffset = (float)(mx - barKnobX0)
            true
         }

         if (MARGIN_L <= ix && ix <= MARGIN_L + viewW && MARGIN_T <= iy && iy <= MARGIN_T + VIEW_H) {
            var var10000: FishingMeleeScreen.Hit = hits.iterator()
            val var19: java.util.Iterator = var10000

            while (var19.hasNext()) {
               var10000 = (FishingMeleeScreen.Hit)var19.next()
               val h: FishingMeleeScreen.Hit = var10000
               if (ix >= (var10000 as FishingMeleeScreen.Hit).x
                  && ix < (var10000 as FishingMeleeScreen.Hit).x + (var10000 as FishingMeleeScreen.Hit).w
                  && iy >= (var10000 as FishingMeleeScreen.Hit).y
                  && iy < (var10000 as FishingMeleeScreen.Hit).y + (var10000 as FishingMeleeScreen.Hit).h) {
                  if (!Config.fishingMeleeAllMobs) {
                     FishingMeleeStore.INSTANCE.toggle(h.name)
                     FishingMeleeStore.INSTANCE.save()
                     ConfigFlush.INSTANCE.flush()
                     if (FishingMeleeStore.INSTANCE.isEnabled(h.name)) {
                        enabledMobsNorm.add(this.normName(h.name))
                     } else {
                        enabledMobsNorm.remove(this.normName(h.name))
                     }

                     this.installOrUpdateMeleePredicateFromStore()
                     true
                  }

                  val var23: ClientPlayerEntity = this.getMc().field_1724
                  if (var23 != null) {
                     var23.method_7353(
                        Text.method_43470(
                           "${JooonReimagined.Companion.PREFIX_CLEAN}§cNote! You currently have \"Attack All Sea Creatures\" §aENABLED§c. If you only want Jooon to melee specific mobs, please disable this in the GUI."
                        ) as Text,
                        false
                     )
                  }

                  true
               }
            }

            dragging = true
            dragStartX = mx
            scrollStartX = scrollX
            true
         }
      }

      super.method_25402(event, handled)
   }

   private fun clampScroll() {
      val viewW: Int = this.field_22789 - MARGIN_L - 40
      scrollX = RangesKt.coerceIn(scrollX, 0.0F, (float)Math.max(0, filtered.size() * (CARD_W + CARD_GAP) - CARD_GAP - viewW))
   }

   fun method_25432() {
      for (`element$iv` in cache.values()) {
         val info: FishingMeleeScreen.TexInfo = `element$iv` as FishingMeleeScreen.TexInfo
         val var7: FishingMeleeScreen = INSTANCE

         try {
            var7.getMc().method_1531().method_4615(info.getId())
            val var12: Any = Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var10: java.lang.Throwable) {
            val `$this$removed_u24lambda_u2410_u24lambda_u249`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var10))
         }
      }

      cache.clear()
      gifOnly.clear()
      super.method_25432()
   }

   fun method_25421(): Boolean {
      false
   }

   private data class Hit(name: String, x: Int, y: Int, w: Int, h: Int) {
      public final val name: String
      public final val x: Int
      public final val y: Int
      public final val w: Int
      public final val h: Int

      init {
         this.name = name
         this.x = x
         this.y = y
         this.w = w
         this.h = h
      }

      public operator fun component1(): String {
         return this.name
      }

      public operator fun component2(): Int {
         return this.x
      }

      public operator fun component3(): Int {
         return this.y
      }

      public operator fun component4(): Int {
         return this.w
      }

      public operator fun component5(): Int {
         return this.h
      }

      public fun copy(name: String = this.name, x: Int = this.x, y: Int = this.y, w: Int = this.w, h: Int = this.h): jooon.gui.FishingMeleeScreen.Hit {
         return FishingMeleeScreen.Hit(name, x, y, w, h)
      }

      public override fun toString(): String {
         return "Hit(name=${this.name}, x=${this.x}, y=${this.y}, w=${this.w}, h=${this.h})"
      }

      public override fun hashCode(): Int {
         return (((this.name.hashCode() * 31 + Integer.hashCode(this.x)) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.w)) * 31
            + Integer.hashCode(this.h)
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is FishingMeleeScreen.Hit
               && this.name == (other as FishingMeleeScreen.Hit).name
               && this.x == (other as FishingMeleeScreen.Hit).x
               && this.y == (other as FishingMeleeScreen.Hit).y
               && this.w == (other as FishingMeleeScreen.Hit).w
               && this.h == (other as FishingMeleeScreen.Hit).h
            }
      }
   }

   private data class TexInfo {
      private Identifier id;
      public final val texW: Int
      public final val texH: Int
      public final val cropX: Int
      public final val cropY: Int
      public final val cropW: Int
      public final val cropH: Int

      fun TexInfo(id: Identifier, texW: Int, texH: Int, cropX: Int, cropY: Int, cropW: Int, cropH: Int) {
         this.id = id
         this.texW = texW
         this.texH = texH
         this.cropX = cropX
         this.cropY = cropY
         this.cropW = cropW
         this.cropH = cropH
      }

      fun getId(): Identifier {
         this.id
      }

      fun component1(): Identifier {
         this.id
      }

      public operator fun component2(): Int {
         return this.texW
      }

      public operator fun component3(): Int {
         return this.texH
      }

      public operator fun component4(): Int {
         return this.cropX
      }

      public operator fun component5(): Int {
         return this.cropY
      }

      public operator fun component6(): Int {
         return this.cropW
      }

      public operator fun component7(): Int {
         return this.cropH
      }

      fun copy(id: Identifier, texW: Int, texH: Int, cropX: Int, cropY: Int, cropW: Int, cropH: Int): FishingMeleeScreen.TexInfo {
         FishingMeleeScreen.TexInfo(id, texW, texH, cropX, cropY, cropW, cropH)
      }

      public override fun toString(): String {
         return "TexInfo(id=${this.id}, texW=${this.texW}, texH=${this.texH}, cropX=${this.cropX}, cropY=${this.cropY}, cropW=${this.cropW}, cropH=${this.cropH})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (((this.id.hashCode() * 31 + Integer.hashCode(this.texW)) * 31 + Integer.hashCode(this.texH)) * 31 + Integer.hashCode(this.cropX))
                                 * 31
                              + Integer.hashCode(this.cropY)
                        )
                        * 31
                     + Integer.hashCode(this.cropW)
               )
               * 31
            + Integer.hashCode(this.cropH)
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is FishingMeleeScreen.TexInfo
               && this.id == (other as FishingMeleeScreen.TexInfo).id
               && this.texW == (other as FishingMeleeScreen.TexInfo).texW
               && this.texH == (other as FishingMeleeScreen.TexInfo).texH
               && this.cropX == (other as FishingMeleeScreen.TexInfo).cropX
               && this.cropY == (other as FishingMeleeScreen.TexInfo).cropY
               && this.cropW == (other as FishingMeleeScreen.TexInfo).cropW
               && this.cropH == (other as FishingMeleeScreen.TexInfo).cropH
            }
      }
   }
}
