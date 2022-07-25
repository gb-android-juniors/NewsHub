package com.example.newsgb.bookmarks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ListViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BookmarksViewModel(
    private val bookmarkRepo: BookmarkRepository,
    store: NewsStore
) : ViewModel() {

    /** переменная состояния экрана со списком закладок */
    private val _stateFlow = MutableStateFlow<ListViewState>(ListViewState.Empty)
    val stateFlow: StateFlow<ListViewState> = _stateFlow.asStateFlow()

    fun renderData() {
        _stateFlow.value = ListViewState.Loading
        viewModelScope.launch {
            val bookmarksList = bookmarkRepo.getAllBookmarks()
            _stateFlow.value = if (bookmarksList.isEmpty()) {
                ListViewState.Empty
            } else {
                ListViewState.Data(bookmarksList)
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
            _stateFlow.value = ListViewState.Empty
        }
    }

    override fun onCleared() {
        _stateFlow.value = ListViewState.Empty
        super.onCleared()
    }
}