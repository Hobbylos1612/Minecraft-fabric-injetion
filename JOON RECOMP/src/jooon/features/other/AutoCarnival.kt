package jooon.features.other

import java.util.ArrayList
import java.util.Locale
import jooon.config.Config
import jooon.util.PlayerController
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nAutoCarnival.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoCarnival.kt\njooon/features/other/AutoCarnival\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,147:1\n800#2,11:148\n*S KotlinDebug\n*F\n+ 1 AutoCarnival.kt\njooon/features/other/AutoCarnival\n*L\n80#1:148,11\n*E\n"])
public object AutoCarnival {
   private final var lastClickMs: Long
   private final val lampCoords: List<Triple<Int, Int, Int>> =
      CollectionsKt.listOf(
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

   public fun register() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         INSTANCE.tick()
      })
   }

   private fun tick() {
      if (Config.autoCarnivalEnabled) {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         if (var10000.field_1687 != null) {
            val world: ClientWorld = var10000.field_1687
            if (var10000.field_1724 != null) {
               val player: ClientPlayerEntity = var10000.field_1724
               val var10: ItemStack = var10000.field_1724.method_6047()
               if (!var10.method_7960()) {
                  val var11: java.lang.String = var10.method_7964().getString()
                  if (StringsKt.contains(var11, "Dart", true)) {
                     if (System.currentTimeMillis() - lastClickMs < 200L) {
                        return
                     }

                     val targets: java.util.List = this.gatherTargets(world, player)
                     if (targets.isEmpty()) {
                        return
                     }

                     val var12: Pair = this.calcYawPitch(player, CollectionsKt.first(targets) as Vec3d)
                     if (var12 == null) {
                        return
                     }

                     val pitch: Float = (var12.component2() as java.lang.Number).floatValue()
                     player.method_36456((var12.component1() as java.lang.Number).floatValue())
                     player.method_36457(pitch)
                     lastClickMs = System.currentTimeMillis()
                     PlayerController.INSTANCE.rightClick()
                     return
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
      val var10000: java.lang.Iterable = world.method_18112()
      val var10: java.lang.Iterable = CollectionsKt.toList(var10000)
      val name: java.util.Collection = ArrayList()

      for (y in var10) {
         if (y is ZombieEntity) {
            name.add(y)
         }
      }

      for (var19 in name as java.util.List) {
         if (!(var19.method_5707(player.method_73189()) > 1600.0)) {
            val var33: ItemStack = var19.method_6118(EquipmentSlot.field_6174)
            if (!var33.method_7960()) {
               val var25: Vec3d = this.projectAhead(var19, player)
               val var34: java.lang.String = var33.method_7964().getString()
               val var35: java.lang.String = var34.toLowerCase(Locale.ROOT)
               if (StringsKt.contains$default(var35, "diamond", false, 2, null)) {
                  diamond.add(var25)
               } else if (StringsKt.contains$default(var35, "gold", false, 2, null)) {
                  gold.add(var25)
               } else if (StringsKt.contains$default(var35, "iron", false, 2, null)) {
                  iron.add(var25)
               } else if (StringsKt.contains$default(var35, "leather", false, 2, null)) {
                  leather.add(var25)
               }
            }
         }
      }

      var var20: java.util.List = CollectionsKt.createListBuilder()
      val var23: java.util.List = var20

      for (var30 in lampCoords) {
         val var31: Int = (var30.component1() as java.lang.Number).intValue()
         val var32: Int = (var30.component2() as java.lang.Number).intValue()
         val z: Int = (var30.component3() as java.lang.Number).intValue()
         val var36: BlockState = world.method_8320(BlockPos(var31, var32, z))
         if (var36.method_27852(Blocks.field_10524) && var36.method_11654(RedstoneLampBlock.field_11413 as Property) == true) {
            var23.add(Vec3d((double)var31 + 0.5, (double)var32 + 0.6, (double)z + 0.5))
         }
      }

      val var18: java.util.List = CollectionsKt.build(var20)
      var20 = ArrayList()
      var20.addAll(diamond)
      var20.addAll(var18)
      var20.addAll(gold)
      var20.addAll(iron)
      var20.addAll(leather)
      var20
   }

   fun projectAhead(z: ZombieEntity, player: ClientPlayerEntity): Vec3d {
      val lead: Double = RangesKt.coerceIn(Math.sqrt(z.method_5858(player as Entity)) / 6.0, 1.0, 12.0)
         + RangesKt.coerceAtLeast(Config.autoCarnivalPing, 0) / 50.0
         val var10000: Vec3d = z.method_18798()
      Vec3d(z.method_23317() + var10000.field_1352 * lead, z.method_23320(), z.method_23321() + var10000.field_1350 * lead)
   }

   fun calcYawPitch(player: ClientPlayerEntity, target: Vec3d): Pair<java.lang.Float, java.lang.Float> {
      var var10000: Vec3d = player.method_33571()
      var10000 = target.method_1020(var10000)
      val yaw: Float = (float)Math.toDegrees(Math.atan2(-var10000.field_1352, var10000.field_1350))
      val distXZ: Double = Math.sqrt(var10000.field_1352 * var10000.field_1352 + var10000.field_1350 * var10000.field_1350)
      if (distXZ == 0.0) {
         null
      } else {
         val pitch: Float = (float)Math.toDegrees(-Math.atan2(var10000.field_1351, distXZ))
         if (!(pitch < -90.0F) && !(pitch > 90.0F) && !java.lang.Float.isNaN(yaw) && !java.lang.Float.isNaN(pitch)) TuplesKt.to(yaw, pitch) else null
      }
   }
}
