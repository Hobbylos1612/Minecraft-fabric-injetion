package jooon.features.dojo

import java.io.Serializable
import java.lang.constant.Constable
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import jooon.config.Config
import jooon.features.dojo.ForceTargetPriority.BrainState
import jooon.features.dojo.ForceTargetPriority.Candidate
import jooon.features.dojo.ForceTargetPriority.Decision
import jooon.features.dojo.ForceTargetPriority.TargetBucket
import jooon.mixins.ServerboundInteractPacketAccessor
import jooon.util.PlayerController
import jooon.util.SmoothMouseAimController
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.class_2824.class_5908
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView
import net.minecraft.world.World

@SourceDebugExtension(["SMAP\nAutomaticForce.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutomaticForce.kt\njooon/features/dojo/AutomaticForce\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,1279:1\n1#2:1280\n1#2:1318\n1549#3:1281\n1620#3,3:1282\n288#3,2:1285\n1620#3,3:1305\n1603#3,9:1308\n1855#3:1317\n1856#3:1319\n1612#3:1320\n1620#3,3:1321\n1774#3,4:1338\n1774#3,4:1342\n1774#3,4:1346\n473#4:1287\n1313#4:1288\n1314#4:1296\n473#4:1304\n1839#4,14:1324\n372#5,7:1289\n372#5,7:1297\n*S KotlinDebug\n*F\n+ 1 AutomaticForce.kt\njooon/features/dojo/AutomaticForce\n*L\n439#1:1318\n231#1:1281\n231#1:1282,3\n246#1:1285,2\n436#1:1305,3\n439#1:1308,9\n439#1:1317\n439#1:1319\n439#1:1320\n640#1:1321,3\n1129#1:1338,4\n1130#1:1342,4\n1131#1:1346,4\n301#1:1287\n303#1:1288\n303#1:1296\n415#1:1304\n977#1:1324,14\n305#1:1289,7\n324#1:1297,7\n*E\n"])
public object AutomaticForce {
   private const val SYNTHETIC_TURN_OWNER: String = "dojo_automatic_force"
   private const val SEARCH_RADIUS: Double = 22.0
   private const val SAME_PLATFORM_Y_TOLERANCE: Double = 2.4
   private const val ATTACK_REACH_PAD: Double = 0.25
   private const val ATTACK_MOVE_DIST: Double = 3.75
   private const val ATTACK_COOLDOWN_TICKS: Int = 2
   private const val TARGET_STALE_TICKS: Int = 8
   private const val TARGET_AIM_Y_FACTOR: Double = 0.6
   private const val SPRINT_RESET_RELEASE_TICKS: Int = 1
   private const val COMBO_YAW_LIMIT: Float = 34.0F
   private const val POSITION_YAW_LIMIT: Float = 72.0F
   private const val PLATFORM_SCAN_RADIUS: Int = 9
   private const val EDGE_SCAN_RADIUS: Double = 8.25
   private const val EDGE_SCAN_STEP: Double = 0.35
   private const val EDGE_DIRECTION_COUNT: Int = 24
   private const val ANCHOR_REACHED_DIST: Double = 0.82
   private const val COMBO_READY_ALIGNMENT: Double = 0.62
   private const val NEGATIVE_CLUSTER_RADIUS: Double = 1.22
   private const val ABANDON_COOLDOWN_TICKS: Int = 18
   private const val TELEMETRY_INTERVAL_MS: Long = 250L
   private const val CANDIDATE_TABLE_INTERVAL_MS: Long = 250L
   private const val TICK_DIAGNOSTIC_INTERVAL_MS: Long = 250L
   private const val MOVEMENT_DIAGNOSTIC_INTERVAL_MS: Long = 180L
   private const val SWING_DIAGNOSTIC_INTERVAL_MS: Long = 120L
   private const val PROGRESS_DIAGNOSTIC_INTERVAL_MS: Long = 120L
   private const val EVENT_LOG_MIN_MS: Long = 180L
   private const val CANDIDATE_TABLE_LIMIT: Int = 7
   private const val ONE_STEP_REACH_PAD: Double = 0.95
   private const val EDGE_HOLD_DISTANCE: Double = 1.35
   private final var active: Boolean
   private final var isPaused: Boolean
   private final var wasSneaking: Boolean
   private final var currentTargetId: Int?
   private final var brainState: BrainState = ForceTargetPriority.BrainState.SCAN
   private final var lastClickTick: Int
   private final var sprintResetReleaseUntilTick: Int
   private final var lastTelemetryMs: Long
   private final var lastCandidateTableMs: Long
   private final var lastEventLogMs: Long
   private final var lastEventKey: String = ""
   private final var lastDecisionFingerprint: String = ""
   private final var lastObservedAttackEntityId: Int = -1
   private final val tracked: MutableMap<Int, jooon.features.dojo.AutomaticForce.TrackedZombie> = LinkedHashMap() as java.util.Map
   private final val lastDiagnosticByKey: MutableMap<String, Long> = LinkedHashMap() as java.util.Map

