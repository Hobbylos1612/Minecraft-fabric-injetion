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
   private final val transition: JooonScreenTransition
   private final val overlayHits: MutableList<jooon.config.ui.OverlayManagerScreen.OverlayHit>
   private final var backButton: UiRect

   fun OverlayManagerScreen(parentScreen: Screen?) {
      super(Text.method_43470("Manage Overlays") as Text)
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
         val contentMouseY: Int = this.transition.transformedMouseY(mouseY)
         val palette: UiPalette = JooonUiThemeKt.currentPalette()
         val outer: UiRect = UiRect(32, 22, this.field_22789 - 64, this.field_22790 - 44)
         val header: UiRect = UiRect(outer.x + 16, outer.y + 16, outer.width - 32, 52)
         val content: UiRect = UiRect(outer.x + 36, header.bottom + 18, outer.width - 72, outer.height - 132)
         this.backButton = UiRect(outer.x + 16, outer.bottom - 40, 84, 24)
         context.method_25294(0, 0, this.field_22789, this.field_22790, palette.backdrop)
         this.transition.push(context)
         context.method_25294(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.method_25294(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.method_25294(content.x, content.y, content.right, content.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, content, palette.line)
         var var10000: MutableText = Text.method_43470("MANAGE OVERLAYS")
         context.method_51439(
            this.field_22793,
            var10000 as Text,
            header.x + (header.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
            header.y + (header.height - this.field_22793.field_2000) / 2,
            palette.text,
            false
         )
         val var22: java.util.List = this.field_22793
            .method_1728(
               Text.method_43470("Move overlays directly here, even if the related feature is currently disabled.") as StringVisitable, content.width - 36
            )
            var infoY: Int = content.y + 18

         for (var23 in var22) {
            context.method_51430(this.field_22793, var23 as OrderedText, content.x + 18, infoY, palette.mutedText, false)
            infoY += this.field_22793.field_2000 + 2
         }

         var var19: Int = infoY + 12

         for (overlay in this.overlayDefinitions()) {
            val row: UiRect = UiRect(content.x + 14, var19, content.width - 28, 44)
            val button: UiRect = UiRect(row.right - 90, row.y + 9, 74, 26)
            context.method_25294(row.x, row.y, row.right, row.bottom, palette.paper)
            JooonUiThemeKt.drawOutline(context, row, palette.lineSoft)
            context.method_51439(this.field_22793, Text.method_43470(overlay.displayName) as Text, row.x + 14, row.y + 15, palette.text, false)
            context.method_25294(button.x, button.y, button.right, button.bottom, palette.field)
            JooonUiThemeKt.drawOutline(context, button, palette.line)
            var10000 = Text.method_43470("Move")
            context.method_51439(
               this.field_22793,
               var10000 as Text,
               button.x + (button.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
               button.y + (button.height - this.field_22793.field_2000) / 2,
               palette.fieldText,
               false
            )
            this.overlayHits.add(OverlayManagerScreen.OverlayHit(overlay, button))
            var19 += 56
         }

         context.method_25294(this.backButton.x, this.backButton.y, this.backButton.right, this.backButton.bottom, palette.field)
         JooonUiThemeKt.drawOutline(context, this.backButton, palette.line)
         var10000 = Text.method_43470("Back")
         context.method_51439(
            this.field_22793,
            var10000 as Text,
            this.backButton.x + (this.backButton.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
            this.backButton.y + (this.backButton.height - this.field_22793.field_2000) / 2,
            palette.fieldText,
            false
         )
         super.method_25394(context, mouseX, mouseY, delta)
         this.transition.pop(context)
      }
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (this.transition.isClosing) {
         true
      } else if (event.method_74245() != 0) {
         super.method_25402(event, handled)
      } else {
         val mouseX: Int = (int)event.comp_4798()
         val mouseY: Int = this.transition.transformedMouseY((int)event.comp_4799())
         if (this.backButton.contains(mouseX, mouseY)) {
            this.transition.beginClose({ 
               if (`this$0`.field_22787 != null) {
                  `this$0`.field_22787.method_1507(`this$0`.parentScreen)
               }

               Unit.INSTANCE
            })
         } else {
            for (hit in this.overlayHits) {
               if (hit.button.contains(mouseX, mouseY)) {
                  this.openOverlayEditor(hit.overlay)
                  true
               }
            }

            super.method_25402(event, handled)
         }
      }
   }

   fun method_25419() {
      this.transition.beginClose({ 
         if (`this$0`.field_22787 != null) {
            `this$0`.field_22787.method_1507(`this$0`.parentScreen)
         }

         Unit.INSTANCE
      })
   }

   fun method_25421(): Boolean {
      false
   }

   private fun openOverlayEditor(overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition) {
      var var10000: Window
      run label21@{
         var10000 = MinecraftClient.method_1551().method_22683()
         val liveOverlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay(overlay.id)
         if (liveOverlay != null) {
            var10000 = liveOverlay.getPosition()
            if (var10000 != null) {
               return@label21
            }
         }

         var10000 = overlay.readAbsolute(var10000.method_4486(), var10000.method_4502()) as Pair
      }

      val x: Int = (var10000.component1() as java.lang.Number).intValue()
      val y: Int = (var10000.component2() as java.lang.Number).intValue()
      if (this.field_22787 != null) {
         this.field_22787.method_1507(MovableOverlayScreen(overlay.id, overlay.displayName, x, y, { newX: Int, newY: Int ->
            val liveOverlay: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay(`$overlay`.id)
            val var10000: Pair
            if (liveOverlay != null) {
               liveOverlay.setPositionSilently(newX, newY)
               var10000 = liveOverlay.getPosition()
            } else {
               var10000 = TuplesKt.to(newX, newY)
            }

            `$overlay`.writeAbsolute(`$window`.method_4486(), `$window`.method_4502(), var10000.getFirst(), var10000.getSecond())
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }, this))
      }
   }

   private fun overlayDefinitions(): List<jooon.config.ui.OverlayManagerScreen.OverlayDefinition> {
      return CollectionsKt.listOf(
         arrayOf(
            OverlayManagerScreen.OverlayDefinition("witherShieldOverlay", "Wither Shield", { width: Int, height: Int ->
               TuplesKt.to(width / 2 + PersistentState.witherShieldX, height / 2 + PersistentState.witherShieldY)
            }, { width: Int, height: Int, x: Int, y: Int ->
               PersistentState.witherShieldX = x - width / 2
               PersistentState.witherShieldY = y - height / 2
               PersistentState.witherShieldInitDone = true
               Unit.INSTANCE
            }),
            OverlayManagerScreen.OverlayDefinition("dbDisplay", "Dungeon Breaker", { width: Int, height: Int ->
               TuplesKt.to(width / 2 + PersistentState.dbDisplayX, height / 2 + PersistentState.dbDisplayY)
            }, { width: Int, height: Int, x: Int, y: Int ->
               PersistentState.dbDisplayX = x - width / 2
               PersistentState.dbDisplayY = y - height / 2
               PersistentState.dbDisplayInitDone = true
               Unit.INSTANCE
            }),
            OverlayManagerScreen.OverlayDefinition("slayerHP", "Slayer HP", { width: Int, height: Int ->
               TuplesKt.to(width / 2 + PersistentState.slayerHPDisplayX, height / 2 + PersistentState.slayerHPDisplayY)
            }, { width: Int, height: Int, x: Int, y: Int ->
               PersistentState.slayerHPDisplayX = x - width / 2
               PersistentState.slayerHPDisplayY = y - height / 2
               PersistentState.slayerHPDisplayInitDone = true
               Unit.INSTANCE
            }),
            OverlayManagerScreen.OverlayDefinition(
               "dungeonMap",
               "Dungeon Map",
               { width: Int, height: Int ->
                  TuplesKt.to(
                     MathKt.roundToInt((double)PersistentState.dungeonMapX / 10000.0 * (double)width),
                     MathKt.roundToInt((double)PersistentState.dungeonMapY / 10000.0 * (double)height)
                  )
               },
               { width: Int, height: Int, x: Int, y: Int ->
                  PersistentState.dungeonMapX = MathKt.roundToInt((double)x / (double)width * (double)10000)
                  PersistentState.dungeonMapY = MathKt.roundToInt((double)y / (double)height * (double)10000)
                  PersistentState.dungeonMapInitDone = true
                  Unit.INSTANCE
               }
            ),
            OverlayManagerScreen.OverlayDefinition("mayorDisplay", "Mayor Display", { var0: Int, var1: Int ->
               TuplesKt.to(PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.mayorDisplayX = x
               PersistentState.mayorDisplayY = y
               Unit.INSTANCE
            }),
            OverlayManagerScreen.OverlayDefinition("fishingHud", "Fishing HUD", { var0: Int, var1: Int ->
               TuplesKt.to(PersistentState.fishingHudX, PersistentState.fishingHudY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.fishingHudX = x
               PersistentState.fishingHudY = y
               PersistentState.fishingHudInitDone = true
               Unit.INSTANCE
            }),
            OverlayManagerScreen.OverlayDefinition("stridersurferFishingMacroHud", "Stridersurfer Fishing Macro HUD", { var0: Int, var1: Int ->
               TuplesKt.to(PersistentState.stridersurferFishingMacroHudX, PersistentState.stridersurferFishingMacroHudY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.stridersurferFishingMacroHudX = x
               PersistentState.stridersurferFishingMacroHudY = y
               PersistentState.stridersurferFishingMacroHudInitDone = true
               Unit.INSTANCE
            }),
            OverlayManagerScreen.OverlayDefinition("dojoHud", "Dojo HUD", { var0: Int, var1: Int ->
               TuplesKt.to(PersistentState.dojoHudX, PersistentState.dojoHudY)
            }, { var0: Int, var1: Int, x: Int, y: Int ->
               PersistentState.dojoHudX = x
               PersistentState.dojoHudY = y
               PersistentState.dojoHudInitDone = true
               Unit.INSTANCE
            })
         )
      )
   }

   private data class OverlayDefinition(id: String,
      displayName: String,
      readAbsolute: (Int, Int) -> Pair<Int, Int>,
      writeAbsolute: (Int, Int, Int, Int) -> Unit
   ) {
      public final val id: String
      public final val displayName: String
      public final val readAbsolute: (Int, Int) -> Pair<Int, Int>
      public final val writeAbsolute: (Int, Int, Int, Int) -> Unit

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

      public fun copy(
         id: String = this.id,
         displayName: String = this.displayName,
         readAbsolute: (Int, Int) -> Pair<Int, Int> = this.readAbsolute,
         writeAbsolute: (Int, Int, Int, Int) -> Unit = this.writeAbsolute
      ): jooon.config.ui.OverlayManagerScreen.OverlayDefinition {
         return OverlayManagerScreen.OverlayDefinition(id, displayName, readAbsolute, writeAbsolute)
      }

      public override fun toString(): String {
         return "OverlayDefinition(id=${this.id}, displayName=${this.displayName}, readAbsolute=${this.readAbsolute}, writeAbsolute=${this.writeAbsolute})"
      }

      public override fun hashCode(): Int {
         return ((this.id.hashCode() * 31 + this.displayName.hashCode()) * 31 + this.readAbsolute.hashCode()) * 31 + this.writeAbsolute.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition
      public final val button: UiRect

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

      public fun copy(overlay: jooon.config.ui.OverlayManagerScreen.OverlayDefinition = this.overlay, button: UiRect = this.button): jooon.config.ui.OverlayManagerScreen.OverlayHit {
         return OverlayManagerScreen.OverlayHit(overlay, button)
      }

      public override fun toString(): String {
         return "OverlayHit(overlay=${this.overlay}, button=${this.button})"
      }

      public override fun hashCode(): Int {
         return this.overlay.hashCode() * 31 + this.button.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
