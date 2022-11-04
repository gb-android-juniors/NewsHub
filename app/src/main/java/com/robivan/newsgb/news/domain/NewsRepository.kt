package com.robivan.newsgb.news.domain

import com.robivan.newsgb._core.data.api.model.ResponseDTO

interface NewsRepository {
    suspend fun getNewsByCategory(page: Int, category: String, apiKey: String): Result<ResponseDTO>
}