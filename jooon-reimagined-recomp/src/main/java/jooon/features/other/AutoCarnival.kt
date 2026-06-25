package jooon.features.other

import java.util.ArrayList
import java.util.Locale
import jooon.config.Config
import jooon.util.PlayerController
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.RedstoneLampBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object AutoCarnival {
   private var lastClickMs: Long
   private val lampCoords: List<Triple<Int, Int, Int>> =
      listOf(
         arrayOf(
            Triple(-96, 76, 31),
            Triple(-99, 77, 32),
            Triple(-102, 75, 32),
            Triple(-106, 77, 31),
            Triple(-109, 75, 30),
            Triple(-112, 76, 28),
            Triple(-115, 77, 25),
            Triple(-117, 76, 22),
            Triple(-118, 76, 19),
            Triple(-119, 75, 15),
            Triple(-119, 77, 12),
            Triple(-118, 76, 9)
         )
      )

   fun register() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         tick()
      })
   }

   private fun tick() {
      if (Config.autoCarnivalEnabled) {

         if (var10000.world != null) {

            if (var10000.player != null) {


               if (!var10.isEmpty()) {

                  if (var11.contains("Dart", true)) {
                     if (System.currentTimeMillis() - lastClickMs < 200L) {
return return
                     }

                     val targets: java.util.List = this.gatherTargets(world, player)
                     if (targets.isEmpty()) {
return return
                     }

                     if (var12 == null) {
return return
                     }

                     player.setYaw((var12.component1() as java.lang.Number).floatValue())
                     player.setPitch(pitch)
                     lastClickMs = System.currentTimeMillis()
                     PlayerController.rightClick()
return return
                  }
               }
            }
         }
      }
   }

   fun gatherTargets(world: ClientWorld, player: ClientPlayerEntity): MutableList<Vec3d> {
      val diamond: java.util.List = ArrayList()
      val gold: java.util.List = ArrayList()
      val iron: java.util.List = ArrayList()
      val leather: java.util.List = ArrayList()
      val var10000: java.lang.Iterable = world.getEntities()
      val var10: java.lang.Iterable = toList(var10000)
      val name: java.util.Collection = ArrayList()

      for (y in var10) {
         if (y is ZombieEntity) {
            name.add(y)
         }
      }

      for (var19 in name as java.util.List) {
         if (!(var19.squaredDistanceTo(player.getEntityPos()) > 1600.0)) {

            if (!var33.isEmpty()) {



               if (contains$default(var35, "diamond", false, 2, null)) {
                  diamond.add(var25)
               } else if (contains$default(var35, "gold", false, 2, null)) {
                  gold.add(var25)
               } else if (contains$default(var35, "iron", false, 2, null)) {
                  iron.add(var25)
               } else if (contains$default(var35, "leather", false, 2, null)) {
                  leather.add(var25)
               }
            }
         }
      }

      var var20: java.util.List = createListBuilder()
      val var23: java.util.List = var20

      for (var30 in lampCoords) {




         if (var36.isOf(Blocks.REDSTONE_LAMP) && var36.get(RedstoneLampBlock.LIT as Property) == true) {
            var23.add(Vec3d(var31.toDouble() + 0.5, var32.toDouble() + 0.6, z.toDouble() + 0.5))
         }
      }

      val var18: java.util.List = build(var20)
      var20 = ArrayList()
      var20.addAll(diamond)
      var20.addAll(var18)
      var20.addAll(gold)
      var20.addAll(iron)
      var20.addAll(leather)
return var20
   }

   fun projectAhead(z: ZombieEntity, player: ClientPlayerEntity): Vec3d {

         + (Config.autoCarnivalPing).coerceAtLeast(0) / 50.0

      Vec3d(z.getX() + var10000.x * lead, z.getEyeY(), z.getZ() + var10000.z * lead)
   }

   fun calcYawPitch(player: ClientPlayerEntity, target: Vec3d): Pair<Float, Float> {
      var var10000: Vec3d = player.getEyePos()
      var10000 = target.subtract(var10000)


      if (distXZ == 0.0) {
return null
      } else {

         if (!(pitch < -90.0F) && !(pitch > 90.0F) && !java.lang.Float.isNaN(yaw) && !java.lang.Float.isNaN(pitch)) Pair(yaw, pitch) else null
      }
   }
}
