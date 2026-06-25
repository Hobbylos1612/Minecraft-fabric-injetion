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
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nStridersurferFishingMacro.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StridersurferFishingMacro.kt\njooon/features/galatea/StridersurferFishingMacro\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1048:1\n800#2,11:1049\n1774#2,4:1060\n800#2,11:1065\n288#2,2:1076\n2333#2,14:1078\n800#2,11:1092\n288#2,2:1103\n1747#2,3:1105\n1#3:1064\n*S KotlinDebug\n*F\n+ 1 StridersurferFishingMacro.kt\njooon/features/galatea/StridersurferFishingMacro\n*L\n352#1:1049,11\n353#1:1060,4\n690#1:1065,11\n691#1:1076,2\n701#1:1078,14\n729#1:1092,11\n730#1:1103,2\n958#1:1105,3\n*E\n"])
public object StridersurferFishingMacro {
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
   private final val galateaAreaRegex: Regex = Regex("\\bArea\\s*:\\s*Galatea\\b", RegexOption.IGNORE_CASE)
   private final val fishingXpRegex: Regex = Regex("\\+([0-9]+(?:\\.[0-9]+)?)\\s+Fishing\\b", RegexOption.IGNORE_CASE)
   private final val colorRegex: Regex = Regex("\\u00A7.")
   private final val levelPrefixRegex: Regex = Regex("(?i)\\[lvl[^\\]]*]\\s*")
   @JvmStatic
   private KeyBinding toggleKey;
   private final var overlayReady: Boolean
   private final var appliedSavedPosition: Boolean
   private final var delayTicks: Int
   private final var postSwapDelayTicks: Int
   private final var currentSurferCount: Int
   private final var lastAnnouncedSurferCount: Int
   @JvmStatic
   private FishingBobberEntity playersBobber;
   private final var savedYaw: Float
   private final var savedPitch: Float
   private final var savedRodSlot: Int = -1
   private final var totalFishingXp: Double
   private final var xpStartMillis: Long
   private final var lastXpGain: Double
   private final var cachedXpPerHour: Double
   private final var lastXpCalcMillis: Long
   private final var currentActionLabel: String = "Disabled"
   private final var lastRotateMs: Long
   private final var mode: jooon.features.galatea.StridersurferFishingMacro.Mode = StridersurferFishingMacro.Mode.FISHING
   private final var sessionEndMillis: Long
   private final var pendingDisableAfterCleanup: Boolean
   private final var sessionStopReelSent: Boolean
   private final var petSwapTarget: jooon.features.galatea.StridersurferFishingMacro.PetSwapTarget?
   private final var petSwapWaitTicks: Int
   private final var petSwapTimeoutTicks: Int
   private final var soulWhipStage: jooon.features.galatea.StridersurferFishingMacro.SoulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
   private final var soulWhipHoldTicksRemaining: Int
   private final var soulWhipGraceUntilMs: Long
   private final var soulWhipInitialSequenceDone: Boolean
   private final var lastKnownActivePetName: String = ""

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.togglestridersurferfishingmacro", 76, Category.field_62556))
      ClientTickEvents.END_CLIENT_TICK.register(lambda_0@{ client: MinecraftClient ->
         while (true) {
            var var10000: KeyBinding = toggleKey
            if (toggleKey == null) {
               Intrinsics.throwUninitializedPropertyAccessException("toggleKey")
               var10000 = null
            }

            if (!var10000.method_1436()) {
               INSTANCE.ensureOverlayReady()
               if (client.field_1724 == null) {
                  return@lambda_0
               }

               val player: ClientPlayerEntity = client.field_1724
               if (client.field_1687 == null) {
                  return@lambda_0
               }

               if (!Config.Companion.stridersurferFishingMacroEnabled) {
                  currentActionLabel = "Disabled"
                  currentSurferCount = 0
                  return@lambda_0
               }

               INSTANCE.tick(client, player)
               return@lambda_0
            }

            INSTANCE.toggleMacro()
         }
      })
      WorldRenderEvents.END_MAIN.register({ context: WorldRenderContext ->
         INSTANCE.renderActivationPlate(context)
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

   public fun openHudEditor() {
      this.getMc().execute({ 
         INSTANCE.ensureOverlayReady()
         val var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("stridersurferFishingMacroHud")
         if (var10000 != null) {
            var10000.setPositionSilently(PersistentState.stridersurferFishingMacroHudX, PersistentState.stridersurferFishingMacroHudY)
            var10000.openPositioningGUI()
         }
      })
   }

   public fun openKeybindMenu() {
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
            INSTANCE.disableImmediately("§cStridersurfer Macro was disabled! You were teleported.")
         })
      }
   }

   private fun toggleMacro() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         if (Config.Companion.stridersurferFishingMacroEnabled) {
            this.disableImmediately("§cStridersurfer Macro disabled.")
         } else if (!this.canStartFromCurrentPosition(var10000)) {
            this.sendMessage(
               "Stridersurfer Macro is only for Galatea! Head to Sawyer (-694 121 80) on the Galatea island, and step in the plate to get started!)"
            )
         } else {
            val rodSlot: Int = this.findFishingRodSlot(var10000)
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
               JooonConfigManager.INSTANCE.write("jooonreimagined")
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
      return PlayerController.INSTANCE.rightClick()
   }

   private fun reelRod(): Boolean {
      return PlayerController.INSTANCE.rightClick()
   }

   fun countStriderSurferTags(player: ClientPlayerEntity): Int {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         0
      } else {
         val var17: java.lang.Iterable = var10000.method_18112()
         val `count$iv`: java.lang.Iterable = CollectionsKt.toList(var17)
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
            val var12: Int = 0

            for (var14 in var10) {
               run label64@{
                  val var15: ArmorStandEntity = var14 as ArmorStandEntity
                  if ((var14 as ArmorStandEntity).method_5858(player as Entity) <= 100.0 && (var14 as ArmorStandEntity).method_5797() != null) {
                     val var18: StridersurferFishingMacro = INSTANCE
                     val var10001: Text = var15.method_5797()
                     val var21: java.lang.String = var10001.getString()
                     if (StringsKt.contains$default(var18.normalizeName(var21), "stridersurfer", false, 2, null)) {
                        var19 = true
                        return@label64
                     }
                  }

                  var19 = false
               }

               if (var19) {
                  if (++var12 < 0) {
                     CollectionsKt.throwCountOverflow()
                  }
               }
            }

            var20 = var12
         }

         var20
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
         true
      } else {
         when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$0[soulWhipStage.ordinal()]) {
            1 -> false
            2 -> {
               currentActionLabel = "Holding Figstone"
               if (!this.equipFigstoneSplitter(player)) {
                  this.disableImmediately("§cCould not find a Figstone Splitter in your hotbar.")
                  true
               } else if (soulWhipHoldTicksRemaining > 0) {
                  soulWhipHoldTicksRemaining += -1
                  true
               } else {
                  val whipSlot: Int = this.findSoulWhipOrFlaySlot(player)
                  if (whipSlot == -1) {
                     this.abortCombatToFishing("§cCould not find Soul Whip or Flaming Flay in your hotbar.")
                     true
                  }

                  this.selectHotbarSlot(player, whipSlot)
                  soulWhipStage = StridersurferFishingMacro.SoulWhipStage.WAITING_AFTER_WHIP_SWAP
                  currentActionLabel = "Swapping to Soul Whip"
                  true
               }
            }
            3 -> {
               currentActionLabel = "Using Soul Whip"
               if (!PlayerController.INSTANCE.rightClick()) {
                  true
               }

               soulWhipStage = StridersurferFishingMacro.SoulWhipStage.WAITING_TO_RETURN_TO_FIGSTONE
               currentActionLabel = "Returning to Figstone"
               true
            }
            4 -> {
               currentActionLabel = "Returning to Figstone"
               if (!this.equipFigstoneSplitter(player)) {
                  this.disableImmediately("§cCould not find a Figstone Splitter in your hotbar.")
                  true
               }

               soulWhipGraceUntilMs = System.currentTimeMillis() + 800L
               soulWhipStage = StridersurferFishingMacro.SoulWhipStage.GRACE_AFTER_USE
               currentActionLabel = if (currentSurferCount > 0) "Waiting after Soul Whip" else "Casting rod"
               true
            }
            5 -> {
               if (currentSurferCount <= 0) {
                  this.finishCombatAndResumeFishing(player)
                  true
               } else {
                  if (System.currentTimeMillis() < soulWhipGraceUntilMs) {
                     currentActionLabel = "Waiting after Soul Whip"
                     true
                  }

                  soulWhipStage = StridersurferFishingMacro.SoulWhipStage.NONE
                  false
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
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         INSTANCE.restoreRodAndOrientation(var10000)
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
         val var10000: ItemStack = player.method_31548().method_5438(slot)
         if (!var10000.method_7960()) {
            val var4: java.lang.String = var10000.method_7964().getString()
            if (StringsKt.contains(var4, "Figstone Splitter", true)) {
               this.selectHotbarSlot(player, slot)
            }
         }
      }

      false
   }

   fun ensureFishingRodEquipped(player: ClientPlayerEntity): Boolean {
      val targetSlot: Int = if (0 <= savedRodSlot && savedRodSlot < 9 && this.isFishingRodSlot(player, savedRodSlot))
         savedRodSlot
         else
         this.findFishingRodSlot(player)
         if (targetSlot == -1) {
         false
      } else {
         savedRodSlot = targetSlot
         this.selectHotbarSlot(player, targetSlot)
      }
   }

   fun findFishingRodSlot(player: ClientPlayerEntity): Int {
      repeat(8) { slot ->
         if (this.isFishingRodSlot(player, slot)) {
            slot
         }
      }

      -1
   }

   fun isFishingRodSlot(player: ClientPlayerEntity, slot: Int): Boolean {
      val var10000: ItemStack = player.method_31548().method_5438(slot)
      if (var10000.method_7960()) {
         false
      } else {
         if (!var10000.method_31574(Items.field_8378)) {
            val var4: java.lang.String = var10000.method_7964().getString()
            if (!StringsKt.contains(var4, "rod", true)) {
               false
            }
         }

         true
      }
   }

   fun findSoulWhipOrFlaySlot(player: ClientPlayerEntity): Int {
      repeat(8) { slot ->
         val var10000: ItemStack = player.method_31548().method_5438(slot)
         if (!var10000.method_7960()) {
            val var10001: java.lang.String = var10000.method_7964().getString()
            val name: java.lang.String = this.normalizeName(var10001)
            if (StringsKt.contains$default(name, "soul whip", false, 2, null) || StringsKt.contains$default(name, "flaming flay", false, 2, null)) {
               slot
            }
         }
      }

      -1
   }

   fun selectHotbarSlot(player: ClientPlayerEntity, slot: Int): Boolean {
      if (0 > slot || slot >= 9) {
         false
      } else if (player.method_31548().method_67532() == slot) {
         true
      } else {
         player.method_31548().method_61496(slot)
         PlayerController.INSTANCE.noteHotbarSwapThisTick()
         postSwapDelayTicks = Math.max(postSwapDelayTicks, 1)
         true
      }
   }

   private fun shouldRunPetSwap(target: jooon.features.galatea.StridersurferFishingMacro.PetSwapTarget): Boolean {
      if (!Config.stridersurferFishingMacroAutoPetSwap) {
         return false
      } else {
         val wanted: java.lang.String = this.normalizeName(this.petNameFor(target))
         if (StringsKt.isBlank(wanted)) {
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
      var var10000: java.lang.String
      when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$1[target.ordinal()]) {
         1 -> var10000 = StringsKt.trim(Config.stridersurferFishingMacroKillPetName).toString()
         2 -> var10000 = StringsKt.trim(Config.stridersurferFishingMacroRecastPetName).toString()
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
            player.field_3944.method_45730("pets")
            petSwapTarget = target
            petSwapWaitTicks = 0
            petSwapTimeoutTicks = 50
            currentActionLabel = if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) "Opening kill pet" else "Opening recast pet"
         }
      }
   }

   fun processPetSwap(player: ClientPlayerEntity): Boolean {
      if (petSwapTarget == null) {
         false
      } else {
         val target: StridersurferFishingMacro.PetSwapTarget = petSwapTarget
         if (petSwapTimeoutTicks > 0) {
            petSwapTimeoutTicks += -1
         }

         if (petSwapTimeoutTicks <= 0) {
            if (this.getMc().field_1755 is HandledScreen) {
               player.method_7346()
            }

            this.finishPetSwap(player, target, false, "§eCould not open the Pets menu.")
            true
         } else {
            val petName: Screen = this.getMc().field_1755
            val var6: HandledScreen = petName as? HandledScreen
            if ((petName as? HandledScreen) != null) {
               val var10000: java.lang.String = var6.method_25440().getString()
               if (StringsKt.contains(var10000, "pets", true)) {
                  if (petSwapWaitTicks < 3) {
                     val var8: Int = petSwapWaitTicks++
                     currentActionLabel = "Waiting for pets menu"
                     true
                  }

                  val var7: java.lang.String = this.petNameFor(target)
                  val slotIndex: Int = this.findPetMenuSlot(var6, player, var7)
                  if (slotIndex == -1) {
                     player.method_7346()
                     this.finishPetSwap(player, target, false, "§eCould not find pet \"$var7\" in the Pets menu.")
                     true
                  }

                  currentActionLabel = if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) "Swapping to kill pet" else "Swapping to recast pet"
                  val var9: ClientPlayerInteractionManager = this.getMc().field_1761
                  if (var9 != null) {
                     var9.method_2906(var6.method_17577().field_7763, slotIndex, 0, SlotActionType.field_7790, player as PlayerEntity)
                  }

                  player.method_7346()
                  this.finishPetSwap(player, target, true, null)
                  true
               }
            }

            currentActionLabel = if (target === StridersurferFishingMacro.PetSwapTarget.KILLING) "Waiting for kill pet menu" else "Waiting for recast pet menu"
            true
         }
      }
   }

   fun finishPetSwap(player: ClientPlayerEntity, target: StridersurferFishingMacro.PetSwapTarget, success: Boolean, failureMessage: java.lang.String) {
      petSwapTarget = null
      petSwapWaitTicks = 0
      petSwapTimeoutTicks = 0
      if (success) {
         lastKnownActivePetName = this.normalizeName(this.petNameFor(target))
      }

      if (!success && failureMessage != null && !StringsKt.isBlank(failureMessage)) {
         this.sendMessage(failureMessage)
      }

      when (StridersurferFishingMacro.WhenMappings.$EnumSwitchMapping$1[target.ordinal()]) {
         1 -> this.startCombatMode(player)
         2 -> currentActionLabel = if (pendingDisableAfterCleanup) "Finishing session" else "Waiting for bite"
         else -> throw NoWhenBranchMatchedException()
      }
   }

   fun findPetMenuSlot(screen: HandledScreen<*>, player: ClientPlayerEntity, petName: java.lang.String): Int {
      val wanted: java.lang.String = this.normalizeName(petName)
      if (StringsKt.isBlank(wanted)) {
         -1
      } else {
         val var5: java.util.Iterator = (screen.method_17577().field_7761 as java.lang.Iterable).iterator()
         var var6: Int = 0

         while (var5.hasNext()) {
            val index: Int = var6++
            val slot: Slot = var5.next() as Slot
            if (slot.field_7871 != player.method_31548()) {
               val var10000: ItemStack = slot.method_7677()
               if (!var10000.method_7960()) {
                  val var10001: java.lang.String = var10000.method_7964().getString()
                  if (StringsKt.contains$default(this.normalizeName(var10001), wanted, false, 2, null)) {
                     index
                  }
               }
            }
         }

         -1
      }
   }

   private fun normalizeName(raw: String): String {
      val var5: java.lang.String = StringsKt.trim(
            Regex("\\s+").replace(levelPrefixRegex.replace(StringsKt.replace$default(colorRegex.replace(raw, ""), "Â", "", false, 4, null), ""), " ")
         )
         .toString()
         val var10000: Locale = Locale.US
      val var6: java.lang.String = var5.toLowerCase(var10000)
      return var6
   }

   fun findExclamationStand(player: ClientPlayerEntity): ArmorStandEntity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var16: java.lang.Iterable = var10000.method_18112()
         val `$this$filterIsInstanceTo$iv$iv`: java.lang.Iterable = CollectionsKt.toList(var16)
         val `element$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterIsInstanceTo$iv$iv`) {
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
               val var14: ArmorStandEntity = var13 as ArmorStandEntity
               if ((var13 as ArmorStandEntity).method_5858(player as Entity) <= 256.0 && (var13 as ArmorStandEntity).method_5797() != null) {
                  val var17: Text = var14.method_5797()
                  val var18: java.lang.String = var17.getString()
                  if (StringsKt.trim(var18).toString() == "!!!") {
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
      if (client.field_1687 != null) {
         var var10000: java.util.List = client.field_1687.method_8390(StriderEntity.class, player.method_5829().method_1014(10.0), { p0: Any ->
            `$tmp0`(p0)
         })
         val `iterator$iv`: java.util.Iterator = var10000.iterator()
         if (!`iterator$iv`.hasNext()) {
            var10000 = null
         } else {
            var `minElem$iv`: Any = `iterator$iv`.next()
            if (!`iterator$iv`.hasNext()) {
               var10000 = (java.util.List)`minElem$iv`
            } else {
               var var16: Double = (`minElem$iv` as StriderEntity).method_5858(player as Entity)

               do {
                  val var17: Any = `iterator$iv`.next()
                  val var18: Double = (var17 as StriderEntity).method_5858(player as Entity)
                  if (java.lang.Double.compare(var16, var18) > 0) {
                     `minElem$iv` = var17
                     var16 = var18
                  }
               } while (`iterator$iv`.hasNext())

               var10000 = (java.util.List)`minElem$iv`
            }
         }

         val target: StriderEntity = var10000 as StriderEntity
         if (var10000 as StriderEntity == null) {
            currentActionLabel = "Looking for strider"
         } else {
            currentActionLabel = "Killing"
            this.aimAtTarget(player, target)
            if (PlayerController.INSTANCE.leftClick()) {
               player.method_6104(Hand.field_5808)
            }
         }
      }
   }

   fun restoreRodAndOrientation(player: ClientPlayerEntity) {
      this.ensureFishingRodEquipped(player)
      player.method_36456(savedYaw)
      player.method_36457(savedPitch)
      player.field_6241 = savedYaw
      player.field_6283 = savedYaw
      player.field_5982 = savedYaw
      player.field_6004 = savedPitch
      player.field_6259 = savedYaw
      player.field_6220 = savedYaw
   }

   fun updatePlayersBobber(player: ClientPlayerEntity) {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 != null) {
         val var16: java.lang.Iterable = var10000.method_18112()
         val `$this$filterIsInstanceTo$iv$iv`: java.lang.Iterable = CollectionsKt.toList(var16)
         val `element$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in `$this$filterIsInstanceTo$iv$iv`) {
            if (`element$iv$iv` is FishingBobberEntity) {
               `element$iv`.add(`element$iv$iv`)
            }
         }

         val var12: java.util.Iterator = (`element$iv` as java.util.List).iterator()

         while (true) {
            if (var12.hasNext()) {
               val var13: Any = var12.next()
               if (!((var13 as FishingBobberEntity).method_24921() == player)) {
                  continue
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
      val dx: Double = target.method_23317() - player.method_23317()
      val dz: Double = target.method_23321() - player.method_23321()
      val dy: Double = target.method_23320() - player.method_23320()
      val horizontalDistance: Double = Math.sqrt(dx * dx + dz * dz)
      player.method_36456((float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F)
      player.method_36457(-((float)Math.toDegrees(Math.atan2(dy, horizontalDistance))))
   }

   private fun handleActionbarMessage(original: String?) {
      if (original != null && !StringsKt.isBlank(original)) {
         val var10000: MatchResult = Regex.find$default(
            fishingXpRegex, StringsKt.replace$default(colorRegex.replace(original, ""), ",", "", false, 4, null), 0, 2, null
         )
         if (var10000 != null) {
            val var7: java.lang.String = CollectionsKt.getOrNull(var10000.getGroupValues(), 1) as java.lang.String
            if (var7 != null) {
               val var8: java.lang.Double = StringsKt.toDoubleOrNull(var7)
               if (var8 != null) {
                  val gain: Double = var8
                  if (xpStartMillis == 0L) {
                     xpStartMillis = System.currentTimeMillis()
                     lastXpCalcMillis = xpStartMillis
                  }

                  totalFishingXp += gain
                  lastXpGain = gain
                  return
               }
            }
         }
      }
   }

   private fun recalcXpPerHourIfNeeded() {
      if (xpStartMillis > 0L && !(totalFishingXp <= 0.0)) {
         val now: Long = System.currentTimeMillis()
         if (now - lastXpCalcMillis >= 5000L) {
            val elapsed: Long = now - xpStartMillis
            if (now - xpStartMillis > 0L) {
               val hours: Double = elapsed / 3600000.0
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
         val targetX: Pair = if (PersistentState.stridersurferFishingMacroHudInitDone)
            TuplesKt.to(PersistentState.stridersurferFishingMacroHudX, PersistentState.stridersurferFishingMacroHudY)
            else
            this.defaultHudPosition()
            var var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("stridersurferFishingMacroHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "stridersurferFishingMacroHud",
                  "Stridersurfer Fishing Macro HUD",
                  (targetX.getFirst() as java.lang.Number).intValue(),
                  (targetX.getSecond() as java.lang.Number).intValue(),
                  190,
                  76
               )
            }

         var10000.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
            INSTANCE.renderHud(context, x, y)
            Unit.INSTANCE
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.stridersurferFishingMacroHudX = x
            PersistentState.stridersurferFishingMacroHudY = y
            PersistentState.stridersurferFishingMacroHudInitDone = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }
         var10000.register()
         overlayReady = true
      }

      if (!appliedSavedPosition) {
         val var3: Int = PersistentState.stridersurferFishingMacroHudX
         val var4: Int = PersistentState.stridersurferFishingMacroHudY
         val var5: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("stridersurferFishingMacroHud")
         if (var5 != null) {
            var5.setPositionSilently(var3, var4)
         }

         appliedSavedPosition = true
      }
   }

   fun renderHud(context: DrawContext, x: Int, y: Int) {
      val preview: Boolean = !Config.Companion.stridersurferFishingMacroEnabled && this.getMc().field_1755 is MovableOverlayScreen
      if (Config.Companion.stridersurferFishingMacroEnabled || preview) {
         this.recalcXpPerHourIfNeeded()
         val lines: java.util.List = CollectionsKt.listOf(
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
            var var24: Int = INSTANCE.getMc().field_1772.method_1727(panelHeight.next() as java.lang.String)

            while (panelHeight.hasNext()) {
               val var27: Int = INSTANCE.getMc().field_1772.method_1727(panelHeight.next() as java.lang.String)
               if (var24 < var27) {
                  var24 = var27
               }
            }

            val panelWidth: Int = Math.max(this.getMc().field_1772.method_1727("JR Strider Macro") + 24, var24 + 18)
            val var23: Int = 14 + this.getMc().field_1772.field_2000 + 10 + lines.size() * (this.getMc().field_1772.field_2000 + 3) + 8
            val var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("stridersurferFishingMacroHud")
            if (var10000 != null) {
               var10000.width = panelWidth
               var10000.height = var23
            }

            val textColor: Int = -6953104
            val textShadow: Int = -14203624
            context.method_25294(x, y, x + panelWidth, y + var23, -871689461)
            context.method_25294(x + 2, y + 2, x + panelWidth - 2, y + var23 - 2, -871359728)
            context.method_25294(x + 3, y + 3, x + panelWidth - 3, y + 18, -1441256939)
            context.method_73198(x, y, panelWidth, var23, -10308789)
            context.method_73198(x + 1, y + 1, panelWidth - 2, var23 - 2, -13541594)
            this.renderSweep(context, x + 3, y + 3, panelWidth - 6, 15)
            this.drawHudText(
               context, "JR Strider Macro", x + (panelWidth - this.getMc().field_1772.method_1727("JR Strider Macro")) / 2, y + 5, -2818175, -12293606
            )
            var lineY: Int = y + 24

            for (line in lines) {
               this.drawHudText(context, line, x + 8, lineY, textColor, textShadow)
               lineY += this.getMc().field_1772.field_2000 + 3
            }
         }
      }
   }

   private fun previewLabel(current: String, preview: Boolean, fallback: String): String {
      return if (!preview && !StringsKt.isBlank(current) && !(current == "Disabled")) current else fallback
   }

   private fun formatXpPerHour(): String {
      if (cachedXpPerHour <= 0.0) {
         return "0"
      } else {
         val var2: Locale = Locale.US
         val var4: Array<Any> = arrayOf(cachedXpPerHour)
         val var10000: java.lang.String = java.lang.String.format(var2, "%,.1f", Arrays.copyOf(var4, var4.length))
         return var10000
      }
   }

   private fun formatTimeLeft(preview: Boolean): String {
      if (preview) {
         return "45m 00s"
      } else if (sessionEndMillis <= 0L) {
         return "00m 00s"
      } else {
         val totalSeconds: Long = Math.max(0L, sessionEndMillis - System.currentTimeMillis()) / 1000L
         val minutes: Long = totalSeconds / 60L
         val seconds: Long = totalSeconds % 60L
         val var11: Locale = Locale.US
         val var13: Array<Any> = arrayOf(minutes, seconds)
         val var10000: java.lang.String = java.lang.String.format(var11, "%02dm %02ds", Arrays.copyOf(var13, var13.length))
         return var10000
      }
   }

   private fun defaultHudPosition(): Pair<Int, Int> {
      return TuplesKt.to(10, 10)
   }

   fun drawHudText(context: DrawContext, text: java.lang.String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.method_51439(this.getMc().field_1772, Text.method_43470(text) as Text, x + 1, y + 1, shadowColor, false)
      context.method_51439(this.getMc().field_1772, Text.method_43470(text) as Text, x, y, color, false)
   }

   fun renderSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {
      val sweepWidth: Int = Math.max(18, width / 5)
      val sweepX: Int = x - sweepWidth + (int)((width + sweepWidth) * ((float)(System.currentTimeMillis() % 2200L) / (float)2200L))
      val left: Int = RangesKt.coerceAtLeast(sweepX, x)
      val right: Int = RangesKt.coerceAtMost(sweepX + sweepWidth, x + width)
      if (right > left) {
         context.method_25294(left, y, right, y + height, 864943990)
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
      val player: ClientPlayerEntity = this.getMc().field_1724
      if (petSwapTarget != null && this.getMc().field_1755 is HandledScreen) {
         if (player != null) {
            player.method_7346()
         }
      }

      if (player != null) {
         this.restoreRodAndOrientation(player)
      }

      Config.Companion.stridersurferFishingMacroEnabled = false
      JooonConfigManager.INSTANCE.write("jooonreimagined")
      this.resetSessionStats()
      this.resetRuntimeState()
      this.sendMessage(message)
   }

   private fun sendMessage(message: String) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var10000.method_7353(Text.method_43470("${JooonReimagined.Companion.PREFIX_CLEAN}$message") as Text, false)
      }
   }

   private fun striderThreshold(): Int {
      return RangesKt.coerceIn(Config.stridersurferFishingMacroThreshold, 2, 30)
   }

   fun canStartFromCurrentPosition(player: ClientPlayerEntity): Boolean {
      this.isInGalateaTabArea() && this.isInsideActivationPlate(player)
   }

   private fun isInGalateaTabArea(): Boolean {
      val var10000: ClientPlayNetworkHandler = this.getMc().method_1562()
      if (var10000 == null) {
         return false
      } else {
         val var10: java.util.Collection = var10000.method_2880()
         val `$this$any$iv`: java.lang.Iterable = var10
         var var11: Boolean
         if ((var10 as java.util.Collection).isEmpty()) {
            var11 = false
         } else {
            val var4: java.util.Iterator = `$this$any$iv`.iterator()

            while (true) {
               if (!var4.hasNext()) {
                  var11 = false
                  break
               }

               run label39@{
                  val info: PlayerListEntry = var4.next() as PlayerListEntry
                  val var12: Text = info.method_2971()
                  if (var12 != null) {
                     var13 = var12.getString()
                     if (var13 != null) {
                        return@label39
                     }
                  }

                  var13 = info.method_2966().name()
               }

               val var14: Regex = colorRegex
               if (galateaAreaRegex.containsMatchIn(StringsKt.trim(StringsKt.replace$default(var14.replace(var13, ""), "Â", "", false, 4, null)).toString())) {
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
      !(Math.abs(player.method_23317() - -694.5) > 0.48)
         && !(Math.abs(player.method_23321() - 80.0) > 0.48)
         && player.method_5829().method_994(this.activationDetectionBox())
      }

   fun applyStartFacing(player: ClientPlayerEntity) {
      player.method_36456(0.0F)
      player.method_36457(16.5F)
      player.field_6241 = 0.0F
      player.field_6283 = 0.0F
      player.field_5982 = 0.0F
      player.field_6004 = 16.5F
      player.field_6259 = 0.0F
      player.field_6220 = 0.0F
   }

   fun tickAntiAfkRotation(player: ClientPlayerEntity) {
      if (Config.Companion.stridersurferFishingMacroEnabled) {
         if (Config.fishingRotate) {
            if (mode === StridersurferFishingMacro.Mode.FISHING) {
               if (!pendingDisableAfterCleanup) {
                  val now: Long = System.currentTimeMillis()
                  if (now - lastRotateMs >= Config.fishingRotateIntervalMs) {
                     player.method_36456(player.method_36454() + (float)((Math.random() - 0.5) * (double)1.2F))
                     player.field_6241 = player.method_36454()
                     player.field_6283 = player.method_36454()
                     lastRotateMs = now
                  }
               }
            }
         }
      }
   }

   private fun renderActivationPlate(context: WorldRenderContext) {
      if (this.isInGalateaTabArea()) {
         val var10000: MatrixStack = context.matrices()
         if (var10000 != null) {
            val var10: VertexConsumerProvider = context.consumers()
            if (var10 != null) {
               val var11: Camera = context.gameRenderer().method_19418()
               val var12: Vec3d = var11.method_71156()
               val var13: Entry = var10000.method_23760()
               val var14: Matrix4f = var13.method_23761()
               val outer: Box = this.activationPlateBox()
               val inner: Box = Box(-694.84, outer.field_1322 + 0.003, 79.66, -694.16, outer.field_1325 - 0.004, 80.34)
               RenderUtils.INSTANCE.renderBoxFill(var10, var14, var13, var12, outer, 0.18F, 0.82F, 0.36F, 0.26F)
               RenderUtils.INSTANCE.renderBoxOutlineRobust(var10, var14, var13, var12, outer, 0.56F, 1.0F, 0.72F, 0.95F, 0.015F)
               RenderUtils.INSTANCE.renderBoxFill(var10, var14, var13, var12, inner, 0.22F, 0.95F, 0.45F, 0.22F)
               RenderUtils.INSTANCE.renderText(var10, var10000, "Stridersurfer Start", -694.5, outer.field_1325 + 0.12, 80.0, -6619214, var11, true)
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

      @JvmStatic
      fun getEntries(): EnumEntries<StridersurferFishingMacro.Mode> {
         $ENTRIES
      }
   }

   private enum class PetSwapTarget {
      KILLING,
      RECASTING;

      @JvmStatic
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

      @JvmStatic
      fun getEntries(): EnumEntries<StridersurferFishingMacro.SoulWhipStage> {
         $ENTRIES
      }
   }
}
