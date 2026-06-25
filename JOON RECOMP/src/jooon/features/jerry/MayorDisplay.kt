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
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nMayorDisplay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MayorDisplay.kt\njooon/features/jerry/MayorDisplay\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,467:1\n1#2:468\n1549#3:469\n1620#3,3:470\n*S KotlinDebug\n*F\n+ 1 MayorDisplay.kt\njooon/features/jerry/MayorDisplay\n*L\n149#1:469\n149#1:470,3\n*E\n"])
public object MayorDisplay {
   private final val mayorCycle: List<jooon.features.jerry.MayorDisplay.Mayor> =
      CollectionsKt.listOf(
         arrayOf(
            MayorDisplay.Mayor.FINNEGAN,
            MayorDisplay.Mayor.COLE,
            MayorDisplay.Mayor.MARINA,
            MayorDisplay.Mayor.DIANA,
            MayorDisplay.Mayor.AATROX,
            MayorDisplay.Mayor.PAUL
         )
      )
      private final val keyphraseMap: Map<String, jooon.features.jerry.MayorDisplay.Mayor> =
      MapsKt.mapOf(
         arrayOf(
            TuplesKt.to("SLASHED Pricing", MayorDisplay.Mayor.AATROX),
            TuplesKt.to("Prospection", MayorDisplay.Mayor.COLE),
            TuplesKt.to("Lucky!", MayorDisplay.Mayor.DIANA),
            TuplesKt.to("Long Term Investment", MayorDisplay.Mayor.DIAZ),
            TuplesKt.to("Pelt-pocalypse", MayorDisplay.Mayor.FINNEGAN),
            TuplesKt.to("Sweet Benevolence", MayorDisplay.Mayor.FOXY),
            TuplesKt.to("Fishing XP Buff", MayorDisplay.Mayor.MARINA),
            TuplesKt.to("Marauder", MayorDisplay.Mayor.PAUL)
         )
      )
      private final var currentMayor: jooon.features.jerry.MayorDisplay.Mayor?
   private final var nextMayorChange: Long
   private final var movableOverlay: MovableOverlay?
   private final var isPositioningMode: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
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
            INSTANCE.renderMayorDisplayAt(context, x, y)
            Unit.INSTANCE
         }
      }

      if (movableOverlay != null) {
         movableOverlay.onPositionChanged = { newX: Int, newY: Int ->
            PersistentState.mayorDisplayX = newX
            PersistentState.mayorDisplayY = newY
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }
      }

      if (movableOverlay != null) {
         movableOverlay.register()
      }
   }

   fun detectMayorFromGUI(screen: HandledScreen<*>) {
      ThreadsKt.thread$default(false, false, null, null, 0, lambda_6@{ 
         try {
            Thread.sleep(100L)
            val var10000: ScreenHandler = `$screen`.method_17577()
            val var21: DefaultedList = var10000.field_7761
            val var22: Slot = CollectionsKt.getOrNull(var21 as java.util.List, 37) as Slot
            if (var22 == null) {
               return@lambda_6 Unit.INSTANCE
            }

            val var23: ItemStack = var22.method_7677()
            if (var23.method_7960()) {
               return@lambda_6 Unit.INSTANCE
            }

            val loreComponent: LoreComponent = var23.method_58694(DataComponentTypes.field_49632) as LoreComponent
            val var25: java.lang.String
            if (loreComponent == null) {
               var25 = ""
            } else {
               val var24: java.util.List = loreComponent.comp_2400()
               val keyphrase: java.lang.Iterable = var24
               val mayor: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var24, 10))

               for (`item$iv$iv` in keyphrase) {
                  mayor.add((`item$iv$iv` as Text).getString())
               }

               var25 = CollectionsKt.joinToString$default(mayor as java.util.List, " ", null, null, 0, null, null, 62, null)
            }

            val lore: java.lang.String = var25

            for (var18 in keyphraseMap.entrySet()) {
               val var19: java.lang.String = var18.getKey() as java.lang.String
               val var20: MayorDisplay.Mayor = var18.getValue() as MayorDisplay.Mayor
               if (StringsKt.contains(lore, var19, true)) {
                  INSTANCE.setCurrentMayor(var20)
                  INSTANCE.extractTimerFromLore(lore)
                  INSTANCE.saveState()
                  return@lambda_6 Unit.INSTANCE
               }
            }
         } catch (var16: Exception) {
         }

         return@lambda_6 Unit.INSTANCE
      }, 31, null)
   }

   private fun extractTimerFromLore(lore: String) {
      val match: MatchResult = Regex.find$default(Regex("Next set of perks in (\\d+)h (\\d+)m"), lore, 0, 2, null)
      if (match != null) {
         nextMayorChange = System.currentTimeMillis()
            + TimeUnit.MINUTES
               .toMillis(
                  (long)(
                     Integer.parseInt(match.getGroupValues().get(1) as java.lang.String) * 60
                        + Integer.parseInt(match.getGroupValues().get(2) as java.lang.String)
                  )
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
         val screen: Screen = this.getMc().field_1755
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
         var10000 = this.getMc().field_1772
         if (this.getMc().field_1755 != null) {
            run label50@{
               val var19: Screen = this.getMc().field_1755
               if (var19 != null) {
                  val var20: Class = var19.getClass()
                  if (var20 != null) {
                     val var21: java.lang.String = var20.getSimpleName()
                     if (var21 != null) {
                        var22 = StringsKt.contains$default(var21, "Chat", false, 2, null)
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

      val var24: MutableText = Text.method_43470("Current Perkpocalypse Mayor: ").method_27692(Formatting.field_1054)
      val var30: MutableText
      if (currentMayor != null) {
         val var25: MayorDisplay.Mayor = currentMayor
         val var26: MutableText = Text.method_43470(var25.displayName)
         val var10001: MayorDisplay.Mayor = currentMayor
         val var27: MutableText = var26.method_27692(var10001.getColor())
         val var28: MutableText = var24.method_27661().method_10852(var27 as Text)
         var unknownText: MutableText = var28
         if (nextMayorChange > 0L) {
            val timeUntilChange: Long = nextMayorChange - System.currentTimeMillis()
            if (timeUntilChange > 0L) {
               val var29: MutableText = Text.method_43470(" (Next one in ")
                  .method_27692(Formatting.field_1080)
                  .method_10852(
                     Text.method_43470("${timeUntilChange / (long)3600000}h ${timeUntilChange % (long)3600000 / (long)60000}m")
                        .method_27692(Formatting.field_1054) as Text
                  )
                  .method_10852(Text.method_43470(")").method_27692(Formatting.field_1080) as Text)
                  unknownText = var28.method_10852(var29 as Text)
            }
         }

         var30 = unknownText
      } else {
         val var31: MutableText = Text.method_43470("Unknown! Please open your Skyblock Calendar.").method_27692(Formatting.field_1080)
         val var17: MutableText = var24.method_27661().method_10852(var31 as Text)
         var30 = var17
      }

      context.method_51439(var10000, var30 as Text, x, y, -1, true)
      if (var23 && currentMayor != null) {
         this.renderUpcomingMayorsAt(context, var10000, x, y)
      }
   }

   fun renderUpcomingMayors(context: DrawContext, textRenderer: TextRenderer) {
      this.renderUpcomingMayorsAt(context, textRenderer, PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
   }

   fun renderUpcomingMayorsAt(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int) {
      if (currentMayor != null) {
         val upcomingMayors: java.util.List = this.getUpcomingMayors(INSTANCE.getMayorIndex(currentMayor))
         var yOffset: Int = y + textRenderer.field_2000 + 5
         val var8: java.util.Iterator = upcomingMayors.iterator()
         var var20: Int = 0

         while (var8.hasNext()) {
            val var21: Int = var20++
            val mayor: MayorDisplay.Mayor = var8.next() as MayorDisplay.Mayor
            val timeUntil: Long = this.calculateTimeUntilMayor(var21 + 1)
            val var10000: MutableText = if (timeUntil > 0L)
               Text.method_43470("> ")
                  .method_27692(Formatting.field_1068)
                  .method_10852(Text.method_43470(mayor.displayName).method_27692(mayor.getColor()) as Text)
                  .method_10852(Text.method_43470(" (In ").method_27692(Formatting.field_1080) as Text)
                  .method_10852(
                     Text.method_43470("${timeUntil / (long)3600000}h ${timeUntil % (long)3600000 / (long)60000}m").method_27692(Formatting.field_1054) as Text
                  )
                  .method_10852(Text.method_43470(")").method_27692(Formatting.field_1080) as Text)
               else
               Text.method_43470("> ")
                  .method_27692(Formatting.field_1068)
                  .method_10852(Text.method_43470(mayor.displayName).method_27692(mayor.getColor()) as Text)
                  .method_10852(Text.method_43470(" (Unknown time)").method_27692(Formatting.field_1080) as Text)
                  context.method_51439(textRenderer, var10000 as Text, x, yOffset, -1, true)
            yOffset += textRenderer.field_2000 + 2
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
      val rotation: java.util.List = CollectionsKt.listOf(
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
      return if (nextMayorChange <= 0L) 0L else Math.max(0L, nextMayorChange - System.currentTimeMillis() + (long)(mayorPosition - 1) * 21600000L)
   }

   public fun getCurrentMayor(): jooon.features.jerry.MayorDisplay.Mayor? {
      return currentMayor
   }

   fun getCurrentMayorText(): Text {
      val var10000: MutableText = Text.method_43470("Current Perkpocalypse Mayor: ").method_27692(Formatting.field_1054)
      val var9: Text
      if (currentMayor != null) {
         val var6: MayorDisplay.Mayor = currentMayor
         val var7: MutableText = Text.method_43470(var6.displayName)
         val var10001: MayorDisplay.Mayor = currentMayor
         val var8: MutableText = var7.method_27692(var10001.getColor())
         val var2: MutableText = var10000.method_27661().method_10852(var8 as Text)
         var9 = var2 as Text
      } else {
         val var10: MutableText = Text.method_43470("Unknown! Please open your Skyblock Calendar.").method_27692(Formatting.field_1080)
         val var4: MutableText = var10000.method_27661().method_10852(var10 as Text)
         var9 = var4 as Text
      }

      var9
   }

   public fun setPositioningMode(enabled: Boolean) {
      isPositioningMode = enabled
   }

   public fun isInPositioningMode(): Boolean {
      return isPositioningMode
   }

   private fun saveState() {
      var var10000: java.lang.String
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
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
   }

   public fun toggleMovableOverlay() {
      PersistentState.mayorDisplayMovable = !PersistentState.mayorDisplayMovable
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
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

   public fun getOverlayPosition(): Pair<Int, Int> {
      if (movableOverlay != null) {
         val var10000: Pair = movableOverlay.getPosition()
         if (var10000 != null) {
            return var10000
         }
      }

      return Pair(PersistentState.mayorDisplayX, PersistentState.mayorDisplayY)
   }

   public fun onConfigChanged() {
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
            (var10000.indexOf(var10001) + (int)((System.currentTimeMillis() - nextMayorChange) / TimeUnit.MINUTES.toMillis(1200L)) + 1) % mayorCycle.size()
         )
         nextMayorChange = 0L
         this.saveState()
      }
   }

   public enum class Mayor {
      AATROX("Aatrox", Formatting.field_1061),
      COLE("Cole", Formatting.field_1078),
      DIANA("Diana", Formatting.field_1076),
      DIAZ("Diaz", Formatting.field_1065),
      FINNEGAN("Finnegan", Formatting.field_1054),
      FOXY("Foxy", Formatting.field_1064),
      MARINA("Marina", Formatting.field_1075),
      PAUL("Paul", Formatting.field_1061);

      public final val displayName: String
      private Formatting color;

      fun Mayor(displayName: java.lang.String, color: Formatting) {
         this.displayName = displayName
         this.color = color
      }

      fun getColor(): Formatting {
         this.color
      }

      @JvmStatic
      fun getEntries(): EnumEntries<MayorDisplay.Mayor> {
         $ENTRIES
      }
   }
}
