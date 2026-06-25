package jooon.features.dojo

import jooon.config.Config
import jooon.util.PlayerController
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

object Discipline {
   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      AttackEntityCallback.EVENT.register(lambda_0@{ player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult ->
         if (!Config.autoDojoEnabled || Config.fullyAutomaticDiscipline) {
            return@lambda_0 ActionResult.PASS as ActionResult
         } else if (!AutoDojo.isChallengeActive(AutoDojo.Challenge.DISCIPLINE)) {
            return@lambda_0 ActionResult.PASS as ActionResult
         } else if (entity !is ZombieEntity) {
            return@lambda_0 ActionResult.PASS as ActionResult
         } else {



            if (requiredSword == null) {
               return@lambda_0 ActionResult.PASS as ActionResult
            } else {

               if (var10 == requiredSword) {
                  return@lambda_0 ActionResult.PASS as ActionResult
               } else {
                  swapToCorrectSword$JooonReimagined_noMidnightLib(var9)
                  return@lambda_0 ActionResult.FAIL as ActionResult
               }
            }
         }
      })
   }

   fun `requiredSwordForHelmet$JooonReimagined_noMidnightLib`(helmet: Item): Item? {
      if (helmet == Items.LEATHER_HELMET)
         Items.WOODEN_SWORD
return else
         (
            if (helmet == Items.IRON_HELMET)
               Items.IRON_SWORD
return else
               (if (helmet == Items.GOLDEN_HELMET) Items.GOLDEN_SWORD else (if (helmet == Items.DIAMOND_HELMET) Items.DIAMOND_SWORD else null))
         )
      }

   fun `swordSlotForHelmet$JooonReimagined_noMidnightLib`(helmet: Item): Int? {
      if (helmet == Items.LEATHER_HELMET)
return 0
return else
         (if (helmet == Items.IRON_HELMET) 1 else (if (helmet == Items.GOLDEN_HELMET) 2 else (if (helmet == Items.DIAMOND_HELMET) 3 else null)))
      }

   fun `swapToCorrectSword$JooonReimagined_noMidnightLib`(helmet: Item) {

      if (var10000 != null) {

         if (var4 != null) {

            if (var10000.getInventory().getSelectedSlot() != targetSlot) {
               var10000.getInventory().setSelectedSlot(targetSlot)
               PlayerController.noteHotbarSwapThisTick()
            }
         }
      }
   }
}
