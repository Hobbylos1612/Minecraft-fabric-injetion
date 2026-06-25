package jooon.features.fishing

import java.util.Locale
import jooon.config.Config
import jooon.mixins.InventoryAccessor
import jooon.util.PlayerController
import kotlin.enums.EnumEntries
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

object FishingMeleeMobs {
   var isBusy: Boolean
      private set

   @Volatile
   
   private Vec3d anchorPos;
   private var anchorYaw: Float
   private var anchorPitch: Float
   private var isGangEncounter: Boolean
   private var gangSweepUntilMs: Long
   private var poseAlignUntilMs: Long
   private var settleUntilMs: Long
   private var settleBestD2: Double = java.lang.Double.POSITIVE_INFINITY
   private var settleLastD2: Double = java.lang.Double.POSITIVE_INFINITY
   private var phase: jooon.features.fishing.FishingMeleeMobs.Phase = FishingMeleeMobs.Phase.IDLE
   private var mobWanted: String?
   private var wasFishing: Boolean
   private var prevHotbar: Int
   
   private ArmorStandEntity standHint;
   
   private LivingEntity target;
   private var armUntilMs: Long
   private var acquireUntilMs: Long
   private var lastScanMs: Long
   private var lastAttackMs: Long
   private var lastProgressMs: Long
   
   private Vec3d lastPos;
   private var jumpHoldUntilMs: Long
   private var nextJumpAllowedMs: Long
   private var jumpPenaltyMs: Long
   private var pendingResume: Boolean
   private var resumeAtMs: Long
   private var pendingRecast: Boolean
   private var recastAtMs: Long
   private var noReengageUntilMs: Long
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
   private var doubleHookWindowUntilMs: Long
   private var doubleHookRemaining: Int

   private var guiEnableCheck: (String) -> Boolean = { var0: String ->
return false
   }

   private val COLOR_RX: Regex = Regex("§.")
   private val JSON_TEXT_PREFIX: String = "{\"text\":\""
   private val LEVEL_TAG_RX: Regex = Regex("\\[\\s*(Lv|Lvl|Level)\\s*\\d+\\s*]", RegexOption.IGNORE_CASE)
   private val HEARTS_RX: Regex = Regex("[❤♥]")
   private val COMMA_RX: Regex = Regex(",")

   private fun isAxePreferred(wanted: String): Boolean {

      return contains$default(var10000, "ent", false, 2, null)
   }

