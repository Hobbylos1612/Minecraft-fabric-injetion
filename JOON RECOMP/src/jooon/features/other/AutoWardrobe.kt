package jooon.features.other

import java.util.Timer
import jooon.config.Config
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.client.render.RenderTickCounter

@SourceDebugExtension(["SMAP\nAutoWardrobe.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoWardrobe.kt\njooon/features/other/AutoWardrobe\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,112:1\n13309#2,2:113\n*S KotlinDebug\n*F\n+ 1 AutoWardrobe.kt\njooon/features/other/AutoWardrobe\n*L\n66#1:113,2\n*E\n"])
public object AutoWardrobe {
   @JvmField
   public final var active: Boolean
      private set

   @JvmField
   public final var targetSlot: Int
      private set

   public final var cwid: Int = -1
   private final var showOverlayUntil: Long
   @JvmStatic
   private KeyBinding[] wardrobeKeys;

   fun getClient(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun triggerDirect(slot: Int) {
      targetSlot = slot
      if (Config.autoWardrobeEnabled) {
         showOverlayUntil = System.currentTimeMillis() + 650L
         this.openMenu()
      }
   }

   private fun openMenu() {
      active = true
      val var10000: ClientPlayerEntity = this.getClient().field_1724
      if (var10000 != null && var10000.field_3944 != null) {
         var10000.field_3944.method_45730("wd")
      }
   }

   public fun onInitializeClient() {
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

   public fun handleOpen(title: String, windowId: Int) {
      if (active) {
         if (StringsKt.contains$default(title, "Wardrobe", false, 2, null)) {
            cwid = windowId
         }
      }
   }

   public fun handleSlotUpdate(slot: Int) {
      if (active && Config.autoWardrobeEnabled) {
         if (slot == targetSlot) {
            val clickWindow: Int = cwid
            val var3: Timer = Timer()
            val var4: Long = Config.autoWardrobeDelay + 150L
            var3.schedule(AutoWardrobe$handleSlotUpdate$$inlined$schedule$1(clickWindow, slot), var4)
         } else if (slot > 45) {
            active = false
            cwid = -1
         }
      }
   }

   @JvmStatic
   fun {
      var var0: Int = 0
      val var1: Array<KeyBinding> = arrayOfNulls(9)

      while (var0 < 9) {
         var1[var0] = KeyBinding("key.jooonreimagined.wardrobe_slot_${var0 + 1}", -1, Category.field_62556)
         var0++
      }

      wardrobeKeys = var1
   }
}
