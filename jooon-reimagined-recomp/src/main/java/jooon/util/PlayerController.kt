package jooon.util

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.Locale
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import jooon.mixins.KeyBindingMixin
import kotlin.concurrent.ThreadsKt
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.InlineMarker
import net.minecraft.client.Keyboard
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyInput
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.Window
import net.minecraft.client.util.InputUtil.Key
import net.minecraft.client.util.InputUtil.Type
import org.lwjgl.glfw.GLFW

object PlayerController {
   private const val MIN_CLICK_INTERVAL_NS: Long = 50000000L
   private val nextClickAllowedNs: AtomicLong = AtomicLong(0L)
   private val lastHotbarSwapTick: AtomicLong = AtomicLong(java.lang.Long.MIN_VALUE)
   private var suppressMost: Boolean

   private val internalDepth: ThreadLocal<Int> = ThreadLocal.withInitial({ 
return 0
   })

   private var kbOnKey: Method?

   fun getMc(): MinecraftClient {
return var10000
   }

   fun setSuppressMost(enabled: Boolean) {
      suppressMost = enabled
   }

   fun isSuppressMost(): Boolean {
      return suppressMost
   }

   private inline fun <T> withInternal(block: () -> Any): Any {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var3: Any
      try {
         var3 = block()
      } finally {
         InlineMarker.finallyStart(1)
         internalDepth.set(internalDepth.get().intValue() - 1)
         InlineMarker.finallyEnd(1)
      }

      return (T)var3
   }

   private fun isInternal(): Boolean {
      return internalDepth.get().intValue() > 0
   }

   fun suppressMostFor(ms: Long): Thread {
      return this.tap({ 

         suppressMost = true

         while (System.currentTimeMillis() < until) {
            sleep(5L)
         }

         suppressMost = false
return Unit
      })
   }

   fun shouldBlockKey(glfwKey: Int, action: Int): Boolean {
      return suppressMost && !this.isInternal() && this.getMc().currentScreen == null && 49 <= glfwKey && glfwKey < 58
   }

   fun shouldBlockMouseButton(button: Int, action: Int): Boolean {
      return suppressMost && !this.isInternal() && this.getMc().currentScreen == null && (button == 0 || button == 1 || button == 2)
   }

   fun shouldBlockScroll(vertical: Double, horizontal: Double): Boolean {
      return suppressMost && !this.isInternal() && this.getMc().currentScreen == null && (vertical != 0.0 || horizontal != 0.0)
   }

   fun pressLeftMouse(down: Boolean) {
      this.pressAttack(down)
   }

   fun pressRightMouse(down: Boolean) {
      this.pressUse(down)
   }

