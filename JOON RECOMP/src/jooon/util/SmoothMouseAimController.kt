package jooon.util

import jooon.util.SyntheticMouseTurnBroker.TurnDelta
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.MathHelper

@SourceDebugExtension(["SMAP\nSmoothMouseAimController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SmoothMouseAimController.kt\njooon/util/SmoothMouseAimController\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,171:1\n1#2:172\n*E\n"])
public class SmoothMouseAimController(owner: String, isActive: () -> Boolean) {
   private final val owner: String
   private final val isActive: () -> Boolean
   private final var currentPlan: jooon.util.SmoothMouseAimController.AimPlan?

   init {
      this.owner = owner
      this.isActive = isActive
   }

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   fun request(
      player: ClientPlayerEntity,
      targetYaw: Float,
      targetPitch: Float,
      baseDurationMs: Int,
      maxDurationMs: Int,
      retargetThresholdDeg: Float,
      minRetargetMs: Long
   ) {
      val now: Long = System.currentTimeMillis()
      val clampedTargetPitch: Float = RangesKt.coerceIn(targetPitch, -89.9F, 89.9F)
      val plan: SmoothMouseAimController.AimPlan = this.currentPlan
      val targetShift: Float = if (this.currentPlan == null)
         java.lang.Float.MAX_VALUE
         else
         (float)Math.hypot((double)MathHelper.method_15393(targetYaw - this.currentPlan.targetYaw), (double)(clampedTargetPitch - plan.targetPitch))
         if (this.currentPlan == null
         || this.currentPlan == null
         || now - this.currentPlan.startAtMs >= plan.durationMs
         || targetShift > retargetThresholdDeg && now - this.currentPlan.startAtMs >= minRetargetMs) {
         this.currentPlan = this.createPlan(player, targetYaw, clampedTargetPitch, baseDurationMs, maxDurationMs, now)
      }

      SyntheticMouseTurnBroker.claim(this.owner, { 
         `this$0`.getSyntheticMouseTurn()
      })
      if (!this.getMc().method_1569()) {
         this.applyUnfocusedRotation(player)
      }
   }

   public fun clear() {
      this.currentPlan = null
      SyntheticMouseTurnBroker.release(this.owner)
   }

   private fun getSyntheticMouseTurn(): TurnDelta? {
      if (!this.isActive() as java.lang.Boolean) {
         return null
      } else {
         val var10000: ClientPlayerEntity = this.getMc().field_1724
         if (var10000 == null) {
            return null
         } else if (this.currentPlan == null) {
            return null
         } else {
            val plan: SmoothMouseAimController.AimPlan = this.currentPlan
            val sample: SmoothMouseAimController.AimSample = this.sample(this.currentPlan, System.currentTimeMillis())
            val stepDeg: Double = RangesKt.coerceAtLeast((double)plan.sensitivityStepDeg, 1.0E-4)
            val yawDelta: Double = MathHelper.method_15393(sample.yaw - var10000.method_36454())
            val pitchDelta: Double = sample.pitch - var10000.method_36455()
            return if (Math.abs(yawDelta) < 0.01 && Math.abs(pitchDelta) < 0.01)
               null
               else
               SyntheticMouseTurnBroker.TurnDelta(yawDelta / stepDeg, pitchDelta / stepDeg)
            }
      }
   }

   fun applyUnfocusedRotation(player: ClientPlayerEntity) {
      if (this.isActive() as java.lang.Boolean) {
         if (this.currentPlan != null) {
            val sample: SmoothMouseAimController.AimSample = this.sample(this.currentPlan, System.currentTimeMillis())
            player.method_36456(sample.yaw)
            player.method_36457(sample.pitch)
            player.field_6241 = sample.yaw
            player.field_6283 = sample.yaw
         }
      }
   }

