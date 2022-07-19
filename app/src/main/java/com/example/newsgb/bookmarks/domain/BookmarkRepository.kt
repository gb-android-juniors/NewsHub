package com.example.newsgb.bookmarks.domain

import com.example.newsgb._core.ui.model.Article

interface BookmarkRepository {
    suspend fun getAllBookmarks(): List<Article>
    suspend fun getBookmarkById(id: Int): Article
    suspend fun saveBookmark(article: Article)
    suspend fun removeBookmark(article: Article)
    suspend fun clearBookmarks()
}