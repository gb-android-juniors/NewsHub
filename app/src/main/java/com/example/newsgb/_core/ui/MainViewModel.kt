package com.example.newsgb._core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.news.ui.NewsDtoToUiMapper
import kotlinx.coroutines.launch

class MainViewModel(
    private val newsRepo: NewsRepository,
    private val mapper: NewsDtoToUiMapper,
    private val store: NewsStore
) : ViewModel() {

    /**
     * метод запроса первой страницы главных новостей
     * сохраняем полученные состояние и данные в NewsStore
     * */
    fun getBreakingNews() {
        store.dispatch(event = AppEvent.Refresh)
        viewModelScope.launch {
            newsRepo.getBreakingNews(page = INITIAL_PAGE)
                .onSuccess { response ->
                    store.dispatch(AppEvent.NewDataReceived(data = mapper(response.articles)))
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