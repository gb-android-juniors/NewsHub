package com.example.newsgb.search.domain

import com.example.newsgb._core.data.api.model.ResponseDTO

interface SearchRepository {
    suspend fun getNewsByPhrase(page: Int, phrase: String, apiKey: String): Result<ResponseDTO>
}