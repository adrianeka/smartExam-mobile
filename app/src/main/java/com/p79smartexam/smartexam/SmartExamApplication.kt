package com.p79smartexam.smartexam

import android.app.Application
import com.p79smartexam.smartexam.di.AppContainer
import com.p79smartexam.smartexam.di.DefaultAppContainer

class SmartExamApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
