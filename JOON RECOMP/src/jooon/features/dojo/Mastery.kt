package jooon.features.dojo

import java.util.LinkedHashSet
import java.util.concurrent.ConcurrentLinkedQueue
import jooon.config.Config
import jooon.util.SmoothMouseAimController
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.class_2338
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

public object Mastery {
   private final val masteryBlocks: ConcurrentLinkedQueue<jooon.features.dojo.Mastery.MasteryBlock> = ConcurrentLinkedQueue()
   private final val seenMastery: MutableSet<class_2338> = LinkedHashSet() as java.util.Set
   private final var isPaused: Boolean
   private final var lastShotTime: Long
   private final var lastActivityMs: Long

   private final val aimController: SmoothMouseAimController = SmoothMouseAimController("dojo_automatic_mastery", { 
      INSTANCE.isMasteryAutomationActive() && !isPaused
   })

   private const val AIM_OFFSET_Y: Double = 0.92
   private final var isActive: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
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
            if (state.method_26204() == Blocks.field_10490) {
               if (seenMastery.add(pos)) {
                  masteryBlocks.add(Mastery.MasteryBlock(pos, System.currentTimeMillis() + (long)3000))
               }
            } else if (!(state.method_26204() == Blocks.field_10314) && !(state.method_26204() == Blocks.field_10490) && seenMastery.remove(pos)) {
            }
         }
      }
   }

   private fun isMasteryAutomationActive(): Boolean {
      return Config.fullyAutomaticMastery && AutoDojo.INSTANCE.isChallengeActive(AutoDojo.Challenge.MASTERY)
   }

   public fun isBowDrawResetFixActive(): Boolean {
      return this.isMasteryAutomationActive() && !isPaused
   }

   fun aimAtTarget(player: ClientPlayerEntity, target: Mastery.MasteryBlock) {
      val tx: Double = target.getPos().method_10263() + 0.5
      val ty: Double = target.getPos().method_10264() + 0.92
      val tz: Double = target.getPos().method_10260() + 0.5
      val var10000: Vec3d = player.method_33571()
      val dx: Double = tx - var10000.field_1352
      val dy: Double = ty - var10000.field_1351
      val dz: Double = tz - var10000.field_1350
      val dist: Double = Math.sqrt(dx * dx + (tz - var10000.field_1350) * (tz - var10000.field_1350))
      val targetYaw: Float = (float)Math.toDegrees(Math.atan2(-dx, dz))
      val targetPitch: Float = (float)Math.toDegrees(-Math.atan2(dy, dist))
      SmoothMouseAimController.request$default(aimController, player, targetYaw, targetPitch, 95, 240, 0.0F, 0L, 96, null)
      this.applyDirectAimFallback(player, targetYaw, targetPitch)
   }

   fun applyDirectAimFallback(player: ClientPlayerEntity, targetYaw: Float, targetPitch: Float) {
      val yawDelta: Float = MathHelper.method_15393(targetYaw - player.method_36454())
      val pitchDelta: Float = targetPitch - player.method_36455()
      if (!(Math.abs(yawDelta) <= 1.5F) || !(Math.abs(pitchDelta) <= 1.5F)) {
         val nextYaw: Float = player.method_36454() + RangesKt.coerceIn(yawDelta, -22.0F, 22.0F)
         val nextPitch: Float = RangesKt.coerceIn(player.method_36455() + RangesKt.coerceIn(pitchDelta, -22.0F, 22.0F), -89.5F, 89.5F)
         player.method_36456(nextYaw)
         player.method_36457(nextPitch)
         player.field_6241 = nextYaw
         player.field_6283 = nextYaw
      }
   }

   private fun clearAim() {
      aimController.clear()
   }

   fun findBowSlot(player: PlayerEntity): Int {
      repeat(8) { i ->
         val var10000: ItemStack = player.method_31548().method_5438(i)
         if (var10000.method_7909() == Items.field_8102) {
            i
         }
      }

      -1
   }

   public data class MasteryBlock {
      private BlockPos pos;
      public final val timeTurnsRed: Long

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

      public override fun toString(): String {
         return "MasteryBlock(pos=${this.pos}, timeTurnsRed=${this.timeTurnsRed})"
      }

      public override fun hashCode(): Int {
         return this.pos.hashCode() * 31 + java.lang.Long.hashCode(this.timeTurnsRed)
      }

      public override operator fun equals(other: Any?): Boolean {
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
