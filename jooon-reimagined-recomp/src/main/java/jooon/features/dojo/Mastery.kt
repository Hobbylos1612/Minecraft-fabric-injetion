package jooon.features.dojo

import java.util.LinkedHashSet
import java.util.concurrent.ConcurrentLinkedQueue
import jooon.config.Config
import jooon.util.SmoothMouseAimController
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object Mastery {
   private val masteryBlocks: ConcurrentLinkedQueue<jooon.features.dojo.Mastery.MasteryBlock> = ConcurrentLinkedQueue()
   private val seenMastery: MutableSet<BlockPos> = LinkedHashSet() as java.util.Set
   private var isPaused: Boolean
   private var lastShotTime: Long
   private var lastActivityMs: Long

   private val aimController: SmoothMouseAimController = SmoothMouseAimController("dojo_automatic_mastery", { 
      isMasteryAutomationActive() && !isPaused
   })

   private const val AIM_OFFSET_Y: Double = 0.92
   private var isActive: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { it: MinecraftClient ->
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

   fun onBlockUpdate(pos: BlockPos, state: BlockState) {
      if (this.isMasteryAutomationActive()) {
         if (!isPaused) {
            if (state.getBlock() == Blocks.YELLOW_WOOL) {
               if (seenMastery.add(pos)) {
                  masteryBlocks.add(Mastery.MasteryBlock(pos, System.currentTimeMillis() + 3000.toLong()))
               }
            } else if (!(state.getBlock() == Blocks.RED_WOOL) && !(state.getBlock() == Blocks.YELLOW_WOOL) && seenMastery.remove(pos)) {
            }
         }
      }
   }

   private fun isMasteryAutomationActive(): Boolean {
      return Config.fullyAutomaticMastery && AutoDojo.isChallengeActive(AutoDojo.Challenge.MASTERY)
   }

   fun isBowDrawResetFixActive(): Boolean {
      return this.isMasteryAutomationActive() && !isPaused
   }

   fun aimAtTarget(player: ClientPlayerEntity, target: Mastery.MasteryBlock) {










      SmoothMouseAimController.request$default(aimController, player, targetYaw, targetPitch, 95, 240, 0.0F, 0L, 96, null)
      this.applyDirectAimFallback(player, targetYaw, targetPitch)
   }

   fun applyDirectAimFallback(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float) {


      if (!(Math.abs(yawDelta) <= 1.5F) || !(Math.abs(pitchDelta) <= 1.5F)) {


         player.setYaw(nextYaw)
         player.setPitch(nextPitch)
         player.headYaw = nextYaw
         player.bodyYaw = nextYaw
      }
   }

   private fun clearAim() {
      aimController.clear()
   }

   fun findBowSlot(player: PlayerEntity): Int {
      repeat(8) { i ->

         if (var10000.getItem() == Items.BOW) {
return i
         }
      }

      -1
   }

   data class MasteryBlock {
      private BlockPos pos;
      val timeTurnsRed: Long

      fun MasteryBlock(pos: BlockPos, timeTurnsRed: Long) {
         this.pos = pos
         this.timeTurnsRed = timeTurnsRed
      }

      fun getPos(): BlockPos {
         this.pos
      }

      fun component1(): BlockPos {
         this.pos
      }

      public operator fun component2(): Long {
         return this.timeTurnsRed
      }

      fun copy(pos: BlockPos, timeTurnsRed: Long): Mastery.MasteryBlock {
         Mastery.MasteryBlock(pos, timeTurnsRed)
      }

      override fun toString(): String {
         return "MasteryBlock(pos=${this.pos}, timeTurnsRed=${this.timeTurnsRed})"
      }

      override fun hashCode(): Int {
         return this.pos.hashCode() * 31 + java.lang.Long.hashCode(this.timeTurnsRed)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is Mastery.MasteryBlock
               && this.pos == (other as Mastery.MasteryBlock).pos
               && this.timeTurnsRed == (other as Mastery.MasteryBlock).timeTurnsRed
            }
      }
   }
}
