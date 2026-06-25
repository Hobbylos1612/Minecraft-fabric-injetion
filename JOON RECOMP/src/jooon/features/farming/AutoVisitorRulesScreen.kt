package jooon.features.farming

import jooon.config.PersistentState
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_437
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@SourceDebugExtension(["SMAP\nAutoVisitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoVisitor.kt\njooon/features/farming/AutoVisitorRulesScreen\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1010:1\n1#2:1011\n*E\n"])
private class AutoVisitorRulesScreen : Screen {
   private Screen parentScreen;
   private final val backScreenFactory: () -> class_437
   private final val setupMode: Boolean
   private ButtonWidget acceptAllButton;
   private ButtonWidget ignoreSpacemanButton;
   private ButtonWidget trySacksButton;
   private ButtonWidget rareItemsButton;
   private ButtonWidget finishButton;
   private TextFieldWidget maxSpendInput;
   private TextFieldWidget minFarmingXpInput;
   private final var acceptAll: Boolean
   private final var ignoreSpaceman: Boolean
   private final var trySacksFirst: Boolean
   private final var rareItemsOnly: Boolean
   private final var maxSpendInvalid: Boolean
   private final var minXpInvalid: Boolean
   private MutableText maxSpendTooltip;
   private MutableText ignoreSpacemanTooltip;
   private MutableText trySacksTooltip;
   private MutableText minXpTooltip;

   fun AutoVisitorRulesScreen(parentScreen: Screen?, backScreenFactory: () -> Screen, setupMode: Boolean) {
      super(Text.method_43470("Auto Visitor Rules") as Text)
      this.parentScreen = parentScreen
      this.backScreenFactory = backScreenFactory
      this.setupMode = setupMode
      this.acceptAll = PersistentState.autoVisitorAcceptAll
      this.ignoreSpaceman = PersistentState.autoVisitorIgnoreSpaceman
      this.trySacksFirst = PersistentState.autoVisitorTrySacksFirst
      this.rareItemsOnly = PersistentState.autoVisitorRareItemsOnly
      var var10001: MutableText = Text.method_43470(
         "Enter the maximum amount of coins that Jooon can spend on a single visitor. Supports K (thousands) and M (millions). You can set 0 to disable maximum spend."
      )
      this.maxSpendTooltip = var10001
      var10001 = Text.method_43470(
         "When enabled, Jooon will ignore Spaceman (not reject). Note: if you place your pad where other visitors are inaccessible while Spaceman is present, Jooon will stop accepting visitors."
      )
      this.ignoreSpacemanTooltip = var10001
      var10001 = Text.method_43470("If enabled, Jooon will try to use items from your sacks first before using the Bazaar.")
      this.trySacksTooltip = var10001
      var10001 = Text.method_43470(
         "When set above 0, Jooon will only accept visitors that are within the maximum spend limit and give at least the specified Farming XP. Supports K (thousands) and M (millions). Example: if Beth offers 8.8K Farming XP and you set 8K, Jooon will accept Beth if the maximum spend is not exceeded."
      )
      this.minXpTooltip = var10001
   }

   fun method_25426() {
      super.method_25426()
      this.method_37063(ButtonWidget.method_46430(Text.method_43470("< Back") as Text, { it: ButtonWidget ->
         if (`this$0`.field_22787 != null) {
            `this$0`.field_22787.method_1507(`this$0`.backScreenFactory() as Screen)
         }
      }).method_46433(8, 8).method_46437(76, 20).method_46431() as Element)
      this.finishButton = this.method_37063(ButtonWidget.method_46430(Text.method_43470("Finish Setup >") as Text, { it: ButtonWidget ->
         `this$0`.commitAndFinish()
      }).method_46433(this.field_22789 - 118, 8).method_46437(110, 20).method_46431() as Element) as ButtonWidget
      val controlX: Int = this.field_22789 / 2 - 210 + 250
      this.acceptAllButton = this.method_37063(this.toggleButton(this.field_22789 / 2 - 210 + 250, 96, { 
         `this$0`.acceptAll = !`this$0`.acceptAll
         `this$0`.refreshControlStates()
         Unit.INSTANCE
      }) as Element) as ButtonWidget
      var var11: Int = 96 + 34
      this.ignoreSpacemanButton = this.method_37063(this.toggleButton(controlX, 96 + 34, { 
         `this$0`.ignoreSpaceman = !`this$0`.ignoreSpaceman
         `this$0`.refreshControlStates()
         Unit.INSTANCE
      }) as Element) as ButtonWidget
      val var12: Int = 96 + 34 + 34
      this.maxSpendInput = TextFieldWidget(this.field_22793, controlX, var11 + 34 + 4, 88, 20, Text.method_43470("Coins") as Text)
      val var7: java.lang.Long = PersistentState.autoVisitorMaxSpendCoins
      val it: Long = var7.longValue()
      val initialMaxSpend: Long = if ((if (it > 0L) var7 else null) != null) if (it > 0L) var7 else null else AutoVisitor.INSTANCE.defaultMaxSpendCoins()
      var var10000: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
         var10000 = null
      }

