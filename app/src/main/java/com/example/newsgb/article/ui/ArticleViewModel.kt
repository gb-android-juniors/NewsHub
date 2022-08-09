package com.example.newsgb.article.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.*
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.article.domain.ArticleUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val useCases: ArticleUseCases,
    private val store: NewsStore,
    private val article: Article
    ) : ViewModel() {

    /** переменная состояния экрана детализации */
    private val _viewState = MutableStateFlow<ItemViewState>(ItemViewState.Loading)
    val viewState: StateFlow<ItemViewState> = _viewState.asStateFlow()

    init {
        /** При инициализации подписываемся на обновления состояний от NewsStore */
        store.storeState.onEach { renderStoreState(it) }.launchIn(viewModelScope)
        store.storeEffect.onEach { renderAppEffect(it) }.launchIn(viewModelScope)

    }

    /**
     * метод обработки состояний NewsStore
     * конвертируем состояния приложения в состояния экрана
     * */
    private fun renderStoreState(storeState: AppState) {
        when(storeState) {
            AppState.Empty, AppState.Loading, is AppState.MoreLoading -> _viewState.value = ItemViewState.Loading
            is AppState.Data -> setSuccessState(data = storeState.data)
            is AppState.BookmarkChecking -> _viewState.value = ItemViewState.Refreshing
            is AppState.Error -> _viewState.value = ItemViewState.Error(message = storeState.message)
            else -> {}
        }
    }

    /**
     * метод обработки команд от NewsStore
     *
     * AppEffect.LoadData - команда на загрузку данных
     * AppEffect.CheckBookmark - команда на добавление\удаление статьи из БД
     * AppEffect.Error - команда отображение ошибки при дозагрузке данных
     * */
    private fun renderAppEffect(effect: AppEffect) {
        when(effect) {
            is AppEffect.CheckBookmark -> checkBookmarkInDatabase(article = effect.dataItem)
            else -> {}
        }
    }

    /**
     * ищем в сторе нужную статью по ее URL и
     * передаем ее в соответствующем состоянии для отображения на экране
     * если статься не найдена, выдаем состояние ошибки
     **/
    private fun setSuccessState(data: List<Article>) {
        try {
            val article = data.first { it.contentUrl == article.contentUrl }
            _viewState.value = ItemViewState.Data(data = article)
        } catch (ex: Throwable) {
            _viewState.value = ItemViewState.Data(data = article)
        }
    }

    /**
     * метод обработки нажатия на фложок закладки
     */
    fun checkBookmark() {
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