package jooon.features.dojo

import java.util.concurrent.ThreadLocalRandom
import jooon.config.Config
import jooon.util.PlayerController
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Window
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.item.Item
import net.minecraft.util.hit.EntityHitResult
import org.lwjgl.glfw.GLFW

@SourceDebugExtension(["SMAP\nClicker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Clicker.kt\njooon/features/dojo/Clicker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,126:1\n1#2:127\n*E\n"])
public object Clicker {
   private final var workerStarted: Boolean
   private final var lastClickNs: Long

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      if (!workerStarted) {
         workerStarted = true
         val `$this$onInitializeClient_u24lambda_u241`: Thread = Thread({ 
            INSTANCE.runLoop()
         }, "Jooon-DojoClicker")
         `$this$onInitializeClient_u24lambda_u241`.setDaemon(true)
         `$this$onInitializeClient_u24lambda_u241`.start()
      }
   }

   private fun runLoop() {
      while (true) {
         if (Config.autoDojoEnabled && Config.dojoClickerEnabled) {
            val isForce: Boolean = AutoDojo.INSTANCE.isChallengeActive(AutoDojo.Challenge.FORCE)
            val isDiscipline: Boolean = AutoDojo.INSTANCE.isChallengeActive(AutoDojo.Challenge.DISCIPLINE) && !Config.fullyAutomaticDiscipline
            if (!isForce && !isDiscipline) {
               lastClickNs = 0L
               this.sleepMs(10L)
            } else {
               val client: MinecraftClient = this.getMc()
               if (client == null) {
                  this.sleepMs(200L)
               } else {
                  val player: Clicker = this

                  var lmbDown: Clicker
                  try {
                     lmbDown = player
                     lmbDown = (Clicker)Result.constructor_impl/* $VF was: constructor-impl */(client.method_22683())
                  } catch (var14: java.lang.Throwable) {
                     lmbDown = (Clicker)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
                  }

                  val window: Window = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(lmbDown)) null else lmbDown) as Window
                  if (window == null) {
                     this.sleepMs(200L)
                  } else if (client.field_1724 == null) {
                     this.sleepMs(30L)
                  } else if (client.field_1729.method_1613() && client.field_1755 == null) {
                     if (GLFW.glfwGetMouseButton(window.method_4490(), 0) != 1) {
                        lastClickNs = 0L
                        this.sleepMs(2L)
                     } else {
                        if (isDiscipline && client.field_1765 is EntityHitResult) {
                           val var10000: Entity = (client.field_1765 as EntityHitResult).method_17782()
                           if (var10000 is ZombieEntity) {
                              val var23: Item = (var10000 as ZombieEntity).method_6118(EquipmentSlot.field_6169).method_7909()
                              Discipline.INSTANCE.swapToCorrectSword$JooonReimagined_noMidnightLib(var23)
                              lastClickNs = System.nanoTime() + 50000000L
                              this.sleepMs(5L)
                              continue
                           }
                        }

                        val var21: Long = 1000000000L / RangesKt.coerceIn(Config.dojoClickerCps, 6, 16)
                        val now: Long = System.nanoTime()
                        if (lastClickNs == 0L || now - lastClickNs >= var21) {
                           this.doClick()
                           lastClickNs = now + ThreadLocalRandom.current().nextLong(3L, 20L) * 1000000L
                        }

                        if (RangesKt.coerceAtLeast(var21 - (System.nanoTime() - lastClickNs), 0L) > 2000000L) {
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
      PlayerController.INSTANCE.leftClick()
   }

   private fun sleepMs(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
