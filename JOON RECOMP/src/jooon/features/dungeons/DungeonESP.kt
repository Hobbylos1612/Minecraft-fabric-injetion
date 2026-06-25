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
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.class_1297
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

@SourceDebugExtension(["SMAP\nDungeonESP.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonESP.kt\njooon/features/dungeons/DungeonESP\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,256:1\n1#2:257\n288#3,2:258\n766#3:260\n857#3,2:261\n2333#3,14:263\n766#3:277\n857#3,2:278\n*S KotlinDebug\n*F\n+ 1 DungeonESP.kt\njooon/features/dungeons/DungeonESP\n*L\n164#1:258,2\n176#1:260\n176#1:261,2\n186#1:263,14\n207#1:277\n207#1:278,2\n*E\n"])
public object DungeonESP {
   private final val tracked: MutableMap<class_1297, jooon.features.dungeons.DungeonESP.ESPData> = LinkedHashMap() as java.util.Map
   private final val standToMob: MutableMap<UUID, UUID> = LinkedHashMap() as java.util.Map
   private final var tickCounter: Int
   private const val SCAN_INTERVAL: Int = 10
   private const val MAX_RENDER_DISTANCE: Double = 60.0
   private const val FADE_START_DISTANCE: Double = 45.0
   private const val CULL_DISTANCE: Double = 80.0
   private final val HEART_RX: Regex = Regex("[❤♥]")
   private final val COLOR_REGEX: Regex = Regex("§.")

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   private fun stripColors(s: String): String {
      return COLOR_REGEX.replace(s, "")
   }

