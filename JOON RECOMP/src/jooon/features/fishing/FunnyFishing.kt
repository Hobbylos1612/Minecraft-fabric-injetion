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
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.class_2338
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

@SourceDebugExtension(["SMAP\nFunnyFishing.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FunnyFishing.kt\njooon/features/fishing/FunnyFishing\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1894:1\n1747#2,3:1895\n800#2,11:1899\n766#2:1910\n857#2,2:1911\n1774#2,4:1913\n1208#2,2:1919\n1238#2,4:1921\n1054#2:1925\n288#2,2:1926\n1#3:1898\n12474#4,2:1917\n*S KotlinDebug\n*F\n+ 1 FunnyFishing.kt\njooon/features/fishing/FunnyFishing\n*L\n818#1:1895,3\n929#1:1899,11\n930#1:1910\n930#1:1911,2\n1155#1:1913,4\n147#1:1919,2\n147#1:1921,4\n150#1:1925\n489#1:1926,2\n1421#1:1917,2\n*E\n"])
public object FunnyFishing : ClientModInitializer {
   private final var masterEnabled: Boolean
   @JvmStatic
   private KeyBinding toggleKey;
   private final var startedAtMs: Long
   @JvmStatic
   private BlockPos mainLookAtBlock;
   private final var lastRotateMs: Long
   private final var lastReelMs: Long
   private final var lastSellMs: Long
   private final var lastDeployCheckMs: Long
   private final var nextTotemDeployAtMs: Long
   private final var nextPowerOrbDeployAtMs: Long
   private final var nextUmbrellaDeployAtMs: Long
   private final var lastCastMs: Long
   private final var lastActivityMs: Long
   private final var recastQueued: Boolean
   private final var biteArmed: Boolean
   private final var hadBobber: Boolean
   private final var exclaimSeenThisCast: Boolean
   private final var lastExclaimTriggerMs: Long
   private final var castVerifyInProgress: Boolean
   private final var rodSequenceInProgress: Boolean
   private final var fishingType: jooon.features.fishing.FunnyFishing.FishingType = FunnyFishing.FishingType.WATER
   @JvmStatic
   private FishingBobberEntity playersBobber;
   private final var killing: Boolean
   private final var currentKillTargetName: String = ""
   private final var suppressNextAutoRecastUntilMs: Long
   private final var fishingHudReady: Boolean
   private final var fishingHudAppliedSavedPosition: Boolean
   private final var fishingHudActionCached: String = "Waiting for bite"
   private final var fishingHudActionUpdatedAtMs: Long
   private final var fishingHudTargetCached: String = "--"
   private final var fishingHudTargetUpdatedAtMs: Long
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
   private final var suppressSlotCheckUntilMs: Long
   @JvmStatic
   private BlockPos lastTotemPlacePos;
   @JvmStatic
   private BlockPos lastPowerOrbPlacePos;
   @JvmStatic
   private BlockPos lastUmbrellaPlacePos;
   private final var placingDeployable: Boolean
   private final var deployableActionLabel: String = ""
   private final var suppressAntiAfkRotation: Boolean
   private final var goldenFishFocus: Boolean
   @JvmStatic
   private ArmorStandEntity goldenFishTarget;
   private final var goldenFishPendingCast: Boolean
   private final var goldenFishAimStartedMs: Long
   private final var lastThrowHeardMs: Long
   private const val EMPTY_SLOTS_SELL_THRESHOLD: Int = 2
   private const val DEPLOY_CHECK_INTERVAL_MS: Long = 450L
   private const val TOTEM_DURATION_MS: Long = 120000L
   private const val SHORT_POWER_ORB_DURATION_MS: Long = 30000L
   private const val LONG_POWER_ORB_DURATION_MS: Long = 60000L
   private const val UMBRELLA_DURATION_MS: Long = 300000L
   private const val GF_CAST_DELAY_MS: Long = 300L
   private const val GF_ACQUIRE_WINDOW_MS: Long = 2500L
   private const val GF_REACQUIRE_MS: Long = 150L
   private final var lastWorldKeyHash: Int?
   private final var gfEventStartMs: Long
   private final var gfLastAcquireMs: Long
   private const val GF_GRACE_AFTER_EMERGE_MS: Long = 3000L
   private final var gfGraceUntilMs: Long
   private const val GF_STATIONARY_MS: Long = 1800L
   private const val GF_BOBBER_MIN_SPEED2: Double = 5.0E-4
   private const val GF_RETRY_COOLDOWN_MS: Long = 700L
   private final var gfLastRetryMs: Long
   private const val GF_NO_TARGET_MS: Long = 1500L
   private const val GF_NO_PROGRESS_MS: Long = 4000L

