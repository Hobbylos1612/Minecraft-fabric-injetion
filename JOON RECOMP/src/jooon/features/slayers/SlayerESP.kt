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

@SourceDebugExtension(["SMAP\nSlayerESP.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SlayerESP.kt\njooon/features/slayers/SlayerESP\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,192:1\n766#2:193\n857#2,2:194\n*S KotlinDebug\n*F\n+ 1 SlayerESP.kt\njooon/features/slayers/SlayerESP\n*L\n137#1:193\n137#1:194,2\n*E\n"])
public object SlayerESP {
   private final val tracked: ConcurrentHashMap<class_1297, jooon.features.slayers.SlayerESP.ESPData> = ConcurrentHashMap()
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
         WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.slayerESPEnabled && Utils.INSTANCE.inSkyblock) {
            INSTANCE.renderESP(ctx)
         }
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         tracked.clear()
      })
   }

   fun updateEntityCache(world: World, player: PlayerEntity) {
      var var10000: Entity = player.method_73189()
      val playerPos: Vec3d = var10000
      val now: Long = System.currentTimeMillis()
      val toRemove: ArrayList = ArrayList()

      for (var10000 in tracked.entrySet()) {
         val e: Entry = var10000
         var10000 = (Entity)var10000.getKey()
         val clientWorld: Entity = var10000
         var10000 = (SlayerESP.ESPData)e.getValue()
         if (var10000.getEntity().method_73189().method_1022(playerPos) > 72.0
            || !var10000.getEntity().method_5805()
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

      val var27: Double = 64.0
      val var38: ClientWorld = world as? ClientWorld
      if ((world as? ClientWorld) != null) {
         val var39: java.lang.Iterable = var38.method_18112()
         val var30: java.util.List = CollectionsKt.toList(var39)

         for (var40 in var30) {
            val ex: Entity = var40 as Entity
            if (!((var40 as Entity).method_73189().method_1022(playerPos) > var27) && var40 as Entity is ArmorStandEntity) {
               val var41: java.lang.String = (ex as ArmorStandEntity).method_5477().getString()
               val var42: java.lang.String = var41.toLowerCase(Locale.ROOT)
               if (StringsKt.contains$default(var42, "revenant horror", false, 2, null)
                  || StringsKt.contains$default(var42, "acolyte", false, 2, null)
                  || StringsKt.contains$default(var42, "deformed", false, 2, null)
                  || StringsKt.contains$default(var42, "tarantula broodfather", false, 2, null)
                  || StringsKt.contains$default(var42, "tarantula vermin", false, 2, null)
                  || StringsKt.contains$default(var42, "tarantula beast", false, 2, null)
                  || StringsKt.contains$default(var42, "sven packmaster", false, 2, null)
                  || StringsKt.contains$default(var42, "pack enforcer", false, 2, null)
                  || StringsKt.contains$default(var42, "sven follower", false, 2, null)
                  || StringsKt.contains$default(var42, "voidgloom seraph", false, 2, null)
                  || StringsKt.contains$default(var42, "voidling", false, 2, null)
                  || StringsKt.contains$default(var42, "riftstalker bloodfiend", false, 2, null)
                  || StringsKt.contains$default(var42, "inferno demonlord", false, 2, null)) {
                  var closestMob: Color
                  try {
                     closestMob = Color.decode(Config.slayerESPColor)
                  } catch (ex: Exception) {
                     closestMob = Color(50, 205, 50)
                  }

                  var var32: LivingEntity = null
                  var minDist: Double = 9.0

                  for (var43 in var30) {
                     val mob: Entity = var43 as Entity
                     if (var43 as Entity is LivingEntity
                        && !(var43 as Entity == ex)
                        && !(var43 as Entity == player)
                        && (var43 as Entity) !is ArmorStandEntity
                        && ((var43 as Entity) as LivingEntity).method_5805()
                        && !(var43 as Entity).method_7325()) {
                        val d: Double = mob.method_73189().method_1025(ex.method_73189())
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
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         val player: ClientPlayerEntity = var10000
         val var49: MatrixStack = ctx.matrices()
         if (var49 != null) {
            val var50: Camera = ctx.gameRenderer().method_19418()
            val var51: Vec3d = var10000.method_73189()
            val playerPos: Vec3d = var51
            val tickDelta: Float = 1.0F
            val var52: java.util.Collection = tracked.values()
            val cameraPos: java.lang.Iterable = var52
            val userOpacity: java.util.Collection = ArrayList()

            for (outlines in cameraPos) {
               if ((outlines as SlayerESP.ESPData).getEntity().method_5805()) {
                  userOpacity.add(outlines)
               }
            }

            val sorted: java.util.List = CollectionsKt.sortedWith(
               userOpacity as java.util.List, SlayerESP$renderESP$$inlined$thenBy$1(SlayerESP$renderESP$$inlined$compareBy$1(), var51)
            )
            val var54: net.minecraft.client.util.math.MatrixStack.Entry = var49.method_23760()
            val var33: net.minecraft.client.util.math.MatrixStack.Entry = var54
            val var55: Matrix4f = var54.method_23761()
            val var34: Matrix4f = var55
            val var56: Vec3d = var50.method_71156()
            val var35: Vec3d = var56
            val var36: Float = RangesKt.coerceIn(Config.slayerESPOpacityPct, 1, 100) / 100.0F

            var var38: Boolean
            try {
               var38 = Config.slayerESPThroughWalls
            } catch (var31: java.lang.Throwable) {
               var38 = false
            }

            val var37: Boolean = var38
            val var57: VertexConsumerProvider = ctx.consumers()
            if (var57 != null) {
               val var39: VertexConsumerProvider = var57
               val var58: MinecraftClient = this.getMc()
               val var59: OutlineVertexConsumerProvider = (var58 as MinecraftAccessor).getRenderBuffers().method_23003()
               val var40: OutlineVertexConsumerProvider = var59

               for (var60 in sorted) {
                  val var42: SlayerESP.ESPData = var60 as SlayerESP.ESPData
                  val e: LivingEntity = (var60 as SlayerESP.ESPData).getEntity()
                  val dist: Double = e.method_73189().method_1022(playerPos)
                  if (!(dist > 64.0)) {
                     val renderBox: Box = RenderUtils.INSTANCE.getInterpolatedBox(e as Entity, tickDelta)
                     val base: Color = RenderUtils.INSTANCE.applyAlpha(var42.color, RenderUtils.INSTANCE.calculateSmoothAlpha(dist, 54.0, 64.0))
                     val var61: RenderUtils = RenderUtils.INSTANCE
                     val var10001: PlayerEntity = player as PlayerEntity
                     val var10003: ClientWorld = this.getMc().field_1687
                     if (var37 && var61.isOccluded(var10001, renderBox, var10003 as World)) {
                        val var43: Int = base.getRed()
                        val var44: Int = base.getGreen()
                        val var45: Int = base.getBlue()
                        val var46: Float = RangesKt.coerceIn(var36, 0.2F, 1.0F)
                        var40.method_23286((int)((float)var43 * var46) shl 16 or (int)((float)var44 * var46) shl 8 or (int)((float)var45 * var46))
                        val var63: VertexConsumer = var40.method_73477(RenderLayers.method_76018(RenderUtils.INSTANCE.getWHITE_TEX()))
                        RenderUtils.INSTANCE.renderFilledAabb(var63, var34, var33, var35, renderBox)
                     } else {
                        val r: Float = base.getRed() / 255.0F
                        val g: Float = base.getGreen() / 255.0F
                        val b: Float = base.getBlue() / 255.0F
                        val var62: VertexConsumer = var39.method_73477(RenderLayers.method_76000(RenderUtils.INSTANCE.getWHITE_TEX()))
                        RenderUtils.INSTANCE.renderEdgeBoxes(var62, var34, var33, var35, renderBox, r, g, b, var36, 0.035)
                     }
                  }
               }

               var40.method_23285()
            }
         }
      }
   }

   public data class ESPData {
      private LivingEntity entity;
      public final var lastSeen: Long
      public final val color: Color
      public final val priority: Int
      public final val alpha: Int

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

      public override fun toString(): String {
         return "ESPData(entity=${this.entity}, lastSeen=${this.lastSeen}, color=${this.color}, priority=${this.priority}, alpha=${this.alpha})"
      }

      public override fun hashCode(): Int {
         return (((this.entity.hashCode() * 31 + java.lang.Long.hashCode(this.lastSeen)) * 31 + this.color.hashCode()) * 31 + Integer.hashCode(this.priority))
               * 31
            + Integer.hashCode(this.alpha)
         }

      public override operator fun equals(other: Any?): Boolean {
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
