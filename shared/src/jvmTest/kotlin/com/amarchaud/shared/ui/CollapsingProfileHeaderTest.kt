package com.amarchaud.shared.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.resume.CollapsingProfileHeader
import com.amarchaud.shared.ui.resume.HeaderActions
import com.amarchaud.shared.ui.resume.HeaderBarHeight
import com.amarchaud.shared.ui.theme.ResumeAppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3Api::class)
class CollapsingProfileHeaderTest {

    /**
     * Fully scrolled, the header shrinks down to its bar, whatever the first estimate of its expanded
     * height: the collapse limit must follow the measured height, even without any other recomposition
     * (e.g. when coming back to portrait after a rotation).
     */
    @Test
    fun collapsesDownToItsBar() = runComposeUiTest {
        lateinit var scrollBehavior: TopAppBarScrollBehavior
        setContent {
            ResumeAppTheme {
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                CollapsingProfileHeader(
                    info = PreviewData.resume.info,
                    stats = PreviewData.resume.info.stats,
                    scrollBehavior = scrollBehavior,
                    actions = HeaderActions(),
                    modifier = Modifier.testTag("header"),
                )
            }
        }
        waitForIdle()

        // As far as the collapse limit allows.
        scrollBehavior.state.heightOffset = -100_000f
        waitForIdle()

        val barHeight = with(density) { HeaderBarHeight.roundToPx() }
        assertEquals(barHeight, onNodeWithTag("header").fetchSemanticsNode().size.height)
    }
}
