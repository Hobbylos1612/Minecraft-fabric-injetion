package jooon.features.autoexperiments

import java.util.ArrayList
import jooon.config.Config
import net.minecraft.item.Item
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

class CycleBasedChrono(containerName: String) : AbstractSolver(containerName) {
   private val terracottaToGlass: Map<Item, Item> =
      mapOf(
         arrayOf(
            Pair(Items.RED_TERRACOTTA, Items.RED_STAINED_GLASS),
            Pair(Items.ORANGE_TERRACOTTA, Items.ORANGE_STAINED_GLASS),
            Pair(Items.YELLOW_TERRACOTTA, Items.YELLOW_STAINED_GLASS),
            Pair(Items.LIME_TERRACOTTA, Items.LIME_STAINED_GLASS),
            Pair(Items.GREEN_TERRACOTTA, Items.GREEN_STAINED_GLASS),
            Pair(Items.CYAN_TERRACOTTA, Items.CYAN_STAINED_GLASS),
            Pair(Items.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_STAINED_GLASS),
            Pair(Items.BLUE_TERRACOTTA, Items.BLUE_STAINED_GLASS),
            Pair(Items.PURPLE_TERRACOTTA, Items.PURPLE_STAINED_GLASS),
            Pair(Items.PINK_TERRACOTTA, Items.PINK_STAINED_GLASS)
         )
      )
      private MinecraftClient client;
   private val clickStack: ArrayList<Item>
   private var glintFound: Boolean
   private var glintFoundAt: Int
   private var lastCycle: Int
   private Item lastModeItem;
   private var currentDelay: Int

   init {

      this.client = var10001
      this.clickStack = ArrayList<>()
      this.glintFoundAt = -1
   }

   override fun tick(screen: HandledScreen) {
      if (Config.autoExperiments) {




         if (currentCycle > 0 && var8 == Items.GLOWSTONE || currentCycle == this.lastCycle && !(var8 == this.lastModeItem)) {
            if (!this.glintFound) {
               for (i in 10..42) {

                  if (var9.hasGlint()) {
                     this.glintFound = true
                     this.glintFoundAt = i

                     if (glass != null) {
                        this.clickStack.add(glass)
                     }
break
                  }
               }
            } else if (!var10000.getSlot(this.glintFoundAt).getStack().hasGlint()) {
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
      if (this.client.player != null) {


         if (var10000.isEmpty() && !this.clickStack.isEmpty()) {


            for (i in 10..42) {
               if (menu.getSlot(i).getStack().getItem() == targetItem) {
                  if (this.client.interactionManager != null) {
                     this.client.interactionManager.clickSlot(menu.syncId, i, 0, SlotActionType.PICKUP, player as PlayerEntity)
                  }

                  this.clickStack.remove(0)
break
               }
            }
         }
      }
   }

   fun getChronoCycle(menu: ScreenHandler): Int {
      menu.getSlot(4).getStack().getCount()
   }

   fun resetClickStack() {
      this.clickStack.clear()
      this.glintFound = false
      this.glintFoundAt = -1
      this.currentDelay = 0
   }
}
