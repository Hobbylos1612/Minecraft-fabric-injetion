package jooon.features.dungeons.map

import java.awt.Color

public data class DungeonMapRenderOptions(colors: Map<DungeonMapColors, Color>,
   roomWidth: Double,
   doorWidth: Double,
   dungeonWidth: Int,
   dungeonHeight: Int,
   padding: Double,
   border: Int,
   checkMark: Boolean,
   roomName: Boolean,
   roomNameNotEFB: Boolean,
   secretCount: Boolean,
   puzzleName: Boolean,
   iconSize: Double,
   iconAlignment: DungeonMapRoomInfoAlignment,
   textSize: Double,
   textAlignment: DungeonMapRoomInfoAlignment,
   colorRoomName: Boolean,
   renderUnknownRooms: Boolean,
   dungeonStarted: Boolean,
   unknownRoomsDarkenFactor: Double
) {
   public final val colors: Map<DungeonMapColors, Color>
   public final val roomWidth: Double
   public final val doorWidth: Double
   public final val dungeonWidth: Int
   public final val dungeonHeight: Int
   public final val padding: Double
   public final val border: Int
   public final val checkMark: Boolean
   public final val roomName: Boolean
   public final val roomNameNotEFB: Boolean
   public final val secretCount: Boolean
   public final val puzzleName: Boolean
   public final val iconSize: Double
   public final val iconAlignment: DungeonMapRoomInfoAlignment
   public final val textSize: Double
   public final val textAlignment: DungeonMapRoomInfoAlignment
   public final val colorRoomName: Boolean
   public final val renderUnknownRooms: Boolean
   public final val dungeonStarted: Boolean
   public final val unknownRoomsDarkenFactor: Double

   init {
      this.colors = colors
      this.roomWidth = roomWidth
      this.doorWidth = doorWidth
      this.dungeonWidth = dungeonWidth
      this.dungeonHeight = dungeonHeight
      this.padding = padding
      this.border = border
      this.checkMark = checkMark
      this.roomName = roomName
      this.roomNameNotEFB = roomNameNotEFB
      this.secretCount = secretCount
      this.puzzleName = puzzleName
      this.iconSize = iconSize
      this.iconAlignment = iconAlignment
      this.textSize = textSize
      this.textAlignment = textAlignment
      this.colorRoomName = colorRoomName
      this.renderUnknownRooms = renderUnknownRooms
      this.dungeonStarted = dungeonStarted
      this.unknownRoomsDarkenFactor = unknownRoomsDarkenFactor
   }

   public operator fun component1(): Map<DungeonMapColors, Color> {
      return this.colors
   }

   public operator fun component2(): Double {
      return this.roomWidth
   }

   public operator fun component3(): Double {
      return this.doorWidth
   }

   public operator fun component4(): Int {
      return this.dungeonWidth
   }

   public operator fun component5(): Int {
      return this.dungeonHeight
   }

   public operator fun component6(): Double {
      return this.padding
   }

   public operator fun component7(): Int {
      return this.border
   }

   public operator fun component8(): Boolean {
      return this.checkMark
   }

   public operator fun component9(): Boolean {
      return this.roomName
   }

   public operator fun component10(): Boolean {
      return this.roomNameNotEFB
   }

   public operator fun component11(): Boolean {
      return this.secretCount
   }

   public operator fun component12(): Boolean {
      return this.puzzleName
   }

   public operator fun component13(): Double {
      return this.iconSize
   }

   public operator fun component14(): DungeonMapRoomInfoAlignment {
      return this.iconAlignment
   }

   public operator fun component15(): Double {
      return this.textSize
   }

   public operator fun component16(): DungeonMapRoomInfoAlignment {
      return this.textAlignment
   }

   public operator fun component17(): Boolean {
      return this.colorRoomName
   }

   public operator fun component18(): Boolean {
      return this.renderUnknownRooms
   }

   public operator fun component19(): Boolean {
      return this.dungeonStarted
   }

   public operator fun component20(): Double {
      return this.unknownRoomsDarkenFactor
   }

   public fun copy(
      colors: Map<DungeonMapColors, Color> = this.colors,
      roomWidth: Double = this.roomWidth,
      doorWidth: Double = this.doorWidth,
      dungeonWidth: Int = this.dungeonWidth,
      dungeonHeight: Int = this.dungeonHeight,
      padding: Double = this.padding,
      border: Int = this.border,
      checkMark: Boolean = this.checkMark,
      roomName: Boolean = this.roomName,
      roomNameNotEFB: Boolean = this.roomNameNotEFB,
      secretCount: Boolean = this.secretCount,
      puzzleName: Boolean = this.puzzleName,
      iconSize: Double = this.iconSize,
      iconAlignment: DungeonMapRoomInfoAlignment = this.iconAlignment,
      textSize: Double = this.textSize,
      textAlignment: DungeonMapRoomInfoAlignment = this.textAlignment,
      colorRoomName: Boolean = this.colorRoomName,
      renderUnknownRooms: Boolean = this.renderUnknownRooms,
      dungeonStarted: Boolean = this.dungeonStarted,
      unknownRoomsDarkenFactor: Double = this.unknownRoomsDarkenFactor
   ): DungeonMapRenderOptions {
      return DungeonMapRenderOptions(
         colors,
         roomWidth,
         doorWidth,
         dungeonWidth,
         dungeonHeight,
         padding,
         border,
         checkMark,
         roomName,
         roomNameNotEFB,
         secretCount,
         puzzleName,
         iconSize,
         iconAlignment,
         textSize,
         textAlignment,
         colorRoomName,
         renderUnknownRooms,
         dungeonStarted,
         unknownRoomsDarkenFactor
      )
   }

   public override fun toString(): String {
      return "DungeonMapRenderOptions(colors=${this.colors}, roomWidth=${this.roomWidth}, doorWidth=${this.doorWidth}, dungeonWidth=${this.dungeonWidth}, dungeonHeight=${this.dungeonHeight}, padding=${this.padding}, border=${this.border}, checkMark=${this.checkMark}, roomName=${this.roomName}, roomNameNotEFB=${this.roomNameNotEFB}, secretCount=${this.secretCount}, puzzleName=${this.puzzleName}, iconSize=${this.iconSize}, iconAlignment=${this.iconAlignment}, textSize=${this.textSize}, textAlignment=${this.textAlignment}, colorRoomName=${this.colorRoomName}, renderUnknownRooms=${this.renderUnknownRooms}, dungeonStarted=${this.dungeonStarted}, unknownRoomsDarkenFactor=${this.unknownRoomsDarkenFactor})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    (
                                                                                                                                             (
                                                                                                                                                      (
                                                                                                                                                               (
                                                                                                                                                                        this.colors
                                                                                                                                                                                 .hashCode()
                                                                                                                                                                              * 31
                                                                                                                                                                           + java.lang.Double.hashCode(
                                                                                                                                                                              this.roomWidth
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                                     * 31
                                                                                                                                                                  + java.lang.Double.hashCode(
                                                                                                                                                                     this.doorWidth
                                                                                                                                                                  )
                                                                                                                                                            )
                                                                                                                                                            * 31
                                                                                                                                                         + Integer.hashCode(
                                                                                                                                                            this.dungeonWidth
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + Integer.hashCode(
                                                                                                                                                   this.dungeonHeight
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + java.lang.Double.hashCode(
                                                                                                                                          this.padding
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + Integer.hashCode(this.border)
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + java.lang.Boolean.hashCode(
                                                                                                                        this.checkMark
                                                                                                                     )
                                                                                                               )
                                                                                                               * 31
                                                                                                            + java.lang.Boolean.hashCode(this.roomName)
                                                                                                      )
                                                                                                      * 31
                                                                                                   + java.lang.Boolean.hashCode(this.roomNameNotEFB)
                                                                                             )
                                                                                             * 31
                                                                                          + java.lang.Boolean.hashCode(this.secretCount)
                                                                                    )
                                                                                    * 31
                                                                                 + java.lang.Boolean.hashCode(this.puzzleName)
                                                                           )
                                                                           * 31
                                                                        + java.lang.Double.hashCode(this.iconSize)
                                                                  )
                                                                  * 31
                                                               + this.iconAlignment.hashCode()
                                                         )
                                                         * 31
                                                      + java.lang.Double.hashCode(this.textSize)
                                                )
                                                * 31
                                             + this.textAlignment.hashCode()
                                       )
                                       * 31
                                    + java.lang.Boolean.hashCode(this.colorRoomName)
                              )
                              * 31
                           + java.lang.Boolean.hashCode(this.renderUnknownRooms)
                     )
                     * 31
                  + java.lang.Boolean.hashCode(this.dungeonStarted)
            )
            * 31
         + java.lang.Double.hashCode(this.unknownRoomsDarkenFactor)
      }

   public override operator fun equals(other: Any?): Boolean {
      label136@
      if (this === other) {
         return true
      } else {
         return other is DungeonMapRenderOptions
            && this.colors == (other as DungeonMapRenderOptions).colors
            && java.lang.Double.compare(this.roomWidth, (other as DungeonMapRenderOptions).roomWidth) == 0
            && java.lang.Double.compare(this.doorWidth, (other as DungeonMapRenderOptions).doorWidth) == 0
            && this.dungeonWidth == (other as DungeonMapRenderOptions).dungeonWidth
            && this.dungeonHeight == (other as DungeonMapRenderOptions).dungeonHeight
            && java.lang.Double.compare(this.padding, (other as DungeonMapRenderOptions).padding) == 0
            && this.border == (other as DungeonMapRenderOptions).border
            && this.checkMark == (other as DungeonMapRenderOptions).checkMark
            && this.roomName == (other as DungeonMapRenderOptions).roomName
            && this.roomNameNotEFB == (other as DungeonMapRenderOptions).roomNameNotEFB
            && this.secretCount == (other as DungeonMapRenderOptions).secretCount
            && this.puzzleName == (other as DungeonMapRenderOptions).puzzleName
            && java.lang.Double.compare(this.iconSize, (other as DungeonMapRenderOptions).iconSize) == 0
            && this.iconAlignment === (other as DungeonMapRenderOptions).iconAlignment
            && java.lang.Double.compare(this.textSize, (other as DungeonMapRenderOptions).textSize) == 0
            && this.textAlignment === (other as DungeonMapRenderOptions).textAlignment
            && this.colorRoomName == (other as DungeonMapRenderOptions).colorRoomName
            && this.renderUnknownRooms == (other as DungeonMapRenderOptions).renderUnknownRooms
            && this.dungeonStarted == (other as DungeonMapRenderOptions).dungeonStarted
            && java.lang.Double.compare(this.unknownRoomsDarkenFactor, (other as DungeonMapRenderOptions).unknownRoomsDarkenFactor) == 0
         }
   }
}
