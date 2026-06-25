package jooon.features.other

import java.util.TimerTask
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nTimer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Timer.kt\nkotlin/concurrent/TimersKt$timerTask$1\n+ 2 AutoWardrobe.kt\njooon/features/other/AutoWardrobe\n*L\n1#1,148:1\n96#2,10:149\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `AutoWardrobe$handleSlotUpdate$$inlined$schedule$1` : TimerTask {
   fun `AutoWardrobe$handleSlotUpdate$$inlined$schedule$1`(var1: Int, var2: Int) {
      this.$clickWindow$inlined = var1
      this.$slot$inlined = var2
   }

   public override fun run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.vineflower.kotlin.expr.KFunctionExprent.toJava(KFunctionExprent.java:196)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
      //
      // Bytecode:
      // 00: aload 0
      // 01: checkcast java/util/TimerTask
      // 04: astore 1
      // 05: bipush 0
      // 06: istore 2
      // 07: getstatic jooon/features/other/AutoWardrobe.INSTANCE Ljooon/features/other/AutoWardrobe;
      // 0a: invokestatic jooon/features/other/AutoWardrobe.access$getClient (Ljooon/features/other/AutoWardrobe;)Lnet/minecraft/client/MinecraftClient;
      // 0d: new jooon/features/other/AutoWardrobe$handleSlotUpdate$1$1
      // 10: dup
      // 11: aload 0
      // 12: getfield jooon/features/other/AutoWardrobe$handleSlotUpdate$$inlined$schedule$1.$clickWindow$inlined I
      // 15: aload 0
      // 16: getfield jooon/features/other/AutoWardrobe$handleSlotUpdate$$inlined$schedule$1.$slot$inlined I
      // 19: invokespecial jooon/features/other/AutoWardrobe$handleSlotUpdate$1$1.<init> (II)V
      // 1c: checkcast java/lang/Runnable
      // 1f: invokevirtual net/minecraft/client/MinecraftClient.execute (Ljava/lang/Runnable;)V
      // 22: nop
      // 23: return
   }
}
