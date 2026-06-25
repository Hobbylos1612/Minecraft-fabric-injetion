package jooon.util

import java.awt.Color
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

object RenderUtils {
   
   private Identifier WHITE_TEX;

   fun getWHITE_TEX(): Identifier {
return WHITE_TEX
   }

   fun isOccluded(player: PlayerEntity, box: Box, world: World): Boolean {


         RaycastContext(
            var10000,
            Vec3d((box.minX + box.maxX) * 0.5, (box.minY + box.maxY) * 0.5, (box.minZ + box.maxZ) * 0.5),
            ShapeType.COLLIDER,
            FluidHandling.NONE,
            player as Entity
         )
      )
      var7.getType() === Type.BLOCK
   }

   fun getInterpolatedBox(e: Entity, tickDelta: Float): Box {

         .offset(
            MathHelper.lerp(tickDelta.toDouble(), e.lastX, e.getX()) - e.getX(),
            MathHelper.lerp(tickDelta.toDouble(), e.lastY, e.getY()) - e.getY(),
            MathHelper.lerp(tickDelta.toDouble(), e.lastZ, e.getZ()) - e.getZ()
         )
return var10000
   }

   fun calculateSmoothAlpha(d: Double, s: Double, m: Double): Float {
      label16@
      if (d <= s) {
         return 1.0F
      } else {
         return if (d >= m) 0.0F else (1.0F - ((d - s) / (m - s)).toFloat()).coerceIn(0.1F, 1.0F)
      }
   }

   fun applyAlpha(c: Color, a: Float): Color {
      return Color(c.getRed(), c.getGreen(), c.getBlue(), ((c.getAlpha().toFloat() * a).toInt()).coerceIn(1, 255))
   }

   fun renderEdgeBoxes(consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, bbIn: Box, r: Float, g: Float, b: Float, a: Float, th: Double) {







      this.renderBox(
         consumer,
         mPos,
         entry,
         var10000.minX - th,
         var10000.minY,
         var10000.minZ - th,
         var10000.minX + th,
         var10000.maxY,
         var10000.minZ + th,
         r,
         g,
         b,
return a
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








      for (p in listOf(
         arrayOf(
            Pair(Vec3d(var10000.minX, var10000.minY, var10000.minZ), Vec3d(maxX, minY, minZ)),
            Pair(Vec3d(maxX, minY, minZ), Vec3d(maxX, minY, maxZ)),
            Pair(Vec3d(maxX, minY, maxZ), Vec3d(minX, minY, maxZ)),
            Pair(Vec3d(minX, minY, maxZ), Vec3d(minX, minY, minZ)),
            Pair(Vec3d(minX, maxY, minZ), Vec3d(maxX, maxY, minZ)),
            Pair(Vec3d(maxX, maxY, minZ), Vec3d(maxX, maxY, maxZ)),
            Pair(Vec3d(maxX, maxY, maxZ), Vec3d(minX, maxY, maxZ)),
            Pair(Vec3d(minX, maxY, maxZ), Vec3d(minX, maxY, minZ)),
            Pair(Vec3d(minX, minY, minZ), Vec3d(minX, maxY, minZ)),
            Pair(Vec3d(maxX, minY, minZ), Vec3d(maxX, maxY, minZ)),
            Pair(Vec3d(maxX, minY, maxZ), Vec3d(maxX, maxY, maxZ)),
            Pair(Vec3d(minX, minY, maxZ), Vec3d(minX, maxY, maxZ))
         )
      )) {

         this.renderLine(consumer, mPos, entry, var10004, p.getFirst() as Vec3d, p.getSecond() as Vec3d, r, g, b, a, thickness)
      }
   }

   fun renderLine(
      consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, start: Vec3d, end: Vec3d, r: Float, g: Float, b: Float, a: Float, thickness: Float
   ) {
      var var10000: Vec3d = start.subtract(cam)

      var10000 = var32.subtract(var10000)
      if (var10000.lengthSquared() < 1.0E-6) {
         var10000 = Vec3d(0.0, 1.0, 0.0)
      } else {
         var10000 = var10000.normalize()
      }

      if (x1.lengthSquared() < 1.0E-6) {
         var10000 = Vec3d(0.0, 0.0, 1.0)
      } else {
         var10000 = x1.normalize()
      }

      this.emitQuadColored(
         consumer,
         mPos,
         entry,
         (var10000.x + var36.x).toFloat(),
         (var10000.y + var36.y).toFloat(),
         (var10000.z + var36.z).toFloat(),
         (var10000.x - var36.x).toFloat(),
         (var10000.y - var36.y).toFloat(),
         (var10000.z - var36.z).toFloat(),
         (var32.x - var36.x).toFloat(),
         (var32.y - var36.y).toFloat(),
         (var32.z - var36.z).toFloat(),
         (var32.x + var36.x).toFloat(),
         (var32.y + var36.y).toFloat(),
         (var32.z + var36.z).toFloat(),
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
return a
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

      this.renderLine(var10000, mPos, entry, cam, start, end, r, g, b, a, thickness)
   }

   fun renderBoxFill(consumers: VertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, r: Float, g: Float, b: Float, a: Float) {

      this.renderFilledAabb(var10000, mPos, entry, cam, box, r, g, b, a)
   }

   fun renderBoxOutlineRobust(
      consumers: VertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, r: Float, g: Float, b: Float, a: Float, th: Float
   ) {

      this.renderBoxOutline(var10000, mPos, entry, cam, box, r, g, b, a, th)
   }

   fun renderBoxFillThroughWalls(outlines: OutlineVertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, color: Color) {
      outlines.setColor(color.getRed() shl 16 or color.getGreen() shl 8 or color.getBlue())

      this.renderFilledAabb(var10000, mPos, entry, cam, box, 1.0F, 1.0F, 1.0F, 1.0F)
   }

   fun renderBoxOutlineThroughWalls(outlines: OutlineVertexConsumerProvider, mPos: Matrix4f, entry: Entry, cam: Vec3d, box: Box, color: Color, th: Float) {
      outlines.setColor(color.getRed() shl 16 or color.getGreen() shl 8 or color.getBlue())

      this.renderBoxOutline(var10000, mPos, entry, cam, box, 1.0F, 1.0F, 1.0F, 1.0F, th)
   }

   fun renderFilledAabb(consumer: VertexConsumer, mPos: Matrix4f, entry: Entry, cam: Vec3d, bbIn: Box, r: Float, g: Float, b: Float, a: Float) {







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
      c.vertex(mPos as Matrix4fc, x1, y1, z1)
         .color(r, g, b, a)
         .texture(u1, v1)
         .overlay(655360)
         .light(15728880)
         .normal(entry, nx, ny, nz)
         c.vertex(mPos as Matrix4fc, x2, y2, z2)
         .color(r, g, b, a)
         .texture(u2, v2)
         .overlay(655360)
         .light(15728880)
         .normal(entry, nx, ny, nz)
         c.vertex(mPos as Matrix4fc, x3, y3, z3)
         .color(r, g, b, a)
         .texture(u3, v3)
         .overlay(655360)
         .light(15728880)
         .normal(entry, nx, ny, nz)
         c.vertex(mPos as Matrix4fc, x4, y4, z4)
         .color(r, g, b, a)
         .texture(u4, v4)
         .overlay(655360)
         .light(15728880)
         .normal(entry, nx, ny, nz)
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
      c.vertex(mPos as Matrix4fc, x1, y1, z1).texture(u1, v1).color(1.0F, 1.0F, 1.0F, 1.0F).normal(entry, nx, ny, nz)
      c.vertex(mPos as Matrix4fc, x2, y2, z2).texture(u2, v2).color(1.0F, 1.0F, 1.0F, 1.0F).normal(entry, nx, ny, nz)
      c.vertex(mPos as Matrix4fc, x3, y3, z3).texture(u3, v3).color(1.0F, 1.0F, 1.0F, 1.0F).normal(entry, nx, ny, nz)
      c.vertex(mPos as Matrix4fc, x4, y4, z4).texture(u4, v4).color(1.0F, 1.0F, 1.0F, 1.0F).normal(entry, nx, ny, nz)
   }

   fun renderText(
      consumers: VertexConsumerProvider,
      matrices: MatrixStack,
      text: String,
      x: Double,
      y: Double,
      z: Double,
      color: Int,
      camera: Camera,
      seeThrough: Boolean
   ) {


      matrices.push()
      matrices.translate(x - var18.x, y - var18.y, z - var18.z)
      matrices.multiply(camera.getRotation() as Quaternionfc)
      matrices.scale(-0.025F, -0.025F, 0.025F)



      var10000.textRenderer
         .draw(text, (-var10000.textRenderer.getWidth(text)).toFloat() / 2.0F, 0.0F, finalColor, false, var19, consumers, mode, 0, 15728880)
         matrices.pop()
   }

   fun parseColor(hex: String): Color {

      return Color(var10002 ?: 16777215)
   }

   
   fun {

      WHITE_TEX = var10000
   }
}
