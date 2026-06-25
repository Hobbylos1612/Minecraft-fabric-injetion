package jooon.features.fishing

import java.util.Locale
import kotlin.enums.EnumEntries
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

object BazaarAutoSell {
   private var stage: jooon.features.fishing.BazaarAutoSell.Stage = BazaarAutoSell.Stage.IDLE
   private var stageEnterMs: Long

   fun init() {
      ScreenEvents.AFTER_INIT.register({ var0: MinecraftClient, screen: Screen, var2: Int, var3: Int ->
         onScreen(screen)
      })
   }

   fun queue() {
      this.enterStage(BazaarAutoSell.Stage.WAIT_BAZAAR)
   }

   fun onScreen(screen: Screen) {
      if (stage != BazaarAutoSell.Stage.IDLE) {
         if (screen is HandledScreen) {

val title: String = this.normalize(var10001)
            when (BazaarAutoSell.WhenMappings.$EnumSwitchMapping$0[stage.ordinal()]) {
               1 -> {
                  if (contains$default(title, "bazaar", false, 2, null)) {
                     Thread(
                           { 
                              Thread.sleep(afterOpenDelay(50L))
                              if (stage === BazaarAutoSell.Stage.WAIT_BAZAAR
                                 && isscreenTitleContains("bazaar")
                                 && clickByNameOrItem("sell inventory now", "sell", Items.CHEST)) {
                                 enterStage(BazaarAutoSell.Stage.WAIT_CONFIRM)
                              }
                           }
                        )
                        .start()
                     }
               }
               2 -> {
                  if (contains$default(title, "are you sure", false, 2, null)) {
                     Thread(
                           { 
                              Thread.sleep(afterOpenDelay(50L))
                              if (stage === BazaarAutoSell.Stage.WAIT_CONFIRM
                                 && isscreenTitleContains("are you sure")
                                 && clickByNameOrItem("sell whole inventory", "sell", Items.HOPPER)) {
                                 enterStage(BazaarAutoSell.Stage.WAIT_BAZAAR_CLOSE)
                              }
                           }
                        )
                        .start()
                     }
               }
               3 -> {
                  if (contains$default(title, "bazaar", false, 2, null)) {
                     Thread({ 
                        Thread.sleep(afterOpenDelay(50L))
                        if (stage === BazaarAutoSell.Stage.WAIT_BAZAAR_CLOSE && isscreenTitleContains("bazaar")) {
                           MinecraftClient.getInstance().execute({ 

                              if (var10000 != null) {
                                 var10000.closeHandledScreen()
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

   fun clickByNameOrItem(mustContain: String, orContains: String, orItem: Item): Boolean {

      if (var10000.player == null) {
return false
      } else {

         if (var10000.interactionManager == null) {
return false
         } else {

            if (var10000.player.currentScreenHandler == null) {
return false
            } else {




               val var20: java.util.List = var23 as java.util.List
               var `index$iv`: Int = 0
               val var14: java.util.Iterator = var20.iterator()

               while (true) {
                  if (!var14.hasNext()) {
                     var27 = -1
break
                  }

                  val var25: Boolean
                  if (var24.isEmpty()) {
                     var25 = false
                  } else {



                     var25 = n == targetExact
                        || targetContains != null && contains$default(n, targetContains, false, 2, null)
                        || orItem != null && var24.getItem() == orItem
                     }

                  if (var25) {
                     var27 = `index$iv`
break
                  }

                  `index$iv`++
               }

               if (var27 >= 0) {
                  var10000.execute({ 
                     `$gm`.clickSlot(`$handler`.syncId, `$idx`, 0, SlotActionType.PICKUP, `$p` as PlayerEntity)
                  })
return true
               } else {
return false
               }
            }
         }
      }
   }

   private fun isscreenTitleContains(needle: String): Boolean {

      val var10000: HandledScreen = var3 as? HandledScreen
      if ((var3 as? HandledScreen) == null) {
         return false
      } else {

         return contains$default(this.normalize(var10001), this.normalize(needle), false, 2, null)
      }
   }

   private fun normalize(s: String): String {

      return trim(var10000).toString()
   }

   private fun getPingMs(): Long {

      if (var10000.player == null) {
         return 0L
      } else {



         return if (entry != null) entry.getLatency() else 0L
      }
   }

   private fun afterOpenDelay(base: Long = 50L): Long {
      return base + (this.getPingMs() / 2.toLong()).coerceAtMost(250L)
   }

   private enum class Stage {
      IDLE,
      WAIT_BAZAAR,
      WAIT_CONFIRM,
      WAIT_BAZAAR_CLOSE;

      
      fun getEntries(): EnumEntries<BazaarAutoSell.Stage> {
         $ENTRIES
      }
   }
}
