package jooon.features.dojo

import java.util.ArrayList
import java.util.HashMap
import java.util.PriorityQueue
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSwiftnessMovementLogic.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SwiftnessMovementLogic.kt\njooon/features/dojo/SwiftnessMovementLogic\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,917:1\n1549#2:918\n1620#2,3:919\n1726#2,3:922\n1726#2,3:925\n*S KotlinDebug\n*F\n+ 1 SwiftnessMovementLogic.kt\njooon/features/dojo/SwiftnessMovementLogic\n*L\n614#1:918\n614#1:919,3\n635#1:922,3\n737#1:925,3\n*E\n"])
public object SwiftnessMovementLogic {
   public const val MAX_GAP_BLOCKS: Int = 3
   public const val MAX_SUPPORTED_WALK_STEPS: Int = 4
   public const val LANE_ERROR_LIMIT: Double = 0.1
   public const val ONE_BLOCK_LANE_ERROR_LIMIT: Double = 0.36
   public const val TWO_BLOCK_LANE_ERROR_LIMIT: Double = 0.36
   public const val THREE_BLOCK_LANE_ERROR_LIMIT: Double = 0.22
   public const val STRAIGHT_THREE_BLOCK_LANE_ERROR_LIMIT: Double = 0.4
   public const val SHORT_ANGLED_TWO_GAP_LANE_ERROR_LIMIT: Double = 0.5
   public const val YAW_ERROR_LIMIT_DEGREES: Double = 12.0
   public const val CAPTURE_DISTANCE: Double = 0.62
   private final val cardinalDirections: List<jooon.features.dojo.SwiftnessMovementLogic.Direction> =
      CollectionsKt.listOf(
         arrayOf(
            SwiftnessMovementLogic.Direction(1, 0),
            SwiftnessMovementLogic.Direction(-1, 0),
            SwiftnessMovementLogic.Direction(0, 1),
            SwiftnessMovementLogic.Direction(0, -1)
         )
      )
      private final val diagonalDirections: List<jooon.features.dojo.SwiftnessMovementLogic.Direction> =
      CollectionsKt.listOf(
         arrayOf(
            SwiftnessMovementLogic.Direction(1, 1),
            SwiftnessMovementLogic.Direction(1, -1),
            SwiftnessMovementLogic.Direction(-1, 1),
            SwiftnessMovementLogic.Direction(-1, -1)
         )
      )

   public fun targetFeetNode(targetBlock: jooon.features.dojo.SwiftnessMovementLogic.Node): jooon.features.dojo.SwiftnessMovementLogic.Node {
      return targetBlock.above()
   }

   public fun jumpTuning(gapBlocks: Int): jooon.features.dojo.SwiftnessMovementLogic.JumpTuning {
      var var10000: SwiftnessMovementLogic.JumpTuning
      when (gapBlocks) {
         1 -> var10000 = SwiftnessMovementLogic.JumpTuning(gapBlocks, 0.0, 0.22, 1, false, false, false, 0.96, 1.3, 2.58, 0.36)
         2 -> var10000 = SwiftnessMovementLogic.JumpTuning(gapBlocks, 0.0, 0.22, 2, false, true, true, 2.18, 2.56, 3.78, 0.36)
         3 -> var10000 = SwiftnessMovementLogic.JumpTuning(gapBlocks, -0.36, 0.42, 3, true, true, true, 3.34, 3.72, 4.62, 0.22)
         else -> throw IllegalStateException(("Unsupported Swiftness jump gap: $gapBlocks").toString())
      }

      return var10000
   }

   public fun jumpTuningForDistance(gapBlocks: Int, horizontalDistance: Double, angled: Boolean): jooon.features.dojo.SwiftnessMovementLogic.JumpTuning {
      val base: SwiftnessMovementLogic.JumpTuning = this.jumpTuning(gapBlocks)
      if (!angled) {
         return if (gapBlocks == 3)
            SwiftnessMovementLogic.JumpTuning.copy$default(base, 0, 0.0, 0.0, 0, false, false, false, 0.0, 0.0, 0.0, 0.4, 1023, null)
            else
            base
         } else if (gapBlocks == 1 && horizontalDistance <= 1.5) {
         return SwiftnessMovementLogic.JumpTuning.copy$default(
            base, 0, 0.0, 0.2, 1, false, false, false, 0.72, 0.96, horizontalDistance + 0.75, 0.0, 1139, null
         )
      } else {
         return if (gapBlocks == 2 && horizontalDistance <= 2.35)
            SwiftnessMovementLogic.JumpTuning.copy$default(
               base,
               0,
               0.0,
               0.28,
               1,
               false,
               false,
               false,
               Math.max(1.1, horizontalDistance - 1.04),
               Math.max(1.32, horizontalDistance - 0.78),
               horizontalDistance + 0.18,
               0.5,
               1,
               null
            )
            else
            base
         }
   }

   public fun laneErrorLimit(gapBlocks: Int): Double {
      var var10000: Double
      when (gapBlocks) {
         1 -> var10000 = 0.36
         2 -> var10000 = 0.36
         else -> var10000 = 0.22
      }

      return var10000
   }

   public fun airborneControl(gapBlocks: Int, projection: Double): jooon.features.dojo.SwiftnessMovementLogic.AirborneControl {
      val tuning: SwiftnessMovementLogic.JumpTuning = this.jumpTuning(gapBlocks)
      return if (projection >= tuning.brakeProjection)
         SwiftnessMovementLogic.AirborneControl(false, true, false)
         else
         (
            if (projection >= tuning.forwardReleaseProjection)
               SwiftnessMovementLogic.AirborneControl(false, false, false)
               else
               SwiftnessMovementLogic.AirborneControl(true, false, tuning.airborneSprint)
         )
      }

   public fun nextJumpPhase(
      current: jooon.features.dojo.SwiftnessMovementLogic.JumpPhase,
      gapBlocks: Int,
      metrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics
   ): jooon.features.dojo.SwiftnessMovementLogic.JumpPhase {
      return this.nextJumpPhase(current, this.jumpTuning(gapBlocks), metrics)
   }

