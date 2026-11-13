package com.amarchaud.shared.platform

import android.content.ClipData
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalContext

actual val isDynamicColorAvailable: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    val context = LocalContext.current
    return if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

/**
 * Explicit light / dark styles are used on purpose: `SystemBarStyle.auto` makes the system enforce
 * contrast, which draws a translucent scrim behind the 3-button navigation bar.
 */
@Composable
internal actual fun SystemBarsAppearance(lightStatusBarIcons: Boolean, darkTheme: Boolean) {
    val activity = LocalActivity.current as? ComponentActivity ?: return
    DisposableEffect(activity, lightStatusBarIcons, darkTheme) {
        activity.enableEdgeToEdge(
            statusBarStyle = transparentBarStyle(lightIcons = lightStatusBarIcons),
            navigationBarStyle = transparentBarStyle(lightIcons = darkTheme),
        )
        onDispose { }
    }
}

private fun transparentBarStyle(lightIcons: Boolean): SystemBarStyle {
    val transparent = Color.Transparent.toArgb()
    return if (lightIcons) SystemBarStyle.dark(transparent) else SystemBarStyle.light(transparent, transparent)
}

internal actual fun plainTextClipEntry(text: String): ClipEntry = ClipEntry(ClipData.newPlainText(text, text))
