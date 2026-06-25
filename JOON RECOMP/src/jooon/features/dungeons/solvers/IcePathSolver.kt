package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.concurrent.ConcurrentLinkedQueue
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nIcePathSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IcePathSolver.kt\njooon/features/dungeons/solvers/IcePathSolver\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,120:1\n1#2:121\n1864#3,3:122\n*S KotlinDebug\n*F\n+ 1 IcePathSolver.kt\njooon/features/dungeons/solvers/IcePathSolver\n*L\n106#1:122,3\n*E\n"])
public object IcePathSolver {
   private final val solutions: List<jooon.features.dungeons.solvers.IcePathSolver.IcePathSolution> =
      CollectionsKt.listOf(
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
      private final val currentSolution: ConcurrentLinkedQueue<jooon.features.dungeons.solvers.IcePathSolver.IcePathSolution> = ConcurrentLinkedQueue()
   private final var inPath: Boolean
   @JvmStatic
   private SilverfishEntity silverfishEntity;

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.icePathSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inPath) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.icePathSolver && Utils.INSTANCE.inDungeon && inPath) {
            INSTANCE.render(ctx)
         }
      })
   }

   private fun reset() {
      inPath = false
      currentSolution.clear()
      silverfishEntity = null
   }

   private fun tick() {
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
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

         val var18: ClientWorld = this.getMc().field_1687
         if (var18 != null) {
            run label105@{
               if (silverfishEntity != null) {
                  val var19: SilverfishEntity = silverfishEntity
                  if (!var19.method_31481()) {
                     val var20: SilverfishEntity = silverfishEntity
                     if (var20.method_5805()) {
                        return@label105
                     }
                  }
               }

               val var21: java.lang.Iterable = var18.method_18112()
               val var6: java.util.Iterator = CollectionsKt.toList(var21).iterator()

               while (true) {
                  if (!var6.hasNext()) {
                     var22 = null
                     break
                  }

                  val first: Any = var6.next()
                  if ((first as Entity).method_5864() == EntityType.field_6125) {
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
               val var13: SilverfishEntity = silverfishEntity
               if (!currentSolution.isEmpty()) {
                  val var23: IcePathSolver.IcePathSolution = currentSolution.peek()
                  if (var23 != null
                     && Math.abs(var13.method_23317() - ((double)var23.x2 + 0.5)) + Math.abs(var13.method_23321() - ((double)var23.z2 + 0.5)) < 0.8) {
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
            return
         }

         val var10000: MatrixStack = ctx.matrices()
         if (var10000 == null) {
            return
         }

         val var19: Camera = ctx.gameRenderer().method_19418()
         val var20: Vec3d = var19.method_71156()
         val cameraPos: Vec3d = var20
         val var21: Entry = var10000.method_23760()
         val lastEntry: Entry = var21
         val var22: Matrix4f = var21.method_23761()
         val posMat: Matrix4f = var22
         val var23: VertexConsumerProvider = ctx.consumers()
         if (var23 == null) {
            return
         }

         val consumers: VertexConsumerProvider = var23
         val `$this$forEachIndexed$iv`: java.lang.Iterable = currentSolution
         var `index$iv`: Int = 0

         for (`item$iv` in `$this$forEachIndexed$iv`) {
            val var13: Int = `index$iv`++
            if (var13 < 0) {
               CollectionsKt.throwIndexOverflow()
            }

            val sol: IcePathSolver.IcePathSolution = `item$iv` as IcePathSolver.IcePathSolution
            val color: Color = if (var13 == 0) Color.GREEN else Color.RED
            RenderUtils.INSTANCE
               .renderLineRobust(
                  consumers,
                  posMat,
                  lastEntry,
                  cameraPos,
                  Vec3d((double)sol.x1 + 0.5, 67.5, (double)sol.z1 + 0.5),
                  Vec3d((double)sol.x2 + 0.5, 67.5, (double)sol.z2 + 0.5),
                  (float)color.getRed() / 255.0F,
                  (float)color.getGreen() / 255.0F,
                  (float)color.getBlue() / 255.0F,
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

   public data class IcePathSolution(x1: Int, z1: Int, x2: Int, z2: Int) {
      public final val x1: Int
      public final val z1: Int
      public final val x2: Int
      public final val z2: Int

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

      public fun copy(x1: Int = this.x1, z1: Int = this.z1, x2: Int = this.x2, z2: Int = this.z2): jooon.features.dungeons.solvers.IcePathSolver.IcePathSolution {
         return IcePathSolver.IcePathSolution(x1, z1, x2, z2)
      }

      public override fun toString(): String {
         return "IcePathSolution(x1=${this.x1}, z1=${this.z1}, x2=${this.x2}, z2=${this.z2})"
      }

      public override fun hashCode(): Int {
         return ((Integer.hashCode(this.x1) * 31 + Integer.hashCode(this.z1)) * 31 + Integer.hashCode(this.x2)) * 31 + Integer.hashCode(this.z2)
      }

      public override operator fun equals(other: Any?): Boolean {
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