   public fun nextJumpPhase(
      current: jooon.features.dojo.SwiftnessMovementLogic.JumpPhase,
      tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning,
      metrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics
   ): jooon.features.dojo.SwiftnessMovementLogic.JumpPhase {
      val aligned: Boolean = metrics.laneError <= tuning.laneErrorLimit && metrics.yawError <= 12.0
val yawAligned: Boolean = metrics.yawError <= 12.0
var var10000: SwiftnessMovementLogic.JumpPhase
      when (SwiftnessMovementLogic.WhenMappings.$EnumSwitchMapping$0[current.ordinal()]) {
         1 -> var10000 = SwiftnessMovementLogic.JumpPhase.ALIGN
         2 -> var10000 = if (!aligned)
               SwiftnessMovementLogic.JumpPhase.ALIGN
               else
               (
                  if (tuning.requiresBackstep)
                     SwiftnessMovementLogic.JumpPhase.BACKSTEP
                     else
                     (
                        if (metrics.onGround && metrics.projection >= tuning.jumpTriggerProjection && !this.shouldAbortLateLaunch(tuning, metrics))
                           SwiftnessMovementLogic.JumpPhase.AIRBORNE
                           else
                           SwiftnessMovementLogic.JumpPhase.RUNUP
                     )
               )
            3 -> var10000 = if (yawAligned && metrics.projection <= tuning.backstepProjection + 0.04)
               SwiftnessMovementLogic.JumpPhase.RUNUP
               else
               SwiftnessMovementLogic.JumpPhase.BACKSTEP
            4 -> var10000 = if (metrics.onGround
                  && aligned
                  && metrics.projection >= tuning.jumpTriggerProjection
                  && !this.shouldAbortLateLaunch(tuning, metrics))
               SwiftnessMovementLogic.JumpPhase.AIRBORNE
               else
               SwiftnessMovementLogic.JumpPhase.RUNUP
            5 -> var10000 = if (metrics.onGround && metrics.elapsedTicks >= 2)
               SwiftnessMovementLogic.JumpPhase.CAPTURE
               else
               SwiftnessMovementLogic.JumpPhase.AIRBORNE
            6 -> var10000 = SwiftnessMovementLogic.JumpPhase.CAPTURE
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   public fun shouldAbortLateLaunch(
      tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning,
      metrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics
   ): Boolean {
      return metrics.onGround && metrics.projection > this.maxLaunchProjection(tuning)
   }

   public fun shouldEmergencyCommitLateLaunch(
      tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning,
      metrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics,
      sourceStandable: Boolean,
      sourceFloorExpiring: Boolean = false,
      horizontalDistance: Double = (double)tuning.gapBlocks + 1.0
   ): Boolean {
      if (!metrics.onGround) {
         return false
      } else if (!(metrics.yawError > 12.0) && !(metrics.laneError > tuning.laneErrorLimit)) {
         val launchLimit: Double = this.maxLaunchProjection(tuning)
         if (metrics.projection <= launchLimit) {
            return false
         } else if (tuning.gapBlocks == 1) {
            return metrics.projection <= Math.min(Math.max(launchLimit + 0.18, tuning.brakeProjection + 0.1), horizontalDistance - 0.55)
         } else {
            label79@
            if (tuning.gapBlocks == 2) {
               return (!sourceStandable || sourceFloorExpiring)
                  && metrics.projection <= Math.min(Math.max(launchLimit + 0.55, horizontalDistance - 1.35), horizontalDistance - 0.6)
               } else {
               label78@
               if (sourceStandable && !sourceFloorExpiring) {
                  return false
               } else {
                  return tuning.requiresBackstep
                     && tuning.gapBlocks >= 3
                     && metrics.projection > launchLimit
                     && metrics.projection <= Math.max(launchLimit + 0.7, horizontalDistance - 1.45)
                  }
            }
         }
      } else {
         return false
      }
   }

   public fun shouldSkipBlockedBackstep(
      tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning,
      metrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics,
      backstepBlocked: Boolean,
      sourceFloorExpiring: Boolean = false,
      stalledTicks: Int,
      stallTickLimit: Int
   ): Boolean {
      return tuning.requiresBackstep
         && metrics.onGround
         && !(metrics.yawError > 12.0)
         && !(metrics.laneError > tuning.laneErrorLimit)
         && !(metrics.projection > this.maxLaunchProjection(tuning))
         && (backstepBlocked || sourceFloorExpiring || stalledTicks >= stallTickLimit)
      }

   public fun maxLaunchProjection(tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning): Double {
      var var10000: Double
      when (tuning.gapBlocks) {
         1 -> var10000 = 0.72
         2 -> var10000 = 0.78
         else -> var10000 = 0.92
      }

      return var10000
   }

   public fun shouldCompleteJumpStep(
      onGround: Boolean,
      horizontalToLanding: Double,
      horizontalSpeed: Double = 0.0,
      tolerance: Double = 0.62,
      speedLimit: Double = 0.16
   ): Boolean {
      return onGround && horizontalToLanding <= tolerance && horizontalSpeed <= speedLimit
   }

   public fun shouldAcceptJumpLanding(
      onGround: Boolean,
      elapsedTicks: Int,
      projection: Double,
      horizontalToLanding: Double,
      projectionLimit: Double,
      projectionFloor: Double = java.lang.Double.NEGATIVE_INFINITY,
      missDistance: Double
   ): Boolean {
      return onGround && elapsedTicks >= 2 && projection >= projectionFloor && projection <= projectionLimit && horizontalToLanding <= missDistance
   }

   public fun airborneOverrunProjection(tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning, horizontalDistance: Double, angled: Boolean): Double {
      return if (!angled) tuning.overrunProjection else Math.min(tuning.overrunProjection, horizontalDistance + 0.75)
   }

   public fun landingProjectionLimit(tuning: jooon.features.dojo.SwiftnessMovementLogic.JumpTuning, horizontalDistance: Double, gapBlocks: Int, angled: Boolean): Double {
      return Math.min(
         this.airborneOverrunProjection(tuning, horizontalDistance, angled),
         horizontalDistance + (if (angled && gapBlocks == 1) 0.75 else (if (gapBlocks == 1) 0.58 else (if (gapBlocks >= 2) 0.9 else 0.34)))
      )
   }

   public fun shouldReplanForNewTarget(
      currentTargetSeq: Long,
      routeTargetSeq: Long,
      onGround: Boolean,
      phase: jooon.features.dojo.SwiftnessMovementLogic.JumpPhase
   ): Boolean {
      return currentTargetSeq != routeTargetSeq
         && onGround
         && phase != SwiftnessMovementLogic.JumpPhase.AIRBORNE
         && phase != SwiftnessMovementLogic.JumpPhase.CAPTURE
      }

   public fun shouldPlanRoute(routeMissing: Boolean, targetChanged: Boolean, replanAllowed: Boolean): Boolean {
      return routeMissing || targetChanged && replanAllowed
   }

   public fun isAdjacentCardinalWalk(step: jooon.features.dojo.SwiftnessMovementLogic.Step): Boolean {
      return step.moveType === SwiftnessMovementLogic.MoveType.WALK
         && step.gapBlocks == 0
         && step.to.y == step.from.y
         && Math.abs(step.to.x - step.from.x) + Math.abs(step.to.z - step.from.z) == 1
      }

   public fun shouldMoveDuringWalk(
      step: jooon.features.dojo.SwiftnessMovementLogic.Step,
      yawError: Double,
      floorSafe: Boolean,
      distance: Double,
      normalYawLimit: Double,
      adjacentYawLimit: Double,
      adjacentDistanceLimit: Double
   ): Boolean {
      return floorSafe
         && (yawError <= normalYawLimit || this.isAdjacentCardinalWalk(step) && distance <= adjacentDistanceLimit && yawError <= adjacentYawLimit)
      }

   public fun shouldFastHandoffAfterJump(
      currentTargetSeq: Long,
      routeTargetSeq: Long,
      onGround: Boolean,
      completedGapBlocks: Int,
      landingDistance: Double,
      horizontalSpeed: Double,
      normalDistanceLimit: Double,
      longGapDistanceLimit: Double,
      settledSpeedLimit: Double,
      strictCaptureDistance: Double,
      nextStep: jooon.features.dojo.SwiftnessMovementLogic.Step?,
      nextMetrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics?
   ): Boolean {
      if (currentTargetSeq != routeTargetSeq && onGround) {
         val distanceLimit: Double = if (completedGapBlocks >= 2) longGapDistanceLimit else normalDistanceLimit
         if (!(landingDistance > (if (completedGapBlocks >= 2) longGapDistanceLimit else normalDistanceLimit)) && !(horizontalSpeed > settledSpeedLimit)) {
            if ((if (nextStep != null) nextStep.moveType else null) != SwiftnessMovementLogic.MoveType.JUMP || nextStep.gapBlocks < 3) {
               return true
            } else if (nextMetrics == null) {
               return false
            } else {
               val dx: Int = nextStep.to.x - nextStep.from.x
               val dz: Int = nextStep.to.z - nextStep.from.z
               val tuning: SwiftnessMovementLogic.JumpTuning = this.jumpTuningForDistance(
                  nextStep.gapBlocks, Math.sqrt((double)(dx * dx + dz * dz)), dx != 0 && dz != 0
               )
               return landingDistance <= (if (dx != 0 && dz != 0) strictCaptureDistance else distanceLimit)
                  && nextMetrics.laneError <= tuning.laneErrorLimit
                  && nextMetrics.projection <= this.maxLaunchProjection(tuning)
               }
         } else {
            return false
         }
      } else {
         return false
      }
   }

   public fun shouldLeaveCaptureForChangedTarget(
      currentTargetSeq: Long,
      routeTargetSeq: Long,
      onGround: Boolean,
      landingDistance: Double,
      horizontalSpeed: Double,
      landingFloorIsLime: Boolean,
      maxExpiringLandingDistance: Double = 0.95,
      settledSpeedLimit: Double = 0.16,
      strictCaptureDistance: Double = 0.28,
      nextStep: jooon.features.dojo.SwiftnessMovementLogic.Step? = null,
      nextMetrics: jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics? = null
   ): Boolean {
      if (currentTargetSeq == routeTargetSeq) {
         return false
      } else if (!onGround || landingFloorIsLime) {
         return false
      } else if (horizontalSpeed > settledSpeedLimit) {
         return false
      } else if (landingDistance > maxExpiringLandingDistance) {
         return false
      } else if ((if (nextStep != null) nextStep.moveType else null) != SwiftnessMovementLogic.MoveType.JUMP || nextStep.gapBlocks < 3) {
         return true
      } else if (nextMetrics == null) {
         return false
      } else {
         val dx: Int = nextStep.to.x - nextStep.from.x
         val dz: Int = nextStep.to.z - nextStep.from.z
         val tuning: SwiftnessMovementLogic.JumpTuning = this.jumpTuningForDistance(
            nextStep.gapBlocks, Math.sqrt((double)(dx * dx + dz * dz)), dx != 0 && dz != 0
         )
         return landingDistance <= strictCaptureDistance
            && nextMetrics.laneError <= tuning.laneErrorLimit
            && nextMetrics.projection <= this.maxLaunchProjection(tuning)
         }
   }

   public fun startupReplacementTarget(
      ignoredBlock: jooon.features.dojo.SwiftnessMovementLogic.Node?,
      liveTargetBlocks: Collection<jooon.features.dojo.SwiftnessMovementLogic.Node>
   ): jooon.features.dojo.SwiftnessMovementLogic.Node? {
      return if (ignoredBlock == null)
         null
         else
         SequencesKt.minWithOrNull(
            SequencesKt.filter(CollectionsKt.asSequence(liveTargetBlocks), { it: SwiftnessMovementLogic.Node ->
               !(it == `$ignoredBlock`)
            }),
            SwiftnessMovementLogic$startupReplacementTarget$$inlined$thenBy$1(
               SwiftnessMovementLogic$startupReplacementTarget$$inlined$compareBy$1(ignoredBlock), ignoredBlock
            )
         ) as SwiftnessMovementLogic.Node
      }

   public fun planDirectRoute(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      isStandable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:770)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:736)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc_w "start"
      // 004: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 007: aload 2
      // 008: ldc_w "target"
      // 00b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 00e: aload 3
      // 00f: ldc_w "isStandable"
      // 012: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 015: aload 4
      // 017: ldc_w "isPassable"
      // 01a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 01d: aload 3
      // 01e: aload 4
      // 020: aload 1
      // 021: invokestatic jooon/features/dojo/SwiftnessMovementLogic.planDirectRoute$standable (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)Z
      // 024: ifeq 031
      // 027: aload 3
      // 028: aload 4
      // 02a: aload 2
      // 02b: invokestatic jooon/features/dojo/SwiftnessMovementLogic.planDirectRoute$standable (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)Z
      // 02e: ifne 033
      // 031: aconst_null
      // 032: areturn
      // 033: aload 1
      // 034: aload 2
      // 035: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 038: ifeq 046
      // 03b: new jooon/features/dojo/SwiftnessMovementLogic$Route
      // 03e: dup
      // 03f: invokestatic kotlin/collections/CollectionsKt.emptyList ()Ljava/util/List;
      // 042: invokespecial jooon/features/dojo/SwiftnessMovementLogic$Route.<init> (Ljava/util/List;)V
      // 045: areturn
      // 046: aload 1
      // 047: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getY ()I
      // 04a: aload 2
      // 04b: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getY ()I
      // 04e: if_icmpeq 053
      // 051: aconst_null
      // 052: areturn
      // 053: aload 2
      // 054: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getX ()I
      // 057: aload 1
      // 058: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getX ()I
      // 05b: isub
      // 05c: istore 5
      // 05e: aload 2
      // 05f: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getZ ()I
      // 062: aload 1
      // 063: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getZ ()I
      // 066: isub
      // 067: istore 6
      // 069: iload 5
      // 06b: invokestatic java/lang/Math.abs (I)I
      // 06e: istore 7
      // 070: iload 6
      // 072: invokestatic java/lang/Math.abs (I)I
      // 075: istore 8
      // 077: iload 7
      // 079: iload 8
      // 07b: iadd
      // 07c: istore 9
      // 07e: iload 7
      // 080: bipush 1
      // 081: if_icmpne 0d9
      // 084: iload 8
      // 086: bipush 1
      // 087: if_icmpne 0d9
      // 08a: aload 1
      // 08b: iload 5
      // 08d: bipush 0
      // 08e: bipush 0
      // 08f: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.offset (III)Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 092: astore 10
      // 094: aload 1
      // 095: bipush 0
      // 096: bipush 0
      // 097: iload 6
      // 099: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.offset (III)Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 09c: astore 11
      // 09e: aload 3
      // 09f: aload 4
      // 0a1: aload 10
      // 0a3: invokestatic jooon/features/dojo/SwiftnessMovementLogic.planDirectRoute$standable (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)Z
      // 0a6: ifeq 0cf
      // 0a9: aload 3
      // 0aa: aload 4
      // 0ac: aload 11
      // 0ae: invokestatic jooon/features/dojo/SwiftnessMovementLogic.planDirectRoute$standable (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)Z
      // 0b1: ifeq 0cf
      // 0b4: new jooon/features/dojo/SwiftnessMovementLogic$Route
      // 0b7: dup
      // 0b8: new jooon/features/dojo/SwiftnessMovementLogic$Step
      // 0bb: dup
      // 0bc: aload 1
      // 0bd: aload 2
      // 0be: getstatic jooon/features/dojo/SwiftnessMovementLogic$MoveType.WALK Ljooon/features/dojo/SwiftnessMovementLogic$MoveType;
      // 0c1: bipush 0
      // 0c2: bipush 8
      // 0c4: aconst_null
      // 0c5: invokespecial jooon/features/dojo/SwiftnessMovementLogic$Step.<init> (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$MoveType;IILkotlin/jvm/internal/DefaultConstructorMarker;)V
      // 0c8: invokestatic kotlin/collections/CollectionsKt.listOf (Ljava/lang/Object;)Ljava/util/List;
      // 0cb: invokespecial jooon/features/dojo/SwiftnessMovementLogic$Route.<init> (Ljava/util/List;)V
      // 0ce: areturn
      // 0cf: aload 0
      // 0d0: aload 1
      // 0d1: aload 2
      // 0d2: aload 3
      // 0d3: aload 4
      // 0d5: invokevirtual jooon/features/dojo/SwiftnessMovementLogic.angledJumpRoute (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)Ljooon/features/dojo/SwiftnessMovementLogic$Route;
      // 0d8: areturn
      // 0d9: iload 7
      // 0db: ifle 123
      // 0de: iload 8
      // 0e0: ifle 123
      // 0e3: iload 9
      // 0e5: bipush 4
      // 0e6: if_icmpgt 10f
      // 0e9: aload 0
      // 0ea: aload 1
      // 0eb: aload 2
      // 0ec: aload 0
      // 0ed: iload 5
      // 0ef: invokespecial jooon/features/dojo/SwiftnessMovementLogic.signInt (I)I
      // 0f2: aload 0
      // 0f3: iload 6
      // 0f5: invokespecial jooon/features/dojo/SwiftnessMovementLogic.signInt (I)I
      // 0f8: iload 7
      // 0fa: iload 8
      // 0fc: new jooon/features/dojo/SwiftnessMovementLogic$planDirectRoute$supportedWalk$1
      // 0ff: dup
      // 100: aload 3
      // 101: aload 4
      // 103: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planDirectRoute$supportedWalk$1.<init> (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V
      // 106: checkcast kotlin/jvm/functions/Function1
      // 109: invokespecial jooon/features/dojo/SwiftnessMovementLogic.planSupportedLWalk (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;IIIILkotlin/jvm/functions/Function1;)Ljooon/features/dojo/SwiftnessMovementLogic$Route;
      // 10c: goto 110
      // 10f: aconst_null
      // 110: astore 10
      // 112: aload 10
      // 114: dup
      // 115: ifnonnull 122
      // 118: pop
      // 119: aload 0
      // 11a: aload 1
      // 11b: aload 2
      // 11c: aload 3
      // 11d: aload 4
      // 11f: invokevirtual jooon/features/dojo/SwiftnessMovementLogic.angledJumpRoute (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)Ljooon/features/dojo/SwiftnessMovementLogic$Route;
      // 122: areturn
      // 123: nop
      // 124: iload 7
      // 126: ifle 133
      // 129: iload 8
      // 12b: ifne 133
      // 12e: iload 7
      // 130: goto 144
      // 133: iload 8
      // 135: ifle 142
      // 138: iload 7
      // 13a: ifne 142
      // 13d: iload 8
      // 13f: goto 144
      // 142: aconst_null
      // 143: areturn
      // 144: istore 10
      // 146: new jooon/features/dojo/SwiftnessMovementLogic$Direction
      // 149: dup
      // 14a: aload 0
      // 14b: iload 5
      // 14d: invokespecial jooon/features/dojo/SwiftnessMovementLogic.signInt (I)I
      // 150: aload 0
      // 151: iload 6
      // 153: invokespecial jooon/features/dojo/SwiftnessMovementLogic.signInt (I)I
      // 156: invokespecial jooon/features/dojo/SwiftnessMovementLogic$Direction.<init> (II)V
      // 159: astore 11
      // 15b: aload 0
      // 15c: aload 1
      // 15d: aload 11
      // 15f: iload 10
      // 161: new jooon/features/dojo/SwiftnessMovementLogic$planDirectRoute$1
      // 164: dup
      // 165: aload 3
      // 166: aload 4
      // 168: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planDirectRoute$1.<init> (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V
      // 16b: checkcast kotlin/jvm/functions/Function1
      // 16e: new jooon/features/dojo/SwiftnessMovementLogic$planDirectRoute$2
      // 171: dup
      // 172: aload 4
      // 174: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planDirectRoute$2.<init> (Lkotlin/jvm/functions/Function1;)V
      // 177: checkcast kotlin/jvm/functions/Function1
      // 17a: aload 4
      // 17c: invokespecial jooon/features/dojo/SwiftnessMovementLogic.planStraightCardinalRoute (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Direction;ILkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)Ljooon/features/dojo/SwiftnessMovementLogic$Route;
      // 17f: areturn
   }

