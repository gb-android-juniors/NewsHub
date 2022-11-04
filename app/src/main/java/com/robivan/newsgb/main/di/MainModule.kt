package com.robivan.newsgb.main.di

import com.robivan.newsgb.main.data.MainRepositoryImpl
import com.robivan.newsgb.main.domain.MainRepository
import com.robivan.newsgb.main.domain.MainUseCases
import com.robivan.newsgb.main.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single<MainRepository> { MainRepositoryImpl(apiService = get()) }
    single { MainUseCases(bookmarkRepo = get(), mainRepo = get()) }
    viewModel { MainViewModel(useCases = get(), store = get()) }
}