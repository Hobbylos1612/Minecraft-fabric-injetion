package jooon.features.dungeons.map.api

import java.util.UUID
import jooon.features.dungeons.map.util.MathUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper

public data class DungeonPlayer(uuid: UUID, name: String) {
   public final val uuid: UUID
   public final var name: String
   public final var role: DungeonClass
   public final var classLevel: Int
   public final var isDead: Boolean
   public final var position: PlayerComponentPosition?
   public final var updateTime: Double?
   public final var lastPosition: PlayerComponentPosition?
   public final var lastUpdateTime: Double?

   init {
      this.uuid = uuid
      this.name = name
      this.role = DungeonClass.Unknown
   }

   public fun tick() {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 != null) {
         val localPlayer: ClientPlayerEntity = var10000.field_1724
         val isSelf: Boolean = this.name == var10000.field_1724.method_7334().name()
         val var8: AbstractClientPlayerEntity
         if (isSelf) {
            var8 = localPlayer as AbstractClientPlayerEntity
         } else {
            val rot: PlayerEntity = if (var10000.field_1687 != null) var10000.field_1687.method_18470(this.uuid) else null
            var8 = rot as? AbstractClientPlayerEntity
         }

         if (var8 != null && !var8.method_29504() && !var8.method_31481()) {
            this.updatePosition(
               PlayerComponentPosition.Companion
                  .fromWorld(var8.method_23317(), var8.method_23321(), if (isSelf) (double)var8.method_36454() - 180.0 else (double)var8.method_36454())
            )
         }
      }
   }

   public fun updatePosition(pos: PlayerComponentPosition) {
      val time: Double = System.currentTimeMillis()
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

   public fun getLerpedPosition(tickDelta: Float): PlayerComponentPosition? {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.field_1724 == null) {
         return this.position
      } else {
         val localPlayer: ClientPlayerEntity = var10000.field_1724
         if (this.name == var10000.field_1724.method_7334().name()) {
            return PlayerComponentPosition.Companion
               .fromWorld(
                  MathHelper.method_16436((double)tickDelta, localPlayer.field_6014, localPlayer.method_23317()),
                  MathHelper.method_16436((double)tickDelta, localPlayer.field_5969, localPlayer.method_23321()),
                  (double)localPlayer.method_5705(tickDelta) - 180.0
               )
            } else {
            val pos: PlayerEntity = if (var10000.field_1687 != null) var10000.field_1687.method_18470(this.uuid) else null
            val entity: AbstractClientPlayerEntity = pos as? AbstractClientPlayerEntity
            if ((pos as? AbstractClientPlayerEntity) != null
               && !(pos as? AbstractClientPlayerEntity).method_29504()
               && !(pos as? AbstractClientPlayerEntity).method_31481()) {
               return PlayerComponentPosition.Companion
                  .fromWorld(
                     MathHelper.method_16436((double)tickDelta, entity.field_6014, entity.method_23317()),
                     MathHelper.method_16436((double)tickDelta, entity.field_5969, entity.method_23321()),
                     (double)entity.method_5705(tickDelta)
                  )
               } else if (this.position == null) {
               return null
            } else {
               val var19: PlayerComponentPosition = this.position
               if (this.lastPosition == null) {
                  return this.position
               } else {
                  val lastPos: PlayerComponentPosition = this.lastPosition
                  if (this.updateTime != null) {
                     val uTime: Double = this.updateTime
                     if (this.lastUpdateTime != null) {
                        val luTime: Double = this.lastUpdateTime
                        val time: Double = System.currentTimeMillis()
                        if (uTime <= luTime) {
                           return var19
                        } else {
                           val pct: Double = (RangesKt.coerceIn((time - uTime) / (uTime - luTime), RangesKt.rangeTo(0.0, 1.0)) as java.lang.Number)
                              .doubleValue()
                              return PlayerComponentPosition(
                              MathUtils.INSTANCE.lerp(pct, lastPos.x, var19.x),
                              MathUtils.INSTANCE.lerp(pct, lastPos.z, var19.z),
                              MathUtils.INSTANCE.lerpAngle(pct, lastPos.r, var19.r)
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

   public fun copy(uuid: UUID = this.uuid, name: String = this.name): DungeonPlayer {
      return DungeonPlayer(uuid, name)
   }

   public override fun toString(): String {
      return "DungeonPlayer(uuid=${this.uuid}, name=${this.name})"
   }

   public override fun hashCode(): Int {
      return this.uuid.hashCode() * 31 + this.name.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is DungeonPlayer && this.uuid == (other as DungeonPlayer).uuid && this.name == (other as DungeonPlayer).name
      }
   }
}
