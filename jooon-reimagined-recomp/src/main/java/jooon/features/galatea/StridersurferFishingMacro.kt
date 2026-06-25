package jooon.features.galatea

import java.util.ArrayList
import java.util.Arrays
import java.util.Locale
import java.util.NoSuchElementException
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.gui.MovableOverlayScreen
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import jooon.util.PlayerController
import jooon.util.RenderUtils
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.Intrinsics
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.passive.StriderEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

object StridersurferFishingMacro {
   private const val STRIDER_TAG_RADIUS: Double = 10.0
   private const val STRIDER_ATTACK_RADIUS: Double = 10.0
   private const val CAST_DELAY: Int = 15
   private const val REEL_DELAY: Int = 10
   private const val POST_SWAP_DELAY_TICKS: Int = 1
   private const val SESSION_LENGTH_MS: Long = 2700000L
   private const val PET_MENU_MIN_OPEN_TICKS: Int = 3
   private const val PET_SWAP_TIMEOUT_TICKS: Int = 50
   private const val SOUL_WHIP_FIGSTONE_HOLD_TICKS: Int = 10
   private const val SOUL_WHIP_KILL_GRACE_MS: Long = 800L
   private const val PLATE_HALF_SIZE: Double = 0.48
   private const val PLATE_HEIGHT: Double = 0.06
   private const val PLATE_X: Double = -694.5
   private const val PLATE_Y: Double = 121.0
   private const val PLATE_Z: Double = 80.0
   private const val START_YAW: Float = 0.0F
   private const val START_PITCH: Float = 16.5F
   private val galateaAreaRegex: Regex = Regex("\\bArea\\s*:\\s*Galatea\\b", RegexOption.IGNORE_CASE)
   private val fishingXpRegex: Regex = Regex("\\+([0-9]+(?:\\.[0-9]+)?)\\s+Fishing\\b", RegexOption.IGNORE_CASE)
   private val colorRegex: Regex = Regex("\\u00A7.")
   private val levelPrefixRegex: Regex = Regex("(?i)\\[lvl[^\\]]*]\\s*")
   
   private KeyBinding toggleKey;
   private var overlayReady: Boolean
   private var appliedSavedPosition: Boolean
   private var delayTicks: Int
   private var postSwapDelayTicks: Int
   private var currentSurferCount: Int
   private var lastAnnouncedSurferCount: Int
   