   private final val seaCreatureNameLookup: Map<String, String> by LazyKt.lazy(
      { 
         val `$this$associateBy$iv`: java.lang.Iterable = CollectionsKt.distinct(FishingStrings.INSTANCE.seaCreatureMessages.values())
         val `destination$iv$iv`: java.util.Map = LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateBy$iv`, 10)), 16)
         )

         for (`element$iv$iv` in `$this$associateBy$iv`) {
            `destination$iv$iv`.put(INSTANCE.normalizeStandName(`element$iv$iv` as java.lang.String), `element$iv$iv` as java.lang.String)
         }

         `destination$iv$iv`
      }
   )
      private final get() {
         return seaCreatureNameLookup$delegate.getValue() as MutableMap<java.lang.String, java.lang.String>
      }


   private final val seaCreatureKeysByLengthDesc: List<String> by LazyKt.lazy(
      { 
         CollectionsKt.sortedWith(
            INSTANCE.seaCreatureNameLookup.keySet(), FunnyFishing$seaCreatureKeysByLengthDesc_delegate$lambda$4$$inlined$sortedByDescending$1()
         )
      }
   )
      private final get() {
         return seaCreatureKeysByLengthDesc$delegate.getValue() as MutableList<java.lang.String>
      }


   private final val crimsonIsleAreaRegex: Regex = Regex("\\bArea\\s*:\\s*Crimson Isle\\b", RegexOption.IGNORE_CASE)
   private final val galateaAreaRegex: Regex = Regex("\\bArea\\s*:\\s*Galatea\\b", RegexOption.IGNORE_CASE)
   private final var savedYaw: Float
   private final var savedPitch: Float
   private final var haveSavedView: Boolean
   private final var hardPausedForMelee: Boolean
   private final var renderHooked: Boolean

   fun getClient(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   fun saveView(p: ClientPlayerEntity) {
      if (!haveSavedView) {
         savedYaw = p.method_36454()
         savedPitch = p.method_36455()
         haveSavedView = true
      }
   }

   private fun restoreViewIfSaved() {
      if (haveSavedView) {
         val var10000: ClientPlayerEntity = this.getClient().field_1724
         if (var10000 != null) {
            var10000.method_36456(savedYaw)
            var10000.method_36457(savedPitch)
         }

         haveSavedView = false
      }
   }

   fun ownsBobber(b: FishingBobberEntity, p: ClientPlayerEntity): Boolean {
      b != null && p != null && b.method_24921() == p && !b.method_31481() && !(b.method_5858(p as Entity) < 1.5)
   }

   fun validBobber(b: FishingBobberEntity, p: ClientPlayerEntity): Boolean {
      if (!this.ownsBobber(b, p)) {
         false
      } else {
         !b.method_24828()
      }
   }

   private fun isRodOut(): Boolean {
      return this.ownsBobber(playersBobber, this.getClient().field_1724)
   }

   public fun pauseForMeleeImmediate() {
      hardPausedForMelee = true
      recastQueued = false
      biteArmed = false
      suppressAntiAfkRotation = true
      Rotator.INSTANCE.clear()
   }

   public fun resumeAfterMelee() {
      suppressAntiAfkRotation = false
      hardPausedForMelee = false
   }

   public fun recastAfterMelee() {
      if (masterEnabled && !hardPausedForMelee) {
         if (!Config.fishingEnabled) {
            Config.fishingEnabled = true
         }

         ThreadsKt.thread$default(true, false, null, "RecastAfterMelee", 0, lambda_7@{ 
            val rod: Int = INSTANCE.getFishingRodSlot()
            if (rod == -1) {
               return@lambda_7 Unit.INSTANCE
            } else {
               INSTANCE.getClient().execute({ 
                  INSTANCE.selectHotbarSlot(`$rod`)
               })
               Thread.sleep(251L)
               castWithVerify$default(INSTANCE, 1000L, 200L, 0, 4, null)
               return@lambda_7 Unit.INSTANCE
            }
         }, 22, null)
      }
   }

   private fun canCatchNow(): Boolean {
      return !Config.slugFishEnabled || System.currentTimeMillis() - lastCastMs >= RangesKt.coerceAtLeast(Config.slugFishMinWaitSec, 0) * 1000L
   }

   public open fun onInitializeClient() {
      this.hookRotatorRenderTick()
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.togglefishing", 71, Category.field_62556))
      ClientTickEvents.END_CLIENT_TICK
         .register(
            lambda_13@{ it: MinecraftClient ->
               PacketRateLimiter.onTick()
               FishingMeleeMobs.INSTANCE.onTick()

               while (true) {
                  var var10000: KeyBinding = toggleKey
                  if (toggleKey == null) {
                     Intrinsics.throwUninitializedPropertyAccessException("toggleKey")
                     var10000 = null
                  }

                  if (!var10000.method_1436()) {
                     INSTANCE.ensureFishingHudReady()
                     val w: ClientWorld = INSTANCE.getClient().field_1687
                     val p: ClientPlayerEntity = INSTANCE.getClient().field_1724
                     if (masterEnabled && Config.fishingEnabled) {
                        if (p != null && w != null) {
                           if (hardPausedForMelee) {
                              return@lambda_13
                           }

                           if (System.currentTimeMillis() - startedAtMs >= Config.fishingSessionMinutes * 60000L) {
                              INSTANCE.message("Session ended (${Config.fishingSessionMinutes} min).")
                              INSTANCE.disableFishing()
                              return@lambda_13
                           }

                           INSTANCE.updatePlayersBobber()
                           val rodSlot: Int = INSTANCE.getFishingRodSlot()
                           if (rodSlot != p.method_31548().method_67532()
                              && System.currentTimeMillis() > suppressSlotCheckUntilMs
                              && rodSlot != -1
                              && startedAtMs != 0L) {
                              INSTANCE.message("Detected slot change, disabling.")
                              INSTANCE.disableFishing()
                              return@lambda_13
                           }

                           if (INSTANCE.processAutoDeployables()) {
                              return@lambda_13
                           }

                           if (!goldenFishFocus
                              && !recastQueued
                              && !killing
                              && !placingDeployable
                              && !rodSequenceInProgress
                              && !INSTANCE.isRodOut()
                              && System.currentTimeMillis() - lastCastMs >= 1200L) {
                              if (Config.fishingKillingMode != Config.Companion.FishingKillMode.OFF
                                 && canUseSeaCreatureFallbackNow$default(INSTANCE, 0L, 1, null)) {
                                 val var21: java.lang.String = INSTANCE.detectNearbySeaCreatureName(35.0)
                                 if (var21 != null && INSTANCE.startAutoKillForCreatureName(var21)) {
                                    return@lambda_13
                                 }
                              }

                              currentKillTargetName = ""
                              castWithVerify$default(INSTANCE, 1000L, 200L, 0, 4, null)
                              return@lambda_13
                           }

                           if (playersBobber != null) {
                              if (!goldenFishFocus && !rodSequenceInProgress && playersBobber.method_24828() && System.currentTimeMillis() - lastCastMs > 1200L
                                 )
                               {
                                 reelIn$default(INSTANCE, true, false, false, 6, null)
                              }
                           }

                           if (!goldenFishFocus) {
                              if (playersBobber != null) {
                                 val var22: FishingBobberEntity = playersBobber
                                 if (!exclaimSeenThisCast
                                    && System.currentTimeMillis() - lastExclaimTriggerMs > 250L
                                    && INSTANCE.detectExclaimAbove(var22)
                                    && INSTANCE.canCatchNow()) {
                                    exclaimSeenThisCast = true
                                    lastExclaimTriggerMs = System.currentTimeMillis()
                                    lastActivityMs = lastExclaimTriggerMs
                                    INSTANCE.queueCatch(true)
                                 }
                              }
                           }

                           if (!goldenFishFocus && !rodSequenceInProgress && System.currentTimeMillis() - lastActivityMs > 44000L) {
                              reelIn$default(INSTANCE, true, false, false, 6, null)
                           }

                           if (Config.fishingRotate
                              && !goldenFishFocus
                              && !suppressAntiAfkRotation
                              && !Rotator.INSTANCE.isActive
                              && System.currentTimeMillis() - lastRotateMs >= Config.fishingRotateIntervalMs) {
                              INSTANCE.performSmallRotation()
                              lastRotateMs = System.currentTimeMillis()
                           }

                           if (Config.enableGoldenFishCatch && goldenFishFocus) {
                              var now: Long
                              run label261@{
                                 suppressAntiAfkRotation = true
                                 biteArmed = false
                                 now = System.currentTimeMillis()
                                 if (goldenFishTarget != null) {
                                    val var25: FunnyFishing = INSTANCE
                                    val var10001: ArmorStandEntity = goldenFishTarget
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
                                    if (goldenFishTarget != null) INSTANCE.preferredHitPoint(goldenFishTarget) else null
                                 }, 0.08F, 720.0F, 0.2F, null, 16, null)
                              } else {
                                 Rotator.INSTANCE.clear()
                                 goldenFishAimStartedMs = 0L
                              }

                              if (goldenFishPendingCast && goldenFishTarget != null && now - goldenFishAimStartedMs >= 300L && !INSTANCE.isRodOut()) {
                                 INSTANCE.castOnGoldenFish()
                              }

                              if (playersBobber != null) {
                                 val bob: FishingBobberEntity = playersBobber
                                 val now2: Long = System.currentTimeMillis()
                                 val sinceCast: Long = now2 - lastCastMs
                                 val sinceRetry: Long = now2 - gfLastRetryMs
                                 val stationary: Boolean = bob.method_18798().method_1027() < 5.0E-4
                                 val noTargetTooLong: Boolean = now2 - gfLastAcquireMs > 1500L
                                 if (now2 >= gfGraceUntilMs
                                    && INSTANCE.isRodOut()
                                    && sinceRetry >= 700L
                                    && (noTargetTooLong || stationary && sinceCast >= 1800L || sinceCast >= 4000L)) {
                                    gfLastRetryMs = now2
                                    reelIn$default(INSTANCE, false, true, false, 4, null)
                                    goldenFishPendingCast = true
                                    goldenFishAimStartedMs = 0L
                                 }
                              }
                           }

                           if (Config.fishingAutoSell && INSTANCE.shouldAutoSellByEmptySlots() && System.currentTimeMillis() - lastSellMs > 10000L) {
                              lastSellMs = System.currentTimeMillis()
                              p.field_3944.method_45730("bz")
                              BazaarAutoSell.INSTANCE.queue()
                           }

                           return@lambda_13
                        }

                        return@lambda_13
                     }

                     if (Rotator.INSTANCE.isActive) {
                        Rotator.INSTANCE.clear()
                     }

                     goldenFishFocus = false
                     goldenFishTarget = null
                     goldenFishPendingCast = false
                     goldenFishAimStartedMs = 0L
                     return@lambda_13
                  }

                  if (FishingMeleeMobs.INSTANCE.isBusy) {
                     FishingMeleeMobs.INSTANCE.forceAbortAndDisableFishing()
                     masterEnabled = false
                     INSTANCE.message("§cAuto Fishing disabled.")
                  } else if (!masterEnabled) {
                     INSTANCE.enableFishing()
                  } else {
                     INSTANCE.disableFishing()
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
      if (this.getClient().field_1724 != null) {
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
         val slug: HitResult = this.getClient().field_1765
         mainLookAtBlock = if ((slug as? BlockHitResult) != null) (slug as? BlockHitResult).method_17777() else null
         FishingMeleeMobs.INSTANCE.onAutoFishingStart()
         val rod: Int = this.getFishingRodSlot()
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

               Unit.INSTANCE
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
      FishingMeleeMobs.INSTANCE.onAutoFishingStop()
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
      Rotator.INSTANCE.clear()
      playersBobber = null
      this.resetFishingHudCache()
      this.restoreViewIfSaved()
      this.stopAntiAfkNudges()
      this.message("§cAuto Fishing disabled.")
   }

   private fun disableFishingForWrongRod() {
      FishingMeleeMobs.INSTANCE.onAutoFishingStop()
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
      Rotator.INSTANCE.clear()
      playersBobber = null
      this.resetFishingHudCache()
      this.restoreViewIfSaved()
      this.stopAntiAfkNudges()
   }

   private fun stopAntiAfkNudges() {
   }

   private fun detectFishingType(): jooon.features.fishing.FunnyFishing.FishingType {
      val hr: HitResult = this.getClient().field_1765
      if (hr is BlockHitResult) {
         val var10000: BlockPos = (hr as BlockHitResult).method_17777()
         val var4: ClientWorld = this.getClient().field_1687
         val var5: BlockState = var4.method_8320(var10000)
         return if (var5.method_26227().method_15767(FluidTags.field_15518)) FunnyFishing.FishingType.LAVA else FunnyFishing.FishingType.WATER
      } else {
         return FunnyFishing.FishingType.WATER
      }
   }

   fun refreshBobberFromPlayer(): FishingBobberEntity {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 == null) {
         null
      } else {
         val hook: FishingBobberEntity = var10000.field_7513
         if (this.ownsBobber(var10000.field_7513, var10000)) hook else null
      }
   }

   fun detectFluidAtBobber(bobber: FishingBobberEntity): FunnyFishing.FishingType {
      val var10000: ClientWorld = this.getClient().field_1687
      if (var10000 == null) {
         fishingType
      } else {
         var level: ClientWorld
         run label49@{
            level = var10000
            if (bobber != null) {
               var6 = bobber.method_24515()
               if (var6 != null) {
                  return@label49
               }
            }

            val var7: ClientPlayerEntity = this.getClient().field_1724
            var6 = if (var7 != null) var7.method_24515() else null
            if (var6 == null) {
               fishingType
            }
         }

         val anchor: BlockPos = var6

         for (i in -1..4) {
            val var8: FluidState = level.method_8320(anchor.method_10087(i)).method_26227()
            if (var8.method_15767(FluidTags.field_15518)) {
               FunnyFishing.FishingType.LAVA
            }

            if (var8.method_15767(FluidTags.field_15517)) {
               FunnyFishing.FishingType.WATER
            }
         }

         fishingType
      }
   }

   private fun randomMsInRange(minMs: Int, maxMs: Int): Long {
      return RangesKt.random(IntRange(RangesKt.coerceAtMost(minMs, maxMs), RangesKt.coerceAtLeast(maxMs, minMs)), Random.Default as Random)
   }

   fun randomPreCatchDelayMs(bobber: FishingBobberEntity): Long {
      var var10000: Long
      when (FunnyFishing.WhenMappings.$EnumSwitchMapping$1[this.detectFluidAtBobber(bobber).ordinal()]) {
         1 -> var10000 = this.randomMsInRange(Config.fishingLavaPreCatchMinMs, Config.fishingLavaPreCatchMaxMs)
         2 -> var10000 = this.randomMsInRange(Config.fishingWaterPreCatchMinMs, Config.fishingWaterPreCatchMaxMs)
         else -> throw NoWhenBranchMatchedException()
      }

      var10000
   }

   fun randomCastDelayMs(bobber: FishingBobberEntity): Long {
      val jitter: Int = RangesKt.coerceAtLeast(Config.fishingRecastJitterMs, 0)
      val base: Int = Config.fishingRecastDelayMs
      val delay: Long = this.randomMsInRange(RangesKt.coerceAtLeast(Config.fishingRecastDelayMs - jitter, 250), base + jitter)
      if (this.detectFluidAtBobber(bobber) === FunnyFishing.FishingType.LAVA) delay + RangesKt.random(LongRange(0L, 40L), Random.Default as Random) else delay
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
                           return@lambda_20 Unit.INSTANCE
                        }

                        reelIn$default(INSTANCE, `$recast`, false, true, 2, null)
                     } finally {
                        Thread.sleep(1350L)
                        recastQueued = false
                     }

                     return@lambda_20 Unit.INSTANCE
                  }, 23, null)
               }
            }
         }
      }
   }

   fun isInOrAboveLavaRobust(e: ArmorStandEntity): Boolean {
      val var10000: ClientWorld = this.getClient().field_1687
      if (var10000 == null) {
         false
      } else {
         val w: ClientWorld = var10000
         val var26: Box = e.method_5829()
         val bb: Box = var26
         val minX: Double = var26.field_1323 - 0.5
         val maxX: Double = var26.field_1320 + 0.5
         val minZ: Double = var26.field_1321 - 0.5
         val maxZ: Double = var26.field_1324 + 0.5
         val x: Double = 0.0

         // $VF: Unable to resugar Kotlin loop from Java for loop
         var var23: Double = minX
         while (true) {
            if (var23 <= maxX + 1.0E-6) break
            val z: Double = 0.0

            // $VF: Unable to resugar Kotlin loop from Java for loop
            var var24: Double = minZ
            while (true) {
               if (var24 <= maxZ + 1.0E-6) break
               var y: Double = 0.0
               y = bb.field_1322 + 0.5
               val var12: Byte = 28

               repeat(var12) { var13 ->
                  y -= 0.125
                  val var27: BlockPos = BlockPos.method_49637(var23, y, var24)
                  if (w.method_8316(var27).method_15767(FluidTags.field_15518)) {
                     true
                  }
               }

               var24 += 0.25
            }

            var23 += 0.25
         }

         false
      }
   }

   fun isInWater(player: PlayerEntity): Boolean {
      player.method_5799() || player.method_5869()
   }

   fun isValidGoldenStand(e: ArmorStandEntity): Boolean {
      e.method_5805() && !e.method_31481() && e.method_5767() && this.isInOrAboveLavaRobust(e)
   }

   fun findGoldenFishEntityRobust(maxDistanceSq: Double): ArmorStandEntity {
      val var10000: ClientWorld = this.getClient().field_1687
      if (var10000 == null) {
         null
      } else {
         val var24: ClientPlayerEntity = this.getClient().field_1724
         if (var24 == null) {
            null
         } else {
            val p: ClientPlayerEntity = var24
            val favorNew: Boolean = System.currentTimeMillis() - gfEventStartMs <= 2500L
            var best: ArmorStandEntity = null
            var bestScore: Double = java.lang.Double.NEGATIVE_INFINITY
            val var25: java.lang.Iterable = var10000.method_18112()

            for (var26 in CollectionsKt.toList(var25)) {
               val e: Entity = var26 as Entity
               if (var26 as Entity is ArmorStandEntity && this.isValidGoldenStand((var26 as Entity) as ArmorStandEntity)) {
                  val d2: Double = e.method_5858(p as Entity)
                  if (!(d2 > maxDistanceSq)) {
                     val score: Double = -d2 + (if (favorNew) 1000.0 / (1.0 + e.field_6012) else 0.0)
                     if (-d2 + (if (favorNew) 1000.0 / (1.0 + e.field_6012) else 0.0) > bestScore) {
                        bestScore = score
                        best = e as ArmorStandEntity
                     }
                  }
               }
            }

            if (best != null) {
               best
            } else {
               bestScore = java.lang.Double.NEGATIVE_INFINITY
               val var27: java.lang.Iterable = var10000.method_18112()

               for (var28 in CollectionsKt.toList(var27)) {
                  val var21: Entity = var28 as Entity
                  if (var28 as Entity is ArmorStandEntity
                     && ((var28 as Entity) as ArmorStandEntity).method_5805()
                     && !((var28 as Entity) as ArmorStandEntity).method_31481()
                     && this.isInOrAboveLavaRobust((var28 as Entity) as ArmorStandEntity)) {
                     val var22: Double = var21.method_5858(p as Entity)
                     if (!(var22 > maxDistanceSq)) {
                        val var23: Double = -var22
                        if (-var22 > bestScore) {
                           bestScore = var23
                           best = var21 as ArmorStandEntity
                        }
                     }
                  }
               }

               best
            }
         }
      }
   }

   fun preferredHitPoint(e: ArmorStandEntity): Vec3d {
      val var10000: Box = e.method_5829()

      var var9: Double
      try {
         var9 = e.method_23320()
      } catch (var12: java.lang.Throwable) {
         var9 = (var10000.field_1325 + var10000.field_1322) * 0.5
      }

      Vec3d((var10000.field_1323 + var10000.field_1320) * 0.5, var9 + 1.0, (var10000.field_1321 + var10000.field_1324) * 0.5)
   }

   private fun updatePlayersBobber() {
      if (this.getClient().field_1724 != null) {
         playersBobber = this.refreshBobberFromPlayer()
         val has: Boolean = playersBobber != null
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
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 != null) {
         var10000.method_36456(var10000.method_36454() + (float)((Math.random() - 0.5) * (double)1.2F))
      }
   }

   private fun canUseSeaCreatureFallbackNow(now: Long = System.currentTimeMillis()): Boolean {
      return lastReelMs > 0L && lastReelMs >= lastCastMs && now - lastReelMs <= 3500L
   }

   private fun shouldIgnoreSeaCreature(messageText: String = "", wantedName: String = ""): Boolean {
      return Config.fishingIgnoreSquids && (StringsKt.contains(wantedName, "squid", true) || StringsKt.contains(messageText, "squid", true))
   }

   private fun isInTabArea(areaRegex: Regex): Boolean {
      val var10000: ClientPlayNetworkHandler = this.getClient().method_1562()
      if (var10000 == null) {
         return false
      } else {
         val var14: java.util.Collection = var10000.method_2880()
         val `$this$any$iv`: java.lang.Iterable = var14
         var var15: Boolean
         if ((var14 as java.util.Collection).isEmpty()) {
            var15 = false
         } else {
            val var5: java.util.Iterator = `$this$any$iv`.iterator()

            while (true) {
               if (!var5.hasNext()) {
                  var15 = false
                  break
               }

               run label39@{
                  val info: PlayerListEntry = var5.next() as PlayerListEntry
                  val var16: Text = info.method_2971()
                  if (var16 != null) {
                     var17 = var16.getString()
                     if (var17 != null) {
                        return@label39
                     }
                  }

                  var17 = info.method_2966().name()
               }

               if (areaRegex.containsMatchIn(StringsKt.trim(StringsKt.replace$default(Regex("§.").replace(var17, ""), "Â", "", false, 4, null)).toString())) {
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
         var var10000: FunnyFishing.FishingType = if (playersBobber != null) INSTANCE.detectFluidAtBobber(playersBobber) else null
         var10000 = var10000
         if (var10000 == null) {
            var10000 = fishingType
         }

         var var6: java.lang.String
         when (FunnyFishing.WhenMappings.$EnumSwitchMapping$1[var10000.ordinal()]) {
            1 -> var6 = "Lava"
            2 -> var6 = "Water"
            else -> throw NoWhenBranchMatchedException()
         }

         return var6
      }
   }

   private fun shouldHandoffToAutoKillNow(now: Long = System.currentTimeMillis()): Boolean {
      return !killing && !hardPausedForMelee && (!StringsKt.isBlank(currentKillTargetName) || now < suppressNextAutoRecastUntilMs)
   }

   private fun reelIn(recast: Boolean, allowDuringGoldenFish: Boolean = false, skipPreCatch: Boolean = false) {
      if (masterEnabled && !hardPausedForMelee) {
         if (!goldenFishFocus || allowDuringGoldenFish) {
            if (!rodSequenceInProgress) {
               if (this.getClient().field_1724 != null) {
                  lastReelMs = System.currentTimeMillis()
                  lastActivityMs = lastReelMs
                  rodSequenceInProgress = true
                  ThreadsKt.thread$default(false, false, null, null, 0, lambda_26@{ 
                     try {
                        var var10000: FishingBobberEntity = playersBobber
                        if (playersBobber == null) {
                           var10000 = INSTANCE.refreshBobberFromPlayer()
                        }

                        if (!`$skipPreCatch`) {
                           Thread.sleep(INSTANCE.randomPreCatchDelayMs(var10000))
                        }

                        if (!masterEnabled || hardPausedForMelee) {
                           return@lambda_26 Unit.INSTANCE
                        }

                        PlayerController.INSTANCE.rightClick()
                        lastActivityMs = System.currentTimeMillis()
                        if (!`$recast`) {
                           return@lambda_26 Unit.INSTANCE
                        }

                        val castDelay: Long = INSTANCE.randomCastDelayMs(var10000)
                        biteArmed = false
                        val waitUntil: Long = System.currentTimeMillis() + castDelay

                        while (masterEnabled && !hardPausedForMelee) {
                           val nearbyCreature: Long = System.currentTimeMillis()
                           if (nearbyCreature >= waitUntil) {
                              break
                           }

                           if (INSTANCE.shouldHandoffToAutoKillNow(nearbyCreature)) {
                              val var10: java.lang.CharSequence = currentKillTargetName
                              var var18: java.lang.CharSequence
                              if (StringsKt.isBlank(currentKillTargetName)) {
                                 var18 = INSTANCE.detectNearbySeaCreatureName(35.0)
                                 if (var18 == null) {
                                    var18 = ""
                                 }
                              } else {
                                 var18 = var10
                              }

                              val var14: java.lang.String = (if (StringsKt.isBlank(var18)) null else var18) as java.lang.String
                              return@lambda_26 Unit.INSTANCE
                           }

                           Thread.sleep(Math.min(25L, waitUntil - nearbyCreature))
                        }

                        if (!masterEnabled || hardPausedForMelee) {
                           return@lambda_26 Unit.INSTANCE
                        }

                        if (killing || System.currentTimeMillis() < suppressNextAutoRecastUntilMs) {
                           return@lambda_26 Unit.INSTANCE
                        }

                        if (Config.fishingKillingMode != Config.Companion.FishingKillMode.OFF && canUseSeaCreatureFallbackNow$default(INSTANCE, 0L, 1, null)) {
                           val var15: java.lang.String = INSTANCE.detectNearbySeaCreatureName(35.0)
                           if (var15 != null) {
                              currentKillTargetName = var15
                              suppressNextAutoRecastUntilMs = System.currentTimeMillis() + 1500L
                              return@lambda_26 Unit.INSTANCE
                           }
                        }

                        PlayerController.INSTANCE.rightClick()
                        lastCastMs = System.currentTimeMillis()
                        lastActivityMs = lastCastMs
                        exclaimSeenThisCast = false
                        INSTANCE.armBiteAfterDelay()
                        INSTANCE.awaitCastConfirmation(700L)
                        Thread.sleep(450L + RangesKt.random(LongRange(0L, 80L), Random.Default as Random))
                     } finally {
                        rodSequenceInProgress = false
                     }

                     return@lambda_26 Unit.INSTANCE
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

         Unit.INSTANCE
      }, 31, null)
   }

   fun detectExclaimAbove(bobber: FishingBobberEntity): Boolean {
      val var10000: ClientWorld = this.getClient().field_1687
      if (var10000 == null) {
         false
      } else {
         val world: ClientWorld = var10000
         val var35: Box = bobber.method_5829().method_1009(1.6, 2.2, 1.6)
         val searchBox: Box = var35
         val var5: FunnyFishing = this

         var stand: FunnyFishing
         try {
            stand = var5
            stand = (FunnyFishing)Result.constructor_impl/* $VF was: constructor-impl */(
               world.method_18023(EntityType.field_6131 as TypeFilter, searchBox, { p0: Any ->
                  `$tmp0`(p0)
               })
            )
         } catch (var17: java.lang.Throwable) {
            stand = (FunnyFishing)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var17))
         }

         val var36: Any
         if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(stand) == null) {
            var36 = stand
         } else {
            val var37: java.lang.Iterable = var10000.method_18112()
            var `destination$iv$iv`: java.util.Collection = ArrayList()

            for (`element$iv$iv` in var37) {
               if (`element$iv$iv` is ArmorStandEntity) {
                  `destination$iv$iv`.add(`element$iv$iv`)
               }
            }

            val var28: java.lang.Iterable = `destination$iv$iv` as java.util.List
            `destination$iv$iv` = ArrayList()

            for (var34 in var28) {
               if ((var34 as ArmorStandEntity).method_5805()
                  && !(var34 as ArmorStandEntity).method_31481()
                  && searchBox.method_994((var34 as ArmorStandEntity).method_5829())) {
                  `destination$iv$iv`.add(var34)
               }
            }

            var36 = `destination$iv$iv` as java.util.List
         }

         for (var21 in var36 as java.util.List) {
            var var38: Text = var21.method_5797()
            if (var38 == null) {
               var38 = var21.method_5477()
            }

            val var39: java.lang.String = var38.getString()
            if (StringsKt.contains$default(
               StringsKt.trim(StringsKt.replace$default(Regex("§.").replace(var39, ""), "Â", "", false, 4, null)).toString(), "!!!", false, 2, null
            )) {
               true
            }
         }

         false
      }
   }

   private fun doFireVeilKill() {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {
         val wandSlot: Int = this.findInHotbarByName("Fire Veil Wand")
         if (wandSlot == -1) {
            this.message("§cFire Veil Wand not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep((long)Config.funnyFishingAutoKillingDelay)
                  swapUseBackClicks$default(INSTANCE, `$wandSlot`, false, 1, 160L, null, false, 48, null)
               } finally {
                  killing = false
               }

               Unit.INSTANCE
            }, 31, null)
         }
      }
   }

   private fun doWitherBladeKill(spawnMessage: String) {
      var var10001: java.lang.String = FishingStrings.INSTANCE.seaCreatureMessages.get(spawnMessage)
      if (var10001 == null) {
         var10001 = ""
      }

      this.doWitherBladeKillByName(var10001)
   }

   private fun doWitherBladeKillByName(wanted: String) {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {
         val var4: java.util.Iterator = CollectionsKt.listOf(arrayOf("Scylla", "Astraea", "Hyperion", "Valkyrie")).iterator()

         var var10000: Int
         while (true) {
            if (var4.hasNext()) {
               val var7: Int = INSTANCE.findInHotbarByName(var4.next() as java.lang.String)
               val var11: Int = if (var7.intValue() != -1) var7 else null
               if (var11 == null) {
                  continue
               }

               var10000 = var11
               break
            }

            var10000 = null
            break
         }

         val var10: Int = var10000 ?: -1
         if (var10 == -1) {
            this.message("§cWither Blade not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep((long)Config.funnyFishingAutoKillingDelay)
                  swapUseUntilArmorStandGone$default(INSTANCE, `$slot`, `$wanted`, true, 160L, 89.1F, true, 0, 64, null)
                  currentKillTargetName = ""
               } finally {
                  killing = false
               }

               Unit.INSTANCE
            }, 31, null)
         }
      }
   }

   private fun doMidasKill(spawnMessage: String) {
      var var10001: java.lang.String = FishingStrings.INSTANCE.seaCreatureMessages.get(spawnMessage)
      if (var10001 == null) {
         var10001 = ""
      }

      this.doMidasKillByName(var10001)
   }

   private fun doMidasKillByName(wanted: String) {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {
         val slot: Int = this.findInHotbarByName("Midas Staff")
         if (slot == -1) {
            this.message("§cMidas Staff not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep((long)Config.funnyFishingAutoKillingDelay)
                  swapUseUntilArmorStandGone$default(INSTANCE, `$slot`, `$wanted`, false, 160L, null, false, 0, 112, null)
                  currentKillTargetName = ""
               } finally {
                  killing = false
               }

               Unit.INSTANCE
            }, 31, null)
         }
      }
   }

   private fun doSpiritSceptreKill(spawnMessage: String) {
      var var10001: java.lang.String = FishingStrings.INSTANCE.seaCreatureMessages.get(spawnMessage)
      if (var10001 == null) {
         var10001 = ""
      }

      this.doSpiritSceptreKillByName(var10001)
   }

   private fun doSpiritSceptreKillByName(wanted: String) {
      if (!killing && !hardPausedForMelee && !placingDeployable && !rodSequenceInProgress && !castVerifyInProgress) {
         val var3: Int = this.findInHotbarByName("Spirit Sceptre")
         val it: Int = var3.intValue()
         val slot: Int = if ((if (it != -1) var3 else null) != null) if (it != -1) var3 else null else this.findInHotbarByName("Spirit Scepter")
         if (slot == -1) {
            this.message("§cSpirit Sceptre not found!")
         } else {
            killing = true
            ThreadsKt.thread$default(false, false, null, null, 0, { 
               try {
                  Thread.sleep((long)Config.funnyFishingAutoKillingDelay)
                  swapUseUntilArmorStandGone$default(INSTANCE, `$slot`, `$wanted`, true, 160L, 89.1F, true, 0, 64, null)
                  currentKillTargetName = ""
               } finally {
                  killing = false
               }

               Unit.INSTANCE
            }, 31, null)
         }
      }
   }

   private fun startAutoKillForCreatureName(wantedName: String): Boolean {
      val wanted: java.lang.String = StringsKt.trim(wantedName).toString()
      if (StringsKt.isBlank(wanted)) {
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
      val var10000: ClientWorld = this.getClient().field_1687
      if (var10000 == null) {
         return null
      } else {
         val var13: ClientPlayerEntity = this.getClient().field_1724
         if (var13 == null) {
            return null
         } else {
            val me: ClientPlayerEntity = var13
            val maxSq: Double = maxRange * maxRange
            val var14: java.lang.Iterable = var10000.method_18112()

            for (var15 in CollectionsKt.toList(var14)) {
               val entity: Entity = var15 as Entity
               if (var15 as Entity is ArmorStandEntity
                  && ((var15 as Entity) as ArmorStandEntity).method_5805()
                  && !((var15 as Entity) as ArmorStandEntity).method_31481()
                  && !((var15 as Entity).method_5858(me as Entity) > maxSq)) {
                  var var10001: Text = (entity as ArmorStandEntity).method_5797()
                  if (var10001 == null) {
                     var10001 = (entity as ArmorStandEntity).method_5477()
                  }

                  val var16: java.lang.String = var10001.getString()
                  val label: java.lang.String = this.normalizeStandName(var16)
                  if (!StringsKt.isBlank(label)) {
                     for (candidate in this.seaCreatureKeysByLengthDesc) {
                        if (!StringsKt.isBlank(candidate) && StringsKt.contains$default(label, candidate, false, 2, null)) {
                           val wanted: java.lang.String = this.seaCreatureNameLookup.get(candidate)
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
            val var10000: ClientPlayerEntity = this.getClient().field_1724
            if (var10000 != null) {
               val p: ClientPlayerEntity = var10000
               val rod: Int = this.getFishingRodSlot()
               if (rod != -1 && 0 <= weaponSlot && weaponSlot < 9) {
                  rodSequenceInProgress = true
                  currentKillTargetName = (if (StringsKt.isBlank(wantedMobName)) "Sea Creature" else wantedMobName) as java.lang.String
                  suppressSlotCheckUntilMs = System.currentTimeMillis() + 2500L
                  lastActivityMs = System.currentTimeMillis()
                  val var18: Float = var10000.method_36455()
                  val var19: Float = var10000.method_36454()
                  if (lockMove) {
                     PlayerController.INSTANCE.pressForward(false)
                     PlayerController.INSTANCE.pressBack(false)
                     PlayerController.INSTANCE.pressLeft(false)
                     PlayerController.INSTANCE.pressRight(false)
                     PlayerController.INSTANCE.pressSprint(false)
                     PlayerController.INSTANCE.pressJump(false)
                  }

                  try {
                     this.selectHotbarSlot(weaponSlot)
                     Thread.sleep(155L)
                     if (lookDown || lookDownPitch != null) {
                        this.getClient().execute({ 
                           `$p`.method_36456(`$prevYaw`)
                           `$p`.method_36457(RangesKt.coerceIn(`$lookDownPitch` ?: 89.0F, -89.9F, 89.9F))
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

                        PlayerController.INSTANCE.rightClick()
                        uses++
                        Thread.sleep(intervalMs)
                        lastActivityMs = System.currentTimeMillis()
                     }

                     this.selectHotbarSlot(rod)
                     Thread.sleep(251L)
                     this.getClient().execute({ 
                        `$p`.method_36457(`$prevPitch`)
                        `$p`.method_36456(`$prevYaw`)
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
      if (StringsKt.isBlank(wantedMobName)) {
         return 0
      } else {
         val var10000: ClientWorld = this.getClient().field_1687
         if (var10000 == null) {
            return 0
         } else {
            val var13: ClientPlayerEntity = this.getClient().field_1724
            if (var13 == null) {
               return 0
            } else {
               val me: ClientPlayerEntity = var13
               val wanted: java.lang.String = this.normalizeStandName(wantedMobName)
               val var14: java.lang.Iterable = var10000.method_18112()
               val `$this$count$iv`: java.lang.Iterable = CollectionsKt.toList(var14)
               val var17: Int
               if (`$this$count$iv` is java.util.Collection && (`$this$count$iv` as java.util.Collection).isEmpty()) {
                  var17 = 0
               } else {
                  val `count$iv`: Int = 0

                  for (`element$iv` in `$this$count$iv`) {
                     val entity: Entity = `element$iv` as Entity
                     val var15: Boolean
                     if (`element$iv` as Entity is ArmorStandEntity
                        && ((`element$iv` as Entity) as ArmorStandEntity).method_5805()
                        && !((`element$iv` as Entity) as ArmorStandEntity).method_31481()) {
                        if (entity.method_5858(me as Entity) > 900.0) {
                           var15 = false
                        } else {
                           val var16: FunnyFishing = INSTANCE
                           var var10001: Text = (entity as ArmorStandEntity).method_5797()
                           if (var10001 == null) {
                              var10001 = (entity as ArmorStandEntity).method_5477()
                           }

                           val var18: java.lang.String = var10001.getString()
                           var15 = StringsKt.contains$default(var16.normalizeStandName(var18), wanted, false, 2, null)
                        }
                     } else {
                        var15 = false
                     }

                     if (var15) {
                        if (++`count$iv` < 0) {
                           CollectionsKt.throwCountOverflow()
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
      val var6: java.lang.String = StringsKt.trim(
            Regex("\\s+")
               .replace(
                  StringsKt.replace$default(
                     StringsKt.replace$default(
                        StringsKt.replace$default(StringsKt.replace$default(Regex("§.").replace(raw, ""), "❤", "", false, 4, null), "Â", "", false, 4, null),
                        "/",
                        " ",
                        false,
                        4,
                        null
                     ),
                     ",",
                     "",
                     false,
                     4,
                     null
                  ),
                  " "
               )
         )
         .toString()
         val var10000: Locale = Locale.US
      val var9: java.lang.String = var6.toLowerCase(var10000)
      return var9
   }

   private fun getFishingRodSlot(): Int {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      val var4: PlayerInventory = var10000.method_31548()
      val inv: PlayerInventory = var4
      var i: Int = 0

      while (true) {
         if (i >= 9) {
            return -1
         }

         val var5: ItemStack = inv.method_5438(i)
         if (!var5.method_7960()) {
            if (var5.method_7909() == Items.field_8378) {
               break
            }

            val var6: java.lang.String = var5.method_7964().getString()
            if (StringsKt.contains(var6, "Rod", true)) {
               break
            }
         }

         i++
      }

      return i
   }

   private fun findInHotbarByName(name: String): Int {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      val var5: PlayerInventory = var10000.method_31548()
      val inv: PlayerInventory = var5

      repeat(8) { i ->
         val var6: ItemStack = inv.method_5438(i)
         if (!var6.method_7960()) {
            val var7: java.lang.String = var6.method_7964().getString()
            if (StringsKt.contains(var7, name, true)) {
               return i
            }
         }
      }

      return -1
   }

   private fun selectHotbarSlot(slot: Int) {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 != null) {
         if (0 <= slot && slot < 9) {
            if (var10000.method_31548().method_67532() != slot) {
               var10000.method_31548().method_61496(slot)
               PlayerController.INSTANCE.noteHotbarSwapThisTick()
            }
         }
      }
   }

   private fun shouldAutoSellByEmptySlots(): Boolean {
      return this.countEmptyMainInvSlots() <= 2
   }

   private fun countEmptyMainInvSlots(): Int {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 == null) {
         return 99
      } else {
         val var5: PlayerInventory = var10000.method_31548()
         val inv: PlayerInventory = var5
         var empty: Int = 0

         for (i in 9..35) {
            if (inv.method_5438(i).method_7960()) {
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
         val now: Long = System.currentTimeMillis()
         if (now - lastDeployCheckMs < 450L) {
            return false
         } else {
            lastDeployCheckMs = now
            val excluded: LinkedHashSet = LinkedHashSet()
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

   private fun tryPlaceCorruptionTotem(excluded: Set<class_2338>): Boolean {
      val slot: Int = this.findInHotbarByName("Totem of Corruption")
      if (slot == -1) {
         nextTotemDeployAtMs = System.currentTimeMillis() + 30000L
         return false
      } else {
         val var10000: BlockPos = this.findDeploySpot(excluded)
         if (var10000 == null) {
            val `$this$tryPlaceCorruptionTotem_u24lambda_u2448`: FunnyFishing = this
            nextTotemDeployAtMs = System.currentTimeMillis() + 5000L
            return false
         } else {
            return this.placeDeployableAt(FunnyFishing.DeployableChoice(slot, "Totem of Corruption", 120000L), var10000, "Placing Totem", { placed: BlockPos ->
               lastTotemPlacePos = placed
               nextTotemDeployAtMs = System.currentTimeMillis() + 120000L
               Unit.INSTANCE
            })
         }
      }
   }

   private fun tryPlacePowerOrb(excluded: Set<class_2338>): Boolean {
      val var10000: FunnyFishing.DeployableChoice = this.findBestPowerOrbChoice()
      if (var10000 == null) {
         val `$this$tryPlacePowerOrb_u24lambda_u2450`: FunnyFishing = this
         nextPowerOrbDeployAtMs = System.currentTimeMillis() + 30000L
         return false
      } else {
         val var8: BlockPos = this.findDeploySpot(excluded)
         if (var8 == null) {
            val `$this$tryPlacePowerOrb_u24lambda_u2451`: FunnyFishing = this
            nextPowerOrbDeployAtMs = System.currentTimeMillis() + 5000L
            return false
         } else {
            return this.placeDeployableAt(var10000, var8, "Placing ${var10000.displayName}", { placed: BlockPos ->
               lastPowerOrbPlacePos = placed
               nextPowerOrbDeployAtMs = System.currentTimeMillis() + `$choice`.durationMs
               Unit.INSTANCE
            })
         }
      }
   }

   private fun tryPlaceUmbrella(excluded: Set<class_2338>): Boolean {
      val var10000: FunnyFishing.DeployableChoice = this.findUmbrellaChoice()
      if (var10000 == null) {
         val `$this$tryPlaceUmbrella_u24lambda_u2453`: FunnyFishing = this
         nextUmbrellaDeployAtMs = System.currentTimeMillis() + 30000L
         return false
      } else {
         val var8: BlockPos = this.findDeploySpot(excluded)
         if (var8 == null) {
            val `$this$tryPlaceUmbrella_u24lambda_u2454`: FunnyFishing = this
            nextUmbrellaDeployAtMs = System.currentTimeMillis() + 5000L
            return false
         } else {
            return this.placeDeployableAt(var10000, var8, "Placing ${var10000.displayName}", { placed: BlockPos ->
               lastUmbrellaPlacePos = placed
               nextUmbrellaDeployAtMs = System.currentTimeMillis() + 300000L
               Unit.INSTANCE
            })
         }
      }
   }

   private fun findBestPowerOrbChoice(): jooon.features.fishing.FunnyFishing.DeployableChoice? {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 != null) {
         val var15: PlayerInventory = var10000.method_31548()
         if (var15 != null) {
            val inv: PlayerInventory = var15
            val priorities: java.util.List = CollectionsKt.listOf(
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
               val var16: ItemStack = inv.method_5438(slot)
               if (!var16.method_7960()) {
                  val var17: java.lang.String = var16.method_7964().getString()
                  val itemName: java.lang.String = var17

                  for (var9 in priorities) {
                     val name: java.lang.String = var9.component1() as java.lang.String
                     val duration: Long = (var9.component2() as java.lang.Number).longValue()
                     val priority: Int = (var9.component3() as java.lang.Number).intValue()
                     if (StringsKt.contains(itemName, name, true) && priority > bestPriority) {
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
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 != null) {
         val var5: PlayerInventory = var10000.method_31548()
         if (var5 != null) {
            val inv: PlayerInventory = var5

            repeat(8) { slot ->
               val var6: ItemStack = inv.method_5438(slot)
               if (!var6.method_7960()) {
                  val var7: java.lang.String = var6.method_7964().getString()
                  if (StringsKt.contains(var7, "Umbrella", true)) {
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
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 == null) {
         null
      } else {
         val var11: ClientWorld = this.getClient().field_1687
         if (var11 == null) {
            null
         } else {
            val world: ClientWorld = var11
            val var12: BlockPos = var10000.method_24515()
            val base: BlockPos = var12

            for (var7 in this.collectPrioritizedDeployOffsets(var10000)) {
               val var13: BlockPos = base.method_10069(
                  (var7.component1() as java.lang.Number).intValue(), -1, (var7.component2() as java.lang.Number).intValue()
               )
               if (!excluded.contains(var13) && this.isValidDeployBase(world, var13)) {
                  var13
               }
            }

            null
         }
      }
   }

   fun collectPrioritizedDeployOffsets(player: ClientPlayerEntity): MutableList<Pair<Integer, Integer>> {
      val var10000: Direction = Direction.method_10150((double)player.method_36454())
      val var10: Direction = var10000.method_10160()
      val var11: Direction = var10000.method_10170()
      val var12: Direction = var10000.method_10153()
      val result: java.util.List = ArrayList()
      val seen: java.util.Set = LinkedHashSet()
      collectPrioritizedDeployOffsets$addDir(seen, result, var10, 1)
      collectPrioritizedDeployOffsets$addDir(seen, result, var11, 1)
      collectPrioritizedDeployOffsets$addDir(seen, result, var10, 2)
      collectPrioritizedDeployOffsets$addDir(seen, result, var11, 2)
      collectPrioritizedDeployOffsets$add(seen, result, var10.method_10148() + var10000.method_10148(), var10.method_10165() + var10000.method_10165())
      collectPrioritizedDeployOffsets$add(seen, result, var11.method_10148() + var10000.method_10148(), var11.method_10165() + var10000.method_10165())
      collectPrioritizedDeployOffsets$add(seen, result, var10.method_10148() + var12.method_10148(), var10.method_10165() + var12.method_10165())
      collectPrioritizedDeployOffsets$add(seen, result, var11.method_10148() + var12.method_10148(), var11.method_10165() + var12.method_10165())
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

      result
   }

   fun isValidDeployBase(world: ClientWorld, posUnder: BlockPos): Boolean {
      var var10000: BlockState = world.method_8320(posUnder)
      if (var10000.method_26215()) {
         false
      } else if (!var10000.method_26227().method_15769()) {
         false
      } else {
         var10000 = world.method_8320(posUnder.method_10084())
         if (!var10000.method_26215()) {
            false
         } else if (!var10000.method_26227().method_15769()) {
            false
         } else {
            val var15: Array<Any> = arrayOf(Direction.field_11043, Direction.field_11035, Direction.field_11034, Direction.field_11039)
            var var8: Int = 0
            val var9: Int = var15.length

            while (true) {
               if (var8 >= var9) {
                  var19 = false
                  break
               }

               val var17: BlockPos = posUnder.method_10093((Direction)var15[var8]).method_10084()
               var10000 = world.method_8320(var17)
               if (var10000.method_26215() && world.method_8316(var17).method_15769()) {
                  var19 = true
                  break
               }

               var8++
            }

            var19
         }
      }
   }

   fun placeDeployableAt(choice: FunnyFishing.DeployableChoice, target: BlockPos, actionLabel: java.lang.String, onPlaced: (BlockPos?) -> Unit): Boolean {
      if (!masterEnabled || hardPausedForMelee) {
         false
      } else if (placingDeployable) {
         false
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
                  val backPos: BlockPos = mainLookAtBlock
                  var var10000: Rotator = Rotator.INSTANCE
                  var var10001: Vec3d = Vec3d.method_24953(`$target` as Vec3i)
                  Rotator.lookAt$default(var10000, var10001, 0.1F, 540.0F, 0.0F, null, 24, null)

                  while (Rotator.INSTANCE.isActive) {
                     Thread.sleep(10L)
                  }

                  INSTANCE.selectHotbarSlot(`$choice`.slot)
                  Thread.sleep(200L)
                  INSTANCE.getClient()
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
                  val rod: Int = INSTANCE.getFishingRodSlot()
                  if (rod != -1 && masterEnabled && !hardPausedForMelee) {
                     INSTANCE.selectHotbarSlot(rod)
                     Thread.sleep(300L)
                     if (backPos != null) {
                        var10000 = Rotator.INSTANCE
                        var10001 = Vec3d.method_24953(backPos as Vec3i)
                        Rotator.lookAt$default(var10000, var10001, 0.1F, 540.0F, 0.0F, null, 24, null)
                     }

                     while (Rotator.INSTANCE.isActive) {
                        Thread.sleep(10L)
                     }
                  }
               } finally {
                  suppressAntiAfkRotation = false
                  placingDeployable = false
                  deployableActionLabel = ""
               }

               Unit.INSTANCE
            },
            22,
            null
         )
         true
      }
   }

   private fun castWithVerify(maxWaitMs: Long = 700L, retryDelayMs: Long = 150L, maxAttempts: Int = 1) {
      if (masterEnabled && !hardPausedForMelee) {
         if (this.getClient().field_1724 != null) {
            if (!castVerifyInProgress) {
               if (!rodSequenceInProgress) {
                  castVerifyInProgress = true
                  rodSequenceInProgress = true
                  ThreadsKt.thread$default(true, false, null, "FunnyFishing-CastVerify", 0, { 
                     try {
                        INSTANCE.runCastWithVerify(`$maxWaitMs`, `$retryDelayMs`, `$maxAttempts`)
                     } finally {
                        castVerifyInProgress = false
                        rodSequenceInProgress = false
                     }

                     Unit.INSTANCE
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
            PlayerController.INSTANCE.rightClick()
            lastCastMs = System.currentTimeMillis()
            lastActivityMs = lastCastMs
            exclaimSeenThisCast = false
            if (this.awaitCastConfirmation(maxWaitMs)) {
               this.armBiteAfterDelay()
               return
            }

            Thread.sleep(retryDelayMs)
            if (masterEnabled && !hardPausedForMelee) {
               continue
            }

            return
         }

         return
      }
   }

   private fun castOnGoldenFish() {
      if (masterEnabled && !hardPausedForMelee) {
         if (this.getClient().field_1724 != null) {
            if (this.isRodOut()) {
               goldenFishPendingCast = false
            } else {
               goldenFishAimStartedMs = if (goldenFishAimStartedMs == 0L) System.currentTimeMillis() else goldenFishAimStartedMs
               PlayerController.INSTANCE.rightClick()
               lastCastMs = System.currentTimeMillis()
               lastActivityMs = lastCastMs
               goldenFishPendingCast = false
               exclaimSeenThisCast = false
               ThreadsKt.thread$default(false, false, null, null, 0, lambda_61@{ 
                  if (!INSTANCE.awaitCastConfirmation(700L)) {
                     Thread.sleep(200L)
                     if (!masterEnabled || hardPausedForMelee) {
                        return@lambda_61 Unit.INSTANCE
                     }

                     PlayerController.INSTANCE.rightClick()
                     INSTANCE.awaitCastConfirmation(700L)
                  }

                  INSTANCE.armBiteAfterDelay()
                  return@lambda_61 Unit.INSTANCE
               }, 31, null)
            }
         }
      }
   }

   private fun awaitCastConfirmation(maxWaitMs: Long = 700L): Boolean {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 == null) {
         return false
      } else {
         val p: ClientPlayerEntity = var10000
         val start: Long = System.currentTimeMillis()
         val throwMark: Long = lastThrowHeardMs

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
         val targetX: Pair = if (PersistentState.fishingHudInitDone)
            TuplesKt.to(PersistentState.fishingHudX, PersistentState.fishingHudY)
            else
            this.defaultFishingHudPosition()
            var var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("fishingHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay(
                  "fishingHud",
                  "Fishing HUD",
                  (targetX.getFirst() as java.lang.Number).intValue(),
                  (targetX.getSecond() as java.lang.Number).intValue(),
                  176,
                  76
               )
            }

         var10000.renderFunction = { context: DrawContext, x: Int, y: Int, var3: Float ->
            INSTANCE.renderFishingHud(context, x, y)
            Unit.INSTANCE
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.fishingHudX = x
            PersistentState.fishingHudY = y
            PersistentState.fishingHudInitDone = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }
         var10000.register()
         fishingHudReady = true
      }

      if (!fishingHudAppliedSavedPosition) {
         val var3: Int = PersistentState.fishingHudX
         val var4: Int = PersistentState.fishingHudY
         val var5: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("fishingHud")
         if (var5 != null) {
            var5.setPositionSilently(var3, var4)
         }

         fishingHudAppliedSavedPosition = true
      }
   }

   fun renderFishingHud(context: DrawContext, x: Int, y: Int) {
      val preview: Boolean = !masterEnabled && this.getClient().field_1755 is MovableOverlayScreen
      if (masterEnabled || preview) {
         val lines: java.util.List = CollectionsKt.listOf(
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
            var var24: Int = INSTANCE.getClient().field_1772.method_1727(panelHeight.next() as java.lang.String)

            while (panelHeight.hasNext()) {
               val var27: Int = INSTANCE.getClient().field_1772.method_1727(panelHeight.next() as java.lang.String)
               if (var24 < var27) {
                  var24 = var27
               }
            }

            val panelWidth: Int = Math.max(270, Math.max(this.getClient().field_1772.method_1727("JR Fishing") + 24, var24 + 20))
            val var23: Int = 14 + this.getClient().field_1772.field_2000 + 10 + lines.size() * (this.getClient().field_1772.field_2000 + 3) + 8
            val var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("fishingHud")
            if (var10000 != null) {
               var10000.width = panelWidth
               var10000.height = var23
            }

            val textColor: Int = -8458079
            val textShadow: Int = -14657478
            context.method_25294(x, y, x + panelWidth, y + var23, -871952102)
            context.method_25294(x + 2, y + 2, x + panelWidth - 2, y + var23 - 2, -871688414)
            context.method_25294(x + 3, y + 3, x + panelWidth - 3, y + 18, -1441781446)
            context.method_73198(x, y, panelWidth, var23, -12994817)
            context.method_73198(x + 1, y + 1, panelWidth - 2, var23 - 2, -12265606)
            this.renderFishingHudSweep(context, x + 3, y + 3, panelWidth - 6, 15)
            this.drawFishingHudText(
               context, "JR Fishing", x + (panelWidth - this.getClient().field_1772.method_1727("JR Fishing")) / 2, y + 5, -7477249, -14787203
            )
            var lineY: Int = y + 24

            for (line in lines) {
               this.drawFishingHudText(context, line, x + 8, lineY, textColor, textShadow)
               lineY += this.getClient().field_1772.field_2000 + 3
            }
         }
      }
   }

   private fun currentFishingHudActionRaw(): String {
      return if (!masterEnabled)
         "Disabled"
         else
         (
            if (hardPausedForMelee)
               "Paused for melee"
               else
               (
                  if (placingDeployable)
                     (if (!StringsKt.isBlank(deployableActionLabel)) deployableActionLabel else "Placing deployable")
                     else
                     (
                        if (goldenFishFocus && goldenFishPendingCast)
                           "Golden Fish cast"
                           else
                           (
                              if (goldenFishFocus)
                                 "Golden Fish tracking"
                                 else
                                 (
                                    if (killing && !StringsKt.isBlank(currentKillTargetName))
                                       "Killing"
                                       else
                                       (
                                          if (killing)
                                             "Auto kill"
                                             else
                                             (if (recastQueued) "Reeling" else (if (this.isRodOut()) "Waiting for bite" else "Casting rod"))
                                       )
                                 )
                           )
                     )
               )
         )
      }

   private fun currentFishingTargetLabelRaw(): String {
      return if (goldenFishFocus) "Golden Fish" else (if (!StringsKt.isBlank(currentKillTargetName)) currentKillTargetName else "--")
   }

   private fun currentFishingHudActionStable(preview: Boolean): String {
      if (preview) {
         return "Waiting for bite"
      } else {
         val now: Long = System.currentTimeMillis()
         val raw: java.lang.String = this.currentFishingHudActionRaw()
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
         val now: Long = System.currentTimeMillis()
         val raw: java.lang.String = this.currentFishingTargetLabelRaw()
         if (raw == fishingHudTargetCached || now - fishingHudTargetUpdatedAtMs >= 220L) {
            fishingHudTargetCached = raw
            fishingHudTargetUpdatedAtMs = now
         }

         return fishingHudTargetCached
      }
   }

   private fun resetFishingHudCache() {
      val now: Long = System.currentTimeMillis()
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
         val var16: Array<Any> = arrayOf(RangesKt.coerceAtLeast(Config.fishingSessionMinutes, 1))
         val var18: java.lang.String = java.lang.String.format("%02dm 00s", Arrays.copyOf(var16, var16.length))
         return var18
      } else if (masterEnabled && startedAtMs > 0L) {
         val totalSeconds: Long = Math.max(
               0L, (long)RangesKt.coerceAtLeast(Config.fishingSessionMinutes, 1) * 60000L - (System.currentTimeMillis() - startedAtMs)
            )
            / 1000L
            val minutes: Long = totalSeconds / 60L
         val seconds: Long = totalSeconds % 60L
         val var13: Locale = Locale.US
         val var17: Array<Any> = arrayOf(minutes, seconds)
         val var10000: java.lang.String = java.lang.String.format(var13, "%02dm %02ds", Arrays.copyOf(var17, var17.length))
         return var10000
      } else {
         return "00m 00s"
      }
   }

   private fun defaultFishingHudPosition(): Pair<Int, Int> {
      return TuplesKt.to(10, 10)
   }

   fun drawFishingHudText(context: DrawContext, text: java.lang.String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.method_51439(this.getClient().field_1772, Text.method_43470(text) as Text, x + 1, y + 1, shadowColor, false)
      context.method_51439(this.getClient().field_1772, Text.method_43470(text) as Text, x, y, color, false)
   }

   fun renderFishingHudSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {
      val sweepWidth: Int = Math.max(18, width / 5)
      val sweepX: Int = x - sweepWidth + (int)((width + sweepWidth) * ((float)(System.currentTimeMillis() % 2400L) / (float)2400L))
      val left: Int = RangesKt.coerceAtLeast(sweepX, x)
      val right: Int = RangesKt.coerceAtMost(sweepX + sweepWidth, x + width)
      if (right > left) {
         context.method_25294(left, y, right, y + height, 860350151)
      }
   }

   private fun message(msg: String) {
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 != null) {
         var10000.method_7353(Text.method_43470("${JooonReimagined.Companion.PREFIX_CLEAN}$msg") as Text, false)
      }
   }

   @JvmStatic
   public fun onParticle(x: Double, y: Double, z: Double, typeKey: String, count: Int, speed: Float) {
   }

   @JvmStatic
   public fun onSound(key: String, x: Double, y: Double, z: Double) {
      if (!StringsKt.endsWith$default(key, "entity.fishing_bobber.throw", false, 2, null)) {
         if (masterEnabled && Config.fishingEnabled && !hardPausedForMelee && biteArmed) {
            if (System.currentTimeMillis() - lastCastMs >= 400L) {
               if (playersBobber != null) {
                  val var8: FishingBobberEntity = playersBobber
                  if (StringsKt.endsWith$default(key, "entity.fishing_bobber.splash", false, 2, null) && var8.method_5649(x, y, z) < 25.0) {
                     INSTANCE.triggerCatch()
                  }
               }
            }
         }
      } else {
         val bobber: ClientPlayerEntity = INSTANCE.getClient().field_1724
         if (bobber == null || bobber.method_5649(x, y, z) <= 16.0) {
            lastThrowHeardMs = System.currentTimeMillis()
         }
      }
   }

   @JvmStatic
   public fun onBobberVelocity(entityId: Int, vx: Double, vy: Double, vz: Double) {
      if (masterEnabled && Config.fishingEnabled && !hardPausedForMelee && biteArmed) {
         if (System.currentTimeMillis() - lastCastMs >= 400L) {
            if (playersBobber != null) {
               if (playersBobber.method_5628() == entityId) {
                  if (vy < -0.3) {
                     INSTANCE.triggerCatch()
                  }
               }
            }
         }
      }
   }

   @JvmStatic
   public fun onHookTick(ownerUuid: UUID?, hookCountdown: Int) {
      if (masterEnabled && Config.fishingEnabled && !hardPausedForMelee && biteArmed) {
         if (System.currentTimeMillis() - lastCastMs >= 400L) {
            val var10000: ClientPlayerEntity = INSTANCE.getClient().field_1724
            if (var10000 != null) {
               if (ownerUuid != null && ownerUuid == var10000.method_5667() && hookCountdown > 0) {
                  INSTANCE.triggerCatch()
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
            val var10000: ClientPlayerEntity = this.getClient().field_1724
            if (var10000 != null) {
               val p: ClientPlayerEntity = var10000
               val rod: Int = this.getFishingRodSlot()
               if (rod != -1 && 0 <= weaponSlot && weaponSlot < 9) {
                  rodSequenceInProgress = true
                  suppressSlotCheckUntilMs = System.currentTimeMillis() + 2000L
                  lastActivityMs = System.currentTimeMillis()
                  val prevPitch: Float = var10000.method_36455()
                  val prevYaw: Float = var10000.method_36454()
                  if (lockMove) {
                     PlayerController.INSTANCE.pressForward(false)
                     PlayerController.INSTANCE.pressBack(false)
                     PlayerController.INSTANCE.pressLeft(false)
                     PlayerController.INSTANCE.pressRight(false)
                     PlayerController.INSTANCE.pressSprint(false)
                     PlayerController.INSTANCE.pressJump(false)
                  }

                  try {
                     this.selectHotbarSlot(weaponSlot)
                     Thread.sleep(155L)
                     if (lookDown || lookDownPitch != null) {
                        this.getClient().execute({ 
                           `$p`.method_36456(`$prevYaw`)
                           `$p`.method_36457(RangesKt.coerceIn(`$lookDownPitch` ?: 89.0F, -89.9F, 89.9F))
                        })
                        Thread.sleep(60L)
                     }

                     repeat(clicks) { var12 ->
                        if (!masterEnabled || hardPausedForMelee) {
                           return
                        }

                        PlayerController.INSTANCE.rightClick()
                        Thread.sleep(intervalMs)
                     }

                     Thread.sleep(400L)
                     this.selectHotbarSlot(rod)
                     Thread.sleep(251L)
                     this.getClient().execute({ 
                        `$p`.method_36457(`$prevPitch`)
                        `$p`.method_36456(`$prevYaw`)
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
            PlayerController.INSTANCE.rightClick()
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

   @JvmStatic
   fun `collectPrioritizedDeployOffsets$add`(seen: MutableSet<Pair<Integer, Integer>>, result: MutableList<Pair<Integer, Integer>>, dx: Int, dz: Int) {
      val key: Pair = TuplesKt.to(dx, dz)
      if (seen.add(key)) {
         result.add(key)
      }
   }

   @JvmStatic
   fun `collectPrioritizedDeployOffsets$addDir`(
      seen: MutableSet<Pair<Integer, Integer>>, result: MutableList<Pair<Integer, Integer>>, dir: Direction, dist: Int
   ) {
      collectPrioritizedDeployOffsets$add(seen, result, dir.method_10148() * dist, dir.method_10165() * dist)
   }

   private data class DeployableChoice(slot: Int, displayName: String, durationMs: Long) {
      public final val slot: Int
      public final val displayName: String
      public final val durationMs: Long

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

      public fun copy(slot: Int = this.slot, displayName: String = this.displayName, durationMs: Long = this.durationMs): jooon.features.fishing.FunnyFishing.DeployableChoice {
         return FunnyFishing.DeployableChoice(slot, displayName, durationMs)
      }

      public override fun toString(): String {
         return "DeployableChoice(slot=${this.slot}, displayName=${this.displayName}, durationMs=${this.durationMs})"
      }

      public override fun hashCode(): Int {
         return (Integer.hashCode(this.slot) * 31 + this.displayName.hashCode()) * 31 + java.lang.Long.hashCode(this.durationMs)
      }

      public override operator fun equals(other: Any?): Boolean {
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

   public enum class FishingType {
      WATER,
      LAVA;

      @JvmStatic
      fun getEntries(): EnumEntries<FunnyFishing.FishingType> {
         $ENTRIES
      }
   }
}
