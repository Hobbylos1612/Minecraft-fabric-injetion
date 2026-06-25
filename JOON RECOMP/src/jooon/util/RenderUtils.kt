package jooon.util

import java.awt.Color
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult.Type
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.RaycastContext.ShapeType
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Quaternionfc

@SourceDebugExtension(["SMAP\nRenderUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RenderUtils.kt\njooon/util/RenderUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,354:1\n1#2:355\n*E\n"])
public object RenderUtils {
   @JvmStatic
   private Identifier WHITE_TEX;

   fun getWHITE_TEX(): Identifier {
      WHITE_TEX
   }

   fun isOccluded(player: PlayerEntity, box: Box, world: World): Boolean {
      val var10000: Vec3d = player.method_33571()
      val var7: BlockHitResult = world.method_17742(
         RaycastContext(
            var10000,
            Vec3d((box.field_1323 + box.field_1320) * 0.5, (box.field_1322 + box.field_1325) * 0.5, (box.field_1321 + box.field_1324) * 0.5),
            ShapeType.field_17558,
            FluidHandling.field_1348,
            player as Entity
         )
      )
      var7.method_17783() === Type.field_1332
   }

   fun getInterpolatedBox(e: Entity, tickDelta: Float): Box {
      val var10000: Box = e.method_5829()
         .method_989(
            MathHelper.method_16436((double)tickDelta, e.field_6014, e.method_23317()) - e.method_23317(),
            MathHelper.method_16436((double)tickDelta, e.field_6036, e.method_23318()) - e.method_23318(),
            MathHelper.method_16436((double)tickDelta, e.field_5969, e.method_23321()) - e.method_23321()
         )
         var10000
   }

   public fun calculateSmoothAlpha(d: Double, s: Double, m: Double): Float {
      label16@
      if (d <= s) {
         return 1.0F
      } else {
         return if (d >= m) 0.0F else RangesKt.coerceIn(1.0F - (float)((d - s) / (m - s)), 0.1F, 1.0F)
      }
   }

   public fun applyAlpha(c: Color, a: Float): Color {
      return Color(c.getRed(), c.getGreen(), c.getBlue(), RangesKt.coerceIn((int)((float)c.getAlpha() * a), 1, 255))
   }

   fun renderEdgeBoxes(consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, bbIn: Box, r: Float, g: Float, b: Float, a: Float, th: Double) {
      val var10000: Box = bbIn.method_989(-cam.field_1352, -cam.field_1351, -cam.field_1350).method_1014(0.1)
      val minX: Double = var10000.field_1323
      val minY: Double = var10000.field_1322
      val minZ: Double = var10000.field_1321
      val maxX: Double = var10000.field_1320
      val maxY: Double = var10000.field_1325
      val maxZ: Double = var10000.field_1324
      this.renderBox(
         consumer,
         mPos,
         entry,
         var10000.field_1323 - th,
         var10000.field_1322,
         var10000.field_1321 - th,
         var10000.field_1323 + th,
         var10000.field_1325,
         var10000.field_1321 + th,
         r,
         g,
         b,
         a
      )
      this.renderBox(consumer, mPos, entry, maxX - th, minY, minZ - th, maxX + th, maxY, minZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX - th, minY, maxZ - th, minX + th, maxY, maxZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, maxX - th, minY, maxZ - th, maxX + th, maxY, maxZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX + th, maxY - th, minZ - th, maxX - th, maxY + th, minZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX + th, maxY - th, maxZ - th, maxX - th, maxY + th, maxZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX - th, maxY - th, minZ + th, minX + th, maxY + th, maxZ - th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, maxX - th, maxY - th, minZ + th, maxX + th, maxY + th, maxZ - th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX + th, minY - th, minZ - th, maxX - th, minY + th, minZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX + th, minY - th, maxZ - th, maxX - th, minY + th, maxZ + th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, minX - th, minY - th, minZ + th, minX + th, minY + th, maxZ - th, r, g, b, a)
      this.renderBox(consumer, mPos, entry, maxX - th, minY - th, minZ + th, maxX + th, minY + th, maxZ - th, r, g, b, a)
   }

