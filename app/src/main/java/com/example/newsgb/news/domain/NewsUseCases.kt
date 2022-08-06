package com.example.newsgb.news.domain

import com.example.newsgb._core.data.api.model.ApiKeys
import com.example.newsgb._core.ui.mapper.EntitiesToArticleMapper
import com.example.newsgb._core.ui.mapper.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.utils.ui.Category

class NewsUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val newsRepo: NewsRepository
) {
    /**
     * метод запроса первой страницы новостей по категории.
     **/
    suspend fun getNewsByCategory(
        initialPage: Int,
        category: Category,
        isRefreshing: Boolean
    ): Result<List<Article>> {
        var tokenIndex = 0
        var token = ApiKeys.values()[tokenIndex].token

        var result = newsRepo.getNewsByCategory(
            page = initialPage,
            category = category.apiCode,
            token = token
        )
        while (result.isFailure && result.exceptionOrNull()?.message == "HTTP 429 ") {
            if (++tokenIndex < ApiKeys.values().size) {
                token = ApiKeys.values()[tokenIndex].token
                result = newsRepo.getNewsByCategory(
                    page = initialPage,
                    category = category.apiCode,
                    token = token
                )
            } else break
        }

        return result.map { response ->
            var remoteArticles = NewsDtoToUiMapper(response.articles, category = category).toMutableList()
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

    /**
     * метод добавления или удаления статьи из БД
     */
    suspend fun checkArticleInBookMarks(article: Article): Result<Boolean> {
        return if (article.isChecked) {
            bookmarkRepo.saveBookmark(article)
        } else {
            bookmarkRepo.removeBookmark(article)
        }
    }
}