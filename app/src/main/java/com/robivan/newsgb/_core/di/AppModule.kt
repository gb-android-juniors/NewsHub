package com.robivan.newsgb._core.di

import androidx.room.Room
import com.robivan.newsgb._core.data.api.ApiService
import com.robivan.newsgb._core.data.db.BookmarkDataBase
import com.robivan.newsgb._core.data.db.DbUtils.DB_NAME
import com.robivan.newsgb._core.ui.store.NewsStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single { ApiService.getInstance() }
    single { Room.databaseBuilder(androidApplication(), BookmarkDataBase::class.java, DB_NAME).build() }
    single { get<BookmarkDataBase>().bookmarkDao() }

    single { NewsStore() }
}


