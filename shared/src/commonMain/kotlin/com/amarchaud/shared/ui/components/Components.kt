package com.amarchaud.shared.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amarchaud.shared.ui.theme.ResumeColors
import com.amarchaud.shared.ui.theme.ResumeTheme

/** Flat card with a hairline border, the base surface of every section. */
@Composable
fun ResumeCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = ResumeTheme.colors
    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.card)
            .border(width = 1.dp, color = colors.cardBorder, shape = shape),
        content = content,
    )
}

/** Title of a whole section, optionally followed by a subtitle and a trailing element. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}

/** Overline title used to group content inside a section. */
@Composable
fun GroupTitle(text: String, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier.semantics { heading() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 16.dp, height = 3.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(scheme.primary, scheme.tertiary))),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.4.sp),
            color = scheme.onSurfaceVariant,
        )
    }
}

/** Small pill displaying a count or a short label next to a [SectionHeader]. */
@Composable
fun CountPill(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
    )
}

/** Compact technology tag. */
@Composable
fun TechChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
    )
}

/** Larger skill tag with a gradient bullet. */
@Composable
fun SkillChip(text: String, modifier: Modifier = Modifier) {
    val colors = ResumeTheme.colors
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.card)
            .border(width = 1.dp, color = colors.cardBorder, shape = CircleShape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(scheme.primary, scheme.tertiary))),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = scheme.onSurface,
            maxLines = 1,
        )
    }
}

/** "Current" status badge, with a softly pulsing dot. */
@Composable
fun CurrentBadge(text: String, modifier: Modifier = Modifier) {
    val color = ResumeTheme.colors.positive
    val transition = rememberInfiniteTransition(label = "current-pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "current-pulse-alpha",
    )
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .graphicsLayer { alpha = pulse }
                .background(color, CircleShape),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

/** Rounded tile displaying initials, used for companies and the profile avatar. */
@Composable
fun Monogram(
    text: String,
    container: Brush,
    contentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shape: Shape = RoundedCornerShape(size * 0.32f),
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
) {
    // Keep the initials proportional to the tile, whatever the user font scale.
    val fontSize = with(LocalDensity.current) { (size * 0.36f).toSp() }
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(container),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = textStyle.copy(fontSize = fontSize, lineHeight = fontSize),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

/** Icon inside a tinted circle. */
@Composable
fun IconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    container: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = tint,
        )
    }
}

/** Icon + text, used for metadata such as dates and locations. */
@Composable
fun MetaItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Dark gradient with two soft glows: the signature background of the profile hero. */
fun Modifier.heroBackground(colors: ResumeColors): Modifier = drawWithCache {
    val base = Brush.linearGradient(
        colors = listOf(colors.heroStart, colors.heroEnd),
        start = Offset.Zero,
        end = Offset(size.width, size.height),
    )
    val radius = (size.maxDimension * 0.75f).coerceAtLeast(1f)
    val glow = Brush.radialGradient(
        colors = listOf(colors.heroGlow.copy(alpha = 0.55f), Color.Transparent),
        center = Offset(size.width * 0.95f, 0f),
        radius = radius,
    )
    val secondaryGlow = Brush.radialGradient(
        colors = listOf(colors.heroGlowSecondary.copy(alpha = 0.30f), Color.Transparent),
        center = Offset(0f, size.height),
        radius = radius * 0.8f,
    )
    onDrawBehind {
        drawRect(base)
        drawRect(glow)
        drawRect(secondaryGlow)
    }
}

/** Opens [uri], ignoring the failure when no app can handle it (no mail client, no browser…). */
fun UriHandler.openSafely(uri: String) {
    try {
        openUri(uri)
    } catch (exception: IllegalArgumentException) {
        // Nothing can open this URI on the device: there is nothing more to do.
    }
}
