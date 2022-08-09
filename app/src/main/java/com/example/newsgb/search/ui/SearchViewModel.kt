package com.example.newsgb.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.*
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.search.domain.SearchUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(private val useCases: SearchUseCases, private val store: NewsStore) :
    ViewModel() {

    /** переменная состояния экрана со списком новостей */
    private val _viewState = MutableStateFlow<ListViewState>(ListViewState.Empty)
    val viewState: StateFlow<ListViewState> = _viewState.asStateFlow()

    init {
        /** При инициализации подписываемся на обновления состояний и команд от NewsStore */
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
    }

    /**
     * метод обработки состояний NewsStore
     * конвертируем состояния приложения в состояния экрана
     * */
    private fun renderStoreState(storeState: AppState) {
        when (storeState) {
            is AppState.Loading -> _viewState.value = ListViewState.Loading
            is AppState.BookmarkChecking -> {
                val currentState = viewState.value
                if (currentState is ListViewState.Data)
                _viewState.value = ListViewState.Refreshing(data = currentState.data)
            }
            else -> {}
        }
    }

    private fun renderAppEffect(effect: AppEffect) {
        when (effect) {
            is AppEffect.CheckBookmark -> checkBookmarkInDatabase(article = effect.dataItem)
            else -> {}
        }
    }

    /** метод обработки нажатия на фложок закладки */
    fun checkBookmark(article: Article) {
        store.dispatch(event = AppEvent.BookmarkCheck(article = article))
    }

    fun getData(phrase: String) {
        if (phrase.isBlank()) {
            _viewState.value = ListViewState.Empty
        } else {
            _viewState.value = ListViewState.Loading
            viewModelScope.launch {
                useCases.getNewsByPhrase(page = INITIAL_PAGE, phrase = phrase)
                    .onSuccess { newsList ->
                        _viewState.value =
                            if(newsList.isEmpty()) ListViewState.Empty else ListViewState.Data(data = newsList)
                    }
                    .onFailure { ex ->
                        _viewState.value = ListViewState.Error(message = ex.message)
                    }
            }
        }
    }

    private fun checkBookmarkInDatabase(article: Article) {
        viewModelScope.launch {
            val checkedArticle = article.copy(isChecked = !article.isChecked)
            useCases.checkArticleInBookMarks(article = checkedArticle)
                .onSuccess {
                    store.dispatch(event = AppEvent.DataReceived(data = listOf(checkedArticle)))
                    val currentState = viewState.value
                    if (currentState is ListViewState.Refreshing) {
                        val newData = checkBookmarkInCurrentData(bookmark = checkedArticle, data = currentState.data)
                        _viewState.value = ListViewState.Data(data = newData)
                    }
                }
                .onFailure { failure ->
                    store.dispatch(event = AppEvent.ErrorReceived(message = failure.message))
                }
        }
    }

    private fun checkBookmarkInCurrentData(bookmark: Article, data: List<Article>): List<Article> =
        data.map { article ->
            if (article.isTheSame(bookmark)) {
                article.copy(isChecked = bookmark.isChecked)
            } else {
                article
            }
        }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}