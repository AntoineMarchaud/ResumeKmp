package com.amarchaud.shared.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlinx.coroutines.delay

/** Maximum width of the readable content on large screens. */
val ContentMaxWidth = 760.dp

/**
 * Vertical list whose content is centered and capped to [ContentMaxWidth], while the whole area
 * stays scrollable (and keeps driving nested scroll) on tablets and unfolded foldables.
 *
 * Its text is selectable, so that it can be copied into an email or an applicant tracking system.
 */
@Composable
fun SectionList(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val start = contentPadding.calculateStartPadding(layoutDirection)
        val end = contentPadding.calculateEndPadding(layoutDirection)
        val extra = max(0.dp, (maxWidth - start - end - ContentMaxWidth) / 2)
        SelectionContainer(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentPadding = PaddingValues(
                    start = start + extra,
                    end = end + extra,
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding(),
                ),
                content = content,
            )
        }
    }
}

/** Keeps track of the items that already played their entrance animation. */
class RevealState internal constructor() {
    internal val revealed: MutableSet<Any> = mutableSetOf()
}

@Composable
fun rememberRevealState(): RevealState = remember { RevealState() }

/**
 * Fades and slides [content] in the first time it is composed. Items scrolled back into view
 * are displayed immediately.
 */
@Composable
fun Reveal(
    key: Any,
    state: RevealState,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable () -> Unit,
) {
    val inspectionMode = LocalInspectionMode.current
    val progress = remember(key) {
        Animatable(if (inspectionMode || key in state.revealed) 1f else 0f)
    }
    LaunchedEffect(key) {
        if (progress.value < 1f) {
            delay(delayMillis.toLong())
            progress.animateTo(1f, tween(durationMillis = 520, easing = EaseOutCubic))
        }
        state.revealed += key
    }
    Box(
        modifier = modifier.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * 28.dp.toPx()
        },
    ) {
        content()
    }
}

/** Stagger delay for the n-th item of a list, so only the first screen is choreographed. */
fun staggerDelay(index: Int, stepMillis: Int = 55, maxItems: Int = 8): Int =
    if (index < maxItems) index * stepMillis else 0
