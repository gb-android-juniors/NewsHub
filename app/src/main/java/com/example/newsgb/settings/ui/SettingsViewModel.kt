package com.example.newsgb.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEffect
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.model.AppState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.*

class SettingsViewModel(private val store: NewsStore): ViewModel() {

    init {
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
    }

    private fun renderAppEffect(effect: AppEffect) {
        when(effect) {
            is AppEffect.LoadData -> refreshStore()
            else ->{}
        }
    }

    private fun refreshStore() {
        val currentState = store.storeState.value
        if (currentState is AppState.Refreshing) {
            val filteredData = currentState.data.filter { it.isChecked }.map { it.copy(category = Category.BOOKMARKS) }
            store.dispatch(AppEvent.DataReceived(data = filteredData))
        }
    }

    fun refreshData() {
        store.dispatch(event = AppEvent.Refresh)
    }

}