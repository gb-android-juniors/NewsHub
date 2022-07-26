package com.example.newsgb.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsgb._core.ui.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.main.domain.MainRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val bookmarkRepo: BookmarkRepository,
    private val mainRepo: MainRepository,
    private val mapper: NewsDtoToUiMapper,
    private val store: NewsStore
) : ViewModel() {

    /**
     * метод запроса первой страницы главных новостей
     * сохраняем полученные состояние и данные в NewsStore
     * */
    fun getInitialData() {
        store.dispatch(event = AppEvent.Refresh)
        viewModelScope.launch {
            mainRepo.getBreakingNews(page = INITIAL_PAGE)
                .onSuccess { response ->
                    val articles = mapper(response.articles)
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

    companion object {
        private const val INITIAL_PAGE = 1
    }
}