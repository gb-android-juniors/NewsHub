package com.example.newsgb.bookmarks.data

import com.example.newsgb._core.data.db.BookmarkDataBase
import com.example.newsgb._core.ui.mapArticleToEntity
import com.example.newsgb._core.ui.mapEntitiesListToArticlesList
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.bookmarks.domain.BookmarkRepository

// думаю, в конструктор сразу можно передать bookmarkDB.bookmarkDao, эта зависимость уже прописана в appModule
// работу с БД нужно организовать через IO поток (см. пример репозитория для получения данных с сервера)
// возможно, данные получаемые с БД тоже нужно обернуть в Result
class BookmarkRepositoryImpl(private var bookmarkDB: BookmarkDataBase): BookmarkRepository {

    override suspend fun getAllBookmarks(): List<Article> {
        val entitiesList = bookmarkDB.bookmarkDao().getAll()
        return mapEntitiesListToArticlesList(entitiesList)
    }

    override suspend fun findArticleInBookmarks(article: Article): Boolean {
        val entity = bookmarkDB.bookmarkDao().getEntityByUrl(article.contentUrl)
        return entity != null
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