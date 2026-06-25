package jooon.util

import jooon.util.SyntheticMouseTurnBroker.TurnDelta
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.MathHelper

class SmoothMouseAimController(owner: String, isActive: () -> Boolean) {
   private val owner: String
   private val isActive: () -> Boolean
   private var currentPlan: jooon.util.SmoothMouseAimController.AimPlan?

   init {
      this.owner = owner
      this.isActive = isActive
   }

   fun getMc(): MinecraftClient {
return var10000
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


      val plan: SmoothMouseAimController.AimPlan = this.currentPlan

         java.lang.Float.MAX_VALUE
return else
         Math.hypot(MathHelper.wrapDegrees(targetYaw - this.currentPlan.targetYaw).toDouble(), (clampedTargetPitch - plan.targetPitch).toDouble()).toFloat()
         if (this.currentPlan == null
         || this.currentPlan == null
         || now - this.currentPlan.startAtMs >= plan.durationMs
         || targetShift > retargetThresholdDeg && now - this.currentPlan.startAtMs >= minRetargetMs) {
         this.currentPlan = this.createPlan(player, targetYaw, clampedTargetPitch, baseDurationMs, maxDurationMs, now)
      }

      SyntheticMouseTurnBroker.claim(this.owner, { 
         `this$0`.getSyntheticMouseTurn()
      })
      if (!this.getMc().isWindowFocused()) {
         this.applyUnfocusedRotation(player)
      }
   }

   fun clear() {
      this.currentPlan = null
      SyntheticMouseTurnBroker.release(this.owner)
   }

   private fun getSyntheticMouseTurn(): TurnDelta? {
      if (!this.isActive() as Boolean) {
         return null
      } else {

         if (var10000 == null) {
            return null
         } else if (this.currentPlan == null) {
            return null
         } else {
            val plan: SmoothMouseAimController.AimPlan = this.currentPlan
            val sample: SmoothMouseAimController.AimSample = this.sample(this.currentPlan, System.currentTimeMillis())



            return if (Math.abs(yawDelta) < 0.01 && Math.abs(pitchDelta) < 0.01)
return null
return else
               SyntheticMouseTurnBroker.TurnDelta(yawDelta / stepDeg, pitchDelta / stepDeg)
            }
      }
   }

   fun applyUnfocusedRotation(player: ClientPlayerEntity) {
      if (this.isActive() as Boolean) {
         if (this.currentPlan != null) {
            val sample: SmoothMouseAimController.AimSample = this.sample(this.currentPlan, System.currentTimeMillis())
            player.setYaw(sample.yaw)
            player.setPitch(sample.pitch)
            player.headYaw = sample.yaw
            player.bodyYaw = sample.yaw
         }
      }
   }

   fun createPlan(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, baseDurationMs: Int, maxDurationMs: Int, nowMs: Long): SmoothMouseAimController.AimPlan {





      var `this24lambda_u241`: Any
      try {
         `this24lambda_u241` = Result.constructor_impl/* $VF was: constructor-impl */(
            var14.getMc().options.getMouseSensitivity().getValue() as Double
         )
      } catch (var17: java.lang.Throwable) {
         `this24lambda_u241` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var17))
      }

      SmoothMouseAimController.AimPlan(
         player.getYaw(),
         player.getPitch(),
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

      if (elapsed >= plan.durationMs) {
         return SmoothMouseAimController.AimSample(MathHelper.wrapDegrees(plan.targetYaw), (plan.targetPitch).coerceIn(-89.9F, 89.9F))
      } else {

         return SmoothMouseAimController.AimSample(
            MathHelper.wrapDegrees(
               plan.startYaw + this.cubicBezier(0.0, plan.controlYawOne.toDouble(), plan.controlYawTwo.toDouble(), plan.deltaYaw.toDouble(), eased).toFloat()
            ),
            (plan.startPitch + this.cubicBezier(0.0, plan.controlPitchOne.toDouble(), plan.controlPitchTwo.toDouble(), plan.deltaPitch.toDouble(), eased).toFloat()).coerceIn(-89.9F, 89.9F)
         )
      }
   }

   private fun cubicBezier(p0: Double, p1: Double, p2: Double, p3: Double, t: Double): Double {
      return (1.0 - t) * (1.0 - t) * (1.0 - t) * p0 + 3.0 * (1.0 - t) * (1.0 - t) * t * p1 + 3.0 * (1.0 - t) * t * t * p2 + t * t * t * p3
   }

   private fun sensitivityStepDeg(sensitivity: Double): Float {

      return ((scaled * scaled * scaled * 1.2).toFloat()).coerceAtLeast(0.01F)
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
      val startYaw: Float
      val startPitch: Float
      val targetYaw: Float
      val targetPitch: Float
      val deltaYaw: Float
      val deltaPitch: Float
      val controlYawOne: Float
      val controlPitchOne: Float
      val controlYawTwo: Float
      val controlPitchTwo: Float
      val startAtMs: Long
      val durationMs: Int
      val sensitivityStepDeg: Float

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

      fun copy(
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
return sensitivityStepDeg
         )
      }

      override fun toString(): String {
         return "AimPlan(startYaw=${this.startYaw}, startPitch=${this.startPitch}, targetYaw=${this.targetYaw}, targetPitch=${this.targetPitch}, deltaYaw=${this.deltaYaw}, deltaPitch=${this.deltaPitch}, controlYawOne=${this.controlYawOne}, controlPitchOne=${this.controlPitchOne}, controlYawTwo=${this.controlYawTwo}, controlPitchTwo=${this.controlPitchTwo}, startAtMs=${this.startAtMs}, durationMs=${this.durationMs}, sensitivityStepDeg=${this.sensitivityStepDeg})"
      }

      override fun hashCode(): Int {
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

      override operator fun equals(other: Any?): Boolean {
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
      val yaw: Float
      val pitch: Float

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

      fun copy(yaw: Float = this.yaw, pitch: Float = this.pitch): jooon.util.SmoothMouseAimController.AimSample {
         return SmoothMouseAimController.AimSample(yaw, pitch)
      }

      override fun toString(): String {
         return "AimSample(yaw=${this.yaw}, pitch=${this.pitch})"
      }

      override fun hashCode(): Int {
         return java.lang.Float.hashCode(this.yaw) * 31 + java.lang.Float.hashCode(this.pitch)
      }

      override operator fun equals(other: Any?): Boolean {
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
