package com.example.newsgb._core.di

import androidx.room.Room
import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.db.BookmarkDataBase
import com.example.newsgb._core.data.db.DbUtils.DB_NAME
import com.example.newsgb._core.ui.MainViewModel
import com.example.newsgb._core.ui.store.NewsStore
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { ApiService.getInstance() }
    single { Room.databaseBuilder(androidApplication(), BookmarkDataBase::class.java, DB_NAME).build() }
    single { get<BookmarkDataBase>().bookmarkDao() }

    factory { NewsStore() }
    viewModel { (store: NewsStore) -> MainViewModel(newsRepo = get(), mapper = get(), store = store) }
}



