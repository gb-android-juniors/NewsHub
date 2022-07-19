package com.example.newsgb.bookmarks.data

import com.example.newsgb._core.data.db.BookmarkDataBase
import com.example.newsgb._core.ui.mapArticleToEntity
import com.example.newsgb._core.ui.mapEntitiesListToArticlesList
import com.example.newsgb._core.ui.mapEntityToArticle
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository

class BookmarkRepositoryImpl(private var bookmarkDB: BookmarkDataBase): BookmarkRepository {

    override suspend fun getAllBookmarks(): List<Article> {
        val entitiesList = bookmarkDB.bookmarkDao().getAll()
        return mapEntitiesListToArticlesList(entitiesList)
    }

    override suspend fun getBookmarkById(id: Int): Article {
        val entity = bookmarkDB.bookmarkDao().getEntityById(id)
        return mapEntityToArticle(entity!!)
    }

    override suspend fun saveBookmark(article: Article) {
        val entity = mapArticleToEntity(article)
        bookmarkDB.bookmarkDao().saveEntity(entity)
    }

    override suspend fun removeBookmark(article: Article) {
        val entity = mapArticleToEntity(article)
        bookmarkDB.bookmarkDao().removeEntity(entity)
    }

    override suspend fun clearBookmarks(){
        bookmarkDB.bookmarkDao().removeAll()
    }

}