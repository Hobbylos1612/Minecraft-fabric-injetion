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
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.math.MathKt
import net.minecraft.class_5481
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

@SourceDebugExtension(["SMAP\nJooonConfigScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonConfigScreen.kt\njooon/config/ui/JooonConfigScreen\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,1534:1\n819#2:1535\n847#2,2:1536\n288#2,2:1538\n288#2,2:1540\n288#2,2:1542\n819#2:1557\n847#2,2:1558\n819#2:1560\n847#2,2:1561\n766#2:1563\n857#2,2:1564\n1726#2,3:1566\n1747#2,3:1569\n1#3:1544\n429#4:1545\n502#4,5:1546\n429#4:1551\n502#4,5:1552\n*S KotlinDebug\n*F\n+ 1 JooonConfigScreen.kt\njooon/config/ui/JooonConfigScreen\n*L\n74#1:1535\n74#1:1536,2\n77#1:1538,2\n159#1:1540,2\n163#1:1542,2\n1283#1:1557\n1283#1:1558,2\n1302#1:1560\n1302#1:1561,2\n1454#1:1563\n1454#1:1564,2\n1455#1:1566,3\n1393#1:1569,3\n1180#1:1545\n1180#1:1546,5\n1208#1:1551\n1208#1:1552,5\n*E\n"])
internal class JooonConfigScreen : Screen {
   private Screen parentScreen;
   private final val definition: ConfigDefinition
   private Identifier logoTexture;
   private final val transition: JooonScreenTransition
   private final var selectedCategoryKey: String
   private final val smoothScroll: SmoothScroll
   private final var maxScroll: Int
   private final val categorySmoothScroll: SmoothScroll
   private final var maxCategoryScroll: Int
   private final var contentArea: UiRect
   private final var categoryArea: UiRect
   private final var footerLink: UiRect
   private final var settingsButton: UiRect
   private final var searchBox: UiRect
   private final var scrollbarTrack: UiRect
   private final var scrollbarThumb: UiRect
   private final var categoryScrollbarTrack: UiRect
   private final var categoryScrollbarThumb: UiRect
   private final val categoryHits: MutableList<jooon.config.ui.JooonConfigScreen.CategoryHit>
   private final val controlHits: MutableList<jooon.config.ui.JooonConfigScreen.ControlHit>
   private final val sliderInputHits: MutableList<jooon.config.ui.JooonConfigScreen.SliderInputHit>
   private final val textInputHits: MutableList<jooon.config.ui.JooonConfigScreen.TextInputHit>
   private final var activeSlider: jooon.config.ui.JooonConfigScreen.SliderDrag?
   private final var activeScrollbar: jooon.config.ui.JooonConfigScreen.ScrollbarDrag?
   private final var activeCategoryScrollbar: jooon.config.ui.JooonConfigScreen.ScrollbarDrag?
   private final var sliderTextInput: jooon.config.ui.JooonConfigScreen.SliderTextInput?
   private final var textEntryInput: jooon.config.ui.JooonConfigScreen.TextEntryInput?
   private final var searchText: String
   private final var searchInputActive: Boolean
   private final var searchReplaceOnNextType: Boolean

   fun JooonConfigScreen(parentScreen: Screen?, definition: ConfigDefinition) {
      super(Text.method_43470(definition.title) as Text)
      this.parentScreen = parentScreen
      this.definition = definition
      val var10001: Identifier = Identifier.method_60655(this.definition.modId, "icon.png")
      this.logoTexture = var10001
      this.transition = JooonScreenTransition(0L, 0L, 0.0F, 7, null)
      val var3: ConfigCategory = CollectionsKt.firstOrNull(this.visibleCategories()) as ConfigCategory
      var var4: java.lang.String = if (var3 != null) var3.key else null
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
      JooonUiSettings.INSTANCE.ensureLoaded()
      if (StringsKt.isBlank(this.selectedCategoryKey) || this.visibleCategory(this.selectedCategoryKey) == null) {
         val var10001: ConfigCategory = CollectionsKt.firstOrNull(this.visibleCategories()) as ConfigCategory
         var var1: java.lang.String = if (var10001 != null) var10001.key else null
         if (var1 == null) {
            var1 = ""
         }

         this.selectedCategoryKey = var1
      }

      super.method_25426()
   }

   private fun visibleCategories(): List<ConfigCategory> {
      val `$this$filterNotTo$iv$iv`: java.lang.Iterable = this.definition.categories
      val `destination$iv$iv`: java.util.Collection = ArrayList()

      for (`element$iv$iv` in `$this$filterNotTo$iv$iv`) {
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
               continue
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
         val contentMouseY: Int = this.transition.transformedMouseY(mouseY)
         val palette: UiPalette = JooonUiThemeKt.currentPalette()
         val outer: UiRect = UiRect(18, 18, this.field_22789 - 36, this.field_22790 - 36)
         val sidebar: UiRect = UiRect(
            outer.x + 14, outer.y + 14, Math.min(210, Math.max(176, MathKt.roundToInt((float)outer.width * 0.2F))) - 14, outer.height - 14 * 2
         )
         val logoBox: UiRect = UiRect(sidebar.x, sidebar.y, sidebar.width, Math.min(148, Math.max(120, sidebar.width - 34)))
         val footerDividerY: Int = sidebar.bottom - 96
         val categoriesStartY: Int = logoBox.bottom + 12
         val categoriesBox: UiRect = UiRect(sidebar.x, categoriesStartY, sidebar.width, footerDividerY - categoriesStartY - 12)
         this.categoryArea = categoriesBox
         val header: UiRect = UiRect(sidebar.right + 10, outer.y + 14, outer.right - sidebar.right - 10 - 14, 52)
         this.contentArea = UiRect(header.x, header.bottom + 12, header.width, outer.bottom - header.bottom - 28)
         val contentItems: java.util.List = this.currentContentItems()
         context.method_25294(
            0,
            0,
            this.field_22789,
            this.field_22790,
            palette.backdrop and 16777215 or RangesKt.coerceIn(
               MathKt.roundToInt(this.transition.currentAlpha() * (float)(palette.backdrop ushr 24 and 255)), 0, 255
            ) shl 24
         )
         this.transition.push(context)
         context.method_25294(outer.x, outer.y, outer.right, outer.bottom, palette.paper)
         JooonUiThemeKt.drawOutline(context, outer, palette.line)
         context.method_25294(sidebar.x, sidebar.y, sidebar.right, sidebar.bottom, palette.sidebar)
         JooonUiThemeKt.drawOutline(context, sidebar, palette.line)
         context.method_25294(header.x, header.y, header.right, header.bottom, palette.header)
         JooonUiThemeKt.drawOutline(context, header, palette.line)
         context.method_25294(this.contentArea.x, this.contentArea.y, this.contentArea.right, this.contentArea.bottom, palette.panel)
         JooonUiThemeKt.drawOutline(context, this.contentArea, palette.line)
         context.method_25294(sidebar.x, footerDividerY, sidebar.right, footerDividerY + 1, palette.line)
         this.renderLogo(context, logoBox, palette)
         this.renderCategories(context, categoriesBox, palette, mouseX, contentMouseY)
         this.renderHeader(context, header, palette)
         val hoveredTooltip: java.lang.String = this.renderContent(context, this.contentArea, palette, mouseX, contentMouseY, contentItems)
         this.renderSidebarFooter(context, sidebar, footerDividerY, palette, mouseX, contentMouseY)
         if (this.sliderTextInput == null && this.textEntryInput == null && hoveredTooltip != null && !StringsKt.isBlank(hoveredTooltip)) {
            this.renderTooltip(context, hoveredTooltip, mouseX, contentMouseY, palette)
         }

         super.method_25394(context, mouseX, mouseY, delta)
         this.transition.pop(context)
      }
   }

