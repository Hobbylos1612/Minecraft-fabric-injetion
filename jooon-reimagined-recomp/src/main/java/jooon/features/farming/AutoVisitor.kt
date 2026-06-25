package jooon.features.farming

import java.util.Arrays
import java.util.Locale
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.util.RenderUtils
import jooon.util.ScoreboardUtil
import kotlin.math.MathKt
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.entity.Entity
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

object AutoVisitor {
   private const val PAD_HALF_SIZE: Double = 0.48
   private const val PAD_HEIGHT: Double = 0.06
   private const val DEFAULT_MAX_SPEND_COINS: Long = 500000L
   private var suppressPreviewPlayerRender: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun prefixed(message: Text): Text {

      var10000 as Text
   }

   fun sendChat(message: Text) {

      if (var10000 != null) {
         var10000.sendMessage(this.prefixed(message), false)
      }
   }

   fun sendOverlay(message: Text) {

      if (var10000 != null) {
         var10000.sendMessage(this.prefixed(message), true)
      }
   }

   fun init() {
      WorldRenderEvents.END_MAIN
         .register(
            { context: WorldRenderContext ->
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

   fun openSetupFromConfig(parentScreen: Screen?) {
      if (this.getMc().player != null) {
         if (!this.isInGarden()) {

            this.sendChat(var3 as Text)
         } else {

               .formatted(Formatting.YELLOW)
               this.sendChat(var10001 as Text)
            this.getMc().execute({ 
               getMc().setScreen(AutoVisitorPadScreen(`$parentScreen`, !PersistentState.autoVisitorSetupCompleted))
            })
         }
      }
   }

   fun resetForTestingFromConfig() {
      if (this.getMc().player != null) {
         if (!this.isInGarden()) {

            this.sendChat(var2 as Text)
         } else {
            this.clearAllState()

            this.sendOverlay(var10001 as Text)
         }
      }
   }

   fun placePlateAtPlayerFeet(): Boolean {

      if (var10000 == null) {
         return false
      } else if (!this.isInGarden()) {

         this.sendChat(var9 as Text)
         return false
      } else if (!var10000.getAbilities().flying && var10000.isOnGround()) {



         PersistentState.autoVisitorPadX = padX
         PersistentState.autoVisitorPadY = padY
         PersistentState.autoVisitorPadZ = padZ
         PersistentState.autoVisitorConfigured = true
         PersistentState.autoVisitorPadPlaced = true
         Config.autoVisitorEnabled = true
         JooonConfigManager.write("jooonreimagined")
         JooonConfigManager.write("jooonreimagined_state")

         this.sendOverlay(var8 as Text)
         return true
      } else {

         this.sendChat(var10001 as Text)
         return false
      }
   }

   fun clearPadOnly() {
      PersistentState.autoVisitorPadX = 0.0
      PersistentState.autoVisitorPadY = 0.0
      PersistentState.autoVisitorPadZ = 0.0
      PersistentState.autoVisitorConfigured = false
      PersistentState.autoVisitorPadPlaced = false
      JooonConfigManager.write("jooonreimagined_state")
   }

   fun saveRulesFromUi(
      acceptAll: Boolean,
      ignoreSpaceman: Boolean,
      maxSpendCoins: Long,
      trySacksFirst: Boolean,
      rareItemsOnly: Boolean,
      minFarmingXp: Long
   ) {
      PersistentState.autoVisitorAcceptAll = acceptAll
      PersistentState.autoVisitorIgnoreSpaceman = ignoreSpaceman
      PersistentState.autoVisitorMaxSpendCoins = (maxSpendCoins).coerceAtLeast(0L)
      PersistentState.autoVisitorTrySacksFirst = trySacksFirst
      PersistentState.autoVisitorRareItemsOnly = rareItemsOnly
      PersistentState.autoVisitorMinFarmingXp = (minFarmingXp).coerceAtLeast(0L)
      PersistentState.autoVisitorSetupCompleted = true
      Config.autoVisitorEnabled = true
      JooonConfigManager.write("jooonreimagined")
      JooonConfigManager.write("jooonreimagined_state")

         .styled({ it: Style ->
            it.withColor(10289003)
         })
         this.sendChat(var10001 as Text)
   }

   fun isPadMissingWarningRequired(): Boolean {
      return Config.autoVisitorEnabled && !PersistentState.autoVisitorPadPlaced
   }

   fun dynamicSetupRowLabel(): String {
      return if (PersistentState.autoVisitorSetupCompleted) "Auto Visitor Config" else "Auto Visitor Setup"
   }

   fun formatCompactValue(value: Long): String {
      val var7: Array<Any> = arrayOf((value).coerceAtLeast(0L))

      return var10000
   }

   fun defaultMaxSpendCoins(): Long {
      return 500000L
   }

   fun parseCompactNumber(input: String): Long? {

      if (raw.length() == 0) {
         return 0L
      } else {

         if (var10000 == null) {
            return null
         } else {

            if (var14 != null) {



               return if (var13 < 0L) null else var13
            } else {
               return null
            }
         }
      }
   }

   fun isInGarden(): Boolean {
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

            if (contains(var3.next() as String, "The Garden", true)) {
               var10000 = true
break
            }
         }
      }

      return var10000
   }

   fun shouldSuppressPreviewPlayerRender(entity: Entity?): Boolean {

      var10000 != null && suppressPreviewPlayerRender && entity == var10000
   }

   fun beginPreviewRenderSuppression() {
      suppressPreviewPlayerRender = true
   }

   fun endPreviewRenderSuppression() {
      suppressPreviewPlayerRender = false
   }

   fun cancelSetup(parentScreen: Screen?) {
      this.clearAllState()

      this.sendChat(var10001 as Text)
      this.getMc().setScreen(parentScreen)
   }

   private fun clearAllState() {
      Config.autoVisitorEnabled = false
      PersistentState.autoVisitorSetupCompleted = false
      PersistentState.autoVisitorPadPlaced = false
      PersistentState.autoVisitorConfigured = false
      PersistentState.autoVisitorPadX = 0.0
      PersistentState.autoVisitorPadY = 0.0
      PersistentState.autoVisitorPadZ = 0.0
      PersistentState.autoVisitorAcceptAll = false
      PersistentState.autoVisitorIgnoreSpaceman = true
      PersistentState.autoVisitorMaxSpendCoins = 500000L
      PersistentState.autoVisitorTrySacksFirst = true
      PersistentState.autoVisitorRareItemsOnly = false
      PersistentState.autoVisitorMinFarmingXp = 0L
      JooonConfigManager.write("jooonreimagined")
      JooonConfigManager.write("jooonreimagined_state")
   }

   private fun shouldRenderPad(): Boolean {
      return Config.autoVisitorEnabled
         && PersistentState.autoVisitorPadPlaced
         && this.isInGarden()
         && this.getMc().world != null
         && this.getMc().player != null
      }

   private fun renderPad(context: WorldRenderContext) {

      if (var10000 != null) {

         if (var16 != null) {








               PersistentState.autoVisitorPadX - 0.48,
               y,
               PersistentState.autoVisitorPadZ - 0.48,
               PersistentState.autoVisitorPadX + 0.48,
               y + 0.06,
               PersistentState.autoVisitorPadZ + 0.48
            )

            RenderUtils.renderBoxFill(var16, var20, var19, var18, outer, 0.18F, 0.82F, 0.36F, 0.26F)
            RenderUtils.renderBoxOutlineRobust(var16, var20, var19, var18, outer, 0.56F, 1.0F, 0.72F, 0.95F, 0.015F)
            RenderUtils.renderBoxFill(var16, var20, var19, var18, inner, 0.22F, 0.95F, 0.45F, 0.22F)
            RenderUtils.renderText(var16, var10000, "Auto Visitor Pad", x, y + 0.18, z, -6619214, var17, true)
         }
      }
   }
}