   private FishingBobberEntity playersBobber;
   private var savedYaw: Float
   private var savedPitch: Float
   private var savedRodSlot: Int = -1
   private var totalFishingXp: Double
   private var xpStartMillis: Long
   private var lastXpGain: Double
   private var cachedXpPerHour: Double
   private var lastXpCalcMillis: Long
   private var currentActionLabel: String = "Disabled"
   private var lastRotateMs: Long
   private var mode: jooon.features.galatea.StridersurferFishingMacro.Mode = StridersurferFishingMacro.Mode.FISHING
   private var sessionEndMillis: Long
   private var pendingDisableAfterCleanup: Boolean
   private var sessionStopReelSent: Boolean
   private var petSwapTarget: jooon.features.galatea.StridersurferFishingMacro.PetSwapTarget?
   private var petSwapWaitTicks: Int
   private var petSwapTimeoutTicks: Int
   private var soulWhipStage: jooon.features.galatea.StridersurferFishingMacro.SoulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
   private var soulWhipHoldTicksRemaining: Int
   private var soulWhipGraceUntilMs: Long
   private var soulWhipInitialSequenceDone: Boolean
   private var lastKnownActivePetName: String = ""

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.togglestridersurferfishingmacro", 76, Category.MISC))
      ClientTickEvents.END_CLIENT_TICK.register(lambda_0@{ client: MinecraftClient ->
         while (true) {
            var var10000: KeyBinding = toggleKey
            if (toggleKey == null) {
               throwUninitializedPropertyAccessException("toggleKey")
               var10000 = null
            }

            if (!var10000.wasPressed()) {
               ensureOverlayReady()
               if (client.player == null) {
                  return@lambda_0
               }

               if (client.world == null) {
                  return@lambda_0
               }

               if (!Config.Companion.stridersurferFishingMacroEnabled) {
                  currentActionLabel = "Disabled"
                  currentSurferCount = 0
                  return@lambda_0
               }

               tick(client, player)
               return@lambda_0
            }

            toggleMacro()
         }
      })
      WorldRenderEvents.END_MAIN.register({ context: WorldRenderContext ->
         renderActivationPlate(context)
      })
      ClientReceiveMessageEvents.GAME
         .register(
            { message: Text, overlay: Boolean ->
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

   fun openHudEditor() {
      this.getMc().execute({ 
         ensureOverlayReady()

         if (var10000 != null) {
            var10000.setPositionSilently(PersistentState.stridersurferFishingMacroHudX, PersistentState.stridersurferFishingMacroHudY)
            var10000.openPositioningGUI()
         }
      })
   }

   fun openKeybindMenu() {
      this.getMc()
         .execute(
            { 
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

   fun onPositionPacket(packet: PlayerPositionLookS2CPacket) {
      if (Config.Companion.stridersurferFishingMacroEnabled) {
         this.getMc().execute({ 
            disableImmediately("§cStridersurfer Macro was disabled! You were teleported.")
         })
      }
   }

   private fun toggleMacro() {

      if (var10000 != null) {
         if (Config.Companion.stridersurferFishingMacroEnabled) {
            this.disableImmediately("§cStridersurfer Macro disabled.")
         } else if (!this.canStartFromCurrentPosition(var10000)) {
            this.sendMessage(
               "Stridersurfer Macro is only for Galatea! Head to Sawyer (-694 121 80) on the Galatea island, and step in the plate to get started!)"
            )
         } else {

            if (rodSlot == -1) {
               this.sendMessage("§cCould not find a fishing rod in your hotbar.")
            } else {
               this.resetSessionStats()
               this.resetRuntimeState()
               savedYaw = 0.0F
               savedPitch = 16.5F
               savedRodSlot = rodSlot
               sessionEndMillis = System.currentTimeMillis() + 2700000L
               currentActionLabel = "Starting up"
               Config.Companion.stridersurferFishingMacroEnabled = true
               this.applyStartFacing(var10000)
               this.selectHotbarSlot(var10000, rodSlot)
               JooonConfigManager.write("jooonreimagined")
               this.sendMessage("§aStridersurfer Macro enabled! §7Change the keybind in Controls -> Key Binds -> Jooon Reimagined.")
            }
         }
      }
   }

   fun tick(client: MinecraftClient, player: ClientPlayerEntity) {
      this.updatePlayersBobber(player)
      currentSurferCount = this.countStriderSurferTags(player)
      if (mode === StridersurferFishingMacro.Mode.FISHING) {
         this.announceSurferCount()
      }

      this.updateSessionTimer()
      this.tickAntiAfkRotation(player)
      if (postSwapDelayTicks > 0) {
         postSwapDelayTicks += -1
      } else if (!this.processPetSwap(player)) {
         if (!this.processSoulWhipSequence(player)) {
            if (mode === StridersurferFishingMacro.Mode.COMBAT) {
               this.processCombat(client, player)
            } else if (currentSurferCount >= this.striderThreshold()) {
               currentActionLabel = "Preparing to kill"
               if (this.shouldRunPetSwap(StridersurferFishingMacro.PetSwapTarget.KILLING)) {
                  this.beginPetSwap(player, StridersurferFishingMacro.PetSwapTarget.KILLING)
               } else {
                  this.startCombatMode(player)
               }
            } else if (pendingDisableAfterCleanup) {
               currentActionLabel = "Finishing session"
               if (playersBobber != null) {
                  if (!sessionStopReelSent) {
                     if (this.reelRod()) {
                        delayTicks = 10
                        sessionStopReelSent = true
                     }
                  } else {
                     if (delayTicks > 0) {
                        delayTicks += -1
                     }
                  }
               } else if (delayTicks > 0) {
                  delayTicks += -1
               } else {
                  this.disableAfterSessionEnd()
               }
            } else if (!this.ensureFishingRodEquipped(player)) {
               this.disableImmediately("§cCould not find a fishing rod in your hotbar.")
            } else if (postSwapDelayTicks > 0) {
               postSwapDelayTicks += -1
               currentActionLabel = if (playersBobber == null) "Waiting to cast" else "Waiting for bite"
            } else if (delayTicks > 0) {
               delayTicks += -1
               currentActionLabel = if (playersBobber == null) "Waiting to cast" else "Waiting for bite"
            } else if (playersBobber == null) {
               currentActionLabel = "Casting rod"
               if (this.castRod()) {
                  delayTicks = 15
                  if (this.shouldRunPetSwap(StridersurferFishingMacro.PetSwapTarget.RECASTING)) {
                     this.beginPetSwap(player, StridersurferFishingMacro.PetSwapTarget.RECASTING)
                  }
               }
            } else if (this.findExclamationStand(player) != null) {
               currentActionLabel = "Reeling"
               if (this.reelRod()) {
                  delayTicks = 10
               }
            } else {
               currentActionLabel = "Waiting for bite"
            }
         }
      }
   }

   private fun castRod(): Boolean {
      return PlayerController.rightClick()
   }

   private fun reelRod(): Boolean {
      return PlayerController.rightClick()
   }

   fun countStriderSurferTags(player: ClientPlayerEntity): Int {

      if (var10000 == null) {
return 0
      } else {
         val var17: java.lang.Iterable = var10000.getEntities()
         val `count$iv`: java.lang.Iterable = toList(var17)
         val `destination$iv$iv`: java.util.Collection = ArrayList()

         for (var9 in `count$iv`) {
            if (var9 is ArmorStandEntity) {
               `destination$iv$iv`.add(var9)
            }
         }

         val var10: java.lang.Iterable = `destination$iv$iv` as java.util.List
         val var20: Int
         if (`destination$iv$iv` as java.util.List is java.util.Collection && ((`destination$iv$iv` as java.util.List) as java.util.Collection).isEmpty()) {
            var20 = 0
         } else {


            for (var14 in var10) {
               run label64@{

                  if ((var14 as ArmorStandEntity).squaredDistanceTo(player as Entity) <= 100.0 && (var14 as ArmorStandEntity).getCustomName() != null) {



                     if (contains$default(var18.normalizeName(var21), "stridersurfer", false, 2, null)) {
                        var19 = true
                        return@label64
                     }
                  }

                  var19 = false
               }

               if (var19) {
                  if (++var12 < 0) {
                     throwCountOverflow()
                  }
               }
            }

            var20 = var12
         }
return var20
      }
   }

   private fun announceSurferCount() {
      if (currentSurferCount != lastAnnouncedSurferCount) {
         if (currentSurferCount > 0 || lastAnnouncedSurferCount > 0) {
            this.sendMessage("§eStridersurfers: §f${currentSurferCount}/${this.striderThreshold()}")
         }

         lastAnnouncedSurferCount = currentSurferCount
      }
   }

   private fun updateSessionTimer() {
      if (Config.Companion.stridersurferFishingMacroEnabled && !pendingDisableAfterCleanup && sessionEndMillis > 0L) {
         if (System.currentTimeMillis() >= sessionEndMillis) {
            pendingDisableAfterCleanup = true
            currentActionLabel = "Finishing session"
         }
      }
   }

   private fun disableAfterSessionEnd() {
      this.disableImmediately("§cStridersurfer Macro disabled! §aYou can now re-enable it to continue fishing.")
   }

   fun startCombatMode(player: ClientPlayerEntity) {
      mode = StridersurferFishingMacro.Mode.COMBAT
      delayTicks = 0
      soulWhipInitialSequenceDone = false
      if (Config.stridersurferFishingMacroUseWhipInstead) {
         this.beginSoulWhipSequence(player)
      } else if (!this.equipFigstoneSplitter(player)) {
         this.disableImmediately("§cCould not find a Figstone Splitter in your hotbar.")
      } else {
         currentActionLabel = "Killing"
      }
   }

   fun beginSoulWhipSequence(player: ClientPlayerEntity) {
      if (!this.equipFigstoneSplitter(player)) {
         this.disableImmediately("§cCould not find a Figstone Splitter in your hotbar.")
      } else {
         mode = StridersurferFishingMacro.Mode.COMBAT
         soulWhipStage = StridersurferFishingMacro.SoulWhipStage.HOLDING_FIGSTONE
         soulWhipHoldTicksRemaining = if (soulWhipInitialSequenceDone) 0 else 10
         soulWhipInitialSequenceDone = true
         currentActionLabel = "Holding Figstone"
      }
   }

   fun processSoulWhipSequence(player: ClientPlayerEntity): Boolean {
      if (soulWhipStage != StridersurferFishingMacro.SoulWhipStage.NONE && currentSurferCount <= 0) {
         this.finishCombatAndResumeFishing(player)
return true
      } else {
         when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$0[soulWhipStage.ordinal()]) {
            1 -> false
            2 -> {
               currentActionLabel = "Holding Figstone"
               if (!this.equipFigstoneSplitter(player)) {
                  this.disableImmediately("§cCould not find a Figstone Splitter in your hotbar.")
return true
               } else if (soulWhipHoldTicksRemaining > 0) {
                  soulWhipHoldTicksRemaining += -1
return true
               } else {

                  if (whipSlot == -1) {
                     this.abortCombatToFishing("§cCould not find Soul Whip or Flaming Flay in your hotbar.")
return true
                  }

                  this.selectHotbarSlot(player, whipSlot)
                  soulWhipStage = StridersurferFishingMacro.SoulWhipStage.WAITING_AFTER_WHIP_SWAP
                  currentActionLabel = "Swapping to Soul Whip"
return true
               }
            }
            3 -> {
               currentActionLabel = "Using Soul Whip"
               if (!PlayerController.rightClick()) {
return true
               }

               soulWhipStage = StridersurferFishingMacro.SoulWhipStage.WAITING_TO_RETURN_TO_FIGSTONE
               currentActionLabel = "Returning to Figstone"
return true
            }
            4 -> {
               currentActionLabel = "Returning to Figstone"
               if (!this.equipFigstoneSplitter(player)) {
                  this.disableImmediately("§cCould not find a Figstone Splitter in your hotbar.")
return true
               }

               soulWhipGraceUntilMs = System.currentTimeMillis() + 800L
               soulWhipStage = StridersurferFishingMacro.SoulWhipStage.GRACE_AFTER_USE
               currentActionLabel = if (currentSurferCount > 0) "Waiting after Soul Whip" else "Casting rod"
return true
            }
            5 -> {
               if (currentSurferCount <= 0) {
                  this.finishCombatAndResumeFishing(player)
return true
               } else {
                  if (System.currentTimeMillis() < soulWhipGraceUntilMs) {
                     currentActionLabel = "Waiting after Soul Whip"
return true
                  }

                  soulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
return false
               }
            }
            else -> throw NoWhenBranchMatchedException()
         }
      }
   }

   fun finishCombatAndResumeFishing(player: ClientPlayerEntity) {
      this.restoreRodAndOrientation(player)
      mode = StridersurferFishingMacro.Mode.FISHING
      soulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
      soulWhipHoldTicksRemaining = 0
      soulWhipGraceUntilMs = 0L
      soulWhipInitialSequenceDone = false
      if (pendingDisableAfterCleanup) {
         this.disableAfterSessionEnd()
      } else {
         delayTicks = 15
         currentActionLabel = "Casting rod"
      }
   }

   private fun abortCombatToFishing(message: String) {

      if (var10000 != null) {
         restoreRodAndOrientation(var10000)
      }

      mode = StridersurferFishingMacro.Mode.FISHING
      soulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
      soulWhipHoldTicksRemaining = 0
      soulWhipGraceUntilMs = 0L
      soulWhipInitialSequenceDone = false
      currentActionLabel = if (pendingDisableAfterCleanup) "Finishing session" else "Waiting for bite"
      delayTicks = if (pendingDisableAfterCleanup) 0 else 15
      this.sendMessage(message)
      if (pendingDisableAfterCleanup) {
         this.disableAfterSessionEnd()
      }
   }

   fun equipFigstoneSplitter(player: ClientPlayerEntity): Boolean {
      repeat(8) { slot ->

         if (!var10000.isEmpty()) {

            if (var4.contains("Figstone Splitter", true)) {
               this.selectHotbarSlot(player, slot)
            }
         }
      }
return false
   }

   fun ensureFishingRodEquipped(player: ClientPlayerEntity): Boolean {
return savedRodSlot
return else
         this.findFishingRodSlot(player)
         if (targetSlot == -1) {
return false
      } else {
         savedRodSlot = targetSlot
         this.selectHotbarSlot(player, targetSlot)
      }
   }

   fun findFishingRodSlot(player: ClientPlayerEntity): Int {
      repeat(8) { slot ->
         if (this.isFishingRodSlot(player, slot)) {
return slot
         }
      }

      -1
   }

   fun isFishingRodSlot(player: ClientPlayerEntity, slot: Int): Boolean {

      if (var10000.isEmpty()) {
return false
      } else {
         if (!var10000.isOf(Items.FISHING_ROD)) {

            if (!var4.contains("rod", true)) {
return false
            }
         }
return true
      }
   }

   fun findSoulWhipOrFlaySlot(player: ClientPlayerEntity): Int {
      repeat(8) { slot ->

         if (!var10000.isEmpty()) {


            if (contains$default(name, "soul whip", false, 2, null) || contains$default(name, "flaming flay", false, 2, null)) {
return slot
            }
         }
      }

      -1
   }

   fun selectHotbarSlot(player: ClientPlayerEntity, slot: Int): Boolean {
      if (0 > slot || slot >= 9) {
return false
      } else if (player.getInventory().getSelectedSlot() == slot) {
return true
      } else {
         player.getInventory().setSelectedSlot(slot)
         PlayerController.noteHotbarSwapThisTick()
         postSwapDelayTicks = Math.max(postSwapDelayTicks, 1)
return true
      }
   }

   private fun shouldRunPetSwap(target: jooon.features.galatea.StridersurferFishingMacro.PetSwapTarget): Boolean {
      if (!Config.stridersurferFishingMacroAutoPetSwap) {
         return false
      } else {

         if (isBlank(wanted)) {
            return false
         } else {
            var var10000: Boolean
            when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$1[target.ordinal()]) {
               1 -> var10000 = !(wanted == lastKnownActivePetName)
               2 -> var10000 = true
               else -> throw NoWhenBranchMatchedException()
            }

            return var10000
         }
      }
   }

   private fun petNameFor(target: jooon.features.galatea.StridersurferFishingMacro.PetSwapTarget): String {
      var var10000: String
      when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$1[target.ordinal()]) {
         1 -> var10000 = trim(Config.stridersurferFishingMacroKillPetName).toString()
         2 -> var10000 = trim(Config.stridersurferFishingMacroRecastPetName).toString()
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun beginPetSwap(player: ClientPlayerEntity, target: StridersurferFishingMacro.PetSwapTarget) {
      if (petSwapTarget == null) {
         if (!this.shouldRunPetSwap(target)) {
            if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) {
               this.startCombatMode(player)
            }
         } else {
            player.networkHandler.sendChatCommand("pets")
            petSwapTarget = target
            petSwapWaitTicks = 0
            petSwapTimeoutTicks = 50
            currentActionLabel = if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) "Opening kill pet" else "Opening recast pet"
         }
      }
   }

   fun processPetSwap(player: ClientPlayerEntity): Boolean {
      if (petSwapTarget == null) {
return false
      } else {
         val target: StridersurferFishingMacro.PetSwapTarget = petSwapTarget
         if (petSwapTimeoutTicks > 0) {
            petSwapTimeoutTicks += -1
         }

         if (petSwapTimeoutTicks <= 0) {
            if (this.getMc().currentScreen is HandledScreen) {
               player.closeHandledScreen()
            }

            this.finishPetSwap(player, target, false, "§eCould not open the Pets menu.")
return true
         } else {

            val var6: HandledScreen = petName as? HandledScreen
            if ((petName as? HandledScreen) != null) {

               if (var10000.contains("pets", true)) {
                  if (petSwapWaitTicks < 3) {

                     currentActionLabel = "Waiting for pets menu"
return true
                  }


                  if (slotIndex == -1) {
                     player.closeHandledScreen()
                     this.finishPetSwap(player, target, false, "§eCould not find pet \"$var7\" in the Pets menu.")
return true
                  }

                  currentActionLabel = if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) "Swapping to kill pet" else "Swapping to recast pet"

                  if (var9 != null) {
                     var9.clickSlot(var6.getScreenHandler().syncId, slotIndex, 0, SlotActionType.PICKUP, player as PlayerEntity)
                  }

                  player.closeHandledScreen()
                  this.finishPetSwap(player, target, true, null)
return true
               }
            }

            currentActionLabel = if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) "Waiting for kill pet menu" else "Waiting for recast pet menu"
return true
         }
      }
   }