   fun createPlan(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, baseDurationMs: Int, maxDurationMs: Int, nowMs: Long): SmoothMouseAimController.AimPlan {
      val deltaYaw: Float = MathHelper.method_15393(targetYaw - player.method_36454())
      val deltaPitch: Float = RangesKt.coerceIn(targetPitch - player.method_36455(), -90.0F, 90.0F)
      val duration: Int = RangesKt.coerceIn(
         (int)((float)baseDurationMs + (float)Math.hypot((double)deltaYaw, (double)deltaPitch) * 0.95F), baseDurationMs, maxDurationMs
      )
      val var14: SmoothMouseAimController = this

      var `$this$createPlan_u24lambda_u241`: Any
      try {
         `$this$createPlan_u24lambda_u241` = Result.constructor_impl/* $VF was: constructor-impl */(
            var14.getMc().field_1690.method_42495().method_41753() as java.lang.Double
         )
      } catch (var17: java.lang.Throwable) {
         `$this$createPlan_u24lambda_u241` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var17))
      }

      val var10000: Any = if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$createPlan_u24lambda_u241`)) 0.5 else `$this$createPlan_u24lambda_u241`
      SmoothMouseAimController.AimPlan(
         player.method_36454(),
         player.method_36455(),
         targetYaw,
         targetPitch,
         deltaYaw,
         deltaPitch,
         deltaYaw * 0.28F,
         deltaPitch * 0.36F,
         deltaYaw * 0.78F,
         deltaPitch * 0.72F,
         nowMs,
         duration,
         this.sensitivityStepDeg((var10000 as java.lang.Number).doubleValue())
      )
   }

   private fun sample(plan: jooon.util.SmoothMouseAimController.AimPlan, nowMs: Long): jooon.util.SmoothMouseAimController.AimSample {
      val elapsed: Long = RangesKt.coerceAtLeast(nowMs - plan.startAtMs, 0L)
      if (elapsed >= plan.durationMs) {
         return SmoothMouseAimController.AimSample(MathHelper.method_15393(plan.targetYaw), RangesKt.coerceIn(plan.targetPitch, -89.9F, 89.9F))
      } else {
         val eased: Double = 0.5 - Math.cos(RangesKt.coerceIn((double)elapsed / (double)plan.durationMs, 0.0, 1.0) * Math.PI) * 0.5
         return SmoothMouseAimController.AimSample(
            MathHelper.method_15393(
               plan.startYaw + (float)this.cubicBezier(0.0, (double)plan.controlYawOne, (double)plan.controlYawTwo, (double)plan.deltaYaw, eased)
            ),
            RangesKt.coerceIn(
               plan.startPitch + (float)this.cubicBezier(0.0, (double)plan.controlPitchOne, (double)plan.controlPitchTwo, (double)plan.deltaPitch, eased),
               -89.9F,
               89.9F
            )
         )
      }
   }

   private fun cubicBezier(p0: Double, p1: Double, p2: Double, p3: Double, t: Double): Double {
      return (1.0 - t) * (1.0 - t) * (1.0 - t) * p0 + 3.0 * (1.0 - t) * (1.0 - t) * t * p1 + 3.0 * (1.0 - t) * t * t * p2 + t * t * t * p3
   }

   private fun sensitivityStepDeg(sensitivity: Double): Float {
      val scaled: Double = RangesKt.coerceIn(sensitivity, 0.0, 1.0) * 0.6 + 0.2
      return RangesKt.coerceAtLeast((float)(scaled * scaled * scaled * 1.2), 0.01F)
   }

   private data class AimPlan(startYaw: Float,
      startPitch: Float,
      targetYaw: Float,
      targetPitch: Float,
      deltaYaw: Float,
      deltaPitch: Float,
      controlYawOne: Float,
      controlPitchOne: Float,
      controlYawTwo: Float,
      controlPitchTwo: Float,
      startAtMs: Long,
      durationMs: Int,
      sensitivityStepDeg: Float
   ) {
      public final val startYaw: Float
      public final val startPitch: Float
      public final val targetYaw: Float
      public final val targetPitch: Float
      public final val deltaYaw: Float
      public final val deltaPitch: Float
      public final val controlYawOne: Float
      public final val controlPitchOne: Float
      public final val controlYawTwo: Float
      public final val controlPitchTwo: Float
      public final val startAtMs: Long
      public final val durationMs: Int
      public final val sensitivityStepDeg: Float

      init {
         this.startYaw = startYaw
         this.startPitch = startPitch
         this.targetYaw = targetYaw
         this.targetPitch = targetPitch
         this.deltaYaw = deltaYaw
         this.deltaPitch = deltaPitch
         this.controlYawOne = controlYawOne
         this.controlPitchOne = controlPitchOne
         this.controlYawTwo = controlYawTwo
         this.controlPitchTwo = controlPitchTwo
         this.startAtMs = startAtMs
         this.durationMs = durationMs
         this.sensitivityStepDeg = sensitivityStepDeg
      }

      public operator fun component1(): Float {
         return this.startYaw
      }

      public operator fun component2(): Float {
         return this.startPitch
      }

      public operator fun component3(): Float {
         return this.targetYaw
      }

      public operator fun component4(): Float {
         return this.targetPitch
      }

      public operator fun component5(): Float {
         return this.deltaYaw
      }

      public operator fun component6(): Float {
         return this.deltaPitch
      }

      public operator fun component7(): Float {
         return this.controlYawOne
      }

      public operator fun component8(): Float {
         return this.controlPitchOne
      }

      public operator fun component9(): Float {
         return this.controlYawTwo
      }

      public operator fun component10(): Float {
         return this.controlPitchTwo
      }

      public operator fun component11(): Long {
         return this.startAtMs
      }

      public operator fun component12(): Int {
         return this.durationMs
      }

      public operator fun component13(): Float {
         return this.sensitivityStepDeg
      }

      public fun copy(
         startYaw: Float = this.startYaw,
         startPitch: Float = this.startPitch,
         targetYaw: Float = this.targetYaw,
         targetPitch: Float = this.targetPitch,
         deltaYaw: Float = this.deltaYaw,
         deltaPitch: Float = this.deltaPitch,
         controlYawOne: Float = this.controlYawOne,
         controlPitchOne: Float = this.controlPitchOne,
         controlYawTwo: Float = this.controlYawTwo,
         controlPitchTwo: Float = this.controlPitchTwo,
         startAtMs: Long = this.startAtMs,
         durationMs: Int = this.durationMs,
         sensitivityStepDeg: Float = this.sensitivityStepDeg
      ): jooon.util.SmoothMouseAimController.AimPlan {
         return SmoothMouseAimController.AimPlan(
            startYaw,
            startPitch,
            targetYaw,
            targetPitch,
            deltaYaw,
            deltaPitch,
            controlYawOne,
            controlPitchOne,
            controlYawTwo,
            controlPitchTwo,
            startAtMs,
            durationMs,
            sensitivityStepDeg
         )
      }

      public override fun toString(): String {
         return "AimPlan(startYaw=${this.startYaw}, startPitch=${this.startPitch}, targetYaw=${this.targetYaw}, targetPitch=${this.targetPitch}, deltaYaw=${this.deltaYaw}, deltaPitch=${this.deltaPitch}, controlYawOne=${this.controlYawOne}, controlPitchOne=${this.controlPitchOne}, controlYawTwo=${this.controlYawTwo}, controlPitchTwo=${this.controlPitchTwo}, startAtMs=${this.startAtMs}, durationMs=${this.durationMs}, sensitivityStepDeg=${this.sensitivityStepDeg})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (
                                                               (
                                                                        (
                                                                                 (
                                                                                          (
                                                                                                   (
                                                                                                            java.lang.Float.hashCode(this.startYaw) * 31
                                                                                                               + java.lang.Float.hashCode(this.startPitch)
                                                                                                         )
                                                                                                         * 31
                                                                                                      + java.lang.Float.hashCode(this.targetYaw)
                                                                                                )
                                                                                                * 31
                                                                                             + java.lang.Float.hashCode(this.targetPitch)
                                                                                       )
                                                                                       * 31
                                                                                    + java.lang.Float.hashCode(this.deltaYaw)
                                                                              )
                                                                              * 31
                                                                           + java.lang.Float.hashCode(this.deltaPitch)
                                                                     )
                                                                     * 31
                                                                  + java.lang.Float.hashCode(this.controlYawOne)
                                                            )
                                                            * 31
                                                         + java.lang.Float.hashCode(this.controlPitchOne)
                                                   )
                                                   * 31
                                                + java.lang.Float.hashCode(this.controlYawTwo)
                                          )
                                          * 31
                                       + java.lang.Float.hashCode(this.controlPitchTwo)
                                 )
                                 * 31
                              + java.lang.Long.hashCode(this.startAtMs)
                        )
                        * 31
                     + Integer.hashCode(this.durationMs)
               )
               * 31
            + java.lang.Float.hashCode(this.sensitivityStepDeg)
         }

      public override operator fun equals(other: Any?): Boolean {
         label94@
         if (this === other) {
            return true
         } else {
            return other is SmoothMouseAimController.AimPlan
               && java.lang.Float.compare(this.startYaw, (other as SmoothMouseAimController.AimPlan).startYaw) == 0
               && java.lang.Float.compare(this.startPitch, (other as SmoothMouseAimController.AimPlan).startPitch) == 0
               && java.lang.Float.compare(this.targetYaw, (other as SmoothMouseAimController.AimPlan).targetYaw) == 0
               && java.lang.Float.compare(this.targetPitch, (other as SmoothMouseAimController.AimPlan).targetPitch) == 0
               && java.lang.Float.compare(this.deltaYaw, (other as SmoothMouseAimController.AimPlan).deltaYaw) == 0
               && java.lang.Float.compare(this.deltaPitch, (other as SmoothMouseAimController.AimPlan).deltaPitch) == 0
               && java.lang.Float.compare(this.controlYawOne, (other as SmoothMouseAimController.AimPlan).controlYawOne) == 0
               && java.lang.Float.compare(this.controlPitchOne, (other as SmoothMouseAimController.AimPlan).controlPitchOne) == 0
               && java.lang.Float.compare(this.controlYawTwo, (other as SmoothMouseAimController.AimPlan).controlYawTwo) == 0
               && java.lang.Float.compare(this.controlPitchTwo, (other as SmoothMouseAimController.AimPlan).controlPitchTwo) == 0
               && this.startAtMs == (other as SmoothMouseAimController.AimPlan).startAtMs
               && this.durationMs == (other as SmoothMouseAimController.AimPlan).durationMs
               && java.lang.Float.compare(this.sensitivityStepDeg, (other as SmoothMouseAimController.AimPlan).sensitivityStepDeg) == 0
            }
      }
   }

   private data class AimSample(yaw: Float, pitch: Float) {
      public final val yaw: Float
      public final val pitch: Float

      init {
         this.yaw = yaw
         this.pitch = pitch
      }

      public operator fun component1(): Float {
         return this.yaw
      }

      public operator fun component2(): Float {
         return this.pitch
      }

      public fun copy(yaw: Float = this.yaw, pitch: Float = this.pitch): jooon.util.SmoothMouseAimController.AimSample {
         return SmoothMouseAimController.AimSample(yaw, pitch)
      }

      public override fun toString(): String {
         return "AimSample(yaw=${this.yaw}, pitch=${this.pitch})"
      }

      public override fun hashCode(): Int {
         return java.lang.Float.hashCode(this.yaw) * 31 + java.lang.Float.hashCode(this.pitch)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is SmoothMouseAimController.AimSample
               && java.lang.Float.compare(this.yaw, (other as SmoothMouseAimController.AimSample).yaw) == 0
               && java.lang.Float.compare(this.pitch, (other as SmoothMouseAimController.AimSample).pitch) == 0
            }
      }
   }
}
