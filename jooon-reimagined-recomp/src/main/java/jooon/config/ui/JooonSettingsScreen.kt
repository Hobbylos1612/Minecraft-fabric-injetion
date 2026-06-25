package jooon.config.ui

import java.util.ArrayList
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

internal class JooonSettingsScreen : Screen {
   private Screen parentScreen;
   private val transition: JooonScreenTransition
   private val themeHits: MutableList<jooon.config.ui.JooonSettingsScreen.ThemeHit>
   private var backButton: UiRect
   private var manageOverlaysButton: UiRect

   fun JooonSettingsScreen(parentScreen: Screen?) {
      super(Text.literal("JR Settings") as Text)
      this.parentScreen = parentScreen
      this.transition = JooonScreenTransition(0L, 0L, 0.0F, 7, null)
      this.themeHits = ArrayList<>()
      this.backButton = UiRect(0, 0, 0, 0)
      this.manageOverlaysButton = UiRect(0, 0, 0, 0)
   }

   fun method_25426() {
      JooonUiSettings.ensureLoaded()
      super.init()
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      if (!this.transition.finishCloseIfReady()) {
         this.themeHits.clear()





         this.backButton = UiRect(outer.x + 16, outer.bottom - 40, 84, 24)
         context.fill(0, 0, this.width, this.height, palette.backdrop)
         this.transition.push(context)
         context.fill(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.fill(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.fill(content.x, content.y, content.right, content.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, content, palette.line)
         var var10000: MutableText = Text.literal("JR SETTINGS")
         context.drawText(
            this.textRenderer,
            var10000 as Text,
            header.x + (header.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
            header.y + (header.height - this.textRenderer.fontHeight) / 2,
            palette.text,
return false
         )
         var10000 = Text.literal("General Settings")
         context.drawText(this.textRenderer, var10000 as Text, content.x + 18, content.y + 18, palette.text, false)
         var10000 = Text.literal("Theme changes apply immediately.")
         context.drawText(this.textRenderer, var10000 as Text, content.x + 18, content.y + 36, palette.mutedText, false)
         var10000 = Text.literal("Theme")
         context.drawText(this.textRenderer, var10000 as Text, content.x + 18, content.y + 78, palette.text, false)



         var buttonX: Int = content.x + 18

         for (descriptionLines in JooonTheme.getEntries()) {





            context.fill(lineY.x, lineY.y, lineY.right, lineY.bottom, backText)
            JooonUiThemeKt.drawOutline(context, lineY, borderColor)
            if (overlaysTitle) {
               context.fill(lineY.x + 1, lineY.bottom - 4, lineY.right - 1, lineY.bottom - 1, palette.toggleOn)
               context.fill(lineY.x + 8, lineY.y + 8, lineY.x + 14, lineY.y + 14, palette.toggleOn)
            }

            var10000 = Text.literal(descriptionLines.displayName)
            context.drawText(
               this.textRenderer,
               var10000 as Text,
               lineY.x + (lineY.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
               lineY.y + (lineY.height - this.textRenderer.fontHeight) / 2,
               if (overlaysTitle) palette.text else (if (manageText) palette.text else palette.fieldText),
return false
            )
            this.themeHits.add(JooonSettingsScreen.ThemeHit(descriptionLines, lineY))
            buttonX += buttonWidth + buttonGap
         }

         var var38: String
         when (JooonSettingsScreen.WhenMappings.$EnumSwitchMapping$0[JooonUiSettings.theme.ordinal()]) {
            1 -> var38 = "Dark is the new default: softer contrast, lighter controls."
            2 -> var38 = "Light keeps the original ivory look."
            3 -> var38 = "Jooon's Lime adds a calm green palette that's easy on the eyes."
            else -> throw NoWhenBranchMatchedException()
         }

         val var39: java.util.List = this.textRenderer.wrapLines(Text.literal(var38) as StringVisitable, content.width - 36)
         var var28: Int = buttonY + 62

         for (var40 in var39) {
            context.drawText(this.textRenderer, var40 as OrderedText, content.x + 18, var28, palette.mutedText, false)
            var28 += this.textRenderer.fontHeight + 2
         }

         var10000 = Text.literal("Overlays")
         context.drawText(this.textRenderer, var10000 as Text, content.x + 18, var28 + 20, palette.text, false)
         this.manageOverlaysButton = UiRect(content.x + 18, var28 + 44, 170, 32)
         context.fill(
            this.manageOverlaysButton.x, this.manageOverlaysButton.y, this.manageOverlaysButton.right, this.manageOverlaysButton.bottom, palette.field
         )
         JooonUiThemeKt.drawOutline(context, this.manageOverlaysButton, palette.line)
         var10000 = Text.literal("Manage Overlays")
         context.drawText(
            this.textRenderer,
            var10000 as Text,
            this.manageOverlaysButton.x + (this.manageOverlaysButton.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
            this.manageOverlaysButton.y + (this.manageOverlaysButton.height - this.textRenderer.fontHeight) / 2,
            palette.fieldText,
return false
         )
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
         } else if (this.manageOverlaysButton.contains(mouseX, mouseY)) {
            this.transition.beginClose({ 
               if (`this$0`.client != null) {
                  `this$0`.client.setScreen(OverlayManagerScreen(`this$0`))
               }
return Unit
            })
         } else {
            for (hit in this.themeHits) {
               if (hit.rect.contains(mouseX, mouseY)) {
                  JooonUiSettings.setTheme(hit.theme)
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

   private data class ThemeHit(theme: JooonTheme, rect: UiRect) {
      val theme: JooonTheme
      val rect: UiRect

      init {
         this.theme = theme
         this.rect = rect
      }

      public operator fun component1(): JooonTheme {
         return this.theme
      }

      public operator fun component2(): UiRect {
         return this.rect
      }

      fun copy(theme: JooonTheme = this.theme, rect: UiRect = this.rect): jooon.config.ui.JooonSettingsScreen.ThemeHit {
         return JooonSettingsScreen.ThemeHit(theme, rect)
      }

      override fun toString(): String {
         return "ThemeHit(theme=${this.theme}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return this.theme.hashCode() * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonSettingsScreen.ThemeHit
               && this.theme === (other as JooonSettingsScreen.ThemeHit).theme
               && this.rect == (other as JooonSettingsScreen.ThemeHit).rect
            }
      }
   }
}
