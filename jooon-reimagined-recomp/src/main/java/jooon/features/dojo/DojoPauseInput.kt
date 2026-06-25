package jooon.features.dojo

import java.lang.reflect.Field
import java.lang.reflect.Method
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.minecraft.client.util.InputUtil.Key
import org.lwjgl.glfw.GLFW

object DojoPauseInput {
   private var previousSneakDown: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun consumeSneakTap(): Boolean {



      if (var5 == null) {
         return false
      } else if (var5.getCode() < 0) {
         previousSneakDown = false
         return false
      } else {

            GLFW.glfwGetMouseButton(var10000.getHandle(), var5.getCode()) == 1
return else
            InputUtil.isKeyPressed(var10000, var5.getCode())

         previousSneakDown = down
         return tapped
      }
   }

   fun reset() {
      previousSneakDown = false
   }

   fun boundKey(binding: KeyBinding): Key {


      try {


         var22 as? Key
      } catch (var16: java.lang.Throwable) {
         Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var16))

         try {

            var21.setAccessible(true)

            var23 as? Key
         } catch (var15: java.lang.Throwable) {
            Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))

            var `this24lambda_u243`: Any
            try {
               var var10000: Method = KeyBinding::class.java.getMethods()
               val `this$iv`: Array<Any> = var10000 as Array<Any>
               var var7: Int = 0


               while (true) {
                  if (var7 >= var8) {
                     var10000 = null
break
                  }

                  val `element$iv`: Any = `this$iv`[var7]
                  if ((`this$iv`[var7] as Method).getReturnType() == Key::class.java
                     && (`this$iv`[var7] as Method).getParameterCount() == 0) {
                     var10000 = (Method)`element$iv`
break
                  }

                  var7++
               }

               `this24lambda_u243` = Result.constructor_impl/* $VF was: constructor-impl */(var13 as? Key)
            } catch (var14: java.lang.Throwable) {
               `this24lambda_u243` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
            }

            (if (Result.isFailure/* $VF was: isFailure-impl */(`this24lambda_u243`)) null else `this24lambda_u243`) as Key
         }
      }
   }
}
