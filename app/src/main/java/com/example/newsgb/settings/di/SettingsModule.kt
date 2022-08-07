package com.example.newsgb.settings.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { (store: NewsStore) -> SettingsViewModel(useCases = get(), store = store) }
}