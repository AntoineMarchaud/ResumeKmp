package com.amarchaud.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.amarchaud.shared.resources.Res
import com.amarchaud.shared.resources.plus_jakarta_sans
import org.jetbrains.compose.resources.Font

/**
 * Plus Jakarta Sans (SIL Open Font License 1.1), bundled as a single variable font file:
 * each weight is an instance of its `wght` axis.
 */
@Composable
private fun plusJakartaSans(): FontFamily {
    @Composable
    fun instance(weight: FontWeight) = Font(
        resource = Res.font.plus_jakarta_sans,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
    )
    return FontFamily(
        instance(FontWeight.Normal),
        instance(FontWeight.Medium),
        instance(FontWeight.SemiBold),
        instance(FontWeight.Bold),
        instance(FontWeight.ExtraBold),
    )
}

private fun TextStyle.with(
    family: FontFamily,
    weight: FontWeight,
    letterSpacing: TextUnit = this.letterSpacing,
    lineHeight: TextUnit = this.lineHeight,
) = copy(
    fontFamily = family,
    fontWeight = weight,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
)

@Composable
internal fun appTypography(): Typography {
    val family = plusJakartaSans()
    return remember(family) {
        val default = Typography()
        Typography(
            displayLarge = default.displayLarge.with(family, FontWeight.ExtraBold, letterSpacing = (-1).sp),
            displayMedium = default.displayMedium.with(family, FontWeight.ExtraBold, letterSpacing = (-0.75).sp),
            displaySmall = default.displaySmall.with(family, FontWeight.Bold, letterSpacing = (-0.5).sp),
            headlineLarge = default.headlineLarge.with(family, FontWeight.Bold, letterSpacing = (-0.5).sp),
            headlineMedium = default.headlineMedium.with(family, FontWeight.Bold, letterSpacing = (-0.4).sp),
            headlineSmall = default.headlineSmall.with(family, FontWeight.Bold, letterSpacing = (-0.25).sp),
            titleLarge = default.titleLarge.with(family, FontWeight.Bold, letterSpacing = (-0.2).sp),
            titleMedium = default.titleMedium.with(family, FontWeight.Bold, letterSpacing = 0.sp),
            titleSmall = default.titleSmall.with(family, FontWeight.SemiBold, letterSpacing = 0.sp),
            bodyLarge = default.bodyLarge.with(family, FontWeight.Normal, letterSpacing = 0.1.sp, lineHeight = 26.sp),
            bodyMedium = default.bodyMedium.with(family, FontWeight.Normal, letterSpacing = 0.1.sp, lineHeight = 22.sp),
            bodySmall = default.bodySmall.with(family, FontWeight.Normal, letterSpacing = 0.2.sp),
            labelLarge = default.labelLarge.with(family, FontWeight.SemiBold, letterSpacing = 0.1.sp),
            labelMedium = default.labelMedium.with(family, FontWeight.SemiBold, letterSpacing = 0.2.sp),
            labelSmall = default.labelSmall.with(family, FontWeight.Bold, letterSpacing = 0.6.sp),
        )
    }
}
