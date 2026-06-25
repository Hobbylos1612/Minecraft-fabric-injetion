package jooon.features.mirrorverse

import jooon.config.Config
import jooon.util.PlayerController
import kotlin.concurrent.ThreadsKt
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

object DRSv2 {
   private const val ARRIVAL_CENTER_EPS: Double = 0.34
   private const val CORRECTION_TAP_MS: Long = 26L
   private const val CORRECTION_TAP_COOLDOWN_MS: Long = 90L
   private const val LOOK_TARGET_YAW: Float = 90.0F
   private const val LOOK_TARGET_PITCH: Float = 90.0F
   private const val LOOK_MAX_STEP_DEG: Float = 18.0F
   private const val JUMP_DELAY_MS: Long = 500L
   private const val PUNCH_DELAY_MS: Long = 800L
   private val PITCH_LIST: Set<Double> = SetsKt.setOf(arrayOf(0.52380955F, 1.0476191F, 0.6984127F, 0.8888889F))
   private const val COMPLETE_PITCH: Double = 0.74603176F
   private const val TARGET_X: Int = -265
   private const val TARGET_Y: Int = 32
   private const val TARGET_Z: Int = -108
   private var beats: Int
   private var isActive: Boolean
   private var inMirrorverse: Boolean
   private var firstTime: Boolean = true
   private var isCompleted: Boolean
   private var lastBeatTime: Long
   private var unloadGeneration: Int
   private var nextCorrectionTapAtMs: Long
   private var moveTargetIndex: Int
   private var isCenteringNow: Boolean
   private var heldMoveKey: jooon.features.mirrorverse.DRSv2.MoveTap?
   private val routeCenters: Array<Pair<Double, Double>>
   private var sneakDown: Boolean
   private var jumpDown: Boolean
   private var lastPadState: jooon.features.mirrorverse.DRSv2.PadState = DRSv2.PadState.OTHER

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         unloadInterrupted()
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         unloadInterrupted()
      })
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         onTick(client)
      })
      HudRenderCallback.EVENT.register({ context: DrawContext, var1: RenderTickCounter ->
         onRender(context)
      })
   }

   private fun chat(msg: String) {

      if (var10000 != null) {
         var10000.sendMessage(Text.literal(msg) as Text, false)
      }
   }

   fun checkInMirrorverse(client: MinecraftClient): Boolean {
      client.player != null && client.player.squaredDistanceTo(-265.0, 32.0, -108.0) < 10000.0
   }

   private fun currentPadState(): jooon.features.mirrorverse.DRSv2.PadState {

      if (var10000.player == null) {
         return DRSv2.PadState.OFFPAD
      } else {

         if (var10000.world == null) {
            return DRSv2.PadState.OFFPAD
         } else {




            if (bx == -265 && by == 32 && bz == -108) {

               if (var9 == Blocks.STONE) {
                  return DRSv2.PadState.STONE
               } else {
                  return if (!(var9 == Blocks.LIME_STAINED_GLASS) && !(var9 == Blocks.LIME_STAINED_GLASS_PANE) && !(var9 == Blocks.GREEN_STAINED_GLASS) && !(var9 == Blocks.GREEN_STAINED_GLASS_PANE))
                     DRSv2.PadState.OTHER
return else
                     DRSv2.PadState.GREEN
                  }
            } else {
               return DRSv2.PadState.OFFPAD
            }
         }
      }
   }

   private fun releaseAllKeys() {
      this.releaseMovementKeys()
      if (sneakDown) {
         sneakDown = false
         PlayerController.pressSneak(false)
      }

      if (jumpDown) {
         jumpDown = false
         PlayerController.pressJump(false)
      }
   }

   private fun releaseMovementKeys() {
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      heldMoveKey = null
   }

   private fun setHeldMovement(direction: jooon.features.mirrorverse.DRSv2.MoveTap?) {
      if (heldMoveKey != direction) {
         this.releaseMovementKeys()
         if (direction != null) {
            when (DRSv2.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
               1 -> PlayerController.pressForward(true)
               2 -> PlayerController.pressBack(true)
               3 -> PlayerController.pressLeft(true)
               4 -> PlayerController.pressRight(true)
               else -> throw NoWhenBranchMatchedException()
            }

            heldMoveKey = direction
         }
      }
   }

   private fun setSneak(down: Boolean) {
      if (sneakDown != down) {
         sneakDown = down
         PlayerController.pressSneak(down)
      }
   }

   private fun canRunDelayed(generation: Int): Boolean {
      return generation == unloadGeneration && isActive && Config.drSolverEnabled && this.getMc().player != null && this.getMc().world != null
   }

   private fun hasLoadedState(): Boolean {
      return isActive || inMirrorverse || beats != 0 || heldMoveKey != null || sneakDown || jumpDown || lastPadState != DRSv2.PadState.OTHER
   }

   private fun setJumpPulse(generation: Int = unloadGeneration) {
      if (this.canRunDelayed(generation)) {
         if (!jumpDown) {
            jumpDown = true
            PlayerController.pressJump(true)
            ThreadsKt.thread$default(false, true, null, null, 0, { 
               try {
                  Thread.sleep(100L)
               } catch (var2: InterruptedException) {
               }

               getMc().execute({ 
                  if (`$generation` == unloadGeneration) {
                     PlayerController.pressJump(false)
                     jumpDown = false
                  }
               })
return Unit
            }, 29, null)
         }
      }
   }

   private fun punchOnceDelayed(delayMs: Long) {
      ThreadsKt.thread$default(false, true, null, null, 0, { 
         try {
            Thread.sleep(`$delayMs`)
         } catch (var4: InterruptedException) {
         }

         getMc().execute({ 
            if (canRunDelayed(`$generation`)) {
               PlayerController.tapLeftMouse(30L)
            }
         })
return Unit
      }, 29, null)
   }

   private fun unload(resetMirrorverse: Boolean, resetCompletion: Boolean) {

      isActive = false
      beats = 0
      lastBeatTime = 0L
      nextCorrectionTapAtMs = 0L
      moveTargetIndex = 0
      isCenteringNow = false
      heldMoveKey = null
      lastPadState = DRSv2.PadState.OTHER
      if (resetMirrorverse) {
         inMirrorverse = false
         firstTime = true
      }

      if (resetCompletion) {
         isCompleted = false
      }

      this.releaseAllKeys()
   }

   private fun setInactive() {
      this.unload(false, false)
   }

   private fun unloadInterrupted() {
      this.unload(true, true)
   }

   private fun setActive() {

      this.releaseAllKeys()
      isActive = true
      beats = 0
      isCompleted = false
      lastBeatTime = 0L
      nextCorrectionTapAtMs = 0L
      isCenteringNow = false
      heldMoveKey = null
      moveTargetIndex = 0
      this.doMove(0)
      this.chat("§a§lJR §7» §aDance Room Solver Started! Hold still.")
   }

   private fun doMove(beat: Int) {
      if (beat == 0 || beat % 2 == 1) {
         moveTargetIndex = moveTargetIndex + 1 and 3
      }

      if (beat >= 8) {
         when (beat % 4) {
            0 -> this.setSneak(true)
            1 -> this.setSneak(false)
            else -> {}
         }
      }

      if (beat >= 24) {
         when (beat % 8) {
            0, 2 -> {
               ThreadsKt.thread$default(false, true, null, null, 0, { 
                  try {
                     Thread.sleep(500L)
                  } catch (var2: InterruptedException) {
                  }

                  getMc().execute({ 
                     setJumpPulse(`$generation`)
                  })
return Unit
               }, 29, null)
            }
            1 -> {}
            else -> {}
         }
      }

      if (beat >= 64 && beat % 2 == 0) {
         this.punchOnceDelayed(800L)
      }
   }

   private fun chooseMirrorverseDirection(errorX: Double, errorZ: Double): jooon.features.mirrorverse.DRSv2.MoveTap {
      return if (Math.abs(errorX) >= Math.abs(errorZ))
         (if (errorX > 0.0) DRSv2.MoveTap.W else DRSv2.MoveTap.S)
return else
         (if (errorZ > 0.0) DRSv2.MoveTap.D else DRSv2.MoveTap.A)
      }

   private fun tapCorrection(direction: jooon.features.mirrorverse.DRSv2.MoveTap) {
      when (DRSv2.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
         1 -> PlayerController.tapForward(26L)
         2 -> PlayerController.tapBack(26L)
         3 -> PlayerController.tapLeft(26L)
         4 -> PlayerController.tapRight(26L)
         else -> throw NoWhenBranchMatchedException()
      }
   }

   private fun isOnTargetTile(playerX: Double, playerZ: Double, targetX: Double, targetZ: Double): Boolean {
      return MathHelper.floor(playerX) == MathHelper.floor(targetX) && MathHelper.floor(playerZ) == MathHelper.floor(targetZ)
   }

   fun applyLookDown(player: ClientPlayerEntity) {


      player.setYaw(nextYaw)
      player.setPitch(nextPitch)
      player.headYaw = nextYaw
      player.bodyYaw = nextYaw
      player.lastYaw = nextYaw
      player.lastPitch = nextPitch
      player.lastHeadYaw = nextYaw
      player.lastBodyYaw = nextYaw
   }

   private fun stepAngle(current: Float, target: Float, maxStep: Float): Float {

      return if (Math.abs(diff) <= maxStep) target else current + (diff).coerceIn(-maxStep, maxStep)
   }

   private fun stepScalar(current: Float, target: Float, maxStep: Float): Float {
      return if (Math.abs(target - current) <= maxStep) target else current + (target - current).coerceIn(-maxStep, maxStep)
   }

   fun applyContinuousCentering(client: MinecraftClient) {
      if (client.player != null) {

         if (isActive) {





            if (!this.isOnTargetTile(player.getX(), player.getZ(), tx, tz)) {
               isCenteringNow = true
               this.setHeldMovement(this.chooseMirrorverseDirection(errorX, errorZ))
            } else {
               this.setHeldMovement(null)
               if (Math.abs(errorX) <= 0.34 && Math.abs(errorZ) <= 0.34) {
                  isCenteringNow = false
               } else {
                  isCenteringNow = true

                  if (now >= nextCorrectionTapAtMs) {
                     this.tapCorrection(this.chooseMirrorverseDirection(errorX, errorZ))
                     nextCorrectionTapAtMs = now + 90L
                  }
               }
            }
         }
      }
   }

   fun onTick(client: MinecraftClient) {
      if (!Config.drSolverEnabled) {
         if (this.hasLoadedState()) {
            this.unloadInterrupted()
         }
      } else {

         if (client.player != null && client.world != null) {
            inMirrorverse = this.checkInMirrorverse(client)
            if (isActive && !inMirrorverse) {
               this.unloadInterrupted()
            } else {
               if (inMirrorverse && firstTime) {
                  this.chat("§a§lJR §7» §aJooon Dance Room Solver is currently §lENABLED!")
                  this.chat("§a§lJR §7» §7To get started, head to the starting position (green stained glass) and center yourself in the middle!")
                  firstTime = false
               }

               val pad: DRSv2.PadState = this.currentPadState()
               if (!isActive && inMirrorverse && lastPadState === DRSv2.PadState.GREEN && pad === DRSv2.PadState.STONE) {
                  this.setActive()
               }

               lastPadState = pad
               if (isActive) {
                  this.applyLookDown(player)
                  this.applyContinuousCentering(client)
                  player.setSprinting(false)
               }
            }
         } else {
            if (this.hasLoadedState()) {
               this.unloadInterrupted()
            }
         }
      }
   }

   fun onSoundPacket(packet: PlaySoundS2CPacket) {
      this.getMc().execute({ 
         handleSoundPacket(`$packet`)
      })
   }

   fun onRender(context: DrawContext) {
      if (isActive) {





         context.drawText(var12, text, x, y, 16777215, true)
         if (isCenteringNow) {
            context.drawText(
               var12,
               "§7> §cCorrecting position! §7<",
               var10000.getWindow().getScaledWidth() / 2 - var12.getWidth("§7> §cCorrecting position! §7<") / 2,
               y + 10,
               16777215,
return true
            )
         }
      }
   }

   private enum class MoveTap {
      W,
      S,
      A,
      D;

      
      fun getEntries(): EnumEntries<DRSv2.MoveTap> {
         $ENTRIES
      }
   }

   private enum class PadState {
      GREEN,
      STONE,
      OTHER,
      OFFPAD;

      
      fun getEntries(): EnumEntries<DRSv2.PadState> {
         $ENTRIES
      }
   }
}
