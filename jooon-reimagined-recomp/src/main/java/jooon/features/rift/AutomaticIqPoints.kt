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

object AutomaticIqPoints {
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
   private var active: Boolean
   private var state: jooon.features.rift.AutomaticIqPoints.State = AutomaticIqPoints.State.IDLE
   private var stateTicks: Int
   private var wasOnLadder: Boolean
   private var lastY: Double = 122.0
   private var settledTicks: Int
   
   private Entity intruderTarget;
   private var intruderClickAttempts: Int
   private var porhtalOpenRetries: Int
   private var sinClickAttempts: Int
   
   private Vec3d preTeleportPos;
   private var overlayReady: Boolean
   private var wasConfigEnabled: Boolean
   private var shownWarpPrompt: Boolean
   private var iqPointsGained: Int
   private var currentAction: String = "Idle"
   
   private Vec3d selectedBoxTarget;
   
   private Entity selectedBoxEntity;
   private var hubRetryCount: Int
   private var warpRiftStartedAtTick: Int
   private var finishingAfterHub: Boolean
   private var enterWasDown: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
   }

   fun tick(client: MinecraftClient) {
      this.ensureOverlayReady()
      this.handleEnterStop(client)

      if (client.player != null && client.world != null) {

         if (Config.automaticIqPointsEnabled && !active && client.currentScreen == null) {
            this.beginWaitingForRift()
         } else if (!Config.automaticIqPointsEnabled && wasConfigEnabled) {
            WalkToController.stop$default(WalkToController.INSTANCE, null, 1, null)
            this.resetSilently()
         }

         wasConfigEnabled = Config.automaticIqPointsEnabled
         if (active) {

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


      var `this24lambda_u240`: AutomaticIqPoints
      try {
         `this24lambda_u240` = var5
         `this24lambda_u240` = Result(client.getWindow().getHandle())
      } catch (var8: java.lang.Throwable) {
         `this24lambda_u240` = Result(ResultKt.createFailure(var8))
      }

         if (Result.isFailure/* $VF was: isFailure-impl */(`this24lambda_u240`)) null else `this24lambda_u240`
      ) as Long
      if (var10000 != null) {


         if (active && client.currentScreen == null && enterDown && !enterWasDown) {
            this.disableFeature("§cAutomatic IQ Points stopped.")
         }

         enterWasDown = enterDown
      }
   }

   private fun actionForState(): String {
      var var10000: String
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

      lastY = if (var10000 != null) var10000.getY() else 122.0
      settledTicks = 0
      intruderTarget = null
      intruderClickAttempts = 0
      porhtalOpenRetries = 0
      sinClickAttempts = 0
      preTeleportPos = null
      finishingAfterHub = false
      selectedBoxTarget = Vec3d.z
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
      selectedBoxTarget = Vec3d.z
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
         wasOnLadder = player.isClimbing()
         lastY = player.getY()
         settledTicks = 0
         this.stopMovement()
         JooonReimagined.Companion.sendMessage("§aLadder reached. Waiting for the descent to finish.")
      } else if (stateTicks > 220) {
         this.stop("§cAutomatic IQ Points [RIFT] could not reach the ladder entry.")
      } else if (!this.isFacingLadder(player)) {
         this.stopMovement()
      } else {
         PlayerController.pressForward(true)
         PlayerController.pressBack(false)
         PlayerController.pressLeft(false)
         PlayerController.pressRight(false)
         PlayerController.pressSprint(false)
         PlayerController.pressJump(false)
         PlayerController.pressSneak(false)
      }
   }

   fun tickDescending(player: ClientPlayerEntity) {
      this.lookForDescent(player)
      this.stopMovement()

      if (onLadder) {
         wasOnLadder = true
      }

      if (Math.abs(player.getY() - lastY) < 0.003) {

      } else {
         settledTicks = 0
      }

      lastY = player.getY()
      if (this.hasReachedBottom(player, onLadder)) {
         this.handOffToPathfinder()
      }
   }

   fun tickPathfindingToIntruder(player: ClientPlayerEntity) {
      if (!WalkToController.isActive()) {
         if (!this.isNearAfterLadderPoint(player)) {
            this.stop("§cAutomatic IQ Points [RIFT] stopped because the pathfinder did not reach the Intruder point.")
         } else {

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


            var10000 = if (intruderTarget.isAlive() && !it.isRemoved()) var4 else null
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


            var10000 = if (intruderTarget.isAlive() && !it.isRemoved()) var4 else null
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

         this.lookAt(player, hitPoint)
         if (this.isWithinIntruderClickRange(player, hitPoint)) {
            this.stopMovement()
            state = AutomaticIqPoints.State.ROTATING_TO_INTRUDER
            stateTicks = 0
         } else if (stateTicks > 80) {
            this.stop("§cAutomatic IQ Points [RIFT] could not move close enough to The Intruder.")
         } else {
            if (this.isYawAligned(player, hitPoint, 8.0F)) {
               PlayerController.pressForward(true)
               PlayerController.pressBack(false)
               PlayerController.pressLeft(false)
               PlayerController.pressRight(false)
               PlayerController.pressSprint(false)
               PlayerController.pressJump(false)
               PlayerController.pressSneak(false)
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

               if (intruderTarget != null) {


                  var10000 = if (intruderTarget.isAlive() && !it.isRemoved()) var3 else null
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

            preTeleportPos = player.getEntityPos()
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

            preTeleportPos = player.getEntityPos()
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
         WalkToController.startWalkTo(23.0, 107.0, 53.0)
      }
   }

   fun tickPathfindingToBox(player: ClientPlayerEntity) {
      if (!WalkToController.isActive()) {
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

      this.lookAt(player, target)
      if (this.isWithinBoxClickRange(player, target)) {
         this.stopMovement()
         state = AutomaticIqPoints.State.ROTATING_TO_BOX
         stateTicks = 0
      } else if (stateTicks > 80) {
         this.timedOut()
      } else {
         if (this.isYawAligned(player, target, 8.0F)) {
            PlayerController.pressForward(true)
            PlayerController.pressBack(false)
            PlayerController.pressLeft(false)
            PlayerController.pressRight(false)
            PlayerController.pressSprint(false)
            PlayerController.pressJump(false)
            PlayerController.pressSneak(false)
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

         if (var10000 != null && var10000.networkHandler != null) {
            var10000.networkHandler.sendChatCommand("warp rift")
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
      if (this.getMc().currentScreen is HandledScreen) {
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
      player.isClimbing()
         || Math.hypot(player.getX() - -43.488, player.getZ() - 72.8) <= 0.58
            && player.getY() <= 122.75
            && (player.getY() < 121.9 || player.getVelocity().y < -0.015)
         }

   fun hasReachedBottom(player: ClientPlayerEntity, onLadder: Boolean): Boolean {
      wasOnLadder && !(player.getY() > 119.0) && (!onLadder && player.isOnGround() || player.isOnGround() && settledTicks >= 16)
   }

   fun isNearAfterLadderPoint(player: ClientPlayerEntity): Boolean {



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

      val var10000: HandledScreen = var2 as? HandledScreen
      if ((var2 as? HandledScreen) == null) {
         return false
      } else {

         return this.cleanLabel(var10001) == "porhtal"
      }
   }

   fun click7thSin(player: ClientPlayerEntity): Boolean {

      if ((slot as? HandledScreen) == null) {
return false
      } else if (!this.isPorhtalScreen()) {
return false
      } else {


         val var7: java.util.Iterator = (var13 as java.lang.Iterable).iterator()

         while (true) {
            if (!var7.hasNext()) {
               var17 = null
break
            }

            var `element$iv`: Any
            run label57@{
               `element$iv` = var7.next()

               if (!var14.isEmpty()) {


                  if (contains$default(var15.cleanLabel(var10001), "the 7th sin", false, 2, null)) {
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

         if (var17 as Slot == null) {
return false
         } else {

            if (var19 != null) {
               var19.clickSlot(var10000.syncId, var18.id, 0, SlotActionType.PICKUP, player as PlayerEntity)
return true
            } else {
return false
            }
         }
      }
   }

   fun hasTeleported(player: ClientPlayerEntity): Boolean {
      preTeleportPos != null && preTeleportPos.squaredDistanceTo(player.getEntityPos()) >= 25.0
   }

   fun isAtRiftStart(player: ClientPlayerEntity): Boolean {
      Math.abs(player.getX() - -44.3) <= 0.35 && Math.abs(player.getY() - 122.0) <= 0.75 && Math.abs(player.getZ() - 69.3) <= 0.35
   }

   fun isAtHub(player: ClientPlayerEntity): Boolean {
      Math.abs(player.getX() - 0.5) <= 1.5 && Math.abs(player.getY() - 77.0) <= 2.0 && Math.abs(player.getZ() - -0.5) <= 1.5
   }

   fun isNearFinalPoint(player: ClientPlayerEntity): Boolean {



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
return var10000
   }

   fun isWithinBoxClickRange(player: ClientPlayerEntity, target: Vec3d): Boolean {
      Vec3d(player.getX(), player.getEyeY(), player.getZ()).squaredDistanceTo(target) <= 7.0225
   }

   private fun hasBoxArmorStand(text: String): Boolean {

      if (var10000 == null) {
         return false
      } else {

         val var17: java.lang.Iterable = var10000.getEntities()
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

                  if (var15.isAlive()
                     && !var15.isRemoved()
                     && Math.abs(var15.getX() - target.x) <= 1.0
                     && Math.abs(var15.getZ() - target.z) <= 1.0
                     && var15.getY() >= target.y
                     && var15.getY() <= target.y + 3.0) {

                     var var10001: Text = var15.getCustomName()
                     if (var10001 == null) {
                        var10001 = var15.getName()
                     }

                     if (contains$default(var18.cleanLabel(var21), text, false, 2, null)) {
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
      this.isWithinBoxClickRange(player, selectedBoxTarget) && PlayerController.rightClick()
   }

   fun findSelectedBoxEntity(target: Vec3d): Entity {

      if (var10000 == null) {
return null
      } else {
         val var35: java.lang.Iterable = var10000.getEntities()
         var `maxElem$iv`: java.util.Collection = ArrayList()

         for (headScore in var35) {
            if (headScore is ArmorStandEntity) {
               `maxElem$iv`.add(headScore)
            }
         }

         val var21: java.lang.Iterable = `maxElem$iv` as java.util.List
         `maxElem$iv` = ArrayList()

         for (var31 in var21) {
            if ((var31 as ArmorStandEntity).isAlive()
               && !(var31 as ArmorStandEntity).isRemoved()
               && isEntityNearSelectedBox((var31 as ArmorStandEntity) as Entity, target)) {
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
               var var27: Double = (if (!(var24 as ArmorStandEntity).getEquippedStack(EquipmentSlot.HEAD).isEmpty()) 10.0 else 0.0)
                  - (var24 as ArmorStandEntity).getEntityPos().squaredDistanceTo(target)

               do {


                     - (var30 as ArmorStandEntity).getEntityPos().squaredDistanceTo(target)
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
      Math.abs(entity.getX() - target.x) <= 0.9
         && Math.abs(entity.getZ() - target.z) <= 0.9
         && entity.getY() >= target.y - 2.0
         && entity.getY() <= target.y + 1.25
      }

   fun clickInfusion(player: ClientPlayerEntity): Boolean {

      if ((slot as? HandledScreen) == null) {
return false
      } else {

var var24: Slot
         when (AutomaticIqPoints.WhenMappings.$EnumSwitchMapping$2[Config.automaticIqPointsInfusion.ordinal()]) {
            1 -> {


               run label96@{
                  for (var16 in var25 as java.lang.Iterable) {
                     if ((var16 as Slot).id == 11) {
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

                        if (!var20.isEmpty()) {
                           if (var20.isOf(Items.DIAMOND)) {
                              return@label88
                           }


                           if (contains$default(var21.cleanLabel(var10001), "bits", false, 2, null)) {
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
return false
         } else {

            if (var27 != null) {
               var27.clickSlot(var10000.syncId, var24.id, 0, SlotActionType.PICKUP, player as PlayerEntity)
return true
            } else {
return false
            }
         }
      }
   }

   fun lookAtLadder(player: ClientPlayerEntity) {
      player.setYaw(this.smoothRotation(player.getYaw(), -15.0F, 7.5F))
      player.setPitch(this.smoothRotation(player.getPitch(), 0.0F, 4.0F))
   }

   fun lookForDescent(player: ClientPlayerEntity) {
      player.setYaw(this.smoothRotation(player.getYaw(), 153.0F, 7.5F))
      player.setPitch(this.smoothRotation(player.getPitch(), 0.0F, 4.0F))
   }

   fun lookAt(player: ClientPlayerEntity, target: Vec3d) {






      player.setYaw(this.smoothRotation(player.getYaw(), targetYaw, 7.5F))
      player.setPitch(this.smoothRotation(player.getPitch(), targetPitch, 4.0F))
   }

   fun isFacingLadder(player: ClientPlayerEntity): Boolean {
      Math.abs(MathHelper.wrapDegrees(-15.0F - player.getYaw())) <= 3.0F && Math.abs(MathHelper.wrapDegrees(0.0F - player.getPitch())) <= 2.0F
   }

   fun isLookingAt(player: ClientPlayerEntity, target: Vec3d): Boolean {



      Math.abs(MathHelper.wrapDegrees((MathHelper.atan2(dz, dx) * (180.0 / Math.PI)).toFloat() - 90.0F - player.getYaw())) <= 3.0F
         && Math.abs(
               MathHelper.wrapDegrees(
                  (-(MathHelper.atan2(dy, (Math.hypot(dx, dz)).coerceAtLeast(0.001)) * (180.0 / Math.PI))).toFloat() - player.getPitch()
               )
            )
            <= 2.0F
         }

   fun isYawAligned(player: ClientPlayerEntity, target: Vec3d, tolerance: Float): Boolean {
      Math.abs(
            MathHelper.wrapDegrees(
               (MathHelper.atan2(target.z - player.getZ(), target.x - player.getX()) * (180.0 / Math.PI)).toFloat()
                  - 90.0F
                  - player.getYaw()
            )
         )
         <= tolerance
      }

   private fun smoothRotation(current: Float, target: Float, maxStep: Float): Float {

      return if (Math.abs(diff) <= maxStep) target else current + Math.signum(diff) * maxStep
   }

   fun findIntruderTarget(player: ClientPlayerEntity): Entity {

      if (var10000 == null) {
return null
      } else {
         val var60: java.lang.Iterable = var10000.getEntities()
         var `iterator$iv`: java.util.Collection = ArrayList()

         for (`e$iv` in var60) {
            if (`e$iv` is ArmorStandEntity) {
               `iterator$iv`.add(`e$iv`)
            }
         }

         val var28: java.lang.Iterable = `iterator$iv` as java.util.List
         `iterator$iv` = ArrayList()

         for (var49 in var28) {
            if ((var49 as ArmorStandEntity).isAlive()
               && !(var49 as ArmorStandEntity).isRemoved()
               && (var49 as ArmorStandEntity).squaredDistanceTo(player as Entity) <= 49.0) {
               `iterator$iv`.add(var49)
            }
         }

         val var29: java.util.Iterator = (`iterator$iv` as java.util.List).iterator()

         while (true) {
            if (var29.hasNext()) {



               var var10001: Text = var39.getCustomName()
               if (var10001 == null) {
                  var10001 = var39.getName()
               }

               if (!contains$default(var62.cleanLabel(var67), "the intruder", false, 2, null)) {
return continue
               }

               var61 = var34
break
            }

            var61 = null
break
         }

         if (var61 as ArmorStandEntity == null) {
return null
         } else {



            val var65: java.lang.Iterable = var10000.getEntities()
            var `destination$iv$ivx`: java.util.Collection = ArrayList()

            for (var54 in var65) {
               if (var54 is ArmorStandEntity) {
                  `destination$iv$ivx`.add(var54)
               }
            }

            val var36: java.lang.Iterable = `destination$iv$ivx` as java.util.List
            `destination$iv$ivx` = ArrayList()

            for (var55 in var36) {
               if ((var55 as ArmorStandEntity).isAlive()
                  && !(var55 as ArmorStandEntity).isRemoved()
                  && var55 as ArmorStandEntity != labelStand
                  && Math.abs((var55 as ArmorStandEntity).getX() - labelStand.getX()) <= 1.35
                  && Math.abs((var55 as ArmorStandEntity).getZ() - labelStand.getZ()) <= 1.35
                  && (var55 as ArmorStandEntity).getY() < labelStand.getY()
                  && (var55 as ArmorStandEntity).getY() >= labelStand.getY() - 3.0) {
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
                  var var48: Double = (if (!(var42 as ArmorStandEntity).getEquippedStack(EquipmentSlot.HEAD).isEmpty()) 10.0 else 0.0)
                     - (var42 as ArmorStandEntity).getEntityPos().squaredDistanceTo(var64)

                  do {


                        - (var53 as ArmorStandEntity).getEntityPos().squaredDistanceTo(labelCenter)
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


      val var11: Double
      if (entity is ArmorStandEntity) {

         var11 = if (!var10.isEmpty()) (entity as ArmorStandEntity).getY() + 1.55 else (var10000.minY + var10000.maxY) * 0.5 + 0.25
      } else {
         var11 = (var10000.minY + var10000.maxY) * 0.5
      }

      Vec3d(x, var11, (var10000.minZ + var10000.maxZ) * 0.5)
   }

   fun isWithinIntruderClickRange(player: ClientPlayerEntity, hitPoint: Vec3d): Boolean {


      eye.squaredDistanceTo(hitPoint) <= range * range
   }

   private fun rightClickIntruder() {
      this.stopMovement()
      if (PlayerController.rightClick()) {

         state = AutomaticIqPoints.State.WAITING_FOR_PORHTAL_OPEN
         stateTicks = 0
      }
   }

   private fun cleanLabel(raw: String): String {

            Regex("\\s+").replace(replace$default(Regex("§.").replace(raw, ""), "Â", "", false, 4, null), " ")
         )
         .toString()
         .toLowerCase(Locale.ROOT)
         return var10000
   }

   private fun onChat(raw: String) {

      if (active || Config.automaticIqPointsEnabled) {
         if (contains$default(clean, "couldn't warp you", false, 2, null)) {
            if (state === AutomaticIqPoints.State.WAITING_FOR_HUB) {
               state = AutomaticIqPoints.State.WAITING_BEFORE_HUB_RETRY
               stateTicks = 0
               currentAction = "Retrying /hub"
            }
         } else if (contains$default(clean, "select an option", false, 2, null)
            && contains$default(clean, "buy infusion", false, 2, null)
            && state === AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN) {
            state = AutomaticIqPoints.State.WAITING_FOR_INFUSION_GUI
            stateTicks = 0
            currentAction = "Buying Rift Infusion"
         } else if (contains$default(clean, "[npc] wizard: excellent", false, 2, null)
            && state === AutomaticIqPoints.State.WAITING_FOR_INFUSION_CONFIRM) {
            this.getMc().setScreen(null)
            state = AutomaticIqPoints.State.WAITING_BEFORE_WARP_RIFT
            stateTicks = 0
            currentAction = "Warping to Rift"
         } else if (contains$default(clean, "you cannot afford the costs to purchase this", false, 2, null)) {
            this.disableFeature("§cYou must have the resource you picked in the JooonReimagined Config ready!")
         } else if (contains$default(clean, "[npc] ubik von neumann: congratulations, you got the reward", false, 2, null)) {

            currentAction = "IQ Point gained"
            state = AutomaticIqPoints.State.WAITING_FOR_UBIK_AFTER_DUE
            stateTicks = 0
         } else if (contains$default(clean, "[npc] ubik von neumann: after due time, come back to play again", false, 2, null)) {
            if (iqPointsGained >= (Config.automaticIqPointsToGet).coerceIn(1, 9)) {
               finishingAfterHub = true
               this.sendHub()
return return
            }

            state = AutomaticIqPoints.State.WAITING_BEFORE_HUB_COMMAND
            stateTicks = 0
            currentAction = "Returning to hub"
         } else if (contains$default(clean, "[npc] ubik von neumann: you did not find the iq point", false, 2, null)) {
            this.sendHub()
         }
      }
   }

   fun onChatComponent(component: Text) {


      if (state === AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN
         && contains$default(clean, "select an option", false, 2, null)
         && contains$default(clean, "buy infusion", false, 2, null)) {
         this.clickFirstChatAction(component)
      }
   }

   fun clickFirstChatAction(component: Text): Boolean {


      var sibling: AutomaticIqPoints
      try {
         sibling = value
         sibling = Result(component.getStyle().getClickEvent())
      } catch (var15: java.lang.Throwable) {
         sibling = Result(ResultKt.createFailure(var15))
      }

      if (click != null) {
         sibling = this

         var var25: AutomaticIqPoints
         try {
            var25 = sibling
            var var10000: Any = click.getClass().getMethods()
            val `this$iv`: Array<Any> = var10000 as Array<Any>
            var var9: Int = 0


            while (true) {
               if (var9 >= var10) {
                  var10000 = null
break
               }

               val `element$iv`: Any = `this$iv`[var9]
               if ((`this$iv`[var9] as Method).getName() == "getValue" || (`this$iv`[var9] as Method).getName() == "value") {
                  var10000 = (Method)`element$iv`
break
               }

               var9++
            }

            run label118@{

               if (var10000 != null) {
                  var10000 = method.invoke(click)
                  if (var10000 != null) {
                     var10000 = var10000.toString()
                     return@label118
                  }
               }

               var10000 = null
            }

            var25 = Result(var10000)
         } catch (var16: java.lang.Throwable) {
            var25 = Result(ResultKt.createFailure(var16))
         }

         if (var18 != null && !isBlank(var18)) {

            if (var31 != null && var31.networkHandler != null) {
               var31.networkHandler.sendChatCommand(removePrefix(var18, "/"))
            }
return true
         }
      }

      for (var30 in component.getSiblings()) {
         if (this.clickFirstChatAction(var30 as Text)) {
return true
         }
      }
return false
   }

   private fun sendHub() {
      this.stopMovement()
      WalkToController.stop$default(WalkToController.INSTANCE, null, 1, null)
      currentAction = "Warping to hub"


      if (var10000 != null && var10000.networkHandler != null) {
         var10000.networkHandler.sendChatCommand("hub")
      }

      state = AutomaticIqPoints.State.WAITING_FOR_HUB
      stateTicks = 0
   }

   private fun isWaitingForWorldTransition(): Boolean {
      return state === AutomaticIqPoints.State.WAITING_FOR_HUB || state === AutomaticIqPoints.State.WAITING_FOR_RIFT_RETURN
   }

   private fun disableFeature(message: String) {
      Config.automaticIqPointsEnabled = false


      try {
         var var6: AutomaticIqPoints = var2
         JooonConfigManager.write("jooonreimagined")
         var6 = Result(Unit)
      } catch (var5: java.lang.Throwable) {
         val `this24lambda_u2417`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
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
      WalkToController.startWalkTo(-49.0, 104.0, 71.0)
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
            JooonConfigManager.write("jooonreimagined_state")
         }

         var var10000: MovableOverlay = MovableOverlayManager.getOverlay("automaticIqPointsHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "automaticIqPointsHud", "Automatic IQ Points HUD", PersistentState.automaticIqPointsHudX, PersistentState.automaticIqPointsHudY, 190, 68
               )
            }

         var10000.renderFunction = lambda_18@{ context: DrawContext, x: Int, y: Int, var3: Float ->
            if (!shouldRenderHud()) {
               return@lambda_18 Unit
            } else {
               renderHud(context, x, y)
               return@lambda_18 Unit
            }
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.automaticIqPointsHudX = x
            PersistentState.automaticIqPointsHudY = y
            PersistentState.automaticIqPointsHudInitDone = true
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }
         var10000.register()
      }
   }

   private fun shouldRenderHud(): Boolean {
      if (!Config.automaticIqPointsEnabled && !active) {
         var var2: String
         run label30@{

            if (var10000 != null) {

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

            "§a§lJooon IQ Points ACTIVE! §eWarp to the Rift once!"
return else
            "§a§lJooon IQ Points ACTIVE! §fPress §eENTER §fto stop!"
            context.drawText(
            this.getMc().textRenderer,
            Text.literal(text) as Text,
            this.getMc().getWindow().getScaledWidth() / 2 - this.getMc().textRenderer.getWidth(text) / 2,
            this.getMc().getWindow().getScaledHeight() / 2 + 15,
            -1,
return false
         )
      }
   }

   fun renderHud(context: DrawContext, x: Int, y: Int) {
      val lines: java.util.List = listOf(
         arrayOf("Action: ${currentAction}", "IQ Points: ${iqPointsGained}/${(Config.automaticIqPointsToGet).coerceIn(1, 9)}")
      )
      val panelHeight: java.util.Iterator = lines.iterator()
      if (!panelHeight.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var24: Int = getMc().textRenderer.getWidth(panelHeight.next() as String)

         while (panelHeight.hasNext()) {

            if (var24 < var27) {
               var24 = var27
            }
         }



         if (var10000 != null) {
            var10000.width = panelWidth
            var10000.height = var23
         }


         context.fill(x, y, x + panelWidth, y + var23, -871885814)
         context.fill(x + 2, y + 2, x + panelWidth - 2, y + var23 - 2, -871686386)
         context.fill(x + 3, y + 3, x + panelWidth - 3, y + 18, -1441780462)
         context.drawStrokedRectangle(x, y, panelWidth, var23, -9306283)
         context.drawStrokedRectangle(x + 1, y + 1, panelWidth - 2, var23 - 2, -13660382)
         this.renderHudSweep(context, x + 3, y + 3, panelWidth - 6, 15)
         this.drawHudText(
            context, "Automatic IQ Points", x + (panelWidth - this.getMc().textRenderer.getWidth("Automatic IQ Points")) / 2, y + 5, -5701745, -15180784
         )
         var lineY: Int = y + 24

         for (line in lines) {
            this.drawHudText(context, line, x + 8, lineY, textColor, textShadow)
            lineY += this.getMc().textRenderer.fontHeight + 3
         }
      }
   }

   fun drawHudText(context: DrawContext, text: String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.drawText(this.getMc().textRenderer, Text.literal(text) as Text, x + 1, y + 1, shadowColor, false)
      context.drawText(this.getMc().textRenderer, Text.literal(text) as Text, x, y, color, false)
   }

   fun renderHudSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {




      if (right > left) {
         context.fill(left, y, right, y + height, 863895393)
      }
   }

   private fun stopMovement() {
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressSprint(false)
      PlayerController.pressJump(false)
      PlayerController.pressSneak(false)
   }

   
   fun {

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

      
      fun getEntries(): EnumEntries<AutomaticIqPoints.State> {
         $ENTRIES
      }
   }
}
