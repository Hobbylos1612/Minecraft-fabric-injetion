package jooon.features.fishing

import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.NoSuchElementException
import java.util.UUID
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.gui.MovableOverlayScreen
import jooon.net.PacketRateLimiter
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import jooon.util.PlayerController
import jooon.util.Rotator
import kotlin.concurrent.ThreadsKt
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.Intrinsics
import kotlin.random.Random
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.fluid.FluidState
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.tag.FluidTags
import net.minecraft.text.Text
import net.minecraft.util.TypeFilter
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

object FunnyFishing : ClientModInitializer {
   private var masterEnabled: Boolean
   
   private KeyBinding toggleKey;
   private var startedAtMs: Long
   
   private BlockPos mainLookAtBlock;
   private var lastRotateMs: Long
   private var lastReelMs: Long
   private var lastSellMs: Long
   private var lastDeployCheckMs: Long
   private var nextTotemDeployAtMs: Long
   private var nextPowerOrbDeployAtMs: Long
   private var nextUmbrellaDeployAtMs: Long
   private var lastCastMs: Long
   private var lastActivityMs: Long
   private var recastQueued: Boolean
   private var biteArmed: Boolean
   private var hadBobber: Boolean
   private var exclaimSeenThisCast: Boolean
   private var lastExclaimTriggerMs: Long
   private var castVerifyInProgress: Boolean
   private var rodSequenceInProgress: Boolean
   private var fishingType: jooon.features.fishing.FunnyFishing.FishingType = FunnyFishing.FishingType.WATER
   
   private FishingBobberEntity playersBobber;
   private var killing: Boolean
   private var currentKillTargetName: String = ""
   private var suppressNextAutoRecastUntilMs: Long
   private var fishingHudReady: Boolean
   private var fishingHudAppliedSavedPosition: Boolean
   private var fishingHudActionCached: String = "Waiting for bite"
   private var fishingHudActionUpdatedAtMs: Long
   private var fishingHudTargetCached: String = "--"
   private var fishingHudTargetUpdatedAtMs: Long
   private const val ARM_DELAY_MS: Long = 350L
   private const val MIN_BITE_AFTER_CAST_MS: Long = 400L
   private const val RECAST_LOCK_MS: Long = 1350L
   private const val POST_CAST_SETTLE_MS: Long = 450L
   private const val FISHING_HUD_STATE_UPDATE_MS: Long = 220L
   private const val EXCLAIM_NAME: String = "!!!"
   private const val EXCLAIM_RADIUS: Double = 1.6
   private const val EXCLAIM_MAX_DY: Double = 2.2
   private const val EXCLAIM_COOLDOWN_MS: Long = 250L
   private const val MIN_SWAP_USE_WEAPON_MS: Long = 155L
   private const val MIN_SWAP_USE_ROD_MS: Long = 251L
   private const val CLICK_INTERVAL_MS: Long = 160L
   private var suppressSlotCheckUntilMs: Long
   
   private BlockPos lastTotemPlacePos;
   
   private BlockPos lastPowerOrbPlacePos;
   
   private BlockPos lastUmbrellaPlacePos;
   private var placingDeployable: Boolean
   private var deployableActionLabel: String = ""
   private var suppressAntiAfkRotation: Boolean
   private var goldenFishFocus: Boolean
   
   private ArmorStandEntity goldenFishTarget;
   private var goldenFishPendingCast: Boolean
   private var goldenFishAimStartedMs: Long
   private var lastThrowHeardMs: Long
   private const val EMPTY_SLOTS_SELL_THRESHOLD: Int = 2
   private const val DEPLOY_CHECK_INTERVAL_MS: Long = 450L
   private const val TOTEM_DURATION_MS: Long = 120000L
   private const val SHORT_POWER_ORB_DURATION_MS: Long = 30000L
   private const val LONG_POWER_ORB_DURATION_MS: Long = 60000L
   private const val UMBRELLA_DURATION_MS: Long = 300000L
   private const val GF_CAST_DELAY_MS: Long = 300L
   private const val GF_ACQUIRE_WINDOW_MS: Long = 2500L
   private const val GF_REACQUIRE_MS: Long = 150L
   private var lastWorldKeyHash: Int?
   private var gfEventStartMs: Long
   private var gfLastAcquireMs: Long
   private const val GF_GRACE_AFTER_EMERGE_MS: Long = 3000L
   private var gfGraceUntilMs: Long
   private const val GF_STATIONARY_MS: Long = 1800L
   private const val GF_BOBBER_MIN_SPEED2: Double = 5.0E-4
   private const val GF_RETRY_COOLDOWN_MS: Long = 700L
   private var gfLastRetryMs: Long
   private const val GF_NO_TARGET_MS: Long = 1500L
   private const val GF_NO_PROGRESS_MS: Long = 4000L

   private val seaCreatureNameLookup: Map<String, String> by lazy(
      { 
         val `this$iv`: java.lang.Iterable = distinct(FishingStrings.seaCreatureMessages.values())
         val `destination$iv$iv`: java.util.Map = LinkedHashMap(
            (mapCapacity(`this$iv`.count().coerceAtLeast(10))).coerceAtLeast(16)
         )

         for (`element$iv$iv` in `this$iv`) {
            `destination$iv$iv`.put(normalizeStandName(`element$iv$iv` as String), `element$iv$iv` as String)
         }

         `destination$iv$iv`
      }
   )
      private get() {
         return seaCreatureNameLookup$delegate.getValue() as MutableMap<String, String>
      }


   private val seaCreatureKeysByLengthDesc: List<String> by lazy(
      { 
         sortedWith(
            seaCreatureNameLookup.keySet(), FunnyFishing$seaCreatureKeysByLengthDesc_delegate$lambda$4$$inlined$sortedByDescending$1()
         )
      }
   )
      private get() {
         return seaCreatureKeysByLengthDesc$delegate.getValue() as MutableList<String>
      }


   private val crimsonIsleAreaRegex: Regex = Regex("\\bArea\\s*:\\s*Crimson Isle\\b", RegexOption.IGNORE_CASE)
   private val galateaAreaRegex: Regex = Regex("\\bArea\\s*:\\s*Galatea\\b", RegexOption.IGNORE_CASE)
   private var savedYaw: Float
   private var savedPitch: Float
   private var haveSavedView: Boolean
   private var hardPausedForMelee: Boolean
   private var renderHooked: Boolean

   fun getClient(): MinecraftClient {
return var10000
   }

   fun saveView(p: ClientPlayerEntity) {
      if (!haveSavedView) {
         savedYaw = p.getYaw()
         savedPitch = p.getPitch()
         haveSavedView = true
      }
   }

   private fun restoreViewIfSaved() {
      if (haveSavedView) {

         if (var10000 != null) {
            var10000.setYaw(savedYaw)
            var10000.setPitch(savedPitch)
         }

         haveSavedView = false
      }
   }

   fun ownsBobber(b: FishingBobberEntity, p: ClientPlayerEntity): Boolean {
      b != null && p != null && b.getOwner() == p && !b.isRemoved() && !(b.squaredDistanceTo(p as Entity) < 1.5)
   }

   fun validBobber(b: FishingBobberEntity, p: ClientPlayerEntity): Boolean {
      if (!this.ownsBobber(b, p)) {
return false
      } else {
         !b.isOnGround()
      }
   }

   private fun isRodOut(): Boolean {
      return this.ownsBobber(playersBobber, this.getClient().player)
   }

   fun pauseForMeleeImmediate() {
      hardPausedForMelee = true
      recastQueued = false
      biteArmed = false
      suppressAntiAfkRotation = true
      Rotator.clear()
   }

   fun resumeAfterMelee() {
      suppressAntiAfkRotation = false
      hardPausedForMelee = false
   }

   fun recastAfterMelee() {
      if (masterEnabled && !hardPausedForMelee) {
         if (!Config.fishingEnabled) {
            Config.fishingEnabled = true
         }

         ThreadsKt.thread$default(true, false, null, "RecastAfterMelee", 0, lambda_7@{ 

            if (rod == -1) {
               return@lambda_7 Unit
            } else {
               getClient().execute({ 
                  selectHotbarSlot(`$rod`)
               })
               Thread.sleep(251L)
               castWithVerify$default(INSTANCE, 1000L, 200L, 0, 4, null)
               return@lambda_7 Unit
            }
         }, 22, null)
      }
   }

   private fun canCatchNow(): Boolean {
      return !Config.slugFishEnabled || System.currentTimeMillis() - lastCastMs >= (Config.slugFishMinWaitSec).coerceAtLeast(0) * 1000L
   }

