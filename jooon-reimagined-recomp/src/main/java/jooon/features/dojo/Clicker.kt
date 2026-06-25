package jooon.features.dojo

import java.util.concurrent.ThreadLocalRandom
import jooon.config.Config
import jooon.util.PlayerController
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Window
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.item.Item
import net.minecraft.util.hit.EntityHitResult
import org.lwjgl.glfw.GLFW

object Clicker {
   private var workerStarted: Boolean
   private var lastClickNs: Long

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      if (!workerStarted) {
         workerStarted = true
         val `this24lambda_u241`: Thread = Thread({ 
            runLoop()
         }, "Jooon-DojoClicker")
         `this24lambda_u241`.setDaemon(true)
         `this24lambda_u241`.start()
      }
   }

   private fun runLoop() {
      while (true) {
         if (Config.autoDojoEnabled && Config.dojoClickerEnabled) {


            if (!isForce && !isDiscipline) {
               lastClickNs = 0L
               this.sleepMs(10L)
            } else {

               if (client == null) {
                  this.sleepMs(200L)
               } else {


                  var lmbDown: Clicker
                  try {
                     lmbDown = player
                     lmbDown = Result(client.getWindow())
                  } catch (var14: java.lang.Throwable) {
                     lmbDown = Result(ResultKt.createFailure(var14))
                  }

                  if (window == null) {
                     this.sleepMs(200L)
                  } else if (client.player == null) {
                     this.sleepMs(30L)
                  } else if (client.mouse.isCursorLocked() && client.currentScreen == null) {
                     if (GLFW.glfwGetMouseButton(window.getHandle(), 0) != 1) {
                        lastClickNs = 0L
                        this.sleepMs(2L)
                     } else {
                        if (isDiscipline && client.crosshairTarget is EntityHitResult) {

                           if (var10000 is ZombieEntity) {

                              Discipline.swapToCorrectSword$JooonReimagined_noMidnightLib(var23)
                              lastClickNs = System.nanoTime() + 50000000L
                              this.sleepMs(5L)
return continue
                           }
                        }


                        if (lastClickNs == 0L || now - lastClickNs >= var21) {
                           this.doClick()
                           lastClickNs = now + ThreadLocalRandom.current().nextLong(3L, 20L) * 1000000L
                        }

                        if ((var21 - (System.nanoTime() - lastClickNs)).coerceAtLeast(0L) > 2000000L) {
                           this.sleepMs(1L)
                        } else {
                           Thread.onSpinWait()
                        }
                     }
                  } else {
                     lastClickNs = 0L
                     this.sleepMs(5L)
                  }
               }
            }
         } else {
            lastClickNs = 0L
            this.sleepMs(10L)
         }
      }
   }

   private fun doClick() {
      PlayerController.leftClick()
   }

   private fun sleepMs(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
