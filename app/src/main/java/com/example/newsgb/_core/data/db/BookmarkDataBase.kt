package com.example.newsgb._core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsgb._core.data.db.dao.BookmarkDao
import com.example.newsgb._core.data.db.entity.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
abstract class BookmarkDataBase : RoomDatabase() {

    abstract fun bookmarkDao() : BookmarkDao
}