   open fun onInitializeClient() {
      this.hookRotatorRenderTick()
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.togglefishing", 71, Category.MISC))
      ClientTickEvents.END_CLIENT_TICK
         .register(
            lambda_13@{ it: MinecraftClient ->
               PacketRateLimiter.onTick()
               FishingMeleeMobs.onTick()

               while (true) {
                  var var10000: KeyBinding = toggleKey
                  if (toggleKey == null) {
                     throwUninitializedPropertyAccessException("toggleKey")
                     var10000 = null
                  }

                  if (!var10000.wasPressed()) {
                     ensureFishingHudReady()


                     if (masterEnabled && Config.fishingEnabled) {
                        if (p != null && w != null) {
                           if (hardPausedForMelee) {
                              return@lambda_13
                           }

                           if (System.currentTimeMillis() - startedAtMs >= Config.fishingSessionMinutes * 60000L) {
                              message("Session ended (${Config.fishingSessionMinutes} min).")
                              disableFishing()
                              return@lambda_13
                           }

                           updatePlayersBobber()

                           if (rodSlot != p.getInventory().getSelectedSlot()
                              && System.currentTimeMillis() > suppressSlotCheckUntilMs
                              && rodSlot != -1
                              && startedAtMs != 0L) {
                              message("Detected slot change, disabling.")
                              disableFishing()
                              return@lambda_13
                           }

                           if (processAutoDeployables()) {
                              return@lambda_13
                           }

                           if (!goldenFishFocus
                              && !recastQueued
                              && !killing
                              && !placingDeployable
                              && !rodSequenceInProgress
                              && !isRodOut()
                              && System.currentTimeMillis() - lastCastMs >= 1200L) {
                              if (Config.fishingKillingMode != Config.Companion.FishingKillMode.OFF
                                 && canUseSeaCreatureFallbackNow$default(INSTANCE, 0L, 1, null)) {

                                 if (var21 != null && startAutoKillForCreatureName(var21)) {
                                    return@lambda_13
                                 }
                              }

                              currentKillTargetName = ""
                              castWithVerify$default(INSTANCE, 1000L, 200L, 0, 4, null)
                              return@lambda_13
                           }

                           if (playersBobber != null) {
                              if (!goldenFishFocus && !rodSequenceInProgress && playersBobber.isOnGround() && System.currentTimeMillis() - lastCastMs > 1200L
                                 )
                               {
                                 reelIn$default(INSTANCE, true, false, false, 6, null)
                              }
                           }

                           if (!goldenFishFocus) {
                              if (playersBobber != null) {

                                 if (!exclaimSeenThisCast
                                    && System.currentTimeMillis() - lastExclaimTriggerMs > 250L
                                    && detectExclaimAbove(var22)
                                    && canCatchNow()) {
                                    exclaimSeenThisCast = true
                                    lastExclaimTriggerMs = System.currentTimeMillis()
                                    lastActivityMs = lastExclaimTriggerMs
                                    queueCatch(true)
                                 }
                              }
                           }

                           if (!goldenFishFocus && !rodSequenceInProgress && System.currentTimeMillis() - lastActivityMs > 44000L) {
                              reelIn$default(INSTANCE, true, false, false, 6, null)
                           }

                           if (Config.fishingRotate
                              && !goldenFishFocus
                              && !suppressAntiAfkRotation
                              && !Rotator.isActive
                              && System.currentTimeMillis() - lastRotateMs >= Config.fishingRotateIntervalMs) {
                              performSmallRotation()
                              lastRotateMs = System.currentTimeMillis()
                           }

                           if (Config.enableGoldenFishCatch && goldenFishFocus) {
                              var now: Long
                              run label261@{
                                 suppressAntiAfkRotation = true
                                 biteArmed = false
                                 now = System.currentTimeMillis()
                                 if (goldenFishTarget != null) {


                                    if (var25.isValidGoldenStand(var10001)) {
                                       var26 = false
                                       return@label261
                                    }
                                 }

                                 var26 = true
                              }

                              if (var26 || now - gfLastAcquireMs >= 150L) {
                                 goldenFishTarget = findGoldenFishEntityRobust$default(INSTANCE, 0.0, 1, null)
                                 gfLastAcquireMs = now
                              }

                              if (goldenFishTarget != null) {
                                 if (goldenFishAimStartedMs == 0L) {
                                    goldenFishAimStartedMs = now
                                 }

                                 Rotator.follow$default(Rotator.INSTANCE, { 
                                    if (goldenFishTarget != null) preferredHitPoint(goldenFishTarget) else null
                                 }, 0.08F, 720.0F, 0.2F, null, 16, null)
                              } else {
                                 Rotator.clear()
                                 goldenFishAimStartedMs = 0L
                              }

                              if (goldenFishPendingCast && goldenFishTarget != null && now - goldenFishAimStartedMs >= 300L && !isRodOut()) {
                                 castOnGoldenFish()
                              }

                              if (playersBobber != null) {






                                 if (now2 >= gfGraceUntilMs
                                    && isRodOut()
                                    && sinceRetry >= 700L
                                    && (noTargetTooLong || stationary && sinceCast >= 1800L || sinceCast >= 4000L)) {
                                    gfLastRetryMs = now2
                                    reelIn$default(INSTANCE, false, true, false, 4, null)
                                    goldenFishPendingCast = true
                                    goldenFishAimStartedMs = 0L
                                 }
                              }
                           }

                           if (Config.fishingAutoSell && shouldAutoSellByEmptySlots() && System.currentTimeMillis() - lastSellMs > 10000L) {
                              lastSellMs = System.currentTimeMillis()
                              p.networkHandler.sendChatCommand("bz")
                              BazaarAutoSell.queue()
                           }

                           return@lambda_13
                        }

                        return@lambda_13
                     }

                     if (Rotator.isActive) {
                        Rotator.clear()
                     }

                     goldenFishFocus = false
                     goldenFishTarget = null
                     goldenFishPendingCast = false
                     goldenFishAimStartedMs = 0L
                     return@lambda_13
                  }

                  if (FishingMeleeMobs.isBusy) {
                     FishingMeleeMobs.forceAbortAndDisableFishing()
                     masterEnabled = false
                     message("§cAuto Fishing disabled.")
                  } else if (!masterEnabled) {
                     enableFishing()
                  } else {
                     disableFishing()
                  }
               }
            }
         )
         ClientReceiveMessageEvents.GAME
         .register(
            { message: Text, var1: Boolean ->
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

   private fun enableFishing() {
      if (this.getClient().player != null) {
         masterEnabled = true
         Config.fishingEnabled = true
         currentKillTargetName = ""
         suppressNextAutoRecastUntilMs = 0L
         this.resetFishingHudCache()
         startedAtMs = System.currentTimeMillis()
         lastActivityMs = startedAtMs
         lastCastMs = startedAtMs
         lastDeployCheckMs = 0L
         nextTotemDeployAtMs = 0L
         nextPowerOrbDeployAtMs = 0L
         nextUmbrellaDeployAtMs = 0L
         lastTotemPlacePos = null
         lastPowerOrbPlacePos = null
         lastUmbrellaPlacePos = null
         placingDeployable = false
         deployableActionLabel = ""

         mainLookAtBlock = if ((slug as? BlockHitResult) != null) (slug as? BlockHitResult).getBlockPos() else null
         FishingMeleeMobs.onAutoFishingStart()

         if (rod == -1) {
            this.message("§cNo fishing rod found in hotbar!")
            masterEnabled = false
            Config.fishingEnabled = false
         } else {
            this.selectHotbarSlot(rod)
            fishingType = this.detectFishingType()
            ThreadsKt.thread$default(true, false, null, "FunnyFishing-InitialCast", 0, { 
               Thread.sleep(251L)
               if (masterEnabled && !hardPausedForMelee) {
                  castWithVerify$default(INSTANCE, 1000L, 200L, 0, 4, null)
               }
return Unit
            }, 22, null)
            this.message("§aAuto Fishing enabled! Please attend this session.${if (Config.slugFishEnabled) " §7(§eSlug Fishing enabled§7)" else ""}")
            if (this.isInTabArea(galateaAreaRegex)) {
               this.message(
                  "§eTip: Did you know Jooon has a Strider Fisher? That's right! You can set it up by doing /jooonreimagined and heading to \"Galatea!\""
               )
            }
         }
      }
   }

   private fun disableFishing() {
      FishingMeleeMobs.onAutoFishingStop()
      masterEnabled = false
      Config.fishingEnabled = false
      currentKillTargetName = ""
      suppressNextAutoRecastUntilMs = 0L
      castVerifyInProgress = false
      rodSequenceInProgress = false
      placingDeployable = false
      deployableActionLabel = ""
      recastQueued = false
      biteArmed = false
      suppressAntiAfkRotation = false
      hardPausedForMelee = false
      lastDeployCheckMs = 0L
      nextTotemDeployAtMs = 0L
      nextPowerOrbDeployAtMs = 0L
      nextUmbrellaDeployAtMs = 0L
      lastTotemPlacePos = null
      lastPowerOrbPlacePos = null
      lastUmbrellaPlacePos = null
      goldenFishFocus = false
      goldenFishTarget = null
      goldenFishPendingCast = false
      goldenFishAimStartedMs = 0L
      Rotator.clear()
      playersBobber = null
      this.resetFishingHudCache()
      this.restoreViewIfSaved()
      this.stopAntiAfkNudges()
      this.message("§cAuto Fishing disabled.")
   }

   private fun disableFishingForWrongRod() {
      FishingMeleeMobs.onAutoFishingStop()
      masterEnabled = false
      Config.fishingEnabled = false
      currentKillTargetName = ""
      castVerifyInProgress = false
      rodSequenceInProgress = false
      placingDeployable = false
      deployableActionLabel = ""
      recastQueued = false
      biteArmed = false
      suppressAntiAfkRotation = false
      hardPausedForMelee = false
      lastDeployCheckMs = 0L
      nextTotemDeployAtMs = 0L
      nextPowerOrbDeployAtMs = 0L
      nextUmbrellaDeployAtMs = 0L
      lastTotemPlacePos = null
      lastPowerOrbPlacePos = null
      lastUmbrellaPlacePos = null
      goldenFishFocus = false
      goldenFishTarget = null
      goldenFishPendingCast = false
      goldenFishAimStartedMs = 0L
      Rotator.clear()
      playersBobber = null
      this.resetFishingHudCache()
      this.restoreViewIfSaved()
      this.stopAntiAfkNudges()
   }

   private fun stopAntiAfkNudges() {
   }

   private fun detectFishingType(): jooon.features.fishing.FunnyFishing.FishingType {

      if (hr is BlockHitResult) {



         return if (var5.getFluidState().isIn(FluidTags.LAVA)) FunnyFishing.FishingType.LAVA else FunnyFishing.FishingType.WATER
      } else {
         return FunnyFishing.FishingType.WATER
      }
   }

   fun refreshBobberFromPlayer(): FishingBobberEntity {

      if (var10000 == null) {
return null
      } else {

         if (this.ownsBobber(var10000.fishHook, var10000)) hook else null
      }
   }

   fun detectFluidAtBobber(bobber: FishingBobberEntity): FunnyFishing.FishingType {

      if (var10000 == null) {
return fishingType
      } else {
         var level: ClientWorld
         run label49@{
            level = var10000
            if (bobber != null) {
               var6 = bobber.getBlockPos()
               if (var6 != null) {
                  return@label49
               }
            }

            var6 = if (var7 != null) var7.getBlockPos() else null
            if (var6 == null) {
return fishingType
            }
         }


         for (i in -1..4) {

            if (var8.isIn(FluidTags.LAVA)) {
               FunnyFishing.FishingType.LAVA
            }

            if (var8.isIn(FluidTags.WATER)) {
               FunnyFishing.FishingType.WATER
            }
         }
return fishingType
      }
   }

   private fun randomMsInRange(minMs: Int, maxMs: Int): Long {
      return random(IntRange((minMs).coerceAtMost(maxMs), (maxMs).coerceAtLeast(minMs)), Random.Default as Random)
   }

   fun randomPreCatchDelayMs(bobber: FishingBobberEntity): Long {
      var var10000: Long
      when (FunnyFishing.WhenMappings.$EnumSwitchMapping$1[this.detectFluidAtBobber(bobber).ordinal()]) {
         1 -> var10000 = this.randomMsInRange(Config.fishingLavaPreCatchMinMs, Config.fishingLavaPreCatchMaxMs)
         2 -> var10000 = this.randomMsInRange(Config.fishingWaterPreCatchMinMs, Config.fishingWaterPreCatchMaxMs)
         else -> throw NoWhenBranchMatchedException()
      }
return var10000
   }

   fun randomCastDelayMs(bobber: FishingBobberEntity): Long {



      if (this.detectFluidAtBobber(bobber) === FunnyFishing.FishingType.LAVA) delay + random(LongRange(0L, 40L), Random.Default as Random) else delay
   }

   private fun queueCatch(recast: Boolean) {
      if (masterEnabled && !hardPausedForMelee) {
         if (!recastQueued) {
            if (!goldenFishFocus || !recast) {
               if (!recast || this.canCatchNow()) {
                  recastQueued = true
                  var var10000: FishingBobberEntity = playersBobber
                  if (playersBobber == null) {
                     var10000 = this.refreshBobberFromPlayer()
                  }

                  ThreadsKt.thread$default(false, false, null, "FunnyFishing-QueueCatch", 0, lambda_20@{ 
                     try {
                        Thread.sleep(`$preCatchMs`)
                        if (!masterEnabled || hardPausedForMelee) {
                           return@lambda_20 Unit
                        }

                        reelIn$default(INSTANCE, `$recast`, false, true, 2, null)
                     } finally {
                        Thread.sleep(1350L)
                        recastQueued = false
                     }

                     return@lambda_20 Unit
                  }, 23, null)
               }
            }
         }
      }
   }

   fun isInOrAboveLavaRobust(e: ArmorStandEntity): Boolean {

      if (var10000 == null) {
return false
      } else {









         // $VF: Unable to resugar Kotlin loop from Java for loop
         var var23: Double = minX
         while (true) {
            if (var23 <= maxX + 1.0E-6) break


            // $VF: Unable to resugar Kotlin loop from Java for loop
            var var24: Double = minZ
            while (true) {
               if (var24 <= maxZ + 1.0E-6) break
               var y: Double = 0.0
               y = bb.minY + 0.5


               repeat(var12) { var13 ->
                  y -= 0.125

                  if (w.getFluidState(var27).isIn(FluidTags.LAVA)) {
return true
                  }
               }

               var24 += 0.25
            }

            var23 += 0.25
         }
return false
      }
   }

   fun isInWater(player: PlayerEntity): Boolean {
      player.isTouchingWater() || player.isSubmergedInWater()
   }

   fun isValidGoldenStand(e: ArmorStandEntity): Boolean {
      e.isAlive() && !e.isRemoved() && e.isInvisible() && this.isInOrAboveLavaRobust(e)
   }

   fun findGoldenFishEntityRobust(maxDistanceSq: Double): ArmorStandEntity {

      if (var10000 == null) {
return null
      } else {

         if (var24 == null) {
return null
         } else {


            var best: ArmorStandEntity = null
            var bestScore: Double = java.lang.Double.NEGATIVE_INFINITY
            val var25: java.lang.Iterable = var10000.getEntities()

            for (var26 in toList(var25)) {

               if (var26 as Entity is ArmorStandEntity && this.isValidGoldenStand((var26 as Entity) as ArmorStandEntity)) {

                  if (!(d2 > maxDistanceSq)) {

                     if (-d2 + (if (favorNew) 1000.0 / (1.0 + e.age) else 0.0) > bestScore) {
                        bestScore = score
                        best = e as ArmorStandEntity
                     }
                  }
               }
            }

            if (best != null) {
return best
            } else {
               bestScore = java.lang.Double.NEGATIVE_INFINITY
               val var27: java.lang.Iterable = var10000.getEntities()

               for (var28 in toList(var27)) {

                  if (var28 as Entity is ArmorStandEntity
                     && ((var28 as Entity) as ArmorStandEntity).isAlive()
                     && !((var28 as Entity) as ArmorStandEntity).isRemoved()
                     && this.isInOrAboveLavaRobust((var28 as Entity) as ArmorStandEntity)) {

                     if (!(var22 > maxDistanceSq)) {

                        if (-var22 > bestScore) {
                           bestScore = var23
                           best = var21 as ArmorStandEntity
                        }
                     }
                  }
               }
return best
            }
         }
      }
   }

   fun preferredHitPoint(e: ArmorStandEntity): Vec3d {


      var var9: Double
      try {
         var9 = e.getEyeY()
      } catch (var12: java.lang.Throwable) {
         var9 = (var10000.maxY + var10000.minY) * 0.5
      }

      Vec3d((var10000.minX + var10000.maxX) * 0.5, var9 + 1.0, (var10000.minZ + var10000.maxZ) * 0.5)
   }

   private fun updatePlayersBobber() {
      if (this.getClient().player != null) {
         playersBobber = this.refreshBobberFromPlayer()

         if (playersBobber != null) {
            fishingType = this.detectFluidAtBobber(playersBobber)
         }

         if (!hadBobber && has) {
            lastCastMs = System.currentTimeMillis()
            lastActivityMs = lastCastMs
            exclaimSeenThisCast = false
            this.armBiteAfterDelay()
         }

         hadBobber = has
      }
   }

   private fun performSmallRotation() {

      if (var10000 != null) {
         var10000.setYaw(var10000.getYaw() + ((Math.random() - 0.5) * 1.2F.toDouble()).toFloat())
      }
   }

   private fun canUseSeaCreatureFallbackNow(now: Long = System.currentTimeMillis()): Boolean {
      return lastReelMs > 0L && lastReelMs >= lastCastMs && now - lastReelMs <= 3500L
   }

   private fun shouldIgnoreSeaCreature(messageText: String = "", wantedName: String = ""): Boolean {
      return Config.fishingIgnoreSquids && (wantedName.contains("squid", true) || messageText.contains("squid", true))
   }

   private fun isInTabArea(areaRegex: Regex): Boolean {

      if (var10000 == null) {
         return false
      } else {
         val var14: java.util.Collection = var10000.getPlayerList()
         val `this$iv`: java.lang.Iterable = var14
         var var15: Boolean
         if ((var14 as java.util.Collection).isEmpty()) {
            var15 = false
         } else {
            val var5: java.util.Iterator = `this$iv`.iterator()

            while (true) {
               if (!var5.hasNext()) {
                  var15 = false
break
               }

               run label39@{


                  if (var16 != null) {
                     var17 = var16.getString()
                     if (var17 != null) {
                        return@label39
                     }
                  }

                  var17 = info.getProfile().name()
               }

               if (areaRegex.containsMatchIn(trim(replace$default(Regex("§.").replace(var17, ""), "Â", "", false, 4, null)).toString())) {
                  var15 = true
break
               }
            }
         }

         return var15
      }
   }

   private fun currentHudFishingType(preview: Boolean): String {
      if (preview) {
         return "Lava"
      } else {
         var var10000: FunnyFishing.FishingType = if (playersBobber != null) detectFluidAtBobber(playersBobber) else null
         var10000 = var10000
         if (var10000 == null) {
            var10000 = fishingType
         }

         var var6: String
         when (FunnyFishing.WhenMappings.$EnumSwitchMapping$1[var10000.ordinal()]) {
            1 -> var6 = "Lava"
            2 -> var6 = "Water"
            else -> throw NoWhenBranchMatchedException()
         }

         return var6
      }
   }

   private fun shouldHandoffToAutoKillNow(now: Long = System.currentTimeMillis()): Boolean {
      return !killing && !hardPausedForMelee && (!isBlank(currentKillTargetName) || now < suppressNextAutoRecastUntilMs)
   }

   private fun reelIn(recast: Boolean, allowDuringGoldenFish: Boolean = false, skipPreCatch: Boolean = false) {
      if (masterEnabled && !hardPausedForMelee) {
         if (!goldenFishFocus || allowDuringGoldenFish) {
            if (!rodSequenceInProgress) {
               if (this.getClient().player != null) {
                  lastReelMs = System.currentTimeMillis()
                  lastActivityMs = lastReelMs
                  rodSequenceInProgress = true
                  ThreadsKt.thread$default(false, false, null, null, 0, lambda_26@{ 
                     try {
                        var var10000: FishingBobberEntity = playersBobber
                        if (playersBobber == null) {
                           var10000 = refreshBobberFromPlayer()
                        }

                        if (!`$skipPreCatch`) {
                           Thread.sleep(randomPreCatchDelayMs(var10000))
                        }

                        if (!masterEnabled || hardPausedForMelee) {
                           return@lambda_26 Unit
                        }

                        PlayerController.rightClick()
                        lastActivityMs = System.currentTimeMillis()
                        if (!`$recast`) {
                           return@lambda_26 Unit
                        }

                        biteArmed = false


                        while (masterEnabled && !hardPausedForMelee) {

                           if (nearbyCreature >= waitUntil) {
break
                           }

                           if (shouldHandoffToAutoKillNow(nearbyCreature)) {
                              val var10: java.lang.CharSequence = currentKillTargetName
                              var var18: java.lang.CharSequence
                              if (isBlank(currentKillTargetName)) {
                                 var18 = detectNearbySeaCreatureName(35.0)
                                 if (var18 == null) {
                                    var18 = ""
                                 }
                              } else {
                                 var18 = var10
                              }

                              return@lambda_26 Unit
                           }

                           Thread.sleep(Math.min(25L, waitUntil - nearbyCreature))
                        }

                        if (!masterEnabled || hardPausedForMelee) {
                           return@lambda_26 Unit
                        }

                        if (killing || System.currentTimeMillis() < suppressNextAutoRecastUntilMs) {
                           return@lambda_26 Unit
                        }

                        if (Config.fishingKillingMode != Config.Companion.FishingKillMode.OFF && canUseSeaCreatureFallbackNow$default(INSTANCE, 0L, 1, null)) {

                           if (var15 != null) {
                              currentKillTargetName = var15
                              suppressNextAutoRecastUntilMs = System.currentTimeMillis() + 1500L
                              return@lambda_26 Unit
                           }
                        }

                        PlayerController.rightClick()
                        lastCastMs = System.currentTimeMillis()
                        lastActivityMs = lastCastMs
                        exclaimSeenThisCast = false
                        armBiteAfterDelay()
                        awaitCastConfirmation(700L)
                        Thread.sleep(450L + random(LongRange(0L, 80L), Random.Default as Random))
                     } finally {
                        rodSequenceInProgress = false
                     }

                     return@lambda_26 Unit
                  }, 31, null)
               }
            }
         }
      }
   }

   private fun armBiteAfterDelay() {
      ThreadsKt.thread$default(false, false, null, null, 0, { 
         Thread.sleep(350L)
         if (masterEnabled && !hardPausedForMelee) {
            biteArmed = true
         }
return Unit
      }, 31, null)
   }

   fun detectExclaimAbove(bobber: FishingBobberEntity): Boolean {

      if (var10000 == null) {
return false
      } else {





         var stand: FunnyFishing
         try {
            stand = var5
            stand = Result(
               world.getEntitiesByType(EntityType.ARMOR_STAND as TypeFilter, searchBox, { p0: Any ->
                  ``(p0)
               })
            )
         } catch (var17: java.lang.Throwable) {
            stand = Result(ResultKt.createFailure(var17))
         }

         val var36: Any
         if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(stand) == null) {
            var36 = stand
         } else {
            val var37: java.lang.Iterable = var10000.getEntities()
            var `destination$iv$iv`: java.util.Collection = ArrayList()

            for (`element$iv$iv` in var37) {
               if (`element$iv$iv` is ArmorStandEntity) {
                  `destination$iv$iv`.add(`element$iv$iv`)
               }
            }

            val var28: java.lang.Iterable = `destination$iv$iv` as java.util.List
            `destination$iv$iv` = ArrayList()

            for (var34 in var28) {
               if ((var34 as ArmorStandEntity).isAlive()
                  && !(var34 as ArmorStandEntity).isRemoved()
                  && searchBox.intersects((var34 as ArmorStandEntity).getBoundingBox())) {
                  `destination$iv$iv`.add(var34)
               }
            }

            var36 = `destination$iv$iv` as java.util.List
         }

         for (var21 in var36 as java.util.List) {
            var var38: Text = var21.getCustomName()
            if (var38 == null) {
               var38 = var21.getName()
            }

            if (contains$default(
               trim(replace$default(Regex("§.").replace(var39, ""), "Â", "", false, 4, null)).toString(), "!!!", false, 2, null
            )) {
return true
            }
         }
return false
      }
   }

   private fun doFireVeilKill() {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {

         if (wandSlot == -1) {
            this.message("§cFire Veil Wand not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                  swapUseBackClicks$default(INSTANCE, `$wandSlot`, false, 1, 160L, null, false, 48, null)
               } finally {
                  killing = false
               }
return Unit
            }, 31, null)
         }
      }
   }

