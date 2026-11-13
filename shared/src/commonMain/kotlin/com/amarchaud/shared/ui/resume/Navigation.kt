package com.amarchaud.shared.ui.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.ui.i18n.LocalStrings
import com.amarchaud.shared.ui.i18n.Strings
import com.amarchaud.shared.ui.icons.AppIcons
import com.amarchaud.shared.ui.theme.ResumeTheme

enum class ResumeSection {
    About,
    Experience,
    Skills,
    Contact,
    ;

    fun label(strings: Strings): String = when (this) {
        About -> strings.sectionAbout
        Experience -> strings.sectionExperience
        Skills -> strings.sectionSkills
        Contact -> strings.sectionContact
    }

    fun icon(selected: Boolean): ImageVector = when (this) {
        About -> if (selected) AppIcons.PersonFilled else AppIcons.Person
        Experience -> if (selected) AppIcons.WorkFilled else AppIcons.Work
        Skills -> if (selected) AppIcons.BoltFilled else AppIcons.Bolt
        Contact -> if (selected) AppIcons.MailFilled else AppIcons.Mail
    }
}

/** Height of [FloatingNavigationBar], used to pad the content scrolling behind it. */
val FloatingNavigationBarHeight = 60.dp

/** Phone navigation: a floating pill whose selected item expands to reveal its label. */
@Composable
fun FloatingNavigationBar(
    selected: ResumeSection,
    onSelect: (ResumeSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(FloatingNavigationBarHeight),
        shape = CircleShape,
        color = ResumeTheme.colors.navigationContainer,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, ResumeTheme.colors.cardBorder),
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ResumeSection.entries.forEach { section ->
                val isSelected = section == selected
                val container by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "nav-container",
                )
                val content by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    label = "nav-content",
                )
                val label = section.label(LocalStrings.current)
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(container)
                        .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(section) })
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = section.icon(isSelected),
                        contentDescription = if (isSelected) null else label,
                        modifier = Modifier.size(22.dp),
                        tint = content,
                    )
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn() + expandHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        ),
                        exit = fadeOut() + shrinkHorizontally(),
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = content,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/** Medium width navigation (small tablets, unfolded foldables): a floating vertical rail. */
@Composable
fun FloatingNavigationRail(
    selected: ResumeSection,
    onSelect: (ResumeSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = ResumeTheme.colors.navigationContainer,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, ResumeTheme.colors.cardBorder),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 6.dp)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ResumeSection.entries.forEach { section ->
                val isSelected = section == selected
                val indicator by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "rail-indicator",
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    label = "rail-icon",
                )
                Column(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(section) })
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 52.dp, height = 32.dp)
                            .clip(CircleShape)
                            .background(indicator),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = section.icon(isSelected),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = iconTint,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = section.label(LocalStrings.current),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Large screen navigation, displayed in the profile card, below its banner. */
@Composable
fun PaneNavigation(
    selected: ResumeSection,
    onSelect: (ResumeSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ResumeSection.entries.forEach { section ->
            val isSelected = section == selected
            val container by animateColorAsState(
                targetValue = if (isSelected) scheme.primary.copy(alpha = 0.10f) else Color.Transparent,
                label = "pane-nav-container",
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(container)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(section) })
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = section.icon(isSelected),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = if (isSelected) scheme.primary else scheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = section.label(LocalStrings.current),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) scheme.primary else scheme.onSurface,
                )
                AnimatedVisibility(visible = isSelected, enter = fadeIn(), exit = fadeOut()) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(scheme.primary),
                    )
                }
            }
        }
    }
}
