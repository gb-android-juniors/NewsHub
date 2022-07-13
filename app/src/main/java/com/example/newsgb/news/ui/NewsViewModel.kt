package com.example.newsgb.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.ViewState
import com.example.newsgb.news.domain.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepo: NewsRepository,
    private val mapper: NewsDtoToUiMapper
) : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Default)
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    var pageNum = 1

    init {
        getBreakingNews()
    }

    private fun getBreakingNews() {
        _viewState.value = ViewState.Loading
        viewModelScope.launch {
            newsRepo.getBreakingNews(page = pageNum)
                .onSuccess { response ->
                    _viewState.value = ViewState.Success(mapper(response.articles))
                }
                .onFailure { ex ->
                    _viewState.value = ViewState.Error(ex.message)
                }
        }
    }
}