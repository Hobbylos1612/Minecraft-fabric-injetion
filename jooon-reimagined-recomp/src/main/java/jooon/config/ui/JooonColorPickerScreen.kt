package jooon.config.ui

import java.util.ArrayList
import java.util.Locale
import jooon.config.ConfigDefinition
import jooon.config.ConfigEntryNode
import jooon.config.JooonConfigManager
import kotlin.enums.EnumEntries
import kotlin.math.MathKt
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.CharInput
import net.minecraft.client.input.KeyInput
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

internal class JooonColorPickerScreen : Screen {
   private Screen parentScreen;
   private val definition: ConfigDefinition
   private val entry: ConfigEntryNode
   private val transition: JooonScreenTransition
   private val sliderHits: MutableList<jooon.config.ui.JooonColorPickerScreen.SliderHit>
   private val channelInputHits: MutableList<jooon.config.ui.JooonColorPickerScreen.ChannelInputHit>
   private var color: RgbColor
   private var hsv: HsvColor
   private var pickerMode: jooon.config.ui.JooonColorPickerScreen.PickerMode
   private var activeDrag: jooon.config.ui.JooonColorPickerScreen.DragTarget?
   private var hexInput: jooon.config.ui.JooonColorPickerScreen.HexInputState?
   private var channelInput: jooon.config.ui.JooonColorPickerScreen.ChannelInputState?
   private var previewRect: UiRect
   private var pencilButton: UiRect
   private var hexBox: UiRect
   private var doneButton: UiRect
   private var pickerSquare: UiRect
   private var hueSlider: UiRect