   fun finishPetSwap(player: ClientPlayerEntity, target: StridersurferFishingMacro.PetSwapTarget, success: Boolean, failureMessage: String) {
      petSwapTarget = null
      petSwapWaitTicks = 0
      petSwapTimeoutTicks = 0
      if (success) {
         lastKnownActivePetName = this.normalizeName(this.petNameFor(target))
      }

      if (!success && failureMessage != null && !isBlank(failureMessage)) {
         this.sendMessage(failureMessage)
      }

      when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$1[target.ordinal()]) {
         1 -> this.startCombatMode(player)
         2 -> currentActionLabel = if (pendingDisableAfterCleanup) "Finishing session" else "Waiting for bite"
         else -> throw NoWhenBranchMatchedException()
      }
   }

   fun findPetMenuSlot(screen: HandledScreen, player: ClientPlayerEntity, petName: String): Int {

      if (isBlank(wanted)) {
         -1
      } else {
         val var5: java.util.Iterator = (screen.getScreenHandler().slots as java.lang.Iterable).iterator()
         var var6: Int = 0

         while (var5.hasNext()) {


            if (slot.inventory != player.getInventory()) {

               if (!var10000.isEmpty()) {

                  if (contains$default(this.normalizeName(var10001), wanted, false, 2, null)) {
return index
                  }
               }
            }
         }

         -1
      }
   }

   private fun normalizeName(raw: String): String {

            Regex("\\s+").replace(levelPrefixRegex.replace(replace$default(colorRegex.replace(raw, ""), "Â", "", false, 4, null), ""), " ")
         )
         .toString()


      return var6
   }

   fun findExclamationStand(player: ClientPlayerEntity): ArmorStandEntity {

      if (var10000 == null) {
return null
      } else {
         val var16: java.lang.Iterable = var10000.getEntities()
         val `this$iv$iv`: java.lang.Iterable = toList(var16)
         val `element$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if (`element$iv$iv` is ArmorStandEntity) {
               `element$iv`.add(`element$iv$iv`)
            }
         }

         val var12: java.util.Iterator = (`element$iv` as java.util.List).iterator()

         while (true) {
            if (!var12.hasNext()) {
               var20 = null
break
            }

            run label52@{
               var13 = var12.next()

               if ((var13 as ArmorStandEntity).squaredDistanceTo(player as Entity) <= 256.0 && (var13 as ArmorStandEntity).getCustomName() != null) {


                  if (trim(var18).toString() == "!!!") {
                     var19 = true
                     return@label52
                  }
               }

               var19 = false
            }

            if (var19) {
               var20 = var13
break
            }
         }

         var20 as ArmorStandEntity
      }
   }

   fun attackNearbyStrider(client: MinecraftClient, player: ClientPlayerEntity) {
      if (client.world != null) {
         var var10000: java.util.List = client.world.getEntitiesByClass(StriderEntity::class.java, player.getBoundingBox().expand(10.0), { p0: Any ->
            ``(p0)
         })
         val `iterator$iv`: java.util.Iterator = var10000.iterator()
         if (!`iterator$iv`.hasNext()) {
            var10000 = null
         } else {
            var `minElem$iv`: Any = `iterator$iv`.next()
            if (!`iterator$iv`.hasNext()) {
               var10000 = (java.util.List)`minElem$iv`
            } else {
               var var16: Double = (`minElem$iv` as StriderEntity).squaredDistanceTo(player as Entity)

               do {


                  if (java.lang.Double.compare(var16, var18) > 0) {
                     `minElem$iv` = var17
                     var16 = var18
                  }
               } while (`iterator$iv`.hasNext())

               var10000 = (java.util.List)`minElem$iv`
            }
         }

         if (var10000 as StriderEntity == null) {
            currentActionLabel = "Looking for strider"
         } else {
            currentActionLabel = "Killing"
            this.aimAtTarget(player, target)
            if (PlayerController.leftClick()) {
               player.swingHand(Hand.MAIN_HAND)
            }
         }
      }
   }

   fun restoreRodAndOrientation(player: ClientPlayerEntity) {
      this.ensureFishingRodEquipped(player)
      player.setYaw(savedYaw)
      player.setPitch(savedPitch)
      player.headYaw = savedYaw
      player.bodyYaw = savedYaw
      player.lastYaw = savedYaw
      player.lastPitch = savedPitch
      player.lastHeadYaw = savedYaw
      player.lastBodyYaw = savedYaw
   }

   fun updatePlayersBobber(player: ClientPlayerEntity) {

      if (var10000 != null) {
         val var16: java.lang.Iterable = var10000.getEntities()
         val `this$iv$iv`: java.lang.Iterable = toList(var16)
         val `element$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `this$iv$iv`) {
            if (`element$iv$iv` is FishingBobberEntity) {
               `element$iv`.add(`element$iv$iv`)
            }
         }

         val var12: java.util.Iterator = (`element$iv` as java.util.List).iterator()

         while (true) {
            if (var12.hasNext()) {

               if (!((var13 as FishingBobberEntity).getOwner() == player)) {
return continue
               }

               var17 = var13
break
            }

            var17 = null
break
         }

         playersBobber = var17 as FishingBobberEntity
      }
   }

   fun aimAtTarget(player: ClientPlayerEntity, target: StriderEntity) {




      player.setYaw(Math.toDegrees(Math.atan2(dz, dx)).toFloat() - 90.0F)
      player.setPitch(-(Math.toDegrees(Math.atan2(dy, horizontalDistance)).toFloat()))
   }

   private fun handleActionbarMessage(original: String?) {
      if (original != null && !isBlank(original)) {

            fishingXpRegex, replace$default(colorRegex.replace(original, ""), ",", "", false, 4, null), 0, 2, null
         )
         if (var10000 != null) {

            if (var7 != null) {

               if (var8 != null) {

                  if (xpStartMillis == 0L) {
                     xpStartMillis = System.currentTimeMillis()
                     lastXpCalcMillis = xpStartMillis
                  }

                  totalFishingXp += gain
                  lastXpGain = gain
return return
               }
            }
         }
      }
   }

   private fun recalcXpPerHourIfNeeded() {
      if (xpStartMillis > 0L && !(totalFishingXp <= 0.0)) {

         if (now - lastXpCalcMillis >= 5000L) {

            if (now - xpStartMillis > 0L) {

               if (!(elapsed / 3600000.0 <= 0.0)) {
                  cachedXpPerHour = totalFishingXp / hours
                  lastXpCalcMillis = now
               }
            }
         }
      }
   }

   private fun ensureOverlayReady() {
      if (!overlayReady) {

            Pair(PersistentState.stridersurferFishingMacroHudX, PersistentState.stridersurferFishingMacroHudY)
return else
            this.defaultHudPosition()
            var var10000: MovableOverlay = MovableOverlayManager.getOverlay("stridersurferFishingMacroHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "stridersurferFishingMacroHud",
                  "Stridersurfer Fishing Macro HUD",
                  (targetX.getFirst() as java.lang.Number).intValue(),
                  (targetX.getSecond() as java.lang.Number).intValue(),
                  190,
return 76
               )
            }

         var10000.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
            renderHud(context, x, y)
return Unit
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.stridersurferFishingMacroHudX = x
            PersistentState.stridersurferFishingMacroHudY = y
            PersistentState.stridersurferFishingMacroHudInitDone = true
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }
         var10000.register()
         overlayReady = true
      }

      if (!appliedSavedPosition) {



         if (var5 != null) {
            var5.setPositionSilently(var3, var4)
         }

         appliedSavedPosition = true
      }
   }

   fun renderHud(context: DrawContext, x: Int, y: Int) {

      if (Config.Companion.stridersurferFishingMacroEnabled || preview) {
         this.recalcXpPerHourIfNeeded()
         val lines: java.util.List = listOf(
            arrayOf(
               "Action: ${this.previewLabel(currentActionLabel, preview, "Waiting for bite")}",
               "Time left: ${this.formatTimeLeft(preview)}",
               "Fishing XP/hr: ${if (preview) "0" else this.formatXpPerHour()}",
               "Stridersurfers: ${if (preview) "0/${this.striderThreshold()}" else "${currentSurferCount}/${this.striderThreshold()}"}"
            )
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


            context.fill(x, y, x + panelWidth, y + var23, -871689461)
            context.fill(x + 2, y + 2, x + panelWidth - 2, y + var23 - 2, -871359728)
            context.fill(x + 3, y + 3, x + panelWidth - 3, y + 18, -1441256939)
            context.drawStrokedRectangle(x, y, panelWidth, var23, -10308789)
            context.drawStrokedRectangle(x + 1, y + 1, panelWidth - 2, var23 - 2, -13541594)
            this.renderSweep(context, x + 3, y + 3, panelWidth - 6, 15)
            this.drawHudText(
               context, "JR Strider Macro", x + (panelWidth - this.getMc().textRenderer.getWidth("JR Strider Macro")) / 2, y + 5, -2818175, -12293606
            )
            var lineY: Int = y + 24

            for (line in lines) {
               this.drawHudText(context, line, x + 8, lineY, textColor, textShadow)
               lineY += this.getMc().textRenderer.fontHeight + 3
            }
         }
      }
   }

   private fun previewLabel(current: String, preview: Boolean, fallback: String): String {
      return if (!preview && !isBlank(current) && !(current == "Disabled")) current else fallback
   }

   private fun formatXpPerHour(): String {
      if (cachedXpPerHour <= 0.0) {
         return "0"
      } else {

         val var4: Array<Any> = arrayOf(cachedXpPerHour)

         return var10000
      }
   }

   private fun formatTimeLeft(preview: Boolean): String {
      if (preview) {
         return "45m 00s"
      } else if (sessionEndMillis <= 0L) {
         return "00m 00s"
      } else {




         val var13: Array<Any> = arrayOf(minutes, seconds)

         return var10000
      }
   }

   private fun defaultHudPosition(): Pair<Int, Int> {
      return Pair(10, 10)
   }

   fun drawHudText(context: DrawContext, text: String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.drawText(this.getMc().textRenderer, Text.literal(text) as Text, x + 1, y + 1, shadowColor, false)
      context.drawText(this.getMc().textRenderer, Text.literal(text) as Text, x, y, color, false)
   }

   fun renderSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {




      if (right > left) {
         context.fill(left, y, right, y + height, 864943990)
      }
   }

   private fun resetSessionStats() {
      totalFishingXp = 0.0
      xpStartMillis = 0L
      lastXpGain = 0.0
      cachedXpPerHour = 0.0
      lastXpCalcMillis = 0L
      sessionEndMillis = 0L
   }

   private fun resetRuntimeState() {
      delayTicks = 0
      postSwapDelayTicks = 0
      currentSurferCount = 0
      lastAnnouncedSurferCount = 0
      playersBobber = null
      mode = StridersurferFishingMacro.Mode.FISHING
      currentActionLabel = "Disabled"
      lastRotateMs = 0L
      pendingDisableAfterCleanup = false
      sessionStopReelSent = false
      petSwapTarget = null
      petSwapWaitTicks = 0
      petSwapTimeoutTicks = 0
      soulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
      soulWhipHoldTicksRemaining = 0
      soulWhipGraceUntilMs = 0L
      soulWhipInitialSequenceDone = false
      lastKnownActivePetName = ""
   }

   private fun disableImmediately(message: String) {

      if (petSwapTarget != null && this.getMc().currentScreen is HandledScreen) {
         if (player != null) {
            player.closeHandledScreen()
         }
      }

      if (player != null) {
         this.restoreRodAndOrientation(player)
      }

      Config.Companion.stridersurferFishingMacroEnabled = false
      JooonConfigManager.write("jooonreimagined")
      this.resetSessionStats()
      this.resetRuntimeState()
      this.sendMessage(message)
   }

   private fun sendMessage(message: String) {

      if (var10000 != null) {
         var10000.sendMessage(Text.literal("${JooonReimagined.Companion.PREFIX_CLEAN}$message") as Text, false)
      }
   }

   private fun striderThreshold(): Int {
      return (Config.stridersurferFishingMacroThreshold).coerceIn(2, 30)
   }

   fun canStartFromCurrentPosition(player: ClientPlayerEntity): Boolean {
      this.isInGalateaTabArea() && this.isInsideActivationPlate(player)
   }

   private fun isInGalateaTabArea(): Boolean {

      if (var10000 == null) {
         return false
      } else {
         val var10: java.util.Collection = var10000.getPlayerList()
         val `this$iv`: java.lang.Iterable = var10
         var var11: Boolean
         if ((var10 as java.util.Collection).isEmpty()) {
            var11 = false
         } else {
            val var4: java.util.Iterator = `this$iv`.iterator()

            while (true) {
               if (!var4.hasNext()) {
                  var11 = false
break
               }

               run label39@{


                  if (var12 != null) {
                     var13 = var12.getString()
                     if (var13 != null) {
                        return@label39
                     }
                  }

                  var13 = info.getProfile().name()
               }

               if (galateaAreaRegex.containsMatchIn(trim(replace$default(var14.replace(var13, ""), "Â", "", false, 4, null)).toString())) {
                  var11 = true
break
               }
            }
         }

         return var11
      }
   }

   fun activationPlateBox(): Box {
      Box(-694.98, 121.015, 79.52, -694.02, 121.015 + 0.06, 80.48)
   }

   fun activationDetectionBox(): Box {
      Box(-694.98, 121.0, 79.52, -694.02, 123.2, 80.48)
   }

   fun isInsideActivationPlate(player: ClientPlayerEntity): Boolean {
      !(Math.abs(player.getX() - -694.5) > 0.48)
         && !(Math.abs(player.getZ() - 80.0) > 0.48)
         && player.getBoundingBox().intersects(this.activationDetectionBox())
      }

   fun applyStartFacing(player: ClientPlayerEntity) {
      player.setYaw(0.0F)
      player.setPitch(16.5F)
      player.headYaw = 0.0F
      player.bodyYaw = 0.0F
      player.lastYaw = 0.0F
      player.lastPitch = 16.5F
      player.lastHeadYaw = 0.0F
      player.lastBodyYaw = 0.0F
   }

   fun tickAntiAfkRotation(player: ClientPlayerEntity) {
      if (Config.Companion.stridersurferFishingMacroEnabled) {
         if (Config.fishingRotate) {
            if (mode === StridersurferFishingMacro.Mode.FISHING) {
               if (!pendingDisableAfterCleanup) {

                  if (now - lastRotateMs >= Config.fishingRotateIntervalMs) {
                     player.setYaw(player.getYaw() + ((Math.random() - 0.5) * 1.2F.toDouble()).toFloat())
                     player.headYaw = player.getYaw()
                     player.bodyYaw = player.getYaw()
                     lastRotateMs = now
                  }
               }
            }
         }
      }
   }

   private fun renderActivationPlate(context: WorldRenderContext) {
      if (this.isInGalateaTabArea()) {

         if (var10000 != null) {

            if (var10 != null) {






               RenderUtils.renderBoxFill(var10, var14, var13, var12, outer, 0.18F, 0.82F, 0.36F, 0.26F)
               RenderUtils.renderBoxOutlineRobust(var10, var14, var13, var12, outer, 0.56F, 1.0F, 0.72F, 0.95F, 0.015F)
               RenderUtils.renderBoxFill(var10, var14, var13, var12, inner, 0.22F, 0.95F, 0.45F, 0.22F)
               RenderUtils.renderText(var10, var10000, "Stridersurfer Start", -694.5, outer.maxY + 0.12, 80.0, -6619214, var11, true)
            }
         }
      }
   }

   private fun isWrongFishingRodMessage(message: String): Boolean {
      return message == "This Fishing Rod does not work in lava." || message == "This Fishing Rod does not work in water."
   }

   private enum class Mode {
      FISHING,
      COMBAT;

      
      fun getEntries(): EnumEntries<StridersurferFishingMacro.Mode> {
         $ENTRIES
      }
   }

   private enum class PetSwapTarget {
      KILLING,
      RECASTING;

      
      fun getEntries(): EnumEntries<StridersurferFishingMacro.PetSwapTarget> {
         $ENTRIES
      }
   }

   private enum class SoulWhipStage {
      NONE,
      HOLDING_FIGSTONE,
      WAITING_AFTER_WHIP_SWAP,
      WAITING_TO_RETURN_TO_FIGSTONE,
      GRACE_AFTER_USE;

      
      fun getEntries(): EnumEntries<StridersurferFishingMacro.SoulWhipStage> {
         $ENTRIES
      }
   }
}
