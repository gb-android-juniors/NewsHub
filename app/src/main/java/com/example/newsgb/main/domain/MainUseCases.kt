package com.example.newsgb.main.domain

import com.example.newsgb._core.ui.EntitiesToArticleMapper
import com.example.newsgb._core.ui.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb.bookmarks.domain.BookmarkRepository

class MainUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val mainRepo: MainRepository
) {
    suspend fun getInitialData(initialPage: Int): AppEvent {

        var event: AppEvent? = null

        /**
         * итоговый список статей, содержащий статьи из закладок + статьи полученнные от API.
         * Его будем возвращать в событии AppEvent.DataReceived.
         */

        mainRepo.getBreakingNews(page = initialPage)
            .onSuccess { response ->
                val remoteArticles = NewsDtoToUiMapper(response.articles)
                bookmarkRepo.getAllBookmarks()
                    .onSuccess { entities ->
                        val bookmarkArticles = EntitiesToArticleMapper(entities)
                        remoteArticles.map { article ->
                            bookmarkArticles.find { it.isTheSame(article) }
                                ?.let { article.copy(isChecked = true) }
                        }
                        event = AppEvent.DataReceived(data = remoteArticles + bookmarkArticles)
                    }
            }
            .onFailure { ex ->
                event = AppEvent.ErrorReceived(message = ex.message)
            }

            .onFailure { ex ->
                event = AppEvent.ErrorReceived(message = ex.message)
            }
        return event!!
    }
}