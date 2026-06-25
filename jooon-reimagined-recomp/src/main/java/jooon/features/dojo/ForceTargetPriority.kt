package jooon.features.dojo

import java.util.ArrayList
import java.util.Arrays
import java.util.Locale
import kotlin.enums.EnumEntries
internal object ForceTargetPriority {
   public const val ZOMBIE_LIFE_TIME_MS: Long = 10100L
   public const val ABANDON_AFTER_TICKS: Int = 24
   public const val ABANDON_AFTER_HITS: Int = 4
   public const val FINISH_EDGE_DISTANCE: Double = 1.35
   public const val COMMIT_EDGE_DISTANCE: Double = 2.25
   private const val MIN_MEANINGFUL_OUTWARD_PROGRESS: Double = 0.34
   private const val LANE_READY_ALIGNMENT: Double = 0.62
   private const val SAFE_NEGATIVE_DISTANCE: Double = 1.65
   private const val DEFAULT_EDGE_DISTANCE: Double = 7.5
   private const val DEFAULT_NEGATIVE_DISTANCE: Double = 6.0
   private const val RECENT_PROGRESS_TICKS: Int = 16
   private const val BAD_HIT_PROGRESS_EPS: Double = 0.18
   private const val STALE_EDGE_REBOUND_TOLERANCE: Double = 0.85
   private const val NO_CONTACT_CHASE_TICKS: Int = 22
   private val pointRegex: Regex = Regex("[-+]?\\d+")

