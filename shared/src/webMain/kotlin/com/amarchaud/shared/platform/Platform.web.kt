package com.amarchaud.shared.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

actual val isDynamicColorAvailable: Boolean = false

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

@Composable
internal actual fun SystemBarsAppearance(lightStatusBarIcons: Boolean, darkTheme: Boolean) = Unit