   fun method_25402(event: Click, handled: Boolean): Boolean {
      if (this.transition.isClosing) {
         true
      } else {
         val mouseX: Int = (int)event.comp_4798()
         val mouseY: Int = this.transition.transformedMouseY((int)event.comp_4799())
         if (event.method_74245() != 0) {
            super.method_25402(event, handled)
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
                     continue
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
                  val var21: Any = var20.next()
                  if (!(var21 as JooonConfigScreen.TextInputHit).rect.contains(mouseX, mouseY)) {
                     continue
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
               true
            } else if (this.settingsButton.contains(mouseX, mouseY)) {
               this.transition.beginClose({ 
                  if (`this$0`.field_22787 != null) {
                     `this$0`.field_22787.method_1507(JooonSettingsScreen(`this$0`))
                  }

                  Unit.INSTANCE
               })
            } else if (this.footerLink.contains(mouseX, mouseY)) {
               this.openExternalLink("https://jooon.xyz")
               true
            } else if (clickedSliderInput != null) {
               this.beginSliderTextInput(clickedSliderInput.entry)
               true
            } else if (var13 != null) {
               this.beginTextEntryInput(var13.entry)
               true
            } else if (this.categoryScrollbarThumb.contains(mouseX, mouseY)) {
               this.activeCategoryScrollbar = JooonConfigScreen.ScrollbarDrag(
                  this.categoryScrollbarTrack, this.categoryScrollbarThumb.height, mouseY - this.categoryScrollbarThumb.y
               )
               true
            } else if (this.categoryScrollbarTrack.contains(mouseX, mouseY) && this.maxCategoryScroll > 0) {
               this.activeCategoryScrollbar = JooonConfigScreen.ScrollbarDrag(
                  this.categoryScrollbarTrack, this.categoryScrollbarThumb.height, this.categoryScrollbarThumb.height / 2
               )
               this.updateCategoryScrollbar(mouseY)
               true
            } else {
               for (var18 in this.categoryHits) {
                  if (var18.rect.contains(mouseX, mouseY)) {
                     this.selectedCategoryKey = var18.categoryKey
                     this.smoothScroll.jump(0)
                     this.sliderTextInput = null
                     this.textEntryInput = null
                     if (!StringsKt.isBlank(this.searchText)) {
                        this.searchText = ""
                        this.searchInputActive = false
                        this.searchReplaceOnNextType = false
                     }

                     true
                  }
               }

               if (this.scrollbarThumb.contains(mouseX, mouseY)) {
                  this.activeScrollbar = JooonConfigScreen.ScrollbarDrag(this.scrollbarTrack, this.scrollbarThumb.height, mouseY - this.scrollbarThumb.y)
                  true
               } else if (this.scrollbarTrack.contains(mouseX, mouseY) && this.maxScroll > 0) {
                  this.activeScrollbar = JooonConfigScreen.ScrollbarDrag(this.scrollbarTrack, this.scrollbarThumb.height, this.scrollbarThumb.height / 2)
                  this.updateScrollbar(mouseY)
                  true
               } else {
                  for (var19 in this.controlHits) {
                     if (var19.rect.contains(mouseX, mouseY)) {
                        when (JooonConfigScreen.WhenMappings.$EnumSwitchMapping$0[var19.kind.ordinal()]) {
                           1 -> {
                              var19.entry.toggleBoolean()
                              this.save()
                              true
                           }
                           2 -> {
                              this.activeSlider = JooonConfigScreen.SliderDrag(var19.entry, var19.rect)
                              this.updateSlider(var19.entry, var19.rect, event.comp_4798())
                              true
                           }
                           3 -> {
                              this.beginTextEntryInput(var19.entry)
                              true
                           }
                           4 -> {
                              this.transition.beginClose({ 
                                 if (`this$0`.field_22787 != null) {
                                    `this$0`.field_22787.method_1507(JooonColorPickerScreen(`this$0`, `this$0`.definition, `$hit`.entry))
                                 }

                                 Unit.INSTANCE
                              })
                           }
                           5 -> {
                              var19.entry.triggerAction()
                              this.save()
                              true
                           }
                           6 -> {
                              ConfigEntryNode.cycleEnum$default(var19.entry, 0, 1, null)
                              this.save()
                              true
                           }
                           else -> throw NoWhenBranchMatchedException()
                        }
                     }
                  }

                  super.method_25402(event, handled)
               }
            }
         }
      }
   }

   fun method_25403(event: Click, dx: Double, dy: Double): Boolean {
      if (this.transition.isClosing) {
         true
      } else if (this.activeSlider != null) {
         val it: JooonConfigScreen.SliderDrag = this.activeSlider
         this.updateSlider(this.activeSlider.entry, it.trackRect, event.comp_4798())
         true
      } else if (this.activeCategoryScrollbar != null) {
         this.updateCategoryScrollbar((int)event.comp_4799())
         true
      } else if (this.activeScrollbar != null) {
         this.updateScrollbar((int)event.comp_4799())
         true
      } else {
         super.method_25403(event, dx, dy)
      }
   }

   fun method_25406(event: Click): Boolean {
      this.activeSlider = null
      this.activeCategoryScrollbar = null
      this.activeScrollbar = null
      super.method_25406(event)
   }

   fun method_25401(mouseX: Double, mouseY: Double, horizontal: Double, vertical: Double): Boolean {
      if (this.transition.isClosing) {
         true
      } else {
         val contentMouseY: Int = this.transition.transformedMouseY((int)mouseY)
         if (this.categoryArea.contains((int)mouseX, (int)mouseY) && this.maxCategoryScroll > 0) {
            this.categorySmoothScroll.addDelta(MathKt.roundToInt(-vertical * 36.0), this.maxCategoryScroll)
            true
         } else if (this.categoryArea.contains((int)mouseX, contentMouseY) && this.maxCategoryScroll > 0) {
            this.categorySmoothScroll.addDelta(MathKt.roundToInt(-vertical * 36.0), this.maxCategoryScroll)
            true
         } else if (this.contentArea.contains((int)mouseX, (int)mouseY) && this.maxScroll > 0) {
            this.smoothScroll.addDelta(MathKt.roundToInt(-vertical * 40.0), this.maxScroll)
            true
         } else if (this.contentArea.contains((int)mouseX, contentMouseY) && this.maxScroll > 0) {
            this.smoothScroll.addDelta(MathKt.roundToInt(-vertical * 40.0), this.maxScroll)
            true
         } else {
            super.method_25401(mouseX, mouseY, horizontal, vertical)
         }
      }
   }

