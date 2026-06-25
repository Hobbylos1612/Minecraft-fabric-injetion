package jooon.features.other

import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Map.Entry
import jooon.config.Config
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nFactoryHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FactoryHelper.kt\njooon/features/other/FactoryHelper\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,300:1\n1864#2,3:301\n1855#2,2:304\n1549#2:306\n1620#2,3:307\n*S KotlinDebug\n*F\n+ 1 FactoryHelper.kt\njooon/features/other/FactoryHelper\n*L\n117#1:301,3\n171#1:304,2\n297#1:306\n297#1:307,3\n*E\n"])
public object FactoryHelper {
   private final val nextCPSRegex: Regex = Regex("^  \\+([\\d.,]+)x? Chocolate per second$")
   private final val currentCPSRegex: Regex = Regex("\\+([\\d.,]+)x? Chocolate per")
   private final val chocolateCostRegex: Regex = Regex("^([\\d,]+) Chocolate$")
   private final val baseChocoRegex: Regex = Regex("^Base Chocolate: ([\\d,.]+) per second$")
   private final val totalMultiplierRegex: Regex = Regex("^Total Multiplier: ([\\d,.]+)x$")
   private final val timeTowerMultiRegex: Regex = Regex("^ *\\+([\\d.]+)x \\(Time Tower\\)$")
   private final val stats: MutableMap<Int, jooon.features.other.FactoryHelper.RabbitStat> = LinkedHashMap() as java.util.Map
   public final var inFactory: Boolean
   private final var chocolatePurse: Double
   private final var currentProduction: Double
   private final var bestSlot: Int = -1
   public final var autoUpgradeEnabled: Boolean
   private final var tickDelay: Int
   private final var guiOpenTime: Long

