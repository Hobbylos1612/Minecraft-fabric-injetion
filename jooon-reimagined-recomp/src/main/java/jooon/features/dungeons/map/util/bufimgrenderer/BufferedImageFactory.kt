package jooon.features.dungeons.map.util.bufimgrenderer

import java.awt.image.BufferedImage

interface BufferedImageFactory {
   abstract fun create(w: Int, h: Int): BufferedImage {
   }
}
