package com.amarchaud.shared.ui.resume

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.data.EducationModel
import com.amarchaud.shared.data.ExperienceModel
import com.amarchaud.shared.data.LanguageModel
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.ui.components.BundledImage
import com.amarchaud.shared.ui.components.GroupTitle
import com.amarchaud.shared.ui.components.IconBadge
import com.amarchaud.shared.ui.components.LogoTile
import com.amarchaud.shared.ui.components.ResumeCard
import com.amarchaud.shared.ui.components.Reveal
import com.amarchaud.shared.ui.components.SectionHeader
import com.amarchaud.shared.ui.components.SectionList
import com.amarchaud.shared.ui.components.rememberBundledImage
import com.amarchaud.shared.ui.components.rememberRevealState
import com.amarchaud.shared.ui.components.staggerDelay
import com.amarchaud.shared.ui.i18n.LocalStrings
import com.amarchaud.shared.ui.icons.AppIcons
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.theme.ResumeAppTheme

@Composable
fun AboutSection(
    resume: ResumeModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val revealState = rememberRevealState()
    val summary = resume.about?.summary.orEmpty()
    val strengths = resume.about?.strengths.orEmpty()
    val clients = resume.experiences.filter { it.logo != null }.distinctBy { it.logo }

    SectionList(contentPadding = contentPadding, modifier = modifier) {
        item(key = "header") {
            SectionHeader(
                title = LocalStrings.current.aboutTitle,
                modifier = Modifier.padding(bottom = 20.dp),
            )
        }
        if (clients.isNotEmpty()) {
            item(key = "clients") {
                Reveal(key = "clients", state = revealState) {
                    ClientsCard(clients = clients, modifier = Modifier.padding(bottom = 14.dp))
                }
            }
        }
        if (summary.isNotEmpty()) {
            item(key = "summary") {
                Reveal(key = "summary", state = revealState, delayMillis = staggerDelay(1)) { SummaryCard(summary) }
            }
        }
        if (strengths.isNotEmpty()) {
            item(key = "strengths") {
                Reveal(key = "strengths", state = revealState, delayMillis = staggerDelay(2)) {
                    Column(modifier = Modifier.padding(top = 32.dp)) {
                        GroupTitle(text = LocalStrings.current.aboutStrengths)
                        Spacer(modifier = Modifier.height(14.dp))
                        StrengthsCard(strengths)
                    }
                }
            }
        }
        if (resume.education.isNotEmpty()) {
            item(key = "education") {
                Reveal(key = "education", state = revealState, delayMillis = staggerDelay(3)) {
                    Column(modifier = Modifier.padding(top = 32.dp)) {
                        GroupTitle(text = LocalStrings.current.aboutEducation)
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            resume.education.forEach { EducationCard(it) }
                        }
                    }
                }
            }
        }
        if (resume.languages.isNotEmpty()) {
            item(key = "languages") {
                Reveal(key = "languages", state = revealState, delayMillis = staggerDelay(4)) {
                    Column(modifier = Modifier.padding(top = 32.dp)) {
                        GroupTitle(text = LocalStrings.current.aboutLanguages)
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            resume.languages.forEach { LanguageCard(it, Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

/** Logos of the companies of the resume, visible from the first screen. */
@Composable
private fun ClientsCard(clients: List<ExperienceModel>, modifier: Modifier = Modifier) {
    ResumeCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = LocalStrings.current.aboutTrustedBy,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                clients.forEach { ClientLogo(it) }
            }
        }
    }
}

/** Companies whose logo cannot be loaded are left out, rather than shown as initials. */
@Composable
private fun ClientLogo(client: ExperienceModel) {
    when (val logo = rememberBundledImage(client.logo)) {
        is BundledImage.Loaded -> LogoTile(image = logo.image, height = ClientLogoHeight, contentDescription = client.company)
        BundledImage.Loading -> LogoTile(image = null, height = ClientLogoHeight)
        BundledImage.Missing -> Unit
    }
}

private val ClientLogoHeight = 40.dp

/** The first paragraph is used as a lead, the next ones as regular body text. */
@Composable
private fun SummaryCard(paragraphs: List<String>) {
    ResumeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            paragraphs.forEachIndexed { index, paragraph ->
                Text(
                    text = paragraph,
                    style = if (index == 0) {
                        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = if (index == 0) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun StrengthsCard(strengths: List<String>) {
    ResumeCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            strengths.forEach { strength ->
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconBadge(icon = AppIcons.Check, size = 28.dp, iconSize = 16.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = strength,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun EducationCard(education: EducationModel) {
    ResumeCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(18.dp)) {
            IconBadge(
                icon = AppIcons.School,
                size = 44.dp,
                iconSize = 22.dp,
                container = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = education.school,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = education.degree,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                education.period?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageCard(language: LanguageModel, modifier: Modifier = Modifier) {
    ResumeCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBadge(
                icon = AppIcons.Translate,
                size = 36.dp,
                iconSize = 18.dp,
                container = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                tint = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = language.level,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AboutSectionPreview() {
    ResumeAppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            AboutSection(resume = PreviewData.resume, contentPadding = PaddingValues(20.dp))
        }
    }
}
