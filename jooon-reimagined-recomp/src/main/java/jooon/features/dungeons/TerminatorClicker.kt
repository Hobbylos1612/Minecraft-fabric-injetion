package jooon.features.dungeons

import java.util.Locale
import java.util.concurrent.ThreadLocalRandom
import jooon.config.Config
import jooon.util.PlayerController
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.Window
import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import org.lwjgl.glfw.GLFW

object TerminatorClicker {
   private var workerStarted: Boolean
   private var lastClickNs: Long
   private val STRIP_COLOR: Regex = Regex("(?i)§[0-9A-FK-OR]")

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      if (!workerStarted) {
         workerStarted = true
         val `this24lambda_u241`: Thread = Thread({ 
            runLoop()
         }, "Jooon-TerminatorClicker")
         `this24lambda_u241`.setDaemon(true)
         `this24lambda_u241`.start()
      }
   }

   private fun runLoop() {
      while (true) {
         if (!Config.terminatorClickerEnabled) {
            lastClickNs = 0L
            this.sleepMs(10L)
         } else {

            if (client == null) {
               this.sleepMs(100L)
            } else {


               var rmbDown: TerminatorClicker
               try {
                  rmbDown = player
                  rmbDown = Result(client.getWindow())
               } catch (var16: java.lang.Throwable) {
                  rmbDown = Result(ResultKt.createFailure(var16))
               }

               if (window == null) {
                  this.sleepMs(100L)
               } else if (client.player == null) {
                  this.sleepMs(20L)
               } else if (client.mouse.isCursorLocked() && client.currentScreen == null) {
                  if (GLFW.glfwGetMouseButton(window.getHandle(), 1) != 1) {
                     lastClickNs = 0L
                     this.sleepMs(2L)
                  } else {

                     if (var10000 != null) {

                        if (var23 != null) {



                           if (contains$default(var25, "terminator", false, 2, null) && var23.getItem() is BowItem) {


                              if (lastClickNs == 0L || now - lastClickNs >= basePeriodNs) {
                                 this.doLeftClick()
                                 lastClickNs = now + ThreadLocalRandom.current().nextLong(3L, 79L) * 1000000L
                              }

                              if ((basePeriodNs - (System.nanoTime() - lastClickNs)).coerceAtLeast(0L) > 2000000L) {
                                 this.sleepMs(1L)
return continue
                              }

                              Thread.onSpinWait()
return continue
                           }

                           lastClickNs = 0L
                           this.sleepMs(4L)
return continue
                        }
                     }
return return
                  }
               } else {
                  lastClickNs = 0L
                  this.sleepMs(5L)
               }
            }
         }
      }
   }

   private fun doLeftClick() {
      PlayerController.leftClick()
   }

   private fun sleepMs(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
