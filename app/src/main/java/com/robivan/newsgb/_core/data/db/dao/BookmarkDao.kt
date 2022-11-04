package com.robivan.newsgb._core.data.db.dao

import androidx.room.*
import com.robivan.newsgb._core.data.db.entity.ArticleEntity

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmark_table")
    suspend fun getAll(): List<ArticleEntity>

    @Query("SELECT * FROM bookmark_table WHERE content_url LIKE :contentUrl")
    suspend fun getEntityByUrl(contentUrl: String): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEntity(entity: ArticleEntity)

    @Delete
    suspend fun removeEntity(entity: ArticleEntity)

    @Query("DELETE FROM bookmark_table")
    suspend fun removeAll()
}