package jooon.features.dungeons.map.util.bufimgrenderer

import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.Raster
import java.awt.image.WritableRaster

public class BufferedImageFactoryImpl : BufferedImageFactory {
   public override fun create(w: Int, h: Int): BufferedImage {
      return BufferedImage(COLOR_MODEL, Raster.createInterleavedRaster(0, w, h, w * 4, 4, intArrayOf(0, 1, 2, 3), null), false, null)
   }

   public companion object {
      public final val COLOR_MODEL: ComponentColorModel
      public final val BLANK_RASTER: WritableRaster
      public final val BLANK_IMAGE: BufferedImage
   }
}
