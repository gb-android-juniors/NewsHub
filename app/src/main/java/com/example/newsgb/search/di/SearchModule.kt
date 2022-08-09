package com.example.newsgb.search.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.news.data.NewsRepositoryImpl
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.news.domain.NewsUseCases
import com.example.newsgb.search.data.SearchRepositoryImpl
import com.example.newsgb.search.domain.SearchRepository
import com.example.newsgb.search.domain.SearchUseCases
import com.example.newsgb.search.ui.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    single<SearchRepository> { SearchRepositoryImpl(apiService = get()) }
    single { SearchUseCases(bookmarkRepo = get(), searchRepo = get()) }
    viewModel { (store: NewsStore) -> SearchViewModel(useCases = get(), store = store) }
}