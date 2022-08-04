package com.example.newsgb.article.domain

import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository

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