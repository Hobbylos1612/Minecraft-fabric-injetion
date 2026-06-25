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

object BlazeSolver {
   private val blazeHpRegex: Regex = Regex("^\\[Lv15] ♨ Blaze [\\d,]+/([\\d,]+)❤$")
   private val entityList: ConcurrentHashMap<Int, Int> = ConcurrentHashMap()
   private var hasPlatform: Boolean
   private var inBlaze: Boolean
   private val blazes: CopyOnWriteArrayList<jooon.features.dungeons.solvers.BlazeSolver.BlazeEntity> = CopyOnWriteArrayList()
   
   private Vec3d efficientPos;
   
   private Vec3d etherToPos;

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ client: MinecraftClient ->
         if (Config.blazeSolver && Utils.inDungeon) {
            tick()
         } else {
            if (inBlaze) {
               reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.blazeSolver && Utils.inDungeon && inBlaze) {
            render(ctx)
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
      if (this.getMc().player != null) {

         if (var10000 != null) {


            if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Blaze") {
               if (!inBlaze) {
                  inBlaze = true

                  if (currentEntities != null) {
                     val `this$iv`: BlockState = WorldUtils.INSTANCE
                        .getBlockState(
                           (currentEntities.getFirst() as java.lang.Number).intValue(), 118, (currentEntities.getSecond() as java.lang.Number).intValue()
                        )
                        hasPlatform = (if (`this$iv` != null) `this$iv`.getBlock() else null) == Blocks.COBBLESTONE
                     val ``: Pair = currentRoom.fromComp(20, 11)
                     if (`` != null) {
                        efficientPos = Vec3d(
                           (``.getFirst() as java.lang.Number).intValue() + 0.5,
                           if (hasPlatform) 103.0 else 53.0,
                           (``.getSecond() as java.lang.Number).intValue() + 0.5
                        )
                        if (hasPlatform) {
                           etherToPos = Vec3d(
                              (``.getFirst() as java.lang.Number).intValue() + 0.5,
                              85.0,
                              (``.getSecond() as java.lang.Number).intValue() + 0.5
                           )
                        }
                     }
                  }
               }

               blazes.clear()
               val var27: java.lang.Iterable = var10000.getEntities()
               val var19: java.util.List = toList(var27)

               label168@ for (var28 in var19) {

                  if ((var28 as Entity).getType() == EntityType.ARMOR_STAND) {

                     if (var29 != null) {

                        if (var30 != null) {
                           val var31: String
                           if (!startsWith$default(var30, "{\"text\":\"", false, 2, null)
                              && !contains$default(var30, "\"text\"", false, 2, null)) {
                              var31 = Utils.stripColor(var30)
                           } else {

                              if (match != -1) {


                                 if (actualBlaze > maxHp) {
                                    var31 = var30.substring(maxHp, actualBlaze)
                                 } else {
                                    var31 = Utils.stripColor(var30)
                                 }
                              } else {
                                 var31 = Utils.stripColor(var30)
                              }
                           }

                           if (var32 != null) {

                                 replace$default(var32.getGroupValues().get(1) as String, ",", "", false, 4, null)
                              )

                              if (var26 != null && var26.getType() == EntityType.BLAZE) {
                                 blazes.add(BlazeSolver.BlazeEntity(var26, var25))
                              } else {


                                 val var15: java.util.Iterator = var19.iterator()

                                 while (true) {
                                    val var34: Any
                                    if (var15.hasNext()) {

                                       if (!((var16 as Entity).getType() == EntityType.BLAZE)
                                          || !((var16 as Entity).getEntityPos().squaredDistanceTo(standPos) < 4.0)) {
return continue
                                       }

                                       var34 = var16
                                    } else {
                                       var34 = null
                                    }

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
                  sortWith(var21, BlazeSolver$tick$$inlined$sortBy$1())
               }

               if (!hasPlatform) {
                  reverse(blazes)
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

         if (var10000 == null) {
return return
         }








         if (var25 == null) {
return return
         }

         if (efficientPos != null) {



            var26.renderBox(var25, var24, var23, var22, var10005, var10006, 0.4F)
         }

         if (etherToPos != null) {



            var27.renderBox(var25, var24, var23, var22, var30, var32, 0.4F)
         }

         val colors: java.util.List = listOf(arrayOf(Color.GREEN, Color.ORANGE, Color.RED))
         var var16: Int = 0

         for (var18 in Math.min(3, blazes.size())..var16) {
            val var20: BlazeSolver.BlazeEntity = blazes.get(var16)


            this.renderBox(consumers, posMat, lastEntry, cameraPos, var28, color, 0.3F)
            if (var16 < Math.min(2, blazes.size() - 1)) {
               val next: BlazeSolver.BlazeEntity = blazes.get(var16 + 1)



               RenderUtils.renderLineRobust$default(
                  var29,
                  consumers,
                  posMat,
                  lastEntry,
                  cameraPos,
                  var31,
                  var33,
                  color.getRed().toFloat() / 255.0F,
                  color.getGreen().toFloat() / 255.0F,
                  color.getBlue().toFloat() / 255.0F,
                  1.0F,
                  0.0F,
                  1024,
return null
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



      RenderUtils.renderBoxFill(consumers, posMat, entry, cameraPos, box, r, g, b, 0.25F)
      RenderUtils.renderBoxOutlineRobust(consumers, posMat, entry, cameraPos, box, r, g, b, 1.0F, 0.02F)
      if (Config.dungeonESPThroughWalls) {


         RenderUtils.renderBoxFillThroughWalls(var12, posMat, entry, cameraPos, box, color)
      }
   }

   data class BlazeEntity {
      private Entity entity;
      val maxHP: Int

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

      override fun toString(): String {
         return "BlazeEntity(entity=${this.entity}, maxHP=${this.maxHP})"
      }

      override fun hashCode(): Int {
         return this.entity.hashCode() * 31 + Integer.hashCode(this.maxHP)
      }

      override operator fun equals(other: Any?): Boolean {
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
