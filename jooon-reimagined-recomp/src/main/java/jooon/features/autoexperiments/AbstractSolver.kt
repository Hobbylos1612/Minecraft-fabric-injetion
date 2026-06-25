package jooon.features.autoexperiments

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
abstract class AbstractSolver {
   val containerName: String

   constructor(containerName: String) {
      this.containerName = containerName
   }

   open fun start(screen: HandledScreen) {
      ScreenEvents.afterTick(screen as Screen).register({ var2: Screen ->
         `this$0`.tick(`$screen`)
      })
   }

   abstract fun tick(var1: HandledScreen)
}
