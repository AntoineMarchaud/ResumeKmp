package com.amarchaud.shared.ui.resume

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.data.InfoModel
import com.amarchaud.shared.data.Language
import com.amarchaud.shared.data.StatModel
import com.amarchaud.shared.data.initialsOf
import com.amarchaud.shared.ui.components.BundledImage
import com.amarchaud.shared.ui.components.LocalProfilePhoto
import com.amarchaud.shared.ui.components.Monogram
import com.amarchaud.shared.ui.components.ResumeCard
import com.amarchaud.shared.ui.components.heroBackground
import com.amarchaud.shared.ui.i18n.LanguageFlag
import com.amarchaud.shared.ui.i18n.LocalStrings
import com.amarchaud.shared.ui.i18n.strings
import com.amarchaud.shared.ui.icons.AppIcons
import com.amarchaud.shared.ui.theme.ResumeTheme
import kotlin.math.roundToInt

/** Height of the header once fully collapsed (status bar excluded). */
internal val HeaderBarHeight = 64.dp

/** Material You toggle, `null` when dynamic colors are not available on the device. */
class DynamicColorAction(val enabled: Boolean, val onToggle: (Boolean) -> Unit)

/** Languages the resume is available in; the switch is shown when there are several. */
class LanguageAction(val selected: Language, val available: List<Language>, val onSelect: (Language) -> Unit)

/** Everything displayed at the top right of the hero banner. */
class HeaderActions(
    val dynamicColor: DynamicColorAction? = null,
    val language: LanguageAction? = null,
)

@Composable
private fun HeaderActionsRow(actions: HeaderActions, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.language?.takeIf { it.available.size > 1 }?.let { LanguageSwitch(it) }
        actions.dynamicColor?.let { DynamicColorToggle(it) }
    }
}

/** Small flags in a glass pill: the selected language is fully opaque and outlined. */
@Composable
private fun LanguageSwitch(action: LanguageAction) {
    val colors = ResumeTheme.colors
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.heroSurface)
            .border(1.dp, colors.heroOutline, CircleShape)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        action.available.forEach { language ->
            val selected = language == action.selected
            val flagShape = RoundedCornerShape(4.dp)
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .selectable(selected = selected, role = Role.RadioButton, onClick = { action.onSelect(language) })
                    .semantics { contentDescription = language.strings.languageName },
                contentAlignment = Alignment.Center,
            ) {
                LanguageFlag(
                    language = language,
                    modifier = Modifier
                        .size(width = 24.dp, height = 16.dp)
                        .graphicsLayer { alpha = if (selected) 1f else 0.45f }
                        .clip(flagShape)
                        .then(if (selected) Modifier.border(1.5.dp, colors.onHero, flagShape) else Modifier),
                )
            }
        }
    }
}

