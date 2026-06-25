package jooon.util

import jooon.gui.MovableOverlayScreen
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.class_332
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.Window

public class MovableOverlay(id: String, displayName: String, defaultX: Int, defaultY: Int, width: Int, height: Int) {
   private final val id: String
   private final val displayName: String
   private final val defaultX: Int
   private final val defaultY: Int
   public final var width: Int
   public final var height: Int
   private final var x: Int
   private final var y: Int
   public final var renderFunction: ((class_332, Int, Int, Float) -> Unit)?
   public final var onPositionChanged: ((Int, Int) -> Unit)?
   private final var isActive: Boolean
   public final var resetFunction: (() -> Pair<Int, Int>)?

   init {
      this.id = id
      this.displayName = displayName
      this.defaultX = defaultX
      this.defaultY = defaultY
      this.width = width
      this.height = height
      this.x = this.defaultX
      this.y = this.defaultY
      this.loadPosition()
   }

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun register() {
      if (!this.isActive) {
         this.isActive = true
         HudRenderCallback.EVENT
            .register(
               { context: DrawContext, deltaTracker: RenderTickCounter ->
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
   }

   public fun unregister() {
      this.isActive = false
   }

   public fun setPosition(newX: Int, newY: Int) {
      this.applyPosition(newX, newY, true)
   }

   public fun setPositionSilently(newX: Int, newY: Int) {
      this.applyPosition(newX, newY, false)
   }

   private fun applyPosition(newX: Int, newY: Int, notify: Boolean) {
      this.x = this.clampX(newX)
      this.y = this.clampY(newY)
      if (notify) {
         if (this.onPositionChanged != null) {
            this.onPositionChanged(this.x, this.y)
         }
      }
   }

   public fun getPosition(): Pair<Int, Int> {
      return Pair(this.x, this.y)
   }

   public fun openPositioningGUI() {
      try {
         this.getMc().method_1507(MovableOverlayScreen(this.id, this.displayName, this.x, this.y, { newX: Int, newY: Int ->
            `this$0`.setPosition(newX, newY)
            Unit.INSTANCE
         }, null, 32, null))
      } catch (var2: Exception) {
         var2.printStackTrace()
      }
   }

   fun renderOverlay(context: DrawContext, tickDelta: Float) {
      if (this.renderFunction != null) {
         this.renderFunction(context, this.x, this.y, tickDelta)
      }
   }

   private fun clampX(x: Int): Int {
      return Math.max(0, Math.min(x, this.getMc().method_22683().method_4486() - this.width))
   }

   private fun clampY(y: Int): Int {
      return Math.max(0, Math.min(y, this.getMc().method_22683().method_4502() - this.height))
   }

   private fun loadPosition() {
      this.x = this.defaultX
      this.y = this.defaultY
   }

   public fun resetPosition() {
      val resetPos: Pair = if (this.resetFunction != null) this.resetFunction() as Pair else null
      if (resetPos != null) {
         this.setPosition((resetPos.getFirst() as java.lang.Number).intValue(), (resetPos.getSecond() as java.lang.Number).intValue())
      } else {
         val var10000: Window = this.getMc().method_22683()
         this.setPosition(var10000.method_4486() / 2 - this.width / 2, var10000.method_4502() / 2 - this.height / 2)
      }
   }
}
