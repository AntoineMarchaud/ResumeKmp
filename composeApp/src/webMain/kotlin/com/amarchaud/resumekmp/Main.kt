package com.amarchaud.resumekmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.amarchaud.shared.App
import com.amarchaud.shared.di.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport(viewportContainerId = "app") {
        App(onDisplayed = ::hideLoadingScreen)
    }
}

/** Fades the HTML loading screen out (see `styles.css`), once the resume is displayed. */
private fun hideLoadingScreen() {
    document.getElementById("loading")?.classList?.add("hidden")
}
