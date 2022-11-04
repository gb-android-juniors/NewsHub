package com.robivan.newsgb.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robivan.newsgb._core.ui.model.*
import com.robivan.newsgb._core.ui.store.NewsStore
import com.robivan.newsgb.search.domain.SearchUseCases
import com.robivan.newsgb.utils.Constants.Companion.INITIAL_PAGE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(private val useCases: SearchUseCases, private val store: NewsStore) :
    ViewModel() {

    private var currentPageNumber = INITIAL_PAGE
    private var hasMoreResults: Boolean = true
    private lateinit var currentSearchPhrase: String
    private val currentArticlesList = mutableListOf<Article>()

    private val _viewState = MutableStateFlow<ListViewState>(ListViewState.Empty)
    val viewState: StateFlow<ListViewState> = _viewState.asStateFlow()

    init {
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)
    }

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

    fun getData(phrase: String, newSearch: Boolean) {
        if (phrase.isBlank()) {
            hasMoreResults = false
            _viewState.value = ListViewState.Empty
        } else {
            if (newSearch) {
                resetToDefaultState(phrase)
                _viewState.value = ListViewState.Loading
            } else {
                _viewState.value = ListViewState.MoreLoading(mainProgressState = false, recyclerProgressState = true)
            }
            viewModelScope.launch {
                useCases.getNewsByPhrase(page = currentPageNumber, phrase = phrase)
                    .onSuccess { newData ->
                        if (newData.isEmpty()) {
                            currentPageNumber--
                            hasMoreResults = false
                        }
                        _viewState.value =
                            if (newData.isEmpty() && newSearch) {
                                ListViewState.Empty
                            } else {
                                currentArticlesList += newData
                                ListViewState.Data(data = currentArticlesList.toList())
                            }
                    }
                    .onFailure { ex ->
                        currentPageNumber--
                        hasMoreResults = false
                        if (ex.message == "HTTP 426 ") {
                            _viewState.value = ListViewState.Data(currentArticlesList.toList())
                        } else {
                            _viewState.value = ListViewState.Error(message = ex.message)
                        }
                    }
            }
        }
    }

    private fun resetToDefaultState(phrase: String) {
        currentSearchPhrase = phrase
        currentPageNumber = INITIAL_PAGE
        hasMoreResults = true
        currentArticlesList.clear()
    }

    private fun checkBookmarkInDatabase(article: Article) {
        viewModelScope.launch {
            val checkedArticle = article.copy(isChecked = !article.isChecked)
            useCases.checkArticleInBookMarks(article = checkedArticle)
                .onSuccess {
                    store.dispatch(event = AppEvent.DataReceived(data = listOf(checkedArticle)))
                    val currentState = viewState.value
                    if (currentState is ListViewState.Refreshing) {
                        val newData = checkBookmarkInCurrentData(
                            bookmark = checkedArticle,
                            data = currentState.data
                        )
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

    fun checkBookmark(article: Article) {
        store.dispatch(event = AppEvent.BookmarkCheck(article = article))
    }

    fun getMoreDataToList() {
        if (hasMoreResults && viewState.value is ListViewState.Data) {
            currentPageNumber++
            getData(phrase = currentSearchPhrase, newSearch = false)
        }
    }
}