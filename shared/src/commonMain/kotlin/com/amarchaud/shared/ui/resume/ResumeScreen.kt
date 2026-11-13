package com.amarchaud.shared.ui.resume

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.data.StatModel
import com.amarchaud.shared.platform.SystemBarsAppearance
import com.amarchaud.shared.resources.Res
import com.amarchaud.shared.resources.stat_apps
import com.amarchaud.shared.resources.stat_positions
import com.amarchaud.shared.resources.stat_skills
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResumeScreen(
    resume: ResumeModel,
    dynamicColor: DynamicColorAction?,
    modifier: Modifier = Modifier,
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val density = LocalDensity.current
    val layout = remember(adaptiveInfo, density) { resumeLayoutFor(adaptiveInfo, density) }
    ResumeScreen(resume = resume, layout = layout, dynamicColor = dynamicColor, modifier = modifier)
}

/** Renders the resume with an explicit [layout]; used directly by previews and tests. */
@Composable
internal fun ResumeScreen(
    resume: ResumeModel,
    layout: ResumeLayout,
    dynamicColor: DynamicColorAction?,
    modifier: Modifier = Modifier,
    initialSection: ResumeSection = ResumeSection.About,
) {
    var section by rememberSaveable { mutableStateOf(initialSection) }
    val stats = headerStats(resume)

    // The hero sits behind the status bar, except in the two-pane layout where the content does.
    val darkTheme = isSystemInDarkTheme()
    SystemBarsAppearance(
        lightStatusBarIcons = layout !is ResumeLayout.TwoPane || darkTheme,
        darkTheme = darkTheme,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (layout) {
            ResumeLayout.Compact, ResumeLayout.Medium -> CollapsingLayout(
                resume = resume,
                stats = stats,
                section = section,
                onSectionSelected = { section = it },
                dynamicColor = dynamicColor,
                useRail = layout == ResumeLayout.Medium,
            )

            is ResumeLayout.TwoPane -> TwoPaneLayout(
                resume = resume,
                stats = stats,
                section = section,
                onSectionSelected = { section = it },
                dynamicColor = dynamicColor,
                paneWidth = layout.paneWidth,
                hingeWidth = layout.hingeWidth,
            )

            is ResumeLayout.Tabletop -> TabletopLayout(
                resume = resume,
                stats = stats,
                section = section,
                onSectionSelected = { section = it },
                dynamicColor = dynamicColor,
                topHeight = layout.topHeight,
                hingeHeight = layout.hingeHeight,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsingLayout(
    resume: ResumeModel,
    stats: List<StatModel>,
    section: ResumeSection,
    onSectionSelected: (ResumeSection) -> Unit,
    dynamicColor: DynamicColorAction?,
    useRail: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navigationBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // A new section starts from the top: bring the header back.
    LaunchedEffect(section) {
        val state = scrollBehavior.state
        if (state.heightOffset != 0f) {
            animate(initialValue = state.heightOffset, targetValue = 0f) { value, _ -> state.heightOffset = value }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CollapsingProfileHeader(
                info = resume.info,
                stats = stats,
                scrollBehavior = scrollBehavior,
                dynamicColor = dynamicColor,
                horizontalPadding = if (useRail) 32.dp else 24.dp,
            )
            if (useRail) {
                Row(modifier = Modifier.weight(1f)) {
                    FloatingNavigationRail(
                        selected = section,
                        onSelect = onSectionSelected,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
                            .padding(start = 16.dp),
                    )
                    SectionsHost(
                        section = section,
                        resume = resume,
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 32.dp,
                            top = 28.dp,
                            bottom = 32.dp + navigationBarsBottom,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                SectionsHost(
                    section = section,
                    resume = resume,
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = FloatingNavigationBarHeight + 40.dp + navigationBarsBottom,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (!useRail) {
            BottomNavigation(
                section = section,
                onSectionSelected = onSectionSelected,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun TwoPaneLayout(
    resume: ResumeModel,
    stats: List<StatModel>,
    section: ResumeSection,
    onSectionSelected: (ResumeSection) -> Unit,
    dynamicColor: DynamicColorAction?,
    paneWidth: Dp?,
    hingeWidth: Dp,
) {
    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = paneWidth ?: (maxWidth * 0.36f).coerceIn(320.dp, 420.dp)
        Row(modifier = Modifier.fillMaxSize()) {
            ProfilePane(
                info = resume.info,
                stats = stats,
                selected = section,
                onSelect = onSectionSelected,
                dynamicColor = dynamicColor,
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight(),
            )
            Spacer(modifier = Modifier.width(hingeWidth))
            SectionsHost(
                section = section,
                resume = resume,
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 32.dp + safeDrawing.calculateEndPadding(layoutDirection),
                    top = 32.dp + safeDrawing.calculateTopPadding(),
                    bottom = 32.dp + safeDrawing.calculateBottomPadding(),
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TabletopLayout(
    resume: ResumeModel,
    stats: List<StatModel>,
    section: ResumeSection,
    onSectionSelected: (ResumeSection) -> Unit,
    dynamicColor: DynamicColorAction?,
    topHeight: Dp,
    hingeHeight: Dp,
) {
    val navigationBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(modifier = Modifier.fillMaxSize()) {
        TabletopHero(
            info = resume.info,
            stats = stats,
            dynamicColor = dynamicColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(topHeight),
        )
        Spacer(modifier = Modifier.height(hingeHeight))
        Box(modifier = Modifier.weight(1f)) {
            SectionsHost(
                section = section,
                resume = resume,
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = FloatingNavigationBarHeight + 40.dp + navigationBarsBottom,
                ),
                modifier = Modifier.fillMaxSize(),
            )
            BottomNavigation(
                section = section,
                onSectionSelected = onSectionSelected,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/** Floating bar over a fade, so the content scrolling behind it stays legible. */
@Composable
private fun BottomNavigation(
    section: ResumeSection,
    onSectionSelected: (ResumeSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.5f to background.copy(alpha = 0.85f),
                    1f to background.copy(alpha = 0.95f),
                ),
            )
            .navigationBarsPadding()
            .padding(top = 28.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        FloatingNavigationBar(selected = section, onSelect = onSectionSelected)
    }
}

@Composable
private fun SectionsHost(
    section: ResumeSection,
    resume: ResumeModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = section,
        modifier = modifier,
        transitionSpec = {
            val forward = targetState.ordinal > initialState.ordinal
            (fadeIn(tween(durationMillis = 260, delayMillis = 60)) +
                slideInVertically(tween(durationMillis = 360)) { height -> if (forward) height / 16 else -height / 16 })
                .togetherWith(fadeOut(tween(durationMillis = 120)))
        },
        label = "section",
    ) { target ->
        when (target) {
            ResumeSection.About -> AboutSection(resume = resume, contentPadding = contentPadding)
            ResumeSection.Experience -> ExperienceSection(
                experiences = resume.experiences,
                contentPadding = contentPadding,
            )
            ResumeSection.Skills -> SkillsSection(resume = resume, contentPadding = contentPadding)
            ResumeSection.Contact -> ContactSection(contact = resume.contact, contentPadding = contentPadding)
        }
    }
}

/** Key figures of the header: the ones from the JSON, or figures computed from the resume. */
@Composable
private fun headerStats(resume: ResumeModel): List<StatModel> {
    if (resume.info.stats.isNotEmpty()) return resume.info.stats
    return listOf(
        StatModel(resume.experiences.size.toString(), stringResource(Res.string.stat_positions)),
        StatModel(resume.experiences.count { it.appId != null }.toString(), stringResource(Res.string.stat_apps)),
        StatModel(resume.skills.size.toString(), stringResource(Res.string.stat_skills)),
    )
}
