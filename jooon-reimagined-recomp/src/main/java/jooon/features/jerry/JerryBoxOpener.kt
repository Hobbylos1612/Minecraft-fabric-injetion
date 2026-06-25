package jooon.features.jerry

import jooon.config.Config
import jooon.util.PlayerController
import kotlin.jvm.internal.Intrinsics
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text

object JerryBoxOpener {
   private var opening: Boolean
   private val waitTimer: jooon.features.jerry.JerryBoxOpener.TimeHelper = JerryBoxOpener.TimeHelper()
   private var hasClickedThisWindow: Boolean
   
   private KeyBinding toggleKey;

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      waitTimer.reset()
      opening = false
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.jerryboxopener", 74, Category.MISC))
      ScreenEvents.AFTER_INIT.register({ var0: MinecraftClient, var1: Screen, var2: Int, var3: Int ->
         hasClickedThisWindow = false
      })
      ScreenEvents.AFTER_INIT
         .register(
            { client: MinecraftClient, screen: Screen, scaledWidth: Int, scaledHeight: Int ->
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
         ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         var var10000: KeyBinding = toggleKey
         if (toggleKey == null) {
            throwUninitializedPropertyAccessException("toggleKey")
            var10000 = null
         }

         if (var10000.wasPressed()) {
            toggle()
         }

         if (Config.jerryBoxOpenerEnabled && Config.Companion.jerryBoxOpenerActive) {
            handleClientTick()
         }
      })
   }

   private fun handleClientTick() {

      if (var10000 != null) {
         if (!var10000.getMainHandStack().isEmpty()) {


            if (var5.contains("Jerry Box", true) && !opening) {
               this.rightClick()
               opening = true
            }
         }
      }
   }

   private fun rightClick() {
      PlayerController.rightClick()
   }

   fun toggle() {
      if (Config.jerryBoxOpenerEnabled) {
         Config.Companion.jerryBoxOpenerActive = !Config.Companion.jerryBoxOpenerActive
         if (!Config.Companion.jerryBoxOpenerActive) {
            opening = false
            hasClickedThisWindow = false
         } else {
            opening = false
            hasClickedThisWindow = false
         }

            Text.literal("§a§lJooonReimagined §7»  §aJerry Box Opener: §aEnabled!")
return else
            Text.literal("§a§lJooonReimagined §7»  §aJerry Box Opener: §cDisabled!")

         if (var10000 != null) {
            var10000.sendMessage(var2 as Text, false)
         }
      }
   }

   private class TimeHelper {
      private var lastMS: Long

      fun reset() {
         this.lastMS = this.getCurrentMS()
      }

      fun getCurrentMS(): Long {
         return System.nanoTime() / 1000000L
      }

      fun hasReached(milliseconds: Long): Boolean {
         return this.getCurrentMS() - this.lastMS >= milliseconds
      }

      fun hasTimeReached(delay: Long): Boolean {
         return System.currentTimeMillis() - this.lastMS >= delay
      }

      fun getDelay(): Long {
         return System.currentTimeMillis() - this.lastMS
      }
   }
}
