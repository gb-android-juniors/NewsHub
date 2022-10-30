package com.example.newsgb.bookmarks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.*
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.domain.BookmarksUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val useCases: BookmarksUseCases,
    private val store: NewsStore
) : ViewModel() {

    private val _viewState = MutableStateFlow<ListViewState>(ListViewState.Empty)
    val viewState: StateFlow<ListViewState> = _viewState.asStateFlow()

    init {
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
    }

    private fun renderStoreState(storeState: AppState) {
        when (storeState) {
            is AppState.Empty, AppState.Loading, is AppState.Refreshing, is AppState.MoreLoading   -> _viewState.value = ListViewState.Loading
            is AppState.Data -> filterBookmarksFromStore(data = storeState.data)
            is AppState.BookmarkChecking -> _viewState.value = ListViewState.Refreshing(data = storeState.data)
            is AppState.BookmarksClearing  -> _viewState.value = ListViewState.Refreshing(data = storeState.data)
            is AppState.Error -> _viewState.value = ListViewState.Error(message = storeState.message)
        }
    }

    private fun renderAppEffect(effect: AppEffect) {
        when (effect) {
            AppEffect.ClearBookmarks -> deleteBookmarksFromDB()
            is AppEffect.CheckBookmark -> checkBookmarkInDatabase(article = effect.dataItem)
            else -> {}
        }
    }

    fun getData() {
        val currentStoreState = store.storeState.value
        if (currentStoreState is AppState.Data) {
            filterBookmarksFromStore(data = currentStoreState.data)
        }
    }

    private fun filterBookmarksFromStore(data: List<Article>) {
        val filteredData = data
            .filter { it.isChecked }
            .distinctBy { it.contentUrl }
            .sortedBy { it.publishedDate }
        if (filteredData.isEmpty()) {
            _viewState.value = ListViewState.Empty
        } else {
            _viewState.value = ListViewState.Data(data = filteredData)
        }
    }

    fun clearBookmarks() {
        store.dispatch(event = AppEvent.BookmarksClear)
    }

    private fun deleteBookmarksFromDB() {
        viewModelScope.launch {
            useCases.clearBookmarks()
                .onSuccess {
                    store.dispatch(event = AppEvent.DataReceived(listOf()))
                }
                .onFailure { failure ->
                    store.dispatch(event = AppEvent.ErrorReceived(message = failure.message))
                }
        }
    }

    fun checkBookmark(article: Article) {
        store.dispatch(event = AppEvent.BookmarkCheck(article = article))
    }

    private fun checkBookmarkInDatabase(article: Article) {
        viewModelScope.launch {
            val checkedArticle = article.copy(isChecked = !article.isChecked)
            useCases.checkArticleInBookMarks(article = checkedArticle)
                .onSuccess {
                    store.dispatch(event = AppEvent.DataReceived(data = listOf(checkedArticle)))
                }
                .onFailure { failure ->
                    store.dispatch(event = AppEvent.ErrorReceived(message = failure.message))
                }
        }
    }
}