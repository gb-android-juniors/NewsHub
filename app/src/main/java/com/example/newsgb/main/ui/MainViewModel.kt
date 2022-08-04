package com.example.newsgb.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.main.domain.MainUseCases
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
        viewModelScope.launch {
            val event = useCases.getInitialData(initialPage = INITIAL_PAGE)
            store.dispatch(event)
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}