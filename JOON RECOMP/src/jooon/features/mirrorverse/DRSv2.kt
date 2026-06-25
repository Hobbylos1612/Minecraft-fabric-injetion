package jooon.features.mirrorverse

import jooon.config.Config
import jooon.util.PlayerController
import kotlin.concurrent.ThreadsKt
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nDRSv2.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DRSv2.kt\njooon/features/mirrorverse/DRSv2\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,493:1\n1747#2,3:494\n*S KotlinDebug\n*F\n+ 1 DRSv2.kt\njooon/features/mirrorverse/DRSv2\n*L\n447#1:494,3\n*E\n"])
public object DRSv2 {
   private const val ARRIVAL_CENTER_EPS: Double = 0.34
   private const val CORRECTION_TAP_MS: Long = 26L
   private const val CORRECTION_TAP_COOLDOWN_MS: Long = 90L
   private const val LOOK_TARGET_YAW: Float = 90.0F
   private const val LOOK_TARGET_PITCH: Float = 90.0F
   private const val LOOK_MAX_STEP_DEG: Float = 18.0F
   private const val JUMP_DELAY_MS: Long = 500L
   private const val PUNCH_DELAY_MS: Long = 800L
   private final val PITCH_LIST: Set<Double> = SetsKt.setOf(arrayOf(0.52380955F, 1.0476191F, 0.6984127F, 0.8888889F))
   private const val COMPLETE_PITCH: Double = 0.74603176F
   private const val TARGET_X: Int = -265
   private const val TARGET_Y: Int = 32
   private const val TARGET_Z: Int = -108
   private final var beats: Int
   private final var isActive: Boolean
   private final var inMirrorverse: Boolean
   private final var firstTime: Boolean = true
   private final var isCompleted: Boolean
   private final var lastBeatTime: Long
   private final var unloadGeneration: Int
   private final var nextCorrectionTapAtMs: Long
   private final var moveTargetIndex: Int
   private final var isCenteringNow: Boolean
   private final var heldMoveKey: jooon.features.mirrorverse.DRSv2.MoveTap?
   private final val routeCenters: Array<Pair<Double, Double>>
   private final var sneakDown: Boolean
   private final var jumpDown: Boolean
   private final var lastPadState: jooon.features.mirrorverse.DRSv2.PadState = DRSv2.PadState.OTHER

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         INSTANCE.unloadInterrupted()
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         INSTANCE.unloadInterrupted()
      })
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         INSTANCE.onTick(client)
      })
      HudRenderCallback.EVENT.register({ context: DrawContext, var1: RenderTickCounter ->
         INSTANCE.onRender(context)
      })
   }

   private fun chat(msg: String) {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         var10000.method_7353(Text.method_43470(msg) as Text, false)
      }
   }

   fun checkInMirrorverse(client: MinecraftClient): Boolean {
      client.field_1724 != null && client.field_1724.method_5649(-265.0, 32.0, -108.0) < 10000.0
   }

   private fun currentPadState(): jooon.features.mirrorverse.DRSv2.PadState {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 == null) {
         return DRSv2.PadState.OFFPAD
      } else {
         val p: ClientPlayerEntity = var10000.field_1724
         if (var10000.field_1687 == null) {
            return DRSv2.PadState.OFFPAD
         } else {
            val w: ClientWorld = var10000.field_1687
            val bx: Int = MathHelper.method_15357(var10000.field_1724.method_23317())
            val by: Int = MathHelper.method_15357(p.method_23318() - 0.1)
            val bz: Int = MathHelper.method_15357(p.method_23321())
            if (bx == -265 && by == 32 && bz == -108) {
               val var9: Block = w.method_8320(BlockPos(bx, by, bz)).method_26204()
               if (var9 == Blocks.field_10340) {
                  return DRSv2.PadState.STONE
               } else {
                  return if (!(var9 == Blocks.field_10157) && !(var9 == Blocks.field_10305) && !(var9 == Blocks.field_10357) && !(var9 == Blocks.field_10419))
                     DRSv2.PadState.OTHER
                     else
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
         PlayerController.INSTANCE.pressSneak(false)
      }

      if (jumpDown) {
         jumpDown = false
         PlayerController.INSTANCE.pressJump(false)
      }
   }

   private fun releaseMovementKeys() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      heldMoveKey = null
   }

   private fun setHeldMovement(direction: jooon.features.mirrorverse.DRSv2.MoveTap?) {
      if (heldMoveKey != direction) {
         this.releaseMovementKeys()
         if (direction != null) {
            when (DRSv2.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
               1 -> PlayerController.INSTANCE.pressForward(true)
               2 -> PlayerController.INSTANCE.pressBack(true)
               3 -> PlayerController.INSTANCE.pressLeft(true)
               4 -> PlayerController.INSTANCE.pressRight(true)
               else -> throw NoWhenBranchMatchedException()
            }

            heldMoveKey = direction
         }
      }
   }

   private fun setSneak(down: Boolean) {
      if (sneakDown != down) {
         sneakDown = down
         PlayerController.INSTANCE.pressSneak(down)
      }
   }

   private fun canRunDelayed(generation: Int): Boolean {
      return generation == unloadGeneration && isActive && Config.drSolverEnabled && this.getMc().field_1724 != null && this.getMc().field_1687 != null
   }

   private fun hasLoadedState(): Boolean {
      return isActive || inMirrorverse || beats != 0 || heldMoveKey != null || sneakDown || jumpDown || lastPadState != DRSv2.PadState.OTHER
   }

   private fun setJumpPulse(generation: Int = unloadGeneration) {
      if (this.canRunDelayed(generation)) {
         if (!jumpDown) {
            jumpDown = true
            PlayerController.INSTANCE.pressJump(true)
            ThreadsKt.thread$default(false, true, null, null, 0, { 
               try {
                  Thread.sleep(100L)
               } catch (var2: InterruptedException) {
               }

               INSTANCE.getMc().execute({ 
                  if (`$generation` == unloadGeneration) {
                     PlayerController.INSTANCE.pressJump(false)
                     jumpDown = false
                  }
               })
               Unit.INSTANCE
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

         INSTANCE.getMc().execute({ 
            if (INSTANCE.canRunDelayed(`$generation`)) {
               PlayerController.INSTANCE.tapLeftMouse(30L)
            }
         })
         Unit.INSTANCE
      }, 29, null)
   }

   private fun unload(resetMirrorverse: Boolean, resetCompletion: Boolean) {
      val var3: Int = unloadGeneration++
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
      val var1: Int = unloadGeneration++
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

                  INSTANCE.getMc().execute({ 
                     INSTANCE.setJumpPulse(`$generation`)
                  })
                  Unit.INSTANCE
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
         else
         (if (errorZ > 0.0) DRSv2.MoveTap.D else DRSv2.MoveTap.A)
      }

   private fun tapCorrection(direction: jooon.features.mirrorverse.DRSv2.MoveTap) {
      when (DRSv2.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
         1 -> PlayerController.INSTANCE.tapForward(26L)
         2 -> PlayerController.INSTANCE.tapBack(26L)
         3 -> PlayerController.INSTANCE.tapLeft(26L)
         4 -> PlayerController.INSTANCE.tapRight(26L)
         else -> throw NoWhenBranchMatchedException()
      }
   }

   private fun isOnTargetTile(playerX: Double, playerZ: Double, targetX: Double, targetZ: Double): Boolean {
      return MathHelper.method_15357(playerX) == MathHelper.method_15357(targetX) && MathHelper.method_15357(playerZ) == MathHelper.method_15357(targetZ)
   }

   fun applyLookDown(player: ClientPlayerEntity) {
      val nextYaw: Float = this.stepAngle(player.method_36454(), 90.0F, 18.0F)
      val nextPitch: Float = this.stepScalar(player.method_36455(), 90.0F, 18.0F)
      player.method_36456(nextYaw)
      player.method_36457(nextPitch)
      player.field_6241 = nextYaw
      player.field_6283 = nextYaw
      player.field_5982 = nextYaw
      player.field_6004 = nextPitch
      player.field_6259 = nextYaw
      player.field_6220 = nextYaw
   }

   private fun stepAngle(current: Float, target: Float, maxStep: Float): Float {
      val diff: Float = MathHelper.method_15393(target - current)
      return if (Math.abs(diff) <= maxStep) target else current + RangesKt.coerceIn(diff, -maxStep, maxStep)
   }

   private fun stepScalar(current: Float, target: Float, maxStep: Float): Float {
      return if (Math.abs(target - current) <= maxStep) target else current + RangesKt.coerceIn(target - current, -maxStep, maxStep)
   }

   fun applyContinuousCentering(client: MinecraftClient) {
      if (client.field_1724 != null) {
         val player: ClientPlayerEntity = client.field_1724
         if (isActive) {
            val var3: Pair = routeCenters[moveTargetIndex]
            val tx: Double = (routeCenters[moveTargetIndex].component1() as java.lang.Number).doubleValue()
            val tz: Double = (var3.component2() as java.lang.Number).doubleValue()
            val errorX: Double = player.method_23317() - tx
            val errorZ: Double = player.method_23321() - tz
            if (!this.isOnTargetTile(player.method_23317(), player.method_23321(), tx, tz)) {
               isCenteringNow = true
               this.setHeldMovement(this.chooseMirrorverseDirection(errorX, errorZ))
            } else {
               this.setHeldMovement(null)
               if (Math.abs(errorX) <= 0.34 && Math.abs(errorZ) <= 0.34) {
                  isCenteringNow = false
               } else {
                  isCenteringNow = true
                  val now: Long = System.currentTimeMillis()
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
         val player: ClientPlayerEntity = client.field_1724
         if (client.field_1724 != null && client.field_1687 != null) {
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
                  player.method_5728(false)
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
         INSTANCE.handleSoundPacket(`$packet`)
      })
   }

   fun onRender(context: DrawContext) {
      if (isActive) {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         val var12: TextRenderer = var10000.field_1772
         val text: java.lang.String = "§7[§aJooon DRS Active - Beat ${beats}§7]"
         val x: Int = var10000.method_22683().method_4486() / 2 - var12.method_1727("§7[§aJooon DRS Active - Beat ${beats}§7]") / 2
         val y: Int = var10000.method_22683().method_4502() / 2 + 10
         context.method_51433(var12, text, x, y, 16777215, true)
         if (isCenteringNow) {
            context.method_51433(
               var12,
               "§7> §cCorrecting position! §7<",
               var10000.method_22683().method_4486() / 2 - var12.method_1727("§7> §cCorrecting position! §7<") / 2,
               y + 10,
               16777215,
               true
            )
         }
      }
   }

   private enum class MoveTap {
      W,
      S,
      A,
      D;

      @JvmStatic
      fun getEntries(): EnumEntries<DRSv2.MoveTap> {
         $ENTRIES
      }
   }

   private enum class PadState {
      GREEN,
      STONE,
      OTHER,
      OFFPAD;

      @JvmStatic
      fun getEntries(): EnumEntries<DRSv2.PadState> {
         $ENTRIES
      }
   }
}
