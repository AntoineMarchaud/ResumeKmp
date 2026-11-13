package com.amarchaud.shared.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runComposeUiTest
import com.amarchaud.shared.data.Language
import com.amarchaud.shared.ui.i18n.LocalStrings
import com.amarchaud.shared.ui.i18n.strings
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.resume.LanguageAction
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
        onNodeWithText("Open a position to see the details").assertIsDisplayed()
        onNodeWithText("Acme Mobile").assertIsDisplayed()
        // Plurals are formatted (Compose resources only support positional arguments such as %1$d).
        onNodeWithText("3 positions").assertIsDisplayed()

        onNode(tab("Skills")).performClick()
        onNodeWithText("Technologies and practices I work with").assertIsDisplayed()

        onNode(tab("Contact")).performClick()
        onNodeWithText("Let’s work together").assertIsDisplayed()
    }

    @Test
    fun headerStaysCollapsedWhenChangingSection() = runComposeUiTest {
        setContent {
            ResumeAppTheme {
                ResumeScreen(resume = PreviewData.resume, layout = ResumeLayout.Compact, dynamicColor = null)
            }
        }
        // The key figures only appear in the expanded header.
        onNodeWithText("Years in software").assertIsDisplayed()

        onRoot().performTouchInput {
            swipe(start = Offset(centerX, height * 0.75f), end = Offset(centerX, height * 0.15f), durationMillis = 400)
        }
        waitForIdle()
        onNodeWithText("Years in software").assertIsNotDisplayed()

        onNode(tab("Experience")).performClick()
        waitForIdle()
        onNodeWithText("Years in software").assertIsNotDisplayed()
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

    @Test
    fun switchesLanguageWithTheFlags() = runComposeUiTest {
        setContent {
            var language by remember { mutableStateOf(Language.English) }
            CompositionLocalProvider(LocalStrings provides language.strings) {
                ResumeAppTheme {
                    ResumeScreen(
                        resume = PreviewData.resume,
                        layout = ResumeLayout.Compact,
                        dynamicColor = null,
                        language = LanguageAction(language, Language.entries) { language = it },
                    )
                }
            }
        }

        onNodeWithText("About me").assertIsDisplayed()
        onNodeWithContentDescription("Français").performClick()
        onNodeWithText("À propos").assertIsDisplayed()
        onNode(tab("Profil")).assertIsSelected()
    }
}