   fun JooonColorPickerScreen(parentScreen: Screen?, definition: ConfigDefinition, entry: ConfigEntryNode) {
      super(Text.literal(entry.label) as Text)
      this.parentScreen = parentScreen
      this.definition = definition
      this.entry = entry
      this.transition = JooonScreenTransition(0L, 0L, 0.0F, 7, null)
      this.sliderHits = ArrayList<>()
      this.channelInputHits = ArrayList<>()
      this.color = JooonColorSupportKt.parseHexColor(this.entry.currentColor())
      this.hsv = this.color.toHsv()
      this.pickerMode = JooonColorPickerScreen.PickerMode.SLIDERS
      this.previewRect = UiRect(0, 0, 0, 0)
      this.pencilButton = UiRect(0, 0, 0, 0)
      this.hexBox = UiRect(0, 0, 0, 0)
      this.doneButton = UiRect(0, 0, 0, 0)
      this.pickerSquare = UiRect(0, 0, 0, 0)
      this.hueSlider = UiRect(0, 0, 0, 0)
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      if (!this.transition.finishCloseIfReady()) {
         this.sliderHits.clear()
         this.channelInputHits.clear()








         context.fill(0, 0, this.width, this.height, palette.backdrop)
         this.transition.push(context)
         context.fill(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.fill(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.fill(content.x, content.y, content.right, content.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, content, palette.line)

         context.drawText(
            this.textRenderer,
            var10000 as Text,
            header.x + (header.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
            header.y + (header.height - this.textRenderer.fontHeight) / 2,
            palette.text,
return false
         )
         context.drawText(this.textRenderer, Text.literal(this.entry.label) as Text, editorArea.x, editorArea.y, palette.text, false)

         if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) {
            this.renderSliderEditor(context, editorArea, contentTop, palette)
         } else {
            this.renderDirectEditor(context, editorArea, contentTop, palette, mouseX, contentMouseY)
         }

         this.renderPreviewPanel(context, previewArea, palette, mouseX, contentMouseY)
         this.renderFooterButtons(context, palette)
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



         val `this$iv`: java.util.Iterator = this.channelInputHits.iterator()

         var var10000: Any
         while (true) {
            if (`this$iv`.hasNext()) {
               val ``: Any = `this$iv`.next()
               if (!(`` as JooonColorPickerScreen.ChannelInputHit).rect.contains(mouseX, mouseY)) {
return continue
               }

               var10000 = ``
break
            }

            var10000 = null
break
         }

         val clickedChannelBox: JooonColorPickerScreen.ChannelInputHit = var10000 as JooonColorPickerScreen.ChannelInputHit
         if (!clickedHex) {
            this.commitHexInput()
         }

         if (clickedChannelBox == null || (if (this.channelInput != null) this.channelInput.channel else null) != clickedChannelBox.channel) {
            this.commitChannelInput()
         }

         if (this.doneButton.contains(mouseX, mouseY)) {
            this.transition.beginClose({ 
               if (`this$0`.client != null) {
                  `this$0`.client.setScreen(`this$0`.parentScreen)
               }
return Unit
            })
         } else if (this.pencilButton.contains(mouseX, mouseY)) {
            this.pickerMode = if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS)
               JooonColorPickerScreen.PickerMode.DIRECT
return else
               JooonColorPickerScreen.PickerMode.SLIDERS
               this.activeDrag = null
return true
         } else if (clickedHex) {
            this.activeDrag = null
            this.beginHexInput()
return true
         } else if (clickedChannelBox != null) {
            this.activeDrag = null
            this.beginChannelInput(clickedChannelBox.channel)
return true
         } else if (this.pickerMode != JooonColorPickerScreen.PickerMode.SLIDERS) {
            if (this.pickerSquare.contains(mouseX, mouseY)) {
               this.activeDrag = JooonColorPickerScreen.DragTarget.PickerSquare.INSTANCE
               this.updatePickerSquare(mouseX, mouseY)
return true
            } else if (this.hueSlider.contains(mouseX, mouseY)) {
               this.activeDrag = JooonColorPickerScreen.DragTarget.HueSlider.INSTANCE
               this.updateHue(mouseX)
return true
            } else {
               super.mouseClicked(event, handled)
            }
         } else {
            val var18: java.util.Iterator = this.sliderHits.iterator()

            while (true) {
               if (var18.hasNext()) {

                  if (!(var19 as JooonColorPickerScreen.SliderHit).rect.contains(mouseX, mouseY)) {
return continue
                  }

                  var10000 = var19
break
               }

               var10000 = null
break
            }

            val var21: JooonColorPickerScreen.SliderHit = var10000 as JooonColorPickerScreen.SliderHit
            if (var10000 as JooonColorPickerScreen.SliderHit == null) {
               super.mouseClicked(event, handled)
            } else {
               this.activeDrag = JooonColorPickerScreen.DragTarget.Channel(var21.channel)
               this.updateChannel(var21.channel, event.x())
return true
            }
         }
      }
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      if (this.transition.isClosing) {
return true
      } else {
         val drag: JooonColorPickerScreen.DragTarget = this.activeDrag
         if (this.activeDrag is JooonColorPickerScreen.DragTarget.Channel) {
            this.updateChannel((this.activeDrag as JooonColorPickerScreen.DragTarget.Channel).channel, event.x())
return true
         } else if (this.activeDrag == JooonColorPickerScreen.DragTarget.PickerSquare.INSTANCE) {
            this.updatePickerSquare(event.x().toInt(), event.y().toInt())
return true
         } else if (drag == JooonColorPickerScreen.DragTarget.HueSlider.INSTANCE) {
            this.updateHue(event.x().toInt())
return true
         } else if (drag != null) {
            throw NoWhenBranchMatchedException()
         } else {
            super.mouseDragged(event, dx, dy)
         }
      }
   }

   fun method_25406(event: Click): Boolean {
      this.activeDrag = null
      super.mouseReleased(event)
   }

   fun method_25404(event: KeyInput): Boolean {
      if (this.transition.isClosing) {
return true
      } else {
         if (this.channelInput != null) {
            if (this.isControlShortcut(event)) {
               when (event.key()) {
                  65 -> {
                     if (this.channelInput != null) {
                        this.channelInput.replaceOnNextType = true
                     }
return true
                  }
                  86 -> {
                     this.applyChannelPaste(this.readClipboard())
return true
                  }
                  else -> {}
               }
            }

            when (event.key()) {
               256 -> {
                  this.channelInput = null
return true
               }
               257, 335 -> {
                  this.commitChannelInput()
return true
               }
               259 -> {
                  if (this.channelInput == null) {
return true
                  }

                  val var3: JooonColorPickerScreen.ChannelInputState = this.channelInput
                  if (this.channelInput.draft.length() > 0) {
                     var3.draft = dropLast(var3.draft, 1)
                  }

                  var3.replaceOnNextType = false
return true
               }
               else -> {}
            }
         }

         label109@
         if (this.hexInput == null) {
            if (event.key() == 256) {
               this.close()
return true
            } else {
               super.keyPressed(event)
            }
         } else {
            if (this.isControlShortcut(event)) {
               when (event.key()) {
                  65 -> {
                     if (this.hexInput != null) {
                        this.hexInput.replaceOnNextType = true
                     }
return true
                  }
                  86 -> {
                     this.applyHexPaste(this.readClipboard())
return true
                  }
                  else -> {}
               }
            }

            when (event.key()) {
               256 -> {
                  this.hexInput = null
return true
               }
               257, 335 -> {
                  this.commitHexInput()
return true
               }
               259 -> {
                  if (this.hexInput == null) {
return true
                  }

                  val current: JooonColorPickerScreen.HexInputState = this.hexInput
                  if (this.hexInput.draft.length() > 0) {
                     current.draft = dropLast(current.draft, 1)
                  }

                  current.replaceOnNextType = false
return true
               }
               else -> break@label109
            }
         }
      }
   }

   fun method_25400(characterEvent: CharInput): Boolean {
      if (this.transition.isClosing) {
return true
      } else {
         val channel: JooonColorPickerScreen.ChannelInputState = this.channelInput
         if (this.channelInput != null) {

            if (var17.length() == 1 && Character.isDigit(var17.charAt(0))) {

               if (var11.length() >= 3) {
return true
               } else {
                  channel.draft = take("$var11${var17.charAt(0)}", 3)
                  channel.replaceOnNextType = false

                  if (var18 != null) {


                     if ((if (0 <= var13 && var13 < 256) var18 else null) != null) {
                        this.applyChannel(channel.channel, var19.intValue())
                     }
                  }
return true
               }
            } else {
return true
            }
         } else if (this.hexInput == null) {
            super.charTyped(characterEvent)
         } else {
            val current: JooonColorPickerScreen.HexInputState = this.hexInput
            var var10000: String = characterEvent.asString()
            if (var10000.length() != 1) {
return true
            } else {


               if (prefix.length() >= 7) {
return true
               } else {
                  if (chr == '#' && prefix.length() == 0) {
                     var10000 = "#"
                  } else {
                     if (!Character.isDigit(chr)) {

                        if ('a' > it || it >= 'g') {
return true
                        }
                     }

                     var10000 = if (startsWith$default(prefix, "#", false, 2, null))
                        "$prefix${Character.toUpperCase(chr)}"
return else
                        "#$prefix${Character.toUpperCase(chr)}"
                     }

                  current.draft = take(var10000, 7)
                  current.replaceOnNextType = false
                  if (this.isValidHex(current.draft)) {
                     applyColor$default(this, JooonColorSupportKt.parseHexColor(current.draft), false, 2, null)
                  }
return true
               }
            }
         }
      }
   }

   fun method_25419() {
      this.commitHexInput()
      this.commitChannelInput()
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

   fun renderSliderEditor(context: DrawContext, rect: UiRect, contentTop: Int, palette: UiPalette) {

      this.renderChannelSlider(context, rect.x, contentTop, rowWidth, palette, "Red", this.color.red, JooonColorPickerScreen.ColorChannel.RED)

      this.renderChannelSlider(context, rect.x, contentTop + 52, rowWidth, palette, "Green", this.color.green, JooonColorPickerScreen.ColorChannel.GREEN)
      this.renderChannelSlider(context, rect.x, var7 + 52, rowWidth, palette, "Blue", this.color.blue, JooonColorPickerScreen.ColorChannel.BLUE)
   }

   fun renderChannelSlider(
      context: DrawContext, x: Int, y: Int, width: Int, palette: UiPalette, label: String, value: Int, channel: JooonColorPickerScreen.ColorChannel
   ) {

var var10000: MutableText = Text.literal("$label $value")
context.drawText(this.textRenderer, var10000 as Text, row.x, row.y, palette.text, false)
val slider: UiRect = UiRect(row.x, row.y + 20, row.width - 80, 8)
val fillRight: Int = slider.x + (slider.width.toFloat() * (value.toFloat() / 255.0F)).roundToInt()
val thumbX: Int = (slider.x.toFloat() + (slider.width - 10).toFloat() * (value.toFloat() / 255.0F)).roundToInt()
var var22: Int
      when (JooonColorPickerScreen.WhenMappings.$EnumSwitchMapping$0[channel.ordinal()]) {
         1 -> var22 = JooonColorSupportKt.rgbInt(255, 96, 96)
         2 -> var22 = JooonColorSupportKt.rgbInt(96, 255, 134)
         3 -> var22 = JooonColorSupportKt.rgbInt(96, 156, 255)
         else -> throw NoWhenBranchMatchedException()
      }

      context.fill(slider.x, slider.y, slider.right, slider.bottom, palette.field)
      context.fill(slider.x, slider.y, fillRight, slider.bottom, var22)
      JooonUiThemeKt.drawOutline(context, slider, palette.line)
      context.fill(thumbX, slider.y - 4, thumbX + 10, slider.bottom + 4, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(thumbX, slider.y - 4, 10, slider.height + 8), palette.line)

      val var23: JooonColorPickerScreen.ChannelInputState
      if (this.channelInput != null) {
         val valueTextX: JooonColorPickerScreen.ChannelInputState = this.channelInput
         var23 = if (this.channelInput.channel === channel) valueTextX else null
      } else {
         var23 = null
      }

      context.fill(valueBox.x, valueBox.y, valueBox.right, valueBox.bottom, if (var23 != null) palette.selected else palette.field)
      JooonUiThemeKt.drawOutline(context, valueBox, if (var23 != null) palette.accent else palette.lineSoft)
      var10000 = Text.literal(
         if (var23 == null)
            java.lang.String.valueOf(value)
return else
            (if (var23.replaceOnNextType) "$value|" else (if (isBlank(var23.draft)) "|" else "${var23.draft}|"))
      )
      context.drawText(
         this.textRenderer,
         var10000 as Text,
         if (var23 == null) valueBox.x + (valueBox.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2 else valueBox.x + 6,
         valueBox.y + (valueBox.height - this.textRenderer.fontHeight) / 2,
         palette.fieldText,
return false
      )
      this.sliderHits.add(JooonColorPickerScreen.SliderHit(channel, slider))
      this.channelInputHits.add(JooonColorPickerScreen.ChannelInputHit(channel, valueBox))
   }

   fun renderDirectEditor(context: DrawContext, rect: UiRect, contentTop: Int, palette: UiPalette, mouseX: Int, mouseY: Int) {

      this.pickerSquare = UiRect(rect.x, contentTop, squareSize, squareSize)
      this.hueSlider = UiRect(rect.x, this.pickerSquare.bottom + 20, squareSize, 12)
      this.drawPickerSquare(context, this.pickerSquare)
      JooonUiThemeKt.drawOutline(context, this.pickerSquare, palette.line)

         this.pickerSquare.x + (this.pickerSquare.width.toDouble() * this.hsv.saturation).roundToInt() - 4,
         this.pickerSquare.y + (this.pickerSquare.height.toDouble() * (1.0 - this.hsv.value)).roundToInt() - 4,
         8,
return 8
      )
      JooonUiThemeKt.drawOutline(context, marker, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(marker.x - 1, marker.y - 1, marker.width + 2, marker.height + 2), palette.line)


      // $VF: Unable to resugar Kotlin loop from Java for loop
      var x: Int = this.hueSlider.x
      while (true) {
         if (x < this.hueSlider.right) break
         context.fill(
            x,
            this.hueSlider.y,
            Math.min(x + hueStep, this.hueSlider.right),
            this.hueSlider.bottom,
            JooonColorSupportKt.hsvToRgb((x - this.hueSlider.x).toDouble() / Math.max(1.0, this.hueSlider.width.toDouble()) * 360.0, 1.0, 1.0).packed()
         )

         x += hueStep
      }

      JooonUiThemeKt.drawOutline(context, this.hueSlider, palette.line)

         this.hueSlider.x + (this.hsv.hue / 360.0 * this.hueSlider.width.toDouble()).roundToInt() - 2, this.hueSlider.y - 3, 4, this.hueSlider.height + 6
      )
      context.fill(hueMarker.x, hueMarker.y, hueMarker.right, hueMarker.bottom, palette.field)
      JooonUiThemeKt.drawOutline(context, hueMarker, palette.line)
      if (this.pickerSquare.contains(mouseX, mouseY) || this.hueSlider.contains(mouseX, mouseY)) {
         context.drawText(
            this.textRenderer, Text.literal("Click precisely on the picker to drag.") as Text, rect.x, this.hueSlider.bottom + 12, palette.mutedText, false
         )
      }
   }

   fun renderPreviewPanel(context: DrawContext, rect: UiRect, palette: UiPalette, mouseX: Int, mouseY: Int) {
      context.fill(rect.x, rect.y, rect.right, rect.bottom, palette.paper)
      JooonUiThemeKt.drawOutline(context, rect, palette.line)
      context.drawText(this.textRenderer, Text.literal("Preview") as Text, rect.x + 14, rect.y + 12, palette.text, false)
      this.previewRect = UiRect(rect.x + (rect.width - 96) / 2, rect.y + 34, 96, 96)
      context.fill(this.previewRect.x, this.previewRect.y, this.previewRect.right, this.previewRect.bottom, this.color.packed())
      JooonUiThemeKt.drawOutline(context, this.previewRect, palette.line)
      this.pencilButton = UiRect(this.previewRect.right - 28, this.previewRect.bottom - 28, 22, 22)
      context.fill(
         this.pencilButton.x,
         this.pencilButton.y,
         this.pencilButton.right,
         this.pencilButton.bottom,
         if (this.pencilButton.contains(mouseX, mouseY)) palette.hover else palette.paper
      )
      JooonUiThemeKt.drawOutline(context, this.pencilButton, palette.line)

      context.drawText(
         this.textRenderer,
         Text.literal(if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) "✎" else "RGB") as Text,
         this.pencilButton.x + (this.pencilButton.width - this.textRenderer.getWidth(pencilLabel)) / 2,
         this.pencilButton.y + (this.pencilButton.height - this.textRenderer.fontHeight) / 2,
         palette.text,
return false
      )
      var var10000: java.util.List = this.textRenderer
         .wrapLines(
            Text.literal(if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) "Open direct picker" else "Return to RGB sliders") as StringVisitable,
            rect.width - 28
         )
         var modeY: Int = this.previewRect.bottom + 12

