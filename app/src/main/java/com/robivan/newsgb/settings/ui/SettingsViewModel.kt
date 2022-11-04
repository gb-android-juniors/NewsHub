package com.robivan.newsgb.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robivan.newsgb._core.ui.model.AppEffect
import com.robivan.newsgb._core.ui.model.AppEvent
import com.robivan.newsgb._core.ui.model.AppState
import com.robivan.newsgb._core.ui.model.SettingsViewState
import com.robivan.newsgb._core.ui.store.NewsStore
import com.robivan.newsgb.main.domain.MainUseCases
import com.robivan.newsgb.utils.Constants.Companion.INITIAL_PAGE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val useCases: MainUseCases, private val store: NewsStore) :
    ViewModel() {

    private val _viewState = MutableStateFlow<SettingsViewState>(SettingsViewState.Data)
    val viewState: StateFlow<SettingsViewState> = _viewState.asStateFlow()

    init {
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
    }

    private fun renderStoreState(appState: AppState) {
        when(appState) {
            is AppState.Refreshing -> _viewState.value = SettingsViewState.CountryLoading
            else -> _viewState.value = SettingsViewState.Data
        }
    }

    private fun renderAppEffect(effect: AppEffect) {
        when (effect) {
            is AppEffect.LoadData -> getInitialData()
            else -> {}
        }
    }

    private fun getInitialData() {
        viewModelScope.launch {
            useCases.getInitialData(INITIAL_PAGE)
                .onSuccess { store.dispatch(AppEvent.DataReceived(data = it)) }
                .onFailure { store.dispatch(AppEvent.ErrorReceived(message = it.message)) }
        }
    }

    fun refreshData() {
        store.dispatch(event = AppEvent.Refresh)
    }
}