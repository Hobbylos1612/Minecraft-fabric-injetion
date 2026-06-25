package jooon.features.jerry

import java.util.ArrayList
import java.util.concurrent.TimeUnit
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import kotlin.concurrent.ThreadsKt
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.collection.DefaultedList

object MayorDisplay {
   private val mayorCycle: List<jooon.features.jerry.MayorDisplay.Mayor> =
      listOf(
         arrayOf(
            MayorDisplay.Mayor.FINNEGAN,
            MayorDisplay.Mayor.COLE,
            MayorDisplay.Mayor.MARINA,
            MayorDisplay.Mayor.DIANA,
            MayorDisplay.Mayor.AATROX,
            MayorDisplay.Mayor.PAUL
         )
      )
      private val keyphraseMap: Map<String, jooon.features.jerry.MayorDisplay.Mayor> =
      mapOf(
         arrayOf(
            Pair("SLASHED Pricing", MayorDisplay.Mayor.AATROX),
            Pair("Prospection", MayorDisplay.Mayor.COLE),
            Pair("Lucky!", MayorDisplay.Mayor.DIANA),
            Pair("Long Term Investment", MayorDisplay.Mayor.DIAZ),
            Pair("Pelt-pocalypse", MayorDisplay.Mayor.FINNEGAN),
            Pair("Sweet Benevolence", MayorDisplay.Mayor.FOXY),
            Pair("Fishing XP Buff", MayorDisplay.Mayor.MARINA),
            Pair("Marauder", MayorDisplay.Mayor.PAUL)
         )
      )
      private var currentMayor: jooon.features.jerry.MayorDisplay.Mayor?
   private var nextMayorChange: Long
   private var movableOverlay: MovableOverlay?
   private var isPositioningMode: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      this.loadSavedState()
      if (Config.mayorDisplayEnabled) {
         this.initializeMovableOverlay()
      }

