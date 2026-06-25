package jooon.util

import java.util.LinkedHashMap

object MovableOverlayManager {
   private val overlays: MutableMap<String, MovableOverlay> = LinkedHashMap() as java.util.Map

   fun createOverlay(id: String, displayName: String, defaultX: Int, defaultY: Int, width: Int, height: Int): MovableOverlay {

      overlays.put(id, overlay)
      return overlay
   }

   fun getOverlay(id: String): MovableOverlay? {
      return overlays.get(id)
   }

   fun removeOverlay(id: String) {

      if (var10000 != null) {
         var10000.unregister()
      }

      overlays.remove(id)
   }

   fun openPositioningGUI(id: String) {

      if (var10000 != null) {
         var10000.openPositioningGUI()
      }
   }

   fun getAllOverlays(): Map<String, MovableOverlay> {
      return toMap(overlays)
   }

   fun resetOverlayPosition(id: String) {

      if (var10000 != null) {
         var10000.resetPosition()
      }
   }
}
