package jooon.pathfinding.voxel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerAbilities

data class VoxelJumpProfile(jumpVelocity: Double,
   jumpHeight: Double,
   stepHeight: Double,
   maxClimb: Double,
   maxHorizontalBlocks: Double,
   maxSkipCells: Int
) {
   val jumpVelocity: Double
   val jumpHeight: Double
   val stepHeight: Double
   val maxClimb: Double
   val maxHorizontalBlocks: Double
   val maxSkipCells: Int

   init {
      this.jumpVelocity = jumpVelocity
      this.jumpHeight = jumpHeight
      this.stepHeight = stepHeight
      this.maxClimb = maxClimb
      this.maxHorizontalBlocks = maxHorizontalBlocks
      this.maxSkipCells = maxSkipCells
   }

   public operator fun component1(): Double {
      return this.jumpVelocity
   }

   public operator fun component2(): Double {
      return this.jumpHeight
   }

   public operator fun component3(): Double {
      return this.stepHeight
   }

   public operator fun component4(): Double {
      return this.maxClimb
   }

   public operator fun component5(): Double {
      return this.maxHorizontalBlocks
   }

   public operator fun component6(): Int {
      return this.maxSkipCells
   }

   fun copy(
      jumpVelocity: Double = this.jumpVelocity,
      jumpHeight: Double = this.jumpHeight,
      stepHeight: Double = this.stepHeight,
      maxClimb: Double = this.maxClimb,
      maxHorizontalBlocks: Double = this.maxHorizontalBlocks,
      maxSkipCells: Int = this.maxSkipCells
   ): VoxelJumpProfile {
      return VoxelJumpProfile(jumpVelocity, jumpHeight, stepHeight, maxClimb, maxHorizontalBlocks, maxSkipCells)
   }

   override fun toString(): String {
      return "VoxelJumpProfile(jumpVelocity=${this.jumpVelocity}, jumpHeight=${this.jumpHeight}, stepHeight=${this.stepHeight}, maxClimb=${this.maxClimb}, maxHorizontalBlocks=${this.maxHorizontalBlocks}, maxSkipCells=${this.maxSkipCells})"
   }

   override fun hashCode(): Int {
      return (
               (
                        (
                                 (java.lang.Double.hashCode(this.jumpVelocity) * 31 + java.lang.Double.hashCode(this.jumpHeight)) * 31
                                    + java.lang.Double.hashCode(this.stepHeight)
                              )
                              * 31
                           + java.lang.Double.hashCode(this.maxClimb)
                     )
                     * 31
                  + java.lang.Double.hashCode(this.maxHorizontalBlocks)
            )
            * 31
         + Integer.hashCode(this.maxSkipCells)
      }

   override operator fun equals(other: Any?): Boolean {
      label52@
      if (this === other) {
         return true
      } else {
         return other is VoxelJumpProfile
            && java.lang.Double.compare(this.jumpVelocity, (other as VoxelJumpProfile).jumpVelocity) == 0
            && java.lang.Double.compare(this.jumpHeight, (other as VoxelJumpProfile).jumpHeight) == 0
            && java.lang.Double.compare(this.stepHeight, (other as VoxelJumpProfile).stepHeight) == 0
            && java.lang.Double.compare(this.maxClimb, (other as VoxelJumpProfile).maxClimb) == 0
            && java.lang.Double.compare(this.maxHorizontalBlocks, (other as VoxelJumpProfile).maxHorizontalBlocks) == 0
            && this.maxSkipCells == (other as VoxelJumpProfile).maxSkipCells
         }
   }

   companion object {
      private const val DEFAULT_JUMP_VELOCITY: Double = 0.42
      private const val GRAVITY: Double = 0.08
      private const val DRAG: Double = 0.98
      private const val MAX_SIM_TICKS: Int = 80

      fun getMc(): MinecraftClient {
return var10000
      }

      fun current(player: ClientPlayerEntity?): VoxelJumpProfile {
         var jumpVelocity: Double
         var stepHeight: Double
         var jumpHeight: Double
         var maxClimb: Double
         var var14: Float
         run label40@{
            jumpVelocity = if (player != null) player.getAttributeValue(EntityAttributes.JUMP_STRENGTH) else 0.42
            stepHeight = (if (player != null) player.getAttributeValue(EntityAttributes.STEP_HEIGHT) else 0.6).coerceAtLeast(0.6)
            jumpHeight = this.simulateJumpHeight(jumpVelocity)
            maxClimb = (Math.max(stepHeight, jumpHeight)).coerceAtLeast(1.25)
            if (player != null) {

               if (var10000 != null) {
                  var14 = var10000.getWalkSpeed()
                  return@label40
               }
            }

            var14 = 0.1F
         }


            1.45
return else
            (1.2 + maxClimb * 0.9 + Math.max(0.0, var14.toDouble() * 1000.0 - 100.0) / 220.0).coerceIn(1.45, 4.0)
            VoxelJumpProfile(jumpVelocity, jumpHeight, stepHeight, maxClimb, horizontal, (Math.floor(horizontal).toInt()).coerceIn(1, 4))
      }

      private fun simulateJumpHeight(initialVelocity: Double): Double {
         var velocity: Double = initialVelocity
         var height: Double = 0.0
         var best: Double = 0.0

         // $VF: Unable to resugar Kotlin loop from Java for loop

         while (true) {
            if (velocity > 0.0 && ticks++ < 80) break
            height += velocity
            if (height > best) {
               best = height
            }

            velocity = (velocity - 0.08) * 0.98
         }

         return best
      }
   }
}
