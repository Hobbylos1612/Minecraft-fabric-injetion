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

public object Discipline {
   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      AttackEntityCallback.EVENT.register(lambda_0@{ player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult ->
         if (!Config.autoDojoEnabled || Config.fullyAutomaticDiscipline) {
            return@lambda_0 ActionResult.field_5811 as ActionResult
         } else if (!AutoDojo.INSTANCE.isChallengeActive(AutoDojo.Challenge.DISCIPLINE)) {
            return@lambda_0 ActionResult.field_5811 as ActionResult
         } else if (entity !is ZombieEntity) {
            return@lambda_0 ActionResult.field_5811 as ActionResult
         } else {
            val var10000: ItemStack = (entity as ZombieEntity).method_6118(EquipmentSlot.field_6169)
            val var9: Item = var10000.method_7909()
            val requiredSword: Item = INSTANCE.requiredSwordForHelmet$JooonReimagined_noMidnightLib(var9)
            if (requiredSword == null) {
               return@lambda_0 ActionResult.field_5811 as ActionResult
            } else {
               val var10: Item = player.method_6047().method_7909()
               if (var10 == requiredSword) {
                  return@lambda_0 ActionResult.field_5811 as ActionResult
               } else {
                  INSTANCE.swapToCorrectSword$JooonReimagined_noMidnightLib(var9)
                  return@lambda_0 ActionResult.field_5814 as ActionResult
               }
            }
         }
      })
   }

   fun `requiredSwordForHelmet$JooonReimagined_noMidnightLib`(helmet: Item): Item? {
      if (helmet == Items.field_8267)
         Items.field_8091
         else
         (
            if (helmet == Items.field_8743)
               Items.field_8371
               else
               (if (helmet == Items.field_8862) Items.field_8845 else (if (helmet == Items.field_8805) Items.field_8802 else null))
         )
      }

   fun `swordSlotForHelmet$JooonReimagined_noMidnightLib`(helmet: Item): Int? {
      if (helmet == Items.field_8267)
         0
         else
         (if (helmet == Items.field_8743) 1 else (if (helmet == Items.field_8862) 2 else (if (helmet == Items.field_8805) 3 else null)))
      }

   fun `swapToCorrectSword$JooonReimagined_noMidnightLib`(helmet: Item) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val var4: Int = this.swordSlotForHelmet$JooonReimagined_noMidnightLib(helmet)
         if (var4 != null) {
            val targetSlot: Int = var4
            if (var10000.method_31548().method_67532() != targetSlot) {
               var10000.method_31548().method_61496(targetSlot)
               PlayerController.INSTANCE.noteHotbarSwapThisTick()
            }
         }
      }
   }
}