   fun renderBox(
      consumer: VertexConsumer,
      mPos: Matrix4f,
      entry: Entry,
      x1: Double,
      y1: Double,
      z1: Double,
      x2: Double,
      y2: Double,
      z2: Double,
      r: Float,
      g: Float,
      b: Float,
      a: Float
   ) {
      val X1: Float = (float)x1
      val Y1: Float = (float)y1
      val Z1: Float = (float)z1
      val X2: Float = (float)x2
      val Y2: Float = (float)y2
      val Z2: Float = (float)z2
      this.emitQuadColored(
         consumer, mPos, entry, X1, Y1, Z1, X2, Y1, Z1, X2, Y2, Z1, X1, Y2, Z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, X2, Y1, Z2, X1, Y1, Z2, X1, Y2, Z2, X2, Y2, Z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, X1, Y1, Z2, X1, Y1, Z1, X1, Y2, Z1, X1, Y2, Z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, X2, Y1, Z1, X2, Y1, Z2, X2, Y2, Z2, X2, Y2, Z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, X1, Y2, Z1, X2, Y2, Z1, X2, Y2, Z2, X1, Y2, Z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, X1, Y1, Z2, X2, Y1, Z2, X2, Y1, Z1, X1, Y1, Z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, r, g, b, a
      )
   }

   fun renderBoxOutline(consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, bbIn: Box, r: Float, g: Float, b: Float, a: Float, thickness: Float) {
      val var10000: Box = bbIn.method_989(-cam.field_1352, -cam.field_1351, -cam.field_1350).method_1014(0.005)
      val minX: Double = var10000.field_1323
      val minY: Double = var10000.field_1322
      val minZ: Double = var10000.field_1321
      val maxX: Double = var10000.field_1320
      val maxY: Double = var10000.field_1325
      val maxZ: Double = var10000.field_1324

      for (p in CollectionsKt.listOf(
         arrayOf(
            TuplesKt.to(Vec3d(var10000.field_1323, var10000.field_1322, var10000.field_1321), Vec3d(maxX, minY, minZ)),
            TuplesKt.to(Vec3d(maxX, minY, minZ), Vec3d(maxX, minY, maxZ)),
            TuplesKt.to(Vec3d(maxX, minY, maxZ), Vec3d(minX, minY, maxZ)),
            TuplesKt.to(Vec3d(minX, minY, maxZ), Vec3d(minX, minY, minZ)),
            TuplesKt.to(Vec3d(minX, maxY, minZ), Vec3d(maxX, maxY, minZ)),
            TuplesKt.to(Vec3d(maxX, maxY, minZ), Vec3d(maxX, maxY, maxZ)),
            TuplesKt.to(Vec3d(maxX, maxY, maxZ), Vec3d(minX, maxY, maxZ)),
            TuplesKt.to(Vec3d(minX, maxY, maxZ), Vec3d(minX, maxY, minZ)),
            TuplesKt.to(Vec3d(minX, minY, minZ), Vec3d(minX, maxY, minZ)),
            TuplesKt.to(Vec3d(maxX, minY, minZ), Vec3d(maxX, maxY, minZ)),
            TuplesKt.to(Vec3d(maxX, minY, maxZ), Vec3d(maxX, maxY, maxZ)),
            TuplesKt.to(Vec3d(minX, minY, maxZ), Vec3d(minX, maxY, maxZ))
         )
      )) {
         val var10004: Vec3d = Vec3d.field_1353
         this.renderLine(consumer, mPos, entry, var10004, p.getFirst() as Vec3d, p.getSecond() as Vec3d, r, g, b, a, thickness)
      }
   }

   fun renderLine(
      consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, start: Vec3d, end: Vec3d, r: Float, g: Float, b: Float, a: Float, thickness: Float
   ) {
      var var10000: Vec3d = start.method_1020(cam)
      val var32: Vec3d = end.method_1020(cam)
      var10000 = var32.method_1020(var10000)
      if (var10000.method_1027() < 1.0E-6) {
         var10000 = Vec3d(0.0, 1.0, 0.0)
      } else {
         var10000 = var10000.method_1029()
      }

      val x1: Vec3d = var10000.method_1019(var32).method_1021(0.5).method_22882()
      if (x1.method_1027() < 1.0E-6) {
         var10000 = Vec3d(0.0, 0.0, 1.0)
      } else {
         var10000 = x1.method_1029()
      }

      val var36: Vec3d = var10000.method_1036(var10000).method_1029().method_1021((double)thickness / 2.0)
      this.emitQuadColored(
         consumer,
         mPos,
         entry,
         (float)(var10000.field_1352 + var36.field_1352),
         (float)(var10000.field_1351 + var36.field_1351),
         (float)(var10000.field_1350 + var36.field_1350),
         (float)(var10000.field_1352 - var36.field_1352),
         (float)(var10000.field_1351 - var36.field_1351),
         (float)(var10000.field_1350 - var36.field_1350),
         (float)(var32.field_1352 - var36.field_1352),
         (float)(var32.field_1351 - var36.field_1351),
         (float)(var32.field_1350 - var36.field_1350),
         (float)(var32.field_1352 + var36.field_1352),
         (float)(var32.field_1351 + var36.field_1351),
         (float)(var32.field_1350 + var36.field_1350),
         0.0F,
         0.0F,
         1.0F,
         1.0F,
         0.0F,
         1.0F,
         1.0F,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         r,
         g,
         b,
         a
      )
   }