   private fun doWitherBladeKill(spawnMessage: String) {
      var var10001: String = FishingStrings.seaCreatureMessages.get(spawnMessage)
      if (var10001 == null) {
         var10001 = ""
      }

      this.doWitherBladeKillByName(var10001)
   }

   private fun doWitherBladeKillByName(wanted: String) {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {
         val var4: java.util.Iterator = listOf(arrayOf("Scylla", "Astraea", "Hyperion", "Valkyrie")).iterator()

         var var10000: Int
         while (true) {
            if (var4.hasNext()) {


               if (var11 == null) {
return continue
               }

               var10000 = var11
break
            }

            var10000 = null
break
         }

         if (var10 == -1) {
            this.message("§cWither Blade not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                  swapUseUntilArmorStandGone$default(INSTANCE, `$slot`, `$wanted`, true, 160L, 89.1F, true, 0, 64, null)
                  currentKillTargetName = ""
               } finally {
                  killing = false
               }
return Unit
            }, 31, null)
         }
      }
   }

   private fun doMidasKill(spawnMessage: String) {
      var var10001: String = FishingStrings.seaCreatureMessages.get(spawnMessage)
      if (var10001 == null) {
         var10001 = ""
      }

      this.doMidasKillByName(var10001)
   }

   private fun doMidasKillByName(wanted: String) {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {

         if (slot == -1) {
            this.message("§cMidas Staff not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                  swapUseUntilArmorStandGone$default(INSTANCE, `$slot`, `$wanted`, false, 160L, null, false, 0, 112, null)
                  currentKillTargetName = ""
               } finally {
                  killing = false
               }
return Unit
            }, 31, null)
         }
      }
   }

   private fun doSpiritSceptreKill(spawnMessage: String) {
      var var10001: String = FishingStrings.seaCreatureMessages.get(spawnMessage)
      if (var10001 == null) {
         var10001 = ""
      }

      this.doSpiritSceptreKillByName(var10001)
   }

   private fun doSpiritSceptreKillByName(wanted: String) {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {



         if (slot == -1) {
            this.message("§cSpirit Sceptre not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                  swapUseUntilArmorStandGone$default(INSTANCE, `$slot`, `$wanted`, true, 160L, 89.1F, true, 0, 64, null)
                  currentKillTargetName = ""
               } finally {
                  killing = false
               }
return Unit
            }, 31, null)
         }
      }
   }

   private fun startAutoKillForCreatureName(wantedName: String): Boolean {

      if (isBlank(wanted)) {
         return false
      } else if (shouldIgnoreSeaCreature$default(this, null, wanted, 1, null)) {
         return false
      } else {
         currentKillTargetName = wanted
         when (FunnyFishing.WhenMappings.$EnumSwitchMapping$0[Config.fishingKillingMode.ordinal()]) {
            1 -> return false
            2 -> this.doFireVeilKill()
            3 -> this.doWitherBladeKillByName(wanted)
            4 -> this.doSpiritSceptreKillByName(wanted)
            5 -> this.doMidasKillByName(wanted)
            else -> throw NoWhenBranchMatchedException()
         }

         return true
      }
   }

   private fun detectNearbySeaCreatureName(maxRange: Double): String? {

      if (var10000 == null) {
         return null
      } else {

         if (var13 == null) {
            return null
         } else {


            val var14: java.lang.Iterable = var10000.getEntities()

            for (var15 in toList(var14)) {

               if (var15 as Entity is ArmorStandEntity
                  && ((var15 as Entity) as ArmorStandEntity).isAlive()
                  && !((var15 as Entity) as ArmorStandEntity).isRemoved()
                  && !((var15 as Entity).squaredDistanceTo(me as Entity) > maxSq)) {
                  var var10001: Text = (entity as ArmorStandEntity).getCustomName()
                  if (var10001 == null) {
                     var10001 = (entity as ArmorStandEntity).getName()
                  }


                  if (!isBlank(label)) {
                     for (candidate in this.seaCreatureKeysByLengthDesc) {
                        if (!isBlank(candidate) && contains$default(label, candidate, false, 2, null)) {

                           if (wanted != null && !shouldIgnoreSeaCreature$default(this, null, wanted, 1, null)) {
                              return wanted
                           }
                        }
                     }
                  }
               }
            }

            return null
         }
      }
   }

   private fun swapUseUntilArmorStandGone(
      weaponSlot: Int,
      wantedMobName: String,
      lookDown: Boolean,
      intervalMs: Long,
      lookDownPitch: Float? = null,
      lockMove: Boolean = false,
      maxUses: Int = 12
   ) {
      if (masterEnabled && !hardPausedForMelee) {
         if (!rodSequenceInProgress) {

            if (var10000 != null) {


               if (rod != -1 && 0 <= weaponSlot && weaponSlot < 9) {
                  rodSequenceInProgress = true
                  currentKillTargetName = (if (isBlank(wantedMobName)) "Sea Creature" else wantedMobName) as String
                  suppressSlotCheckUntilMs = System.currentTimeMillis() + 2500L
                  lastActivityMs = System.currentTimeMillis()


                  if (lockMove) {
                     PlayerController.pressForward(false)
                     PlayerController.pressBack(false)
                     PlayerController.pressLeft(false)
                     PlayerController.pressRight(false)
                     PlayerController.pressSprint(false)
                     PlayerController.pressJump(false)
                  }

                  try {
                     this.selectHotbarSlot(weaponSlot)
                     Thread.sleep(155L)
                     if (lookDown || lookDownPitch != null) {
                        this.getClient().execute({ 
                           `$p`.setYaw(`$prevYaw`)
                           `$p`.setPitch((`$lookDownPitch` ?: 89.0F).coerceIn(-89.9F, 89.9F))
                        })
                        Thread.sleep(60L)
                     }

                     var uses: Int = 0
                     var emptyChecks: Int = 0

                     while (uses < maxUses && masterEnabled && !hardPausedForMelee) {
                        if (uses > 0 && this.countSeaCreatureArmorStands(wantedMobName) <= 0) {
                           if (++emptyChecks >= 2) {
break
                           }
                        } else {
                           emptyChecks = 0
                        }

                        PlayerController.rightClick()
                        uses++
                        Thread.sleep(intervalMs)
                        lastActivityMs = System.currentTimeMillis()
                     }

                     this.selectHotbarSlot(rod)
                     Thread.sleep(251L)
                     this.getClient().execute({ 
                        `$p`.setPitch(`$prevPitch`)
                        `$p`.setYaw(`$prevYaw`)
                     })
                     this.castSingleFromRod()
                     lastActivityMs = System.currentTimeMillis()
                  } finally {
                     rodSequenceInProgress = false
                  }
               }
            }
         }
      }
   }

   private fun countSeaCreatureArmorStands(wantedMobName: String): Int {
      if (isBlank(wantedMobName)) {
         return 0
      } else {

         if (var10000 == null) {
            return 0
         } else {

            if (var13 == null) {
               return 0
            } else {


               val var14: java.lang.Iterable = var10000.getEntities()
               val `this$iv`: java.lang.Iterable = toList(var14)
               val var17: Int
               if (`this$iv` is java.util.Collection && (`this$iv` as java.util.Collection).isEmpty()) {
                  var17 = 0
               } else {
                  val `count$iv`: Int = 0

                  for (`element$iv` in `this$iv`) {

                     val var15: Boolean
                     if (`element$iv` as Entity is ArmorStandEntity
                        && ((`element$iv` as Entity) as ArmorStandEntity).isAlive()
                        && !((`element$iv` as Entity) as ArmorStandEntity).isRemoved()) {
                        if (entity.squaredDistanceTo(me as Entity) > 900.0) {
                           var15 = false
                        } else {

                           var var10001: Text = (entity as ArmorStandEntity).getCustomName()
                           if (var10001 == null) {
                              var10001 = (entity as ArmorStandEntity).getName()
                           }

                           var15 = contains$default(var16.normalizeStandName(var18), wanted, false, 2, null)
                        }
                     } else {
                        var15 = false
                     }

                     if (var15) {
                        if (++`count$iv` < 0) {
                           throwCountOverflow()
                        }
                     }
                  }

                  var17 = `count$iv`
               }

               return var17
            }
         }
      }
   }

   private fun normalizeStandName(raw: String): String {

            Regex("\\s+")
               .replace(
                  replace$default(
                     replace$default(
                        replace$default(replace$default(Regex("§.").replace(raw, ""), "❤", "", false, 4, null), "Â", "", false, 4, null),
                        "/",
                        " ",
                        false,
                        4,
return null
                     ),
                     ",",
                     "",
                     false,
                     4,
return null
                  ),
                  " "
               )
         )
         .toString()


      return var9
   }

   private fun getFishingRodSlot(): Int {



      var i: Int = 0

      while (true) {
         if (i >= 9) {
            return -1
         }

         if (!var5.isEmpty()) {
            if (var5.getItem() == Items.FISHING_ROD) {
break
            }

            if (var6.contains("Rod", true)) {
break
            }
         }

         i++
      }

      return i
   }

   private fun findInHotbarByName(name: String): Int {




      repeat(8) { i ->

         if (!var6.isEmpty()) {

            if (var7.contains(name, true)) {
               return i
            }
         }
      }

      return -1
   }

   private fun selectHotbarSlot(slot: Int) {

      if (var10000 != null) {
         if (0 <= slot && slot < 9) {
            if (var10000.getInventory().getSelectedSlot() != slot) {
               var10000.getInventory().setSelectedSlot(slot)
               PlayerController.noteHotbarSwapThisTick()
            }
         }
      }
   }

   private fun shouldAutoSellByEmptySlots(): Boolean {
      return this.countEmptyMainInvSlots() <= 2
   }

   private fun countEmptyMainInvSlots(): Int {

      if (var10000 == null) {
         return 99
      } else {


         var empty: Int = 0

         for (i in 9..35) {
            if (inv.getStack(i).isEmpty()) {
               empty++
            }
         }

         return empty
      }
   }

   private fun canPlaceDeployableNow(): Boolean {
      return masterEnabled
         && !hardPausedForMelee
         && !placingDeployable
         && !killing
         && !rodSequenceInProgress
         && !castVerifyInProgress
         && !recastQueued
         && !goldenFishFocus
         && !this.isRodOut()
      }

   private fun processAutoDeployables(): Boolean {
      if (!this.canPlaceDeployableNow()) {
         return false
      } else {

         if (now - lastDeployCheckMs < 450L) {
            return false
         } else {
            lastDeployCheckMs = now

            if (lastTotemPlacePos != null) {
               excluded.add(lastTotemPlacePos)
            }

            if (lastPowerOrbPlacePos != null) {
               excluded.add(lastPowerOrbPlacePos)
            }

            if (lastUmbrellaPlacePos != null) {
               excluded.add(lastUmbrellaPlacePos)
            }

            if (Config.fishingTotem) {
               if (now >= nextTotemDeployAtMs && this.tryPlaceCorruptionTotem(excluded)) {
                  return true
               }

               if (lastTotemPlacePos != null) {
                  excluded.add(lastTotemPlacePos)
               }
            }

            if (Config.fishingAutoPowerOrb) {
               if (now >= nextPowerOrbDeployAtMs && this.tryPlacePowerOrb(excluded)) {
                  return true
               }

               if (lastPowerOrbPlacePos != null) {
                  excluded.add(lastPowerOrbPlacePos)
               }
            }

            return Config.fishingAutoUmbrella && now >= nextUmbrellaDeployAtMs && this.tryPlaceUmbrella(excluded)
         }
      }
   }

   private fun tryPlaceCorruptionTotem(excluded: Set<BlockPos>): Boolean {

      if (slot == -1) {
         nextTotemDeployAtMs = System.currentTimeMillis() + 30000L
         return false
      } else {

         if (var10000 == null) {
            val `this24lambda_u2448`: FunnyFishing = this
            nextTotemDeployAtMs = System.currentTimeMillis() + 5000L
            return false
         } else {
            return this.placeDeployableAt(FunnyFishing.DeployableChoice(slot, "Totem of Corruption", 120000L), var10000, "Placing Totem", { placed: BlockPos ->
               lastTotemPlacePos = placed
               nextTotemDeployAtMs = System.currentTimeMillis() + 120000L
return Unit
            })
         }
      }
   }

   private fun tryPlacePowerOrb(excluded: Set<BlockPos>): Boolean {
      val var10000: FunnyFishing.DeployableChoice = this.findBestPowerOrbChoice()
      if (var10000 == null) {
         val `this24lambda_u2450`: FunnyFishing = this
         nextPowerOrbDeployAtMs = System.currentTimeMillis() + 30000L
         return false
      } else {

         if (var8 == null) {
            val `this24lambda_u2451`: FunnyFishing = this
            nextPowerOrbDeployAtMs = System.currentTimeMillis() + 5000L
            return false
         } else {
            return this.placeDeployableAt(var10000, var8, "Placing ${var10000.displayName}", { placed: BlockPos ->
               lastPowerOrbPlacePos = placed
               nextPowerOrbDeployAtMs = System.currentTimeMillis() + `$choice`.durationMs
return Unit
            })
         }
      }
   }

   private fun tryPlaceUmbrella(excluded: Set<BlockPos>): Boolean {
      val var10000: FunnyFishing.DeployableChoice = this.findUmbrellaChoice()
      if (var10000 == null) {
         val `this24lambda_u2453`: FunnyFishing = this
         nextUmbrellaDeployAtMs = System.currentTimeMillis() + 30000L
         return false
      } else {

         if (var8 == null) {
            val `this24lambda_u2454`: FunnyFishing = this
            nextUmbrellaDeployAtMs = System.currentTimeMillis() + 5000L
            return false
         } else {
            return this.placeDeployableAt(var10000, var8, "Placing ${var10000.displayName}", { placed: BlockPos ->
               lastUmbrellaPlacePos = placed
               nextUmbrellaDeployAtMs = System.currentTimeMillis() + 300000L
return Unit
            })
         }
      }
   }

   private fun findBestPowerOrbChoice(): jooon.features.fishing.FunnyFishing.DeployableChoice? {

      if (var10000 != null) {

         if (var15 != null) {

            val priorities: java.util.List = listOf(
               arrayOf(
                  Triple("SOS Flare", 60000L, 7),
                  Triple("Plasmaflux Power Orb", 60000L, 6),
                  Triple("Overflux Power Orb", 60000L, 5),
                  Triple("Alert Flare", 60000L, 4),
                  Triple("Mana Flux Power Orb", 30000L, 3),
                  Triple("Warning Flare", 30000L, 2),
                  Triple("Radiant Power Orb", 30000L, 1)
               )
            )
            var var14: FunnyFishing.DeployableChoice = null
            var bestPriority: Int = Integer.MIN_VALUE

            repeat(8) { slot ->

               if (!var16.isEmpty()) {



                  for (var9 in priorities) {



                     if (itemName.contains(name, true) && priority > bestPriority) {
                        bestPriority = priority
                        var14 = FunnyFishing.DeployableChoice(slot, name, duration)
                     }
                  }
               }
            }

            return var14
         }
      }

      return null
   }

   private fun findUmbrellaChoice(): jooon.features.fishing.FunnyFishing.DeployableChoice? {

      if (var10000 != null) {

         if (var5 != null) {


            repeat(8) { slot ->

               if (!var6.isEmpty()) {

                  if (var7.contains("Umbrella", true)) {
                     return FunnyFishing.DeployableChoice(slot, "Umbrella", 300000L)
                  }
               }
            }

            return null
         }
      }

      return null
   }

   fun findDeploySpot(excluded: MutableSet<BlockPos>): BlockPos {

      if (var10000 == null) {
return null
      } else {

         if (var11 == null) {
return null
         } else {




            for (var7 in this.collectPrioritizedDeployOffsets(var10000)) {

                  (var7.component1() as java.lang.Number).intValue(), -1, (var7.component2() as java.lang.Number).intValue()
               )
               if (!excluded.contains(var13) && this.isValidDeployBase(world, var13)) {
return var13
               }
            }
return null
         }
      }
   }

   fun collectPrioritizedDeployOffsets(player: ClientPlayerEntity): MutableList<Pair<Integer, Integer>> {




      val result: java.util.List = ArrayList()
      val seen: java.util.Set = LinkedHashSet()
      collectPrioritizedDeployOffsets$addDir(seen, result, var10, 1)
      collectPrioritizedDeployOffsets$addDir(seen, result, var11, 1)
      collectPrioritizedDeployOffsets$addDir(seen, result, var10, 2)
      collectPrioritizedDeployOffsets$addDir(seen, result, var11, 2)
      collectPrioritizedDeployOffsets$add(seen, result, var10.getOffsetX() + var10000.getOffsetX(), var10.getOffsetZ() + var10000.getOffsetZ())
      collectPrioritizedDeployOffsets$add(seen, result, var11.getOffsetX() + var10000.getOffsetX(), var11.getOffsetZ() + var10000.getOffsetZ())
      collectPrioritizedDeployOffsets$add(seen, result, var10.getOffsetX() + var12.getOffsetX(), var10.getOffsetZ() + var12.getOffsetZ())
      collectPrioritizedDeployOffsets$add(seen, result, var11.getOffsetX() + var12.getOffsetX(), var11.getOffsetZ() + var12.getOffsetZ())
      collectPrioritizedDeployOffsets$addDir(seen, result, var10000, 1)
      collectPrioritizedDeployOffsets$addDir(seen, result, var12, 1)
      collectPrioritizedDeployOffsets$addDir(seen, result, var10000, 2)
      collectPrioritizedDeployOffsets$addDir(seen, result, var12, 2)

      for (dx in -2..2) {
         for (dz in -2..2) {
            if (dx != 0 || dz != 0) {
               collectPrioritizedDeployOffsets$add(seen, result, dx, dz)
            }
         }
      }
return result
   }

   fun isValidDeployBase(world: ClientWorld, posUnder: BlockPos): Boolean {
      var var10000: BlockState = world.getBlockState(posUnder)
      if (var10000.isAir()) {
return false
      } else if (!var10000.getFluidState().isEmpty()) {
return false
      } else {
         var10000 = world.getBlockState(posUnder.up())
         if (!var10000.isAir()) {
return false
         } else if (!var10000.getFluidState().isEmpty()) {
return false
         } else {
            val var15: Array<Any> = arrayOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            var var8: Int = 0


            while (true) {
               if (var8 >= var9) {
                  var19 = false
break
               }

               var10000 = world.getBlockState(var17)
               if (var10000.isAir() && world.getFluidState(var17).isEmpty()) {
                  var19 = true
break
               }

               var8++
            }
return var19
         }
      }
   }

   fun placeDeployableAt(choice: FunnyFishing.DeployableChoice, target: BlockPos, actionLabel: String, onPlaced: (BlockPos?) -> Unit): Boolean {
      if (!masterEnabled || hardPausedForMelee) {
return false
      } else if (placingDeployable) {
return false
      } else {
         placingDeployable = true
         deployableActionLabel = actionLabel
         ThreadsKt.thread$default(
            true,
            false,
            null,
            "FunnyFishing-Deploy-${choice.displayName}",
            0,
            { 
               try {
                  suppressSlotCheckUntilMs = System.currentTimeMillis() + 2000L
                  suppressAntiAfkRotation = true
                  lastActivityMs = System.currentTimeMillis()

                  var var10000: Rotator = Rotator.INSTANCE
                  var var10001: Vec3d = Vec3d.ofCenter(`$target` as Vec3i)
                  Rotator.lookAt$default(var10000, var10001, 0.1F, 540.0F, 0.0F, null, 24, null)

                  while (Rotator.isActive) {
                     Thread.sleep(10L)
                  }

                  selectHotbarSlot(`$choice`.slot)
                  Thread.sleep(200L)
                  getClient()
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
                     Thread.sleep(300L)
                  `$onPlaced`(`$target`)

                  if (rod != -1 && masterEnabled && !hardPausedForMelee) {
                     selectHotbarSlot(rod)
                     Thread.sleep(300L)
                     if (backPos != null) {
                        var10000 = Rotator.INSTANCE
                        var10001 = Vec3d.ofCenter(backPos as Vec3i)
                        Rotator.lookAt$default(var10000, var10001, 0.1F, 540.0F, 0.0F, null, 24, null)
                     }

                     while (Rotator.isActive) {
                        Thread.sleep(10L)
                     }
                  }
               } finally {
                  suppressAntiAfkRotation = false
                  placingDeployable = false
                  deployableActionLabel = ""
               }
return Unit
            },
            22,
return null
         )
return true
      }
   }

   private fun castWithVerify(maxWaitMs: Long = 700L, retryDelayMs: Long = 150L, maxAttempts: Int = 1) {
      if (masterEnabled && !hardPausedForMelee) {
         if (this.getClient().player != null) {
            if (!castVerifyInProgress) {
               if (!rodSequenceInProgress) {
                  castVerifyInProgress = true
                  rodSequenceInProgress = true
                  ThreadsKt.thread$default(true, false, null, "FunnyFishing-CastVerify", 0, { 
                     try {
                        runCastWithVerify(`$maxWaitMs`, `$retryDelayMs`, `$maxAttempts`)
                     } finally {
                        castVerifyInProgress = false
                        rodSequenceInProgress = false
                     }
return Unit
                  }, 22, null)
               }
            }
         }
      }
   }

   private fun runCastWithVerify(maxWaitMs: Long, retryDelayMs: Long, maxAttempts: Int) {
      var attempt: Int = 0

      while (attempt < maxAttempts && masterEnabled && !hardPausedForMelee) {
         attempt++
         if (masterEnabled && !hardPausedForMelee) {
            PlayerController.rightClick()
            lastCastMs = System.currentTimeMillis()
            lastActivityMs = lastCastMs
            exclaimSeenThisCast = false
            if (this.awaitCastConfirmation(maxWaitMs)) {
               this.armBiteAfterDelay()
return return
            }

            Thread.sleep(retryDelayMs)
            if (masterEnabled && !hardPausedForMelee) {
return continue
            }
return return
         }
return return
      }
   }

   private fun castOnGoldenFish() {
      if (masterEnabled && !hardPausedForMelee) {
         if (this.getClient().player != null) {
            if (this.isRodOut()) {
               goldenFishPendingCast = false
            } else {
               goldenFishAimStartedMs = if (goldenFishAimStartedMs == 0L) System.currentTimeMillis() else goldenFishAimStartedMs
               PlayerController.rightClick()
               lastCastMs = System.currentTimeMillis()
               lastActivityMs = lastCastMs
               goldenFishPendingCast = false
               exclaimSeenThisCast = false
               ThreadsKt.thread$default(false, false, null, null, 0, lambda_61@{ 
                  if (!awaitCastConfirmation(700L)) {
                     Thread.sleep(200L)
                     if (!masterEnabled || hardPausedForMelee) {
                        return@lambda_61 Unit
                     }

                     PlayerController.rightClick()
                     awaitCastConfirmation(700L)
                  }

                  armBiteAfterDelay()
                  return@lambda_61 Unit
               }, 31, null)
            }
         }
      }
   }

   private fun awaitCastConfirmation(maxWaitMs: Long = 700L): Boolean {

      if (var10000 == null) {
         return false
      } else {




         while (System.currentTimeMillis() - start < maxWaitMs) {
            playersBobber = this.refreshBobberFromPlayer()
            if (this.validBobber(playersBobber, p)) {
               return true
            }

            if (lastThrowHeardMs != 0L && lastThrowHeardMs != throwMark) {
            }

            Thread.sleep(20L)
         }

         playersBobber = this.refreshBobberFromPlayer()
         return this.validBobber(playersBobber, p)
      }
   }

   private fun hookRotatorRenderTick() {
      if (!renderHooked) {
         renderHooked = true
         WorldRenderEvents.END_MAIN.register({ var0: WorldRenderContext ->
            Rotator.onTick()
         })
      }
   }

   private fun ensureFishingHudReady() {
      if (!fishingHudReady) {

            Pair(PersistentState.fishingHudX, PersistentState.fishingHudY)
return else
            this.defaultFishingHudPosition()
            var var10000: MovableOverlay = MovableOverlayManager.getOverlay("fishingHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "fishingHud",
                  "Fishing HUD",
                  (targetX.getFirst() as java.lang.Number).intValue(),
                  (targetX.getSecond() as java.lang.Number).intValue(),
                  176,
return 76
               )
            }

         var10000.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
            renderFishingHud(context, x, y)
return Unit
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.fishingHudX = x
            PersistentState.fishingHudY = y
            PersistentState.fishingHudInitDone = true
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }
         var10000.register()
         fishingHudReady = true
      }

      if (!fishingHudAppliedSavedPosition) {



         if (var5 != null) {
            var5.setPositionSilently(var3, var4)
         }

         fishingHudAppliedSavedPosition = true
      }
   }

   fun renderFishingHud(context: DrawContext, x: Int, y: Int) {

      if (masterEnabled || preview) {
         val lines: java.util.List = listOf(
            arrayOf(
               "Action: ${this.currentFishingHudActionStable(preview)}",
               "Time left: ${this.formatFishingTimeLeft(preview)}",
               "Type: ${this.currentHudFishingType(preview)}",
               "Target: ${this.currentFishingTargetLabelStable(preview)}"
            )
         )
         val panelHeight: java.util.Iterator = lines.iterator()
         if (!panelHeight.hasNext()) {
            throw NoSuchElementException()
         } else {
            var var24: Int = getClient().textRenderer.getWidth(panelHeight.next() as String)

            while (panelHeight.hasNext()) {

               if (var24 < var27) {
                  var24 = var27
               }
            }



            if (var10000 != null) {
               var10000.width = panelWidth
               var10000.height = var23
            }


            context.fill(x, y, x + panelWidth, y + var23, -871952102)
            context.fill(x + 2, y + 2, x + panelWidth - 2, y + var23 - 2, -871688414)
            context.fill(x + 3, y + 3, x + panelWidth - 3, y + 18, -1441781446)
            context.drawStrokedRectangle(x, y, panelWidth, var23, -12994817)
            context.drawStrokedRectangle(x + 1, y + 1, panelWidth - 2, var23 - 2, -12265606)
            this.renderFishingHudSweep(context, x + 3, y + 3, panelWidth - 6, 15)
            this.drawFishingHudText(
               context, "JR Fishing", x + (panelWidth - this.getClient().textRenderer.getWidth("JR Fishing")) / 2, y + 5, -7477249, -14787203
            )
            var lineY: Int = y + 24

            for (line in lines) {
               this.drawFishingHudText(context, line, x + 8, lineY, textColor, textShadow)
               lineY += this.getClient().textRenderer.fontHeight + 3
            }
         }
      }
   }

   private fun currentFishingHudActionRaw(): String {
      return if (!masterEnabled)
         "Disabled"
return else
         (
            if (hardPausedForMelee)
               "Paused for melee"
return else
               (
                  if (placingDeployable)
                     (if (!isBlank(deployableActionLabel)) deployableActionLabel else "Placing deployable")
return else
                     (
                        if (goldenFishFocus && goldenFishPendingCast)
                           "Golden Fish cast"
return else
                           (
                              if (goldenFishFocus)
                                 "Golden Fish tracking"
return else
                                 (
                                    if (killing && !isBlank(currentKillTargetName))
                                       "Killing"
return else
                                       (
                                          if (killing)
                                             "Auto kill"
return else
                                             (if (recastQueued) "Reeling" else (if (this.isRodOut()) "Waiting for bite" else "Casting rod"))
                                       )
                                 )
                           )
                     )
               )
         )
      }

   private fun currentFishingTargetLabelRaw(): String {
      return if (goldenFishFocus) "Golden Fish" else (if (!isBlank(currentKillTargetName)) currentKillTargetName else "--")
   }

   private fun currentFishingHudActionStable(preview: Boolean): String {
      if (preview) {
         return "Waiting for bite"
      } else {


         if (raw == fishingHudActionCached || now - fishingHudActionUpdatedAtMs >= 220L) {
            fishingHudActionCached = raw
            fishingHudActionUpdatedAtMs = now
         }

         return fishingHudActionCached
      }
   }

   private fun currentFishingTargetLabelStable(preview: Boolean): String {
      if (preview) {
         return "Sea Creature"
      } else {


         if (raw == fishingHudTargetCached || now - fishingHudTargetUpdatedAtMs >= 220L) {
            fishingHudTargetCached = raw
            fishingHudTargetUpdatedAtMs = now
         }

         return fishingHudTargetCached
      }
   }

   private fun resetFishingHudCache() {

      fishingHudActionCached = "Waiting for bite"
      fishingHudActionUpdatedAtMs = now
      fishingHudTargetCached = "--"
      fishingHudTargetUpdatedAtMs = now
   }

   private fun isWrongFishingRodMessage(message: String): Boolean {
      return message == "This Fishing Rod does not work in lava." || message == "This Fishing Rod does not work in water."
   }

   private fun formatFishingTimeLeft(preview: Boolean): String {
      if (preview) {
         val var16: Array<Any> = arrayOf((Config.fishingSessionMinutes).coerceAtLeast(1))

         return var18
      } else if (masterEnabled && startedAtMs > 0L) {

               0L, (Config.fishingSessionMinutes).coerceAtLeast(1).toLong() * 60000L - (System.currentTimeMillis() - startedAtMs)
            )
            / 1000L



         val var17: Array<Any> = arrayOf(minutes, seconds)

         return var10000
      } else {
         return "00m 00s"
      }
   }

   private fun defaultFishingHudPosition(): Pair<Int, Int> {
      return Pair(10, 10)
   }

   fun drawFishingHudText(context: DrawContext, text: String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.drawText(this.getClient().textRenderer, Text.literal(text) as Text, x + 1, y + 1, shadowColor, false)
      context.drawText(this.getClient().textRenderer, Text.literal(text) as Text, x, y, color, false)
   }

   fun renderFishingHudSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {




      if (right > left) {
         context.fill(left, y, right, y + height, 860350151)
      }
   }

   private fun message(msg: String) {

      if (var10000 != null) {
         var10000.sendMessage(Text.literal("${JooonReimagined.Companion.PREFIX_CLEAN}$msg") as Text, false)
      }
   }

   
   fun onParticle(x: Double, y: Double, z: Double, typeKey: String, count: Int, speed: Float) {
   }

   
   fun onSound(key: String, x: Double, y: Double, z: Double) {
      if (!endsWith$default(key, "entity.fishing_bobber.throw", false, 2, null)) {
         if (masterEnabled && Config.fishingEnabled && !hardPausedForMelee && biteArmed) {
            if (System.currentTimeMillis() - lastCastMs >= 400L) {
               if (playersBobber != null) {

                  if (endsWith$default(key, "entity.fishing_bobber.splash", false, 2, null) && var8.squaredDistanceTo(x, y, z) < 25.0) {
                     triggerCatch()
                  }
               }
            }
         }
      } else {

         if (bobber == null || bobber.squaredDistanceTo(x, y, z) <= 16.0) {
            lastThrowHeardMs = System.currentTimeMillis()
         }
      }
   }

   
   fun onBobberVelocity(entityId: Int, vx: Double, vy: Double, vz: Double) {
      if (masterEnabled && Config.fishingEnabled && !hardPausedForMelee && biteArmed) {
         if (System.currentTimeMillis() - lastCastMs >= 400L) {
            if (playersBobber != null) {
               if (playersBobber.getId() == entityId) {
                  if (vy < -0.3) {
                     triggerCatch()
                  }
               }
            }
         }
      }
   }

   
   fun onHookTick(ownerUuid: UUID?, hookCountdown: Int) {
      if (masterEnabled && Config.fishingEnabled && !hardPausedForMelee && biteArmed) {
         if (System.currentTimeMillis() - lastCastMs >= 400L) {

            if (var10000 != null) {
               if (ownerUuid != null && ownerUuid == var10000.getUuid() && hookCountdown > 0) {
                  triggerCatch()
               }
            }
         }
      }
   }

   private fun swapUseBackClicks(
      weaponSlot: Int,
      lookDown: Boolean = false,
      clicks: Int = 1,
      intervalMs: Long = 160L,
      lookDownPitch: Float? = null,
      lockMove: Boolean = false
   ) {
      if (masterEnabled && !hardPausedForMelee) {
         if (!rodSequenceInProgress) {

            if (var10000 != null) {


               if (rod != -1 && 0 <= weaponSlot && weaponSlot < 9) {
                  rodSequenceInProgress = true
                  suppressSlotCheckUntilMs = System.currentTimeMillis() + 2000L
                  lastActivityMs = System.currentTimeMillis()


                  if (lockMove) {
                     PlayerController.pressForward(false)
                     PlayerController.pressBack(false)
                     PlayerController.pressLeft(false)
                     PlayerController.pressRight(false)
                     PlayerController.pressSprint(false)
                     PlayerController.pressJump(false)
                  }

                  try {
                     this.selectHotbarSlot(weaponSlot)
                     Thread.sleep(155L)
                     if (lookDown || lookDownPitch != null) {
                        this.getClient().execute({ 
                           `$p`.setYaw(`$prevYaw`)
                           `$p`.setPitch((`$lookDownPitch` ?: 89.0F).coerceIn(-89.9F, 89.9F))
                        })
                        Thread.sleep(60L)
                     }

                     repeat(clicks) { var12 ->
                        if (!masterEnabled || hardPausedForMelee) {
return return
                        }

                        PlayerController.rightClick()
                        Thread.sleep(intervalMs)
                     }

                     Thread.sleep(400L)
                     this.selectHotbarSlot(rod)
                     Thread.sleep(251L)
                     this.getClient().execute({ 
                        `$p`.setPitch(`$prevPitch`)
                        `$p`.setYaw(`$prevYaw`)
                     })
                     this.castSingleFromRod()
                     lastActivityMs = System.currentTimeMillis()
                  } finally {
                     rodSequenceInProgress = false
                  }
               }
            }
         }
      }
   }

   private fun castSingleFromRod() {
      if (masterEnabled && !hardPausedForMelee) {
         if (!this.isRodOut()) {
            PlayerController.rightClick()
            lastCastMs = System.currentTimeMillis()
            lastActivityMs = lastCastMs
            exclaimSeenThisCast = false
            this.armBiteAfterDelay()
            this.awaitCastConfirmation(700L)
         }
      }
   }

   private fun triggerCatch() {
      if (masterEnabled && !hardPausedForMelee) {
         if (!goldenFishFocus) {
            this.queueCatch(true)
         }
      }
   }

   
   fun `collectPrioritizedDeployOffsets$add`(seen: MutableSet<Pair<Integer, Integer>>, result: MutableList<Pair<Integer, Integer>>, dx: Int, dz: Int) {

      if (seen.add(key)) {
         result.add(key)
      }
   }

   
   fun `collectPrioritizedDeployOffsets$addDir`(
      seen: MutableSet<Pair<Integer, Integer>>, result: MutableList<Pair<Integer, Integer>>, dir: Direction, dist: Int
   ) {
      collectPrioritizedDeployOffsets$add(seen, result, dir.getOffsetX() * dist, dir.getOffsetZ() * dist)
   }

   private data class DeployableChoice(slot: Int, displayName: String, durationMs: Long) {
      val slot: Int
      val displayName: String
      val durationMs: Long

      init {
         this.slot = slot
         this.displayName = displayName
         this.durationMs = durationMs
      }

      public operator fun component1(): Int {
         return this.slot
      }

      public operator fun component2(): String {
         return this.displayName
      }

      public operator fun component3(): Long {
         return this.durationMs
      }

      fun copy(slot: Int = this.slot, displayName: String = this.displayName, durationMs: Long = this.durationMs): jooon.features.fishing.FunnyFishing.DeployableChoice {
         return FunnyFishing.DeployableChoice(slot, displayName, durationMs)
      }

      override fun toString(): String {
         return "DeployableChoice(slot=${this.slot}, displayName=${this.displayName}, durationMs=${this.durationMs})"
      }

      override fun hashCode(): Int {
         return (Integer.hashCode(this.slot) * 31 + this.displayName.hashCode()) * 31 + java.lang.Long.hashCode(this.durationMs)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is FunnyFishing.DeployableChoice
               && this.slot == (other as FunnyFishing.DeployableChoice).slot
               && this.displayName == (other as FunnyFishing.DeployableChoice).displayName
               && this.durationMs == (other as FunnyFishing.DeployableChoice).durationMs
            }
      }
   }

   enum class FishingType {
      WATER,
      LAVA;

      
      fun getEntries(): EnumEntries<FunnyFishing.FishingType> {
         $ENTRIES
      }
   }
}
