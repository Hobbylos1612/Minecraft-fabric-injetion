package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import java.util.Arrays
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.Text

object PingCommand {
   private var lastPingStart: Long

   fun register() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(register$lambda$1$root("jooonreimagined"))
         dispatcher.register(register$lambda$1$root("jr"))
      })
   }

   fun handleStatResponse() {
      if (lastPingStart != 0L) {

         lastPingStart = 0L

         var10000.execute({ 

            if (`$client`.player != null) {
               val var5: Array<Any> = arrayOf(`$elapsed`)

               var10000.sendMessage(Text.literal("§a§lJooon Reimagined §7» §aPing: §7$var10001ms") as Text, false)
            }
         })
      }
   }
}
