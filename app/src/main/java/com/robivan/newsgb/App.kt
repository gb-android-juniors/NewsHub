package com.robivan.newsgb

import android.app.Application
import com.robivan.newsgb._core.di.appModule
import com.robivan.newsgb.article.di.articleModule
import com.robivan.newsgb.bookmarks.di.bookmarkModule
import com.robivan.newsgb.main.di.mainModule
import com.robivan.newsgb.news.di.newsModule
import com.robivan.newsgb.search.di.searchModule
import com.robivan.newsgb.settings.di.settingsModule
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