package jooon.features.dungeons

import java.util.Locale
import java.util.concurrent.ThreadLocalRandom
import jooon.config.Config
import jooon.util.PlayerController
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.Window
import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import org.lwjgl.glfw.GLFW

@SourceDebugExtension(["SMAP\nTerminatorClicker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TerminatorClicker.kt\njooon/features/dungeons/TerminatorClicker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,92:1\n1#2:93\n*E\n"])
public object TerminatorClicker {
   private final var workerStarted: Boolean
   private final var lastClickNs: Long
   private final val STRIP_COLOR: Regex = Regex("(?i)§[0-9A-FK-OR]")

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      if (!workerStarted) {
         workerStarted = true
         val `$this$onInitializeClient_u24lambda_u241`: Thread = Thread({ 
            INSTANCE.runLoop()
         }, "Jooon-TerminatorClicker")
         `$this$onInitializeClient_u24lambda_u241`.setDaemon(true)
         `$this$onInitializeClient_u24lambda_u241`.start()
      }
   }

   private fun runLoop() {
      while (true) {
         if (!Config.terminatorClickerEnabled) {
            lastClickNs = 0L
            this.sleepMs(10L)
         } else {
            val client: MinecraftClient = this.getMc()
            if (client == null) {
               this.sleepMs(100L)
            } else {
               val player: TerminatorClicker = this

               var rmbDown: TerminatorClicker
               try {
                  rmbDown = player
                  rmbDown = (TerminatorClicker)Result.constructor_impl/* $VF was: constructor-impl */(client.method_22683())
               } catch (var16: java.lang.Throwable) {
                  rmbDown = (TerminatorClicker)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var16))
               }

               val window: Window = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(rmbDown)) null else rmbDown) as Window
               if (window == null) {
                  this.sleepMs(100L)
               } else if (client.field_1724 == null) {
                  this.sleepMs(20L)
               } else if (client.field_1729.method_1613() && client.field_1755 == null) {
                  if (GLFW.glfwGetMouseButton(window.method_4490(), 1) != 1) {
                     lastClickNs = 0L
                     this.sleepMs(2L)
                  } else {
                     val var10000: ClientPlayerEntity = this.getMc().field_1724
                     if (var10000 != null) {
                        val var23: ItemStack = var10000.method_6047()
                        if (var23 != null) {
                           val var24: Regex = STRIP_COLOR
                           val var10001: java.lang.String = var23.method_7964().getString()
                           val var25: java.lang.String = var24.replace(var10001, "").toLowerCase(Locale.ROOT)
                           if (StringsKt.contains$default(var25, "terminator", false, 2, null) && var23.method_7909() is BowItem) {
                              val basePeriodNs: Long = 1000000000L / RangesKt.coerceIn(Config.terminatorClickerCps, 5, 20)
                              val now: Long = System.nanoTime()
                              if (lastClickNs == 0L || now - lastClickNs >= basePeriodNs) {
                                 this.doLeftClick()
                                 lastClickNs = now + ThreadLocalRandom.current().nextLong(3L, 79L) * 1000000L
                              }

                              if (RangesKt.coerceAtLeast(basePeriodNs - (System.nanoTime() - lastClickNs), 0L) > 2000000L) {
                                 this.sleepMs(1L)
                                 continue
                              }

                              Thread.onSpinWait()
                              continue
                           }

                           lastClickNs = 0L
                           this.sleepMs(4L)
                           continue
                        }
                     }

                     return
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
      PlayerController.INSTANCE.leftClick()
   }

   private fun sleepMs(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
