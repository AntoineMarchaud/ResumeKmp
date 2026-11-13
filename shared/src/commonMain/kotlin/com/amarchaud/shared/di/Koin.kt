package com.amarchaud.shared.di

import com.amarchaud.shared.data.ResumeRepository
import com.amarchaud.shared.ui.ResumeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::ResumeRepository)
    viewModelOf(::ResumeViewModel)
}

/** Starts Koin. Called once by each platform entry point. */
fun initKoin() {
    startKoin {
        modules(sharedModule)
    }
}
