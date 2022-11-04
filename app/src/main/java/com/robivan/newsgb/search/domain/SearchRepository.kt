package com.robivan.newsgb.search.domain

import com.robivan.newsgb._core.data.api.model.ResponseDTO

interface SearchRepository {
    suspend fun getNewsByPhrase(page: Int, phrase: String, apiKey: String): Result<ResponseDTO>
}