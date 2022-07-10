package com.example.newsgb._core.data.api

data class NewsResponse(
    val articles: MutableList<ArticleEntity>,
    val status: String,
    val totalResults: Int
)