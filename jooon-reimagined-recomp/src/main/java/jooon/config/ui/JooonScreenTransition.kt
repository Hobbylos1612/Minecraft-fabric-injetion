package jooon.config.ui

import kotlin.jvm.functions.Function0
import net.minecraft.client.gui.DrawContext

internal class JooonScreenTransition(openDurationMs: Long = 180L, closeDurationMs: Long = 80L, offsetPixels: Float = 18.0F) {
   private val openDurationMs: Long
   private val closeDurationMs: Long
   private val offsetPixels: Float
   private val openedAt: Long
   private var closeStartedAt: Long
   private var closeAction: (() -> Unit)?

   init {
      this.openDurationMs = openDurationMs
      this.closeDurationMs = closeDurationMs
      this.offsetPixels = offsetPixels
      this.openedAt = System.currentTimeMillis()
      this.closeStartedAt = -1L
   }

   val isClosing: Boolean
      public get() {
         return this.closeAction != null
      }


   fun beginClose(action: () -> Unit): Boolean {
      if (this.closeAction != null) {
         return true
      } else {
         this.closeStartedAt = System.currentTimeMillis()
         this.closeAction = action
         return true
      }
   }

   fun finishCloseIfReady(): Boolean {
      if (this.closeAction == null) {
         return false
      } else {

         if (System.currentTimeMillis() - this.closeStartedAt < this.closeDurationMs) {
            return false
         } else {
            this.closeAction = null
            action()
            return true
         }
      }
   }

   fun transformedMouseY(mouseY: Int): Int {
      return (mouseY - this.currentOffset()).toInt()
   }

   fun push(context: DrawContext) {
      context.getMatrices().pushMatrix()
      context.getMatrices().translate(0.0F, this.currentOffset())
   }

   fun pop(context: DrawContext) {
      context.getMatrices().popMatrix()
   }

   fun currentAlpha(): Float {
      return this.easeOutCubic(if (this.closeAction == null) this.normalizedOpenProgress() else 1.0F - this.normalizedCloseProgress())
   }

   private fun currentOffset(): Float {
      return (1.0F - this.easeOutCubic(if (this.closeAction == null) this.normalizedOpenProgress() else 1.0F - this.normalizedCloseProgress()))
         * this.offsetPixels
      }

   private fun normalizedOpenProgress(): Float {
      return ((System.currentTimeMillis() - this.openedAt).coerceAtLeast(0L).toFloat() / this.openDurationMs.toFloat()).coerceIn(0.0F, 1.0F)
   }

   private fun normalizedCloseProgress(): Float {
      return if (this.closeStartedAt < 0L)
         1.0F
return else
         ((System.currentTimeMillis() - this.closeStartedAt).coerceAtLeast(0L).toFloat() / this.closeDurationMs.toFloat()).coerceIn(0.0F, 1.0F)
      }

   private fun easeOutCubic(t: Float): Float {
      return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t)
   }

   fun JooonScreenTransition() {
      this(0L, 0L, 0.0F, 7, null)
   }
}
