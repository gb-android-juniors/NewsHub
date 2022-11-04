package com.robivan.newsgb.settings.di

import com.robivan.newsgb.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(useCases = get(), store = get()) }
}