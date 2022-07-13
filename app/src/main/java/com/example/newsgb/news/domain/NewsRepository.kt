package com.example.newsgb.news.domain

import com.example.newsgb._core.data.api.model.ResponseDTO

interface NewsRepository {
   suspend fun getBreakingNews(page: Int): Result<ResponseDTO>
   suspend fun getNewsByCategory(category: String, countryCode: String): Result<ResponseDTO>
}