package jooon.features.autoexperiments

import java.util.ArrayList
import jooon.config.Config
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

class UltrasequencerSolver(containerName: String) : AbstractSolver(containerName) {
   private MinecraftClient client;
   private val clickStack: ArrayList<Int>
   private var currentDelay: Int

   init {

      this.client = var10001
      this.clickStack = ArrayList<>()
   }

   override fun tick(screen: HandledScreen) {
      if (Config.autoExperiments) {
         if (this.currentDelay > 0) {
            this.currentDelay += -1
         } else {



            if (var8 == Items.GLOWSTONE) {
               repeat(44) { i ->


                  if (!endsWith$default(var10, "pane", false, 2, null) && var9.getCount() == this.clickStack.size() + 1) {
                     this.clickStack.add(i)
                  }
               }
            } else if (var8 == Items.CLOCK) {
               this.inputSequence(screen)
            }
         }
      }
   }

   fun inputSequence(screen: HandledScreen) {
      if (this.client.player != null) {


         if (var10000.isEmpty() && !this.clickStack.isEmpty()) {

            if (this.client.interactionManager != null) {
               this.client.interactionManager.clickSlot(screen.getScreenHandler().syncId, targetSlot, 0, SlotActionType.PICKUP, player as PlayerEntity)
            }

            this.clickStack.remove(0)
            this.currentDelay = Config.autoExperimentsTickDelay
         }
      }
   }

   fun clearItemStack() {
      this.clickStack.clear()
      this.currentDelay = 0
   }
}
