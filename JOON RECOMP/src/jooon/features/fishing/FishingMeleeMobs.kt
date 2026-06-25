package jooon.features.fishing

import java.util.Locale
import jooon.config.Config
import jooon.mixins.InventoryAccessor
import jooon.util.PlayerController
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView

@SourceDebugExtension(["SMAP\nFishingMeleeMobs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FishingMeleeMobs.kt\njooon/features/fishing/FishingMeleeMobs\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,825:1\n1#2:826\n*E\n"])
public object FishingMeleeMobs {
   public final var isBusy: Boolean
      private set

   @Volatile
   @JvmStatic
   private Vec3d anchorPos;
   private final var anchorYaw: Float
   private final var anchorPitch: Float
   private final var isGangEncounter: Boolean
   private final var gangSweepUntilMs: Long
   private final var poseAlignUntilMs: Long
   private final var settleUntilMs: Long
   private final var settleBestD2: Double = java.lang.Double.POSITIVE_INFINITY
   private final var settleLastD2: Double = java.lang.Double.POSITIVE_INFINITY
   private final var phase: jooon.features.fishing.FishingMeleeMobs.Phase = FishingMeleeMobs.Phase.IDLE
   private final var mobWanted: String?
   private final var wasFishing: Boolean
   private final var prevHotbar: Int
   @JvmStatic
   private ArmorStandEntity standHint;
   @JvmStatic
   private LivingEntity target;
   private final var armUntilMs: Long
   private final var acquireUntilMs: Long
   private final var lastScanMs: Long
   private final var lastAttackMs: Long
   private final var lastProgressMs: Long
   @JvmStatic
   private Vec3d lastPos;
   private final var jumpHoldUntilMs: Long
   private final var nextJumpAllowedMs: Long
   private final var jumpPenaltyMs: Long
   private final var pendingResume: Boolean
   private final var resumeAtMs: Long
   private final var pendingRecast: Boolean
   private final var recastAtMs: Long
   private final var noReengageUntilMs: Long
   private const val ARM_MS: Int = 300
   private const val ACQUIRE_MS: Int = 800
   private const val SCAN_EVERY_MS: Int = 140
   private const val SEARCH_RADIUS: Double = 64.0
   private const val APPROACH_DIST: Double = 2.35
   private const val ATTACK_RANGE: Double = 3.0
   private const val PROGRESS_TIMEOUT_MS: Long = 700L
   private const val STEP_CHECK_DIST: Double = 0.62
   private const val STEP_HEIGHT_TRIGGER: Double = 0.95
   private const val MAX_TURN_PER_TICK_DEG: Float = 48.0F
   private const val MAX_PITCH_PER_TICK_DEG: Float = 36.0F
   private const val JUMP_HOLD_WATER_MS: Long = 120L
   private const val JUMP_HOLD_STEP_MS: Long = 130L
   private const val JUMP_BASE_COOLDOWN_MS: Long = 180L
   private const val JUMP_PENALTY_STEP_MS: Long = 80L
   private const val JUMP_PENALTY_DECAY_MS: Long = 40L
   private const val JUMP_PENALTY_MAX_MS: Long = 600L
   private const val RESUME_DELAY_MS: Long = 600L
   private const val REENGAGE_GUARD_MS: Long = 900L
   private final var doubleHookWindowUntilMs: Long
   private final var doubleHookRemaining: Int

   private final var guiEnableCheck: (String) -> Boolean = { var0: java.lang.String ->
      false
   }

   private final val COLOR_RX: Regex = Regex("§.")
   private final val JSON_TEXT_PREFIX: String = "{\"text\":\""
   private final val LEVEL_TAG_RX: Regex = Regex("\\[\\s*(Lv|Lvl|Level)\\s*\\d+\\s*]", RegexOption.IGNORE_CASE)
   private final val HEARTS_RX: Regex = Regex("[❤♥]")
   private final val COMMA_RX: Regex = Regex(",")

   private fun isAxePreferred(wanted: String): Boolean {
      val var10000: java.lang.String = this.cleanLabel(wanted).toLowerCase(Locale.ROOT)
      return StringsKt.contains$default(var10000, "ent", false, 2, null)
   }

   private fun findAxeSlot(): Int {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val var4: PlayerInventory = var10000.method_31548()
         if (var4 != null) {
            val inv: PlayerInventory = var4

            repeat(8) { i ->
               val var5: ItemStack = inv.method_5438(i)
               if (!var5.method_7960()) {
                  val var6: java.lang.String = var5.method_7964().getString()
                  if (StringsKt.contains(var6, "axe", true)) {
                     return i
                  }
               }
            }

            return -1
         }
      }

