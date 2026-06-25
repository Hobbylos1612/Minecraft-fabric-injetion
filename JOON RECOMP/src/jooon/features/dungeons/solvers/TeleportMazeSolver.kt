package jooon.features.dungeons.solvers

import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import jooon.config.Config
import jooon.features.dungeons.map.api.DungeonRoom
import jooon.features.dungeons.map.api.DungeonScanner
import jooon.features.dungeons.map.api.mapEnums.RoomTypes
import jooon.mixins.MinecraftAccessor
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nTeleportMazeSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeleportMazeSolver.kt\njooon/features/dungeons/solvers/TeleportMazeSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,171:1\n1855#2,2:172\n2333#2,14:174\n2333#2,14:188\n1855#2,2:202\n1855#2,2:204\n*S KotlinDebug\n*F\n+ 1 TeleportMazeSolver.kt\njooon/features/dungeons/solvers/TeleportMazeSolver\n*L\n137#1:172,2\n102#1:174,14\n103#1:188,14\n106#1:202,2\n119#1:204,2\n*E\n"])
public object TeleportMazeSolver {
   private final val endFramePositions: List<jooon.features.dungeons.solvers.TeleportMazeSolver.CompPad> =
      CollectionsKt.listOf(
         arrayOf(
            TeleportMazeSolver.CompPad(4, 6, 5, 7, false, false, 48, null),
            TeleportMazeSolver.CompPad(4, 12, 5, 11, false, false, 48, null),
            TeleportMazeSolver.CompPad(4, 14, 5, 15, false, false, 48, null),
            TeleportMazeSolver.CompPad(4, 20, 5, 19, false, false, 48, null),
            TeleportMazeSolver.CompPad(4, 22, 5, 23, false, false, 48, null),
            TeleportMazeSolver.CompPad(4, 28, 5, 27, false, false, 48, null),
            TeleportMazeSolver.CompPad(10, 6, 9, 7, false, false, 48, null),
            TeleportMazeSolver.CompPad(10, 12, 9, 11, false, false, 48, null),
            TeleportMazeSolver.CompPad(10, 14, 9, 15, false, false, 48, null),
            TeleportMazeSolver.CompPad(10, 20, 9, 19, false, false, 48, null),
            TeleportMazeSolver.CompPad(10, 22, 9, 23, false, false, 48, null),
            TeleportMazeSolver.CompPad(10, 28, 9, 27, false, false, 48, null),
            TeleportMazeSolver.CompPad(12, 22, 13, 23, false, false, 48, null),
            TeleportMazeSolver.CompPad(12, 28, 13, 27, false, false, 48, null),
            TeleportMazeSolver.CompPad(18, 22, 17, 23, false, false, 48, null),
            TeleportMazeSolver.CompPad(18, 28, 17, 27, false, false, 48, null),
            TeleportMazeSolver.CompPad(20, 6, 21, 7, false, false, 48, null),
            TeleportMazeSolver.CompPad(20, 12, 21, 11, false, false, 48, null),
            TeleportMazeSolver.CompPad(20, 14, 21, 15, false, false, 48, null),
            TeleportMazeSolver.CompPad(20, 20, 21, 19, false, false, 48, null),
            TeleportMazeSolver.CompPad(20, 22, 21, 23, false, false, 48, null),
            TeleportMazeSolver.CompPad(20, 28, 21, 27, false, false, 48, null),
            TeleportMazeSolver.CompPad(26, 6, 25, 7, false, false, 48, null),
            TeleportMazeSolver.CompPad(26, 12, 25, 11, false, false, 48, null),
            TeleportMazeSolver.CompPad(26, 14, 25, 15, false, false, 48, null),
            TeleportMazeSolver.CompPad(26, 20, 25, 19, false, false, 48, null),
            TeleportMazeSolver.CompPad(26, 22, 25, 23, false, false, 48, null),
            TeleportMazeSolver.CompPad(26, 28, 25, 27, false, false, 48, null),
            TeleportMazeSolver.CompPad(15, 12, 14, 11, true, false, 32, null),
            TeleportMazeSolver.CompPad(15, 14, 16, 15, true, true)
         )
      )
      private final var inMaze: Boolean
   private final val pads: CopyOnWriteArrayList<jooon.features.dungeons.solvers.TeleportMazeSolver.Pad> = CopyOnWriteArrayList()

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register({ it: MinecraftClient ->
         if (Config.teleportMazeSolver && Utils.INSTANCE.inDungeon) {
            INSTANCE.tick()
         } else {
            if (inMaze) {
               INSTANCE.reset()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ ctx: WorldRenderContext ->
         if (Config.teleportMazeSolver && Utils.INSTANCE.inDungeon && inMaze) {
            INSTANCE.render(ctx)
         }
      })
   }

   private fun reset() {
      inMaze = false
      pads.clear()
   }

   private fun tick() {
      val currentRoom: DungeonRoom = DungeonScanner.INSTANCE.currentRoom
      if ((if (currentRoom != null) currentRoom.type else null) === RoomTypes.PUZZLE && currentRoom.name == "Teleport Maze") {
         if (!inMaze) {
            inMaze = true
            pads.clear()

            for (comp in endFramePositions) {
               var var10000: Pair = currentRoom.fromComp(comp.cx, comp.cz)
               if (var10000 != null) {
                  var10000 = currentRoom.fromComp(comp.tx, comp.tz)
                  if (var10000 != null) {
                     pads.add(
                        TeleportMazeSolver.Pad(
                           (var10000.getFirst() as java.lang.Number).intValue(),
                           (var10000.getSecond() as java.lang.Number).intValue(),
                           (var10000.getFirst() as java.lang.Number).intValue(),
                           (var10000.getSecond() as java.lang.Number).intValue(),
                           comp.special,
                           comp.isEnd
                        )
                     )
                  }
               }
            }
         }
      } else {
         if (inMaze) {
            this.reset()
         }
      }
   }

   fun onPositionPacket(packet: PlayerPositionLookS2CPacket) {
      this.getMc()
         .execute(
            { 
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
      }

   private fun render(ctx: WorldRenderContext) {
      val var10000: MatrixStack = ctx.matrices()
      if (var10000 != null) {
         val var20: Camera = ctx.gameRenderer().method_19418()
         val var21: Vec3d = var20.method_71156()
         val cameraPos: Vec3d = var21
         val var22: Entry = var10000.method_23760()
         val lastEntry: Entry = var22
         val var23: Matrix4f = var22.method_23761()
         val posMat: Matrix4f = var23
         val var24: VertexConsumerProvider = ctx.consumers()
         if (var24 != null) {
            val consumers: VertexConsumerProvider = var24

            for (`element$iv` in pads) {
               val it: TeleportMazeSolver.Pad = `element$iv` as TeleportMazeSolver.Pad
               val var25: Color
               if ((`element$iv` as TeleportMazeSolver.Pad).correct) {
                  var25 = Color.GREEN
               } else if (it.visited) {
                  var25 = Color.RED
               } else {
                  if (!it.possible) {
                     continue
                  }

                  var25 = Color.ORANGE
               }

               val box: Box = Box(it.x, 69.0, it.z, it.x + 1.0, 70.0, it.z + 1.0)
               RenderUtils.INSTANCE
                  .renderBoxFill(
                     consumers,
                     posMat,
                     lastEntry,
                     cameraPos,
                     box,
                     (float)var25.getRed() / 255.0F,
                     (float)var25.getGreen() / 255.0F,
                     (float)var25.getBlue() / 255.0F,
                     0.4F
                  )
                  if (Config.dungeonESPThroughWalls) {
                  val var26: MinecraftClient = INSTANCE.getMc()
                  val var27: OutlineVertexConsumerProvider = (var26 as MinecraftAccessor).getRenderBuffers().method_23003()
                  val var28: RenderUtils = RenderUtils.INSTANCE
                  var28.renderBoxFillThroughWalls(var27, posMat, lastEntry, cameraPos, box, var25)
               }
            }
         }
      }
   }

   private data class CompPad(cx: Int, cz: Int, tx: Int, tz: Int, special: Boolean = false, isEnd: Boolean = false) {
      public final val cx: Int
      public final val cz: Int
      public final val tx: Int
      public final val tz: Int
      public final val special: Boolean
      public final val isEnd: Boolean

      init {
         this.cx = cx
         this.cz = cz
         this.tx = tx
         this.tz = tz
         this.special = special
         this.isEnd = isEnd
      }

      public operator fun component1(): Int {
         return this.cx
      }

      public operator fun component2(): Int {
         return this.cz
      }

      public operator fun component3(): Int {
         return this.tx
      }

      public operator fun component4(): Int {
         return this.tz
      }

      public operator fun component5(): Boolean {
         return this.special
      }

      public operator fun component6(): Boolean {
         return this.isEnd
      }

      public fun copy(cx: Int = this.cx, cz: Int = this.cz, tx: Int = this.tx, tz: Int = this.tz, special: Boolean = this.special, isEnd: Boolean = this.isEnd): jooon.features.dungeons.solvers.TeleportMazeSolver.CompPad {
         return TeleportMazeSolver.CompPad(cx, cz, tx, tz, special, isEnd)
      }

      public override fun toString(): String {
         return "CompPad(cx=${this.cx}, cz=${this.cz}, tx=${this.tx}, tz=${this.tz}, special=${this.special}, isEnd=${this.isEnd})"
      }

      public override fun hashCode(): Int {
         return (
                  (((Integer.hashCode(this.cx) * 31 + Integer.hashCode(this.cz)) * 31 + Integer.hashCode(this.tx)) * 31 + Integer.hashCode(this.tz)) * 31
                     + java.lang.Boolean.hashCode(this.special)
               )
               * 31
            + java.lang.Boolean.hashCode(this.isEnd)
         }

      public override operator fun equals(other: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is TeleportMazeSolver.CompPad
               && this.cx == (other as TeleportMazeSolver.CompPad).cx
               && this.cz == (other as TeleportMazeSolver.CompPad).cz
               && this.tx == (other as TeleportMazeSolver.CompPad).tx
               && this.tz == (other as TeleportMazeSolver.CompPad).tz
               && this.special == (other as TeleportMazeSolver.CompPad).special
               && this.isEnd == (other as TeleportMazeSolver.CompPad).isEnd
            }
      }
   }

   private data class Pad(x: Int, z: Int, tx: Int, tz: Int, special: Boolean, isEnd: Boolean) {
      public final val x: Int
      public final val z: Int
      public final val tx: Int
      public final val tz: Int
      public final val special: Boolean
      public final val isEnd: Boolean
      public final var visited: Boolean
      public final var correct: Boolean
      public final var possible: Boolean
      public final var incorrect: Boolean

      init {
         this.x = x
         this.z = z
         this.tx = tx
         this.tz = tz
         this.special = special
         this.isEnd = isEnd
      }

      public operator fun component1(): Int {
         return this.x
      }

      public operator fun component2(): Int {
         return this.z
      }

      public operator fun component3(): Int {
         return this.tx
      }

      public operator fun component4(): Int {
         return this.tz
      }

      public operator fun component5(): Boolean {
         return this.special
      }

      public operator fun component6(): Boolean {
         return this.isEnd
      }

      public fun copy(x: Int = this.x, z: Int = this.z, tx: Int = this.tx, tz: Int = this.tz, special: Boolean = this.special, isEnd: Boolean = this.isEnd): jooon.features.dungeons.solvers.TeleportMazeSolver.Pad {
         return TeleportMazeSolver.Pad(x, z, tx, tz, special, isEnd)
      }

      public override fun toString(): String {
         return "Pad(x=${this.x}, z=${this.z}, tx=${this.tx}, tz=${this.tz}, special=${this.special}, isEnd=${this.isEnd})"
      }

      public override fun hashCode(): Int {
         return (
                  (((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.z)) * 31 + Integer.hashCode(this.tx)) * 31 + Integer.hashCode(this.tz)) * 31
                     + java.lang.Boolean.hashCode(this.special)
               )
               * 31
            + java.lang.Boolean.hashCode(this.isEnd)
         }

      public override operator fun equals(other: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is TeleportMazeSolver.Pad
               && this.x == (other as TeleportMazeSolver.Pad).x
               && this.z == (other as TeleportMazeSolver.Pad).z
               && this.tx == (other as TeleportMazeSolver.Pad).tx
               && this.tz == (other as TeleportMazeSolver.Pad).tz
               && this.special == (other as TeleportMazeSolver.Pad).special
               && this.isEnd == (other as TeleportMazeSolver.Pad).isEnd
            }
      }
   }

