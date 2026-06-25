package jooon.config.ui

internal data class UiRect(x: Int, y: Int, width: Int, height: Int) {
   val x: Int
   val y: Int
   val width: Int
   val height: Int

   init {
      this.x = x
      this.y = y
      this.width = width
      this.height = height
   }

   val right: Int
      public get() {
         return this.x + this.width
      }


   val bottom: Int
      public get() {
         return this.y + this.height
      }


   fun contains(px: Int, py: Int): Boolean {
      return px >= this.x && px < this.right && py >= this.y && py < this.bottom
   }

   public operator fun component1(): Int {
      return this.x
   }

   public operator fun component2(): Int {
      return this.y
   }

   public operator fun component3(): Int {
      return this.width
   }

   public operator fun component4(): Int {
      return this.height
   }

   fun copy(x: Int = this.x, y: Int = this.y, width: Int = this.width, height: Int = this.height): UiRect {
      return UiRect(x, y, width, height)
   }

   override fun toString(): String {
      return "UiRect(x=${this.x}, y=${this.y}, width=${this.width}, height=${this.height})"
   }

   override fun hashCode(): Int {
      return ((Integer.hashCode(this.x) * 31 + Integer.hashCode(this.y)) * 31 + Integer.hashCode(this.width)) * 31 + Integer.hashCode(this.height)
   }

   override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is UiRect
            && this.x == (other as UiRect).x
            && this.y == (other as UiRect).y
            && this.width == (other as UiRect).width
            && this.height == (other as UiRect).height
         }
   }
}
