package jooon.config.ui

import java.util.ArrayList
import java.util.Locale
import jooon.config.ConfigDefinition
import jooon.config.ConfigEntryNode
import jooon.config.JooonConfigManager
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nJooonColorPickerScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonColorPickerScreen.kt\njooon/config/ui/JooonColorPickerScreen\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,718:1\n288#2,2:719\n288#2,2:721\n288#2,2:724\n1#3:723\n1083#4,2:726\n429#4:728\n502#4,5:729\n429#4:734\n502#4,5:735\n1064#4,2:740\n1064#4,2:742\n*S KotlinDebug\n*F\n+ 1 JooonColorPickerScreen.kt\njooon/config/ui/JooonColorPickerScreen\n*L\n109#1:719,2\n142#1:721,2\n551#1:724,2\n623#1:726,2\n642#1:728\n642#1:729,5\n648#1:734\n648#1:735,5\n650#1:740,2\n667#1:742,2\n*E\n"])
internal class JooonColorPickerScreen : Screen {
   private Screen parentScreen;
   private final val definition: ConfigDefinition
   private final val entry: ConfigEntryNode
   private final val transition: JooonScreenTransition
   private final val sliderHits: MutableList<jooon.config.ui.JooonColorPickerScreen.SliderHit>
   private final val channelInputHits: MutableList<jooon.config.ui.JooonColorPickerScreen.ChannelInputHit>
   private final var color: RgbColor
   private final var hsv: HsvColor
   private final var pickerMode: jooon.config.ui.JooonColorPickerScreen.PickerMode
   private final var activeDrag: jooon.config.ui.JooonColorPickerScreen.DragTarget?
   private final var hexInput: jooon.config.ui.JooonColorPickerScreen.HexInputState?
   private final var channelInput: jooon.config.ui.JooonColorPickerScreen.ChannelInputState?
   private final var previewRect: UiRect
   private final var pencilButton: UiRect
   private final var hexBox: UiRect
   private final var doneButton: UiRect
   private final var pickerSquare: UiRect
   private final var hueSlider: UiRect

