package com.example.newsgb._core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsgb._core.data.db.DbUtils.TABLE_NAME
import com.example.newsgb.utils.ui.Category

@Entity(tableName = TABLE_NAME)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "source_name") val sourceName: String,
    @ColumnInfo(name = "author_name") val author: String?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "content_url") val contentUrl: String,
    @ColumnInfo(name = "image_url") val urlToImage: String?,
    @ColumnInfo(name = "published_date") val publishedDate: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "category") val category: Category
)
