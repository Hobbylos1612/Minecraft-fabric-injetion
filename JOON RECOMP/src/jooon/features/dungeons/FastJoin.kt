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

public object FastJoin {
   public fun register() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            { dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
               val dungeon: Array<java.lang.String> = arrayOf(
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
                        INSTANCE.sendJoin(`$dungeon`[`$i` - 1])
                     }

                     1
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
                                 INSTANCE.sendJoin(`$dungeon`[IntegerArgumentType.getInteger(ctx, "number") - 1])
                                 return@lambda_6_lambda_1 1
                              }
                           })
                     ) as LiteralArgumentBuilder
               )
               val var7: Array<java.lang.String> = arrayOf(
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
                        INSTANCE.sendJoin(`$master`[`$i` - 1])
                     }

                     1
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
                                 INSTANCE.sendJoin(`$master`[IntegerArgumentType.getInteger(ctx, "number") - 1])
                                 return@lambda_6_lambda_3 1
                              }
                           })
                     ) as LiteralArgumentBuilder
               )
               val var9: Array<java.lang.String> = arrayOf("KUUDRA_NORMAL", "KUUDRA_HOT", "KUUDRA_BURNING", "KUUDRA_FIERY", "KUUDRA_INFERNAL")

               for (var10 in 1..5) {
                  dispatcher.register(ClientCommandManager.literal("t$var10").executes({ it: CommandContext ->
                     if (Config.fastJoinKuudra) {
                        INSTANCE.sendJoin(`$kuudra`[`$i` - 1])
                     }

                     1
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
                                 INSTANCE.sendJoin(`$kuudra`[IntegerArgumentType.getInteger(ctx, "number") - 1])
                                 return@lambda_6_lambda_5 1
                              }
                           })
                     ) as LiteralArgumentBuilder
               )
            }
         )
      }

   private fun sendJoin(instanceId: String) {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 != null) {
         var10000.field_1724.field_3944.method_45730("joininstance $instanceId")
      }
   }
}
