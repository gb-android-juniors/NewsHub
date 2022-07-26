package com.example.newsgb.bookmarks.data

import com.example.newsgb._core.data.db.BookmarkDataBase
import com.example.newsgb._core.data.db.entity.ArticleEntity
import com.example.newsgb._core.ui.mapArticleToEntity
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarkRepositoryImpl(private val bookmarkDB: BookmarkDataBase): BookmarkRepository {

    override suspend fun getAllBookmarks(): Result<List<ArticleEntity>> {
        return try {
            val entitiesList = withContext(Dispatchers.IO) {
                bookmarkDB.bookmarkDao().getAll()
            }
            Result.success(value = entitiesList)
        } catch (ex: Exception) {
            Result.failure(exception = ex)
        }
    }

    override suspend fun findArticleInBookmarks(article: Article): Result<Boolean> {
        return try {
            val entity = bookmarkDB.bookmarkDao().getEntityByUrl(article.contentUrl)
            Result.success(value = entity != null)
        } catch (ex: Exception) {
            Result.failure(exception = ex)
        }
    }

    override suspend fun saveBookmark(article: Article): Result<Boolean> {
        return try {
            val entity = mapArticleToEntity(article)
            bookmarkDB.bookmarkDao().saveEntity(entity)
            Result.success(value = true)
        } catch (ex: Exception) {
            Result.failure(exception = ex)
        }
    }

    override suspend fun removeBookmark(article: Article): Result<Boolean> {
        return try {
            val entity = mapArticleToEntity(article)
            bookmarkDB.bookmarkDao().removeEntity(entity)
            Result.success(value = true)
        } catch (ex: Exception) {
            Result.failure(exception = ex)
        }
    }

    override suspend fun clearBookmarks(): Result<Boolean> {
        return try {
            bookmarkDB.bookmarkDao().removeAll()
            Result.success(value = true)
        } catch (ex: Exception) {
            Result.failure(exception = ex)
        }
    }
}