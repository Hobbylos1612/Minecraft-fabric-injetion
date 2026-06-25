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

public abstract class BufferedImageRenderer<T> {
   public final val name: String
   protected final val uploader: BufferedImageUploader
   protected final val dirtyImage: AtomicRef<BufferedImage?>
   protected final val bimgProvider: BufferedImageFactory
   protected final var running: Boolean
   protected final var waiting: Triple<Int, Int, Any>?
   protected final var lastFuture: Future<*>?
   private Identifier mcid;
   protected final var valid: Boolean

   open fun BufferedImageRenderer(name: java.lang.String) {
      this.name = name
      this.uploader = BufferedImageUploader(this.name)
      this.dirtyImage = AtomicFU.atomic(null)
      this.bimgProvider = BufferedImageFactoryImpl()
      val var10001: java.lang.String = this.name.toLowerCase(Locale.ROOT)
      val var2: Identifier = Identifier.method_60654("jooonreimagined:buffered_image/$var10001")
      this.mcid = var2
      this.valid = true
      this.uploader.register(this.mcid)
   }

   fun getMcid(): Identifier {
      this.mcid
   }

   protected abstract fun drawImage(img: BufferedImage, param: Any): BufferedImage {
   }

   public fun update(w: Int, h: Int, param: Any) {
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
      val bimg: BufferedImage = this.dirtyImage.getAndSet(null) as BufferedImage
      if (bimg != null) {
         this.uploader.upload(bimg)
         this.valid = true
      }
   }

   public fun invalidate() {
      this.valid = false
      if (this.lastFuture != null) {
         this.lastFuture.cancel(true)
      }
   }

   fun draw(ctx: DrawContext, x: Float, y: Float, scale: Float) {
      this.uploadImage()
      this.draw(ctx, x, y, (float)this.uploader.w * scale, (float)this.uploader.h * scale)
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
               MapRenderUtils.INSTANCE, ctx, this.mcid, x + w / (float)2, y + h / (float)2, w, h, 0.0F, 0.0F, 1.0F, 1.0F, (int)w, (int)h, 0.0F, 4096, null
            )
            GlStateManager._disableBlend()
         }
      }
   }

   public fun dispose() {
      try {
         val var10000: GpuTexture = this.uploader.method_68004()
         if (var10000 != null) {
            var10000.close()
         }
      } catch (var2: IllegalStateException) {
      }
   }

   @JvmStatic
   fun {
      val var10000: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
      pool = var10000
   }

   public companion object {
      public final val pool: ExecutorService
   }
}
