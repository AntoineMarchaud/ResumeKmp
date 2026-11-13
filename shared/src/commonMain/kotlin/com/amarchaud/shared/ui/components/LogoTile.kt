package com.amarchaud.shared.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.ui.theme.ResumeTheme

private val LogoTileHorizontalPadding = 8.dp
private val LogoTileVerticalPadding = 7.dp

/** Widest tile, relative to its height, so that long wordmarks do not take a whole row. */
private const val LogoTileMaxAspectRatio = 2.4f

/**
 * Company logo on a white tile whatever the theme, as most logos are designed for light backgrounds.
 * The tile follows the logo's aspect ratio, so that wordmarks stay readable. An empty tile is shown
 * while [image] loads.
 */
@Composable
fun LogoTile(
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    contentDescription: String? = null,
) {
    val shape = RoundedCornerShape(height * 0.32f)
    val width = if (image == null) {
        height
    } else {
        val contentHeight = height - LogoTileVerticalPadding * 2
        (contentHeight * (image.width.toFloat() / image.height) + LogoTileHorizontalPadding * 2)
            .coerceIn(height, height * LogoTileMaxAspectRatio)
    }
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(Color.White)
            .border(width = 1.dp, color = ResumeTheme.colors.cardBorder, shape = shape)
            .padding(horizontal = LogoTileHorizontalPadding, vertical = LogoTileVerticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            Image(bitmap = image, contentDescription = contentDescription, contentScale = ContentScale.Fit)
        }
    }
}