   fun method_25404(event: KeyInput): Boolean {
      if (this.transition.isClosing) {
         true
      } else {
         label157@
         if (!this.searchInputActive) {
            val input: JooonConfigScreen.SliderTextInput = this.sliderTextInput
            label147@
            if (this.sliderTextInput == null) {
               val textInput: JooonConfigScreen.TextEntryInput = this.textEntryInput
               label140@
               if (this.textEntryInput == null) {
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
                           textInput.replaceOnNextType = textInput.draft.length() > 0
                           true
                        }
                        86 -> {
                           this.applyTextPaste(textInput, this.readClipboard())
                           true
                        }
                        else -> {}
                     }
                  }

                  when (event.comp_4795()) {
                     256 -> {
                        this.textEntryInput = null
                        true
                     }
                     257, 335 -> {
                        this.commitTextEntryInput()
                        true
                     }
                     259 -> {
                        if (textInput.draft.length() > 0) {
                           textInput.draft = StringsKt.dropLast(textInput.draft, 1)
                        }

                        textInput.replaceOnNextType = false
                        true
                     }
                     else -> break@label140
                  }
               }
            } else {
               if (this.isControlShortcut(event)) {
                  when (event.comp_4795()) {
                     65 -> {
                        input.replaceOnNextType = input.draft.length() > 0
                        true
                     }
                     86 -> {
                        this.applySliderPaste(input, this.readClipboard())
                        true
                     }
                     else -> {}
                  }
               }

               when (event.comp_4795()) {
                  256 -> {
                     this.sliderTextInput = null
                     true
                  }
                  257, 335 -> {
                     this.commitSliderTextInput()
                     true
                  }
                  259 -> {
                     if (input.draft.length() > 0) {
                        input.draft = StringsKt.dropLast(input.draft, 1)
                     }

                     input.replaceOnNextType = false
                     true
                  }
                  else -> break@label147
               }
            }
         } else {
            if (this.isControlShortcut(event)) {
               when (event.comp_4795()) {
                  65 -> {
                     this.searchReplaceOnNextType = this.searchText.length() > 0
                     true
                  }
                  86 -> {
                     this.applySearchPaste(this.readClipboard())
                     true
                  }
                  else -> {}
               }
            }

            when (event.comp_4795()) {
               256 -> {
                  if (!StringsKt.isBlank(this.searchText)) {
                     this.searchText = ""
                     this.smoothScroll.jump(0)
                  } else {
                     this.searchInputActive = false
                  }

                  this.searchReplaceOnNextType = false
                  true
               }
               257, 335 -> true
               259 -> {
                  if (this.searchReplaceOnNextType) {
                     this.searchText = ""
                  } else if (this.searchText.length() > 0) {
                     this.searchText = StringsKt.dropLast(this.searchText, 1)
                  }

                  this.searchReplaceOnNextType = false
                  this.smoothScroll.jump(0)
                  true
               }
               else -> break@label157
            }
         }
      }
   }

   fun method_25400(characterEvent: CharInput): Boolean {
      if (this.transition.isClosing) {
         true
      } else if (this.searchInputActive) {
         val var14: java.lang.String = characterEvent.method_74226()
         if (var14.length() != 1) {
            true
         } else {
            val var9: Char = var14.charAt(0)
            if (var9 >= ' ' && this.searchText.length() < 40) {
               this.searchText = StringsKt.take("${if (this.searchReplaceOnNextType) "" else this.searchText}$var9", 40)
               this.searchReplaceOnNextType = false
               this.smoothScroll.jump(0)
               true
            } else {
               true
            }
         }
      } else {
         val input: JooonConfigScreen.SliderTextInput = this.sliderTextInput
         if (this.sliderTextInput != null) {
            val var13: java.lang.String = characterEvent.method_74226()
            if (var13.length() != 1) {
               true
            } else {
               val var10: Char = var13.charAt(0)
               if (!this.acceptsSliderCharacter(input.entry, input, var10)) {
                  true
               } else {
                  input.draft = StringsKt.take("${if (input.replaceOnNextType) "" else input.draft}$var10", 16)
                  input.replaceOnNextType = false
                  true
               }
            }
         } else if (this.textEntryInput == null) {
            super.method_25400(characterEvent)
         } else {
            val textInput: JooonConfigScreen.TextEntryInput = this.textEntryInput
            val var10000: java.lang.String = characterEvent.method_74226()
            if (var10000.length() != 1) {
               true
            } else {
               val chr: Char = var10000.charAt(0)
               if (chr >= ' ' && (textInput.draft.length() < 32 || textInput.replaceOnNextType)) {
                  textInput.draft = StringsKt.take("${if (textInput.replaceOnNextType) "" else textInput.draft}$chr", 32)
                  textInput.replaceOnNextType = false
                  true
               } else {
                  true
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
         if (`this$0`.field_22787 != null) {
            `this$0`.field_22787.method_1507(`this$0`.parentScreen)
         }

         Unit.INSTANCE
      })
   }

   fun method_25421(): Boolean {
      false
   }

   fun renderLogo(context: DrawContext, rect: UiRect, palette: UiPalette) {
      context.method_25294(rect.x, rect.y, rect.right, rect.bottom, palette.paper)
      JooonUiThemeKt.drawOutline(context, rect, palette.line)
      val logoSize: Int = RangesKt.coerceAtLeast(Math.min(rect.width - 16, rect.height - 16), 24)
      context.method_25302(
         RenderPipelines.field_56883,
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
         128
      )
   }

   fun renderCategories(context: DrawContext, rect: UiRect, palette: UiPalette, mouseX: Int, mouseY: Int) {
      val categories: java.util.List = this.visibleCategories()
      val rowHeight: Int = 32
      val rowGap: Int = 8
      this.maxCategoryScroll = Math.max(0, categories.size() * 32 + Math.max(0, categories.size() - 1) * 8 + 10 + 10 - rect.height)
      val categoryScroll: Int = this.categorySmoothScroll.tick(this.maxCategoryScroll)
      val scrollbarReserved: Int = if (this.maxCategoryScroll > 0) 14 else 0
      val rowWidth: Int = rect.width - 20 - scrollbarReserved
      var rowY: Int = rect.y + 10 - categoryScroll

      for (category in categories) {
         val row: UiRect = UiRect(rect.x + 10, rowY, rowWidth, rowHeight)
         val hovered: Boolean = row.contains(mouseX, mouseY)
         val selected: Boolean = category.key == this.selectedCategoryKey
         if (row.bottom > rect.y && row.y < rect.bottom) {
            context.method_25294(row.x, row.y, row.right, row.bottom, if (selected) palette.selected else (if (hovered) palette.hover else palette.sidebar))
            JooonUiThemeKt.drawOutline(context, row, palette.line)
            val var10000: MutableText = Text.method_43470(category.displayName)
            context.method_51439(
               this.field_22793,
               var10000 as Text,
               row.x + 12,
               row.y + (row.height - this.field_22793.field_2000) / 2,
               if (selected) palette.accent else palette.text,
               false
            )
            this.categoryHits.add(JooonConfigScreen.CategoryHit(category.key, row))
         }

         rowY += rowHeight + rowGap
      }

      this.renderCategoryScrollbar(context, rect, palette)
   }

   fun renderHeader(context: DrawContext, rect: UiRect, palette: UiPalette) {
      val var10000: MutableText = Text.method_43470(this.currentHeaderTitle())
      context.method_51439(
         this.field_22793,
         var10000 as Text,
         rect.x + (rect.width - this.field_22793.method_27525(var10000 as StringVisitable)) / 2,
         rect.y + (rect.height - this.field_22793.field_2000) / 2,
         palette.text,
         false
      )
   }

   fun renderContent(context: DrawContext, rect: UiRect, palette: UiPalette, mouseX: Int, mouseY: Int, items: MutableList<ConfigNode>): java.lang.String {
      val controlWidth: Int = Math.min(230, Math.max(166, rect.width / 3))
      val labelWidth: Int = rect.width - controlWidth - 54
      if (items.isEmpty()) {
         this.maxScroll = 0
         this.smoothScroll.jump(0)
         context.method_51439(
            this.field_22793,
            Text.method_43470(if (StringsKt.isBlank(this.searchText)) "No settings available." else "No results for \"${this.searchText}\"") as Text,
            rect.x + 18,
            rect.y + 18,
            palette.mutedText,
            false
         )
         null
      } else {
         this.maxScroll = Math.max(0, this.measureItemsHeight(items, labelWidth) - rect.height + 18)
         val contentScroll: Int = this.smoothScroll.tick(this.maxScroll)
         context.method_44379(rect.x + 1, rect.y + 1, rect.right - 1, rect.bottom - 1)
         var y: Int = rect.y + 14 - contentScroll
         var hoveredTooltip: java.lang.String = null

         for (item in items) {
            if (item is ConfigCommentNode) {
               val var37: java.util.List = this.field_22793
                  .method_1728(Text.method_43470((item as ConfigCommentNode).label) as StringVisitable, rect.width - 48)
                  val var30: Int = 18 + var37.size() * (this.field_22793.field_2000 + 1)
               if (y + var30 >= rect.y && y <= rect.bottom) {
                  this.renderCommentNode(context, rect, palette, y, var37, (item as ConfigCommentNode).fieldName == "autoVisitorWarningDynamic")
               }

               y += var30 + 10
            } else {
               if (item !is ConfigEntryNode) {
                  throw NoWhenBranchMatchedException()
               }

               val var10000: java.util.List = this.field_22793
                  .method_1728(Text.method_43470(this.displayLabelForEntry(item as ConfigEntryNode)) as StringVisitable, labelWidth)
                  val helperLines: java.util.List = this.helperLinesForEntry(item as ConfigEntryNode, labelWidth)
               val helperHeight: Int = if (helperLines.isEmpty()) 0 else 3 + helperLines.size() * (this.field_22793.field_2000 + 1)
               val height: Int = Math.max(48, 16 + var10000.size() * (this.field_22793.field_2000 + 2) + helperHeight)
               val row: UiRect = UiRect(rect.x + 12, y, rect.width - 24, height)
               val controlRect: UiRect = UiRect(row.right - controlWidth - 12, row.y + 10, controlWidth, row.height - 20)
               val hovered: Boolean = row.contains(mouseX, mouseY)
               if (row.bottom >= rect.y && row.y <= rect.bottom) {
                  context.method_25294(row.x, row.y, row.right, row.bottom, if (hovered) palette.hover else palette.panel)
                  JooonUiThemeKt.drawOutline(context, row, palette.lineSoft)
                  var lineY: Int = row.y + 10

                  for (var10000 in var10000) {
                     context.method_51430(this.field_22793, var10000 as OrderedText, row.x + 12, lineY, palette.text, false)
                     lineY += this.field_22793.field_2000 + 2
                  }

                  if (!helperLines.isEmpty()) {
                     lineY++

                     for (var34 in helperLines) {
                        context.method_51430(this.field_22793, var34, row.x + 12, lineY, palette.mutedText, false)
                        lineY += this.field_22793.field_2000 + 1
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
                        val box: UiRect = this.renderTextInput(context, item as ConfigEntryNode, controlRect, palette)
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
                     if (var33 != null && !StringsKt.isBlank(var33)) {
                        hoveredTooltip = (item as ConfigEntryNode).tooltip
                     }
                  }
               }

               y += height + 10
            }
         }

         context.method_44380()
         this.renderScrollbar(context, rect, palette)
         hoveredTooltip
      }
   }

   fun renderCommentNode(context: DrawContext, rect: UiRect, palette: UiPalette, y: Int, lines: MutableList<OrderedText>, warning: Boolean) {
      val centerY: Int = y + 2
      val dividerColor: java.util.Iterator = lines.iterator()
      val var10000: java.lang.Comparable
      if (!dividerColor.hasNext()) {
         var10000 = null
      } else {
         var var17: java.lang.Comparable = this.field_22793.method_30880(dividerColor.next() as OrderedText)

         while (dividerColor.hasNext()) {
            val var20: java.lang.Comparable = this.field_22793.method_30880(dividerColor.next() as OrderedText)
            if (var17.compareTo(var20) < 0) {
               var17 = var20
            }
         }

         var10000 = var17
      }

      val textWidth: Int = if (var10000 as Int != null) var10000 as Int else 0
      val leftLineRight: Int = rect.x + (rect.width - textWidth) / 2 - 10
      val rightLineLeft: Int = rect.x + (rect.width + textWidth) / 2 + 10
      val var16: Int = if (warning) -4626328 else palette.mutedLine
      val var18: Int = if (warning) -29299 else palette.mutedText
      context.method_25294(rect.x + 20, centerY + 5, leftLineRight, centerY + 6, var16)
      context.method_25294(rightLineLeft, centerY + 5, rect.right - 20, centerY + 6, var16)
      var var21: Int = y

      for (line in lines) {
         context.method_51430(this.field_22793, line, rect.x + (rect.width - this.field_22793.method_30880(line)) / 2, var21, var18, false)
         var21 += this.field_22793.field_2000 + 1
      }
   }

   fun renderToggle(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette) {
      val track: UiRect = UiRect(rect.right - 56, rect.y + (rect.height - 24) / 2, 56, 24)
      val enabled: Boolean = entry.currentBoolean()
      val knobX: Int = if (enabled) track.right - 18 - 3 else track.x + 3
      context.method_25294(track.x, track.y, track.right, track.bottom, if (enabled) palette.toggleOn else palette.toggleOff)
      JooonUiThemeKt.drawOutline(context, track, palette.line)
      context.method_25294(knobX, track.y + 3, knobX + 18, track.bottom - 3, palette.field)
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
            val var26: java.lang.String = var10000.draft
            if (var26 != null) {
               var27 = if (!StringsKt.isBlank(var26)) var26 else null
               if (var27 != null) {
                  return@label77
               }
            }
         }

         var27 = entry.formatValue()
      }

      val var16: Int = Math.max(54, this.field_22793.method_1727((if (StringsKt.isBlank(var27)) "0000" else var27) as java.lang.String) + 16)
      val var18: UiRect = UiRect(rect.x, rect.y + rect.height / 2 - 3, rect.width - var16 - 12, 6)
      val var22: Float = (float)entry.currentRatio()
      val var24: Int = var18.x + MathKt.roundToInt((float)var18.width * var22)
      val var25: Int = MathKt.roundToInt((float)var18.x + (float)(var18.width - 10) * var22)
      context.method_25294(var18.x, var18.y, var18.right, var18.bottom, palette.field)
      context.method_25294(var18.x, var18.y, var24, var18.bottom, palette.accentSoft)
      JooonUiThemeKt.drawOutline(context, var18, palette.line)
      context.method_25294(var25, var18.y - 4, var25 + 10, var18.bottom + 4, palette.field)
      JooonUiThemeKt.drawOutline(context, UiRect(var25, var18.y - 4, 10, var18.height + 8), palette.line)
      val valueBox: UiRect = UiRect(var18.right + 12, rect.y + (rect.height - 22) / 2, var16, 22)
      val active: Boolean = var10000 != null
      context.method_25294(valueBox.x, valueBox.y, valueBox.right, valueBox.bottom, if (var10000 != null) palette.selected else palette.field)
      JooonUiThemeKt.drawOutline(context, valueBox, if (active) palette.accent else palette.lineSoft)
      context.method_51439(
         this.field_22793,
         Text.method_43470(
            if (var10000 == null)
               entry.formatValue()
               else
               (if (var10000.replaceOnNextType) "${entry.formatValue()}|" else (if (StringsKt.isBlank(var10000.draft)) "|" else "${var10000.draft}|"))
         ) as Text,
         valueBox.x + 8,
         valueBox.y + (valueBox.height - this.field_22793.field_2000) / 2,
         palette.fieldText,
         false
      )
      if (active) {
         context.method_51439(
            this.field_22793,
            Text.method_43470("Typing - Enter saves") as Text,
            Math.max(rect.x, valueBox.x - 88),
            valueBox.y - this.field_22793.field_2000 - 2,
            palette.accent,
            false
         )
      }

      JooonConfigScreen.SliderVisual(var18, valueBox)
   }

   fun renderColorButton(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {
      val box: UiRect = UiRect(rect.right - 148, rect.y + (rect.height - 26) / 2, 148, 26)
      val swatch: UiRect = UiRect(box.x + 5, box.y + 5, 16, 16)
      val colorValue: RgbColor = JooonColorSupportKt.parseHexColor(entry.currentColor())
      context.method_25294(box.x, box.y, box.right, box.bottom, palette.field)
      JooonUiThemeKt.drawOutline(context, box, palette.lineSoft)
      context.method_25294(swatch.x, swatch.y, swatch.right, swatch.bottom, colorValue.packed())
      JooonUiThemeKt.drawOutline(context, swatch, palette.line)
      context.method_51439(
         this.field_22793,
         Text.method_43470(entry.currentColor()) as Text,
         swatch.right + 8,
         box.y + (box.height - this.field_22793.field_2000) / 2,
         palette.fieldText,
         false
      )
      box
   }

   fun renderTextInput(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {
      val box: UiRect = UiRect(rect.right - 168, rect.y + (rect.height - 24) / 2, 168, 24)
      val var10000: JooonConfigScreen.TextEntryInput
      if (this.textEntryInput != null) {
         val valueText: JooonConfigScreen.TextEntryInput = this.textEntryInput
         var10000 = if (this.textEntryInput.entry.fieldName == entry.fieldName) valueText else null
      } else {
         var10000 = null
      }

      val active: Boolean = var10000 != null
      context.method_25294(box.x, box.y, box.right, box.bottom, if (var10000 != null) palette.selected else palette.field)
      JooonUiThemeKt.drawOutline(context, box, if (active) palette.accent else palette.lineSoft)
      context.method_51439(
         this.field_22793,
         Text.method_43470(
            if (var10000 == null && StringsKt.isBlank(entry.currentText()))
               "Leave blank"
               else
               (
                  if (var10000 == null)
                     entry.currentText()
                     else
                     (if (var10000.replaceOnNextType) "${entry.currentText()}|" else (if (StringsKt.isBlank(var10000.draft)) "|" else "${var10000.draft}|"))
               )
         ) as Text,
         box.x + 8,
         box.y + (box.height - this.field_22793.field_2000) / 2,
         if (var10000 == null && StringsKt.isBlank(entry.currentText())) palette.mutedText else palette.fieldText,
         false
      )
      if (active) {
         context.method_51439(
            this.field_22793,
            Text.method_43470("Typing - Enter saves") as Text,
            Math.max(rect.x, box.x - 92),
            box.y - this.field_22793.field_2000 - 2,
            palette.accent,
            false
         )
      }

      box
   }

   fun renderActionButton(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {
      val label: java.lang.String = if (StringsKt.contains(entry.fieldName, "move", true))
         "Move"
         else
         (if (!StringsKt.contains(entry.fieldName, "open", true) && !StringsKt.contains(entry.label, "Configure", true)) "Run" else "Open")
         val button: UiRect = UiRect(rect.right - 92, rect.y + (rect.height - 26) / 2, 92, 26)
      this.renderFlatButton(context, button, label, palette)
      button
   }

   fun renderEnumButton(context: DrawContext, entry: ConfigEntryNode, rect: UiRect, palette: UiPalette): UiRect {
      val enumValue: java.lang.Enum = entry.currentEnum()
      val label: java.lang.String = if (enumValue != null) this.definition.enumLabel(enumValue) else entry.formatValue()
      val button: UiRect = UiRect(rect.right - 126, rect.y + (rect.height - 26) / 2, 126, 26)
      this.renderFlatButton(context, button, label, palette)
      button
   }

   fun renderFlatButton(context: DrawContext, rect: UiRect, label: java.lang.String, palette: UiPalette) {
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

   fun renderScrollbar(context: DrawContext, rect: UiRect, palette: UiPalette) {
      if (this.maxScroll <= 0) {
         this.scrollbarTrack = UiRect(0, 0, 0, 0)
         this.scrollbarThumb = UiRect(0, 0, 0, 0)
      } else {
         val track: UiRect = UiRect(rect.right - 8, rect.y + 10, 4, rect.height - 20)
         val thumbHeight: Int = Math.max(24, MathKt.roundToInt((float)track.height * ((float)rect.height / (float)(rect.height + this.maxScroll))))
         val thumb: UiRect = UiRect(
            track.x - 1,
            track.y + MathKt.roundToInt((float)this.smoothScroll.value / (float)this.maxScroll * (float)Math.max(1, track.height - thumbHeight)),
            6,
            thumbHeight
         )
         context.method_25294(track.x, track.y, track.right, track.bottom, palette.field)
         context.method_25294(thumb.x, thumb.y, thumb.right, thumb.bottom, palette.accentSoft)
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
         val track: UiRect = UiRect(rect.right - 8, rect.y + 10, 4, rect.height - 20)
         val thumbHeight: Int = Math.max(24, MathKt.roundToInt((float)track.height * ((float)rect.height / (float)(rect.height + this.maxCategoryScroll))))
         val thumb: UiRect = UiRect(
            track.x - 1,
            track.y
               + MathKt.roundToInt((float)this.categorySmoothScroll.value / (float)this.maxCategoryScroll * (float)Math.max(1, track.height - thumbHeight)),
            6,
            thumbHeight
         )
         context.method_25294(track.x, track.y, track.right, track.bottom, palette.field)
         context.method_25294(thumb.x, thumb.y, thumb.right, thumb.bottom, palette.accentSoft)
         JooonUiThemeKt.drawOutline(context, UiRect(track.x - 1, track.y, 6, track.height), palette.lineSoft)
         JooonUiThemeKt.drawOutline(context, thumb, palette.line)
         this.categoryScrollbarTrack = UiRect(track.x - 1, track.y, 6, track.height)
         this.categoryScrollbarThumb = thumb
      }
   }

   fun renderSidebarFooter(context: DrawContext, sidebar: UiRect, dividerY: Int, palette: UiPalette, mouseX: Int, mouseY: Int) {
      this.searchBox = UiRect(sidebar.x + 12, dividerY + 12, sidebar.width - 24, 28)
      val footerY: Int = this.searchBox.bottom + 14
      val footerButtonWidth: Int = (sidebar.width - 24 - 10) / 2
      this.settingsButton = UiRect(sidebar.x + 12, footerY, footerButtonWidth, 28)
      this.footerLink = UiRect(this.settingsButton.right + 10, footerY, footerButtonWidth, 28)
      val searchHovered: Boolean = this.searchBox.contains(mouseX, mouseY)
      val searchActive: Boolean = this.searchInputActive
      context.method_25294(
         this.searchBox.x,
         this.searchBox.y,
         this.searchBox.right,
         this.searchBox.bottom,
         if (searchActive) palette.selected else (if (searchHovered) palette.hover else palette.panel)
      )
      JooonUiThemeKt.drawOutline(context, this.searchBox, if (searchActive) palette.accent else palette.mutedLine)
      context.method_51439(
         this.field_22793,
         Text.method_43470(
            if (StringsKt.isBlank(this.searchText) && searchActive)
               "|"
               else
               (if (StringsKt.isBlank(this.searchText)) "Search settings" else (if (searchActive) "${this.searchText}|" else this.searchText))
         ) as Text,
         this.searchBox.x + 10,
         this.searchBox.y + (this.searchBox.height - this.field_22793.field_2000) / 2,
         if (StringsKt.isBlank(this.searchText) && !searchActive) palette.mutedText else palette.text,
         false
      )
      this.renderFlatButton(context, this.settingsButton, "JR Settings", palette)
      this.renderFlatButton(context, this.footerLink, "jooon.xyz", palette)
      if (this.settingsButton.contains(mouseX, mouseY)) {
         context.method_25294(this.settingsButton.x, this.settingsButton.bottom - 2, this.settingsButton.right, this.settingsButton.bottom, palette.accent)
      }

      if (this.footerLink.contains(mouseX, mouseY)) {
         context.method_25294(this.footerLink.x, this.footerLink.bottom - 2, this.footerLink.right, this.footerLink.bottom, palette.accent)
      }
   }

   fun renderTooltip(context: DrawContext, tooltip: java.lang.String, mouseX: Int, mouseY: Int, palette: UiPalette) {
      val var10000: java.util.List = this.field_22793.method_1728(Text.method_43470(tooltip) as StringVisitable, 220)
      val clampedY: java.util.Iterator = var10000.iterator()
      val var22: java.lang.Comparable
      if (!clampedY.hasNext()) {
         var22 = null
      } else {
         var var16: java.lang.Comparable = this.field_22793.method_30880(clampedY.next() as OrderedText)

         while (clampedY.hasNext()) {
            val var19: java.lang.Comparable = this.field_22793.method_30880(clampedY.next() as OrderedText)
            if (var16.compareTo(var19) < 0) {
               var16 = var19
            }
         }

         var22 = var16
      }

      val width: Int = (if (var22 as Int != null) var22 as Int else 0) + 12
      val height: Int = var10000.size() * (this.field_22793.field_2000 + 2) + 10
      val var17: UiRect = UiRect(
         Math.max(8, Math.min(mouseX + 14, this.field_22789 - width - 8)), Math.max(8, Math.min(mouseY + 14, this.field_22790 - height - 8)), width, height
      )
      context.method_25294(var17.x, var17.y, var17.right, var17.bottom, palette.tooltipBack)
      JooonUiThemeKt.drawOutline(context, var17, palette.line)
      var var20: Int = var17.y + 5

      for (var23 in var10000) {
         context.method_51430(this.field_22793, var23 as OrderedText, var17.x + 6, var20, palette.tooltipText, false)
         var20 += this.field_22793.field_2000 + 2
      }
   }

   private fun updateSlider(entry: ConfigEntryNode, trackRect: UiRect, mouseX: Double) {
      if (trackRect.width > 0) {
         entry.setFromRatio(RangesKt.coerceIn((mouseX - (double)trackRect.x) / (double)trackRect.width, 0.0, 1.0))
         this.save()
      }
   }

   private fun updateScrollbar(mouseY: Int) {
      if (this.activeScrollbar != null) {
         val drag: JooonConfigScreen.ScrollbarDrag = this.activeScrollbar
         if (this.maxScroll > 0) {
            this.smoothScroll
               .jump(
                  RangesKt.coerceIn(
                     MathKt.roundToInt(
                        (double)(RangesKt.coerceIn(mouseY - this.activeScrollbar.grabOffset, drag.track.y, drag.track.bottom - drag.thumbHeight) - drag.track.y)
                           / (double)Math.max(1, this.activeScrollbar.track.height - drag.thumbHeight)
                           * (double)this.maxScroll
                     ),
                     0,
                     this.maxScroll
                  )
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
                  RangesKt.coerceIn(
                     MathKt.roundToInt(
                        (double)(
                              RangesKt.coerceIn(mouseY - this.activeCategoryScrollbar.grabOffset, drag.track.y, drag.track.bottom - drag.thumbHeight)
                                 - drag.track.y
                           )
                           / (double)Math.max(1, this.activeCategoryScrollbar.track.height - drag.thumbHeight)
                           * (double)this.maxCategoryScroll
                     ),
                     0,
                     this.maxCategoryScroll
                  )
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
         else
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
         else
         JooonConfigScreen.TextEntryInput(entry, entry.currentText(), true)
      }

   private fun commitSliderTextInput() {
      if (this.sliderTextInput != null) {
         val input: JooonConfigScreen.SliderTextInput = this.sliderTextInput
         val var10000: Any = this.parseSliderValue(this.sliderTextInput.entry, input.draft)
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
         this.textEntryInput.entry.setText(StringsKt.trim(input.draft).toString())
         this.save()
         this.textEntryInput = null
      }
   }

   private fun parseSliderValue(entry: ConfigEntryNode, raw: String): Any? {
      val cleaned: java.lang.String = StringsKt.trim(raw).toString()
      if (StringsKt.isBlank(cleaned)) {
         return null
      } else {
         val var6: JooonConfigScreen = this

         var min: JooonConfigScreen
         try {
            min = var6
            val var9: Class = entry.field.getType()
            min = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(
               if (var9 == Int::class.javaPrimitiveType || var9 == Integer::class.javaObjectType)
                  Integer.parseInt(cleaned)
                  else
                  (
                     if (var9 == java.lang.Long::class.javaPrimitiveType || var9 == java.lang.Long::class.javaObjectType)
                        java.lang.Long.parseLong(cleaned)
                        else
                        (
                           if (var9 == java.lang.Float::class.javaPrimitiveType || var9 == java.lang.Float::class.javaObjectType)
                              java.lang.Float.parseFloat(cleaned)
                              else
                              (
                                 if (!(var9 == java.lang.Double::class.javaPrimitiveType) && !(var9 == java.lang.Double::class.javaObjectType))
                                    null
                                    else
                                    java.lang.Double.parseDouble(cleaned)
                              )
                        )
                  )
            )
         } catch (var10: java.lang.Throwable) {
            min = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var10))
         }

         val var10000: Any = if (Result.isFailure_impl/* $VF was: isFailure-impl */(min)) null else min
         if (var10000 == null) {
            return null
         } else {
            val numericValue: Double = (var10000 as java.lang.Number).doubleValue()
            min = entry.min
            val var14: java.lang.Double = entry.max
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
         val currentText: java.lang.String = if (input.replaceOnNextType) "" else input.draft
val isFractional: Boolean = entry.field.getType() == java.lang.Float::class.javaPrimitiveType
   || entry.field.getType() == java.lang.Float::class.javaObjectType
   || entry.field.getType() == java.lang.Double::class.javaPrimitiveType
   || entry.field.getType() == java.lang.Double::class.javaObjectType
   var var10000: Boolean
         when (chr) {
            45 -> var10000 = currentText.length() == 0 && (entry.min == null || entry.min < 0.0)
            46 -> var10000 = isFractional && !StringsKt.contains$default(currentText, '.', false, 2, null)
            else -> var10000 = false
         }

         return var10000
      }
   }

   private fun applySearchPaste(rawClipboard: String) {
      val `$this$filterTo$iv$iv`: java.lang.CharSequence = StringsKt.replace$default(
         StringsKt.replace$default(rawClipboard, "\r", " ", false, 4, null), "\n", " ", false, 4, null
      )
      val `destination$iv$iv`: Appendable = StringBuilder()
      var `index$iv$iv`: Int = 0

      for (var9 in `$this$filterTo$iv$iv`.length()..`index$iv$iv`) {
         val `element$iv$iv`: Char = `$this$filterTo$iv$iv`.charAt(`index$iv$iv`)
         if (`element$iv$iv` >= ' ') {
            `destination$iv$iv`.append(`element$iv$iv`)
         }
      }

      val var10000: java.lang.String = (`destination$iv$iv` as StringBuilder).toString()
      this.searchText = StringsKt.take(
         "${if (this.searchReplaceOnNextType) "" else this.searchText}${StringsKt.take(StringsKt.trim(var10000).toString(), 40)}", 40
      )
      this.searchReplaceOnNextType = false
      this.smoothScroll.jump(0)
   }

   private fun applySliderPaste(input: jooon.config.ui.JooonConfigScreen.SliderTextInput, rawClipboard: String) {
      var var10000: java.lang.String = SequencesKt.firstOrNull(StringsKt.lineSequence(rawClipboard)) as java.lang.String
      if (var10000 != null) {
         var10000 = StringsKt.trim(var10000).toString()
         if (var10000 != null) {
            var10000 = StringsKt.take(var10000, 16)
            if (var10000 != null) {
               if (StringsKt.isBlank(var10000)) {
                  return
               }

               val candidate: java.lang.String = StringsKt.take("${if (input.replaceOnNextType) "" else input.draft}$var10000", 16)
               if (this.parseSliderValue(input.entry, candidate) == null) {
                  return
               }

               input.draft = candidate
               input.replaceOnNextType = false
               return
            }
         }
      }
   }

   private fun applyTextPaste(input: jooon.config.ui.JooonConfigScreen.TextEntryInput, rawClipboard: String) {
      val `$this$filterTo$iv$iv`: java.lang.CharSequence = StringsKt.replace$default(
         StringsKt.replace$default(rawClipboard, "\r", " ", false, 4, null), "\n", " ", false, 4, null
      )
      val `destination$iv$iv`: Appendable = StringBuilder()
      var `index$iv$iv`: Int = 0

      for (var10 in `$this$filterTo$iv$iv`.length()..`index$iv$iv`) {
         val `element$iv$iv`: Char = `$this$filterTo$iv$iv`.charAt(`index$iv$iv`)
         if (`element$iv$iv` >= ' ') {
            `destination$iv$iv`.append(`element$iv$iv`)
         }
      }

      val var10000: java.lang.String = (`destination$iv$iv` as StringBuilder).toString()
      input.draft = StringsKt.take("${if (input.replaceOnNextType) "" else input.draft}${StringsKt.take(StringsKt.trim(var10000).toString(), 32)}", 32)
      input.replaceOnNextType = false
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

   private fun measureCategoryHeight(category: ConfigCategory, labelWidth: Int): Int {
      return this.measureItemsHeight(category.items, labelWidth)
   }

   private fun measureItemsHeight(items: List<ConfigNode>, labelWidth: Int): Int {
      var total: Int = 18

      for (item in items) {
         val var11: Int
         if (item is ConfigCommentNode) {
            val var10001: java.util.List = this.field_22793
               .method_1728(Text.method_43470((item as ConfigCommentNode).label) as StringVisitable, this.contentArea.width - 48)
               var11 = 18 + var10001.size() * (this.field_22793.field_2000 + 1) + 10
         } else {
            if (item !is ConfigEntryNode) {
               throw NoWhenBranchMatchedException()
            }

            val var12: java.util.List = this.field_22793.method_1728(Text.method_43470((item as ConfigEntryNode).label) as StringVisitable, labelWidth)
            val helperLines: java.util.List = this.helperLinesForEntry(item as ConfigEntryNode, labelWidth)
            val helperHeight: Int = if (helperLines.isEmpty()) 0 else 3 + helperLines.size() * (this.field_22793.field_2000 + 1)
            var11 = Math.max(48, 16 + var12.size() * (this.field_22793.field_2000 + 2) + helperHeight) + 10
         }

         total += var11
      }

      return total
   }

   private fun helperLinesForEntry(entry: ConfigEntryNode, labelWidth: Int): List<class_5481> {
      if (entry.fieldName == "clickDelayMs") {
         val var10000: java.util.List = this.field_22793
            .method_1728(
               Text.method_43470("Jooon automatically manages your stacked wool delay! Your delay should be set close to your ping, find it using /jr ping!") as StringVisitable,
               labelWidth
            )
            return var10000
      } else {
         return CollectionsKt.emptyList()
      }
   }

   private fun currentHeaderTitle(): String {
      if (!StringsKt.isBlank(this.searchText)) {
         return "SEARCH RESULTS"
      } else {
         var var10000: ConfigCategory = this.visibleCategory(this.selectedCategoryKey)
         if (var10000 == null) {
            var10000 = CollectionsKt.firstOrNull(this.visibleCategories()) as ConfigCategory
         }

         if (var10000 != null) {
            val var2: java.lang.String = var10000.displayName
            if (var2 != null) {
               val var3: java.lang.String = var2.toUpperCase(Locale.ROOT)
               if (var3 != null) {
                  return var3
               }
            }
         }

         val var4: java.lang.String = this.definition.title.toUpperCase(Locale.ROOT)
         return var4
      }
   }

   private fun currentContentItems(): List<ConfigNode> {
      if (!StringsKt.isBlank(this.searchText)) {
         return this.buildSearchItems(this.searchText)
      } else {
         var var3: java.util.List
         run label47@{
            val var10000: ConfigCategory = this.visibleCategory(this.selectedCategoryKey)
            if (var10000 != null) {
               var3 = var10000.items
               if (var3 != null) {
                  return@label47
               }
            }

            var3 = CollectionsKt.emptyList()
         }

         val var2: java.lang.String = this.selectedCategoryKey
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
         val `$this$filterNotTo$iv$iv`: java.lang.Iterable = items
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterNotTo$iv$iv`) {
            val node: ConfigNode = `element$iv$iv` as ConfigNode
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
         val `$this$filterNotTo$iv$iv`: java.lang.Iterable = items
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterNotTo$iv$iv`) {
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
         val inserted: BooleanRef = BooleanRef()

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
      return if (entry.fieldName == "autoVisitorConfigButton") AutoVisitor.INSTANCE.dynamicSetupRowLabel() else entry.label
   }

   private fun buildSearchItems(rawQuery: String): List<ConfigNode> {
      val query: java.lang.String = this.normalizeForSearch(rawQuery)
      if (StringsKt.isBlank(query)) {
         return CollectionsKt.emptyList()
      } else {
         val results: java.util.List = ArrayList()
         var syntheticIndex: Int = 0

         for (category in this.visibleCategories()) {
            val sections: java.util.List = ArrayList()
            val currentComment: ObjectRef = ObjectRef()
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
               val var13: StringBuilder = StringBuilder()
               var13.append(var17.categoryLabel)
               if (!StringsKt.equals(var17.sectionLabel, var17.categoryLabel, true)) {
                  var13.append(" • ")
                  var13.append(var17.sectionLabel)
               }

               val var10000: java.lang.String = var13.toString()
               results.add(ConfigCommentNode("search_result_${syntheticIndex++}", category.key, true, var10000))
               CollectionsKt.addAll(results, var17.entries)
            }
         }

         return results
      }
   }

   private fun matchesSearch(text: String?, normalizedQuery: String): Boolean {
      if (text == null || StringsKt.isBlank(text)) {
         return false
      } else {
         val var16: java.lang.String = this.normalizeForSearch(text)
         if (StringsKt.isBlank(var16)) {
            return false
         } else if (StringsKt.contains$default(var16, normalizedQuery, false, 2, null)) {
            return true
         } else {
            val compactHaystack: java.lang.String = StringsKt.replace$default(var16, " ", "", false, 4, null)
            val compactQuery: java.lang.String = StringsKt.replace$default(normalizedQuery, " ", "", false, 4, null)
            if (!StringsKt.isBlank(compactQuery) && StringsKt.contains$default(compactHaystack, compactQuery, false, 2, null)) {
               return true
            } else {
               val `$this$filterTo$iv$iv`: java.lang.Iterable = StringsKt.split$default(normalizedQuery, charArrayOf(' '), false, 0, 6, null)
               val `element$iv`: java.util.Collection = ArrayList()

               for (`element$iv$iv` in `$this$filterTo$iv$iv`) {
                  if (!StringsKt.isBlank(`element$iv$iv` as java.lang.String)) {
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

                        if (!StringsKt.contains$default(var16, var20.next() as java.lang.String, false, 2, null)) {
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
      val var10000: java.lang.String = StringsKt.trim(
            Regex("[^a-zA-Z0-9]+").replace(StringsKt.replace$default(Regex("([a-z0-9])([A-Z])").replace(raw, "$1 $2"), '_', ' ', false, 4, null), " ")
         )
         .toString()
         .toLowerCase(Locale.ROOT)
         return Regex("\\s+").replace(var10000, " ")
   }

   private fun cleanSectionLabel(label: String): String {
      return StringsKt.trim(StringsKt.replace$default(StringsKt.replace$default(label, ">", "", false, 4, null), "<", "", false, 4, null)).toString()
   }

   private fun openExternalLink(url: String) {
      var osName: JooonConfigScreen = this

      var fallbackCommand: JooonConfigScreen
      try {
         fallbackCommand = osName
         fallbackCommand = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(URI.create(url))
      } catch (var10: java.lang.Throwable) {
         fallbackCommand = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var10))
      }

      val var10000: URI = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(fallbackCommand)) null else fallbackCommand) as URI
      if (var10000 != null) {
         val uri: URI = var10000
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

            fallbackCommand = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(var29)
         } catch (var11: java.lang.Throwable) {
            fallbackCommand = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var11))
         }

         if (!(if (Result.isFailure_impl/* $VF was: isFailure-impl */(fallbackCommand)) false else fallbackCommand) as java.lang.Boolean) {
            val var30: java.lang.String = System.getProperty("os.name", "")
            val var31: java.lang.String = var30.toLowerCase(Locale.ROOT)
            fallbackCommand = if (StringsKt.contains$default(var31, "win", false, 2, null))
               CollectionsKt.listOf(arrayOf("rundll32", "url.dll,FileProtocolHandler", var10000.toString()))
               else
               (
                  if (StringsKt.contains$default(var31, "mac", false, 2, null))
                     CollectionsKt.listOf(arrayOf("open", var10000.toString()))
                     else
                     CollectionsKt.listOf(arrayOf("xdg-open", var10000.toString()))
               )
               val var25: JooonConfigScreen = this

            try {
               var var26: JooonConfigScreen = var25
               var26 = (JooonConfigScreen)Result.constructor_impl/* $VF was: constructor-impl */(ProcessBuilder(fallbackCommand).start())
            } catch (var9: java.lang.Throwable) {
               val `$this$openExternalLink_u24lambda_u2434`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var9))
            }
         }
      }
   }

   private fun save() {
      JooonConfigManager.INSTANCE.write(this.definition.modId)
   }

   @JvmStatic
   fun `augmentDynamicFarmingItems$addDynamicNodes`(inserted: BooleanRef, augmented: MutableList<ConfigNode>) {
      if (!inserted.element) {
         inserted.element = true
         val var3: StringBuilder = StringBuilder()
         var3.append("Auto Visitor: ")
         if (PersistentState.autoVisitorPadPlaced) {
            var3.append("Pad ")
            var3.append((int)PersistentState.autoVisitorPadX)
            var3.append(", ")
            var3.append((int)PersistentState.autoVisitorPadY)
            var3.append(", ")
            var3.append((int)PersistentState.autoVisitorPadZ)
         } else {
            var3.append("Pad not placed")
         }

         var3.append(" | Accept all: ")
         var3.append(if (PersistentState.autoVisitorAcceptAll) "ON" else "OFF")
         var3.append(" | Max spend: ")
         var3.append(AutoVisitor.INSTANCE.formatCompactValue(PersistentState.autoVisitorMaxSpendCoins))
         var3.append(" | Min XP: ")
         var3.append(AutoVisitor.INSTANCE.formatCompactValue(PersistentState.autoVisitorMinFarmingXp))
         val var10000: java.lang.String = var3.toString()
         augmented.add(ConfigCommentNode("autoVisitorSummaryDynamic", "farming", true, var10000))
         if (AutoVisitor.INSTANCE.isPadMissingWarningRequired()) {
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

   @JvmStatic
   fun `buildSearchItems$flushSection`(
      currentComment: ObjectRef<ConfigCommentNode>,
      currentEntries: MutableList<ConfigEntryNode>,
      `this$0`: JooonConfigScreen,
      category: ConfigCategory,
      query: java.lang.String,
      sections: MutableList<JooonConfigScreen.SearchSection>
   ) {
      if (currentComment.element != null || !currentEntries.isEmpty()) {
         val categoryMatch: Boolean = `this$0`.matchesSearch(category.displayName, query)
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

               val entry: ConfigEntryNode = var20.next() as ConfigEntryNode
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
               val var19: ConfigCommentNode = currentComment.element as ConfigCommentNode
               if (currentComment.element as ConfigCommentNode != null) {
                  val var21: java.lang.String = var19.label
                  if (var21 != null) {
                     val var22: java.lang.String = `this$0`.cleanSectionLabel(var21)
                     if (var22 != null) {
                        val var23: java.lang.String = if (!StringsKt.isBlank(var22)) var22 else null
                        if (var23 != null) {
                           var30 = var23
                           return@label112
                        }
                     }
                  }
               }

               val var29: ConfigEntryNode = CollectionsKt.firstOrNull(currentEntries) as ConfigEntryNode
               var30 = if (var29 != null) var29.label else category.displayName
            }

            sections.add(JooonConfigScreen.SearchSection(category.displayName, var30, CollectionsKt.toList(currentEntries)))
         }

         currentComment.element = null
         currentEntries.clear()
      }
   }

   private data class CategoryHit(categoryKey: String, rect: UiRect) {
      public final val categoryKey: String
      public final val rect: UiRect

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

      public fun copy(categoryKey: String = this.categoryKey, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.CategoryHit {
         return JooonConfigScreen.CategoryHit(categoryKey, rect)
      }

      public override fun toString(): String {
         return "CategoryHit(categoryKey=${this.categoryKey}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return this.categoryKey.hashCode() * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val entry: ConfigEntryNode
      public final val kind: ConfigControlKind
      public final val rect: UiRect

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

      public fun copy(entry: ConfigEntryNode = this.entry, kind: ConfigControlKind = this.kind, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.ControlHit {
         return JooonConfigScreen.ControlHit(entry, kind, rect)
      }

      public override fun toString(): String {
         return "ControlHit(entry=${this.entry}, kind=${this.kind}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return (this.entry.hashCode() * 31 + this.kind.hashCode()) * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val track: UiRect
      public final val thumbHeight: Int
      public final val grabOffset: Int

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

      public fun copy(track: UiRect = this.track, thumbHeight: Int = this.thumbHeight, grabOffset: Int = this.grabOffset): jooon.config.ui.JooonConfigScreen.ScrollbarDrag {
         return JooonConfigScreen.ScrollbarDrag(track, thumbHeight, grabOffset)
      }

      public override fun toString(): String {
         return "ScrollbarDrag(track=${this.track}, thumbHeight=${this.thumbHeight}, grabOffset=${this.grabOffset})"
      }

      public override fun hashCode(): Int {
         return (this.track.hashCode() * 31 + Integer.hashCode(this.thumbHeight)) * 31 + Integer.hashCode(this.grabOffset)
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val categoryLabel: String
      public final val sectionLabel: String
      public final val entries: List<ConfigEntryNode>

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

      public fun copy(categoryLabel: String = this.categoryLabel, sectionLabel: String = this.sectionLabel, entries: List<ConfigEntryNode> = this.entries): jooon.config.ui.JooonConfigScreen.SearchSection {
         return JooonConfigScreen.SearchSection(categoryLabel, sectionLabel, entries)
      }

      public override fun toString(): String {
         return "SearchSection(categoryLabel=${this.categoryLabel}, sectionLabel=${this.sectionLabel}, entries=${this.entries})"
      }

      public override fun hashCode(): Int {
         return (this.categoryLabel.hashCode() * 31 + this.sectionLabel.hashCode()) * 31 + this.entries.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val entry: ConfigEntryNode
      public final val trackRect: UiRect

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

      public fun copy(entry: ConfigEntryNode = this.entry, trackRect: UiRect = this.trackRect): jooon.config.ui.JooonConfigScreen.SliderDrag {
         return JooonConfigScreen.SliderDrag(entry, trackRect)
      }

      public override fun toString(): String {
         return "SliderDrag(entry=${this.entry}, trackRect=${this.trackRect})"
      }

      public override fun hashCode(): Int {
         return this.entry.hashCode() * 31 + this.trackRect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val entry: ConfigEntryNode
      public final val rect: UiRect

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

      public fun copy(entry: ConfigEntryNode = this.entry, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.SliderInputHit {
         return JooonConfigScreen.SliderInputHit(entry, rect)
      }

      public override fun toString(): String {
         return "SliderInputHit(entry=${this.entry}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return this.entry.hashCode() * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val entry: ConfigEntryNode
      public final var draft: String
      public final var replaceOnNextType: Boolean

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

      public fun copy(entry: ConfigEntryNode = this.entry, draft: String = this.draft, replaceOnNextType: Boolean = this.replaceOnNextType): jooon.config.ui.JooonConfigScreen.SliderTextInput {
         return JooonConfigScreen.SliderTextInput(entry, draft, replaceOnNextType)
      }

      public override fun toString(): String {
         return "SliderTextInput(entry=${this.entry}, draft=${this.draft}, replaceOnNextType=${this.replaceOnNextType})"
      }

      public override fun hashCode(): Int {
         return (this.entry.hashCode() * 31 + this.draft.hashCode()) * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val trackRect: UiRect
      public final val valueBox: UiRect

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

      public fun copy(trackRect: UiRect = this.trackRect, valueBox: UiRect = this.valueBox): jooon.config.ui.JooonConfigScreen.SliderVisual {
         return JooonConfigScreen.SliderVisual(trackRect, valueBox)
      }

      public override fun toString(): String {
         return "SliderVisual(trackRect=${this.trackRect}, valueBox=${this.valueBox})"
      }

      public override fun hashCode(): Int {
         return this.trackRect.hashCode() * 31 + this.valueBox.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val entry: ConfigEntryNode
      public final var draft: String
      public final var replaceOnNextType: Boolean

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

      public fun copy(entry: ConfigEntryNode = this.entry, draft: String = this.draft, replaceOnNextType: Boolean = this.replaceOnNextType): jooon.config.ui.JooonConfigScreen.TextEntryInput {
         return JooonConfigScreen.TextEntryInput(entry, draft, replaceOnNextType)
      }

      public override fun toString(): String {
         return "TextEntryInput(entry=${this.entry}, draft=${this.draft}, replaceOnNextType=${this.replaceOnNextType})"
      }

      public override fun hashCode(): Int {
         return (this.entry.hashCode() * 31 + this.draft.hashCode()) * 31 + java.lang.Boolean.hashCode(this.replaceOnNextType)
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val entry: ConfigEntryNode
      public final val rect: UiRect

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

      public fun copy(entry: ConfigEntryNode = this.entry, rect: UiRect = this.rect): jooon.config.ui.JooonConfigScreen.TextInputHit {
         return JooonConfigScreen.TextInputHit(entry, rect)
      }

      public override fun toString(): String {
         return "TextInputHit(entry=${this.entry}, rect=${this.rect})"
      }

      public override fun hashCode(): Int {
         return this.entry.hashCode() * 31 + this.rect.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
