package com.example.newsgb.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.main.domain.MainUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val useCases: MainUseCases,
    private val store: NewsStore
) : ViewModel() {

    /**
     * метод запроса первой страницы главных новостей
     * сохраняем полученные состояние и данные в NewsStore
     * */
    fun getInitialData() {
        store.dispatch(event = AppEvent.Refresh)
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getInitialData(INITIAL_PAGE)
                .onSuccess { store.dispatch(AppEvent.DataReceived(data = it)) }
                .onFailure { store.dispatch(AppEvent.ErrorReceived(message = it.message)) }
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}