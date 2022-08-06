package com.example.newsgb.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEffect
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.main.domain.MainUseCases
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val useCases: MainUseCases,
    private val store: NewsStore
) : ViewModel() {

    init {
        /** При инициализации подписываемся на обновления команд от NewsStore */
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
    }

    private fun renderAppEffect(effect: AppEffect) {
        when (effect) {
            is AppEffect.LoadData -> getInitialData()
            else -> {}
        }
    }

    fun initData() {
        store.dispatch(event = AppEvent.Refresh)
    }

    /**
     * метод запроса первой страницы главных новостей
     * сохраняем полученные состояние и данные в NewsStore
     **/
    private fun getInitialData() {
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