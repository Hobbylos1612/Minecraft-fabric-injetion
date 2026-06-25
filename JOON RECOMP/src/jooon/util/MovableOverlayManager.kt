package jooon.util

import java.util.LinkedHashMap

public object MovableOverlayManager {
   private final val overlays: MutableMap<String, MovableOverlay> = LinkedHashMap() as java.util.Map

   public fun createOverlay(id: String, displayName: String, defaultX: Int, defaultY: Int, width: Int, height: Int): MovableOverlay {
      val overlay: MovableOverlay = MovableOverlay(id, displayName, defaultX, defaultY, width, height)
      overlays.put(id, overlay)
      return overlay
   }

   public fun getOverlay(id: String): MovableOverlay? {
      return overlays.get(id)
   }

   public fun removeOverlay(id: String) {
      val var10000: MovableOverlay = overlays.get(id)
      if (var10000 != null) {
         var10000.unregister()
      }

      overlays.remove(id)
   }

   public fun openPositioningGUI(id: String) {
      val var10000: MovableOverlay = overlays.get(id)
      if (var10000 != null) {
         var10000.openPositioningGUI()
      }
   }

   public fun getAllOverlays(): Map<String, MovableOverlay> {
      return MapsKt.toMap(overlays)
   }

   public fun resetOverlayPosition(id: String) {
      val var10000: MovableOverlay = overlays.get(id)
      if (var10000 != null) {
         var10000.resetPosition()
      }
   }
}
