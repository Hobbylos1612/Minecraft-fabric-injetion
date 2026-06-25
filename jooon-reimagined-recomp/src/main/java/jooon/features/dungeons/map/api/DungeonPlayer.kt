package jooon.features.dungeons.map.api

import java.util.UUID
import jooon.features.dungeons.map.util.MathUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper

data class DungeonPlayer(uuid: UUID, name: String) {
   val uuid: UUID
   var name: String
   var role: DungeonClass
   var classLevel: Int
   var isDead: Boolean
   var position: PlayerComponentPosition?
   var updateTime: Double?
   var lastPosition: PlayerComponentPosition?
   var lastUpdateTime: Double?

   init {
      this.uuid = uuid
      this.name = name
      this.role = DungeonClass.Unknown
   }

   fun tick() {

      if (var10000.player != null) {


         val var8: AbstractClientPlayerEntity
         if (isSelf) {
            var8 = localPlayer as AbstractClientPlayerEntity
         } else {

            var8 = rot as? AbstractClientPlayerEntity
         }

         if (var8 != null && !var8.isDead() && !var8.isRemoved()) {
            this.updatePosition(
               PlayerComponentPosition.Companion
                  .fromWorld(var8.getX(), var8.getZ(), if (isSelf) var8.getYaw().toDouble() - 180.0 else var8.getYaw().toDouble())
            )
         }
      }
   }

   fun updatePosition(pos: PlayerComponentPosition) {

      if (this.position == null) {
         this.position = pos
         this.updateTime = time
      } else {
         this.lastPosition = this.position
         this.lastUpdateTime = this.updateTime
         this.position = pos
         this.updateTime = time
      }
   }

   fun getLerpedPosition(tickDelta: Float): PlayerComponentPosition? {

      if (var10000.player == null) {
         return this.position
      } else {

         if (this.name == var10000.player.getGameProfile().name()) {
            return PlayerComponentPosition.Companion
               .fromWorld(
                  MathHelper.lerp(tickDelta.toDouble(), localPlayer.lastX, localPlayer.getX()),
                  MathHelper.lerp(tickDelta.toDouble(), localPlayer.lastZ, localPlayer.getZ()),
                  localPlayer.getYaw(tickDelta).toDouble() - 180.0
               )
            } else {


            if ((pos as? AbstractClientPlayerEntity) != null
               && !(pos as? AbstractClientPlayerEntity).isDead()
               && !(pos as? AbstractClientPlayerEntity).isRemoved()) {
               return PlayerComponentPosition.Companion
                  .fromWorld(
                     MathHelper.lerp(tickDelta.toDouble(), entity.lastX, entity.getX()),
                     MathHelper.lerp(tickDelta.toDouble(), entity.lastZ, entity.getZ()),
                     entity.getYaw(tickDelta).toDouble()
                  )
               } else if (this.position == null) {
               return null
            } else {

               if (this.lastPosition == null) {
                  return this.position
               } else {

                  if (this.updateTime != null) {

                     if (this.lastUpdateTime != null) {


                        if (uTime <= luTime) {
                           return var19
                        } else {

                              .doubleValue()
                              return PlayerComponentPosition(
                              MathUtils.lerp(pct, lastPos.x, var19.x),
                              MathUtils.lerp(pct, lastPos.z, var19.z),
                              MathUtils.lerpAngle(pct, lastPos.r, var19.r)
                           )
                        }
                     } else {
                        return var19
                     }
                  } else {
                     return this.position
                  }
               }
            }
         }
      }
   }

   public operator fun component1(): UUID {
      return this.uuid
   }

   public operator fun component2(): String {
      return this.name
   }

   fun copy(uuid: UUID = this.uuid, name: String = this.name): DungeonPlayer {
      return DungeonPlayer(uuid, name)
   }

   override fun toString(): String {
      return "DungeonPlayer(uuid=${this.uuid}, name=${this.name})"
   }

   override fun hashCode(): Int {
      return this.uuid.hashCode() * 31 + this.name.hashCode()
   }

   override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is DungeonPlayer && this.uuid == (other as DungeonPlayer).uuid && this.name == (other as DungeonPlayer).name
      }
   }
}
