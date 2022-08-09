package com.example.newsgb

import android.app.Application
import com.example.newsgb._core.di.appModule
import com.example.newsgb.article.di.articleModule
import com.example.newsgb.bookmarks.di.bookmarkModule
import com.example.newsgb.main.di.mainModule
import com.example.newsgb.news.di.newsModule
import com.example.newsgb.search.di.searchModule
import com.example.newsgb.settings.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

     override fun onCreate() {
        super.onCreate()
        initKoin()
        instance = this
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                appModule,
                mainModule,
                newsModule,
                articleModule,
                bookmarkModule,
                settingsModule,
                searchModule
            )
        }
    }

    companion object {
        var instance: App? = null
            private set
        var countryCode: String = ""
    }
}