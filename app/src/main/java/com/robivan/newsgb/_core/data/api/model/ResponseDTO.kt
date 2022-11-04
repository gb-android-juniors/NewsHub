package com.robivan.newsgb._core.data.api.model

import com.google.gson.annotations.SerializedName

data class ResponseDTO(
    @SerializedName("status")
    val status: String,

    @SerializedName("code")
    val errorCode: String?,

    @SerializedName("message")
    val errorMessage: String?,

    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("articles")
    val articles: List<ArticleDTO>
)