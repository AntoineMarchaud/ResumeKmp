package com.amarchaud.shared.ui.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.data.ExperienceModel
import com.amarchaud.shared.data.currentYearMonth
import com.amarchaud.shared.data.durationInMonths
import com.amarchaud.shared.data.formattedPeriod
import com.amarchaud.shared.data.initialsOf
import com.amarchaud.shared.data.isCurrent
import com.amarchaud.shared.data.splitDuration
import com.amarchaud.shared.resources.Res
import com.amarchaud.shared.resources.duration_months
import com.amarchaud.shared.resources.duration_years
import com.amarchaud.shared.resources.experience_collapse
import com.amarchaud.shared.resources.experience_current
import com.amarchaud.shared.resources.experience_expand
import com.amarchaud.shared.resources.experience_hint
import com.amarchaud.shared.resources.experience_present
import com.amarchaud.shared.resources.experience_title
import com.amarchaud.shared.resources.experience_view_on_play
import com.amarchaud.shared.resources.positions_count
import com.amarchaud.shared.ui.components.CountPill
import com.amarchaud.shared.ui.components.CurrentBadge
import com.amarchaud.shared.ui.components.MetaItem
import com.amarchaud.shared.ui.components.Monogram
import com.amarchaud.shared.ui.components.openSafely
import com.amarchaud.shared.ui.components.ResumeCard
import com.amarchaud.shared.ui.components.Reveal
import com.amarchaud.shared.ui.components.SectionHeader
import com.amarchaud.shared.ui.components.SectionList
import com.amarchaud.shared.ui.components.TechChip
import com.amarchaud.shared.ui.components.rememberRevealState
import com.amarchaud.shared.ui.components.staggerDelay
import com.amarchaud.shared.ui.icons.AppIcons
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.theme.ResumeTheme
import com.amarchaud.shared.ui.theme.ResumeAppTheme
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="

/** Width of the timeline rail drawn on the left of the cards. */
private val RailWidth = 28.dp

/** Vertical position of a timeline dot: aligned with the center of the company monogram. */
private val DotOffset = 40.dp

@Composable
fun ExperienceSection(
    experiences: List<ExperienceModel>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val revealState = rememberRevealState()
    val now = remember { currentYearMonth() }

    SectionList(contentPadding = contentPadding, modifier = modifier) {
        item(key = "header") {
            SectionHeader(
                title = stringResource(Res.string.experience_title),
                subtitle = stringResource(Res.string.experience_hint),
                modifier = Modifier.padding(bottom = 20.dp),
                trailing = {
                    CountPill(pluralStringResource(Res.plurals.positions_count, experiences.size, experiences.size))
                },
            )
        }
        itemsIndexed(
            items = experiences,
            key = { index, experience -> "$index-${experience.company}" },
        ) { index, experience ->
            Reveal(key = index, state = revealState, delayMillis = staggerDelay(index)) {
                TimelineEntry(
                    experience = experience,
                    index = index,
                    isFirst = index == 0,
                    isLast = index == experiences.lastIndex,
                    now = now,
                )
            }
        }
    }
}

