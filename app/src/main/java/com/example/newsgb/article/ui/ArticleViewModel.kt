package com.example.newsgb.article.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.AppState
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ItemViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val bookmarkRepo: BookmarkRepository,
    store: NewsStore,
    private val articleUrl: String
    ) : ViewModel() {

    /** переменная состояния экрана детализации */
    private val _viewState = MutableStateFlow<ItemViewState>(ItemViewState.Loading)
    val viewState: StateFlow<ItemViewState> = _viewState.asStateFlow()

    init {
        /** При инициализации подписываемся на обновления состояний от NewsStore */
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
    }

    /**
     * метод обработки состояний NewsStore
     * конвертируем состояния приложения в состояния экрана
     * */
    private fun renderStoreState(storeState: AppState) {
        when(storeState) {
            AppState.Empty, AppState.Loading, is AppState.MoreLoading -> _viewState.value = ItemViewState.Loading
            is AppState.Data -> setSuccessState(data = storeState.data)
            is AppState.Error -> _viewState.value = ItemViewState.Error(message = storeState.message)
        }
    }

    /**
     * ищем в сторе нужную статью по ее URL и
     * передаем ее в соответствующем состоянии для отображения на экране
     * если статься не найдена, выдаем состояние ошибки
     **/
    private fun setSuccessState(data: List<Article>) {
        try {
            val article = data.first { it.contentUrl == articleUrl }
            _viewState.value = ItemViewState.Data(data = article)
        } catch (ex: Throwable) {
            _viewState.value = ItemViewState.Error(message = ex.message)
        }
    }

    /**
     * сохранение статьи в закладках (добавить в БД)
     */
    fun saveToDB(article: Article) {
        viewModelScope.launch {
            bookmarkRepo.saveBookmark(article)
        }
    }

    /**
     * удаление статьи из закладок (удалить из БД)
     */
    fun deleteBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkRepo.removeBookmark(article)
        }
    }
}