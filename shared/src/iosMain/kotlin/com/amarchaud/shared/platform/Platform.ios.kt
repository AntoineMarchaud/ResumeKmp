package com.amarchaud.shared.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.window.ComposeUIViewController
import com.amarchaud.shared.App
import platform.UIKit.UIViewController

actual val isDynamicColorAvailable: Boolean = false

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

/** Receives `true` when the status bar icons must be light (hero behind them). */
private val LocalStatusBarIconsHandler = staticCompositionLocalOf<(Boolean) -> Unit> { {} }

/**
 * On iOS, the status bar style belongs to the parent view controller hosting Compose:
 * the app only reports which icons it needs.
 */
@Composable
internal actual fun SystemBarsAppearance(lightStatusBarIcons: Boolean, darkTheme: Boolean) {
    val onLightStatusBarIconsChange = LocalStatusBarIconsHandler.current
    SideEffect { onLightStatusBarIconsChange(lightStatusBarIcons) }
}

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun plainTextClipEntry(text: String): ClipEntry = ClipEntry.withPlainText(text)

/**
 * View controller rendering the app. [onLightStatusBarIconsChange] lets the parent view controller
 * update its `preferredStatusBarStyle`.
 */
fun resumeViewController(onLightStatusBarIconsChange: (Boolean) -> Unit): UIViewController =
    ComposeUIViewController {
        CompositionLocalProvider(LocalStatusBarIconsHandler provides onLightStatusBarIconsChange) {
            App()
        }
    }
