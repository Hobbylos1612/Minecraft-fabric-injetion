package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess

public object JooonReimaginedGUI : ClientModInitializer {
   public open fun onInitializeClient() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(onInitializeClient$lambda$1$root("jooonreimagined"))
         dispatcher.register(onInitializeClient$lambda$1$root("jr"))
      })
   }
}
