package com.amarchaud.shared.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

actual val isDynamicColorAvailable: Boolean = false

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

@Composable
internal actual fun SystemBarsAppearance(lightStatusBarIcons: Boolean, darkTheme: Boolean) = Unit

/** The desktop clipboard is the AWT one: the platform API, like `ClipData` on Android. */
@OptIn(ExperimentalComposeUiApi::class)
internal actual fun plainTextClipEntry(text: String): ClipEntry = ClipEntry(StringSelection(text))
