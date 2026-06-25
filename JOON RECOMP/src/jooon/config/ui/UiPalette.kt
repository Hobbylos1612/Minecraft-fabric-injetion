package jooon.config.ui

internal data class UiPalette(backdrop: Int,
   paper: Int,
   sidebar: Int,
   header: Int,
   panel: Int,
   field: Int,
   hover: Int,
   selected: Int,
   text: Int,
   fieldText: Int,
   mutedText: Int,
   line: Int,
   lineSoft: Int,
   mutedLine: Int,
   accent: Int,
   accentSoft: Int,
   toggleOn: Int,
   toggleOff: Int,
   tooltipBack: Int,
   tooltipText: Int
) {
   public final val backdrop: Int
   public final val paper: Int
   public final val sidebar: Int
   public final val header: Int
   public final val panel: Int
   public final val field: Int
   public final val hover: Int
   public final val selected: Int
   public final val text: Int
   public final val fieldText: Int
   public final val mutedText: Int
   public final val line: Int
   public final val lineSoft: Int
   public final val mutedLine: Int
   public final val accent: Int
   public final val accentSoft: Int
   public final val toggleOn: Int
   public final val toggleOff: Int
   public final val tooltipBack: Int
   public final val tooltipText: Int

   init {
      this.backdrop = backdrop
      this.paper = paper
      this.sidebar = sidebar
      this.header = header
      this.panel = panel
      this.field = field
      this.hover = hover
      this.selected = selected
      this.text = text
      this.fieldText = fieldText
      this.mutedText = mutedText
      this.line = line
      this.lineSoft = lineSoft
      this.mutedLine = mutedLine
      this.accent = accent
      this.accentSoft = accentSoft
      this.toggleOn = toggleOn
      this.toggleOff = toggleOff
      this.tooltipBack = tooltipBack
      this.tooltipText = tooltipText
   }

   public operator fun component1(): Int {
      return this.backdrop
   }

   public operator fun component2(): Int {
      return this.paper
   }

   public operator fun component3(): Int {
      return this.sidebar
   }

   public operator fun component4(): Int {
      return this.header
   }

   public operator fun component5(): Int {
      return this.panel
   }

   public operator fun component6(): Int {
      return this.field
   }

   public operator fun component7(): Int {
      return this.hover
   }

   public operator fun component8(): Int {
      return this.selected
   }

   public operator fun component9(): Int {
      return this.text
   }

   public operator fun component10(): Int {
      return this.fieldText
   }

   public operator fun component11(): Int {
      return this.mutedText
   }

   public operator fun component12(): Int {
      return this.line
   }

   public operator fun component13(): Int {
      return this.lineSoft
   }

   public operator fun component14(): Int {
      return this.mutedLine
   }

   public operator fun component15(): Int {
      return this.accent
   }

   public operator fun component16(): Int {
      return this.accentSoft
   }

   public operator fun component17(): Int {
      return this.toggleOn
   }

   public operator fun component18(): Int {
      return this.toggleOff
   }

   public operator fun component19(): Int {
      return this.tooltipBack
   }

   public operator fun component20(): Int {
      return this.tooltipText
   }

   public fun copy(
      backdrop: Int = this.backdrop,
      paper: Int = this.paper,
      sidebar: Int = this.sidebar,
      header: Int = this.header,
      panel: Int = this.panel,
      field: Int = this.field,
      hover: Int = this.hover,
      selected: Int = this.selected,
      text: Int = this.text,
      fieldText: Int = this.fieldText,
      mutedText: Int = this.mutedText,
      line: Int = this.line,
      lineSoft: Int = this.lineSoft,
      mutedLine: Int = this.mutedLine,
      accent: Int = this.accent,
      accentSoft: Int = this.accentSoft,
      toggleOn: Int = this.toggleOn,
      toggleOff: Int = this.toggleOff,
      tooltipBack: Int = this.tooltipBack,
      tooltipText: Int = this.tooltipText
   ): UiPalette {
      return UiPalette(
         backdrop,
         paper,
         sidebar,
         header,
         panel,
         field,
         hover,
         selected,
         text,
         fieldText,
         mutedText,
         line,
         lineSoft,
         mutedLine,
         accent,
         accentSoft,
         toggleOn,
         toggleOff,
         tooltipBack,
         tooltipText
      )
   }

   public override fun toString(): String {
      return "UiPalette(backdrop=${this.backdrop}, paper=${this.paper}, sidebar=${this.sidebar}, header=${this.header}, panel=${this.panel}, field=${this.field}, hover=${this.hover}, selected=${this.selected}, text=${this.text}, fieldText=${this.fieldText}, mutedText=${this.mutedText}, line=${this.line}, lineSoft=${this.lineSoft}, mutedLine=${this.mutedLine}, accent=${this.accent}, accentSoft=${this.accentSoft}, toggleOn=${this.toggleOn}, toggleOff=${this.toggleOff}, tooltipBack=${this.tooltipBack}, tooltipText=${this.tooltipText})"
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
                                                                                                                                                                        Integer.hashCode(
                                                                                                                                                                                 this.backdrop
                                                                                                                                                                              )
                                                                                                                                                                              * 31
                                                                                                                                                                           + Integer.hashCode(
                                                                                                                                                                              this.paper
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                                     * 31
                                                                                                                                                                  + Integer.hashCode(
                                                                                                                                                                     this.sidebar
                                                                                                                                                                  )
                                                                                                                                                            )
                                                                                                                                                            * 31
                                                                                                                                                         + Integer.hashCode(
                                                                                                                                                            this.header
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + Integer.hashCode(
                                                                                                                                                   this.panel
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + Integer.hashCode(
                                                                                                                                          this.field
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + Integer.hashCode(this.hover)
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + Integer.hashCode(this.selected)
                                                                                                               )
                                                                                                               * 31
                                                                                                            + Integer.hashCode(this.text)
                                                                                                      )
                                                                                                      * 31
                                                                                                   + Integer.hashCode(this.fieldText)
                                                                                             )
                                                                                             * 31
                                                                                          + Integer.hashCode(this.mutedText)
                                                                                    )
                                                                                    * 31
                                                                                 + Integer.hashCode(this.line)
                                                                           )
                                                                           * 31
                                                                        + Integer.hashCode(this.lineSoft)
                                                                  )
                                                                  * 31
                                                               + Integer.hashCode(this.mutedLine)
                                                         )
                                                         * 31
                                                      + Integer.hashCode(this.accent)
                                                )
                                                * 31
                                             + Integer.hashCode(this.accentSoft)
                                       )
                                       * 31
                                    + Integer.hashCode(this.toggleOn)
                              )
                              * 31
                           + Integer.hashCode(this.toggleOff)
                     )
                     * 31
                  + Integer.hashCode(this.tooltipBack)
            )
            * 31
         + Integer.hashCode(this.tooltipText)
      }

   public override operator fun equals(other: Any?): Boolean {
      label136@
      if (this === other) {
         return true
      } else {
         return other is UiPalette
            && this.backdrop == (other as UiPalette).backdrop
            && this.paper == (other as UiPalette).paper
            && this.sidebar == (other as UiPalette).sidebar
            && this.header == (other as UiPalette).header
            && this.panel == (other as UiPalette).panel
            && this.field == (other as UiPalette).field
            && this.hover == (other as UiPalette).hover
            && this.selected == (other as UiPalette).selected
            && this.text == (other as UiPalette).text
            && this.fieldText == (other as UiPalette).fieldText
            && this.mutedText == (other as UiPalette).mutedText
            && this.line == (other as UiPalette).line
            && this.lineSoft == (other as UiPalette).lineSoft
            && this.mutedLine == (other as UiPalette).mutedLine
            && this.accent == (other as UiPalette).accent
            && this.accentSoft == (other as UiPalette).accentSoft
            && this.toggleOn == (other as UiPalette).toggleOn
            && this.toggleOff == (other as UiPalette).toggleOff
            && this.tooltipBack == (other as UiPalette).tooltipBack
            && this.tooltipText == (other as UiPalette).tooltipText
         }
   }
}
