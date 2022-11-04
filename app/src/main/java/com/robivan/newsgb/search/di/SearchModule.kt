package com.robivan.newsgb.search.di

import com.robivan.newsgb.search.data.SearchRepositoryImpl
import com.robivan.newsgb.search.domain.SearchRepository
import com.robivan.newsgb.search.domain.SearchUseCases
import com.robivan.newsgb.search.ui.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    single<SearchRepository> { SearchRepositoryImpl(apiService = get()) }
    single { SearchUseCases(bookmarkRepo = get(), searchRepo = get()) }
    viewModel { SearchViewModel(useCases = get(), store = get()) }
}