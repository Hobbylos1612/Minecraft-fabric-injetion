package jooon.config.ui

import kotlin.jvm.functions.Function0
import net.minecraft.client.gui.DrawContext

internal class JooonScreenTransition(openDurationMs: Long = 180L, closeDurationMs: Long = 80L, offsetPixels: Float = 18.0F) {
   private final val openDurationMs: Long
   private final val closeDurationMs: Long
   private final val offsetPixels: Float
   private final val openedAt: Long
   private final var closeStartedAt: Long
   private final var closeAction: (() -> Unit)?

   init {
      this.openDurationMs = openDurationMs
      this.closeDurationMs = closeDurationMs
      this.offsetPixels = offsetPixels
      this.openedAt = System.currentTimeMillis()
      this.closeStartedAt = -1L
   }

   public final val isClosing: Boolean
      public final get() {
         return this.closeAction != null
      }


   public fun beginClose(action: () -> Unit): Boolean {
      if (this.closeAction != null) {
         return true
      } else {
         this.closeStartedAt = System.currentTimeMillis()
         this.closeAction = action
         return true
      }
   }

   public fun finishCloseIfReady(): Boolean {
      if (this.closeAction == null) {
         return false
      } else {
         val action: Function0 = this.closeAction
         if (System.currentTimeMillis() - this.closeStartedAt < this.closeDurationMs) {
            return false
         } else {
            this.closeAction = null
            action()
            return true
         }
      }
   }

   public fun transformedMouseY(mouseY: Int): Int {
      return (int)(mouseY - this.currentOffset())
   }

   fun push(context: DrawContext) {
      context.method_51448().pushMatrix()
      context.method_51448().translate(0.0F, this.currentOffset())
   }

   fun pop(context: DrawContext) {
      context.method_51448().popMatrix()
   }

   public fun currentAlpha(): Float {
      return this.easeOutCubic(if (this.closeAction == null) this.normalizedOpenProgress() else 1.0F - this.normalizedCloseProgress())
   }

   private fun currentOffset(): Float {
      return (1.0F - this.easeOutCubic(if (this.closeAction == null) this.normalizedOpenProgress() else 1.0F - this.normalizedCloseProgress()))
         * this.offsetPixels
      }

   private fun normalizedOpenProgress(): Float {
      return RangesKt.coerceIn((float)RangesKt.coerceAtLeast(System.currentTimeMillis() - this.openedAt, 0L) / (float)this.openDurationMs, 0.0F, 1.0F)
   }

   private fun normalizedCloseProgress(): Float {
      return if (this.closeStartedAt < 0L)
         1.0F
         else
         RangesKt.coerceIn((float)RangesKt.coerceAtLeast(System.currentTimeMillis() - this.closeStartedAt, 0L) / (float)this.closeDurationMs, 0.0F, 1.0F)
      }

   private fun easeOutCubic(t: Float): Float {
      return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t)
   }

   fun JooonScreenTransition() {
      this(0L, 0L, 0.0F, 7, null)
   }
}
