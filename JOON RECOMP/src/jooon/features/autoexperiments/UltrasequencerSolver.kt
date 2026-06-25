package jooon.features.autoexperiments

import java.util.ArrayList
import jooon.config.Config
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

public class UltrasequencerSolver(containerName: String) : AbstractSolver(containerName) {
   private MinecraftClient client;
   private final val clickStack: ArrayList<Int>
   private final var currentDelay: Int

   init {
      val var10001: MinecraftClient = MinecraftClient.method_1551()
      this.client = var10001
      this.clickStack = ArrayList<>()
   }

   override fun tick(screen: HandledScreen<*>) {
      if (Config.autoExperiments) {
         if (this.currentDelay > 0) {
            this.currentDelay += -1
         } else {
            val var10000: ScreenHandler = screen.method_17577()
            val menu: ScreenHandler = var10000
            val var8: Item = var10000.method_7611(49).method_7677().method_7909()
            if (var8 == Items.field_8801) {
               repeat(44) { i ->
                  val var9: ItemStack = menu.method_7611(i).method_7677()
                  val var10: java.lang.String = Registries.field_41178.method_10221(var9.method_7909()).method_12832()
                  if (!StringsKt.endsWith$default(var10, "pane", false, 2, null) && var9.method_7947() == this.clickStack.size() + 1) {
                     this.clickStack.add(i)
                  }
               }
            } else if (var8 == Items.field_8557) {
               this.inputSequence(screen)
            }
         }
      }
   }

   fun inputSequence(screen: HandledScreen<*>) {
      if (this.client.field_1724 != null) {
         val player: ClientPlayerEntity = this.client.field_1724
         val var10000: ItemStack = this.client.field_1724.field_7512.method_34255()
         if (var10000.method_7960() && !this.clickStack.isEmpty()) {
            val targetSlot: Int = (CollectionsKt.first(this.clickStack) as java.lang.Number).intValue()
            if (this.client.field_1761 != null) {
               this.client.field_1761.method_2906(screen.method_17577().field_7763, targetSlot, 0, SlotActionType.field_7790, player as PlayerEntity)
            }

            this.clickStack.remove(0)
            this.currentDelay = Config.autoExperimentsTickDelay
         }
      }
   }

   public fun clearItemStack() {
      this.clickStack.clear()
      this.currentDelay = 0
   }
}
