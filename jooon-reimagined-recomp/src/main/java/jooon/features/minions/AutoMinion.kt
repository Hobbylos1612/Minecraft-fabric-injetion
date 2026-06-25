package jooon.features.minions

import jooon.config.Config
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text

object AutoMinion {
   private var warnedNoCoins: Boolean
   private var claimCooldown: Int

   fun init() {
      ScreenEvents.AFTER_INIT.register({ var0: MinecraftClient, screen: Screen, var2: Int, var3: Int ->
         warnedNoCoins = false
         claimCooldown = 4
         if (screen is HandledScreen) {

            if (contains$default(var10000, "Minion", false, 2, null)) {
               ScreenEvents.afterTick(screen).register({ tickingScreen: Screen ->
                  tick(tickingScreen as HandledScreen)
               })
            }
         }
      })
   }
}