   fun JooonColorPickerScreen(parentScreen: Screen?, definition: ConfigDefinition, entry: ConfigEntryNode) {
      super(Text.method_43470(entry.label) as Text)
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
         val contentMouseY: Int = this.transition.transformedMouseY(mouseY)
         val palette: UiPalette = JooonUiThemeKt.currentPalette()
         val outer: UiRect = UiRect(32, 22, this.field_22789 - 64, this.field_22790 - 44)
         val header: UiRect = UiRect(outer.x + 16, outer.y + 16, outer.width - 32, 52)
         val content: UiRect = UiRect(outer.x + 22, header.bottom + 16, outer.width - 44, outer.height - 112)
         val previewWidth: Int = Math.min(220, Math.max(180, content.width / 4))
         val editorArea: UiRect = UiRect(content.x + 18, content.y + 18, content.width - previewWidth - 48, content.height - 36)
         val previewArea: UiRect = UiRect(editorArea.right + 18, content.y + 18, previewWidth, content.height - 36)
         context.method_25294(0, 0, this.field_22789, this.field_22790, palette.backdrop)
         this.transition.push(context)
         context.method_25294(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.method_25294(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.method_25294(content.x, content.y, content.right, content.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, content, palette.line)
         val var10000: MutableText = Text.method_43470("COLOR PICKER")
         context.method_51439(
            this.field_22793,
            var10000 as Text,
            header.x + (header.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
            header.y + (header.height - this.field_22793.field_2000) / 2,
            palette.text,
            false
         )
         context.method_51439(this.field_22793, Text.method_43470(this.entry.label) as Text, editorArea.x, editorArea.y, palette.text, false)
         val contentTop: Int = editorArea.y + this.field_22793.field_2000 + 14
         if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) {
            this.renderSliderEditor(context, editorArea, contentTop, palette)
         } else {
            this.renderDirectEditor(context, editorArea, contentTop, palette, mouseX, contentMouseY)
         }

         this.renderPreviewPanel(context, previewArea, palette, mouseX, contentMouseY)
         this.renderFooterButtons(context, palette)
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
         val clickedHex: Boolean = this.hexBox.contains(mouseX, mouseY)
         val `$this$firstOrNull$iv`: java.util.Iterator = this.channelInputHits.iterator()

         var var10000: Any
         while (true) {
            if (`$this$firstOrNull$iv`.hasNext()) {
               val `$i$f$firstOrNull`: Any = `$this$firstOrNull$iv`.next()
               if (!(`$i$f$firstOrNull` as JooonColorPickerScreen.ChannelInputHit).rect.contains(mouseX, mouseY)) {
                  continue
               }

               var10000 = `$i$f$firstOrNull`
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
               if (`this$0`.field_22787 != null) {
                  `this$0`.field_22787.method_1507(`this$0`.parentScreen)
               }

               Unit.INSTANCE
            })
         } else if (this.pencilButton.contains(mouseX, mouseY)) {
            this.pickerMode = if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS)
               JooonColorPickerScreen.PickerMode.DIRECT
               else
               JooonColorPickerScreen.PickerMode.SLIDERS
               this.activeDrag = null
            true
         } else if (clickedHex) {
            this.activeDrag = null
            this.beginHexInput()
            true
         } else if (clickedChannelBox != null) {
            this.activeDrag = null
            this.beginChannelInput(clickedChannelBox.channel)
            true
         } else if (this.pickerMode != JooonColorPickerScreen.PickerMode.SLIDERS) {
            if (this.pickerSquare.contains(mouseX, mouseY)) {
               this.activeDrag = JooonColorPickerScreen.DragTarget.PickerSquare.INSTANCE
               this.updatePickerSquare(mouseX, mouseY)
               true
            } else if (this.hueSlider.contains(mouseX, mouseY)) {
               this.activeDrag = JooonColorPickerScreen.DragTarget.HueSlider.INSTANCE
               this.updateHue(mouseX)
               true
            } else {
               super.method_25402(event, handled)
            }
         } else {
            val var18: java.util.Iterator = this.sliderHits.iterator()

            while (true) {
               if (var18.hasNext()) {
                  val var19: Any = var18.next()
                  if (!(var19 as JooonColorPickerScreen.SliderHit).rect.contains(mouseX, mouseY)) {
                     continue
                  }

                  var10000 = var19
                  break
               }

               var10000 = null
               break
            }

            val var21: JooonColorPickerScreen.SliderHit = var10000 as JooonColorPickerScreen.SliderHit
            if (var10000 as JooonColorPickerScreen.SliderHit == null) {
               super.method_25402(event, handled)
            } else {
               this.activeDrag = JooonColorPickerScreen.DragTarget.Channel(var21.channel)
               this.updateChannel(var21.channel, event.comp_4798())
               true
            }
         }
      }
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      if (this.transition.isClosing) {
         true
      } else {
         val drag: JooonColorPickerScreen.DragTarget = this.activeDrag
         if (this.activeDrag is JooonColorPickerScreen.DragTarget.Channel) {
            this.updateChannel((this.activeDrag as JooonColorPickerScreen.DragTarget.Channel).channel, event.comp_4798())
            true
         } else if (this.activeDrag == JooonColorPickerScreen.DragTarget.PickerSquare.INSTANCE) {
            this.updatePickerSquare((int)event.comp_4798(), (int)event.comp_4799())
            true
         } else if (drag == JooonColorPickerScreen.DragTarget.HueSlider.INSTANCE) {
            this.updateHue((int)event.comp_4798())
            true
         } else if (drag != null) {
            throw NoWhenBranchMatchedException()
         } else {
            super.method_25403(event, dx, dy)
         }
      }
   }

   fun method_25406(event: Click): Boolean {
      this.activeDrag = null
      super.method_25406(event)
   }

   fun method_25404(event: KeyInput): Boolean {
      if (this.transition.isClosing) {
         true
      } else {
         if (this.channelInput != null) {
            if (this.isControlShortcut(event)) {
               when (event.comp_4795()) {
                  65 -> {
                     if (this.channelInput != null) {
                        this.channelInput.replaceOnNextType = true
                     }

                     true
                  }
                  86 -> {
                     this.applyChannelPaste(this.readClipboard())
                     true
                  }
                  else -> {}
               }
            }

            when (event.comp_4795()) {
               256 -> {
                  this.channelInput = null
                  true
               }
               257, 335 -> {
                  this.commitChannelInput()
                  true
               }
               259 -> {
                  if (this.channelInput == null) {
                     true
                  }

                  val var3: JooonColorPickerScreen.ChannelInputState = this.channelInput
                  if (this.channelInput.draft.length() > 0) {
                     var3.draft = StringsKt.dropLast(var3.draft, 1)
                  }

                  var3.replaceOnNextType = false
                  true
               }
               else -> {}
            }
         }

         label109@
         if (this.hexInput == null) {
            if (event.comp_4795() == 256) {
               this.method_25419()
               true
            } else {
               super.method_25404(event)
            }
         } else {
            if (this.isControlShortcut(event)) {
               when (event.comp_4795()) {
                  65 -> {
                     if (this.hexInput != null) {
                        this.hexInput.replaceOnNextType = true
                     }

                     true
                  }
                  86 -> {
                     this.applyHexPaste(this.readClipboard())
                     true
                  }
                  else -> {}
               }
            }

            when (event.comp_4795()) {
               256 -> {
                  this.hexInput = null
                  true
               }
               257, 335 -> {
                  this.commitHexInput()
                  true
               }
               259 -> {
                  if (this.hexInput == null) {
                     true
                  }

                  val current: JooonColorPickerScreen.HexInputState = this.hexInput
                  if (this.hexInput.draft.length() > 0) {
                     current.draft = StringsKt.dropLast(current.draft, 1)
                  }

                  current.replaceOnNextType = false
                  true
               }
               else -> break@label109
            }
         }
      }
   }

   fun method_25400(characterEvent: CharInput): Boolean {
      if (this.transition.isClosing) {
         true
      } else {
         val channel: JooonColorPickerScreen.ChannelInputState = this.channelInput
         if (this.channelInput != null) {
            val var17: java.lang.String = characterEvent.method_74226()
            if (var17.length() == 1 && Character.isDigit(var17.charAt(0))) {
               val var11: java.lang.String = if (channel.replaceOnNextType) "" else channel.draft
               if (var11.length() >= 3) {
                  true
               } else {
                  channel.draft = StringsKt.take("$var11${var17.charAt(0)}", 3)
                  channel.replaceOnNextType = false
                  val var18: Int = StringsKt.toIntOrNull(channel.draft)
                  if (var18 != null) {
                     val var13: Int = var18.intValue()
                     val var19: Int = if (0 <= var13 && var13 < 256) var18 else null
                     if ((if (0 <= var13 && var13 < 256) var18 else null) != null) {
                        this.applyChannel(channel.channel, var19.intValue())
                     }
                  }

                  true
               }
            } else {
               true
            }
         } else if (this.hexInput == null) {
            super.method_25400(characterEvent)
         } else {
            val current: JooonColorPickerScreen.HexInputState = this.hexInput
            var var10000: java.lang.String = characterEvent.method_74226()
            if (var10000.length() != 1) {
               true
            } else {
               val chr: Char = var10000.charAt(0)
               val prefix: java.lang.String = if (current.replaceOnNextType) "#" else current.draft
               if (prefix.length() >= 7) {
                  true
               } else {
                  if (chr == '#' && prefix.length() == 0) {
                     var10000 = "#"
                  } else {
                     if (!Character.isDigit(chr)) {
                        val it: Char = Character.toLowerCase(chr)
                        if ('a' > it || it >= 'g') {
                           true
                        }
                     }

                     var10000 = if (StringsKt.startsWith$default(prefix, "#", false, 2, null))
                        "$prefix${Character.toUpperCase(chr)}"
                        else
                        "#$prefix${Character.toUpperCase(chr)}"
                     }

                  current.draft = StringsKt.take(var10000, 7)
                  current.replaceOnNextType = false
                  if (this.isValidHex(current.draft)) {
                     applyColor$default(this, JooonColorSupportKt.parseHexColor(current.draft), false, 2, null)
                  }

                  true
               }
            }
         }
      }
   }

   fun method_25419() {
      this.commitHexInput()
      this.commitChannelInput()
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

   fun renderSliderEditor(context: DrawContext, rect: UiRect, contentTop: Int, palette: UiPalette) {
      val rowWidth: Int = rect.width - 12
      this.renderChannelSlider(context, rect.x, contentTop, rowWidth, palette, "Red", this.color.red, JooonColorPickerScreen.ColorChannel.RED)
      val var7: Int = contentTop + 52
      this.renderChannelSlider(context, rect.x, contentTop + 52, rowWidth, palette, "Green", this.color.green, JooonColorPickerScreen.ColorChannel.GREEN)
      this.renderChannelSlider(context, rect.x, var7 + 52, rowWidth, palette, "Blue", this.color.blue, JooonColorPickerScreen.ColorChannel.BLUE)
   }

   fun renderChannelSlider(
      context: DrawContext, x: Int, y: Int, width: Int, palette: UiPalette, label: java.lang.String, value: Int, channel: JooonColorPickerScreen.ColorChannel
   ) {
      val row: UiRect = UiRect(x, y, width, 42)
var var10000: MutableText = Text.method_43470("$label $value")
context.method_51439(this.field_22793, var10000 as Text, row.x, row.y, palette.text, false)
val slider: UiRect = UiRect(row.x, row.y + 20, row.width - 80, 8)
val fillRight: Int = slider.x + MathKt.roundToInt((float)slider.width * ((float)value / 255.0F))
val thumbX: Int = MathKt.roundToInt((float)slider.x + (float)(slider.width - 10) * ((float)value / 255.0F))
var var22: Int
      when (JooonColorPickerScreen.WhenMappings.$EnumSwitchMapping$0[channel.ordinal()]) {
         1 -> var22 = JooonColorSupportKt.rgbInt(255, 96, 96)
         2 -> var22 = JooonColorSupportKt.rgbInt(96, 255, 134)
         3 -> var22 = JooonColorSupportKt.rgbInt(96, 156, 255)
         else -> throw NoWhenBranchMatchedException()
      }

      context.method_25294(slider.x, slider.y, slider.right, slider.bottom, palette.field)
      context.method_25294(slider.x, slider.y, fillRight, slider.bottom, var22)
      JooonUiThemeKt.drawOutline(context, slider, palette.line)
      context.method_25294(thumbX, slider.y - 4, thumbX + 10, slider.bottom + 4, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(thumbX, slider.y - 4, 10, slider.height + 8), palette.line)
      val valueBox: UiRect = UiRect(slider.right + 12, row.y + 12, 52, 20)
      val var23: JooonColorPickerScreen.ChannelInputState
      if (this.channelInput != null) {
         val valueTextX: JooonColorPickerScreen.ChannelInputState = this.channelInput
         var23 = if (this.channelInput.channel === channel) valueTextX else null
      } else {
         var23 = null
      }

      context.method_25294(valueBox.x, valueBox.y, valueBox.right, valueBox.bottom, if (var23 != null) palette.selected else palette.field)
      JooonUiThemeKt.drawOutline(context, valueBox, if (var23 != null) palette.accent else palette.lineSoft)
      var10000 = Text.method_43470(
         if (var23 == null)
            java.lang.String.valueOf(value)
            else
            (if (var23.replaceOnNextType) "$value|" else (if (StringsKt.isBlank(var23.draft)) "|" else "${var23.draft}|"))
      )
      context.method_51439(
         this.field_22793,
         var10000 as Text,
         if (var23 == null) valueBox.x + (valueBox.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2 else valueBox.x + 6,
         valueBox.y + (valueBox.height - this.field_22793.field_2000) / 2,
         palette.fieldText,
         false
      )
      this.sliderHits.add(JooonColorPickerScreen.SliderHit(channel, slider))
      this.channelInputHits.add(JooonColorPickerScreen.ChannelInputHit(channel, valueBox))
   }

   fun renderDirectEditor(context: DrawContext, rect: UiRect, contentTop: Int, palette: UiPalette, mouseX: Int, mouseY: Int) {
      val squareSize: Int = Math.min(220, Math.max(140, Math.min(rect.width - 20, rect.bottom - contentTop - 76)))
      this.pickerSquare = UiRect(rect.x, contentTop, squareSize, squareSize)
      this.hueSlider = UiRect(rect.x, this.pickerSquare.bottom + 20, squareSize, 12)
      this.drawPickerSquare(context, this.pickerSquare)
      JooonUiThemeKt.drawOutline(context, this.pickerSquare, palette.line)
      val marker: UiRect = UiRect(
         this.pickerSquare.x + MathKt.roundToInt((double)this.pickerSquare.width * this.hsv.saturation) - 4,
         this.pickerSquare.y + MathKt.roundToInt((double)this.pickerSquare.height * (1.0 - this.hsv.value)) - 4,
         8,
         8
      )
      JooonUiThemeKt.drawOutline(context, marker, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(marker.x - 1, marker.y - 1, marker.width + 2, marker.height + 2), palette.line)
      val hueStep: Int = Math.max(1, this.hueSlider.width / 120)

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var x: Int = this.hueSlider.x
      while (true) {
         if (x < this.hueSlider.right) break
         context.method_25294(
            x,
            this.hueSlider.y,
            Math.min(x + hueStep, this.hueSlider.right),
            this.hueSlider.bottom,
            JooonColorSupportKt.hsvToRgb((double)(x - this.hueSlider.x) / Math.max(1.0, (double)this.hueSlider.width) * 360.0, 1.0, 1.0).packed()
         )

         x += hueStep
      }

      JooonUiThemeKt.drawOutline(context, this.hueSlider, palette.line)
      val hueMarker: UiRect = UiRect(
         this.hueSlider.x + MathKt.roundToInt(this.hsv.hue / 360.0 * (double)this.hueSlider.width) - 2, this.hueSlider.y - 3, 4, this.hueSlider.height + 6
      )
      context.method_25294(hueMarker.x, hueMarker.y, hueMarker.right, hueMarker.bottom, palette.field)
      JooonUiThemeKt.drawOutline(context, hueMarker, palette.line)
      if (this.pickerSquare.contains(mouseX, mouseY) || this.hueSlider.contains(mouseX, mouseY)) {
         context.method_51439(
            this.field_22793, Text.method_43470("Click precisely on the picker to drag.") as Text, rect.x, this.hueSlider.bottom + 12, palette.mutedText, false
         )
      }
   }

   fun renderPreviewPanel(context: DrawContext, rect: UiRect, palette: UiPalette, mouseX: Int, mouseY: Int) {
      context.method_25294(rect.x, rect.y, rect.right, rect.bottom, palette.paper)
      JooonUiThemeKt.drawOutline(context, rect, palette.line)
      context.method_51439(this.field_22793, Text.method_43470("Preview") as Text, rect.x + 14, rect.y + 12, palette.text, false)
      this.previewRect = UiRect(rect.x + (rect.width - 96) / 2, rect.y + 34, 96, 96)
      context.method_25294(this.previewRect.x, this.previewRect.y, this.previewRect.right, this.previewRect.bottom, this.color.packed())
      JooonUiThemeKt.drawOutline(context, this.previewRect, palette.line)
      this.pencilButton = UiRect(this.previewRect.right - 28, this.previewRect.bottom - 28, 22, 22)
      context.method_25294(
         this.pencilButton.x,
         this.pencilButton.y,
         this.pencilButton.right,
         this.pencilButton.bottom,
         if (this.pencilButton.contains(mouseX, mouseY)) palette.hover else palette.paper
      )
      JooonUiThemeKt.drawOutline(context, this.pencilButton, palette.line)
      val pencilLabel: java.lang.String = if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) "✎" else "RGB"
      context.method_51439(
         this.field_22793,
         Text.method_43470(if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) "✎" else "RGB") as Text,
         this.pencilButton.x + (this.pencilButton.width - this.field_22793.method_1727(pencilLabel)) / 2,
         this.pencilButton.y + (this.pencilButton.height - this.field_22793.field_2000) / 2,
         palette.text,
         false
      )
      var var10000: java.util.List = this.field_22793
         .method_1728(
            Text.method_43470(if (this.pickerMode === JooonColorPickerScreen.PickerMode.SLIDERS) "Open direct picker" else "Return to RGB sliders") as StringVisitable,
            rect.width - 28
         )
         var modeY: Int = this.previewRect.bottom + 12

      for (var10000 in var10000) {
         context.method_51430(this.field_22793, var10000 as OrderedText, rect.x + 14, modeY, palette.mutedText, false)
         modeY += this.field_22793.field_2000 + 2
      }

      var var19: Boolean
      var var20: UiRect
      run label89@{
         context.method_51439(this.field_22793, Text.method_43470("Hex Value") as Text, rect.x + 14, modeY + 8, palette.text, false)
         this.hexBox = UiRect(rect.x + 14, modeY + 26, rect.width - 28, 28)
         var19 = this.hexInput != null
         context.method_25294(this.hexBox.x, this.hexBox.y, this.hexBox.right, this.hexBox.bottom, if (var19) palette.selected else palette.field)
         JooonUiThemeKt.drawOutline(context, this.hexBox, if (var19) palette.accent else palette.lineSoft)
         var20 = UiRect(this.hexBox.x + 6, this.hexBox.y + 6, 16, 16)
         context.method_25294(var20.x, var20.y, var20.right, var20.bottom, this.color.packed())
         JooonUiThemeKt.drawOutline(context, var20, palette.line)
         if (this.hexInput != null) {
            val helperY: JooonColorPickerScreen.HexInputState = this.hexInput
            var10000 = if (StringsKt.isBlank(this.hexInput.draft)) "#" else helperY.draft
            if (var10000 != null) {
               return@label89
            }
         }

         var10000 = this.entry.currentColor()
      }

      context.method_51439(
         this.field_22793,
         Text.method_43470((java.lang.String)(if (var19) "$var10000|" else var10000)) as Text,
         var20.right + 8,
         this.hexBox.y + (this.hexBox.height - this.field_22793.field_2000) / 2,
         palette.fieldText,
         false
      )
      val var25: java.util.List = this.field_22793
         .method_1728(
            Text.method_43470(if (var19) "Type 6 hex digits, then press Enter." else "Click the box for an exact value.") as StringVisitable, rect.width - 28
         )
         var var21: Int = this.hexBox.bottom + 10

      for (var26 in var25) {
         context.method_51430(this.field_22793, var26 as OrderedText, rect.x + 14, var21, palette.mutedText, false)
         var21 += this.field_22793.field_2000 + 2
      }

      this.doneButton = UiRect(rect.x + 14, rect.bottom - 38, rect.width - 28, 24)
   }

   fun renderFooterButtons(context: DrawContext, palette: UiPalette) {
      this.renderButton(context, this.doneButton, "Done", palette)
   }

   fun renderButton(context: DrawContext, rect: UiRect, label: java.lang.String, palette: UiPalette) {
      context.method_25294(rect.x, rect.y, rect.right, rect.bottom, palette.field)
      JooonUiThemeKt.drawOutline(context, rect, palette.line)
      context.method_51439(
         this.field_22793,
         Text.method_43470(label) as Text,
         rect.x + (rect.width - this.field_22793.method_1727(label)) / 2,
         rect.y + (rect.height - this.field_22793.field_2000) / 2,
         palette.fieldText,
         false
      )
   }

   private fun updateChannel(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, mouseX: Double) {
      val var8: java.util.Iterator = this.sliderHits.iterator()

      var var10000: JooonColorPickerScreen.SliderHit
      while (true) {
         if (var8.hasNext()) {
            val `element$iv`: Any = var8.next()
            if ((`element$iv` as JooonColorPickerScreen.SliderHit).channel != channel) {
               continue
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
            RangesKt.coerceIn(MathKt.roundToInt(RangesKt.coerceIn((mouseX - (double)var10000.rect.x) / (double)var10000.rect.width, 0.0, 1.0) * 255.0), 0, 255)
         )
      }
   }

   private fun updatePickerSquare(mouseX: Int, mouseY: Int) {
      this.hsv = HsvColor.copy$default(
         this.hsv,
         0.0,
         RangesKt.coerceIn((double)(mouseX - this.pickerSquare.x) / (double)this.pickerSquare.width, 0.0, 1.0),
         RangesKt.coerceIn(1.0 - (double)(mouseY - this.pickerSquare.y) / (double)this.pickerSquare.height, 0.0, 1.0),
         1,
         null
      )
      this.applyColor(JooonColorSupportKt.hsvToRgb(this.hsv.hue, this.hsv.saturation, this.hsv.value), true)
   }

   private fun updateHue(mouseX: Int) {
      this.hsv = HsvColor.copy$default(
         this.hsv, RangesKt.coerceIn((double)(mouseX - this.hueSlider.x) / (double)this.hueSlider.width, 0.0, 1.0) * 360.0, 0.0, 0.0, 6, null
      )
      this.applyColor(JooonColorSupportKt.hsvToRgb(this.hsv.hue, this.hsv.saturation, this.hsv.value), true)
   }

   private fun applyColor(newColor: RgbColor, keepHsv: Boolean = false) {
      this.color = newColor
      if (!keepHsv) {
         this.hsv = newColor.toHsv()
      }

      this.entry.setColor(JooonColorSupportKt.formatHexColor(newColor.red, newColor.green, newColor.blue))
      JooonConfigManager.INSTANCE.write(this.definition.modId)
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
         var var10000: Int = StringsKt.toIntOrNull(this.channelInput.draft)
         if (var10000 != null) {
            val it: Int = var10000.intValue()
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
         var var10000: java.lang.String = SequencesKt.firstOrNull(StringsKt.lineSequence(rawClipboard)) as java.lang.String
         if (var10000 != null) {
            var10000 = StringsKt.trim(var10000).toString()
            if (var10000 != null) {
               var10000 = StringsKt.take(var10000, 3)
               if (var10000 != null) {
                  if (!StringsKt.isBlank(var10000)) {
                     val prefix: java.lang.CharSequence = var10000

                     run label81@{
                                                var18 = false
                     }

                     if (!var18) {
                        val var13: java.lang.String = StringsKt.take("${if (current.replaceOnNextType) "" else current.draft}$var10000", 3)
                        val var19: Int = StringsKt.toIntOrNull(var13)
                        if (var19 != null) {
                           val it: Int = var19.intValue()
                           val var20: Int = if (0 <= it && it < 256) var19 else null
                           if ((if (0 <= it && it < 256) var19 else null) != null) {
                              val var14: Int = var20
                              current.draft = var13
                              current.replaceOnNextType = false
                              this.applyChannel(current.channel, var14)
                              return
                           }
                        }

                        return
                     }
                  }

                  return
               }
            }
         }
      }
   }

   private fun applyHexPaste(rawClipboard: String) {
      if (this.hexInput != null) {
         val current: JooonColorPickerScreen.HexInputState = this.hexInput
         var prefix: java.lang.String = SequencesKt.firstOrNull(StringsKt.lineSequence(rawClipboard)) as java.lang.String
         if (prefix != null) {
            val base: java.lang.String = StringsKt.trim(prefix).toString()
            if (base != null) {
               var candidate: java.lang.String = StringsKt.replace$default(base, "#", "", false, 4, null)
               if (candidate != null) {
                  var var10000: java.lang.String = candidate.toUpperCase(Locale.ROOT)
                  if (var10000 != null) {
                     val it: java.lang.CharSequence = var10000
                     val var12: Appendable = StringBuilder()
                     var itx: Int = 0

                     for (var15 in it.length()..itx) {
                        val `element$iv$iv`: Char = it.charAt(itx)
                        if (Character.isDigit(`element$iv$iv`) || 'A' <= `element$iv$iv` && `element$iv$iv` < 'G') {
                           var12.append(`element$iv$iv`)
                        }
                     }

                     var10000 = (var12 as StringBuilder).toString()
                     if (var10000 != null) {
                        val var27: java.lang.String = StringsKt.take(var10000, 6)
                        if (var27 != null) {
                           if (StringsKt.isBlank(var27)) {
                              return
                           }

                           prefix = if (current.replaceOnNextType) "#" else current.draft
                           if (StringsKt.startsWith$default(prefix, "#", false, 2, null)) {
                              var10000 = StringsKt.drop(prefix, 1)
                           } else {
                              val var25: java.lang.CharSequence = prefix
                              val `destination$iv$ivx`: Appendable = StringBuilder()
                              var `index$iv$ivx`: Int = 0

                              for (var34 in var25.length()..`index$iv$ivx`) {
                                 var var36: Char
                                 run label163@{
                                    var36 = var25.charAt(`index$iv$ivx`)
                                    if (!Character.isDigit(var36)) {
                                       val var39: Char = Character.toUpperCase(var36)
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

                           candidate = "#${StringsKt.take("$var10000$var27", 6)}"
                           val var24: java.lang.CharSequence = StringsKt.drop(candidate, 1)
                           var var29: Int = 0

                           while (true) {
                              if (var29 >= var24.length()) {
                                 var43 = true
                                 break
                              }

                              val var33: Char = var24.charAt(var29)
                              if (!Character.isDigit(var33) && ('A' > var33 || var33 >= 'G')) {
                                 var43 = false
                                 break
                              }

                              var29++
                           }

                           if (!var43) {
                              return
                           }

                           current.draft = candidate
                           current.replaceOnNextType = false
                           if (this.isValidHex(candidate)) {
                              applyColor$default(this, JooonColorSupportKt.parseHexColor(candidate), false, 2, null)
                           }

                           return
                        }
                     }
                  }
               }
            }
         }
      }
   }

   fun isControlShortcut(event: KeyInput): Boolean {
      (event.comp_4797() and 2) != 0
   }

   private fun readClipboard(): String {
      var var10000: java.lang.String = if (this.field_22787 != null && this.field_22787.field_1774 != null) this.field_22787.field_1774.method_1460() else null
      if (var10000 == null) {
         var10000 = ""
      }

      return var10000
   }

   private fun isValidHex(value: String): Boolean {
      val normalized: java.lang.String = if (StringsKt.startsWith$default(value, "#", false, 2, null)) value else "#$value"
      if (normalized.length() == 7) {
         val `$this$all$iv`: java.lang.CharSequence = StringsKt.drop(normalized, 1)
         var var5: Int = 0

         var var10: Boolean
         while (true) {
            if (var5 >= `$this$all$iv`.length()) {
               var10 = true
               break
            }

            run label63@{
               val it: Char = `$this$all$iv`.charAt(var5)
               if (!Character.isDigit(it)) {
                  val var9: Char = Character.toLowerCase(it)
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
      val step: Int = 4

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var y: Int = rect.y
      while (true) {
         if (y < rect.bottom) break
         // $VF: Unable to resugar Kotlin loop from Java for loop
         var x: Int = rect.x
         while (true) {
            if (x < rect.right) break
            context.method_25294(
               x,
               y,
               Math.min(x + step, rect.right),
               Math.min(y + step, rect.bottom),
               JooonColorSupportKt.hsvToRgb(
                     this.hsv.hue,
                     RangesKt.coerceIn((double)(x - rect.x) / (double)rect.width, 0.0, 1.0),
                     RangesKt.coerceIn(1.0 - (double)(y - rect.y) / (double)rect.height, 0.0, 1.0)
                  )
                  .packed()
            )

            x += step
         }

         y += step
      }
   }

   private data class ChannelInputHit(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, rect: UiRect) {
      public final val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel
      public final val rect: UiRect

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

      public fun copy(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel, rect: UiRect = this.rect): jooon.config.ui.JooonColorPickerScreen.ChannelInputHit {
         return JooonColorPickerScreen.ChannelInputHit(channel, rect)
      }

      public override fun toString(): String {
         return "ChannelInputHit(channel=${this.channel}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return this.channel.hashCode() * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final var draft: String
      public final val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel
      public final var replaceOnNextType: Boolean

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

      public fun copy(
         draft: String = this.draft,
         channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel,
         replaceOnNextType: Boolean = this.replaceOnNextType
      ): jooon.config.ui.JooonColorPickerScreen.ChannelInputState {
         return JooonColorPickerScreen.ChannelInputState(draft, channel, replaceOnNextType)
      }

      public override fun toString(): String {
         return "ChannelInputState(draft=${this.draft}, channel=${this.channel}, replaceOnNextType=${this.replaceOnNextType})"
      }

      public override fun hashCode(): Int {
         return (this.draft.hashCode() * 31 + this.channel.hashCode()) * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      public override operator fun equals(other: Any?): Boolean {
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

      @JvmStatic
      fun getEntries(): EnumEntries<JooonColorPickerScreen.ColorChannel> {
         $ENTRIES
      }
   }

   private sealed interface DragTarget {
      public data class Channel(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel) : JooonColorPickerScreen.DragTarget {
         public final val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel

         init {
            this.channel = channel
         }

         public operator fun component1(): jooon.config.ui.JooonColorPickerScreen.ColorChannel {
            return this.channel
         }

         public fun copy(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel): jooon.config.ui.JooonColorPickerScreen.DragTarget.Channel {
            return JooonColorPickerScreen.DragTarget.Channel(channel)
         }

         public override fun toString(): String {
            return "Channel(channel=${this.channel})"
         }

         public override fun hashCode(): Int {
            return this.channel.hashCode()
         }

         public override operator fun equals(other: Any?): Boolean {
            label22@
            if (this === other) {
               return true
            } else {
               return other is JooonColorPickerScreen.DragTarget.Channel && this.channel === (other as JooonColorPickerScreen.DragTarget.Channel).channel
            }
         }
      }

      public data object HueSlider : JooonColorPickerScreen.DragTarget {
         public override fun toString(): String {
            return "HueSlider"
         }

         public override fun hashCode(): Int {
            return -1654385641
         }

         public override operator fun equals(other: Any?): Boolean {
            return this === other || other is JooonColorPickerScreen.DragTarget.HueSlider
         }
      }

      public data object PickerSquare : JooonColorPickerScreen.DragTarget {
         public override fun toString(): String {
            return "PickerSquare"
         }

         public override fun hashCode(): Int {
            return -360474003
         }

         public override operator fun equals(other: Any?): Boolean {
            return this === other || other is JooonColorPickerScreen.DragTarget.PickerSquare
         }
      }
   }

   private data class HexInputState(draft: String, replaceOnNextType: Boolean) {
      public final var draft: String
      public final var replaceOnNextType: Boolean

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

      public fun copy(draft: String = this.draft, replaceOnNextType: Boolean = this.replaceOnNextType): jooon.config.ui.JooonColorPickerScreen.HexInputState {
         return JooonColorPickerScreen.HexInputState(draft, replaceOnNextType)
      }

      public override fun toString(): String {
         return "HexInputState(draft=${this.draft}, replaceOnNextType=${this.replaceOnNextType})"
      }

      public override fun hashCode(): Int {
         return this.draft.hashCode() * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      public override operator fun equals(other: Any?): Boolean {
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

      @JvmStatic
      fun getEntries(): EnumEntries<JooonColorPickerScreen.PickerMode> {
         $ENTRIES
      }
   }

   private data class SliderHit(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel, rect: UiRect) {
      public final val channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel
      public final val rect: UiRect

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

      public fun copy(channel: jooon.config.ui.JooonColorPickerScreen.ColorChannel = this.channel, rect: UiRect = this.rect): jooon.config.ui.JooonColorPickerScreen.SliderHit {
         return JooonColorPickerScreen.SliderHit(channel, rect)
      }

      public override fun toString(): String {
         return "SliderHit(channel=${this.channel}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return this.channel.hashCode() * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