   public fun init() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         INSTANCE.onTick()
      })
   }

   public fun onOpenScreen(title: String) {
      inFactory = StringsKt.contains$default(title, "Chocolate Factory", false, 2, null)
      autoUpgradeEnabled = false
      tickDelay = 0
      stats.clear()
      bestSlot = -1
      chocolatePurse = 0.0
      currentProduction = 0.0
      guiOpenTime = System.currentTimeMillis()
   }

   public fun onTick() {
      if (Config.factoryHelperEnabled && inFactory && autoUpgradeEnabled) {
         val var10000: MinecraftClient = MinecraftClient.method_1551()
         if (var10000.field_1755 == null) {
            autoUpgradeEnabled = false
         } else if (System.currentTimeMillis() - guiOpenTime >= 400L) {
            if (tickDelay > 0) {
               tickDelay += -1
            } else {
               if (bestSlot != -1) {
                  this.clickSlot(bestSlot)
                  tickDelay = RangesKt.random(IntRange(3, 6), Random.Default as Random)
               }
            }
         }
      }
   }

   private fun clickSlot(slotId: Int) {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1761 != null) {
         if (var10000.field_1724 != null) {
            val player: ClientPlayerEntity = var10000.field_1724
            val var7: Screen = var10000.field_1755
            val var8: HandledScreen = var10000.field_1755 as? HandledScreen
            if ((var10000.field_1755 as? HandledScreen) != null) {
               var10000.field_1761.method_2906(var8.method_17577().field_7763, slotId, 0, SlotActionType.field_7790, player as PlayerEntity)
            }
         }
      }
   }

   fun onSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket) {
      if (Config.factoryHelperEnabled && inFactory) {
         val slot: Int = packet.method_11450()
         if (slot <= 53) {
            val var10000: ItemStack = packet.method_11449()
            this.processItem(slot, var10000)
         }
      }
   }

   fun onInventoryUpdate(packet: InventoryS2CPacket) {
      if (Config.factoryHelperEnabled && inFactory) {
         val var10000: java.util.List = packet.comp_3839()
         val `$this$forEachIndexed$iv`: java.lang.Iterable = var10000
         var `index$iv`: Int = 0

         for (`item$iv` in `$this$forEachIndexed$iv`) {
            val var7: Int = `index$iv`++
            if (var7 < 0) {
               CollectionsKt.throwIndexOverflow()
            }

            val itemStack: ItemStack = `item$iv` as ItemStack
            if (var7 <= 53) {
               val var12: FactoryHelper = INSTANCE
               var12.processItem(var7, itemStack)
            }
         }
      }
   }

   fun processItem(slot: Int, itemStack: ItemStack) {
      if (!itemStack.method_7960()) {
         if (itemStack.method_7909() == Items.field_8575 || itemStack.method_7909() == Items.field_8116) {
            val var10000: java.lang.String = itemStack.method_7964().getString()
            val lore: java.util.List = this.getLore(itemStack)
            if (StringsKt.endsWith$default(var10000, "Chocolate", false, 2, null)) {
               val match: MatchResult = chocolateCostRegex.matchEntire(StringsKt.trim(var10000).toString())
               if (match != null) {
                  chocolatePurse = java.lang.Double.parseDouble(
                     StringsKt.replace$default(match.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null)
                  )
                  this.findBest()
               } else if (StringsKt.contains$default(var10000, "Chocolate", false, 2, null)) {
               }
            } else if (var10000 == "Chocolate Production") {
               this.chocoProduction(lore)
            } else if (StringsKt.startsWith$default(var10000, "Coach Jack", false, 2, null)) {
               this.coachStats(slot, lore)
            } else if (StringsKt.startsWith$default(var10000, "Rabbit", false, 2, null)) {
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
            val slot: Int = ((`element$iv` as Entry).getKey() as java.lang.Number).intValue()
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
         val baseChocoMatch: MatchResult = baseChocoRegex.matchEntire(line)
         if (baseChocoMatch != null) {
            baseChoco = java.lang.Double.parseDouble(
               StringsKt.replace$default(baseChocoMatch.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null)
            )
         } else {
            val totalMultxMatch: MatchResult = totalMultiplierRegex.matchEntire(line)
            if (totalMultxMatch != null) {
               totalMultx = java.lang.Double.parseDouble(
                  StringsKt.replace$default(totalMultxMatch.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null)
               )
            } else {
               val match: MatchResult = timeTowerMultiRegex.matchEntire(line)
               if (match != null) {
                  timeTower = java.lang.Double.parseDouble(
                     StringsKt.replace$default(match.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null)
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
         val var10000: MatchResult = chocolateCostRegex.matchEntire(line)
         if (var10000 != null) {
            cost = java.lang.Double.parseDouble(StringsKt.replace$default(var10000.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null))
         }
      }

      if (cost != 0.0) {
         val var9: Double = if (currentProduction == 0.0) 1.0 else currentProduction * 0.01
         stats.put(slot, FactoryHelper.RabbitStat((int)(cost / var9), cost))
         this.findBest()
      }
   }

   private fun rabbitStats(slot: Int, lore: List<String>) {
      var currentCps: Int = 0
      var nextCps: Int = 0
      var cost: Double = 0.0

      for (cpsGain in lore) {
         val costMatch: MatchResult = chocolateCostRegex.matchEntire(cpsGain)
         if (costMatch != null) {
            cost = java.lang.Double.parseDouble(StringsKt.replace$default(costMatch.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null))
         } else {
            val nextCPSMatch: MatchResult = nextCPSRegex.matchEntire(cpsGain)
            if (nextCPSMatch != null) {
               nextCps = Integer.parseInt(StringsKt.replace$default(nextCPSMatch.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null))
            } else {
               val match: MatchResult = Regex.find$default(currentCPSRegex, cpsGain, 0, 2, null)
               if (match != null) {
                  currentCps = Integer.parseInt(StringsKt.replace$default(match.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null))
               }
            }
         }
      }

      if (cost == 0.0) {
         stats.remove(slot)
         this.findBest()
      } else {
         stats.put(slot, FactoryHelper.RabbitStat((int)cost / (if (nextCps - currentCps > 0) nextCps - currentCps else 1), cost))
         this.findBest()
      }
   }

   fun shouldRenderSlot(slot: Slot): Boolean {
      if (Config.factoryHelperEnabled && inFactory) {
         if (bestSlot != -1 && slot.field_7874 == bestSlot) {
            val var10000: Inventory = slot.field_7871
            val var10001: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
            if (!(var10000 == (if (var10001 != null) var10001.method_31548() else null))) {
               true
            }
         }

         false
      } else {
         false
      }
   }

   fun renderSlotOverlay(drawContext: DrawContext, slot: Slot) {
      drawContext.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 16, -2147418113)
   }

   fun getLore(stack: ItemStack): MutableList<java.lang.String> {
      val var10000: LoreComponent = stack.method_58694(DataComponentTypes.field_49632) as LoreComponent
      if (var10000 == null) {
         CollectionsKt.emptyList()
      } else {
         val var13: java.util.List = var10000.comp_2400()
         val `$this$mapTo$iv$iv`: java.lang.Iterable = var13
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var13, 10))

         for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
            `destination$iv$iv`.add((`item$iv$iv` as Text).getString())
         }

         `destination$iv$iv` as java.util.List
      }
   }

   public data class RabbitStat(cpsCost: Int, cost: Double) {
      public final val cpsCost: Int
      public final val cost: Double

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

      public fun copy(cpsCost: Int = this.cpsCost, cost: Double = this.cost): jooon.features.other.FactoryHelper.RabbitStat {
         return FactoryHelper.RabbitStat(cpsCost, cost)
      }

      public override fun toString(): String {
         return "RabbitStat(cpsCost=${this.cpsCost}, cost=${this.cost})"
      }

      public override fun hashCode(): Int {
         return Integer.hashCode(this.cpsCost) * 31 + java.lang.Double.hashCode(this.cost)
      }

      public override operator fun equals(other: Any?): Boolean {
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
