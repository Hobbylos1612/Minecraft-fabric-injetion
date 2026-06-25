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
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.texture.GlTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL15

public class BufferedImageUploader(name: String) : AbstractTexture {
   public final val name: String
   public final var w: Int
   public final var h: Int
   public final var texId: Int
   public final var pboId: Int

   private final val bimgFactory: BufferedImageFactoryImpl
      private final get() {
         return this.bimgFactory$delegate.getValue() as BufferedImageFactoryImpl
      }


   init {
      this.name = name
      this.texId = -1
      this.pboId = -1
      this.bimgFactory$delegate = LazyKt.lazy({ 
         BufferedImageFactoryImpl()
      })
   }

   private fun create(img: BufferedImage) {
      this.w = img.getWidth()
      this.h = img.getHeight()
      this.field_56974 = RenderSystem.getDevice().createTexture(this.name, 0, TextureFormat.RGBA8, this.w, this.h, 1, 1)
      val var10001: GpuDevice = RenderSystem.getDevice()
      val var10002: GpuTexture = this.field_56974
      this.field_60597 = var10001.createTextureView(var10002, 0, 1)
      val var2: GpuTexture = this.field_56974
      this.texId = (var2 as GlTexture).method_68427()
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
      GlStateManager._glBufferData(35052, (long)(this.w * this.h) * 4L, 35040)
      GlStateManager._glBindBuffer(35052, 0)
   }

   private fun uploadImpl(img: BufferedImage) {
      val w: Int = img.getWidth()
      val h: Int = img.getHeight()
      var imgx: BufferedImage = img
      if (!Companion.isRGBAByteInterleaved(img)) {
         val pixels: BufferedImage = this.bimgFactory.create(w, h)
         val buf: Graphics2D = pixels.createGraphics()
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
      val var10000: DataBuffer = imgx.getRaster().getDataBuffer()
      val var7: ByteArray = (var10000 as DataBufferByte).getData()
      GlStateManager._glBindBuffer(35052, this.pboId)
      val var8: ByteBuffer = GL15.glMapBuffer(35052, 35001, (long)(w * h) * 4L, null)
      if (var8 != null) {
         var8.put(var7)
         GL15.glUnmapBuffer(35052)
      }

      GlStateManager._texSubImage2D(3553, 0, 0, 0, w, h, 6408, 5121, 0L)
      GlStateManager._glBindBuffer(35052, 0)
   }

   public fun upload(img: BufferedImage) {
      if (RenderSystem.tryGetDevice() == null) {
         MinecraftClient.method_1551().execute({ 
            `this$0`.uploadImpl(`$img`)
         })
      } else {
         this.uploadImpl(img)
      }
   }

   fun register(mcid: Identifier): BufferedImageUploader {
      val `$this$register_u24lambda_u242`: BufferedImageUploader = this
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000 != null) {
         val var6: TextureManager = var10000.method_1531()
         if (var6 != null) {
            var6.method_4616(mcid, `$this$register_u24lambda_u242`)
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

         if (this.field_56974 != null) {
            this.field_56974.close()
         }

         this.pboId = -1
         this.texId = this.pboId
      }
   }

   @SourceDebugExtension(["SMAP\nBufferedImageUploader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BufferedImageUploader.kt\njooon/features/dungeons/map/util/bufimgrenderer/BufferedImageUploader$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,146:1\n1726#2,3:147\n*S KotlinDebug\n*F\n+ 1 BufferedImageUploader.kt\njooon/features/dungeons/map/util/bufimgrenderer/BufferedImageUploader$Companion\n*L\n142#1:147,3\n*E\n"])
   public companion object {
      private fun getImg(path: String): BufferedImage? {
         val var10000: InputStream = this.getClass().getResourceAsStream(path)
         return if (var10000 == null) null else ImageIO.read(var10000)
      }

      public fun fromResource(path: String): BufferedImageUploader? {
         val var10000: BufferedImage = this.getImg(path)
         val var7: BufferedImageUploader
         if (var10000 != null) {
            val var4: BufferedImageUploader = BufferedImageUploader(path)
            var4.upload(var10000)
            var7 = var4
         } else {
            var7 = null
         }

         return var7
      }

      private fun isRGBAByteInterleaved(img: BufferedImage): Boolean {
         val raster: WritableRaster = img.getRaster()
         if (raster.getDataBuffer() !is DataBufferByte) {
            return false
         } else {
            val sm: SampleModel = raster.getSampleModel()
            if (sm !is PixelInterleavedSampleModel) {
               return false
            } else {
               val var10000: IntArray = (sm as PixelInterleavedSampleModel).getBandOffsets()
               val `$this$all$iv`: java.lang.Iterable = ArraysKt.withIndex(var10000)
               val var11: Boolean
               if (`$this$all$iv` is java.util.Collection && (`$this$all$iv` as java.util.Collection).isEmpty()) {
                  var11 = true
               } else {
                  for (`element$iv` in `$this$all$iv`) {
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