   public fun planTemporaryPlatformRoute(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      isStandable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      if (!planTemporaryPlatformRoute$endpointStandable(isStandable, isPassable, start)
         || !planTemporaryPlatformRoute$endpointStandable(isStandable, isPassable, target)) {
         return null
      } else if (start == target) {
         return SwiftnessMovementLogic.Route(CollectionsKt.emptyList())
      } else if (start.y != target.y) {
         return null
      } else {
         val dx: Int = target.x - start.x
         val dz: Int = target.z - start.z
         val absX: Int = Math.abs(dx)
         val absZ: Int = Math.abs(dz)
         if ((absX != 1 || absZ != 0) && (absX != 0 || absZ != 1)) {
            if (absX != 0 && absZ != 0) {
               return this.angledJumpRoute(start, target, { node: SwiftnessMovementLogic.Node ->
                  node == `$start` || node == `$target`
               }, isPassable)
            } else {
               val distance: Int = Math.max(absX, absZ)
               val gapBlocks: Int = distance - 1
               if (1 > distance - 1 || distance - 1 >= 4) {
                  return null
               } else {
                  val stepX: Int = this.signInt(dx)
                  val stepZ: Int = this.signInt(dz)

                  for (i in 1..distance) {
                     if (!planTemporaryPlatformRoute$bodyClear$3(isPassable, start.offset(stepX * i, 0, stepZ * i))) {
                        return null
                     }
                  }

                  return SwiftnessMovementLogic.Route(
                     CollectionsKt.listOf(SwiftnessMovementLogic.Step(start, target, SwiftnessMovementLogic.MoveType.JUMP, gapBlocks))
                  )
               }
            }
         } else {
            return SwiftnessMovementLogic.Route(
               CollectionsKt.listOf(SwiftnessMovementLogic.Step(start, target, SwiftnessMovementLogic.MoveType.WALK, 0, 8, null))
            )
         }
      }
   }

   public fun planTemporaryPlatformRouteWithIntermediates(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      intermediateNodes: Set<jooon.features.dojo.SwiftnessMovementLogic.Node>,
      isStandable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      return this.planRoute(start, target, { node: SwiftnessMovementLogic.Node ->
         `$allowedNodes`.contains(node) && `$isStandable`(node)
      }, isPassable, SwiftnessMovementLogic.PlanConfig(6, 0, 0, 0, 14, null))
   }

   private fun planStraightCardinalRoute(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      direction: jooon.features.dojo.SwiftnessMovementLogic.Direction,
      distance: Int,
      standable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      bodyClear: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      var steps: java.lang.Iterable = IntRange(0, distance) as java.lang.Iterable
      val maxLandingIndex: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(steps, 10))
      val landingIndex: java.util.Iterator = steps.iterator()

