package jooon.features.jerry

import com.mojang.authlib.GameProfile
import java.time.Instant
import java.util.Locale
import jooon.config.Config
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.network.message.SignedMessage
import net.minecraft.network.message.MessageType.Parameters
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object JerryAlert {
   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientReceiveMessageEvents.CHAT
         .register(
            { message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         ClientReceiveMessageEvents.GAME
         .register(
            { message: Text, overlay: Boolean ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
      }

   private fun handleChatMessage(message: String) {

         Regex("☺\\s*(?:You found a|Some|A wild|A|There is a|You located a hidden)\\s+(\\w+)\\s+Jerry!?"), this.stripFormattingCodes(message), 0, 2, null
      )
      if (var10000 != null) {

var var9: JerryAlert.JerryType
         when (var8.hashCode()) {
            -1240337143 -> {
               if (!var8.equals("golden")) {
return return
               }

               var9 = JerryAlert.JerryType.GOLDEN
break
            }
            -976943172 -> {
               if (!var8.equals("purple")) {
return return
               }

               var9 = JerryAlert.JerryType.PURPLE
break
            }
            3027034 -> {
               if (!var8.equals("blue")) {
return return
               }

               var9 = JerryAlert.JerryType.BLUE
break
            }
            98619139 -> {
               if (var8.equals("green")) {
                  var9 = JerryAlert.JerryType.GREEN
break
               }
return return
            }
            else -> return
         }

         this.showAlert(var9, message)
      }
   }

   private fun stripFormattingCodes(text: String): String {
      return Regex("§[0-9a-fk-or]").replace(text, "")
   }

   private fun showAlert(jerryType: jooon.features.jerry.JerryAlert.JerryType, originalMessage: String) {
      this.playNotificationSound()


      this.getMc().inGameHud.setTitle(var10000 as Text)
      this.getMc().inGameHud.setSubtitle(var5 as Text)
   }

   private fun playNotificationSound() {
      if (Config.jerryAlertSoundEnabled) {

         this.getMc().getSoundManager().play(var10000 as SoundInstance)
      }
   }

   fun testAlert(jerryTypeName: String) {

var var5: JerryAlert.JerryType
      when (var10000.hashCode()) {
         -1240337143 -> {
            if (!var10000.equals("golden")) {
return return
            }

            var5 = JerryAlert.JerryType.GOLDEN
break
         }
         -976943172 -> {
            if (!var10000.equals("purple")) {
return return
            }

            var5 = JerryAlert.JerryType.PURPLE
break
         }
         3027034 -> {
            if (!var10000.equals("blue")) {
return return
            }

            var5 = JerryAlert.JerryType.BLUE
break
         }
         98619139 -> {
            if (var10000.equals("green")) {
               var5 = JerryAlert.JerryType.GREEN
break
            }
return return
         }
         else -> return
      }

      this.showAlert(var5, "☺ You found a ${var5.displayName}!")
   }

   private enum class JerryType {
      GREEN("Green Jerry", Formatting.GREEN),
      BLUE("Blue Jerry", Formatting.BLUE),
      PURPLE("Purple Jerry", Formatting.DARK_PURPLE),
      GOLDEN("Golden Jerry", Formatting.GOLD);

      val displayName: String
      private Formatting formatting;

      fun JerryType(displayName: String, formatting: Formatting) {
         this.displayName = displayName
         this.formatting = formatting
      }

      fun getFormatting(): Formatting {
         this.formatting
      }

      
      fun getEntries(): EnumEntries<JerryAlert.JerryType> {
         $ENTRIES
      }
   }
}
