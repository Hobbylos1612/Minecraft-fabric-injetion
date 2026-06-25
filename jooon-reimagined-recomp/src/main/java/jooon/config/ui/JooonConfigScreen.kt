package jooon.config.ui

import java.awt.Desktop
import java.awt.Desktop.Action
import java.net.URI
import java.util.ArrayList
import java.util.Locale
import jooon.config.Config
import jooon.config.ConfigCategory
import jooon.config.ConfigCommentNode
import jooon.config.ConfigControlKind
import jooon.config.ConfigDefinition
import jooon.config.ConfigEntryNode
import jooon.config.ConfigNode
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.features.farming.AutoVisitor
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.math.MathKt
import net.minecraft.text.OrderedText
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.CharInput
import net.minecraft.client.input.KeyInput
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Identifier

internal class JooonConfigScreen : Screen {
   private Screen parentScreen;
   private val definition: ConfigDefinition
   private Identifier logoTexture;
   private val transition: JooonScreenTransition
   private var selectedCategoryKey: String
   private val smoothScroll: SmoothScroll
   private var maxScroll: Int
   private val categorySmoothScroll: SmoothScroll
   private var maxCategoryScroll: Int
   private var contentArea: UiRect
   private var categoryArea: UiRect
   private var footerLink: UiRect
   private var settingsButton: UiRect
   private var searchBox: UiRect
   private var scrollbarTrack: UiRect
   private var scrollbarThumb: UiRect
   private var categoryScrollbarTrack: UiRect
   private var categoryScrollbarThumb: UiRect
   private val categoryHits: MutableList<jooon.config.ui.JooonConfigScreen.CategoryHit>
   private val controlHits: MutableList<jooon.config.ui.JooonConfigScreen.ControlHit>
   private val sliderInputHits: MutableList<jooon.config.ui.JooonConfigScreen.SliderInputHit>
   private val textInputHits: MutableList<jooon.config.ui.JooonConfigScreen.TextInputHit>
   private var activeSlider: jooon.config.ui.JooonConfigScreen.SliderDrag?
   private var activeScrollbar: jooon.config.ui.JooonConfigScreen.ScrollbarDrag?
   private var activeCategoryScrollbar: jooon.config.ui.JooonConfigScreen.ScrollbarDrag?
   private var sliderTextInput: jooon.config.ui.JooonConfigScreen.SliderTextInput?
   private var textEntryInput: jooon.config.ui.JooonConfigScreen.TextEntryInput?
   private var searchText: String
   private var searchInputActive: Boolean
   private var searchReplaceOnNextType: Boolean

   fun JooonConfigScreen(parentScreen: Screen?, definition: ConfigDefinition) {
      super(Text.literal(definition.title) as Text)
      this.parentScreen = parentScreen
      this.definition = definition

      this.logoTexture = var10001
      this.transition = JooonScreenTransition(0L, 0L, 0.0F, 7, null)

      var var4: String = if (var3 != null) var3.key else null
      if (var4 == null) {
         var4 = ""
      }

      this.selectedCategoryKey = var4
      this.smoothScroll = SmoothScroll()
      this.categorySmoothScroll = SmoothScroll()
      this.contentArea = UiRect(0, 0, 0, 0)
      this.categoryArea = UiRect(0, 0, 0, 0)
      this.footerLink = UiRect(0, 0, 0, 0)
      this.settingsButton = UiRect(0, 0, 0, 0)
      this.searchBox = UiRect(0, 0, 0, 0)
      this.scrollbarTrack = UiRect(0, 0, 0, 0)
      this.scrollbarThumb = UiRect(0, 0, 0, 0)
      this.categoryScrollbarTrack = UiRect(0, 0, 0, 0)
      this.categoryScrollbarThumb = UiRect(0, 0, 0, 0)
      this.categoryHits = ArrayList<>()
      this.controlHits = ArrayList<>()
      this.sliderInputHits = ArrayList<>()
      this.textInputHits = ArrayList<>()
      this.searchText = ""
   }

   fun method_25426() {
      JooonUiSettings.ensureLoaded()
      if (isBlank(this.selectedCategoryKey) || this.visibleCategory(this.selectedCategoryKey) == null) {

         var var1: String = if (var10001 != null) var10001.key else null
         if (var1 == null) {
            var1 = ""
         }

         this.selectedCategoryKey = var1
      }

      super.init()
   }

   private fun visibleCategories(): List<ConfigCategory> {
      val `this$iv$iv`: java.lang.Iterable = this.definition.categories
      val `destination$iv$iv`: java.util.Collection = ArrayList()

      for (`element$iv$iv` in `this$iv$iv`) {
         if (!((`element$iv$iv` as ConfigCategory).key == "farming")) {
            `destination$iv$iv`.add(`element$iv$iv`)
         }
      }

      return `destination$iv$iv` as MutableList<ConfigCategory>
   }

