package jooon.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.Arrays
import java.util.Locale
import jooon.util.Rotator
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.Text

public object RotateCommand {
   public fun register() {
      ClientCommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, var1: CommandRegistryAccess ->
         dispatcher.register(register$lambda$0$root("jooonreimagined"))
         dispatcher.register(register$lambda$0$root("jr"))
      })
   }

   private fun rotateTree(): LiteralArgumentBuilder<FabricClientCommandSource> {
      val var10000: ArgumentBuilder = ClientCommandManager.literal("rotate")
         .then(
            ClientCommandManager.argument("yaw", FloatArgumentType.floatArg(-180.0F, 180.0F) as ArgumentType)
               .then(
                  (ClientCommandManager.argument("pitch", FloatArgumentType.floatArg(-90.0F, 90.0F) as ArgumentType).executes({ ctx: CommandContext ->
                        INSTANCE.executeRotate(FloatArgumentType.getFloat(ctx, "yaw"), FloatArgumentType.getFloat(ctx, "pitch"), 5.0F, 0.15F)
                     }) as RequiredArgumentBuilder)
                     .then(
                        (ClientCommandManager.argument("maxStep", FloatArgumentType.floatArg(0.5F, 45.0F) as ArgumentType)
                              .executes(
                                 { ctx: CommandContext ->
                                    INSTANCE.executeRotate(
                                       FloatArgumentType.getFloat(ctx, "yaw"),
                                       FloatArgumentType.getFloat(ctx, "pitch"),
                                       FloatArgumentType.getFloat(ctx, "maxStep"),
                                       0.15F
                                    )
                                 }
                              ) as RequiredArgumentBuilder)
                           .then(
                              ClientCommandManager.argument("deadzone", FloatArgumentType.floatArg(0.01F, 5.0F) as ArgumentType)
                                 .executes(
                                    { ctx: CommandContext ->
                                       INSTANCE.executeRotate(
                                          FloatArgumentType.getFloat(ctx, "yaw"),
                                          FloatArgumentType.getFloat(ctx, "pitch"),
                                          FloatArgumentType.getFloat(ctx, "maxStep"),
                                          FloatArgumentType.getFloat(ctx, "deadzone")
                                       )
                                    }
                                 )
                           )
                     )
               )
         )
         return var10000 as LiteralArgumentBuilder<FabricClientCommandSource>
   }

   private fun executeRotate(yaw: Float, pitch: Float, step: Float, deadzone: Float): Int {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 == null) {
         return 1
      } else {
         val player: ClientPlayerEntity = var10000.field_1724
         val clampedYaw: Float = this.normalizeYaw(yaw)
         val clampedPitch: Float = RangesKt.coerceIn(pitch, -90.0F, 90.0F)
         Rotator.rotateToAngles$default(Rotator.INSTANCE, clampedYaw, clampedPitch, step, deadzone, null, 16, null)
         player.method_7353(
            Text.method_43470("§aJooonReimagined §7» §aRotating to §l${this.fmt1(clampedYaw)}§7, §a§l${this.fmt1(clampedPitch)}") as Text, false
         )
         return 1
      }
   }

   private fun normalizeYaw(y: Float): Float {
      var x: Float = y

      while (x <= -180.0F) {
         x += 360.0F
      }

      while (x > 180.0F) {
         x -= 360.0F
      }

      return x
   }

   private fun fmt1(v: Float): String {
      val var2: Locale = Locale.US
      val var5: Array<Any> = arrayOf(v)
      val var10000: java.lang.String = java.lang.String.format(var2, "%.1f", Arrays.copyOf(var5, var5.length))
      return var10000
   }

   private fun fmt2(v: Float): String {
      val var2: Locale = Locale.US
      val var5: Array<Any> = arrayOf(v)
      val var10000: java.lang.String = java.lang.String.format(var2, "%.2f", Arrays.copyOf(var5, var5.length))
      return var10000
   }
}