   fun tapLeftMouse(ms: Long = 30L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            leftClick()
            if (`$ms` > 0L) {
               sleep(`$ms`)
            }
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun tapRightMouse(ms: Long = 30L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            rightClick()
            if (`$ms` > 0L) {
               sleep(`$ms`)
            }
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun leftClick(): Boolean {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var7: Boolean
      try {


         var7 = var10000.clickOnce(var10001)
      } finally {
         internalDepth.set(internalDepth.get().intValue() - 1)
      }

      return var7
   }

   fun rightClick(): Boolean {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var7: Boolean
      try {
         val var10000: Boolean
         if (hasHotbarSwapThisTick()) {
            var10000 = false
         } else {


            var10000 = var8.clickOnce(var10001)
         }

         var7 = var10000
      } finally {
         internalDepth.set(internalDepth.get().intValue() - 1)
      }

      return var7
   }

   fun rightClickOnceHuman(): Boolean {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var9: Boolean
      try {
         val var10000: Boolean
         if (suppressMost) {
            var10000 = false
         } else {
            sleep(ThreadLocalRandom.current().nextLong(10L, 23L))
            var10000 = rightClick()
         }

         var9 = var10000
      } finally {
         internalDepth.set(internalDepth.get().intValue() - 1)
      }

      return var9
   }

   fun rightClickOnceHumanAsync(): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            rightClickOnceHuman()
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun pressForward(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressBack(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressLeft(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressRight(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressJump(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressSneak(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressSprint(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressUse(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun pressAttack(down: Boolean) {

      this.keyBinding(var10001, down)
   }

   fun tapForward(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            pressForward(true)
            sleep(`$ms`)
            pressForward(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun tapBack(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            pressBack(true)
            sleep(`$ms`)
            pressBack(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun tapLeft(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            pressLeft(true)
            sleep(`$ms`)
            pressLeft(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun tapRight(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            pressRight(true)
            sleep(`$ms`)
            pressRight(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun pressHotbar(slot1to9: Int, down: Boolean) {
      if (1 <= slot1to9 && slot1to9 < 10) {

         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            keyboardKey(key, down)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
      }
   }

   fun tapHotbar(slot1to9: Int, ms: Long = 25L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            pressHotbar(`$slot1to9`, true)
            sleep(`$ms`)
            pressHotbar(`$slot1to9`, false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun tapKey(binding: KeyBinding, ms: Long): Thread {
      this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            keyBinding(`$binding`, true)
            sleep(`$ms`)
            keyBinding(`$binding`, false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
return Unit
      })
   }

   fun noteHotbarSwapThisTick() {

      if (var10000 != null) {
         lastHotbarSwapTick.set(var10000.age.toLong())
      }
   }

   fun keyBinding(binding: KeyBinding, down: Boolean) {


      try {
         var var14: PlayerController = key
         (binding as KeyBindingMixin).isDown = down
         var14 = Result(Unit)
      } catch (var11: java.lang.Throwable) {
         val `this_$iv`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var11))
      }

      if (var10000 != null) {

         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            if (var13.getCategory() != Type.MOUSE) {
               keyboardKey(var13.getCode(), down)
            }
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
      }
   }

   fun clickOnce(binding: KeyBinding): Boolean {


      val client: Long
      do {
         client = nextClickAllowedNs.get()
         if (now < client) {
return false
         }
      } while (!nextClickAllowedNs.compareAndSet(client, now + 50000000L))




         try {
            (`$binding` as KeyBindingMixin).clickCount = (`$binding` as KeyBindingMixin).clickCount + 1

         } catch (var5: java.lang.Throwable) {
            val `this24lambda_u2427_u24lambda_u2426`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
         }
return Unit
      }
      if (var6.isOnThread()) {
         click()
      } else {
         var6.execute({ 
            ``()
         })
      }
return true
   }

   private fun hasHotbarSwapThisTick(): Boolean {

      if (var10000 != null) {

         return lastHotbarSwapTick.get() == tick
      } else {
         return false
      }
   }

   private fun keyboardKey(glfwKeyCode: Int, down: Boolean) {

      if (var10000 != null) {



         var `this_$iv`: PlayerController
         try {
            `this_$iv` = action
            `this_$iv` = Result(GLFW.glfwGetKeyScancode(glfwKeyCode))
         } catch (var14: java.lang.Throwable) {
            `this_$iv` = Result(ResultKt.createFailure(var14))
         }


         internalDepth.set(internalDepth.get().intValue() + 1)

         try {

         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
      }
   }

   private fun tryInvokeKeyboardOnKey(win: Long, key: Int, sc: Int, act: Int, mods: Int): Boolean {
      var kb: Boolean
      try {


         var var17: Method = kbOnKey
         if (kbOnKey == null) {

            var16.setAccessible(true)
            kbOnKey = var16
            var17 = var16
         }

         var17.invoke(var10000, win, act, var8)
         kb = true
      } catch (var14: java.lang.Throwable) {
         kb = false
      }

      return kb
   }

   fun keyOf(binding: KeyBinding): Key {
      var var2: PlayerController = this

      try {
         var var19: PlayerController = var2

         val ``: Key = k as? Key
         if ((k as? Key) != null) {
            ``
         }

         var19 = Result(Unit)
      } catch (var15: java.lang.Throwable) {
         val `this24lambda_u2435`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
      }

      var2 = this

      try {
         var var22: PlayerController = var2

         var29.setAccessible(true)


         if ((var32 as? Key) != null) {
return kx
         }

         var22 = Result(Unit)
      } catch (var14: java.lang.Throwable) {

      }

      var2 = this

      try {
         var var25: PlayerController = var2
         var var10000: Method = KeyBinding::class.java.getMethods()
         val var30: Array<Any> = var10000 as Array<Any>
         var var35: Int = 0


         while (true) {
            if (var35 >= var8) {
               var10000 = null
break
            }

            val `element$iv`: Any = var30[var35]



            if (it.getParameterCount() == 0 && (var37 == "getdefaultkey" || var37 == "defaultkey")) {
               var10000 = (Method)`element$iv`
break
            }

            var35++
         }


         if ((var34 as? Key) != null) {
return var31
         }

         var25 = Result(Unit)
      } catch (var16: java.lang.Throwable) {

      }
return null
   }

   private fun tap(block: () -> Unit): Thread {
      return ThreadsKt.thread$default(true, true, null, null, 0, { 
         `$block`()
return Unit
      }, 28, null)
   }

   private fun sleep(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
