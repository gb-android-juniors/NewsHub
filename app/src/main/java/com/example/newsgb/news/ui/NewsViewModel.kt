package com.example.newsgb.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.model.*
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.news.domain.NewsUseCases
import com.example.newsgb.utils.Constants
import com.example.newsgb.utils.Constants.Companion.INITIAL_PAGE
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val useCases: NewsUseCases,
    private val store: NewsStore,
    private val category: Category
) : ViewModel() {

    private var currentPageNumber = INITIAL_PAGE
    private var hasMoreResults: Boolean = true

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
        when (storeState) {
            AppState.Empty, AppState.Loading -> _viewState.value = ListViewState.Loading
            is AppState.MoreLoading -> initMoreLoadingStatus(data = storeState.data)
            is AppState.Refreshing -> _viewState.value =
                ListViewState.Refreshing(data = storeState.data)
            is AppState.BookmarkChecking -> _viewState.value =
                ListViewState.Refreshing(data = storeState.data)
            is AppState.Data -> setSuccessState(data = storeState.data)
            is AppState.Error -> _viewState.value =
                ListViewState.Error(message = storeState.message)
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
        when (effect) {
            is AppEffect.LoadData -> getNewsByCategory(isRefreshing = effect.isRefreshing)
            AppEffect.Refresh -> refreshData()
            is AppEffect.CheckBookmark -> checkBookmarkInDatabase(article = effect.dataItem)
            else -> {}
        }
    }

    private fun initMoreLoadingStatus(data: List<Article>) {
        val filteredData = data.filter { it.category == category }
        _viewState.value = if (filteredData.isEmpty()) {
            ListViewState.MoreLoading(
                mainProgressState = true,
                recyclerProgressState = false
            )
        } else {
            ListViewState.MoreLoading(mainProgressState = false, recyclerProgressState = true)
        }
    }

    /**
     * отфильтровываем список данных из стора по категории и
     * передаем их в соответствующем состоянии для отображения на экране
     **/
    private fun setSuccessState(data: List<Article>) {
        val filteredData = data.filter { it.category == category }
        _viewState.value = if (filteredData.isEmpty()) {
            ListViewState.Empty
        } else {
            ListViewState.Data(data = filteredData)
        }
    }

    private fun getNewsByCategory(isRefreshing: Boolean) {
        viewModelScope.launch {
            useCases.getNewsByCategory(
                page = currentPageNumber,
                category = category,
                isRefreshing = isRefreshing
            )
                .onSuccess { newData ->
                    if (newData.isEmpty()) {
                        currentPageNumber--
                        hasMoreResults = false
                    }
                    store.dispatch(AppEvent.DataReceived(data = newData))
                }
                .onFailure {
                    currentPageNumber--
                    hasMoreResults = false
                    store.dispatch(AppEvent.ErrorReceived(message = it.message))
                }
        }
    }

    /** метод обновления списков статей для всех категорий */
    fun refreshData() {
        currentPageNumber = INITIAL_PAGE
        hasMoreResults = true
        store.dispatch(event = AppEvent.Refresh)
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

    /**
     * метод получения списка новостей по категории.
     * Проверяем, если данные уже загружены ранее и хранятся в стор, берем их от туда отфильтровав по категории.
     * Если данные еще не загружены, инициируем событие по дозагрузке данных
     **/
    fun getInitData() {
        when (val currentStoreState = store.storeState.value) {
            is AppState.Data -> dispatchFilteredDataStore(currentStoreState.data)
            is AppState.MoreLoading -> dispatchFilteredDataStore(currentStoreState.data)
            else -> {}
        }
    }

    private fun dispatchFilteredDataStore(data: List<Article>) {
        val filteredData = data.filter { it.category == category }
        if (filteredData.isEmpty()) {
            store.dispatch(event = AppEvent.LoadMore)
        } else {
            currentPageNumber = calculateCurrentPage(filteredData.size)
            _viewState.value = ListViewState.Data(data = filteredData)
        }
    }

    private fun calculateCurrentPage(dataSize: Int): Int =
        (dataSize / Constants.DEFAULT_API_PAGE_SIZE) + 1

    fun getMoreDataToList() {
        if (hasMoreResults && store.storeState.value is AppState.Data) {
            currentPageNumber++
            store.dispatch(AppEvent.LoadMore)
        }
    }

    /** метод обработки нажатия на фложок закладки */
    fun checkBookmark(article: Article) {
        store.dispatch(event = AppEvent.BookmarkCheck(article = article))
    }
}