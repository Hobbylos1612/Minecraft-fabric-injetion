package jooon.features.dungeons

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import jooon.config.Config
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandRegistryAccess

object FastJoin {
   fun register() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            { dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
               val dungeon: Array<String> = arrayOf(
                  "CATACOMBS_FLOOR_ONE",
                  "CATACOMBS_FLOOR_TWO",
                  "CATACOMBS_FLOOR_THREE",
                  "CATACOMBS_FLOOR_FOUR",
                  "CATACOMBS_FLOOR_FIVE",
                  "CATACOMBS_FLOOR_SIX",
                  "CATACOMBS_FLOOR_SEVEN"
               )

               for (var6 in 1..7) {
                  dispatcher.register(ClientCommandManager.literal("f$var6").executes({ it: CommandContext ->
                     if (Config.fastJoinDungeons) {
                        sendJoin(`$dungeon`[`$i` - 1])
                     }
return 1
                  }) as LiteralArgumentBuilder)
               }

               dispatcher.register(
                  ClientCommandManager.literal("f")
                     .then(
                        ClientCommandManager.argument("number", IntegerArgumentType.integer(1, 7) as ArgumentType)
                           .executes(lambda_6_lambda_1@{ ctx: CommandContext ->
                              if (!Config.fastJoinDungeons) {
                                 return@lambda_6_lambda_1 0
                              } else {
                                 sendJoin(`$dungeon`[IntegerArgumentType.getInteger(ctx, "number") - 1])
                                 return@lambda_6_lambda_1 1
                              }
                           })
                     ) as LiteralArgumentBuilder
               )
               val var7: Array<String> = arrayOf(
                  "MASTER_CATACOMBS_FLOOR_ONE",
                  "MASTER_CATACOMBS_FLOOR_TWO",
                  "MASTER_CATACOMBS_FLOOR_THREE",
                  "MASTER_CATACOMBS_FLOOR_FOUR",
                  "MASTER_CATACOMBS_FLOOR_FIVE",
                  "MASTER_CATACOMBS_FLOOR_SIX",
                  "MASTER_CATACOMBS_FLOOR_SEVEN"
               )

               for (var8 in 1..7) {
                  dispatcher.register(ClientCommandManager.literal("m$var8").executes({ it: CommandContext ->
                     if (Config.fastJoinMaster) {
                        sendJoin(`$master`[`$i` - 1])
                     }
return 1
                  }) as LiteralArgumentBuilder)
               }

               dispatcher.register(
                  ClientCommandManager.literal("m")
                     .then(
                        ClientCommandManager.argument("number", IntegerArgumentType.integer(1, 7) as ArgumentType)
                           .executes(lambda_6_lambda_3@{ ctx: CommandContext ->
                              if (!Config.fastJoinMaster) {
                                 return@lambda_6_lambda_3 0
                              } else {
                                 sendJoin(`$master`[IntegerArgumentType.getInteger(ctx, "number") - 1])
                                 return@lambda_6_lambda_3 1
                              }
                           })
                     ) as LiteralArgumentBuilder
               )
               val var9: Array<String> = arrayOf("KUUDRA_NORMAL", "KUUDRA_HOT", "KUUDRA_BURNING", "KUUDRA_FIERY", "KUUDRA_INFERNAL")

               for (var10 in 1..5) {
                  dispatcher.register(ClientCommandManager.literal("t$var10").executes({ it: CommandContext ->
                     if (Config.fastJoinKuudra) {
                        sendJoin(`$kuudra`[`$i` - 1])
                     }
return 1
                  }) as LiteralArgumentBuilder)
               }

               dispatcher.register(
                  ClientCommandManager.literal("t")
                     .then(
                        ClientCommandManager.argument("number", IntegerArgumentType.integer(1, 5) as ArgumentType)
                           .executes(lambda_6_lambda_5@{ ctx: CommandContext ->
                              if (!Config.fastJoinKuudra) {
                                 return@lambda_6_lambda_5 0
                              } else {
                                 sendJoin(`$kuudra`[IntegerArgumentType.getInteger(ctx, "number") - 1])
                                 return@lambda_6_lambda_5 1
                              }
                           })
                     ) as LiteralArgumentBuilder
               )
            }
         )
      }

   private fun sendJoin(instanceId: String) {

      if (var10000.player != null) {
         var10000.player.networkHandler.sendChatCommand("joininstance $instanceId")
      }
   }
}