      for (var10000 in var10000) {
         context.drawText(this.textRenderer, var10000 as OrderedText, rect.x + 14, modeY, palette.mutedText, false)
         modeY += this.textRenderer.fontHeight + 2
      }

      var var19: Boolean
      var var20: UiRect
      run label89@{
         context.drawText(this.textRenderer, Text.literal("Hex Value") as Text, rect.x + 14, modeY + 8, palette.text, false)
         this.hexBox = UiRect(rect.x + 14, modeY + 26, rect.width - 28, 28)
         var19 = this.hexInput != null
         context.fill(this.hexBox.x, this.hexBox.y, this.hexBox.right, this.hexBox.bottom, if (var19) palette.selected else palette.field)
         JooonUiThemeKt.drawOutline(context, this.hexBox, if (var19) palette.accent else palette.lineSoft)
         var20 = UiRect(this.hexBox.x + 6, this.hexBox.y + 6, 16, 16)
         context.fill(var20.x, var20.y, var20.right, var20.bottom, this.color.packed())
         JooonUiThemeKt.drawOutline(context, var20, palette.line)
         if (this.hexInput != null) {
            val helperY: JooonColorPickerScreen.HexInputState = this.hexInput
            var10000 = if (isBlank(this.hexInput.draft)) "#" else helperY.draft
            if (var10000 != null) {
               return@label89
            }
         }

         var10000 = this.entry.currentColor()
      }

