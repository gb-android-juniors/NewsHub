package com.robivan.newsgb.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robivan.newsgb._core.ui.model.AppEvent
import com.robivan.newsgb._core.ui.store.NewsStore
import com.robivan.newsgb.main.domain.MainUseCases
import kotlinx.coroutines.launch

class MainViewModel(
    private val useCases: MainUseCases,
    private val store: NewsStore
) : ViewModel() {

    fun getInitialData() {
        store.dispatch(event = AppEvent.Refresh)
        viewModelScope.launch {
            useCases.getInitialData(INITIAL_PAGE)
                .onSuccess { store.dispatch(AppEvent.DataReceived(data = it)) }
                .onFailure { store.dispatch(AppEvent.ErrorReceived(message = it.message)) }
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}