package jooon.mixins

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin([PlayerInteractEntityC2SPacket::class])
public interface ServerboundInteractPacketAccessor {
   @Accessor("field_12870")
   public abstract fun jooonEntityId(): Int {
   }
}
