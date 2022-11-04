package com.robivan.newsgb.news.domain

import com.robivan.newsgb._core.data.api.model.ApiKey
import com.robivan.newsgb._core.ui.mapper.EntitiesToArticleMapper
import com.robivan.newsgb._core.ui.mapper.NewsDtoToUiMapper
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.bookmarks.domain.BookmarkRepository
import com.robivan.newsgb.utils.ui.Category

class NewsUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val newsRepo: NewsRepository
) {

    suspend fun getNewsByCategory(
        page: Int,
        category: Category,
        isRefreshing: Boolean
    ): Result<List<Article>> {
        var result = newsRepo.getNewsByCategory(
            page = page,
            category = category.apiCode,
            apiKey = ApiKey.getKey()
        )
        while (result.isFailure && result.exceptionOrNull()?.message == "HTTP 429 ") {
            val nextToken = ApiKey.nextKey() ?: break
            result = newsRepo.getNewsByCategory(
                page = page,
                category = category.apiCode,
                apiKey = nextToken
            )
        }

        return result.map { response ->
            var remoteArticles =
                NewsDtoToUiMapper(response.articles, category = category).toMutableList()
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
                if (isRefreshing) remoteArticles += bookmarkArticles
            }
            remoteArticles
        }
    }

    suspend fun checkArticleInBookMarks(article: Article): Result<Boolean> {
        return if (article.isChecked) {
            bookmarkRepo.saveBookmark(article)
        } else {
            bookmarkRepo.removeBookmark(article)
        }
    }
}