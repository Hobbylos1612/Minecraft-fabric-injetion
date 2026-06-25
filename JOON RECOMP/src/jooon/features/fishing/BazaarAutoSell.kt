package jooon.features.fishing

import java.util.Locale
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.collection.DefaultedList

@SourceDebugExtension(["SMAP\nBazaarAutoSell.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BazaarAutoSell.kt\njooon/features/fishing/BazaarAutoSell\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,162:1\n1#2:163\n350#3,7:164\n*S KotlinDebug\n*F\n+ 1 BazaarAutoSell.kt\njooon/features/fishing/BazaarAutoSell\n*L\n127#1:164,7\n*E\n"])
public object BazaarAutoSell {
   private final var stage: jooon.features.fishing.BazaarAutoSell.Stage = BazaarAutoSell.Stage.IDLE
   private final var stageEnterMs: Long

   public fun init() {
      ScreenEvents.AFTER_INIT.register({ var0: MinecraftClient, screen: Screen, var2: Int, var3: Int ->
         INSTANCE.onScreen(screen)
      })
   }

   public fun queue() {
      this.enterStage(BazaarAutoSell.Stage.WAIT_BAZAAR)
   }

   fun onScreen(screen: Screen) {
      if (stage != BazaarAutoSell.Stage.IDLE) {
         if (screen is HandledScreen) {
            val var10001: java.lang.String = (screen as HandledScreen).method_25440().getString()
val title: java.lang.String = this.normalize(var10001)
            when (BazaarAutoSell.WhenMappings.$EnumSwitchMapping$0[stage.ordinal()]) {
               1 -> {
                  if (StringsKt.contains$default(title, "bazaar", false, 2, null)) {
                     Thread(
                           { 
                              Thread.sleep(INSTANCE.afterOpenDelay(50L))
                              if (stage === BazaarAutoSell.Stage.WAIT_BAZAAR
                                 && INSTANCE.isscreenTitleContains("bazaar")
                                 && INSTANCE.clickByNameOrItem("sell inventory now", "sell", Items.field_8106)) {
                                 INSTANCE.enterStage(BazaarAutoSell.Stage.WAIT_CONFIRM)
                              }
                           }
                        )
                        .start()
                     }
               }
               2 -> {
                  if (StringsKt.contains$default(title, "are you sure", false, 2, null)) {
                     Thread(
                           { 
                              Thread.sleep(INSTANCE.afterOpenDelay(50L))
                              if (stage === BazaarAutoSell.Stage.WAIT_CONFIRM
                                 && INSTANCE.isscreenTitleContains("are you sure")
                                 && INSTANCE.clickByNameOrItem("sell whole inventory", "sell", Items.field_8239)) {
                                 INSTANCE.enterStage(BazaarAutoSell.Stage.WAIT_BAZAAR_CLOSE)
                              }
                           }
                        )
                        .start()
                     }
               }
               3 -> {
                  if (StringsKt.contains$default(title, "bazaar", false, 2, null)) {
                     Thread({ 
                        Thread.sleep(INSTANCE.afterOpenDelay(50L))
                        if (stage === BazaarAutoSell.Stage.WAIT_BAZAAR_CLOSE && INSTANCE.isscreenTitleContains("bazaar")) {
                           MinecraftClient.method_1551().execute({ 
                              val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
                              if (var10000 != null) {
                                 var10000.method_7346()
                              }
                           })
                           stage = BazaarAutoSell.Stage.IDLE
                        }
                     }).start()
                  }
               }
               else -> {}
            }
         }
      }
   }

   private fun enterStage(next: jooon.features.fishing.BazaarAutoSell.Stage) {
      stage = next
      stageEnterMs = System.currentTimeMillis()
      Thread({ 
         Thread.sleep(`$timeout`)
         if (stage === `$next` && System.currentTimeMillis() - stageEnterMs >= `$timeout`) {
            stage = BazaarAutoSell.Stage.IDLE
         }
      }).start()
   }

   fun clickByNameOrItem(mustContain: java.lang.String, orContains: java.lang.String, orItem: Item): Boolean {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 == null) {
         false
      } else {
         val p: ClientPlayerEntity = var10000.field_1724
         if (var10000.field_1761 == null) {
            false
         } else {
            val gm: ClientPlayerInteractionManager = var10000.field_1761
            if (var10000.field_1724.field_7512 == null) {
               false
            } else {
               val handler: ScreenHandler = var10000.field_1724.field_7512
               val targetExact: java.lang.String = this.normalize(mustContain)
               val targetContains: java.lang.String = if (orContains != null) INSTANCE.normalize(orContains) else null
               val var23: DefaultedList = handler.field_7761
               val var20: java.util.List = var23 as java.util.List
               var `index$iv`: Int = 0
               val var14: java.util.Iterator = var20.iterator()

               while (true) {
                  if (!var14.hasNext()) {
                     var27 = -1
                     break
                  }

                  val var24: ItemStack = (var14.next() as Slot).method_7677()
                  val var25: Boolean
                  if (var24.method_7960()) {
                     var25 = false
                  } else {
                     val var26: BazaarAutoSell = INSTANCE
                     val var10001: java.lang.String = var24.method_7964().getString()
                     val n: java.lang.String = var26.normalize(var10001)
                     var25 = n == targetExact
                        || targetContains != null && StringsKt.contains$default(n, targetContains, false, 2, null)
                        || orItem != null && var24.method_7909() == orItem
                     }

                  if (var25) {
                     var27 = `index$iv`
                     break
                  }

                  `index$iv`++
               }

               if (var27 >= 0) {
                  var10000.execute({ 
                     `$gm`.method_2906(`$handler`.field_7763, `$idx`, 0, SlotActionType.field_7790, `$p` as PlayerEntity)
                  })
                  true
               } else {
                  false
               }
            }
         }
      }
   }

   private fun isscreenTitleContains(needle: String): Boolean {
      val var3: Screen = MinecraftClient.method_1551().field_1755
      val var10000: HandledScreen = var3 as? HandledScreen
      if ((var3 as? HandledScreen) == null) {
         return false
      } else {
         val var10001: java.lang.String = var10000.method_25440().getString()
         return StringsKt.contains$default(this.normalize(var10001), this.normalize(needle), false, 2, null)
      }
   }

   private fun normalize(s: String): String {
      val var10000: java.lang.String = Regex("§.").replace(s, "").toLowerCase(Locale.ROOT)
      return StringsKt.trim(var10000).toString()
   }

   private fun getPingMs(): Long {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 == null) {
         return 0L
      } else {
         val p: ClientPlayerEntity = var10000.field_1724
         val var4: ClientPlayNetworkHandler = var10000.method_1562()
         val entry: PlayerListEntry = if (var4 != null) var4.method_2871(p.method_5667()) else null
         return if (entry != null) entry.method_2959() else 0L
      }
   }

   private fun afterOpenDelay(base: Long = 50L): Long {
      return base + RangesKt.coerceAtMost(this.getPingMs() / (long)2, 250L)
   }

   private enum class Stage {
      IDLE,
      WAIT_BAZAAR,
      WAIT_CONFIRM,
      WAIT_BAZAAR_CLOSE;

      @JvmStatic
      fun getEntries(): EnumEntries<BazaarAutoSell.Stage> {
         $ENTRIES
      }
   }
}
