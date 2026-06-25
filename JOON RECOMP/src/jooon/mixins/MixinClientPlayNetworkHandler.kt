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

@Mixin([ClientPlayNetworkHandler::class])
public class MixinClientPlayNetworkHandler {
   @Inject(method = ["method_11109"], at = [@At("HEAD")])
   fun jooonOnSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket, ci: CallbackInfo) {
      Melody.INSTANCE.onSlotPacket(packet)
      AutoWardrobe.INSTANCE.handleSlotUpdate(packet.method_11450())
      FactoryHelper.INSTANCE.onSlotUpdate(packet)
   }

   @Inject(method = ["method_11079"], at = [@At("HEAD")])
   fun jooonOnSetTime(packet: WorldTimeUpdateS2CPacket, ci: CallbackInfo) {
      Melody.INSTANCE.onSetTimePacket(packet)
   }

   @Inject(method = ["method_17587"], at = [@At("HEAD")])
   fun jooonOnOpenScreen(packet: OpenScreenS2CPacket, ci: CallbackInfo) {
      val var10000: java.lang.String = packet.method_17594().getString()
      AutoWardrobe.INSTANCE.handleOpen(var10000, packet.method_17592())
      FactoryHelper.INSTANCE.onOpenScreen(var10000)
   }

   @Inject(method = ["method_11129"], at = [@At("HEAD")])
   fun jooonOnStats(packet: StatisticsS2CPacket, ci: CallbackInfo) {
      PingCommand.INSTANCE.handleStatResponse()
   }

   @Inject(method = ["method_11077"], at = [@At("TAIL")])
   fun jooonOnParticle(packet: ParticleS2CPacket, ci: CallbackInfo) {
      var key: java.lang.String = ""

      try {
         var var7: java.lang.String
         run label25@{
            val var10000: ParticleType = packet.method_11551().method_10295()
            val id: Identifier = Registries.field_41180.method_10221(var10000)
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

      FunnyFishing.onParticle(packet.method_11544(), packet.method_11547(), packet.method_11546(), key, packet.method_11545(), packet.method_11543())
   }

   @Inject(method = ["method_11146"], at = [@At("HEAD")])
   fun jooonOnPlaySound(pkt: PlaySoundS2CPacket, ci: CallbackInfo) {
      var var10000: java.lang.String
      run label30@{
         DRSv2.INSTANCE.onSoundPacket(pkt)
         val id: Identifier = Registries.field_41172.method_10221(pkt.method_11894().comp_349())
         if (id != null) {
            var10000 = id.toString()
            if (var10000 != null) {
               return@label30
            }
         }

         var10000 = ""
      }

      if (pkt.method_11894().comp_349() == SoundEvents.field_14905 && pkt.method_11891() == 1.0F && Math.abs(pkt.method_11892() - 0.6984127F) < 0.001F) {
         WitherShieldOverlay.INSTANCE.trigger()
      }

      FunnyFishing.onSound(var10000, pkt.method_11890(), pkt.method_11889(), pkt.method_11893())
   }

   @Inject(method = ["method_11125"], at = [@At("HEAD")])
   fun jooonOnPlaySoundFromEntity(pkt: PlaySoundFromEntityS2CPacket, ci: CallbackInfo) {
      var var10000: java.lang.String
      run label22@{
         val id: Identifier = Registries.field_41172.method_10221(pkt.method_11882().comp_349())
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
      if (MinecraftClient.method_1551().field_1687 != null) {
         val var12: ClientWorld = MinecraftClient.method_1551().field_1687
         val e: Entity = var12.method_8469(pkt.method_11883())
         if (e != null) {
            x = e.method_23317()
            y = e.method_23318()
            z = e.method_23321()
         }
      }

      FunnyFishing.onSound(var10000, x, y, z)
   }

   @Inject(method = ["method_11132"], at = [@At("HEAD")])
   fun jooonOnVelocity(pkt: EntityVelocityUpdateS2CPacket, ci: CallbackInfo) {
      FunnyFishing.onBobberVelocity(pkt.method_11818(), pkt.method_73085().field_1352, pkt.method_73085().field_1351, pkt.method_73085().field_1350)
   }

   @Inject(method = ["method_11136"], at = [@At("TAIL")])
   fun jooonOnBlockUpdate(packet: BlockUpdateS2CPacket, ci: CallbackInfo) {
      val var10000: Mastery = Mastery.INSTANCE
      var var10001: BlockPos = packet.method_11309()
      var var10002: BlockState = packet.method_11308()
      var10000.onBlockUpdate(var10001, var10002)
      val var3: IceFillSolver = IceFillSolver.INSTANCE
      var10001 = packet.method_11309()
      var10002 = packet.method_11308()
      var3.onBlockUpdate(var10001, var10002)
      val var4: AutomaticSwiftness = AutomaticSwiftness.INSTANCE
      var10001 = packet.method_11309()
      var10002 = packet.method_11308()
      var4.onBlockUpdate(var10001, var10002)
   }

   @Inject(method = ["method_11100"], at = [@At("TAIL")])
   fun jooonSwiftnessBatch(pkt: ChunkDeltaUpdateS2CPacket, ci: CallbackInfo) {
      if (MinecraftClient.method_1551().field_1687 != null) {
         pkt.method_30621({ posIn: BlockPos, state: BlockState ->
            Mastery.INSTANCE.onBlockUpdate(posIn, state)
         })
         AutomaticSwiftness.INSTANCE.onSectionBlocksUpdate(pkt)
         IceFillSolver.INSTANCE.onSectionBlocksUpdate(pkt)
      }
   }

   @Inject(method = ["method_11157"], at = [@At("RETURN")])
   fun jooonOnMovePlayer(packet: PlayerPositionLookS2CPacket, ci: CallbackInfo) {
      TeleportMazeSolver.INSTANCE.onPositionPacket(packet)
      StridersurferFishingMacro.INSTANCE.onPositionPacket(packet)
   }

   @Inject(method = ["method_11088"], at = [@At("HEAD")])
   fun jooonOnMapData(packet: MapUpdateS2CPacket, ci: CallbackInfo) {
      DungeonMapFeature.INSTANCE.onMapPacket(packet)
   }

   @Inject(method = ["method_11113"], at = [@At("HEAD")])
   fun jooonOnPlayerInfo(packet: PlayerListS2CPacket, ci: CallbackInfo) {
      DungeonScanner.INSTANCE.onPlayerInfoPacket(packet)
   }

   @Inject(method = ["method_12666"], at = [@At("HEAD")])
   fun jooonOnPong(packet: PingResultS2CPacket, ci: CallbackInfo) {
      PingUtil.INSTANCE.onPongResponse(packet)
   }
}
