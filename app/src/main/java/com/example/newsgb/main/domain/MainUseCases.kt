package com.example.newsgb.main.domain

import com.example.newsgb._core.data.api.model.ApiKeys
import com.example.newsgb._core.ui.mapper.EntitiesToArticleMapper
import com.example.newsgb._core.ui.mapper.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository

class MainUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val mainRepo: MainRepository
) {

    /**
     * итоговый список статей, содержащий статьи из закладок + статьи полученнные от API.
     */
    suspend fun getInitialData(initialPage: Int): Result<List<Article>> {

        var tokenIndex = 0
        var token = ApiKeys.values()[tokenIndex].token

        var result = mainRepo.getBreakingNews(page = initialPage, apiKey = token)
        while (result.isFailure && result.exceptionOrNull()?.message == "HTTP 429 ") {
            if (++tokenIndex < ApiKeys.values().size) {
                token = ApiKeys.values()[tokenIndex].token
                result = mainRepo.getBreakingNews(page = initialPage, apiKey = token)
            } else break
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