   fun renderLineRobust(
      consumers: VertexConsumerProvider,
      mPos: Matrix4f,
      entry: Entry,
      cam: Vec3d,
      start: Vec3d,
      end: Vec3d,
      r: Float,
      g: Float,
      b: Float,
      a: Float,
      thickness: Float
   ) {
      val var10000: VertexConsumer = consumers.method_73477(RenderLayers.method_76000(WHITE_TEX))
      this.renderLine(var10000, mPos, entry, cam, start, end, r, g, b, a, thickness)
   }

   fun renderBoxFill(consumers: VertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, r: Float, g: Float, b: Float, a: Float) {
      val var10000: VertexConsumer = consumers.method_73477(RenderLayers.method_76000(WHITE_TEX))
      this.renderFilledAabb(var10000, mPos, entry, cam, box, r, g, b, a)
   }

   fun renderBoxOutlineRobust(
      consumers: VertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, r: Float, g: Float, b: Float, a: Float, th: Float
   ) {
      val var10000: VertexConsumer = consumers.method_73477(RenderLayers.method_76000(WHITE_TEX))
      this.renderBoxOutline(var10000, mPos, entry, cam, box, r, g, b, a, th)
   }

   fun renderBoxFillThroughWalls(outlines: OutlineVertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, color: Color) {
      outlines.method_23286(color.getRed() shl 16 or color.getGreen() shl 8 or color.getBlue())
      val var10000: VertexConsumer = outlines.method_73477(RenderLayers.method_76018(WHITE_TEX))
      this.renderFilledAabb(var10000, mPos, entry, cam, box, 1.0F, 1.0F, 1.0F, 1.0F)
   }

   fun renderBoxOutlineThroughWalls(outlines: OutlineVertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, color: Color, th: Float) {
      outlines.method_23286(color.getRed() shl 16 or color.getGreen() shl 8 or color.getBlue())
      val var10000: VertexConsumer = outlines.method_73477(RenderLayers.method_76018(WHITE_TEX))
      this.renderBoxOutline(var10000, mPos, entry, cam, box, 1.0F, 1.0F, 1.0F, 1.0F, th)
   }

   fun renderFilledAabb(consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, bbIn: Box, r: Float, g: Float, b: Float, a: Float) {
      val var10000: Box = bbIn.method_989(0.0, 0.01, 0.0).method_1014(0.003).method_989(-cam.field_1352, -cam.field_1351, -cam.field_1350)
      val x1: Float = (float)var10000.field_1323
      val y1: Float = (float)var10000.field_1322
      val z1: Float = (float)var10000.field_1321
      val x2: Float = (float)var10000.field_1320
      val y2: Float = (float)var10000.field_1325
      val z2: Float = (float)var10000.field_1324
      this.emitQuadColored(
         consumer, mPos, entry, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, x1, y1, z1, x1, y1, z2, x2, y1, z2, x2, y1, z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, r, g, b, a
      )
      this.emitQuadColored(
         consumer, mPos, entry, x1, y2, z2, x1, y2, z1, x2, y2, z1, x2, y2, z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, r, g, b, a
      )
   }

   fun renderFilledAabb(consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, bbIn: Box) {
      val var10000: Box = bbIn.method_989(0.0, 0.01, 0.0).method_1014(0.003).method_989(-cam.field_1352, -cam.field_1351, -cam.field_1350)
      val x1: Float = (float)var10000.field_1323
      val y1: Float = (float)var10000.field_1322
      val z1: Float = (float)var10000.field_1321
      val x2: Float = (float)var10000.field_1320
      val y2: Float = (float)var10000.field_1325
      val z2: Float = (float)var10000.field_1324
      this.emitQuadWhite(
         consumer, mPos, entry, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F
      )
      this.emitQuadWhite(
         consumer, mPos, entry, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F
      )
      this.emitQuadWhite(
         consumer, mPos, entry, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F
      )
      this.emitQuadWhite(
         consumer, mPos, entry, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F
      )
      this.emitQuadWhite(
         consumer, mPos, entry, x1, y1, z1, x1, y1, z2, x2, y1, z2, x2, y1, z1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F
      )
      this.emitQuadWhite(
         consumer, mPos, entry, x1, y2, z2, x1, y2, z1, x2, y2, z1, x2, y2, z2, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F
      )
   }

