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

object FishingMeleeScreen : Screen(Text.literal("Auto Fishing Melee Mobs") as Text) {
   private val CARD_W: Int = 300
   private val CARD_H: Int = 360
   private val CARD_PAD: Int = 10
   private val CARD_GAP: Int = 16
   private val MARGIN_L: Int = 40
   private val MARGIN_T: Int = 70
   private val VIEW_H: Int = CARD_H + 2
   private var barGrabOffset: Float
   private val allMobs: List<String> =
      listOf(
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
      private var query: String = ""
   private var filtered: List<String> = allMobs
   private var scrollX: Float
   private var dragging: Boolean
   private var dragStartX: Double
   private var scrollStartX: Float
   private var barDragging: Boolean
   private var barKnobX0: Int
   private var barKnobW: Int
   
   private TextFieldWidget search;
   private val cache: MutableMap<String, jooon.gui.FishingMeleeScreen.TexInfo> = LinkedHashMap() as java.util.Map
   private val gifOnly: MutableSet<String> = LinkedHashSet() as java.util.Set
   private var blitSignatureLogged: Boolean
   private var lastLoggedError: String = ""
   private val hits: ArrayList<jooon.gui.FishingMeleeScreen.Hit> = ArrayList(64)
   private val enabledMobsNorm: HashSet<String> = HashSet()

   fun open() {
      MinecraftClient.getInstance().setScreen(this)
   }

   fun getMc(): MinecraftClient {
return var10000
   }

   fun getBufferSource(): Immediate {
return var1
   }

   private fun debug(msg: String) {
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {






      if (barDragging) {

         scrollX = (((mx - barGrabOffset.toDouble()).toInt()).coerceIn(viewX, viewX + var19) - viewX).toFloat() / var19 * maxScroll
return true
      } else if (dragging) {
         scrollX = (scrollStartX - (mx - dragStartX).toFloat()).coerceIn(0.0F, maxScroll)
return true
      } else {
         super.mouseDragged(event, dx, dy)
      }
   }

   fun method_25406(event: Click): Boolean {
      dragging = false
      barDragging = false
      super.mouseReleased(event)
   }

   fun method_25401(mx: Double, my: Double, horizontal: Double, vertical: Double): Boolean {





      if (mx >= viewX && mx <= viewX + viewW && my >= viewY && my <= viewY + viewH) {


         scrollX = (scrollX + (-vertical * step.toDouble() + horizontal * step.toDouble()).toFloat()).coerceIn(0.0F, maxScroll)
return true
      } else {
         super.mouseScrolled(mx, my, horizontal, vertical)
      }
   }

   private fun normName(s: String): String {

      return trim(var10000).toString()
   }

   private fun installOrUpdateMeleePredicateFromStore() {
      enabledMobsNorm.clear()

      for (n in allMobs) {
         if (FishingMeleeStore.isEnabled(n)) {
            enabledMobsNorm.add(this.normName(n))
         }
      }

      FishingMeleeMobs.installGuiEnableCheck({ nameNorm: String ->
         Config.fishingMeleeAllow && (Config.fishingMeleeAllMobs || enabledMobsNorm.contains(nameNorm))
      })
   }

   fun method_25426() {
      FishingMeleeStore.load()
      this.installOrUpdateMeleePredicateFromStore()
      search = TextFieldWidget(this.textRenderer, MARGIN_L, 36, 220, 16, Text.literal("Search") as Text)


      try {
         var var5: FishingMeleeScreen = var1
         var var10000: TextFieldWidget = search
         if (search == null) {
            throwUninitializedPropertyAccessException("search")
            var10000 = null
         }

         var10000.setPlaceholder(Text.literal("Search...") as Text)
         var5 = Result(Unit)
      } catch (var4: java.lang.Throwable) {
         val `this24lambda_u243`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var4))
      }

      var var7: TextFieldWidget = search
      if (search == null) {
         throwUninitializedPropertyAccessException("search")
         var7 = null
      }

      var7.setText(query)
      var7 = search
      if (search == null) {
         throwUninitializedPropertyAccessException("search")
         var7 = null
      }

      var7.setChangedListener({ it: String ->
         query = trim(it).toString()
         val var10000: java.util.List
         if (query.length() == 0) {
            var10000 = allMobs
         } else {
            val `this$iv$iv`: java.lang.Iterable = allMobs
            val `destination$iv$iv`: java.util.Collection = ArrayList()

            for (`element$iv$iv` in `this$iv$iv`) {
               if (contains(`element$iv$iv` as String, query, true)) {
                  `destination$iv$iv`.add(`element$iv$iv`)
               }
            }

            var10000 = `destination$iv$iv` as java.util.List
         }

         filtered = var10000
         clampScroll()
      })
      var var10001: TextFieldWidget = search
      if (search == null) {
         throwUninitializedPropertyAccessException("search")
         var10001 = null
      }

      this.addDrawableChild(var10001 as Element)
      var10001 = search
      if (search == null) {
         throwUninitializedPropertyAccessException("search")
         var10001 = null
      }

      this.setFocused(var10001 as Element)
      var7 = search
      if (search == null) {
         throwUninitializedPropertyAccessException("search")
         var7 = null
      }

      var7.setFocused(true)
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close") as Text, { it: ButtonWidget ->
         FishingMeleeStore.save()
         ConfigFlush.flush()
         installOrUpdateMeleePredicateFromStore()

         try {

            if (field_22787 != null) {
               field_22787.setScreen(configScreen)
            }
         } catch (var3: Exception) {
            var3.printStackTrace()
            method_25419()
         }
      }).position(this.width / 2 - 40, this.height - 28).size(80, 20).build() as Element)
      super.init()
   }

   private fun normalizedBase(name: String): String {

      return Regex("\\s+")
         .replace(
            trim(
                  Regex("[^a-z0-9 ]")
                     .replace(replace$default(var10000.replace("â€™", ""), "'", "", false, 4, null), " ")
               )
               .toString(),
            "_"
         )
      }

   private fun nameToCandidates(name: String): List<String> {


      return listOf(
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





            if (var26.isPresent()) {
               try {

                  var var8: java.lang.Throwable = null

                  try {



                     var27.close()
                     debug("Image read: $wx$h")
                     val info: FishingMeleeScreen.TexInfo = FishingMeleeScreen.TexInfo(id, w, h, 0, 0, w, h)
                     cache.put(name, info)
                     debug("Using resource texture: $id")
                     return info
                  } catch (var19: java.lang.Throwable) {
                     var8 = var19
                     throw var19
                  } finally {
                     e.close()
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
      ctx.fill(0, 0, this.width, this.height, Integer.MIN_VALUE)
      hits.clear()
      var var10000: MutableText = Text.literal("Auto Fishing Melee Mobs").formatted(Formatting.BLUE)


      ctx.drawText(this.textRenderer, var10000 as Text, this.width / 2 - this.textRenderer.getWidth(var10000 as StringVisitable) / 2, 12, -1, false)
      ctx.drawText(this.textRenderer, var21 as Text, cx - this.textRenderer.getWidth(var21 as StringVisitable) / 2, 24, -1, false)
      if (Config.fishingMeleeAllMobs) {
         var10000 = Text.literal("Attack All Mobs is ON - per-mob toggles ignored.").formatted(Formatting.GOLD)
         ctx.drawText(this.textRenderer, var10000 as Text, cx - this.textRenderer.getWidth(var10000 as StringVisitable) / 2, 38, -1, false)
      }



      ctx.enableScissor(MARGIN_L, MARGIN_T, MARGIN_L + (this.width - MARGIN_L - 40), MARGIN_T + VIEW_H)
      var x: Int = (var18 - scrollX).toInt()


      for (barY in filtered) {
         this.drawCard(ctx, x, y, barY)
         x += CARD_W + CARD_GAP
      }

      ctx.disableScissor()

      if (var19 > viewW) {

         ctx.fill(var18, viewY + VIEW_H + 6, var18 + viewW, viewY + VIEW_H + 6 + 2, 1090519039)
         barKnobW = Math.max((viewW.toFloat() * (viewW.toFloat() / var19.toFloat())).toInt(), 24)


         barKnobX0 = var18 + ((knobTravel.toFloat() * (scrollX / maxScroll)).toInt()).coerceIn(0, knobTravel)
         ctx.fill(barKnobX0, var20 - 1, barKnobX0 + barKnobW, var20 + 3, -1593835521)
      }

      super.render(ctx, mouseX, mouseY, delta)
   }

   fun drawCard(ctx: DrawContext, x: Int, y: Int, name: String) {


      if (x + CARD_W >= MARGIN_L - 8 && x <= MARGIN_L + (this.width - MARGIN_L - 40) + 8) {
         ctx.fill(x, y, x + CARD_W, y + CARD_H, 1275068416)
         ctx.drawStrokedRectangle(x, y, bw, bh, -2130706433)

         ctx.drawText(
            this.textRenderer, var10000 as Text, x + bw / 2 - this.textRenderer.getWidth(var10000 as StringVisitable) / 2, y + CARD_PAD, -1, false
         )






         val info: FishingMeleeScreen.TexInfo = this.loadTextureFor(name)
         if (info != null) {



            this.robustBlit(
               ctx, info.getId(), left + (boxW - dw) / 2, top + (boxH - dh) / 2, info.cropX, info.cropY, info.cropW, info.cropH, dw, dh, info.texW, info.texH
            )
         } else {
            ctx.fill(left, top, right, bottom, -12303292)

               Text.literal("gif not supported").formatted(Formatting.RED)
return else
               Text.literal("image missing").formatted(Formatting.DARK_RED)
               ctx.drawText(
               this.textRenderer, var28 as Text, x + bw / 2 - this.textRenderer.getWidth(var28 as StringVisitable) / 2, y + bh / 2 - 4, -1, false
            )
         }

         this.drawToggle(ctx, x + bw / 2 - 50, y + bh - CARD_PAD - 20, 100, 20, name)
      }
   }

   fun drawToggle(ctx: DrawContext, x: Int, y: Int, w: Int, h: Int, name: String) {



      ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, if (Config.fishingMeleeAllMobs) 1627389951 else -16777216)
      ctx.fill(x, y, x + w, y + h, bg)

         Text.literal("All Mobs").formatted(Formatting.GOLD)
return else
         (if (on) Text.literal("Enabled").formatted(Formatting.GREEN) else Text.literal("Disabled").formatted(Formatting.RED))
         ctx.drawText(
         this.textRenderer,
         color as Text,
         x + w / 2 - this.textRenderer.getWidth(color as StringVisitable) / 2,
         y + (h - 8) / 2,
         if (global) -2639542 else -1,
return false
      )
      if (!global) {
         hits.add(FishingMeleeScreen.Hit(name, x, y, w, h))
      }
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (event.button() == 0) {




         if (iy <= MARGIN_T + VIEW_H + 6 + 3 && MARGIN_T + VIEW_H + 6 - 3 <= iy && ix >= barKnobX0 && ix <= barKnobX0 + barKnobW) {
            barDragging = true
            barGrabOffset = (mx - barKnobX0).toFloat()
return true
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
                     FishingMeleeStore.toggle(h.name)
                     FishingMeleeStore.save()
                     ConfigFlush.flush()
                     if (FishingMeleeStore.isEnabled(h.name)) {
                        enabledMobsNorm.add(this.normName(h.name))
                     } else {
                        enabledMobsNorm.remove(this.normName(h.name))
                     }

                     this.installOrUpdateMeleePredicateFromStore()
return true
                  }

                  if (var23 != null) {
                     var23.sendMessage(
                        Text.literal(
                           "${JooonReimagined.Companion.PREFIX_CLEAN}§cNote! You currently have \"Attack All Sea Creatures\" §aENABLED§c. If you only want Jooon to melee specific mobs, please disable this in the GUI."
                        ) as Text,
return false
                     )
                  }
return true
               }
            }

            dragging = true
            dragStartX = mx
            scrollStartX = scrollX
return true
         }
      }

      super.mouseClicked(event, handled)
   }

   private fun clampScroll() {

      scrollX = (scrollX).coerceIn(0.0F, Math.max(0, filtered.size() * (CARD_W + CARD_GAP) - CARD_GAP - viewW).toFloat())
   }

   fun method_25432() {
      for (`element$iv` in cache.values()) {
         val info: FishingMeleeScreen.TexInfo = `element$iv` as FishingMeleeScreen.TexInfo


         try {
            var7.getMc().getTextureManager().destroyTexture(info.getId())

         } catch (var10: java.lang.Throwable) {
            val `this24lambda_u2410_u24lambda_u249`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var10))
         }
      }

      cache.clear()
      gifOnly.clear()
      super.removed()
   }

   fun method_25421(): Boolean {
return false
   }

   private data class Hit(name: String, x: Int, y: Int, w: Int, h: Int) {
      val name: String
      val x: Int
      val y: Int
      val w: Int
      val h: Int

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

      fun copy(name: String = this.name, x: Int = this.x, y: Int = this.y, w: Int = this.w, h: Int = this.h): jooon.gui.FishingMeleeScreen.Hit {
         return FishingMeleeScreen.Hit(name, x, y, w, h)
      }

      override fun toString(): String {
         return "Hit(name=${this.name}, x=${this.x}, y=${this.y}, w=${this.w}, h=${this.h})"
      }

      override fun hashCode(): Int {
         return (((this.name.hashCode() * 31 + Integer.hashCode(this.x)) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.w)) * 31
            + Integer.hashCode(this.h)
         }

      override operator fun equals(other: Any?): Boolean {
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
      val texW: Int
      val texH: Int
      val cropX: Int
      val cropY: Int
      val cropW: Int
      val cropH: Int

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

      override fun toString(): String {
         return "TexInfo(id=${this.id}, texW=${this.texW}, texH=${this.texH}, cropX=${this.cropX}, cropY=${this.cropY}, cropW=${this.cropW}, cropH=${this.cropH})"
      }

      override fun hashCode(): Int {
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

      override operator fun equals(other: Any?): Boolean {
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