      ScreenEvents.AFTER_INIT
         .register(
            { client: MinecraftClient, screen: Screen, scaledWidth: Int, scaledHeight: Int ->
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
         HudRenderCallback.EVENT
         .register(
            { context: DrawContext, tickDelta: RenderTickCounter ->
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
            { client: MinecraftClient ->
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

   private fun initializeMovableOverlay() {
      movableOverlay = MovableOverlayManager.INSTANCE
         .createOverlay("mayorDisplay", "Mayor Display", PersistentState.mayorDisplayX, PersistentState.mayorDisplayY, 200, 20)
         if (movableOverlay != null) {
         movableOverlay.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
            renderMayorDisplayAt(context, x, y)
return Unit
         }
      }

      if (movableOverlay != null) {
         movableOverlay.onPositionChanged = { newX: Int, newY: Int ->
            PersistentState.mayorDisplayX = newX
            PersistentState.mayorDisplayY = newY
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }
      }

      if (movableOverlay != null) {
         movableOverlay.register()
      }
   }

   fun detectMayorFromGUI(screen: HandledScreen) {
      ThreadsKt.thread$default(false, false, null, null, 0, lambda_6@{ 
         try {
            Thread.sleep(100L)



            if (var22 == null) {
               return@lambda_6 Unit
            }

            if (var23.isEmpty()) {
               return@lambda_6 Unit
            }

            val var25: String
            if (loreComponent == null) {
               var25 = ""
            } else {
               val var24: java.util.List = loreComponent.lines()
               val keyphrase: java.lang.Iterable = var24
               val mayor: java.util.Collection = ArrayList(var24.count().coerceAtLeast(10))

               for (`item$iv$iv` in keyphrase) {
                  mayor.add((`item$iv$iv` as Text).getString())
               }

               var25 = mayor as java.util.List.joinToString("")
            }


            for (var18 in keyphraseMap.entrySet()) {

               val var20: MayorDisplay.Mayor = var18.getValue() as MayorDisplay.Mayor
               if (lore.contains(var19, true)) {
                  setCurrentMayor(var20)
                  extractTimerFromLore(lore)
                  saveState()
                  return@lambda_6 Unit
               }
            }
         } catch (var16: Exception) {
         }

         return@lambda_6 Unit
      }, 31, null)
   }

   private fun extractTimerFromLore(lore: String) {

      if (match != null) {
         nextMayorChange = System.currentTimeMillis()
            + TimeUnit.MINUTES
               .toMillis(
                  (
                     Integer.parseInt(match.getGroupValues().get(1) as String) * 60
                        + Integer.parseInt(match.getGroupValues().get(2) as String)
                  ).toLong()
               )
            }
   }

   private fun setCurrentMayor(mayor: jooon.features.jerry.MayorDisplay.Mayor) {
      currentMayor = mayor
   }

   private fun checkMayorChange() {
      if (currentMayor != null && nextMayorChange != 0L) {
         if (System.currentTimeMillis() >= nextMayorChange) {
            val var10000: java.util.List = mayorCycle
            val var10001: MayorDisplay.Mayor = currentMayor
            currentMayor = mayorCycle.get((var10000.indexOf(var10001) + 1) % mayorCycle.size())
            nextMayorChange = 0L
            this.saveState()
         }
      }
   }

   fun renderMayorDisplay(context: DrawContext) {
      if (!isPositioningMode) {

         if (screen == null || !(screen.getClass().getSimpleName() == "MovableOverlayScreen")) {
            if (!PersistentState.mayorDisplayMovable || movableOverlay == null) {
               this.renderMayorDisplayAt(context, PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
            }
         }
      }
   }

   fun renderMayorDisplayAt(context: DrawContext, x: Int, y: Int) {
      var var10000: TextRenderer
      run label54@{
         var10000 = this.getMc().textRenderer
         if (this.getMc().currentScreen != null) {
            run label50@{

               if (var19 != null) {

                  if (var20 != null) {

                     if (var21 != null) {
                        var22 = contains$default(var21, "Chat", false, 2, null)
                        return@label50
                     }
                  }
               }

               var22 = false
            }

            if (var22) {
               var23 = true
               return@label54
            }
         }

         var23 = false
      }

      val var30: MutableText
      if (currentMayor != null) {
         val var25: MayorDisplay.Mayor = currentMayor

         val var10001: MayorDisplay.Mayor = currentMayor


         var unknownText: MutableText = var28
         if (nextMayorChange > 0L) {

            if (timeUntilChange > 0L) {

                  .formatted(Formatting.GRAY)
                  .append(
                     Text.literal("${timeUntilChange / 3600000.toLong()}h ${timeUntilChange % 3600000.toLong() / 60000.toLong()}m")
                        .formatted(Formatting.YELLOW) as Text
                  )
                  .append(Text.literal(")").formatted(Formatting.GRAY) as Text)
                  unknownText = var28.append(var29 as Text)
            }
         }

         var30 = unknownText
      } else {


         var30 = var17
      }

      context.drawText(var10000, var30 as Text, x, y, -1, true)
      if (var23 && currentMayor != null) {
         this.renderUpcomingMayorsAt(context, var10000, x, y)
      }
   }

   fun renderUpcomingMayors(context: DrawContext, textRenderer: TextRenderer) {
      this.renderUpcomingMayorsAt(context, textRenderer, PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
   }

   fun renderUpcomingMayorsAt(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int) {
      if (currentMayor != null) {
         val upcomingMayors: java.util.List = this.getUpcomingMayors(getMayorIndex(currentMayor))
         var yOffset: Int = y + textRenderer.fontHeight + 5
         val var8: java.util.Iterator = upcomingMayors.iterator()
         var var20: Int = 0

         while (var8.hasNext()) {

            val mayor: MayorDisplay.Mayor = var8.next() as MayorDisplay.Mayor


               Text.literal("> ")
                  .formatted(Formatting.WHITE)
                  .append(Text.literal(mayor.displayName).formatted(mayor.getColor()) as Text)
                  .append(Text.literal(" (In ").formatted(Formatting.GRAY) as Text)
                  .append(
                     Text.literal("${timeUntil / 3600000.toLong()}h ${timeUntil % 3600000.toLong() / 60000.toLong()}m").formatted(Formatting.YELLOW) as Text
                  )
                  .append(Text.literal(")").formatted(Formatting.GRAY) as Text)
return else
               Text.literal("> ")
                  .formatted(Formatting.WHITE)
                  .append(Text.literal(mayor.displayName).formatted(mayor.getColor()) as Text)
                  .append(Text.literal(" (Unknown time)").formatted(Formatting.GRAY) as Text)
                  context.drawText(textRenderer, var10000 as Text, x, yOffset, -1, true)
            yOffset += textRenderer.fontHeight + 2
         }
      }
   }

   private fun getMayorIndex(mayor: jooon.features.jerry.MayorDisplay.Mayor): Int {
      var var10000: Byte
      when (MayorDisplay.WhenMappings.$EnumSwitchMapping$0[mayor.ordinal()]) {
         1 -> var10000 = 0
         2 -> var10000 = 1
         3 -> var10000 = 2
         4 -> var10000 = 3
         5 -> var10000 = 4
         6 -> var10000 = 5
         7 -> var10000 = -1
         8 -> var10000 = -1
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   private fun getUpcomingMayors(currentIndex: Int): List<jooon.features.jerry.MayorDisplay.Mayor> {
      val rotation: java.util.List = listOf(
         arrayOf(
            MayorDisplay.Mayor.FINNEGAN,
            MayorDisplay.Mayor.COLE,
            MayorDisplay.Mayor.MARINA,
            MayorDisplay.Mayor.DIANA,
            MayorDisplay.Mayor.AATROX,
            MayorDisplay.Mayor.PAUL
         )
      )
      val var6: java.util.List = ArrayList()

      for (i in 1..5) {
         var6.add(rotation.get((currentIndex + i) % 6))
      }

      return var6
   }

   private fun calculateTimeUntilMayor(mayorPosition: Int): Long {
      return if (nextMayorChange <= 0L) 0L else Math.max(0L, nextMayorChange - System.currentTimeMillis() + (mayorPosition - 1).toLong() * 21600000L)
   }

   fun getCurrentMayor(): jooon.features.jerry.MayorDisplay.Mayor? {
      return currentMayor
   }

   fun getCurrentMayorText(): Text {

      val var9: Text
      if (currentMayor != null) {
         val var6: MayorDisplay.Mayor = currentMayor

         val var10001: MayorDisplay.Mayor = currentMayor


         var9 = var2 as Text
      } else {


         var9 = var4 as Text
      }
return var9
   }

   fun setPositioningMode(enabled: Boolean) {
      isPositioningMode = enabled
   }

   fun isInPositioningMode(): Boolean {
      return isPositioningMode
   }

   private fun saveState() {
      var var10000: String
      run label15@{
         if (currentMayor != null) {
            var10000 = currentMayor.name()
            if (var10000 != null) {
               return@label15
            }
         }

         var10000 = ""
      }

      PersistentState.currentMayor = var10000
      PersistentState.nextMayorChange = nextMayorChange
      JooonConfigManager.write("jooonreimagined_state")
   }

   fun toggleMovableOverlay() {
      PersistentState.mayorDisplayMovable = !PersistentState.mayorDisplayMovable
      JooonConfigManager.write("jooonreimagined_state")
      if (PersistentState.mayorDisplayMovable) {
         if (movableOverlay == null) {
            this.initializeMovableOverlay()
         } else if (movableOverlay != null) {
            movableOverlay.register()
         }
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   fun getOverlayPosition(): Pair<Int, Int> {
      if (movableOverlay != null) {

         if (var10000 != null) {
            return var10000
         }
      }

      return Pair(PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
   }

   fun onConfigChanged() {
      if (Config.mayorDisplayEnabled) {
         if (movableOverlay == null) {
            this.initializeMovableOverlay()
         } else if (movableOverlay != null) {
            movableOverlay.register()
         }
      } else if (movableOverlay != null) {
         movableOverlay.unregister()
      }
   }

   private fun loadSavedState() {
      if (PersistentState.currentMayor.length() > 0) {
         try {
            currentMayor = MayorDisplay.Mayor.valueOf(PersistentState.currentMayor)
         } catch (var4: IllegalArgumentException) {
            currentMayor = null
         }
      }

      nextMayorChange = PersistentState.nextMayorChange
      if (nextMayorChange > 0L && System.currentTimeMillis() >= nextMayorChange && currentMayor != null) {
         val var10000: java.util.List = mayorCycle
         val var10001: MayorDisplay.Mayor = currentMayor
         currentMayor = mayorCycle.get(
            (var10000.indexOf(var10001) + ((System.currentTimeMillis() - nextMayorChange) / TimeUnit.MINUTES.toMillis(1200L)).toInt() + 1) % mayorCycle.size()
         )
         nextMayorChange = 0L
         this.saveState()
      }
   }

   enum class Mayor {
      AATROX("Aatrox", Formatting.RED),
      COLE("Cole", Formatting.BLUE),
      DIANA("Diana", Formatting.LIGHT_PURPLE),
      DIAZ("Diaz", Formatting.GOLD),
      FINNEGAN("Finnegan", Formatting.YELLOW),
      FOXY("Foxy", Formatting.DARK_PURPLE),
      MARINA("Marina", Formatting.AQUA),
      PAUL("Paul", Formatting.RED);

      val displayName: String
      private Formatting color;

      fun Mayor(displayName: String, color: Formatting) {
         this.displayName = displayName
         this.color = color
      }

      fun getColor(): Formatting {
         this.color
      }

      
      fun getEntries(): EnumEntries<MayorDisplay.Mayor> {
         $ENTRIES
      }
   }
}
