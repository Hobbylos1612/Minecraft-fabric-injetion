package jooon.features.farming

import jooon.config.PersistentState
import kotlin.jvm.internal.Intrinsics
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private class AutoVisitorRulesScreen : Screen {
   private Screen parentScreen;
   private val backScreenFactory: () -> Screen
   private val setupMode: Boolean
   private ButtonWidget acceptAllButton;
   private ButtonWidget ignoreSpacemanButton;
   private ButtonWidget trySacksButton;
   private ButtonWidget rareItemsButton;
   private ButtonWidget finishButton;
   private TextFieldWidget maxSpendInput;
   private TextFieldWidget minFarmingXpInput;
   private var acceptAll: Boolean
   private var ignoreSpaceman: Boolean
   private var trySacksFirst: Boolean
   private var rareItemsOnly: Boolean
   private var maxSpendInvalid: Boolean
   private var minXpInvalid: Boolean
   private MutableText maxSpendTooltip;
   private MutableText ignoreSpacemanTooltip;
   private MutableText trySacksTooltip;
   private MutableText minXpTooltip;

   fun AutoVisitorRulesScreen(parentScreen: Screen?, backScreenFactory: () -> Screen, setupMode: Boolean) {
      super(Text.literal("Auto Visitor Rules") as Text)
      this.parentScreen = parentScreen
      this.backScreenFactory = backScreenFactory
      this.setupMode = setupMode
      this.acceptAll = PersistentState.autoVisitorAcceptAll
      this.ignoreSpaceman = PersistentState.autoVisitorIgnoreSpaceman
      this.trySacksFirst = PersistentState.autoVisitorTrySacksFirst
      this.rareItemsOnly = PersistentState.autoVisitorRareItemsOnly
      var var10001: MutableText = Text.literal(
         "Enter the maximum amount of coins that Jooon can spend on a single visitor. Supports K (thousands) and M (millions). You can set 0 to disable maximum spend."
      )
      this.maxSpendTooltip = var10001
      var10001 = Text.literal(
         "When enabled, Jooon will ignore Spaceman (not reject). Note: if you place your pad where other visitors are inaccessible while Spaceman is present, Jooon will stop accepting visitors."
      )
      this.ignoreSpacemanTooltip = var10001
      var10001 = Text.literal("If enabled, Jooon will try to use items from your sacks first before using the Bazaar.")
      this.trySacksTooltip = var10001
      var10001 = Text.literal(
         "When set above 0, Jooon will only accept visitors that are within the maximum spend limit and give at least the specified Farming XP. Supports K (thousands) and M (millions). Example: if Beth offers 8.8K Farming XP and you set 8K, Jooon will accept Beth if the maximum spend is not exceeded."
      )
      this.minXpTooltip = var10001
   }

   fun method_25426() {
      super.init()
      this.addDrawableChild(ButtonWidget.builder(Text.literal("< Back") as Text, { it: ButtonWidget ->
         if (`this$0`.client != null) {
            `this$0`.client.setScreen(`this$0`.backScreenFactory() as Screen)
         }
      }).position(8, 8).size(76, 20).build() as Element)
      this.finishButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Finish Setup >") as Text, { it: ButtonWidget ->
         `this$0`.commitAndFinish()
      }).position(this.width - 118, 8).size(110, 20).build() as Element) as ButtonWidget

      this.acceptAllButton = this.addDrawableChild(this.toggleButton(this.width / 2 - 210 + 250, 96, { 
         `this$0`.acceptAll = !`this$0`.acceptAll
         `this$0`.refreshControlStates()
return Unit
      }) as Element) as ButtonWidget
      var var11: Int = 96 + 34
      this.ignoreSpacemanButton = this.addDrawableChild(this.toggleButton(controlX, 96 + 34, { 
         `this$0`.ignoreSpaceman = !`this$0`.ignoreSpaceman
         `this$0`.refreshControlStates()
return Unit
      }) as Element) as ButtonWidget

      this.maxSpendInput = TextFieldWidget(this.textRenderer, controlX, var11 + 34 + 4, 88, 20, Text.literal("Coins") as Text)



      var var10000: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         throwUninitializedPropertyAccessException("maxSpendInput")
         var10000 = null
      }

      var10000.setText(
         if (initialMaxSpend == AutoVisitor.defaultMaxSpendCoins()) "500K" else AutoVisitor.formatCompactValue(initialMaxSpend)
      )
      var10000 = this.maxSpendInput
      if (this.maxSpendInput == null) {
         throwUninitializedPropertyAccessException("maxSpendInput")
         var10000 = null
      }

      var10000.setChangedListener({ it: String ->
         `this$0`.maxSpendInvalid = AutoVisitor.parseCompactNumber(it) == null
      })
      var var10001: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         throwUninitializedPropertyAccessException("maxSpendInput")
         var10001 = null
      }

      this.addDrawableChild(var10001 as Element)
      var11 = var12 + 34
      this.trySacksButton = this.addDrawableChild(this.toggleButton(controlX, var12 + 34, { 
         `this$0`.trySacksFirst = !`this$0`.trySacksFirst
         `this$0`.refreshControlStates()
return Unit
      }) as Element) as ButtonWidget

      this.rareItemsButton = this.addDrawableChild(this.toggleButton(controlX, var11 + 34, { 
         if (!`this$0`.acceptAll) {
            `this$0`.rareItemsOnly = !`this$0`.rareItemsOnly
            `this$0`.refreshControlStates()
         }
return Unit
      }) as Element) as ButtonWidget
      this.minFarmingXpInput = TextFieldWidget(this.textRenderer, controlX, var14 + 34 + 4, 88, 20, Text.literal("XP") as Text)
      var10000 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10000 = null
      }

      var10000.setText(AutoVisitor.formatCompactValue(PersistentState.autoVisitorMinFarmingXp))
      var10000 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10000 = null
      }

      var10000.setChangedListener({ it: String ->
         `this$0`.minXpInvalid = AutoVisitor.parseCompactNumber(it) == null
      })
      var10001 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10001 = null
      }

      this.addDrawableChild(var10001 as Element)
      this.refreshControlStates()
   }

   fun toggleButton(controlX: Int, y: Int, onPress: () -> Unit): ButtonWidget {

         `$onPress`()
      }).position(controlX, y + 3).size(118, 22).build()
