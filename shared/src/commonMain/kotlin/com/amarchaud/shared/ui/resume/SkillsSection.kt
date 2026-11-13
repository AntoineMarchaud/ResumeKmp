package com.amarchaud.shared.ui.resume

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.data.CareerSplit
import com.amarchaud.shared.data.ExperienceModel
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.data.TechnologyUsage
import com.amarchaud.shared.data.careerSplit
import com.amarchaud.shared.data.technologyUsage
import com.amarchaud.shared.ui.components.CountPill
import com.amarchaud.shared.ui.components.GroupTitle
import com.amarchaud.shared.ui.components.ResumeCard
import com.amarchaud.shared.ui.components.Reveal
import com.amarchaud.shared.ui.components.SectionHeader
import com.amarchaud.shared.ui.components.SectionList
import com.amarchaud.shared.ui.components.SkillChip
import com.amarchaud.shared.ui.components.TechChip
import com.amarchaud.shared.ui.components.rememberRevealState
import com.amarchaud.shared.ui.i18n.LocalStrings
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.theme.ResumeAppTheme
import kotlinx.coroutines.delay

private const val MAX_USAGE_ROWS = 6

@Composable
fun SkillsSection(
    resume: ResumeModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val revealState = rememberRevealState()
    val split = remember(resume) { resume.careerSplit() }
    val usage = remember(split) { split.focusPositions.technologyUsage().take(MAX_USAGE_ROWS) }
    val positions = split.focusPositions.size

    SectionList(contentPadding = contentPadding, modifier = modifier) {
        item(key = "header") {
            SectionHeader(
                title = LocalStrings.current.skillsTitle,
                subtitle = LocalStrings.current.skillsSubtitle,
                modifier = Modifier.padding(bottom = 20.dp),
                trailing = { CountPill(resume.skills.size.toString()) },
            )
        }
        item(key = "skills") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                resume.skills.forEachIndexed { index, skill ->
                    Reveal(key = "skill-$index", state = revealState, delayMillis = index * 30) {
                        SkillChip(text = skill)
                    }
                }
            }
        }
        if (usage.isNotEmpty() && positions > 0) {
            item(key = "usage") {
                Column(modifier = Modifier.padding(top = 36.dp)) {
                    GroupTitle(text = LocalStrings.current.skillsUsageTitle)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = usageSubtitle(split),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    UsageCard(usage = usage, totalPositions = positions)
                }
            }
        }
        if (split.otherPositions.isNotEmpty()) {
            item(key = "other-positions") {
                Column(modifier = Modifier.padding(top = 36.dp)) {
                    GroupTitle(text = otherPositionsTitle(split))
                    Spacer(modifier = Modifier.height(14.dp))
                    OtherPositionsCard(positions = split.otherPositions)
                }
            }
        }
    }
}

/** `Across my 8 Android positions, since 2014`, or the generic subtitle without focus. */
@Composable
private fun usageSubtitle(split: CareerSplit): String {
    val focus = split.focus
    val year = split.focusSinceYear
    return if (focus != null && year != null) {
        LocalStrings.current.skillsUsageFocusSubtitle(split.focusPositions.size, focus, year)
    } else {
        LocalStrings.current.skillsUsageSubtitle
    }
}

/** `Before Android · 2007 – 2014`. */
@Composable
private fun otherPositionsTitle(split: CareerSplit): String {
    val focus = split.focus
    val title = if (focus != null && split.othersAreEarlier) {
        LocalStrings.current.skillsEarlierTitle(focus)
    } else {
        LocalStrings.current.skillsOtherTitle
    }
    val years = split.otherYears?.let { if (it.first == it.last) "${it.first}" else "${it.first} – ${it.last}" }
    return if (years == null) title else "$title · $years"
}

/** The positions outside the focus career: one sentence, then their other technologies. */
@Composable
private fun OtherPositionsCard(positions: List<ExperienceModel>) {
    val usage = remember(positions) { positions.technologyUsage() }
    val main = usage.firstOrNull()
    ResumeCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (main == null) {
                    LocalStrings.current.positionsCount(positions.size)
                } else {
                    LocalStrings.current.skillsOtherSummary(positions.size, main.name, main.positions == positions.size)
                },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (usage.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    usage.drop(1).forEach { TechChip(text = it.name) }
                }
            }
        }
    }
}

@Composable
private fun UsageCard(usage: List<TechnologyUsage>, totalPositions: Int) {
    ResumeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            usage.forEachIndexed { index, technology ->
                UsageRow(
                    technology = technology,
                    fraction = technology.positions / totalPositions.toFloat(),
                    delayMillis = 200 + index * 80,
                )
            }
        }
    }
}

@Composable
private fun UsageRow(technology: TechnologyUsage, fraction: Float, delayMillis: Int) {
    val scheme = MaterialTheme.colorScheme
    val inspectionMode = LocalInspectionMode.current
    val progress = remember(technology) { Animatable(if (inspectionMode) fraction else 0f) }
    LaunchedEffect(technology, fraction) {
        delay(delayMillis.toLong())
        progress.animateTo(fraction, tween(durationMillis = 800, easing = EaseOutCubic))
    }
    val barBrush = Brush.horizontalGradient(listOf(scheme.primary, scheme.tertiary))

    Column {
        Row {
            Text(
                text = technology.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurface,
            )
            Text(
                text = LocalStrings.current.positionsCount(technology.positions),
                style = MaterialTheme.typography.labelMedium,
                color = scheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(scheme.surfaceContainerHigh)
                .drawBehind {
                    drawRoundRect(
                        brush = barBrush,
                        size = Size(size.width * progress.value, size.height),
                        cornerRadius = CornerRadius(size.height / 2f),
                    )
                },
        )
    }
}

@PreviewLightDark
@Composable
private fun SkillsSectionPreview() {
    ResumeAppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SkillsSection(resume = PreviewData.resume, contentPadding = PaddingValues(20.dp))
        }
    }
}
