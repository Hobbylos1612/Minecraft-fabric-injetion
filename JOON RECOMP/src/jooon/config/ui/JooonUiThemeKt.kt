package jooon.config.ui

import net.minecraft.client.gui.DrawContext

internal fun currentPalette(): UiPalette {
   JooonUiSettings.INSTANCE.ensureLoaded()
var var10000: UiPalette
   when (JooonUiThemeKt.WhenMappings.$EnumSwitchMapping$0[JooonUiSettings.INSTANCE.theme.ordinal()]) {
      1 -> var10000 = UiPalette(
            -15724010,
            -15262944,
            -15460323,
            -14868185,
            -14670548,
            -856347,
            -14012360,
            -13551551,
            -527124,
            -15197666,
            -3093825,
            -1580330,
            -7435906,
            -11512225,
            -461590,
            -2304052,
            -11087004,
            -503479,
            -301265647,
            -527124
         )
      2 -> var10000 = UiPalette(
            -4475735,
            -988191,
            -2567740,
            -1712431,
            -593174,
            -1909556,
            -1251880,
            -395794,
            -14541545,
            -14541545,
            -10595512,
            -14607337,
            -9542568,
            -6910849,
            -10979766,
            -7294846,
            -8468115,
            -2065814,
            -299622630,
            -593174
         )
      3 -> var10000 = UiPalette(
            -4931416,
            -1642789,
            -2628407,
            -2102577,
            -919576,
            -459535,
            -1576230,
            -459278,
            -14405601,
            -14405601,
            -10982833,
            -13747416,
            -9404058,
            -7100537,
            -8998315,
            -5778039,
            -9255077,
            -1868174,
            -299749343,
            -853781
         )
      else -> throw NoWhenBranchMatchedException()
   }

   return var10000
}

fun DrawContext.drawOutline(rect: UiRect, color: Int) {
   `$this$drawOutline`.method_25294(rect.x, rect.y, rect.right, rect.y + 1, color)
   `$this$drawOutline`.method_25294(rect.x, rect.bottom - 1, rect.right, rect.bottom, color)
   `$this$drawOutline`.method_25294(rect.x, rect.y, rect.x + 1, rect.bottom, color)
   `$this$drawOutline`.method_25294(rect.right - 1, rect.y, rect.right, rect.bottom, color)
}