      while (landingIndex.hasNext()) {
         val landing: Int = (landingIndex as IntIterator).nextInt()
         maxLandingIndex.add(start.offset(direction.dx * landing, 0, direction.dz * landing))
      }

      val nodes: java.util.List = maxLandingIndex as java.util.List
      steps = ArrayList()
      var var27: Int = 0

      while (var27 < distance) {
         val var28: SwiftnessMovementLogic.Node = nodes.get(var27 + 1) as SwiftnessMovementLogic.Node
         if (standable(var28) as java.lang.Boolean) {
            steps.add(SwiftnessMovementLogic.Step(nodes.get(var27) as SwiftnessMovementLogic.Node, var28, SwiftnessMovementLogic.MoveType.WALK, 0, 8, null))
            var27++
         } else {
            val var29: Int = Math.min(distance, var27 + 3 + 1)
            var var30: Boolean = false
            var var31: Int = var29
            val var32: Int = var27 + 2
            if (var27 + 2 <= var29) {
               while (true) {
                  val var33: SwiftnessMovementLogic.Node = nodes.get(var31) as SwiftnessMovementLogic.Node
                  val var34: Int = var31 - var27 - 1
                  if (standable(var33) as java.lang.Boolean && 1 <= var31 - var27 - 1 && var31 - var27 - 1 < 4) {
                     val `$this$all$iv`: java.lang.Iterable = RangesKt.until(var27 + 1, var31) as java.lang.Iterable
                     var var10000: Boolean
                     if (`$this$all$iv` is java.util.Collection && (`$this$all$iv` as java.util.Collection).isEmpty()) {
                        var10000 = true
                     } else {
                        val var20: java.util.Iterator = `$this$all$iv`.iterator()

                        while (true) {
                           if (!var20.hasNext()) {
                              var10000 = true
                              break
                           }

                           val gapNode: SwiftnessMovementLogic.Node = nodes.get((var20 as IntIterator).nextInt()) as SwiftnessMovementLogic.Node
                           if (!bodyClear(gapNode) as java.lang.Boolean
                              || standable(gapNode) as java.lang.Boolean
                              || !isPassable(gapNode.offset(0, -1, 0)) as java.lang.Boolean) {
                              var10000 = false
                              break
                           }
                        }
                     }

                     if (var10000) {
                        steps.add(
                           SwiftnessMovementLogic.Step(nodes.get(var27) as SwiftnessMovementLogic.Node, var33, SwiftnessMovementLogic.MoveType.JUMP, var34)
                        )
                        var27 = var31
                        var30 = true
                        break
                     }
                  }

                  if (var31 == var32) {
                     break
                  }

                  var31--
               }
            }

            if (!var30) {
               return null
            }
         }
      }

