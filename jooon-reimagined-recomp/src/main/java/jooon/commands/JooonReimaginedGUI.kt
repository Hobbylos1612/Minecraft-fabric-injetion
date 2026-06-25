package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess

object JooonReimaginedGUI : ClientModInitializer {
   open fun onInitializeClient() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(onInitializeClient$lambda$1$root("jooonreimagined"))
         dispatcher.register(onInitializeClient$lambda$1$root("jr"))
      })
   }
}
