package com.example.newsgb.news.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.news.data.NewsRepositoryImpl
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.news.ui.NewsViewModel
import com.example.newsgb.utils.ui.Category
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newsModule = module {
    single<NewsRepository> { NewsRepositoryImpl(apiService = get()) }
    viewModel { (store: NewsStore, category: Category) ->
        NewsViewModel(
            bookmarkRepo = get(),
            newsRepo = get(),
            mapper = get(),
            store = store,
            category = category
        )
    }
}