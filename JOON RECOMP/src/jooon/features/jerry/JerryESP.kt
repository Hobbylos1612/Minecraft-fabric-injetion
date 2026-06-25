package jooon.features.jerry

import java.awt.Color
import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import jooon.util.RenderUtils
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.class_1297
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

@SourceDebugExtension(["SMAP\nJerryESP.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JerryESP.kt\njooon/features/jerry/JerryESP\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,129:1\n766#2:130\n857#2,2:131\n1045#2:133\n*S KotlinDebug\n*F\n+ 1 JerryESP.kt\njooon/features/jerry/JerryESP\n*L\n102#1:130\n102#1:131,2\n103#1:133\n*E\n"])
public object JerryESP {
   private final val tracked: ConcurrentHashMap<class_1297, jooon.features.jerry.JerryESP.JerryESPData> = ConcurrentHashMap()
   private const val MAX_RENDER_DISTANCE: Double = 64.0
   private const val CULL_DISTANCE: Double = 72.0

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
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
      var var10000: Color = player.method_73189()
      val playerPos: Vec3d = var10000
      val now: Long = System.currentTimeMillis()
      tracked.entrySet().removeIf({ p0: Any ->
         `$tmp0`(p0)
      })
      val r: Double = 64.0
      val var18: ClientWorld = world as? ClientWorld
      if ((world as? ClientWorld) != null) {
         val var19: java.lang.Iterable = var18.method_18112()

         for (var10000 in CollectionsKt.toList(var19)) {
            val e: Entity = var10000 as Entity
            if (var10000 as Entity is ArmorStandEntity && !((var10000 as Entity).method_73189().method_1022(playerPos) > r)) {
               val var21: java.lang.String = (e as ArmorStandEntity).method_5477().getString()
               val var22: java.lang.String = var21.toLowerCase(Locale.ROOT)
               val type: java.lang.String = if (StringsKt.contains$default(var22, "golden jerry", false, 2, null))
                  "Golden"
                  else
                  (
                     if (StringsKt.contains$default(var22, "purple jerry", false, 2, null))
                        "Purple"
                        else
                        (
                           if (StringsKt.contains$default(var22, "blue jerry", false, 2, null))
                              "Blue"
                              else
                              (if (StringsKt.contains$default(var22, "green jerry", false, 2, null)) "Green" else null)
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
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val var33: MatrixStack = ctx.matrices()
         if (var33 != null) {
            val var34: Camera = ctx.gameRenderer().method_19418()
            val var35: Vec3d = var10000.method_73189()
            val playerPos: Vec3d = var35
            val var36: java.util.Collection = tracked.values()
            val cameraPos: java.lang.Iterable = var36
            val consumers: java.util.Collection = ArrayList()

            for (e in cameraPos) {
               if ((e as JerryESP.JerryESPData).getEntity().method_5805()) {
                  consumers.add(e)
               }
            }

            val sorted: java.util.List = CollectionsKt.sortedWith(consumers as java.util.List, JerryESP$renderESP$$inlined$sortedBy$1(var35))
            val var37: Entry = var33.method_23760()
            val var24: Entry = var37
            val var38: Matrix4f = var37.method_23761()
            val var26: Matrix4f = var38
            val var39: Vec3d = var34.method_71156()
            val var27: Vec3d = var39
            val var40: VertexConsumerProvider = ctx.consumers()
            if (var40 != null) {
               val var28: VertexConsumerProvider = var40

               for (var41 in sorted) {
                  val var30: JerryESP.JerryESPData = var41 as JerryESP.JerryESPData
                  val var31: Entity = (var41 as JerryESP.JerryESPData).getEntity()
                  if (!(var31.method_73189().method_1022(playerPos) > 64.0)) {
                     val base: Color = var30.color
                     val r: Float = base.getRed() / 255.0F
                     val g: Float = base.getGreen() / 255.0F
                     val b: Float = base.getBlue() / 255.0F
                     val renderBox: Box = RenderUtils.INSTANCE.getInterpolatedBox(var31, 1.0F)
                     val var42: VertexConsumer = var28.method_73477(RenderLayers.method_76000(RenderUtils.INSTANCE.getWHITE_TEX()))
                     RenderUtils.INSTANCE.renderEdgeBoxes(var42, var26, var24, var27, renderBox, r, g, b, 0.5F, 0.035)
                  }
               }
            }
         }
      }
   }

   public data class JerryESPData {
      private Entity entity;
      public final var lastSeen: Long
      public final val color: Color
      public final val jerryType: String

      fun JerryESPData(entity: Entity, lastSeen: Long, color: Color, jerryType: java.lang.String) {
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

      fun copy(entity: Entity, lastSeen: Long, color: Color, jerryType: java.lang.String): JerryESP.JerryESPData {
         JerryESP.JerryESPData(entity, lastSeen, color, jerryType)
      }

      public override fun toString(): String {
         return "JerryESPData(entity=${this.entity}, lastSeen=${this.lastSeen}, color=${this.color}, jerryType=${this.jerryType})"
      }

      public override fun hashCode(): Int {
         return ((this.entity.hashCode() * 31 + java.lang.Long.hashCode(this.lastSeen)) * 31 + this.color.hashCode()) * 31 + this.jerryType.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
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