   fun select(
      candidates: List<jooon.features.dojo.ForceTargetPriority.Candidate>,
      nowMs: Long,
      preventNegative: Boolean,
      currentTargetId: Int?,
      sprintResetLocked: Boolean = false
   ): jooon.features.dojo.ForceTargetPriority.Decision {
      val selectable: java.util.List = toList(
         filterNot(filter(asSequence(candidates), { it: ForceTargetPriority.Candidate ->
            isSelectable(it, `$preventNegative`)
         }), { it: ForceTargetPriority.Candidate ->
            it.abandoned
         })
      )
      if (selectable.isEmpty()) {
         return ForceTargetPriority.Decision(null, ForceTargetPriority.BrainState.SCAN, null, "no_selectable_positive")
      } else {
         val `this$iv`: java.util.Iterator = selectable.iterator()

         var var50: ForceTargetPriority.Candidate
         while (true) {
            if (!`this$iv`.hasNext()) {
               var50 = null
break
            }

            if (currentTargetId != null && (it as ForceTargetPriority.Candidate).id == currentTargetId) {
               var50 = (ForceTargetPriority.Candidate)it
break
            }
         }

         val current: ForceTargetPriority.Candidate = var50
         val var38: java.lang.Iterable = selectable
         val var40: java.util.Collection = ArrayList()

         for (`element$iv$iv` in var38) {
            if (isFinishNow(`element$iv$iv` as ForceTargetPriority.Candidate)) {
               var40.add(`element$iv$iv`)
            }
         }

         val var28: ForceTargetPriority.Candidate = minWithOrNull(
            var40 as java.util.List,
            ForceTargetPriority$select$$inlined$thenBy$2(
               ForceTargetPriority$select$$inlined$thenBy$1(
                  ForceTargetPriority$select$$inlined$thenByDescending$1(ForceTargetPriority$select$$inlined$compareBy$1())
               )
            )
         ) as ForceTargetPriority.Candidate
         if (var28 == null) {
            if (sprintResetLocked && current != null && !this.shouldAbandon(current)) {
               return ForceTargetPriority.Decision(
                  current, ForceTargetPriority.BrainState.RESET_SPRINT, ForceTargetPriority.TargetBucket.COMMITTED, "retain sprint_reset bucket=COMMITTED"
               )
            } else {
               run label127@{
                  if (current != null) {
                     if (this.shouldAbandon(current)) {
                        val var37: java.lang.Iterable = selectable
                        val `destination$iv$ivx`: java.util.Collection = ArrayList()

                        for (var47 in var37) {
                           if ((var47 as ForceTargetPriority.Candidate).id != current.id) {
                              `destination$iv$ivx`.add(var47)
                           }
                        }

                        val var35: ForceTargetPriority.Candidate = this.nextBest(`destination$iv$ivx` as MutableList<ForceTargetPriority.Candidate>, nowMs)
                        var50 = var35
                        var var10001: ForceTargetPriority.BrainState = ForceTargetPriority.BrainState.ABANDON
                        val var10002: ForceTargetPriority.TargetBucket
                        if (var35 != null) {
                           var50 = var35
                           var10001 = ForceTargetPriority.BrainState.ABANDON
                           var10002 = ForceTargetPriority.TargetBucket.NEXT_BEST
                        } else {
                           var10002 = null
                        }

                        return ForceTargetPriority.Decision(
                           var50,
                           var10001,
                           var10002,
                           "abandon id=${current.id} hits=${current.hitCount} bad=${current.consecutiveBadHits} bestEdge=${this.format(current.bestEdgeDistance)} progress=${this.format(
                              current.outwardProgress
                           )}"
                        )
                     }

                     if (this.shouldCommit(current)) {
                        return ForceTargetPriority.Decision(
                           current,
                           this.stateFor(current),
                           ForceTargetPriority.TargetBucket.COMMITTED,
                           "commit retain edge=${this.format(current.edgeDistance)} bestEdge=${this.format(current.bestEdgeDistance)} velocity=${this.format(
                              current.edgeVelocity
                           )} bad=${current.consecutiveBadHits}"
                        )
                     }
                  }

                  var50 = this.nextBest(selectable, nowMs)
                  return if (var50 == null)
                     ForceTargetPriority.Decision(null, ForceTargetPriority.BrainState.SCAN, null, "no_next_best")
return else
                     ForceTargetPriority.Decision(
                        var50,
                        this.stateFor(var50),
                        ForceTargetPriority.TargetBucket.NEXT_BEST,
                        "next_best score=${this.format(this.nextBestScore(var50, nowMs))} edge=${this.format(var50.edgeDistance)} value=${var50.pointValue}"
                     )
                  }
            }
         } else {
            return ForceTargetPriority.Decision(
               var28,
               this.stateFor(var28),
               ForceTargetPriority.TargetBucket.FINISH_NOW,
               "${if (currentTargetId != null && var28.id == currentTargetId) "finish_current" else "finish_interrupt"} edge=${this.format(var28.edgeDistance)} value=${var28.pointValue}"
            )
         }
      }
   }

   fun pointValueFromName(name: String): Int? {

      if (var10000 != null) {

         if (var3 != null) {
            return toIntOrNull(var3)
         }
      }

      return null
   }

   fun pointValueFromNameOrHelmet(name: String, helmetKey: String?): Int {

      return var10000 ?: this.pointValueFromHelmetKey(helmetKey)
   }

   fun pointValueFromHelmetKey(helmetKey: String?): Int {
      if (helmetKey != null) {

         if (var10000 != null) {

            if (var4 != null) {

               if (var5 != null) {
                  when (var5.hashCode()) {
                     -336627272 -> {
                        if (var5.equals("diamond_helmet")) {
                           return 30
                        }
                     }
                     72679523 -> {
                        if (var5.equals("golden_helmet")) {
                           return 20
                        }
                     }
                     530127812 -> {
                        if (var5.equals("iron_helmet")) {
                           return 10
                        }
                     }
                     1297765676 -> {
                        if (var5.equals("gold_helmet")) {
                           return 20
                        }
                     }
                     else -> {}
                  }

                  return -30
               }
            }
         }
      }

      return -30
   }

   fun isNegativeName(name: String): Boolean {
      return contains$default(name, "-", false, 2, null)
   }

   fun isSwingEligible(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      return !candidate.negative
         && candidate.pointValue > 0
         && candidate.negativeRisk === ForceTargetPriority.NegativeRisk.NONE
         && candidate.laneUsable
         && candidate.laneAlignment >= 0.62
      }

   fun isFinishNow(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      return this.isSwingEligible(candidate)
         && this.normalizedEdgeDistance(candidate.edgeDistance) <= 1.35
         && (candidate.attackReachable || candidate.oneStepReachable)
      }

   fun shouldCommit(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      return !candidate.abandoned
         && !candidate.negative
         && candidate.pointValue > 0
         && (
            this.isFinishNow(candidate)
               || this.hasLiveEdgeIntent(candidate)
               || !this.isStaleEdgeMemory(candidate)
                  && !this.isNoContactChaseStall(candidate)
                  && (candidate.edgeVelocity > 0.18 || candidate.ticksSinceProgress <= 16 || candidate.hitCount > 0 && candidate.outwardProgress >= 0.34)
         )
      }

   fun shouldAbandon(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      if (candidate.abandoned) {
         return true
      } else if (candidate.pointValue <= 0 || candidate.negative) {
         return true
      } else if (this.hasLiveEdgeIntent(candidate)) {
         return false
      } else {

         return this.isStaleEdgeMemory(candidate) && noRecentProgress
            || this.isNoContactChaseStall(candidate)
            || candidate.consecutiveBadHits >= 4 && noRecentProgress && candidate.outwardProgress < 0.34
            || candidate.hitCount > 0 && candidate.ticksOnTarget >= 24 && noRecentProgress && candidate.outwardProgress < 0.34
            || candidate.ticksOnTarget >= 12 && candidate.hitCount > 0 && candidate.laneAlignment < 0.0 && noRecentProgress
         }
   }

   fun shouldReposition(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      return candidate.negativeRisk != ForceTargetPriority.NegativeRisk.NONE || !candidate.laneUsable || candidate.laneAlignment < 0.62
   }

   fun bucketFor(candidate: jooon.features.dojo.ForceTargetPriority.Candidate, currentTargetId: Int?): jooon.features.dojo.ForceTargetPriority.TargetBucket {
      if (this.isFinishNow(candidate)) {
         return ForceTargetPriority.TargetBucket.FINISH_NOW
      } else {
         return if (currentTargetId != null && candidate.id == currentTargetId && this.shouldCommit(candidate))
            ForceTargetPriority.TargetBucket.COMMITTED
return else
            ForceTargetPriority.TargetBucket.NEXT_BEST
         }
   }

   fun scoreIntent(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): String {
      return if (this.isFinishNow(candidate))
         "finish"
return else
         (
            if (this.isStaleEdgeMemory(candidate))
               "stale_edge"
return else
               (
                  if (this.hasLiveEdgeIntent(candidate))
                     "commit_edge"
return else
                     (
                        if (candidate.ticksSinceProgress <= 16)
                           "commit_progress"
return else
                           (
                              if (candidate.consecutiveBadHits >= 4)
                                 "stalled"
return else
                                 (
                                    if (this.isNoContactChaseStall(candidate))
                                       "no_contact"
return else
                                       (if (this.shouldReposition(candidate)) "reposition" else "setup")
                                 )
                           )
                     )
               )
         )
      }

   fun roiScore(candidate: jooon.features.dojo.ForceTargetPriority.Candidate, nowMs: Long): Double {
      return this.nextBestScore(candidate, nowMs)
   }

   fun nextBestScore(candidate: jooon.features.dojo.ForceTargetPriority.Candidate, nowMs: Long): Double {
      if (!candidate.negative && candidate.pointValue > 0 && !candidate.abandoned) {



         return candidate.pointValue / (this.estimatedTicksToScore(candidate)).coerceAtLeast(1.0)
            + (if (edgeDistance <= 2.25) 2.2 else 0.0)
            + (negativeDistance - 1.65).coerceIn(-1.65, 3.0) * 0.45
            + (if (candidate.laneUsable) (candidate.laneAlignment).coerceIn(-1.0, 1.0) else -1.0)
            + (if (remainingMs < 1200L) -1.5 else (if (remainingMs < 2600L) 0.6 else 0.0))
            + (candidate.edgeVelocity).coerceIn(-1.0, 1.0) * 1.5
            - candidate.consecutiveBadHits * 0.55
         } else {
         return -10000.0
      }
   }

   fun estimatedTicksToScore(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Double {


      return distance / 0.18
         + edgeDistance / 0.3
         + (if (candidate.attackReachable && edgeDistance <= 1.35) 1.0 else (if (candidate.hitCount > 0) 2.0 else 4.0))
      }

   fun negativeConeRisk(
      player: jooon.features.dojo.ForceTargetPriority.Point3,
      target: jooon.features.dojo.ForceTargetPriority.Point3,
      negatives: List<jooon.features.dojo.ForceTargetPriority.Point3>,
      coneRadius: Double = 0.82
   ): jooon.features.dojo.ForceTargetPriority.NegativeRisk {




      if (!(vx * vx + vy * vy + vz * vz < 1.0E-5) && !negatives.isEmpty()) {
         for (negative in negatives) {

            if (Math.hypot(Math.hypot(negative.x - (player.x + vx * t), negative.z - (player.z + vz * t)), (negative.y - (player.y + vy * t)) * 0.55)
               <= coneRadius) {
               return ForceTargetPriority.NegativeRisk.SWEPT_CONE
            }
         }

         return ForceTargetPriority.NegativeRisk.NONE
      } else {
         return ForceTargetPriority.NegativeRisk.NONE
      }
   }

   fun choosePushLane(
      player: jooon.features.dojo.ForceTargetPriority.Point2,
      target: jooon.features.dojo.ForceTargetPriority.Point2,
      edges: List<jooon.features.dojo.ForceTargetPriority.EdgeOption>
   ): jooon.features.dojo.ForceTargetPriority.PushLane? {
      val `iterator$iv`: java.util.Iterator = mapNotNull(asSequence(edges), { edge: ForceTargetPriority.EdgeOption ->
         val var10000: ForceTargetPriority.Point2 = normalized(edge.direction)
         if (var10000 == null) null else ForceTargetPriority.EdgeOption.copy$default(edge, var10000, 0.0, 2, null)
      }).iterator()
      var var10000: ForceTargetPriority.EdgeOption
      if (!`iterator$iv`.hasNext()) {
         var10000 = null
      } else {
         var `minElem$iv`: Any = `iterator$iv`.next()
         if (!`iterator$iv`.hasNext()) {
            var10000 = (ForceTargetPriority.EdgeOption)`minElem$iv`
         } else {
            var var18: Double = (`minElem$iv` as ForceTargetPriority.EdgeOption).distance

            do {


               if (java.lang.Double.compare(var18, var20) > 0) {
                  `minElem$iv` = var19
                  var18 = var20
               }
            } while (`iterator$iv`.hasNext())

            var10000 = (ForceTargetPriority.EdgeOption)`minElem$iv`
         }
      }

      var10000 = var10000
      if (var10000 == null) {
         return null
      } else {
         val anchor: ForceTargetPriority.Point2 = choosePushAnchor$default(this, target, var10000.direction, 0.0, 4, null)
         val var16: ForceTargetPriority.Point2 = this.normalized(ForceTargetPriority.Point2(target.x - player.x, target.z - player.z))
         return ForceTargetPriority.PushLane(
            var10000.direction, anchor, if (var16 != null) dot(var16, var10000.direction) else -1.0, var10000.distance
         )
      }
   }

   fun choosePushAnchor(
      target: jooon.features.dojo.ForceTargetPriority.Point2,
      edgeDirection: jooon.features.dojo.ForceTargetPriority.Point2,
      preferredDistance: Double = 2.55
   ): jooon.features.dojo.ForceTargetPriority.Point2 {
      var var10000: ForceTargetPriority.Point2 = this.normalized(edgeDirection)
      if (var10000 == null) {
         var10000 = ForceTargetPriority.Point2(0.0, 1.0)
      }

      return ForceTargetPriority.Point2(target.x - var10000.x * preferredDistance, target.z - var10000.z * preferredDistance)
   }

   private fun nextBest(candidates: List<jooon.features.dojo.ForceTargetPriority.Candidate>, nowMs: Long): jooon.features.dojo.ForceTargetPriority.Candidate? {
      return maxWithOrNull(
         candidates,
         ForceTargetPriority$nextBest$$inlined$thenBy$2(
            ForceTargetPriority$nextBest$$inlined$thenBy$1(
               ForceTargetPriority$nextBest$$inlined$thenByDescending$1(ForceTargetPriority$nextBest$$inlined$compareBy$1(nowMs))
            )
         )
      ) as ForceTargetPriority.Candidate
   }

   private fun isSelectable(candidate: jooon.features.dojo.ForceTargetPriority.Candidate, preventNegative: Boolean): Boolean {
      return candidate.pointValue > 0 && (!preventNegative || !candidate.negative)
   }

   private fun hasLiveEdgeIntent(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      label42@
      if (candidate.negativeRisk != ForceTargetPriority.NegativeRisk.NONE) {
         return false
      } else {
         return this.normalizedEdgeDistance(candidate.edgeDistance) <= 2.25
            || this.normalizedEdgeDistance(candidate.bestEdgeDistance) <= 2.25
               && this.normalizedEdgeDistance(candidate.edgeDistance) <= 3.1
               && candidate.ticksSinceProgress <= 16
            }
   }

   private fun isStaleEdgeMemory(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      return this.normalizedEdgeDistance(candidate.bestEdgeDistance) <= 2.25
         && this.normalizedEdgeDistance(candidate.edgeDistance) > 3.1
         && candidate.ticksSinceProgress > 16
      }

   private fun isNoContactChaseStall(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): Boolean {
      return candidate.hitCount == 0
         && candidate.ticksOnTarget >= 22
         && !candidate.attackReachable
         && !candidate.oneStepReachable
         && this.normalizedEdgeDistance(candidate.edgeDistance) > 2.25
      }

   private fun stateFor(candidate: jooon.features.dojo.ForceTargetPriority.Candidate): jooon.features.dojo.ForceTargetPriority.BrainState {
      if (this.shouldReposition(candidate)) {
         return ForceTargetPriority.BrainState.POSITION
      } else {
         return if (this.isFinishNow(candidate) && !candidate.attackReachable)
            ForceTargetPriority.BrainState.POSITION
return else
            ForceTargetPriority.BrainState.COMBO
         }
   }

   private fun normalizedEdgeDistance(value: Double): Double {
      return if (java.lang.Double.isInfinite(value) || java.lang.Double.isNaN(value)) 7.5 else (value).coerceIn(0.35, 7.5)
   }

   private fun normalizedNegativeDistance(value: Double): Double {
      return if (java.lang.Double.isInfinite(value) || java.lang.Double.isNaN(value)) 6.0 else (value).coerceIn(0.0, 6.0)
   }

   private fun normalized(point: jooon.features.dojo.ForceTargetPriority.Point2): jooon.features.dojo.ForceTargetPriority.Point2? {

      return if (len < 1.0E-5) null else ForceTargetPriority.Point2(point.x / len, point.z / len)
   }

   private fun dot(a: jooon.features.dojo.ForceTargetPriority.Point2, b: jooon.features.dojo.ForceTargetPriority.Point2): Double {
      return a.x * b.x + a.z * b.z
   }

   private fun format(value: Double): String {
      if (java.lang.Double.isInfinite(value) || java.lang.Double.isNaN(value)) {
         return "inf"
      } else {

         val var7: Array<Any> = arrayOf(value)

         return var10000
      }
   }

   enum class BrainState {
      SCAN,
      POSITION,
      COMBO,
      RESET_SPRINT,
      ABANDON,
      RECENTER;

      
      fun getEntries(): EnumEntries<ForceTargetPriority.BrainState> {
         $ENTRIES
      }
   }

   data class Candidate(id: Int,
      distanceSqr: Double,
      pointValue: Int,
      negative: Boolean,
      expiresAtMs: Long,
      edgeDistance: Double = 7.5,
      nearestNegativeDistance: Double = 6.0,
      laneAlignment: Double = 1.0,
      laneUsable: Boolean = true,
      attackReachable: Boolean = false,
      oneStepReachable: Boolean = false,
      committed: Boolean = false,
      hitCount: Int = 0,
      ticksOnTarget: Int = 0,
      outwardProgress: Double = 0.0,
      bestEdgeDistance: Double = 7.5,
      edgeVelocity: Double = 0.0,
      ticksSinceProgress: Int = Integer.MAX_VALUE,
      ticksSinceUsefulHit: Int = Integer.MAX_VALUE,
      consecutiveBadHits: Int = 0,
      negativeRisk: jooon.features.dojo.ForceTargetPriority.NegativeRisk = ForceTargetPriority.NegativeRisk.NONE,
      abandoned: Boolean = false
   ) {
      val id: Int
      val distanceSqr: Double
      val pointValue: Int
      val negative: Boolean
      val expiresAtMs: Long
      val edgeDistance: Double
      val nearestNegativeDistance: Double
      val laneAlignment: Double
      val laneUsable: Boolean
      val attackReachable: Boolean
      val oneStepReachable: Boolean
      val committed: Boolean
      val hitCount: Int
      val ticksOnTarget: Int
      val outwardProgress: Double
      val bestEdgeDistance: Double
      val edgeVelocity: Double
      val ticksSinceProgress: Int
      val ticksSinceUsefulHit: Int
      val consecutiveBadHits: Int
      val negativeRisk: jooon.features.dojo.ForceTargetPriority.NegativeRisk
      val abandoned: Boolean

      init {
         this.id = id
         this.distanceSqr = distanceSqr
         this.pointValue = pointValue
         this.negative = negative
         this.expiresAtMs = expiresAtMs
         this.edgeDistance = edgeDistance
         this.nearestNegativeDistance = nearestNegativeDistance
         this.laneAlignment = laneAlignment
         this.laneUsable = laneUsable
         this.attackReachable = attackReachable
         this.oneStepReachable = oneStepReachable
         this.committed = committed
         this.hitCount = hitCount
         this.ticksOnTarget = ticksOnTarget
         this.outwardProgress = outwardProgress
         this.bestEdgeDistance = bestEdgeDistance
         this.edgeVelocity = edgeVelocity
         this.ticksSinceProgress = ticksSinceProgress
         this.ticksSinceUsefulHit = ticksSinceUsefulHit
         this.consecutiveBadHits = consecutiveBadHits
         this.negativeRisk = negativeRisk
         this.abandoned = abandoned
      }

      public operator fun component1(): Int {
         return this.id
      }

      public operator fun component2(): Double {
         return this.distanceSqr
      }

      public operator fun component3(): Int {
         return this.pointValue
      }

      public operator fun component4(): Boolean {
         return this.negative
      }

      public operator fun component5(): Long {
         return this.expiresAtMs
      }

      public operator fun component6(): Double {
         return this.edgeDistance
      }

      public operator fun component7(): Double {
         return this.nearestNegativeDistance
      }

      public operator fun component8(): Double {
         return this.laneAlignment
      }

      public operator fun component9(): Boolean {
         return this.laneUsable
      }

      public operator fun component10(): Boolean {
         return this.attackReachable
      }

      public operator fun component11(): Boolean {
         return this.oneStepReachable
      }

      public operator fun component12(): Boolean {
         return this.committed
      }

      public operator fun component13(): Int {
         return this.hitCount
      }

      public operator fun component14(): Int {
         return this.ticksOnTarget
      }

      public operator fun component15(): Double {
         return this.outwardProgress
      }

      public operator fun component16(): Double {
         return this.bestEdgeDistance
      }

      public operator fun component17(): Double {
         return this.edgeVelocity
      }

      public operator fun component18(): Int {
         return this.ticksSinceProgress
      }

      public operator fun component19(): Int {
         return this.ticksSinceUsefulHit
      }

      public operator fun component20(): Int {
         return this.consecutiveBadHits
      }

      public operator fun component21(): jooon.features.dojo.ForceTargetPriority.NegativeRisk {
         return this.negativeRisk
      }

      public operator fun component22(): Boolean {
         return this.abandoned
      }

      fun copy(
         id: Int = this.id,
         distanceSqr: Double = this.distanceSqr,
         pointValue: Int = this.pointValue,
         negative: Boolean = this.negative,
         expiresAtMs: Long = this.expiresAtMs,
         edgeDistance: Double = this.edgeDistance,
         nearestNegativeDistance: Double = this.nearestNegativeDistance,
         laneAlignment: Double = this.laneAlignment,
         laneUsable: Boolean = this.laneUsable,
         attackReachable: Boolean = this.attackReachable,
         oneStepReachable: Boolean = this.oneStepReachable,
         committed: Boolean = this.committed,
         hitCount: Int = this.hitCount,
         ticksOnTarget: Int = this.ticksOnTarget,
         outwardProgress: Double = this.outwardProgress,
         bestEdgeDistance: Double = this.bestEdgeDistance,
         edgeVelocity: Double = this.edgeVelocity,
         ticksSinceProgress: Int = this.ticksSinceProgress,
         ticksSinceUsefulHit: Int = this.ticksSinceUsefulHit,
         consecutiveBadHits: Int = this.consecutiveBadHits,
         negativeRisk: jooon.features.dojo.ForceTargetPriority.NegativeRisk = this.negativeRisk,
         abandoned: Boolean = this.abandoned
      ): jooon.features.dojo.ForceTargetPriority.Candidate {
         return ForceTargetPriority.Candidate(
            id,
            distanceSqr,
            pointValue,
            negative,
            expiresAtMs,
            edgeDistance,
            nearestNegativeDistance,
            laneAlignment,
            laneUsable,
            attackReachable,
            oneStepReachable,
            committed,
            hitCount,
            ticksOnTarget,
            outwardProgress,
            bestEdgeDistance,
            edgeVelocity,
            ticksSinceProgress,
            ticksSinceUsefulHit,
            consecutiveBadHits,
            negativeRisk,
return abandoned
         )
      }

      override fun toString(): String {
         return "Candidate(id=${this.id}, distanceSqr=${this.distanceSqr}, pointValue=${this.pointValue}, negative=${this.negative}, expiresAtMs=${this.expiresAtMs}, edgeDistance=${this.edgeDistance}, nearestNegativeDistance=${this.nearestNegativeDistance}, laneAlignment=${this.laneAlignment}, laneUsable=${this.laneUsable}, attackReachable=${this.attackReachable}, oneStepReachable=${this.oneStepReachable}, committed=${this.committed}, hitCount=${this.hitCount}, ticksOnTarget=${this.ticksOnTarget}, outwardProgress=${this.outwardProgress}, bestEdgeDistance=${this.bestEdgeDistance}, edgeVelocity=${this.edgeVelocity}, ticksSinceProgress=${this.ticksSinceProgress}, ticksSinceUsefulHit=${this.ticksSinceUsefulHit}, consecutiveBadHits=${this.consecutiveBadHits}, negativeRisk=${this.negativeRisk}, abandoned=${this.abandoned})"
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
                                                                                                            (
                                                                                                                     (
                                                                                                                              (
                                                                                                                                       (
                                                                                                                                                (
                                                                                                                                                         (
                                                                                                                                                                  (
                                                                                                                                                                           (
                                                                                                                                                                                    (
                                                                                                                                                                                             Integer.hashCode(
                                                                                                                                                                                                      this.id
                                                                                                                                                                                                   )
                                                                                                                                                                                                   * 31
                                                                                                                                                                                                + java.lang.Double.hashCode(
                                                                                                                                                                                                   this.distanceSqr
                                                                                                                                                                                                )
                                                                                                                                                                                          )
                                                                                                                                                                                          * 31
                                                                                                                                                                                       + Integer.hashCode(
                                                                                                                                                                                          this.pointValue
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                                 * 31
                                                                                                                                                                              + java.lang.Boolean.hashCode(
                                                                                                                                                                                 this.negative
                                                                                                                                                                              )
                                                                                                                                                                        )
                                                                                                                                                                        * 31
                                                                                                                                                                     + java.lang.Long.hashCode(
                                                                                                                                                                        this.expiresAtMs
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                               * 31
                                                                                                                                                            + java.lang.Double.hashCode(
                                                                                                                                                               this.edgeDistance
                                                                                                                                                            )
                                                                                                                                                      )
                                                                                                                                                      * 31
                                                                                                                                                   + java.lang.Double.hashCode(
                                                                                                                                                      this.nearestNegativeDistance
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                             * 31
                                                                                                                                          + java.lang.Double.hashCode(
                                                                                                                                             this.laneAlignment
                                                                                                                                          )
                                                                                                                                    )
                                                                                                                                    * 31
                                                                                                                                 + java.lang.Boolean.hashCode(
                                                                                                                                    this.laneUsable
                                                                                                                                 )
                                                                                                                           )
                                                                                                                           * 31
                                                                                                                        + java.lang.Boolean.hashCode(
                                                                                                                           this.attackReachable
                                                                                                                        )
                                                                                                                  )
                                                                                                                  * 31
                                                                                                               + java.lang.Boolean.hashCode(
                                                                                                                  this.oneStepReachable
                                                                                                               )
                                                                                                         )
                                                                                                         * 31
                                                                                                      + java.lang.Boolean.hashCode(this.committed)
                                                                                                )
                                                                                                * 31
                                                                                             + Integer.hashCode(this.hitCount)
                                                                                       )
                                                                                       * 31
                                                                                    + Integer.hashCode(this.ticksOnTarget)
                                                                              )
                                                                              * 31
                                                                           + java.lang.Double.hashCode(this.outwardProgress)
                                                                     )
                                                                     * 31
                                                                  + java.lang.Double.hashCode(this.bestEdgeDistance)
                                                            )
                                                            * 31
                                                         + java.lang.Double.hashCode(this.edgeVelocity)
                                                   )
                                                   * 31
                                                + Integer.hashCode(this.ticksSinceProgress)
                                          )
                                          * 31
                                       + Integer.hashCode(this.ticksSinceUsefulHit)
                                 )
                                 * 31
                              + Integer.hashCode(this.consecutiveBadHits)
                        )
                        * 31
                     + this.negativeRisk.hashCode()
               )
               * 31
            + java.lang.Boolean.hashCode(this.abandoned)
         }

      override operator fun equals(other: Any?): Boolean {
         label148@
         if (this === other) {
            return true
         } else {
            return other is ForceTargetPriority.Candidate
               && this.id == (other as ForceTargetPriority.Candidate).id
               && java.lang.Double.compare(this.distanceSqr, (other as ForceTargetPriority.Candidate).distanceSqr) == 0
               && this.pointValue == (other as ForceTargetPriority.Candidate).pointValue
               && this.negative == (other as ForceTargetPriority.Candidate).negative
               && this.expiresAtMs == (other as ForceTargetPriority.Candidate).expiresAtMs
               && java.lang.Double.compare(this.edgeDistance, (other as ForceTargetPriority.Candidate).edgeDistance) == 0
               && java.lang.Double.compare(this.nearestNegativeDistance, (other as ForceTargetPriority.Candidate).nearestNegativeDistance) == 0
               && java.lang.Double.compare(this.laneAlignment, (other as ForceTargetPriority.Candidate).laneAlignment) == 0
               && this.laneUsable == (other as ForceTargetPriority.Candidate).laneUsable
               && this.attackReachable == (other as ForceTargetPriority.Candidate).attackReachable
               && this.oneStepReachable == (other as ForceTargetPriority.Candidate).oneStepReachable
               && this.committed == (other as ForceTargetPriority.Candidate).committed
               && this.hitCount == (other as ForceTargetPriority.Candidate).hitCount
               && this.ticksOnTarget == (other as ForceTargetPriority.Candidate).ticksOnTarget
               && java.lang.Double.compare(this.outwardProgress, (other as ForceTargetPriority.Candidate).outwardProgress) == 0
               && java.lang.Double.compare(this.bestEdgeDistance, (other as ForceTargetPriority.Candidate).bestEdgeDistance) == 0
               && java.lang.Double.compare(this.edgeVelocity, (other as ForceTargetPriority.Candidate).edgeVelocity) == 0
               && this.ticksSinceProgress == (other as ForceTargetPriority.Candidate).ticksSinceProgress
               && this.ticksSinceUsefulHit == (other as ForceTargetPriority.Candidate).ticksSinceUsefulHit
               && this.consecutiveBadHits == (other as ForceTargetPriority.Candidate).consecutiveBadHits
               && this.negativeRisk === (other as ForceTargetPriority.Candidate).negativeRisk
               && this.abandoned == (other as ForceTargetPriority.Candidate).abandoned
            }
      }
   }

   data class Decision(selected: jooon.features.dojo.ForceTargetPriority.Candidate?,
      state: jooon.features.dojo.ForceTargetPriority.BrainState,
      bucket: jooon.features.dojo.ForceTargetPriority.TargetBucket?,
      reason: String
   ) {
      val selected: jooon.features.dojo.ForceTargetPriority.Candidate?
      val state: jooon.features.dojo.ForceTargetPriority.BrainState
      val bucket: jooon.features.dojo.ForceTargetPriority.TargetBucket?
      val reason: String

      init {
         this.selected = selected
         this.state = state
         this.bucket = bucket
         this.reason = reason
      }

      public operator fun component1(): jooon.features.dojo.ForceTargetPriority.Candidate? {
         return this.selected
      }

      public operator fun component2(): jooon.features.dojo.ForceTargetPriority.BrainState {
         return this.state
      }

      public operator fun component3(): jooon.features.dojo.ForceTargetPriority.TargetBucket? {
         return this.bucket
      }

      public operator fun component4(): String {
         return this.reason
      }

      fun copy(
         selected: jooon.features.dojo.ForceTargetPriority.Candidate? = this.selected,
         state: jooon.features.dojo.ForceTargetPriority.BrainState = this.state,
         bucket: jooon.features.dojo.ForceTargetPriority.TargetBucket? = this.bucket,
         reason: String = this.reason
      ): jooon.features.dojo.ForceTargetPriority.Decision {
         return ForceTargetPriority.Decision(selected, state, bucket, reason)
      }

      override fun toString(): String {
         return "Decision(selected=${this.selected}, state=${this.state}, bucket=${this.bucket}, reason=${this.reason})"
      }

      override fun hashCode(): Int {
         return (
                  ((if (this.selected == null) 0 else this.selected.hashCode()) * 31 + this.state.hashCode()) * 31
                     + (if (this.bucket == null) 0 else this.bucket.hashCode())
               )
               * 31
            + this.reason.hashCode()
         }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is ForceTargetPriority.Decision
               && this.selected == (other as ForceTargetPriority.Decision).selected
               && this.state === (other as ForceTargetPriority.Decision).state
               && this.bucket === (other as ForceTargetPriority.Decision).bucket
               && this.reason == (other as ForceTargetPriority.Decision).reason
            }
      }
   }

   data class EdgeOption(direction: jooon.features.dojo.ForceTargetPriority.Point2, distance: Double) {
      val direction: jooon.features.dojo.ForceTargetPriority.Point2
      val distance: Double

      init {
         this.direction = direction
         this.distance = distance
      }

      public operator fun component1(): jooon.features.dojo.ForceTargetPriority.Point2 {
         return this.direction
      }

      public operator fun component2(): Double {
         return this.distance
      }

      fun copy(direction: jooon.features.dojo.ForceTargetPriority.Point2 = this.direction, distance: Double = this.distance): jooon.features.dojo.ForceTargetPriority.EdgeOption {
         return ForceTargetPriority.EdgeOption(direction, distance)
      }

      override fun toString(): String {
         return "EdgeOption(direction=${this.direction}, distance=${this.distance})"
      }

      override fun hashCode(): Int {
         return this.direction.hashCode() * 31 + java.lang.Double.hashCode(this.distance)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is ForceTargetPriority.EdgeOption
               && this.direction == (other as ForceTargetPriority.EdgeOption).direction
               && java.lang.Double.compare(this.distance, (other as ForceTargetPriority.EdgeOption).distance) == 0
            }
      }
   }

   enum class NegativeRisk {
      NONE,
      CROSSHAIR_NEGATIVE,
      SWEPT_CONE,
      CLUSTERED;

      
      fun getEntries(): EnumEntries<ForceTargetPriority.NegativeRisk> {
         $ENTRIES
      }
   }

   data class Point2(x: Double, z: Double) {
      val x: Double
      val z: Double

      init {
         this.x = x
         this.z = z
      }

      public operator fun component1(): Double {
         return this.x
      }

      public operator fun component2(): Double {
         return this.z
      }

      fun copy(x: Double = this.x, z: Double = this.z): jooon.features.dojo.ForceTargetPriority.Point2 {
         return ForceTargetPriority.Point2(x, z)
      }

      override fun toString(): String {
         return "Point2(x=${this.x}, z=${this.z})"
      }

      override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.z)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is ForceTargetPriority.Point2
               && java.lang.Double.compare(this.x, (other as ForceTargetPriority.Point2).x) == 0
               && java.lang.Double.compare(this.z, (other as ForceTargetPriority.Point2).z) == 0
            }
      }
   }

   data class Point3(x: Double, y: Double, z: Double) {
      val x: Double
      val y: Double
      val z: Double

      init {
         this.x = x
         this.y = y
         this.z = z
      }

      public operator fun component1(): Double {
         return this.x
      }

      public operator fun component2(): Double {
         return this.y
      }

      public operator fun component3(): Double {
         return this.z
      }

      fun copy(x: Double = this.x, y: Double = this.y, z: Double = this.z): jooon.features.dojo.ForceTargetPriority.Point3 {
         return ForceTargetPriority.Point3(x, y, z)
      }

      override fun toString(): String {
         return "Point3(x=${this.x}, y=${this.y}, z=${this.z})"
      }

      override fun hashCode(): Int {
         return (java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.y)) * 31 + java.lang.Double.hashCode(this.z)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is ForceTargetPriority.Point3
               && java.lang.Double.compare(this.x, (other as ForceTargetPriority.Point3).x) == 0
               && java.lang.Double.compare(this.y, (other as ForceTargetPriority.Point3).y) == 0
               && java.lang.Double.compare(this.z, (other as ForceTargetPriority.Point3).z) == 0
            }
      }
   }

   data class PushLane(edgeDirection: jooon.features.dojo.ForceTargetPriority.Point2,
      anchor: jooon.features.dojo.ForceTargetPriority.Point2,
      alignment: Double,
      edgeDistance: Double
   ) {
      val edgeDirection: jooon.features.dojo.ForceTargetPriority.Point2
      val anchor: jooon.features.dojo.ForceTargetPriority.Point2
      val alignment: Double
      val edgeDistance: Double

      init {
         this.edgeDirection = edgeDirection
         this.anchor = anchor
         this.alignment = alignment
         this.edgeDistance = edgeDistance
      }

      public operator fun component1(): jooon.features.dojo.ForceTargetPriority.Point2 {
         return this.edgeDirection
      }

      public operator fun component2(): jooon.features.dojo.ForceTargetPriority.Point2 {
         return this.anchor
      }

      public operator fun component3(): Double {
         return this.alignment
      }

      public operator fun component4(): Double {
         return this.edgeDistance
      }

      fun copy(
         edgeDirection: jooon.features.dojo.ForceTargetPriority.Point2 = this.edgeDirection,
         anchor: jooon.features.dojo.ForceTargetPriority.Point2 = this.anchor,
         alignment: Double = this.alignment,
         edgeDistance: Double = this.edgeDistance
      ): jooon.features.dojo.ForceTargetPriority.PushLane {
         return ForceTargetPriority.PushLane(edgeDirection, anchor, alignment, edgeDistance)
      }

      override fun toString(): String {
         return "PushLane(edgeDirection=${this.edgeDirection}, anchor=${this.anchor}, alignment=${this.alignment}, edgeDistance=${this.edgeDistance})"
      }

      override fun hashCode(): Int {
         return ((this.edgeDirection.hashCode() * 31 + this.anchor.hashCode()) * 31 + java.lang.Double.hashCode(this.alignment)) * 31
            + java.lang.Double.hashCode(this.edgeDistance)
         }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is ForceTargetPriority.PushLane
               && this.edgeDirection == (other as ForceTargetPriority.PushLane).edgeDirection
               && this.anchor == (other as ForceTargetPriority.PushLane).anchor
               && java.lang.Double.compare(this.alignment, (other as ForceTargetPriority.PushLane).alignment) == 0
               && java.lang.Double.compare(this.edgeDistance, (other as ForceTargetPriority.PushLane).edgeDistance) == 0
            }
      }
   }

   enum class TargetBucket {
      FINISH_NOW,
      COMMITTED,
      NEXT_BEST;

      
      fun getEntries(): EnumEntries<ForceTargetPriority.TargetBucket> {
         $ENTRIES
      }
   }
}
