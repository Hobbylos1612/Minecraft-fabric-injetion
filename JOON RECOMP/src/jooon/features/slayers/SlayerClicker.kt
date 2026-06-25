package jooon.features.slayers

import java.util.Locale
import java.util.concurrent.ThreadLocalRandom
import jooon.config.Config
import jooon.util.PlayerController
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.Window
import net.minecraft.item.ItemStack
import org.lwjgl.glfw.GLFW

@SourceDebugExtension(["SMAP\nSlayerClicker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SlayerClicker.kt\njooon/features/slayers/SlayerClicker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,138:1\n1#2:139\n1747#3,3:140\n*S KotlinDebug\n*F\n+ 1 SlayerClicker.kt\njooon/features/slayers/SlayerClicker\n*L\n107#1:140,3\n*E\n"])
public object SlayerClicker {
   private final val slayerWeaponKeys: List<String> =
      CollectionsKt.listOf(
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
      private final val STRIP_COLOR: Regex = Regex("(?i)§[0-9A-FK-OR]")
   private final var workerStarted: Boolean
   private final var lastClickNs: Long
   private final var suppressUntilNs: Long

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   @JvmStatic
   public fun suppressForMs(durationMs: Long) {
      val target: Long = System.nanoTime() + RangesKt.coerceAtLeast(durationMs, 0L) * 1000000L
      if (target > suppressUntilNs) {
         suppressUntilNs = target
      }
   }

   public fun onInitializeClient() {
      if (!workerStarted) {
         workerStarted = true
         val `$this$onInitializeClient_u24lambda_u241`: Thread = Thread({ 
            INSTANCE.runLoop()
         }, "Jooon-SlayerClicker")
         `$this$onInitializeClient_u24lambda_u241`.setDaemon(true)
         `$this$onInitializeClient_u24lambda_u241`.start()
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
            val client: MinecraftClient = this.getMc()
            if (client == null) {
               this.sleepMs(100L)
            } else {
               val player: SlayerClicker = this

               var lmbDown: SlayerClicker
               try {
                  lmbDown = player
                  lmbDown = (SlayerClicker)Result.constructor_impl/* $VF was: constructor-impl */(client.method_22683())
               } catch (var15: java.lang.Throwable) {
                  lmbDown = (SlayerClicker)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
               }

               val window: Window = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(lmbDown)) null else lmbDown) as Window
               if (window == null) {
                  this.sleepMs(100L)
               } else {
                  val var17: ClientPlayerEntity = client.field_1724
                  if (client.field_1724 == null) {
                     this.sleepMs(20L)
                  } else if (client.method_1569() && client.field_1755 == null) {
                     if (GLFW.glfwGetMouseButton(window.method_4490(), 0) != 1) {
                        lastClickNs = 0L
                        this.sleepMs(2L)
                     } else {
                        val var10000: ItemStack = var17.method_6047()
                        if (var10000.method_7960()) {
                           lastClickNs = 0L
                           this.sleepMs(2L)
                        } else {
                           val var26: Regex = STRIP_COLOR
                           val var10001: java.lang.String = var10000.method_7964().getString()
                           val var27: java.lang.String = var26.replace(var10001, "").toLowerCase(Locale.ROOT)
                           val name: java.lang.String = var27
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

                                 if (StringsKt.contains$default(name, var10.next() as java.lang.String, false, 2, null)) {
                                    var28 = true
                                    break
                                 }
                              }
                           }

                           if (!var28) {
                              lastClickNs = 0L
                              this.sleepMs(4L)
                           } else {
                              val var22: Long = 1000000000L / RangesKt.coerceIn(Config.slayerClickerCps, 1, 16)
                              val var23: Long = System.nanoTime()
                              if (lastClickNs == 0L || var23 - lastClickNs >= var22) {
                                 this.doClick()
                                 lastClickNs = var23 + ThreadLocalRandom.current().nextLong(3L, 79L) * 1000000L
                              }

                              if (RangesKt.coerceAtLeast(var22 - (System.nanoTime() - lastClickNs), 0L) > 2000000L) {
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
      PlayerController.INSTANCE.leftClick()
   }

   private fun sleepMs(ms: Long) {
      try {
         Thread.sleep(ms)
      } catch (var4: InterruptedException) {
      }
   }
}
