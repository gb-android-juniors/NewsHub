package com.robivan.newsgb.news.di

import com.robivan.newsgb.news.data.NewsRepositoryImpl
import com.robivan.newsgb.news.domain.NewsRepository
import com.robivan.newsgb.news.domain.NewsUseCases
import com.robivan.newsgb.news.ui.NewsViewModel
import com.robivan.newsgb.utils.ui.Category
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