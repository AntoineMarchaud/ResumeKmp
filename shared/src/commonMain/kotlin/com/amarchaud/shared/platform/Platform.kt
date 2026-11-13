package com.amarchaud.shared.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipEntry

/** Material You (wallpaper based) colors: only available on Android 12+. */
expect val isDynamicColorAvailable: Boolean

/** Wallpaper based color scheme, or `null` when the platform does not provide one. */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?

/**
 * Keeps the system bars transparent, with icons readable on top of what is drawn behind them.
 * No-op on platforms without system bars.
 */
@Composable
internal expect fun SystemBarsAppearance(lightStatusBarIcons: Boolean, darkTheme: Boolean)

/** A clipboard entry holding plain text. */
internal expect fun plainTextClipEntry(text: String): ClipEntry
