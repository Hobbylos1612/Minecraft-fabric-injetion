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
   private final val transition: JooonScreenTransition
   private final val themeHits: MutableList<jooon.config.ui.JooonSettingsScreen.ThemeHit>
   private final var backButton: UiRect
   private final var manageOverlaysButton: UiRect

   fun JooonSettingsScreen(parentScreen: Screen?) {
      super(Text.method_43470("JR Settings") as Text)
      this.parentScreen = parentScreen
      this.transition = JooonScreenTransition(0L, 0L, 0.0F, 7, null)
      this.themeHits = ArrayList<>()
      this.backButton = UiRect(0, 0, 0, 0)
      this.manageOverlaysButton = UiRect(0, 0, 0, 0)
   }

   fun method_25426() {
      JooonUiSettings.INSTANCE.ensureLoaded()
      super.method_25426()
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      if (!this.transition.finishCloseIfReady()) {
         this.themeHits.clear()
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
         var var10000: MutableText = Text.method_43470("JR SETTINGS")
         context.method_51439(
            this.field_22793,
            var10000 as Text,
            header.x + (header.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
            header.y + (header.height - this.field_22793.field_2000) / 2,
            palette.text,
            false
         )
         var10000 = Text.method_43470("General Settings")
         context.method_51439(this.field_22793, var10000 as Text, content.x + 18, content.y + 18, palette.text, false)
         var10000 = Text.method_43470("Theme changes apply immediately.")
         context.method_51439(this.field_22793, var10000 as Text, content.x + 18, content.y + 36, palette.mutedText, false)
         var10000 = Text.method_43470("Theme")
         context.method_51439(this.field_22793, var10000 as Text, content.x + 18, content.y + 78, palette.text, false)
         val buttonGap: Int = 12
         val buttonWidth: Int = Math.max(120, (content.width - 36 - 12 * 2) / 3)
         val buttonY: Int = content.y + 102
         var buttonX: Int = content.x + 18

         for (descriptionLines in JooonTheme.getEntries()) {
            val lineY: UiRect = UiRect(buttonX, buttonY, buttonWidth, 42)
            val overlaysTitle: Boolean = descriptionLines === JooonUiSettings.INSTANCE.theme
            val manageText: Boolean = lineY.contains(mouseX, contentMouseY)
            val backText: Int = if (overlaysTitle) palette.selected else (if (manageText) palette.hover else palette.field)
            val borderColor: Int = if (overlaysTitle) palette.toggleOn else palette.line
            context.method_25294(lineY.x, lineY.y, lineY.right, lineY.bottom, backText)
            JooonUiThemeKt.drawOutline(context, lineY, borderColor)
            if (overlaysTitle) {
               context.method_25294(lineY.x + 1, lineY.bottom - 4, lineY.right - 1, lineY.bottom - 1, palette.toggleOn)
               context.method_25294(lineY.x + 8, lineY.y + 8, lineY.x + 14, lineY.y + 14, palette.toggleOn)
            }

            var10000 = Text.method_43470(descriptionLines.displayName)
            context.method_51439(
               this.field_22793,
               var10000 as Text,
               lineY.x + (lineY.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
               lineY.y + (lineY.height - this.field_22793.field_2000) / 2,
               if (overlaysTitle) palette.text else (if (manageText) palette.text else palette.fieldText),
               false
            )
            this.themeHits.add(JooonSettingsScreen.ThemeHit(descriptionLines, lineY))
            buttonX += buttonWidth + buttonGap
         }

         var var38: java.lang.String
         when (JooonSettingsScreen.WhenMappings.$EnumSwitchMapping$0[JooonUiSettings.INSTANCE.theme.ordinal()]) {
            1 -> var38 = "Dark is the new default: softer contrast, lighter controls."
            2 -> var38 = "Light keeps the original ivory look."
            3 -> var38 = "Jooon's Lime adds a calm green palette that's easy on the eyes."
            else -> throw NoWhenBranchMatchedException()
         }

         val var39: java.util.List = this.field_22793.method_1728(Text.method_43470(var38) as StringVisitable, content.width - 36)
         var var28: Int = buttonY + 62

         for (var40 in var39) {
            context.method_51430(this.field_22793, var40 as OrderedText, content.x + 18, var28, palette.mutedText, false)
            var28 += this.field_22793.field_2000 + 2
         }

         var10000 = Text.method_43470("Overlays")
         context.method_51439(this.field_22793, var10000 as Text, content.x + 18, var28 + 20, palette.text, false)
         this.manageOverlaysButton = UiRect(content.x + 18, var28 + 44, 170, 32)
         context.method_25294(
            this.manageOverlaysButton.x, this.manageOverlaysButton.y, this.manageOverlaysButton.right, this.manageOverlaysButton.bottom, palette.field
         )
         JooonUiThemeKt.drawOutline(context, this.manageOverlaysButton, palette.line)
         var10000 = Text.method_43470("Manage Overlays")
         context.method_51439(
            this.field_22793,
            var10000 as Text,
            this.manageOverlaysButton.x + (this.manageOverlaysButton.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
            this.manageOverlaysButton.y + (this.manageOverlaysButton.height - this.field_22793.field_2000) / 2,
            palette.fieldText,
            false
         )
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
         } else if (this.manageOverlaysButton.contains(mouseX, mouseY)) {
            this.transition.beginClose({ 
               if (`this$0`.field_22787 != null) {
                  `this$0`.field_22787.method_1507(OverlayManagerScreen(`this$0`))
               }

               Unit.INSTANCE
            })
         } else {
            for (hit in this.themeHits) {
               if (hit.rect.contains(mouseX, mouseY)) {
                  JooonUiSettings.INSTANCE.setTheme(hit.theme)
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

   private data class ThemeHit(theme: JooonTheme, rect: UiRect) {
      public final val theme: JooonTheme
      public final val rect: UiRect

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

      public fun copy(theme: JooonTheme = this.theme, rect: UiRect = this.rect): jooon.config.ui.JooonSettingsScreen.ThemeHit {
         return JooonSettingsScreen.ThemeHit(theme, rect)
      }

      public override fun toString(): String {
         return "ThemeHit(theme=${this.theme}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return this.theme.hashCode() * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
