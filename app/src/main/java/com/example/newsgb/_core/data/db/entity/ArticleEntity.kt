package com.example.newsgb._core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsgb._core.data.db.DbUtils.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "source_name") val sourceName: String,
    @ColumnInfo(name = "author_name") val authorName: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "source_url") val sourceUrl: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "publish_date") val publishDate: String,
    @ColumnInfo(name = "content") val content: String
)