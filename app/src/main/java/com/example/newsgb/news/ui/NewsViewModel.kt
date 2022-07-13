package com.example.newsgb.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppState
import com.example.newsgb._core.ui.model.ViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.news.domain.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepo: NewsRepository,
    private val mapper: NewsDtoToUiMapper,
    private val store: NewsStore
) : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Default)
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    init {
        /**
         * При инициализации подписываемся на обновления состояний NewsStore
         * */
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
    }

    /**
     * метод обработки состояний NewsStore
     * конвертируем состояния приложения в состояния экрана
     * также в случае успеха конвертируем поулченные данные с помощью маппера
     * */
    private fun renderStoreState(storeState: AppState) {
        when(storeState) {
            AppState.Default -> _viewState.value = ViewState.Default
            AppState.Loading -> _viewState.value = ViewState.Loading
            is AppState.Success -> _viewState.value = ViewState.Success(data = mapper(storeState.data))
            is AppState.Error -> _viewState.value = ViewState.Error(message = storeState.message)
        }
    }
}