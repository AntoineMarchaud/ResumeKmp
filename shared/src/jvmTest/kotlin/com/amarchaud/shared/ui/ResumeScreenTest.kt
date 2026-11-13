package com.amarchaud.shared.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.resume.ResumeLayout
import com.amarchaud.shared.ui.resume.ResumeScreen
import com.amarchaud.shared.ui.theme.ResumeAppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ResumeScreenTest {

    private fun tab(label: String) = isSelectable() and (hasText(label) or hasContentDescription(label))

    @Test
    fun opensOnAboutAndNavigatesBetweenSections() = runComposeUiTest {
        setContent {
            ResumeAppTheme {
                ResumeScreen(resume = PreviewData.resume, layout = ResumeLayout.Compact, dynamicColor = null)
            }
        }

        onNodeWithText("About me").assertIsDisplayed()
        onNode(tab("About")).assertIsSelected()

        onNode(tab("Experience")).performClick()
        onNodeWithText("Tap a position to see the details").assertIsDisplayed()
        onNodeWithText("Acme Mobile").assertIsDisplayed()
        // Plurals are formatted (Compose resources only support positional arguments such as %1$d).
        onNodeWithText("3 positions").assertIsDisplayed()

        onNode(tab("Skills")).performClick()
        onNodeWithText("Technologies and practices I work with").assertIsDisplayed()

        onNode(tab("Contact")).performClick()
        onNodeWithText("Let’s work together").assertIsDisplayed()
    }

    @Test
    fun largeScreensUseTheProfilePane() = runComposeUiTest {
        setContent {
            ResumeAppTheme {
                ResumeScreen(resume = PreviewData.resume, layout = ResumeLayout.TwoPane(), dynamicColor = null)
            }
        }

        onNode(tab("About")).assertIsSelected()
        onNode(tab("Skills")).performClick()
        onNode(tab("Skills")).assertIsSelected()
        onNodeWithText("Technologies and practices I work with").assertIsDisplayed()
    }
}
