package com.robivan.newsgb.article.domain

import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.bookmarks.domain.BookmarkRepository

class ArticleUseCases(private val bookmarkRepo: BookmarkRepository) {

    /**
     * добавление или удаление статьи из БД
     */
    suspend fun checkArticleInBookMarks(article: Article) : Result<Boolean>{
        return if (article.isChecked) {
            bookmarkRepo.saveBookmark(article)
        } else {
            bookmarkRepo.removeBookmark(article)
        }
    }
}