package com.example.newsgb.news.domain

import com.example.newsgb._core.data.api.model.ApiKeys
import com.example.newsgb._core.ui.EntitiesToArticleMapper
import com.example.newsgb._core.ui.NewsDtoToUiMapper
import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.utils.ui.Category

class NewsUseCases(
    private val bookmarkRepo: BookmarkRepository,
    private val newsRepo: NewsRepository
) {
    /**
     * метод запроса первой страницы новостей по категории.
     * В случае успеха конвертируем поулченные данные с помощью маппера и
     * передаем в качестве успешного события AppEvent.DataReceived в NewsStore
     * */
    suspend fun getNewsByCategory(
        initialPage: Int,
        countryCode: String = "ru",
        category: Category
    ): AppEvent {
        var tokenIndex = 0
        var token = ApiKeys.values()[tokenIndex].token

        var event: AppEvent = getRequestToApi(
            initialPage = initialPage,
            countryCode = countryCode,
            category = category,
            token = token
        )
        while (event is AppEvent.ErrorReceived && event.message.equals("HTTP 429 ")) {
            if (++tokenIndex < ApiKeys.values().size) {
                token = ApiKeys.values()[tokenIndex].token
                event = getRequestToApi(
                    initialPage = initialPage,
                    countryCode = countryCode,
                    category = category,
                    token = token
                )
            } else break
        }
        return event
    }

    private suspend fun getRequestToApi(
        initialPage: Int,
        countryCode: String = "ru",
        category: Category,
        token: String
    ): AppEvent {
        var event: AppEvent? = null

        newsRepo.getNewsByCategory(
            page = initialPage,
            countryCode = countryCode,
            category = category.apiCode,
            token = token
        )
            .onSuccess { response ->
                val remoteArticles =
                    NewsDtoToUiMapper(newsList = response.articles, category = category)
                bookmarkRepo.getAllBookmarks()
                    .onSuccess { entities ->
                        val bookmarkArticles = EntitiesToArticleMapper(entities)
                        var filteredArticles = remoteArticles
                        bookmarkArticles.forEach { bookmark ->
                            filteredArticles = filteredArticles.map { article ->
                                if (article.isTheSame(bookmark)) {
                                    article.copy(isChecked = true)
                                } else {
                                    article
                                }
                            }
                        }
                        event = AppEvent.DataReceived(data = filteredArticles)
                    }
                    .onFailure { ex ->
                        event = AppEvent.ErrorReceived(message = ex.message)
                    }
            }
            .onFailure { ex ->
                event = AppEvent.ErrorReceived(message = ex.message)
            }
        return event!!
    }

    /**
     * метод добавление или удаление статьи из БД
     */
    suspend fun checkArticleInBookMarks(article: Article): Result<Boolean> {
        return if (article.isChecked) {
            bookmarkRepo.saveBookmark(article)
        } else {
            bookmarkRepo.removeBookmark(article)
        }
    }
}