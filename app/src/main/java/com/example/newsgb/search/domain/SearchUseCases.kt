package com.example.newsgb.search.domain

import com.example.newsgb._core.data.api.model.ApiKey
import com.example.newsgb._core.ui.mapper.EntitiesToArticleMapper
import com.example.newsgb._core.ui.mapper.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.utils.ui.Category

class SearchUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val searchRepo: SearchRepository
) {

    suspend fun getNewsByPhrase(
        page: Int,
        phrase: String,
    ): Result<List<Article>> {

        var result =
            searchRepo.getNewsByPhrase(page = page, phrase = phrase, apiKey = ApiKey.getKey())
        while (result.isFailure && result.exceptionOrNull()?.message == "HTTP 429 ") {
            val nextToken = ApiKey.nextKey() ?: break
            result = searchRepo.getNewsByPhrase(page = page, phrase = phrase, apiKey = nextToken)
        }

        return result.map { response ->
            var remoteArticles =
                NewsDtoToUiMapper(response.articles, category = Category.SEARCH).toMutableList()
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