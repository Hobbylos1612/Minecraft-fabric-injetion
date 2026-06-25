package jooon.features.dungeons.map.util.bufimgrenderer

import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.Raster
import java.awt.image.WritableRaster

class BufferedImageFactoryImpl : BufferedImageFactory {
   override fun create(w: Int, h: Int): BufferedImage {
      return BufferedImage(COLOR_MODEL, Raster.createInterleavedRaster(0, w, h, w * 4, 4, intArrayOf(0, 1, 2, 3), null), false, null)
   }

   companion object {
      val COLOR_MODEL: ComponentColorModel
      val BLANK_RASTER: WritableRaster
      val BLANK_IMAGE: BufferedImage
   }
}
