package jooon.features.autoexperiments

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.collection.DefaultedList

public object SolverManager {
   public final val chronomatronSolver: CycleBasedChrono = CycleBasedChrono("Chronomatron")
   public final val ultrasequencerSolver: UltrasequencerSolver = UltrasequencerSolver("Ultrasequencer")
   public final val TARGET_SCREEN_TITLES: List<String> = CollectionsKt.listOf(arrayOf("Chronomatron ➜ Stakes", "Ultrasequencer ➜ Stakes"))

   public fun isExperimentScreen(title: String): Boolean {
      return TARGET_SCREEN_TITLES.contains(title)
   }

   public fun init() {
      ScreenEvents.BEFORE_INIT.register({ var0: MinecraftClient, screen: Screen, var2: Int, var3: Int ->
         if (screen is HandledScreen) {
            val var10000: java.lang.String = (screen as HandledScreen).method_25440().getString()
            if (StringsKt.startsWith$default(var10000, "Chronomatron (", false, 2, null)) {
               chronomatronSolver.start(screen as HandledScreen<*>)
            } else if (StringsKt.startsWith$default(var10000, "Ultrasequencer (", false, 2, null)) {
               ultrasequencerSolver.start(screen as HandledScreen<*>)
            } else {
               chronomatronSolver.resetClickStack()
               ultrasequencerSolver.clearItemStack()
            }
         }
      })
   }

   public fun starterMotor() {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1761 != null) {
         val gameMode: ClientPlayerInteractionManager = var10000.field_1761
         if (var10000.field_1724 != null) {
            val player: ClientPlayerEntity = var10000.field_1724
            if (var10000.field_1724.field_7512 != null) {
               val containerMenu: ScreenHandler = var10000.field_1724.field_7512

               for (i in 24 downTo 21) {
                  val var8: DefaultedList = containerMenu.field_7761
                  val var9: Slot = CollectionsKt.getOrNull(var8 as java.util.List, i) as Slot
                  if (var9 != null) {
                     val var10: ItemStack = var9.method_7677()
                     if (!var10.method_7960() && !(var10.method_7909() == Items.field_8298)) {
                        gameMode.method_2906(containerMenu.field_7763, i, 0, SlotActionType.field_7790, player as PlayerEntity)
                     }
                  }
               }
            }
         }
      }
   }
}
