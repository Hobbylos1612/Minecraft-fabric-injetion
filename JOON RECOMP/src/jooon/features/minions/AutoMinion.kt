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

public object AutoMinion {
   private final var warnedNoCoins: Boolean
   private final var claimCooldown: Int

   public fun init() {
      ScreenEvents.AFTER_INIT.register({ var0: MinecraftClient, screen: Screen, var2: Int, var3: Int ->
         warnedNoCoins = false
         claimCooldown = 4
         if (screen is HandledScreen) {
            val var10000: java.lang.String = (screen as HandledScreen).method_25440().getString()
            if (StringsKt.contains$default(var10000, "Minion", false, 2, null)) {
               ScreenEvents.afterTick(screen).register({ tickingScreen: Screen ->
                  INSTANCE.tick(tickingScreen as HandledScreen<*>)
               })
            }
         }
      })
   }
}
