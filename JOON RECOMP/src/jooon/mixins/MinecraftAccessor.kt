package jooon.mixins

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilderStorage
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin([MinecraftClient::class])
public interface MinecraftAccessor {
   @Accessor("field_20909")
   fun getRenderBuffers(): BufferBuilderStorage
}