      return SwiftnessMovementLogic.Route(steps as MutableList<SwiftnessMovementLogic.Step>)
   }

   public fun angledJumpRoute(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      isStandable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      if (angledJumpRoute$standable$9(isStandable, isPassable, start) && angledJumpRoute$standable$9(isStandable, isPassable, target)) {
         if (!(start == target) && start.y == target.y) {
            val dx: Int = target.x - start.x
            val dz: Int = target.z - start.z
            val absX: Int = Math.abs(dx)
            val absZ: Int = Math.abs(dz)
            if (absX != 0 && absZ != 0) {
               val horizontalDistance: Double = Math.sqrt((double)(dx * dx + dz * dz))
               val gapBlocks: Int = this.angledJumpGapBlocks(absX, absZ, horizontalDistance)
               if (1 > gapBlocks || gapBlocks >= 4) {
                  return null
               } else {
                  val unitX: Double = dx / horizontalDistance
                  val unitZ: Double = dz / horizontalDistance

                  // $VF: Unable to resugar Kotlin loop from Java for loop
                  var projection: Double = 0.45
                  while (true) {
                     if (projection < horizontalDistance - 0.45) break
                     val sample: SwiftnessMovementLogic.Node = SwiftnessMovementLogic.Node(
                        (int)Math.floor((double)start.x + 0.5 + unitX * projection), start.y, (int)Math.floor((double)start.z + 0.5 + unitZ * projection)
                     )
                     if (!(sample == start) && !(sample == target) && !angledJumpRoute$bodyClear$8(isPassable, sample)) {
                        return null
                     }

                     projection += 0.35
                  }

                  return SwiftnessMovementLogic.Route(
                     CollectionsKt.listOf(SwiftnessMovementLogic.Step(start, target, SwiftnessMovementLogic.MoveType.JUMP, gapBlocks))
                  )
               }
            } else {
               return null
            }
         } else {
            return null
         }
      } else {
         return null
      }
   }

   private fun angledJumpGapBlocks(absX: Int, absZ: Int, horizontalDistance: Double): Int {
      return if (Math.max(absX, absZ) == 4 && Math.min(absX, absZ) == 1) 3 else RangesKt.coerceAtLeast((int)Math.ceil(horizontalDistance) - 1, 1)
   }

   private fun planSupportedLWalk(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      stepX: Int,
      stepZ: Int,
      countX: Int,
      countZ: Int,
      standable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      var var10000: SwiftnessMovementLogic.Route = planSupportedLWalk$build(countX, countZ, start, target, standable, stepX, stepZ, true)
      if (var10000 == null) {
         var10000 = planSupportedLWalk$build(countX, countZ, start, target, standable, stepX, stepZ, false)
      }

      return var10000
   }

   public fun planRoute(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      isStandable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      config: jooon.features.dojo.SwiftnessMovementLogic.PlanConfig = SwiftnessMovementLogic.PlanConfig(0, 0, 0, 0, 15, null)
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:770)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:736)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc_w "start"
      // 004: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 007: aload 2
      // 008: ldc_w "target"
      // 00b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 00e: aload 3
      // 00f: ldc_w "isStandable"
      // 012: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 015: aload 4
      // 017: ldc_w "isPassable"
      // 01a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 01d: aload 5
      // 01f: ldc_w "config"
      // 022: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 025: aload 3
      // 026: aload 4
      // 028: aload 1
      // 029: invokestatic jooon/features/dojo/SwiftnessMovementLogic.planRoute$standable$15 (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)Z
      // 02c: ifeq 039
      // 02f: aload 3
      // 030: aload 4
      // 032: aload 2
      // 033: invokestatic jooon/features/dojo/SwiftnessMovementLogic.planRoute$standable$15 (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)Z
      // 036: ifne 03b
      // 039: aconst_null
      // 03a: areturn
      // 03b: aload 1
      // 03c: aload 2
      // 03d: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 040: ifeq 04e
      // 043: new jooon/features/dojo/SwiftnessMovementLogic$Route
      // 046: dup
      // 047: invokestatic kotlin/collections/CollectionsKt.emptyList ()Ljava/util/List;
      // 04a: invokespecial jooon/features/dojo/SwiftnessMovementLogic$Route.<init> (Ljava/util/List;)V
      // 04d: areturn
      // 04e: aload 1
      // 04f: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getX ()I
      // 052: aload 2
      // 053: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getX ()I
      // 056: invokestatic java/lang/Math.min (II)I
      // 059: aload 5
      // 05b: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getSearchMargin ()I
      // 05e: isub
      // 05f: istore 6
      // 061: aload 1
      // 062: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getX ()I
      // 065: aload 2
      // 066: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getX ()I
      // 069: invokestatic java/lang/Math.max (II)I
      // 06c: aload 5
      // 06e: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getSearchMargin ()I
      // 071: iadd
      // 072: istore 7
      // 074: aload 1
      // 075: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getY ()I
      // 078: aload 2
      // 079: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getY ()I
      // 07c: invokestatic java/lang/Math.min (II)I
      // 07f: aload 5
      // 081: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getYMargin ()I
      // 084: isub
      // 085: istore 8
      // 087: aload 1
      // 088: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getY ()I
      // 08b: aload 2
      // 08c: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getY ()I
      // 08f: invokestatic java/lang/Math.max (II)I
      // 092: aload 5
      // 094: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getYMargin ()I
      // 097: iadd
      // 098: istore 9
      // 09a: aload 1
      // 09b: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getZ ()I
      // 09e: aload 2
      // 09f: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getZ ()I
      // 0a2: invokestatic java/lang/Math.min (II)I
      // 0a5: aload 5
      // 0a7: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getSearchMargin ()I
      // 0aa: isub
      // 0ab: istore 10
      // 0ad: aload 1
      // 0ae: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getZ ()I
      // 0b1: aload 2
      // 0b2: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Node.getZ ()I
      // 0b5: invokestatic java/lang/Math.max (II)I
      // 0b8: aload 5
      // 0ba: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getSearchMargin ()I
      // 0bd: iadd
      // 0be: istore 11
      // 0c0: new java/util/PriorityQueue
      // 0c3: dup
      // 0c4: new jooon/features/dojo/SwiftnessMovementLogic$planRoute$$inlined$compareBy$1
      // 0c7: dup
      // 0c8: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planRoute$$inlined$compareBy$1.<init> ()V
      // 0cb: checkcast java/util/Comparator
      // 0ce: astore 13
      // 0d0: new jooon/features/dojo/SwiftnessMovementLogic$planRoute$$inlined$thenBy$1
      // 0d3: dup
      // 0d4: aload 13
      // 0d6: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planRoute$$inlined$thenBy$1.<init> (Ljava/util/Comparator;)V
      // 0d9: checkcast java/util/Comparator
      // 0dc: invokespecial java/util/PriorityQueue.<init> (Ljava/util/Comparator;)V
      // 0df: astore 12
      // 0e1: new java/util/HashMap
      // 0e4: dup
      // 0e5: invokespecial java/util/HashMap.<init> ()V
      // 0e8: astore 13
      // 0ea: new java/util/HashMap
      // 0ed: dup
      // 0ee: invokespecial java/util/HashMap.<init> ()V
      // 0f1: astore 14
      // 0f3: bipush 0
      // 0f4: istore 15
      // 0f6: aload 13
      // 0f8: checkcast java/util/Map
      // 0fb: aload 1
      // 0fc: bipush 0
      // 0fd: invokestatic java/lang/Integer.valueOf (I)Ljava/lang/Integer;
      // 100: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 105: pop
      // 106: aload 12
      // 108: new jooon/features/dojo/SwiftnessMovementLogic$SearchState
      // 10b: dup
      // 10c: aload 1
      // 10d: bipush 0
      // 10e: aload 0
      // 10f: aload 1
      // 110: aload 2
      // 111: invokespecial jooon/features/dojo/SwiftnessMovementLogic.heuristic (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)I
      // 114: invokespecial jooon/features/dojo/SwiftnessMovementLogic$SearchState.<init> (Ljooon/features/dojo/SwiftnessMovementLogic$Node;II)V
      // 117: invokevirtual java/util/PriorityQueue.add (Ljava/lang/Object;)Z
      // 11a: pop
      // 11b: aload 12
      // 11d: checkcast java/util/Collection
      // 120: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 125: ifne 12c
      // 128: bipush 1
      // 129: goto 12d
      // 12c: bipush 0
      // 12d: ifeq 26d
      // 130: iload 15
      // 132: aload 5
      // 134: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$PlanConfig.getMaxVisitedNodes ()I
      // 137: if_icmpge 26d
      // 13a: aload 12
      // 13c: invokevirtual java/util/PriorityQueue.poll ()Ljava/lang/Object;
      // 13f: checkcast jooon/features/dojo/SwiftnessMovementLogic$SearchState
      // 142: astore 16
      // 144: aload 13
      // 146: aload 16
      // 148: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$SearchState.getNode ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 14b: invokevirtual java/util/HashMap.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 14e: checkcast java/lang/Integer
      // 151: dup
      // 152: ifnull 15b
      // 155: invokevirtual java/lang/Integer.intValue ()I
      // 158: goto 15f
      // 15b: pop
      // 15c: goto 11b
      // 15f: istore 17
      // 161: aload 16
      // 163: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$SearchState.getG ()I
      // 166: iload 17
      // 168: if_icmpne 11b
      // 16b: iinc 15 1
      // 16e: aload 16
      // 170: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$SearchState.getNode ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 173: aload 2
      // 174: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 177: ifeq 186
      // 17a: aload 0
      // 17b: aload 1
      // 17c: aload 2
      // 17d: aload 14
      // 17f: checkcast java/util/Map
      // 182: invokespecial jooon/features/dojo/SwiftnessMovementLogic.rebuildRoute (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljava/util/Map;)Ljooon/features/dojo/SwiftnessMovementLogic$Route;
      // 185: areturn
      // 186: aload 0
      // 187: aload 16
      // 189: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$SearchState.getNode ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 18c: new jooon/features/dojo/SwiftnessMovementLogic$planRoute$1
      // 18f: dup
      // 190: iload 6
      // 192: iload 7
      // 194: iload 8
      // 196: iload 9
      // 198: iload 10
      // 19a: iload 11
      // 19c: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planRoute$1.<init> (IIIIII)V
      // 19f: checkcast kotlin/jvm/functions/Function1
      // 1a2: new jooon/features/dojo/SwiftnessMovementLogic$planRoute$2
      // 1a5: dup
      // 1a6: aload 3
      // 1a7: aload 4
      // 1a9: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planRoute$2.<init> (Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V
      // 1ac: checkcast kotlin/jvm/functions/Function1
      // 1af: aload 4
      // 1b1: new jooon/features/dojo/SwiftnessMovementLogic$planRoute$3
      // 1b4: dup
      // 1b5: aload 4
      // 1b7: invokespecial jooon/features/dojo/SwiftnessMovementLogic$planRoute$3.<init> (Lkotlin/jvm/functions/Function1;)V
      // 1ba: checkcast kotlin/jvm/functions/Function1
      // 1bd: aload 5
      // 1bf: invokespecial jooon/features/dojo/SwiftnessMovementLogic.neighbors (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Ljooon/features/dojo/SwiftnessMovementLogic$PlanConfig;)Ljava/util/List;
      // 1c2: invokeinterface java/util/List.iterator ()Ljava/util/Iterator; 1
      // 1c7: astore 18
      // 1c9: aload 18
      // 1cb: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 1d0: ifeq 11b
      // 1d3: aload 18
      // 1d5: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 1da: checkcast kotlin/Pair
      // 1dd: astore 19
      // 1df: aload 19
      // 1e1: invokevirtual kotlin/Pair.component1 ()Ljava/lang/Object;
      // 1e4: checkcast jooon/features/dojo/SwiftnessMovementLogic$Step
      // 1e7: astore 20
      // 1e9: aload 19
      // 1eb: invokevirtual kotlin/Pair.component2 ()Ljava/lang/Object;
      // 1ee: checkcast java/lang/Number
      // 1f1: invokevirtual java/lang/Number.intValue ()I
      // 1f4: istore 21
      // 1f6: aload 16
      // 1f8: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$SearchState.getG ()I
      // 1fb: iload 21
      // 1fd: iadd
      // 1fe: istore 22
      // 200: aload 13
      // 202: aload 20
      // 204: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Step.getTo ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 207: invokevirtual java/util/HashMap.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 20a: checkcast java/lang/Integer
      // 20d: astore 23
      // 20f: aload 23
      // 211: ifnull 21e
      // 214: iload 22
      // 216: aload 23
      // 218: invokevirtual java/lang/Integer.intValue ()I
      // 21b: if_icmpge 1c9
      // 21e: iload 22
      // 220: invokestatic java/lang/Integer.valueOf (I)Ljava/lang/Integer;
      // 223: astore 24
      // 225: aload 13
      // 227: checkcast java/util/Map
      // 22a: aload 20
      // 22c: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Step.getTo ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 22f: aload 24
      // 231: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 236: pop
      // 237: aload 14
      // 239: checkcast java/util/Map
      // 23c: aload 20
      // 23e: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Step.getTo ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 241: aload 20
      // 243: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 248: pop
      // 249: aload 12
      // 24b: new jooon/features/dojo/SwiftnessMovementLogic$SearchState
      // 24e: dup
      // 24f: aload 20
      // 251: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Step.getTo ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 254: iload 22
      // 256: iload 22
      // 258: aload 0
      // 259: aload 20
      // 25b: invokevirtual jooon/features/dojo/SwiftnessMovementLogic$Step.getTo ()Ljooon/features/dojo/SwiftnessMovementLogic$Node;
      // 25e: aload 2
      // 25f: invokespecial jooon/features/dojo/SwiftnessMovementLogic.heuristic (Ljooon/features/dojo/SwiftnessMovementLogic$Node;Ljooon/features/dojo/SwiftnessMovementLogic$Node;)I
      // 262: iadd
      // 263: invokespecial jooon/features/dojo/SwiftnessMovementLogic$SearchState.<init> (Ljooon/features/dojo/SwiftnessMovementLogic$Node;II)V
      // 266: invokevirtual java/util/PriorityQueue.add (Ljava/lang/Object;)Z
      // 269: pop
      // 26a: goto 1c9
      // 26d: aconst_null
      // 26e: areturn
   }

   private fun neighbors(
      node: jooon.features.dojo.SwiftnessMovementLogic.Node,
      inBounds: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      standable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      bodyClear: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      config: jooon.features.dojo.SwiftnessMovementLogic.PlanConfig
   ): List<Pair<jooon.features.dojo.SwiftnessMovementLogic.Step, Int>> {
      val result: ArrayList = ArrayList(16)

      for (direction in cardinalDirections) {
         this.addWalkEdges(node, direction, inBounds, standable, result)
         this.addJumpEdges(node, direction, inBounds, standable, isPassable, bodyClear, config, result)
      }

      for (var11 in diagonalDirections) {
         this.addDiagonalWalkEdges(node, var11, inBounds, standable, result)
      }

      return result
   }

   private fun addWalkEdges(
      node: jooon.features.dojo.SwiftnessMovementLogic.Node,
      direction: jooon.features.dojo.SwiftnessMovementLogic.Direction,
      inBounds: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      standable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      result: MutableList<Pair<jooon.features.dojo.SwiftnessMovementLogic.Step, Int>>
   ) {
      for (dy in -1..1) {
         val to: SwiftnessMovementLogic.Node = node.offset(direction.dx, dy, direction.dz)
         if (inBounds(to) as java.lang.Boolean && standable(to) as java.lang.Boolean) {
            result.add(TuplesKt.to(SwiftnessMovementLogic.Step(node, to, SwiftnessMovementLogic.MoveType.WALK, 0, 8, null), 10 + Math.abs(dy) * 3))
         }
      }
   }

   private fun addDiagonalWalkEdges(
      node: jooon.features.dojo.SwiftnessMovementLogic.Node,
      direction: jooon.features.dojo.SwiftnessMovementLogic.Direction,
      inBounds: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      standable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      result: MutableList<Pair<jooon.features.dojo.SwiftnessMovementLogic.Step, Int>>
   ) {
      for (dy in -1..1) {
         val to: SwiftnessMovementLogic.Node = node.offset(direction.dx, dy, direction.dz)
         if (inBounds(to) as java.lang.Boolean
            && standable(to) as java.lang.Boolean
            && standable(node.offset(direction.dx, dy, 0)) as java.lang.Boolean
            && standable(node.offset(0, dy, direction.dz)) as java.lang.Boolean) {
            result.add(TuplesKt.to(SwiftnessMovementLogic.Step(node, to, SwiftnessMovementLogic.MoveType.WALK, 0, 8, null), 14 + Math.abs(dy) * 4))
         }
      }
   }

   private fun addJumpEdges(
      node: jooon.features.dojo.SwiftnessMovementLogic.Node,
      direction: jooon.features.dojo.SwiftnessMovementLogic.Direction,
      inBounds: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      standable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      isPassable: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      bodyClear: (jooon.features.dojo.SwiftnessMovementLogic.Node) -> Boolean,
      config: jooon.features.dojo.SwiftnessMovementLogic.PlanConfig,
      result: MutableList<Pair<jooon.features.dojo.SwiftnessMovementLogic.Step, Int>>
   ) {
      var gapBlocks: Int = 1
      val var10: Int = RangesKt.coerceAtMost(config.maxGapBlocks, 3)
      if (1 <= var10) {
         while (true) {
            val landing: SwiftnessMovementLogic.Node = node.offset(direction.dx * (gapBlocks + 1), 0, direction.dz * (gapBlocks + 1))
            if (inBounds(landing) as java.lang.Boolean && standable(landing) as java.lang.Boolean) {
               var validGap: Boolean = true
               var i: Int = 1
               if (1 <= gapBlocks) {
                  while (true) {
                     val gapNode: SwiftnessMovementLogic.Node = node.offset(direction.dx * i, 0, direction.dz * i)
                     if (!bodyClear(gapNode) as java.lang.Boolean || standable(gapNode) as java.lang.Boolean) {
                        validGap = false
                        break
                     }

                     if (!isPassable(gapNode.offset(0, -1, 0)) as java.lang.Boolean) {
                        validGap = false
                        break
                     }

                     if (i == gapBlocks) {
                        break
                     }

                     i++
                  }
               }

               if (validGap) {
                  result.add(TuplesKt.to(SwiftnessMovementLogic.Step(node, landing, SwiftnessMovementLogic.MoveType.JUMP, gapBlocks), 32 + gapBlocks * 8))
               }
            }

            if (gapBlocks == var10) {
               break
            }

            gapBlocks++
         }
      }
   }

   private fun rebuildRoute(
      start: jooon.features.dojo.SwiftnessMovementLogic.Node,
      target: jooon.features.dojo.SwiftnessMovementLogic.Node,
      parents: Map<jooon.features.dojo.SwiftnessMovementLogic.Node, jooon.features.dojo.SwiftnessMovementLogic.Step>
   ): jooon.features.dojo.SwiftnessMovementLogic.Route? {
      val steps: ArrayList = ArrayList()
      var cursor: SwiftnessMovementLogic.Node = target

      while (!(cursor == start)) {
         val var10000: SwiftnessMovementLogic.Step = parents.get(cursor) as SwiftnessMovementLogic.Step
         if (var10000 == null) {
            return null
         }

         steps.add(var10000)
         cursor = var10000.from
      }

      CollectionsKt.reverse(steps)
      return SwiftnessMovementLogic.Route(steps)
   }

   private fun heuristic(a: jooon.features.dojo.SwiftnessMovementLogic.Node, b: jooon.features.dojo.SwiftnessMovementLogic.Node): Int {
      return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z)) * 10
   }

   private fun Int.signInt(): Int {
      return if (`$this$signInt` > 0) 1 else (if (`$this$signInt` < 0) -1 else 0)
   }

   @JvmStatic
   fun `planDirectRoute$bodyClear`(`$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean, node: SwiftnessMovementLogic.Node): Boolean {
      `$isPassable`(node) && `$isPassable`(node.above())
   }

   @JvmStatic
   fun `planDirectRoute$standable`(
      `$isStandable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      `$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      node: SwiftnessMovementLogic.Node
   ): Boolean {
      planDirectRoute$bodyClear(`$isPassable`, node) && `$isStandable`(node)
   }

   @JvmStatic
   fun `planTemporaryPlatformRoute$bodyClear$3`(`$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean, node: SwiftnessMovementLogic.Node): Boolean {
      `$isPassable`(node) && `$isPassable`(node.above())
   }

   @JvmStatic
   fun `planTemporaryPlatformRoute$endpointStandable`(
      `$isStandable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      `$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      node: SwiftnessMovementLogic.Node
   ): Boolean {
      planTemporaryPlatformRoute$bodyClear$3(`$isPassable`, node) && `$isStandable`(node)
   }

   @JvmStatic
   fun `angledJumpRoute$bodyClear$8`(`$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean, node: SwiftnessMovementLogic.Node): Boolean {
      `$isPassable`(node) && `$isPassable`(node.above())
   }

   @JvmStatic
   fun `angledJumpRoute$standable$9`(
      `$isStandable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      `$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      node: SwiftnessMovementLogic.Node
   ): Boolean {
      angledJumpRoute$bodyClear$8(`$isPassable`, node) && `$isStandable`(node)
   }

   @JvmStatic
   fun `planSupportedLWalk$build`(
      `$countX`: Int,
      `$countZ`: Int,
      `$start`: SwiftnessMovementLogic.Node,
      `$target`: SwiftnessMovementLogic.Node,
      `$standable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      `$stepX`: Int,
      `$stepZ`: Int,
      axisXFirst: Boolean
   ): SwiftnessMovementLogic.Route {
      val nodes: ArrayList = ArrayList(`$countX` + `$countZ`)
      var var14: Any = `$start`
      if (axisXFirst) {
         repeat(`$countX`) { steps ->
            var14 = var14.offset(`$stepX`, 0, 0)
            nodes.add(var14)
         }

         repeat(`$countZ`) { var15 ->
            var14 = var14.offset(0, 0, `$stepZ`)
            nodes.add(var14)
         }
      } else {
         repeat(`$countZ`) { var16 ->
            var14 = var14.offset(0, 0, `$stepZ`)
            nodes.add(var14)
         }

         repeat(`$countX`) { var17 ->
            var14 = var14.offset(`$stepX`, 0, 0)
            nodes.add(var14)
         }
      }

      if (CollectionsKt.lastOrNull(nodes) == `$target`) {
         var var18: java.lang.Iterable = nodes
         var var10000: Boolean
         if (nodes is java.util.Collection && (nodes as java.util.Collection).isEmpty()) {
            var10000 = true
         } else {
            val var24: java.util.Iterator = var18.iterator()

            while (true) {
               if (!var24.hasNext()) {
                  var10000 = true
                  break
               }

               if (!`$standable`(var24.next()) as java.lang.Boolean) {
                  var10000 = false
                  break
               }
            }
         }

         if (var10000) {
            var18 = ArrayList(nodes.size())
            var var20: SwiftnessMovementLogic.Node = `$start`
            val var27: java.util.Iterator = nodes.iterator()
            val var25: java.util.Iterator = var27

            while (var25.hasNext()) {
               val var28: Any = var25.next()
               val var26: SwiftnessMovementLogic.Node = var28 as SwiftnessMovementLogic.Node
               var18.add(SwiftnessMovementLogic.Step(var20, var28 as SwiftnessMovementLogic.Node, SwiftnessMovementLogic.MoveType.WALK, 0, 8, null))
               var20 = var26
            }

            SwiftnessMovementLogic.Route(var18 as MutableList<SwiftnessMovementLogic.Step>)
         }
      }

      null
   }

   @JvmStatic
   fun `planRoute$bodyClear$14`(`$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean, node: SwiftnessMovementLogic.Node): Boolean {
      `$isPassable`(node) && `$isPassable`(node.above())
   }

   @JvmStatic
   fun `planRoute$standable$15`(
      `$isStandable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      `$isPassable`: (SwiftnessMovementLogic.Node?) -> java.lang.Boolean,
      node: SwiftnessMovementLogic.Node
   ): Boolean {
      planRoute$bodyClear$14(`$isPassable`, node) && `$isStandable`(node)
   }

   @JvmStatic
   fun `planRoute$inBounds`(minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int, node: SwiftnessMovementLogic.Node): Boolean {
      var var7: Int = node.x
      if (minX <= var7 && var7 <= maxX) {
         var7 = node.y
         if (minY <= var7 && var7 <= maxY) {
            var7 = node.z
            if (minZ <= var7 && var7 <= maxZ) {
               true
            }
         }
      }

      false
   }

   public data class AirborneControl(forward: Boolean, back: Boolean, sprint: Boolean) {
      public final val forward: Boolean
      public final val back: Boolean
      public final val sprint: Boolean

      init {
         this.forward = forward
         this.back = back
         this.sprint = sprint
      }

      public operator fun component1(): Boolean {
         return this.forward
      }

      public operator fun component2(): Boolean {
         return this.back
      }

      public operator fun component3(): Boolean {
         return this.sprint
      }

      public fun copy(forward: Boolean = this.forward, back: Boolean = this.back, sprint: Boolean = this.sprint): jooon.features.dojo.SwiftnessMovementLogic.AirborneControl {
         return SwiftnessMovementLogic.AirborneControl(forward, back, sprint)
      }

      public override fun toString(): String {
         return "AirborneControl(forward=${this.forward}, back=${this.back}, sprint=${this.sprint})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Boolean.hashCode(this.forward) * 31 + java.lang.Boolean.hashCode(this.back)) * 31 + java.lang.Boolean.hashCode(this.sprint)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.AirborneControl
               && this.forward == (other as SwiftnessMovementLogic.AirborneControl).forward
               && this.back == (other as SwiftnessMovementLogic.AirborneControl).back
               && this.sprint == (other as SwiftnessMovementLogic.AirborneControl).sprint
            }
      }
   }

   public data class Direction(dx: Int, dz: Int) {
      public final val dx: Int
      public final val dz: Int

      init {
         this.dx = dx
         this.dz = dz
      }

      public final val cardinal: Boolean
         public final get() {
            return this.dx == 0 || this.dz == 0
         }


      public operator fun component1(): Int {
         return this.dx
      }

      public operator fun component2(): Int {
         return this.dz
      }

      public fun copy(dx: Int = this.dx, dz: Int = this.dz): jooon.features.dojo.SwiftnessMovementLogic.Direction {
         return SwiftnessMovementLogic.Direction(dx, dz)
      }

      public override fun toString(): String {
         return "Direction(dx=${this.dx}, dz=${this.dz})"
      }

      public override fun hashCode(): Int {
         return Integer.hashCode(this.dx) * 31 + Integer.hashCode(this.dz)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.Direction
               && this.dx == (other as SwiftnessMovementLogic.Direction).dx
               && this.dz == (other as SwiftnessMovementLogic.Direction).dz
            }
      }
   }

   public data class JumpMetrics(projection: Double, laneError: Double, yawError: Double, onGround: Boolean, elapsedTicks: Int = 0) {
      public final val projection: Double
      public final val laneError: Double
      public final val yawError: Double
      public final val onGround: Boolean
      public final val elapsedTicks: Int

      init {
         this.projection = projection
         this.laneError = laneError
         this.yawError = yawError
         this.onGround = onGround
         this.elapsedTicks = elapsedTicks
      }

      public operator fun component1(): Double {
         return this.projection
      }

      public operator fun component2(): Double {
         return this.laneError
      }

      public operator fun component3(): Double {
         return this.yawError
      }

      public operator fun component4(): Boolean {
         return this.onGround
      }

      public operator fun component5(): Int {
         return this.elapsedTicks
      }

      public fun copy(
         projection: Double = this.projection,
         laneError: Double = this.laneError,
         yawError: Double = this.yawError,
         onGround: Boolean = this.onGround,
         elapsedTicks: Int = this.elapsedTicks
      ): jooon.features.dojo.SwiftnessMovementLogic.JumpMetrics {
         return SwiftnessMovementLogic.JumpMetrics(projection, laneError, yawError, onGround, elapsedTicks)
      }

      public override fun toString(): String {
         return "JumpMetrics(projection=${this.projection}, laneError=${this.laneError}, yawError=${this.yawError}, onGround=${this.onGround}, elapsedTicks=${this.elapsedTicks})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (java.lang.Double.hashCode(this.projection) * 31 + java.lang.Double.hashCode(this.laneError)) * 31
                              + java.lang.Double.hashCode(this.yawError)
                        )
                        * 31
                     + java.lang.Boolean.hashCode(this.onGround)
               )
               * 31
            + Integer.hashCode(this.elapsedTicks)
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.JumpMetrics
               && java.lang.Double.compare(this.projection, (other as SwiftnessMovementLogic.JumpMetrics).projection) == 0
               && java.lang.Double.compare(this.laneError, (other as SwiftnessMovementLogic.JumpMetrics).laneError) == 0
               && java.lang.Double.compare(this.yawError, (other as SwiftnessMovementLogic.JumpMetrics).yawError) == 0
               && this.onGround == (other as SwiftnessMovementLogic.JumpMetrics).onGround
               && this.elapsedTicks == (other as SwiftnessMovementLogic.JumpMetrics).elapsedTicks
            }
      }
   }

   public enum class JumpPhase {
      IDLE,
      ALIGN,
      BACKSTEP,
      RUNUP,
      AIRBORNE,
      CAPTURE;

      @JvmStatic
      fun getEntries(): EnumEntries<SwiftnessMovementLogic.JumpPhase> {
         $ENTRIES
      }
   }

   public data class JumpTuning(gapBlocks: Int,
      backstepProjection: Double,
      jumpTriggerProjection: Double,
      jumpHoldTicks: Int,
      requiresBackstep: Boolean,
      runupSprint: Boolean,
      airborneSprint: Boolean,
      forwardReleaseProjection: Double,
      brakeProjection: Double,
      overrunProjection: Double,
      laneErrorLimit: Double
   ) {
      public final val gapBlocks: Int
      public final val backstepProjection: Double
      public final val jumpTriggerProjection: Double
      public final val jumpHoldTicks: Int
      public final val requiresBackstep: Boolean
      public final val runupSprint: Boolean
      public final val airborneSprint: Boolean
      public final val forwardReleaseProjection: Double
      public final val brakeProjection: Double
      public final val overrunProjection: Double
      public final val laneErrorLimit: Double

      init {
         this.gapBlocks = gapBlocks
         this.backstepProjection = backstepProjection
         this.jumpTriggerProjection = jumpTriggerProjection
         this.jumpHoldTicks = jumpHoldTicks
         this.requiresBackstep = requiresBackstep
         this.runupSprint = runupSprint
         this.airborneSprint = airborneSprint
         this.forwardReleaseProjection = forwardReleaseProjection
         this.brakeProjection = brakeProjection
         this.overrunProjection = overrunProjection
         this.laneErrorLimit = laneErrorLimit
      }

      public operator fun component1(): Int {
         return this.gapBlocks
      }

      public operator fun component2(): Double {
         return this.backstepProjection
      }

      public operator fun component3(): Double {
         return this.jumpTriggerProjection
      }

      public operator fun component4(): Int {
         return this.jumpHoldTicks
      }

      public operator fun component5(): Boolean {
         return this.requiresBackstep
      }

      public operator fun component6(): Boolean {
         return this.runupSprint
      }

      public operator fun component7(): Boolean {
         return this.airborneSprint
      }

      public operator fun component8(): Double {
         return this.forwardReleaseProjection
      }

      public operator fun component9(): Double {
         return this.brakeProjection
      }

      public operator fun component10(): Double {
         return this.overrunProjection
      }

      public operator fun component11(): Double {
         return this.laneErrorLimit
      }

      public fun copy(
         gapBlocks: Int = this.gapBlocks,
         backstepProjection: Double = this.backstepProjection,
         jumpTriggerProjection: Double = this.jumpTriggerProjection,
         jumpHoldTicks: Int = this.jumpHoldTicks,
         requiresBackstep: Boolean = this.requiresBackstep,
         runupSprint: Boolean = this.runupSprint,
         airborneSprint: Boolean = this.airborneSprint,
         forwardReleaseProjection: Double = this.forwardReleaseProjection,
         brakeProjection: Double = this.brakeProjection,
         overrunProjection: Double = this.overrunProjection,
         laneErrorLimit: Double = this.laneErrorLimit
      ): jooon.features.dojo.SwiftnessMovementLogic.JumpTuning {
         return SwiftnessMovementLogic.JumpTuning(
            gapBlocks,
            backstepProjection,
            jumpTriggerProjection,
            jumpHoldTicks,
            requiresBackstep,
            runupSprint,
            airborneSprint,
            forwardReleaseProjection,
            brakeProjection,
            overrunProjection,
            laneErrorLimit
         )
      }

      public override fun toString(): String {
         return "JumpTuning(gapBlocks=${this.gapBlocks}, backstepProjection=${this.backstepProjection}, jumpTriggerProjection=${this.jumpTriggerProjection}, jumpHoldTicks=${this.jumpHoldTicks}, requiresBackstep=${this.requiresBackstep}, runupSprint=${this.runupSprint}, airborneSprint=${this.airborneSprint}, forwardReleaseProjection=${this.forwardReleaseProjection}, brakeProjection=${this.brakeProjection}, overrunProjection=${this.overrunProjection}, laneErrorLimit=${this.laneErrorLimit})"
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
                                                                                          Integer.hashCode(this.gapBlocks) * 31
                                                                                             + java.lang.Double.hashCode(this.backstepProjection)
                                                                                       )
                                                                                       * 31
                                                                                    + java.lang.Double.hashCode(this.jumpTriggerProjection)
                                                                              )
                                                                              * 31
                                                                           + Integer.hashCode(this.jumpHoldTicks)
                                                                     )
                                                                     * 31
                                                                  + java.lang.Boolean.hashCode(this.requiresBackstep)
                                                            )
                                                            * 31
                                                         + java.lang.Boolean.hashCode(this.runupSprint)
                                                   )
                                                   * 31
                                                + java.lang.Boolean.hashCode(this.airborneSprint)
                                          )
                                          * 31
                                       + java.lang.Double.hashCode(this.forwardReleaseProjection)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.brakeProjection)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.overrunProjection)
               )
               * 31
            + java.lang.Double.hashCode(this.laneErrorLimit)
         }

      public override operator fun equals(other: Any?): Boolean {
         label82@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.JumpTuning
               && this.gapBlocks == (other as SwiftnessMovementLogic.JumpTuning).gapBlocks
               && java.lang.Double.compare(this.backstepProjection, (other as SwiftnessMovementLogic.JumpTuning).backstepProjection) == 0
               && java.lang.Double.compare(this.jumpTriggerProjection, (other as SwiftnessMovementLogic.JumpTuning).jumpTriggerProjection) == 0
               && this.jumpHoldTicks == (other as SwiftnessMovementLogic.JumpTuning).jumpHoldTicks
               && this.requiresBackstep == (other as SwiftnessMovementLogic.JumpTuning).requiresBackstep
               && this.runupSprint == (other as SwiftnessMovementLogic.JumpTuning).runupSprint
               && this.airborneSprint == (other as SwiftnessMovementLogic.JumpTuning).airborneSprint
               && java.lang.Double.compare(this.forwardReleaseProjection, (other as SwiftnessMovementLogic.JumpTuning).forwardReleaseProjection) == 0
               && java.lang.Double.compare(this.brakeProjection, (other as SwiftnessMovementLogic.JumpTuning).brakeProjection) == 0
               && java.lang.Double.compare(this.overrunProjection, (other as SwiftnessMovementLogic.JumpTuning).overrunProjection) == 0
               && java.lang.Double.compare(this.laneErrorLimit, (other as SwiftnessMovementLogic.JumpTuning).laneErrorLimit) == 0
            }
      }
   }

   public enum class MoveType {
      WALK,
      JUMP;

      @JvmStatic
      fun getEntries(): EnumEntries<SwiftnessMovementLogic.MoveType> {
         $ENTRIES
      }
   }

   public data class Node(x: Int, y: Int, z: Int) {
      public final val x: Int
      public final val y: Int
      public final val z: Int

      init {
         this.x = x
         this.y = y
         this.z = z
      }

      public fun offset(dx: Int, dy: Int, dz: Int): jooon.features.dojo.SwiftnessMovementLogic.Node {
         return SwiftnessMovementLogic.Node(this.x + dx, this.y + dy, this.z + dz)
      }

      public fun above(): jooon.features.dojo.SwiftnessMovementLogic.Node {
         return this.offset(0, 1, 0)
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.y
      }

      public operator fun component3(): Int {
         return this.z
      }

      public fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z): jooon.features.dojo.SwiftnessMovementLogic.Node {
         return SwiftnessMovementLogic.Node(x, y, z)
      }

      public override fun toString(): String {
         return "Node(x=${this.x}, y=${this.y}, z=${this.z})"
      }

      public override fun hashCode(): Int {
         return (Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.z)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.Node
               && this.x == (other as SwiftnessMovementLogic.Node).x
               && this.y == (other as SwiftnessMovementLogic.Node).y
               && this.z == (other as SwiftnessMovementLogic.Node).z
            }
      }
   }

   public data class PlanConfig(searchMargin: Int = 12, yMargin: Int = 2, maxVisitedNodes: Int = 12000, maxGapBlocks: Int = 3) {
      public final val searchMargin: Int
      public final val yMargin: Int
      public final val maxVisitedNodes: Int
      public final val maxGapBlocks: Int

      init {
         this.searchMargin = searchMargin
         this.yMargin = yMargin
         this.maxVisitedNodes = maxVisitedNodes
         this.maxGapBlocks = maxGapBlocks
      }

      public operator fun component1(): Int {
         return this.searchMargin
      }

      public operator fun component2(): Int {
         return this.yMargin
      }

      public operator fun component3(): Int {
         return this.maxVisitedNodes
      }

      public operator fun component4(): Int {
         return this.maxGapBlocks
      }

      public fun copy(
         searchMargin: Int = this.searchMargin,
         yMargin: Int = this.yMargin,
         maxVisitedNodes: Int = this.maxVisitedNodes,
         maxGapBlocks: Int = this.maxGapBlocks
      ): jooon.features.dojo.SwiftnessMovementLogic.PlanConfig {
         return SwiftnessMovementLogic.PlanConfig(searchMargin, yMargin, maxVisitedNodes, maxGapBlocks)
      }

      public override fun toString(): String {
         return "PlanConfig(searchMargin=${this.searchMargin}, yMargin=${this.yMargin}, maxVisitedNodes=${this.maxVisitedNodes}, maxGapBlocks=${this.maxGapBlocks})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.searchMargin) * 31 + Integer.hashCode(this.yMargin)) * 31 + Integer.hashCode(this.maxVisitedNodes)) * 31
            + Integer.hashCode(this.maxGapBlocks)
         }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.PlanConfig
               && this.searchMargin == (other as SwiftnessMovementLogic.PlanConfig).searchMargin
               && this.yMargin == (other as SwiftnessMovementLogic.PlanConfig).yMargin
               && this.maxVisitedNodes == (other as SwiftnessMovementLogic.PlanConfig).maxVisitedNodes
               && this.maxGapBlocks == (other as SwiftnessMovementLogic.PlanConfig).maxGapBlocks
            }
      }

      fun PlanConfig() {
         this(0, 0, 0, 0, 15, null)
      }
   }

   @SourceDebugExtension(["SMAP\nSwiftnessMovementLogic.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SwiftnessMovementLogic.kt\njooon/features/dojo/SwiftnessMovementLogic$Route\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,917:1\n1549#2:918\n1620#2,3:919\n*S KotlinDebug\n*F\n+ 1 SwiftnessMovementLogic.kt\njooon/features/dojo/SwiftnessMovementLogic$Route\n*L\n56#1:918\n56#1:919,3\n*E\n"])
   public data class Route(steps: List<jooon.features.dojo.SwiftnessMovementLogic.Step>) {
      public final val steps: List<jooon.features.dojo.SwiftnessMovementLogic.Step>

      init {
         this.steps = steps
      }

      public final val nodes: List<jooon.features.dojo.SwiftnessMovementLogic.Node>
         public final get() {
            val var10000: java.util.List
            if (this.steps.isEmpty()) {
               var10000 = CollectionsKt.emptyList()
            } else {
               val var12: java.util.Collection = CollectionsKt.listOf((CollectionsKt.first(this.steps) as SwiftnessMovementLogic.Step).from)
               val `$this$map$iv`: java.lang.Iterable = this.steps
               val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(this.steps, 10))

               for (`item$iv$iv` in `$this$map$iv`) {
                  `destination$iv$iv`.add((`item$iv$iv` as SwiftnessMovementLogic.Step).to)
               }

               var10000 = CollectionsKt.plus(var12, `destination$iv$iv` as java.util.List)
            }

            return var10000
         }


      public operator fun component1(): List<jooon.features.dojo.SwiftnessMovementLogic.Step> {
         return this.steps
      }

      public fun copy(steps: List<jooon.features.dojo.SwiftnessMovementLogic.Step> = this.steps): jooon.features.dojo.SwiftnessMovementLogic.Route {
         return SwiftnessMovementLogic.Route(steps)
      }

      public override fun toString(): String {
         return "Route(steps=${this.steps})"
      }

      public override fun hashCode(): Int {
         return this.steps.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label22@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.Route && this.steps == (other as SwiftnessMovementLogic.Route).steps
         }
      }
   }

   private data class SearchState(node: jooon.features.dojo.SwiftnessMovementLogic.Node, g: Int, f: Int) {
      public final val node: jooon.features.dojo.SwiftnessMovementLogic.Node
      public final val g: Int
      public final val f: Int

      init {
         this.node = node
         this.g = g
         this.f = f
      }

      public operator fun component1(): jooon.features.dojo.SwiftnessMovementLogic.Node {
         return this.node
      }

      public operator fun component2(): Int {
         return this.g
      }

      public operator fun component3(): Int {
         return this.f
      }

      public fun copy(node: jooon.features.dojo.SwiftnessMovementLogic.Node = this.node, g: Int = this.g, f: Int = this.f): jooon.features.dojo.SwiftnessMovementLogic.SearchState {
         return SwiftnessMovementLogic.SearchState(node, g, f)
      }

      public override fun toString(): String {
         return "SearchState(node=${this.node}, g=${this.g}, f=${this.f})"
      }

      public override fun hashCode(): Int {
         return (this.node.hashCode() * 31 + Integer.hashCode(this.g)) * 31 + Integer.hashCode(this.f)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.SearchState
               && this.node == (other as SwiftnessMovementLogic.SearchState).node
               && this.g == (other as SwiftnessMovementLogic.SearchState).g
               && this.f == (other as SwiftnessMovementLogic.SearchState).f
            }
      }
   }

   public data class Step(from: jooon.features.dojo.SwiftnessMovementLogic.Node,
      to: jooon.features.dojo.SwiftnessMovementLogic.Node,
      moveType: jooon.features.dojo.SwiftnessMovementLogic.MoveType,
      gapBlocks: Int = 0
   ) {
      public final val from: jooon.features.dojo.SwiftnessMovementLogic.Node
      public final val to: jooon.features.dojo.SwiftnessMovementLogic.Node
      public final val moveType: jooon.features.dojo.SwiftnessMovementLogic.MoveType
      public final val gapBlocks: Int

      init {
         this.from = from
         this.to = to
         this.moveType = moveType
         this.gapBlocks = gapBlocks
      }

      public operator fun component1(): jooon.features.dojo.SwiftnessMovementLogic.Node {
         return this.from
      }

      public operator fun component2(): jooon.features.dojo.SwiftnessMovementLogic.Node {
         return this.to
      }

      public operator fun component3(): jooon.features.dojo.SwiftnessMovementLogic.MoveType {
         return this.moveType
      }

      public operator fun component4(): Int {
         return this.gapBlocks
      }

      public fun copy(
         from: jooon.features.dojo.SwiftnessMovementLogic.Node = this.from,
         to: jooon.features.dojo.SwiftnessMovementLogic.Node = this.to,
         moveType: jooon.features.dojo.SwiftnessMovementLogic.MoveType = this.moveType,
         gapBlocks: Int = this.gapBlocks
      ): jooon.features.dojo.SwiftnessMovementLogic.Step {
         return SwiftnessMovementLogic.Step(from, to, moveType, gapBlocks)
      }

      public override fun toString(): String {
         return "Step(from=${this.from}, to=${this.to}, moveType=${this.moveType}, gapBlocks=${this.gapBlocks})"
      }

      public override fun hashCode(): Int {
         return ((this.from.hashCode() * 31 + this.to.hashCode()) * 31 + this.moveType.hashCode()) * 31 + Integer.hashCode(this.gapBlocks)
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is SwiftnessMovementLogic.Step
               && this.from == (other as SwiftnessMovementLogic.Step).from
               && this.to == (other as SwiftnessMovementLogic.Step).to
               && this.moveType === (other as SwiftnessMovementLogic.Step).moveType
               && this.gapBlocks == (other as SwiftnessMovementLogic.Step).gapBlocks
            }
      }
   }
}
