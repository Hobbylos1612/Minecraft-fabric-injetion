package jooon.features.jerry

import java.awt.Color
import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import jooon.util.RenderUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.entity.Entity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Matrix4f

object JerryESP {
   private val tracked: ConcurrentHashMap<Entity, jooon.features.jerry.JerryESP.JerryESPData> = ConcurrentHashMap()
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
         WorldRenderEvents.END_MAIN
         .register(
            { ctx: WorldRenderContext ->
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
         ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         tracked.clear()
      })
   }

   fun updateEntityCache(world: World, player: PlayerEntity) {
      var var10000: Color = player.getEntityPos()


      tracked.entrySet().removeIf({ p0: Any ->
         ``(p0)
      })


      if ((world as? ClientWorld) != null) {
         val var19: java.lang.Iterable = var18.getEntities()

         for (var10000 in toList(var19)) {

            if (var10000 as Entity is ArmorStandEntity && !((var10000 as Entity).getEntityPos().distanceTo(playerPos) > r)) {



                  "Golden"
return else
                  (
                     if (contains$default(var22, "purple jerry", false, 2, null))
                        "Purple"
return else
                        (
                           if (contains$default(var22, "blue jerry", false, 2, null))
                              "Blue"
return else
                              (if (contains$default(var22, "green jerry", false, 2, null)) "Green" else null)
                        )
                  )
                  if (type != null) {
                  run label78@{
                     when (type.hashCode()) {
                        -1893076004 -> {
                           if (type.equals("Purple")) {
                              var10000 = Color.MAGENTA
                              return@label78
                           }
                        }
                        2073722 -> {
                           if (type.equals("Blue")) {
                              var10000 = Color.BLUE
                              return@label78
                           }
                        }
                        69066467 -> {
                           if (type.equals("Green")) {
                              var10000 = Color.GREEN
                              return@label78
                           }
                        }
                        2138497321 -> {
                           if (type.equals("Golden")) {
                              var10000 = Color.YELLOW
                              return@label78
                           }
                        }
                        else -> {}
                     }

                     var10000 = Color.WHITE
                  }

                  val var24: java.util.Map = tracked
                  var24.put(e, JerryESP.JerryESPData(e, now, var10000, type))
               }
            }
         }
      }
   }

   private fun renderESP(ctx: WorldRenderContext) {

      if (var10000 != null) {

         if (var33 != null) {



            val var36: java.util.Collection = tracked.values()
            val cameraPos: java.lang.Iterable = var36
            val consumers: java.util.Collection = ArrayList()

            for (e in cameraPos) {
               if ((e as JerryESP.JerryESPData).getEntity().isAlive()) {
                  consumers.add(e)
               }
            }

            val sorted: java.util.List = sortedWith(consumers as java.util.List, JerryESP$renderESP$$inlined$sortedBy$1(var35))







            if (var40 != null) {


               for (var41 in sorted) {
                  val var30: JerryESP.JerryESPData = var41 as JerryESP.JerryESPData

                  if (!(var31.getEntityPos().distanceTo(playerPos) > 64.0)) {






                     RenderUtils.renderEdgeBoxes(var42, var26, var24, var27, renderBox, r, g, b, 0.5F, 0.035)
                  }
               }
            }
         }
      }
   }

   data class JerryESPData {
      private Entity entity;
      var lastSeen: Long
      val color: Color
      val jerryType: String

      fun JerryESPData(entity: Entity, lastSeen: Long, color: Color, jerryType: String) {
         this.entity = entity
         this.lastSeen = lastSeen
         this.color = color
         this.jerryType = jerryType
      }

      fun getEntity(): Entity {
         this.entity
      }

      fun component1(): Entity {
         this.entity
      }

      public operator fun component2(): Long {
         return this.lastSeen
      }

      public operator fun component3(): Color {
         return this.color
      }

      public operator fun component4(): String {
         return this.jerryType
      }

      fun copy(entity: Entity, lastSeen: Long, color: Color, jerryType: String): JerryESP.JerryESPData {
         JerryESP.JerryESPData(entity, lastSeen, color, jerryType)
      }

      override fun toString(): String {
         return "JerryESPData(entity=${this.entity}, lastSeen=${this.lastSeen}, color=${this.color}, jerryType=${this.jerryType})"
      }

      override fun hashCode(): Int {
         return ((this.entity.hashCode() * 31 + java.lang.Long.hashCode(this.lastSeen)) * 31 + this.color.hashCode()) * 31 + this.jerryType.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is JerryESP.JerryESPData
               && this.entity == (other as JerryESP.JerryESPData).entity
               && this.lastSeen == (other as JerryESP.JerryESPData).lastSeen
               && this.color == (other as JerryESP.JerryESPData).color
               && this.jerryType == (other as JerryESP.JerryESPData).jerryType
            }
      }
   }
}
