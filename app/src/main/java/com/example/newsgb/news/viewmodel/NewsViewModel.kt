package com.example.newsgb.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.data.api.NewsResponse
import com.example.newsgb._core.state.ViewState
import com.example.newsgb.news.domain.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepo: NewsRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.DefaultState)
    val viewState: StateFlow<ViewState?> = _viewState.asStateFlow()

    var hotPageNews = 1

    init {
        getBreakingNews()
    }

    fun getBreakingNews() {
        _viewState.value = ViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo.getBreakingNews(hotPageNews).run {
                onSuccess {
                    _viewState.value = ViewState.SuccessState(it)
                }
                onFailure {
                    _viewState.value = ViewState.ErrorState(it.message)
                }
            }
        }
    }
}