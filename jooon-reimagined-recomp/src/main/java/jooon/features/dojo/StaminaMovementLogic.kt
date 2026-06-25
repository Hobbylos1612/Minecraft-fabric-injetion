package jooon.features.dojo

import kotlin.enums.EnumEntries

internal object StaminaMovementLogic {
   fun axisFor(hole: jooon.features.dojo.StaminaMovementLogic.HoleBox): jooon.features.dojo.StaminaMovementLogic.Axis {
      return if (hole.xSize <= hole.zSize) StaminaMovementLogic.Axis.X else StaminaMovementLogic.Axis.Z
   }

   fun axisFor(direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection, hole: jooon.features.dojo.StaminaMovementLogic.HoleBox): jooon.features.dojo.StaminaMovementLogic.Axis {
      var var10000: StaminaMovementLogic.Axis
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
         1, 2 -> var10000 = StaminaMovementLogic.Axis.X
         3, 4 -> var10000 = StaminaMovementLogic.Axis.Z
         5, 6 -> var10000 = this.axisFor(hole)
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun directionFromPrevious(
      hole: jooon.features.dojo.StaminaMovementLogic.HoleBox,
      previous: Collection<jooon.features.dojo.StaminaMovementLogic.HoleBox>
   ): jooon.features.dojo.StaminaMovementLogic.HoleDirection {
      if (previous.contains(hole)) {
         return StaminaMovementLogic.HoleDirection.UNCHANGED
      } else if (previous.contains(hole.move(1.0, 0.0))) {
         return StaminaMovementLogic.HoleDirection.POSITIVE_X
      } else if (previous.contains(hole.move(-1.0, 0.0))) {
         return StaminaMovementLogic.HoleDirection.NEGATIVE_X
      } else if (previous.contains(hole.move(0.0, 1.0))) {
         return StaminaMovementLogic.HoleDirection.POSITIVE_Z
      } else {
         return if (previous.contains(hole.move(0.0, -1.0))) StaminaMovementLogic.HoleDirection.NEGATIVE_Z else StaminaMovementLogic.HoleDirection.NEW
      }
   }

   fun isIncoming(
      hole: jooon.features.dojo.StaminaMovementLogic.HoleBox,
      direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection,
      player: jooon.features.dojo.StaminaMovementLogic.Point
   ): Boolean {
      var var10000: Boolean
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
         1 -> var10000 = player.x < hole.minX - 0.05
         2 -> var10000 = player.x > hole.maxX + 0.05
         3 -> var10000 = player.z < hole.minZ - 0.05
         4 -> var10000 = player.z > hole.maxZ + 0.05
         5, 6 -> var10000 = true
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun entrySideFor(
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      center: jooon.features.dojo.StaminaMovementLogic.Point
   ): Double {
      var var10000: Double
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
         1 -> var10000 = if (axis === StaminaMovementLogic.Axis.X) -1.0 else this.currentSide(axis, player, center)
         2 -> var10000 = if (axis === StaminaMovementLogic.Axis.X) 1.0 else this.currentSide(axis, player, center)
         3 -> var10000 = if (axis === StaminaMovementLogic.Axis.Z) -1.0 else this.currentSide(axis, player, center)
         4 -> var10000 = if (axis === StaminaMovementLogic.Axis.Z) 1.0 else this.currentSide(axis, player, center)
         5, 6 -> var10000 = this.currentSide(axis, player, center)
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun currentSide(
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      center: jooon.features.dojo.StaminaMovementLogic.Point
   ): Double {
      var var10000: Double
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
         1 -> var10000 = if (player.x < center.x) -1.0 else 1.0
         2 -> var10000 = if (player.z < center.z) -1.0 else 1.0
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun passTarget(
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      center: jooon.features.dojo.StaminaMovementLogic.Point,
      entrySide: Double,
      distance: Double
   ): jooon.features.dojo.StaminaMovementLogic.Point {
      var var10000: StaminaMovementLogic.Point
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
         1 -> var10000 = StaminaMovementLogic.Point(center.x - entrySide * distance, center.z)
         2 -> var10000 = StaminaMovementLogic.Point(center.x, center.z - entrySide * distance)
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun entryApproachTarget(
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      center: jooon.features.dojo.StaminaMovementLogic.Point,
      entrySide: Double,
      distance: Double
   ): jooon.features.dojo.StaminaMovementLogic.Point {
      var var10000: StaminaMovementLogic.Point
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
         1 -> var10000 = StaminaMovementLogic.Point(center.x + entrySide * distance, center.z)
         2 -> var10000 = StaminaMovementLogic.Point(center.x, center.z + entrySide * distance)
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun stagedJumpTarget(
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      center: jooon.features.dojo.StaminaMovementLogic.Point,
      entrySide: Double,
      distance: Double
   ): jooon.features.dojo.StaminaMovementLogic.Point {
      return this.entryApproachTarget(axis, center, entrySide, distance)
   }

   fun planHole(
      hole: jooon.features.dojo.StaminaMovementLogic.HoleBox,
      direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      recentlyPassed: jooon.features.dojo.StaminaMovementLogic.Pass?,
      lateralAlignTolerance: Double,
      passApproachDistance: Double,
      passThroughDistance: Double,
      passedWallCenterDistance: Double,
      entryApproachDistance: Double = 1.35
   ): jooon.features.dojo.StaminaMovementLogic.PlannedHole? {
      val axis: StaminaMovementLogic.Axis = this.axisFor(direction, hole)
      val center: StaminaMovementLogic.Point = hole.center
      if (!this.isIncoming(hole, direction, player)) {
         return null
      } else if (this.isRecentlyPassedWall(axis, center, player, recentlyPassed, passedWallCenterDistance)) {
         return null
      } else {

var var10000: Double
         when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
            1 -> var10000 = Math.abs(player.x - center.x)
            2 -> var10000 = Math.abs(player.z - center.z)
            else -> throw NoWhenBranchMatchedException()
         }

         when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
            1 -> var10000 = Math.abs(player.z - center.z)
            2 -> var10000 = Math.abs(player.x - center.x)
            else -> throw NoWhenBranchMatchedException()
         }

         if (var10000 > lateralAlignTolerance && var10000 <= passApproachDistance) {
            return null
         } else {
            var phase: StaminaMovementLogic.ApproachPhase
            run label68@{
               phase = if (!(var10000 > lateralAlignTolerance) && !(var10000 > passApproachDistance))
   StaminaMovementLogic.ApproachPhase.PASS_THROUGH
return else
   StaminaMovementLogic.ApproachPhase.ALIGN_LATERAL
                  when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$2[(if (!(var10000 > lateralAlignTolerance) && !(var10000 > passApproachDistance))
                     StaminaMovementLogic.ApproachPhase.PASS_THROUGH
return else
                     StaminaMovementLogic.ApproachPhase.ALIGN_LATERAL)
                  .ordinal()]) {
                  1 -> {
                     when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
                        1 -> {
                           var26 = if (var10000 <= lateralAlignTolerance)
                              this.entryApproachTarget(axis, center, entrySide, entryApproachDistance)
return else
                              StaminaMovementLogic.Point(player.x, center.z)
                              break@label68
                        }
                        2 -> {
                           var26 = if (var10000 <= lateralAlignTolerance)
                              this.entryApproachTarget(axis, center, entrySide, entryApproachDistance)
return else
                              StaminaMovementLogic.Point(center.x, player.z)
                              break@label68
                        }
                        else -> throw NoWhenBranchMatchedException()
                     }
                  }
                  2 -> var26 = this.stagedJumpTarget(axis, center, entrySide, entryApproachDistance)
                  3 -> var26 = this.passTarget(axis, center, entrySide, passThroughDistance)
                  else -> throw NoWhenBranchMatchedException()
               }
            }

            return StaminaMovementLogic.PlannedHole(hole, direction, axis, phase, center, var26, entrySide, var10000, var10000)
         }
      }
   }

   fun hasCompletedPass(
      pass: jooon.features.dojo.StaminaMovementLogic.Pass,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      passedDistance: Double
   ): Boolean {
      return this.exitSideDistance(pass, player) > passedDistance
   }

   fun shouldBrakeAfterJumpPass(
      pass: jooon.features.dojo.StaminaMovementLogic.Pass,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      brakeDistance: Double
   ): Boolean {
      return this.exitSideDistance(pass, player) >= brakeDistance
   }

   fun shouldRefreshActivePass(
      pass: jooon.features.dojo.StaminaMovementLogic.Pass,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      maxExitSideDistance: Double
   ): Boolean {
      return this.exitSideDistance(pass, player) <= maxExitSideDistance
   }

   fun isRecentlyPassedWall(
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      center: jooon.features.dojo.StaminaMovementLogic.Point,
      player: jooon.features.dojo.StaminaMovementLogic.Point,
      passed: jooon.features.dojo.StaminaMovementLogic.Pass?,
      maxCenterDistance: Double
   ): Boolean {
      if (passed == null) {
         return false
      } else if (passed.axis != axis) {
         return false
      } else {
         var var10000: Double
         when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[axis.ordinal()]) {
            1 -> var10000 = Math.abs(center.x - passed.center.x)
            2 -> var10000 = Math.abs(center.z - passed.center.z)
            else -> throw NoWhenBranchMatchedException()
         }

         return var10000 <= maxCenterDistance
      }
   }

   fun nearestMatchingPassHole(
      pass: jooon.features.dojo.StaminaMovementLogic.Pass,
      holes: Iterable<jooon.features.dojo.StaminaMovementLogic.HoleBox>,
      maxCenterDistance: Double
   ): jooon.features.dojo.StaminaMovementLogic.HoleBox? {
      var best: StaminaMovementLogic.HoleBox = null
      var bestDistance: Double = java.lang.Double.MAX_VALUE

      for (hole in holes) {
         if (this.axisFor(hole) === pass.axis) {

            if (!(distance > maxCenterDistance) && distance < bestDistance) {
               best = hole
               bestDistance = distance
            }
         }
      }

      return best
   }

   fun shouldMoveOnGapRoute(routeHasGap: Boolean, jumpPressed: Boolean, forwardFloorSafe: Boolean): Boolean {
      return !routeHasGap || jumpPressed || forwardFloorSafe
   }

   fun shouldStartJumpForRoute(
      highHole: Boolean,
      hasJumpableGap: Boolean,
      firstGapStartDistance: Double?,
      wallDistance: Double,
      targetDistance: Double,
      jumpWindow: Double,
      highHoleJumpWindow: Double = jumpWindow
   ): Boolean {
      return highHole && (wallDistance <= highHoleJumpWindow || targetDistance <= highHoleJumpWindow)
         || hasJumpableGap && firstGapStartDistance != null && firstGapStartDistance <= jumpWindow
      }

   fun scanGap(floorSamples: List<Boolean>, sampleStep: Double, maxGapWidth: Double, firstSampleDistance: Double = 0.0): jooon.features.dojo.StaminaMovementLogic.GapScan {
      var gapStart: Int = null
      var gapEnd: Int = -1
      var sawFloorAfterGap: Boolean = false
      val start: java.util.Iterator = floorSamples.iterator()
      var width: Int = 0

      while (start.hasNext()) {

         if (!start.next() as Boolean) {
            if (gapStart == null) {
               gapStart = index
            }

            gapEnd = index
         } else if (gapStart != null) {
            sawFloorAfterGap = true
         }
      }

      if (gapStart == null) {
         return StaminaMovementLogic.GapScan(false, true, null, 4, null)
      } else {

         return StaminaMovementLogic.GapScan(
            sawFloorAfterGap && (gapEnd - var17 + 1) * sampleStep <= maxGapWidth,
            sawFloorAfterGap && (gapEnd - var17 + 1) * sampleStep <= maxGapWidth,
            firstSampleDistance + var17.toDouble() * sampleStep
         )
      }
   }

   fun exactFloorOrFallback(x: Int, z: Int, exactFloorY: Int?, fallbackTopFloorY: Int, hasSolidFloor: (Int, Int, Int) -> Boolean): Boolean {
      if (exactFloorY != null) {
         return hasSolidFloor(x, exactFloorY, z) as Boolean
      } else {
         for (dy in 0 downTo -2) {
            if (hasSolidFloor(x, fallbackTopFloorY + dy, z) as Boolean) {
               return true
            }
         }

         return false
      }
   }

   private fun horizontalDistance(a: jooon.features.dojo.StaminaMovementLogic.Point, b: jooon.features.dojo.StaminaMovementLogic.Point): Double {
      return Math.hypot(a.x - b.x, a.z - b.z)
   }

   private fun exitSideDistance(pass: jooon.features.dojo.StaminaMovementLogic.Pass, player: jooon.features.dojo.StaminaMovementLogic.Point): Double {
      var var10000: Double
      when (StaminaMovementLogic.WhenMappings.$EnumSwitchMapping$1[pass.axis.ordinal()]) {
         1 -> var10000 = (player.x - pass.center.x) * -pass.entrySide
         2 -> var10000 = (player.z - pass.center.z) * -pass.entrySide
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   enum class ApproachPhase {
      ALIGN_LATERAL,
      STAGE_FOR_JUMP,
      PASS_THROUGH;

      
      fun getEntries(): EnumEntries<StaminaMovementLogic.ApproachPhase> {
         $ENTRIES
      }
   }

   enum class Axis {
      X,
      Z;

      
      fun getEntries(): EnumEntries<StaminaMovementLogic.Axis> {
         $ENTRIES
      }
   }

   data class GapScan(hasShortJumpableGap: Boolean, routeAcceptable: Boolean, firstGapStartDistance: Double? = null) {
      val hasShortJumpableGap: Boolean
      val routeAcceptable: Boolean
      val firstGapStartDistance: Double?

      init {
         this.hasShortJumpableGap = hasShortJumpableGap
         this.routeAcceptable = routeAcceptable
         this.firstGapStartDistance = firstGapStartDistance
      }

      public operator fun component1(): Boolean {
         return this.hasShortJumpableGap
      }

      public operator fun component2(): Boolean {
         return this.routeAcceptable
      }

      public operator fun component3(): Double? {
         return this.firstGapStartDistance
      }

      fun copy(
         hasShortJumpableGap: Boolean = this.hasShortJumpableGap,
         routeAcceptable: Boolean = this.routeAcceptable,
         firstGapStartDistance: Double? = this.firstGapStartDistance
      ): jooon.features.dojo.StaminaMovementLogic.GapScan {
         return StaminaMovementLogic.GapScan(hasShortJumpableGap, routeAcceptable, firstGapStartDistance)
      }

      override fun toString(): String {
         return "GapScan(hasShortJumpableGap=${this.hasShortJumpableGap}, routeAcceptable=${this.routeAcceptable}, firstGapStartDistance=${this.firstGapStartDistance})"
      }

      override fun hashCode(): Int {
         return (java.lang.Boolean.hashCode(this.hasShortJumpableGap) * 31 + java.lang.Boolean.hashCode(this.routeAcceptable)) * 31
            + (if (this.firstGapStartDistance == null) 0 else this.firstGapStartDistance.hashCode())
         }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is StaminaMovementLogic.GapScan
               && this.hasShortJumpableGap == (other as StaminaMovementLogic.GapScan).hasShortJumpableGap
               && this.routeAcceptable == (other as StaminaMovementLogic.GapScan).routeAcceptable
               && this.firstGapStartDistance == (other as StaminaMovementLogic.GapScan).firstGapStartDistance
            }
      }
   }

   data class HoleBox(minX: Double, maxX: Double, minZ: Double, maxZ: Double) {
      val minX: Double
      val maxX: Double
      val minZ: Double
      val maxZ: Double

      init {
         this.minX = minX
         this.maxX = maxX
         this.minZ = minZ
         this.maxZ = maxZ
      }

      val xSize: Double
         public get() {
            return this.maxX - this.minX
         }


      val zSize: Double
         public get() {
            return this.maxZ - this.minZ
         }


      val center: jooon.features.dojo.StaminaMovementLogic.Point
         public get() {
            return StaminaMovementLogic.Point((this.minX + this.maxX) * 0.5, (this.minZ + this.maxZ) * 0.5)
         }


      fun move(dx: Double, dz: Double): jooon.features.dojo.StaminaMovementLogic.HoleBox {
         return this.copy(this.minX + dx, this.maxX + dx, this.minZ + dz, this.maxZ + dz)
      }

      public operator fun component1(): Double {
         return this.minX
      }

      public operator fun component2(): Double {
         return this.maxX
      }

      public operator fun component3(): Double {
         return this.minZ
      }

      public operator fun component4(): Double {
         return this.maxZ
      }

      fun copy(minX: Double = this.minX, maxX: Double = this.maxX, minZ: Double = this.minZ, maxZ: Double = this.maxZ): jooon.features.dojo.StaminaMovementLogic.HoleBox {
         return StaminaMovementLogic.HoleBox(minX, maxX, minZ, maxZ)
      }

      override fun toString(): String {
         return "HoleBox(minX=${this.minX}, maxX=${this.maxX}, minZ=${this.minZ}, maxZ=${this.maxZ})"
      }

      override fun hashCode(): Int {
         return ((java.lang.Double.hashCode(this.minX) * 31 + java.lang.Double.hashCode(this.maxX)) * 31 + java.lang.Double.hashCode(this.minZ)) * 31
            + java.lang.Double.hashCode(this.maxZ)
         }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is StaminaMovementLogic.HoleBox
               && java.lang.Double.compare(this.minX, (other as StaminaMovementLogic.HoleBox).minX) == 0
               && java.lang.Double.compare(this.maxX, (other as StaminaMovementLogic.HoleBox).maxX) == 0
               && java.lang.Double.compare(this.minZ, (other as StaminaMovementLogic.HoleBox).minZ) == 0
               && java.lang.Double.compare(this.maxZ, (other as StaminaMovementLogic.HoleBox).maxZ) == 0
            }
      }
   }

   enum class HoleDirection {
      POSITIVE_X,
      POSITIVE_Z,
      NEGATIVE_X,
      NEGATIVE_Z,
      NEW,
      UNCHANGED;

      
      fun getEntries(): EnumEntries<StaminaMovementLogic.HoleDirection> {
         $ENTRIES
      }
   }

   data class Pass(axis: jooon.features.dojo.StaminaMovementLogic.Axis, center: jooon.features.dojo.StaminaMovementLogic.Point, entrySide: Double) {
      val axis: jooon.features.dojo.StaminaMovementLogic.Axis
      val center: jooon.features.dojo.StaminaMovementLogic.Point
      val entrySide: Double

      init {
         this.axis = axis
         this.center = center
         this.entrySide = entrySide
      }

      public operator fun component1(): jooon.features.dojo.StaminaMovementLogic.Axis {
         return this.axis
      }

      public operator fun component2(): jooon.features.dojo.StaminaMovementLogic.Point {
         return this.center
      }

      public operator fun component3(): Double {
         return this.entrySide
      }

      fun copy(
         axis: jooon.features.dojo.StaminaMovementLogic.Axis = this.axis,
         center: jooon.features.dojo.StaminaMovementLogic.Point = this.center,
         entrySide: Double = this.entrySide
      ): jooon.features.dojo.StaminaMovementLogic.Pass {
         return StaminaMovementLogic.Pass(axis, center, entrySide)
      }

      override fun toString(): String {
         return "Pass(axis=${this.axis}, center=${this.center}, entrySide=${this.entrySide})"
      }

      override fun hashCode(): Int {
         return (this.axis.hashCode() * 31 + this.center.hashCode()) * 31 + java.lang.Double.hashCode(this.entrySide)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is StaminaMovementLogic.Pass
               && this.axis === (other as StaminaMovementLogic.Pass).axis
               && this.center == (other as StaminaMovementLogic.Pass).center
               && java.lang.Double.compare(this.entrySide, (other as StaminaMovementLogic.Pass).entrySide) == 0
            }
      }
   }

   data class PlannedHole(hole: jooon.features.dojo.StaminaMovementLogic.HoleBox,
      direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection,
      axis: jooon.features.dojo.StaminaMovementLogic.Axis,
      phase: jooon.features.dojo.StaminaMovementLogic.ApproachPhase,
      center: jooon.features.dojo.StaminaMovementLogic.Point,
      target: jooon.features.dojo.StaminaMovementLogic.Point,
      entrySide: Double,
      wallDistance: Double,
      lateralError: Double
   ) {
      val hole: jooon.features.dojo.StaminaMovementLogic.HoleBox
      val direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection
      val axis: jooon.features.dojo.StaminaMovementLogic.Axis
      val phase: jooon.features.dojo.StaminaMovementLogic.ApproachPhase
      val center: jooon.features.dojo.StaminaMovementLogic.Point
      val target: jooon.features.dojo.StaminaMovementLogic.Point
      val entrySide: Double
      val wallDistance: Double
      val lateralError: Double

      init {
         this.hole = hole
         this.direction = direction
         this.axis = axis
         this.phase = phase
         this.center = center
         this.target = target
         this.entrySide = entrySide
         this.wallDistance = wallDistance
         this.lateralError = lateralError
      }

      public operator fun component1(): jooon.features.dojo.StaminaMovementLogic.HoleBox {
         return this.hole
      }

      public operator fun component2(): jooon.features.dojo.StaminaMovementLogic.HoleDirection {
         return this.direction
      }

      public operator fun component3(): jooon.features.dojo.StaminaMovementLogic.Axis {
         return this.axis
      }

      public operator fun component4(): jooon.features.dojo.StaminaMovementLogic.ApproachPhase {
         return this.phase
      }

      public operator fun component5(): jooon.features.dojo.StaminaMovementLogic.Point {
         return this.center
      }

      public operator fun component6(): jooon.features.dojo.StaminaMovementLogic.Point {
         return this.target
      }

      public operator fun component7(): Double {
         return this.entrySide
      }

      public operator fun component8(): Double {
         return this.wallDistance
      }

      public operator fun component9(): Double {
         return this.lateralError
      }

      fun copy(
         hole: jooon.features.dojo.StaminaMovementLogic.HoleBox = this.hole,
         direction: jooon.features.dojo.StaminaMovementLogic.HoleDirection = this.direction,
         axis: jooon.features.dojo.StaminaMovementLogic.Axis = this.axis,
         phase: jooon.features.dojo.StaminaMovementLogic.ApproachPhase = this.phase,
         center: jooon.features.dojo.StaminaMovementLogic.Point = this.center,
         target: jooon.features.dojo.StaminaMovementLogic.Point = this.target,
         entrySide: Double = this.entrySide,
         wallDistance: Double = this.wallDistance,
         lateralError: Double = this.lateralError
      ): jooon.features.dojo.StaminaMovementLogic.PlannedHole {
         return StaminaMovementLogic.PlannedHole(hole, direction, axis, phase, center, target, entrySide, wallDistance, lateralError)
      }

      override fun toString(): String {
         return "PlannedHole(hole=${this.hole}, direction=${this.direction}, axis=${this.axis}, phase=${this.phase}, center=${this.center}, target=${this.target}, entrySide=${this.entrySide}, wallDistance=${this.wallDistance}, lateralError=${this.lateralError})"
      }

      override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      ((this.hole.hashCode() * 31 + this.direction.hashCode()) * 31 + this.axis.hashCode()) * 31
                                                         + this.phase.hashCode()
                                                   )
                                                   * 31
                                                + this.center.hashCode()
                                          )
                                          * 31
                                       + this.target.hashCode()
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.entrySide)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.wallDistance)
               )
               * 31
            + java.lang.Double.hashCode(this.lateralError)
         }

      override operator fun equals(other: Any?): Boolean {
         label70@
         if (this === other) {
            return true
         } else {
            return other is StaminaMovementLogic.PlannedHole
               && this.hole == (other as StaminaMovementLogic.PlannedHole).hole
               && this.direction === (other as StaminaMovementLogic.PlannedHole).direction
               && this.axis === (other as StaminaMovementLogic.PlannedHole).axis
               && this.phase === (other as StaminaMovementLogic.PlannedHole).phase
               && this.center == (other as StaminaMovementLogic.PlannedHole).center
               && this.target == (other as StaminaMovementLogic.PlannedHole).target
               && java.lang.Double.compare(this.entrySide, (other as StaminaMovementLogic.PlannedHole).entrySide) == 0
               && java.lang.Double.compare(this.wallDistance, (other as StaminaMovementLogic.PlannedHole).wallDistance) == 0
               && java.lang.Double.compare(this.lateralError, (other as StaminaMovementLogic.PlannedHole).lateralError) == 0
            }
      }
   }

   data class Point(x: Double, z: Double) {
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

      fun copy(x: Double = this.x, z: Double = this.z): jooon.features.dojo.StaminaMovementLogic.Point {
         return StaminaMovementLogic.Point(x, z)
      }

      override fun toString(): String {
         return "Point(x=${this.x}, z=${this.z})"
      }

      override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.z)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is StaminaMovementLogic.Point
               && java.lang.Double.compare(this.x, (other as StaminaMovementLogic.Point).x) == 0
               && java.lang.Double.compare(this.z, (other as StaminaMovementLogic.Point).z) == 0
            }
      }
   }
}
