package jooon.features.slayers

import java.awt.Color
import java.util.ArrayList
import java.util.Locale
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import jooon.config.Config
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.entity.Entity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Matrix4f

object SlayerESP {
   private val tracked: ConcurrentHashMap<Entity, jooon.features.slayers.SlayerESP.ESPData> = ConcurrentHashMap()
   private const val MAX_RENDER_DISTANCE: Double = 64.0
   private const val CULL_DISTANCE: Double = 72.0

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
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
         if (Config.slayerESPEnabled && Utils.inSkyblock) {
            renderESP(ctx)
         }
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         tracked.clear()
      })
   }

   fun updateEntityCache(world: World, player: PlayerEntity) {
      var var10000: Entity = player.getEntityPos()




      for (var10000 in tracked.entrySet()) {

         var10000 = (Entity)var10000.getKey()

         var10000 = (SlayerESP.ESPData)e.getValue()
         if (var10000.getEntity().getEntityPos().distanceTo(playerPos) > 72.0
            || !var10000.getEntity().isAlive()
            || now - (var10000 as SlayerESP.ESPData).lastSeen > 10000L) {
            toRemove.add(clientWorld)
         }
      }

      val var36: java.util.Iterator = toRemove.iterator()
      val var26: java.util.Iterator = var36

      while (var26.hasNext()) {
         var10000 = (Entity)var26.next()
         tracked.remove(var10000)
      }


      if ((world as? ClientWorld) != null) {
         val var39: java.lang.Iterable = var38.getEntities()
         val var30: java.util.List = toList(var39)

         for (var40 in var30) {

            if (!((var40 as Entity).getEntityPos().distanceTo(playerPos) > var27) && var40 as Entity is ArmorStandEntity) {


               if (contains$default(var42, "revenant horror", false, 2, null)
                  || contains$default(var42, "acolyte", false, 2, null)
                  || contains$default(var42, "deformed", false, 2, null)
                  || contains$default(var42, "tarantula broodfather", false, 2, null)
                  || contains$default(var42, "tarantula vermin", false, 2, null)
                  || contains$default(var42, "tarantula beast", false, 2, null)
                  || contains$default(var42, "sven packmaster", false, 2, null)
                  || contains$default(var42, "pack enforcer", false, 2, null)
                  || contains$default(var42, "sven follower", false, 2, null)
                  || contains$default(var42, "voidgloom seraph", false, 2, null)
                  || contains$default(var42, "voidling", false, 2, null)
                  || contains$default(var42, "riftstalker bloodfiend", false, 2, null)
                  || contains$default(var42, "inferno demonlord", false, 2, null)) {
                  var closestMob: Color
                  try {
                     closestMob = Color.decode(Config.slayerESPColor)
                  } catch (ex: Exception) {
                     closestMob = Color(50, 205, 50)
                  }

                  var var32: LivingEntity = null
                  var minDist: Double = 9.0

                  for (var43 in var30) {

                     if (var43 as Entity is LivingEntity
                        && !(var43 as Entity == ex)
                        && !(var43 as Entity == player)
                        && (var43 as Entity) !is ArmorStandEntity
                        && ((var43 as Entity) as LivingEntity).isAlive()
                        && !(var43 as Entity).isSpectator()) {

                        if (d < minDist) {
                           minDist = d
                           var32 = mob as LivingEntity
                        }
                     }
                  }

                  if (var32 != null) {
                     val var44: java.util.Map = tracked
                     var44.put(var32, SlayerESP.ESPData(var32, now, closestMob, 10, 0, 16, null))
                  }
               }
            }
         }
      }
   }

   private fun renderESP(ctx: WorldRenderContext) {

      if (var10000 != null) {


         if (var49 != null) {




            val var52: java.util.Collection = tracked.values()
            val cameraPos: java.lang.Iterable = var52
            val userOpacity: java.util.Collection = ArrayList()

            for (outlines in cameraPos) {
               if ((outlines as SlayerESP.ESPData).getEntity().isAlive()) {
                  userOpacity.add(outlines)
               }
            }

            val sorted: java.util.List = sortedWith(
               userOpacity as java.util.List, SlayerESP$renderESP$$inlined$thenBy$1(SlayerESP$renderESP$$inlined$compareBy$1(), var51)
            )
            val var54: net.minecraft.client.util.math.MatrixStack.Entry = var49.peek()
            val var33: net.minecraft.client.util.math.MatrixStack.Entry = var54






            var var38: Boolean
            try {
               var38 = Config.slayerESPThroughWalls
            } catch (var31: java.lang.Throwable) {
               var38 = false
            }


            if (var57 != null) {





               for (var60 in sorted) {
                  val var42: SlayerESP.ESPData = var60 as SlayerESP.ESPData


                  if (!(dist > 64.0)) {





                     if (var37 && var61.isOccluded(var10001, renderBox, var10003 as World)) {




                        var40.setColor((var43.toFloat() * var46).toInt() shl 16 or (var44.toFloat() * var46).toInt() shl 8 or (var45.toFloat() * var46).toInt())

                        RenderUtils.renderFilledAabb(var63, var34, var33, var35, renderBox)
                     } else {




                        RenderUtils.renderEdgeBoxes(var62, var34, var33, var35, renderBox, r, g, b, var36, 0.035)
                     }
                  }
               }

               var40.draw()
            }
         }
      }
   }

   data class ESPData {
      private LivingEntity entity;
      var lastSeen: Long
      val color: Color
      val priority: Int
      val alpha: Int

      fun ESPData(entity: LivingEntity, lastSeen: Long, color: Color, priority: Int, alpha: Int) {
         this.entity = entity
         this.lastSeen = lastSeen
         this.color = color
         this.priority = priority
         this.alpha = alpha
      }

      fun getEntity(): LivingEntity {
         this.entity
      }

      fun component1(): LivingEntity {
         this.entity
      }

      public operator fun component2(): Long {
         return this.lastSeen
      }

      public operator fun component3(): Color {
         return this.color
      }

      public operator fun component4(): Int {
         return this.priority
      }

      public operator fun component5(): Int {
         return this.alpha
      }

      fun copy(entity: LivingEntity, lastSeen: Long, color: Color, priority: Int, alpha: Int): SlayerESP.ESPData {
         SlayerESP.ESPData(entity, lastSeen, color, priority, alpha)
      }

      override fun toString(): String {
         return "ESPData(entity=${this.entity}, lastSeen=${this.lastSeen}, color=${this.color}, priority=${this.priority}, alpha=${this.alpha})"
      }

      override fun hashCode(): Int {
         return (((this.entity.hashCode() * 31 + java.lang.Long.hashCode(this.lastSeen)) * 31 + this.color.hashCode()) * 31 + Integer.hashCode(this.priority))
               * 31
            + Integer.hashCode(this.alpha)
         }

      override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is SlayerESP.ESPData
               && this.entity == (other as SlayerESP.ESPData).entity
               && this.lastSeen == (other as SlayerESP.ESPData).lastSeen
               && this.color == (other as SlayerESP.ESPData).color
               && this.priority == (other as SlayerESP.ESPData).priority
               && this.alpha == (other as SlayerESP.ESPData).alpha
            }
      }
   }
}
