package com.example.newsgb.search.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.search.ui.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    viewModel { (store: NewsStore) -> SearchViewModel(store = store) }
}