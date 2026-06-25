package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess

object BZInstaSell {
   private var inited: Boolean

   fun register() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(register$lambda$1$root("jooonreimagined"))
         dispatcher.register(register$lambda$1$root("jr"))
      })
   }
}
