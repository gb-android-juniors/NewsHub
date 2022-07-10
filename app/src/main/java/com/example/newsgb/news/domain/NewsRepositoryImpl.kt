package com.example.newsgb.news.domain

import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.api.NewsResponse

class NewsRepositoryImpl(
    private val apiService: ApiService,
) : NewsRepository {

    override suspend fun getBreakingNews(
        page: Int, countryCode: String,
    ): Result<NewsResponse> {

        return try {
            Result.success(apiService.getBreakingNews())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNewsByCategory(
        category: String,
        countryCode: String,
    ): Result<NewsResponse> {
        TODO("Not yet implemented")
    }
}