package jooon.features.dojo

import java.util.ArrayList
import java.util.Arrays
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.features.other.Melody
import jooon.gui.MovableOverlayScreen
import jooon.mixins.OptionsAccessor
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import jooon.util.ScoreboardUtil
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.option.GameOptions
import net.minecraft.text.Text

object AutoDojo {
   private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
   var isDojoActive: Boolean
   private var currentChallenge: jooon.features.dojo.AutoDojo.Challenge?
   private var scanTimer: Int
   private var savedPauseOnLostFocus: Boolean?
   private var challengeStartedAtMs: Long
   private var overlayReady: Boolean
   private var appliedSavedPosition: Boolean
   var subtitle: String = ""
   private val pointsRegex: Regex = Regex("Points:\\s*(-?[\\d,]+)(?:\\s*\\([^)]+\\))?", RegexOption.IGNORE_CASE)
   private val timeBankRegex: Regex =
      Regex("(?:Time|Time Bank):\\s*([0-9]+(?:\\.[0-9]+)?s?)(?:\\s*\\(([+-]?[0-9]+(?:\\.[0-9]+)?s?)\\))?", RegexOption.IGNORE_CASE)
      private val colorCodeRegex: Regex = Regex("§.")
   private const val HUD_LABEL_COLOR: Int = -1769606
   private const val HUD_VALUE_COLOR: Int = -3136
   private const val HUD_TPS_GOOD_COLOR: Int = -9240718
   private const val HUD_TPS_WARN_COLOR: Int = -11174
   private const val HUD_TPS_BAD_COLOR: Int = -39065

   fun getMc(): MinecraftClient {
return var10000
   }

   private val enabledChallenges: List<jooon.features.dojo.AutoDojo.Challenge>
      private get() {
         val `this$iv$iv`: java.lang.Iterable = AutoDojo.Challenge.getEntries() as java.lang.Iterable
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if (`element$iv$iv` as AutoDojo.Challenge != AutoDojo.Challenge.STAMINA) {
               `destination$iv$iv`.add(`element$iv$iv`)
            }
         }

