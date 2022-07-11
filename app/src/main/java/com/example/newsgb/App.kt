package com.example.newsgb

import android.app.Application
import com.example.newsgb._core.di.appModule
import com.example.newsgb.article.di.articleModule
import com.example.newsgb.bookmarks.di.bookmarkModule
import com.example.newsgb.news.di.newsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(appModule, newsModule, articleModule, bookmarkModule)
        }
    }
}