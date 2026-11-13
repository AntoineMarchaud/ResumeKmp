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
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.data.TechnologyUsage
import com.amarchaud.shared.data.technologyUsage
import com.amarchaud.shared.resources.Res
import com.amarchaud.shared.resources.positions_count
import com.amarchaud.shared.resources.skills_subtitle
import com.amarchaud.shared.resources.skills_title
import com.amarchaud.shared.resources.skills_usage_subtitle
import com.amarchaud.shared.resources.skills_usage_title
import com.amarchaud.shared.ui.components.CountPill
import com.amarchaud.shared.ui.components.GroupTitle
import com.amarchaud.shared.ui.components.ResumeCard
import com.amarchaud.shared.ui.components.Reveal
import com.amarchaud.shared.ui.components.SectionHeader
import com.amarchaud.shared.ui.components.SectionList
import com.amarchaud.shared.ui.components.SkillChip
import com.amarchaud.shared.ui.components.rememberRevealState
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.theme.TemplateResumeTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val MAX_USAGE_ROWS = 6

@Composable
fun SkillsSection(
    resume: ResumeModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val revealState = rememberRevealState()
    val usage = remember(resume) { resume.technologyUsage().take(MAX_USAGE_ROWS) }
    val positions = resume.experiences.size

    SectionList(contentPadding = contentPadding, modifier = modifier) {
        item(key = "header") {
            SectionHeader(
                title = stringResource(Res.string.skills_title),
                subtitle = stringResource(Res.string.skills_subtitle),
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
                    GroupTitle(text = stringResource(Res.string.skills_usage_title))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(Res.string.skills_usage_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    UsageCard(usage = usage, totalPositions = positions)
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
                text = pluralStringResource(Res.plurals.positions_count, technology.positions, technology.positions),
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
    TemplateResumeTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SkillsSection(resume = PreviewData.resume, contentPadding = PaddingValues(20.dp))
        }
    }
}