   private final val aimController: SmoothMouseAimController = SmoothMouseAimController("dojo_automatic_force", { 
      active && Config.fullyAutomaticForce && !isPaused
   })

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         INSTANCE.tick(client)
      })
   }

   fun onOutgoingPacket(packet: Packet<*>) {
      if (packet is PlayerInteractEntityC2SPacket) {
         if (active && Config.fullyAutomaticForce) {
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
               val var4: AutomaticForce = this

               var `$this$onOutgoingPacket_u24lambda_u242`: AutomaticForce
               try {
                  `$this$onOutgoingPacket_u24lambda_u242` = var4
                  `$this$onOutgoingPacket_u24lambda_u242` = (AutomaticForce)Result.constructor_impl/* $VF was: constructor-impl */(
                     (packet as ServerboundInteractPacketAccessor).jooonEntityId()
                  )
               } catch (var7: java.lang.Throwable) {
                  `$this$onOutgoingPacket_u24lambda_u242` = (AutomaticForce)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7))
               }

               val entityId: Int = ((
                     if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$onOutgoingPacket_u24lambda_u242`))
                        -1
                        else
                        `$this$onOutgoingPacket_u24lambda_u242`
                  ) as java.lang.Number)
                  .intValue()
                  if (entityId >= 0) {
                  lastObservedAttackEntityId = entityId
               }
            }
         }
      }
   }

   fun tick(client: MinecraftClient) {
      if (!Config.fullyAutomaticForce || !AutoDojo.INSTANCE.isChallengeActive(AutoDojo.Challenge.FORCE)) {
         if (active) {
            this.reset()
         }
      } else {
         val player: ClientPlayerEntity = client.field_1724
         val level: ClientWorld = client.field_1687
         if (client.field_1724 != null && client.field_1687 != null && !AutoDojo.INSTANCE.isAutomationBlockedByScreen()) {
            if (!active) {
               active = true
               isPaused = false
               wasSneaking = false
               currentTargetId = null
               brainState = ForceTargetPriority.BrainState.SCAN
               lastObservedAttackEntityId = -1
               sprintResetReleaseUntilTick = 0
               lastCandidateTableMs = 0L
               lastDecisionFingerprint = ""
               lastDiagnosticByKey.clear()
               tracked.clear()
               AutoDojo.INSTANCE.subtitle = "§7[§cSneak to pause§7]"
               this.logEvent("start", "state=SCAN reason=force_active diagnostics=dense")
            }

            if (player.method_5715() && !wasSneaking) {
               isPaused = !isPaused
               if (isPaused) {
                  AutoDojo.INSTANCE.subtitle = "§7[§cPAUSED§7]"
                  currentTargetId = null
                  this.clearAim()
                  this.stopComboMovement()
                  this.logEvent("pause", "state=${ForceTargetPriority.BrainState.SCAN} reason=paused")
               } else {
                  AutoDojo.INSTANCE.subtitle = "§7[§cSneak to pause§7]"
                  this.logEvent("resume", "state=${brainState} reason=resumed")
               }
            }

            wasSneaking = player.method_5715()
            if (!isPaused) {
               if (AutoDojo.INSTANCE.subtitle.length() == 0 || StringsKt.contains$default(AutoDojo.INSTANCE.subtitle, "PAUSED", false, 2, null)) {
                  AutoDojo.INSTANCE.subtitle = "§7[§cSneak to pause§7]"
               }

               val nowMs: Long = System.currentTimeMillis()
               this.scanForceZombies(level, player, nowMs)
               val platform: AutomaticForce.PlatformSnapshot = this.detectPlatform(level as World, player)
               this.consumeObservedAttack(player, nowMs)
               val snapshot: AutomaticForce.ForceSnapshot = this.buildForceSnapshot(level, player, nowMs, platform)
               val var10000: ForceTargetPriority = ForceTargetPriority.INSTANCE
               val runtime: java.lang.Iterable = snapshot.candidates
               val selected: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(runtime, 10))

               for (`$i$f$firstOrNull` in runtime) {
                  selected.add((`$i$f$firstOrNull` as AutomaticForce.RuntimeCandidate).priority)
               }

               val decision: ForceTargetPriority.Decision = var10000.select(
                  selected as MutableList<ForceTargetPriority.Candidate>,
                  nowMs,
                  Config.forcePreventNegative,
                  currentTargetId,
                  player.field_6012 <= sprintResetReleaseUntilTick
               )
               this.logTickDiagnostics(player, platform, snapshot, decision, nowMs)
               this.logDecisionDiagnostics(currentTargetId, decision, snapshot, nowMs)
               this.logCandidateTable(snapshot, decision, nowMs)
               if (decision.state === ForceTargetPriority.BrainState.ABANDON) {
                  this.markCurrentTargetAbandoned(player, decision.reason)
               }

               val var32: ForceTargetPriority.Candidate = decision.selected
               val var34: AutomaticForce.RuntimeCandidate
               if (var32 != null) {
                  val var26: ForceTargetPriority.Candidate = var32

                  run label173@{
                     for (var31 in snapshot.candidates) {
                        if ((var31 as AutomaticForce.RuntimeCandidate).getZombie().method_5628() == var26.id) {
                           var33 = var31
                           return@label173
                        }
                     }

                     var33 = null
                  }

                  var34 = var33 as AutomaticForce.RuntimeCandidate
               } else {
                  var34 = null
               }

               if (var34 == null) {
                  currentTargetId = null
                  if (!this.tryRecenter(level, player, platform, "no_target:${decision.reason}")) {
                     brainState = ForceTargetPriority.BrainState.SCAN
                     this.clearAim()
                     this.stopComboMovement()
                     this.logTelemetry(null, brainState, decision.reason, null)
                  }
               } else {
                  if (currentTargetId == null || currentTargetId != var34.getZombie().method_5628()) {
                     this.beginTarget(player, nowMs, var34, decision.reason)
                  }

                  val var25: ForceTargetPriority.BrainState = if (player.field_6012 <= sprintResetReleaseUntilTick
      && decision.bucket != ForceTargetPriority.TargetBucket.FINISH_NOW)
   ForceTargetPriority.BrainState.RESET_SPRINT
   else
   (if (ForceTargetPriority.INSTANCE.shouldReposition(var34.priority)) ForceTargetPriority.BrainState.POSITION else ForceTargetPriority.BrainState.COMBO)
   brainState = var25
                  when (AutomaticForce.WhenMappings.$EnumSwitchMapping$0[var25.ordinal()]) {
                     1 -> {
                        this.aimAtZombie(player, var34.getZombie())
                        this.driveSprintReset(player)
                     }
                     2 -> this.positionForLane(level, player, var34)
                     3 -> {
                        this.aimAtZombie(player, var34.getZombie())
                        this.driveComboMovement(level, player, var34)
                        this.tryAttack(player, var34, snapshot.negatives)
                     }
                     4, 5, 6 -> {}
                     else -> throw NoWhenBranchMatchedException()
                  }

                  this.logTelemetry(var34, var25, decision.reason, decision.bucket)
               }
            }
         } else {
            this.clearAim()
            this.stopComboMovement()
         }
      }
   }

   fun consumeObservedAttack(player: ClientPlayerEntity, nowMs: Long) {
      val attackedId: Int = lastObservedAttackEntityId
      if (lastObservedAttackEntityId >= 0) {
         lastObservedAttackEntityId = -1
         val `$this$getOrPut$iv`: java.util.Map = tracked
         val `key$iv`: Any = attackedId
         val `value$iv`: Any = `$this$getOrPut$iv`.get(`key$iv`)
         val var10000: Any
         if (`value$iv` == null) {
            val `answer$iv`: Any = AutomaticForce.TrackedZombie(
               nowMs + 10100L, player.field_6012, 0, 0L, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0, 0, 0.0, 0, false, 131068, null
            )
            `$this$getOrPut$iv`.put(`key$iv`, `answer$iv`)
            var10000 = `answer$iv`
         } else {
            var10000 = `value$iv`
         }

         val state: AutomaticForce.TrackedZombie = var10000 as AutomaticForce.TrackedZombie
         (var10000 as AutomaticForce.TrackedZombie).expiresAtMs = nowMs + 10100L
         state.lastSeenTick = player.field_6012
         state.hitCount = state.hitCount + 1
         state.lastHitTick = player.field_6012
         state.pendingHitTick = player.field_6012
         state.pendingHitEdgeDistance = state.lastEdgeDistance
         if (state.comboStartedTick < 0) {
            state.comboStartedTick = player.field_6012
            state.comboStartedMs = nowMs
         }

         if (currentTargetId != null && currentTargetId == attackedId) {
            sprintResetReleaseUntilTick = player.field_6012 + 1
         }

         val var10001: java.lang.String = "hit:$attackedId:${state.hitCount}"
         val var10002: ForceTargetPriority.BrainState = brainState
         val var10004: Int = state.hitCount
         val var10005: Int = state.lastHitTick
         val var10006: java.lang.String = this.fmt(state.pendingHitEdgeDistance)
         val var10007: java.lang.String = this.fmt(state.bestEdgeDistance)
         var var10009: Serializable = currentTargetId
         if (currentTargetId == null) {
            var10009 = "none"
         }

         this.logEvent(
            var10001,
            "state=$var10002 target=$attackedId hits=$var10004 lastConfirmedAttack=$var10005 edgeAtHit=$var10006 bestEdge=$var10007 resetUntil=${sprintResetReleaseUntilTick} current=$var10009"
         )
      }
   }

   private fun updateEdgeProgress(id: Int, state: jooon.features.dojo.AutomaticForce.TrackedZombie, edgeDistance: Double, tick: Int) {
      if (!java.lang.Double.isInfinite(edgeDistance) && !java.lang.Double.isNaN(edgeDistance)) {
         var var18: Double = state.lastEdgeDistance
         if (java.lang.Double.isInfinite(var18) || java.lang.Double.isNaN(var18)) {
            state.lastEdgeDistance = edgeDistance
            state.bestEdgeDistance = edgeDistance
            state.lastProgressTick = tick
            this.logDiagnostic("PROGRESS", "progress:init:$id", 500L, { 
               "id=$`$id` init edge=${INSTANCE.fmt(`$edgeDistance`)} best=${INSTANCE.fmt(`$state`.bestEdgeDistance)} tick=$`$tick`"
            })
         } else {
            var18 = state.lastEdgeDistance
            val previousBest: Double = state.bestEdgeDistance
            state.edgeVelocity = state.lastEdgeDistance - edgeDistance
            var bestImproved: Boolean = false
            val pendingResult: Double = state.bestEdgeDistance
            if (java.lang.Double.isInfinite(pendingResult) || java.lang.Double.isNaN(pendingResult) || edgeDistance < state.bestEdgeDistance - 0.12) {
               state.bestEdgeDistance = edgeDistance
               state.lastProgressTick = tick
               bestImproved = true
            }

            val var20: ObjectRef = ObjectRef()
            if (state.pendingHitTick >= 0 && tick - state.pendingHitTick >= 4) {
               val edgeJump: Double = state.pendingHitEdgeDistance
               val hitProgress: Double = if (!java.lang.Double.isInfinite(edgeJump) && !java.lang.Double.isNaN(edgeJump))
                  edgeJump - edgeDistance
                  else
                  state.edgeVelocity
                  if (!(hitProgress >= 0.18) && !(edgeDistance <= 2.25)) {
                  if (tick - state.pendingHitTick >= 6) {
                     state.consecutiveBadHits = state.consecutiveBadHits + 1
                     var20.element = "bad hitProgress=${this.fmt(hitProgress)}"
                  }
               } else {
                  state.consecutiveBadHits = 0
                  state.lastUsefulHitTick = state.pendingHitTick
                  state.lastProgressTick = tick
                  var20.element = "useful hitProgress=${this.fmt(hitProgress)}"
               }

               if (hitProgress >= 0.18 || tick - state.pendingHitTick >= 6) {
                  state.pendingHitTick = -1
                  state.pendingHitEdgeDistance = java.lang.Double.NaN
               }
            }

            state.lastEdgeDistance = edgeDistance
            if (bestImproved || var20.element != null || Math.abs(var18 - edgeDistance) >= 0.7 || edgeDistance <= 1.35) {
               this.logDiagnostic(
                  "PROGRESS",
                  "progress:$id",
                  120L,
                  { 
                     val var10001: java.lang.String = INSTANCE.fmt(`$edgeDistance`)
                     val var10002: java.lang.String = INSTANCE.fmt(`$previousEdge`)
                     val var10003: java.lang.String = INSTANCE.fmt(`$previousEdge` - `$edgeDistance`)
                     val var10004: java.lang.String = INSTANCE.fmt(`$state`.bestEdgeDistance)
                     val var10005: java.lang.String = INSTANCE.fmt(`$previousBest`)
                     val var10006: java.lang.String = INSTANCE.fmt(`$state`.edgeVelocity)
                     var var10007: java.lang.String = `$pendingResult`.element as java.lang.String
                     if (`$pendingResult`.element as java.lang.String == null) {
                        var10007 = "none"
                     }

                     "id=$`$id` edge=$var10001 prev=$var10002 delta=$var10003 best=$var10004 prevBest=$var10005 velocity=$var10006 pending=$var10007 badHits=${`$state`.consecutiveBadHits} lastProgress=${`$state`.lastProgressTick} lastUsefulHit=${`$state`.lastUsefulHitTick}"
                  }
               )
            }
         }
      }
   }

   fun beginTarget(player: ClientPlayerEntity, nowMs: Long, runtime: AutomaticForce.RuntimeCandidate, reason: java.lang.String) {
      val previousTargetId: Int = currentTargetId
      currentTargetId = runtime.getZombie().method_5628()
      val var10000: AutomaticForce.TrackedZombie = tracked.get(runtime.getZombie().method_5628())
      if (var10000 != null) {
         if (var10000.hardAbandoned || var10000.comboStartedTick < 0) {
            var10000.comboStartedTick = player.field_6012
            var10000.comboStartedMs = nowMs
            var10000.comboStartEdgeDistance = runtime.edge.distance
            var10000.hitCount = 0
            var10000.lastHitTick = -1
            var10000.lastUsefulHitTick = -1
            var10000.consecutiveBadHits = 0
            var10000.pendingHitTick = -1
            var10000.pendingHitEdgeDistance = java.lang.Double.NaN
            var10000.hardAbandoned = false
         }
      }

      var var23: AutomaticForce = this
      val var10001: java.lang.String = "select:${runtime.getZombie().method_5628()}"
      val var10002: ForceTargetPriority.BrainState = brainState
      var var10003: Serializable = previousTargetId
      if (previousTargetId == null) {
         var10003 = "none"
      }

      var var24: java.lang.String
      var var10004: Int
      var var10005: ForceTargetPriority.TargetBucket
      var var10006: Int
      var var10007: java.lang.String
      var var10008: java.lang.String
      var var10009: java.lang.String
      var var10010: java.lang.String
      run label36@{
         var10004 = runtime.getZombie().method_5628()
         var10005 = ForceTargetPriority.INSTANCE.bucketFor(runtime.priority, currentTargetId)
         var10006 = runtime.priority.pointValue
         var10007 = reason
         var10008 = this.fmt(runtime.edge.distance)
         var10009 = this.fmt(runtime.priority.bestEdgeDistance)
         var10010 = this.fmt(runtime.lane.alignment)
         val var10011: Vec3d = runtime.lane.getAnchor()
         if (var10011 != null) {
            val var21: java.lang.String = INSTANCE.fmtVec(var10011)
            var23 = this
            var10007 = reason
            var24 = var21
            if (var21 != null) {
               return@label36
            }
         }

         var24 = "none"
      }

      var23.logEvent(
         var10001,
         "state=$var10002 prev=$var10003 target=$var10004 bucket=$var10005 value=$var10006 reason=$var10007 edge=$var10008 bestEdge=$var10009 lane=$var10010 anchor=$var24 anchorDist=${this.fmt(
            runtime.lane.anchorDistance
         )}"
      )
   }

   fun markCurrentTargetAbandoned(player: ClientPlayerEntity, reason: java.lang.String) {
      if (currentTargetId != null) {
         val id: Int = currentTargetId
         val var10000: AutomaticForce.TrackedZombie = tracked.get(id)
         if (var10000 != null) {
            var10000.abandonedUntilTick = player.field_6012 + 18
            var10000.hardAbandoned = true
         }

         val statex: AutomaticForce.TrackedZombie = tracked.get(id)
         this.logEvent(
            "abandon:$id",
            "state=${ForceTargetPriority.BrainState.ABANDON} target=$id reason=$reason hits=${if (statex != null) statex.hitCount else 0} bestEdge=${this.fmt(
               if (statex != null) statex.bestEdgeDistance else java.lang.Double.NaN
            )} lastEdge=${this.fmt(if (statex != null) statex.lastEdgeDistance else java.lang.Double.NaN)} velocity=${this.fmt(
               if (statex != null) statex.edgeVelocity else java.lang.Double.NaN
            )} badHits=${if (statex != null) statex.consecutiveBadHits else 0} cooldownUntil=${if (statex != null) statex.abandonedUntilTick else 0}"
         )
      }
   }

   fun isPotentialForceTarget(player: ClientPlayerEntity, zombie: ZombieEntity): Boolean {
      zombie.method_5805()
         && !zombie.method_31481()
         && !(player.method_5858(zombie as Entity) > 484.0)
         && !(zombie.method_23318() < player.method_23318() - 1.2)
         && Math.abs(zombie.method_23318() - player.method_23318()) <= 2.4
      }

   fun aimAtZombie(player: ClientPlayerEntity, target: ZombieEntity) {
      val aimPos: Vec3d = Vec3d(target.method_23317(), target.method_23318() + target.method_17682() * 0.6, target.method_23321())
      val var4: Pair = this.aimAngles(player, aimPos)
      val targetYaw: Float = (var4.component1() as java.lang.Number).floatValue()
      val targetPitch: Float = (var4.component2() as java.lang.Number).floatValue()
      this.logDiagnostic(
         "AIM",
         "aim:zombie:${target.method_5628()}",
         180L,
         { 
            "mode=zombie target=${`$target`.method_5628()} aim=${INSTANCE.fmtVec(`$aimPos`)} yaw=${INSTANCE.fmt((double)`$targetYaw`)} pitch=${INSTANCE.fmt(
               (double)`$targetPitch`
            )} yawError=${INSTANCE.fmt((double)Math.abs(MathHelper.method_15393(`$targetYaw` - `$player`.method_36454())))} pitchError=${INSTANCE.fmt(
               (double)Math.abs(`$targetPitch` - `$player`.method_36455())
            )}"
         }
      )
      this.requestAim(player, targetYaw, targetPitch, 28, 72)
   }

   fun aimAtPoint(player: ClientPlayerEntity, point: Vec3d, baseDurationMs: Int) {
      val var4: Pair = this.aimAngles(player, point)
      val targetYaw: Float = (var4.component1() as java.lang.Number).floatValue()
      val targetPitch: Float = (var4.component2() as java.lang.Number).floatValue()
      this.logDiagnostic(
         "AIM",
         "aim:point",
         180L,
         { 
            "mode=point point=${INSTANCE.fmtVec(`$point`)} yaw=${INSTANCE.fmt((double)`$targetYaw`)} pitch=${INSTANCE.fmt((double)`$targetPitch`)} yawError=${INSTANCE.fmt(
               (double)Math.abs(MathHelper.method_15393(`$targetYaw` - `$player`.method_36454()))
            )} pitchError=${INSTANCE.fmt((double)Math.abs(`$targetPitch` - `$player`.method_36455()))}"
         }
      )
      this.requestAim(player, targetYaw, targetPitch, baseDurationMs, 96)
   }

   fun requestAim(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float, baseDurationMs: Int, maxDurationMs: Int) {
      aimController.request(player, targetYaw, targetPitch, baseDurationMs, maxDurationMs, 2.6F, 8L)
   }

   fun tryAttack(player: ClientPlayerEntity, runtime: AutomaticForce.RuntimeCandidate, negatives: MutableList<AutomaticForce.NegativeZombie>) {
      val target: ZombieEntity = runtime.getZombie()
      if (player.field_6012 <= sprintResetReleaseUntilTick) {
         this.logSwingDecision(player, runtime, "blocked_reset_window")
      } else if (player.field_6012 - lastClickTick < 2) {
         this.logSwingDecision(player, runtime, "blocked_click_cooldown:${player.field_6012 - lastClickTick}")
      } else if (!this.isNearAttackRange(player, target)) {
         this.logSwingDecision(player, runtime, "blocked_range")
      } else {
         val var5: java.lang.String = this.swingBlockReason(player, runtime, negatives)
         if (var5 != null) {
            INSTANCE.logSwingDecision(player, runtime, "blocked_$var5")
         } else if (!this.isCrosshairOnTarget(target)) {
            this.logSwingDecision(player, runtime, "blocked_crosshair:${this.crosshairDebug()}")
         } else {
            if (PlayerController.INSTANCE.leftClick()) {
               lastClickTick = player.field_6012
               this.logSwingDecision(player, runtime, "attack_request")
            }
         }
      }
   }

   fun swingBlockReason(player: ClientPlayerEntity, runtime: AutomaticForce.RuntimeCandidate, negatives: MutableList<AutomaticForce.NegativeZombie>): java.lang.String {
      val candidate: ForceTargetPriority.Candidate = runtime.priority
      if (!candidate.negative && candidate.pointValue > 0) {
         val coneRisk: java.lang.Iterable = negatives
         val `destination$iv`: java.util.Collection = LinkedHashSet()

         for (`item$iv` in coneRisk) {
            `destination$iv`.add((`item$iv` as AutomaticForce.NegativeZombie).id)
         }

         val var14: Int = this.negativeUnderCrosshair(`destination$iv` as MutableSet<Int>)
         if (var14 != null) {
            "crosshair_negative:${var14.intValue()}"
         } else if (ForceTargetPriority.negativeConeRisk$default(
               ForceTargetPriority.INSTANCE,
               ForceTargetPriority.Point3(player.method_23317(), player.method_23320(), player.method_23321()),
               ForceTargetPriority.Point3(
                  runtime.getZombie().method_23317(),
                  runtime.getZombie().method_23318() + (double)runtime.getZombie().method_17682() * 0.6,
                  runtime.getZombie().method_23321()
               ),
               SequencesKt.toList(SequencesKt.map(SequencesKt.filter(CollectionsKt.asSequence(negatives), { it: AutomaticForce.NegativeZombie ->
                  it.id != `$runtime`.getZombie().method_5628()
               }), { it: AutomaticForce.NegativeZombie ->
                  ForceTargetPriority.Point3(it.getPos().field_1352, it.getPos().field_1351, it.getPos().field_1350)
               })),
               0.0,
               8,
               null
            )
            != ForceTargetPriority.NegativeRisk.NONE) {
            "swept_cone"
         } else {
            if (!ForceTargetPriority.INSTANCE.isSwingEligible(candidate))
               "lane_or_negative_risk:${candidate.negativeRisk}:${this.fmt(candidate.laneAlignment)}"
               else
               null
            }
      } else {
         "selected_negative"
      }
   }

   fun isNearAttackRange(player: ClientPlayerEntity, target: ZombieEntity): Boolean {
      player.method_76729(target.method_5829(), 0.25) && player.method_5739(target as Entity) <= 3.75
   }

   fun isOneStepReachable(level: World, player: ClientPlayerEntity, target: ZombieEntity): Boolean {
      if (this.isNearAttackRange(player, target)) {
         true
      } else {
         label21@
         if (player.method_5739(target as Entity) > 4.7) {
            false
         } else {
            val var10000: Pair = this.horizontalDirection(player.method_23317(), player.method_23321(), target.method_23317(), target.method_23321())
            var10000 != null
               && this.isMoveDirectionSafe(
                  level,
                  player,
                  (var10000.getFirst() as java.lang.Number).doubleValue(),
                  (var10000.getSecond() as java.lang.Number).doubleValue(),
                  false,
                  0.95,
                  0.12
               )
            }
      }
   }

   fun isCrosshairOnTarget(target: ZombieEntity): Boolean {
      val var3: HitResult = this.getMc().field_1765
      (var3 as? EntityHitResult) != null && (var3 as? EntityHitResult).method_17782().method_5628() == target.method_5628()
   }

   private fun crosshairDebug(): String {
      val entityName: HitResult = this.getMc().field_1765
      val var10000: EntityHitResult = entityName as? EntityHitResult
      if ((entityName as? EntityHitResult) == null) {
         return "none"
      } else {
         val var12: Entity = var10000.method_17782()
         val entity: Entity = var12
         val var4: AutomaticForce = this

         var `$this$crosshairDebug_u24lambda_u2432`: AutomaticForce
         try {
            `$this$crosshairDebug_u24lambda_u2432` = var4
            `$this$crosshairDebug_u24lambda_u2432` = (AutomaticForce)Result.constructor_impl/* $VF was: constructor-impl */(entity.method_5477().getString())
         } catch (var7: java.lang.Throwable) {
            `$this$crosshairDebug_u24lambda_u2432` = (AutomaticForce)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7))
         }

         val var13: Any = if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$crosshairDebug_u24lambda_u2432`))
            var12.method_5864().toString()
            else
            `$this$crosshairDebug_u24lambda_u2432`
            return "${var12.method_5628()}:${var12.getClass().getSimpleName()}:${var13 as java.lang.String}"
      }
   }

   private fun negativeUnderCrosshair(negativeIds: Set<Int>): Int? {
      val var4: HitResult = this.getMc().field_1765
      val var10000: EntityHitResult = var4 as? EntityHitResult
      if ((var4 as? EntityHitResult) == null) {
         return null
      } else {
         val var5: Entity = var10000.method_17782()
         return if (var5 is ZombieEntity && negativeIds.contains((var5 as ZombieEntity).method_5628())) (var5 as ZombieEntity).method_5628() else null
      }
   }

   fun driveSprintReset(player: ClientPlayerEntity) {
      this.logDiagnostic("MOVE", "move:reset", 180L, { 
         var var10002: Serializable = currentTargetId
         if (currentTargetId == null) {
            var10002 = "none"
         }

         "mode=sprint_reset tick=${`$player`.field_6012} releaseUntil=${sprintResetReleaseUntilTick} current=$var10002"
      })
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressSprint(false)
      player.method_5728(false)
   }

   fun driveComboMovement(level: ClientWorld, player: ClientPlayerEntity, runtime: AutomaticForce.RuntimeCandidate) {
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
      val target: ZombieEntity = runtime.getZombie()
      val dir: Pair = this.horizontalDirection(player.method_23317(), player.method_23321(), target.method_23317(), target.method_23321())
      if (dir == null) {
         this.logMovement("combo_stop", player, runtime, "reason=no_dir")
         PlayerController.INSTANCE.pressForward(false)
         PlayerController.INSTANCE.pressSprint(false)
         player.method_5728(false)
      } else if (runtime.priority.laneAlignment < 0.62) {
         this.logMovement(
            "combo_stop",
            player,
            runtime,
            "reason=lane_alignment lane=${this.fmt(runtime.priority.laneAlignment)} required=${this.fmt(0.62)} laneReason=${runtime.lane.reason}"
         )
         PlayerController.INSTANCE.pressForward(false)
         PlayerController.INSTANCE.pressSprint(false)
         player.method_5728(false)
      } else if (runtime.edge.distance <= 1.35) {
         this.logMovement(
            "combo_stop",
            player,
            runtime,
            "reason=edge_hold edge=${this.fmt(runtime.edge.distance)} hold=${this.fmt(1.35)} lane=${this.fmt(runtime.priority.laneAlignment)}"
         )
         PlayerController.INSTANCE.pressForward(false)
         PlayerController.INSTANCE.pressSprint(false)
         player.method_5728(false)
      } else {
         val targetYaw: Float = this.yawTo((dir.getFirst() as java.lang.Number).doubleValue(), (dir.getSecond() as java.lang.Number).doubleValue())
         val yawError: Float = Math.abs(MathHelper.method_15393(targetYaw - player.method_36454()))
         val safeForward: Boolean = yawError <= 34.0F
            && this.isMoveDirectionSafe(
               level as World, player, (dir.getFirst() as java.lang.Number).doubleValue(), (dir.getSecond() as java.lang.Number).doubleValue(), true, 1.55, 0.1
            )
            this.logMovement(
            "combo_forward",
            player,
            runtime,
            "targetYaw=${this.fmt((double)targetYaw)} yawError=${this.fmt((double)yawError)} safeForward=$safeForward dir=${this.fmt(
               (dir.getFirst() as java.lang.Number).doubleValue()
            )},${this.fmt((dir.getSecond() as java.lang.Number).doubleValue())} edge=${this.fmt(runtime.edge.distance)} lane=${this.fmt(
               runtime.priority.laneAlignment
            )}"
         )
         PlayerController.INSTANCE.pressForward(safeForward)
         PlayerController.INSTANCE.pressSprint(safeForward)
         player.method_5728(safeForward)
      }
   }

   fun tryRecenter(level: ClientWorld, player: ClientPlayerEntity, platform: AutomaticForce.PlatformSnapshot, reason: java.lang.String): Boolean {
      if (platform == null) {
         false
      } else {
         val var10001: World = level as World
         val var10002: Vec3d = player.method_73189()
         val playerEdge: AutomaticForce.EdgeInfo = this.nearestEdgeInfo(var10001, var10002, platform)
         val var9: Vec3d = player.method_73189()
         val centerDist: Double = this.horizontalDistance(var9, platform.getCenter())
         if (!(playerEdge.distance >= 1.55) && !(centerDist <= 1.05)) {
            brainState = ForceTargetPriority.BrainState.RECENTER
            this.aimAtPoint(player, Vec3d(platform.getCenter().field_1352, player.method_23320(), platform.getCenter().field_1350), 44)
            this.moveTowardPoint(level as World, player, platform.getCenter(), false, "recenter")
            this.logTelemetry(null, brainState, "$reason recenter edge=${this.fmt(playerEdge.distance)} center=${this.fmt(centerDist)}", null)
            true
         } else {
            false
         }
      }
   }

   fun moveTowardPoint(level: World, player: ClientPlayerEntity, point: Vec3d, allowSprint: Boolean, context: java.lang.String) {
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
      val dir: Pair = this.horizontalDirection(player.method_23317(), player.method_23321(), point.field_1352, point.field_1350)
      val var10001: Vec3d = player.method_73189()
      val dist: Double = this.horizontalDistance(var10001, point)
      if (dir != null && !(dist <= 0.82)) {
         val targetYaw: Float = this.yawTo((dir.getFirst() as java.lang.Number).doubleValue(), (dir.getSecond() as java.lang.Number).doubleValue())
         val yawError: Float = Math.abs(MathHelper.method_15393(targetYaw - player.method_36454()))
         val canWalk: Boolean = yawError <= 72.0F
            && this.isMoveDirectionSafe(
               level, player, (dir.getFirst() as java.lang.Number).doubleValue(), (dir.getSecond() as java.lang.Number).doubleValue(), false, 1.25, 0.16
            )
            val canSprint: Boolean = allowSprint
            && dist > 2.1
            && yawError <= 38.0F
            && this.isMoveDirectionSafe(
               level, player, (dir.getFirst() as java.lang.Number).doubleValue(), (dir.getSecond() as java.lang.Number).doubleValue(), true, 2.05, 0.12
            )
            this.logDiagnostic(
            "MOVE",
            "move:$context",
            180L,
            { 
               "mode=$`$context` point=${INSTANCE.fmtVec(`$point`)} dist=${INSTANCE.fmt(`$dist`)} targetYaw=${INSTANCE.fmt((double)`$targetYaw`)} yawError=${INSTANCE.fmt(
                  (double)`$yawError`
               )} canWalk=$`$canWalk` canSprint=$`$canSprint` allowSprint=$`$allowSprint` dir=${INSTANCE.fmt(
                  (`$dir`.getFirst() as java.lang.Number).doubleValue()
               )},${INSTANCE.fmt((`$dir`.getSecond() as java.lang.Number).doubleValue())}"
            }
         )
         PlayerController.INSTANCE.pressForward(canWalk || canSprint)
         PlayerController.INSTANCE.pressSprint(canSprint)
         player.method_5728(canSprint)
      } else {
         this.logDiagnostic("MOVE", "move:$context", 180L, { 
            "mode=$`$context` stop reason=${if (`$dir` == null) "no_dir" else "arrived"} point=${INSTANCE.fmtVec(`$point`)} dist=${INSTANCE.fmt(`$dist`)}"
         })
         PlayerController.INSTANCE.pressForward(false)
         PlayerController.INSTANCE.pressSprint(false)
         player.method_5728(false)
      }
   }

   fun detectPlatform(level: World, player: ClientPlayerEntity): AutomaticForce.PlatformSnapshot {
      val var10000: Int = this.detectFloorY(level, player)
      if (var10000 == null) {
         null
      } else {
         val floorY: Int = var10000
         val px: Int = MathHelper.method_15357(player.method_23317())
         val pz: Int = MathHelper.method_15357(player.method_23321())
         var samples: Int = 0
         var sumX: Double = 0.0
         var sumZ: Double = 0.0
         var minX: Int = Integer.MAX_VALUE
         var maxX: Int = Integer.MIN_VALUE
         var minZ: Int = Integer.MAX_VALUE
         var maxZ: Int = Integer.MIN_VALUE

         for (dx in -9..9) {
            for (dz in -9..9) {
               val x: Int = px + dx
               val z: Int = pz + dz
               if (this.hasSolidFloor(level, x, floorY, pz + dz)) {
                  samples++
                  sumX += x + 0.5
                  sumZ += z + 0.5
                  minX = Math.min(minX, x)
                  maxX = Math.max(maxX, x)
                  minZ = Math.min(minZ, z)
                  maxZ = Math.max(maxZ, z)
               }
            }
         }

         if (samples < 12)
            null
            else
            AutomaticForce.PlatformSnapshot(Vec3d(sumX / samples, player.method_23318(), sumZ / samples), floorY, minX, maxX, minZ, maxZ, samples)
         }
   }

   fun nearestEdgeInfo(level: World, position: Vec3d, platform: AutomaticForce.PlatformSnapshot): AutomaticForce.EdgeInfo {
      var bestDirX: Double = 0.0
      var bestDirZ: Double = 0.0
      var bestDistance: Double = java.lang.Double.POSITIVE_INFINITY

      repeat(23) { center ->
         val player: Double = Math.cos((Math.PI * 2) * (double)center / (double)24)
         val dirZ: Double = Math.sin((Math.PI * 2) * (double)center / (double)24)
         val edgeDistance: Double = this.distanceToOpenFloor(level, position, player, dirZ)
         if (edgeDistance < bestDistance) {
            bestDistance = edgeDistance
            bestDirX = player
            bestDirZ = dirZ
         }
      }

      if (!java.lang.Double.isInfinite(bestDistance) && !java.lang.Double.isNaN(bestDistance)) {
         AutomaticForce.EdgeInfo(bestDirX, bestDirZ, bestDistance, true)
      } else {
         val var20: Vec3d = if (platform != null) platform.getCenter() else null
         var var10000: Pair
         if (var20 != null) {
            var10000 = this.horizontalDirection(var20.field_1352, var20.field_1350, position.field_1352, position.field_1350)
         } else {
            val var22: ClientPlayerEntity = this.getMc().field_1724
            var10000 = if (var22 != null)
               this.horizontalDirection(var22.method_23317(), var22.method_23321(), position.field_1352, position.field_1350)
               else
               null
            }

         if (var10000 == null) {
            var10000 = TuplesKt.to(0.0, 1.0)
         }

         AutomaticForce.EdgeInfo((var10000.getFirst() as java.lang.Number).doubleValue(), (var10000.getSecond() as java.lang.Number).doubleValue(), 8.25, false)
      }
   }

   fun distanceToOpenFloor(level: World, position: Vec3d, dirX: Double, dirZ: Double): Double {
      // $VF: Unable to resugar Kotlin loop from Java for loop
      var probe: Double = 0.45
      while (true) {
         if (probe <= 8.25) break
         if (!this.hasWalkableFloor(level, position.field_1352 + dirX * probe, position.field_1351, position.field_1350 + dirZ * probe)) {
            probe
         }

         probe += 0.35
      }

      java.lang.Double.POSITIVE_INFINITY
   }

   fun computePushLane(level: World, player: ClientPlayerEntity, target: ZombieEntity, edge: AutomaticForce.EdgeInfo): AutomaticForce.LaneInfo {
      val var10000: ForceTargetPriority.PushLane = ForceTargetPriority.INSTANCE
         .choosePushLane(
            ForceTargetPriority.Point2(player.method_23317(), player.method_23321()),
            ForceTargetPriority.Point2(target.method_23317(), target.method_23321()),
            CollectionsKt.listOf(ForceTargetPriority.EdgeOption(ForceTargetPriority.Point2(edge.dirX, edge.dirZ), edge.distance))
         )
         if (var10000 == null) {
         AutomaticForce.LaneInfo(false, -1.0, null, java.lang.Double.POSITIVE_INFINITY, "no_lane")
      } else {
         val anchor: Vec3d = this.chooseSafeAnchor(level, player, target, var10000.edgeDirection)
         val var16: Double
         if (anchor != null) {
            val var15: AutomaticForce = INSTANCE
            val var10001: Vec3d = player.method_73189()
            var16 = var15.horizontalDistance(var10001, anchor)
         } else {
            var16 = java.lang.Double.POSITIVE_INFINITY
         }

         run label51@{
            if (anchor != null) {
               val reason: Double = edge.distance
               if (!java.lang.Double.isInfinite(reason) && !java.lang.Double.isNaN(reason) && edge.distance <= 8.25) {
                  var17 = true
                  return@label51
               }
            }

            var17 = false
         }

         AutomaticForce.LaneInfo(
            var17, var10000.alignment, anchor, var16, if (!var17) "no_safe_anchor" else (if (var10000.alignment < 0.62) "wrong_side" else "ready")
         )
      }
   }

   fun chooseSafeAnchor(level: World, player: ClientPlayerEntity, target: ZombieEntity, edgeDirection: ForceTargetPriority.Point2): Vec3d {
      val dirLen: Double = Math.hypot(edgeDirection.x, edgeDirection.z)
      if (dirLen < 1.0E-5) {
         null
      } else {
         val dirX: Double = edgeDirection.x / dirLen
         val dirZ: Double = edgeDirection.z / dirLen
         val sideX: Double = -dirZ
         val sideZ: Double = dirX
         val options: java.util.List = ArrayList()
         val `$this$minByOrNull$iv`: java.util.Iterator = CollectionsKt.listOf(arrayOf(2.25, 2.65, 3.05)).iterator()

         while (`$this$minByOrNull$iv`.hasNext()) {
            val var27: Double = (`$this$minByOrNull$iv`.next() as java.lang.Number).doubleValue()
            val `minElem$iv`: java.util.Iterator = CollectionsKt.listOf(arrayOf(0.0, -0.78, 0.78, -1.18, 1.18)).iterator()

            while (`minElem$iv`.hasNext()) {
               val var30: Double = (`minElem$iv`.next() as java.lang.Number).doubleValue()
               options.add(
                  Vec3d(target.method_23317() - dirX * var27 + sideX * var30, player.method_23318(), target.method_23321() - dirZ * var27 + sideZ * var30)
               )
            }
         }

         val `iterator$iv`: java.util.Iterator = SequencesKt.filter(CollectionsKt.asSequence(options), { it: Vec3d ->
            INSTANCE.isAnchorSafe(`$level`, it.field_1352, it.field_1351, it.field_1350)
         }).iterator()
         val var10000: Any
         if (!`iterator$iv`.hasNext()) {
            var10000 = null
         } else {
            var var29: Any = `iterator$iv`.next()
            if (!`iterator$iv`.hasNext()) {
               var10000 = var29
            } else {
               val var31: Vec3d = var29 as Vec3d
               var var35: AutomaticForce = INSTANCE
               var var10001: Vec3d = player.method_73189()
               var var32: Double = var35.horizontalDistance(var10001, var31)

               do {
                  val var33: Any = `iterator$iv`.next()
                  val itx: Vec3d = var33 as Vec3d
                  var35 = INSTANCE
                  var10001 = player.method_73189()
                  val var34: Double = var35.horizontalDistance(var10001, itx)
                  if (java.lang.Double.compare(var32, var34) > 0) {
                     var29 = var33
                     var32 = var34
                  }
               } while (`iterator$iv`.hasNext())

               var10000 = var29
            }
         }

         var10000 as Vec3d
      }
   }

   fun nearestNegativeDistance(target: ZombieEntity, negatives: MutableList<AutomaticForce.NegativeZombie>): Double {
      val var10000: java.lang.Double = SequencesKt.minOrNull(
         SequencesKt.map(SequencesKt.filter(CollectionsKt.asSequence(negatives), { it: AutomaticForce.NegativeZombie ->
            it.id != `$target`.method_5628()
         }), { it: AutomaticForce.NegativeZombie ->
            val var10000: AutomaticForce = INSTANCE
            val var10001: Vec3d = `$target`.method_73189()
            var10000.horizontalDistance(var10001, it.getPos())
         })
      )
      var10000 ?: java.lang.Double.POSITIVE_INFINITY
   }

   fun isMoveDirectionSafe(level: World, player: ClientPlayerEntity, dirX: Double, dirZ: Double, sprinting: Boolean, maxForward: Double, sideClearance: Double): Boolean {
      val px: Double = player.method_23317()
      val py: Double = player.method_23318()
      val pz: Double = player.method_23321()
      val sideX: Double = -dirZ
      val sideZ: Double = dirX
      val var34: java.util.Iterator = (if (sprinting)
            CollectionsKt.listOf(arrayOf(0.45, 0.9, 1.25, maxForward))
            else
            CollectionsKt.listOf(arrayOf(0.35, 0.7, maxForward)))
         .iterator()

      while (var34.hasNext()) {
         val forward: Double = (var34.next() as java.lang.Number).doubleValue()
         val centerX: Double = px + dirX * forward
         val centerZ: Double = pz + dirZ * forward
         if (!this.hasWalkableFloor(level, centerX, py, pz + dirZ * forward)) {
            false
         }

         val var30: java.util.Iterator = CollectionsKt.listOf(arrayOf(-sideClearance, sideClearance)).iterator()

         while (var30.hasNext()) {
            val var35: Double = (var30.next() as java.lang.Number).doubleValue()
            if (!this.hasWalkableFloor(level, centerX + sideX * var35, py, centerZ + sideZ * var35)) {
               false
            }
         }
      }

      true
   }

   fun isAnchorSafe(level: World, x: Double, y: Double, z: Double): Boolean {
      if (!this.hasWalkableFloor(level, x, y, z)) {
         false
      } else {
         val bx: Int = MathHelper.method_15357(x)
         val feetY: Int = MathHelper.method_15357(y)
         val bz: Int = MathHelper.method_15357(z)
         this.isPassable(level, bx, feetY, bz) && this.isPassable(level, bx, feetY + 1, bz)
      }
   }

   fun hasWalkableFloor(level: World, x: Double, y: Double, z: Double): Boolean {
      val bx: Int = MathHelper.method_15357(x)
      val bz: Int = MathHelper.method_15357(z)
      val topFloor: Int = MathHelper.method_15357(y) - 1

      for (dy in 0 downTo -2) {
         if (this.hasSolidFloor(level, bx, topFloor + dy, bz)) {
            true
         }
      }

      false
   }

   fun detectFloorY(level: World, player: ClientPlayerEntity): Int {
      val x: Int = MathHelper.method_15357(player.method_23317())
      val z: Int = MathHelper.method_15357(player.method_23321())
      val feet: Int = MathHelper.method_15357(player.method_23318())
      var floorY: Int = feet - 1
      val var7: Int = feet - 5
      if (feet - 5 <= floorY) {
         while (true) {
            if (this.hasSolidFloor(level, x, floorY, z)) {
               floorY
            }

            if (floorY == var7) {
               break
            }

            floorY--
         }
      }

      null
   }

   fun hasSolidFloor(level: World, x: Int, y: Int, z: Int): Boolean {
      val pos: BlockPos = BlockPos(x, y, z)
      val var10000: BlockState = level.method_8320(pos)
      !var10000.method_26194(level as BlockView, pos, ShapeContext.method_16194()).method_1110()
   }

   fun isPassable(level: World, x: Int, y: Int, z: Int): Boolean {
      val pos: BlockPos = BlockPos(x, y, z)
      val var10000: BlockState = level.method_8320(pos)
      var10000.method_26194(level as BlockView, pos, ShapeContext.method_16194()).method_1110()
   }

   fun forcePointValue(zombie: ZombieEntity, name: java.lang.String): Int {
      val var4: Item = zombie.method_6118(EquipmentSlot.field_6169).method_7909()
      ForceTargetPriority.INSTANCE
         .pointValueFromNameOrHelmet(
            name,
            if (var4 == Items.field_8805)
               "diamond_helmet"
               else
               (if (var4 == Items.field_8862) "golden_helmet" else (if (var4 == Items.field_8743) "iron_helmet" else null))
         )
      }

   fun forceName(zombie: ZombieEntity): java.lang.String {
      val var3: AutomaticForce = this

      var `$this$forceName_u24lambda_u2442`: AutomaticForce
      try {
         `$this$forceName_u24lambda_u2442` = var3
         val var10000: Text = zombie.method_5797()
         `$this$forceName_u24lambda_u2442` = (AutomaticForce)Result.constructor_impl/* $VF was: constructor-impl */(
            if (var10000 != null) var10000.getString() else null
         )
      } catch (var6: java.lang.Throwable) {
         `$this$forceName_u24lambda_u2442` = (AutomaticForce)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var6))
      }

      val custom: java.lang.String = (
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$forceName_u24lambda_u2442`)) null else `$this$forceName_u24lambda_u2442`
      ) as java.lang.String
      if (custom != null && !StringsKt.isBlank(custom)) {
         custom
      } else {
         val var10: java.lang.String = zombie.method_5477().getString()
         var10
      }
   }

   fun aimAngles(player: ClientPlayerEntity, target: Vec3d): Pair<java.lang.Float, java.lang.Float> {
      val dx: Double = target.field_1352 - player.method_23317()
      val dy: Double = target.field_1351 - player.method_23320()
      val dz: Double = target.field_1350 - player.method_23321()
      TuplesKt.to(
         MathHelper.method_15393((float)Math.toDegrees(Math.atan2(-dx, dz))),
         RangesKt.coerceIn((float)Math.toDegrees(-Math.atan2(dy, RangesKt.coerceAtLeast(Math.sqrt(dx * dx + dz * dz), 0.001))), -89.9F, 89.9F)
      )
   }

   private fun horizontalDirection(fromX: Double, fromZ: Double, toX: Double, toZ: Double): Pair<Double, Double>? {
      val len: Double = Math.hypot(toX - fromX, toZ - fromZ)
      return if (len < 1.0E-5) null else TuplesKt.to((toX - fromX) / len, (toZ - fromZ) / len)
   }

   private fun yawTo(dirX: Double, dirZ: Double): Float {
      return (float)Math.toDegrees(Math.atan2(-dirX, dirZ))
   }

   fun horizontalDistance(a: Vec3d, b: Vec3d): Double {
      Math.hypot(a.field_1352 - b.field_1352, a.field_1350 - b.field_1350)
   }

   private fun clearAim() {
      aimController.clear()
   }

   private fun stopComboMovement() {
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

   fun logTickDiagnostics(
      player: ClientPlayerEntity,
      platform: AutomaticForce.PlatformSnapshot,
      snapshot: AutomaticForce.ForceSnapshot,
      decision: ForceTargetPriority.Decision,
      nowMs: Long
   ) {
      this.logDiagnostic(
         "TICK",
         "tick",
         250L,
         { 
            val resetTicks: Int = RangesKt.coerceAtLeast(sprintResetReleaseUntilTick - `$player`.field_6012, 0)
            val abandoned: java.lang.Iterable = `$snapshot`.candidates
            var var10000: Int
            if (abandoned is java.util.Collection && (abandoned as java.util.Collection).isEmpty()) {
               var10000 = 0
            } else {
               val platformText: Int = 0

               for (`count$iv` in abandoned) {
                  if (!(`count$iv` as AutomaticForce.RuntimeCandidate).priority.negative
                     && (`count$iv` as AutomaticForce.RuntimeCandidate).priority.pointValue > 0) {
                     if (++platformText < 0) {
                        CollectionsKt.throwCountOverflow()
                     }
                  }
               }

               var10000 = platformText
            }

            val `$this$count$ivx`: java.lang.Iterable = `$snapshot`.candidates
            if (`$this$count$ivx` is java.util.Collection && (`$this$count$ivx` as java.util.Collection).isEmpty()) {
               var10000 = 0
            } else {
               val var23: Int = 0

               for (var27 in `$this$count$ivx`) {
                  if ((var27 as AutomaticForce.RuntimeCandidate).priority.abandoned) {
                     if (++var23 < 0) {
                        CollectionsKt.throwCountOverflow()
                     }
                  }
               }

               var10000 = var23
            }

            val `$this$count$ivxx`: java.lang.Iterable = `$snapshot`.candidates
            if (`$this$count$ivxx` is java.util.Collection && (`$this$count$ivxx` as java.util.Collection).isEmpty()) {
               var10000 = 0
            } else {
               val var26: Int = 0

               for (var31 in `$this$count$ivxx`) {
                  if (ForceTargetPriority.INSTANCE.isFinishNow((var31 as AutomaticForce.RuntimeCandidate).priority)) {
                     if (++var26 < 0) {
                        CollectionsKt.throwCountOverflow()
                     }
                  }
               }

               var10000 = var26
            }

            run label131@{
               if (`$platform` != null) {
                  var36 = "platform=center${INSTANCE.fmtVec(`$platform`.getCenter())} floor=${`$platform`.floorY} bounds=x${`$platform`.minX}..${`$platform`.maxX},z${`$platform`.minZ}..${`$platform`.maxZ} samples=${`$platform`.samples}"
                  if (var36 != null) {
                     return@label131
                  }
               }

               var36 = "platform=none"
            }

            var10000 = `$player`.field_6012
            val var10002: ForceTargetPriority.BrainState = brainState
            val var10003: Boolean = isPaused
            var var10004: Serializable = currentTargetId
            if (currentTargetId == null) {
               var10004 = "none"
            }

            val var10005: ForceTargetPriority.Candidate = `$decision`.selected
            val var38: Serializable = if (var10005 != null) var10005.id else "none"
            var var10006: Constable = `$decision`.bucket
            if (var10006 == null) {
               var10006 = "NONE"
            }

            val var10007: ForceTargetPriority.BrainState = `$decision`.state
            val var10008: java.lang.String = `$decision`.reason
            val var10010: Int = `$snapshot`.candidates.size()
            val var10012: Int = `$snapshot`.negatives.size()
            val var10015: AutomaticForce = INSTANCE
            val var10016: Vec3d = `$player`.method_73189()
            "tick=$var10000 nowMs=$`$nowMs` state=$var10002 paused=$var10003 current=$var10004 selected=$var38 bucket=$var10006 decision=$var10007 reason=$var10008 resetTicks=$resetTicks candidates=$var10010 positives=$var10000 negatives=$var10012 abandoned=$var10000 finishable=$var10000 player=${var10015.fmtVec(
               var10016
            )} yaw=${INSTANCE.fmt((double)`$player`.method_36454())} pitch=${INSTANCE.fmt((double)`$player`.method_36455())} $var36"
         }
      )
   }

   private fun logDecisionDiagnostics(currentBefore: Int?, decision: Decision, snapshot: jooon.features.dojo.AutomaticForce.ForceSnapshot, nowMs: Long) {
   }

   fun logSwingDecision(player: ClientPlayerEntity, runtime: AutomaticForce.RuntimeCandidate, reason: java.lang.String) {
      this.logDiagnostic(
         "SWING",
         "swing:${runtime.getZombie().method_5628()}:$reason",
         120L,
         { 
            val target: ZombieEntity = `$runtime`.getZombie()
            "target=${target.method_5628()} reason=$`$reason` state=${brainState} bucket=${ForceTargetPriority.INSTANCE
               .bucketFor(`$runtime`.priority, currentTargetId)} value=${`$runtime`.priority.pointValue} dist=${INSTANCE.fmt(
               (double)`$player`.method_5739(target as Entity)
            )} attackReachable=${`$runtime`.priority.attackReachable} oneStep=${`$runtime`.priority.oneStepReachable} crosshair=${INSTANCE.crosshairDebug()} edge=${INSTANCE.fmt(
               `$runtime`.edge.distance
            )} lane=${INSTANCE.fmt(`$runtime`.priority.laneAlignment)}/${`$runtime`.lane.reason} risk=${`$runtime`.priority.negativeRisk} cooldown=${`$player`.field_6012
               - lastClickTick} resetTicks=${RangesKt.coerceAtLeast(sprintResetReleaseUntilTick - `$player`.field_6012, 0)}"
         }
      )
   }

   fun logMovement(mode: java.lang.String, player: ClientPlayerEntity, runtime: AutomaticForce.RuntimeCandidate, details: java.lang.String) {
      this.logDiagnostic(
         "MOVE",
         "move:${runtime.getZombie().method_5628()}:$mode",
         180L,
         { 
            val var10001: Int = `$runtime`.getZombie().method_5628()
            val var10002: Int = `$runtime`.priority.pointValue
            val var10003: AutomaticForce = INSTANCE
            val var10004: Vec3d = `$player`.method_73189()
            val var4: java.lang.String = var10003.fmtVec(var10004)
            val var5: AutomaticForce = INSTANCE
            val var10005: Vec3d = `$runtime`.getZombie().method_73189()
            "mode=$`$mode` target=$var10001 value=$var10002 player=$var4 targetPos=${var5.fmtVec(var10005)} edge=${INSTANCE.fmt(`$runtime`.edge.distance)} best=${INSTANCE.fmt(
               `$runtime`.priority.bestEdgeDistance
            )} lane=${INSTANCE.fmt(`$runtime`.priority.laneAlignment)}/${`$runtime`.lane.reason} anchorDist=${INSTANCE.fmt(`$runtime`.lane.anchorDistance)} $`$details`"
         }
      )
   }

   private fun logCandidateTable(snapshot: jooon.features.dojo.AutomaticForce.ForceSnapshot, decision: Decision, nowMs: Long) {
   }

   private fun candidateDebug(runtime: jooon.features.dojo.AutomaticForce.RuntimeCandidate, nowMs: Long): String {
      val candidate: ForceTargetPriority.Candidate = runtime.priority
      val bucket: ForceTargetPriority.TargetBucket = ForceTargetPriority.INSTANCE.bucketFor(candidate, currentTargetId)
      val remainingMs: Long = RangesKt.coerceAtLeast(candidate.expiresAtMs - nowMs, 0L)
      val var10000: Int = candidate.id
      var var10001: ForceTargetPriority.TargetBucket = bucket
      val var10002: Int = candidate.pointValue
      val var10003: Boolean = candidate.negative
      val var10004: Boolean = ForceTargetPriority.INSTANCE.isFinishNow(candidate)
      val var10005: Boolean = ForceTargetPriority.INSTANCE.shouldCommit(candidate)
      val var10006: Boolean = ForceTargetPriority.INSTANCE.shouldAbandon(candidate)
      val var10007: Boolean = ForceTargetPriority.INSTANCE.shouldReposition(candidate)
      val var10008: Boolean = ForceTargetPriority.INSTANCE.isSwingEligible(candidate)
      val var10009: java.lang.String = ForceTargetPriority.INSTANCE.scoreIntent(candidate)
      val var10010: java.lang.String = this.fmt(ForceTargetPriority.INSTANCE.nextBestScore(candidate, nowMs))
      val var10011: java.lang.String = this.fmt(Math.sqrt(candidate.distanceSqr))
      val var10012: java.lang.String = this.fmt(candidate.edgeDistance)
      val var10013: java.lang.String = this.fmt(candidate.bestEdgeDistance)
      val var10014: java.lang.String = this.fmt(candidate.edgeVelocity)
      val var10015: java.lang.String = this.fmt(candidate.outwardProgress)
      val var10016: Int = candidate.hitCount
      val var10017: Int = candidate.ticksOnTarget
      val var10018: java.lang.String = this.tickText(candidate.ticksSinceProgress)
      val var10019: java.lang.String = this.tickText(candidate.ticksSinceUsefulHit)
      val var10020: Int = candidate.consecutiveBadHits
      val var10021: Boolean = candidate.attackReachable
      val var10022: Boolean = candidate.oneStepReachable
      val var10023: java.lang.String = this.fmt(candidate.nearestNegativeDistance)
      val var10024: ForceTargetPriority.NegativeRisk = candidate.negativeRisk
      val var10025: java.lang.String = this.fmt(candidate.laneAlignment)
      val var10026: java.lang.String = runtime.lane.reason
      val var10027: Boolean = candidate.laneUsable
      val var10028: java.lang.String = this.fmt(runtime.lane.anchorDistance)
      val var10029: Vec3d = runtime.lane.getAnchor()
      if (var10029 != null) {
         val var39: java.lang.String = INSTANCE.fmtVec(var10029)
         var10001 = bucket
         if (var39 != null) {
            return "id=$var10000 bucket=$bucket value=$var10002 neg=$var10003 finish=$var10004 commit=$var10005 abandon=$var10006 reposition=$var10007 swing=$var10008 intent=$var10009 roi=$var10010 dist=$var10011 edge=$var10012 best=$var10013 velocity=$var10014 progress=$var10015 hits=$var10016 age=$var10017 sinceProgress=$var10018 sinceUseful=$var10019 badHits=$var10020 reach=$var10021/$var10022 negDist=$var10023 risk=$var10024 lane=$var10025/$var10026 usable=$var10027 anchorDist=$var10028 anchor=$var39 edgeDir=${this.fmt(
               runtime.edge.dirX
            )},${this.fmt(runtime.edge.dirZ)} openEdge=${runtime.edge.foundOpenEdge} expiresMs=$remainingMs abandoned=${candidate.abandoned}"
         }
      }

      return "id=$var10000 bucket=$var10001 value=$var10002 neg=$var10003 finish=$var10004 commit=$var10005 abandon=$var10006 reposition=$var10007 swing=$var10008 intent=$var10009 roi=$var10010 dist=$var10011 edge=$var10012 best=$var10013 velocity=$var10014 progress=$var10015 hits=$var10016 age=$var10017 sinceProgress=$var10018 sinceUseful=$var10019 badHits=$var10020 reach=$var10021/$var10022 negDist=$var10023 risk=$var10024 lane=$var10025/$var10026 usable=$var10027 anchorDist=$var10028 anchor=none edgeDir=${this.fmt(
         runtime.edge.dirX
      )},${this.fmt(runtime.edge.dirZ)} openEdge=${runtime.edge.foundOpenEdge} expiresMs=$remainingMs abandoned=${candidate.abandoned}"
   }

   private fun candidateDecisionFacts(candidate: Candidate, nowMs: Long): String {
      return "id=${candidate.id} value=${candidate.pointValue} finish=${ForceTargetPriority.INSTANCE.isFinishNow(candidate)} commit=${ForceTargetPriority.INSTANCE
         .shouldCommit(candidate)} abandon=${ForceTargetPriority.INSTANCE.shouldAbandon(candidate)} reposition=${ForceTargetPriority.INSTANCE
         .shouldReposition(candidate)} intent=${ForceTargetPriority.INSTANCE.scoreIntent(candidate)} roi=${this.fmt(
         ForceTargetPriority.INSTANCE.nextBestScore(candidate, nowMs)
      )} edge=${this.fmt(candidate.edgeDistance)} best=${this.fmt(candidate.bestEdgeDistance)} velocity=${this.fmt(candidate.edgeVelocity)} hits=${candidate.hitCount} badHits=${candidate.consecutiveBadHits} progress=${this.fmt(
         candidate.outwardProgress
      )} risk=${candidate.negativeRisk} lane=${this.fmt(candidate.laneAlignment)}"
   }

   private fun bucketOrder(bucket: TargetBucket): Int {
      var var10000: Byte
      when (AutomaticForce.WhenMappings.$EnumSwitchMapping$1[bucket.ordinal()]) {
         1 -> var10000 = 0
         2 -> var10000 = 1
         3 -> var10000 = 2
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   private fun logTelemetry(runtime: jooon.features.dojo.AutomaticForce.RuntimeCandidate?, state: BrainState, reason: String, decisionBucket: TargetBucket?) {
   }

   private fun logEvent(key: String, message: String) {
   }

   private fun logDiagnostic(tag: String, key: String, minIntervalMs: Long, message: () -> String) {
   }

   private fun fmt(value: Double): String {
      if (java.lang.Double.isInfinite(value) || java.lang.Double.isNaN(value)) {
         return "inf"
      } else {
         val var4: Locale = Locale.US
         val var7: Array<Any> = arrayOf(value)
         val var10000: java.lang.String = java.lang.String.format(var4, "%.2f", Arrays.copyOf(var7, var7.length))
         return var10000
      }
   }

   fun fmtVec(value: Vec3d): java.lang.String {
      "(${this.fmt(value.field_1352)},${this.fmt(value.field_1351)},${this.fmt(value.field_1350)})"
   }

   private fun tickText(ticks: Int): String {
      return if (ticks == Integer.MAX_VALUE) "never" else java.lang.String.valueOf(ticks)
   }

   private fun reset() {
      this.logEvent("stop", "state=${brainState} reason=force_inactive")
      active = false
      isPaused = false
      wasSneaking = false
      currentTargetId = null
      brainState = ForceTargetPriority.BrainState.SCAN
      lastObservedAttackEntityId = -1
      sprintResetReleaseUntilTick = 0
      lastTelemetryMs = 0L
      lastCandidateTableMs = 0L
      lastDecisionFingerprint = ""
      lastDiagnosticByKey.clear()
      tracked.clear()
      this.clearAim()
      this.stopComboMovement()
   }

   private data class EdgeInfo(dirX: Double, dirZ: Double, distance: Double, foundOpenEdge: Boolean) {
      public final val dirX: Double
      public final val dirZ: Double
      public final val distance: Double
      public final val foundOpenEdge: Boolean

      init {
         this.dirX = dirX
         this.dirZ = dirZ
         this.distance = distance
         this.foundOpenEdge = foundOpenEdge
      }

      public operator fun component1(): Double {
         return this.dirX
      }

      public operator fun component2(): Double {
         return this.dirZ
      }

      public operator fun component3(): Double {
         return this.distance
      }

      public operator fun component4(): Boolean {
         return this.foundOpenEdge
      }

      public fun copy(dirX: Double = this.dirX, dirZ: Double = this.dirZ, distance: Double = this.distance, foundOpenEdge: Boolean = this.foundOpenEdge): jooon.features.dojo.AutomaticForce.EdgeInfo {
         return AutomaticForce.EdgeInfo(dirX, dirZ, distance, foundOpenEdge)
      }

      public override fun toString(): String {
         return "EdgeInfo(dirX=${this.dirX}, dirZ=${this.dirZ}, distance=${this.distance}, foundOpenEdge=${this.foundOpenEdge})"
      }

      public override fun hashCode(): Int {
         return ((java.lang.Double.hashCode(this.dirX) * 31 + java.lang.Double.hashCode(this.dirZ)) * 31 + java.lang.Double.hashCode(this.distance)) * 31
            + java.lang.Boolean.hashCode(this.foundOpenEdge)
         }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.EdgeInfo
               && java.lang.Double.compare(this.dirX, (other as AutomaticForce.EdgeInfo).dirX) == 0
               && java.lang.Double.compare(this.dirZ, (other as AutomaticForce.EdgeInfo).dirZ) == 0
               && java.lang.Double.compare(this.distance, (other as AutomaticForce.EdgeInfo).distance) == 0
               && this.foundOpenEdge == (other as AutomaticForce.EdgeInfo).foundOpenEdge
            }
      }
   }

   private data class ForceSnapshot(candidates: List<jooon.features.dojo.AutomaticForce.RuntimeCandidate>,
      negatives: List<jooon.features.dojo.AutomaticForce.NegativeZombie>
   ) {
      public final val candidates: List<jooon.features.dojo.AutomaticForce.RuntimeCandidate>
      public final val negatives: List<jooon.features.dojo.AutomaticForce.NegativeZombie>

      init {
         this.candidates = candidates
         this.negatives = negatives
      }

      public operator fun component1(): List<jooon.features.dojo.AutomaticForce.RuntimeCandidate> {
         return this.candidates
      }

      public operator fun component2(): List<jooon.features.dojo.AutomaticForce.NegativeZombie> {
         return this.negatives
      }

      public fun copy(
         candidates: List<jooon.features.dojo.AutomaticForce.RuntimeCandidate> = this.candidates,
         negatives: List<jooon.features.dojo.AutomaticForce.NegativeZombie> = this.negatives
      ): jooon.features.dojo.AutomaticForce.ForceSnapshot {
         return AutomaticForce.ForceSnapshot(candidates, negatives)
      }

      public override fun toString(): String {
         return "ForceSnapshot(candidates=${this.candidates}, negatives=${this.negatives})"
      }

      public override fun hashCode(): Int {
         return this.candidates.hashCode() * 31 + this.negatives.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.ForceSnapshot
               && this.candidates == (other as AutomaticForce.ForceSnapshot).candidates
               && this.negatives == (other as AutomaticForce.ForceSnapshot).negatives
            }
      }
   }

   private data class LaneInfo {
      public final val usable: Boolean
      public final val alignment: Double
      private Vec3d anchor;
      public final val anchorDistance: Double
      public final val reason: String

      fun LaneInfo(usable: Boolean, alignment: Double, anchor: Vec3d?, anchorDistance: Double, reason: java.lang.String) {
         this.usable = usable
         this.alignment = alignment
         this.anchor = anchor
         this.anchorDistance = anchorDistance
         this.reason = reason
      }

      fun getAnchor(): Vec3d? {
         this.anchor
      }

      public operator fun component1(): Boolean {
         return this.usable
      }

      public operator fun component2(): Double {
         return this.alignment
      }

      fun component3(): Vec3d? {
         this.anchor
      }

      public operator fun component4(): Double {
         return this.anchorDistance
      }

      public operator fun component5(): String {
         return this.reason
      }

      fun copy(usable: Boolean, alignment: Double, anchor: Vec3d?, anchorDistance: Double, reason: java.lang.String): AutomaticForce.LaneInfo {
         AutomaticForce.LaneInfo(usable, alignment, anchor, anchorDistance, reason)
      }

      public override fun toString(): String {
         return "LaneInfo(usable=${this.usable}, alignment=${this.alignment}, anchor=${this.anchor}, anchorDistance=${this.anchorDistance}, reason=${this.reason})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (java.lang.Boolean.hashCode(this.usable) * 31 + java.lang.Double.hashCode(this.alignment)) * 31
                              + (if (this.anchor == null) 0 else this.anchor.hashCode())
                        )
                        * 31
                     + java.lang.Double.hashCode(this.anchorDistance)
               )
               * 31
            + this.reason.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.LaneInfo
               && this.usable == (other as AutomaticForce.LaneInfo).usable
               && java.lang.Double.compare(this.alignment, (other as AutomaticForce.LaneInfo).alignment) == 0
               && this.anchor == (other as AutomaticForce.LaneInfo).anchor
               && java.lang.Double.compare(this.anchorDistance, (other as AutomaticForce.LaneInfo).anchorDistance) == 0
               && this.reason == (other as AutomaticForce.LaneInfo).reason
            }
      }
   }

   private data class NegativeZombie {
      public final val id: Int
      private Vec3d pos;

      fun NegativeZombie(id: Int, pos: Vec3d) {
         this.id = id
         this.pos = pos
      }

      fun getPos(): Vec3d {
         this.pos
      }

      public operator fun component1(): Int {
         return this.id
      }

      fun component2(): Vec3d {
         this.pos
      }

      fun copy(id: Int, pos: Vec3d): AutomaticForce.NegativeZombie {
         AutomaticForce.NegativeZombie(id, pos)
      }

      public override fun toString(): String {
         return "NegativeZombie(id=${this.id}, pos=${this.pos})"
      }

      public override fun hashCode(): Int {
         return Integer.hashCode(this.id) * 31 + this.pos.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.NegativeZombie
               && this.id == (other as AutomaticForce.NegativeZombie).id
               && this.pos == (other as AutomaticForce.NegativeZombie).pos
            }
      }
   }

   private data class PlatformSnapshot {
      private Vec3d center;
      public final val floorY: Int
      public final val minX: Int
      public final val maxX: Int
      public final val minZ: Int
      public final val maxZ: Int
      public final val samples: Int

      fun PlatformSnapshot(center: Vec3d, floorY: Int, minX: Int, maxX: Int, minZ: Int, maxZ: Int, samples: Int) {
         this.center = center
         this.floorY = floorY
         this.minX = minX
         this.maxX = maxX
         this.minZ = minZ
         this.maxZ = maxZ
         this.samples = samples
      }

      fun getCenter(): Vec3d {
         this.center
      }

      fun component1(): Vec3d {
         this.center
      }

      public operator fun component2(): Int {
         return this.floorY
      }

      public operator fun component3(): Int {
         return this.minX
      }

      public operator fun component4(): Int {
         return this.maxX
      }

      public operator fun component5(): Int {
         return this.minZ
      }

      public operator fun component6(): Int {
         return this.maxZ
      }

      public operator fun component7(): Int {
         return this.samples
      }

      fun copy(center: Vec3d, floorY: Int, minX: Int, maxX: Int, minZ: Int, maxZ: Int, samples: Int): AutomaticForce.PlatformSnapshot {
         AutomaticForce.PlatformSnapshot(center, floorY, minX, maxX, minZ, maxZ, samples)
      }

      public override fun toString(): String {
         return "PlatformSnapshot(center=${this.center}, floorY=${this.floorY}, minX=${this.minX}, maxX=${this.maxX}, minZ=${this.minZ}, maxZ=${this.maxZ}, samples=${this.samples})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    ((this.center.hashCode() * 31 + Integer.hashCode(this.floorY)) * 31 + Integer.hashCode(this.minX)) * 31
                                       + Integer.hashCode(this.maxX)
                                 )
                                 * 31
                              + Integer.hashCode(this.minZ)
                        )
                        * 31
                     + Integer.hashCode(this.maxZ)
               )
               * 31
            + Integer.hashCode(this.samples)
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.PlatformSnapshot
               && this.center == (other as AutomaticForce.PlatformSnapshot).center
               && this.floorY == (other as AutomaticForce.PlatformSnapshot).floorY
               && this.minX == (other as AutomaticForce.PlatformSnapshot).minX
               && this.maxX == (other as AutomaticForce.PlatformSnapshot).maxX
               && this.minZ == (other as AutomaticForce.PlatformSnapshot).minZ
               && this.maxZ == (other as AutomaticForce.PlatformSnapshot).maxZ
               && this.samples == (other as AutomaticForce.PlatformSnapshot).samples
            }
      }
   }

   private data class RuntimeCandidate {
      private ZombieEntity zombie;
      public final val priority: Candidate
      public final val edge: jooon.features.dojo.AutomaticForce.EdgeInfo
      public final val lane: jooon.features.dojo.AutomaticForce.LaneInfo

      fun RuntimeCandidate(zombie: ZombieEntity, priority: ForceTargetPriority.Candidate, edge: AutomaticForce.EdgeInfo, lane: AutomaticForce.LaneInfo) {
         this.zombie = zombie
         this.priority = priority
         this.edge = edge
         this.lane = lane
      }

      fun getZombie(): ZombieEntity {
         this.zombie
      }

      fun component1(): ZombieEntity {
         this.zombie
      }

      public operator fun component2(): Candidate {
         return this.priority
      }

      public operator fun component3(): jooon.features.dojo.AutomaticForce.EdgeInfo {
         return this.edge
      }

      public operator fun component4(): jooon.features.dojo.AutomaticForce.LaneInfo {
         return this.lane
      }

      fun copy(zombie: ZombieEntity, priority: ForceTargetPriority.Candidate, edge: AutomaticForce.EdgeInfo, lane: AutomaticForce.LaneInfo): AutomaticForce.RuntimeCandidate {
         AutomaticForce.RuntimeCandidate(zombie, priority, edge, lane)
      }

      public override fun toString(): String {
         return "RuntimeCandidate(zombie=${this.zombie}, priority=${this.priority}, edge=${this.edge}, lane=${this.lane})"
      }

      public override fun hashCode(): Int {
         return ((this.zombie.hashCode() * 31 + this.priority.hashCode()) * 31 + this.edge.hashCode()) * 31 + this.lane.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.RuntimeCandidate
               && this.zombie == (other as AutomaticForce.RuntimeCandidate).zombie
               && this.priority == (other as AutomaticForce.RuntimeCandidate).priority
               && this.edge == (other as AutomaticForce.RuntimeCandidate).edge
               && this.lane == (other as AutomaticForce.RuntimeCandidate).lane
            }
      }
   }

   private data class TrackedZombie(expiresAtMs: Long,
      lastSeenTick: Int,
      comboStartedTick: Int = -1,
      comboStartedMs: Long = 0L,
      comboStartEdgeDistance: Double = java.lang.Double.NaN,
      hitCount: Int = 0,
      lastHitTick: Int = -1,
      lastUsefulHitTick: Int = -1,
      lastProgressTick: Int = -1,
      lastEdgeDistance: Double = java.lang.Double.NaN,
      bestEdgeDistance: Double = java.lang.Double.POSITIVE_INFINITY,
      edgeVelocity: Double = 0.0,
      consecutiveBadHits: Int = 0,
      pendingHitTick: Int = -1,
      pendingHitEdgeDistance: Double = java.lang.Double.NaN,
      abandonedUntilTick: Int = 0,
      hardAbandoned: Boolean = false
   ) {
      public final var expiresAtMs: Long
      public final var lastSeenTick: Int
      public final var comboStartedTick: Int
      public final var comboStartedMs: Long
      public final var comboStartEdgeDistance: Double
      public final var hitCount: Int
      public final var lastHitTick: Int
      public final var lastUsefulHitTick: Int
      public final var lastProgressTick: Int
      public final var lastEdgeDistance: Double
      public final var bestEdgeDistance: Double
      public final var edgeVelocity: Double
      public final var consecutiveBadHits: Int
      public final var pendingHitTick: Int
      public final var pendingHitEdgeDistance: Double
      public final var abandonedUntilTick: Int
      public final var hardAbandoned: Boolean

      init {
         this.expiresAtMs = expiresAtMs
         this.lastSeenTick = lastSeenTick
         this.comboStartedTick = comboStartedTick
         this.comboStartedMs = comboStartedMs
         this.comboStartEdgeDistance = comboStartEdgeDistance
         this.hitCount = hitCount
         this.lastHitTick = lastHitTick
         this.lastUsefulHitTick = lastUsefulHitTick
         this.lastProgressTick = lastProgressTick
         this.lastEdgeDistance = lastEdgeDistance
         this.bestEdgeDistance = bestEdgeDistance
         this.edgeVelocity = edgeVelocity
         this.consecutiveBadHits = consecutiveBadHits
         this.pendingHitTick = pendingHitTick
         this.pendingHitEdgeDistance = pendingHitEdgeDistance
         this.abandonedUntilTick = abandonedUntilTick
         this.hardAbandoned = hardAbandoned
      }

      public operator fun component1(): Long {
         return this.expiresAtMs
      }

      public operator fun component2(): Int {
         return this.lastSeenTick
      }

      public operator fun component3(): Int {
         return this.comboStartedTick
      }

      public operator fun component4(): Long {
         return this.comboStartedMs
      }

      public operator fun component5(): Double {
         return this.comboStartEdgeDistance
      }

      public operator fun component6(): Int {
         return this.hitCount
      }

      public operator fun component7(): Int {
         return this.lastHitTick
      }

      public operator fun component8(): Int {
         return this.lastUsefulHitTick
      }

      public operator fun component9(): Int {
         return this.lastProgressTick
      }

      public operator fun component10(): Double {
         return this.lastEdgeDistance
      }

      public operator fun component11(): Double {
         return this.bestEdgeDistance
      }

      public operator fun component12(): Double {
         return this.edgeVelocity
      }

      public operator fun component13(): Int {
         return this.consecutiveBadHits
      }

      public operator fun component14(): Int {
         return this.pendingHitTick
      }

      public operator fun component15(): Double {
         return this.pendingHitEdgeDistance
      }

      public operator fun component16(): Int {
         return this.abandonedUntilTick
      }

      public operator fun component17(): Boolean {
         return this.hardAbandoned
      }

      public fun copy(
         expiresAtMs: Long = this.expiresAtMs,
         lastSeenTick: Int = this.lastSeenTick,
         comboStartedTick: Int = this.comboStartedTick,
         comboStartedMs: Long = this.comboStartedMs,
         comboStartEdgeDistance: Double = this.comboStartEdgeDistance,
         hitCount: Int = this.hitCount,
         lastHitTick: Int = this.lastHitTick,
         lastUsefulHitTick: Int = this.lastUsefulHitTick,
         lastProgressTick: Int = this.lastProgressTick,
         lastEdgeDistance: Double = this.lastEdgeDistance,
         bestEdgeDistance: Double = this.bestEdgeDistance,
         edgeVelocity: Double = this.edgeVelocity,
         consecutiveBadHits: Int = this.consecutiveBadHits,
         pendingHitTick: Int = this.pendingHitTick,
         pendingHitEdgeDistance: Double = this.pendingHitEdgeDistance,
         abandonedUntilTick: Int = this.abandonedUntilTick,
         hardAbandoned: Boolean = this.hardAbandoned
      ): jooon.features.dojo.AutomaticForce.TrackedZombie {
         return AutomaticForce.TrackedZombie(
            expiresAtMs,
            lastSeenTick,
            comboStartedTick,
            comboStartedMs,
            comboStartEdgeDistance,
            hitCount,
            lastHitTick,
            lastUsefulHitTick,
            lastProgressTick,
            lastEdgeDistance,
            bestEdgeDistance,
            edgeVelocity,
            consecutiveBadHits,
            pendingHitTick,
            pendingHitEdgeDistance,
            abandonedUntilTick,
            hardAbandoned
         )
      }

      public override fun toString(): String {
         return "TrackedZombie(expiresAtMs=${this.expiresAtMs}, lastSeenTick=${this.lastSeenTick}, comboStartedTick=${this.comboStartedTick}, comboStartedMs=${this.comboStartedMs}, comboStartEdgeDistance=${this.comboStartEdgeDistance}, hitCount=${this.hitCount}, lastHitTick=${this.lastHitTick}, lastUsefulHitTick=${this.lastUsefulHitTick}, lastProgressTick=${this.lastProgressTick}, lastEdgeDistance=${this.lastEdgeDistance}, bestEdgeDistance=${this.bestEdgeDistance}, edgeVelocity=${this.edgeVelocity}, consecutiveBadHits=${this.consecutiveBadHits}, pendingHitTick=${this.pendingHitTick}, pendingHitEdgeDistance=${this.pendingHitEdgeDistance}, abandonedUntilTick=${this.abandonedUntilTick}, hardAbandoned=${this.hardAbandoned})"
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
                                                                                                                     (
                                                                                                                              (
                                                                                                                                       (
                                                                                                                                                java.lang.Long.hashCode(
                                                                                                                                                         this.expiresAtMs
                                                                                                                                                      )
                                                                                                                                                      * 31
                                                                                                                                                   + Integer.hashCode(
                                                                                                                                                      this.lastSeenTick
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                             * 31
                                                                                                                                          + Integer.hashCode(
                                                                                                                                             this.comboStartedTick
                                                                                                                                          )
                                                                                                                                    )
                                                                                                                                    * 31
                                                                                                                                 + java.lang.Long.hashCode(
                                                                                                                                    this.comboStartedMs
                                                                                                                                 )
                                                                                                                           )
                                                                                                                           * 31
                                                                                                                        + java.lang.Double.hashCode(
                                                                                                                           this.comboStartEdgeDistance
                                                                                                                        )
                                                                                                                  )
                                                                                                                  * 31
                                                                                                               + Integer.hashCode(this.hitCount)
                                                                                                         )
                                                                                                         * 31
                                                                                                      + Integer.hashCode(this.lastHitTick)
                                                                                                )
                                                                                                * 31
                                                                                             + Integer.hashCode(this.lastUsefulHitTick)
                                                                                       )
                                                                                       * 31
                                                                                    + Integer.hashCode(this.lastProgressTick)
                                                                              )
                                                                              * 31
                                                                           + java.lang.Double.hashCode(this.lastEdgeDistance)
                                                                     )
                                                                     * 31
                                                                  + java.lang.Double.hashCode(this.bestEdgeDistance)
                                                            )
                                                            * 31
                                                         + java.lang.Double.hashCode(this.edgeVelocity)
                                                   )
                                                   * 31
                                                + Integer.hashCode(this.consecutiveBadHits)
                                          )
                                          * 31
                                       + Integer.hashCode(this.pendingHitTick)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.pendingHitEdgeDistance)
                        )
                        * 31
                     + Integer.hashCode(this.abandonedUntilTick)
               )
               * 31
            + java.lang.Boolean.hashCode(this.hardAbandoned)
         }

      public override operator fun equals(other: Any?): Boolean {
         label118@
         if (this === other) {
            return true
         } else {
            return other is AutomaticForce.TrackedZombie
               && this.expiresAtMs == (other as AutomaticForce.TrackedZombie).expiresAtMs
               && this.lastSeenTick == (other as AutomaticForce.TrackedZombie).lastSeenTick
               && this.comboStartedTick == (other as AutomaticForce.TrackedZombie).comboStartedTick
               && this.comboStartedMs == (other as AutomaticForce.TrackedZombie).comboStartedMs
               && java.lang.Double.compare(this.comboStartEdgeDistance, (other as AutomaticForce.TrackedZombie).comboStartEdgeDistance) == 0
               && this.hitCount == (other as AutomaticForce.TrackedZombie).hitCount
               && this.lastHitTick == (other as AutomaticForce.TrackedZombie).lastHitTick
               && this.lastUsefulHitTick == (other as AutomaticForce.TrackedZombie).lastUsefulHitTick
               && this.lastProgressTick == (other as AutomaticForce.TrackedZombie).lastProgressTick
               && java.lang.Double.compare(this.lastEdgeDistance, (other as AutomaticForce.TrackedZombie).lastEdgeDistance) == 0
               && java.lang.Double.compare(this.bestEdgeDistance, (other as AutomaticForce.TrackedZombie).bestEdgeDistance) == 0
               && java.lang.Double.compare(this.edgeVelocity, (other as AutomaticForce.TrackedZombie).edgeVelocity) == 0
               && this.consecutiveBadHits == (other as AutomaticForce.TrackedZombie).consecutiveBadHits
               && this.pendingHitTick == (other as AutomaticForce.TrackedZombie).pendingHitTick
               && java.lang.Double.compare(this.pendingHitEdgeDistance, (other as AutomaticForce.TrackedZombie).pendingHitEdgeDistance) == 0
               && this.abandonedUntilTick == (other as AutomaticForce.TrackedZombie).abandonedUntilTick
               && this.hardAbandoned == (other as AutomaticForce.TrackedZombie).hardAbandoned
            }
      }
   }
}
