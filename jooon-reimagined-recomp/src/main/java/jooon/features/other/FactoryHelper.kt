package jooon.features.other

import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Map.Entry
import jooon.config.Config
import kotlin.random.Random
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text

object FactoryHelper {
   private val nextCPSRegex: Regex = Regex("^  \\+([\\d.,]+)x? Chocolate per second$")
   private val currentCPSRegex: Regex = Regex("\\+([\\d.,]+)x? Chocolate per")
   private val chocolateCostRegex: Regex = Regex("^([\\d,]+) Chocolate$")
   private val baseChocoRegex: Regex = Regex("^Base Chocolate: ([\\d,.]+) per second$")
   private val totalMultiplierRegex: Regex = Regex("^Total Multiplier: ([\\d,.]+)x$")
   private val timeTowerMultiRegex: Regex = Regex("^ *\\+([\\d.]+)x \\(Time Tower\\)$")
   private val stats: MutableMap<Int, jooon.features.other.FactoryHelper.RabbitStat> = LinkedHashMap() as java.util.Map
   var inFactory: Boolean
   private var chocolatePurse: Double
   private var currentProduction: Double
   private var bestSlot: Int = -1
   var autoUpgradeEnabled: Boolean
   private var tickDelay: Int
   private var guiOpenTime: Long

   fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         onTick()
      })
   }

   fun onOpenScreen(title: String) {
      inFactory = contains$default(title, "Chocolate Factory", false, 2, null)
      autoUpgradeEnabled = false
      tickDelay = 0
      stats.clear()
      bestSlot = -1
      chocolatePurse = 0.0
      currentProduction = 0.0
      guiOpenTime = System.currentTimeMillis()
   }

   fun onTick() {
      if (Config.factoryHelperEnabled && inFactory && autoUpgradeEnabled) {

         if (var10000.currentScreen == null) {
            autoUpgradeEnabled = false
         } else if (System.currentTimeMillis() - guiOpenTime >= 400L) {
            if (tickDelay > 0) {
               tickDelay += -1
            } else {
               if (bestSlot != -1) {
                  this.clickSlot(bestSlot)
                  tickDelay = random(IntRange(3, 6), Random.Default as Random)
               }
            }
         }
      }
   }

   private fun clickSlot(slotId: Int) {

      if (var10000.interactionManager != null) {
         if (var10000.player != null) {


            val var8: HandledScreen = var10000.currentScreen as? HandledScreen
            if ((var10000.currentScreen as? HandledScreen) != null) {
               var10000.interactionManager.clickSlot(var8.getScreenHandler().syncId, slotId, 0, SlotActionType.PICKUP, player as PlayerEntity)
            }
         }
      }
   }

   fun onSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket) {
      if (Config.factoryHelperEnabled && inFactory) {

         if (slot <= 53) {

            this.processItem(slot, var10000)
         }
      }
   }

   fun onInventoryUpdate(packet: InventoryS2CPacket) {
      if (Config.factoryHelperEnabled && inFactory) {
         val var10000: java.util.List = packet.contents()
         val `this$iv`: java.lang.Iterable = var10000
         var `index$iv`: Int = 0

         for (`item$iv` in `this$iv`) {

            if (var7 < 0) {
               throwIndexOverflow()
            }

            if (var7 <= 53) {

               var12.processItem(var7, itemStack)
            }
         }
      }
   }

   fun processItem(slot: Int, itemStack: ItemStack) {
      if (!itemStack.isEmpty()) {
         if (itemStack.getItem() == Items.PLAYER_HEAD || itemStack.getItem() == Items.COCOA_BEANS) {

            val lore: java.util.List = this.getLore(itemStack)
            if (endsWith$default(var10000, "Chocolate", false, 2, null)) {

               if (match != null) {
                  chocolatePurse = java.lang.Double.parseDouble(
                     replace$default(match.getGroupValues().get(1) as String, ",", "", false, 4, null)
                  )
                  this.findBest()
               } else if (contains$default(var10000, "Chocolate", false, 2, null)) {
               }
            } else if (var10000 == "Chocolate Production") {
               this.chocoProduction(lore)
            } else if (startsWith$default(var10000, "Coach Jack", false, 2, null)) {
               this.coachStats(slot, lore)
            } else if (startsWith$default(var10000, "Rabbit", false, 2, null)) {
               this.rabbitStats(slot, lore)
            }
         }
      }
   }

   private fun findBest() {
      if (stats.isEmpty()) {
         bestSlot = -1
      } else {
         var var11: Int = -1
         var bestStat: Any = null

         for (`element$iv` in stats.entrySet()) {

            val stat: FactoryHelper.RabbitStat = (`element$iv` as Entry).getValue() as FactoryHelper.RabbitStat
            if (!(stat.cost > chocolatePurse) && (bestStat == null || stat.cpsCost < bestStat.cpsCost)) {
               var11 = slot
               bestStat = stat
            }
         }

         bestSlot = var11
      }
   }

   private fun chocoProduction(lore: List<String>) {
      var timeTower: Double = 0.0
      var baseChoco: Double = 0.0
      var totalMultx: Double = 0.0

      for (line in lore) {

         if (baseChocoMatch != null) {
            baseChoco = java.lang.Double.parseDouble(
               replace$default(baseChocoMatch.getGroupValues().get(1) as String, ",", "", false, 4, null)
            )
         } else {

            if (totalMultxMatch != null) {
               totalMultx = java.lang.Double.parseDouble(
                  replace$default(totalMultxMatch.getGroupValues().get(1) as String, ",", "", false, 4, null)
               )
            } else {

               if (match != null) {
                  timeTower = java.lang.Double.parseDouble(
                     replace$default(match.getGroupValues().get(1) as String, ",", "", false, 4, null)
                  )
               }
            }
         }
      }

      if (timeTower != 0.0) {
         totalMultx -= timeTower
      }

      currentProduction = baseChoco * totalMultx
   }

   private fun coachStats(slot: Int, lore: List<String>) {
      var cost: Double = 0.0

      for (line in lore) {

         if (var10000 != null) {
            cost = java.lang.Double.parseDouble(replace$default(var10000.getGroupValues().get(1) as String, ",", "", false, 4, null))
         }
      }

      if (cost != 0.0) {

         stats.put(slot, FactoryHelper.RabbitStat((cost / var9).toInt(), cost))
         this.findBest()
      }
   }

   private fun rabbitStats(slot: Int, lore: List<String>) {
      var currentCps: Int = 0
      var nextCps: Int = 0
      var cost: Double = 0.0

      for (cpsGain in lore) {

         if (costMatch != null) {
            cost = java.lang.Double.parseDouble(replace$default(costMatch.getGroupValues().get(1) as String, ",", "", false, 4, null))
         } else {

            if (nextCPSMatch != null) {
               nextCps = Integer.parseInt(replace$default(nextCPSMatch.getGroupValues().get(1) as String, ",", "", false, 4, null))
            } else {

               if (match != null) {
                  currentCps = Integer.parseInt(replace$default(match.getGroupValues().get(1) as String, ",", "", false, 4, null))
               }
            }
         }
      }

      if (cost == 0.0) {
         stats.remove(slot)
         this.findBest()
      } else {
         stats.put(slot, FactoryHelper.RabbitStat(cost.toInt() / (if (nextCps - currentCps > 0) nextCps - currentCps else 1), cost))
         this.findBest()
      }
   }

   fun shouldRenderSlot(slot: Slot): Boolean {
      if (Config.factoryHelperEnabled && inFactory) {
         if (bestSlot != -1 && slot.id == bestSlot) {


            if (!(var10000 == (if (var10001 != null) var10001.getInventory() else null))) {
return true
            }
         }
return false
      } else {
return false
      }
   }

   fun renderSlotOverlay(drawContext: DrawContext, slot: Slot) {
      drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, -2147418113)
   }

   fun getLore(stack: ItemStack): MutableList<String> {

      if (var10000 == null) {
         emptyList()
      } else {
         val var13: java.util.List = var10000.lines()
         val `this$iv$iv`: java.lang.Iterable = var13
         val `destination$iv$iv`: java.util.Collection = ArrayList(var13.count().coerceAtLeast(10))

         for (`item$iv$iv` in `this$iv$iv`) {
            `destination$iv$iv`.add((`item$iv$iv` as Text).getString())
         }

         `destination$iv$iv` as java.util.List
      }
   }

   data class RabbitStat(cpsCost: Int, cost: Double) {
      val cpsCost: Int
      val cost: Double

      init {
         this.cpsCost = cpsCost
         this.cost = cost
      }

      public operator fun component1(): Int {
         return this.cpsCost
      }

      public operator fun component2(): Double {
         return this.cost
      }

      fun copy(cpsCost: Int = this.cpsCost, cost: Double = this.cost): jooon.features.other.FactoryHelper.RabbitStat {
         return FactoryHelper.RabbitStat(cpsCost, cost)
      }

      override fun toString(): String {
         return "RabbitStat(cpsCost=${this.cpsCost}, cost=${this.cost})"
      }

      override fun hashCode(): Int {
         return Integer.hashCode(this.cpsCost) * 31 + java.lang.Double.hashCode(this.cost)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is FactoryHelper.RabbitStat
               && this.cpsCost == (other as FactoryHelper.RabbitStat).cpsCost
               && java.lang.Double.compare(this.cost, (other as FactoryHelper.RabbitStat).cost) == 0
            }
      }
   }
}