         return `destination$iv$iv` as MutableList<AutoDojo.Challenge>
      }


   fun init() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         reset()
         if (Config.Companion.dojoChallengeTrackingEnabled()) {
            scheduler.schedule({ 
               if (getMc().world != null && Config.Companion.dojoChallengeTrackingEnabled()) {
                  checkScoreboardForDojo()
               }
            }, 10L, TimeUnit.SECONDS)
         }
      })
      ClientReceiveMessageEvents.GAME
         .register(
            { message: Text, var1: Boolean ->
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
         ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         ensureOverlayReady()
         if (!Config.Companion.dojoChallengeTrackingEnabled()) {
            if (isDojoActive) {
               reset()
            }

            restorePauseOnLostFocus()
         } else {
            if (scanTimer > 0) {
               scanTimer += -1
               if (checkScoreboardForDojo()) {
                  isDojoActive = true
                  challengeStartedAtMs = System.currentTimeMillis()
                  scanTimer = 0
               }
            }

            if (isDojoActive && !checkScoreboardForDojo()) {
               reset()
            }

            updateBackgroundAutomationPolicy()
         }
      })
   }

   private fun startScanning(challenge: jooon.features.dojo.AutoDojo.Challenge) {
      currentChallenge = challenge
      challengeStartedAtMs = 0L
      scanTimer = 60
   }

   private fun checkScoreboardForDojo(): Boolean {
      val `this$iv`: java.lang.Iterable = ScoreboardUtil.getSidebarLines()
      var var10000: Boolean
      if (`this$iv` is java.util.Collection && (`this$iv` as java.util.Collection).isEmpty()) {
         var10000 = false
      } else {
         val var3: java.util.Iterator = `this$iv`.iterator()

         while (true) {
            if (!var3.hasNext()) {
               var10000 = false
break
            }

            if (contains(var3.next() as String, "Dojo Arena", true)) {
               var10000 = true
break
            }
         }
      }

      return var10000
   }

   private fun reset() {
      isDojoActive = false
      currentChallenge = null
      scanTimer = 0
      challengeStartedAtMs = 0L
      subtitle = ""
      this.restorePauseOnLostFocus()
   }

   fun isChallengeActive(challenge: jooon.features.dojo.AutoDojo.Challenge): Boolean {
      return challenge != AutoDojo.Challenge.STAMINA && isDojoActive && currentChallenge === challenge
   }

   fun canRunWithCurrentScreen(): Boolean {

      return var10000 == null || this.isBackgroundAutomationActive() && !this.getMc().isWindowFocused() && var10000 is GameMenuScreen
   }

   fun isAutomationBlockedByScreen(): Boolean {
      return !this.canRunWithCurrentScreen()
   }

   private fun isBackgroundAutomationActive(): Boolean {
      return Config.Companion.dojoChallengeTrackingEnabled() && isDojoActive && currentChallenge != null
   }

   private fun updateBackgroundAutomationPolicy() {
      if (!this.isBackgroundAutomationActive()) {
         this.restorePauseOnLostFocus()
      } else {


         if (savedPauseOnLostFocus == null) {
            savedPauseOnLostFocus = options.jooonGetPauseOnLostFocus()
         }

         if (options.jooonGetPauseOnLostFocus()) {
            options.jooonSetPauseOnLostFocus(false)
         }

         if (!this.getMc().isWindowFocused() && this.getMc().currentScreen is GameMenuScreen) {
            this.getMc().setScreen(null)
         }
      }
   }

   private fun restorePauseOnLostFocus() {
      if (savedPauseOnLostFocus != null) {

         savedPauseOnLostFocus = null


         try {

            (var10000 as OptionsAccessor).jooonSetPauseOnLostFocus(original)

         } catch (var5: java.lang.Throwable) {
            val `this24lambda_u246`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
         }
      }
   }

   private fun ensureOverlayReady() {
      if (this.getMc().getWindow() != null) {
         if (!overlayReady) {

               Pair(PersistentState.dojoHudX, PersistentState.dojoHudY)
return else
               this.defaultHudPosition()
               var var10000: MovableOverlay = MovableOverlayManager.getOverlay("dojoHud")
            if (var10000 == null) {
               var10000 = MovableOverlayManager.INSTANCE
                  .createOverlay(
                     "dojoHud",
                     "Dojo HUD",
                     (defaultPosition.getFirst() as java.lang.Number).intValue(),
                     (defaultPosition.getSecond() as java.lang.Number).intValue(),
                     190,
return 78
                  )
               }

            var10000.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
               renderHud(context, x, y)
return Unit
            }
            var10000.onPositionChanged = { x: Int, y: Int ->
               PersistentState.dojoHudX = x
               PersistentState.dojoHudY = y
               PersistentState.dojoHudInitDone = true
               JooonConfigManager.write("jooonreimagined_state")
return Unit
            }
            var10000.resetFunction = { 
               defaultHudPosition()
            }
            var10000.register()
            overlayReady = true
         }

         if (!appliedSavedPosition) {

            if (var3 != null) {
               var3.setPositionSilently(PersistentState.dojoHudX, PersistentState.dojoHudY)
            }

            appliedSavedPosition = true
         }
      }
   }

   private fun hudLines(challenge: jooon.features.dojo.AutoDojo.Challenge, snapshot: jooon.features.dojo.AutoDojo.ScoreboardSnapshot): List<
         jooon.features.dojo.AutoDojo.HudLine
      > {
      val lines: java.util.List = mutableListOf(
         arrayOf(
            this.hudLine("TPS: ", this.stripColor(Melody.coloredTpsValue()), this.tpsColor(Melody.coloredTpsValue())),
            hudLine$default(this, "Time elapsed: ", this.formatElapsedTime(), 0, 4, null)
         )
      )
      if (challenge === AutoDojo.Challenge.CONTROL) {
         val var5: java.util.Collection = lines
         var var10002: String = snapshot.timeBank
         if (var10002 == null) {
            var10002 = "?"
         }

         var5.add(hudLine$default(this, "Time Bank: ", var10002, 0, 4, null))
      }

      val var6: java.util.Collection = lines
      var var7: String = snapshot.points
      if (var7 == null) {
         var7 = "?"
      }

      var6.add(hudLine$default(this, "Points: ", var7, 0, 4, null))
      return lines
   }

   private fun readScoreboardSnapshot(): jooon.features.dojo.AutoDojo.ScoreboardSnapshot {
      var points: Any = null
      var timeBank: Any = null

      for (line in ScoreboardUtil.getSidebarLines()) {

         if (points == null) {

            if (var10000 != null) {
               points = replace$default(var10000.getGroupValues().get(1) as String, ",", "", false, 4, null)
            }
         }

         if (timeBank == null) {

            if (var19 != null) {
               var value: String
               run label61@{
                  value = ensureSecondsSuffix(var19.getGroupValues().get(1) as String)

                  if (var20 != null) {

                     if (p0x != null) {
                        var22 = ensureSecondsSuffix(p0x)
                        return@label61
                     }
                  }

                  var22 = null
               }

               timeBank = if (var22 == null) value else "$value ($var22)"
            }
         }
      }

      return AutoDojo.ScoreboardSnapshot((String)points, (String)timeBank)
   }

   private fun formatElapsedTime(): String {
      if (challengeStartedAtMs <= 0L) {
         return "0:00"
      } else {




         val var10: Array<Any> = arrayOf(minutes, seconds)

         return var10000
      }
   }

   private fun ensureSecondsSuffix(value: String): String {
      return if (endsWith(value, "s", true)) value else "$values"
   }

   private fun isFullyAutomatic(challenge: jooon.features.dojo.AutoDojo.Challenge): Boolean {
      var var10000: Boolean
      when (AutoDojo.WhenMappings.$EnumSwitchMapping$0[challenge.ordinal()]) {
         1 -> var10000 = Config.fullyAutomaticControl
         2 -> var10000 = Config.fullyAutomaticMastery
         3 -> var10000 = Config.fullyAutomaticDiscipline
         4 -> var10000 = Config.fullyAutomaticSwiftness
         5, 6 -> var10000 = false
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   private fun defaultHudPosition(): Pair<Int, Int> {
      return Pair(12, 72)
   }

   private fun stripColor(text: String): String {
      return colorCodeRegex.replace(text, "")
   }

   fun drawHudText(context: DrawContext, text: String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.drawText(this.getMc().textRenderer, text, x + 1, y + 1, shadowColor, false)
      context.drawText(this.getMc().textRenderer, text, x, y, color, false)
   }

   private fun hudLine(label: String, value: String, valueColor: Int = -3136): jooon.features.dojo.AutoDojo.HudLine {
      return AutoDojo.HudLine(listOf(arrayOf(AutoDojo.HudSegment(label, -1769606), AutoDojo.HudSegment(value, valueColor))))
   }

   private fun lineWidth(line: jooon.features.dojo.AutoDojo.HudLine): Int {
      val var2: java.lang.Iterable = line.segments
      var var3: Int = 0

      for (var5 in var2) {
         var3 += getMc().textRenderer.getWidth((var5 as AutoDojo.HudSegment).text)
      }

      return var3
   }

   fun drawHudLine(context: DrawContext, line: AutoDojo.HudLine, x: Int, y: Int, shadowColor: Int) {
      var cursorX: Int = x

      for (segment in line.segments) {
         context.drawText(this.getMc().textRenderer, segment.text, cursorX + 1, y + 1, shadowColor, false)
         cursorX += this.getMc().textRenderer.getWidth(segment.text)
      }

      cursorX = x

      for (var11 in line.segments) {
         context.drawText(this.getMc().textRenderer, var11.text, cursorX, y, var11.color, false)
         cursorX += this.getMc().textRenderer.getWidth(var11.text)
      }
   }

   private fun tpsColor(coloredTps: String): Int {
      return if (startsWith$default(coloredTps, "§a", false, 2, null))
         -9240718
return else
         (
            if (startsWith$default(coloredTps, "§e", false, 2, null))
               -11174
return else
               (if (startsWith$default(coloredTps, "§c", false, 2, null)) -39065 else -3136)
         )
      }

   fun renderSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {




      if (right > left) {
         context.fill(left, y, right, y + height, 872395584)
      }
   }

   enum class Challenge(keyphrase: String) {
      FORCE("Force"),
      STAMINA("Stamina"),
      MASTERY("Mastery"),
      DISCIPLINE("Discipline"),
      SWIFTNESS("Swiftness"),
      CONTROL("Control");

      val keyphrase: String

      init {
         this.keyphrase = keyphrase
      }

      
      fun getEntries(): EnumEntries<AutoDojo.Challenge> {
         $ENTRIES
      }
   }

   private data class HudLine(segments: List<jooon.features.dojo.AutoDojo.HudSegment>) {
      val segments: List<jooon.features.dojo.AutoDojo.HudSegment>

      init {
         this.segments = segments
      }

      public operator fun component1(): List<jooon.features.dojo.AutoDojo.HudSegment> {
         return this.segments
      }

      fun copy(segments: List<jooon.features.dojo.AutoDojo.HudSegment> = this.segments): jooon.features.dojo.AutoDojo.HudLine {
         return AutoDojo.HudLine(segments)
      }

      override fun toString(): String {
         return "HudLine(segments=${this.segments})"
      }

      override fun hashCode(): Int {
         return this.segments.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is AutoDojo.HudLine && this.segments == (other as AutoDojo.HudLine).segments
         }
      }
   }

   private data class HudSegment(text: String, color: Int) {
      val text: String
      val color: Int

      init {
         this.text = text
         this.color = color
      }

      public operator fun component1(): String {
         return this.text
      }

      public operator fun component2(): Int {
         return this.color
      }

      fun copy(text: String = this.text, color: Int = this.color): jooon.features.dojo.AutoDojo.HudSegment {
         return AutoDojo.HudSegment(text, color)
      }

      override fun toString(): String {
         return "HudSegment(text=${this.text}, color=${this.color})"
      }

      override fun hashCode(): Int {
         return this.text.hashCode() * 31 + Integer.hashCode(this.color)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutoDojo.HudSegment && this.text == (other as AutoDojo.HudSegment).text && this.color == (other as AutoDojo.HudSegment).color
         }
      }
   }

   private data class ScoreboardSnapshot(points: String?, timeBank: String?) {
      val points: String?
      val timeBank: String?

      init {
         this.points = points
         this.timeBank = timeBank
      }

      public operator fun component1(): String? {
         return this.points
      }

      public operator fun component2(): String? {
         return this.timeBank
      }

      fun copy(points: String? = this.points, timeBank: String? = this.timeBank): jooon.features.dojo.AutoDojo.ScoreboardSnapshot {
         return AutoDojo.ScoreboardSnapshot(points, timeBank)
      }

      override fun toString(): String {
         return "ScoreboardSnapshot(points=${this.points}, timeBank=${this.timeBank})"
      }

      override fun hashCode(): Int {
         return (if (this.points == null) 0 else this.points.hashCode()) * 31 + (if (this.timeBank == null) 0 else this.timeBank.hashCode())
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutoDojo.ScoreboardSnapshot
               && this.points == (other as AutoDojo.ScoreboardSnapshot).points
               && this.timeBank == (other as AutoDojo.ScoreboardSnapshot).timeBank
            }
      }
   }
}
