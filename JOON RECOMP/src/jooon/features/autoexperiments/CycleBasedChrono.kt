package jooon.features.autoexperiments

import java.util.ArrayList
import jooon.config.Config
import net.minecraft.class_1792
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

public class CycleBasedChrono(containerName: String) : AbstractSolver(containerName) {
   private final val terracottaToGlass: Map<class_1792, class_1792> =
      MapsKt.mapOf(
         arrayOf(
            TuplesKt.to(Items.field_8353, Items.field_8636),
            TuplesKt.to(Items.field_8043, Items.field_8393),
            TuplesKt.to(Items.field_8385, Items.field_8095),
            TuplesKt.to(Items.field_8672, Items.field_8340),
            TuplesKt.to(Items.field_8798, Items.field_8734),
            TuplesKt.to(Items.field_8821, Items.field_8685),
            TuplesKt.to(Items.field_8717, Items.field_8869),
            TuplesKt.to(Items.field_8455, Items.field_8126),
            TuplesKt.to(Items.field_8715, Items.field_8838),
            TuplesKt.to(Items.field_8853, Items.field_8770)
         )
      )
      private MinecraftClient client;
   private final val clickStack: ArrayList<class_1792>
   private final var glintFound: Boolean
   private final var glintFoundAt: Int
   private final var lastCycle: Int
   private Item lastModeItem;
   private final var currentDelay: Int

   init {
      val var10001: MinecraftClient = MinecraftClient.method_1551()
      this.client = var10001
      this.clickStack = ArrayList<>()
      this.glintFoundAt = -1
   }

   override fun tick(screen: HandledScreen<*>) {
      if (Config.autoExperiments) {
         val var10000: ScreenHandler = screen.method_17577()
         val menu: ScreenHandler = var10000
         val currentCycle: Int = this.getChronoCycle(var10000)
         val var8: Item = var10000.method_7611(49).method_7677().method_7909()
         if (currentCycle > 0 && var8 == Items.field_8801 || currentCycle == this.lastCycle && !(var8 == this.lastModeItem)) {
            if (!this.glintFound) {
               for (i in 10..42) {
                  val var9: ItemStack = menu.method_7611(i).method_7677()
                  if (var9.method_7958()) {
                     this.glintFound = true
                     this.glintFoundAt = i
                     val glass: Item = this.terracottaToGlass.get(var9.method_7909())
                     if (glass != null) {
                        this.clickStack.add(glass)
                     }
                     break
                  }
               }
            } else if (!var10000.method_7611(this.glintFoundAt).method_7677().method_7958()) {
               this.glintFound = false
               this.glintFoundAt = -1
            }
         } else {
            this.inputSequence(var10000)
         }

         this.lastCycle = currentCycle
         this.lastModeItem = var8
      }
   }

   fun inputSequence(menu: ScreenHandler) {
      if (this.client.field_1724 != null) {
         val player: ClientPlayerEntity = this.client.field_1724
         val var10000: ItemStack = this.client.field_1724.field_7512.method_34255()
         if (var10000.method_7960() && !this.clickStack.isEmpty()) {
            val targetItem: Item = CollectionsKt.first(this.clickStack) as Item

            for (i in 10..42) {
               if (menu.method_7611(i).method_7677().method_7909() == targetItem) {
                  if (this.client.field_1761 != null) {
                     this.client.field_1761.method_2906(menu.field_7763, i, 0, SlotActionType.field_7790, player as PlayerEntity)
                  }

                  this.clickStack.remove(0)
                  break
               }
            }
         }
      }
   }

   fun getChronoCycle(menu: ScreenHandler): Int {
      menu.method_7611(4).method_7677().method_7947()
   }

   public fun resetClickStack() {
      this.clickStack.clear()
      this.glintFound = false
      this.glintFoundAt = -1
      this.currentDelay = 0
   }
}
