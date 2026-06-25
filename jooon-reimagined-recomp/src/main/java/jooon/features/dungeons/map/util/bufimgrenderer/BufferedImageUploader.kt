package jooon.features.dungeons.map.util.bufimgrenderer

import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.systems.GpuDevice
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.TextureFormat
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.SampleModel
import java.awt.image.WritableRaster
import java.io.InputStream
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.texture.GlTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL15

class BufferedImageUploader(name: String) : AbstractTexture {
   val name: String
   var w: Int
   var h: Int
   var texId: Int
   var pboId: Int

   private val bimgFactory: BufferedImageFactoryImpl
      private get() {
         return this.bimgFactory$delegate.getValue() as BufferedImageFactoryImpl
      }


   init {
      this.name = name
      this.texId = -1
      this.pboId = -1
      this.bimgFactory$delegate = lazy({ 
         BufferedImageFactoryImpl()
      })
   }

   private fun create(img: BufferedImage) {
      this.w = img.getWidth()
      this.h = img.getHeight()
      this.glTexture = RenderSystem.getDevice().createTexture(this.name, 0, TextureFormat.RGBA8, this.w, this.h, 1, 1)


      this.glTextureView = var10001.createTextureView(var10002, 0, 1)

      this.texId = (var2 as GlTexture).getGlId()
      GlStateManager._bindTexture(this.texId)
      GlStateManager._texImage2D(3553, 0, 6408, this.w, this.h, 0, 6408, 5121, null)
      GlStateManager._texParameter(3553, 10241, 9728)
      GlStateManager._texParameter(3553, 10240, 9728)
      GlStateManager._texParameter(3553, 10242, 33071)
      GlStateManager._texParameter(3553, 10243, 33071)
      GlStateManager._texParameter(3553, 33085, 0)
      GlStateManager._texParameter(3553, 33082, 0)
      GlStateManager._texParameter(3553, 33083, 0)
      GlStateManager._texParameter(3553, 34049, 0)
      this.pboId = GlStateManager._glGenBuffers()
      GlStateManager._glBindBuffer(35052, this.pboId)
      GlStateManager._glBufferData(35052, (this.w * this.h).toLong() * 4L, 35040)
      GlStateManager._glBindBuffer(35052, 0)
   }

   private fun uploadImpl(img: BufferedImage) {


      var imgx: BufferedImage = img
      if (!Companion.isRGBAByteInterleaved(img)) {


         buf.drawImage(img, 0, 0, null)
         buf.dispose()
         imgx = pixels
      }

      if (this.texId == -1) {
         this.create(imgx)
      } else if (w != this.w || h != this.h) {
         this.destroy()
         this.create(imgx)
      }

      GlStateManager._bindTexture(this.texId)
      GlStateManager._pixelStore(3314, w)
      GlStateManager._pixelStore(3316, 0)
      GlStateManager._pixelStore(3315, 0)
      GlStateManager._pixelStore(3317, 4)


      GlStateManager._glBindBuffer(35052, this.pboId)

      if (var8 != null) {
         var8.put(var7)
         GL15.glUnmapBuffer(35052)
      }

      GlStateManager._texSubImage2D(3553, 0, 0, 0, w, h, 6408, 5121, 0L)
      GlStateManager._glBindBuffer(35052, 0)
   }

   fun upload(img: BufferedImage) {
      if (RenderSystem.tryGetDevice() == null) {
         MinecraftClient.getInstance().execute({ 
            `this$0`.uploadImpl(`$img`)
         })
      } else {
         this.uploadImpl(img)
      }
   }

   fun register(mcid: Identifier): BufferedImageUploader {
      val `this24lambda_u242`: BufferedImageUploader = this

      if (var10000 != null) {

         if (var6 != null) {
            var6.registerTexture(mcid, `this24lambda_u242`)
         }
      }

      this as BufferedImageUploader
   }

   private fun destroy() {
      if (this.texId != -1) {
         GlStateManager._deleteTexture(this.texId)
         if (this.pboId != -1) {
            GlStateManager._glDeleteBuffers(this.pboId)
         }

         if (this.glTexture != null) {
            this.glTexture.close()
         }

         this.pboId = -1
         this.texId = this.pboId
      }
   }

   @SourceDebugExtension(["SMAP\nBufferedImageUploader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BufferedImageUploader.kt\njooon/features/dungeons/map/util/bufimgrenderer/BufferedImageUploader$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,146:1\n1726#2,3:147\n*S KotlinDebug\n*F\n+ 1 BufferedImageUploader.kt\njooon/features/dungeons/map/util/bufimgrenderer/BufferedImageUploader$Companion\n*L\n142#1:147,3\n*E\n"])
   companion object {
      private fun getImg(path: String): BufferedImage? {

         return if (var10000 == null) null else ImageIO.read(var10000)
      }

      fun fromResource(path: String): BufferedImageUploader? {

         val var7: BufferedImageUploader
         if (var10000 != null) {

            var4.upload(var10000)
            var7 = var4
         } else {
            var7 = null
         }

         return var7
      }

      private fun isRGBAByteInterleaved(img: BufferedImage): Boolean {

         if (raster.getDataBuffer() !is DataBufferByte) {
            return false
         } else {

            if (sm !is PixelInterleavedSampleModel) {
               return false
            } else {

               val `this$iv`: java.lang.Iterable = withIndex(var10000)
               val var11: Boolean
               if (`this$iv` is java.util.Collection && (`this$iv` as java.util.Collection).isEmpty()) {
                  var11 = true
               } else {
                  for (`element$iv` in `this$iv`) {
                     if ((`element$iv` as IndexedValue).getIndex() != ((`element$iv` as IndexedValue).getValue() as java.lang.Number).intValue()) {
                        return false
                     }
                  }

                  var11 = true
               }

               return var11
            }
         }
      }
   }
}
