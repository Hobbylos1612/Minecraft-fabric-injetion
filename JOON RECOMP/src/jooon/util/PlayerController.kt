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
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.Keyboard
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyInput
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.Window
import net.minecraft.client.util.InputUtil.Key
import net.minecraft.client.util.InputUtil.Type
import org.lwjgl.glfw.GLFW

@SourceDebugExtension(["SMAP\nPlayerController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PlayerController.kt\njooon/util/PlayerController\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,209:1\n28#1,2:210\n28#1,2:212\n28#1,2:214\n28#1,2:216\n28#1,2:219\n28#1,2:221\n28#1,2:225\n28#1,2:227\n28#1,2:229\n28#1,2:231\n28#1,2:233\n28#1,2:235\n28#1,2:237\n28#1,2:239\n28#1,2:241\n1#2:218\n1282#3,2:223\n*S KotlinDebug\n*F\n+ 1 PlayerController.kt\njooon/util/PlayerController\n*L\n70#1:210,2\n71#1:212,2\n76#1:214,2\n106#1:216,2\n125#1:219,2\n165#1:221,2\n68#1:225,2\n69#1:227,2\n82#1:229,2\n96#1:231,2\n97#1:233,2\n99#1:235,2\n100#1:237,2\n109#1:239,2\n112#1:241,2\n195#1:223,2\n*E\n"])
public object PlayerController {
   private const val MIN_CLICK_INTERVAL_NS: Long = 50000000L
   private final val nextClickAllowedNs: AtomicLong = AtomicLong(0L)
   private final val lastHotbarSwapTick: AtomicLong = AtomicLong(java.lang.Long.MIN_VALUE)
   private final var suppressMost: Boolean

   private final val internalDepth: ThreadLocal<Int> = ThreadLocal.withInitial({ 
      0
   })

   private final var kbOnKey: Method?

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun setSuppressMost(enabled: Boolean) {
      suppressMost = enabled
   }

