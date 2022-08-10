package com.example.newsgb.settings.di

import com.example.newsgb.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(useCases = get(), store = get()) }
}