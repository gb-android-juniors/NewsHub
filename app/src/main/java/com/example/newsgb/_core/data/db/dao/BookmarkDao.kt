package com.example.newsgb._core.data.db.dao

import androidx.room.*
import com.example.newsgb._core.data.db.entity.ArticleEntity

@Dao
interface BookmarkDao {

    /**
     * Получить весь список закладок
     **/
    @Query("SELECT * FROM bookmark_table")
    suspend fun getAll(): List<ArticleEntity>

    /**
     *  Получить конкретную закладку
     **/
    @Query("SELECT * FROM bookmark_table WHERE content_url LIKE :contentUrl")
    suspend fun getEntityByUrl(contentUrl: String): ArticleEntity?

    /**
     * Сохранить новую закладку.
     * onConflict = OnConflictStrategy.IGNORE - дубликаты будут перезаписаны
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEntity(entity: ArticleEntity)

    /**
     * Удалить одну закладку
     **/
    @Delete
    suspend fun removeEntity(entity: ArticleEntity)

    /**
     * Удалить все из таблицы
     **/
    @Query("DELETE FROM bookmark_table")
    suspend fun removeAll()
}