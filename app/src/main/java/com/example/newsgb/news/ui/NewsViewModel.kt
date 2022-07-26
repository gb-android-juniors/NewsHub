package com.example.newsgb.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.*
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val bookmarkRepo: BookmarkRepository,
    private val newsRepo: NewsRepository,
    private val mapper: NewsDtoToUiMapper,
    private val store: NewsStore,
    private val category: Category
) : ViewModel() {

    /** переменная состояния экрана со списком новостей */
    private val _viewState = MutableStateFlow<ListViewState>(ListViewState.Loading)
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
        when(storeState) {
            AppState.Empty, AppState.Loading, is AppState.MoreLoading -> _viewState.value = ListViewState.Loading
            is AppState.Data -> setSuccessState(data = storeState.data)
            is AppState.Error -> _viewState.value = ListViewState.Error(message = storeState.message)
        }
    }

    /**
     * метод обработки команд от NewsStore
     *
     * AppEffect.LoadData - команда на загрузку данных
     * AppEffect.Error - команда отображение ошибки при дозагрузке данных
     * */
    private fun renderAppEffect(effect: AppEffect) {
        when (effect) {
            AppEffect.LoadData -> getNewsByCategory()
            is AppEffect.Error -> {}
        }
    }

    /**
     * отфильтровываем список данных из стора по категории и
     * передаем их в соответствующем состоянии для отображения на экране
     **/
    private fun setSuccessState(data: List<Article>) {
        val filteredData = data.filter { it.category == category }
        if (filteredData.isEmpty()) {
            _viewState.value = ListViewState.Empty
        } else {
            _viewState.value = ListViewState.Data(data = filteredData)
        }
    }

    /**
     * метод получения списка новостей по категории.
     * Проверяем, если данные уже загружены ранее и хранятся в стор, берем их от туда отфильтровав по категории.
     * Если данные еще не загружены, инициируем событие по дозагрузке данных
     **/
    fun getData() {
        val currentStoreState = store.storeState.value
        if (currentStoreState is AppState.Data) {
            val filteredData = currentStoreState.data.filter { it.category == category }
            if (filteredData.isEmpty()) {
                store.dispatch(event = AppEvent.LoadMore)
            } else {
                _viewState.value = ListViewState.Data(data = filteredData)
            }
        }
    }

    /**
     * метод запроса первой страницы новостей по категории.
     * В случае успеха конвертируем поулченные данные с помощью маппера и
     * передаем в качестве успешного события AppEvent.DataReceived в NewsStore
     * */
    private fun getNewsByCategory() {
        viewModelScope.launch {
            newsRepo.getNewsByCategory(page = INITIAL_PAGE, countryCode = "ru", category = category.apiCode)
                .onSuccess { response ->
                    val articles = mapper(response.articles, category = category)

                    articles.map { article ->
                        // думаю, лучше выгружать из бд сразу все статьи и сравнивать два списка.
                        // Вот тут то и понадобится интерактор или юзкейс
                        bookmarkRepo.findArticleInBookmarks(article).onSuccess { isChecked ->
                            article.isChecked = isChecked
                        }
                    }
                    store.dispatch(AppEvent.DataReceived(data = articles))
                }
                .onFailure { ex ->
                    store.dispatch(AppEvent.ErrorReceived(message = ex.message))
                }
        }
    }

    /**
     * сохранение статьи в закладках (добавить в БД)
     */
    fun saveToDB(article: Article) {
        viewModelScope.launch {
            bookmarkRepo.saveBookmark(article)
            refreshDataBookmarks(article, true)
        }
    }

    /**
     * удаление статьи из закладок (удалить из БД)
     */
    fun deleteBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkRepo.removeBookmark(article)
            refreshDataBookmarks(article, false)
        }
    }

    // этим всем будет заниматься стор в случае успешного (onSuccess) добавления/удаления статей в списке закладок
    private fun refreshDataBookmarks(article: Article, isChecked: Boolean) {
        val currentStoreState = store.storeState.value
        if (currentStoreState is AppState.Data) {
            val newArticles = currentStoreState.data
            newArticles.map { oldArticle ->
                if (oldArticle.contentUrl == article.contentUrl) {
                    oldArticle.isChecked = isChecked
                }
            }
            store.dispatch(AppEvent.DataReceived(data = newArticles))
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}