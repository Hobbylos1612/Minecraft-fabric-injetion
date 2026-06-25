package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.concurrent.ConcurrentLinkedQueue
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.util.RenderUtils
import jooon.util.Utils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.SilverfishEntity
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

object IcePathSolver {
   private val solutions: List<jooon.features.dungeons.solvers.IcePathSolver.IcePathSolution> =
      listOf(
         arrayOf(
            IcePathSolver.IcePathSolution(8, 9, 12, 9),
            IcePathSolver.IcePathSolution(12, 9, 12, 8),
            IcePathSolver.IcePathSolution(12, 8, 20, 8),
            IcePathSolver.IcePathSolution(20, 8, 20, 24),
            IcePathSolver.IcePathSolution(20, 24, 19, 24),
            IcePathSolver.IcePathSolution(19, 24, 19, 23),
            IcePathSolver.IcePathSolution(19, 23, 21, 23),
            IcePathSolver.IcePathSolution(21, 23, 21, 14),
            IcePathSolver.IcePathSolution(21, 14, 14, 14),
            IcePathSolver.IcePathSolution(14, 14, 14, 25)
         )
      )
      private val currentSolution: ConcurrentLinkedQueue<jooon.features.dungeons.solvers.IcePathSolver.IcePathSolution> = ConcurrentLinkedQueue()
   private var inPath: Boolean
   
   private SilverfishEntity silverfishEntity;

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.icePathSolver && Utils.inDungeon) {
            tick()
         } else {
            if (inPath) {
               reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.icePathSolver && Utils.inDungeon && inPath) {
            render(ctx)
         }
      })
   }

   private fun reset() {
      inPath = false
      currentSolution.clear()
      silverfishEntity = null
   }

   private fun tick() {

      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Ice Path") {
         if (!inPath) {
            inPath = true
            currentSolution.clear()

            for (pos in solutions) {
               var var10000: Pair = currentRoom.fromComp(pos.x1, pos.z1)
               if (var10000 != null) {
                  var10000 = currentRoom.fromComp(pos.x2, pos.z2)
                  if (var10000 != null) {
                     currentSolution.add(
                        IcePathSolver.IcePathSolution(
                           (var10000.getFirst() as java.lang.Number).intValue(),
                           (var10000.getSecond() as java.lang.Number).intValue(),
                           (var10000.getFirst() as java.lang.Number).intValue(),
                           (var10000.getSecond() as java.lang.Number).intValue()
                        )
                     )
                  }
               }
            }
         }

         if (var18 != null) {
            run label105@{
               if (silverfishEntity != null) {

                  if (!var19.isRemoved()) {

                     if (var20.isAlive()) {
                        return@label105
                     }
                  }
               }

               val var21: java.lang.Iterable = var18.getEntities()
               val var6: java.util.Iterator = toList(var21).iterator()

               while (true) {
                  if (!var6.hasNext()) {
                     var22 = null
break
                  }

                  if ((first as Entity).getType() == EntityType.SILVERFISH) {
                     var22 = first
break
                  }
               }

               silverfishEntity = var22 as? SilverfishEntity
               if (currentSolution.size() == 1) {
                  currentSolution.clear()
               }
            }

            if (silverfishEntity != null) {

               if (!currentSolution.isEmpty()) {
                  val var23: IcePathSolver.IcePathSolution = currentSolution.peek()
                  if (var23 != null
                     && Math.abs(var13.getX() - (var23.x2.toDouble() + 0.5)) + Math.abs(var13.getZ() - (var23.z2.toDouble() + 0.5)) < 0.8) {
                     currentSolution.poll()
                  }
               }
            }
         }
      } else {
         if (inPath) {
            this.reset()
         }
      }
   }

   private fun render(ctx: WorldRenderContext) {
      try {
         if (currentSolution.isEmpty()) {
return return
         }

         if (var10000 == null) {
return return
         }








         if (var23 == null) {
return return
         }

         val `this$iv`: java.lang.Iterable = currentSolution
         var `index$iv`: Int = 0

         for (`item$iv` in `this$iv`) {

            if (var13 < 0) {
               throwIndexOverflow()
            }

            val sol: IcePathSolver.IcePathSolution = `item$iv` as IcePathSolver.IcePathSolution

            RenderUtils.INSTANCE
               .renderLineRobust(
                  consumers,
                  posMat,
                  lastEntry,
                  cameraPos,
                  Vec3d(sol.x1.toDouble() + 0.5, 67.5, sol.z1.toDouble() + 0.5),
                  Vec3d(sol.x2.toDouble() + 0.5, 67.5, sol.z2.toDouble() + 0.5),
                  color.getRed().toFloat() / 255.0F,
                  color.getGreen().toFloat() / 255.0F,
                  color.getBlue().toFloat() / 255.0F,
                  1.0F,
                  0.08F
               )
            }
      } catch (var18: java.lang.Throwable) {
         if (System.currentTimeMillis() % 10000 < 50L) {
            var18.printStackTrace()
         }
      }
   }

   data class IcePathSolution(x1: Int, z1: Int, x2: Int, z2: Int) {
      val x1: Int
      val z1: Int
      val x2: Int
      val z2: Int

      init {
         this.x1 = x1
         this.z1 = z1
         this.x2 = x2
         this.z2 = z2
      }

      public operator fun component1(): Int {
         return this.x1
      }

      public operator fun component2(): Int {
         return this.z1
      }

      public operator fun component3(): Int {
         return this.x2
      }

      public operator fun component4(): Int {
         return this.z2
      }

      fun copy(x1: Int = this.x1, z1: Int = this.z1, x2: Int = this.x2, z2: Int = this.z2): jooon.features.dungeons.solvers.IcePathSolver.IcePathSolution {
         return IcePathSolver.IcePathSolution(x1, z1, x2, z2)
      }

      override fun toString(): String {
         return "IcePathSolution(x1=${this.x1}, z1=${this.z1}, x2=${this.x2}, z2=${this.z2})"
      }

      override fun hashCode(): Int {
         return ((Integer.hashCode(this.x1) * 31 + Integer.hashCode(this.z1)) * 31 + Integer.hashCode(this.x2)) * 31 + Integer.hashCode(this.z2)
      }

      override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is IcePathSolver.IcePathSolution
               && this.x1 == (other as IcePathSolver.IcePathSolution).x1
               && this.z1 == (other as IcePathSolver.IcePathSolution).z1
               && this.x2 == (other as IcePathSolver.IcePathSolution).x2
               && this.z2 == (other as IcePathSolver.IcePathSolution).z2
            }
      }
   }
}
