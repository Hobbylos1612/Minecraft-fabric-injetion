package jooon.features.other

import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Locale
import jooon.JooonReimagined
import jooon.config.Config
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.registry.tag.BlockTags
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

object Melody {
   private var inHarp: Boolean
   private var wasInHarp: Boolean
   private var tpsPreviousTime: Long
   private var tpsPreviousGameTime: Long = java.lang.Long.MIN_VALUE
   private var averageTps: Float?
   private var displayedTps: Float?
   private var displayedTpsTicker: Int
   private val lock: Any = Any()
   private val primaryQueue: MutableList<jooon.features.other.Melody.PrimaryClick> = ArrayList() as java.util.List
   private val secondaryQueue: MutableList<jooon.features.other.Melody.SecondaryClick> = ArrayList() as java.util.List
   private val slotDebounceUntil: MutableMap<Int, Long> = LinkedHashMap() as java.util.Map

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         updateHarpContext(client)
         updateDisplayedTps()
         processPrimaryQueue()
         processSecondaryQueue()
      })
   }

   fun onSlotPacket(packet: ScreenHandlerSlotUpdateS2CPacket) {
      this.getMc().execute({ 
         onSlotPacketMain(`$packet`)
      })
   }

   fun onSetTimePacket(packet: WorldTimeUpdateS2CPacket) {


      if (tpsPreviousTime != 0L && tpsPreviousGameTime != java.lang.Long.MIN_VALUE) {


         if (elapsedMs > 0L && gameTime - tpsPreviousGameTime > 0L && gameTime - tpsPreviousGameTime <= 200L) {
            averageTps = (elapsedTicks.toFloat() * 1000.0F / elapsedMs.toFloat()).coerceIn(0.0F, 20.0F)
         }
      }

      tpsPreviousTime = now
      tpsPreviousGameTime = gameTime
   }

   fun renderHarpTooltip(context: DrawContext, screen: HandledScreen) {
      if (Config.enableAutoMelody && inHarp && this.isHarpScreen(screen)) {




         val lines: java.util.List = listOf(
            arrayOf("§aJooonReimagined§f: §dAuto Melody!", "§7§lTPS: ${this.coloredTpsValue()}", "§7§o(Higher is better!)")
         )
         var var16: Int = y

         for (line in lines) {

            context.drawText(
               this.getMc().textRenderer, var10000 as Text, var15 - this.getMc().textRenderer.getWidth(var10000 as StringVisitable) / 2, var16, -1, true
            )
            var16 += this.getMc().textRenderer.fontHeight + 2
         }
      }
   }

   fun coloredTpsValue(): String {
      if (displayedTps != null) {

         return "${if (tps >= 17.0F) "§a" else (if (tps >= 14.0F) "§e" else "§c")}${this.formatTps(tps)}"
      } else {
         return "§7..."
      }
   }

   private fun processPrimaryQueue() {
      if (Config.enableAutoMelody && inHarp) {

         var next: Any = null
         synchronized (lock) {
            if (!primaryQueue.isEmpty() && (first(primaryQueue) as Melody.PrimaryClick).dueMs <= now) {
               next = primaryQueue.remove(0)
            }

               ``(p0)
            })
         }

         val var10000: Melody.PrimaryClick = next as Melody.PrimaryClick
         if (next as Melody.PrimaryClick != null) {
            val click: Melody.PrimaryClick = var10000

            if (var22 != null) {
               if (var22.currentScreenHandler != null) {

                  var upperSlot: Int = (var22.currentScreenHandler.slots as java.util.Collection).size()

                  if (0 <= stackedDelay && stackedDelay < upperSlot) {
                     this.sendHumanLeftClick(var10000.targetSlot)
                     upperSlot = var10000.laneSlot - 9
                     if (0 <= upperSlot && upperSlot < (var18.slots as java.util.Collection).size()) {

                        if (this.isWoolStack(var10001)) {

                           synchronized (lock) {
                              secondaryQueue.add(Melody.SecondaryClick(System.currentTimeMillis() + var20, click.targetSlot))
                              val `this$iv`: java.util.List = secondaryQueue
                              if (secondaryQueue.size() > 1) {
                                 sortWith(`this$iv`, Melody$processPrimaryQueue$lambda$9$$inlined$sortBy$1())
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private fun processSecondaryQueue() {
      if (Config.enableAutoMelody && inHarp) {

         var next: Any = null
         synchronized (lock) {
            if (!secondaryQueue.isEmpty() && (first(secondaryQueue) as Melody.SecondaryClick).dueMs <= now) {
               next = secondaryQueue.remove(0)
            }
         }

         val var10000: Melody.SecondaryClick = next as Melody.SecondaryClick
         if (next as Melody.SecondaryClick != null) {

            if (var13 != null) {
               if (var13.currentScreenHandler != null) {


                  if (0 <= var8 && var8 < var7) {
                     this.sendHumanLeftClick(var10000.targetSlot)
                  }
               }
            }
         }
      }
   }

   private fun sendHumanLeftClick(slot: Int) {

      if (var10000 != null) {
         if (var10000.currentScreenHandler != null) {

            if (0 <= slot && slot < (var10000.currentScreenHandler.slots as java.util.Collection).size()) {

               if (var4 != null) {
                  var4.clickSlot(menu.syncId, slot, 0, SlotActionType.PICKUP, var10000 as PlayerEntity)
               }
            }
         }
      }
   }

   fun updateHarpContext(client: MinecraftClient) {
      val screen: HandledScreen = client.currentScreen as? HandledScreen

      if (var10000 && !wasInHarp) {
         inHarp = true



            "${JooonReimagined.Companion.PREFIX_CLEAN}§dDetected song: §l$songName§d, starting now!"
return else
            "${JooonReimagined.Companion.PREFIX_CLEAN}§dDetected Harp, starting now!"
            if (client.player != null) {
            client.player.sendMessage(Text.literal(var8) as Text, false)
         }
      } else if (!var10000 && wasInHarp) {
         this.clearRuntimeState()
      }

      wasInHarp = var10000
      inHarp = var10000
   }

   fun isHarpScreen(screen: HandledScreen): Boolean {

      startsWith$default(var10000, "Harp -", false, 2, null)
   }

   private fun clearRuntimeState() {
      synchronized (lock) {
         primaryQueue.clear()
         secondaryQueue.clear()
         slotDebounceUntil.clear()
      }

      inHarp = false
      wasInHarp = false
   }

   private fun computePrimaryDelayMs(): Long {
      return (Config.clickDelayMs).coerceAtLeast(0)
   }

   private fun computeStackedDelayMs(): Long {
      return (Config.clickDelayMs).coerceAtLeast(0) + 100L
   }

   private fun updateDisplayedTps() {

      if (displayedTpsTicker >= 5) {
         displayedTpsTicker = 0
         displayedTps = averageTps
      }
   }

   private fun formatTps(tps: Float): String {

      val var5: Array<Any> = arrayOf((tps).coerceIn(0.0F, 20.0F))

      return var10000
   }

   fun readScreenInt(screen: HandledScreen, names: Array<String>, fallback: Int): Int {
      // $VF: Unable to resugar Kotlin loop from Java for loop
      var var15: Any = screen.getClass()
      while (true) {
         if (var15 != null) break
         for (name in names) {


            var `this24lambda_u2412`: Melody
            try {
               `this24lambda_u2412` = var9

               field.setAccessible(true)

               `this24lambda_u2412` = Result(
                  if ((var13 as? java.lang.Number) != null) (var13 as? java.lang.Number).intValue() else null
               )
            } catch (var14: java.lang.Throwable) {
               `this24lambda_u2412` = Result(ResultKt.createFailure(var14))
            }

               if (Result.isFailure/* $VF was: isFailure-impl */(`this24lambda_u2412`)) null else `this24lambda_u2412`
            ) as Int
            if (value != null) {
return value
            }
         }

         var15 = var15.getSuperclass()
      }
return fallback
   }

   fun isWoolStack(stack: ItemStack): Boolean {

      (var3 as? BlockItem) != null && (var3 as? BlockItem).getBlock().getDefaultState().isIn(BlockTags.WOOL)
   }

   private data class PrimaryClick(dueMs: Long, laneSlot: Int, targetSlot: Int) {
      val dueMs: Long
      val laneSlot: Int
      val targetSlot: Int

      init {
         this.dueMs = dueMs
         this.laneSlot = laneSlot
         this.targetSlot = targetSlot
      }

      public operator fun component1(): Long {
         return this.dueMs
      }

      public operator fun component2(): Int {
         return this.laneSlot
      }

      public operator fun component3(): Int {
         return this.targetSlot
      }

      fun copy(dueMs: Long = this.dueMs, laneSlot: Int = this.laneSlot, targetSlot: Int = this.targetSlot): jooon.features.other.Melody.PrimaryClick {
         return Melody.PrimaryClick(dueMs, laneSlot, targetSlot)
      }

      override fun toString(): String {
         return "PrimaryClick(dueMs=${this.dueMs}, laneSlot=${this.laneSlot}, targetSlot=${this.targetSlot})"
      }

      override fun hashCode(): Int {
         return (java.lang.Long.hashCode(this.dueMs) * 31 + Integer.hashCode(this.laneSlot)) * 31 + Integer.hashCode(this.targetSlot)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is Melody.PrimaryClick
               && this.dueMs == (other as Melody.PrimaryClick).dueMs
               && this.laneSlot == (other as Melody.PrimaryClick).laneSlot
               && this.targetSlot == (other as Melody.PrimaryClick).targetSlot
            }
      }
   }

   private data class SecondaryClick(dueMs: Long, targetSlot: Int) {
      val dueMs: Long
      val targetSlot: Int

      init {
         this.dueMs = dueMs
         this.targetSlot = targetSlot
      }

      public operator fun component1(): Long {
         return this.dueMs
      }

      public operator fun component2(): Int {
         return this.targetSlot
      }

      fun copy(dueMs: Long = this.dueMs, targetSlot: Int = this.targetSlot): jooon.features.other.Melody.SecondaryClick {
         return Melody.SecondaryClick(dueMs, targetSlot)
      }

      override fun toString(): String {
         return "SecondaryClick(dueMs=${this.dueMs}, targetSlot=${this.targetSlot})"
      }

      override fun hashCode(): Int {
         return java.lang.Long.hashCode(this.dueMs) * 31 + Integer.hashCode(this.targetSlot)
      }

      override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is Melody.SecondaryClick
               && this.dueMs == (other as Melody.SecondaryClick).dueMs
               && this.targetSlot == (other as Melody.SecondaryClick).targetSlot
            }
      }
   }
}
