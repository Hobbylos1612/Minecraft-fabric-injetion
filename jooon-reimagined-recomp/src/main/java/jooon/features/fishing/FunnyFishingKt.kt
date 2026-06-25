package jooon.features.fishing

import net.minecraft.client.network.ClientPlayerEntity

fun ClientPlayerEntity.sendCommand(cmd: String) {
   this.networkHandler.sendChatCommand(cmd)
}
