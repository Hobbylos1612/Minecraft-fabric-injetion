package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess

public object BZInstaSell {
   private final var inited: Boolean

   public fun register() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(register$lambda$1$root("jooonreimagined"))
         dispatcher.register(register$lambda$1$root("jr"))
      })
   }
}