@Composable
private fun TimelineEntry(
    experience: ExperienceModel,
    index: Int,
    isFirst: Boolean,
    isLast: Boolean,
    now: YearMonth,
) {
    val scheme = MaterialTheme.colorScheme
    val railColor = scheme.outlineVariant
    val dotColor = if (experience.isCurrent) scheme.primary else scheme.outline
    val background = scheme.background
    val isCurrent = experience.isCurrent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val x = RailWidth.toPx() / 2f
                val dotY = DotOffset.toPx()
                val top = if (isFirst) dotY else 0f
                val bottom = if (isLast) dotY else size.height
                if (bottom > top) {
                    drawLine(
                        color = railColor,
                        start = Offset(x, top),
                        end = Offset(x, bottom),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                if (isCurrent) {
                    drawCircle(dotColor.copy(alpha = 0.18f), radius = 11.dp.toPx(), center = Offset(x, dotY))
                    drawCircle(dotColor, radius = 6.dp.toPx(), center = Offset(x, dotY))
                } else {
                    drawCircle(background, radius = 6.dp.toPx(), center = Offset(x, dotY))
                    drawCircle(
                        color = dotColor,
                        radius = 5.dp.toPx(),
                        center = Offset(x, dotY),
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            },
    ) {
        Spacer(modifier = Modifier.width(RailWidth + 8.dp))
        ExperienceCard(
            experience = experience,
            index = index,
            initiallyExpanded = index == 0,
            now = now,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 14.dp),
        )
    }
}

@Composable
private fun ExperienceCard(
    experience: ExperienceModel,
    index: Int,
    initiallyExpanded: Boolean,
    now: YearMonth,
    modifier: Modifier = Modifier,
) {
    val hasDetails = experience.description != null || experience.highlights.isNotEmpty() || experience.appId != null
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded && hasDetails) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "chevron",
    )

    ResumeCard(
        modifier = modifier,
        onClick = if (hasDetails) {
            { expanded = !expanded }
        } else {
            null
        },
        onClickLabel = stringResource(if (expanded) Res.string.experience_collapse else Res.string.experience_expand),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompanyMonogram(experience = experience, index = index)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = experience.company,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = experience.role,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (hasDetails) {
                    Icon(
                        imageVector = AppIcons.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(chevronRotation),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                if (experience.isCurrent) {
                    CurrentBadge(text = stringResource(Res.string.experience_current))
                }
                periodLabel(experience, now)?.let { MetaItem(icon = AppIcons.CalendarMonth, text = it) }
                experience.location?.let { MetaItem(icon = AppIcons.LocationOn, text = it) }
            }

            if (experience.technologies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    experience.technologies.forEach { TechChip(text = it) }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                ) + fadeOut(),
            ) {
                ExperienceDetails(experience = experience)
            }
        }
    }
}

@Composable
private fun CompanyMonogram(experience: ExperienceModel, index: Int) {
    val scheme = MaterialTheme.colorScheme
    val colors = ResumeTheme.colors
    val (container, content) = when {
        experience.isCurrent -> Brush.linearGradient(listOf(colors.heroEnd, colors.heroGlow)) to colors.onHero
        index % 3 == 1 -> SolidColor(scheme.primaryContainer) to scheme.onPrimaryContainer
        index % 3 == 2 -> SolidColor(scheme.tertiaryContainer) to scheme.onTertiaryContainer
        else -> SolidColor(scheme.secondaryContainer) to scheme.onSecondaryContainer
    }
    Monogram(text = initialsOf(experience.company), container = container, contentColor = content)
}

@Composable
private fun ExperienceDetails(experience: ExperienceModel) {
    val uriHandler = LocalUriHandler.current
    Column(modifier = Modifier.padding(top = 18.dp)) {
        HorizontalDivider(color = ResumeTheme.colors.cardBorder)
        Spacer(modifier = Modifier.height(16.dp))
        experience.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        experience.highlights.forEach { highlight ->
            Row(modifier = Modifier.padding(vertical = 5.dp)) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = highlight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        experience.appId?.let { appId ->
            Spacer(modifier = Modifier.height(14.dp))
            FilledTonalButton(
                onClick = { uriHandler.openSafely(PLAY_STORE_URL + appId) },
                contentPadding = PaddingValues(start = 16.dp, end = 18.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Icon(imageVector = AppIcons.Shop, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(Res.string.experience_view_on_play))
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = AppIcons.ArrowOutward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/** `Sep 2025 – Aug 2026 · 1 yr`. */
@Composable
private fun periodLabel(experience: ExperienceModel, now: YearMonth): String? {
    val period = experience.formattedPeriod(stringResource(Res.string.experience_present)) ?: return null
    val months = experience.durationInMonths(now) ?: return period
    val (years, remainingMonths) = splitDuration(months)
    val duration = buildList {
        if (years > 0) add(pluralStringResource(Res.plurals.duration_years, years, years))
        if (remainingMonths > 0) add(pluralStringResource(Res.plurals.duration_months, remainingMonths, remainingMonths))
    }.joinToString(separator = " ")
    return if (duration.isEmpty()) period else "$period · $duration"
}

@PreviewLightDark
@Composable
private fun ExperienceSectionPreview() {
    ResumeAppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ExperienceSection(
                experiences = PreviewData.resume.experiences,
                contentPadding = PaddingValues(20.dp),
            )
        }
    }
}