return var10000
   }

   fun method_25420(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      context.fill(0, 0, this.width, this.height, -804253160)
   }

   fun method_25394(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
      this.renderBackground(context, mouseX, mouseY, delta)


      context.drawText(
         this.textRenderer, var10000 as Text, this.width / 2 - this.textRenderer.getWidth(var10000 as StringVisitable) / 2, 34, -1, false
      )
      context.drawText(this.textRenderer, var16 as Text, this.width / 2 - this.textRenderer.getWidth(var16 as StringVisitable) / 2, 50, -1, false)


      this.drawRuleLabel(context, "Accept all", this.width / 2 - 210, 96)
      var var11: Int = 96 + 34
      this.drawRuleLabel(context, "Ignore Spaceman", startX, 96 + 34)

      this.drawRuleLabel(context, "Maximum spend of", startX, var11 + 34)
      context.drawText(this.textRenderer, Text.literal("coins").formatted(Formatting.GOLD) as Text, controlX + 88 + 8, var12 + 9, -1, false)
      if (this.maxSpendInvalid) {
         context.drawText(this.textRenderer, Text.literal("Invalid value").formatted(Formatting.RED) as Text, controlX, var12 + 26, -1, false)
      }

      var11 = var12 + 34
      this.drawRuleLabel(context, "Try Sacks first", startX, var12 + 34)

      this.drawRuleLabel(context, "Rare items only", startX, var11 + 34)
      var11 = var14 + 34
      this.drawRuleLabel(context, "Minimum of", startX, var14 + 34)
      context.drawText(this.textRenderer, Text.literal("Farming XP") as Text, controlX + 88 + 8, var11 + 9, -1, false)
      if (this.minXpInvalid) {
         context.drawText(this.textRenderer, Text.literal("Invalid value").formatted(Formatting.RED) as Text, controlX, var11 + 26, -1, false)
      }

      super.render(context, mouseX, mouseY, delta)
      this.renderRuleTooltips(context, mouseX, mouseY, startX)
   }

   fun drawRuleLabel(context: DrawContext, text: String, x: Int, y: Int) {
      context.fill(x, y, x + 422, y + 28, -2145376720)
      context.drawStrokedRectangle(x, y, 422, 28, -11904142)
      context.drawText(this.textRenderer, Text.literal(text) as Text, x + 10, y + 9, -1511688, false)
   }

   fun renderRuleTooltips(context: DrawContext, mouseX: Int, mouseY: Int, startX: Int) {





      if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y2)) {
         AutoVisitorKt.access$renderTooltipBox(context, listOf(this.ignoreSpacemanTooltip), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y3)) {
         AutoVisitorKt.access$renderTooltipBox(context, listOf(this.maxSpendTooltip), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y4)) {
         AutoVisitorKt.access$renderTooltipBox(context, listOf(this.trySacksTooltip), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y5)) {
         AutoVisitorKt.access$renderTooltipBox(context, listOf(this.rareItemsTooltip()), mouseX, mouseY)
      } else if (renderRuleTooltips$hovered(startX, mouseX, 422, mouseY, 28, y6)) {
         AutoVisitorKt.access$renderTooltipBox(context, listOf(this.minXpTooltip), mouseX, mouseY)
      }
   }

   fun rareItemsTooltip(): Text {

         .append(rareItemsTooltip$colored("Flowering Bouquet", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Overgrown Grass", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Green Bandana", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Dedication IV", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Music Rune", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Cultivating I", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Replenish I", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Delicate V", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Copper Dye", Formatting.DARK_GRAY))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Jungle Key", Formatting.DARK_PURPLE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Fruit Bowl", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Harvest Harbinger V", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Hypercharge Chip", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Quickdraw Chip", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Farming Exp Boost", Formatting.DARK_PURPLE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Unfulfilled Jerryseed", Formatting.GREEN))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Voter's Badge", Formatting.WHITE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("VIP Voter's Badge", Formatting.GREEN))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Elite Voter's Badge", Formatting.BLUE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Supreme Voter's Badge", Formatting.DARK_PURPLE))
         .append(Text.literal(", ") as Text)
         .append(rareItemsTooltip$colored("Wild Strawberry Dye.", Formatting.LIGHT_PURPLE))
         var10000 as Text
   }

   private fun refreshControlStates() {
      var var10000: ButtonWidget = this.acceptAllButton
      if (this.acceptAllButton == null) {
         throwUninitializedPropertyAccessException("acceptAllButton")
         var10000 = null
      }

      var10000.setMessage(Text.literal(this.toggleText(this.acceptAll)) as Text)
      var10000 = this.ignoreSpacemanButton
      if (this.ignoreSpacemanButton == null) {
         throwUninitializedPropertyAccessException("ignoreSpacemanButton")
         var10000 = null
      }

      var10000.setMessage(Text.literal(this.toggleText(this.ignoreSpaceman)) as Text)
      var10000 = this.trySacksButton
      if (this.trySacksButton == null) {
         throwUninitializedPropertyAccessException("trySacksButton")
         var10000 = null
      }

      var10000.setMessage(Text.literal(this.toggleText(this.trySacksFirst)) as Text)
      var10000 = this.rareItemsButton
      if (this.rareItemsButton == null) {
         throwUninitializedPropertyAccessException("rareItemsButton")
         var10000 = null
      }

      var10000.setMessage(Text.literal(this.toggleText(this.rareItemsOnly)) as Text)

      var10000 = this.rareItemsButton
      if (this.rareItemsButton == null) {
         throwUninitializedPropertyAccessException("rareItemsButton")
         var10000 = null
      }

      var10000.active = !this.acceptAll
      var var12: TextFieldWidget = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var12 = null
      }

      var12.active = !lockedByAcceptAll


      try {
         var var6: AutoVisitorRulesScreen = var2
         var var13: TextFieldWidget = var2.minFarmingXpInput
         if (var6.minFarmingXpInput == null) {
            throwUninitializedPropertyAccessException("minFarmingXpInput")
            var13 = null
         }

         var13.setEditable(!lockedByAcceptAll)
         var6 = Result(Unit)
      } catch (var5: java.lang.Throwable) {
         val `this24lambda_u2410`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }
   }

   private fun toggleText(on: Boolean): String {
      return if (on) "ON" else "OFF"
   }

   private fun commitAndFinish() {

      var finalMax: Long = minParsed.longValue()

         if (finalMax > 0L) minParsed else null
return else
         AutoVisitor.defaultMaxSpendCoins()
         var var10000: AutoVisitor = AutoVisitor.INSTANCE
      var var10001: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         throwUninitializedPropertyAccessException("maxSpendInput")
         var10001 = null
      }

      if (var10000.parseCompactNumber(var21) == null) {
         this.maxSpendInvalid = true
         var var12: TextFieldWidget = this.maxSpendInput
         if (this.maxSpendInput == null) {
            throwUninitializedPropertyAccessException("maxSpendInput")
            var12 = null
         }

         var12.setText(if (maxFallback == AutoVisitor.defaultMaxSpendCoins()) "500K" else AutoVisitor.formatCompactValue(maxFallback))
      }

      var10000 = AutoVisitor.INSTANCE
      var10001 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10001 = null
      }

      if (var10000.parseCompactNumber(var23) == null) {
         this.minXpInvalid = true
         var var14: TextFieldWidget = this.minFarmingXpInput
         if (this.minFarmingXpInput == null) {
            throwUninitializedPropertyAccessException("minFarmingXpInput")
            var14 = null
         }

         var14.setText(AutoVisitor.formatCompactValue(PersistentState.autoVisitorMinFarmingXp))
      }

      var10000 = AutoVisitor.INSTANCE
      var10001 = this.maxSpendInput
      if (this.maxSpendInput == null) {
         throwUninitializedPropertyAccessException("maxSpendInput")
         var10001 = null
      }


      finalMax = (var16 ?: maxFallback).coerceAtLeast(0L)
      var10000 = AutoVisitor.INSTANCE
      var10001 = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var10001 = null
      }



      this.maxSpendInvalid = false
      this.minXpInvalid = false
      var var19: TextFieldWidget = this.maxSpendInput
      if (this.maxSpendInput == null) {
         throwUninitializedPropertyAccessException("maxSpendInput")
         var19 = null
      }

      var19.setText(if (finalMax == AutoVisitor.defaultMaxSpendCoins()) "500K" else AutoVisitor.formatCompactValue(finalMax))
      var var20: TextFieldWidget = this.minFarmingXpInput
      if (this.minFarmingXpInput == null) {
         throwUninitializedPropertyAccessException("minFarmingXpInput")
         var20 = null
      }

      var20.setText(AutoVisitor.formatCompactValue(var11))
      AutoVisitor.saveRulesFromUi(this.acceptAll, this.ignoreSpaceman, finalMax, this.trySacksFirst, this.rareItemsOnly, var11)
      if (this.client != null) {
         this.client.setScreen(this.parentScreen)
      }
   }

   fun method_25419() {
      if (this.setupMode) {
         AutoVisitor.cancelSetup(this.parentScreen)
      } else if (this.client != null) {
         this.client.setScreen(this.parentScreen)
      }
   }

   fun method_25421(): Boolean {
return false
   }

   
   fun `renderRuleTooltips$hovered`(rowRectX: Int, `$mouseX`: Int, rowWidth: Int, `$mouseY`: Int, rowHeight: Int, y: Int): Boolean {
      rowRectX <= `$mouseX` && `$mouseX` < rowRectX + rowWidth && y <= `$mouseY` && `$mouseY` < y + rowHeight
   }

   
   fun `rareItemsTooltip$colored`(name: String, color: Formatting): Text {

      var10000 as Text
   }
}