      context.drawText(
         this.textRenderer,
         Text.literal((String)(if (var19) "$var10000|" else var10000)) as Text,
         var20.right + 8,
         this.hexBox.y + (this.hexBox.height - this.textRenderer.fontHeight) / 2,
         palette.fieldText,
return false
      )
      val var25: java.util.List = this.textRenderer
         .wrapLines(
            Text.literal(if (var19) "Type 6 hex digits, then press Enter." else "Click the box for an exact value.") as StringVisitable, rect.width - 28
         )
         var var21: Int = this.hexBox.bottom + 10

      for (var26 in var25) {
         context.drawText(this.textRenderer, var26 as OrderedText, rect.x + 14, var21, palette.mutedText, false)
         var21 += this.textRenderer.fontHeight + 2
      }

      this.doneButton = UiRect(rect.x + 14, rect.bottom - 38, rect.width - 28, 24)
   }

   fun renderFooterButtons(context: DrawContext, palette: UiPalette) {
      this.renderButton(context, this.doneButton, "Done", palette)
   }

   fun renderButton(context: DrawContext, rect: UiRect, label: String, palette: UiPalette) {
      context.fill(rect.x, rect.y, rect.right, rect.bottom, palette.field)
      JooonUiThemeKt.drawOutline(context, rect, palette.line)
      context.drawText(
         this.textRenderer,
         Text.literal(label) as Text,
         rect.x + (rect.width - this.textRenderer.getWidth(label)) / 2,
         rect.y + (rect.height - this.textRenderer.fontHeight) / 2,
         palette.fieldText,
return false
      )
   }

   private fun updateChannel(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, mouseX: Double) {
      val var8: java.util.Iterator = this.sliderHits.iterator()

      var var10000: JooonColorPickerScreen.SliderHit
      while (true) {
         if (var8.hasNext()) {
            val `element$iv`: Any = var8.next()
            if ((`element$iv` as JooonColorPickerScreen.SliderHit).channel != channel) {
return continue
            }

            var10000 = (JooonColorPickerScreen.SliderHit)`element$iv`
break
         }

         var10000 = null
break
      }

      var10000 = var10000
      if (var10000 != null) {
         this.applyChannel(
            channel,
            ((((mouseX - var10000.rect.x.toDouble()) / var10000.rect.width.toDouble()).coerceIn(0.0, 1.0) * 255.0).roundToInt()).coerceIn(0, 255)
         )
      }
   }

   private fun updatePickerSquare(mouseX: Int, mouseY: Int) {
      this.hsv = HsvColor.copy$default(
         this.hsv,
         0.0,
         ((mouseX - this.pickerSquare.x).toDouble() / this.pickerSquare.width.toDouble()).coerceIn(0.0, 1.0),
         (1.0 - (mouseY - this.pickerSquare.y).toDouble() / this.pickerSquare.height.toDouble()).coerceIn(0.0, 1.0),
         1,
return null
      )
      this.applyColor(JooonColorSupportKt.hsvToRgb(this.hsv.hue, this.hsv.saturation, this.hsv.value), true)
   }

   private fun updateHue(mouseX: Int) {
      this.hsv = HsvColor.copy$default(
         this.hsv, ((mouseX - this.hueSlider.x).toDouble() / this.hueSlider.width.toDouble()).coerceIn(0.0, 1.0) * 360.0, 0.0, 0.0, 6, null
      )
      this.applyColor(JooonColorSupportKt.hsvToRgb(this.hsv.hue, this.hsv.saturation, this.hsv.value), true)
   }

   private fun applyColor(newColor: RgbColor, keepHsv: Boolean = false) {
      this.color = newColor
      if (!keepHsv) {
         this.hsv = newColor.toHsv()
      }

      this.entry.setColor(JooonColorSupportKt.formatHexColor(newColor.red, newColor.green, newColor.blue))
      JooonConfigManager.write(this.definition.modId)
   }

   private fun commitHexInput() {
      if (this.hexInput != null) {
         val current: JooonColorPickerScreen.HexInputState = this.hexInput
         if (this.isValidHex(this.hexInput.draft)) {
            applyColor$default(this, JooonColorSupportKt.parseHexColor(current.draft), false, 2, null)
         }

         this.hexInput = null
      }
   }

   private fun beginHexInput() {
      this.channelInput = null
      this.hexInput = JooonColorPickerScreen.HexInputState(this.entry.currentColor(), true)
   }

   private fun beginChannelInput(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel) {
      this.hexInput = null
      this.channelInput = JooonColorPickerScreen.ChannelInputState(java.lang.String.valueOf(this.currentChannelValue(channel)), channel, true)
   }

   private fun commitChannelInput() {
      if (this.channelInput != null) {
         val current: JooonColorPickerScreen.ChannelInputState = this.channelInput
         var var10000: Int = toIntOrNull(this.channelInput.draft)
         if (var10000 != null) {

            var10000 = if (0 <= it && it < 256) var10000 else null
            if ((if (0 <= it && it < 256) var10000 else null) != null) {
               this.applyChannel(current.channel, var10000.intValue())
            }
         }

         this.channelInput = null
      }
   }

   private fun currentChannelValue(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel): Int {
      var var10000: Int
      when (JooonColorPickerScreen.WhenMappings.$EnumSwitchMapping$0[channel.ordinal()]) {
         1 -> var10000 = this.color.red
         2 -> var10000 = this.color.green
         3 -> var10000 = this.color.blue
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   private fun applyChannel(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, value: Int) {
      var var10000: RgbColor
      when (JooonColorPickerScreen.WhenMappings.$EnumSwitchMapping$0[channel.ordinal()]) {
         1 -> var10000 = RgbColor.copy$default(this.color, value, 0, 0, 6, null)
         2 -> var10000 = RgbColor.copy$default(this.color, 0, value, 0, 5, null)
         3 -> var10000 = RgbColor.copy$default(this.color, 0, 0, value, 3, null)
         else -> throw NoWhenBranchMatchedException()
      }

      applyColor$default(this, var10000, false, 2, null)
   }

   private fun applyChannelPaste(rawClipboard: String) {
      if (this.channelInput != null) {
         val current: JooonColorPickerScreen.ChannelInputState = this.channelInput
         var var10000: String = firstOrNull(lineSequence(rawClipboard)) as String
         if (var10000 != null) {
            var10000 = trim(var10000).toString()
            if (var10000 != null) {
               var10000 = take(var10000, 3)
               if (var10000 != null) {
                  if (!isBlank(var10000)) {
                     val prefix: java.lang.CharSequence = var10000

                     run label81@{
                                                var18 = false
                     }

                     if (!var18) {


                        if (var19 != null) {


                           if ((if (0 <= it && it < 256) var19 else null) != null) {

                              current.draft = var13
                              current.replaceOnNextType = false
                              this.applyChannel(current.channel, var14)
return return
                           }
                        }
return return
                     }
                  }
return return
               }
            }
         }
      }
   }

   private fun applyHexPaste(rawClipboard: String) {
      if (this.hexInput != null) {
         val current: JooonColorPickerScreen.HexInputState = this.hexInput
         var prefix: String = firstOrNull(lineSequence(rawClipboard)) as String
         if (prefix != null) {

            if (base != null) {
               var candidate: String = base.replace("#", "")
               if (candidate != null) {
                  var var10000: String = candidate.toUpperCase(Locale.ROOT)
                  if (var10000 != null) {
                     val it: java.lang.CharSequence = var10000

                     var itx: Int = 0

                     for (var15 in it.length()..itx) {
                        val `element$iv$iv`: Char = it.charAt(itx)
                        if (Character.isDigit(`element$iv$iv`) || 'A' <= `element$iv$iv` && `element$iv$iv` < 'G') {
                           var12.append(`element$iv$iv`)
                        }
                     }

                     var10000 = (var12 as StringBuilder).toString()
                     if (var10000 != null) {

                        if (var27 != null) {
                           if (isBlank(var27)) {
return return
                           }

                           prefix = if (current.replaceOnNextType) "#" else current.draft
                           if (startsWith$default(prefix, "#", false, 2, null)) {
                              var10000 = drop(prefix, 1)
                           } else {
                              val var25: java.lang.CharSequence = prefix
                              val `destination$iv$ivx`: Appendable = StringBuilder()
                              var `index$iv$ivx`: Int = 0

                              for (var34 in var25.length()..`index$iv$ivx`) {
                                 var var36: Char
                                 run label163@{
                                    var36 = var25.charAt(`index$iv$ivx`)
                                    if (!Character.isDigit(var36)) {

                                       if ('A' > var39 || var39 >= 'G') {
                                          var42 = false
                                          return@label163
                                       }
                                    }

                                    var42 = true
                                 }

                                 if (var42) {
                                    `destination$iv$ivx`.append(var36)
                                 }
                              }

                              var10000 = (`destination$iv$ivx` as StringBuilder).toString()
                           }

                           candidate = "#${take("$var10000$var27", 6)}"
                           val var24: java.lang.CharSequence = drop(candidate, 1)
                           var var29: Int = 0

                           while (true) {
                              if (var29 >= var24.length()) {
                                 var43 = true
break
                              }

                              if (!Character.isDigit(var33) && ('A' > var33 || var33 >= 'G')) {
                                 var43 = false
break
                              }

                              var29++
                           }

                           if (!var43) {
return return
                           }

                           current.draft = candidate
                           current.replaceOnNextType = false
                           if (this.isValidHex(candidate)) {
                              applyColor$default(this, JooonColorSupportKt.parseHexColor(candidate), false, 2, null)
                           }
return return
                        }
                     }
                  }
               }
            }
         }
      }
   }

   fun isControlShortcut(event: KeyInput): Boolean {
      (event.modifiers() and 2) != 0
   }

   private fun readClipboard(): String {
      var var10000: String = if (this.client != null && this.client.keyboard != null) this.client.keyboard.getClipboard() else null
      if (var10000 == null) {
         var10000 = ""
      }

      return var10000
   }

   private fun isValidHex(value: String): Boolean {

      if (normalized.length() == 7) {
         val `this$iv`: java.lang.CharSequence = drop(normalized, 1)
         var var5: Int = 0

         var var10: Boolean
         while (true) {
            if (var5 >= `this$iv`.length()) {
               var10 = true
break
            }

            run label63@{

               if (!Character.isDigit(it)) {

                  if ('a' > var9 || var9 >= 'g') {
                     var10 = false
                     return@label63
                  }
               }

               var10 = true
            }

            if (!var10) {
               var10 = false
break
            }

            var5++
         }

         if (var10) {
            return true
         }
      }

      return false
   }

   fun drawPickerSquare(context: DrawContext, rect: UiRect) {


      // $VF: Unable to resugar Kotlin loop from Java for loop
      var y: Int = rect.y
      while (true) {
         if (y < rect.bottom) break
         // $VF: Unable to resugar Kotlin loop from Java for loop
         var x: Int = rect.x
         while (true) {
            if (x < rect.right) break
            context.fill(
               x,
               y,
               Math.min(x + step, rect.right),
               Math.min(y + step, rect.bottom),
               JooonColorSupportKt.hsvToRgb(
                     this.hsv.hue,
                     ((x - rect.x).toDouble() / rect.width.toDouble()).coerceIn(0.0, 1.0),
                     (1.0 - (y - rect.y).toDouble() / rect.height.toDouble()).coerceIn(0.0, 1.0)
                  )
                  .packed()
            )

            x += step
         }

         y += step
      }
   }

   private data class ChannelInputHit(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, rect: UiRect) {
      val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel
      val rect: UiRect

      init {
         this.channel = channel
         this.rect = rect
      }

      public operator fun component1(): jooon.config.ui.JooonColorPickerScreen.ColorChannel {
         return this.channel
      }

      public operator fun component2(): UiRect {
         return this.rect
      }

      fun copy(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel, rect: UiRect = this.rect): jooon.config.ui.JooonColorPickerScreen.ChannelInputHit {
         return JooonColorPickerScreen.ChannelInputHit(channel, rect)
      }

      override fun toString(): String {
         return "ChannelInputHit(channel=${this.channel}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return this.channel.hashCode() * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonColorPickerScreen.ChannelInputHit
               && this.channel === (other as JooonColorPickerScreen.ChannelInputHit).channel
               && this.rect == (other as JooonColorPickerScreen.ChannelInputHit).rect
            }
      }
   }

   private data class ChannelInputState(draft: String, channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, replaceOnNextType: Boolean) {
      var draft: String
      val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel
      var replaceOnNextType: Boolean

      init {
         this.draft = draft
         this.channel = channel
         this.replaceOnNextType = replaceOnNextType
      }

      public operator fun component1(): String {
         return this.draft
      }

      public operator fun component2(): jooon.config.ui.JooonColorPickerScreen.ColorChannel {
         return this.channel
      }

      public operator fun component3(): Boolean {
         return this.replaceOnNextType
      }

      fun copy(
         draft: String = this.draft,
         channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel,
         replaceOnNextType: Boolean = this.replaceOnNextType
      ): jooon.config.ui.JooonColorPickerScreen.ChannelInputState {
         return JooonColorPickerScreen.ChannelInputState(draft, channel, replaceOnNextType)
      }

      override fun toString(): String {
         return "ChannelInputState(draft=${this.draft}, channel=${this.channel}, replaceOnNextType=${this.replaceOnNextType})"
      }

      override fun hashCode(): Int {
         return (this.draft.hashCode() * 31 + this.channel.hashCode()) * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is JooonColorPickerScreen.ChannelInputState
               && this.draft == (other as JooonColorPickerScreen.ChannelInputState).draft
               && this.channel === (other as JooonColorPickerScreen.ChannelInputState).channel
               && this.replaceOnNextType == (other as JooonColorPickerScreen.ChannelInputState).replaceOnNextType
            }
      }
   }

   private enum class ColorChannel {
      RED,
      GREEN,
      BLUE;

      
      fun getEntries(): EnumEntries<JooonColorPickerScreen.ColorChannel> {
         $ENTRIES
      }
   }

   private sealed interface DragTarget {
      data class Channel(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel) : JooonColorPickerScreen.DragTarget {
         val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel

         init {
            this.channel = channel
         }

         public operator fun component1(): jooon.config.ui.JooonColorPickerScreen.ColorChannel {
            return this.channel
         }

         fun copy(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel): jooon.config.ui.JooonColorPickerScreen.DragTarget.Channel {
            return JooonColorPickerScreen.DragTarget.Channel(channel)
         }

         override fun toString(): String {
            return "Channel(channel=${this.channel})"
         }

         override fun hashCode(): Int {
            return this.channel.hashCode()
         }

         override operator fun equals(other: Any?): Boolean {
            label22@
            if (this === other) {
               return true
            } else {
               return other is JooonColorPickerScreen.DragTarget.Channel && this.channel === (other as JooonColorPickerScreen.DragTarget.Channel).channel
            }
         }
      }

      data object HueSlider : JooonColorPickerScreen.DragTarget {
         override fun toString(): String {
            return "HueSlider"
         }

         override fun hashCode(): Int {
            return -1654385641
         }

         override operator fun equals(other: Any?): Boolean {
            return this === other || other is JooonColorPickerScreen.DragTarget.HueSlider
         }
      }

      data object PickerSquare : JooonColorPickerScreen.DragTarget {
         override fun toString(): String {
            return "PickerSquare"
         }

         override fun hashCode(): Int {
            return -360474003
         }

         override operator fun equals(other: Any?): Boolean {
            return this === other || other is JooonColorPickerScreen.DragTarget.PickerSquare
         }
      }
   }

   private data class HexInputState(draft: String, replaceOnNextType: Boolean) {
      var draft: String
      var replaceOnNextType: Boolean

      init {
         this.draft = draft
         this.replaceOnNextType = replaceOnNextType
      }

      public operator fun component1(): String {
         return this.draft
      }

      public operator fun component2(): Boolean {
         return this.replaceOnNextType
      }

      fun copy(draft: String = this.draft, replaceOnNextType: Boolean = this.replaceOnNextType): jooon.config.ui.JooonColorPickerScreen.HexInputState {
         return JooonColorPickerScreen.HexInputState(draft, replaceOnNextType)
      }

      override fun toString(): String {
         return "HexInputState(draft=${this.draft}, replaceOnNextType=${this.replaceOnNextType})"
      }

      override fun hashCode(): Int {
         return this.draft.hashCode() * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonColorPickerScreen.HexInputState
               && this.draft == (other as JooonColorPickerScreen.HexInputState).draft
               && this.replaceOnNextType == (other as JooonColorPickerScreen.HexInputState).replaceOnNextType
            }
      }
   }

   private enum class PickerMode {
      SLIDERS,
      DIRECT;

      
      fun getEntries(): EnumEntries<JooonColorPickerScreen.PickerMode> {
         $ENTRIES
      }
   }

   private data class SliderHit(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, rect: UiRect) {
      val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel
      val rect: UiRect

      init {
         this.channel = channel
         this.rect = rect
      }

      public operator fun component1(): jooon.config.ui.JooonColorPickerScreen.ColorChannel {
         return this.channel
      }

      public operator fun component2(): UiRect {
         return this.rect
      }

      fun copy(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel, rect: UiRect = this.rect): jooon.config.ui.JooonColorPickerScreen.SliderHit {
         return JooonColorPickerScreen.SliderHit(channel, rect)
      }

      override fun toString(): String {
         return "SliderHit(channel=${this.channel}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return this.channel.hashCode() * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonColorPickerScreen.SliderHit
               && this.channel === (other as JooonColorPickerScreen.SliderHit).channel
               && this.rect == (other as JooonColorPickerScreen.SliderHit).rect
            }
      }
   }
}
