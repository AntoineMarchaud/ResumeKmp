package com.amarchaud.resumekmp

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.amarchaud.shared.App
import com.amarchaud.shared.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = DpSize(1280.dp, 820.dp)),
            title = "Resume",
        ) {
            App()
        }
    }
}
