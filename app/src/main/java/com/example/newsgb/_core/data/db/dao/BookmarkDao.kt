package com.example.newsgb._core.data.db.dao

import androidx.room.*
import com.example.newsgb._core.data.db.entity.ArticleEntity

@Dao
interface BookmarkDao {

    /** Получить весь список закладок*/
    @Query("SELECT * FROM bookmark_table")
    suspend fun all(): List<ArticleEntity>
    /** Получить конкретную закладку */
    @Query("SELECT * FROM bookmark_table WHERE id LIKE :id")
    suspend fun getDataById(id: Int): ArticleEntity?
    /** Сохранить новую закладку. onConflict = OnConflictStrategy.IGNORE означает, что дубликаты не будут сохраняться */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ArticleEntity)
    /** Удалить закладку */
    @Delete
    suspend fun delete(entity: ArticleEntity)
}