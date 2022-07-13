package com.example.newsgb._core.data.api.model

import com.google.gson.annotations.SerializedName

data class ArticleDTO(
    @SerializedName("source")
    val source: SourceDTO,

    @SerializedName("author")
    val author: String?,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("url")
    val contentUrl: String,

    @SerializedName("urlToImage")
    val imageUrl: String?,

    @SerializedName("publishedAt")
    val publishedDate: String,

    @SerializedName("content")
    val content: String?
)
