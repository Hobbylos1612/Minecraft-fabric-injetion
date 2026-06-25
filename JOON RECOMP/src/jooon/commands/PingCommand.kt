package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import java.util.Arrays
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.Text

public object PingCommand {
   private final var lastPingStart: Long

   public fun register() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(register$lambda$1$root("jooonreimagined"))
         dispatcher.register(register$lambda$1$root("jr"))
      })
   }

   public fun handleStatResponse() {
      if (lastPingStart != 0L) {
         val elapsed: Double = (System.nanoTime() - lastPingStart) / 1000000.0
         lastPingStart = 0L
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         var10000.execute({ 
            val var10000: ClientPlayerEntity = `$client`.field_1724
            if (`$client`.field_1724 != null) {
               val var5: Array<Any> = arrayOf(`$elapsed`)
               val var10001: java.lang.String = java.lang.String.format("%.0f", Arrays.copyOf(var5, var5.length))
               var10000.method_7353(Text.method_43470("§a§lJooon Reimagined §7» §aPing: §7$var10001ms") as Text, false)
            }
         })
      }
   }
}
