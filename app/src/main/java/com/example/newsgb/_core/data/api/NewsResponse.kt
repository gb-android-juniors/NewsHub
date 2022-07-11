package com.example.newsgb._core.data.api

import com.example.newsgb._core.data.db.entity.ArticleEntity

data class NewsResponse(
    val articles: MutableList<ArticleEntity>,
    val status: String,
    val totalResults: Int
)