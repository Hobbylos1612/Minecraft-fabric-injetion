package jooon.features.other

import java.util.Timer
import jooon.config.Config
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.client.render.RenderTickCounter

object AutoWardrobe {
   @JvmField
   var active: Boolean
      private set

   @JvmField
   var targetSlot: Int
      private set

   var cwid: Int = -1
   private var showOverlayUntil: Long
   
   private KeyBinding[] wardrobeKeys;

   fun getClient(): MinecraftClient {
return var10000
   }

   fun triggerDirect(slot: Int) {
      targetSlot = slot
      if (Config.autoWardrobeEnabled) {
         showOverlayUntil = System.currentTimeMillis() + 650L
         this.openMenu()
      }
   }

   private fun openMenu() {
      active = true

      if (var10000 != null && var10000.networkHandler != null) {
         var10000.networkHandler.sendChatCommand("wd")
      }
   }

   fun onInitializeClient() {
      HudRenderCallback.EVENT
         .register(
            { context: DrawContext, var1: RenderTickCounter ->
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

      for (`element$iv` in wardrobeKeys) {
         KeyBindingHelper.registerKeyBinding((KeyBinding)`element$iv`)
      }

      ClientTickEvents.END_CLIENT_TICK
         .register(
            { client: MinecraftClient ->
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

   fun handleOpen(title: String, windowId: Int) {
      if (active) {
         if (contains$default(title, "Wardrobe", false, 2, null)) {
            cwid = windowId
         }
      }
   }

   fun handleSlotUpdate(slot: Int) {
      if (active && Config.autoWardrobeEnabled) {
         if (slot == targetSlot) {



            var3.schedule(AutoWardrobe$handleSlotUpdate$$inlined$schedule$1(clickWindow, slot), var4)
         } else if (slot > 45) {
            active = false
            cwid = -1
         }
      }
   }

   
   fun {
      var var0: Int = 0
      val var1: Array<KeyBinding> = arrayOfNulls(9)

      while (var0 < 9) {
         var1[var0] = KeyBinding("key.jooonreimagined.wardrobe_slot_${var0 + 1}", -1, Category.MISC)
         var0++
      }

      wardrobeKeys = var1
   }
}
