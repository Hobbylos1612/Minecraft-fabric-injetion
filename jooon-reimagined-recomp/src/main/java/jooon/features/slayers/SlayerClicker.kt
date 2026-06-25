package jooon.features.slayers

import java.util.Locale
import java.util.concurrent.ThreadLocalRandom
import jooon.config.Config
import jooon.util.PlayerController
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.Window
import net.minecraft.item.ItemStack
import org.lwjgl.glfw.GLFW

object SlayerClicker {
   private val slayerWeaponKeys: List<String> =
      listOf(
         arrayOf(
            "revenant falchion",
            "reaper falchion",
            "reaper scythe",
            "halbred of the shredded",
            "recluse fang",
            "scorpion foil",
            "tarantula fang",
            "sting",
            "shaman sword",
            "pooch sword",
            "voidwalker katana",
            "voidedge katana",
            "vorpal katana",
            "atomsplit katana",
            "silver-laced karambit",
            "silver-twist karambit",
            "firedust dagger",
            "twilight dagger",
            "kindlebane dagger",
            "mawdredge dagger",
            "pyrochaos dagger",
            "deathripper dagger"
         )
      )
      private val STRIP_COLOR: Regex = Regex("(?i)§[0-9A-FK-OR]")
   private var workerStarted: Boolean
   private var lastClickNs: Long
   private var suppressUntilNs: Long

   fun getMc(): MinecraftClient {
return var10000
   }

   
   fun suppressForMs(durationMs: Long) {

      if (target > suppressUntilNs) {
         suppressUntilNs = target
      }
   }

   fun onInitializeClient() {
      if (!workerStarted) {
         workerStarted = true
         val `this24lambda_u241`: Thread = Thread({ 
            runLoop()
         }, "Jooon-SlayerClicker")
         `this24lambda_u241`.setDaemon(true)
         `this24lambda_u241`.start()
      }
   }

   private fun runLoop() {
      while (true) {
         if (!Config.slayerClickerEnabled) {
            lastClickNs = 0L
            this.sleepMs(10L)
         } else if (System.nanoTime() < suppressUntilNs) {
            lastClickNs = 0L
            this.sleepMs(2L)
         } else {

            if (client == null) {
               this.sleepMs(100L)
            } else {


               var lmbDown: SlayerClicker
               try {
                  lmbDown = player
                  lmbDown = Result(client.getWindow())
               } catch (var15: java.lang.Throwable) {
                  lmbDown = Result(ResultKt.createFailure(var15))
               }

               if (window == null) {
                  this.sleepMs(100L)
               } else {

                  if (client.player == null) {
                     this.sleepMs(20L)
                  } else if (client.isWindowFocused() && client.currentScreen == null) {
                     if (GLFW.glfwGetMouseButton(window.getHandle(), 0) != 1) {
                        lastClickNs = 0L
                        this.sleepMs(2L)
                     } else {

                        if (var10000.isEmpty()) {
                           lastClickNs = 0L
                           this.sleepMs(2L)
                        } else {




                           val cps: java.lang.Iterable = slayerWeaponKeys
                           var var28: Boolean
                           if (slayerWeaponKeys is java.util.Collection && slayerWeaponKeys.isEmpty()) {
                              var28 = false
                           } else {
                              val var10: java.util.Iterator = cps.iterator()

                              while (true) {
                                 if (!var10.hasNext()) {
                                    var28 = false
break
                                 }

                                 if (contains$default(name, var10.next() as String, false, 2, null)) {
                                    var28 = true
break
                                 }
                              }
                           }

                           if (!var28) {
                              lastClickNs = 0L
                              this.sleepMs(4L)
                           } else {


                              if (lastClickNs == 0L || var23 - lastClickNs >= var22) {
                                 this.doClick()
                                 lastClickNs = var23 + ThreadLocalRandom.current().nextLong(3L, 79L) * 1000000L
                              }

                              if ((var22 - (System.nanoTime() - lastClickNs)).coerceAtLeast(0L) > 2000000L) {
                                 this.sleepMs(1L)
                              } else {
                                 Thread.onSpinWait()
                              }
                           }
                        }
                     }
                  } else {
                     lastClickNs = 0L
                     this.sleepMs(5L)
                  }
               }
            }
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
