package jooon.features.farming

import java.util.Arrays
import java.util.Locale
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.util.RenderUtils
import jooon.util.ScoreboardUtil
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nAutoVisitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoVisitor.kt\njooon/features/farming/AutoVisitor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1010:1\n1747#2,3:1011\n*S KotlinDebug\n*F\n+ 1 AutoVisitor.kt\njooon/features/farming/AutoVisitor\n*L\n200#1:1011,3\n*E\n"])
public object AutoVisitor {
   private const val PAD_HALF_SIZE: Double = 0.48
   private const val PAD_HEIGHT: Double = 0.06
   private const val DEFAULT_MAX_SPEND_COINS: Long = 500000L
   private final var suppressPreviewPlayerRender: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   fun prefixed(message: Text): Text {
      val var10000: MutableText = Text.method_43470(JooonReimagined.Companion.PREFIX_CLEAN).method_10852(message)
      var10000 as Text
   }

   fun sendChat(message: Text) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var10000.method_7353(this.prefixed(message), false)
      }
   }

   fun sendOverlay(message: Text) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var10000.method_7353(this.prefixed(message), true)
      }
   }

   public fun init() {
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
      if (this.getMc().field_1724 != null) {
         if (!this.isInGarden()) {
            val var3: MutableText = Text.method_43470("You must be in The Garden to configure this.").method_27692(Formatting.field_1061)
            this.sendChat(var3 as Text)
         } else {
            val var10001: MutableText = Text.method_43470("Setup requirement: You must be on the ground before placing the plate.")
               .method_27692(Formatting.field_1054)
               this.sendChat(var10001 as Text)
            this.getMc().execute({ 
               INSTANCE.getMc().method_1507(AutoVisitorPadScreen(`$parentScreen`, !PersistentState.autoVisitorSetupCompleted))
            })
         }
      }
   }

   public fun resetForTestingFromConfig() {
      if (this.getMc().field_1724 != null) {
         if (!this.isInGarden()) {
            val var2: MutableText = Text.method_43470("Temp reset only works while in The Garden.").method_27692(Formatting.field_1061)
            this.sendChat(var2 as Text)
         } else {
            this.clearAllState()
            val var10001: MutableText = Text.method_43470("Temp reset complete.").method_27692(Formatting.field_1054)
            this.sendOverlay(var10001 as Text)
         }
      }
   }

   public fun placePlateAtPlayerFeet(): Boolean {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 == null) {
         return false
      } else if (!this.isInGarden()) {
         val var9: MutableText = Text.method_43470("You must be in The Garden.").method_27692(Formatting.field_1061)
         this.sendChat(var9 as Text)
         return false
      } else if (!var10000.method_31549().field_7479 && var10000.method_24828()) {
         val padX: Double = Math.floor(var10000.method_23317()) + 0.5
         val padY: Double = Math.floor(var10000.method_23318())
         val padZ: Double = Math.floor(var10000.method_23321()) + 0.5
         PersistentState.autoVisitorPadX = padX
         PersistentState.autoVisitorPadY = padY
         PersistentState.autoVisitorPadZ = padZ
         PersistentState.autoVisitorConfigured = true
         PersistentState.autoVisitorPadPlaced = true
         Config.autoVisitorEnabled = true
         JooonConfigManager.INSTANCE.write("jooonreimagined")
         JooonConfigManager.INSTANCE.write("jooonreimagined_state")
         val var8: MutableText = Text.method_43470("Plate set at ${(int)padX}, ${(int)padY}, ${(int)padZ}.").method_27692(Formatting.field_1060)
         this.sendOverlay(var8 as Text)
         return true
      } else {
         val var10001: MutableText = Text.method_43470("You must be on the ground before placing the plate.").method_27692(Formatting.field_1061)
         this.sendChat(var10001 as Text)
         return false
      }
   }

   public fun clearPadOnly() {
      PersistentState.autoVisitorPadX = 0.0
      PersistentState.autoVisitorPadY = 0.0
      PersistentState.autoVisitorPadZ = 0.0
      PersistentState.autoVisitorConfigured = false
      PersistentState.autoVisitorPadPlaced = false
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
   }

   public fun saveRulesFromUi(
      acceptAll: Boolean,
      ignoreSpaceman: Boolean,
      maxSpendCoins: Long,
      trySacksFirst: Boolean,
      rareItemsOnly: Boolean,
      minFarmingXp: Long
   ) {
      PersistentState.autoVisitorAcceptAll = acceptAll
      PersistentState.autoVisitorIgnoreSpaceman = ignoreSpaceman
      PersistentState.autoVisitorMaxSpendCoins = RangesKt.coerceAtLeast(maxSpendCoins, 0L)
      PersistentState.autoVisitorTrySacksFirst = trySacksFirst
      PersistentState.autoVisitorRareItemsOnly = rareItemsOnly
      PersistentState.autoVisitorMinFarmingXp = RangesKt.coerceAtLeast(minFarmingXp, 0L)
      PersistentState.autoVisitorSetupCompleted = true
      Config.autoVisitorEnabled = true
      JooonConfigManager.INSTANCE.write("jooonreimagined")
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
      val var10001: MutableText = Text.method_43470("Setup complete. Leave the pad, then step back onto it when you're ready to start.")
         .method_27694({ it: Style ->
            it.method_36139(10289003)
         })
         this.sendChat(var10001 as Text)
   }

   public fun isPadMissingWarningRequired(): Boolean {
      return Config.autoVisitorEnabled && !PersistentState.autoVisitorPadPlaced
   }

   public fun dynamicSetupRowLabel(): String {
      return if (PersistentState.autoVisitorSetupCompleted) "Auto Visitor Config" else "Auto Visitor Setup"
   }

   public fun formatCompactValue(value: Long): String {
      val var7: Array<Any> = arrayOf(RangesKt.coerceAtLeast(value, 0L))
      val var10000: java.lang.String = java.lang.String.format("%,d", Arrays.copyOf(var7, var7.length))
      return var10000
   }

   public fun defaultMaxSpendCoins(): Long {
      return 500000L
   }

   public fun parseCompactNumber(input: String): Long? {
      val raw: java.lang.String = StringsKt.trim(input).toString()
      if (raw.length() == 0) {
         return 0L
      } else {
         val var10000: MatchResult = Regex("^([0-9]+(?:\\.[0-9]+)?)([kKmM]?)$").matchEntire(Regex("[,_\\s\\u00A0\\u202F]").replace(raw, ""))
         if (var10000 == null) {
            return null
         } else {
            val var14: java.lang.Double = StringsKt.toDoubleOrNull(var10000.getGroupValues().get(1) as java.lang.String)
            if (var14 != null) {
               val var12: Double = var14
               val var15: java.lang.String = (var10000.getGroupValues().get(2) as java.lang.String).toLowerCase(Locale.ROOT)
               val var13: Long = MathKt.roundToLong(var12 * (if (var15 == "k") 1000.0 else (if (var15 == "m") 1000000.0 else 1.0)))
               return if (var13 < 0L) null else var13
            } else {
               return null
            }
         }
      }
   }

   public fun isInGarden(): Boolean {
      val `$this$any$iv`: java.lang.Iterable = ScoreboardUtil.INSTANCE.getSidebarLines()
      var var10000: Boolean
      if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
         var10000 = false
      } else {
         val var3: java.util.Iterator = `$this$any$iv`.iterator()

         while (true) {
            if (!var3.hasNext()) {
               var10000 = false
               break
            }

            if (StringsKt.contains(var3.next() as java.lang.String, "The Garden", true)) {
               var10000 = true
               break
            }
         }
      }

      return var10000
   }

   fun shouldSuppressPreviewPlayerRender(entity: Entity?): Boolean {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      var10000 != null && suppressPreviewPlayerRender && entity == var10000
   }

   public fun beginPreviewRenderSuppression() {
      suppressPreviewPlayerRender = true
   }

   public fun endPreviewRenderSuppression() {
      suppressPreviewPlayerRender = false
   }

   fun cancelSetup(parentScreen: Screen?) {
      this.clearAllState()
      val var10001: MutableText = Text.method_43470("Setup cancelled!").method_27692(Formatting.field_1054)
      this.sendChat(var10001 as Text)
      this.getMc().method_1507(parentScreen)
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
      JooonConfigManager.INSTANCE.write("jooonreimagined")
      JooonConfigManager.INSTANCE.write("jooonreimagined_state")
   }

   private fun shouldRenderPad(): Boolean {
      return Config.autoVisitorEnabled
         && PersistentState.autoVisitorPadPlaced
         && this.isInGarden()
         && this.getMc().field_1687 != null
         && this.getMc().field_1724 != null
      }

   private fun renderPad(context: WorldRenderContext) {
      val var10000: MatrixStack = context.matrices()
      if (var10000 != null) {
         val var16: VertexConsumerProvider = context.consumers()
         if (var16 != null) {
            val var17: Camera = context.gameRenderer().method_19418()
            val var18: Vec3d = var17.method_71156()
            val var19: Entry = var10000.method_23760()
            val var20: Matrix4f = var19.method_23761()
            val x: Double = PersistentState.autoVisitorPadX
            val y: Double = PersistentState.autoVisitorPadY + 0.015
            val z: Double = PersistentState.autoVisitorPadZ
            val outer: Box = Box(
               PersistentState.autoVisitorPadX - 0.48,
               y,
               PersistentState.autoVisitorPadZ - 0.48,
               PersistentState.autoVisitorPadX + 0.48,
               y + 0.06,
               PersistentState.autoVisitorPadZ + 0.48
            )
            val inner: Box = Box(x - 0.34, y + 0.003, z - 0.34, x + 0.34, y + 0.06 - 0.004, z + 0.34)
            RenderUtils.INSTANCE.renderBoxFill(var16, var20, var19, var18, outer, 0.18F, 0.82F, 0.36F, 0.26F)
            RenderUtils.INSTANCE.renderBoxOutlineRobust(var16, var20, var19, var18, outer, 0.56F, 1.0F, 0.72F, 0.95F, 0.015F)
            RenderUtils.INSTANCE.renderBoxFill(var16, var20, var19, var18, inner, 0.22F, 0.95F, 0.45F, 0.22F)
            RenderUtils.INSTANCE.renderText(var16, var10000, "Auto Visitor Pad", x, y + 0.18, z, -6619214, var17, true)
         }
      }
   }
}
