package jooon.features.dojo

import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.minecraft.client.util.InputUtil.Key
import org.lwjgl.glfw.GLFW

@SourceDebugExtension(["SMAP\nDojoPauseInput.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DojoPauseInput.kt\njooon/features/dojo/DojoPauseInput\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,56:1\n1282#2,2:57\n*S KotlinDebug\n*F\n+ 1 DojoPauseInput.kt\njooon/features/dojo/DojoPauseInput\n*L\n51#1:57,2\n*E\n"])
public object DojoPauseInput {
   private final var previousSneakDown: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun consumeSneakTap(): Boolean {
      val var10000: Window = this.getMc().method_22683()
      val var10001: KeyBinding = this.getMc().field_1690.field_1832
      val var5: Key = this.boundKey(var10001)
      if (var5 == null) {
         return false
      } else if (var5.method_1444() < 0) {
         previousSneakDown = false
         return false
      } else {
         val down: Boolean = if (DojoPauseInput.WhenMappings.$EnumSwitchMapping$0[var5.method_1442().ordinal()] == 1)
            GLFW.glfwGetMouseButton(var10000.method_4490(), var5.method_1444()) == 1
            else
            InputUtil.method_15987(var10000, var5.method_1444())
            val tapped: Boolean = down && !previousSneakDown
         previousSneakDown = down
         return tapped
      }
   }

   public fun reset() {
      previousSneakDown = false
   }

   fun boundKey(binding: KeyBinding): Key {
      val var2: DojoPauseInput = this

      try {
         val var18: DojoPauseInput = var2
         val var22: Any = KeyBinding.class.getMethod("getBoundKey").invoke(binding)
         var22 as? Key
      } catch (var16: java.lang.Throwable) {
         Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var16))

         try {
            val var21: Field = KeyBinding.class.getDeclaredField("key")
            var21.setAccessible(true)
            val var23: Any = var21.get(binding)
            var23 as? Key
         } catch (var15: java.lang.Throwable) {
            Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))

            var `$this$boundKey_u24lambda_u243`: Any
            try {
               var var10000: Method = KeyBinding.class.getMethods()
               val `$this$firstOrNull$iv`: Array<Any> = var10000 as Array<Any>
               var var7: Int = 0
               val var8: Int = `$this$firstOrNull$iv`.length

               while (true) {
                  if (var7 >= var8) {
                     var10000 = null
                     break
                  }

                  val `element$iv`: Any = `$this$firstOrNull$iv`[var7]
                  if ((`$this$firstOrNull$iv`[var7] as Method).getReturnType() == Key::class.java
                     && (`$this$firstOrNull$iv`[var7] as Method).getParameterCount() == 0) {
                     var10000 = (Method)`element$iv`
                     break
                  }

                  var7++
               }

               val var13: Any = if (var10000 as Method != null) var10000.invoke(binding) else null
               `$this$boundKey_u24lambda_u243` = Result.constructor_impl/* $VF was: constructor-impl */(var13 as? Key)
            } catch (var14: java.lang.Throwable) {
               `$this$boundKey_u24lambda_u243` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
            }

            (if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$boundKey_u24lambda_u243`)) null else `$this$boundKey_u24lambda_u243`) as Key
         }
      }
   }
}
