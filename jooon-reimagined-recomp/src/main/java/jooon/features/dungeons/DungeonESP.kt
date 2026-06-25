package jooon.features.dungeons

import java.awt.Color
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.UUID
import jooon.config.Config
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.enums.EnumEntries
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.entity.Entity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Matrix4f

object DungeonESP {
   private val tracked: MutableMap<Entity, jooon.features.dungeons.DungeonESP.ESPData> = LinkedHashMap() as java.util.Map
   private val standToMob: MutableMap<UUID, UUID> = LinkedHashMap() as java.util.Map
   private var tickCounter: Int
   private const val SCAN_INTERVAL: Int = 10
   private const val MAX_RENDER_DISTANCE: Double = 60.0
   private const val FADE_START_DISTANCE: Double = 45.0
   private const val CULL_DISTANCE: Double = 80.0
   private val HEART_RX: Regex = Regex("[❤♥]")
   private val COLOR_REGEX: Regex = Regex("§.")

   fun getMc(): MinecraftClient {
return var10000
   }

   private fun stripColors(s: String): String {
      return COLOR_REGEX.replace(s, "")
   }

   fun onInitializeClient() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         reset()
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         reset()
      })
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { client: MinecraftClient ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.dungeonESPEnabled && Utils.inDungeon) {
            renderESP(ctx)
         }
      })
   }

   private fun reset() {
      tracked.clear()
      standToMob.clear()
   }

   fun handleOtherPlayer(e: OtherClientPlayerEntity) {


      if (n.contains("Shadow Assassin", true)) {
         tracked.put(
            e,
            DungeonESP.ESPData(
               e as Entity,
               DungeonESP.ESPType.SHADOW_ASSASSIN,
               RenderUtils.parseColor(Config.dungeonESPSAColor),
               DungeonESP.ESPType.SHADOW_ASSASSIN.priority,
               0L,
               16,
return null
            )
         )
      } else if (n.contains("Diamond Guy", true) || n.contains("Lost Adventurer", true)) {
         tracked.put(
            e,
            DungeonESP.ESPData(
               e as Entity,
               DungeonESP.ESPType.DIAMOND_GUY,
               RenderUtils.parseColor(Config.dungeonESPStarColor),
               DungeonESP.ESPType.DIAMOND_GUY.priority,
               0L,
               16,
return null
            )
         )
      }
   }

   fun handleBat(e: BatEntity) {
      if (listOf(arrayOf(100.0F, 200.0F, 400.0F, 800.0F)).contains(e.getMaxHealth())) {
         tracked.put(
            e,
            DungeonESP.ESPData(
               e as Entity, DungeonESP.ESPType.BAT, RenderUtils.parseColor(Config.dungeonESPBatColor), DungeonESP.ESPType.BAT.priority, 0L, 16, null
            )
         )
      }
   }

   fun getMobEntity(stand: ArmorStandEntity): Entity {
      var var10000: UUID = stand.getUuid()


      if ((if (box is ClientWorld) box else null) == null) {
return null
      } else {

         if (lockedMobUuid != null) {
            val var41: java.lang.Iterable = var40.getEntities()
            val closest: java.util.Iterator = var41.iterator()

            while (true) {
               if (!closest.hasNext()) {
                  var10000 = null
break
               }

               if ((dist as Entity).getUuid() == lockedMobUuid) {
                  var10000 = (UUID)dist
break
               }
            }

            if (var10000 as Entity != null
               && (var10000 as Entity).isAlive()
               && (var10000 as Entity).getEntityPos().distanceTo(stand.getEntityPos()) <= 4.5) {
return var20
            }

         }


         val var45: java.lang.Iterable = var40.getEntities()
         val var28: java.lang.Iterable = toList(var45)
         val var30: java.util.Collection = ArrayList()

         for (`minValue$iv` in var28) {
            val `e$iv`: Entity = `minValue$iv` as Entity
            if ((`minValue$iv` as Entity).isAlive()
               && `minValue$iv` as Entity is LivingEntity
               && (`minValue$iv` as Entity) !is ArmorStandEntity
               && !(`minValue$iv` as Entity == getMc().player)
               && ((`minValue$iv` as Entity) !is WitherEntity || !((`minValue$iv` as Entity) as WitherEntity).isInvisible())
               && `minValue$iv` as Entity != stand
               && var21.intersects(((`minValue$iv` as Entity) as LivingEntity).getBoundingBox())) {
               var30.add(`minValue$iv`)
            }
         }

         val var32: java.util.Iterator = (var30 as java.util.List).iterator()
         if (!var32.hasNext()) {
            var10000 = null
         } else {
            var var33: Any = var32.next()
            if (!var32.hasNext()) {
               var10000 = (UUID)var33
            } else {
               var var35: Double = (var33 as Entity).getEntityPos().squaredDistanceTo(stand.getEntityPos())

               do {


                  if (java.lang.Double.compare(var35, var39) > 0) {
                     var33 = var37
                     var35 = var39
                  }
               } while (var32.hasNext())

               var10000 = (UUID)var33
            }
         }

         var10000 = var10000 as Entity
         if (var10000 as Entity == null) {
return null
         } else {
            if (var10000.getEntityPos().distanceTo(stand.getEntityPos()) <= 4.5) {
               standToMob.put(var10000, var10000.getUuid())
               var10000 = var10000
            } else {
               var10000 = null
            }
return var10000
         }
      }
   }

   private fun renderESP(ctx: WorldRenderContext) {
      try {

         if (var10000 == null) {
return return
         }


         if (var50 == null) {
return return
         }


         if (var51 == null) {
return return
         }





         val camPos: java.lang.Iterable = tracked.values()
         val userOpacity: java.util.Collection = ArrayList()

         for (outlines in camPos) {
            if ((outlines as DungeonESP.ESPData).getEntity().isAlive()) {
               userOpacity.add(outlines)
            }
         }

         val sorted: java.util.List = sortedWith(
            userOpacity as java.util.List, DungeonESP$renderESP$$inlined$thenBy$1(DungeonESP$renderESP$$inlined$compareBy$1(), var52)
         )









         if (var57 == null) {
return return
         }





         for (var43 in sorted) {


            if (!(dist > maxRenderDist)) {


               if (var39 && RenderUtils.isOccluded(player as PlayerEntity, renderBox, world as World)) {




                  var41.setColor((var44.toFloat() * var47).toInt() shl 16 or (var45.toFloat() * var47).toInt() shl 8 or (var46.toFloat() * var47).toInt())

                  RenderUtils.renderFilledAabb(var61, var36, var35, var37, renderBox)
               } else {




                  RenderUtils.renderFilledAabb(var60, var36, var35, var37, renderBox, r, g, b, var38 * 0.4F)
                  RenderUtils.renderEdgeBoxes(var60, var36, var35, var37, renderBox, r, g, b, 1.0F, 0.02)
               }
            }
         }

         var41.draw()
      } catch (var33: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var33.printStackTrace()
         }
      }
   }

   fun onWorldChange() {
      tracked.clear()
   }

   private data class ESPData {
      private Entity entity;
      val type: jooon.features.dungeons.DungeonESP.ESPType
      val outlineColor: Color
      val priority: Int
      var lastSeen: Long

      fun ESPData(entity: Entity, type: DungeonESP.ESPType, outlineColor: Color, priority: Int, lastSeen: Long) {
         this.entity = entity
         this.type = type
         this.outlineColor = outlineColor
         this.priority = priority
         this.lastSeen = lastSeen
      }

      fun getEntity(): Entity {
         this.entity
      }

      fun component1(): Entity {
         this.entity
      }

      public operator fun component2(): jooon.features.dungeons.DungeonESP.ESPType {
         return this.type
      }

      public operator fun component3(): Color {
         return this.outlineColor
      }

      public operator fun component4(): Int {
         return this.priority
      }

      public operator fun component5(): Long {
         return this.lastSeen
      }

      fun copy(entity: Entity, type: DungeonESP.ESPType, outlineColor: Color, priority: Int, lastSeen: Long): DungeonESP.ESPData {
         DungeonESP.ESPData(entity, type, outlineColor, priority, lastSeen)
      }

      override fun toString(): String {
         return "ESPData(entity=${this.entity}, type=${this.type}, outlineColor=${this.outlineColor}, priority=${this.priority}, lastSeen=${this.lastSeen})"
      }

      override fun hashCode(): Int {
         return (((this.entity.hashCode() * 31 + this.type.hashCode()) * 31 + this.outlineColor.hashCode()) * 31 + Integer.hashCode(this.priority)) * 31
            + java.lang.Long.hashCode(this.lastSeen)
         }

      override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is DungeonESP.ESPData
               && this.entity == (other as DungeonESP.ESPData).entity
               && this.type === (other as DungeonESP.ESPData).type
               && this.outlineColor == (other as DungeonESP.ESPData).outlineColor
               && this.priority == (other as DungeonESP.ESPData).priority
               && this.lastSeen == (other as DungeonESP.ESPData).lastSeen
            }
      }
   }

   private enum class ESPType(priority: Int) {
      STARRED_MOB(5),
      SHADOW_ASSASSIN(4),
      DIAMOND_GUY(3),
      LOST_ADVENTURER(3),
      BAT(1);

      val priority: Int

      init {
         this.priority = priority
      }

      
      fun getEntries(): EnumEntries<DungeonESP.ESPType> {
         $ENTRIES
      }
   }
}
