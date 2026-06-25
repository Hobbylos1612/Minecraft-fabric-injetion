package jooon.features.other

import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Locale
import jooon.JooonReimagined
import jooon.config.Config
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nMelody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Melody.kt\njooon/features/other/Melody\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,284:1\n1747#2,3:285\n1002#2,2:288\n1002#2,2:290\n1#3:292\n*S KotlinDebug\n*F\n+ 1 Melody.kt\njooon/features/other/Melody\n*L\n124#1:285,3\n136#1:288,2\n171#1:290,2\n*E\n"])
public object Melody {
   private final var inHarp: Boolean
   private final var wasInHarp: Boolean
   private final var tpsPreviousTime: Long
   private final var tpsPreviousGameTime: Long = java.lang.Long.MIN_VALUE
   private final var averageTps: Float?
   private final var displayedTps: Float?
   private final var displayedTpsTicker: Int
   private final val lock: Any = Any()
   private final val primaryQueue: MutableList<jooon.features.other.Melody.PrimaryClick> = ArrayList() as java.util.List
   private final val secondaryQueue: MutableList<jooon.features.other.Melody.SecondaryClick> = ArrayList() as java.util.List
   private final val slotDebounceUntil: MutableMap<Int, Long> = LinkedHashMap() as java.util.Map

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         INSTANCE.updateHarpContext(client)
         INSTANCE.updateDisplayedTps()
         INSTANCE.processPrimaryQueue()
         INSTANCE.processSecondaryQueue()
      })
   }

   fun onSlotPacket(packet: ScreenHandlerSlotUpdateS2CPacket) {
      this.getMc().execute({ 
         INSTANCE.onSlotPacketMain(`$packet`)
      })
   }

   fun onSetTimePacket(packet: WorldTimeUpdateS2CPacket) {
      val now: Long = System.currentTimeMillis()
      val gameTime: Long = packet.comp_3219()
      if (tpsPreviousTime != 0L && tpsPreviousGameTime != java.lang.Long.MIN_VALUE) {
         val elapsedMs: Long = now - tpsPreviousTime
         val elapsedTicks: Long = gameTime - tpsPreviousGameTime
         if (elapsedMs > 0L && gameTime - tpsPreviousGameTime > 0L && gameTime - tpsPreviousGameTime <= 200L) {
            averageTps = RangesKt.coerceIn((float)elapsedTicks * 1000.0F / (float)elapsedMs, 0.0F, 20.0F)
         }
      }

      tpsPreviousTime = now
      tpsPreviousGameTime = gameTime
   }

   fun renderHarpTooltip(context: DrawContext, screen: HandledScreen<*>) {
      if (Config.enableAutoMelody && inHarp && this.isHarpScreen(screen)) {
         val guiLeft: Int = this.readScreenInt(screen, arrayOf("leftPos", "left"), (screen.field_22789 - 176) / 2)
         val var13: Int = this.readScreenInt(screen, arrayOf("topPos", "top"), (screen.field_22790 - 166) / 2)
         val var15: Int = guiLeft + this.readScreenInt(screen, arrayOf("imageWidth", "backgroundWidth"), 176) / 2
         val y: Int = RangesKt.coerceAtLeast(var13 - 132, 8)
         val lines: java.util.List = CollectionsKt.listOf(
            arrayOf("§aJooonReimagined§f: §dAuto Melody!", "§7§lTPS: ${this.coloredTpsValue()}", "§7§o(Higher is better!)")
         )
         var var16: Int = y

         for (line in lines) {
            val var10000: MutableText = Text.method_43470(line)
            context.method_51439(
               this.getMc().field_1772, var10000 as Text, var15 - this.getMc().field_1772.method_27525(var10000 as StringVisitable) / 2, var16, -1, true
            )
            var16 += this.getMc().field_1772.field_2000 + 2
         }
      }
   }

   public fun coloredTpsValue(): String {
      if (displayedTps != null) {
         val tps: Float = displayedTps
         return "${if (tps >= 17.0F) "§a" else (if (tps >= 14.0F) "§e" else "§c")}${this.formatTps(tps)}"
      } else {
         return "§7..."
      }
   }

   private fun processPrimaryQueue() {
      if (Config.enableAutoMelody && inHarp) {
         val now: Long = System.currentTimeMillis()
         var next: Any = null
         synchronized (lock) {
            if (!primaryQueue.isEmpty() && (CollectionsKt.first(primaryQueue) as Melody.PrimaryClick).dueMs <= now) {
               next = primaryQueue.remove(0)
            }

            val var17: Boolean = slotDebounceUntil.entrySet().removeIf({ p0: Any ->
               `$tmp0`(p0)
            })
         }

         val var10000: Melody.PrimaryClick = next as Melody.PrimaryClick
         if (next as Melody.PrimaryClick != null) {
            val click: Melody.PrimaryClick = var10000
            val var22: ClientPlayerEntity = this.getMc().field_1724
            if (var22 != null) {
               if (var22.field_7512 != null) {
                  val var18: ScreenHandler = var22.field_7512
                  var upperSlot: Int = (var22.field_7512.field_7761 as java.util.Collection).size()
                  val stackedDelay: Int = var10000.targetSlot
                  if (0 <= stackedDelay && stackedDelay < upperSlot) {
                     this.sendHumanLeftClick(var10000.targetSlot)
                     upperSlot = var10000.laneSlot - 9
                     if (0 <= upperSlot && upperSlot < (var18.field_7761 as java.util.Collection).size()) {
                        val var10001: ItemStack = (var18.field_7761.get(upperSlot) as Slot).method_7677()
                        if (this.isWoolStack(var10001)) {
                           val var20: Long = this.computeStackedDelayMs()
                           synchronized (lock) {
                              secondaryQueue.add(Melody.SecondaryClick(System.currentTimeMillis() + var20, click.targetSlot))
                              val `$this$sortBy$iv`: java.util.List = secondaryQueue
                              if (secondaryQueue.size() > 1) {
                                 CollectionsKt.sortWith(`$this$sortBy$iv`, Melody$processPrimaryQueue$lambda$9$$inlined$sortBy$1())
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
         val now: Long = System.currentTimeMillis()
         var next: Any = null
         synchronized (lock) {
            if (!secondaryQueue.isEmpty() && (CollectionsKt.first(secondaryQueue) as Melody.SecondaryClick).dueMs <= now) {
               next = secondaryQueue.remove(0)
            }
         }

         val var10000: Melody.SecondaryClick = next as Melody.SecondaryClick
         if (next as Melody.SecondaryClick != null) {
            val var13: ClientPlayerEntity = this.getMc().field_1724
            if (var13 != null) {
               if (var13.field_7512 != null) {
                  val var7: Int = (var13.field_7512.field_7761 as java.util.Collection).size()
                  val var8: Int = var10000.targetSlot
                  if (0 <= var8 && var8 < var7) {
                     this.sendHumanLeftClick(var10000.targetSlot)
                  }
               }
            }
         }
      }
   }

   private fun sendHumanLeftClick(slot: Int) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         if (var10000.field_7512 != null) {
            val menu: ScreenHandler = var10000.field_7512
            if (0 <= slot && slot < (var10000.field_7512.field_7761 as java.util.Collection).size()) {
               val var4: ClientPlayerInteractionManager = this.getMc().field_1761
               if (var4 != null) {
                  var4.method_2906(menu.field_7763, slot, 0, SlotActionType.field_7790, var10000 as PlayerEntity)
               }
            }
         }
      }
   }

   fun updateHarpContext(client: MinecraftClient) {
      val screen: HandledScreen = client.field_1755 as? HandledScreen
      val var10000: Boolean = (client.field_1755 as? HandledScreen) != null && this.isHarpScreen(client.field_1755 as? HandledScreen)
      if (var10000 && !wasInHarp) {
         inHarp = true
         val var9: java.lang.String = screen.method_25440().getString()
         val songName: java.lang.String = StringsKt.trim(StringsKt.removePrefix(var9, "Harp -")).toString()
         val var8: java.lang.String = if (songName.length() > 0)
            "${JooonReimagined.Companion.PREFIX_CLEAN}§dDetected song: §l$songName§d, starting now!"
            else
            "${JooonReimagined.Companion.PREFIX_CLEAN}§dDetected Harp, starting now!"
            if (client.field_1724 != null) {
            client.field_1724.method_7353(Text.method_43470(var8) as Text, false)
         }
      } else if (!var10000 && wasInHarp) {
         this.clearRuntimeState()
      }

      wasInHarp = var10000
      inHarp = var10000
   }

   fun isHarpScreen(screen: HandledScreen<*>): Boolean {
      val var10000: java.lang.String = screen.method_25440().getString()
      StringsKt.startsWith$default(var10000, "Harp -", false, 2, null)
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
      return RangesKt.coerceAtLeast(Config.clickDelayMs, 0)
   }

   private fun computeStackedDelayMs(): Long {
      return RangesKt.coerceAtLeast(Config.clickDelayMs, 0) + 100L
   }

   private fun updateDisplayedTps() {
      val var1: Int = displayedTpsTicker++
      if (displayedTpsTicker >= 5) {
         displayedTpsTicker = 0
         displayedTps = averageTps
      }
   }

   private fun formatTps(tps: Float): String {
      val var2: Locale = Locale.US
      val var5: Array<Any> = arrayOf(RangesKt.coerceIn(tps, 0.0F, 20.0F))
      val var10000: java.lang.String = java.lang.String.format(var2, "%.1f", Arrays.copyOf(var5, var5.length))
      return var10000
   }

   fun readScreenInt(screen: HandledScreen<*>, names: Array<java.lang.String>, fallback: Int): Int {
      // $VF: Unable to resugar Kotlin loop from Java for loop
      var var15: Any = screen.getClass()
      while (true) {
         if (var15 != null) break
         for (name in names) {
            val var9: Melody = this

            var `$this$readScreenInt_u24lambda_u2412`: Melody
            try {
               `$this$readScreenInt_u24lambda_u2412` = var9
               val field: Field = var15.getDeclaredField(name)
               field.setAccessible(true)
               val var13: Any = field.get(screen)
               `$this$readScreenInt_u24lambda_u2412` = (Melody)Result.constructor_impl/* $VF was: constructor-impl */(
                  if ((var13 as? java.lang.Number) != null) (var13 as? java.lang.Number).intValue() else null
               )
            } catch (var14: java.lang.Throwable) {
               `$this$readScreenInt_u24lambda_u2412` = (Melody)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var14))
            }

            val value: Int = (
               if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$readScreenInt_u24lambda_u2412`)) null else `$this$readScreenInt_u24lambda_u2412`
            ) as Int
            if (value != null) {
               value
            }
         }

         var15 = var15.getSuperclass()
      }

      fallback
   }

   fun isWoolStack(stack: ItemStack): Boolean {
      val var3: Item = stack.method_7909()
      (var3 as? BlockItem) != null && (var3 as? BlockItem).method_7711().method_9564().method_26164(BlockTags.field_15481)
   }

   private data class PrimaryClick(dueMs: Long, laneSlot: Int, targetSlot: Int) {
      public final val dueMs: Long
      public final val laneSlot: Int
      public final val targetSlot: Int

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

      public fun copy(dueMs: Long = this.dueMs, laneSlot: Int = this.laneSlot, targetSlot: Int = this.targetSlot): jooon.features.other.Melody.PrimaryClick {
         return Melody.PrimaryClick(dueMs, laneSlot, targetSlot)
      }

      public override fun toString(): String {
         return "PrimaryClick(dueMs=${this.dueMs}, laneSlot=${this.laneSlot}, targetSlot=${this.targetSlot})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Long.hashCode(this.dueMs) * 31 + Integer.hashCode(this.laneSlot)) * 31 + Integer.hashCode(this.targetSlot)
      }

      public override operator fun equals(other: Any?): Boolean {
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
      public final val dueMs: Long
      public final val targetSlot: Int

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

      public fun copy(dueMs: Long = this.dueMs, targetSlot: Int = this.targetSlot): jooon.features.other.Melody.SecondaryClick {
         return Melody.SecondaryClick(dueMs, targetSlot)
      }

      public override fun toString(): String {
         return "SecondaryClick(dueMs=${this.dueMs}, targetSlot=${this.targetSlot})"
      }

      public override fun hashCode(): Int {
         return java.lang.Long.hashCode(this.dueMs) * 31 + Integer.hashCode(this.targetSlot)
      }

      public override operator fun equals(other: Any?): Boolean {
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
