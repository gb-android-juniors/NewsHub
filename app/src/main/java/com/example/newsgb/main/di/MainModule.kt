package com.example.newsgb.main.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.main.data.MainRepositoryImpl
import com.example.newsgb.main.domain.MainRepository
import com.example.newsgb.main.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single<MainRepository> { MainRepositoryImpl(apiService = get()) }
    viewModel { (store: NewsStore) -> MainViewModel(mainRepo = get(), mapper = get(), store = store) }
}