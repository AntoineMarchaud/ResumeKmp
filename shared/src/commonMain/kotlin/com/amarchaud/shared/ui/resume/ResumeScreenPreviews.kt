package com.amarchaud.shared.ui.resume

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.amarchaud.shared.ui.preview.PreviewData
import com.amarchaud.shared.ui.theme.TemplateResumeTheme

/*
 * One preview per adaptive layout. The layout is passed explicitly because foldable postures
 * (book, tabletop) cannot be emulated by the preview renderer.
 */

private const val PHONE = "spec:width=411dp,height=891dp"
private const val PHONE_LANDSCAPE = "spec:width=891dp,height=411dp"
private const val TABLET_PORTRAIT = "spec:width=800dp,height=1280dp,dpi=320"
private const val TABLET_LANDSCAPE = "spec:width=1280dp,height=800dp,dpi=320"
private const val FOLDABLE = "spec:width=851dp,height=882dp,dpi=390"
private const val FOLDABLE_TABLETOP = "spec:width=882dp,height=851dp,dpi=390"

private class SectionProvider : PreviewParameterProvider<ResumeSection> {
    override val values = ResumeSection.entries.asSequence()
}

@Composable
private fun ResumePreview(layout: ResumeLayout, section: ResumeSection = ResumeSection.About) {
    TemplateResumeTheme {
        ResumeScreen(
            resume = PreviewData.resume,
            layout = layout,
            dynamicColor = DynamicColorAction(enabled = false, onToggle = {}),
            initialSection = section,
        )
    }
}

@Preview(name = "Phone", group = "Phone", device = PHONE, showSystemUi = true)
@Composable
private fun PhonePreview(@PreviewParameter(SectionProvider::class) section: ResumeSection) {
    ResumePreview(layout = ResumeLayout.Compact, section = section)
}

@Preview(
    name = "Phone - dark",
    group = "Phone",
    device = PHONE,
    showSystemUi = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_YES or AndroidUiModes.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun PhoneDarkPreview(@PreviewParameter(SectionProvider::class) section: ResumeSection) {
    ResumePreview(layout = ResumeLayout.Compact, section = section)
}

@Preview(name = "Phone - landscape", group = "Phone", device = PHONE_LANDSCAPE, showSystemUi = true)
@Composable
private fun PhoneLandscapePreview() {
    ResumePreview(layout = ResumeLayout.TwoPane(), section = ResumeSection.Experience)
}

@Preview(name = "Tablet - portrait", group = "Tablet", device = TABLET_PORTRAIT, showSystemUi = true)
@Composable
private fun TabletPortraitPreview() {
    ResumePreview(layout = ResumeLayout.Medium, section = ResumeSection.Experience)
}

@Preview(name = "Tablet - landscape", group = "Tablet", device = TABLET_LANDSCAPE, showSystemUi = true)
@Composable
private fun TabletLandscapePreview() {
    ResumePreview(layout = ResumeLayout.TwoPane(), section = ResumeSection.Experience)
}

@Preview(
    name = "Tablet - landscape dark",
    group = "Tablet",
    device = TABLET_LANDSCAPE,
    showSystemUi = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_YES or AndroidUiModes.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun TabletLandscapeDarkPreview() {
    ResumePreview(layout = ResumeLayout.TwoPane(), section = ResumeSection.Skills)
}

@Preview(name = "Foldable - unfolded", group = "Foldable", device = FOLDABLE, showSystemUi = true)
@Composable
private fun FoldableUnfoldedPreview() {
    ResumePreview(layout = ResumeLayout.TwoPane(), section = ResumeSection.About)
}

@Preview(name = "Foldable - book posture", group = "Foldable", device = FOLDABLE, showSystemUi = true)
@Composable
private fun FoldableBookPreview() {
    // Half-opened with a vertical hinge: the profile pane ends exactly at the fold.
    ResumePreview(
        layout = ResumeLayout.TwoPane(paneWidth = 425.dp, hingeWidth = 0.dp),
        section = ResumeSection.Experience,
    )
}

@Preview(name = "Foldable - tabletop posture", group = "Foldable", device = FOLDABLE_TABLETOP, showSystemUi = true)
@Composable
private fun FoldableTabletopPreview() {
    // Half-opened with a horizontal hinge: the profile stays above the fold.
    ResumePreview(
        layout = ResumeLayout.Tabletop(topHeight = 425.dp, hingeHeight = 0.dp),
        section = ResumeSection.Skills,
    )
}