@Composable
fun ProfileAvatar(name: String, size: Dp, modifier: Modifier = Modifier) {
    val colors = ResumeTheme.colors
    Box(
        modifier = modifier
            .size(size)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(colors.onHero.copy(alpha = 0.9f), colors.onHero.copy(alpha = 0.15f))),
                shape = CircleShape,
            )
            .padding(4.dp),
    ) {
        when (val photo = LocalProfilePhoto.current) {
            is BundledImage.Loaded -> Image(
                bitmap = photo.image,
                contentDescription = null,
                modifier = Modifier
                    .size(size - 8.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            // Initials when there is no photo, or when it cannot be read.
            else -> Monogram(
                text = if (photo == BundledImage.Loading) "" else initialsOf(name),
                container = Brush.linearGradient(listOf(colors.heroGlow, colors.heroGlowSecondary)),
                contentColor = colors.onHero,
                size = size - 8.dp,
                shape = CircleShape,
                textStyle = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
private fun LocationChip(location: String, modifier: Modifier = Modifier) {
    val colors = ResumeTheme.colors
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.heroSurface)
            .border(1.dp, colors.heroOutline, CircleShape)
            .padding(start = 8.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = AppIcons.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = colors.onHeroMuted,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = location,
            style = MaterialTheme.typography.labelMedium,
            color = colors.onHero,
            maxLines = 1,
        )
    }
}

@Composable
private fun DynamicColorToggle(action: DynamicColorAction, modifier: Modifier = Modifier) {
    val colors = ResumeTheme.colors
    IconToggleButton(
        checked = action.enabled,
        onCheckedChange = action.onToggle,
        modifier = modifier,
        colors = IconButtonDefaults.iconToggleButtonColors(
            containerColor = colors.heroSurface,
            contentColor = colors.onHero,
            checkedContainerColor = colors.onHero,
            checkedContentColor = colors.heroStart,
        ),
    ) {
        Icon(
            imageVector = if (action.enabled) AppIcons.PaletteFilled else AppIcons.Palette,
            contentDescription = LocalStrings.current.headerDynamicColor,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** Location chip on the left, Material You toggle on the right. */
@Composable
private fun HeroTopRow(info: InfoModel, actions: HeaderActions, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            info.location?.let { LocationChip(it) }
        }
        HeaderActionsRow(actions)
    }
}

/** Minimum width of a stat tile; below it, the stats are stacked in a single card. */
private val MinStatTileWidth = 104.dp

/**
 * Key figures as three tiles, or as a stacked list when the available width is too narrow
 * (profile pane of an unfolded foldable, large font scales…).
 */
@Composable
fun StatsRow(stats: List<StatModel>, modifier: Modifier = Modifier) {
    val visibleStats = stats.take(3)
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val tileWidth = (maxWidth - StatSpacing * (visibleStats.size - 1)) / visibleStats.size
        if (tileWidth >= MinStatTileWidth) {
            StatTiles(visibleStats)
        } else {
            StatList(visibleStats)
        }
    }
}

private val StatSpacing = 10.dp
private val StatShape = RoundedCornerShape(18.dp)

@Composable
private fun StatTiles(stats: List<StatModel>) {
    val colors = ResumeTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(StatSpacing),
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(StatShape)
                    .background(colors.heroSurface)
                    .border(1.dp, colors.heroOutline, StatShape)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onHero,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onHeroMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatList(stats: List<StatModel>) {
    val colors = ResumeTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(StatShape)
            .background(colors.heroSurface)
            .border(1.dp, colors.heroOutline, StatShape)
            .padding(vertical = 6.dp),
    ) {
        stats.forEachIndexed { index, stat ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = colors.heroOutline,
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stat.value,
                    modifier = Modifier.widthIn(min = 72.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onHero,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onHeroMuted,
                )
            }
        }
    }
}

/** Avatar, name and job title. */
@Composable
private fun HeroIdentity(
    info: InfoModel,
    avatarSize: Dp,
    nameStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val colors = ResumeTheme.colors
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        ProfileAvatar(name = info.name, size = avatarSize)
        Spacer(modifier = Modifier.width(18.dp))
        Column {
            Text(text = info.name, style = nameStyle, color = colors.onHero)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = info.job,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onHeroMuted,
            )
        }
    }
}