   public fun onInitializeClient() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         INSTANCE.reset()
      })
      ClientPlayConnectionEvents.DISCONNECT.register({ var0: ClientPlayNetworkHandler, var1: MinecraftClient ->
         INSTANCE.reset()
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
         if (Config.dungeonESPEnabled && Utils.INSTANCE.inDungeon) {
            INSTANCE.renderESP(ctx)
         }
      })
   }

   private fun reset() {
      tracked.clear()
      standToMob.clear()
   }

   fun handleOtherPlayer(e: OtherClientPlayerEntity) {
      val var10001: java.lang.String = e.method_5477().getString()
      val n: java.lang.String = this.stripColors(var10001)
      if (StringsKt.contains(n, "Shadow Assassin", true)) {
         tracked.put(
            e,
            DungeonESP.ESPData(
               e as Entity,
               DungeonESP.ESPType.SHADOW_ASSASSIN,
               RenderUtils.INSTANCE.parseColor(Config.dungeonESPSAColor),
               DungeonESP.ESPType.SHADOW_ASSASSIN.priority,
               0L,
               16,
               null
            )
         )
      } else if (StringsKt.contains(n, "Diamond Guy", true) || StringsKt.contains(n, "Lost Adventurer", true)) {
         tracked.put(
            e,
            DungeonESP.ESPData(
               e as Entity,
               DungeonESP.ESPType.DIAMOND_GUY,
               RenderUtils.INSTANCE.parseColor(Config.dungeonESPStarColor),
               DungeonESP.ESPType.DIAMOND_GUY.priority,
               0L,
               16,
               null
            )
         )
      }
   }

   fun handleBat(e: BatEntity) {
      if (CollectionsKt.listOf(arrayOf(100.0F, 200.0F, 400.0F, 800.0F)).contains(e.method_6063())) {
         tracked.put(
            e,
            DungeonESP.ESPData(
               e as Entity, DungeonESP.ESPType.BAT, RenderUtils.INSTANCE.parseColor(Config.dungeonESPBatColor), DungeonESP.ESPType.BAT.priority, 0L, 16, null
            )
         )
      }
   }

   fun getMobEntity(stand: ArmorStandEntity): Entity {
      var var10000: UUID = stand.method_5667()
      val box: ClientWorld = this.getMc().field_1687
      val var40: ClientWorld = if (box is ClientWorld) box else null
      if ((if (box is ClientWorld) box else null) == null) {
         null
      } else {
         val lockedMobUuid: UUID = standToMob.get(var10000)
         if (lockedMobUuid != null) {
            val var41: java.lang.Iterable = var40.method_18112()
            val closest: java.util.Iterator = var41.iterator()

            while (true) {
               if (!closest.hasNext()) {
                  var10000 = null
                  break
               }

               val dist: Any = closest.next()
               if ((dist as Entity).method_5667() == lockedMobUuid) {
                  var10000 = (UUID)dist
                  break
               }
            }

            val var20: Entity = var10000 as Entity
            if (var10000 as Entity != null
               && (var10000 as Entity).method_5805()
               && (var10000 as Entity).method_73189().method_1022(stand.method_73189()) <= 4.5) {
               var20
            }

            val var43: UUID = standToMob.remove(var10000)
         }

         val var44: Box = stand.method_5829().method_1009(2.0, 4.0, 2.0)
         val var21: Box = var44
         val var45: java.lang.Iterable = var40.method_18112()
         val var28: java.lang.Iterable = CollectionsKt.toList(var45)
         val var30: java.util.Collection = ArrayList()

         for (`minValue$iv` in var28) {
            val `e$iv`: Entity = `minValue$iv` as Entity
            if ((`minValue$iv` as Entity).method_5805()
               && `minValue$iv` as Entity is LivingEntity
               && (`minValue$iv` as Entity) !is ArmorStandEntity
               && !(`minValue$iv` as Entity == INSTANCE.getMc().field_1724)
               && ((`minValue$iv` as Entity) !is WitherEntity || !((`minValue$iv` as Entity) as WitherEntity).method_5767())
               && `minValue$iv` as Entity != stand
               && var21.method_994(((`minValue$iv` as Entity) as LivingEntity).method_5829())) {
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
               var var35: Double = (var33 as Entity).method_73189().method_1025(stand.method_73189())

               do {
                  val var37: Any = var32.next()
                  val var39: Double = (var37 as Entity).method_73189().method_1025(stand.method_73189())
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
            null
         } else {
            if (var10000.method_73189().method_1022(stand.method_73189()) <= 4.5) {
               standToMob.put(var10000, var10000.method_5667())
               var10000 = var10000
            } else {
               var10000 = null
            }

            var10000
         }
      }
   }

   private fun renderESP(ctx: WorldRenderContext) {
      try {
         val var10000: MatrixStack = ctx.matrices()
         if (var10000 == null) {
            return
         }

         val var49: Camera = ctx.gameRenderer().method_19418()
         val var50: ClientPlayerEntity = this.getMc().field_1724
         if (var50 == null) {
            return
         }

         val player: ClientPlayerEntity = var50
         val var51: ClientWorld = this.getMc().field_1687
         if (var51 == null) {
            return
         }

         val world: ClientWorld = var51
         val var52: Vec3d = var50.method_73189()
         val playerPos: Vec3d = var52
         val tickDelta: Float = 1.0F
         val maxRenderDist: Double = if (Config.dungeonESPPerfMode) 30.0 else 60.0
         val camPos: java.lang.Iterable = tracked.values()
         val userOpacity: java.util.Collection = ArrayList()

         for (outlines in camPos) {
            if ((outlines as DungeonESP.ESPData).getEntity().method_5805()) {
               userOpacity.add(outlines)
            }
         }

         val sorted: java.util.List = CollectionsKt.sortedWith(
            userOpacity as java.util.List, DungeonESP$renderESP$$inlined$thenBy$1(DungeonESP$renderESP$$inlined$compareBy$1(), var52)
         )
         val var54: Entry = var10000.method_23760()
         val var35: Entry = var54
         val var55: Matrix4f = var54.method_23761()
         val var36: Matrix4f = var55
         val var56: Vec3d = var49.method_71156()
         val var37: Vec3d = var56
         val var38: Float = RangesKt.coerceIn(Config.dungeonESPOpacityPct, 1, 100) / 100.0F
         val var39: Boolean = Config.dungeonESPThroughWalls
         val var57: VertexConsumerProvider = ctx.consumers()
         if (var57 == null) {
            return
         }

         val var40: VertexConsumerProvider = var57
         val var58: MinecraftClient = this.getMc()
         val var59: OutlineVertexConsumerProvider = (var58 as MinecraftAccessor).getRenderBuffers().method_23003()
         val var41: OutlineVertexConsumerProvider = var59

         for (var43 in sorted) {
            val e: Entity = var43.getEntity()
            val dist: Double = e.method_73189().method_1022(playerPos)
            if (!(dist > maxRenderDist)) {
               val renderBox: Box = RenderUtils.INSTANCE.getInterpolatedBox(e, tickDelta)
               val base: Color = RenderUtils.INSTANCE.applyAlpha(var43.outlineColor, RenderUtils.INSTANCE.calculateSmoothAlpha(dist, 45.0, maxRenderDist))
               if (var39 && RenderUtils.INSTANCE.isOccluded(player as PlayerEntity, renderBox, world as World)) {
                  val var44: Int = base.getRed()
                  val var45: Int = base.getGreen()
                  val var46: Int = base.getBlue()
                  val var47: Float = RangesKt.coerceIn(var38, 0.2F, 1.0F)
                  var41.method_23286((int)((float)var44 * var47) shl 16 or (int)((float)var45 * var47) shl 8 or (int)((float)var46 * var47))
                  val var61: VertexConsumer = var41.method_73477(RenderLayers.method_76018(RenderUtils.INSTANCE.getWHITE_TEX()))
                  RenderUtils.INSTANCE.renderFilledAabb(var61, var36, var35, var37, renderBox)
               } else {
                  val r: Float = base.getRed() / 255.0F
                  val g: Float = base.getGreen() / 255.0F
                  val b: Float = base.getBlue() / 255.0F
                  val var60: VertexConsumer = var40.method_73477(RenderLayers.method_76000(RenderUtils.INSTANCE.getWHITE_TEX()))
                  RenderUtils.INSTANCE.renderFilledAabb(var60, var36, var35, var37, renderBox, r, g, b, var38 * 0.4F)
                  RenderUtils.INSTANCE.renderEdgeBoxes(var60, var36, var35, var37, renderBox, r, g, b, 1.0F, 0.02)
               }
            }
         }

         var41.method_23285()
      } catch (var33: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var33.printStackTrace()
         }
      }
   }

   public fun onWorldChange() {
      tracked.clear()
   }

   private data class ESPData {
      private Entity entity;
      public final val type: jooon.features.dungeons.DungeonESP.ESPType
      public final val outlineColor: Color
      public final val priority: Int
      public final var lastSeen: Long

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

      public override fun toString(): String {
         return "ESPData(entity=${this.entity}, type=${this.type}, outlineColor=${this.outlineColor}, priority=${this.priority}, lastSeen=${this.lastSeen})"
      }

      public override fun hashCode(): Int {
         return (((this.entity.hashCode() * 31 + this.type.hashCode()) * 31 + this.outlineColor.hashCode()) * 31 + Integer.hashCode(this.priority)) * 31
            + java.lang.Long.hashCode(this.lastSeen)
         }

      public override operator fun equals(other: Any?): Boolean {
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

      public final val priority: Int

      init {
         this.priority = priority
      }

      @JvmStatic
      fun getEntries(): EnumEntries<DungeonESP.ESPType> {
         $ENTRIES
      }
   }
}
