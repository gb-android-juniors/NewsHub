package com.example.newsgb.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEffect
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.main.domain.MainUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val useCases: MainUseCases, private val store: NewsStore) :
    ViewModel() {

    init {
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
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

    companion object {
        private const val INITIAL_PAGE = 1
    }

}