package jooon.util

import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.class_1297
import net.minecraft.class_243
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

@SourceDebugExtension(["SMAP\nRotator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Rotator.kt\njooon/util/Rotator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,201:1\n1#2:202\n*E\n"])
public object Rotator {
   public final var isActive: Boolean
      private set

   private final var followSupplier: (() -> class_243?)?
   private final var onSettled: (() -> Unit)?
   private final var maxStepDegPerFrame: Float = 5.0F
   private final var deadzone: Float = 0.15F
   private final var renderHookInstalled: Boolean

   private fun ensureRenderHook() {
      if (!renderHookInstalled) {
         renderHookInstalled = true
         WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
            INSTANCE.onRenderFrame()
         })
      }
   }

   @JvmStatic
   public fun onTick() {
   }

   public fun clear() {
      isActive = false
      followSupplier = null
      onSettled = null
   }

   fun lookAt(to: Vec3d, maxStepDegPerFrame: Float, deadzone: Float, onSettled: (() -> Unit)?) {
      Rotator.maxStepDegPerFrame = maxStepDegPerFrame
      Rotator.deadzone = deadzone
      Rotator.onSettled = onSettled
      followSupplier = { 
         `$to`
      }
      isActive = true
      this.ensureRenderHook()
   }

   public fun follow(targetProvider: () -> class_243?, maxStepDegPerFrame: Float = 5.0F, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      maxStepDegPerFrame = maxStepDegPerFrame
      deadzone = deadzone
      onSettled = onSettled
      followSupplier = targetProvider
      isActive = true
      this.ensureRenderHook()
   }

   public fun rotateToAngles(yaw: Float, pitch: Float, maxStepDegPerFrame: Float = 5.0F, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      maxStepDegPerFrame = maxStepDegPerFrame
      deadzone = deadzone
      onSettled = onSettled
      followSupplier = { 
         val p: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
         var var10000: Vec3d
         if (p == null) {
            var10000 = null
         } else {
            var10000 = p.method_33571()
            val yr: Double = Math.toRadians((double)`$yaw`)
            val pr: Double = Math.toRadians((double)`$pitch`)
            var10000 = var10000.method_1019(Vec3d(-Math.sin(yr) * Math.cos(pr), -Math.sin(pr), Math.cos(yr) * Math.cos(pr)).method_1021(1000.0))
         }

         var10000
      }
      isActive = true
      this.ensureRenderHook()
   }

   private fun speedToStep(maxSpeedDegPerSec: Float): Float {
      return RangesKt.coerceAtLeast(maxSpeedDegPerSec / 60.0F, 0.05F)
   }

   fun lookAt(to: Vec3d, smoothTime: Float, maxSpeedDegPerSec: Float, deadzone: Float, onSettled: (() -> Unit)?) {
      this.lookAt(to, this.speedToStep(maxSpeedDegPerSec), deadzone, onSettled)
   }

   public fun follow(targetProvider: () -> class_243?, smoothTime: Float, maxSpeedDegPerSec: Float, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      this.follow(targetProvider, this.speedToStep(maxSpeedDegPerSec), deadzone, onSettled)
   }

   public fun rotateToAngles(yaw: Float, pitch: Float, smoothTime: Float, maxSpeedDegPerSec: Float, deadzone: Float = 0.15F, onSettled: (() -> Unit)? = null) {
      this.rotateToAngles(yaw, pitch, this.speedToStep(maxSpeedDegPerSec), deadzone, onSettled)
   }

   private fun onRenderFrame() {
      if (isActive) {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         if (var10000.field_1724 == null) {
            this.clear()
         } else {
            val player: ClientPlayerEntity = var10000.field_1724
            if (followSupplier != null) {
               val var19: Vec3d = followSupplier() as Vec3d
               if (var19 != null) {
                  val var4: Pair = this.calcYawPitch(var19)
                  val targetYaw: Float = (var4.component1() as java.lang.Number).floatValue()
                  val targetPitch: Float = (var4.component2() as java.lang.Number).floatValue()
                  val var17: Float = player.method_36454()
                  val var18: Float = player.method_36455()
                  val dy: Float = this.wrap180(targetYaw - var17)
                  val dp: Float = this.wrap180(targetPitch - var18)
                  val step: Float = maxStepDegPerFrame
                  val ny: Float = var17 + RangesKt.coerceIn(dy, -maxStepDegPerFrame, maxStepDegPerFrame)
                  val np: Float = var18 + RangesKt.coerceIn(dp, -step, step)
                  player.method_36456(ny)
                  player.method_36457(np)
                  if (Math.abs(dy) <= deadzone && Math.abs(dp) <= deadzone) {
                     player.method_36456(targetYaw)
                     player.method_36457(targetPitch)
                     val done: Function0 = onSettled
                     this.clear()
                     if (done != null) {
                        done()
                     }
                  }

                  return
               }
            }

            this.clear()
         }
      }
   }

   fun calcYawPitch(to: Vec3d): Pair<java.lang.Float, java.lang.Float> {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 == null) {
         TuplesKt.to(0.0F, 0.0F)
      } else {
         val var7: Vec3d = var10000.method_33571()
         val var8: Vec3d = to.method_1020(var7)
         TuplesKt.to(
            (float)Math.toDegrees(Math.atan2(-var8.field_1352, var8.field_1350)),
            (float)Math.toDegrees(-Math.atan(var8.field_1351 / Math.sqrt(var8.field_1352 * var8.field_1352 + var8.field_1350 * var8.field_1350)))
         )
      }
   }

   private fun wrap180(a: Float): Float {
      var x: Float = a

      while (x <= -180.0F) {
         x += 360.0F
      }

      while (x > 180.0F) {
         x -= 360.0F
      }

      return x
   }

   fun lookAtEntityHead(e: Entity, headYOffset: Double) {
      val var10001: Vec3d = e.method_73189().method_1031(0.0, headYOffset, 0.0)
      lookAt$default(this, var10001, 0.0F, 0.0F, null, 14, null)
   }

   public fun followEntityHead(provider: () -> class_1297?, headYOffset: Double = 1.62) {
      follow$default(this, lambda_5@{ 
         val var10000: Entity = `$provider`() as Entity
         if (var10000 != null) {
            val var3: Vec3d = var10000.method_73189()
            if (var3 != null) {
               return@lambda_5 var3.method_1031(0.0, `$headYOffset`, 0.0)
            }
         }

         return@lambda_5 null
      }, 0.0F, 0.0F, null, 14, null)
   }
}
