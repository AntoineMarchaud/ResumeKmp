package com.amarchaud.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** Brand palette: deep indigo with violet and cyan accents, on cool neutral surfaces. */
internal val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF4338CA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    inversePrimary = Color(0xFFA5B4FC),
    secondary = Color(0xFF0E7490),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF164E63),
    tertiary = Color(0xFF7C3AED),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF2E1065),
    background = Color(0xFFF5F6FA),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFF5F6FA),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    surfaceTint = Color(0xFF4338CA),
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF1F5F9),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFE2E8F0),
    surfaceBright = Color.White,
    surfaceDim = Color(0xFFE5E7EF),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAFBFD),
    surfaceContainer = Color(0xFFEEF0F6),
    surfaceContainerHigh = Color(0xFFE8EBF2),
    surfaceContainerHighest = Color(0xFFE1E5EE),
)

internal val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFA5B4FC),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    inversePrimary = Color(0xFF4338CA),
    secondary = Color(0xFF67E8F9),
    onSecondary = Color(0xFF083344),
    secondaryContainer = Color(0xFF155E75),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = Color(0xFFC4B5FD),
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF5B21B6),
    onTertiaryContainer = Color(0xFFEDE9FE),
    background = Color(0xFF0B0F1A),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF0B0F1A),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceTint = Color(0xFFA5B4FC),
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF0F172A),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF1F2937),
    surfaceBright = Color(0xFF283044),
    surfaceDim = Color(0xFF0B0F1A),
    surfaceContainerLowest = Color(0xFF070A12),
    surfaceContainerLow = Color(0xFF111726),
    surfaceContainer = Color(0xFF151C2D),
    surfaceContainerHigh = Color(0xFF1B2335),
    surfaceContainerHighest = Color(0xFF222B3F),
)

/** Colors that are not part of the Material [ColorScheme]. */
@Immutable
data class ResumeColors(
    val heroStart: Color,
    val heroEnd: Color,
    val heroGlow: Color,
    val heroGlowSecondary: Color,
    val onHero: Color,
    val onHeroMuted: Color,
    val heroSurface: Color,
    val heroOutline: Color,
    val card: Color,
    val cardBorder: Color,
    val navigationContainer: Color,
    val positive: Color,
)

/**
 * Derives the extra colors from the active [ColorScheme], so they follow Material You colors too.
 * The hero is always dark, whatever the theme, to keep white content readable.
 */
internal fun resumeColorsFor(scheme: ColorScheme, darkTheme: Boolean): ResumeColors {
    val base = if (darkTheme) scheme.primaryContainer else scheme.primary
    return ResumeColors(
        heroStart = lerp(base, Color.Black, 0.62f),
        heroEnd = lerp(base, Color.Black, if (darkTheme) 0.08f else 0.18f),
        heroGlow = if (darkTheme) scheme.tertiaryContainer else scheme.tertiary,
        heroGlowSecondary = if (darkTheme) scheme.secondaryContainer else scheme.secondary,
        onHero = Color.White,
        onHeroMuted = Color.White.copy(alpha = 0.74f),
        heroSurface = Color.White.copy(alpha = 0.10f),
        heroOutline = Color.White.copy(alpha = 0.16f),
        card = if (darkTheme) scheme.surfaceContainerLow else scheme.surfaceContainerLowest,
        cardBorder = if (darkTheme) scheme.outlineVariant else scheme.outlineVariant.copy(alpha = 0.9f),
        navigationContainer = if (darkTheme) scheme.surfaceContainerHigh else scheme.surfaceContainerLowest,
        positive = if (darkTheme) Color(0xFF34D399) else Color(0xFF047857),
    )
}
