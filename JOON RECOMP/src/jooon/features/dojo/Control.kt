package jooon.features.dojo

import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import jooon.config.Config
import jooon.mixins.ServerboundInteractPacketAccessor
import jooon.util.PingUtil
import jooon.util.PlayerController
import jooon.util.ScoreboardUtil
import jooon.util.SmoothMouseAimController
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.Blocks
import net.minecraft.class_2824.class_5908
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.WitherSkeletonEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

@SourceDebugExtension(["SMAP\nControl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Control.kt\njooon/features/dojo/Control\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,991:1\n1#2:992\n473#3:993\n473#3:1011\n3819#4:994\n4337#4,2:995\n1963#5,14:997\n*S KotlinDebug\n*F\n+ 1 Control.kt\njooon/features/dojo/Control\n*L\n291#1:993\n810#1:1011\n673#1:994\n673#1:995,2\n679#1:997,14\n*E\n"])
public object Control {
   private const val SKELETON_SEARCH_RADIUS: Double = 36.0
   private const val PLATFORM_SCAN_RADIUS: Int = 7
   private const val CENTER_DEADZONE: Double = 0.18
   private const val CENTER_WALK_DEADZONE: Double = 0.04
   private const val CENTER_SPRINT_DISTANCE: Double = 2.4
   private const val CENTER_EMERGENCY_DISTANCE: Double = 0.72
   private const val CENTER_ULTRA_EMERGENCY_DISTANCE: Double = 0.45
   private const val CENTER_EMERGENCY_DEADZONE: Double = 0.08
   private const val CENTER_EMERGENCY_SPRINT_DISTANCE: Double = 0.24
   private const val ZOMBIE_SEARCH_RADIUS: Double = 2.7
   private const val ZOMBIE_ENGAGE_DISTANCE: Double = 2.25
   private const val ZOMBIE_ENGAGE_HORIZONTAL: Double = 1.95
   private const val ZOMBIE_ATTACK_MOVE_DIST: Double = 3.15
   private const val ZOMBIE_ATTACK_REACH_PAD: Double = 0.15
   private const val ATTACK_COOLDOWN_TICKS: Int = 2
   private const val CONTROL_LEAD_MIN_MS: Int = 80
   private const val CONTROL_LEAD_MAX_MS: Int = 500
   private const val JUMP_VERTICAL_THRESHOLD: Double = 0.045
   private const val JUMP_UP_VERTICAL_LEAD_TICKS: Double = 2.2
   private const val JUMP_DOWN_VERTICAL_LEAD_TICKS: Double = 2.8
   private const val JUMP_VERTICAL_LEAD_MIN: Double = -0.85
   private const val JUMP_VERTICAL_LEAD_MAX: Double = 0.95
   private const val SPEEDUP_POINTS: Int = 500
   private const val ULTRA_SPEEDUP_POINTS: Int = 700
   private const val SPEEDUP_PREARM_POINTS: Int = 450
   private const val ULTRA_SPEEDUP_PREARM_POINTS: Int = 680
   private const val LOW_TIME_FAST_AIM_SECONDS: Int = 14
   private const val CRITICAL_TIME_ULTRA_AIM_SECONDS: Int = 4
   private const val HORIZONTAL_LEAD_MAX_TICKS: Double = 10.5
   private const val HORIZONTAL_PREDICTION_MAX_BLOCKS: Double = 3.25
   private const val ULTRA_HORIZONTAL_PREDICTION_MAX_BLOCKS: Double = 3.85
   private const val CORE_AIM_PROBE_COUNT: Int = 3
   private const val NORMAL_AIM_PROBE_COUNT: Int = 3
   private const val FAST_AIM_PROBE_COUNT: Int = 3
   private const val JUMP_PRESSURE_AIM_PROBE_COUNT: Int = 9
   private const val DIAMOND_HOLD_TICKS: Int = 45
   private const val GOOD_HOLD_TICKS: Int = 10
   private const val CALIBRATION_PROBE_FAILURE_TICKS: Int = 14
   private const val PRESSURE_PROBE_FAILURE_TICKS: Int = 8

   private final val aimController: SmoothMouseAimController = SmoothMouseAimController("dojo_automatic_control", { 
      INSTANCE.isControlAutomationActive() && !isPaused
   })

