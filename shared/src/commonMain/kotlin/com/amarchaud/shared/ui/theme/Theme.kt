package com.amarchaud.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.platform.dynamicColorScheme

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

private val LocalResumeColors = staticCompositionLocalOf { resumeColorsFor(LightColors, darkTheme = false) }

/** Entry point to the app specific design tokens. */
object ResumeTheme {
    val colors: ResumeColors
        @Composable @ReadOnlyComposable get() = LocalResumeColors.current
}

@Composable
fun TemplateResumeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = (if (dynamicColor) dynamicColorScheme(darkTheme) else null)
        ?: if (darkTheme) DarkColors else LightColors
    val resumeColors = remember(colorScheme, darkTheme) { resumeColorsFor(colorScheme, darkTheme) }

    CompositionLocalProvider(LocalResumeColors provides resumeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography(),
            shapes = AppShapes,
            content = content,
        )
    }
}
