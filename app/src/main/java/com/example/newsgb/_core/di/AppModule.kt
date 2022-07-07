package com.example.newsgb._core.di

import androidx.room.Room
import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.db.BookmarkDataBase
import com.example.newsgb._core.data.db.DbUtils.DB_NAME
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { ApiService.getInstance() }
    single { Room.databaseBuilder(androidApplication(), BookmarkDataBase::class.java, DB_NAME).build() }
    single { get<BookmarkDataBase>().bookmarkDao() }
}