   private final val AIM_PROBES: Array<jooon.features.dojo.Control.AimProbe>
   private final var active: Boolean
   private final var isPaused: Boolean
   private final var scanTick: Int
   @JvmStatic
   private WitherSkeletonEntity skeletonEntity;
   @JvmStatic
   private Vec3d platformCenter;
   private final var platformFeetY: Int?
   private final var meleeTargetId: Int?
   private final var lastClickTick: Int = -100
   private final var swappedAtTick: Int = -100
   private final val aimStates: Array<jooon.features.dojo.Control.AimState>
   private final var lastAppliedAimPhase: jooon.features.dojo.Control.AimPhase
   private final var lastObservedAttackEntityId: Int
   private final val presumedHitIds: MutableSet<Int>
   private final val POINTS_REGEX: Regex
   private final val TIME_REGEX: Regex

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { client: MinecraftClient ->
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

   fun onOutgoingPacket(packet: Packet<*>) {
      if (packet is PlayerInteractEntityC2SPacket) {
         if (active && Config.fullyAutomaticControl) {
            val isAttack: BooleanRef = BooleanRef()
            (packet as PlayerInteractEntityC2SPacket).method_34209(object : class_5908 {
               fun method_34219(hand: Hand) {
               }

               fun method_34220(hand: Hand, location: Vec3d) {
               }

               fun method_34218() {
                  isAttack.element = true
               }
            })
            if (isAttack.element) {
               val var4: Control = this

               var `$this$onOutgoingPacket_u24lambda_u243`: Control
               try {
                  `$this$onOutgoingPacket_u24lambda_u243` = var4
                  `$this$onOutgoingPacket_u24lambda_u243` = (Control)Result.constructor_impl/* $VF was: constructor-impl */(
                     (packet as ServerboundInteractPacketAccessor).jooonEntityId()
                  )
               } catch (var7: java.lang.Throwable) {
                  `$this$onOutgoingPacket_u24lambda_u243` = (Control)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7))
               }

               val entityId: Int = ((
                     if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$onOutgoingPacket_u24lambda_u243`))
                        -1
                        else
                        `$this$onOutgoingPacket_u24lambda_u243`
                  ) as java.lang.Number)
                  .intValue()
                  if (entityId >= 0) {
                  lastObservedAttackEntityId = entityId
               }
            }
         }
      }
   }

   private fun isControlAutomationActive(): Boolean {
      return Config.fullyAutomaticControl && AutoDojo.INSTANCE.isChallengeActive(AutoDojo.Challenge.CONTROL)
   }

   fun updateSkeletonTarget(level: ClientWorld, player: ClientPlayerEntity) {
      val current: WitherSkeletonEntity = skeletonEntity
      if (skeletonEntity == null || !this.isValidSkeleton(skeletonEntity) || player.method_5858(current as Entity) > 1296.0) {
         skeletonEntity = this.pickSkeleton(level, player)
      }
   }

   fun isValidSkeleton(entity: WitherSkeletonEntity): Boolean {
      entity.method_5805() && !entity.method_31481() && !(entity.method_6118(EquipmentSlot.field_6169).method_7909() == Items.field_8793)
   }

   fun aimAtControlBox(level: ClientWorld, player: ClientPlayerEntity, score: Control.ScoreDebug, quality: Control.TargetQuality): Control.AimDebug {
      val skeleton: WitherSkeletonEntity = skeletonEntity
      if (skeletonEntity != null && this.isValidSkeleton(skeletonEntity)) {
         val speedTier: java.lang.String = this.speedTierFor(score)
         val jumpMode: java.lang.String = this.jumpModeFor(skeleton)
         val probeIndex: Int = this.selectAimProbe(quality, score, speedTier, jumpMode)
         val aim: Control.AimDebug = this.soopyStylePingAimPos(level, skeleton, speedTier, AIM_PROBES[probeIndex])
         val phase: Control.AimPhase = this.aimPhaseFor(jumpMode)
         this.aimStateFor(phase).lastAppliedIndex = probeIndex
         lastAppliedAimPhase = phase
         val var12: Pair = this.aimAngles(player, aim.getPos())
         val targetYaw: Float = (var12.component1() as java.lang.Number).floatValue()
         val targetPitch: Float = (var12.component2() as java.lang.Number).floatValue()
         val jumping: Boolean = !(aim.jumpMode == "GROUND")
         val fast: Boolean = speedTier == "FAST"
         aimController.request(
            player,
            targetYaw,
            targetPitch,
            if (speedTier == "ULTRA" && jumping)
               10
               else
               (if (jumping && fast) 14 else (if (jumping) 22 else (if (speedTier == "ULTRA") 22 else (if (fast) 34 else 58)))),
            if (speedTier == "ULTRA" && jumping)
               22
               else
               (if (jumping && fast) 30 else (if (jumping) 46 else (if (speedTier == "ULTRA") 48 else (if (fast) 72 else 118)))),
            if (speedTier == "ULTRA" && jumping)
               0.12F
               else
               (if (jumping && fast) 0.25F else (if (jumping) 0.45F else (if (speedTier == "ULTRA") 0.45F else (if (fast) 0.9F else 2.4F)))),
            if (speedTier == "ULTRA") 0L else (if (jumping) 0L else (if (fast) 5L else 18L))
         )
         aim
      } else {
         aimController.clear()
         null
      }
   }

   fun soopyStylePingAimPos(level: ClientWorld, skeleton: WitherSkeletonEntity, speedTier: java.lang.String, probe: Control.AimProbe): Control.AimDebug {
      val tuning: Control.ControlTuning = Control.ControlTuningStore.get$default(Control.ControlTuningStore.INSTANCE, 0L, 1, null)
      val basePing: Int = PingUtil.INSTANCE.getControlLeadPing()
      val dynamicPing: Int = RangesKt.coerceIn(RangesKt.coerceAtLeast(basePing + tuning.controlLeadBiasMs, tuning.controlLeadMinMs), 80, 500)
      val baseLeadTicks: Double = dynamicPing / 1000.0 * 20.0
      val motionX: Double = skeleton.method_23317() - skeleton.field_6014
      val motionZ: Double = skeleton.method_23321() - skeleton.field_5969
      val horizontalVelocity: Double = Math.hypot(motionX, motionZ)
      val rawLeadTicks: Double = RangesKt.coerceIn(baseLeadTicks + probe.leadOffsetTicks, 0.5, 10.5)
      val leadTicks: Double = RangesKt.coerceAtMost(
         rawLeadTicks, if (horizontalVelocity > 0.05) RangesKt.coerceAtLeast((if (speedTier == "ULTRA") 3.85 else 3.25) / horizontalVelocity, 0.5) else 10.5
      )
      val horizontalPrediction: Double = horizontalVelocity * leadTicks
      val x: Double = skeleton.method_23317() + motionX * leadTicks
      val verticalVelocity: Double = skeleton.method_23318() - skeleton.field_6036
      val jumpMode: java.lang.String = this.jumpModeFor(verticalVelocity)
      val verticalLead: Double = RangesKt.coerceIn(
         (
               if (jumpMode == "JUMP_UP")
                  RangesKt.coerceAtLeast(verticalVelocity * 2.2, 0.0)
                  else
                  (if (jumpMode == "JUMP_DOWN") RangesKt.coerceAtMost(verticalVelocity * 2.8, 0.0) else verticalVelocity * Math.min(leadTicks, 1.0) * 0.1)
            )
            + probe.verticalOffset,
         -0.85,
         0.95
      )
      var var38: Double = skeleton.method_23318() + verticalLead + skeleton.method_5751()
      val z: Double = skeleton.method_23321() + motionZ * leadTicks

      // $VF: Unable to resugar Kotlin loop from Java for loop
      lifts = 0
      while (true) {
         if (lifts < 12 && !level.method_8320(BlockPos.method_49637(x, var38, z)).method_26215()) break
         var38 += 0.2

         lifts++
      }

      Control.AimDebug(
         Vec3d(x, var38, z),
         basePing,
         dynamicPing,
         rawLeadTicks,
         leadTicks,
         speedTier,
         probe.label,
         horizontalVelocity,
         horizontalPrediction,
         jumpMode,
         verticalVelocity,
         verticalLead,
         lifts
      )
   }

   private fun updateAimProbeFeedback(
      quality: jooon.features.dojo.Control.TargetQuality?,
      score: jooon.features.dojo.Control.ScoreDebug,
      allowAdjustment: Boolean
   ) {
      if (allowAdjustment) {
         if (quality != null) {
            val pressure: Boolean = this.isAimPressure(score, this.speedTierFor(score))
            val feedbackPhase: Control.AimPhase = lastAppliedAimPhase
            val state: Control.AimState = this.aimStateFor(lastAppliedAimPhase)
            val jumpMode: java.lang.String = feedbackPhase.jumpMode
            val tuning: Control.ControlTuning = Control.ControlTuningStore.get$default(Control.ControlTuningStore.INSTANCE, 0L, 1, null)
            val diamondSeek: Boolean = this.isDiamondSeek(score, tuning)
            val feedbackProbeIndex: Int = this.normalizedProbeIndex(state.lastAppliedIndex, this.probeLimitFor(jumpMode, pressure), state)
            val pointDelta: Int = score.pointDelta
            val timeDelta: Int = score.timeDeltaSeconds
            val productive: Boolean = pointDelta != null && pointDelta >= 6 && (timeDelta == null || timeDelta >= 0)
            val timeDropping: Boolean = timeDelta != null && timeDelta < 0
            val jumping: Boolean = !(jumpMode == "GROUND")
            val goodEnough: Boolean = quality.score >= (if (diamondSeek) 3 else 2) && pointDelta != null && pointDelta != 0 && !timeDropping
            state.scores[feedbackProbeIndex] = state.scores[feedbackProbeIndex] * 0.72
               + (quality.score * 5.0 + this.pointRewardFor(quality, pointDelta, diamondSeek, tuning) + (if (timeDropping) -3.0 else 0.0)) * 0.28
               if (quality.score >= 3 && !timeDropping) {
               this.lockAimProbe(state, feedbackProbeIndex, jumpMode, pressure)
               state.holdTicks = RangesKt.coerceAtMost(state.holdTicks + 2, tuning.diamondHoldTicks)
               state.lowQualityTicks = 0
               state.failureTicks = 0
            } else if (goodEnough) {
               if (pressure || productive) {
                  this.lockAimProbe(state, feedbackProbeIndex, jumpMode, pressure)
               }

               state.holdTicks = RangesKt.coerceAtMost(state.holdTicks + 1, this.goodHoldLimit(score, tuning, diamondSeek))
               state.lowQualityTicks = 0
               state.failureTicks = 0
            } else {
               state.holdTicks = RangesKt.coerceAtLeast(state.holdTicks - 1, 0)
               state.lowQualityTicks = RangesKt.coerceAtMost(state.lowQualityTicks + 1, 200)
               if (pressure && (quality.score < 2 || pointDelta != null && pointDelta == 0 || timeDropping)) {
                  state.failureTicks = RangesKt.coerceAtMost(
                     state.failureTicks + (if (jumping && quality.score < 2) 2 else 1), this.failureLimitForPressure(pressure, jumpMode, diamondSeek, tuning)
                  )
               } else {
                  state.failureTicks = RangesKt.coerceAtLeast(state.failureTicks - 1, 0)
               }
            }
         }
      }
   }

   private fun selectAimProbe(
      quality: jooon.features.dojo.Control.TargetQuality?,
      score: jooon.features.dojo.Control.ScoreDebug,
      speedTier: String,
      jumpMode: String
   ): Int {
      val pressure: Boolean = this.isAimPressure(score, speedTier)
      val state: Control.AimState = this.aimStateFor(this.aimPhaseFor(jumpMode))
      val tuning: Control.ControlTuning = Control.ControlTuningStore.get$default(Control.ControlTuningStore.INSTANCE, 0L, 1, null)
      val diamondSeek: Boolean = this.isDiamondSeek(score, tuning)
      val stableQualityScore: Int = if (diamondSeek) 3 else 2
      val probeLimit: Int = this.probeLimitFor(jumpMode, pressure)
      var var10000: Control.AimState = state
      var var10001: Int = state.lockedIndex
      if (var10001 != null) {
         var10001 = INSTANCE.normalizedProbeIndex(var10001.intValue(), probeLimit, state)
         var10000 = state
      } else {
         var10001 = null
      }

      var lowQuality: Boolean
      var lowQualityBlocksLock: Boolean
      var var24: Boolean
      run label334@{
         run label333@{
            var10000.lockedIndex = var10001
            val positivePoints: Boolean = score.pointDelta != null && score.pointDelta > 0
            val noTimeLoss: Boolean = score.timeDeltaSeconds == null || score.timeDeltaSeconds >= 0
            var24 = score.pointDelta != null && score.pointDelta >= 6 && noTimeLoss
            val var25: Boolean = score.timeDeltaSeconds != null && score.timeDeltaSeconds < 0
            lowQuality = quality == null || quality.score < stableQualityScore
            lowQualityBlocksLock = lowQuality
               && (!diamondSeek || quality == null || quality.score != 2 || !positivePoints || !noTimeLoss || state.holdTicks <= 0)
               && (
                  !diamondSeek || quality == null || quality.score < 1 || !positivePoints || !noTimeLoss || state.holdTicks < tuning.diamondSeekRecentLockTicks
               )
               if (pressure && !(jumpMode == "GROUND")) {
               if (lowQualityBlocksLock) {
                  return@label333
               }

               val var27: Int = score.pointDelta
               if (var27 != null && var27 == 0 || var25) {
                  return@label333
               }
            }

            var28 = false
            return@label334
         }

         var28 = true
      }

      val failureLimit: Int = this.failureLimitForPressure(pressure, jumpMode, diamondSeek, tuning)
      val lockedProbe: Int = state.lockedIndex
      if (lockedProbe == null || var28 || lowQualityBlocksLock || state.failureTicks >= failureLimit || !pressure && state.holdTicks <= 0 && !var24) {
         if (pressure) {
            val var26: Int = if (!var28 && (lockedProbe == null || state.failureTicks < failureLimit))
               this.bestAimProbeIndex(state, probeLimit)
               else
               this.nextPressureProbe(state, lockedProbe, probeLimit, jumpMode)
               this.lockAimProbe(state, var26, jumpMode, true)
            state.failureTicks = 0
            state.activeIndex = var26
            return state.activeIndex
         } else if (!lowQuality && var24 && state.holdTicks > 0) {
            state.activeIndex = this.normalizedProbeIndex(state.lastAppliedIndex, probeLimit, state)
            return state.activeIndex
         } else if (quality == null || quality.score != 3 || state.holdTicks <= 0 || score.timeDeltaSeconds != null && score.timeDeltaSeconds < 0) {
            if (quality != null && quality.score == 2 && state.holdTicks > 0 && score.pointDelta != null) {
               val var29: Int = score.pointDelta
               if ((var29 == null || var29 != 0) && (score.timeDeltaSeconds == null || score.timeDeltaSeconds >= 0)) {
                  state.activeIndex = this.normalizedProbeIndex(state.lastAppliedIndex, probeLimit, state)
                  return state.activeIndex
               }
            }

            run label342@{
               label341@
               if (quality != null && quality.score >= 2) {
                  if (!var24 && quality.score < 3) {
                     val var30: Int = score.pointDelta
                     if (var30 != null && var30 == 0) {
                        break@label341
                     }
                  }

                  if (score.timeDeltaSeconds == null || score.timeDeltaSeconds >= 0) {
                     var31 = false
                     return@label342
                  }
               }

               var31 = true
            }

            if (!var31 && state.lowQualityTicks <= 0) {
               state.activeIndex = if (quality.score >= 3)
                  this.normalizedProbeIndex(state.lastAppliedIndex, probeLimit, state)
                  else
                  this.bestAimProbeIndex(state, probeLimit)
                  return state.activeIndex
            } else {
               val var33: Int
               if (state.lowQualityTicks >= 14) {
                  state.cursor = (state.cursor + 1) % probeLimit
                  var33 = state.cursor
               } else {
                  var33 = this.bestAimProbeIndex(state, probeLimit)
               }

               state.activeIndex = var33
               return state.activeIndex
            }
         } else {
            state.activeIndex = this.normalizedProbeIndex(state.lastAppliedIndex, probeLimit, state)
            return state.activeIndex
         }
      } else {
         state.activeIndex = lockedProbe
         return state.activeIndex
      }
   }

   private fun speedTierFor(score: jooon.features.dojo.Control.ScoreDebug): String {
      var var10000: Int = score.points
      val points: Int = var10000 ?: 0
      var10000 = score.timeSeconds
      val timeSeconds: Int = var10000 ?: Integer.MAX_VALUE
      return if (points < 700 && points < 680 && timeSeconds > 4) (if (points < 500 && points < 450 && timeSeconds > 14) "NORMAL" else "FAST") else "ULTRA"
   }

   private fun isAimPressure(score: jooon.features.dojo.Control.ScoreDebug, speedTier: String): Boolean {
      var var10000: Int = score.points
      val points: Int = var10000 ?: 0
      var10000 = score.timeSeconds
      return !(speedTier == "NORMAL") || points >= 450 || (var10000 ?: Integer.MAX_VALUE) <= 14
   }

   private fun isDiamondSeek(score: jooon.features.dojo.Control.ScoreDebug, tuning: jooon.features.dojo.Control.ControlTuning): Boolean {
      if (!tuning.autoDiamondSeek) {
         return false
      } else {
         var var10000: Int = score.points
         val points: Int = var10000 ?: 0
         var10000 = score.timeSeconds
         return points >= tuning.diamondSeekPoints || (var10000 ?: Integer.MAX_VALUE) <= tuning.diamondSeekTimeSeconds
      }
   }

   private fun goodHoldLimit(score: jooon.features.dojo.Control.ScoreDebug, tuning: jooon.features.dojo.Control.ControlTuning, diamondSeek: Boolean): Int {
      if (diamondSeek) {
         return tuning.diamondSeekGoodHoldTicks
      } else {
         var var10000: Int = score.points
         val points: Int = var10000 ?: 0
         var10000 = score.timeSeconds
         return if (points < 680 && (var10000 ?: Integer.MAX_VALUE) > 14) tuning.normalGoodHoldTicks else tuning.ultraGoodHoldTicks
      }
   }

   private fun pointRewardFor(
      quality: jooon.features.dojo.Control.TargetQuality,
      pointDelta: Int?,
      diamondSeek: Boolean,
      tuning: jooon.features.dojo.Control.ControlTuning
   ): Double {
      if (diamondSeek && quality.score == 2) {
         return tuning.diamondSeekGoodPointPenalty
      } else if (diamondSeek && quality.score == 1) {
         return tuning.diamondSeekAcceptPointPenalty
      } else if (quality.score < 2) {
         return if (pointDelta == null) 0.0 else tuning.badPointPenalty
      } else {
         return if (pointDelta == null)
            0.0
            else
            (if (pointDelta >= 10) 4.0 else (if (pointDelta >= 6) 2.5 else (if (pointDelta >= 3) 0.8 else tuning.badPointPenalty)))
         }
   }

   fun jumpModeFor(skeleton: WitherSkeletonEntity): java.lang.String {
      this.jumpModeFor(skeleton.method_23318() - skeleton.field_6036)
   }

   private fun jumpModeFor(verticalVelocity: Double): String {
      return if (verticalVelocity > 0.045) "JUMP_UP" else (if (verticalVelocity < -0.045) "JUMP_DOWN" else "GROUND")
   }

   private fun probeLimitFor(jumpMode: String, pressure: Boolean): Int {
      return if (pressure && !(jumpMode == "GROUND")) 9 else (if (pressure) 3 else 3)
   }

   private fun aimPhaseFor(jumpMode: String): jooon.features.dojo.Control.AimPhase {
      return if (jumpMode == "JUMP_UP") Control.AimPhase.JUMP_UP else (if (jumpMode == "JUMP_DOWN") Control.AimPhase.JUMP_DOWN else Control.AimPhase.GROUND)
   }

   private fun aimStateFor(phase: jooon.features.dojo.Control.AimPhase): jooon.features.dojo.Control.AimState {
      return aimStates[phase.ordinal()]
   }

   private fun normalizedProbeIndex(index: Int, limit: Int, state: jooon.features.dojo.Control.AimState): Int {
      val bounded: Int = RangesKt.coerceIn(index, 0, ArraysKt.getLastIndex(AIM_PROBES))
      val boundedLimit: Int = RangesKt.coerceIn(limit, 1, AIM_PROBES.length)
      return if (bounded < boundedLimit) bounded else this.bestAimProbeIndex(state, boundedLimit)
   }

   private fun lockAimProbe(state: jooon.features.dojo.Control.AimState, index: Int, jumpMode: String, pressure: Boolean) {
      val normalized: Int = this.normalizedProbeIndex(index, this.probeLimitFor(jumpMode, pressure), state)
      state.lockedIndex = normalized
      state.cursor = normalized
   }

   private fun nextPressureProbe(state: jooon.features.dojo.Control.AimState, exclude: Int?, limit: Int, jumpMode: String): Int {
      val `$this$maxByOrNull$iv`: IntArray = this.pressureProbeOrder(jumpMode)
      val `$i$f$maxByOrNull`: java.util.Collection = ArrayList()

      for (`e$iv` in `$this$maxByOrNull$iv`) {
         if (`e$iv` < RangesKt.coerceAtMost(limit, AIM_PROBES.length)) {
            `$i$f$maxByOrNull`.add(`e$iv`)
         }
      }

      val candidates: java.util.List = `$i$f$maxByOrNull` as java.util.List
      if ((`$i$f$maxByOrNull` as java.util.List).isEmpty()) {
         return this.bestAimProbeIndex(state, limit)
      } else if (exclude != null && candidates.contains(exclude)) {
         return (candidates.get((candidates.indexOf(exclude) + 1) % candidates.size()) as java.lang.Number).intValue()
      } else {
         val var22: java.util.Iterator = candidates.iterator()
         val var10000: Any
         if (!var22.hasNext()) {
            var10000 = null
         } else {
            var var23: Any = var22.next()
            if (!var22.hasNext()) {
               var10000 = var23
            } else {
               var var25: Double = state.scores[(var23 as java.lang.Number).intValue()]

               do {
                  val var27: Any = var22.next()
                  val var29: Double = state.scores[(var27 as java.lang.Number).intValue()]
                  if (java.lang.Double.compare(var25, var29) < 0) {
                     var23 = var27
                     var25 = var29
                  }
               } while (var22.hasNext())

               var10000 = var23
            }
         }

         val var18: Int = if (var10000 as Int != null) var10000 as Int else this.bestAimProbeIndex(state, limit)
         return if (exclude != null && var18 == exclude) (CollectionsKt.first(candidates) as java.lang.Number).intValue() else var18
      }
   }

   private fun pressureProbeOrder(jumpMode: String): IntArray {
      return if (!(jumpMode == "JUMP_UP") && !(jumpMode == "JUMP_DOWN")) intArrayOf(0, 1, 2) else intArrayOf(0, 1, 2, 3, 5, 7, 4, 6, 8)
   }

   private fun failureLimitForPressure(
      pressure: Boolean,
      jumpMode: String,
      diamondSeek: Boolean = false,
      tuning: jooon.features.dojo.Control.ControlTuning = Control.ControlTuningStore.get$default(Control.ControlTuningStore.INSTANCE, 0L, 1, null)
   ): Int {
      return if (!pressure) 14 else (if (diamondSeek) tuning.diamondSeekProbeFailureTicks else 8)
   }

   private fun bestAimProbeIndex(state: jooon.features.dojo.Control.AimState, limit: Int = 3): Int {
      var bestIndex: Int = 0
      var bestScore: Double = java.lang.Double.NEGATIVE_INFINITY
      var i: Int = 0

      for (var7 in RangesKt.coerceAtMost(limit, AIM_PROBES.length)..i) {
         if (state.scores[i] > bestScore) {
            bestScore = state.scores[i]
            bestIndex = i
         }
      }

      return bestIndex
   }

   fun updatePlatformCenter(level: ClientWorld, player: ClientPlayerEntity) {
      val feetY: Int = MathHelper.method_15357(player.method_23318())
      val baseX: Int = MathHelper.method_15357(player.method_23317())
      val baseZ: Int = MathHelper.method_15357(player.method_23321())
      var bestY: Int = null
      var bestCount: Int = 0
      var bestMinX: Int = baseX
      var bestMaxX: Int = baseX
      var bestMinZ: Int = baseZ
      var bestMaxZ: Int = baseZ
      var below: Int = feetY - 2
      if (feetY - 2 <= feetY) {
         while (true) {
            var count: Int = 0
            var minX: Int = Integer.MAX_VALUE
            var maxX: Int = Integer.MIN_VALUE
            var minZ: Int = Integer.MAX_VALUE
            var maxZ: Int = Integer.MIN_VALUE

            for (dx in -7..7) {
               for (dz in -7..7) {
                  val pos: BlockPos = BlockPos(baseX + dx, below, baseZ + dz)
                  if (level.method_8320(pos).method_26204() == Blocks.field_10552) {
                     count++
                     minX = Math.min(minX, pos.method_10263())
                     maxX = Math.max(maxX, pos.method_10263())
                     minZ = Math.min(minZ, pos.method_10260())
                     maxZ = Math.max(maxZ, pos.method_10260())
                  }
               }
            }

            if (count > bestCount) {
               bestY = below + 1
               bestCount = count
               bestMinX = minX
               bestMaxX = maxX
               bestMinZ = minZ
               bestMaxZ = maxZ
            }

            if (below == feetY) {
               break
            }

            below++
         }
      }

      if (bestY != null && bestCount >= 4) {
         platformFeetY = bestY
         platformCenter = Vec3d((bestMinX + bestMaxX + 1) * 0.5, bestY.intValue(), (bestMinZ + bestMaxZ + 1) * 0.5)
      } else {
         val var10000: BlockPos = player.method_24515().method_10074()
         if (level.method_8320(var10000).method_26204() == Blocks.field_10552) {
            platformFeetY = var10000.method_10264() + 1
            platformCenter = Vec3d.method_24953(var10000 as Vec3i).method_1031(0.0, 0.5, 0.0)
         }
      }
   }

   fun isEmergencyRecenterNeeded(player: ClientPlayerEntity, speedTier: java.lang.String): Boolean {
      if (platformCenter == null) {
         false
      } else {
         val center: Vec3d = platformCenter
         val threshold: Double = if (speedTier == "ULTRA") 0.45 else 0.72
         val var10001: Vec3d = player.method_73189()
         this.horizontalDistance(var10001, center) >= threshold
      }
   }

   fun moveToPlatformCenter(player: ClientPlayerEntity, urgent: Boolean) {
      if (platformCenter == null) {
         this.stopMotion()
      } else {
         val center: Vec3d = platformCenter
         val dx: Double = platformCenter.field_1352 - player.method_23317()
         val dz: Double = center.field_1350 - player.method_23321()
         val dist: Double = Math.hypot(dx, dz)
         if (dist <= (if (urgent) 0.08 else 0.18)) {
            PlayerController.INSTANCE.pressForward(false)
            PlayerController.INSTANCE.pressBack(false)
            PlayerController.INSTANCE.pressLeft(false)
            PlayerController.INSTANCE.pressRight(false)
            PlayerController.INSTANCE.pressSprint(false)
            player.method_5728(false)
         } else {
            val yawRad: Double = Math.toRadians((double)player.method_36454())
            val forwardInput: Double = RangesKt.coerceIn(dx * -Math.sin(yawRad) + dz * Math.cos(yawRad), -1.0, 1.0)
            val strafeInput: Double = RangesKt.coerceIn(dx * Math.cos(yawRad) + dz * Math.sin(yawRad), -1.0, 1.0)
            val sprint: Boolean = dist > (if (urgent) 0.24 else 2.4)
            PlayerController.INSTANCE.pressForward(forwardInput > 0.04)
            PlayerController.INSTANCE.pressBack(forwardInput < -0.04)
            PlayerController.INSTANCE.pressLeft(strafeInput > 0.04)
            PlayerController.INSTANCE.pressRight(strafeInput < -0.04)
            PlayerController.INSTANCE.pressSprint(sprint)
            player.method_5728(sprint)
         }
      }
   }

   fun isDangerouslyCloseZombie(player: ClientPlayerEntity, zombie: ZombieEntity): Boolean {
      player.method_5739(zombie as Entity) <= 2.25
         || Math.hypot(zombie.method_23317() - player.method_23317(), zombie.method_23321() - player.method_23321()) <= 1.95
         || player.method_76729(zombie.method_5829(), 0.15)
      }

   fun aimAtZombie(player: ClientPlayerEntity, target: ZombieEntity) {
      val var4: Pair = this.aimAngles(player, Vec3d(target.method_23317(), target.method_23318() + (double)target.method_17682() * 0.62, target.method_23321()))
      aimController.request(
         player, (var4.component1() as java.lang.Number).floatValue(), (var4.component2() as java.lang.Number).floatValue(), 42, 88, 4.0F, 12L
      )
   }

   fun tryAttack(player: ClientPlayerEntity, target: ZombieEntity) {
      if (player.field_6012 - swappedAtTick > 1) {
         if (player.field_6012 - lastClickTick >= 2) {
            if (player.method_6047().method_7909() == Items.field_8091) {
               if (this.isCrosshairOnTarget(target)) {
                  if (PlayerController.INSTANCE.leftClick()) {
                     lastClickTick = player.field_6012
                  }
               }
            }
         }
      }
   }

   fun canAttemptAttack(player: ClientPlayerEntity, target: ZombieEntity): Boolean {
      player.method_76729(target.method_5829(), 0.15) && player.method_5739(target as Entity) <= 3.15 && this.isCrosshairOnTarget(target)
   }

   fun isCrosshairOnTarget(target: ZombieEntity): Boolean {
      val var3: HitResult = this.getMc().field_1765
      (var3 as? EntityHitResult) != null && (var3 as? EntityHitResult).method_17782().method_5628() == target.method_5628()
   }

   private fun consumeObservedAttack() {
      val attackedId: Int = lastObservedAttackEntityId
      if (lastObservedAttackEntityId >= 0) {
         lastObservedAttackEntityId = -1
         presumedHitIds.add(attackedId)
         if (meleeTargetId != null && meleeTargetId == attackedId) {
            meleeTargetId = null
         }
      }
   }

   fun cleanupPresumedHits(level: ClientWorld) {
      if (!presumedHitIds.isEmpty()) {
         val var10000: java.lang.Iterable = level.method_18112()
         presumedHitIds.removeIf({ p0: Any ->
            `$tmp0`(p0)
         })
      }
   }

   fun aimAngles(player: ClientPlayerEntity, target: Vec3d): Pair<java.lang.Float, java.lang.Float> {
      val dx: Double = target.field_1352 - player.method_23317()
      val dy: Double = target.field_1351 - player.method_23320()
      val dz: Double = target.field_1350 - player.method_23321()
      TuplesKt.to(
         (float)Math.toDegrees(Math.atan2(-dx, dz)), (float)Math.toDegrees(-Math.atan2(dy, RangesKt.coerceAtLeast(Math.sqrt(dx * dx + dz * dz), 0.001)))
      )
   }

   fun targetQuality(skeleton: WitherSkeletonEntity): Control.TargetQuality {
      if (skeleton != null) {
         val var10000: ItemStack = skeleton.method_6118(EquipmentSlot.field_6169)
         if (var10000 != null) {
            val var5: Item = var10000.method_7909()
            if (var5 != null) {
               val var6: java.lang.String = var5.method_7876()
               val itemName: java.lang.String = StringsKt.replace$default(
                  StringsKt.substringAfterLast$default(var6, '.', null, 2, null), '_', ' ', false, 4, null
               )
               if (var5 == Items.field_8603)
                  Control.TargetQuality("VERY_GOOD", "§b", 3, itemName)
                  else
                  (
                     if (var5 == Items.field_8494)
                        Control.TargetQuality("GOOD", "§6", 2, itemName)
                        else
                        (if (var5 == Items.field_8773) Control.TargetQuality("ACCEPT", "§f", 1, itemName) else Control.TargetQuality("BAD", "§c", 0, itemName))
                  )
               }
         }
      }

      null
   }

   private fun readScoreDebug(): jooon.features.dojo.Control.ScoreDebug {
      var points: Any = null
      val pointDelta: ObjectRef = ObjectRef()
      var timeSeconds: Any = null
      val timeDeltaSeconds: ObjectRef = ObjectRef()

      for (line in ScoreboardUtil.INSTANCE.getSidebarLines()) {
         var var10000: MatchResult = Regex.find$default(POINTS_REGEX, line, 0, 2, null)
         if (var10000 != null) {
            var var24: Int
            run label72@{
               points = StringsKt.toIntOrNull(var10000.getGroupValues().get(1) as java.lang.String)
               var19 = pointDelta
               val var10001: java.lang.String = CollectionsKt.getOrNull(var10000.getGroupValues(), 2) as java.lang.String
               if (var10001 != null) {
                  val var22: java.lang.Boolean = var10001.length() > 0
                  var19 = pointDelta
                  val var23: java.lang.String = if (var22) var10001 else null
                  if (var23 != null) {
                     var24 = StringsKt.toIntOrNull(var23)
                     return@label72
                  }
               }

               var24 = null
            }

            var19.element = var24
         }

         var10000 = Regex.find$default(TIME_REGEX, line, 0, 2, null)
         if (var10000 != null) {
            var var28: Int
            run label77@{
               timeSeconds = StringsKt.toIntOrNull(var10000.getGroupValues().get(1) as java.lang.String)
               var21 = timeDeltaSeconds
               val var25: java.lang.String = CollectionsKt.getOrNull(var10000.getGroupValues(), 2) as java.lang.String
               if (var25 != null) {
                  val var26: java.lang.Boolean = var25.length() > 0
                  var21 = timeDeltaSeconds
                  val var27: java.lang.String = if (var26) var25 else null
                  if (var27 != null) {
                     var28 = StringsKt.toIntOrNull(var27)
                     return@label77
                  }
               }

               var28 = null
            }

            var21.element = var28
         }
      }

      return Control.ScoreDebug((Integer)points, pointDelta.element as Int, (Integer)timeSeconds, timeDeltaSeconds.element as Int)
   }

   fun horizontalDistance(a: Vec3d, b: Vec3d): Double {
      Math.hypot(a.field_1352 - b.field_1352, a.field_1350 - b.field_1350)
   }

   private fun clearCombatTargets() {
      skeletonEntity = null
      meleeTargetId = null
      lastObservedAttackEntityId = -1
      presumedHitIds.clear()
   }

   private fun stopMotion() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
      PlayerController.INSTANCE.pressSprint(false)
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var10000.method_5728(false)
      }
   }

   private fun reset() {
      active = false
      isPaused = false
      DojoPauseInput.INSTANCE.reset()
      scanTick = 0
      this.clearCombatTargets()
      platformCenter = null
      platformFeetY = null
      lastClickTick = -100
      swappedAtTick = -100

      for (state in aimStates) {
         Arrays.fill(state.scores, 0.0)
         state.activeIndex = 0
         state.lastAppliedIndex = 0
         state.cursor = 0
         state.lockedIndex = null
         state.failureTicks = 0
         state.holdTicks = 0
         state.lowQualityTicks = 0
      }

      lastAppliedAimPhase = Control.AimPhase.GROUND
      AutoDojo.INSTANCE.subtitle = ""
      aimController.clear()
      this.stopMotion()
   }

   @JvmStatic
   fun {
      var var4: Int = 0
      val var1: Int = Control.AimPhase.getEntries().size()
      val var2: Array<Control.AimState> = arrayOfNulls(var1)

      while (var4 < var1) {
         var2[var4] = Control.AimState()
         var4++
      }

      aimStates = var2
      lastAppliedAimPhase = Control.AimPhase.GROUND
      lastObservedAttackEntityId = -1
      presumedHitIds = LinkedHashSet<>()
      POINTS_REGEX = Regex("Points:\\s*(-?\\d+)(?:\\s*\\(([+-]?\\d+)\\))?")
      TIME_REGEX = Regex("Time:\\s*(-?\\d+)s?(?:\\s*\\(([+-]?\\d+)s?\\))?")
   }

   private data class AimDebug {
      private Vec3d pos;
      public final val basePingMs: Int
      public final val pingMs: Int
      public final val rawLeadTicks: Double
      public final val leadTicks: Double
      public final val speedTier: String
      public final val probeLabel: String
      public final val horizontalVelocity: Double
      public final val horizontalPrediction: Double
      public final val jumpMode: String
      public final val verticalVelocity: Double
      public final val verticalLead: Double
      public final val lifts: Int

      fun AimDebug(
         pos: Vec3d,
         basePingMs: Int,
         pingMs: Int,
         rawLeadTicks: Double,
         leadTicks: Double,
         speedTier: java.lang.String,
         probeLabel: java.lang.String,
         horizontalVelocity: Double,
         horizontalPrediction: Double,
         jumpMode: java.lang.String,
         verticalVelocity: Double,
         verticalLead: Double,
         lifts: Int
      ) {
         this.pos = pos
         this.basePingMs = basePingMs
         this.pingMs = pingMs
         this.rawLeadTicks = rawLeadTicks
         this.leadTicks = leadTicks
         this.speedTier = speedTier
         this.probeLabel = probeLabel
         this.horizontalVelocity = horizontalVelocity
         this.horizontalPrediction = horizontalPrediction
         this.jumpMode = jumpMode
         this.verticalVelocity = verticalVelocity
         this.verticalLead = verticalLead
         this.lifts = lifts
      }

      fun getPos(): Vec3d {
         this.pos
      }

      fun component1(): Vec3d {
         this.pos
      }

      public operator fun component2(): Int {
         return this.basePingMs
      }

      public operator fun component3(): Int {
         return this.pingMs
      }

      public operator fun component4(): Double {
         return this.rawLeadTicks
      }

      public operator fun component5(): Double {
         return this.leadTicks
      }

      public operator fun component6(): String {
         return this.speedTier
      }

      public operator fun component7(): String {
         return this.probeLabel
      }

      public operator fun component8(): Double {
         return this.horizontalVelocity
      }

      public operator fun component9(): Double {
         return this.horizontalPrediction
      }

      public operator fun component10(): String {
         return this.jumpMode
      }

      public operator fun component11(): Double {
         return this.verticalVelocity
      }

      public operator fun component12(): Double {
         return this.verticalLead
      }

      public operator fun component13(): Int {
         return this.lifts
      }

      fun copy(
         pos: Vec3d,
         basePingMs: Int,
         pingMs: Int,
         rawLeadTicks: Double,
         leadTicks: Double,
         speedTier: java.lang.String,
         probeLabel: java.lang.String,
         horizontalVelocity: Double,
         horizontalPrediction: Double,
         jumpMode: java.lang.String,
         verticalVelocity: Double,
         verticalLead: Double,
         lifts: Int
      ): Control.AimDebug {
         Control.AimDebug(
            pos,
            basePingMs,
            pingMs,
            rawLeadTicks,
            leadTicks,
            speedTier,
            probeLabel,
            horizontalVelocity,
            horizontalPrediction,
            jumpMode,
            verticalVelocity,
            verticalLead,
            lifts
         )
      }

      public override fun toString(): String {
         return "AimDebug(pos=${this.pos}, basePingMs=${this.basePingMs}, pingMs=${this.pingMs}, rawLeadTicks=${this.rawLeadTicks}, leadTicks=${this.leadTicks}, speedTier=${this.speedTier}, probeLabel=${this.probeLabel}, horizontalVelocity=${this.horizontalVelocity}, horizontalPrediction=${this.horizontalPrediction}, jumpMode=${this.jumpMode}, verticalVelocity=${this.verticalVelocity}, verticalLead=${this.verticalLead}, lifts=${this.lifts})"
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
                                                                                                            this.pos.hashCode() * 31
                                                                                                               + Integer.hashCode(this.basePingMs)
                                                                                                         )
                                                                                                         * 31
                                                                                                      + Integer.hashCode(this.pingMs)
                                                                                                )
                                                                                                * 31
                                                                                             + java.lang.Double.hashCode(this.rawLeadTicks)
                                                                                       )
                                                                                       * 31
                                                                                    + java.lang.Double.hashCode(this.leadTicks)
                                                                              )
                                                                              * 31
                                                                           + this.speedTier.hashCode()
                                                                     )
                                                                     * 31
                                                                  + this.probeLabel.hashCode()
                                                            )
                                                            * 31
                                                         + java.lang.Double.hashCode(this.horizontalVelocity)
                                                   )
                                                   * 31
                                                + java.lang.Double.hashCode(this.horizontalPrediction)
                                          )
                                          * 31
                                       + this.jumpMode.hashCode()
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.verticalVelocity)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.verticalLead)
               )
               * 31
            + Integer.hashCode(this.lifts)
         }

      public override operator fun equals(other: Any?): Boolean {
         label94@
         if (this === other) {
            return true
         } else {
            return other is Control.AimDebug
               && this.pos == (other as Control.AimDebug).pos
               && this.basePingMs == (other as Control.AimDebug).basePingMs
               && this.pingMs == (other as Control.AimDebug).pingMs
               && java.lang.Double.compare(this.rawLeadTicks, (other as Control.AimDebug).rawLeadTicks) == 0
               && java.lang.Double.compare(this.leadTicks, (other as Control.AimDebug).leadTicks) == 0
               && this.speedTier == (other as Control.AimDebug).speedTier
               && this.probeLabel == (other as Control.AimDebug).probeLabel
               && java.lang.Double.compare(this.horizontalVelocity, (other as Control.AimDebug).horizontalVelocity) == 0
               && java.lang.Double.compare(this.horizontalPrediction, (other as Control.AimDebug).horizontalPrediction) == 0
               && this.jumpMode == (other as Control.AimDebug).jumpMode
               && java.lang.Double.compare(this.verticalVelocity, (other as Control.AimDebug).verticalVelocity) == 0
               && java.lang.Double.compare(this.verticalLead, (other as Control.AimDebug).verticalLead) == 0
               && this.lifts == (other as Control.AimDebug).lifts
            }
      }
   }

   private enum class AimPhase(jumpMode: String) {
      GROUND("GROUND"),
      JUMP_UP("JUMP_UP"),
      JUMP_DOWN("JUMP_DOWN");

      public final val jumpMode: String

      init {
         this.jumpMode = jumpMode
      }

      @JvmStatic
      fun getEntries(): EnumEntries<Control.AimPhase> {
         $ENTRIES
      }
   }

   private data class AimProbe(label: String, leadOffsetTicks: Double, verticalOffset: Double) {
      public final val label: String
      public final val leadOffsetTicks: Double
      public final val verticalOffset: Double

      init {
         this.label = label
         this.leadOffsetTicks = leadOffsetTicks
         this.verticalOffset = verticalOffset
      }

      public operator fun component1(): String {
         return this.label
      }

      public operator fun component2(): Double {
         return this.leadOffsetTicks
      }

      public operator fun component3(): Double {
         return this.verticalOffset
      }

      public fun copy(label: String = this.label, leadOffsetTicks: Double = this.leadOffsetTicks, verticalOffset: Double = this.verticalOffset): jooon.features.dojo.Control.AimProbe {
         return Control.AimProbe(label, leadOffsetTicks, verticalOffset)
      }

      public override fun toString(): String {
         return "AimProbe(label=${this.label}, leadOffsetTicks=${this.leadOffsetTicks}, verticalOffset=${this.verticalOffset})"
      }

      public override fun hashCode(): Int {
         return (this.label.hashCode() * 31 + java.lang.Double.hashCode(this.leadOffsetTicks)) * 31 + java.lang.Double.hashCode(this.verticalOffset)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is Control.AimProbe
               && this.label == (other as Control.AimProbe).label
               && java.lang.Double.compare(this.leadOffsetTicks, (other as Control.AimProbe).leadOffsetTicks) == 0
               && java.lang.Double.compare(this.verticalOffset, (other as Control.AimProbe).verticalOffset) == 0
            }
      }
   }

   private class AimState {
      public final val scores: DoubleArray = DoubleArray(Control.AIM_PROBES.length)
      public final var activeIndex: Int
      public final var lastAppliedIndex: Int
      public final var cursor: Int
      public final var lockedIndex: Int?
      public final var failureTicks: Int
      public final var holdTicks: Int
      public final var lowQualityTicks: Int
   }

   private data class ControlTuning(autoDiamondSeek: Boolean = true,
      diamondSeekPoints: Int = 650,
      diamondSeekTimeSeconds: Int = 5,
      controlLeadBiasMs: Int = 220,
      controlLeadMinMs: Int = 430,
      diamondHoldTicks: Int = 45,
      normalGoodHoldTicks: Int = 10,
      ultraGoodHoldTicks: Int = 4,
      diamondSeekGoodHoldTicks: Int = 1,
      diamondSeekRecentLockTicks: Int = 12,
      diamondSeekProbeFailureTicks: Int = 14,
      badPointPenalty: Double = -4.0,
      diamondSeekGoodPointPenalty: Double = -2.0,
      diamondSeekAcceptPointPenalty: Double = -5.0
   ) {
      public final var autoDiamondSeek: Boolean
      public final var diamondSeekPoints: Int
      public final var diamondSeekTimeSeconds: Int
      public final var controlLeadBiasMs: Int
      public final var controlLeadMinMs: Int
      public final var diamondHoldTicks: Int
      public final var normalGoodHoldTicks: Int
      public final var ultraGoodHoldTicks: Int
      public final var diamondSeekGoodHoldTicks: Int
      public final var diamondSeekRecentLockTicks: Int
      public final var diamondSeekProbeFailureTicks: Int
      public final var badPointPenalty: Double
      public final var diamondSeekGoodPointPenalty: Double
      public final var diamondSeekAcceptPointPenalty: Double

      init {
         this.autoDiamondSeek = autoDiamondSeek
         this.diamondSeekPoints = diamondSeekPoints
         this.diamondSeekTimeSeconds = diamondSeekTimeSeconds
         this.controlLeadBiasMs = controlLeadBiasMs
         this.controlLeadMinMs = controlLeadMinMs
         this.diamondHoldTicks = diamondHoldTicks
         this.normalGoodHoldTicks = normalGoodHoldTicks
         this.ultraGoodHoldTicks = ultraGoodHoldTicks
         this.diamondSeekGoodHoldTicks = diamondSeekGoodHoldTicks
         this.diamondSeekRecentLockTicks = diamondSeekRecentLockTicks
         this.diamondSeekProbeFailureTicks = diamondSeekProbeFailureTicks
         this.badPointPenalty = badPointPenalty
         this.diamondSeekGoodPointPenalty = diamondSeekGoodPointPenalty
         this.diamondSeekAcceptPointPenalty = diamondSeekAcceptPointPenalty
      }

      public operator fun component1(): Boolean {
         return this.autoDiamondSeek
      }

      public operator fun component2(): Int {
         return this.diamondSeekPoints
      }

      public operator fun component3(): Int {
         return this.diamondSeekTimeSeconds
      }

      public operator fun component4(): Int {
         return this.controlLeadBiasMs
      }

      public operator fun component5(): Int {
         return this.controlLeadMinMs
      }

      public operator fun component6(): Int {
         return this.diamondHoldTicks
      }

      public operator fun component7(): Int {
         return this.normalGoodHoldTicks
      }

      public operator fun component8(): Int {
         return this.ultraGoodHoldTicks
      }

      public operator fun component9(): Int {
         return this.diamondSeekGoodHoldTicks
      }

      public operator fun component10(): Int {
         return this.diamondSeekRecentLockTicks
      }

      public operator fun component11(): Int {
         return this.diamondSeekProbeFailureTicks
      }

      public operator fun component12(): Double {
         return this.badPointPenalty
      }

      public operator fun component13(): Double {
         return this.diamondSeekGoodPointPenalty
      }

      public operator fun component14(): Double {
         return this.diamondSeekAcceptPointPenalty
      }

      public fun copy(
         autoDiamondSeek: Boolean = this.autoDiamondSeek,
         diamondSeekPoints: Int = this.diamondSeekPoints,
         diamondSeekTimeSeconds: Int = this.diamondSeekTimeSeconds,
         controlLeadBiasMs: Int = this.controlLeadBiasMs,
         controlLeadMinMs: Int = this.controlLeadMinMs,
         diamondHoldTicks: Int = this.diamondHoldTicks,
         normalGoodHoldTicks: Int = this.normalGoodHoldTicks,
         ultraGoodHoldTicks: Int = this.ultraGoodHoldTicks,
         diamondSeekGoodHoldTicks: Int = this.diamondSeekGoodHoldTicks,
         diamondSeekRecentLockTicks: Int = this.diamondSeekRecentLockTicks,
         diamondSeekProbeFailureTicks: Int = this.diamondSeekProbeFailureTicks,
         badPointPenalty: Double = this.badPointPenalty,
         diamondSeekGoodPointPenalty: Double = this.diamondSeekGoodPointPenalty,
         diamondSeekAcceptPointPenalty: Double = this.diamondSeekAcceptPointPenalty
      ): jooon.features.dojo.Control.ControlTuning {
         return Control.ControlTuning(
            autoDiamondSeek,
            diamondSeekPoints,
            diamondSeekTimeSeconds,
            controlLeadBiasMs,
            controlLeadMinMs,
            diamondHoldTicks,
            normalGoodHoldTicks,
            ultraGoodHoldTicks,
            diamondSeekGoodHoldTicks,
            diamondSeekRecentLockTicks,
            diamondSeekProbeFailureTicks,
            badPointPenalty,
            diamondSeekGoodPointPenalty,
            diamondSeekAcceptPointPenalty
         )
      }

      public override fun toString(): String {
         return "ControlTuning(autoDiamondSeek=${this.autoDiamondSeek}, diamondSeekPoints=${this.diamondSeekPoints}, diamondSeekTimeSeconds=${this.diamondSeekTimeSeconds}, controlLeadBiasMs=${this.controlLeadBiasMs}, controlLeadMinMs=${this.controlLeadMinMs}, diamondHoldTicks=${this.diamondHoldTicks}, normalGoodHoldTicks=${this.normalGoodHoldTicks}, ultraGoodHoldTicks=${this.ultraGoodHoldTicks}, diamondSeekGoodHoldTicks=${this.diamondSeekGoodHoldTicks}, diamondSeekRecentLockTicks=${this.diamondSeekRecentLockTicks}, diamondSeekProbeFailureTicks=${this.diamondSeekProbeFailureTicks}, badPointPenalty=${this.badPointPenalty}, diamondSeekGoodPointPenalty=${this.diamondSeekGoodPointPenalty}, diamondSeekAcceptPointPenalty=${this.diamondSeekAcceptPointPenalty})"
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
                                                                                                            (
                                                                                                                     java.lang.Boolean.hashCode(
                                                                                                                              this.autoDiamondSeek
                                                                                                                           )
                                                                                                                           * 31
                                                                                                                        + Integer.hashCode(
                                                                                                                           this.diamondSeekPoints
                                                                                                                        )
                                                                                                                  )
                                                                                                                  * 31
                                                                                                               + Integer.hashCode(this.diamondSeekTimeSeconds)
                                                                                                         )
                                                                                                         * 31
                                                                                                      + Integer.hashCode(this.controlLeadBiasMs)
                                                                                                )
                                                                                                * 31
                                                                                             + Integer.hashCode(this.controlLeadMinMs)
                                                                                       )
                                                                                       * 31
                                                                                    + Integer.hashCode(this.diamondHoldTicks)
                                                                              )
                                                                              * 31
                                                                           + Integer.hashCode(this.normalGoodHoldTicks)
                                                                     )
                                                                     * 31
                                                                  + Integer.hashCode(this.ultraGoodHoldTicks)
                                                            )
                                                            * 31
                                                         + Integer.hashCode(this.diamondSeekGoodHoldTicks)
                                                   )
                                                   * 31
                                                + Integer.hashCode(this.diamondSeekRecentLockTicks)
                                          )
                                          * 31
                                       + Integer.hashCode(this.diamondSeekProbeFailureTicks)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.badPointPenalty)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.diamondSeekGoodPointPenalty)
               )
               * 31
            + java.lang.Double.hashCode(this.diamondSeekAcceptPointPenalty)
         }

      public override operator fun equals(other: Any?): Boolean {
         label100@
         if (this === other) {
            return true
         } else {
            return other is Control.ControlTuning
               && this.autoDiamondSeek == (other as Control.ControlTuning).autoDiamondSeek
               && this.diamondSeekPoints == (other as Control.ControlTuning).diamondSeekPoints
               && this.diamondSeekTimeSeconds == (other as Control.ControlTuning).diamondSeekTimeSeconds
               && this.controlLeadBiasMs == (other as Control.ControlTuning).controlLeadBiasMs
               && this.controlLeadMinMs == (other as Control.ControlTuning).controlLeadMinMs
               && this.diamondHoldTicks == (other as Control.ControlTuning).diamondHoldTicks
               && this.normalGoodHoldTicks == (other as Control.ControlTuning).normalGoodHoldTicks
               && this.ultraGoodHoldTicks == (other as Control.ControlTuning).ultraGoodHoldTicks
               && this.diamondSeekGoodHoldTicks == (other as Control.ControlTuning).diamondSeekGoodHoldTicks
               && this.diamondSeekRecentLockTicks == (other as Control.ControlTuning).diamondSeekRecentLockTicks
               && this.diamondSeekProbeFailureTicks == (other as Control.ControlTuning).diamondSeekProbeFailureTicks
               && java.lang.Double.compare(this.badPointPenalty, (other as Control.ControlTuning).badPointPenalty) == 0
               && java.lang.Double.compare(this.diamondSeekGoodPointPenalty, (other as Control.ControlTuning).diamondSeekGoodPointPenalty) == 0
               && java.lang.Double.compare(this.diamondSeekAcceptPointPenalty, (other as Control.ControlTuning).diamondSeekAcceptPointPenalty) == 0
            }
      }

      fun ControlTuning() {
         this(false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 16383, null)
      }
   }

   private object ControlTuningStore {
      private final val current: jooon.features.dojo.Control.ControlTuning =
         Control.ControlTuning(false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 16383, null)

      public fun get(now: Long = System.currentTimeMillis()): jooon.features.dojo.Control.ControlTuning {
         return current
      }
   }

   private data class ScoreDebug(points: Int?, pointDelta: Int?, timeSeconds: Int?, timeDeltaSeconds: Int?) {
      public final val points: Int?
      public final val pointDelta: Int?
      public final val timeSeconds: Int?
      public final val timeDeltaSeconds: Int?

      init {
         this.points = points
         this.pointDelta = pointDelta
         this.timeSeconds = timeSeconds
         this.timeDeltaSeconds = timeDeltaSeconds
      }

      public operator fun component1(): Int? {
         return this.points
      }

      public operator fun component2(): Int? {
         return this.pointDelta
      }

      public operator fun component3(): Int? {
         return this.timeSeconds
      }

      public operator fun component4(): Int? {
         return this.timeDeltaSeconds
      }

      public fun copy(
         points: Int? = this.points,
         pointDelta: Int? = this.pointDelta,
         timeSeconds: Int? = this.timeSeconds,
         timeDeltaSeconds: Int? = this.timeDeltaSeconds
      ): jooon.features.dojo.Control.ScoreDebug {
         return Control.ScoreDebug(points, pointDelta, timeSeconds, timeDeltaSeconds)
      }

      public override fun toString(): String {
         return "ScoreDebug(points=${this.points}, pointDelta=${this.pointDelta}, timeSeconds=${this.timeSeconds}, timeDeltaSeconds=${this.timeDeltaSeconds})"
      }

      public override fun hashCode(): Int {
         return (
                  ((if (this.points == null) 0 else this.points.hashCode()) * 31 + (if (this.pointDelta == null) 0 else this.pointDelta.hashCode())) * 31
                     + (if (this.timeSeconds == null) 0 else this.timeSeconds.hashCode())
               )
               * 31
            + (if (this.timeDeltaSeconds == null) 0 else this.timeDeltaSeconds.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is Control.ScoreDebug
               && this.points == (other as Control.ScoreDebug).points
               && this.pointDelta == (other as Control.ScoreDebug).pointDelta
               && this.timeSeconds == (other as Control.ScoreDebug).timeSeconds
               && this.timeDeltaSeconds == (other as Control.ScoreDebug).timeDeltaSeconds
            }
      }
   }

   private data class TargetQuality(label: String, color: String, score: Int, itemName: String) {
      public final val label: String
      public final val color: String
      public final val score: Int
      public final val itemName: String

      init {
         this.label = label
         this.color = color
         this.score = score
         this.itemName = itemName
      }

      public operator fun component1(): String {
         return this.label
      }

      public operator fun component2(): String {
         return this.color
      }

      public operator fun component3(): Int {
         return this.score
      }

      public operator fun component4(): String {
         return this.itemName
      }

      public fun copy(label: String = this.label, color: String = this.color, score: Int = this.score, itemName: String = this.itemName): jooon.features.dojo.Control.TargetQuality {
         return Control.TargetQuality(label, color, score, itemName)
      }

      public override fun toString(): String {
         return "TargetQuality(label=${this.label}, color=${this.color}, score=${this.score}, itemName=${this.itemName})"
      }

      public override fun hashCode(): Int {
         return ((this.label.hashCode() * 31 + this.color.hashCode()) * 31 + Integer.hashCode(this.score)) * 31 + this.itemName.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is Control.TargetQuality
               && this.label == (other as Control.TargetQuality).label
               && this.color == (other as Control.TargetQuality).color
               && this.score == (other as Control.TargetQuality).score
               && this.itemName == (other as Control.TargetQuality).itemName
            }
      }
   }
}
