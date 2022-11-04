package com.robivan.newsgb.main.domain

import com.robivan.newsgb._core.data.api.model.ApiKey
import com.robivan.newsgb._core.ui.mapper.EntitiesToArticleMapper
import com.robivan.newsgb._core.ui.mapper.NewsDtoToUiMapper
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.bookmarks.domain.BookmarkRepository

class MainUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val mainRepo: MainRepository
) {

    suspend fun getInitialData(initialPage: Int): Result<List<Article>> {

        var result = mainRepo.getBreakingNews(page = initialPage, apiKey = ApiKey.getKey())
        while (result.isFailure && result.exceptionOrNull()?.message == "HTTP 429 ") {
            val nextKey = ApiKey.nextKey() ?: break
            result = mainRepo.getBreakingNews(page = initialPage, apiKey = nextKey)
        }

        return result.map { response ->
            var remoteArticles = NewsDtoToUiMapper(response.articles).toMutableList()
            bookmarkRepo.getAllBookmarks().onSuccess { entities ->
                val bookmarkArticles = EntitiesToArticleMapper(entities)

                bookmarkArticles.forEach { bookmark ->
                    remoteArticles = remoteArticles.map { article ->
                        if (article.isTheSame(bookmark)) {
                            article.copy(isChecked = true)
                        } else {
                            article
                        }
                    }.toMutableList()
                }
                remoteArticles += bookmarkArticles
            }
            remoteArticles
        }
    }
}