   private fun findAxeSlot(): Int {

      if (var10000 != null) {

         if (var4 != null) {


            repeat(8) { i ->

               if (!var5.isEmpty()) {

                  if (var6.contains("axe", true)) {
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

         if (axe != -1) {
            return axe
         }
      }

      return this.meleeSlot0to8()
   }

   fun willMeleeForMessage(plain: String): Boolean {
      if (System.currentTimeMillis() < noReengageUntilMs) {
         return false
      } else {
         label21@
         if (plain == "A gang of Liltads!") {
            return this.shouldMelee("Liltad")
         } else {

            return var10000 != null && this.shouldMelee(var10000)
         }
      }
   }

   fun tryEngageForMessage(plain: String): Boolean {

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
      return Math.abs(MathHelper.wrapDegrees(a - b))
   }

   fun forceAbortAndDisableFishing() {
      if (this.getMc().player != null) {
         this.stopAllMovement()
         this.selectHotbarSlot(prevHotbar)
         this.setFishingEnabled(false)
         noReengageUntilMs = System.currentTimeMillis() + 900L
         this.resetState()
      }
   }

   fun onAutoFishingStart() {

      if (var10000 != null) {
         anchorPos = var10000.getEntityPos()
         anchorYaw = var10000.getYaw()
         anchorPitch = var10000.getPitch()
      }
   }

   fun onAutoFishingStop() {
      anchorPos = null
   }

   fun getMc(): MinecraftClient {
return var10000
   }

   private val ATTACK_COOLDOWN_MS: Long
      private get() {
         return (1000.0 / this.meleeCps()).toLong()
      }


   private fun canJump(now: Long): Boolean {
      return now >= nextJumpAllowedMs
   }

   private fun requestJump(now: Long, holdMs: Long) {
      jumpHoldUntilMs = Math.max(jumpHoldUntilMs, now + holdMs)
      nextJumpAllowedMs = now + 180L + jumpPenaltyMs
      jumpPenaltyMs = (jumpPenaltyMs + 80L).coerceAtMost(600L)
   }

   private fun decayJumpPenalty() {
      jumpPenaltyMs = (jumpPenaltyMs - 40L).coerceAtLeast(0L)
   }

   private fun startEngage(wantedMobName: String) {

      if (var10000 != null) {
         if (anchorPos == null) {
            anchorPos = var10000.getEntityPos()
            anchorYaw = var10000.getYaw()
            anchorPitch = var10000.getPitch()
         }

         isBusy = true

         prevHotbar = (var3 as InventoryAccessor).selected
         wasFishing = this.masterFishingEnabled()
         FunnyFishing.pauseForMeleeImmediate()
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


      return if (contains$default(var6, "liltad", false, 2, null) || contains$default(var6, "tadgang", false, 2, null) || isGangEncounter)
         contains$default(var10000, "liltad", false, 2, null)
            || contains$default(var10000, "tadgang", false, 2, null)
            || startsWith$default(var10000, "tad", false, 2, null)
return else
         contains$default(var10000, var6, false, 2, null)
      }

   fun facingAligned(p: PlayerEntity): Boolean {
      this.angleErrorDeg(p.getYaw(), anchorYaw) <= 0.6F && Math.abs(p.getPitch() - anchorPitch) <= 0.8F
   }

   fun findNextTadShard(): LivingEntity {
      run label27@{
         var var10000: ArmorStandEntity = this.findStandByNameLoose("Tadgang")
         if (var10000 != null) {

            if (var11 != null) {
return var11
            }
         }

         var10000 = this.findStandByNameLoose("Liltad")
         if (var10000 != null) {

            if (var13 != null) {
return var13
            }
         }

         var14 ?: this.findNearestByEncounter("Liltad")
      }
   }

   private fun snapToAnchorFacing() {

      if (var10000 != null) {
         var10000.setYaw(anchorYaw)
         var10000.setPitch(anchorPitch)
         var10000.setYaw(anchorYaw)
         var10000.setPitch(anchorPitch)
      }
   }

   fun findNearestByEncounter(wanted: String): LivingEntity {

      if (var10000 == null) {
return null
      } else {

         if (var13 == null) {
return null
         } else {

            var best: LivingEntity = null
            var bestD: Double = java.lang.Double.MAX_VALUE
            val var14: java.lang.Iterable = var10000.getEntities()

            for (var15 in toList(var14)) {

               if (var15 as Entity is LivingEntity
                  && (var15 as Entity) !is ArmorStandEntity
                  && ((var15 as Entity) as LivingEntity).isAlive()
                  && !((var15 as Entity) as LivingEntity).isRemoved()
                  && !((var15 as Entity).getEntityPos().squaredDistanceTo(me.getEntityPos()) > 4096.0)) {
                  var var16: Text = (e as LivingEntity).getCustomName()
                  if (var16 == null) {
                     var16 = (e as LivingEntity).getName()
                  }

                  if (var17 != null && this.isSameEncounter(var17, wanted)) {

                     if (d < bestD) {
                        bestD = d
                        best = e as LivingEntity
                     }
                  }
               }
            }
return best
         }
      }
   }

   private fun hasRemainingClusterTargets(wanted: String, radius: Double = 18.0): Boolean {

      if (var10000 == null) {
         return false
      } else {

         if (var10 == null) {
            return false
         } else {

            val var11: java.lang.Iterable = var10000.getEntities()

            for (var12 in toList(var11)) {

               if (var12 as Entity is LivingEntity
                  && (var12 as Entity) !is ArmorStandEntity
                  && ((var12 as Entity) as LivingEntity).isAlive()
                  && !((var12 as Entity) as LivingEntity).isRemoved()
                  && !((var12 as Entity).getEntityPos().squaredDistanceTo(me.getEntityPos()) > radius * radius)) {
                  var var13: Text = (e as LivingEntity).getCustomName()
                  if (var13 == null) {
                     var13 = (e as LivingEntity).getName()
                  }

                  if (var14 != null && this.isSameEncounter(var14, wanted)) {
                     return true
                  }
               }
            }

            return false
         }
      }
   }

   fun onTick() {

      if (!isBusy && pendingResume && now >= resumeAtMs) {
         pendingResume = false

         try {
            this.snapToAnchorFacing()
            this.setFishingEnabled(true)
            FunnyFishing.resumeAfterMelee()
         } catch (var14: java.lang.Throwable) {
         }

         pendingRecast = true
         recastAtMs = now + this.recastDelayAfterResumeMs()
      }

      if (!isBusy && pendingRecast && now >= recastAtMs) {
         pendingRecast = false

         try {
            FunnyFishing.recastAfterMelee()
         } catch (var13: java.lang.Throwable) {
         }
      }

      if (isBusy) {

         if (var10000 != null) {
            this.decayJumpPenalty()
PlayerController.pressJump(now < jumpHoldUntilMs)
            when (FishingMeleeMobs.WhenMappings.$EnumSwitchMapping$0[phase.ordinal()]) {
               1 -> {
                  if (now >= armUntilMs) {

                     this.selectHotbarSlot(this.weaponSlotFor(var10002))
                     acquireUntilMs = now + 800
                     phase = FishingMeleeMobs.Phase.ACQUIRE
                  }
               }
               2 -> {
                  if (now - lastScanMs >= 140L) {
                     run label330@{
                        lastScanMs = now

                        standHint = this.findStandByNameLoose(var41)
                        if (standHint != null) {
                           var42 = findLivingBelow(standHint)
                           if (var42 != null) {
                              return@label330
                           }
                        }

                        var42 = this.findNearestByEncounter(var41)
                     }

                     target = var42
                  }

                  if (target != null) {

                     if (var43.isAlive()) {
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

                  if (target == null || !target.isAlive() || var16.isRemoved()) {
                     phase = FishingMeleeMobs.Phase.RETURN
return return
                  }

                  this.aimAtAndSmooth(var10000 as PlayerEntity, var16)

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

                        var var33: Text = standHint.getCustomName()
                        if (var33 == null) {
                           var33 = var25.getName()
                        }

                        if (itx != null) {
                           var35 = extractFrontHp(itx)
                           return@label345
                        }
                     }

                     var35 = null
                  }

                  if (target == null || !target.isAlive() || var15.isRemoved() || var18 != null && (var18.isRemoved() || var35 != null && var35 <= 0)
                     )
                   {
                     PlayerController.pressLeftMouse(false)
                     standHint = null
                     target = null
                     lastScanMs = 0L

                     if (isGangEncounter || mobWanted != null && mobWanted.contains("liltad", true)) {

                        if (var28 != null && var28.isAlive()) {
                           isGangEncounter = true
                           gangSweepUntilMs = var22 + 1000L
                           target = var28
                           standHint = null
                           acquireUntilMs = var22 + 800
                           phase = FishingMeleeMobs.Phase.ACQUIRE
return return
                        }

                        if (var22 < gangSweepUntilMs) {
                           acquireUntilMs = var22 + 800
                           phase = FishingMeleeMobs.Phase.ACQUIRE
return return
                        }

                        isGangEncounter = false
                     }

                     var var40: String = mobWanted
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
return return
                  }

                  this.aimAtAndSmooth(var10000 as PlayerEntity, var15)
                  if (var21 > 2.35) {
                     phase = FishingMeleeMobs.Phase.APPROACH
                  } else if (now - lastAttackMs >= this.ATTACK_COOLDOWN_MS) {
                     var10000.swingHand(Hand.MAIN_HAND)
                     PlayerController.tapLeftMouse(26L)
                     lastAttackMs = now
                  }

                  run label361@{

                     if (lastPos != null) {

                        if (!(var36.squaredDistanceTo(var10001) > 4.5E-4)) {
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
                     if (!isGangEncounter && (mobWanted == null || !mobWanted.contains("liltad", true))) {
                        var var38: String = mobWanted
                        if (mobWanted == null) {
                           var38 = "Liltad"
                        }

                        run label365@{
                           if (standHint != null) {
                              var39 = findLivingBelow(standHint)
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
                  PlayerController.pressLeftMouse(false)
                  PlayerController.pressRightMouse(false)
                  if (anchorPos == null) {
                     this.onAutoFishingStart()
                  }

                  if (anchorPos == null) {
                     this.finishAndResumeFishing()
return return
                  }



                  PlayerController.pressSprint(wantSprint)
                  PlayerController.pressForward(true)
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

      var i: Int = 0

      while (i < s.length()) {

         if (c == 167 && i + 1 < s.length()) {
            i += 2
         } else {
            sb.append(c)
            i++
         }
      }

      return var10000
   }

   private fun extractFrontHp(labelRaw: String): Int? {


      if (slash <= 0) {
         return null
      } else {
         var i: Int = slash - 1

         while (i >= 0 && (Character.isDigit(s.charAt(i)) || s.charAt(i) == ',' || CharsKt.isWhitespace(s.charAt(i)))) {
            i--
         }

         return toIntOrNull(trim(replace$default(var10000, ",", "", false, 4, null)).toString())
      }
   }

   private fun resumeFishingAsIfNothingHappened() {

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
      PlayerController.pressLeftMouse(false)
      PlayerController.pressRightMouse(false)
      PlayerController.pressForward(false)
      PlayerController.pressSprint(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressJump(false)
   }

   fun installGuiEnableCheck(predicate: (String) -> Boolean) {
      guiEnableCheck = predicate
   }

   private fun norm(name: String): String {

      return var10000
   }

   private fun shouldMelee(mobName: String): Boolean {
      return Config.fishingMeleeAllow && (Config.fishingMeleeAllMobs || FishingMeleeStore.isEnabled(mobName))
   }

   private fun chat(msg: String) {

      if (var10000 != null) {
         var10000.sendMessage(Text.literal(msg) as Text, false)
      }
   }

   private fun cleanLabel(raw: String): String {
      val var10000: String
      if (startsWith$default(raw, JSON_TEXT_PREFIX, false, 2, null)) {


         if (0 <= i && i < it) {
            var10000 = raw.substring(i, it)
         } else {
            var10000 = raw
         }
      } else {
         var10000 = raw
      }

      return trim(
            COMMA_RX.replace(
               replace$default(
                  replace$default(
                     replace$default(
                        replace$default(
                           replace$default(HEARTS_RX.replace(LEVEL_TAG_RX.replace(COLOR_RX.replace(var10000, ""), ""), ""), "✯", "", false, 4, null),
                           "⚓",
                           "",
                           false,
                           4,
return null
                        ),
                        "\ud83e\udeb6",
                        "",
                        false,
                        4,
return null
                     ),
                     "❤",
                     "",
                     false,
                     4,
return null
                  ),
                  "/",
                  " ",
                  false,
                  4,
return null
               ),
               ""
            )
         )
         .toString()
      }

   private fun containsMobName(label: String, wanted: String): Boolean {
      return contains(this.cleanLabel(label), wanted, true)
   }

   fun findStandByNameLoose(name: String): ArmorStandEntity {

      if (var10000 == null) {
return null
      } else {

         if (var13 == null) {
return null
         } else {

            var best: ArmorStandEntity = null
            var bestD: Double = java.lang.Double.MAX_VALUE
            val var14: java.lang.Iterable = var10000.getEntities()

            for (var15 in toList(var14)) {

               if (var15 as Entity is ArmorStandEntity
                  && ((var15 as Entity) as ArmorStandEntity).isAlive()
                  && !((var15 as Entity) as ArmorStandEntity).isRemoved()
                  && !((var15 as Entity).getEntityPos().squaredDistanceTo(me.getEntityPos()) > 4096.0)) {
                  var var16: Text = (e as ArmorStandEntity).getCustomName()
                  if (var16 == null) {
                     var16 = (e as ArmorStandEntity).getName()
                  }

                  if (var17 != null && this.containsMobName(var17, name)) {

                     if (d < bestD) {
                        bestD = d
                        best = e as ArmorStandEntity
                     }
                  }
               }
            }
return best
         }
      }
   }

   fun findLivingBelow(stand: ArmorStandEntity): LivingEntity {

      if (var10000 == null) {
return null
      } else {


            var13.minX - 2.5, var13.minY - 3.2, var13.minZ - 2.5, var13.maxX + 2.5, var13.minY - 0.2, var13.maxZ + 2.5
         )
         var best: LivingEntity = null
         var bestD: Double = java.lang.Double.MAX_VALUE


         val var15: java.lang.Iterable = var10000.getEntities()

         for (var16 in toList(var15)) {

            if (var16 as Entity is LivingEntity
               && (var16 as Entity) !is ArmorStandEntity
               && ((var16 as Entity) as LivingEntity).isAlive()
               && !((var16 as Entity) as LivingEntity).isRemoved()
               && below.intersects(((var16 as Entity) as LivingEntity).getBoundingBox())) {

               if (d < bestD) {
                  bestD = d
                  best = e as LivingEntity
               }
            }
         }
return best
      }
   }

   fun findNearestByNameLoose(name: String): LivingEntity {

      if (var10000 == null) {
return null
      } else {

         if (var13 == null) {
return null
         } else {

            var best: LivingEntity = null
            var bestD: Double = java.lang.Double.MAX_VALUE
            val var14: java.lang.Iterable = var10000.getEntities()

            for (var15 in toList(var14)) {

               if (var15 as Entity is LivingEntity
                  && (var15 as Entity) !is ArmorStandEntity
                  && ((var15 as Entity) as LivingEntity).isAlive()
                  && !((var15 as Entity) as LivingEntity).isRemoved()
                  && !((var15 as Entity).getEntityPos().squaredDistanceTo(me.getEntityPos()) > 4096.0)) {
                  var var16: Text = (e as LivingEntity).getCustomName()
                  if (var16 == null) {
                     var16 = (e as LivingEntity).getName()
                  }

                  if (var17 != null && this.containsMobName(var17, name)) {

                     if (d < bestD) {
                        bestD = d
                        best = e as LivingEntity
                     }
                  }
               }
            }
return best
         }
      }
   }

   fun preferredHitPoint(e: Entity): Vec3d {



      if (e is LivingEntity)
         Vec3d(cx, var10000.minY + Math.max(0.08 * (var10000.maxY - var10000.minY).coerceAtLeast(0.01), 0.1), cz)
return else
         Vec3d(cx, (var10000.minY + var10000.maxY) * 0.5 - 0.9, (var10000.minZ + var10000.maxZ) * 0.5)
      }

   fun aimAtAndSmooth(player: PlayerEntity, e: LivingEntity) {








      player.setYaw(this.stepAngle(player.getYaw(), wantYaw + this.randRange(-0.15F, 0.15F), 48.0F))
      player.setPitch(this.stepScalar(player.getPitch(), this.clamp(wantPitch + this.randRange(-0.1F, 0.1F), -89.9F, 89.9F), 36.0F))
   }

   fun aimYawToward(player: PlayerEntity, pos: Vec3d) {
      player.setYaw(
         this.stepAngle(
            player.getYaw(), Math.toDegrees(Math.atan2(-(pos.x - player.getX()), pos.z - player.getZ())).toFloat(), 48.0F
         )
      )
   }

   fun moveForwardWithStep(player: PlayerEntity, now: Long, sprint: Boolean) {
      PlayerController.pressSprint(sprint)
      PlayerController.pressForward(true)
      if (this.isInWater(player)) {

         if ((player.isSubmergedInWater() || sinking) && this.canJump(now)) {
            this.requestJump(now, 120L)
         }
      } else if (this.shouldStepJumpImmediate(player) && this.canJump(now)) {
         this.requestJump(now, 130L)
      }
   }

   private fun stopForward() {
      PlayerController.pressForward(false)
      PlayerController.pressSprint(false)
      PlayerController.pressJump(false)
   }

   private fun stopAllMovement() {
      this.stopForward()
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressJump(false)
   }

   fun shouldStepJumpImmediate(player: PlayerEntity): Boolean {
      if (!player.isOnGround()) {
return false
      } else if (player.horizontalCollision) {
return true
      } else {





         var var10000: Double = this.findTopAirY(player.getX(), player.getZ(), player.getY())
         label33@
         if (var10000 != null) {

            var10000 = this.findTopAirY(aheadX, aheadZ, player.getY())
            var10000 != null && var10000 - hereYTop >= 0.95
         } else {
return false
         }
      }
   }

   private fun findTopAirY(x: Double, z: Double, yRef: Double): Double? {

      if (var10000 == null) {
         return null
      } else {






         // $VF: Unable to resugar Kotlin loop from Java for loop
         var y: Int = y0 + 2
         while (true) {
            if (y >= yMin) break





            if (solidHere && this.isPassable(var20, var10002)) {
               return (y + 1).toDouble()
            }

            y--
         }

         return null
      }
   }

   fun isSolid(state: BlockState, pos: BlockPos): Boolean {
      if (!state.isAir()) {

         if (!state.getCollisionShape(var10001 as BlockView, pos, ShapeContext.absent()).isEmpty()) {
return true
         }
      }
return false
   }

   fun isPassable(state: BlockState, pos: BlockPos): Boolean {
      if (!state.isAir()) {

         if (!state.getCollisionShape(var10001 as BlockView, pos, ShapeContext.absent()).isEmpty()) {
return false
         }
      }
return true
   }

   fun isInWater(player: PlayerEntity): Boolean {
      player.isTouchingWater() || player.isSubmergedInWater()
   }

   private fun selectHotbarSlot(slot0to8: Int) {

      if (var10000 != null) {
         if (0 <= slot0to8 && slot0to8 < 9) {


            if ((var4 as InventoryAccessor).selected != slot0to8) {
               inv.selected = slot0to8
               PlayerController.noteHotbarSwapThisTick()
            }
         }
      }
   }

   private fun meleeSlot0to8(): Int {

      return (var10000 ?: 1).coerceIn(1, 9) - 1
   }

   private fun meleeCps(): Int {

      return (var10000 ?: 10).coerceIn(5, 16)
   }

   private fun recastDelayAfterResumeMs(): Long {
      return (Config.fishingRecastDelayMs).coerceAtLeast(0)
   }

   private fun restoreRod() {
      if (this.getMc().player != null) {

         this.selectHotbarSlot(if (rod != -1) rod else prevHotbar)
         PlayerController.pressLeftMouse(false)
         PlayerController.pressRightMouse(false)
      }
   }

   private fun findRodSlot(): Int {

      if (var10000 != null) {

         if (var4 != null) {


            repeat(8) { i ->

               if (!var5.isEmpty()) {

                  if (var6.contains("Rod", true)) {
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
         Config::class.java.getField("fishingEnabled").setBoolean(null, v)
      } catch (var3: java.lang.Throwable) {
      }
   }

   private fun nudge() {
      PlayerController.tapForward(80L)
      if (System.currentTimeMillis() / 300 % 2L == 0L) {
         PlayerController.tapLeft(85L)
      } else {
         PlayerController.tapRight(85L)
      }

      if (this.canJump(now)) {
         this.requestJump(now, 110L)
      }
   }

   private fun stepAngle(cur: Float, goal: Float, step: Float): Float {
      var out: Float = cur + (MathHelper.wrapDegrees(goal - cur)).coerceIn(-step, step)

      while (out <= -180.0F) {
         out += 360.0F
      }

      return out
   }

   private fun clamp(v: Float, min: Float, max: Float): Float {
      return (v).coerceIn(min, max)
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
         var2 = Config::class.java.getField(key).getInt(null)
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

      
      fun getEntries(): EnumEntries<FishingMeleeMobs.Phase> {
         $ENTRIES
      }
   }
}