   private fun visibleCategory(key: String): ConfigCategory? {
      val var4: java.util.Iterator = this.visibleCategories().iterator()

      var var10000: Any
      while (true) {
         if (var4.hasNext()) {
            val `element$iv`: Any = var4.next()
            if (!((`element$iv` as ConfigCategory).key == key)) {
return continue
            }

            var10000 = `element$iv`
break
         }

         var10000 = null
break
      }

      return var10000 as ConfigCategory
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      if (!this.transition.finishCloseIfReady()) {
         this.categoryHits.clear()
         this.controlHits.clear()
         this.sliderInputHits.clear()
         this.textInputHits.clear()




            outer.x + 14, outer.y + 14, Math.min(210, Math.max(176, (outer.width.toFloat() * 0.2F).roundToInt())) - 14, outer.height - 14 * 2
         )




         this.categoryArea = categoriesBox

         this.contentArea = UiRect(header.x, header.bottom + 12, header.width, outer.bottom - header.bottom - 28)
         val contentItems: java.util.List = this.currentContentItems()
         context.fill(
            0,
            0,
            this.width,
            this.height,
            palette.backdrop and 16777215 or ((this.transition.currentAlpha() * (palette.backdrop ushr 24 and 255).toFloat()).roundToInt()).coerceIn(0, 255) shl 24
         )
         this.transition.push(context)
         context.fill(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.fill(sidebar.x, sidebar.y, sidebar.right, sidebar.bottom, palette.sidebar)
         JooonUiThemeKt.drawOutline(context, sidebar, palette.line)
         context.fill(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.fill(this.contentArea.x, this.contentArea.y, this.contentArea.right, this.contentArea.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, this.contentArea, palette.line)
         context.fill(sidebar.x, footerDividerY, sidebar.right, footerDividerY + 1, palette.line)
         this.renderLogo(context, logoBox, palette)
         this.renderCategories(context, categoriesBox, palette, mouseX, contentMouseY)
         this.renderHeader(context, header, palette)

         this.renderSidebarFooter(context, sidebar, footerDividerY, palette, mouseX, contentMouseY)
         if (this.sliderTextInput == null && this.textEntryInput == null && hoveredTooltip != null && !isBlank(hoveredTooltip)) {
            this.renderTooltip(context, hoveredTooltip, mouseX, contentMouseY, palette)
         }

         super.render(context, mouseX, mouseY, delta)
         this.transition.pop(context)
      }
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (this.transition.isClosing) {
return true
      } else {


         if (event.button() != 0) {
            super.mouseClicked(event, handled)
         } else {
            if (!this.searchBox.contains(mouseX, mouseY)) {
               this.searchInputActive = false
               this.searchReplaceOnNextType = false
            }

            val hit: java.util.Iterator = this.sliderInputHits.iterator()

            var var10000: Any
            while (true) {
               if (hit.hasNext()) {
                  val `element$iv`: Any = hit.next()
                  if (!(`element$iv` as JooonConfigScreen.SliderInputHit).rect.contains(mouseX, mouseY)) {
return continue
                  }

                  var10000 = `element$iv`
break
               }

               var10000 = null
break
            }

            val clickedSliderInput: JooonConfigScreen.SliderInputHit = var10000 as JooonConfigScreen.SliderInputHit
            if (var10000 as JooonConfigScreen.SliderInputHit == null) {
               this.commitSliderTextInput()
            }

            val var20: java.util.Iterator = this.textInputHits.iterator()

            while (true) {
               if (var20.hasNext()) {

                  if (!(var21 as JooonConfigScreen.TextInputHit).rect.contains(mouseX, mouseY)) {
return continue
                  }

                  var10000 = var21
break
               }

               var10000 = null
break
            }

            val var13: JooonConfigScreen.TextInputHit = var10000 as JooonConfigScreen.TextInputHit
            if (var10000 as JooonConfigScreen.TextInputHit == null) {
               this.commitTextEntryInput()
            }

            if (this.searchBox.contains(mouseX, mouseY)) {
               this.searchInputActive = true
               this.searchReplaceOnNextType = false
return true
            } else if (this.settingsButton.contains(mouseX, mouseY)) {
               this.transition.beginClose({ 
                  if (`this$0`.client != null) {
                     `this$0`.client.setScreen(JooonSettingsScreen(`this$0`))
                  }
return Unit
               })
            } else if (this.footerLink.contains(mouseX, mouseY)) {
               this.openExternalLink("https://jooon.xyz")
return true
            } else if (clickedSliderInput != null) {
               this.beginSliderTextInput(clickedSliderInput.entry)
return true
            } else if (var13 != null) {
               this.beginTextEntryInput(var13.entry)
return true
            } else if (this.categoryScrollbarThumb.contains(mouseX, mouseY)) {
               this.activeCategoryScrollbar = JooonConfigScreen.ScrollbarDrag(
                  this.categoryScrollbarTrack, this.categoryScrollbarThumb.height, mouseY - this.categoryScrollbarThumb.y
               )
return true
            } else if (this.categoryScrollbarTrack.contains(mouseX, mouseY) && this.maxCategoryScroll > 0) {
               this.activeCategoryScrollbar = JooonConfigScreen.ScrollbarDrag(
                  this.categoryScrollbarTrack, this.categoryScrollbarThumb.height, this.categoryScrollbarThumb.height / 2
               )
               this.updateCategoryScrollbar(mouseY)
return true
            } else {
               for (var18 in this.categoryHits) {
                  if (var18.rect.contains(mouseX, mouseY)) {
                     this.selectedCategoryKey = var18.categoryKey
                     this.smoothScroll.jump(0)
                     this.sliderTextInput = null
                     this.textEntryInput = null
                     if (!isBlank(this.searchText)) {
                        this.searchText = ""
                        this.searchInputActive = false
                        this.searchReplaceOnNextType = false
                     }
return true
                  }
               }

               if (this.scrollbarThumb.contains(mouseX, mouseY)) {
                  this.activeScrollbar = JooonConfigScreen.ScrollbarDrag(this.scrollbarTrack, this.scrollbarThumb.height, mouseY - this.scrollbarThumb.y)
return true
               } else if (this.scrollbarTrack.contains(mouseX, mouseY) && this.maxScroll > 0) {
                  this.activeScrollbar = JooonConfigScreen.ScrollbarDrag(this.scrollbarTrack, this.scrollbarThumb.height, this.scrollbarThumb.height / 2)
                  this.updateScrollbar(mouseY)
return true
               } else {
                  for (var19 in this.controlHits) {
                     if (var19.rect.contains(mouseX, mouseY)) {
                        when (JooonConfigScreen.WhenMappings.$EnumSwitchMapping$0[var19.kind.ordinal()]) {
                           1 -> {
                              var19.entry.toggleBoolean()
                              this.save()
return true
                           }
                           2 -> {
                              this.activeSlider = JooonConfigScreen.SliderDrag(var19.entry, var19.rect)
                              this.updateSlider(var19.entry, var19.rect, event.x())
return true
                           }
                           3 -> {
                              this.beginTextEntryInput(var19.entry)
return true
                           }
                           4 -> {
                              this.transition.beginClose({ 
                                 if (`this$0`.client != null) {
                                    `this$0`.client.setScreen(JooonColorPickerScreen(`this$0`, `this$0`.definition, `$hit`.entry))
                                 }
return Unit
                              })
                           }
                           5 -> {
                              var19.entry.triggerAction()
                              this.save()
return true
                           }
                           6 -> {
                              ConfigEntryNode.cycleEnum$default(var19.entry, 0, 1, null)
                              this.save()
return true
                           }
                           else -> throw NoWhenBranchMatchedException()
                        }
                     }
                  }

                  super.mouseClicked(event, handled)
               }
            }
         }
      }
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      if (this.transition.isClosing) {
return true
      } else if (this.activeSlider != null) {
         val it: JooonConfigScreen.SliderDrag = this.activeSlider
         this.updateSlider(this.activeSlider.entry, it.trackRect, event.x())
return true
      } else if (this.activeCategoryScrollbar != null) {
         this.updateCategoryScrollbar(event.y().toInt())
return true
      } else if (this.activeScrollbar != null) {
         this.updateScrollbar(event.y().toInt())
return true
      } else {
         super.mouseDragged(event, dx, dy)
      }
   }

   fun method_25406(event: Click): Boolean {
      this.activeSlider = null
      this.activeCategoryScrollbar = null
      this.activeScrollbar = null
      super.mouseReleased(event)
   }

   fun method_25401(mouseX: Double, mouseY: Double, horizontal: Double, vertical: Double): Boolean {
      if (this.transition.isClosing) {
return true
      } else {

         if (this.categoryArea.contains(mouseX.toInt(), mouseY.toInt()) && this.maxCategoryScroll > 0) {
            this.categorySmoothScroll.addDelta((-vertical * 36.0).roundToInt(), this.maxCategoryScroll)
return true
         } else if (this.categoryArea.contains(mouseX.toInt(), contentMouseY) && this.maxCategoryScroll > 0) {
            this.categorySmoothScroll.addDelta((-vertical * 36.0).roundToInt(), this.maxCategoryScroll)
return true
         } else if (this.contentArea.contains(mouseX.toInt(), mouseY.toInt()) && this.maxScroll > 0) {
            this.smoothScroll.addDelta((-vertical * 40.0).roundToInt(), this.maxScroll)
return true
         } else if (this.contentArea.contains(mouseX.toInt(), contentMouseY) && this.maxScroll > 0) {
            this.smoothScroll.addDelta((-vertical * 40.0).roundToInt(), this.maxScroll)
return true
         } else {
            super.mouseScrolled(mouseX, mouseY, horizontal, vertical)
         }
      }
   }

   fun method_25404(event: KeyInput): Boolean {
      if (this.transition.isClosing) {
return true
      } else {
         label157@
         if (!this.searchInputActive) {
            val input: JooonConfigScreen.SliderTextInput = this.sliderTextInput
            label147@
            if (this.sliderTextInput == null) {
               val textInput: JooonConfigScreen.TextEntryInput = this.textEntryInput
               label140@
               if (this.textEntryInput == null) {
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
                           textInput.replaceOnNextType = textInput.draft.length() > 0
return true
                        }
                        86 -> {
                           this.applyTextPaste(textInput, this.readClipboard())
return true
                        }
                        else -> {}
                     }
                  }

                  when (event.key()) {
                     256 -> {
                        this.textEntryInput = null
return true
                     }
                     257, 335 -> {
                        this.commitTextEntryInput()
return true
                     }
                     259 -> {
                        if (textInput.draft.length() > 0) {
                           textInput.draft = dropLast(textInput.draft, 1)
                        }

                        textInput.replaceOnNextType = false
return true
                     }
                     else -> break@label140
                  }
               }
            } else {
               if (this.isControlShortcut(event)) {
                  when (event.key()) {
                     65 -> {
                        input.replaceOnNextType = input.draft.length() > 0
return true
                     }
                     86 -> {
                        this.applySliderPaste(input, this.readClipboard())
return true
                     }
                     else -> {}
                  }
               }

               when (event.key()) {
                  256 -> {
                     this.sliderTextInput = null
return true
                  }
                  257, 335 -> {
                     this.commitSliderTextInput()
return true
                  }
                  259 -> {
                     if (input.draft.length() > 0) {
                        input.draft = dropLast(input.draft, 1)
                     }

                     input.replaceOnNextType = false
return true
                  }
                  else -> break@label147
               }
            }
         } else {
            if (this.isControlShortcut(event)) {
               when (event.key()) {
                  65 -> {
                     this.searchReplaceOnNextType = this.searchText.length() > 0
return true
                  }
                  86 -> {
                     this.applySearchPaste(this.readClipboard())
return true
                  }
                  else -> {}
               }
            }

            when (event.key()) {
               256 -> {
                  if (!isBlank(this.searchText)) {
                     this.searchText = ""
                     this.smoothScroll.jump(0)
                  } else {
                     this.searchInputActive = false
                  }

                  this.searchReplaceOnNextType = false
return true
               }
               257, 335 -> true
               259 -> {
                  if (this.searchReplaceOnNextType) {
                     this.searchText = ""
                  } else if (this.searchText.length() > 0) {
                     this.searchText = dropLast(this.searchText, 1)
                  }

                  this.searchReplaceOnNextType = false
                  this.smoothScroll.jump(0)
return true
               }
               else -> break@label157
            }
         }
      }
   }

   fun method_25400(characterEvent: CharInput): Boolean {
      if (this.transition.isClosing) {
return true
      } else if (this.searchInputActive) {

         if (var14.length() != 1) {
return true
         } else {

            if (var9 >= ' ' && this.searchText.length() < 40) {
               this.searchText = take("${if (this.searchReplaceOnNextType) "" else this.searchText}$var9", 40)
               this.searchReplaceOnNextType = false
               this.smoothScroll.jump(0)
return true
            } else {
return true
            }
         }
      } else {
         val input: JooonConfigScreen.SliderTextInput = this.sliderTextInput
         if (this.sliderTextInput != null) {

            if (var13.length() != 1) {
return true
            } else {

               if (!this.acceptsSliderCharacter(input.entry, input, var10)) {
return true
               } else {
                  input.draft = take("${if (input.replaceOnNextType) "" else input.draft}$var10", 16)
                  input.replaceOnNextType = false
return true
               }
            }
         } else if (this.textEntryInput == null) {
            super.charTyped(characterEvent)
         } else {
            val textInput: JooonConfigScreen.TextEntryInput = this.textEntryInput

            if (var10000.length() != 1) {
return true
            } else {

               if (chr >= ' ' && (textInput.draft.length() < 32 || textInput.replaceOnNextType)) {
                  textInput.draft = take("${if (textInput.replaceOnNextType) "" else textInput.draft}$chr", 32)
                  textInput.replaceOnNextType = false
return true
               } else {
return true
               }
            }
         }
      }
   }

   fun method_25419() {
      this.commitSliderTextInput()
      this.commitTextEntryInput()
      this.save()
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

   fun renderLogo(context: DrawContext, rect: UiRect, palette: UiPalette) {
      context.fill(rect.x, rect.y, rect.right, rect.bottom, palette.paper)
      JooonUiThemeKt.drawOutline(context, rect, palette.line)

      context.drawTexture(
         RenderPipelines.GUI_TEXTURED,
         this.logoTexture,
         rect.x + (rect.width - logoSize) / 2,
         rect.y + (rect.height - logoSize) / 2,
         0.0F,
         0.0F,
         logoSize,
         logoSize,
         128,
         128,
         128,
return 128
      )
   }

   fun renderCategories(context: DrawContext, rect: UiRect, palette: UiPalette, mouseX: Int, mouseY: Int) {
      val categories: java.util.List = this.visibleCategories()


      this.maxCategoryScroll = Math.max(0, categories.size() * 32 + Math.max(0, categories.size() - 1) * 8 + 10 + 10 - rect.height)



      var rowY: Int = rect.y + 10 - categoryScroll

      for (category in categories) {



         if (row.bottom > rect.y && row.y < rect.bottom) {
            context.fill(row.x, row.y, row.right, row.bottom, if (selected) palette.selected else (if (hovered) palette.hover else palette.sidebar))
            JooonUiThemeKt.drawOutline(context, row, palette.line)

            context.drawText(
               this.textRenderer,
               var10000 as Text,
               row.x + 12,
               row.y + (row.height - this.textRenderer.fontHeight) / 2,
               if (selected) palette.accent else palette.text,
return false
            )
            this.categoryHits.add(JooonConfigScreen.CategoryHit(category.key, row))
         }

         rowY += rowHeight + rowGap
      }

      this.renderCategoryScrollbar(context, rect, palette)
   }

   fun renderHeader(context: DrawContext, rect: UiRect, palette: UiPalette) {

      context.drawText(
         this.textRenderer,
         var10000 as Text,
         rect.x + (rect.width - this.textRenderer.getWidth(var10000 as StringVisitable)) / 2,
         rect.y + (rect.height - this.textRenderer.fontHeight) / 2,
         palette.text,
return false
      )
   }

   fun renderContent(context: DrawContext, rect: UiRect, palette: UiPalette, mouseX: Int, mouseY: Int, items: MutableList<ConfigNode>): String {


      if (items.isEmpty()) {
         this.maxScroll = 0
         this.smoothScroll.jump(0)
         context.drawText(
            this.textRenderer,
            Text.literal(if (isBlank(this.searchText)) "No settings available." else "No results for \"${this.searchText}\"") as Text,
            rect.x + 18,
            rect.y + 18,
            palette.mutedText,
return false
         )
return null
      } else {
         this.maxScroll = Math.max(0, this.measureItemsHeight(items, labelWidth) - rect.height + 18)

         context.enableScissor(rect.x + 1, rect.y + 1, rect.right - 1, rect.bottom - 1)
         var y: Int = rect.y + 14 - contentScroll
         var hoveredTooltip: String = null

         for (item in items) {
            if (item is ConfigCommentNode) {
               val var37: java.util.List = this.textRenderer
                  .wrapLines(Text.literal((item as ConfigCommentNode).label) as StringVisitable, rect.width - 48)

               if (y + var30 >= rect.y && y <= rect.bottom) {
                  this.renderCommentNode(context, rect, palette, y, var37, (item as ConfigCommentNode).fieldName == "autoVisitorWarningDynamic")
               }

               y += var30 + 10
            } else {
               if (item !is ConfigEntryNode) {
                  throw NoWhenBranchMatchedException()
               }

               val var10000: java.util.List = this.textRenderer
                  .wrapLines(Text.literal(this.displayLabelForEntry(item as ConfigEntryNode)) as StringVisitable, labelWidth)
                  val helperLines: java.util.List = this.helperLinesForEntry(item as ConfigEntryNode, labelWidth)





               if (row.bottom >= rect.y && row.y <= rect.bottom) {
                  context.fill(row.x, row.y, row.right, row.bottom, if (hovered) palette.hover else palette.panel)
                  JooonUiThemeKt.drawOutline(context, row, palette.lineSoft)
                  var lineY: Int = row.y + 10

                  for (var10000 in var10000) {
                     context.drawText(this.textRenderer, var10000 as OrderedText, row.x + 12, lineY, palette.text, false)
                     lineY += this.textRenderer.fontHeight + 2
                  }

                  if (!helperLines.isEmpty()) {
                     lineY++

                     for (var34 in helperLines) {
                        context.drawText(this.textRenderer, var34, row.x + 12, lineY, palette.mutedText, false)
                        lineY += this.textRenderer.fontHeight + 1
                     }
                  }

                  when (JooonConfigScreen.WhenMappings.$EnumSwitchMapping$0[(item as ConfigEntryNode).kind.ordinal()]) {
                     1 -> {
                        this.renderToggle(context, item as ConfigEntryNode, controlRect, palette)
                        this.controlHits.add(JooonConfigScreen.ControlHit(item as ConfigEntryNode, (item as ConfigEntryNode).kind, row))
                     }
                     2 -> {
                        val var35: JooonConfigScreen.SliderVisual = this.renderSlider(context, item as ConfigEntryNode, controlRect, palette)
                        this.controlHits.add(JooonConfigScreen.ControlHit(item as ConfigEntryNode, (item as ConfigEntryNode).kind, var35.trackRect))
                        this.sliderInputHits.add(JooonConfigScreen.SliderInputHit(item as ConfigEntryNode, var35.valueBox))
                     }
                     3 -> {

                        this.controlHits.add(JooonConfigScreen.ControlHit(item as ConfigEntryNode, (item as ConfigEntryNode).kind, box))
                        this.textInputHits.add(JooonConfigScreen.TextInputHit(item as ConfigEntryNode, box))
                     }
                     4 -> this.controlHits
                           .add(
                              JooonConfigScreen.ControlHit(
                                 item as ConfigEntryNode,
                                 (item as ConfigEntryNode).kind,
                                 this.renderColorButton(context, item as ConfigEntryNode, controlRect, palette)
                              )
                           )
                        5 -> this.controlHits
                           .add(
                              JooonConfigScreen.ControlHit(
                                 item as ConfigEntryNode,
                                 (item as ConfigEntryNode).kind,
                                 this.renderActionButton(context, item as ConfigEntryNode, controlRect, palette)
                              )
                           )
                        6 -> this.controlHits
                           .add(
                              JooonConfigScreen.ControlHit(
                                 item as ConfigEntryNode,
                                 (item as ConfigEntryNode).kind,
                                 this.renderEnumButton(context, item as ConfigEntryNode, controlRect, palette)
                              )
                           )
                        else -> throw NoWhenBranchMatchedException()
                  }

                  if (hovered) {
                     val var33: java.lang.CharSequence = (item as ConfigEntryNode).tooltip
                     if (var33 != null && !isBlank(var33)) {
                        hoveredTooltip = (item as ConfigEntryNode).tooltip
                     }
                  }
               }

               y += height + 10
            }
         }

         context.disableScissor()
         this.renderScrollbar(context, rect, palette)
return hoveredTooltip
      }
   }

   fun renderCommentNode(context: DrawContext, rect: UiRect, palette: UiPalette, y: Int, lines: MutableList<OrderedText>, warning: Boolean) {

      val dividerColor: java.util.Iterator = lines.iterator()
      val var10000: java.lang.Comparable
      if (!dividerColor.hasNext()) {
         var10000 = null
      } else {
         var var17: java.lang.Comparable = this.textRenderer.getWidth(dividerColor.next() as OrderedText)

         while (dividerColor.hasNext()) {
            val var20: java.lang.Comparable = this.textRenderer.getWidth(dividerColor.next() as OrderedText)
            if (var17.compareTo(var20) < 0) {
               var17 = var20
            }
         }

         var10000 = var17
      }





      context.fill(rect.x + 20, centerY + 5, leftLineRight, centerY + 6, var16)
      context.fill(rightLineLeft, centerY + 5, rect.right - 20, centerY + 6, var16)
      var var21: Int = y

      for (line in lines) {
         context.drawText(this.textRenderer, line, rect.x + (rect.width - this.textRenderer.getWidth(line)) / 2, var21, var18, false)
         var21 += this.textRenderer.fontHeight + 1
      }
   }

   fun renderToggle(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette) {



      context.fill(track.x, track.y, track.right, track.bottom, if (enabled) palette.toggleOn else palette.toggleOff)
      JooonUiThemeKt.drawOutline(context, track, palette.line)
      context.fill(knobX, track.y + 3, knobX + 18, track.bottom - 3, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(knobX, track.y + 3, 18, track.height - 6), palette.line)
   }

   fun renderSlider(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): JooonConfigScreen.SliderVisual {
      val var10000: JooonConfigScreen.SliderTextInput
      if (this.sliderTextInput != null) {
         val valueWidth: JooonConfigScreen.SliderTextInput = this.sliderTextInput
         var10000 = if (this.sliderTextInput.entry.fieldName == entry.fieldName) valueWidth else null
      } else {
         var10000 = null
      }

      run label77@{
         if (var10000 != null) {

            if (var26 != null) {
               var27 = if (!isBlank(var26)) var26 else null
               if (var27 != null) {
                  return@label77
               }
            }
         }

         var27 = entry.formatValue()
      }





      context.fill(var18.x, var18.y, var18.right, var18.bottom, palette.field)
      context.fill(var18.x, var18.y, var24, var18.bottom, palette.accentSoft)
      JooonUiThemeKt.drawOutline(context, var18, palette.line)
      context.fill(var25, var18.y - 4, var25 + 10, var18.bottom + 4, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(var25, var18.y - 4, 10, var18.height + 8), palette.line)


      context.fill(valueBox.x, valueBox.y, valueBox.right, valueBox.bottom, if (var10000 != null) palette.selected else palette.field)
      JooonUiThemeKt.drawOutline(context, valueBox, if (active) palette.accent else palette.lineSoft)
      context.drawText(
         this.textRenderer,
         Text.literal(
            if (var10000 == null)
               entry.formatValue()
return else
               (if (var10000.replaceOnNextType) "${entry.formatValue()}|" else (if (isBlank(var10000.draft)) "|" else "${var10000.draft}|"))
         ) as Text,
         valueBox.x + 8,
         valueBox.y + (valueBox.height - this.textRenderer.fontHeight) / 2,
         palette.fieldText,
return false
      )
      if (active) {
         context.drawText(
            this.textRenderer,
            Text.literal("Typing - Enter saves") as Text,
            Math.max(rect.x, valueBox.x - 88),
            valueBox.y - this.textRenderer.fontHeight - 2,
            palette.accent,
return false
         )
      }

      JooonConfigScreen.SliderVisual(var18, valueBox)
   }

   fun renderColorButton(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {



      context.fill(box.x, box.y, box.right, box.bottom, palette.field)
      JooonUiThemeKt.drawOutline(context, box, palette.lineSoft)
      context.fill(swatch.x, swatch.y, swatch.right, swatch.bottom, colorValue.packed())
      JooonUiThemeKt.drawOutline(context, swatch, palette.line)
      context.drawText(
         this.textRenderer,
         Text.literal(entry.currentColor()) as Text,
         swatch.right + 8,
         box.y + (box.height - this.textRenderer.fontHeight) / 2,
         palette.fieldText,
return false
      )
return box
   }

   fun renderTextInput(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {

      val var10000: JooonConfigScreen.TextEntryInput
      if (this.textEntryInput != null) {
         val valueText: JooonConfigScreen.TextEntryInput = this.textEntryInput
         var10000 = if (this.textEntryInput.entry.fieldName == entry.fieldName) valueText else null
      } else {
         var10000 = null
      }

      context.fill(box.x, box.y, box.right, box.bottom, if (var10000 != null) palette.selected else palette.field)
      JooonUiThemeKt.drawOutline(context, box, if (active) palette.accent else palette.lineSoft)
      context.drawText(
         this.textRenderer,
         Text.literal(
            if (var10000 == null && isBlank(entry.currentText()))
               "Leave blank"
return else
               (
                  if (var10000 == null)
                     entry.currentText()
return else
                     (if (var10000.replaceOnNextType) "${entry.currentText()}|" else (if (isBlank(var10000.draft)) "|" else "${var10000.draft}|"))
               )
         ) as Text,
         box.x + 8,
         box.y + (box.height - this.textRenderer.fontHeight) / 2,
         if (var10000 == null && isBlank(entry.currentText())) palette.mutedText else palette.fieldText,
return false
      )
      if (active) {
         context.drawText(
            this.textRenderer,
            Text.literal("Typing - Enter saves") as Text,
            Math.max(rect.x, box.x - 92),
            box.y - this.textRenderer.fontHeight - 2,
            palette.accent,
return false
         )
      }
return box
   }

   fun renderActionButton(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {

         "Move"
return else
         (if (!contains(entry.fieldName, "open", true) && !contains(entry.label, "Configure", true)) "Run" else "Open")

      this.renderFlatButton(context, button, label, palette)
return button
   }

   fun renderEnumButton(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {
      val enumValue: java.lang.Enum = entry.currentEnum()


      this.renderFlatButton(context, button, label, palette)
return button
   }

   fun renderFlatButton(context: DrawContext, rect: UiRect, label: String, palette: UiPalette) {
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

   fun renderScrollbar(context: DrawContext, rect: UiRect, palette: UiPalette) {
      if (this.maxScroll <= 0) {
         this.scrollbarTrack = UiRect(0, 0, 0, 0)
         this.scrollbarThumb = UiRect(0, 0, 0, 0)
      } else {



            track.x - 1,
            track.y + (this.smoothScroll.value.toFloat() / this.maxScroll.toFloat() * Math.max(1, track.height - thumbHeight).toFloat()).roundToInt(),
            6,
return thumbHeight
         )
         context.fill(track.x, track.y, track.right, track.bottom, palette.field)
         context.fill(thumb.x, thumb.y, thumb.right, thumb.bottom, palette.accentSoft)
         JooonUiThemeKt.drawOutline(context, UiRect(track.x - 1, track.y, 6, track.height), palette.lineSoft)
         JooonUiThemeKt.drawOutline(context, thumb, palette.line)
         this.scrollbarTrack = UiRect(track.x - 1, track.y, 6, track.height)
         this.scrollbarThumb = thumb
      }
   }

   fun renderCategoryScrollbar(context: DrawContext, rect: UiRect, palette: UiPalette) {
      if (this.maxCategoryScroll <= 0) {
         this.categoryScrollbarTrack = UiRect(0, 0, 0, 0)
         this.categoryScrollbarThumb = UiRect(0, 0, 0, 0)
      } else {



            track.x - 1,
            track.y
               + (this.categorySmoothScroll.value.toFloat() / this.maxCategoryScroll.toFloat() * Math.max(1, track.height - thumbHeight).toFloat()).roundToInt(),
            6,
return thumbHeight
         )
         context.fill(track.x, track.y, track.right, track.bottom, palette.field)
         context.fill(thumb.x, thumb.y, thumb.right, thumb.bottom, palette.accentSoft)
         JooonUiThemeKt.drawOutline(context, UiRect(track.x - 1, track.y, 6, track.height), palette.lineSoft)
         JooonUiThemeKt.drawOutline(context, thumb, palette.line)
         this.categoryScrollbarTrack = UiRect(track.x - 1, track.y, 6, track.height)
         this.categoryScrollbarThumb = thumb
      }
   }

   fun renderSidebarFooter(context: DrawContext, sidebar: UiRect, dividerY: Int, palette: UiPalette, mouseX: Int, mouseY: Int) {
      this.searchBox = UiRect(sidebar.x + 12, dividerY + 12, sidebar.width - 24, 28)


      this.settingsButton = UiRect(sidebar.x + 12, footerY, footerButtonWidth, 28)
      this.footerLink = UiRect(this.settingsButton.right + 10, footerY, footerButtonWidth, 28)


      context.fill(
         this.searchBox.x,
         this.searchBox.y,
         this.searchBox.right,
         this.searchBox.bottom,
         if (searchActive) palette.selected else (if (searchHovered) palette.hover else palette.panel)
      )
      JooonUiThemeKt.drawOutline(context, this.searchBox, if (searchActive) palette.accent else palette.mutedLine)
      context.drawText(
         this.textRenderer,
         Text.literal(
            if (isBlank(this.searchText) && searchActive)
               "|"
return else
               (if (isBlank(this.searchText)) "Search settings" else (if (searchActive) "${this.searchText}|" else this.searchText))
         ) as Text,
         this.searchBox.x + 10,
         this.searchBox.y + (this.searchBox.height - this.textRenderer.fontHeight) / 2,
         if (isBlank(this.searchText) && !searchActive) palette.mutedText else palette.text,
return false
      )
      this.renderFlatButton(context, this.settingsButton, "JR Settings", palette)
      this.renderFlatButton(context, this.footerLink, "jooon.xyz", palette)
      if (this.settingsButton.contains(mouseX, mouseY)) {
         context.fill(this.settingsButton.x, this.settingsButton.bottom - 2, this.settingsButton.right, this.settingsButton.bottom, palette.accent)
      }

      if (this.footerLink.contains(mouseX, mouseY)) {
         context.fill(this.footerLink.x, this.footerLink.bottom - 2, this.footerLink.right, this.footerLink.bottom, palette.accent)
      }
   }

   fun renderTooltip(context: DrawContext, tooltip: String, mouseX: Int, mouseY: Int, palette: UiPalette) {
      val var10000: java.util.List = this.textRenderer.wrapLines(Text.literal(tooltip) as StringVisitable, 220)
      val clampedY: java.util.Iterator = var10000.iterator()
      val var22: java.lang.Comparable
      if (!clampedY.hasNext()) {
         var22 = null
      } else {
         var var16: java.lang.Comparable = this.textRenderer.getWidth(clampedY.next() as OrderedText)

         while (clampedY.hasNext()) {
            val var19: java.lang.Comparable = this.textRenderer.getWidth(clampedY.next() as OrderedText)
            if (var16.compareTo(var19) < 0) {
               var16 = var19
            }
         }

         var22 = var16
      }



         Math.max(8, Math.min(mouseX + 14, this.width - width - 8)), Math.max(8, Math.min(mouseY + 14, this.height - height - 8)), width, height
      )
      context.fill(var17.x, var17.y, var17.right, var17.bottom, palette.tooltipBack)
      JooonUiThemeKt.drawOutline(context, var17, palette.line)
      var var20: Int = var17.y + 5

      for (var23 in var10000) {
         context.drawText(this.textRenderer, var23 as OrderedText, var17.x + 6, var20, palette.tooltipText, false)
         var20 += this.textRenderer.fontHeight + 2
      }
   }

   private fun updateSlider(entry: ConfigEntryNode, trackRect: UiRect, mouseX: Double) {
      if (trackRect.width > 0) {
         entry.setFromRatio(((mouseX - trackRect.x.toDouble()) / trackRect.width.toDouble()).coerceIn(0.0, 1.0))
         this.save()
      }
   }

   private fun updateScrollbar(mouseY: Int) {
      if (this.activeScrollbar != null) {
         val drag: JooonConfigScreen.ScrollbarDrag = this.activeScrollbar
         if (this.maxScroll > 0) {
            this.smoothScroll
               .jump(
                  ((((mouseY - this.activeScrollbar.grabOffset).coerceIn(drag.track.y, drag.track.bottom - drag.thumbHeight) - drag.track.y).toDouble()
                           / Math.max(1, this.activeScrollbar.track.height - drag.thumbHeight).toDouble()
                           * this.maxScroll.toDouble()).roundToInt()).coerceIn(0, this.maxScroll)
               )
            }
      }
   }

   private fun updateCategoryScrollbar(mouseY: Int) {
      if (this.activeCategoryScrollbar != null) {
         val drag: JooonConfigScreen.ScrollbarDrag = this.activeCategoryScrollbar
         if (this.maxCategoryScroll > 0) {
            this.categorySmoothScroll
               .jump(
                  (((
                              (mouseY - this.activeCategoryScrollbar.grabOffset).coerceIn(drag.track.y, drag.track.bottom - drag.thumbHeight)
                                 - drag.track.y
                           ).toDouble()
                           / Math.max(1, this.activeCategoryScrollbar.track.height - drag.thumbHeight).toDouble()
                           * this.maxCategoryScroll.toDouble()).roundToInt()).coerceIn(0, this.maxCategoryScroll)
               )
            }
      }
   }

   private fun beginSliderTextInput(entry: ConfigEntryNode) {
      val var10000: JooonConfigScreen.SliderTextInput
      if (this.sliderTextInput != null) {
         val var3: JooonConfigScreen.SliderTextInput = this.sliderTextInput
         var10000 = if (this.sliderTextInput.entry.fieldName == entry.fieldName) var3 else null
      } else {
         var10000 = null
      }

      this.sliderTextInput = if (var10000 != null)
         JooonConfigScreen.SliderTextInput.copy$default(var10000, null, null, true, 3, null)
return else
         JooonConfigScreen.SliderTextInput(entry, entry.formatValue(), true)
      }

   private fun beginTextEntryInput(entry: ConfigEntryNode) {
      val var10000: JooonConfigScreen.TextEntryInput
      if (this.textEntryInput != null) {
         val var3: JooonConfigScreen.TextEntryInput = this.textEntryInput
         var10000 = if (this.textEntryInput.entry.fieldName == entry.fieldName) var3 else null
      } else {
         var10000 = null
      }

      this.textEntryInput = if (var10000 != null)
         JooonConfigScreen.TextEntryInput.copy$default(var10000, null, null, true, 3, null)
return else
         JooonConfigScreen.TextEntryInput(entry, entry.currentText(), true)
      }

   private fun commitSliderTextInput() {
      if (this.sliderTextInput != null) {
         val input: JooonConfigScreen.SliderTextInput = this.sliderTextInput

         if (var10000 != null) {
            input.entry.field.set(null, var10000)
            this.save()
         }

         this.sliderTextInput = null
      }
   }

   private fun commitTextEntryInput() {
      if (this.textEntryInput != null) {
         val input: JooonConfigScreen.TextEntryInput = this.textEntryInput
         this.textEntryInput.entry.setText(trim(input.draft).toString())
         this.save()
         this.textEntryInput = null
      }
   }

   private fun parseSliderValue(entry: ConfigEntryNode, raw: String): Any? {

      if (isBlank(cleaned)) {
         return null
      } else {


         var min: JooonConfigScreen
         try {
            min = var6

            min = Result(
               if (var9 == Int::class.javaPrimitiveType || var9 == Integer::class.javaObjectType)
                  Integer.parseInt(cleaned)
return else
                  (
                     if (var9 == Long::class.javaPrimitiveType || var9 == Long::class.javaObjectType)
                        java.lang.Long.parseLong(cleaned)
return else
                        (
                           if (var9 == Float::class.javaPrimitiveType || var9 == Float::class.javaObjectType)
                              java.lang.Float.parseFloat(cleaned)
return else
                              (
                                 if (!(var9 == Double::class.javaPrimitiveType) && !(var9 == Double::class.javaObjectType))
return null
return else
                                    java.lang.Double.parseDouble(cleaned)
                              )
                        )
                  )
            )
         } catch (var10: java.lang.Throwable) {
            min = Result(ResultKt.createFailure(var10))
         }

         if (var10000 == null) {
            return null
         } else {

            min = entry.min

            if (min != null && numericValue < min) {
               return null
            } else {
               return if (var14 != null && numericValue > var14) null else var10000
            }
         }
      }
   }

   private fun acceptsSliderCharacter(entry: ConfigEntryNode, input: jooon.config.ui.JooonConfigScreen.SliderTextInput, chr: Char): Boolean {
      if (Character.isDigit(chr)) {
         return true
      } else {

val isFractional: Boolean = entry.field.getType() == Float::class.javaPrimitiveType
   || entry.field.getType() == Float::class.javaObjectType
   || entry.field.getType() == Double::class.javaPrimitiveType
   || entry.field.getType() == Double::class.javaObjectType
   var var10000: Boolean
         when (chr) {
            45 -> var10000 = currentText.length() == 0 && (entry.min == null || entry.min < 0.0)
            46 -> var10000 = isFractional && !contains$default(currentText, '.', false, 2, null)
            else -> var10000 = false
         }

         return var10000
      }
   }

   private fun applySearchPaste(rawClipboard: String) {
      val `this$iv$iv`: java.lang.CharSequence = 
         replace$default(rawClipboard.replace("\r", " "), "\n", " ", false, 4, null
      )
      val `destination$iv$iv`: Appendable = StringBuilder()
      var `index$iv$iv`: Int = 0

      for (var9 in `this$iv$iv`.length()..`index$iv$iv`) {
         val `element$iv$iv`: Char = `this$iv$iv`.charAt(`index$iv$iv`)
         if (`element$iv$iv` >= ' ') {
            `destination$iv$iv`.append(`element$iv$iv`)
         }
      }

      this.searchText = take(
         "${if (this.searchReplaceOnNextType) "" else this.searchText}${take(trim(var10000).toString(), 40)}", 40
      )
      this.searchReplaceOnNextType = false
      this.smoothScroll.jump(0)
   }

   private fun applySliderPaste(input: jooon.config.ui.JooonConfigScreen.SliderTextInput, rawClipboard: String) {
      var var10000: String = firstOrNull(lineSequence(rawClipboard)) as String
      if (var10000 != null) {
         var10000 = trim(var10000).toString()
         if (var10000 != null) {
            var10000 = take(var10000, 16)
            if (var10000 != null) {
               if (isBlank(var10000)) {
return return
               }

               if (this.parseSliderValue(input.entry, candidate) == null) {
return return
               }

               input.draft = candidate
               input.replaceOnNextType = false
return return
            }
         }
      }
   }

   private fun applyTextPaste(input: jooon.config.ui.JooonConfigScreen.TextEntryInput, rawClipboard: String) {
      val `this$iv$iv`: java.lang.CharSequence = 
         replace$default(rawClipboard.replace("\r", " "), "\n", " ", false, 4, null
      )
      val `destination$iv$iv`: Appendable = StringBuilder()
      var `index$iv$iv`: Int = 0

      for (var10 in `this$iv$iv`.length()..`index$iv$iv`) {
         val `element$iv$iv`: Char = `this$iv$iv`.charAt(`index$iv$iv`)
         if (`element$iv$iv` >= ' ') {
            `destination$iv$iv`.append(`element$iv$iv`)
         }
      }

      input.draft = take("${if (input.replaceOnNextType) "" else input.draft}${take(trim(var10000).toString(), 32)}", 32)
      input.replaceOnNextType = false
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

   private fun measureCategoryHeight(category: ConfigCategory, labelWidth: Int): Int {
      return this.measureItemsHeight(category.items, labelWidth)
   }

   private fun measureItemsHeight(items: List<ConfigNode>, labelWidth: Int): Int {
      var total: Int = 18

      for (item in items) {
         val var11: Int
         if (item is ConfigCommentNode) {
            val var10001: java.util.List = this.textRenderer
               .wrapLines(Text.literal((item as ConfigCommentNode).label) as StringVisitable, this.contentArea.width - 48)
               var11 = 18 + var10001.size() * (this.textRenderer.fontHeight + 1) + 10
         } else {
            if (item !is ConfigEntryNode) {
               throw NoWhenBranchMatchedException()
            }

            val var12: java.util.List = this.textRenderer.wrapLines(Text.literal((item as ConfigEntryNode).label) as StringVisitable, labelWidth)
            val helperLines: java.util.List = this.helperLinesForEntry(item as ConfigEntryNode, labelWidth)

            var11 = Math.max(48, 16 + var12.size() * (this.textRenderer.fontHeight + 2) + helperHeight) + 10
         }

         total += var11
      }

      return total
   }

   private fun helperLinesForEntry(entry: ConfigEntryNode, labelWidth: Int): List<OrderedText> {
      if (entry.fieldName == "clickDelayMs") {
         val var10000: java.util.List = this.textRenderer
            .wrapLines(
               Text.literal("Jooon automatically manages your stacked wool delay! Your delay should be set close to your ping, find it using /jr ping!") as StringVisitable,
return labelWidth
            )
            return var10000
      } else {
         return emptyList()
      }
   }

   private fun currentHeaderTitle(): String {
      if (!isBlank(this.searchText)) {
         return "SEARCH RESULTS"
      } else {
         var var10000: ConfigCategory = this.visibleCategory(this.selectedCategoryKey)
         if (var10000 == null) {
            var10000 = firstOrNull(this.visibleCategories()) as ConfigCategory
         }

         if (var10000 != null) {

            if (var2 != null) {

               if (var3 != null) {
                  return var3
               }
            }
         }

         return var4
      }
   }

   private fun currentContentItems(): List<ConfigNode> {
      if (!isBlank(this.searchText)) {
         return this.buildSearchItems(this.searchText)
      } else {
         var var3: java.util.List
         run label47@{

            if (var10000 != null) {
               var3 = var10000.items
               if (var3 != null) {
                  return@label47
               }
            }

            var3 = emptyList()
         }

         when (this.selectedCategoryKey.hashCode()) {
            -1078244372 -> {
               if (var2.equals("farming")) {
                  return this.augmentDynamicFarmingItems(var3)
               }
            }
            -848436598 -> {
               if (var2.equals("fishing")) {
                  return this.augmentDynamicFishingItems(var3)
               }
            }
            -196629023 -> {
               if (var2.equals("galatea")) {
                  return this.augmentDynamicGalateaItems(var3)
               }
            }
            else -> {}
         }

         return var3
      }
   }

   private fun augmentDynamicFishingItems(items: List<ConfigNode>): List<ConfigNode> {
      if (!(this.selectedCategoryKey == "fishing")) {
         return items
      } else {
         val `this$iv$iv`: java.lang.Iterable = items
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {

            if ((`element$iv$iv` as ConfigNode) !is ConfigEntryNode
               || (
                     Config.fishingMeleeAllow
                        || !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "fishingMeleeWeaponSlot")
                           && !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "fishingMeleeCps")
                           && !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "fishingMeleeOpenGui")
                           && !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "fishingMeleeAllMobs")
                  )
                  && (Config.slugFishEnabled || !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "slugFishMinWaitSec"))) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ConfigNode>
      }
   }

   private fun augmentDynamicGalateaItems(items: List<ConfigNode>): List<ConfigNode> {
      if (!(this.selectedCategoryKey == "galatea")) {
         return items
      } else {
         val `this$iv$iv`: java.lang.Iterable = items
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if (Config.stridersurferFishingMacroAutoPetSwap
               || (`element$iv$iv` as ConfigNode) !is ConfigEntryNode
               || !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "stridersurferFishingMacroKillPetName")
                  && !(((`element$iv$iv` as ConfigNode) as ConfigEntryNode).fieldName == "stridersurferFishingMacroRecastPetName")) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<ConfigNode>
      }
   }

   private fun augmentDynamicFarmingItems(items: List<ConfigNode>): List<ConfigNode> {
      if (!(this.selectedCategoryKey == "farming")) {
         return items
      } else {
         val augmented: java.util.List = ArrayList()


         for (node in items) {
            augmented.add(node)
            if (!inserted.element && node is ConfigCommentNode && (node as ConfigCommentNode).fieldName == "farmingSplitter") {
               augmentDynamicFarmingItems$addDynamicNodes(inserted, augmented)
            }
         }

         if (!inserted.element) {
            augmentDynamicFarmingItems$addDynamicNodes(inserted, augmented)
         }

         return augmented
      }
   }

   private fun displayLabelForEntry(entry: ConfigEntryNode): String {
      return if (entry.fieldName == "autoVisitorConfigButton") AutoVisitor.dynamicSetupRowLabel() else entry.label
   }

   private fun buildSearchItems(rawQuery: String): List<ConfigNode> {

      if (isBlank(query)) {
         return emptyList()
      } else {
         val results: java.util.List = ArrayList()
         var syntheticIndex: Int = 0

         for (category in this.visibleCategories()) {
            val sections: java.util.List = ArrayList()

            val currentEntries: java.util.List = ArrayList()

            for (section in category.items) {
               if (section is ConfigCommentNode) {
                  buildSearchItems$flushSection(currentComment, currentEntries, this, category, query, sections)
                  currentComment.element = section
               } else {
                  if (section !is ConfigEntryNode) {
                     throw NoWhenBranchMatchedException()
                  }

                  currentEntries.add(section)
               }
            }

            buildSearchItems$flushSection(currentComment, currentEntries, this, category, query, sections)

            for (var17 in sections) {

               var13.append(var17.categoryLabel)
               if (!equals(var17.sectionLabel, var17.categoryLabel, true)) {
                  var13.append(" • ")
                  var13.append(var17.sectionLabel)
               }

               results.add(ConfigCommentNode("search_result_${syntheticIndex++}", category.key, true, var10000))
               addAll(results, var17.entries)
            }
         }

         return results
      }
   }

   private fun matchesSearch(text: String?, normalizedQuery: String): Boolean {
      if (text == null || isBlank(text)) {
         return false
      } else {

         if (isBlank(var16)) {
            return false
         } else if (contains$default(var16, normalizedQuery, false, 2, null)) {
            return true
         } else {


            if (!isBlank(compactQuery) && contains$default(compactHaystack, compactQuery, false, 2, null)) {
               return true
            } else {
               val `this$iv$iv`: java.lang.Iterable = split$default(normalizedQuery, charArrayOf(' '), false, 0, 6, null)
               val `element$iv`: java.util.Collection = ArrayList()

               for (`element$iv$iv` in `this$iv$iv`) {
                  if (!isBlank(`element$iv$iv` as String)) {
                     `element$iv`.add(`element$iv$iv`)
                  }
               }

               val queryTokens: java.util.List = `element$iv` as java.util.List
               if (!(`element$iv` as java.util.List).isEmpty()) {
                  val var18: java.lang.Iterable = queryTokens
                  var var24: Boolean
                  if (queryTokens is java.util.Collection && (queryTokens as java.util.Collection).isEmpty()) {
                     var24 = true
                  } else {
                     val var20: java.util.Iterator = var18.iterator()

                     while (true) {
                        if (!var20.hasNext()) {
                           var24 = true
break
                        }

                        if (!contains$default(var16, var20.next() as String, false, 2, null)) {
                           var24 = false
break
                        }
                     }
                  }

                  if (var24) {
                     return true
                  }
               }

               return false
            }
         }
      }
   }

   private fun normalizeForSearch(raw: String): String {

            Regex("[^a-zA-Z0-9]+").replace(replace$default(Regex("([a-z0-9])([A-Z])").replace(raw, "$1 $2"), '_', ' ', false, 4, null), " ")
         )
         .toString()
         .toLowerCase(Locale.ROOT)
         return Regex("\\s+").replace(var10000, " ")
   }

   private fun cleanSectionLabel(label: String): String {
      return trim(replace$default(label.replace(">", ""), "<", "", false, 4, null)).toString()
   }

   private fun openExternalLink(url: String) {
      var osName: JooonConfigScreen = this

      var fallbackCommand: JooonConfigScreen
      try {
         fallbackCommand = osName
         fallbackCommand = Result(URI.create(url))
      } catch (var10: java.lang.Throwable) {
         fallbackCommand = Result(ResultKt.createFailure(var10))
      }

      if (var10000 != null) {

         osName = this

         try {
            run label77@{
               fallbackCommand = osName
               if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                  Desktop.getDesktop().browse(uri)
                  if (true) {
                     var29 = true
                     return@label77
                  }
               }

               var29 = false
            }

            fallbackCommand = Result(var29)
         } catch (var11: java.lang.Throwable) {
            fallbackCommand = Result(ResultKt.createFailure(var11))
         }

         if (!(if (Result.isFailure/* $VF was: isFailure-impl */(fallbackCommand)) false else fallbackCommand) as Boolean) {


            fallbackCommand = if (contains$default(var31, "win", false, 2, null))
               listOf(arrayOf("rundll32", "url.dll,FileProtocolHandler", var10000.toString()))
return else
               (
                  if (contains$default(var31, "mac", false, 2, null))
                     listOf(arrayOf("open", var10000.toString()))
return else
                     listOf(arrayOf("xdg-open", var10000.toString()))
               )


            try {
               var var26: JooonConfigScreen = var25
               var26 = Result(ProcessBuilder(fallbackCommand).start())
            } catch (var9: java.lang.Throwable) {
               val `this24lambda_u2434`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var9))
            }
         }
      }
   }

   private fun save() {
      JooonConfigManager.write(this.definition.modId)
   }

   
   fun `augmentDynamicFarmingItems$addDynamicNodes`(inserted: BooleanRef, augmented: MutableList<ConfigNode>) {
      if (!inserted.element) {
         inserted.element = true

         var3.append("Auto Visitor: ")
         if (PersistentState.autoVisitorPadPlaced) {
            var3.append("Pad ")
            var3.append(PersistentState.autoVisitorPadX.toInt())
            var3.append(", ")
            var3.append(PersistentState.autoVisitorPadY.toInt())
            var3.append(", ")
            var3.append(PersistentState.autoVisitorPadZ.toInt())
         } else {
            var3.append("Pad not placed")
         }

         var3.append(" | Accept all: ")
         var3.append(if (PersistentState.autoVisitorAcceptAll) "ON" else "OFF")
         var3.append(" | Max spend: ")
         var3.append(AutoVisitor.formatCompactValue(PersistentState.autoVisitorMaxSpendCoins))
         var3.append(" | Min XP: ")
         var3.append(AutoVisitor.formatCompactValue(PersistentState.autoVisitorMinFarmingXp))

         augmented.add(ConfigCommentNode("autoVisitorSummaryDynamic", "farming", true, var10000))
         if (AutoVisitor.isPadMissingWarningRequired()) {
            augmented.add(
               ConfigCommentNode(
                  "autoVisitorWarningDynamic",
                  "farming",
                  true,
                  "You have Auto Visitor enabled, but have not configured the Auto Visitor pad yet! Click \"Run\" to set it up now."
               )
            )
         }
      }
   }

   
   fun `buildSearchItems$flushSection`(
      currentComment: ObjectRef<ConfigCommentNode>,
      currentEntries: MutableList<ConfigEntryNode>,
      `this$0`: JooonConfigScreen,
      category: ConfigCategory,
      query: String,
      sections: MutableList<JooonConfigScreen.SearchSection>
   ) {
      if (currentComment.element != null || !currentEntries.isEmpty()) {

         var var27: Boolean = currentComment.element as ConfigCommentNode != null
            && `this$0`.matchesSearch((currentComment.element as ConfigCommentNode).label, query)
            val sectionLabel: java.lang.Iterable = currentEntries
         if (currentEntries is java.util.Collection && (currentEntries as java.util.Collection).isEmpty()) {
            var27 = false
         } else {
            val var20: java.util.Iterator = sectionLabel.iterator()

            while (true) {
               if (!var20.hasNext()) {
                  var27 = false
break
               }

               if (`this$0`.matchesSearch(entry.label, query) || `this$0`.matchesSearch(entry.tooltip, query) || `this$0`.matchesSearch(entry.fieldName, query)
                  )
                {
                  var27 = true
break
               }
            }
         }

         if (categoryMatch || var27 || var27) {
            run label112@{

               if (currentComment.element as ConfigCommentNode != null) {

                  if (var21 != null) {

                     if (var22 != null) {

                        if (var23 != null) {
                           var30 = var23
                           return@label112
                        }
                     }
                  }
               }

               var30 = if (var29 != null) var29.label else category.displayName
            }

            sections.add(JooonConfigScreen.SearchSection(category.displayName, var30, toList(currentEntries)))
         }

         currentComment.element = null
         currentEntries.clear()
      }
   }

   private data class CategoryHit(categoryKey: String, rect: UiRect) {
      val categoryKey: String
      val rect: UiRect

      init {
         this.categoryKey = categoryKey
         this.rect = rect
      }

      public operator fun component1(): String {
         return this.categoryKey
      }

      public operator fun component2(): UiRect {
         return this.rect
      }

      fun copy(categoryKey: String = this.categoryKey, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.CategoryHit {
         return JooonConfigScreen.CategoryHit(categoryKey, rect)
      }

      override fun toString(): String {
         return "CategoryHit(categoryKey=${this.categoryKey}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return this.categoryKey.hashCode() * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.CategoryHit
               && this.categoryKey == (other as JooonConfigScreen.CategoryHit).categoryKey
               && this.rect == (other as JooonConfigScreen.CategoryHit).rect
            }
      }
   }

   private data class ControlHit(entry: ConfigEntryNode, kind: ConfigControlKind, rect: UiRect) {
      val entry: ConfigEntryNode
      val kind: ConfigControlKind
      val rect: UiRect

      init {
         this.entry = entry
         this.kind = kind
         this.rect = rect
      }

      public operator fun component1(): ConfigEntryNode {
         return this.entry
      }

      public operator fun component2(): ConfigControlKind {
         return this.kind
      }

      public operator fun component3(): UiRect {
         return this.rect
      }

      fun copy(entry: ConfigEntryNode = this.entry, kind: ConfigControlKind = this.kind, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.ControlHit {
         return JooonConfigScreen.ControlHit(entry, kind, rect)
      }

      override fun toString(): String {
         return "ControlHit(entry=${this.entry}, kind=${this.kind}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return (this.entry.hashCode() * 31 + this.kind.hashCode()) * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.ControlHit
               && this.entry == (other as JooonConfigScreen.ControlHit).entry
               && this.kind === (other as JooonConfigScreen.ControlHit).kind
               && this.rect == (other as JooonConfigScreen.ControlHit).rect
            }
      }
   }

   private data class ScrollbarDrag(track: UiRect, thumbHeight: Int, grabOffset: Int) {
      val track: UiRect
      val thumbHeight: Int
      val grabOffset: Int

      init {
         this.track = track
         this.thumbHeight = thumbHeight
         this.grabOffset = grabOffset
      }

      public operator fun component1(): UiRect {
         return this.track
      }

      public operator fun component2(): Int {
         return this.thumbHeight
      }

      public operator fun component3(): Int {
         return this.grabOffset
      }

      fun copy(track: UiRect = this.track, thumbHeight: Int = this.thumbHeight, grabOffset: Int = this.grabOffset): jooon.config.ui.JooonConfigScreen.ScrollbarDrag {
         return JooonConfigScreen.ScrollbarDrag(track, thumbHeight, grabOffset)
      }

      override fun toString(): String {
         return "ScrollbarDrag(track=${this.track}, thumbHeight=${this.thumbHeight}, grabOffset=${this.grabOffset})"
      }

      override fun hashCode(): Int {
         return (this.track.hashCode() * 31 + Integer.hashCode(this.thumbHeight)) * 31 + Integer.hashCode(this.grabOffset)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.ScrollbarDrag
               && this.track == (other as JooonConfigScreen.ScrollbarDrag).track
               && this.thumbHeight == (other as JooonConfigScreen.ScrollbarDrag).thumbHeight
               && this.grabOffset == (other as JooonConfigScreen.ScrollbarDrag).grabOffset
            }
      }
   }

   private data class SearchSection(categoryLabel: String, sectionLabel: String, entries: List<ConfigEntryNode>) {
      val categoryLabel: String
      val sectionLabel: String
      val entries: List<ConfigEntryNode>

      init {
         this.categoryLabel = categoryLabel
         this.sectionLabel = sectionLabel
         this.entries = entries
      }

      public operator fun component1(): String {
         return this.categoryLabel
      }

      public operator fun component2(): String {
         return this.sectionLabel
      }

      public operator fun component3(): List<ConfigEntryNode> {
         return this.entries
      }

      fun copy(categoryLabel: String = this.categoryLabel, sectionLabel: String = this.sectionLabel, entries: List<ConfigEntryNode> = this.entries): jooon.config.ui.JooonConfigScreen.SearchSection {
         return JooonConfigScreen.SearchSection(categoryLabel, sectionLabel, entries)
      }

      override fun toString(): String {
         return "SearchSection(categoryLabel=${this.categoryLabel}, sectionLabel=${this.sectionLabel}, entries=${this.entries})"
      }

      override fun hashCode(): Int {
         return (this.categoryLabel.hashCode() * 31 + this.sectionLabel.hashCode()) * 31 + this.entries.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.SearchSection
               && this.categoryLabel == (other as JooonConfigScreen.SearchSection).categoryLabel
               && this.sectionLabel == (other as JooonConfigScreen.SearchSection).sectionLabel
               && this.entries == (other as JooonConfigScreen.SearchSection).entries
            }
      }
   }

   private data class SliderDrag(entry: ConfigEntryNode, trackRect: UiRect) {
      val entry: ConfigEntryNode
      val trackRect: UiRect

      init {
         this.entry = entry
         this.trackRect = trackRect
      }

      public operator fun component1(): ConfigEntryNode {
         return this.entry
      }

      public operator fun component2(): UiRect {
         return this.trackRect
      }

      fun copy(entry: ConfigEntryNode = this.entry, trackRect: UiRect = this.trackRect): jooon.config.ui.JooonConfigScreen.SliderDrag {
         return JooonConfigScreen.SliderDrag(entry, trackRect)
      }

      override fun toString(): String {
         return "SliderDrag(entry=${this.entry}, trackRect=${this.trackRect})"
      }

      override fun hashCode(): Int {
         return this.entry.hashCode() * 31 + this.trackRect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.SliderDrag
               && this.entry == (other as JooonConfigScreen.SliderDrag).entry
               && this.trackRect == (other as JooonConfigScreen.SliderDrag).trackRect
            }
      }
   }

   private data class SliderInputHit(entry: ConfigEntryNode, rect: UiRect) {
      val entry: ConfigEntryNode
      val rect: UiRect

      init {
         this.entry = entry
         this.rect = rect
      }

      public operator fun component1(): ConfigEntryNode {
         return this.entry
      }

      public operator fun component2(): UiRect {
         return this.rect
      }

      fun copy(entry: ConfigEntryNode = this.entry, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.SliderInputHit {
         return JooonConfigScreen.SliderInputHit(entry, rect)
      }

      override fun toString(): String {
         return "SliderInputHit(entry=${this.entry}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return this.entry.hashCode() * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.SliderInputHit
               && this.entry == (other as JooonConfigScreen.SliderInputHit).entry
               && this.rect == (other as JooonConfigScreen.SliderInputHit).rect
            }
      }
   }

   private data class SliderTextInput(entry: ConfigEntryNode, draft: String, replaceOnNextType: Boolean) {
      val entry: ConfigEntryNode
      var draft: String
      var replaceOnNextType: Boolean

      init {
         this.entry = entry
         this.draft = draft
         this.replaceOnNextType = replaceOnNextType
      }

      public operator fun component1(): ConfigEntryNode {
         return this.entry
      }

      public operator fun component2(): String {
         return this.draft
      }

      public operator fun component3(): Boolean {
         return this.replaceOnNextType
      }

      fun copy(entry: ConfigEntryNode = this.entry, draft: String = this.draft, replaceOnNextType: Boolean = this.replaceOnNextType): jooon.config.ui.JooonConfigScreen.SliderTextInput {
         return JooonConfigScreen.SliderTextInput(entry, draft, replaceOnNextType)
      }

      override fun toString(): String {
         return "SliderTextInput(entry=${this.entry}, draft=${this.draft}, replaceOnNextType=${this.replaceOnNextType})"
      }

      override fun hashCode(): Int {
         return (this.entry.hashCode() * 31 + this.draft.hashCode()) * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.SliderTextInput
               && this.entry == (other as JooonConfigScreen.SliderTextInput).entry
               && this.draft == (other as JooonConfigScreen.SliderTextInput).draft
               && this.replaceOnNextType == (other as JooonConfigScreen.SliderTextInput).replaceOnNextType
            }
      }
   }

   private data class SliderVisual(trackRect: UiRect, valueBox: UiRect) {
      val trackRect: UiRect
      val valueBox: UiRect

      init {
         this.trackRect = trackRect
         this.valueBox = valueBox
      }

      public operator fun component1(): UiRect {
         return this.trackRect
      }

      public operator fun component2(): UiRect {
         return this.valueBox
      }

      fun copy(trackRect: UiRect = this.trackRect, valueBox: UiRect = this.valueBox): jooon.config.ui.JooonConfigScreen.SliderVisual {
         return JooonConfigScreen.SliderVisual(trackRect, valueBox)
      }

      override fun toString(): String {
         return "SliderVisual(trackRect=${this.trackRect}, valueBox=${this.valueBox})"
      }

      override fun hashCode(): Int {
         return this.trackRect.hashCode() * 31 + this.valueBox.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.SliderVisual
               && this.trackRect == (other as JooonConfigScreen.SliderVisual).trackRect
               && this.valueBox == (other as JooonConfigScreen.SliderVisual).valueBox
            }
      }
   }

   private data class TextEntryInput(entry: ConfigEntryNode, draft: String, replaceOnNextType: Boolean) {
      val entry: ConfigEntryNode
      var draft: String
      var replaceOnNextType: Boolean

      init {
         this.entry = entry
         this.draft = draft
         this.replaceOnNextType = replaceOnNextType
      }

      public operator fun component1(): ConfigEntryNode {
         return this.entry
      }

      public operator fun component2(): String {
         return this.draft
      }

      public operator fun component3(): Boolean {
         return this.replaceOnNextType
      }

      fun copy(entry: ConfigEntryNode = this.entry, draft: String = this.draft, replaceOnNextType: Boolean = this.replaceOnNextType): jooon.config.ui.JooonConfigScreen.TextEntryInput {
         return JooonConfigScreen.TextEntryInput(entry, draft, replaceOnNextType)
      }

      override fun toString(): String {
         return "TextEntryInput(entry=${this.entry}, draft=${this.draft}, replaceOnNextType=${this.replaceOnNextType})"
      }

      override fun hashCode(): Int {
         return (this.entry.hashCode() * 31 + this.draft.hashCode()) * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.TextEntryInput
               && this.entry == (other as JooonConfigScreen.TextEntryInput).entry
               && this.draft == (other as JooonConfigScreen.TextEntryInput).draft
               && this.replaceOnNextType == (other as JooonConfigScreen.TextEntryInput).replaceOnNextType
            }
      }
   }

   private data class TextInputHit(entry: ConfigEntryNode, rect: UiRect) {
      val entry: ConfigEntryNode
      val rect: UiRect

      init {
         this.entry = entry
         this.rect = rect
      }

      public operator fun component1(): ConfigEntryNode {
         return this.entry
      }

      public operator fun component2(): UiRect {
         return this.rect
      }

      fun copy(entry: ConfigEntryNode = this.entry, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.TextInputHit {
         return JooonConfigScreen.TextInputHit(entry, rect)
      }

      override fun toString(): String {
         return "TextInputHit(entry=${this.entry}, rect=${this.rect})"
      }

      override fun hashCode(): Int {
         return this.entry.hashCode() * 31 + this.rect.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is JooonConfigScreen.TextInputHit
               && this.entry == (other as JooonConfigScreen.TextInputHit).entry
               && this.rect == (other as JooonConfigScreen.TextInputHit).rect
            }
      }
   }
}
