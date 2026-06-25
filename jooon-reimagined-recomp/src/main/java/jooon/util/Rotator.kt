package jooon.util

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.client.MinecraftClient
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Rotator {
   var isActive: Boolean = false
      private set

   private var followSupplier: (() -> Vec3d?)? = null
   private var onSettled: (() -> Unit)? = null
   private var maxStepDegPerFrame: Float = 5.0F
   private var deadzone: Float = 0.15F
   private var renderHookInstalled: Boolean = false

   private fun ensureRenderHook() {
      if (!renderHookInstalled) {
         renderHookInstalled = true
         WorldRenderEvents.END_MAIN.register { _: WorldRenderContext ->
            onRenderFrame()
         }
      }
   }

   fun onTick() {
   }

   fun clear() {
      isActive = false
      followSupplier = null
      onSettled = null
   }

   fun lookAt(to: Vec3d, maxStepDegPerFrame: Float, deadzone: Float, onSettled: (() -> Unit)?) {
      this.maxStepDegPerFrame = maxStepDegPerFrame
      this.deadzone = deadzone
      this.onSettled = onSettled
      followSupplier = { to }
      isActive = true
      ensureRenderHook()
   }

   fun follow(targetProvider: () -> Vec3d?, maxStepDegPerFrame: Float = 5.0F, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      this.maxStepDegPerFrame = maxStepDegPerFrame
      this.deadzone = deadzone
      this.onSettled = onSettled
      followSupplier = targetProvider
      isActive = true
      ensureRenderHook()
   }

   fun rotateToAngles(yaw: Float, pitch: Float, maxStepDegPerFrame: Float = 5.0F, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      this.maxStepDegPerFrame = maxStepDegPerFrame
      this.deadzone = deadzone
      this.onSettled = onSettled
      val player = MinecraftClient.getInstance().player ?: return
      val yr = Math.toRadians(yaw.toDouble())
      val pr = Math.toRadians(pitch.toDouble())
      followSupplier = {
         player.getEyePos().add(Vec3d(-sin(yr) * cos(pr), -sin(pr), cos(yr) * cos(pr)).multiply(1000.0))
      }
      isActive = true
      ensureRenderHook()
   }

   private fun speedToStep(maxSpeedDegPerSec: Float): Float {
      return (maxSpeedDegPerSec / 60.0F).coerceAtLeast(0.05F)
   }

   fun lookAt(to: Vec3d, smoothTime: Float, maxSpeedDegPerSec: Float, deadzone: Float, onSettled: (() -> Unit)?) {
      lookAt(to, speedToStep(maxSpeedDegPerSec), deadzone, onSettled)
   }

   fun follow(targetProvider: () -> Vec3d?, smoothTime: Float, maxSpeedDegPerSec: Float, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      follow(targetProvider, speedToStep(maxSpeedDegPerSec), deadzone, onSettled)
   }

   fun rotateToAngles(yaw: Float, pitch: Float, smoothTime: Float, maxSpeedDegPerSec: Float, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      rotateToAngles(yaw, pitch, speedToStep(maxSpeedDegPerSec), deadzone, onSettled)
   }

   private fun onRenderFrame() {
      if (!isActive) return
      val player = MinecraftClient.getInstance().player ?: return
      val supplier = followSupplier ?: return

      val target = supplier() ?: run { clear(); return }

      val d = target.subtract(player.getEyePos())
      val targetYaw = Math.toDegrees(atan2(-d.x, d.z)).toFloat()
      val targetPitch = Math.toDegrees(-atan(d.y / sqrt(d.x * d.x + d.z * d.z))).toFloat()

      val dy = wrap180(targetYaw - player.yaw)
      val dp = targetPitch - player.pitch

      if (Math.abs(dy) <= deadzone && Math.abs(dp) <= deadzone) {
         player.yaw = targetYaw
         player.pitch = targetPitch
         clear()
         onSettled?.invoke()
      } else {
         player.yaw += dy.coerceIn(-maxStepDegPerFrame, maxStepDegPerFrame)
         player.pitch += dp.coerceIn(-maxStepDegPerFrame, maxStepDegPerFrame)
      }
   }

   fun calcYawPitch(to: Vec3d): Pair<Float, Float> {
      val player = MinecraftClient.getInstance().player ?: return Pair(0.0F, 0.0F)
      val d = to.subtract(player.getEyePos())
      val yaw = Math.toDegrees(atan2(-d.x, d.z)).toFloat()
      val pitch = Math.toDegrees(-atan(d.y / sqrt(d.x * d.x + d.z * d.z))).toFloat()
      return Pair(yaw, pitch)
   }

   private fun wrap180(a: Float): Float {
      var x = a
      while (x <= -180.0F) x += 360.0F
      while (x > 180.0F) x -= 360.0F
      return x
   }

   fun lookAtEntityHead(e: Entity, headYOffset: Double) {
      lookAt(e.getEyePos().add(0.0, headYOffset, 0.0), maxStepDegPerFrame, deadzone, onSettled)
   }

   fun followEntityHead(provider: () -> Entity?, headYOffset: Double = 1.62) {
      follow({
         val entity = provider() ?: return@follow null
         entity.getEyePos().add(0.0, headYOffset, 0.0)
      }, maxStepDegPerFrame, deadzone, onSettled)
   }
}
