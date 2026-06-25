package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import jooon.config.Config
import jooon.features.jerry.JerryBoxOpener
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.Text

public object JerryOpenerCommand {
   public fun register() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            { dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
               dispatcher.register(
                  ClientCommandManager.literal("jerryopener").executes(INSTANCE.jooon/commands/JerryOpenerCommand##Lambda_1_121(INSTANCE)) as LiteralArgumentBuilder
               )
            }
         )
      }

   private fun execute(context: CommandContext<FabricClientCommandSource>): Int {
      if (!Config.jerryBoxOpenerEnabled) {
         (context.getSource() as FabricClientCommandSource)
            .sendFeedback(Text.method_43470("§7[§aJooon Reimagined§7] §cJerry Box Opener is disabled in config!") as Text)
            return 1
      } else {
         JerryBoxOpener.INSTANCE.toggle()
         return 1
      }
   }
}
