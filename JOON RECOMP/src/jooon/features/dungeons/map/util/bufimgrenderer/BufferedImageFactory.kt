package jooon.features.dungeons.map.util.bufimgrenderer

import java.awt.image.BufferedImage

public interface BufferedImageFactory {
   public abstract fun create(w: Int, h: Int): BufferedImage {
   }
}
