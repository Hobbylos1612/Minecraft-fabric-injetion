package jooon.mixins

import jooon.commands.PingCommand
import jooon.features.dojo.AutomaticSwiftness
import jooon.features.dojo.Mastery
import jooon.features.dungeons.map.DungeonMapFeature
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.solvers.IceFillSolver
import jooon.features.dungeons.solvers.TeleportMazeSolver
import jooon.features.fishing.FunnyFishing
import jooon.features.galatea.StridersurferFishingMacro
import jooon.features.mirrorverse.DRSv2
import jooon.features.other.AutoWardrobe
import jooon.features.other.FactoryHelper
import jooon.features.other.Melody
import jooon.features.other.WitherShieldOverlay
import jooon.util.PingUtil
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientPlayNetworkHandler::class)
class MixinClientPlayNetworkHandler {
   @Inject(method = ["method_11109"], at = [@At("HEAD")])
   fun jooonOnSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket, ci: CallbackInfo) {
      Melody.onSlotPacket(packet)
      AutoWardrobe.handleSlotUpdate(packet.getSlot())
      FactoryHelper.onSlotUpdate(packet)
   }

   @Inject(method = ["method_11079"], at = [@At("HEAD")])
   fun jooonOnSetTime(packet: WorldTimeUpdateS2CPacket, ci: CallbackInfo) {
      Melody.onSetTimePacket(packet)
   }

   @Inject(method = ["method_17587"], at = [@At("HEAD")])
   fun jooonOnOpenScreen(packet: OpenScreenS2CPacket, ci: CallbackInfo) {

      AutoWardrobe.handleOpen(var10000, packet.getSyncId())
      FactoryHelper.onOpenScreen(var10000)
   }

   @Inject(method = ["method_11129"], at = [@At("HEAD")])
   fun jooonOnStats(packet: StatisticsS2CPacket, ci: CallbackInfo) {
      PingCommand.handleStatResponse()
   }

   @Inject(method = ["method_11077"], at = [@At("TAIL")])
   fun jooonOnParticle(packet: ParticleS2CPacket, ci: CallbackInfo) {
      var key: String = ""

      try {
         var var7: String
         run label25@{


            if (id != null) {
               var7 = id.toString()
               if (var7 != null) {
                  return@label25
               }
            }

            var7 = ""
         }

         key = var7
      } catch (var6: java.lang.Throwable) {
      }

      FunnyFishing.onParticle(packet.getX(), packet.getY(), packet.getZ(), key, packet.getCount(), packet.getSpeed())
   }

   @Inject(method = ["method_11146"], at = [@At("HEAD")])
   fun jooonOnPlaySound(pkt: PlaySoundS2CPacket, ci: CallbackInfo) {
      var var10000: String
      run label30@{
         DRSv2.onSoundPacket(pkt)

         if (id != null) {
            var10000 = id.toString()
            if (var10000 != null) {
               return@label30
            }
         }

         var10000 = ""
      }

      if (pkt.getSound().value() == SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE && pkt.getVolume() == 1.0F && Math.abs(pkt.getPitch() - 0.6984127F) < 0.001F) {
         WitherShieldOverlay.trigger()
      }

      FunnyFishing.onSound(var10000, pkt.getX(), pkt.getY(), pkt.getZ())
   }

   @Inject(method = ["method_11125"], at = [@At("HEAD")])
   fun jooonOnPlaySoundFromEntity(pkt: PlaySoundFromEntityS2CPacket, ci: CallbackInfo) {
      var var10000: String
      run label22@{

         if (id != null) {
            var10000 = id.toString()
            if (var10000 != null) {
               return@label22
            }
         }

         var10000 = ""
      }

      var x: Double = 0.0
      var y: Double = 0.0
      var z: Double = 0.0
      if (MinecraftClient.getInstance().world != null) {


         if (e != null) {
            x = e.getX()
            y = e.getY()
            z = e.getZ()
         }
      }

      FunnyFishing.onSound(var10000, x, y, z)
   }

   @Inject(method = ["method_11132"], at = [@At("HEAD")])
   fun jooonOnVelocity(pkt: EntityVelocityUpdateS2CPacket, ci: CallbackInfo) {
      FunnyFishing.onBobberVelocity(pkt.getEntityId(), pkt.getVelocity().x, pkt.getVelocity().y, pkt.getVelocity().z)
   }

   @Inject(method = ["method_11136"], at = [@At("TAIL")])
   fun jooonOnBlockUpdate(packet: BlockUpdateS2CPacket, ci: CallbackInfo) {

      var var10001: BlockPos = packet.getPos()
      var var10002: BlockState = packet.getState()
      var10000.onBlockUpdate(var10001, var10002)

      var10001 = packet.getPos()
      var10002 = packet.getState()
      var3.onBlockUpdate(var10001, var10002)

      var10001 = packet.getPos()
      var10002 = packet.getState()
      var4.onBlockUpdate(var10001, var10002)
   }

   @Inject(method = ["method_11100"], at = [@At("TAIL")])
   fun jooonSwiftnessBatch(pkt: ChunkDeltaUpdateS2CPacket, ci: CallbackInfo) {
      if (MinecraftClient.getInstance().world != null) {
         pkt.visitUpdates({ posIn: BlockPos, state: BlockState ->
            Mastery.onBlockUpdate(posIn, state)
         })
         AutomaticSwiftness.onSectionBlocksUpdate(pkt)
         IceFillSolver.onSectionBlocksUpdate(pkt)
      }
   }

   @Inject(method = ["method_11157"], at = [@At("RETURN")])
   fun jooonOnMovePlayer(packet: PlayerPositionLookS2CPacket, ci: CallbackInfo) {
      TeleportMazeSolver.onPositionPacket(packet)
      StridersurferFishingMacro.onPositionPacket(packet)
   }

   @Inject(method = ["method_11088"], at = [@At("HEAD")])
   fun jooonOnMapData(packet: MapUpdateS2CPacket, ci: CallbackInfo) {
      DungeonMapFeature.onMapPacket(packet)
   }

   @Inject(method = ["method_11113"], at = [@At("HEAD")])
   fun jooonOnPlayerInfo(packet: PlayerListS2CPacket, ci: CallbackInfo) {
      DungeonScanner.onPlayerInfoPacket(packet)
   }

   @Inject(method = ["method_12666"], at = [@At("HEAD")])
   fun jooonOnPong(packet: PingResultS2CPacket, ci: CallbackInfo) {
      PingUtil.onPongResponse(packet)
   }
}
