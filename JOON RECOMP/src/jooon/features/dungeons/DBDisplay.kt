package jooon.features.dungeons

import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.mixins.OptionsAccessor
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.AttackIndicator
import net.minecraft.client.option.GameOptions
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.Window
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

public object DBDisplay {
   private final var currentCharges: Int
   private final var isHoldingDungeonBreaker: Boolean
   private final var movableOverlay: MovableOverlay?
   private final var isPositioningMode: Boolean
   private final var appliedSavedPos: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      HudRenderCallback.EVENT
         .register(
            { context: DrawContext, var1: RenderTickCounter ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         ClientTickEvents.END_CLIENT_TICK
         .register(
            { it: MinecraftClient ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
      }

   fun ensureOverlayReady(context: DrawContext) {
      if (!PersistentState.dbDisplayInitDone) {
         val var10000: TextRenderer = this.getMc().field_1772
         val winH: Text = this.getDisplayTextWithColor()
         val var17: GameOptions = this.getMc().field_1690
         val var18: Any = (var17 as OptionsAccessor).getAttackIndicator().method_41753()
         val attackGap: Int = if (var18 as AttackIndicator === AttackIndicator.field_18152) 9 else 0
         val ox: Int = -var10000.method_27525(winH as StringVisitable) / 2
         val oy: Int = -attackGap - 4 - var10000.field_2000
         PersistentState.dbDisplayX = ox
         PersistentState.dbDisplayY = oy
         PersistentState.dbDisplayInitDone = true
         JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      }

      if (movableOverlay == null) {
         movableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "dbDisplay",
               "⛏ 20",
               this.getMc().method_22683().method_4486() / 2 + PersistentState.dbDisplayX,
               this.getMc().method_22683().method_4502() / 2 + PersistentState.dbDisplayY,
               50,
               20
            )
            if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_2@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->
               if (!isHoldingDungeonBreaker) {
                  return@lambda_2 Unit.INSTANCE
               } else {
                  val var10000: TextRenderer = INSTANCE.getMc().field_1772
                  ctx.method_51439(var10000, INSTANCE.getDisplayTextWithColor(), x, y, -1, true)
                  return@lambda_2 Unit.INSTANCE
               }
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->
               val ox: Int = x - INSTANCE.getMc().method_22683().method_4486() / 2
               val oy: Int = y - INSTANCE.getMc().method_22683().method_4502() / 2
               PersistentState.dbDisplayX = ox
               PersistentState.dbDisplayY = oy
               PersistentState.dbDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.INSTANCE.write("jooonreimagined_state")
               Unit.INSTANCE
            }
         }

         if (Config.dbDisplayEnabled && PersistentState.dbDisplayMovable) {
            if (movableOverlay != null) {
               movableOverlay.register()
            }
         }
      }

      if (!appliedSavedPos) {
         val var14: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.dbDisplayX
         val var16: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.dbDisplayY
         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(var14, var16)
         }

         appliedSavedPos = true
      }
   }

   private fun updateChargesCount() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val var3: ItemStack = var10000.method_6047()
         if (!this.isDungeonBreaker(var3)) {
            currentCharges = 0
            isHoldingDungeonBreaker = false
         } else {
            isHoldingDungeonBreaker = true
            currentCharges = this.extractChargesFromLore(var3)
         }
      }
   }

   fun isDungeonBreaker(itemStack: ItemStack): Boolean {
      if (itemStack.method_7960()) {
         false
      } else {
         var var10000: java.lang.String = itemStack.method_7909().toString()
         if (!StringsKt.contains$default(var10000, "diamond_pickaxe", false, 2, null)) {
            false
         } else {
            val var3: Text = itemStack.method_58694(DataComponentTypes.field_49631) as Text
            if (var3 != null) {
               var10000 = var3.getString()
               if (var10000 != null) {
                  StringsKt.contains(var10000, "DungeonBreaker", true)
               }
            }

            false
         }
      }
   }

   fun extractChargesFromLore(itemStack: ItemStack): Int {
      val var10000: LoreComponent = itemStack.method_58694(DataComponentTypes.field_49632) as LoreComponent
      if (var10000 == null) {
         0
      } else {
         val var5: java.util.List = var10000.comp_2400()
         val var6: MatchResult = Regex.find$default(
            Regex("Charges:\\s*(\\d+)/20", RegexOption.IGNORE_CASE), CollectionsKt.joinToString$default(var5, " ", null, null, 0, null, { it: Text ->
               val var10000: java.lang.String = it.getString()
               var10000 as java.lang.CharSequence
            }, 30, null), 0, 2, null
         )
         if (var6 == null) {
            0
         } else {
            val var7: Int = StringsKt.toIntOrNull(var6.getGroupValues().get(1) as java.lang.String)
            var7 ?: 0
         }
      }
   }

   fun renderDBDisplay(context: DrawContext) {
      if (!isPositioningMode) {
         val scr: Screen = this.getMc().field_1755
         if (scr == null || !(scr.getClass().getSimpleName() == "MovableOverlayScreen")) {
            if (!PersistentState.dbDisplayMovable || movableOverlay == null) {
               if (isHoldingDungeonBreaker) {
                  this.renderDBDisplayAt(
                     context, context.method_51421() / 2 + PersistentState.dbDisplayX, context.method_51443() / 2 + PersistentState.dbDisplayY
                  )
               }
            }
         }
      }
   }

   fun renderDBDisplayAt(context: DrawContext, x: Int, y: Int) {
      if (isHoldingDungeonBreaker) {
         val var10000: TextRenderer = this.getMc().field_1772
         context.method_51439(var10000, this.getDisplayTextWithColor(), x, y, -1, true)
      }
   }

   public fun setPositioningMode(enabled: Boolean) {
      isPositioningMode = enabled
   }

   public fun isInPositioningMode(): Boolean {
      return isPositioningMode
   }

   public fun toggleMovableOverlay() {
      PersistentState.dbDisplayMovable = !PersistentState.dbDisplayMovable
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      if (PersistentState.dbDisplayMovable) {
         this.ensureOverlayReadyForToggle()
         val realX: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.dbDisplayX
         val realY: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.dbDisplayY
         if (movableOverlay != null) {
            movableOverlay.register()
         }

         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(realX, realY)
         }
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   private fun ensureOverlayReadyForToggle() {
      if (movableOverlay == null) {
         val var5: MovableOverlay = MovableOverlayManager.INSTANCE
            .createOverlay(
               "dbDisplay",
               "⛏ 20",
               this.getMc().method_22683().method_4486() / 2 + PersistentState.dbDisplayX,
               this.getMc().method_22683().method_4502() / 2 + PersistentState.dbDisplayY,
               50,
               20
            )
            var5.resetFunction = { 
            val var10000: Window = INSTANCE.getMc().method_22683()
            val var5: TextRenderer = INSTANCE.getMc().field_1772
            val ox: Int = -var5.method_1727("⛏ 2") / 2
            val oy: Int = -15 - var5.field_2000
            TuplesKt.to(var10000.method_4486() / 2 + ox, var10000.method_4502() / 2 + oy)
         }
         movableOverlay = var5
         if (movableOverlay != null) {
            movableOverlay.renderFunction = lambda_7@{ ctx: DrawContext, x: Int, y: Int, var3: Float ->
               if (!isHoldingDungeonBreaker) {
                  return@lambda_7 Unit.INSTANCE
               } else {
                  val var10000: TextRenderer = INSTANCE.getMc().field_1772
                  ctx.method_51439(var10000, INSTANCE.getDisplayTextWithColor(), x, y, -1, true)
                  return@lambda_7 Unit.INSTANCE
               }
            }
         }

         if (movableOverlay != null) {
            movableOverlay.onPositionChanged = { x: Int, y: Int ->
               val ox: Int = x - INSTANCE.getMc().method_22683().method_4486() / 2
               val oy: Int = y - INSTANCE.getMc().method_22683().method_4502() / 2
               PersistentState.dbDisplayX = ox
               PersistentState.dbDisplayY = oy
               PersistentState.dbDisplayInitDone = true
               appliedSavedPos = true
               JooonConfigManager.INSTANCE.write("jooonreimagined_state")
               Unit.INSTANCE
            }
         }
      }
   }

   public fun getOverlayPosition(): Pair<Int, Int> {
      if (movableOverlay != null) {
         val var10000: Pair = movableOverlay.getPosition()
         if (var10000 != null) {
            return var10000
         }
      }

      return Pair(PersistentState.dbDisplayX, PersistentState.dbDisplayY)
   }

   public fun onConfigChanged() {
      if (Config.dbDisplayEnabled && PersistentState.dbDisplayMovable) {
         this.ensureOverlayReadyForToggle()
         val realX: Int = this.getMc().method_22683().method_4486() / 2 + PersistentState.dbDisplayX
         val realY: Int = this.getMc().method_22683().method_4502() / 2 + PersistentState.dbDisplayY
         if (movableOverlay != null) {
            movableOverlay.register()
         }

         if (movableOverlay != null) {
            movableOverlay.setPositionSilently(realX, realY)
         }
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   public fun getCurrentCharges(): Int {
      return currentCharges
   }

   fun getDisplayTextWithColor(): Text {
      val color: Formatting = if (currentCharges >= 14) Formatting.field_1060 else (if (currentCharges >= 6) Formatting.field_1065 else Formatting.field_1061)
      val var10000: MutableText = Text.method_43470("⛏ ${currentCharges}").method_27692(color)
      var10000 as Text
   }

   fun getDisplayText(): Text {
      this.getDisplayTextWithColor()
   }
}
