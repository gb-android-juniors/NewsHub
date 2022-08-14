package com.example.newsgb.news.di

import com.example.newsgb.news.data.NewsRepositoryImpl
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.news.domain.NewsUseCases
import com.example.newsgb.news.ui.NewsViewModel
import com.example.newsgb.utils.ui.Category
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newsModule = module {
    single<NewsRepository> { NewsRepositoryImpl(apiService = get()) }
    single { NewsUseCases(bookmarkRepo = get(), newsRepo = get()) }
    viewModel { (category: Category) ->
        NewsViewModel(
            useCases = get(),
            store = get(),
            category = category
        )
    }
}