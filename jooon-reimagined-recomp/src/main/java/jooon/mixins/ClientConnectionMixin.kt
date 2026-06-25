package jooon.mixins

import jooon.features.dojo.AutomaticDiscipline
import jooon.features.dojo.Control
import jooon.net.PacketRateLimiter
import net.minecraft.network.ClientConnection
import net.minecraft.network.packet.Packet
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientConnection::class)
class ClientConnectionMixin {
   @Inject(method = ["method_10743"], at = [@At("HEAD")], cancellable = true)
   fun jooonRateLimit(packet: Packet, ci: CallbackInfo) {
      if (PacketRateLimiter.intercept(this as ClientConnection, packet)) {
         ci.cancel()
      } else {
         AutomaticDiscipline.onOutgoingPacket(packet)
         Control.onOutgoingPacket(packet)
      }
   }
}
