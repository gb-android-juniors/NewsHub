package com.example.newsgb.news.domain

import com.example.newsgb._core.data.api.model.ResponseDTO

interface NewsRepository {
   suspend fun getNewsByCategory(page: Int, countryCode: String, category: String, token: String): Result<ResponseDTO>
}