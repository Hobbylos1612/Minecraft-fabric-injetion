package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.features.dungeons.map.util.WorldUtils
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nBlazeSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlazeSolver.kt\njooon/features/dungeons/solvers/BlazeSolver\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,184:1\n1#2:185\n1002#3,2:186\n*S KotlinDebug\n*F\n+ 1 BlazeSolver.kt\njooon/features/dungeons/solvers/BlazeSolver\n*L\n126#1:186,2\n*E\n"])
public object BlazeSolver {
   private final val blazeHpRegex: Regex = Regex("^\\[Lv15] ♨ Blaze [\\d,]+/([\\d,]+)❤$")
   private final val entityList: ConcurrentHashMap<Int, Int> = ConcurrentHashMap()
   private final var hasPlatform: Boolean
   private final var inBlaze: Boolean
   private final val blazes: CopyOnWriteArrayList<jooon.features.dungeons.solvers.BlazeSolver.BlazeEntity> = CopyOnWriteArrayList()
   @JvmStatic
   private Vec3d efficientPos;
   @JvmStatic
   private Vec3d etherToPos;

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         if (Config.blazeSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inBlaze) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.blazeSolver && Utils.INSTANCE.inDungeon && inBlaze) {
            INSTANCE.render(ctx)
         }
      })
   }

   private fun reset() {
      inBlaze = false
      hasPlatform = false
      efficientPos = null
      etherToPos = null
      blazes.clear()
      entityList.clear()
   }

   private fun tick() {
      if (this.getMc().field_1724 != null) {
         val var10000: ClientWorld = this.getMc().field_1687
         if (var10000 != null) {
            val world: ClientWorld = var10000
            val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
            if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Blaze") {
               if (!inBlaze) {
                  inBlaze = true
                  val currentEntities: Pair = currentRoom.fromComp(15, 14)
                  if (currentEntities != null) {
                     val `$this$sortBy$iv`: BlockState = WorldUtils.INSTANCE
                        .getBlockState(
                           (currentEntities.getFirst() as java.lang.Number).intValue(), 118, (currentEntities.getSecond() as java.lang.Number).intValue()
                        )
                        hasPlatform = (if (`$this$sortBy$iv` != null) `$this$sortBy$iv`.method_26204() else null) == Blocks.field_10445
                     val `$i$f$sortBy`: Pair = currentRoom.fromComp(20, 11)
                     if (`$i$f$sortBy` != null) {
                        efficientPos = Vec3d(
                           (`$i$f$sortBy`.getFirst() as java.lang.Number).intValue() + 0.5,
                           if (hasPlatform) 103.0 else 53.0,
                           (`$i$f$sortBy`.getSecond() as java.lang.Number).intValue() + 0.5
                        )
                        if (hasPlatform) {
                           etherToPos = Vec3d(
                              (`$i$f$sortBy`.getFirst() as java.lang.Number).intValue() + 0.5,
                              85.0,
                              (`$i$f$sortBy`.getSecond() as java.lang.Number).intValue() + 0.5
                           )
                        }
                     }
                  }
               }

               blazes.clear()
               val var27: java.lang.Iterable = var10000.method_18112()
               val var19: java.util.List = CollectionsKt.toList(var27)

               label168@ for (var28 in var19) {
                  val var22: Entity = var28 as Entity
                  if ((var28 as Entity).method_5864() == EntityType.field_6131) {
                     val var29: Text = var22.method_5797()
                     if (var29 != null) {
                        val var30: java.lang.String = var29.getString()
                        if (var30 != null) {
                           val var31: java.lang.String
                           if (!StringsKt.startsWith$default(var30, "{\"text\":\"", false, 2, null)
                              && !StringsKt.contains$default(var30, "\"text\"", false, 2, null)) {
                              var31 = Utils.stripColor(var30)
                           } else {
                              val match: Int = StringsKt.indexOf$default(var30, "\"text\":\"", 0, false, 6, null)
                              if (match != -1) {
                                 val maxHp: Int = match + 8
                                 val actualBlaze: Int = StringsKt.indexOf$default(var30, "\"", match + 8, false, 4, null)
                                 if (actualBlaze > maxHp) {
                                    var31 = var30.substring(maxHp, actualBlaze)
                                 } else {
                                    var31 = Utils.stripColor(var30)
                                 }
                              } else {
                                 var31 = Utils.stripColor(var30)
                              }
                           }

                           val var32: MatchResult = blazeHpRegex.matchEntire(var31)
                           if (var32 != null) {
                              val var25: Int = Integer.parseInt(
                                 StringsKt.replace$default(var32.getGroupValues().get(1) as java.lang.String, ",", "", false, 4, null)
                              )
                              val var26: Entity = world.method_8469(var22.method_5628() - 1)
                              if (var26 != null && var26.method_5864() == EntityType.field_6099) {
                                 blazes.add(BlazeSolver.BlazeEntity(var26, var25))
                              } else {
                                 val var33: Vec3d = var22.method_73189()
                                 val standPos: Vec3d = var33
                                 val var15: java.util.Iterator = var19.iterator()

                                 while (true) {
                                    val var34: Any
                                    if (var15.hasNext()) {
                                       val var16: Any = var15.next()
                                       if (!((var16 as Entity).method_5864() == EntityType.field_6099)
                                          || !((var16 as Entity).method_73189().method_1025(standPos) < 4.0)) {
                                          continue
                                       }

                                       var34 = var16
                                    } else {
                                       var34 = null
                                    }

                                    val fallback: Entity = var34 as Entity
                                    if (var34 as Entity != null) {
                                       blazes.add(BlazeSolver.BlazeEntity(fallback, var25))
                                    }
                                    continue@label168
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               val var21: java.util.List = blazes
               if (blazes.size() > 1) {
                  CollectionsKt.sortWith(var21, BlazeSolver$tick$$inlined$sortBy$1())
               }

               if (!hasPlatform) {
                  CollectionsKt.reverse(blazes)
               }
            } else {
               if (inBlaze) {
                  this.reset()
               }
            }
         }
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         val var10000: MatrixStack = ctx.matrices()
         if (var10000 == null) {
            return
         }

         val var21: Camera = ctx.gameRenderer().method_19418()
         val var22: Vec3d = var21.method_71156()
         val cameraPos: Vec3d = var22
         val var23: Entry = var10000.method_23760()
         val lastEntry: Entry = var23
         val var24: Matrix4f = var23.method_23761()
         val posMat: Matrix4f = var24
         val var25: VertexConsumerProvider = ctx.consumers()
         if (var25 == null) {
            return
         }

         val consumers: VertexConsumerProvider = var25
         if (efficientPos != null) {
            val var26: BlazeSolver = INSTANCE
            val var10005: Box = Box.method_29968(efficientPos.method_1023(0.5, 0.0, 0.5))
            val var10006: Color = Color.GREEN
            var26.renderBox(var25, var24, var23, var22, var10005, var10006, 0.4F)
         }

         if (etherToPos != null) {
            val var27: BlazeSolver = INSTANCE
            val var30: Box = Box.method_29968(etherToPos.method_1023(0.5, 0.0, 0.5))
            val var32: Color = Color.CYAN
            var27.renderBox(var25, var24, var23, var22, var30, var32, 0.4F)
         }

         val colors: java.util.List = CollectionsKt.listOf(arrayOf(Color.GREEN, Color.ORANGE, Color.RED))
         var var16: Int = 0

         for (var18 in Math.min(3, blazes.size())..var16) {
            val var20: BlazeSolver.BlazeEntity = blazes.get(var16)
            val color: Color = colors.get(var16) as Color
            val var28: Box = var20.getEntity().method_5829()
            this.renderBox(consumers, posMat, lastEntry, cameraPos, var28, color, 0.3F)
            if (var16 < Math.min(2, blazes.size() - 1)) {
               val next: BlazeSolver.BlazeEntity = blazes.get(var16 + 1)
               val var29: RenderUtils = RenderUtils.INSTANCE
               val var31: Vec3d = var20.getEntity().method_73189().method_1031(0.0, 0.5, 0.0)
               val var33: Vec3d = next.getEntity().method_73189().method_1031(0.0, 0.5, 0.0)
               RenderUtils.renderLineRobust$default(
                  var29,
                  consumers,
                  posMat,
                  lastEntry,
                  cameraPos,
                  var31,
                  var33,
                  (float)color.getRed() / 255.0F,
                  (float)color.getGreen() / 255.0F,
                  (float)color.getBlue() / 255.0F,
                  1.0F,
                  0.0F,
                  1024,
                  null
               )
            }
         }
      } catch (var15: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var15.printStackTrace()
         }
      }
   }

   fun renderBox(consumers: VertexConsumerProvider, posMat: Matrix4f, entry: Entry, cameraPos: Vec3d, box: Box, color: Color, alpha: Float) {
      val r: Float = color.getRed() / 255.0F
      val g: Float = color.getGreen() / 255.0F
      val b: Float = color.getBlue() / 255.0F
      RenderUtils.INSTANCE.renderBoxFill(consumers, posMat, entry, cameraPos, box, r, g, b, 0.25F)
      RenderUtils.INSTANCE.renderBoxOutlineRobust(consumers, posMat, entry, cameraPos, box, r, g, b, 1.0F, 0.02F)
      if (Config.dungeonESPThroughWalls) {
         val var10000: MinecraftClient = this.getMc()
         val var12: OutlineVertexConsumerProvider = (var10000 as MinecraftAccessor).getRenderBuffers().method_23003()
         RenderUtils.INSTANCE.renderBoxFillThroughWalls(var12, posMat, entry, cameraPos, box, color)
      }
   }

   public data class BlazeEntity {
      private Entity entity;
      public final val maxHP: Int

      fun BlazeEntity(entity: Entity, maxHP: Int) {
         this.entity = entity
         this.maxHP = maxHP
      }

      fun getEntity(): Entity {
         this.entity
      }

      fun component1(): Entity {
         this.entity
      }

      public operator fun component2(): Int {
         return this.maxHP
      }

      fun copy(entity: Entity, maxHP: Int): BlazeSolver.BlazeEntity {
         BlazeSolver.BlazeEntity(entity, maxHP)
      }

      public override fun toString(): String {
         return "BlazeEntity(entity=${this.entity}, maxHP=${this.maxHP})"
      }

      public override fun hashCode(): Int {
         return this.entity.hashCode() * 31 + Integer.hashCode(this.maxHP)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is BlazeSolver.BlazeEntity
               && this.entity == (other as BlazeSolver.BlazeEntity).entity
               && this.maxHP == (other as BlazeSolver.BlazeEntity).maxHP
            }
      }
   }
}
