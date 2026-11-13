package com.amarchaud.templateresumekmp

import com.amarchaud.shared.di.initKoin
import com.amarchaud.shared.platform.resumeViewController
import platform.UIKit.UIViewController

/** Called once by the iOS app, when it launches. */
fun startKoin() = initKoin()

/**
 * View controller rendering the app, to embed in a parent view controller.
 * [onLightStatusBarIconsChange] receives `true` when the status bar icons must be light.
 */
fun MainViewController(onLightStatusBarIconsChange: (Boolean) -> Unit): UIViewController =
    resumeViewController(onLightStatusBarIconsChange)