      var10000.method_1852(
         if (initialMaxSpend == AutoVisitor.INSTANCE.defaultMaxSpendCoins()) "500K" else AutoVisitor.INSTANCE.formatCompactValue(initialMaxSpend)
      )
      var10000 = this.maxSpendInput
      if (this.maxSpendInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
         var10000 = null
      }

      var10000.method_1863({ it: java.lang.String ->
         `this$0`.maxSpendInvalid = AutoVisitor.INSTANCE.parseCompactNumber(it) == null
      })
      var var10001: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
         var10001 = null
      }

      this.method_37063(var10001 as Element)
      var11 = var12 + 34
      this.trySacksButton = this.method_37063(this.toggleButton(controlX, var12 + 34, { 
         `this$0`.trySacksFirst = !`this$0`.trySacksFirst
         `this$0`.refreshControlStates()
         Unit.INSTANCE
      }) as Element) as ButtonWidget
      val var14: Int = var12 + 34 + 34
      this.rareItemsButton = this.method_37063(this.toggleButton(controlX, var11 + 34, { 
         if (!`this$0`.acceptAll) {
            `this$0`.rareItemsOnly = !`this$0`.rareItemsOnly
            `this$0`.refreshControlStates()
         }

         Unit.INSTANCE
      }) as Element) as ButtonWidget
      this.minFarmingXpInput = TextFieldWidget(this.field_22793, controlX, var14 + 34 + 4, 88, 20, Text.method_43470("XP") as Text)
      var10000 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10000 = null
      }

      var10000.method_1852(AutoVisitor.INSTANCE.formatCompactValue(PersistentState.autoVisitorMinFarmingXp))
      var10000 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10000 = null
      }

      var10000.method_1863({ it: java.lang.String ->
         `this$0`.minXpInvalid = AutoVisitor.INSTANCE.parseCompactNumber(it) == null
      })
      var10001 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10001 = null
      }

      this.method_37063(var10001 as Element)
      this.refreshControlStates()
   }

   fun toggleButton(controlX: Int, y: Int, onPress: () -> Unit): ButtonWidget {
      val var10000: ButtonWidget = ButtonWidget.method_46430(Text.method_43470("...") as Text, { it: ButtonWidget ->
         `$onPress`()
      }).method_46433(controlX, y + 3).method_46437(118, 22).method_46431()
      var10000
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      context.method_25294(0, 0, this.field_22789, this.field_22790, -804253160)
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.method_25420(context, mouseX, mouseY, delta)
      val var10000: MutableText = Text.method_43470("Auto Visitors:").method_27692(Formatting.field_1060)
      val var16: MutableText = Text.method_43470("Who should Jooon accept?").method_27692(Formatting.field_1080)
      context.method_51439(
         this.field_22793, var10000 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var10000 as StringVisitable) / 2, 34, -1, false
      )
      context.method_51439(this.field_22793, var16 as Text, this.field_22789 / 2 - this.field_22793.method_27525(var16 as StringVisitable) / 2, 50, -1, false)
      val startX: Int = this.field_22789 / 2 - 210
      val controlX: Int = this.field_22789 / 2 - 210 + 250
      this.drawRuleLabel(context, "Accept all", this.field_22789 / 2 - 210, 96)
      var var11: Int = 96 + 34
      this.drawRuleLabel(context, "Ignore Spaceman", startX, 96 + 34)
      val var12: Int = var11 + 34
      this.drawRuleLabel(context, "Maximum spend of", startX, var11 + 34)
      context.method_51439(this.field_22793, Text.method_43470("coins").method_27692(Formatting.field_1065) as Text, controlX + 88 + 8, var12 + 9, -1, false)
      if (this.maxSpendInvalid) {
         context.method_51439(this.field_22793, Text.method_43470("Invalid value").method_27692(Formatting.field_1061) as Text, controlX, var12 + 26, -1, false)
      }

      var11 = var12 + 34
      this.drawRuleLabel(context, "Try Sacks first", startX, var12 + 34)
      val var14: Int = var11 + 34
      this.drawRuleLabel(context, "Rare items only", startX, var11 + 34)
      var11 = var14 + 34
      this.drawRuleLabel(context, "Minimum of", startX, var14 + 34)
      context.method_51439(this.field_22793, Text.method_43470("Farming XP") as Text, controlX + 88 + 8, var11 + 9, -1, false)
      if (this.minXpInvalid) {
         context.method_51439(this.field_22793, Text.method_43470("Invalid value").method_27692(Formatting.field_1061) as Text, controlX, var11 + 26, -1, false)
      }

      super.method_25394(context, mouseX, mouseY, delta)
      this.renderRuleTooltips(context, mouseX, mouseY, startX)
   }

   fun drawRuleLabel(context: DrawContext, text: java.lang.String, x: Int, y: Int) {
      context.method_25294(x, y, x + 422, y + 28, -2145376720)
      context.method_73198(x, y, 422, 28, -11904142)
      context.method_51439(this.field_22793, Text.method_43470(text) as Text, x + 10, y + 9, -1511688, false)
   }

   fun renderRuleTooltips(context: DrawContext, mouseX: Int, mouseY: Int, startX: Int) {
      val y2: Int = 96 + 34
      val y3: Int = 96 + 34 + 34
      val y4: Int = 96 + 34 + 34 + 34
      val y5: Int = 96 + 34 + 34 + 34 + 34
      val y6: Int = 96 + 34 + 34 + 34 + 34 + 34
      if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y2)) {
         AutoVisitorKt.access$renderTooltipBox(context, CollectionsKt.listOf(this.ignoreSpacemanTooltip), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y3)) {
         AutoVisitorKt.access$renderTooltipBox(context, CollectionsKt.listOf(this.maxSpendTooltip), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y4)) {
         AutoVisitorKt.access$renderTooltipBox(context, CollectionsKt.listOf(this.trySacksTooltip), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y5)) {
         AutoVisitorKt.access$renderTooltipBox(context, CollectionsKt.listOf(this.rareItemsTooltip()), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y6)) {
         AutoVisitorKt.access$renderTooltipBox(context, CollectionsKt.listOf(this.minXpTooltip), mouseX, mouseY)
      }
   }

   fun rareItemsTooltip(): Text {
      val var10000: MutableText = Text.method_43470("If enabled, Jooon will only accept visitors that are within the maximum spend and that offer: ")
         .method_10852(rareItemsTooltip$colored("Flowering Bouquet", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Overgrown Grass", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Green Bandana", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Dedication IV", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Music Rune", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Cultivating I", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Replenish I", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Delicate V", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Copper Dye", Formatting.field_1063))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Jungle Key", Formatting.field_1064))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Fruit Bowl", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Harvest Harbinger V", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Hypercharge Chip", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Quickdraw Chip", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Farming Exp Boost", Formatting.field_1064))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Unfulfilled Jerryseed", Formatting.field_1060))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Voter's Badge", Formatting.field_1068))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("VIP Voter's Badge", Formatting.field_1060))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Elite Voter's Badge", Formatting.field_1078))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Supreme Voter's Badge", Formatting.field_1064))
         .method_10852(Text.method_43470(", ") as Text)
         .method_10852(rareItemsTooltip$colored("Wild Strawberry Dye.", Formatting.field_1076))
         var10000 as Text
   }

   private fun refreshControlStates() {
      var var10000: ButtonWidget = this.acceptAllButton
      if (this.acceptAllButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("acceptAllButton")
         var10000 = null
      }

      var10000.method_25355(Text.method_43470(this.toggleText(this.acceptAll)) as Text)
      var10000 = this.ignoreSpacemanButton
      if (this.ignoreSpacemanButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("ignoreSpacemanButton")
         var10000 = null
      }

      var10000.method_25355(Text.method_43470(this.toggleText(this.ignoreSpaceman)) as Text)
      var10000 = this.trySacksButton
      if (this.trySacksButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("trySacksButton")
         var10000 = null
      }

      var10000.method_25355(Text.method_43470(this.toggleText(this.trySacksFirst)) as Text)
      var10000 = this.rareItemsButton
      if (this.rareItemsButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("rareItemsButton")
         var10000 = null
      }

      var10000.method_25355(Text.method_43470(this.toggleText(this.rareItemsOnly)) as Text)
      val lockedByAcceptAll: Boolean = this.acceptAll
      var10000 = this.rareItemsButton
      if (this.rareItemsButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("rareItemsButton")
         var10000 = null
      }

      var10000.field_22763 = !this.acceptAll
      var var12: TextFieldWidget = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var12 = null
      }

      var12.field_22763 = !lockedByAcceptAll
      val var2: AutoVisitorRulesScreen = this

      try {
         var var6: AutoVisitorRulesScreen = var2
         var var13: TextFieldWidget = var2.minFarmingXpInput
         if (var6.minFarmingXpInput == null) {
            Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
            var13 = null
         }

         var13.method_1888(!lockedByAcceptAll)
         var6 = (AutoVisitorRulesScreen)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var5: java.lang.Throwable) {
         val `$this$refreshControlStates_u24lambda_u2410`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }
   }

   private fun toggleText(on: Boolean): String {
      return if (on) "ON" else "OFF"
   }

   private fun commitAndFinish() {
      val minParsed: java.lang.Long = PersistentState.autoVisitorMaxSpendCoins
      var finalMax: Long = minParsed.longValue()
      val maxFallback: Long = if ((if (finalMax > 0L) minParsed else null) != null)
         if (finalMax > 0L) minParsed else null
         else
         AutoVisitor.INSTANCE.defaultMaxSpendCoins()
         var var10000: AutoVisitor = AutoVisitor.INSTANCE
      var var10001: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
         var10001 = null
      }

      val var21: java.lang.String = var10001.method_1882()
      if (var10000.parseCompactNumber(var21) == null) {
         this.maxSpendInvalid = true
         var var12: TextFieldWidget = this.maxSpendInput
         if (this.maxSpendInput == null) {
            Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
            var12 = null
         }

         var12.method_1852(if (maxFallback == AutoVisitor.INSTANCE.defaultMaxSpendCoins()) "500K" else AutoVisitor.INSTANCE.formatCompactValue(maxFallback))
      }

      var10000 = AutoVisitor.INSTANCE
      var10001 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10001 = null
      }

      val var23: java.lang.String = var10001.method_1882()
      if (var10000.parseCompactNumber(var23) == null) {
         this.minXpInvalid = true
         var var14: TextFieldWidget = this.minFarmingXpInput
         if (this.minFarmingXpInput == null) {
            Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
            var14 = null
         }

         var14.method_1852(AutoVisitor.INSTANCE.formatCompactValue(PersistentState.autoVisitorMinFarmingXp))
      }

      var10000 = AutoVisitor.INSTANCE
      var10001 = this.maxSpendInput
      if (this.maxSpendInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
         var10001 = null
      }

      val var25: java.lang.String = var10001.method_1882()
      val var16: java.lang.Long = var10000.parseCompactNumber(var25)
      finalMax = RangesKt.coerceAtLeast(var16 ?: maxFallback, 0L)
      var10000 = AutoVisitor.INSTANCE
      var10001 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10001 = null
      }

      val var27: java.lang.String = var10001.method_1882()
      val var18: java.lang.Long = var10000.parseCompactNumber(var27)
      val var11: Long = RangesKt.coerceAtLeast(var18 ?: PersistentState.autoVisitorMinFarmingXp, 0L)
      this.maxSpendInvalid = false
      this.minXpInvalid = false
      var var19: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("maxSpendInput")
         var19 = null
      }

      var19.method_1852(if (finalMax == AutoVisitor.INSTANCE.defaultMaxSpendCoins()) "500K" else AutoVisitor.INSTANCE.formatCompactValue(finalMax))
      var var20: TextFieldWidget = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         Intrinsics.throwUninitializedPropertyAccessException("minFarmingXpInput")
         var20 = null
      }

      var20.method_1852(AutoVisitor.INSTANCE.formatCompactValue(var11))
      AutoVisitor.INSTANCE.saveRulesFromUi(this.acceptAll, this.ignoreSpaceman, finalMax, this.trySacksFirst, this.rareItemsOnly, var11)
      if (this.field_22787 != null) {
         this.field_22787.method_1507(this.parentScreen)
      }
   }

   fun method_25419() {
      if (this.setupMode) {
         AutoVisitor.INSTANCE.cancelSetup(this.parentScreen)
      } else if (this.field_22787 != null) {
         this.field_22787.method_1507(this.parentScreen)
      }
   }

   fun method_25421(): Boolean {
      false
   }

   @JvmStatic
   fun `renderRuleTooltips$hovered`(rowRectX: Int, `$mouseX`: Int, rowWidth: Int, `$mouseY`: Int, rowHeight: Int, y: Int): Boolean {
      rowRectX <= `$mouseX` && `$mouseX` < rowRectX + rowWidth && y <= `$mouseY` && `$mouseY` < y + rowHeight
   }

   @JvmStatic
   fun `rareItemsTooltip$colored`(name: java.lang.String, color: Formatting): Text {
      val var10000: MutableText = Text.method_43470(name).method_27692(color)
      var10000 as Text
   }
}