   private data class Vec2(u: Double, v: Double) {
      public final val u: Double
      public final val v: Double

      init {
         this.u = u
         this.v = v
      }

      public fun parallel(o: jooon.features.dungeons.solvers.TeleportMazeSolver.Vec2): Boolean {
         if (Math.abs(this.u) < 0.01) {
            return Math.abs(o.u) < 0.01 && this.sign == this.sign
         } else {
            return if (Math.abs(this.v) < 0.01)
               Math.abs(o.v) < 0.01 && this.sign == this.sign
               else
               Math.abs(this.u * o.v - this.v * o.u) < 0.01 && this.sign == this.sign
            }
      }

      private final val sign: Int
         private final get() {
            return if (`$this$sign` > 0.0) 1 else (if (`$this$sign` < 0.0) -1 else 0)
         }


      public operator fun component1(): Double {
         return this.u
      }

      public operator fun component2(): Double {
         return this.v
      }

      public fun copy(u: Double = this.u, v: Double = this.v): jooon.features.dungeons.solvers.TeleportMazeSolver.Vec2 {
         return TeleportMazeSolver.Vec2(u, v)
      }

      public override fun toString(): String {
         return "Vec2(u=${this.u}, v=${this.v})"
      }

      public override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.u) * 31 + java.lang.Double.hashCode(this.v)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is TeleportMazeSolver.Vec2
               && java.lang.Double.compare(this.u, (other as TeleportMazeSolver.Vec2).u) == 0
               && java.lang.Double.compare(this.v, (other as TeleportMazeSolver.Vec2).v) == 0
            }
      }
   }
}
