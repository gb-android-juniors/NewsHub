package com.example.newsgb._core.data.api

data class ArticleEntity(
    val title: String,
    val description: String,
    val source: Source,
    val url: String,
    val urlToImage: String
)