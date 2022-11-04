package com.robivan.newsgb._core.ui.model

sealed class SettingsViewState {
    object Data:SettingsViewState()
    object CountryLoading: SettingsViewState()
}