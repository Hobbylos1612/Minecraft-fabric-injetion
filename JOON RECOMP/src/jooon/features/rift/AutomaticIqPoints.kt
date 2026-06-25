package jooon.features.rift

import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Locale
import java.util.NoSuchElementException
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.pathfinding.WalkToController
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW

@SourceDebugExtension(["SMAP\nAutomaticIqPoints.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutomaticIqPoints.kt\njooon/features/rift/AutomaticIqPoints\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1205:1\n1#2:1206\n288#3,2:1207\n800#3,11:1209\n1747#3,3:1220\n800#3,11:1223\n766#3:1234\n857#3,2:1235\n1963#3,14:1237\n288#3,2:1251\n288#3,2:1253\n800#3,11:1255\n766#3:1266\n857#3,2:1267\n288#3,2:1269\n800#3,11:1271\n766#3:1282\n857#3,2:1283\n1963#3,14:1285\n1282#4,2:1299\n*S KotlinDebug\n*F\n+ 1 AutomaticIqPoints.kt\njooon/features/rift/AutomaticIqPoints\n*L\n743#1:1207,2\n793#1:1209,11\n794#1:1220,3\n813#1:1223,11\n814#1:1234\n814#1:1235,2\n815#1:1237,14\n831#1:1251,2\n832#1:1253,2\n894#1:1255,11\n895#1:1266\n895#1:1267,2\n896#1:1269,2\n901#1:1271,11\n902#1:1282\n902#1:1283,2\n911#1:1285,14\n1014#1:1299,2\n*E\n"])
public object AutomaticIqPoints {
   private const val LADDER_X: Double = -43.488
   private const val LADDER_Y: Double = 122.0
   private const val LADDER_Z: Double = 72.8
   private const val LADDER_YAW: Float = -15.0F
   private const val LADDER_PITCH: Float = 0.0F
   private const val DESCENT_YAW: Float = 153.0F
   private const val DESCENT_PITCH: Float = 0.0F
   private const val AFTER_LADDER_X: Double = -49.0
   private const val AFTER_LADDER_Y: Double = 104.0
   private const val AFTER_LADDER_Z: Double = 71.0
   private const val INTRUDER_SEARCH_RADIUS: Double = 7.0
   private const val INTRUDER_CLICK_RANGE: Double = 2.85
   private const val INTRUDER_MIN_RETRY_RANGE: Double = 1.75
   private const val INTRUDER_MOVE_TIMEOUT_TICKS: Int = 80
   private const val INTRUDER_MAX_CLICK_ATTEMPTS: Int = 4
   private const val PORHTAL_OPEN_TIMEOUT_TICKS: Int = 20
   private const val GUI_SETTLE_TICKS: Int = 10
   private const val TELEPORT_TIMEOUT_TICKS: Int = 20
   private const val POST_TELEPORT_DELAY_TICKS: Int = 10
   private const val FINAL_X: Double = 23.0
   private const val FINAL_Y: Double = 107.0
   private const val FINAL_Z: Double = 53.0
   private const val HUB_X: Double = 0.5
   private const val HUB_Y: Double = 77.0
   private const val HUB_Z: Double = -0.5
   private const val BOX_CLICK_RANGE: Double = 2.65
   private const val BOX_MOVE_TIMEOUT_TICKS: Int = 80
   private const val BOX_CONFIRM_DELAY_TICKS: Int = 5
   private const val BOX_RESULT_TIMEOUT_TICKS: Int = 30
   private const val HUB_COMMAND_TIMEOUT_TICKS: Int = 100
   private const val HUB_RETRY_DELAY_TICKS: Int = 80
   private const val WARP_RIFT_DELAY_TICKS: Int = 10
   private const val INFUSION_GUI_DELAY_TICKS: Int = 10
   private const val RIFT_RETURN_TIMEOUT_TICKS: Int = 160
   private const val YAW_STEP: Float = 7.5F
   private const val PITCH_STEP: Float = 4.0F
   private const val YAW_READY_TOLERANCE: Float = 3.0F
   private const val PITCH_READY_TOLERANCE: Float = 2.0F
   private const val ENTRY_DISTANCE: Double = 0.58
   private const val APPROACH_TIMEOUT_TICKS: Int = 220
   private const val MIN_DESCENT_DISTANCE: Double = 3.0
   private const val SETTLED_BOTTOM_TICKS: Int = 16
   private final var active: Boolean
   private final var state: jooon.features.rift.AutomaticIqPoints.State = AutomaticIqPoints.State.IDLE
   private final var stateTicks: Int
   private final var wasOnLadder: Boolean
   private final var lastY: Double = 122.0
   private final var settledTicks: Int
   @JvmStatic
   private Entity intruderTarget;
   private final var intruderClickAttempts: Int
   private final var porhtalOpenRetries: Int
   private final var sinClickAttempts: Int
   @JvmStatic
   private Vec3d preTeleportPos;
   private final var overlayReady: Boolean
   private final var wasConfigEnabled: Boolean
   private final var shownWarpPrompt: Boolean
   private final var iqPointsGained: Int
   private final var currentAction: String = "Idle"
   @JvmStatic
   private Vec3d selectedBoxTarget;
   @JvmStatic
   private Entity selectedBoxEntity;
   private final var hubRetryCount: Int
   private final var warpRiftStartedAtTick: Int
   private final var finishingAfterHub: Boolean
   private final var enterWasDown: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
   }

   fun tick(client: MinecraftClient) {
      this.ensureOverlayReady()
      this.handleEnterStop(client)
      val player: ClientPlayerEntity = client.field_1724
      if (client.field_1724 != null && client.field_1687 != null) {
         val configEnabled: Boolean = Config.automaticIqPointsEnabled
         if (Config.automaticIqPointsEnabled && !active && client.field_1755 == null) {
            this.beginWaitingForRift()
         } else if (!Config.automaticIqPointsEnabled && wasConfigEnabled) {
            WalkToController.stop$default(WalkToController.INSTANCE, null, 1, null)
            this.resetSilently()
         }

         wasConfigEnabled = Config.automaticIqPointsEnabled
         if (active) {
            val var4: Int = stateTicks++
currentAction = this.actionForState()
            when (AutomaticIqPoints.WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
               1 -> this.resetSilently()
               2 -> this.tickWaitingForRiftStart(player)
               3 -> this.tickRotateToLadder(player)
               4 -> this.tickApproach(player)
               5 -> this.tickDescending(player)
               6 -> this.tickPathfindingToIntruder(player)
               7 -> this.tickRotateToIntruder(player)
               8 -> this.tickMoveCloserToIntruder(player)
               9 -> this.tickWaitForPorhtalOpen(player)
               10 -> this.tickWaitBefore7thSinClick(player)
               11 -> this.tickWaitForTeleport(player)
               12 -> this.tickPostTeleportDelay()
               13 -> this.tickPathfindingToBox(player)
               14 -> this.tickRotateToBox(player)
               15 -> this.tickMoveCloserToBox(player)
               16 -> this.tickWaitingForChooseMe(player)
               17 -> this.tickBeforeChooseMeClick(player)
               18 -> this.tickWaitingForKeep(player)
               19 -> this.tickBeforeKeepClick(player)
               20 -> this.tickWaitingForUbikResult()
               21 -> this.tickWaitingForAfterDue()
               22 -> this.tickBeforeHubCommand()
               23 -> this.tickWaitingForHub(player)
               24 -> this.tickBeforeHubRetry()
               25 -> this.tickBeforeWarpRift()
               26 -> this.tickWaitingForRiftReturn(player)
               27 -> this.tickWaitingForInfusionGui()
               28 -> this.tickBeforeInfusionClick(player)
               29 -> this.tickWaitingForInfusionConfirm()
               else -> throw NoWhenBranchMatchedException()
            }
         }
      } else if (!active || !this.isWaitingForWorldTransition()) {
         this.resetSilently()
      }
   }

   fun handleEnterStop(client: MinecraftClient) {
      val var5: AutomaticIqPoints = this

      var `$this$handleEnterStop_u24lambda_u240`: AutomaticIqPoints
      try {
         `$this$handleEnterStop_u24lambda_u240` = var5
         `$this$handleEnterStop_u24lambda_u240` = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(client.method_22683().method_4490())
      } catch (var8: java.lang.Throwable) {
         `$this$handleEnterStop_u24lambda_u240` = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8))
      }

      val var10000: java.lang.Long = (
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$handleEnterStop_u24lambda_u240`)) null else `$this$handleEnterStop_u24lambda_u240`
      ) as java.lang.Long
      if (var10000 != null) {
         val window: Long = var10000
         val enterDown: Boolean = GLFW.glfwGetKey(window, 257) == 1 || GLFW.glfwGetKey(window, 335) == 1
         if (active && client.field_1755 == null && enterDown && !enterWasDown) {
            this.disableFeature("§cAutomatic IQ Points stopped.")
         }

         enterWasDown = enterDown
      }
   }

   private fun actionForState(): String {
      var var10000: java.lang.String
      when (AutomaticIqPoints.WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
         1 -> var10000 = "Idle"
         2 -> var10000 = "Waiting for Rift (Warp to the Rift once!)"
         3 -> var10000 = "Looking at ladder"
         4 -> var10000 = "Walking to ladder"
         5 -> var10000 = "Descending ladder"
         6 -> var10000 = "Walking to Intruder"
         7 -> var10000 = "Aiming at Intruder"
         8 -> var10000 = "Moving closer to Intruder"
         9 -> var10000 = "Opening Porhtal"
         10 -> var10000 = "Selecting The 7th Sin"
         11 -> var10000 = "Waiting for teleport"
         12 -> var10000 = "Preparing box route"
         13 -> var10000 = "Walking to boxes"
         14 -> var10000 = "Aiming at selected box"
         15 -> var10000 = "Moving closer to selected box"
         16 -> var10000 = "Waiting for Choose me"
         17 -> var10000 = "Confirming selected box"
         18 -> var10000 = "Waiting for KEEP"
         19 -> var10000 = "Keeping selected box"
         20 -> var10000 = "Waiting for Ubik result"
         21 -> var10000 = "Waiting for retry timer"
         22 -> var10000 = "Preparing /hub"
         23 -> var10000 = "Warping to hub"
         24 -> var10000 = "Retrying /hub"
         25 -> var10000 = "Preparing /warp rift"
         26 -> var10000 = "Warping to Rift"
         27 -> var10000 = "Opening infusion menu"
         28 -> var10000 = "Buying Rift Infusion"
         29 -> var10000 = "Waiting for infusion confirmation"
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   private fun begin() {
      active = true
      state = AutomaticIqPoints.State.ROTATING_TO_LADDER
      stateTicks = 0
      wasOnLadder = false
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      lastY = if (var10000 != null) var10000.method_23318() else 122.0
      settledTicks = 0
      intruderTarget = null
      intruderClickAttempts = 0
      porhtalOpenRetries = 0
      sinClickAttempts = 0
      preTeleportPos = null
      finishingAfterHub = false
      selectedBoxTarget = Vec3d.field_1353
      selectedBoxEntity = null
      this.stopMovement()
      JooonReimagined.Companion.sendMessage("§aAutomatic IQ Points [RIFT] started.")
   }

   private fun beginWaitingForRift() {
      active = true
      state = AutomaticIqPoints.State.WAITING_FOR_RIFT_START
      stateTicks = 0
      shownWarpPrompt = false
      iqPointsGained = 0
      currentAction = "Waiting for Rift (Warp to the Rift once!)"
      finishingAfterHub = false
      selectedBoxTarget = Vec3d.field_1353
      selectedBoxEntity = null
      this.stopMovement()
   }

   fun tickWaitingForRiftStart(player: ClientPlayerEntity) {
      this.stopMovement()
      if (this.isAtRiftStart(player)) {
         this.begin()
      } else {
         if (!shownWarpPrompt) {
            shownWarpPrompt = true
            JooonReimagined.Companion.sendMessage("§ePlease warp to the Rift once!")
         }
      }
   }

   fun tickRotateToLadder(player: ClientPlayerEntity) {
      this.lookAtLadder(player)
      this.stopMovement()
      if (this.isFacingLadder(player)) {
         state = AutomaticIqPoints.State.APPROACH_LADDER
         stateTicks = 0
      }
   }

   fun tickApproach(player: ClientPlayerEntity) {
      this.lookAtLadder(player)
      if (this.hasReachedLadderEntry(player)) {
         state = AutomaticIqPoints.State.DESCENDING
         stateTicks = 0
         wasOnLadder = player.method_6101()
         lastY = player.method_23318()
         settledTicks = 0
         this.stopMovement()
         JooonReimagined.Companion.sendMessage("§aLadder reached. Waiting for the descent to finish.")
      } else if (stateTicks > 220) {
         this.stop("§cAutomatic IQ Points [RIFT] could not reach the ladder entry.")
      } else if (!this.isFacingLadder(player)) {
         this.stopMovement()
      } else {
         PlayerController.INSTANCE.pressForward(true)
         PlayerController.INSTANCE.pressBack(false)
         PlayerController.INSTANCE.pressLeft(false)
         PlayerController.INSTANCE.pressRight(false)
         PlayerController.INSTANCE.pressSprint(false)
         PlayerController.INSTANCE.pressJump(false)
         PlayerController.INSTANCE.pressSneak(false)
      }
   }

   fun tickDescending(player: ClientPlayerEntity) {
      this.lookForDescent(player)
      this.stopMovement()
      val onLadder: Boolean = player.method_6101()
      if (onLadder) {
         wasOnLadder = true
      }

      if (Math.abs(player.method_23318() - lastY) < 0.003) {
         val var5: Int = settledTicks++
      } else {
         settledTicks = 0
      }

      lastY = player.method_23318()
      if (this.hasReachedBottom(player, onLadder)) {
         this.handOffToPathfinder()
      }
   }

   fun tickPathfindingToIntruder(player: ClientPlayerEntity) {
      if (!WalkToController.INSTANCE.isActive()) {
         if (!this.isNearAfterLadderPoint(player)) {
            this.stop("§cAutomatic IQ Points [RIFT] stopped because the pathfinder did not reach the Intruder point.")
         } else {
            val target: Entity = this.findIntruderTarget(player)
            if (target == null) {
               this.stop("§cAutomatic IQ Points [RIFT] could not find The Intruder nearby.")
            } else {
               intruderTarget = target
               intruderClickAttempts = 0
               state = AutomaticIqPoints.State.ROTATING_TO_INTRUDER
               stateTicks = 0
               this.stopMovement()
            }
         }
      }
   }

   fun tickRotateToIntruder(player: ClientPlayerEntity) {
      var var10000: Entity
      run label50@{
         if (intruderTarget != null) {
            val var4: Entity = intruderTarget
            val it: Entity = intruderTarget
            var10000 = if (intruderTarget.method_5805() && !it.method_31481()) var4 else null
            if (var10000 != null) {
               return@label50
            }
         }

         var10000 = this.findIntruderTarget(player)
      }

      if (var10000 == null) {
         this.stop("§cAutomatic IQ Points [RIFT] lost The Intruder target.")
      } else {
         intruderTarget = var10000
         val hitPoint: Vec3d = this.preferredIntruderHitPoint(var10000)
         this.lookAt(player, hitPoint)
         this.stopMovement()
         if (!this.isWithinIntruderClickRange(player, hitPoint)) {
            state = AutomaticIqPoints.State.MOVING_CLOSER_TO_INTRUDER
            stateTicks = 0
         } else {
            if (this.isLookingAt(player, hitPoint) && stateTicks >= 2) {
               this.rightClickIntruder()
            }
         }
      }
   }

   fun tickMoveCloserToIntruder(player: ClientPlayerEntity) {
      var var10000: Entity
      run label54@{
         if (intruderTarget != null) {
            val var4: Entity = intruderTarget
            val it: Entity = intruderTarget
            var10000 = if (intruderTarget.method_5805() && !it.method_31481()) var4 else null
            if (var10000 != null) {
               return@label54
            }
         }

         var10000 = this.findIntruderTarget(player)
      }

      if (var10000 == null) {
         this.stop("§cAutomatic IQ Points [RIFT] lost The Intruder target.")
      } else {
         intruderTarget = var10000
         val hitPoint: Vec3d = this.preferredIntruderHitPoint(var10000)
         this.lookAt(player, hitPoint)
         if (this.isWithinIntruderClickRange(player, hitPoint)) {
            this.stopMovement()
            state = AutomaticIqPoints.State.ROTATING_TO_INTRUDER
            stateTicks = 0
         } else if (stateTicks > 80) {
            this.stop("§cAutomatic IQ Points [RIFT] could not move close enough to The Intruder.")
         } else {
            if (this.isYawAligned(player, hitPoint, 8.0F)) {
               PlayerController.INSTANCE.pressForward(true)
               PlayerController.INSTANCE.pressBack(false)
               PlayerController.INSTANCE.pressLeft(false)
               PlayerController.INSTANCE.pressRight(false)
               PlayerController.INSTANCE.pressSprint(false)
               PlayerController.INSTANCE.pressJump(false)
               PlayerController.INSTANCE.pressSneak(false)
            } else {
               this.stopMovement()
            }
         }
      }
   }

   fun tickWaitForPorhtalOpen(player: ClientPlayerEntity) {
      this.stopMovement()
      if (this.isPorhtalScreen()) {
         state = AutomaticIqPoints.State.WAITING_BEFORE_7TH_SIN_CLICK
         stateTicks = 0
      } else if (stateTicks >= 20) {
         if (porhtalOpenRetries >= 1) {
            this.timedOut()
         } else {
            var var10000: Entity
            run label53@{
               val target: Int = porhtalOpenRetries++
               if (intruderTarget != null) {
                  val var3: Entity = intruderTarget
                  val it: Entity = intruderTarget
                  var10000 = if (intruderTarget.method_5805() && !it.method_31481()) var3 else null
                  if (var10000 != null) {
                     return@label53
                  }
               }

               var10000 = this.findIntruderTarget(player)
            }

            if (var10000 == null) {
               this.timedOut()
            } else {
               intruderTarget = var10000
               state = AutomaticIqPoints.State.ROTATING_TO_INTRUDER
               stateTicks = 0
            }
         }
      }
   }

   fun tickWaitBefore7thSinClick(player: ClientPlayerEntity) {
      this.stopMovement()
      if (!this.isPorhtalScreen()) {
         this.timedOut()
      } else if (stateTicks >= 10) {
         if (!this.click7thSin(player)) {
            this.timedOut()
         } else {
            val var2: Int = sinClickAttempts++
            preTeleportPos = player.method_73189()
            state = AutomaticIqPoints.State.WAITING_FOR_TELEPORT
            stateTicks = 0
         }
      }
   }

   fun tickWaitForTeleport(player: ClientPlayerEntity) {
      this.stopMovement()
      if (this.hasTeleported(player)) {
         state = AutomaticIqPoints.State.POST_TELEPORT_DELAY
         stateTicks = 0
      } else if (stateTicks >= 20) {
         if (sinClickAttempts >= 2 || !this.isPorhtalScreen()) {
            this.timedOut()
         } else if (!this.click7thSin(player)) {
            this.timedOut()
         } else {
            val var2: Int = sinClickAttempts++
            preTeleportPos = player.method_73189()
            stateTicks = 0
         }
      }
   }

   private fun tickPostTeleportDelay() {
      this.stopMovement()
      if (stateTicks >= 10) {
         state = AutomaticIqPoints.State.PATHFINDING_TO_BOX
         stateTicks = 0
         JooonReimagined.Companion.sendMessage("§aTeleport complete. Walking to the next point.")
         WalkToController.INSTANCE.startWalkTo(23.0, 107.0, 53.0)
      }
   }

   fun tickPathfindingToBox(player: ClientPlayerEntity) {
      if (!WalkToController.INSTANCE.isActive()) {
         if (!this.isNearFinalPoint(player)) {
            this.timedOut()
         } else {
            state = AutomaticIqPoints.State.ROTATING_TO_BOX
            stateTicks = 0
            this.stopMovement()
         }
      }
   }

   fun tickRotateToBox(player: ClientPlayerEntity) {
      val target: Vec3d = this.selectedBoxPoint()
      this.lookAt(player, target)
      this.stopMovement()
      selectedBoxTarget = target
      selectedBoxEntity = this.findSelectedBoxEntity(target)
      if (!this.isWithinBoxClickRange(player, target)) {
         state = AutomaticIqPoints.State.MOVING_CLOSER_TO_BOX
         stateTicks = 0
      } else if (this.isLookingAt(player, target) && stateTicks >= 2 && this.rightClickSelectedBox(player)) {
         currentAction = "Waiting for Choose me"
         state = AutomaticIqPoints.State.WAITING_FOR_CHOOSE_ME
         stateTicks = 0
      }
   }

   fun tickMoveCloserToBox(player: ClientPlayerEntity) {
      val target: Vec3d = this.selectedBoxPoint()
      this.lookAt(player, target)
      if (this.isWithinBoxClickRange(player, target)) {
         this.stopMovement()
         state = AutomaticIqPoints.State.ROTATING_TO_BOX
         stateTicks = 0
      } else if (stateTicks > 80) {
         this.timedOut()
      } else {
         if (this.isYawAligned(player, target, 8.0F)) {
            PlayerController.INSTANCE.pressForward(true)
            PlayerController.INSTANCE.pressBack(false)
            PlayerController.INSTANCE.pressLeft(false)
            PlayerController.INSTANCE.pressRight(false)
            PlayerController.INSTANCE.pressSprint(false)
            PlayerController.INSTANCE.pressJump(false)
            PlayerController.INSTANCE.pressSneak(false)
         } else {
            this.stopMovement()
         }
      }
   }

   fun tickWaitingForChooseMe(player: ClientPlayerEntity) {
      this.stopMovement()
      this.lookAt(player, selectedBoxTarget)
      if (this.hasBoxArmorStand("choose me")) {
         state = AutomaticIqPoints.State.WAITING_BEFORE_CHOOSE_ME_CLICK
         stateTicks = 0
      } else {
         if (stateTicks > 30) {
            state = AutomaticIqPoints.State.ROTATING_TO_BOX
            stateTicks = 0
         }
      }
   }

   fun tickBeforeChooseMeClick(player: ClientPlayerEntity) {
      this.stopMovement()
      this.lookAt(player, selectedBoxTarget)
      if (stateTicks >= 5) {
         if (this.rightClickSelectedBox(player)) {
            currentAction = "Waiting for KEEP"
            state = AutomaticIqPoints.State.WAITING_FOR_KEEP
            stateTicks = 0
         }
      }
   }

   fun tickWaitingForKeep(player: ClientPlayerEntity) {
      this.stopMovement()
      this.lookAt(player, selectedBoxTarget)
      if (this.hasBoxArmorStand("keep")) {
         state = AutomaticIqPoints.State.WAITING_BEFORE_KEEP_CLICK
         stateTicks = 0
      } else {
         if (stateTicks > 30) {
            state = AutomaticIqPoints.State.WAITING_BEFORE_CHOOSE_ME_CLICK
            stateTicks = 0
         }
      }
   }

   fun tickBeforeKeepClick(player: ClientPlayerEntity) {
      this.stopMovement()
      this.lookAt(player, selectedBoxTarget)
      if (stateTicks >= 5) {
         if (this.rightClickSelectedBox(player)) {
            currentAction = "Waiting for Ubik"
            state = AutomaticIqPoints.State.WAITING_FOR_UBIK_RESULT
            stateTicks = 0
         }
      }
   }

   private fun tickWaitingForUbikResult() {
      this.stopMovement()
   }

   private fun tickWaitingForAfterDue() {
      this.stopMovement()
   }

   private fun tickBeforeHubCommand() {
      this.stopMovement()
      if (stateTicks >= 10) {
         this.sendHub()
      }
   }

   fun tickWaitingForHub(player: ClientPlayerEntity) {
      this.stopMovement()
      if (this.isAtHub(player)) {
         if (finishingAfterHub) {
            this.disableFeature("§aAutomatic IQ Points finished.")
         } else {
            currentAction = "Waiting to warp Rift"
            state = AutomaticIqPoints.State.WAITING_BEFORE_WARP_RIFT
            stateTicks = 0
         }
      } else {
         if (stateTicks > 100) {
            this.timedOut()
         }
      }
   }

   private fun tickBeforeHubRetry() {
      this.stopMovement()
      if (stateTicks >= 80) {
         this.sendHub()
      }
   }

   private fun tickBeforeWarpRift() {
      this.stopMovement()
      if (stateTicks >= 10) {
         currentAction = "Warping to Rift"
         warpRiftStartedAtTick = stateTicks
         val var10000: ClientPlayerEntity = this.getMc().field_1724
         if (var10000 != null && var10000.field_3944 != null) {
            var10000.field_3944.method_45730("warp rift")
         }

         state = AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN
         stateTicks = 0
      }
   }

   fun tickWaitingForRiftReturn(player: ClientPlayerEntity) {
      this.stopMovement()
      if (this.isAtRiftStart(player)) {
         this.begin()
      } else {
         if (stateTicks > 160) {
            this.timedOut()
         }
      }
   }

   private fun tickWaitingForInfusionGui() {
      this.stopMovement()
      if (this.getMc().field_1755 is HandledScreen) {
         state = AutomaticIqPoints.State.WAITING_BEFORE_INFUSION_CLICK
         stateTicks = 0
      }
   }

   fun tickBeforeInfusionClick(player: ClientPlayerEntity) {
      this.stopMovement()
      if (stateTicks >= 10) {
         if (this.clickInfusion(player)) {
            state = AutomaticIqPoints.State.WAITING_FOR_INFUSION_CONFIRM
            stateTicks = 0
            currentAction = "Waiting for infusion"
         } else {
            this.timedOut()
         }
      }
   }

   private fun tickWaitingForInfusionConfirm() {
      this.stopMovement()
      if (stateTicks > 160) {
         this.timedOut()
      }
   }

   fun hasReachedLadderEntry(player: ClientPlayerEntity): Boolean {
      player.method_6101()
         || Math.hypot(player.method_23317() - -43.488, player.method_23321() - 72.8) <= 0.58
            && player.method_23318() <= 122.75
            && (player.method_23318() < 121.9 || player.method_18798().field_1351 < -0.015)
         }

   fun hasReachedBottom(player: ClientPlayerEntity, onLadder: Boolean): Boolean {
      wasOnLadder && !(player.method_23318() > 119.0) && (!onLadder && player.method_24828() || player.method_24828() && settledTicks >= 16)
   }

   fun isNearAfterLadderPoint(player: ClientPlayerEntity): Boolean {
      val dx: Double = player.method_23317() - -49.0
      val dy: Double = Math.abs(player.method_23318() - 104.0)
      val dz: Double = player.method_23321() - 71.0
      dx * dx + dz * dz <= 10.5625 && dy <= 3.0
   }

   private fun isAllowedGuiState(): Boolean {
      return state === AutomaticIqPoints.State.WAITING_FOR_PORHTAL_OPEN
         || state === AutomaticIqPoints.State.WAITING_BEFORE_7TH_SIN_CLICK
         || state === AutomaticIqPoints.State.WAITING_FOR_TELEPORT
         || state === AutomaticIqPoints.State.WAITING_FOR_INFUSION_GUI
         || state === AutomaticIqPoints.State.WAITING_BEFORE_INFUSION_CLICK
         || state === AutomaticIqPoints.State.WAITING_FOR_INFUSION_CONFIRM
      }

   private fun isPorhtalScreen(): Boolean {
      val var2: Screen = this.getMc().field_1755
      val var10000: HandledScreen = var2 as? HandledScreen
      if ((var2 as? HandledScreen) == null) {
         return false
      } else {
         val var10001: java.lang.String = var10000.method_25440().getString()
         return this.cleanLabel(var10001) == "porhtal"
      }
   }

   fun click7thSin(player: ClientPlayerEntity): Boolean {
      val slot: Screen = this.getMc().field_1755
      if ((slot as? HandledScreen) == null) {
         false
      } else if (!this.isPorhtalScreen()) {
         false
      } else {
         val var10000: ScreenHandler = player.field_7512
         val var13: DefaultedList = var10000.field_7761
         val var7: java.util.Iterator = (var13 as java.lang.Iterable).iterator()

         while (true) {
            if (!var7.hasNext()) {
               var17 = null
               break
            }

            var `element$iv`: Any
            run label57@{
               `element$iv` = var7.next()
               val var14: ItemStack = (`element$iv` as Slot).method_7677()
               if (!var14.method_7960()) {
                  val var15: AutomaticIqPoints = INSTANCE
                  val var10001: java.lang.String = var14.method_7964().getString()
                  if (StringsKt.contains$default(var15.cleanLabel(var10001), "the 7th sin", false, 2, null)) {
                     var16 = true
                     return@label57
                  }
               }

               var16 = false
            }

            if (var16) {
               var17 = `element$iv`
               break
            }
         }

         val var18: Slot = var17 as Slot
         if (var17 as Slot == null) {
            false
         } else {
            val var19: ClientPlayerInteractionManager = this.getMc().field_1761
            if (var19 != null) {
               var19.method_2906(var10000.field_7763, var18.field_7874, 0, SlotActionType.field_7790, player as PlayerEntity)
               true
            } else {
               false
            }
         }
      }
   }

   fun hasTeleported(player: ClientPlayerEntity): Boolean {
      preTeleportPos != null && preTeleportPos.method_1025(player.method_73189()) >= 25.0
   }

   fun isAtRiftStart(player: ClientPlayerEntity): Boolean {
      Math.abs(player.method_23317() - -44.3) <= 0.35 && Math.abs(player.method_23318() - 122.0) <= 0.75 && Math.abs(player.method_23321() - 69.3) <= 0.35
   }

   fun isAtHub(player: ClientPlayerEntity): Boolean {
      Math.abs(player.method_23317() - 0.5) <= 1.5 && Math.abs(player.method_23318() - 77.0) <= 2.0 && Math.abs(player.method_23321() - -0.5) <= 1.5
   }

   fun isNearFinalPoint(player: ClientPlayerEntity): Boolean {
      val dx: Double = player.method_23317() - 23.0
      val dy: Double = Math.abs(player.method_23318() - 107.0)
      val dz: Double = player.method_23321() - 53.0
      dx * dx + dz * dz <= 10.5625 && dy <= 3.0
   }

   fun selectedBoxPoint(): Vec3d {
      var var10000: Vec3d
      when (AutomaticIqPoints.WhenMappings.$EnumSwitchMapping$1[Config.automaticIqPointsBoxToPick.ordinal()]) {
         1 -> var10000 = Vec3d(26.5, 108.55, 53.5)
         2 -> var10000 = Vec3d(25.5, 108.55, 55.5)
         3 -> var10000 = Vec3d(23.5, 108.55, 56.5)
         else -> throw NoWhenBranchMatchedException()
      }

      var10000
   }

   fun isWithinBoxClickRange(player: ClientPlayerEntity, target: Vec3d): Boolean {
      Vec3d(player.method_23317(), player.method_23320(), player.method_23321()).method_1025(target) <= 7.0225
   }

   private fun hasBoxArmorStand(text: String): Boolean {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         return false
      } else {
         val target: Vec3d = selectedBoxTarget
         val var17: java.lang.Iterable = var10000.method_18112()
         val `element$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in var17) {
            if (`element$iv$iv` is ArmorStandEntity) {
               `element$iv`.add(`element$iv$iv`)
            }
         }

         val var11: java.lang.Iterable = `element$iv` as java.util.List
         var var20: Boolean
         if (`element$iv` as java.util.List is java.util.Collection && ((`element$iv` as java.util.List) as java.util.Collection).isEmpty()) {
            var20 = false
         } else {
            val var13: java.util.Iterator = var11.iterator()

            while (true) {
               if (!var13.hasNext()) {
                  var20 = false
                  break
               }

               run label76@{
                  val var15: ArmorStandEntity = var13.next() as ArmorStandEntity
                  if (var15.method_5805()
                     && !var15.method_31481()
                     && Math.abs(var15.method_23317() - target.field_1352) <= 1.0
                     && Math.abs(var15.method_23321() - target.field_1350) <= 1.0
                     && var15.method_23318() >= target.field_1351
                     && var15.method_23318() <= target.field_1351 + 3.0) {
                     val var18: AutomaticIqPoints = INSTANCE
                     var var10001: Text = var15.method_5797()
                     if (var10001 == null) {
                        var10001 = var15.method_5477()
                     }

                     val var21: java.lang.String = var10001.getString()
                     if (StringsKt.contains$default(var18.cleanLabel(var21), text, false, 2, null)) {
                        var19 = true
                        return@label76
                     }
                  }

                  var19 = false
               }

               if (var19) {
                  var20 = true
                  break
               }
            }
         }

         return var20
      }
   }

   fun rightClickSelectedBox(player: ClientPlayerEntity): Boolean {
      this.isWithinBoxClickRange(player, selectedBoxTarget) && PlayerController.INSTANCE.rightClick()
   }

   fun findSelectedBoxEntity(target: Vec3d): Entity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var35: java.lang.Iterable = var10000.method_18112()
         var `maxElem$iv`: java.util.Collection = ArrayList()

         for (headScore in var35) {
            if (headScore is ArmorStandEntity) {
               `maxElem$iv`.add(headScore)
            }
         }

         val var21: java.lang.Iterable = `maxElem$iv` as java.util.List
         `maxElem$iv` = ArrayList()

         for (var31 in var21) {
            if ((var31 as ArmorStandEntity).method_5805()
               && !(var31 as ArmorStandEntity).method_31481()
               && INSTANCE.isEntityNearSelectedBox((var31 as ArmorStandEntity) as Entity, target)) {
               `maxElem$iv`.add(var31)
            }
         }

         val var22: java.util.Iterator = (`maxElem$iv` as java.util.List).iterator()
         val var36: Any
         if (!var22.hasNext()) {
            var36 = null
         } else {
            var var24: Any = var22.next()
            if (!var22.hasNext()) {
               var36 = var24
            } else {
               var var27: Double = (if (!(var24 as ArmorStandEntity).method_6118(EquipmentSlot.field_6169).method_7960()) 10.0 else 0.0)
                  - (var24 as ArmorStandEntity).method_73189().method_1025(target)

               do {
                  val var30: Any = var22.next()
                  val var34: Double = (if (!(var30 as ArmorStandEntity).method_6118(EquipmentSlot.field_6169).method_7960()) 10.0 else 0.0)
                     - (var30 as ArmorStandEntity).method_73189().method_1025(target)
                     if (java.lang.Double.compare(var27, var34) < 0) {
                     var24 = var30
                     var27 = var34
                  }
               } while (var22.hasNext())

               var36 = var24
            }
         }

         var36 as Entity
      }
   }

   fun isEntityNearSelectedBox(entity: Entity, target: Vec3d): Boolean {
      Math.abs(entity.method_23317() - target.field_1352) <= 0.9
         && Math.abs(entity.method_23321() - target.field_1350) <= 0.9
         && entity.method_23318() >= target.field_1351 - 2.0
         && entity.method_23318() <= target.field_1351 + 1.25
      }

   fun clickInfusion(player: ClientPlayerEntity): Boolean {
      val slot: Screen = this.getMc().field_1755
      if ((slot as? HandledScreen) == null) {
         false
      } else {
         val var10000: ScreenHandler = player.field_7512
var var24: Slot
         when (AutomaticIqPoints.WhenMappings.$EnumSwitchMapping$2[Config.automaticIqPointsInfusion.ordinal()]) {
            1 -> {
               val var25: DefaultedList = var10000.field_7761

               run label96@{
                  for (var16 in var25 as java.lang.Iterable) {
                     if ((var16 as Slot).field_7874 == 11) {
                        var26 = var16
                        return@label96
                     }
                  }

                  var26 = null
               }

               var24 = var26 as Slot
               break
            }
            2 -> {
               val var19: DefaultedList = var10000.field_7761
               val var7: java.util.Iterator = (var19 as java.lang.Iterable).iterator()

               while (true) {
                  if (!var7.hasNext()) {
                     var23 = null
                     break
                  }

                  var `element$iv`: Any
                  run label89@{
                     run label88@{
                        `element$iv` = var7.next()
                        val var20: ItemStack = (`element$iv` as Slot).method_7677()
                        if (!var20.method_7960()) {
                           if (var20.method_31574(Items.field_8477)) {
                              return@label88
                           }

                           val var21: AutomaticIqPoints = INSTANCE
                           val var10001: java.lang.String = var20.method_7964().getString()
                           if (StringsKt.contains$default(var21.cleanLabel(var10001), "bits", false, 2, null)) {
                              return@label88
                           }
                        }

                        var22 = false
                        return@label89
                     }

                     var22 = true
                  }

                  if (var22) {
                     var23 = `element$iv`
                     break
                  }
               }

               var24 = var23 as Slot
               break
            }
            else -> throw NoWhenBranchMatchedException()
         }

         if (var24 == null) {
            false
         } else {
            val var27: ClientPlayerInteractionManager = this.getMc().field_1761
            if (var27 != null) {
               var27.method_2906(var10000.field_7763, var24.field_7874, 0, SlotActionType.field_7790, player as PlayerEntity)
               true
            } else {
               false
            }
         }
      }
   }

   fun lookAtLadder(player: ClientPlayerEntity) {
      player.method_36456(this.smoothRotation(player.method_36454(), -15.0F, 7.5F))
      player.method_36457(this.smoothRotation(player.method_36455(), 0.0F, 4.0F))
   }

   fun lookForDescent(player: ClientPlayerEntity) {
      player.method_36456(this.smoothRotation(player.method_36454(), 153.0F, 7.5F))
      player.method_36457(this.smoothRotation(player.method_36455(), 0.0F, 4.0F))
   }

   fun lookAt(player: ClientPlayerEntity, target: Vec3d) {
      val dx: Double = target.field_1352 - player.method_23317()
      val dy: Double = target.field_1351 - player.method_23320()
      val dz: Double = target.field_1350 - player.method_23321()
      val horizontal: Double = RangesKt.coerceAtLeast(Math.hypot(dx, dz), 0.001)
      val targetYaw: Float = (float)(MathHelper.method_15349(dz, dx) * (180.0 / Math.PI)) - 90.0F
      val targetPitch: Float = (float)(-(MathHelper.method_15349(dy, horizontal) * (180.0 / Math.PI)))
      player.method_36456(this.smoothRotation(player.method_36454(), targetYaw, 7.5F))
      player.method_36457(this.smoothRotation(player.method_36455(), targetPitch, 4.0F))
   }

   fun isFacingLadder(player: ClientPlayerEntity): Boolean {
      Math.abs(MathHelper.method_15393(-15.0F - player.method_36454())) <= 3.0F && Math.abs(MathHelper.method_15393(0.0F - player.method_36455())) <= 2.0F
   }

   fun isLookingAt(player: ClientPlayerEntity, target: Vec3d): Boolean {
      val dx: Double = target.field_1352 - player.method_23317()
      val dy: Double = target.field_1351 - player.method_23320()
      val dz: Double = target.field_1350 - player.method_23321()
      Math.abs(MathHelper.method_15393((float)(MathHelper.method_15349(dz, dx) * (180.0 / Math.PI)) - 90.0F - player.method_36454())) <= 3.0F
         && Math.abs(
               MathHelper.method_15393(
                  (float)(-(MathHelper.method_15349(dy, RangesKt.coerceAtLeast(Math.hypot(dx, dz), 0.001)) * (180.0 / Math.PI))) - player.method_36455()
               )
            )
            <= 2.0F
         }

   fun isYawAligned(player: ClientPlayerEntity, target: Vec3d, tolerance: Float): Boolean {
      Math.abs(
            MathHelper.method_15393(
               (float)(MathHelper.method_15349(target.field_1350 - player.method_23321(), target.field_1352 - player.method_23317()) * (180.0 / Math.PI))
                  - 90.0F
                  - player.method_36454()
            )
         )
         <= tolerance
      }

   private fun smoothRotation(current: Float, target: Float, maxStep: Float): Float {
      val diff: Float = MathHelper.method_15393(target - current)
      return if (Math.abs(diff) <= maxStep) target else current + Math.signum(diff) * maxStep
   }

   fun findIntruderTarget(player: ClientPlayerEntity): Entity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var60: java.lang.Iterable = var10000.method_18112()
         var `iterator$iv`: java.util.Collection = ArrayList()

         for (`e$iv` in var60) {
            if (`e$iv` is ArmorStandEntity) {
               `iterator$iv`.add(`e$iv`)
            }
         }

         val var28: java.lang.Iterable = `iterator$iv` as java.util.List
         `iterator$iv` = ArrayList()

         for (var49 in var28) {
            if ((var49 as ArmorStandEntity).method_5805()
               && !(var49 as ArmorStandEntity).method_31481()
               && (var49 as ArmorStandEntity).method_5858(player as Entity) <= 49.0) {
               `iterator$iv`.add(var49)
            }
         }

         val var29: java.util.Iterator = (`iterator$iv` as java.util.List).iterator()

         while (true) {
            if (var29.hasNext()) {
               val var34: Any = var29.next()
               val var39: ArmorStandEntity = var34 as ArmorStandEntity
               val var62: AutomaticIqPoints = INSTANCE
               var var10001: Text = var39.method_5797()
               if (var10001 == null) {
                  var10001 = var39.method_5477()
               }

               val var67: java.lang.String = var10001.getString()
               if (!StringsKt.contains$default(var62.cleanLabel(var67), "the intruder", false, 2, null)) {
                  continue
               }

               var61 = var34
               break
            }

            var61 = null
            break
         }

         val var63: ArmorStandEntity = var61 as ArmorStandEntity
         if (var61 as ArmorStandEntity == null) {
            null
         } else {
            val labelStand: ArmorStandEntity = var63
            val var64: Vec3d = var63.method_5829().method_1005()
            val labelCenter: Vec3d = var64
            val var65: java.lang.Iterable = var10000.method_18112()
            var `destination$iv$ivx`: java.util.Collection = ArrayList()

            for (var54 in var65) {
               if (var54 is ArmorStandEntity) {
                  `destination$iv$ivx`.add(var54)
               }
            }

            val var36: java.lang.Iterable = `destination$iv$ivx` as java.util.List
            `destination$iv$ivx` = ArrayList()

            for (var55 in var36) {
               if ((var55 as ArmorStandEntity).method_5805()
                  && !(var55 as ArmorStandEntity).method_31481()
                  && var55 as ArmorStandEntity != labelStand
                  && Math.abs((var55 as ArmorStandEntity).method_23317() - labelStand.method_23317()) <= 1.35
                  && Math.abs((var55 as ArmorStandEntity).method_23321() - labelStand.method_23321()) <= 1.35
                  && (var55 as ArmorStandEntity).method_23318() < labelStand.method_23318()
                  && (var55 as ArmorStandEntity).method_23318() >= labelStand.method_23318() - 3.0) {
                  `destination$iv$ivx`.add(var55)
               }
            }

            val var37: java.util.Iterator = (`destination$iv$ivx` as java.util.List).iterator()
            val var66: Any
            if (!var37.hasNext()) {
               var66 = null
            } else {
               var var42: Any = var37.next()
               if (!var37.hasNext()) {
                  var66 = var42
               } else {
                  var var48: Double = (if (!(var42 as ArmorStandEntity).method_6118(EquipmentSlot.field_6169).method_7960()) 10.0 else 0.0)
                     - (var42 as ArmorStandEntity).method_73189().method_1025(var64)

                  do {
                     val var53: Any = var37.next()
                     val var59: Double = (if (!(var53 as ArmorStandEntity).method_6118(EquipmentSlot.field_6169).method_7960()) 10.0 else 0.0)
                        - (var53 as ArmorStandEntity).method_73189().method_1025(labelCenter)
                        if (java.lang.Double.compare(var48, var59) < 0) {
                        var42 = var53
                        var48 = var59
                     }
                  } while (var37.hasNext())

                  var66 = var42
               }
            }

            if (var66 as ArmorStandEntity != null) (var66 as ArmorStandEntity) as Entity else labelStand as Entity
         }
      }
   }

   fun preferredIntruderHitPoint(entity: Entity): Vec3d {
      val var10000: Box = entity.method_5829()
      val x: Double = (var10000.field_1323 + var10000.field_1320) * 0.5
      val var11: Double
      if (entity is ArmorStandEntity) {
         val var10: ItemStack = (entity as ArmorStandEntity).method_6118(EquipmentSlot.field_6169)
         var11 = if (!var10.method_7960()) (entity as ArmorStandEntity).method_23318() + 1.55 else (var10000.field_1322 + var10000.field_1325) * 0.5 + 0.25
      } else {
         var11 = (var10000.field_1322 + var10000.field_1325) * 0.5
      }

      Vec3d(x, var11, (var10000.field_1321 + var10000.field_1324) * 0.5)
   }

   fun isWithinIntruderClickRange(player: ClientPlayerEntity, hitPoint: Vec3d): Boolean {
      val eye: Vec3d = Vec3d(player.method_23317(), player.method_23320(), player.method_23321())
      val range: Double = RangesKt.coerceAtLeast(2.85 - (double)intruderClickAttempts * 0.35, 1.75)
      eye.method_1025(hitPoint) <= range * range
   }

   private fun rightClickIntruder() {
      this.stopMovement()
      if (PlayerController.INSTANCE.rightClick()) {
         val var1: Int = intruderClickAttempts++
         state = AutomaticIqPoints.State.WAITING_FOR_PORHTAL_OPEN
         stateTicks = 0
      }
   }

   private fun cleanLabel(raw: String): String {
      val var10000: java.lang.String = StringsKt.trim(
            Regex("\\s+").replace(StringsKt.replace$default(Regex("§.").replace(raw, ""), "Â", "", false, 4, null), " ")
         )
         .toString()
         .toLowerCase(Locale.ROOT)
         return var10000
   }

   private fun onChat(raw: String) {
      val clean: java.lang.String = this.cleanLabel(raw)
      if (active || Config.automaticIqPointsEnabled) {
         if (StringsKt.contains$default(clean, "couldn't warp you", false, 2, null)) {
            if (state === AutomaticIqPoints.State.WAITING_FOR_HUB) {
               state = AutomaticIqPoints.State.WAITING_BEFORE_HUB_RETRY
               stateTicks = 0
               currentAction = "Retrying /hub"
            }
         } else if (StringsKt.contains$default(clean, "select an option", false, 2, null)
            && StringsKt.contains$default(clean, "buy infusion", false, 2, null)
            && state === AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN) {
            state = AutomaticIqPoints.State.WAITING_FOR_INFUSION_GUI
            stateTicks = 0
            currentAction = "Buying Rift Infusion"
         } else if (StringsKt.contains$default(clean, "[npc] wizard: excellent", false, 2, null)
            && state === AutomaticIqPoints.State.WAITING_FOR_INFUSION_CONFIRM) {
            this.getMc().method_1507(null)
            state = AutomaticIqPoints.State.WAITING_BEFORE_WARP_RIFT
            stateTicks = 0
            currentAction = "Warping to Rift"
         } else if (StringsKt.contains$default(clean, "you cannot afford the costs to purchase this", false, 2, null)) {
            this.disableFeature("§cYou must have the resource you picked in the JooonReimagined Config ready!")
         } else if (StringsKt.contains$default(clean, "[npc] ubik von neumann: congratulations, you got the reward", false, 2, null)) {
            val var3: Int = iqPointsGained++
            currentAction = "IQ Point gained"
            state = AutomaticIqPoints.State.WAITING_FOR_UBIK_AFTER_DUE
            stateTicks = 0
         } else if (StringsKt.contains$default(clean, "[npc] ubik von neumann: after due time, come back to play again", false, 2, null)) {
            if (iqPointsGained >= RangesKt.coerceIn(Config.automaticIqPointsToGet, 1, 9)) {
               finishingAfterHub = true
               this.sendHub()
               return
            }

            state = AutomaticIqPoints.State.WAITING_BEFORE_HUB_COMMAND
            stateTicks = 0
            currentAction = "Returning to hub"
         } else if (StringsKt.contains$default(clean, "[npc] ubik von neumann: you did not find the iq point", false, 2, null)) {
            this.sendHub()
         }
      }
   }

   fun onChatComponent(component: Text) {
      val var10001: java.lang.String = component.getString()
      val clean: java.lang.String = this.cleanLabel(var10001)
      if (state === AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN
         && StringsKt.contains$default(clean, "select an option", false, 2, null)
         && StringsKt.contains$default(clean, "buy infusion", false, 2, null)) {
         this.clickFirstChatAction(component)
      }
   }

   fun clickFirstChatAction(component: Text): Boolean {
      val value: AutomaticIqPoints = this

      var sibling: AutomaticIqPoints
      try {
         sibling = value
         sibling = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(component.method_10866().method_10970())
      } catch (var15: java.lang.Throwable) {
         sibling = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
      }

      val click: ClickEvent = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(sibling)) null else sibling) as ClickEvent
      if (click != null) {
         sibling = this

         var var25: AutomaticIqPoints
         try {
            var25 = sibling
            var var10000: Any = click.getClass().getMethods()
            val `$this$firstOrNull$iv`: Array<Any> = var10000 as Array<Any>
            var var9: Int = 0
            val var10: Int = `$this$firstOrNull$iv`.length

            while (true) {
               if (var9 >= var10) {
                  var10000 = null
                  break
               }

               val `element$iv`: Any = `$this$firstOrNull$iv`[var9]
               if ((`$this$firstOrNull$iv`[var9] as Method).getName() == "getValue" || (`$this$firstOrNull$iv`[var9] as Method).getName() == "value") {
                  var10000 = (Method)`element$iv`
                  break
               }

               var9++
            }

            run label118@{
               val method: Method = var10000
               if (var10000 != null) {
                  var10000 = method.invoke(click)
                  if (var10000 != null) {
                     var10000 = var10000.toString()
                     return@label118
                  }
               }

               var10000 = null
            }

            var25 = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(var10000)
         } catch (var16: java.lang.Throwable) {
            var25 = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var16))
         }

         val var18: java.lang.String = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var25)) null else var25) as java.lang.String
         if (var18 != null && !StringsKt.isBlank(var18)) {
            val var31: ClientPlayerEntity = this.getMc().field_1724
            if (var31 != null && var31.field_3944 != null) {
               var31.field_3944.method_45730(StringsKt.removePrefix(var18, "/"))
            }

            true
         }
      }

      for (var30 in component.method_10855()) {
         if (this.clickFirstChatAction(var30 as Text)) {
            true
         }
      }

      false
   }

   private fun sendHub() {
      this.stopMovement()
      WalkToController.stop$default(WalkToController.INSTANCE, null, 1, null)
      currentAction = "Warping to hub"
      val var1: Int = hubRetryCount++
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null && var10000.field_3944 != null) {
         var10000.field_3944.method_45730("hub")
      }

      state = AutomaticIqPoints.State.WAITING_FOR_HUB
      stateTicks = 0
   }

   private fun isWaitingForWorldTransition(): Boolean {
      return state === AutomaticIqPoints.State.WAITING_FOR_HUB || state === AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN
   }

   private fun disableFeature(message: String) {
      Config.automaticIqPointsEnabled = false
      val var2: AutomaticIqPoints = this

      try {
         var var6: AutomaticIqPoints = var2
         JooonConfigManager.INSTANCE.write("jooonreimagined")
         var6 = (AutomaticIqPoints)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var5: java.lang.Throwable) {
         val `$this$disableFeature_u24lambda_u2417`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }

      this.resetSilently()
      JooonReimagined.Companion.sendMessage(message)
   }

   private fun stop(message: String) {
      this.resetSilently()
      JooonReimagined.Companion.sendMessage(message)
   }

   private fun timedOut() {
      if (state != AutomaticIqPoints.State.IDLE) {
         this.disableFeature("§cAutomatic IQ Points timed out and was disabled.")
      } else {
         this.disableFeature("§cAutomatic IQ Points timed out and was disabled.")
         this.resetSilently()
         JooonReimagined.Companion.sendMessage("§cAutomatic IQ Points timed out and was disabled.")
      }
   }

   private fun handOffToPathfinder() {
      this.stopMovement()
      active = true
      state = AutomaticIqPoints.State.PATHFINDING_TO_INTRUDER
      stateTicks = 0
      intruderTarget = null
      intruderClickAttempts = 0
      JooonReimagined.Companion.sendMessage("§aLadder descent complete. Walking to the next point.")
      WalkToController.INSTANCE.startWalkTo(-49.0, 104.0, 71.0)
   }

   private fun resetSilently() {
      this.stopMovement()
      active = false
      state = AutomaticIqPoints.State.IDLE
      stateTicks = 0
      wasOnLadder = false
      settledTicks = 0
      intruderTarget = null
      intruderClickAttempts = 0
      porhtalOpenRetries = 0
      sinClickAttempts = 0
      preTeleportPos = null
      finishingAfterHub = false
   }

   private fun ensureOverlayReady() {
      if (!overlayReady) {
         overlayReady = true
         if (!PersistentState.automaticIqPointsHudInitDone) {
            PersistentState.automaticIqPointsHudX = 10
            PersistentState.automaticIqPointsHudY = 10
            PersistentState.automaticIqPointsHudInitDone = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
         }

         var var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("automaticIqPointsHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "automaticIqPointsHud", "Automatic IQ Points HUD", PersistentState.automaticIqPointsHudX, PersistentState.automaticIqPointsHudY, 190, 68
               )
            }

         var10000.renderFunction = lambda_18@{ context: DrawContext, x: Int, y: Int, var3: Float ->
            if (!INSTANCE.shouldRenderHud()) {
               return@lambda_18 Unit.INSTANCE
            } else {
               INSTANCE.renderHud(context, x, y)
               return@lambda_18 Unit.INSTANCE
            }
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.automaticIqPointsHudX = x
            PersistentState.automaticIqPointsHudY = y
            PersistentState.automaticIqPointsHudInitDone = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }
         var10000.register()
      }
   }

   private fun shouldRenderHud(): Boolean {
      if (!Config.automaticIqPointsEnabled && !active) {
         var var2: java.lang.String
         run label30@{
            val var10000: Screen = this.getMc().field_1755
            if (var10000 != null) {
               val var1: Class = var10000.getClass()
               if (var1 != null) {
                  var2 = var1.getSimpleName()
                  return@label30
               }
            }

            var2 = null
         }

         if (!(var2 == "MovableOverlayScreen")) {
            return false
         }
      }

      return true
   }

   fun renderCenterActiveHud(context: DrawContext) {
      if (active) {
         val text: java.lang.String = if (state === AutomaticIqPoints.State.WAITING_FOR_RIFT_START)
            "§a§lJooon IQ Points ACTIVE! §eWarp to the Rift once!"
            else
            "§a§lJooon IQ Points ACTIVE! §fPress §eENTER §fto stop!"
            context.method_51439(
            this.getMc().field_1772,
            Text.method_43470(text) as Text,
            this.getMc().method_22683().method_4486() / 2 - this.getMc().field_1772.method_1727(text) / 2,
            this.getMc().method_22683().method_4502() / 2 + 15,
            -1,
            false
         )
      }
   }

   fun renderHud(context: DrawContext, x: Int, y: Int) {
      val lines: java.util.List = CollectionsKt.listOf(
         arrayOf("Action: ${currentAction}", "IQ Points: ${iqPointsGained}/${RangesKt.coerceIn(Config.automaticIqPointsToGet, 1, 9)}")
      )
      val panelHeight: java.util.Iterator = lines.iterator()
      if (!panelHeight.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var24: Int = INSTANCE.getMc().field_1772.method_1727(panelHeight.next() as java.lang.String)

         while (panelHeight.hasNext()) {
            val var27: Int = INSTANCE.getMc().field_1772.method_1727(panelHeight.next() as java.lang.String)
            if (var24 < var27) {
               var24 = var27
            }
         }

         val panelWidth: Int = Math.max(this.getMc().field_1772.method_1727("Automatic IQ Points") + 24, var24 + 18)
         val var23: Int = 14 + this.getMc().field_1772.field_2000 + 10 + lines.size() * (this.getMc().field_1772.field_2000 + 3) + 8
         val var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("automaticIqPointsHud")
         if (var10000 != null) {
            var10000.width = panelWidth
            var10000.height = var23
         }

         val textColor: Int = -8519839
         val textShadow: Int = -15712501
         context.method_25294(x, y, x + panelWidth, y + var23, -871885814)
         context.method_25294(x + 2, y + 2, x + panelWidth - 2, y + var23 - 2, -871686386)
         context.method_25294(x + 3, y + 3, x + panelWidth - 3, y + 18, -1441780462)
         context.method_73198(x, y, panelWidth, var23, -9306283)
         context.method_73198(x + 1, y + 1, panelWidth - 2, var23 - 2, -13660382)
         this.renderHudSweep(context, x + 3, y + 3, panelWidth - 6, 15)
         this.drawHudText(
            context, "Automatic IQ Points", x + (panelWidth - this.getMc().field_1772.method_1727("Automatic IQ Points")) / 2, y + 5, -5701745, -15180784
         )
         var lineY: Int = y + 24

         for (line in lines) {
            this.drawHudText(context, line, x + 8, lineY, textColor, textShadow)
            lineY += this.getMc().field_1772.field_2000 + 3
         }
      }
   }

   fun drawHudText(context: DrawContext, text: java.lang.String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.method_51439(this.getMc().field_1772, Text.method_43470(text) as Text, x + 1, y + 1, shadowColor, false)
      context.method_51439(this.getMc().field_1772, Text.method_43470(text) as Text, x, y, color, false)
   }

   fun renderHudSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {
      val sweepWidth: Int = Math.max(18, width / 5)
      val sweepX: Int = x - sweepWidth + (int)((width + sweepWidth) * ((float)(System.currentTimeMillis() % 2300L) / (float)2300L))
      val left: Int = RangesKt.coerceAtLeast(sweepX, x)
      val right: Int = RangesKt.coerceAtMost(sweepX + sweepWidth, x + width)
      if (right > left) {
         context.method_25294(left, y, right, y + height, 863895393)
      }
   }

   private fun stopMovement() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressSprint(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
   }

   @JvmStatic
   fun {
      val var10000: Vec3d = Vec3d.field_1353
      selectedBoxTarget = var10000
   }

   private enum class State {
      IDLE,
      WAITING_FOR_RIFT_START,
      ROTATING_TO_LADDER,
      APPROACH_LADDER,
      DESCENDING,
      PATHFINDING_TO_INTRUDER,
      ROTATING_TO_INTRUDER,
      MOVING_CLOSER_TO_INTRUDER,
      WAITING_FOR_PORHTAL_OPEN,
      WAITING_BEFORE_7TH_SIN_CLICK,
      WAITING_FOR_TELEPORT,
      POST_TELEPORT_DELAY,
      PATHFINDING_TO_BOX,
      ROTATING_TO_BOX,
      MOVING_CLOSER_TO_BOX,
      WAITING_FOR_CHOOSE_ME,
      WAITING_BEFORE_CHOOSE_ME_CLICK,
      WAITING_FOR_KEEP,
      WAITING_BEFORE_KEEP_CLICK,
      WAITING_FOR_UBIK_RESULT,
      WAITING_FOR_UBIK_AFTER_DUE,
      WAITING_BEFORE_HUB_COMMAND,
      WAITING_FOR_HUB,
      WAITING_BEFORE_HUB_RETRY,
      WAITING_BEFORE_WARP_RIFT,
      WAITING_FOR_RIFT_RETURN,
      WAITING_FOR_INFUSION_GUI,
      WAITING_BEFORE_INFUSION_CLICK,
      WAITING_FOR_INFUSION_CONFIRM;

      @JvmStatic
      fun getEntries(): EnumEntries<AutomaticIqPoints.State> {
         $ENTRIES
      }
   }
}
