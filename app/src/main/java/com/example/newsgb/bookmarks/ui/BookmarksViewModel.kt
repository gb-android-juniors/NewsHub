package com.example.newsgb.bookmarks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppState
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookmarksViewModel(
    private val bookmarkRepo: BookmarkRepository
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<AppState>(AppState.Empty)
    val stateFlow: StateFlow<AppState> = _stateFlow.asStateFlow()

    fun renderData() {
        _stateFlow.value = AppState.Loading
        viewModelScope.launch {
            val bookmarksList = bookmarkRepo.getAllBookmarks()
            _stateFlow.value = if (bookmarksList.isEmpty()) {
                AppState.Empty
            } else {
                AppState.Data(bookmarksList)
            }
        }
    }

    fun deleteBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkRepo.removeBookmark(article)
            renderData()
        }
    }

    fun clearBookmarks() {
        viewModelScope.launch {
            bookmarkRepo.clearBookmarks()
            _stateFlow.value = AppState.Empty
        }
    }


    fun saveToDB(article: Article) {
        viewModelScope.launch {
            bookmarkRepo.saveBookmark(article)
            renderData()
        }
    }


    override fun onCleared() {
        _stateFlow.value = AppState.Empty
        super.onCleared()
    }
}