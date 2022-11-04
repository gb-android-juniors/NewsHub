package com.robivan.newsgb._core.ui.store

import com.robivan.newsgb._core.ui.model.AppEffect
import com.robivan.newsgb._core.ui.model.AppEvent
import com.robivan.newsgb._core.ui.model.AppState
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.utils.ui.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsStore : CoroutineScope by MainScope() {

    private val _storeState = MutableStateFlow<AppState>(AppState.Empty)
    val storeState: StateFlow<AppState> = _storeState.asStateFlow()

    private val _storeEffect: MutableSharedFlow<AppEffect> = MutableSharedFlow()
    val storeEffect: SharedFlow<AppEffect> = _storeEffect.asSharedFlow()

    fun dispatch(event: AppEvent) {
        val currentState = storeState.value // сохраняем в переменную текущее состояние
        val newState = when (event) {
            is AppEvent.Refresh -> {
                when (currentState) {
                    is AppState.Empty, is AppState.Error -> {
                        launch { _storeEffect.emit(AppEffect.LoadData(isRefreshing = true)) }
                        AppState.Loading
                    }
                    is AppState.Data -> {
                        launch { _storeEffect.emit(AppEffect.LoadData(isRefreshing = true)) }
                        AppState.Refreshing(data = currentState.data)
                    }
                    is AppState.MoreLoading ->{
                        launch { _storeEffect.emit(AppEffect.Refresh) }
                        AppState.Refreshing(data = currentState.data)
                    }
                    is AppState.Refreshing -> {
                        AppState.Data(data = currentState.data)
                    }
                    else -> currentState
                }
            }
            is AppEvent.ErrorReceived -> {
                when (currentState) {
                    AppState.Loading -> AppState.Error(message = event.message)
                    is AppState.MoreLoading -> {
                        launch { _storeEffect.emit(AppEffect.Error(message = event.message)) }
                        AppState.Data(data = currentState.data)
                    }
                    is AppState.BookmarkChecking -> {
                        launch { _storeEffect.emit(AppEffect.Error(message = event.message)) }
                        AppState.Data(data = currentState.data)
                    }
                    is AppState.Refreshing -> {
                        launch { _storeEffect.emit(AppEffect.Error(message = event.message)) }
                        AppState.Data(data = currentState.data)
                    }
                    else -> currentState
                }
            }
            is AppEvent.DataReceived -> {
                when (currentState) {
                    AppState.Loading, is AppState.Refreshing -> {
                        if (event.data.isEmpty()) AppState.Empty
                        else AppState.Data(data = event.data)
                    }
                    is AppState.MoreLoading -> {
                        AppState.Data(data = currentState.data + event.data)
                    }
                    is AppState.BookmarkChecking -> {
                        val newData = checkBookmarkInCurrentData(
                            bookmark = event.data[0],
                            currentData = currentState.data
                        )
                        AppState.Data(data = newData)
                    }
                    is AppState.BookmarksClearing -> {
                        val newData = clearBookmarksInCurrentData(data = currentState.data)
                        AppState.Data(data = newData)
                    }
                    else -> currentState
                }
            }
            is AppEvent.LoadMore -> {
                when (currentState) {
                    is AppState.Data -> {
                        launch { _storeEffect.emit(AppEffect.LoadData(isRefreshing = false)) }
                        AppState.MoreLoading(data = currentState.data)
                    }
                    is AppState.MoreLoading -> {
                        launch { _storeEffect.emit(AppEffect.LoadData(isRefreshing = false)) }
                        AppState.Refreshing(data = currentState.data)
                    }
                    else -> currentState
                }
            }
            is AppEvent.BookmarkCheck -> {
                when (currentState) {
                    is AppState.Data -> {
                        launch { _storeEffect.emit(AppEffect.CheckBookmark(event.article)) }
                        AppState.BookmarkChecking(data = currentState.data)
                    }
                    else -> currentState
                }
            }
            is AppEvent.BookmarksClear -> {
                when (currentState) {
                    is AppState.Data -> {
                        launch { _storeEffect.emit(AppEffect.ClearBookmarks) }
                        AppState.BookmarksClearing(data = currentState.data)
                    }
                    else -> currentState
                }
            }
        }

        if (newState != currentState) {
            _storeState.value = newState
        }
    }

    private fun clearBookmarksInCurrentData(data: List<Article>): List<Article> =
        data.filter { it.category != Category.BOOKMARKS }.map { article ->
            article.copy(isChecked = false)
        }

    private fun checkBookmarkInCurrentData(bookmark: Article, currentData: List<Article>): List<Article> {
        return if (currentData.any { it.isTheSame(bookmark) }) {
            currentData.map { article ->
                if (article.isTheSame(bookmark)) {
                    article.copy(isChecked = bookmark.isChecked)
                } else {
                    article
                }
            }
        } else {
            currentData + bookmark
        }
    }
}