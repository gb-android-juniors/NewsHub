package com.robivan.newsgb.article.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robivan.newsgb._core.ui.model.*
import com.robivan.newsgb._core.ui.store.NewsStore
import com.robivan.newsgb.article.domain.ArticleUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val useCases: ArticleUseCases,
    private val store: NewsStore,
    private val article: Article
    ) : ViewModel() {

    private val _viewState = MutableStateFlow<ItemViewState>(ItemViewState.Loading)
    val viewState: StateFlow<ItemViewState> = _viewState.asStateFlow()

    init {
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)

    }

    private fun renderStoreState(storeState: AppState) {
        when(storeState) {
            AppState.Empty, AppState.Loading, is AppState.MoreLoading -> _viewState.value = ItemViewState.Loading
            is AppState.Data -> setSuccessState(data = storeState.data)
            is AppState.BookmarkChecking -> _viewState.value = ItemViewState.Refreshing
            is AppState.Error -> _viewState.value = ItemViewState.Error(message = storeState.message)
            else -> {}
        }
    }

    private fun renderAppEffect(effect: AppEffect) {
        when(effect) {
            is AppEffect.CheckBookmark -> checkBookmarkInDatabase(article = effect.dataItem)
            else -> {}
        }
    }

    private fun setSuccessState(data: List<Article>) {
        try {
            val article = data.first { it.contentUrl == article.contentUrl }
            _viewState.value = ItemViewState.Data(data = article)
        } catch (ex: Throwable) {
            _viewState.value = ItemViewState.Data(data = article)
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