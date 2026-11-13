package com.amarchaud.shared.ui.resume

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/** How the resume is arranged for the current window. */
sealed interface ResumeLayout {

    /** Phones in portrait: collapsing header and floating bottom navigation. */
    data object Compact : ResumeLayout

    /** Small tablets in portrait, unfolded foldables: collapsing header and floating rail. */
    data object Medium : ResumeLayout

    /**
     * Large screens, landscape phones and foldables in book posture: profile pane next to the content.
     * [paneWidth] is imposed by a vertical hinge, and [hingeWidth] keeps content out of it.
     */
    data class TwoPane(val paneWidth: Dp? = null, val hingeWidth: Dp = 0.dp) : ResumeLayout

    /** Half-opened foldable with a horizontal hinge: profile above the hinge, content below. */
    data class Tabletop(val topHeight: Dp, val hingeHeight: Dp) : ResumeLayout
}

/** Picks the [ResumeLayout] following the Material 3 adaptive guidelines. */
internal fun resumeLayoutFor(info: WindowAdaptiveInfo, density: Density): ResumeLayout {
    val sizeClass = info.windowSizeClass
    val posture = info.windowPosture
    val hinge = posture.hingeList.firstOrNull { it.isSeparating }

    return with(density) {
        when {
            hinge != null && posture.isTabletop && !hinge.isVertical -> ResumeLayout.Tabletop(
                topHeight = hinge.bounds.top.toDp(),
                hingeHeight = hinge.bounds.height.toDp(),
            )

            hinge != null && hinge.isVertical -> ResumeLayout.TwoPane(
                paneWidth = hinge.bounds.left.toDp(),
                hingeWidth = hinge.bounds.width.toDp(),
            )

            sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> ResumeLayout.TwoPane()

            // Landscape phones: not enough height for a collapsing header, plenty of width for a pane.
            sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
                !sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> ResumeLayout.TwoPane()

            sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> ResumeLayout.Medium

            else -> ResumeLayout.Compact
        }
    }
}
