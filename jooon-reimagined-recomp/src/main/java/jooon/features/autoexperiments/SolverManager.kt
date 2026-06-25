package jooon.features.autoexperiments

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.collection.DefaultedList

object SolverManager {


   val TARGET_SCREEN_TITLES: List<String> = listOf(arrayOf("Chronomatron ➜ Stakes", "Ultrasequencer ➜ Stakes"))

   fun isExperimentScreen(title: String): Boolean {
      return TARGET_SCREEN_TITLES.contains(title)
   }

   fun init() {
      ScreenEvents.BEFORE_INIT.register({ var0: MinecraftClient, screen: Screen, var2: Int, var3: Int ->
         if (screen is HandledScreen) {

            if (startsWith$default(var10000, "Chronomatron (", false, 2, null)) {
               chronomatronSolver.start(screen as HandledScreen)
            } else if (startsWith$default(var10000, "Ultrasequencer (", false, 2, null)) {
               ultrasequencerSolver.start(screen as HandledScreen)
            } else {
               chronomatronSolver.resetClickStack()
               ultrasequencerSolver.clearItemStack()
            }
         }
      })
   }

   fun starterMotor() {

      if (var10000.interactionManager != null) {

         if (var10000.player != null) {

            if (var10000.player.currentScreenHandler != null) {


               for (i in 24 downTo 21) {


                  if (var9 != null) {

                     if (!var10.isEmpty() && !(var10.getItem() == Items.GRAY_DYE)) {
                        gameMode.clickSlot(containerMenu.syncId, i, 0, SlotActionType.PICKUP, player as PlayerEntity)
                     }
                  }
               }
            }
         }
      }
   }
}
