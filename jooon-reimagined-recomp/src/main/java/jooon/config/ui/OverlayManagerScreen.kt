package jooon.config.ui

import java.util.ArrayList
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.gui.MovableOverlayScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import kotlin.math.MathKt
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.Window
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

internal class OverlayManagerScreen : Screen {
   private Screen parentScreen;
   private val transition: JooonScreenTransition
   private val overlayHits: MutableList<jooon.config.ui.OverlayManagerScreen.OverlayHit>
   private var backButton: UiRect

   fun OverlayManagerScreen(parentScreen: Screen?) {
      super(Text.literal("Manage Overlays") as Text)
      this.parentScreen = parentScreen
      this.transition = JooonScreenTransition(0L, 0L, 0.0F, 7, null)
      this.overlayHits = ArrayList<>()
      this.backButton = UiRect(0, 0, 0, 0)
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      if (!this.transition.finishCloseIfReady()) {
         this.overlayHits.clear()





         this.backButton = UiRect(outer.x + 16, outer.bottom - 40, 84, 24)
         context.fill(0, 0, this.width, this.height, palette.backdrop)
         this.transition.push(context)
         context.fill(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.fill(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.fill(content.x, content.y, content.right, content.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, content, palette.line)
         var var10000: MutableText = Text.literal("MANAGE OVERLAYS")
         context.drawText(
            this.textRenderer,
            var10000 as Text,
            header.x + (header.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
            header.y + (header.height - this.textRenderer.fontHeight) / 2,
            palette.text,
return false
         )
         val var22: java.util.List = this.textRenderer
            .wrapLines(
               Text.literal("Move overlays directly here, even if the related feature is currently disabled.") as StringVisitable, content.width - 36
            )
            var infoY: Int = content.y + 18

         for (var23 in var22) {
            context.drawText(this.textRenderer, var23 as OrderedText, content.x + 18, infoY, palette.mutedText, false)
            infoY += this.textRenderer.fontHeight + 2
         }

         var var19: Int = infoY + 12

         for (overlay in this.overlayDefinitions()) {


            context.fill(row.x, row.y, row.right, row.bottom, palette.paper)
            JooonUiThemeKt.drawOutline(context, row, palette.lineSoft)
            context.drawText(this.textRenderer, Text.literal(overlay.displayName) as Text, row.x + 14, row.y + 15, palette.text, false)
            context.fill(button.x, button.y, button.right, button.bottom, palette.field)
            JooonUiThemeKt.drawOutline(context, button, palette.line)
            var10000 = Text.literal("Move")
            context.drawText(
               this.textRenderer,
               var10000 as Text,
               button.x + (button.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
               button.y + (button.height - this.textRenderer.fontHeight) / 2,
               palette.fieldText,
return false
            )
            this.overlayHits.add(OverlayManagerScreen.OverlayHit(overlay, button))
            var19 += 56
         }

         context.fill(this.backButton.x, this.backButton.y, this.backButton.right, this.backButton.bottom, palette.field)
         JooonUiThemeKt.drawOutline(context, this.backButton, palette.line)
         var10000 = Text.literal("Back")
         context.drawText(
            this.textRenderer,
            var10000 as Text,
            this.backButton.x + (this.backButton.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
            this.backButton.y + (this.backButton.height - this.textRenderer.fontHeight) / 2,
            palette.fieldText,
return false
         )
         super.render(context, mouseX, mouseY, delta)
         this.transition.pop(context)
      }
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (this.transition.isClosing) {
return true
      } else if (event.button() != 0) {
         super.mouseClicked(event, handled)
      } else {


         if (this.backButton.contains(mouseX, mouseY)) {
            this.transition.beginClose({ 
               if (`this$0`.client != null) {
                  `this$0`.client.setScreen(`this$0`.parentScreen)
               }
return Unit
            })
         } else {
            for (hit in this.overlayHits) {
               if (hit.button.contains(mouseX, mouseY)) {
                  this.openOverlayEditor(hit.overlay)
return true
               }
            }

            super.mouseClicked(event, handled)
         }
      }
   }

   fun method_25419() {
      this.transition.beginClose({ 
         if (`this$0`.client != null) {
            `this$0`.client.setScreen(`this$0`.parentScreen)
         }
return Unit
      })
   }

   fun method_25421(): Boolean {
return false
   }

   private fun openOverlayEditor(overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition) {
      var var10000: Window
      run label21@{
         var10000 = MinecraftClient.getInstance().getWindow()

         if (liveOverlay != null) {
            var10000 = liveOverlay.getPosition()
            if (var10000 != null) {
               return@label21
            }
         }

         var10000 = overlay.readAbsolute(var10000.getScaledWidth(), var10000.getScaledHeight()) as Pair
      }


      if (this.client != null) {
         this.client.setScreen(MovableOverlayScreen(overlay.id, overlay.displayName, x, y, { newX: Int, newY: Int ->

            val var10000: Pair
            if (liveOverlay != null) {
               liveOverlay.setPositionSilently(newX, newY)
               var10000 = liveOverlay.getPosition()
            } else {
               var10000 = Pair(newX, newY)
            }

            `$overlay`.writeAbsolute(`$window`.getScaledWidth(), `$window`.getScaledHeight(), var10000.getFirst(), var10000.getSecond())
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }, this))
      }
   }

   private fun overlayDefinitions(): List<jooon.config.ui.OverlayManagerScreen.OverlayDefinition> {
      return listOf(
         arrayOf(
            OverlayManagerScreen.OverlayDefinition("witherShieldOverlay", "Wither Shield", { width: Int, height: Int ->
               Pair(width / 2 + PersistentState.witherShieldX, height / 2 + PersistentState.witherShieldY)
            }, { width: Int, height: Int, x: Int, y: Int ->
               PersistentState.witherShieldX = x - width / 2
               PersistentState.witherShieldY = y - height / 2
               PersistentState.witherShieldInitDone = true
return Unit
            }),
            OverlayManagerScreen.OverlayDefinition("dbDisplay", "Dungeon Breaker", { width: Int, height: Int ->
               Pair(width / 2 + PersistentState.dbDisplayX, height / 2 + PersistentState.dbDisplayY)
            }, { width: Int, height: Int, x: Int, y: Int ->
               PersistentState.dbDisplayX = x - width / 2
               PersistentState.dbDisplayY = y - height / 2
               PersistentState.dbDisplayInitDone = true
return Unit
            }),
            OverlayManagerScreen.OverlayDefinition("slayerHP", "Slayer HP", { width: Int, height: Int ->
               Pair(width / 2 + PersistentState.slayerHPDisplayX, height / 2 + PersistentState.slayerHPDisplayY)
            }, { width: Int, height: Int, x: Int, y: Int ->
               PersistentState.slayerHPDisplayX = x - width / 2
               PersistentState.slayerHPDisplayY = y - height / 2
               PersistentState.slayerHPDisplayInitDone = true
return Unit
            }),
            OverlayManagerScreen.OverlayDefinition(
               "dungeonMap",
               "Dungeon Map",
               { width: Int, height: Int ->
                  Pair((PersistentState.dungeonMapX.toDouble() / 10000.0 * width.toDouble()).roundToInt(), (PersistentState.dungeonMapY.toDouble() / 10000.0 * height.toDouble()).roundToInt())
               },
               { width: Int, height: Int, x: Int, y: Int ->
                  PersistentState.dungeonMapX = (x.toDouble() / width.toDouble() * 10000.toDouble()).roundToInt()
                  PersistentState.dungeonMapY = (y.toDouble() / height.toDouble() * 10000.toDouble()).roundToInt()
                  PersistentState.dungeonMapInitDone = true
return Unit
               }
            ),
            OverlayManagerScreen.OverlayDefinition("mayorDisplay", "Mayor Display", { var0: Int, var1: Int ->
               Pair(PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.mayorDisplayX = x
               PersistentState.mayorDisplayY = y
return Unit
            }),
            OverlayManagerScreen.OverlayDefinition("fishingHud", "Fishing HUD", { var0: Int, var1: Int ->
               Pair(PersistentState.fishingHudX, PersistentState.fishingHudY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.fishingHudX = x
               PersistentState.fishingHudY = y
               PersistentState.fishingHudInitDone = true
return Unit
            }),
            OverlayManagerScreen.OverlayDefinition("stridersurferFishingMacroHud", "Stridersurfer Fishing Macro HUD", { var0: Int, var1: Int ->
               Pair(PersistentState.stridersurferFishingMacroHudX, PersistentState.stridersurferFishingMacroHudY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.stridersurferFishingMacroHudX = x
               PersistentState.stridersurferFishingMacroHudY = y
               PersistentState.stridersurferFishingMacroHudInitDone = true
return Unit
            }),
            OverlayManagerScreen.OverlayDefinition("dojoHud", "Dojo HUD", { var0: Int, var1: Int ->
               Pair(PersistentState.dojoHudX, PersistentState.dojoHudY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.dojoHudX = x
               PersistentState.dojoHudY = y
               PersistentState.dojoHudInitDone = true
return Unit
            })
         )
      )
   }

   private data class OverlayDefinition(id: String,
      displayName: String,
      readAbsolute: (Int, Int) -> Pair<Int, Int>,
      writeAbsolute: (Int, Int, Int, Int) -> Unit
   ) {
      val id: String
      val displayName: String
      val readAbsolute: (Int, Int) -> Pair<Int, Int>
      val writeAbsolute: (Int, Int, Int, Int) -> Unit

      init {
         this.id = id
         this.displayName = displayName
         this.readAbsolute = readAbsolute
         this.writeAbsolute = writeAbsolute
      }

      public operator fun component1(): String {
         return this.id
      }

      public operator fun component2(): String {
         return this.displayName
      }

      public operator fun component3(): (Int, Int) -> Pair<Int, Int> {
         return this.readAbsolute
      }

      public operator fun component4(): (Int, Int, Int, Int) -> Unit {
         return this.writeAbsolute
      }

      fun copy(
         id: String = this.id,
         displayName: String = this.displayName,
         readAbsolute: (Int, Int) -> Pair<Int, Int> = this.readAbsolute,
         writeAbsolute: (Int, Int, Int, Int) -> Unit = this.writeAbsolute
      ): jooon.config.ui.OverlayManagerScreen.OverlayDefinition {
         return OverlayManagerScreen.OverlayDefinition(id, displayName, readAbsolute, writeAbsolute)
      }

      override fun toString(): String {
         return "OverlayDefinition(id=${this.id}, displayName=${this.displayName}, readAbsolute=${this.readAbsolute}, writeAbsolute=${this.writeAbsolute})"
      }

      override fun hashCode(): Int {
         return ((this.id.hashCode() * 31 + this.displayName.hashCode()) * 31 + this.readAbsolute.hashCode()) * 31 + this.writeAbsolute.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is OverlayManagerScreen.OverlayDefinition
               && this.id == (other as OverlayManagerScreen.OverlayDefinition).id
               && this.displayName == (other as OverlayManagerScreen.OverlayDefinition).displayName
               && this.readAbsolute == (other as OverlayManagerScreen.OverlayDefinition).readAbsolute
               && this.writeAbsolute == (other as OverlayManagerScreen.OverlayDefinition).writeAbsolute
            }
      }
   }

   private data class OverlayHit(overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition, button: UiRect) {
      val overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition
      val button: UiRect

      init {
         this.overlay = overlay
         this.button = button
      }

      public operator fun component1(): jooon.config.ui.OverlayManagerScreen.OverlayDefinition {
         return this.overlay
      }

      public operator fun component2(): UiRect {
         return this.button
      }

      fun copy(overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition = this.overlay, button: UiRect = this.button): jooon.config.ui.OverlayManagerScreen.OverlayHit {
         return OverlayManagerScreen.OverlayHit(overlay, button)
      }

      override fun toString(): String {
         return "OverlayHit(overlay=${this.overlay}, button=${this.button})"
      }

      override fun hashCode(): Int {
         return this.overlay.hashCode() * 31 + this.button.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is OverlayManagerScreen.OverlayHit
               && this.overlay == (other as OverlayManagerScreen.OverlayHit).overlay
               && this.button == (other as OverlayManagerScreen.OverlayHit).button
            }
      }
   }
}