   fun emitQuadColored(
      c: VertexConsumer,
      mPos: Matrix4f,
      entry: Entry,
      x1: Float,
      y1: Float,
      z1: Float,
      x2: Float,
      y2: Float,
      z2: Float,
      x3: Float,
      y3: Float,
      z3: Float,
      x4: Float,
      y4: Float,
      z4: Float,
      u1: Float,
      v1: Float,
      u2: Float,
      v2: Float,
      u3: Float,
      v3: Float,
      u4: Float,
      v4: Float,
      nx: Float,
      ny: Float,
      nz: Float,
      r: Float,
      g: Float,
      b: Float,
      a: Float
   ) {
      c.method_22918(mPos as Matrix4fc, x1, y1, z1)
         .method_22915(r, g, b, a)
         .method_22913(u1, v1)
         .method_22922(655360)
         .method_60803(15728880)
         .method_60831(entry, nx, ny, nz)
         c.method_22918(mPos as Matrix4fc, x2, y2, z2)
         .method_22915(r, g, b, a)
         .method_22913(u2, v2)
         .method_22922(655360)
         .method_60803(15728880)
         .method_60831(entry, nx, ny, nz)
         c.method_22918(mPos as Matrix4fc, x3, y3, z3)
         .method_22915(r, g, b, a)
         .method_22913(u3, v3)
         .method_22922(655360)
         .method_60803(15728880)
         .method_60831(entry, nx, ny, nz)
         c.method_22918(mPos as Matrix4fc, x4, y4, z4)
         .method_22915(r, g, b, a)
         .method_22913(u4, v4)
         .method_22922(655360)
         .method_60803(15728880)
         .method_60831(entry, nx, ny, nz)
      }

   fun emitQuadWhite(
      c: VertexConsumer,
      mPos: Matrix4f,
      entry: Entry,
      x1: Float,
      y1: Float,
      z1: Float,
      x2: Float,
      y2: Float,
      z2: Float,
      x3: Float,
      y3: Float,
      z3: Float,
      x4: Float,
      y4: Float,
      z4: Float,
      u1: Float,
      v1: Float,
      u2: Float,
      v2: Float,
      u3: Float,
      v3: Float,
      u4: Float,
      v4: Float,
      nx: Float,
      ny: Float,
      nz: Float
   ) {
      c.method_22918(mPos as Matrix4fc, x1, y1, z1).method_22913(u1, v1).method_22915(1.0F, 1.0F, 1.0F, 1.0F).method_60831(entry, nx, ny, nz)
      c.method_22918(mPos as Matrix4fc, x2, y2, z2).method_22913(u2, v2).method_22915(1.0F, 1.0F, 1.0F, 1.0F).method_60831(entry, nx, ny, nz)
      c.method_22918(mPos as Matrix4fc, x3, y3, z3).method_22913(u3, v3).method_22915(1.0F, 1.0F, 1.0F, 1.0F).method_60831(entry, nx, ny, nz)
      c.method_22918(mPos as Matrix4fc, x4, y4, z4).method_22913(u4, v4).method_22915(1.0F, 1.0F, 1.0F, 1.0F).method_60831(entry, nx, ny, nz)
   }

   fun renderText(
      consumers: VertexConsumerProvider,
      matrices: MatrixStack,
      text: java.lang.String,
      x: Double,
      y: Double,
      z: Double,
      color: Int,
      camera: Camera,
      seeThrough: Boolean
   ) {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      val var18: Vec3d = camera.method_71156()
      matrices.method_22903()
      matrices.method_22904(x - var18.field_1352, y - var18.field_1351, z - var18.field_1350)
      matrices.method_22907(camera.method_23767() as Quaternionfc)
      matrices.method_22905(-0.025F, -0.025F, 0.025F)
      val finalColor: Int = if (color == -1) -1 else color
      val var19: Matrix4f = matrices.method_23760().method_23761()
      val mode: TextLayerType = if (seeThrough) TextLayerType.field_33994 else TextLayerType.field_33993
      var10000.field_1772
         .method_27521(text, (float)(-var10000.field_1772.method_1727(text)) / 2.0F, 0.0F, finalColor, false, var19, consumers, mode, 0, 15728880)
         matrices.method_22909()
   }

   public fun parseColor(hex: String): Color {
      val var10002: Int = StringsKt.toIntOrNull(StringsKt.removePrefix(hex, "#"), 16)
      return Color(var10002 ?: 16777215)
   }

   @JvmStatic
   fun {
      val var10000: Identifier = Identifier.method_60655("jooonreimagined", "textures/misc/white.png")
      WHITE_TEX = var10000
   }
}