      return -1
   }

   private fun weaponSlotFor(wanted: String): Int {
      if (this.isAxePreferred(wanted)) {
         val axe: Int = this.findAxeSlot()
         if (axe != -1) {
            return axe
         }
      }

      return this.meleeSlot0to8()
   }

   public fun willMeleeForMessage(plain: String): Boolean {
      if (System.currentTimeMillis() < noReengageUntilMs) {
         return false
      } else {
         label21@
         if (plain == "A gang of Liltads!") {
            return this.shouldMelee("Liltad")
         } else {
            val var10000: java.lang.String = FishingStrings.INSTANCE.seaCreatureMessages.get(plain)
            return var10000 != null && this.shouldMelee(var10000)
         }
      }
   }

   public fun tryEngageForMessage(plain: String): Boolean {
      val now: Long = System.currentTimeMillis()
      if (plain == "It's a Double Hook!") {
         if (now <= doubleHookWindowUntilMs) {
            doubleHookRemaining = Math.max(doubleHookRemaining, 2)
         }

         return false
      } else {
         doubleHookWindowUntilMs = now + 1200
         if (plain == "A gang of Liltads!") {
            if (now < noReengageUntilMs) {
               return false
            } else if (this.shouldMelee("Liltad") && !isBusy) {
               isGangEncounter = true
               gangSweepUntilMs = now + 1000L
               this.startEngage("Liltad")
               return true
            } else {
               return false
            }
         } else {
            val var10000: java.lang.String = FishingStrings.INSTANCE.seaCreatureMessages.get(plain)
            if (var10000 == null) {
               return false
            } else if (now < noReengageUntilMs) {
               return false
            } else if (this.shouldMelee(var10000) && !isBusy) {
               this.startEngage(var10000)
               return true
            } else {
               return false
            }
         }
      }
   }

   private fun angleErrorDeg(a: Float, b: Float): Float {
      return Math.abs(MathHelper.method_15393(a - b))
   }

   public fun forceAbortAndDisableFishing() {
      if (this.getMc().field_1724 != null) {
         this.stopAllMovement()
         this.selectHotbarSlot(prevHotbar)
         this.setFishingEnabled(false)
         noReengageUntilMs = System.currentTimeMillis() + 900L
         this.resetState()
      }
   }

   public fun onAutoFishingStart() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         anchorPos = var10000.method_73189()
         anchorYaw = var10000.method_36454()
         anchorPitch = var10000.method_36455()
      }
   }

   public fun onAutoFishingStop() {
      anchorPos = null
   }

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   private final val ATTACK_COOLDOWN_MS: Long
      private final get() {
         return (long)(1000.0 / this.meleeCps())
      }


   private fun canJump(now: Long): Boolean {
      return now >= nextJumpAllowedMs
   }

   private fun requestJump(now: Long, holdMs: Long) {
      jumpHoldUntilMs = Math.max(jumpHoldUntilMs, now + holdMs)
      nextJumpAllowedMs = now + 180L + jumpPenaltyMs
      jumpPenaltyMs = RangesKt.coerceAtMost(jumpPenaltyMs + 80L, 600L)
   }

   private fun decayJumpPenalty() {
      jumpPenaltyMs = RangesKt.coerceAtLeast(jumpPenaltyMs - 40L, 0L)
   }

   private fun startEngage(wantedMobName: String) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         if (anchorPos == null) {
            anchorPos = var10000.method_73189()
            anchorYaw = var10000.method_36454()
            anchorPitch = var10000.method_36455()
         }

         isBusy = true
         val var3: PlayerInventory = var10000.method_31548()
         prevHotbar = (var3 as InventoryAccessor).selected
         wasFishing = this.masterFishingEnabled()
         FunnyFishing.INSTANCE.pauseForMeleeImmediate()
         this.setFishingEnabled(false)
         mobWanted = wantedMobName
         standHint = null
         target = null
         armUntilMs = System.currentTimeMillis() + 300
         acquireUntilMs = 0L
         lastScanMs = 0L
         lastAttackMs = 0L
         lastProgressMs = System.currentTimeMillis()
         lastPos = null
         jumpHoldUntilMs = 0L
         nextJumpAllowedMs = 0L
         jumpPenaltyMs = 0L
         phase = FishingMeleeMobs.Phase.ARM_DELAY
      }
   }

   private fun isSameEncounter(label: String, wanted: String): Boolean {
      val var10000: java.lang.String = this.cleanLabel(label).toLowerCase(Locale.ROOT)
      val var6: java.lang.String = wanted.toLowerCase(Locale.ROOT)
      return if (StringsKt.contains$default(var6, "liltad", false, 2, null) || StringsKt.contains$default(var6, "tadgang", false, 2, null) || isGangEncounter)
         StringsKt.contains$default(var10000, "liltad", false, 2, null)
            || StringsKt.contains$default(var10000, "tadgang", false, 2, null)
            || StringsKt.startsWith$default(var10000, "tad", false, 2, null)
         else
         StringsKt.contains$default(var10000, var6, false, 2, null)
      }

   fun facingAligned(p: PlayerEntity): Boolean {
      this.angleErrorDeg(p.method_36454(), anchorYaw) <= 0.6F && Math.abs(p.method_36455() - anchorPitch) <= 0.8F
   }

   fun findNextTadShard(): LivingEntity {
      run label27@{
         var var10000: ArmorStandEntity = this.findStandByNameLoose("Tadgang")
         if (var10000 != null) {
            val var11: LivingEntity = INSTANCE.findLivingBelow(var10000)
            if (var11 != null) {
               var11
            }
         }

         var10000 = this.findStandByNameLoose("Liltad")
         if (var10000 != null) {
            val var13: LivingEntity = INSTANCE.findLivingBelow(var10000)
            if (var13 != null) {
               var13
            }
         }

         val var14: LivingEntity = this.findNearestByEncounter("Tadgang")
         var14 ?: this.findNearestByEncounter("Liltad")
      }
   }

   private fun snapToAnchorFacing() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var10000.method_36456(anchorYaw)
         var10000.method_36457(anchorPitch)
         var10000.method_36456(anchorYaw)
         var10000.method_36457(anchorPitch)
      }
   }

   fun findNearestByEncounter(wanted: java.lang.String): LivingEntity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var13: ClientPlayerEntity = this.getMc().field_1724
         if (var13 == null) {
            null
         } else {
            val me: ClientPlayerEntity = var13
            var best: LivingEntity = null
            var bestD: Double = java.lang.Double.MAX_VALUE
            val var14: java.lang.Iterable = var10000.method_18112()

            for (var15 in CollectionsKt.toList(var14)) {
               val e: Entity = var15 as Entity
               if (var15 as Entity is LivingEntity
                  && (var15 as Entity) !is ArmorStandEntity
                  && ((var15 as Entity) as LivingEntity).method_5805()
                  && !((var15 as Entity) as LivingEntity).method_31481()
                  && !((var15 as Entity).method_73189().method_1025(me.method_73189()) > 4096.0)) {
                  var var16: Text = (e as LivingEntity).method_5797()
                  if (var16 == null) {
                     var16 = (e as LivingEntity).method_5477()
                  }

                  val var17: java.lang.String = var16.getString()
                  if (var17 != null && this.isSameEncounter(var17, wanted)) {
                     val d: Double = me.method_5858(e)
                     if (d < bestD) {
                        bestD = d
                        best = e as LivingEntity
                     }
                  }
               }
            }

            best
         }
      }
   }

   private fun hasRemainingClusterTargets(wanted: String, radius: Double = 18.0): Boolean {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         return false
      } else {
         val var10: ClientPlayerEntity = this.getMc().field_1724
         if (var10 == null) {
            return false
         } else {
            val me: ClientPlayerEntity = var10
            val var11: java.lang.Iterable = var10000.method_18112()

            for (var12 in CollectionsKt.toList(var11)) {
               val e: Entity = var12 as Entity
               if (var12 as Entity is LivingEntity
                  && (var12 as Entity) !is ArmorStandEntity
                  && ((var12 as Entity) as LivingEntity).method_5805()
                  && !((var12 as Entity) as LivingEntity).method_31481()
                  && !((var12 as Entity).method_73189().method_1025(me.method_73189()) > radius * radius)) {
                  var var13: Text = (e as LivingEntity).method_5797()
                  if (var13 == null) {
                     var13 = (e as LivingEntity).method_5477()
                  }

                  val var14: java.lang.String = var13.getString()
                  if (var14 != null && this.isSameEncounter(var14, wanted)) {
                     return true
                  }
               }
            }

            return false
         }
      }
   }

   public fun onTick() {
      val now: Long = System.currentTimeMillis()
      if (!isBusy && pendingResume && now >= resumeAtMs) {
         pendingResume = false

         try {
            this.snapToAnchorFacing()
            this.setFishingEnabled(true)
            FunnyFishing.INSTANCE.resumeAfterMelee()
         } catch (var14: java.lang.Throwable) {
         }

         pendingRecast = true
         recastAtMs = now + this.recastDelayAfterResumeMs()
      }

      if (!isBusy && pendingRecast && now >= recastAtMs) {
         pendingRecast = false

         try {
            FunnyFishing.INSTANCE.recastAfterMelee()
         } catch (var13: java.lang.Throwable) {
         }
      }

      if (isBusy) {
         val var10000: ClientPlayerEntity = this.getMc().field_1724
         if (var10000 != null) {
            this.decayJumpPenalty()
PlayerController.INSTANCE.pressJump(now < jumpHoldUntilMs)
            when (FishingMeleeMobs.WhenMappings.$EnumSwitchMapping$0[phase.ordinal()]) {
               1 -> {
                  if (now >= armUntilMs) {
                     val var10002: java.lang.String = mobWanted
                     this.selectHotbarSlot(this.weaponSlotFor(var10002))
                     acquireUntilMs = now + 800
                     phase = FishingMeleeMobs.Phase.ACQUIRE
                  }
               }
               2 -> {
                  if (now - lastScanMs >= 140L) {
                     run label330@{
                        lastScanMs = now
                        val var41: java.lang.String = mobWanted
                        standHint = this.findStandByNameLoose(var41)
                        if (standHint != null) {
                           var42 = INSTANCE.findLivingBelow(standHint)
                           if (var42 != null) {
                              return@label330
                           }
                        }

                        var42 = this.findNearestByEncounter(var41)
                     }

                     target = var42
                  }

                  if (target != null) {
                     val var43: LivingEntity = target
                     if (var43.method_5805()) {
                        phase = FishingMeleeMobs.Phase.APPROACH
                        break
                     }
                  }

                  if (now >= acquireUntilMs) {
                     phase = FishingMeleeMobs.Phase.RETURN
                  }
                  break
               }
               3 -> {
                  val var16: LivingEntity = target
                  if (target == null || !target.method_5805() || var16.method_31481()) {
                     phase = FishingMeleeMobs.Phase.RETURN
                     return
                  }

                  this.aimAtAndSmooth(var10000 as PlayerEntity, var16)
                  val var19: Float = var10000.method_5739(var16 as Entity)
                  if (var19 > 2.35) {
                     moveForwardWithStep$default(this, var10000 as PlayerEntity, now, false, 4, null)
                  } else {
                     this.stopForward()
                     if (var19 <= 3.0) {
                        phase = FishingMeleeMobs.Phase.ATTACK
                     }
                  }
                  break
               }
               4 -> {
                  var var15: LivingEntity
                  var var18: ArmorStandEntity
                  run label345@{
                     var15 = target
                     var18 = standHint
                     if (standHint != null) {
                        val var25: ArmorStandEntity = standHint
                        var var33: Text = standHint.method_5797()
                        if (var33 == null) {
                           var33 = var25.method_5477()
                        }

                        val itx: java.lang.String = if (var33 != null) var33.getString() else null
                        if (itx != null) {
                           var35 = INSTANCE.extractFrontHp(itx)
                           return@label345
                        }
                     }

                     var35 = null
                  }

                  if (target == null || !target.method_5805() || var15.method_31481() || var18 != null && (var18.method_31481() || var35 != null && var35 <= 0)
                     )
                   {
                     PlayerController.INSTANCE.pressLeftMouse(false)
                     standHint = null
                     target = null
                     lastScanMs = 0L
                     val var22: Long = System.currentTimeMillis()
                     if (isGangEncounter || mobWanted != null && StringsKt.contains(mobWanted, "liltad", true)) {
                        val var28: LivingEntity = this.findNextTadShard()
                        if (var28 != null && var28.method_5805()) {
                           isGangEncounter = true
                           gangSweepUntilMs = var22 + 1000L
                           target = var28
                           standHint = null
                           acquireUntilMs = var22 + 800
                           phase = FishingMeleeMobs.Phase.ACQUIRE
                           return
                        }

                        if (var22 < gangSweepUntilMs) {
                           acquireUntilMs = var22 + 800
                           phase = FishingMeleeMobs.Phase.ACQUIRE
                           return
                        }

                        isGangEncounter = false
                     }

                     var var40: java.lang.String = mobWanted
                     if (mobWanted == null) {
                        var40 = ""
                     }

                     if (doubleHookRemaining <= 1 && !hasRemainingClusterTargets$default(this, var40, 0.0, 2, null)) {
                        doubleHookRemaining = 0
                        phase = FishingMeleeMobs.Phase.RETURN
                     } else {
                        if (doubleHookRemaining > 0) {
                           doubleHookRemaining--
                        }

                        acquireUntilMs = var22 + 800
                        phase = FishingMeleeMobs.Phase.ACQUIRE
                     }

                     return
                  }

                  val var21: Float = var10000.method_5739(var15 as Entity)
                  this.aimAtAndSmooth(var10000 as PlayerEntity, var15)
                  if (var21 > 2.35) {
                     phase = FishingMeleeMobs.Phase.APPROACH
                  } else if (now - lastAttackMs >= this.ATTACK_COOLDOWN_MS) {
                     var10000.method_6104(Hand.field_5808)
                     PlayerController.INSTANCE.tapLeftMouse(26L)
                     lastAttackMs = now
                  }

                  run label361@{
                     val var36: Vec3d = var10000.method_73189()
                     if (lastPos != null) {
                        val var10001: Vec3d = lastPos
                        if (!(var36.method_1025(var10001) > 4.5E-4)) {
                           if (now - lastProgressMs > 700L) {
                              this.nudge()
                              lastProgressMs = now
                           }
                           return@label361
                        }
                     }

                     lastPos = var36
                     lastProgressMs = now
                  }

                  if (now / 250 % 2L == 0L) {
                     if (!isGangEncounter && (mobWanted == null || !StringsKt.contains(mobWanted, "liltad", true))) {
                        var var38: java.lang.String = mobWanted
                        if (mobWanted == null) {
                           var38 = "Liltad"
                        }

                        run label365@{
                           if (standHint != null) {
                              var39 = INSTANCE.findLivingBelow(standHint)
                              if (var39 != null) {
                                 return@label365
                              }
                           }

                           var39 = this.findNearestByEncounter(var38)
                        }

                        target = var39
                     } else {
                        var var37: LivingEntity = this.findNextTadShard()
                        if (var37 == null) {
                           var37 = target
                        }

                        target = var37
                     }
                  }

                  if (this.isInWater(var10000 as PlayerEntity) && this.canJump(now)) {
                     this.requestJump(now, 120L)
                  }
                  break
               }
               5 -> {
                  PlayerController.INSTANCE.pressLeftMouse(false)
                  PlayerController.INSTANCE.pressRightMouse(false)
                  if (anchorPos == null) {
                     this.onAutoFishingStart()
                  }

                  if (anchorPos == null) {
                     this.finishAndResumeFishing()
                     return
                  }

                  val sp: Vec3d = anchorPos
                  val d2: Double = var10000.method_73189().method_1025(sp)
                  val wantSprint: Boolean = !this.isInWater(var10000 as PlayerEntity) && Math.sqrt(d2) > 1.6
                  PlayerController.INSTANCE.pressSprint(wantSprint)
                  PlayerController.INSTANCE.pressForward(true)
                  if (d2 > 0.64) {
                     this.aimYawToward(var10000 as PlayerEntity, sp)
                     this.moveForwardWithStep(var10000 as PlayerEntity, now, wantSprint)
                  } else {
                     this.stopAllMovement()
                     this.finishAndResumeFishing()
                  }
                  break
               }
               6 -> throw NotImplementedError(null, 1, null)
               else -> throw NoWhenBranchMatchedException()
            }
         }
      }
   }

   private fun stripMinecraftColors(s: String): String {
      val sb: StringBuilder = StringBuilder(s.length())
      var i: Int = 0

      while (i < s.length()) {
         val c: Char = s.charAt(i)
         if (c == 167 && i + 1 < s.length()) {
            i += 2
         } else {
            sb.append(c)
            i++
         }
      }

      val var10000: java.lang.String = sb.toString()
      return var10000
   }

   private fun extractFrontHp(labelRaw: String): Int? {
      val s: java.lang.String = this.stripMinecraftColors(labelRaw)
      val slash: Int = StringsKt.indexOf$default(s, '/', 0, false, 6, null)
      if (slash <= 0) {
         return null
      } else {
         var i: Int = slash - 1

         while (i >= 0 && (Character.isDigit(s.charAt(i)) || s.charAt(i) == ',' || CharsKt.isWhitespace(s.charAt(i)))) {
            i--
         }

         val var10000: java.lang.String = s.substring(i + 1, slash)
         return StringsKt.toIntOrNull(StringsKt.trim(StringsKt.replace$default(var10000, ",", "", false, 4, null)).toString())
      }
   }

   private fun resumeFishingAsIfNothingHappened() {
      val wantResume: Boolean = wasFishing
      noReengageUntilMs = System.currentTimeMillis() + 900L
      this.snapToAnchorFacing()
      this.restoreRod()
      this.resetState()
      if (wantResume) {
         pendingResume = true
         resumeAtMs = System.currentTimeMillis() + 600L
      }
   }

   private fun finishAndResumeFishing() {
      val wantResume: Boolean = wasFishing
      noReengageUntilMs = System.currentTimeMillis() + 900L
      this.snapToAnchorFacing()
      this.restoreRod()
      this.resetState()
      if (wantResume) {
         pendingResume = true
         resumeAtMs = System.currentTimeMillis() + 600L
      }
   }

   private fun cleanupAndStop() {
      this.resetState()
   }

   private fun resetState() {
      isBusy = false
      phase = FishingMeleeMobs.Phase.IDLE
      mobWanted = null
      standHint = null
      target = null
      doubleHookRemaining = 0
      settleUntilMs = 0L
      settleBestD2 = java.lang.Double.POSITIVE_INFINITY
      settleLastD2 = java.lang.Double.POSITIVE_INFINITY
      isGangEncounter = false
      gangSweepUntilMs = 0L
      PlayerController.INSTANCE.pressLeftMouse(false)
      PlayerController.INSTANCE.pressRightMouse(false)
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressSprint(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
   }

   public fun installGuiEnableCheck(predicate: (String) -> Boolean) {
      guiEnableCheck = predicate
   }

   private fun norm(name: String): String {
      val var10000: java.lang.String = this.cleanLabel(name).toLowerCase(Locale.ROOT)
      return var10000
   }

   private fun shouldMelee(mobName: String): Boolean {
      return Config.fishingMeleeAllow && (Config.fishingMeleeAllMobs || FishingMeleeStore.INSTANCE.isEnabled(mobName))
   }

   private fun chat(msg: String) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var10000.method_7353(Text.method_43470(msg) as Text, false)
      }
   }

   private fun cleanLabel(raw: String): String {
      val var10000: java.lang.String
      if (StringsKt.startsWith$default(raw, JSON_TEXT_PREFIX, false, 2, null)) {
         val i: Int = StringsKt.indexOf$default(raw, "\"text\":\"", 0, false, 6, null) + 8
         val it: Int = StringsKt.indexOf$default(raw, "\"", i, false, 4, null)
         if (0 <= i && i < it) {
            var10000 = raw.substring(i, it)
         } else {
            var10000 = raw
         }
      } else {
         var10000 = raw
      }

      return StringsKt.trim(
            COMMA_RX.replace(
               StringsKt.replace$default(
                  StringsKt.replace$default(
                     StringsKt.replace$default(
                        StringsKt.replace$default(
                           StringsKt.replace$default(HEARTS_RX.replace(LEVEL_TAG_RX.replace(COLOR_RX.replace(var10000, ""), ""), ""), "✯", "", false, 4, null),
                           "⚓",
                           "",
                           false,
                           4,
                           null
                        ),
                        "\ud83e\udeb6",
                        "",
                        false,
                        4,
                        null
                     ),
                     "❤",
                     "",
                     false,
                     4,
                     null
                  ),
                  "/",
                  " ",
                  false,
                  4,
                  null
               ),
               ""
            )
         )
         .toString()
      }

   private fun containsMobName(label: String, wanted: String): Boolean {
      return StringsKt.contains(this.cleanLabel(label), wanted, true)
   }

   fun findStandByNameLoose(name: java.lang.String): ArmorStandEntity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var13: ClientPlayerEntity = this.getMc().field_1724
         if (var13 == null) {
            null
         } else {
            val me: ClientPlayerEntity = var13
            var best: ArmorStandEntity = null
            var bestD: Double = java.lang.Double.MAX_VALUE
            val var14: java.lang.Iterable = var10000.method_18112()

            for (var15 in CollectionsKt.toList(var14)) {
               val e: Entity = var15 as Entity
               if (var15 as Entity is ArmorStandEntity
                  && ((var15 as Entity) as ArmorStandEntity).method_5805()
                  && !((var15 as Entity) as ArmorStandEntity).method_31481()
                  && !((var15 as Entity).method_73189().method_1025(me.method_73189()) > 4096.0)) {
                  var var16: Text = (e as ArmorStandEntity).method_5797()
                  if (var16 == null) {
                     var16 = (e as ArmorStandEntity).method_5477()
                  }

                  val var17: java.lang.String = var16.getString()
                  if (var17 != null && this.containsMobName(var17, name)) {
                     val d: Double = me.method_5858(e)
                     if (d < bestD) {
                        bestD = d
                        best = e as ArmorStandEntity
                     }
                  }
               }
            }

            best
         }
      }
   }

   fun findLivingBelow(stand: ArmorStandEntity): LivingEntity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var13: Box = stand.method_5829()
         val below: Box = Box(
            var13.field_1323 - 2.5, var13.field_1322 - 3.2, var13.field_1321 - 2.5, var13.field_1320 + 2.5, var13.field_1322 - 0.2, var13.field_1324 + 2.5
         )
         var best: LivingEntity = null
         var bestD: Double = java.lang.Double.MAX_VALUE
         val var14: Vec3d = var13.method_1005()
         val center: Vec3d = var14
         val var15: java.lang.Iterable = var10000.method_18112()

         for (var16 in CollectionsKt.toList(var15)) {
            val e: Entity = var16 as Entity
            if (var16 as Entity is LivingEntity
               && (var16 as Entity) !is ArmorStandEntity
               && ((var16 as Entity) as LivingEntity).method_5805()
               && !((var16 as Entity) as LivingEntity).method_31481()
               && below.method_994(((var16 as Entity) as LivingEntity).method_5829())) {
               val d: Double = e.method_73189().method_1025(center)
               if (d < bestD) {
                  bestD = d
                  best = e as LivingEntity
               }
            }
         }

         best
      }
   }

   fun findNearestByNameLoose(name: java.lang.String): LivingEntity {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         null
      } else {
         val var13: ClientPlayerEntity = this.getMc().field_1724
         if (var13 == null) {
            null
         } else {
            val me: ClientPlayerEntity = var13
            var best: LivingEntity = null
            var bestD: Double = java.lang.Double.MAX_VALUE
            val var14: java.lang.Iterable = var10000.method_18112()

            for (var15 in CollectionsKt.toList(var14)) {
               val e: Entity = var15 as Entity
               if (var15 as Entity is LivingEntity
                  && (var15 as Entity) !is ArmorStandEntity
                  && ((var15 as Entity) as LivingEntity).method_5805()
                  && !((var15 as Entity) as LivingEntity).method_31481()
                  && !((var15 as Entity).method_73189().method_1025(me.method_73189()) > 4096.0)) {
                  var var16: Text = (e as LivingEntity).method_5797()
                  if (var16 == null) {
                     var16 = (e as LivingEntity).method_5477()
                  }

                  val var17: java.lang.String = var16.getString()
                  if (var17 != null && this.containsMobName(var17, name)) {
                     val d: Double = me.method_5858(e)
                     if (d < bestD) {
                        bestD = d
                        best = e as LivingEntity
                     }
                  }
               }
            }

            best
         }
      }
   }

   fun preferredHitPoint(e: Entity): Vec3d {
      val var10000: Box = e.method_5829()
      val cx: Double = (var10000.field_1323 + var10000.field_1320) * 0.5
      val cz: Double = (var10000.field_1321 + var10000.field_1324) * 0.5
      if (e is LivingEntity)
         Vec3d(cx, var10000.field_1322 + Math.max(0.08 * RangesKt.coerceAtLeast(var10000.field_1325 - var10000.field_1322, 0.01), 0.1), cz)
         else
         Vec3d(cx, (var10000.field_1322 + var10000.field_1325) * 0.5 - 0.9, (var10000.field_1321 + var10000.field_1324) * 0.5)
      }

   fun aimAtAndSmooth(player: PlayerEntity, e: LivingEntity) {
      val aimPoint: Vec3d = this.preferredHitPoint(e as Entity)
      val from: Vec3d = Vec3d(player.method_23317(), player.method_23320(), player.method_23321())
      val dx: Double = aimPoint.field_1352 - from.field_1352
      val dy: Double = aimPoint.field_1351 - from.field_1351
      val dz: Double = aimPoint.field_1350 - from.field_1350
      val flat: Double = Math.sqrt(dx * dx + (aimPoint.field_1350 - from.field_1350) * (aimPoint.field_1350 - from.field_1350))
      val wantYaw: Float = (float)Math.toDegrees(Math.atan2(-dx, dz))
      val wantPitch: Float = (float)Math.toDegrees(-Math.atan2(dy, flat))
      player.method_36456(this.stepAngle(player.method_36454(), wantYaw + this.randRange(-0.15F, 0.15F), 48.0F))
      player.method_36457(this.stepScalar(player.method_36455(), this.clamp(wantPitch + this.randRange(-0.1F, 0.1F), -89.9F, 89.9F), 36.0F))
   }

   fun aimYawToward(player: PlayerEntity, pos: Vec3d) {
      player.method_36456(
         this.stepAngle(
            player.method_36454(), (float)Math.toDegrees(Math.atan2(-(pos.field_1352 - player.method_23317()), pos.field_1350 - player.method_23321())), 48.0F
         )
      )
   }

   fun moveForwardWithStep(player: PlayerEntity, now: Long, sprint: Boolean) {
      PlayerController.INSTANCE.pressSprint(sprint)
      PlayerController.INSTANCE.pressForward(true)
      if (this.isInWater(player)) {
         val sinking: Boolean = player.method_18798().field_1351 < 0.02
         if ((player.method_5869() || sinking) && this.canJump(now)) {
            this.requestJump(now, 120L)
         }
      } else if (this.shouldStepJumpImmediate(player) && this.canJump(now)) {
         this.requestJump(now, 130L)
      }
   }

   private fun stopForward() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressSprint(false)
      PlayerController.INSTANCE.pressJump(false)
   }

   private fun stopAllMovement() {
      this.stopForward()
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
   }

   fun shouldStepJumpImmediate(player: PlayerEntity): Boolean {
      if (!player.method_24828()) {
         false
      } else if (player.field_5976) {
         true
      } else {
         val yawRad: Double = Math.toRadians((double)player.method_36454())
         val fx: Double = -Math.sin(yawRad)
         val fz: Double = Math.cos(yawRad)
         val aheadX: Double = player.method_23317() + fx * 0.62
         val aheadZ: Double = player.method_23321() + fz * 0.62
         var var10000: java.lang.Double = this.findTopAirY(player.method_23317(), player.method_23321(), player.method_23318())
         label33@
         if (var10000 != null) {
            val hereYTop: Double = var10000
            var10000 = this.findTopAirY(aheadX, aheadZ, player.method_23318())
            var10000 != null && var10000 - hereYTop >= 0.95
         } else {
            false
         }
      }
   }

   private fun findTopAirY(x: Double, z: Double, yRef: Double): Double? {
      val var10000: ClientWorld = this.getMc().field_1687
      if (var10000 == null) {
         return null
      } else {
         val world: ClientWorld = var10000
         val bx: Int = MathHelper.method_15357(x)
         val bz: Int = MathHelper.method_15357(z)
         val y0: Int = MathHelper.method_15357(yRef)
         val yMin: Int = y0 - 2

         // $VF: Unable to resugar Kotlin loop from Java for loop
         var y: Int = y0 + 2
         while (true) {
            if (y >= yMin) break
            val pos: BlockPos = BlockPos(bx, y, bz)
            val var19: BlockState = world.method_8320(pos)
            val var20: BlockState = world.method_8320(pos.method_10084())
            val solidHere: Boolean = this.isSolid(var19, pos)
            val var10002: BlockPos = pos.method_10084()
            if (solidHere && this.isPassable(var20, var10002)) {
               return (double)(y + 1)
            }

            y--
         }

         return null
      }
   }

   fun isSolid(state: BlockState, pos: BlockPos): Boolean {
      if (!state.method_26215()) {
         val var10001: ClientWorld = this.getMc().field_1687
         if (!state.method_26194(var10001 as BlockView, pos, ShapeContext.method_16194()).method_1110()) {
            true
         }
      }

      false
   }

   fun isPassable(state: BlockState, pos: BlockPos): Boolean {
      if (!state.method_26215()) {
         val var10001: ClientWorld = this.getMc().field_1687
         if (!state.method_26194(var10001 as BlockView, pos, ShapeContext.method_16194()).method_1110()) {
            false
         }
      }

      true
   }

   fun isInWater(player: PlayerEntity): Boolean {
      player.method_5799() || player.method_5869()
   }

   private fun selectHotbarSlot(slot0to8: Int) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         if (0 <= slot0to8 && slot0to8 < 9) {
            val var4: PlayerInventory = var10000.method_31548()
            val inv: InventoryAccessor = var4 as InventoryAccessor
            if ((var4 as InventoryAccessor).selected != slot0to8) {
               inv.selected = slot0to8
               PlayerController.INSTANCE.noteHotbarSwapThisTick()
            }
         }
      }
   }

   private fun meleeSlot0to8(): Int {
      val var10000: Int = this.getInt("fishingMeleeWeaponSlot")
      return RangesKt.coerceIn(var10000 ?: 1, 1, 9) - 1
   }

   private fun meleeCps(): Int {
      val var10000: Int = this.getInt("fishingMeleeCps")
      return RangesKt.coerceIn(var10000 ?: 10, 5, 16)
   }

   private fun recastDelayAfterResumeMs(): Long {
      return RangesKt.coerceAtLeast(Config.fishingRecastDelayMs, 0)
   }

   private fun restoreRod() {
      if (this.getMc().field_1724 != null) {
         val rod: Int = this.findRodSlot()
         this.selectHotbarSlot(if (rod != -1) rod else prevHotbar)
         PlayerController.INSTANCE.pressLeftMouse(false)
         PlayerController.INSTANCE.pressRightMouse(false)
      }
   }

   private fun findRodSlot(): Int {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val var4: PlayerInventory = var10000.method_31548()
         if (var4 != null) {
            val inv: PlayerInventory = var4

            repeat(8) { i ->
               val var5: ItemStack = inv.method_5438(i)
               if (!var5.method_7960()) {
                  val var6: java.lang.String = var5.method_7964().getString()
                  if (StringsKt.contains(var6, "Rod", true)) {
                     return i
                  }
               }
            }

            return -1
         }
      }

      return -1
   }

   private fun masterFishingEnabled(): Boolean {
      var var1: Boolean
      try {
         var1 = Config.fishingEnabled
      } catch (var3: java.lang.Throwable) {
         var1 = true
      }

      return var1
   }

   private fun setFishingEnabled(v: Boolean) {
      try {
         Config.class.getField("fishingEnabled").setBoolean(null, v)
      } catch (var3: java.lang.Throwable) {
      }
   }

   private fun nudge() {
      PlayerController.INSTANCE.tapForward(80L)
      if (System.currentTimeMillis() / 300 % 2L == 0L) {
         PlayerController.INSTANCE.tapLeft(85L)
      } else {
         PlayerController.INSTANCE.tapRight(85L)
      }

      val now: Long = System.currentTimeMillis()
      if (this.canJump(now)) {
         this.requestJump(now, 110L)
      }
   }

   private fun stepAngle(cur: Float, goal: Float, step: Float): Float {
      var out: Float = cur + RangesKt.coerceIn(MathHelper.method_15393(goal - cur), -step, step)

      while (out <= -180.0F) {
         out += 360.0F
      }

      return out
   }

   private fun clamp(v: Float, min: Float, max: Float): Float {
      return RangesKt.coerceIn(v, min, max)
   }

   private fun stepScalar(cur: Float, goal: Float, step: Float): Float {
      return if (Math.abs(goal - cur) <= step) goal else cur + Math.signum(goal - cur) * step
   }

   private fun randRange(min: Float, max: Float): Float {
      return min + Random.Default.nextFloat() * (max - min)
   }

   private fun getInt(key: String): Int? {
      var var2: Int
      try {
         var2 = Config.class.getField(key).getInt(null)
      } catch (var4: java.lang.Throwable) {
         var2 = null
      }

      return var2
   }

   private enum class Phase {
      IDLE,
      ARM_DELAY,
      ACQUIRE,
      APPROACH,
      ATTACK,
      RETURN;

      @JvmStatic
      fun getEntries(): EnumEntries<FishingMeleeMobs.Phase> {
         $ENTRIES
      }
   }
}
