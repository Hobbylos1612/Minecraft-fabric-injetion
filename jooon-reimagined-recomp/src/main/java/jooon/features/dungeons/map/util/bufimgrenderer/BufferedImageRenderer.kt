package jooon.features.dungeons.map.util.bufimgrenderer

import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.textures.GpuTexture
import java.awt.image.BufferedImage
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import jooon.features.dungeons.map.util.MapRenderUtils
import kotlinx.atomicfu.AtomicFU
import kotlinx.atomicfu.AtomicRef
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

abstract class BufferedImageRenderer<T> {
   val name: String
   protected val uploader: BufferedImageUploader
   protected val dirtyImage: AtomicRef<BufferedImage?>
   protected val bimgProvider: BufferedImageFactory
   protected var running: Boolean
   protected var waiting: Triple<Int, Int, Any>?
   protected var lastFuture: Future<*>?
   private Identifier mcid;
   protected var valid: Boolean

   constructor(name: String) {
      this.name = name
      this.uploader = BufferedImageUploader(this.name)
      this.dirtyImage = AtomicFU.atomic(null)
      this.bimgProvider = BufferedImageFactoryImpl()


      this.mcid = var2
      this.valid = true
      this.uploader.register(this.mcid)
   }

   fun getMcid(): Identifier {
      this.mcid
   }

   protected abstract fun drawImage(img: BufferedImage, param: Any): BufferedImage {
   }

   fun update(w: Int, h: Int, param: Any) {
      if (this.running) {
         this.waiting = Triple(w, h, param)
      } else {
         this.running = true
         this.lastFuture = pool.submit(
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
   }

   private fun uploadImage() {

      if (bimg != null) {
         this.uploader.upload(bimg)
         this.valid = true
      }
   }

   fun invalidate() {
      this.valid = false
      if (this.lastFuture != null) {
         this.lastFuture.cancel(true)
      }
   }

   fun draw(ctx: DrawContext, x: Float, y: Float, scale: Float) {
      this.uploadImage()
      this.draw(ctx, x, y, this.uploader.w.toFloat() * scale, this.uploader.h.toFloat() * scale)
   }

   fun drawStretched(ctx: DrawContext, x: Float, y: Float, w: Float, h: Float) {
      this.uploadImage()
      this.draw(ctx, x, y, w, h)
   }

   fun draw(ctx: DrawContext, x: Float, y: Float, w: Float, h: Float) {
      if (this.uploader.texId != -1) {
         if (this.valid) {
            GlStateManager._activeTexture(33984)
            GlStateManager._bindTexture(this.uploader.texId)
            GlStateManager._enableBlend()
            MapRenderUtils.drawRotatedQuad$default(
               MapRenderUtils.INSTANCE, ctx, this.mcid, x + w / 2.toFloat(), y + h / 2.toFloat(), w, h, 0.0F, 0.0F, 1.0F, 1.0F, w.toInt(), h.toInt(), 0.0F, 4096, null
            )
            GlStateManager._disableBlend()
         }
      }
   }

   fun dispose() {
      try {

         if (var10000 != null) {
            var10000.close()
         }
      } catch (var2: IllegalStateException) {
      }
   }

   
   fun {

      pool = var10000
   }

   companion object {
      val pool: ExecutorService
   }
}
