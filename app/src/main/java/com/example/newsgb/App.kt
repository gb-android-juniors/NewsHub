package com.example.newsgb

import android.app.Application
import com.example.newsgb._core.di.appModule
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            modules(appModule)
        }
    }
}