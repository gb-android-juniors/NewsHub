package com.example.newsgb.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.model.AppState
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepo: NewsRepository,
    private val mapper: NewsDtoToUiMapper,
    private val store: NewsStore,
    private val category: Category
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
     * */
    private fun renderStoreState(storeState: AppState) {
        when(storeState) {
            AppState.Default -> _viewState.value = ViewState.Default
            AppState.Loading -> _viewState.value = ViewState.Loading
            is AppState.Success -> setSuccessState(data = storeState.data)
            is AppState.Error -> _viewState.value = ViewState.Error(message = storeState.message)
        }
    }

    /**
     * отфильтровываем список данных из стора по категории и
     * передаем их в состояние экрана ViewState.Success */
    private fun setSuccessState(data: List<Article>) {
        val filteredData = data.filter { it.category == category }
        _viewState.value = ViewState.Success(data = filteredData)
    }

    fun getData() {
        val currentStoreState = store.storeState.value
        if (currentStoreState is AppState.Success) {
            val filteredData = currentStoreState.data.filter { it.category == category }
            if (filteredData.isEmpty()) {
                getNewsByCategory()
            } else {
                _viewState.value = ViewState.Success(data = filteredData)
            }
        }
    }

    /**
     * метод запроса первой страницы новостей по категории.
     * В случае успеха конвертируем поулченные данные с помощью маппера и
     * передаем в качестве успешного события AppEvent.MoreDataReceived в NewsStore
     * */
    private fun getNewsByCategory() {
        store.dispatch(event = AppEvent.Refresh)
        viewModelScope.launch {
            newsRepo.getNewsByCategory(page = INITIAL_PAGE, countryCode = "ru", category = category.apiCode)
                .onSuccess { response ->
                    store.dispatch(AppEvent.MoreDataReceived(data = mapper(newsList = response.articles, category = category)))
                }
                .onFailure { ex ->
                    store.dispatch(AppEvent.ErrorReceived(message = ex.message))
                }
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}