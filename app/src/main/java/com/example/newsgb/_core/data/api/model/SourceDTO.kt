package com.example.newsgb._core.data.api.model

import com.google.gson.annotations.SerializedName

data class SourceDTO(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?
)