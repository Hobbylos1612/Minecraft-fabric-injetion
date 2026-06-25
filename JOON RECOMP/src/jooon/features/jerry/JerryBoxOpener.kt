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

public object JerryBoxOpener {
   private final var opening: Boolean
   private final val waitTimer: jooon.features.jerry.JerryBoxOpener.TimeHelper = JerryBoxOpener.TimeHelper()
   private final var hasClickedThisWindow: Boolean
   @JvmStatic
   private KeyBinding toggleKey;

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      waitTimer.reset()
      opening = false
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.jerryboxopener", 74, Category.field_62556))
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
            Intrinsics.throwUninitializedPropertyAccessException("toggleKey")
            var10000 = null
         }

         if (var10000.method_1436()) {
            INSTANCE.toggle()
         }

         if (Config.jerryBoxOpenerEnabled && Config.Companion.jerryBoxOpenerActive) {
            INSTANCE.handleClientTick()
         }
      })
   }

   private fun handleClientTick() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         if (!var10000.method_6047().method_7960()) {
            val var4: ItemStack = var10000.method_6047()
            val var5: java.lang.String = var4.method_7964().getString()
            if (StringsKt.contains(var5, "Jerry Box", true) && !opening) {
               this.rightClick()
               opening = true
            }
         }
      }
   }

   private fun rightClick() {
      PlayerController.INSTANCE.rightClick()
   }

   public fun toggle() {
      if (Config.jerryBoxOpenerEnabled) {
         Config.Companion.jerryBoxOpenerActive = !Config.Companion.jerryBoxOpenerActive
         if (!Config.Companion.jerryBoxOpenerActive) {
            opening = false
            hasClickedThisWindow = false
         } else {
            opening = false
            hasClickedThisWindow = false
         }

         val var2: MutableText = if (Config.Companion.jerryBoxOpenerActive)
            Text.method_43470("§a§lJooonReimagined §7»  §aJerry Box Opener: §aEnabled!")
            else
            Text.method_43470("§a§lJooonReimagined §7»  §aJerry Box Opener: §cDisabled!")
            val var10000: ClientPlayerEntity = this.getMc().field_1724
         if (var10000 != null) {
            var10000.method_7353(var2 as Text, false)
         }
      }
   }

   private class TimeHelper {
      private final var lastMS: Long

      public fun reset() {
         this.lastMS = this.getCurrentMS()
      }

      public fun getCurrentMS(): Long {
         return System.nanoTime() / 1000000L
      }

      public fun hasReached(milliseconds: Long): Boolean {
         return this.getCurrentMS() - this.lastMS >= milliseconds
      }

      public fun hasTimeReached(delay: Long): Boolean {
         return System.currentTimeMillis() - this.lastMS >= delay
      }

      public fun getDelay(): Long {
         return System.currentTimeMillis() - this.lastMS
      }
   }
}
