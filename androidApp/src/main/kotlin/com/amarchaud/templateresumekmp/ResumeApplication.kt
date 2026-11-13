package com.amarchaud.templateresumekmp

import android.app.Application
import com.amarchaud.shared.di.initKoin

class ResumeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