/**
 * Hero header that collapses into a compact bar while the content scrolls (phones and
 * medium width windows). It plugs into a Material 3 [TopAppBarScrollBehavior].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingProfileHeader(
    info: InfoModel,
    stats: List<StatModel>,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: HeaderActions,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 24.dp,
) {
    val colors = ResumeTheme.colors
    val state = scrollBehavior.state
    val density = LocalDensity.current
    val barHeightPx = with(density) { HeaderBarHeight.roundToPx() }
    // Natural height of the expanded part, measured below; the estimate avoids a jump on first frame.
    var expandedHeightPx by remember { mutableIntStateOf(with(density) { 196.dp.roundToPx() }) }

    // Read during composition, not in the effect: a new measured height then recomposes and updates
    // the collapse limit, even when nothing else recomposes (e.g. back to portrait after a rotation).
    val heightOffsetLimit = -expandedHeightPx.toFloat()
    SideEffect {
        if (state.heightOffsetLimit != heightOffsetLimit) state.heightOffsetLimit = heightOffsetLimit
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                val radius = 32.dp.toPx() * (1f - state.collapsedFraction)
                shape = RoundedCornerShape(bottomStart = radius, bottomEnd = radius)
                clip = true
            }
            .heroBackground(colors)
            .statusBarsPadding()
            .layout { measurable, constraints ->
                val height = (barHeightPx + expandedHeightPx + state.heightOffset)
                    .roundToInt()
                    .coerceAtLeast(barHeightPx)
                val placeable = measurable.measure(constraints.copy(minHeight = height, maxHeight = height))
                layout(placeable.width, height) { placeable.place(0, 0) }
            }
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .padding(horizontal = horizontalPadding),
    ) {
        // Expanded content, laid out at its natural height and progressively clipped and faded.
        Column(
            modifier = Modifier
                .padding(top = HeaderBarHeight)
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .onSizeChanged { expandedHeightPx = it.height }
                .graphicsLayer {
                    alpha = (1f - state.collapsedFraction * 1.6f).coerceIn(0f, 1f)
                    translationY = state.heightOffset * 0.35f
                }
                .padding(top = 4.dp, bottom = 28.dp),
        ) {
            HeroIdentity(info = info, avatarSize = 76.dp, nameStyle = MaterialTheme.typography.headlineMedium)
            if (stats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                StatsRow(stats = stats)
            }
        }

        // Pinned bar: location in the expanded state, avatar and name once collapsed.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderBarHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                info.location?.let { location ->
                    LocationChip(
                        location = location,
                        modifier = Modifier.graphicsLayer {
                            alpha = (1f - state.collapsedFraction * 2.5f).coerceIn(0f, 1f)
                        },
                    )
                }
                Row(
                    modifier = Modifier
                        .clearAndSetSemantics { }
                        .graphicsLayer {
                            val progress = ((state.collapsedFraction - 0.55f) / 0.45f).coerceIn(0f, 1f)
                            alpha = progress
                            translationY = (1f - progress) * 12.dp.toPx()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProfileAvatar(name = info.name, size = 36.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = info.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onHero,
                            maxLines = 1,
                        )
                        Text(
                            text = info.job,
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onHeroMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            HeaderActionsRow(actions)
        }
    }
}

/**
 * Profile card of the two-pane layout (tablets, landscape, foldables in book posture): the identity
 * on a gradient banner, then the navigation. The card is only as tall as its content, so that the
 * resume itself stays the focus.
 */
@Composable
fun ProfilePane(
    info: InfoModel,
    stats: List<StatModel>,
    selected: ResumeSection,
    onSelect: (ResumeSection) -> Unit,
    actions: HeaderActions,
    modifier: Modifier = Modifier,
) {
    val colors = ResumeTheme.colors
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        ResumeCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heroBackground(colors)
                    .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
            ) {
                HeroTopRow(info = info, actions = actions)
                Spacer(modifier = Modifier.height(28.dp))
                ProfileAvatar(name = info.name, size = 96.dp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = info.name, style = MaterialTheme.typography.headlineMedium, color = colors.onHero)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = info.job, style = MaterialTheme.typography.bodyLarge, color = colors.onHeroMuted)
                if (stats.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    StatsRow(stats = stats)
                }
            }
            PaneNavigation(selected = selected, onSelect = onSelect, modifier = Modifier.padding(12.dp))
        }
    }
}

/** Top half of a foldable in tabletop posture: the profile stays above the hinge. */
@Composable
fun TabletopHero(
    info: InfoModel,
    stats: List<StatModel>,
    actions: HeaderActions,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .heroBackground(ResumeTheme.colors)
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .padding(horizontal = 32.dp, vertical = 16.dp),
    ) {
        HeroTopRow(info = info, actions = actions, modifier = Modifier.align(Alignment.TopCenter))
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeroIdentity(info = info, avatarSize = 84.dp, nameStyle = MaterialTheme.typography.headlineMedium)
            if (stats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                StatsRow(stats = stats, modifier = Modifier.widthIn(max = 560.dp))
            }
        }
    }
}