   public fun isSuppressMost(): Boolean {
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

   public fun suppressMostFor(ms: Long): Thread {
      return this.tap({ 
         val until: Long = System.currentTimeMillis() + `$ms`
         suppressMost = true

         while (System.currentTimeMillis() < until) {
            INSTANCE.sleep(5L)
         }

         suppressMost = false
         Unit.INSTANCE
      })
   }

   public fun shouldBlockKey(glfwKey: Int, action: Int): Boolean {
      return suppressMost && !this.isInternal() && this.getMc().field_1755 == null && 49 <= glfwKey && glfwKey < 58
   }

   public fun shouldBlockMouseButton(button: Int, action: Int): Boolean {
      return suppressMost && !this.isInternal() && this.getMc().field_1755 == null && (button == 0 || button == 1 || button == 2)
   }

   public fun shouldBlockScroll(vertical: Double, horizontal: Double): Boolean {
      return suppressMost && !this.isInternal() && this.getMc().field_1755 == null && (vertical != 0.0 || horizontal != 0.0)
   }

   public fun pressLeftMouse(down: Boolean) {
      this.pressAttack(down)
   }

   public fun pressRightMouse(down: Boolean) {
      this.pressUse(down)
   }

   public fun tapLeftMouse(ms: Long = 30L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.leftClick()
            if (`$ms` > 0L) {
               INSTANCE.sleep(`$ms`)
            }
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun tapRightMouse(ms: Long = 30L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.rightClick()
            if (`$ms` > 0L) {
               INSTANCE.sleep(`$ms`)
            }
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun leftClick(): Boolean {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var7: Boolean
      try {
         val var10000: PlayerController = INSTANCE
         val var10001: KeyBinding = INSTANCE.getMc().field_1690.field_1886
         var7 = var10000.clickOnce(var10001)
      } finally {
         internalDepth.set(internalDepth.get().intValue() - 1)
      }

      return var7
   }

   public fun rightClick(): Boolean {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var7: Boolean
      try {
         val var10000: Boolean
         if (INSTANCE.hasHotbarSwapThisTick()) {
            var10000 = false
         } else {
            val var8: PlayerController = INSTANCE
            val var10001: KeyBinding = INSTANCE.getMc().field_1690.field_1904
            var10000 = var8.clickOnce(var10001)
         }

         var7 = var10000
      } finally {
         internalDepth.set(internalDepth.get().intValue() - 1)
      }

      return var7
   }

   public fun rightClickOnceHuman(): Boolean {
      internalDepth.set(internalDepth.get().intValue() + 1)

      var var9: Boolean
      try {
         val var10000: Boolean
         if (suppressMost) {
            var10000 = false
         } else {
            INSTANCE.sleep(ThreadLocalRandom.current().nextLong(10L, 23L))
            var10000 = INSTANCE.rightClick()
         }

         var9 = var10000
      } finally {
         internalDepth.set(internalDepth.get().intValue() - 1)
      }

      return var9
   }

   public fun rightClickOnceHumanAsync(): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.rightClickOnceHuman()
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun pressForward(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1894
      this.keyBinding(var10001, down)
   }

   public fun pressBack(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1881
      this.keyBinding(var10001, down)
   }

   public fun pressLeft(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1913
      this.keyBinding(var10001, down)
   }

   public fun pressRight(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1849
      this.keyBinding(var10001, down)
   }

   public fun pressJump(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1903
      this.keyBinding(var10001, down)
   }

   public fun pressSneak(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1832
      this.keyBinding(var10001, down)
   }

   public fun pressSprint(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1867
      this.keyBinding(var10001, down)
   }

   public fun pressUse(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1904
      this.keyBinding(var10001, down)
   }

   public fun pressAttack(down: Boolean) {
      val var10001: KeyBinding = this.getMc().field_1690.field_1886
      this.keyBinding(var10001, down)
   }

   public fun tapForward(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.pressForward(true)
            INSTANCE.sleep(`$ms`)
            INSTANCE.pressForward(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun tapBack(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.pressBack(true)
            INSTANCE.sleep(`$ms`)
            INSTANCE.pressBack(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun tapLeft(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.pressLeft(true)
            INSTANCE.sleep(`$ms`)
            INSTANCE.pressLeft(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun tapRight(ms: Long = 40L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.pressRight(true)
            INSTANCE.sleep(`$ms`)
            INSTANCE.pressRight(false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun pressHotbar(slot1to9: Int, down: Boolean) {
      if (1 <= slot1to9 && slot1to9 < 10) {
         val key: Int = 48 + slot1to9
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.keyboardKey(key, down)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
      }
   }

   public fun tapHotbar(slot1to9: Int, ms: Long = 25L): Thread {
      return this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.pressHotbar(`$slot1to9`, true)
            INSTANCE.sleep(`$ms`)
            INSTANCE.pressHotbar(`$slot1to9`, false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   fun tapKey(binding: KeyBinding, ms: Long): Thread {
      this.tap({ 
         val `this_$iv`: PlayerController = INSTANCE
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            INSTANCE.keyBinding(`$binding`, true)
            INSTANCE.sleep(`$ms`)
            INSTANCE.keyBinding(`$binding`, false)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }

         Unit.INSTANCE
      })
   }

   public fun noteHotbarSwapThisTick() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         lastHotbarSwapTick.set((long)var10000.field_6012)
      }
   }

   fun keyBinding(binding: KeyBinding, down: Boolean) {
      val key: PlayerController = this

      try {
         var var14: PlayerController = key
         (binding as KeyBindingMixin).isDown = down
         var14 = (PlayerController)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var11: java.lang.Throwable) {
         val `this_$iv`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var11))
      }

      val var10000: Key = this.keyOf(binding)
      if (var10000 != null) {
         val var13: Key = var10000
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            if (var13.method_1442() != Type.field_1672) {
               INSTANCE.keyboardKey(var13.method_1444(), down)
            }
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
      }
   }

   fun clickOnce(binding: KeyBinding): Boolean {
      val now: Long = System.nanoTime()

      val client: Long
      do {
         client = nextClickAllowedNs.get()
         if (now < client) {
            false
         }
      } while (!nextClickAllowedNs.compareAndSet(client, now + 50000000L))

      val var6: MinecraftClient = this.getMc()
      val click: Function0 = { 
         val var1: PlayerController = INSTANCE

         try {
            (`$binding` as KeyBindingMixin).clickCount = (`$binding` as KeyBindingMixin).clickCount + 1
            val var6: Any = Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var5: java.lang.Throwable) {
            val `$this$clickOnce_u24lambda_u2427_u24lambda_u2426`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
         }

         Unit.INSTANCE
      }
      if (var6.method_18854()) {
         click()
      } else {
         var6.execute({ 
            `$tmp0`()
         })
      }

      true
   }

   private fun hasHotbarSwapThisTick(): Boolean {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val tick: Long = var10000.field_6012
         return lastHotbarSwapTick.get() == tick
      } else {
         return false
      }
   }

   private fun keyboardKey(glfwKeyCode: Int, down: Boolean) {
      val var10000: Window = this.getMc().method_22683()
      if (var10000 != null) {
         val window: Window = var10000
         val action: PlayerController = this

         var `this_$iv`: PlayerController
         try {
            `this_$iv` = action
            `this_$iv` = (PlayerController)Result.constructor_impl/* $VF was: constructor-impl */(GLFW.glfwGetKeyScancode(glfwKeyCode))
         } catch (var14: java.lang.Throwable) {
            `this_$iv` = (PlayerController)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
         }

         val scancode: Int = ((if (Result.isFailure_impl/* $VF was: isFailure-impl */(`this_$iv`)) 0 else `this_$iv`) as java.lang.Number).intValue()
         val var16: Int = if (down) 1 else 0
         internalDepth.set(internalDepth.get().intValue() + 1)

         try {
            val var9: Boolean = INSTANCE.tryInvokeKeyboardOnKey(window.method_4490(), glfwKeyCode, scancode, var16, 0)
         } finally {
            internalDepth.set(internalDepth.get().intValue() - 1)
         }
      }
   }

   private fun tryInvokeKeyboardOnKey(win: Long, key: Int, sc: Int, act: Int, mods: Int): Boolean {
      var kb: Boolean
      try {
         val var10000: Keyboard = this.getMc().field_1774
         val var8: KeyInput = KeyInput(key, sc, mods)
         var var17: Method = kbOnKey
         if (kbOnKey == null) {
            val var16: Method = var10000.getClass().getDeclaredMethod("keyPress", long.class, int.class, KeyInput.class)
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
         val k: Any = KeyBinding.class.getMethod("getBoundKey").invoke(binding)
         val `$i$f$firstOrNull`: Key = k as? Key
         if ((k as? Key) != null) {
            `$i$f$firstOrNull`
         }

         var19 = (PlayerController)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var15: java.lang.Throwable) {
         val `$this$keyOf_u24lambda_u2435`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
      }

      var2 = this

      try {
         var var22: PlayerController = var2
         val var29: Field = KeyBinding.class.getDeclaredField("key")
         var29.setAccessible(true)
         val var32: Any = var29.get(binding)
         val kx: Key = var32 as? Key
         if ((var32 as? Key) != null) {
            kx
         }

         var22 = (PlayerController)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var14: java.lang.Throwable) {
         val var21: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
      }

      var2 = this

      try {
         var var25: PlayerController = var2
         var var10000: Method = KeyBinding.class.getMethods()
         val var30: Array<Any> = var10000 as Array<Any>
         var var35: Int = 0
         val var8: Int = var30.length

         while (true) {
            if (var35 >= var8) {
               var10000 = null
               break
            }

            val `element$iv`: Any = var30[var35]
            val it: Method = var30[var35] as Method
            val var36: java.lang.String = (var30[var35] as Method).getName()
            val var37: java.lang.String = var36.toLowerCase(Locale.ROOT)
            if (it.getParameterCount() == 0 && (var37 == "getdefaultkey" || var37 == "defaultkey")) {
               var10000 = (Method)`element$iv`
               break
            }

            var35++
         }

         val var34: Any = if (var10000 as Method != null) var10000.invoke(binding) else null
         val var31: Key = var34 as? Key
         if ((var34 as? Key) != null) {
            var31
         }

         var25 = (PlayerController)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
      } catch (var16: java.lang.Throwable) {
         val var24: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var16))
      }

      null
   }

   private fun tap(block: () -> Unit): Thread {
      return ThreadsKt.thread$default(true, true, null, null, 0, { 
         `$block`()
         Unit.INSTANCE
      }, 28, null)
   }

   private fun sleep(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
