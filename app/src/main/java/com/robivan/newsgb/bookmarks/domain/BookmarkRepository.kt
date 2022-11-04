package com.robivan.newsgb.bookmarks.domain

import com.robivan.newsgb._core.data.db.entity.ArticleEntity
import com.robivan.newsgb._core.ui.model.Article

interface BookmarkRepository {
    suspend fun getAllBookmarks(): Result<List<ArticleEntity>>
    suspend fun findArticleInBookmarks(article: Article): Result<Boolean>
    suspend fun saveBookmark(article: Article): Result<Boolean>
    suspend fun removeBookmark(article: Article): Result<Boolean>
    suspend fun clearBookmarks(): Result<Boolean>
}