package com.example.newsgb.bookmarks.domain

import com.example.newsgb._core.ui.model.Article

class BookmarksUseCases(private val bookmarkRepo: BookmarkRepository) {

    suspend fun clearBookmarks(): Result<Boolean> {
        return bookmarkRepo.clearBookmarks()
    }

    suspend fun checkArticleInBookMarks(article: Article): Result<Boolean> {
        return if (article.isChecked) {
            bookmarkRepo.saveBookmark(article)
        } else {
            bookmarkRepo.removeBookmark(article)
        }
    }
}